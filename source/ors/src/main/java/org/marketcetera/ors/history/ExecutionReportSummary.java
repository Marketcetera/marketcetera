package org.marketcetera.ors.history;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.persist.*;
import org.marketcetera.persist.PersistenceException;
import org.marketcetera.trade.OrderID;
import org.marketcetera.trade.Side;
import org.marketcetera.trade.ExecutionReport;
import org.marketcetera.trade.OrderStatus;
import org.marketcetera.core.MSymbol;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/* $License$ */
/**
 * Maintains a summary of fields of an ExecutionReport
 * to aid Position calculations. The lifecycle of this object
 * is controlled by {@link PersistentReport}
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
@Entity
@Table(name="execreports")

@NamedQuery(name = "rootIDForOrderID",
        query = "select e.rootID from ExecutionReportSummary e " +
                "where e.orderID = :orderID")

@SqlResultSetMapping(name = "positionForSymbol",
        columns = {@ColumnResult(name = "position")})

@NamedNativeQuery(name = "positionForSymbol",query = "select " +
        "sum(case when e.side = :sideBuy then e.cumQuantity else -e.cumQuantity end) as position " +
        "from execreports e " +
        "where e.symbol = :symbol " +
        "and e.sendingTime <= :sendingTime " +
        "and e.id = " +
        "(select max(s.id) from execreports s where s.rootID = e.rootID)",
        resultSetMapping = "positionForSymbol")

class ExecutionReportSummary extends EntityBase {

    /**
     * Gets the current aggregate position for the symbol based on execution
     * reports received before the supplied time.
     * <p>
     * Buy trades result in positive positions. All other kinds of trades
     * result in negative positions.
     *
     * @param inDate the time. execution reports with sending time values less
     * than this time are included in this calculation.
     * @param inSymbol the symbol for which this position needs to be computed
     *
     * @return the aggreget position for the symbol.
     *
     * @throws PersistenceException if there were errors retrieving the
     * position.
     */
    static BigDecimal getPositionForSymbol(final Date inDate,
                                           final MSymbol inSymbol)
            throws PersistenceException {
        BigDecimal position = executeRemote(new Transaction<BigDecimal>() {
            private static final long serialVersionUID = 1L;

            public BigDecimal execute(EntityManager em, PersistContext context) {
                Query query = em.createNamedQuery(
                        "positionForSymbol");  //$NON-NLS-1$
                query.setParameter("sideBuy", Side.Buy.ordinal());  //$NON-NLS-1$
                query.setParameter("symbol", inSymbol.getFullSymbol());  //$NON-NLS-1$
                query.setParameter("sendingTime", inDate,  //$NON-NLS-1$
                        TemporalType.TIMESTAMP);
                return (BigDecimal) query.getSingleResult();  //$NON-NLS-1$
            }
        }, null);
        return position == null? BigDecimal.ZERO: position;

    }

    /**
     * Creates an instance.
     *
     * @param inReport The original execution report message.
     * @param inSavedReport the saved persistent report.
     */
    ExecutionReportSummary(ExecutionReport inReport,
                           PersistentReport inSavedReport) {
        setReport(inSavedReport);
        mOrderID = inReport.getOrderID();
        mOrigOrderID = inReport.getOriginalOrderID();
        MSymbol symbol = inReport.getSymbol();
        mSymbol = symbol == null? null: symbol.getFullSymbol();
        mSide = inReport.getSide();
        mCumQuantity = inReport.getCumulativeQuantity();
        mAvgPrice = inReport.getAveragePrice();
        mLastQuantity = inReport.getLastQuantity();
        mLastPrice = inReport.getLastPrice();
        mOrderStatus = inReport.getOrderStatus();
        mSendingTime = inReport.getSendingTime();
    }

    /**
     * Saves this instance within an existing transaction.
     *
     * @param inManager the entity manager instance
     * @param inContext the persistence context
     *
     * @throws PersistenceException if there were errors.
     */
    void localSave(EntityManager inManager,
                   PersistContext inContext)
            throws PersistenceException {
        super.saveLocal(inManager, inContext);
    }

    @Override
    protected void preSaveLocal(EntityManager em, PersistContext context)
            throws PersistenceException {
        super.preSaveLocal(em, context);
        //Set the root ID on the object.
        if(getOrigOrderID() == null) {
            //This is the first order in this chain
            setRootID(getOrderID());
        } else {
            //fetch the rootID from the original order
            Query query = em.createNamedQuery("rootIDForOrderID");  //$NON-NLS-1$
            query.setParameter("orderID", getOrigOrderID());  //$NON-NLS-1$
            List list = query.getResultList();
            if (!list.isEmpty()) {
                setRootID((OrderID) list.get(0));
            } else {
                Messages.LOG_ROOT_ID_NOT_FOUND.warn(this, getOrderID(),
                        getOrigOrderID());
                setRootID(getOrigOrderID());
            }
        }
    }

    @OneToOne(optional = false)
    PersistentReport getReport() {
        return mReport;
    }
    
    private void setReport(PersistentReport inReport) {
        mReport = inReport;
    }

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name="value",
                    column = @Column(name = "rootID", nullable = false))})
    @Column(nullable = false)
    OrderID getRootID() {
        return mRootID;
    }

    private void setRootID(OrderID inRootID) {
        mRootID = inRootID;
    }

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name="value",
                    column = @Column(name = "orderID", nullable = false))})
    OrderID getOrderID() {
        return mOrderID;
    }

    private void setOrderID(OrderID inOrderID) {
        mOrderID = inOrderID;
    }

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name="value",
                    column = @Column(name = "origOrderID"))})
    OrderID getOrigOrderID() {
        return mOrigOrderID;
    }

    private void setOrigOrderID(OrderID inOrigOrderID) {
        mOrigOrderID = inOrigOrderID;
    }

    @Column(nullable = false)
    String getSymbol() {
        return mSymbol;
    }

    private void setSymbol(String inSymbol) {
        mSymbol = inSymbol;
    }

    @Column(nullable = false)
    Side getSide() {
        return mSide;
    }

    private void setSide(Side inSide) {
        mSide = inSide;
    }

    @Column(precision = 15, scale = 5, nullable = false)
    BigDecimal getCumQuantity() {
        return mCumQuantity;
    }

    private void setCumQuantity(BigDecimal inCumQuantity) {
        mCumQuantity = inCumQuantity;
    }

    @Column(precision = 15, scale = 5, nullable = false)
    BigDecimal getAvgPrice() {
        return mAvgPrice;
    }

    private void setAvgPrice(BigDecimal inAvgPrice) {
        mAvgPrice = inAvgPrice;
    }

    @Column(precision = 15, scale = 5)
    BigDecimal getLastQuantity() {
        return mLastQuantity;
    }

    private void setLastQuantity(BigDecimal inLastQuantity) {
        mLastQuantity = inLastQuantity;
    }

    @Column(precision = 15, scale = 5)
    BigDecimal getLastPrice() {
        return mLastPrice;
    }

    private void setLastPrice(BigDecimal inLastPrice) {
        mLastPrice = inLastPrice;
    }

    @Column(nullable = false)
    OrderStatus getOrderStatus() {
        return mOrderStatus;
    }

    private void setOrderStatus(OrderStatus inOrderStatus) {
        mOrderStatus = inOrderStatus;
    }

    @Column(nullable = false)
    Date getSendingTime() {
        return mSendingTime;
    }

    private void setSendingTime(Date inSendingTime) {
        mSendingTime = inSendingTime;
    }

    /**
     * Defined to get JPA to work.
     */
    ExecutionReportSummary() {
    }

    private OrderID mRootID;
    private OrderID mOrderID;
    private OrderID mOrigOrderID;
    private String mSymbol;
    private Side mSide;
    private BigDecimal mCumQuantity;
    private BigDecimal mAvgPrice;
    private BigDecimal mLastQuantity;
    private BigDecimal mLastPrice;
    private OrderStatus mOrderStatus;
    private Date mSendingTime;
    private PersistentReport mReport;
    /**
     * The entity name as is used in various JPQL Queries
     */
    static final String ENTITY_NAME = ExecutionReportSummary.class.getSimpleName();
    private static final long serialVersionUID = 1L;
}
