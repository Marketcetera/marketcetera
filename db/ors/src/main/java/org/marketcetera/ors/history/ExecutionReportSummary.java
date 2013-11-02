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
@Entity
@Table(name="exec_reports")
@NamedQueries({
    @NamedQuery(name="ExecutionReportSummary.findRootIDForOrderID",query="select e.rootOrderId from ExecutionReportSummary e where e.orderId=?1"),
    @NamedQuery(name="setIsOpen",query="update ExecutionReportSummary e set e.isOpen=false where e.rootOrderId=:rootID and e.id!=:Id") })
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
            "sum(case when e.side = :sideBuy then e.cum_qy else -e.cum_qty end) as position " +
            "from exec_reports e " +
            "where e.symbol = :symbol " +
            "and (e.security_type is null " +
            "or e.security_type = :securityType) " +
            "and e.send_time <= :sendingTime " +
            "and (:allViewers or e.viewer_id = :viewerID) " +
            "and e.id = " +
            "(select max(s.id) from exec_reports s where s.root_id = e.root_id and s.ord_status not in (7,11,15))",
            resultSetMapping = "positionForSymbol"),
    @NamedNativeQuery(name = "eqAllPositions",query = "select " +
            "e.symbol as symbol, e.account as account, r.actor_id as actor, sum(case when e.side = :sideBuy then e.cumQuantity else -e.cumQuantity end) as position " +
            "from exec_reports e " +
            "join reports r on (e.report_id=r.id) " +
            "where e.sendingTime <= :sendingTime " +
            "and (e.securityType is null " +
            "or e.securityType = :securityType) " +
            "and (:allViewers or e.viewer_id = :viewerID) " +
            "and e.id = " +
            "(select max(s.id) from exec_reports s where s.rootID = e.rootID and s.orderStatus not in (7,11,15)) " +
            "group by symbol, account, actor having position <> 0",
            resultSetMapping = "eqAllPositions"),
    @NamedNativeQuery(name = "ExecutionReportSummary.findCurrencyPositionForSymbol",query = "select " +
            "sum(case when e.side = ?1 then e.cum_qty else -e.cum_qty end) as position " +
            "from exec_reports e " +
            "where e.symbol = ?2 " +
            "and (e.security_type is null " +
            "or e.security_type = ?3) " +
            "and e.send_time <= ?4 " +
            "and (?5 or e.viewer_id = ?6) " +
            "and e.id = " +
            "(select max(s.id) from exec_reports s where s.root_order_id = e.root_order_id and s.ord_status not in (7,11,15))",
            resultSetMapping = "positionForSymbol"),
    @NamedNativeQuery(name = "crAllPositions",query = "select " +
            "e.symbol as symbol, e.account as account, r.actor_id as actor, sum(case when e.side = :sideBuy then e.cumQuantity else -e.cumQuantity end) as position " +
            "from exec_reports e " +
            "join reports r on (e.report_id=r.id) " +
            "where e.sendingTime <= :sendingTime " +
            "and (e.securityType is null " +
            "or e.securityType = :securityType) " +
            "and (:allViewers or e.viewer_id = :viewerID) " +
            "and e.id = " +
            "(select max(s.id) from exec_reports s where s.rootID = e.rootID and s.orderStatus not in (7,11,15)) " +
            "group by symbol, account, actor having position <> 0",
             resultSetMapping = "crAllPositions"),    
    @NamedNativeQuery(name = "futPositionForSymbol",query = "select " +
            "sum(case when e.side = :sideBuy then e.cumQuantity else -e.cumQuantity end) as position " +
            "from exec_reports e " +
            "where e.symbol = :symbol " +
            "and e.securityType = :securityType " +
            "and e.sendingTime <= :sendingTime " +
            "and (:allViewers or e.viewer_id = :viewerID) " +
            "and e.id = " +
            "(select max(s.id) from exec_reports s where s.rootID = e.rootID and s.orderStatus not in (7,11,15))",
            resultSetMapping = "positionForSymbol"),
    @NamedNativeQuery(name = "futAllPositions",query = "select " +
            "e.symbol as symbol, e.expiry as expiry, e.account as account, r.actor_id as actor, sum(case when e.side = :sideBuy then e.cumQuantity else -e.cumQuantity end) as position " +
            "from exec_reports e " +
            "join reports r on (e.report_id=r.id) " +
            "where e.sendingTime <= :sendingTime " +
            "and e.securityType = :securityType " +
            "and (:allViewers or e.viewer_id = :viewerID) " +
            "and e.id = " +
            "(select max(s.id) from exec_reports s where s.rootID = e.rootID and s.orderStatus not in (7,11,15)) " +
            "group by symbol, account, actor having position <> 0",
            resultSetMapping = "futAllPositions"),
    @NamedNativeQuery(name = "optPositionForTuple",query = "select " +
            "sum(case when e.side = :sideBuy then e.cumQuantity else -e.cumQuantity end) as position " +
            "from exec_reports e " +
            "where e.symbol = :symbol " +
            "and e.securityType = :securityType " +
            "and e.expiry = :expiry " +
            "and e.strikePrice = :strikePrice " +
            "and e.optionType = :optionType " +
            "and e.sendingTime <= :sendingTime " +
            "and (:allViewers or e.viewer_id = :viewerID) " +
            "and e.id = " +
            "(select max(s.id) from exec_reports s where s.rootID = e.rootID and s.orderStatus not in (7,11,15))",
            resultSetMapping = "positionForSymbol"),
    @NamedNativeQuery(name = "optAllPositions",query = "select " +
            "e.symbol as symbol, e.expiry as expiry, e.strikePrice as strikePrice, e.optionType as optionType, e.account as account, r.actor_id as actor, sum(case when e.side = :sideBuy then e.cumQuantity else -e.cumQuantity end) as position " +
            "from exec_reports e " +
            "join reports r on (e.report_id=r.id) " +
            "where e.sendingTime <= :sendingTime " +
            "and e.securityType = :securityType " +
            "and (:allViewers or e.viewer_id = :viewerID) " +
            "and e.id = " +
            "(select max(s.id) from exec_reports s where s.rootID = e.rootID and s.orderStatus not in (7,11,15)) " +
            "group by symbol, expiry, strikePrice, optionType, account, actor having position <> 0",
            resultSetMapping = "optAllPositions"),
    @NamedNativeQuery(name = "optPositionsForRoots",query = "select " +
            "e.symbol as symbol, e.expiry as expiry, e.strikePrice as strikePrice, e.optionType as optionType, e.account as account, r.actor_id as actor, sum(case when e.side = :sideBuy then e.cumQuantity else -e.cumQuantity end) as position " +
            "from exec_reports e " +
            "join reports r on (e.report_id=r.id) " +
            "where e.sendingTime <= :sendingTime " +
            "and e.securityType = :securityType " +
            "and e.symbol in (:symbols) " +
            "and (:allViewers or e.viewer_id = :viewerID) " +
            "and e.id = " +
            "(select max(s.id) from exec_reports s where s.rootID = e.rootID and s.orderStatus not in (7,11,15)) " +
            "group by symbol, expiry, strikePrice, optionType, account, actor having position <> 0",
            resultSetMapping = "optAllPositions"),
    @NamedNativeQuery(name="openOrders",query="select * from exec_reports e where e.isOpen=true and (:allViewers=true or e.viewer_id=:viewerID)",resultClass=ExecutionReportSummary.class),
    @NamedNativeQuery(name="deleteReportsFor",query="delete from exec_reports where report_id=:id",resultClass=ExecutionReportSummary.class)
        })
@ClassVersion("$Id$")
public class ExecutionReportSummary
        extends EntityBase
{
    /**
     * Creates an instance.
     *
     * @param inReport The original execution report message.
     * @param inSavedReport the saved persistent report.
     */
    public ExecutionReportSummary(ExecutionReport inReport,
                                  PersistentReport inSavedReport)
    {
        setReport(inSavedReport);
        orderId = inReport.getOrderID();
        origOrderID = inReport.getOriginalOrderID();
        Instrument instrument = inReport.getInstrument();
        if(instrument != null) {
            securityType = instrument.getSecurityType();
            symbol = instrument.getSymbol();
            InstrumentSummaryFields<?> summaryFields = InstrumentSummaryFields.SELECTOR.forInstrument(instrument);
            optionType = summaryFields.getOptionType(instrument);
            strikePrice = summaryFields.getStrikePrice(instrument);
            expiry = summaryFields.getExpiry(instrument);
        }
        account = inReport.getAccount();
        side = inReport.getSide();
        cumQuantity = inReport.getCumulativeQuantity();
        if(side == Side.Buy) {
            effectiveCumQuantity = cumQuantity;
        } else {
            effectiveCumQuantity = cumQuantity == null ? BigDecimal.ZERO : cumQuantity.negate();
        }
        avgPrice = inReport.getAveragePrice();
        lastQuantity = inReport.getLastQuantity();
        lastPrice = inReport.getLastPrice();
        orderStatus = inReport.getOrderStatus();
        sendingTime = inReport.getSendingTime();
        viewer = inSavedReport.getViewer();
        actor = inSavedReport.getActor();
        isOpen = inReport.isCancelable();
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
        report = inReport;
    }

    OrderID getRootID() {
        return rootOrderId;
    }

    public void setRootID(OrderID inRootID)
    {
        rootOrderId = inRootID;
    }
    /**
     * 
     *
     *
     * @return
     */
    public OrderID getOrderID()
    {
        return orderId;
    }

    @SuppressWarnings("unused")
    private void setOrderID(OrderID inOrderID) {
        orderId = inOrderID;
    }
    /**
     * 
     *
     *
     * @return
     */
    public OrderID getOrigOrderID()
    {
        return origOrderID;
    }

    @SuppressWarnings("unused")
    private void setOrigOrderID(OrderID inOrigOrderID) {
        origOrderID = inOrigOrderID;
    }

    SecurityType getSecurityType() {
        return securityType;
    }

    @SuppressWarnings("unused")
    private void setSecurityType(SecurityType inSecurityType) {
        securityType = inSecurityType;
    }

    String getSymbol() {
        return symbol;
    }

    @SuppressWarnings("unused")
    private void setSymbol(String inSymbol) {
        symbol = inSymbol;
    }

    String getExpiry() {
        return expiry;
    }

    @SuppressWarnings("unused")
    private void setExpiry(String inExpiry) {
        expiry = inExpiry;
    }

    BigDecimal getStrikePrice() {
        return strikePrice;
    }

    @SuppressWarnings("unused")
    private void setStrikePrice(BigDecimal inStrikePrice) {
        strikePrice = inStrikePrice;
    }

    OptionType getOptionType() {
        return optionType;
    }

    @SuppressWarnings("unused")
    private void setOptionType(OptionType inOptionType) {
        optionType = inOptionType;
    }

    String getAccount() {
        return account;
    }

    @SuppressWarnings("unused")
    private void setAccount(String inAccount) {
        account = inAccount;
    }

    Side getSide() {
        return side;
    }

    @SuppressWarnings("unused")
    private void setSide(Side inSide) {
        side = inSide;
    }

    BigDecimal getCumQuantity()
    {
        return cumQuantity;
    }
    /**
     * Get the effectiveCumQuantity value.
     *
     * @return a <code>BigDecimal</code> value
     */
    public BigDecimal getEffectiveCumQuantity()
    {
        return effectiveCumQuantity;
    }
    /**
     * Sets the effectiveCumQuantity value.
     *
     * @param inEffectiveCumQuantity a <code>BigDecimal</code> value
     */
    public void setEffectiveCumQuantity(BigDecimal inEffectiveCumQuantity)
    {
        effectiveCumQuantity = inEffectiveCumQuantity;
    }
    @SuppressWarnings("unused")
    private void setCumQuantity(BigDecimal inCumQuantity) {
        cumQuantity = inCumQuantity;
    }

    BigDecimal getAvgPrice() {
        return avgPrice;
    }

    @SuppressWarnings("unused")
    private void setAvgPrice(BigDecimal inAvgPrice) {
        avgPrice = inAvgPrice;
    }

    BigDecimal getLastQuantity() {
        return lastQuantity;
    }

    @SuppressWarnings("unused")
    private void setLastQuantity(BigDecimal inLastQuantity) {
        lastQuantity = inLastQuantity;
    }

    BigDecimal getLastPrice() {
        return lastPrice;
    }

    @SuppressWarnings("unused")
    private void setLastPrice(BigDecimal inLastPrice) {
        lastPrice = inLastPrice;
    }

    OrderStatus getOrderStatus() {
        return orderStatus;
    }

    @SuppressWarnings("unused")
    private void setOrderStatus(OrderStatus inOrderStatus) {
        orderStatus = inOrderStatus;
    }

    Date getSendingTime() {
        return sendingTime;
    }

    @SuppressWarnings("unused")
    private void setSendingTime(Date inSendingTime) {
        sendingTime = inSendingTime;
    }

    public SimpleUser getViewer() {
        return viewer;
    }

    @SuppressWarnings("unused")
    private void setViewer(SimpleUser inViewer) {
        viewer = inViewer;
    }
    /**
     * Get the actor value.
     *
     * @return a <code>SimpleUser</code> value
     */
    public SimpleUser getActor()
    {
        return actor;
    }
    /**
     * Sets the actor value.
     *
     * @param inActor a <code>SimpleUser</code> value
     */
    public void setActor(SimpleUser inActor)
    {
        actor = inActor;
    }
    /**
     * Gets the is open value.
     *
     * @return a <code>boolean</code> value
     */
    public boolean getIsOpen()
    {
        return isOpen;
    }
    /**
     * Sets the is open value.
     *
     * @param a <code>boolean</code> value
     */
    public void setIsOpen(boolean inIsOpen)
    {
        isOpen = inIsOpen;
    }
    /**
    *
    *
    * @return
    */
   public PersistentReport getReport()
   {
       return report;
   }
    UserID getViewerID() {
        if (getViewer()==null) {
            return null;
        }
        return getViewer().getUserID();
    }
    /**
     * Defined to get JPA to work.
     */
    @SuppressWarnings("unused")
    private ExecutionReportSummary() {}
    /*
    @Embedded
    @AttributeOverrides({@AttributeOverride(name="value",column = @Column(name = "rootID", nullable = false))})
    @Column(nullable = false)
    private OrderID mRootID;
    @Embedded
    @AttributeOverrides({@AttributeOverride(name="value",column = @Column(name = "orderID", nullable = false))})
    private OrderID mOrderID;
    @Embedded
    @AttributeOverrides({@AttributeOverride(name="value",column = @Column(name = "origOrderID"))})
    private OrderID mOrigOrderID;
    @Column(nullable = false)
    private String mSymbol;
    @Column(precision = DECIMAL_PRECISION, scale = DECIMAL_SCALE, nullable = true)
    private BigDecimal mStrikePrice;
    @Column(nullable = false)
    private Side mSide;
    @Column(precision = DECIMAL_PRECISION, scale = DECIMAL_SCALE, nullable = false)
    private BigDecimal mCumQuantity;
    @Column(precision = DECIMAL_PRECISION, scale = DECIMAL_SCALE, nullable = false)
    private BigDecimal mAvgPrice;
    @Column(precision = DECIMAL_PRECISION, scale = DECIMAL_SCALE)
    private BigDecimal mLastQuantity;
    @Column(precision = DECIMAL_PRECISION, scale = DECIMAL_SCALE)
    private BigDecimal mLastPrice;
    @Column(nullable = false)
    private OrderStatus mOrderStatus;
    @Column(nullable = false)
    private Date mSendingTime;
    @ManyToOne
    private SimpleUser mViewer; 
    @Column
    private boolean mIsOpen;
    @Transient
    private SecurityType mSecurityType;
    @Transient
    private String mExpiry;
    @Transient
    private OptionType mOptionType;
    @Transient
    private String mAccount;
    @Transient
    private PersistentReport mReport;
     */
    /**
     * 
     */
    @Embedded
    @AttributeOverrides({@AttributeOverride(name="mValue",column=@Column(name="root_order_id",nullable=false))})
    private OrderID rootOrderId;
    /**
     * 
     */
    @Embedded
    @AttributeOverrides({@AttributeOverride(name="mValue",column=@Column(name="order_id",nullable=false))})
    private OrderID orderId;
    /**
     * 
     */
    @Embedded
    @AttributeOverrides({@AttributeOverride(name="mValue",column=@Column(name="orig_order_id",nullable=true))})
    private OrderID origOrderID;
    /**
     * 
     */
    @Column(name="symbol",nullable=false)
    private String symbol;
    /**
     * 
     */
    @Column(name="strike_price",precision=DECIMAL_PRECISION,scale=DECIMAL_SCALE,nullable=true)
    private BigDecimal strikePrice;
    /**
     * 
     */
    @Column(name="side",nullable=false)
    private Side side;
    /**
     * 
     */
    @Column(name="cum_qty",precision=DECIMAL_PRECISION,scale=DECIMAL_SCALE,nullable=false)
    private BigDecimal cumQuantity;
    /**
     * 
     */
    @Column(name="eff_cum_qty",precision=DECIMAL_PRECISION,scale=DECIMAL_SCALE,nullable=false)
    private BigDecimal effectiveCumQuantity;
    /**
     * 
     */
    @Column(name="avg_price",precision=DECIMAL_PRECISION,scale=DECIMAL_SCALE,nullable=false)
    private BigDecimal avgPrice;
    /**
     * 
     */
    @Column(name="last_qty",precision=DECIMAL_PRECISION,scale=DECIMAL_SCALE,nullable=false)
    private BigDecimal lastQuantity;
    /**
     * 
     */
    @Column(name="last_price",precision=DECIMAL_PRECISION,scale=DECIMAL_SCALE,nullable=true)
    private BigDecimal lastPrice;
    /**
     * 
     */
    @Column(name="ord_status",nullable=false)
    private OrderStatus orderStatus;
    /**
     * 
     */
    @Column(name="send_time",nullable=false)
    private Date sendingTime;
    /**
     * 
     */
    @ManyToOne
    @JoinColumn(name="viewer_id")
    private SimpleUser viewer; 
    /**
     * 
     */
    @ManyToOne
    @JoinColumn(name="actor_id")
    private SimpleUser actor; 
    /**
     * 
     */
    @Column(name="is_open",nullable=false)
    private boolean isOpen;
    /**
     * 
     */
    @Column(name="security_type",nullable=false)
    private SecurityType securityType;
    /**
     * 
     */
    @Column(name="expiry",nullable=true)
    private String expiry;
    /**
     * 
     */
    @Column(name="option_type",nullable=true)
    private OptionType optionType;
    /**
     * 
     */
    @Column(name="account",nullable=true)
    private String account;
    /**
     * 
     */
    @OneToOne(optional=false)
    @JoinColumn(name="report_id")
    private PersistentReport report;
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
    public static final int DECIMAL_SCALE = 7;
    /**
     * The precision used for storing all decimal values.
     */
    public static final int DECIMAL_PRECISION = 17;
    private static final long serialVersionUID = -6939295144839290006L;
}
