package org.marketcetera.ors.history;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.*;

import org.marketcetera.ors.security.SimpleUser;
import org.marketcetera.persist.EntityBase;
import org.marketcetera.persist.PersistenceException;
import org.marketcetera.trade.*;
import org.marketcetera.util.misc.ClassVersion;

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
@NamedQueries({
    @NamedQuery(name="rootIDForOrderID",query="select e.rootID from ExecutionReportSummary e where e.orderID = :orderID"),
    @NamedQuery(name="setIsOpen",query="update ExecutionReportSummary e set e.isOpen = false where e.rootID = :rootID and e.id != :Id") })
@SqlResultSetMappings({
    @SqlResultSetMapping(name = "positionForSymbol",
            columns = {@ColumnResult(name = "position")}),
    @SqlResultSetMapping(name = "eqAllPositions",
            columns = {
                @ColumnResult(name = "symbol"),
                @ColumnResult(name = "account"),
                @ColumnResult(name = "actor"),
                @ColumnResult(name = "position")
                    }),
    @SqlResultSetMapping(name = "crAllPositions",
           columns = {
                @ColumnResult(name = "symbol"),
                @ColumnResult(name = "account"),
                @ColumnResult(name = "actor"),
                @ColumnResult(name = "position")
                   }),
   @SqlResultSetMapping(name = "futAllPositions",
                        columns = {
           @ColumnResult(name = "symbol"),
           @ColumnResult(name = "expiry"),
           @ColumnResult(name = "account"),
           @ColumnResult(name = "actor"),
           @ColumnResult(name = "position")
   }),
    @SqlResultSetMapping(name = "optAllPositions",
            columns = {
                @ColumnResult(name = "symbol"),
                @ColumnResult(name = "expiry"),
                @ColumnResult(name = "strikePrice"),
                @ColumnResult(name = "optionType"),
                @ColumnResult(name = "account"),
                @ColumnResult(name = "actor"),
                @ColumnResult(name = "position")
                    })
        })
// CD 26-Apr-2012 ORS-84
// The position queries should ignore PENDING ERs. This is done by excluding ORS with particular order status values.
// Hibernate maps enums to 0-based index values, so 7, 11, and 15 map to values in the OrderStatus enum, the PENDING values.
@NamedNativeQueries({
    @NamedNativeQuery(name = "eqPositionForSymbol",query = "select " +
            "sum(case when e.side = :sideBuy then e.cumQuantity else -e.cumQuantity end) as position " +
            "from execreports e " +
            "where e.symbol = :symbol " +
            "and (e.securityType is null " +
            "or e.securityType = :securityType) " +
            "and e.sendingTime <= :sendingTime " +
            "and (:allViewers or e.viewer_id = :viewerID) " +
            "and e.id = " +
            "(select max(s.id) from execreports s where s.rootID = e.rootID and s.orderStatus not in (7,11,15))",
            resultSetMapping = "positionForSymbol"),
    @NamedNativeQuery(name = "eqAllPositions",query = "select " +
            "e.symbol as symbol, e.account as account, r.actor_id as actor, sum(case when e.side = :sideBuy then e.cumQuantity else -e.cumQuantity end) as position " +
            "from execreports e " +
            "join reports r on (e.report_id=r.id) " +
            "where e.sendingTime <= :sendingTime " +
            "and (e.securityType is null " +
            "or e.securityType = :securityType) " +
            "and (:allViewers or e.viewer_id = :viewerID) " +
            "and e.id = " +
            "(select max(s.id) from execreports s where s.rootID = e.rootID and s.orderStatus not in (7,11,15)) " +
            "group by symbol, account, actor having position <> 0",
            resultSetMapping = "eqAllPositions"),
    @NamedNativeQuery(name = "crPositionForSymbol",query = "select " +
            "sum(case when e.side = :sideBuy then e.cumQuantity else -e.cumQuantity end) as position " +
            "from execreports e " +
            "where e.symbol = :symbol " +
            "and (e.securityType is null " +
            "or e.securityType = :securityType) " +
            "and e.sendingTime <= :sendingTime " +
            "and (:allViewers or e.viewer_id = :viewerID) " +
            "and e.id = " +
            "(select max(s.id) from execreports s where s.rootID = e.rootID and s.orderStatus not in (7,11,15))",
            resultSetMapping = "positionForSymbol"),
    @NamedNativeQuery(name = "crAllPositions",query = "select " +
            "e.symbol as symbol, e.account as account, r.actor_id as actor, sum(case when e.side = :sideBuy then e.cumQuantity else -e.cumQuantity end) as position " +
            "from execreports e " +
            "join reports r on (e.report_id=r.id) " +
            "where e.sendingTime <= :sendingTime " +
            "and (e.securityType is null " +
            "or e.securityType = :securityType) " +
            "and (:allViewers or e.viewer_id = :viewerID) " +
            "and e.id = " +
            "(select max(s.id) from execreports s where s.rootID = e.rootID and s.orderStatus not in (7,11,15)) " +
            "group by symbol, account, actor having position <> 0",
             resultSetMapping = "crAllPositions"),    
    @NamedNativeQuery(name = "futPositionForSymbol",query = "select " +
            "sum(case when e.side = :sideBuy then e.cumQuantity else -e.cumQuantity end) as position " +
            "from execreports e " +
            "where e.symbol = :symbol " +
            "and e.securityType = :securityType " +
            "and e.sendingTime <= :sendingTime " +
            "and (:allViewers or e.viewer_id = :viewerID) " +
            "and e.id = " +
            "(select max(s.id) from execreports s where s.rootID = e.rootID and s.orderStatus not in (7,11,15))",
            resultSetMapping = "positionForSymbol"),
    @NamedNativeQuery(name = "futAllPositions",query = "select " +
            "e.symbol as symbol, e.expiry as expiry, e.account as account, r.actor_id as actor, sum(case when e.side = :sideBuy then e.cumQuantity else -e.cumQuantity end) as position " +
            "from execreports e " +
            "join reports r on (e.report_id=r.id) " +
            "where e.sendingTime <= :sendingTime " +
            "and e.securityType = :securityType " +
            "and (:allViewers or e.viewer_id = :viewerID) " +
            "and e.id = " +
            "(select max(s.id) from execreports s where s.rootID = e.rootID and s.orderStatus not in (7,11,15)) " +
            "group by symbol, account, actor having position <> 0",
            resultSetMapping = "futAllPositions"),
    @NamedNativeQuery(name = "optPositionForTuple",query = "select " +
            "sum(case when e.side = :sideBuy then e.cumQuantity else -e.cumQuantity end) as position " +
            "from execreports e " +
            "where e.symbol = :symbol " +
            "and e.securityType = :securityType " +
            "and e.expiry = :expiry " +
            "and e.strikePrice = :strikePrice " +
            "and e.optionType = :optionType " +
            "and e.sendingTime <= :sendingTime " +
            "and (:allViewers or e.viewer_id = :viewerID) " +
            "and e.id = " +
            "(select max(s.id) from execreports s where s.rootID = e.rootID and s.orderStatus not in (7,11,15))",
            resultSetMapping = "positionForSymbol"),
    @NamedNativeQuery(name = "optAllPositions",query = "select " +
            "e.symbol as symbol, e.expiry as expiry, e.strikePrice as strikePrice, e.optionType as optionType, e.account as account, r.actor_id as actor, sum(case when e.side = :sideBuy then e.cumQuantity else -e.cumQuantity end) as position " +
            "from execreports e " +
            "join reports r on (e.report_id=r.id) " +
            "where e.sendingTime <= :sendingTime " +
            "and e.securityType = :securityType " +
            "and (:allViewers or e.viewer_id = :viewerID) " +
            "and e.id = " +
            "(select max(s.id) from execreports s where s.rootID = e.rootID and s.orderStatus not in (7,11,15)) " +
            "group by symbol, expiry, strikePrice, optionType, account, actor having position <> 0",
            resultSetMapping = "optAllPositions"),
    @NamedNativeQuery(name = "optPositionsForRoots",query = "select " +
            "e.symbol as symbol, e.expiry as expiry, e.strikePrice as strikePrice, e.optionType as optionType, e.account as account, r.actor_id as actor, sum(case when e.side = :sideBuy then e.cumQuantity else -e.cumQuantity end) as position " +
            "from execreports e " +
            "join reports r on (e.report_id=r.id) " +
            "where e.sendingTime <= :sendingTime " +
            "and e.securityType = :securityType " +
            "and e.symbol in (:symbols) " +
            "and (:allViewers or e.viewer_id = :viewerID) " +
            "and e.id = " +
            "(select max(s.id) from execreports s where s.rootID = e.rootID and s.orderStatus not in (7,11,15)) " +
            "group by symbol, expiry, strikePrice, optionType, account, actor having position <> 0",
            resultSetMapping = "optAllPositions"),
    @NamedNativeQuery(name="openOrders",query="select * from execreports e where e.isOpen=true and (:allViewers=true or e.viewer_id=:viewerID)",resultClass=ExecutionReportSummary.class),
    @NamedNativeQuery(name="deleteReportsFor",query="delete from execreports where report_id=:id",resultClass=ExecutionReportSummary.class)
        })

public class ExecutionReportSummary extends EntityBase {
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
        Instrument instrument = inReport.getInstrument();
        if (instrument != null) {
            mSecurityType = instrument.getSecurityType();
            mSymbol = instrument.getSymbol();
            InstrumentSummaryFields<?> summaryFields = InstrumentSummaryFields.SELECTOR.forInstrument(instrument);
            mOptionType = summaryFields.getOptionType(instrument);
            mStrikePrice = summaryFields.getStrikePrice(instrument);
            mExpiry = summaryFields.getExpiry(instrument);
        }
        mAccount = inReport.getAccount();
        mSide = inReport.getSide();
        mCumQuantity = inReport.getCumulativeQuantity();
        mAvgPrice = inReport.getAveragePrice();
        mLastQuantity = inReport.getLastQuantity();
        mLastPrice = inReport.getLastPrice();
        mOrderStatus = inReport.getOrderStatus();
        mSendingTime = inReport.getSendingTime();
        mViewer = inSavedReport.getViewer();
        mIsOpen = inReport.isCancelable();
    }

    /**
     * Saves this instance within an existing transaction.
     *
     * @param inManager the entity manager instance
     * @param inContext the persistence context
     *
     * @throws PersistenceException if there were errors.
     */
//    void localSave(EntityManager inManager,
//                   PersistContext inContext)
//            throws PersistenceException {
//        super.saveLocal(inManager, inContext);
//    }

//    @Override
//    protected void preSaveLocal(EntityManager em, PersistContext context)
//            throws PersistenceException {
//        super.preSaveLocal(em, context);
//        // CD 17-Mar-2011 ORS-79
//        // we need to find the correct root ID of the incoming ER. for cancels and cancel/replaces,
//        //  this is easy - we can look up the root ID from the origOrderID. for a partial fill or fill
//        //  of an original order, this is also easy - the rootID is just the orderID. the difficult case
//        //  is a partial fill or fill of a replaced order. the origOrderID won't be present (not required)
//        //  but there still exists an order chain to be respected or position reporting will be broken.
//        //  therefore, the algorithm should be:
//        // if the original orderID is present, use the root from that order
//        // if it's not present, look for the rootID of an existing record with the same orderID
//        Query query = em.createNamedQuery("rootIDForOrderID");  //$NON-NLS-1$
//        SLF4JLoggerProxy.debug(ExecutionReportSummary.class,
//                               "Searching for rootID for {}",  //$NON-NLS-1$
//                               getOrderID());
//        if(getOrigOrderID() == null) {
//            SLF4JLoggerProxy.debug(ExecutionReportSummary.class,
//                                   "No origOrderID present, using orderID for query");  //$NON-NLS-1$
//            query.setParameter("orderID",  //$NON-NLS-1$
//                               getOrderID());
//        } else {
//            SLF4JLoggerProxy.debug(ExecutionReportSummary.class,
//                                   "Using origOrderID {} for query",  //$NON-NLS-1$
//                                   getOrigOrderID());
//            query.setParameter("orderID",  //$NON-NLS-1$
//                               getOrigOrderID());
//        }
//        List<?> list = query.getResultList();
//        if(list.isEmpty()) {
//            SLF4JLoggerProxy.debug(ExecutionReportSummary.class,
//                                   "No other orders match this orderID - this must be the first in the order chain");  //$NON-NLS-1$
//            // this is the first order in this chain
//            setRootID(getOrderID());
//        } else {
//            OrderID rootID = (OrderID)list.get(0);
//            SLF4JLoggerProxy.debug(ExecutionReportSummary.class,
//                                   "Using {} for rootID",  //$NON-NLS-1$
//                                   rootID);
//            setRootID(rootID);
//        }
//    }
    /* (non-Javadoc)
     * @see org.marketcetera.persist.EntityBase#postSaveLocal(javax.persistence.EntityManager, org.marketcetera.persist.EntityBase, org.marketcetera.persist.PersistContext)
     */
//    @Override
//    protected void postSaveLocal(EntityManager inEntityManager,
//                                 EntityBase inMerged,
//                                 PersistContext inContext)
//            throws PersistenceException
//    {
//        super.postSaveLocal(inEntityManager,
//                            inMerged,
//                            inContext);
//        // CD 27-Jul-2013 MATP-350
//        // mark all other orders of this family as closed
//        Query query = inEntityManager.createNamedQuery("setIsOpen"); //$NON-NLS-1$
//        ExecutionReportSummary summaryReport = (ExecutionReportSummary)inMerged;
//        query.setParameter("Id",summaryReport.getId()).setParameter("rootID",summaryReport.getRootID()).executeUpdate();
//    }
//    @OneToOne(optional = false)
//    PersistentReport getReport() {
//        return mReport;
//    }
    
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

    @SuppressWarnings("unused")
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

    @SuppressWarnings("unused")
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

    @SuppressWarnings("unused")
    private void setOrigOrderID(OrderID inOrigOrderID) {
        mOrigOrderID = inOrigOrderID;
    }

    SecurityType getSecurityType() {
        return mSecurityType;
    }

    @SuppressWarnings("unused")
    private void setSecurityType(SecurityType inSecurityType) {
        mSecurityType = inSecurityType;
    }

    @Column(nullable = false)
    String getSymbol() {
        return mSymbol;
    }

    @SuppressWarnings("unused")
    private void setSymbol(String inSymbol) {
        mSymbol = inSymbol;
    }

    String getExpiry() {
        return mExpiry;
    }

    @SuppressWarnings("unused")
    private void setExpiry(String inExpiry) {
        mExpiry = inExpiry;
    }

    @Column(precision = DECIMAL_PRECISION, scale = DECIMAL_SCALE, nullable = true)
    BigDecimal getStrikePrice() {
        return mStrikePrice;
    }

    @SuppressWarnings("unused")
    private void setStrikePrice(BigDecimal inStrikePrice) {
        mStrikePrice = inStrikePrice;
    }

    OptionType getOptionType() {
        return mOptionType;
    }

    @SuppressWarnings("unused")
    private void setOptionType(OptionType inOptionType) {
        mOptionType = inOptionType;
    }

    String getAccount() {
        return mAccount;
    }

    @SuppressWarnings("unused")
    private void setAccount(String inAccount) {
        mAccount = inAccount;
    }

    @Column(nullable = false)
    Side getSide() {
        return mSide;
    }

    @SuppressWarnings("unused")
    private void setSide(Side inSide) {
        mSide = inSide;
    }

    @Column(precision = DECIMAL_PRECISION, scale = DECIMAL_SCALE, nullable = false)
    BigDecimal getCumQuantity() {
        return mCumQuantity;
    }

    @SuppressWarnings("unused")
    private void setCumQuantity(BigDecimal inCumQuantity) {
        mCumQuantity = inCumQuantity;
    }

    @Column(precision = DECIMAL_PRECISION, scale = DECIMAL_SCALE, nullable = false)
    BigDecimal getAvgPrice() {
        return mAvgPrice;
    }

    @SuppressWarnings("unused")
    private void setAvgPrice(BigDecimal inAvgPrice) {
        mAvgPrice = inAvgPrice;
    }

    @Column(precision = DECIMAL_PRECISION, scale = DECIMAL_SCALE)
    BigDecimal getLastQuantity() {
        return mLastQuantity;
    }

    @SuppressWarnings("unused")
    private void setLastQuantity(BigDecimal inLastQuantity) {
        mLastQuantity = inLastQuantity;
    }

    @Column(precision = DECIMAL_PRECISION, scale = DECIMAL_SCALE)
    BigDecimal getLastPrice() {
        return mLastPrice;
    }

    @SuppressWarnings("unused")
    private void setLastPrice(BigDecimal inLastPrice) {
        mLastPrice = inLastPrice;
    }

    @Column(nullable = false)
    OrderStatus getOrderStatus() {
        return mOrderStatus;
    }

    @SuppressWarnings("unused")
    private void setOrderStatus(OrderStatus inOrderStatus) {
        mOrderStatus = inOrderStatus;
    }

    @Column(nullable = false)
    Date getSendingTime() {
        return mSendingTime;
    }

    @SuppressWarnings("unused")
    private void setSendingTime(Date inSendingTime) {
        mSendingTime = inSendingTime;
    }

    @ManyToOne
    public SimpleUser getViewer() {
        return mViewer;
    }

    @SuppressWarnings("unused")
    private void setViewer(SimpleUser inViewer) {
        mViewer = inViewer;
    }
    /**
     * Gets the is open value.
     *
     * @return a <code>boolean</code> value
     */
    @Column
    public boolean getIsOpen()
    {
        return mIsOpen;
    }
    /**
     * Sets the is open value.
     *
     * @param a <code>boolean</code> value
     */
    public void setIsOpen(boolean inIsOpen)
    {
        mIsOpen = inIsOpen;
    }
    /**
    *
    *
    * @return
    */
   public PersistentReport getReport()
   {
       return mReport;
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
    private SecurityType mSecurityType;
    private String mSymbol;
    private String mExpiry;
    private BigDecimal mStrikePrice;
    private OptionType mOptionType;
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
    private boolean mIsOpen;
    /**
     * The attribute viewer used in JPQL queries
     */
    static final String ATTRIBUTE_VIEWER = "viewer";  //$NON-NLS-1$
    /**
     * attribute isOpen used in JPQL queries
     */
    static final String ATTRIBUTE_IS_OPEN = "isOpen"; //$NON-NLS-1$
    /**
     * The entity name as is used in various JPQL Queries
     */
    static final String ENTITY_NAME = ExecutionReportSummary.class.getSimpleName();
    /**
     * The scale used for storing all decimal values.
     */
    static final int DECIMAL_SCALE = 7;
    /**
     * The precision used for storing all decimal values.
     */
    static final int DECIMAL_PRECISION = 17;
    private static final long serialVersionUID = -6939295144839290006L;
}
