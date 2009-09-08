package org.marketcetera.ors.history;

import org.marketcetera.core.position.PositionKey;
import org.marketcetera.core.position.impl.PositionKeyImpl;
import org.marketcetera.ors.security.SimpleUser;
import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.persist.*;
import org.marketcetera.persist.PersistenceException;
import org.marketcetera.trade.MSymbol;
import org.marketcetera.trade.OrderID;
import org.marketcetera.trade.UserID;
import org.marketcetera.trade.Side;
import org.marketcetera.trade.ExecutionReport;
import org.marketcetera.trade.OrderStatus;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/* $License$ */
/**
 * Maintains a summary of fields of an ExecutionReport
 * to aid Position calculations. The lifecycle of this object
 * is controlled by {@link PersistentReport}
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$")
@Entity
@Table(name="execreports")

@NamedQuery(name = "rootIDForOrderID",
        query = "select e.rootID from ExecutionReportSummary e " +
                "where e.orderID = :orderID")

@SqlResultSetMappings({
    @SqlResultSetMapping(name = "positionForSymbol",
            columns = {@ColumnResult(name = "position")}),
    @SqlResultSetMapping(name = "allPositions",
            columns = {
                @ColumnResult(name = "symbol"),
                @ColumnResult(name = "account"),
                @ColumnResult(name = "actor"),
                @ColumnResult(name = "position")
                    })
        })

@NamedNativeQueries({
    @NamedNativeQuery(name = "positionForSymbol",query = "select " +
            "sum(case when e.side = :sideBuy then e.cumQuantity else -e.cumQuantity end) as position " +
            "from execreports e " +
            "where e.symbol = :symbol " +
            "and e.sendingTime <= :sendingTime " +
            "and (:allViewers or e.viewer_id = :viewerID) " +
            "and e.id = " +
            "(select max(s.id) from execreports s where s.rootID = e.rootID)",
            resultSetMapping = "positionForSymbol"),
    @NamedNativeQuery(name = "allPositions",query = "select " +
            "e.symbol as symbol, e.account as account, r.actor_id as actor, sum(case when e.side = :sideBuy then e.cumQuantity else -e.cumQuantity end) as position " +
            "from execreports e " +
            "join reports r on (e.report_id=r.id) " +
            "where e.sendingTime <= :sendingTime " +
            "and (:allViewers or e.viewer_id = :viewerID) " +
            "and e.id = " +
            "(select max(s.id) from execreports s where s.rootID = e.rootID) " +
            "group by symbol, account, actor having position <> 0",
            resultSetMapping = "allPositions")
        })

class ExecutionReportSummary extends EntityBase {

    /**
     * Gets the current aggregate position for the symbol based on
     * execution reports received before the supplied time, and which
     * are visible to the given user.
     *
     * <p>
     * Buy trades result in positive positions. All other kinds of trades
     * result in negative positions.
     *
     * @param inUser the user making the query. Cannot be null.
     * @param inDate the time. execution reports with sending time values less
     * than this time are included in this calculation.
     * @param inSymbol the symbol for which this position needs to be computed
     *
     * @return the aggreget position for the symbol.
     *
     * @throws PersistenceException if there were errors retrieving the
     * position.
     */
    static BigDecimal getPositionForSymbol
        (final SimpleUser inUser,
         final Date inDate,
         final MSymbol inSymbol)
        throws PersistenceException
    {
        BigDecimal position = executeRemote(new Transaction<BigDecimal>() {
            private static final long serialVersionUID = 1L;

            @Override
            public BigDecimal execute(EntityManager em, PersistContext context) {
                Query query = em.createNamedQuery(
                        "positionForSymbol");  //$NON-NLS-1$

                query.setParameter("viewerID",inUser.getUserID().getValue());  //$NON-NLS-1$
                query.setParameter("allViewers",inUser.isSuperuser());  //$NON-NLS-1$
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
     * Returns the aggregate position of each (symbol,account,actor)
     * tuple based on all reports received for each tuple on or before
     * the supplied date, and which are visible to the given user.
     *
     * <p> Buy trades result in positive positions. All other kinds of
     * trades result in negative positions.
     *
     * @param inUser the user making the query. Cannot be null.
     * @param inDate the date to compare with all the reports. Only
     * the reports that were received on or prior to this date will be
     * used in this calculation.  Cannot be null.
     *
     * @return the position map.
     *
     * @throws PersistenceException if there were errors retrieving the
     * position map.
     */
    static Map<PositionKey, BigDecimal> getPositionsAsOf
        (final SimpleUser inUser,
         final Date inDate)
        throws PersistenceException
    {
        return executeRemote(new Transaction<Map<PositionKey, BigDecimal>>() {
            private static final long serialVersionUID = 1L;

            @Override
            public Map<PositionKey, BigDecimal> execute(EntityManager em,
                                                    PersistContext context) {
                Query query = em.createNamedQuery(
                        "allPositions");  //$NON-NLS-1$
                query.setParameter("viewerID",inUser.getUserID().getValue());  //$NON-NLS-1$
                query.setParameter("allViewers",inUser.isSuperuser());  //$NON-NLS-1$
                query.setParameter("sideBuy", Side.Buy.ordinal());  //$NON-NLS-1$
                query.setParameter("sendingTime", inDate,  //$NON-NLS-1$
                        TemporalType.TIMESTAMP);
                HashMap<PositionKey, BigDecimal> map =
                        new HashMap<PositionKey, BigDecimal>();
                List<?> list = query.getResultList();
                Object[] columns;
                for(Object o: list) {
                    columns = (Object[]) o;
                    //4 columns
                    if(columns.length > 1) {
                        //first one is the symbol
                        //second one is the account
                        //third one is the actor ID
                        //fourth one is the position
                        map.put(new PositionKeyImpl
                                ((String)columns[0],
                                 (String)columns[1],
                                 ((columns[2]==null)?null:
                                  ((BigInteger)columns[2]).toString())),
                                 (BigDecimal)columns[3]);
                    }
                }
                return map;
            }
        }, null);

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
        mAccount = inReport.getAccount();
        mSide = inReport.getSide();
        mCumQuantity = inReport.getCumulativeQuantity();
        mAvgPrice = inReport.getAveragePrice();
        mLastQuantity = inReport.getLastQuantity();
        mLastPrice = inReport.getLastPrice();
        mOrderStatus = inReport.getOrderStatus();
        mSendingTime = inReport.getSendingTime();
        mViewer = inSavedReport.getViewer();
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
            List<?> list = query.getResultList();
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

    String getAccount() {
        return mAccount;
    }

    private void setAccount(String inAccount) {
        mAccount = inAccount;
    }

    @Column(nullable = false)
    Side getSide() {
        return mSide;
    }

    private void setSide(Side inSide) {
        mSide = inSide;
    }

    @Column(precision = DECIMAL_PRECISION, scale = DECIMAL_SCALE, nullable = false)
    BigDecimal getCumQuantity() {
        return mCumQuantity;
    }

    private void setCumQuantity(BigDecimal inCumQuantity) {
        mCumQuantity = inCumQuantity;
    }

    @Column(precision = DECIMAL_PRECISION, scale = DECIMAL_SCALE, nullable = false)
    BigDecimal getAvgPrice() {
        return mAvgPrice;
    }

    private void setAvgPrice(BigDecimal inAvgPrice) {
        mAvgPrice = inAvgPrice;
    }

    @Column(precision = DECIMAL_PRECISION, scale = DECIMAL_SCALE)
    BigDecimal getLastQuantity() {
        return mLastQuantity;
    }

    private void setLastQuantity(BigDecimal inLastQuantity) {
        mLastQuantity = inLastQuantity;
    }

    @Column(precision = DECIMAL_PRECISION, scale = DECIMAL_SCALE)
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

    @ManyToOne
    public SimpleUser getViewer() {
        return mViewer;
    }

    private void setViewer(SimpleUser inViewer) {
        mViewer = inViewer;
    }

    @Transient
    UserID getViewerID() {
        if (getViewer()==null) {
            return null;
        }
        return getViewer().getUserID();
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
    private String mAccount;
    private Side mSide;
    private BigDecimal mCumQuantity;
    private BigDecimal mAvgPrice;
    private BigDecimal mLastQuantity;
    private BigDecimal mLastPrice;
    private OrderStatus mOrderStatus;
    private Date mSendingTime;
    private SimpleUser mViewer; 
    private PersistentReport mReport;
    /**
     * The attribute viewer used in JPQL queries
     */
    static final String ATTRIBUTE_VIEWER = "viewer";  //$NON-NLS-1$
    /**
     * The entity name as is used in various JPQL Queries
     */
    static final String ENTITY_NAME = ExecutionReportSummary.class.getSimpleName();
    /**
     * The scale used for storing all decimal values.
     */
    static final int DECIMAL_SCALE = 5;
    /**
     * The precision used for storing all decimal values.
     */
    static final int DECIMAL_PRECISION = 15;
    private static final long serialVersionUID = 1L;
}
