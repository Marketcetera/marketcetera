package org.marketcetera.ors.history;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.*;

import org.marketcetera.ors.security.SimpleUser;
import org.marketcetera.persist.EntityBase;
import org.marketcetera.trade.*;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Maintains a summary of fields of an ExecutionReport to aid Position calculations.
 *
 * @author anshul@marketcetera.com
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 1.0.0
 */
@Entity
@Table(name="exec_reports")
@NamedQueries({
    @NamedQuery(name="ExecutionReportSummary.findRootIDForOrderID",query="select e.rootOrderId from ExecutionReportSummary e where e.orderId=?1"),
    @NamedQuery(name="ExecutionReportSummary.setIsOpen",query="update ExecutionReportSummary e set e.isOpen=false where e.rootOrderId=?1 and e.id!=?2"),
    @NamedQuery(name="ExecutionReportSummary.findOpenOrders",query="select e from ExecutionReportSummary e where e.isOpen=true and (e.viewer.superuser=true or e.viewer=?1)")
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
     * Gets the order id value.
     *
     * @return an <code>OrderID</code> value
     */
    public OrderID getOrderID()
    {
        return orderId;
    }
    /**
     * Sets the order id value.
     *
     * @param inOrderID an <code>OrderID</code> value
     */
    public void setOrderID(OrderID inOrderID)
    {
        orderId = inOrderID;
    }
    /**
     * Gets the original order id value.
     *
     * @return an <code>OrderID</code> value
     */
    public OrderID getOrigOrderID()
    {
        return origOrderID;
    }
    /**
     * Sets the original order id value.
     *
     * @param inOrigOrderID an <code>OrderID</code> value
     */
    public void setOrigOrderID(OrderID inOrigOrderID)
    {
        origOrderID = inOrigOrderID;
    }
    /**
     * Gets the security type value.
     *
     * @return a <code>SecurityType</code> value
     */
    public SecurityType getSecurityType()
    {
        return securityType;
    }
    /**
     * Sets the security type value.
     *
     * @param inSecurityType a <code>SecurityType</code> value
     */
    public void setSecurityType(SecurityType inSecurityType)
    {
        securityType = inSecurityType;
    }
    /**
     * Gets the symbol value.
     *
     * @return a <code>String</code> value
     */
    public String getSymbol()
    {
        return symbol;
    }
    /**
     * Sets the symbol value.
     *
     * @param inSymbol a <code>String</code> value
     */
    public void setSymbol(String inSymbol)
    {
        symbol = inSymbol;
    }
    /**
     * Gets the expiry value.
     *
     * @return a <code>String</code> value
     */
    public String getExpiry()
    {
        return expiry;
    }
    /**
     * Sets the expiry value.
     *
     * @param inExpiry a <code>String</code> value
     */
    public void setExpiry(String inExpiry)
    {
        expiry = inExpiry;
    }
    /**
     * Gets the strike price value.
     *
     * @return a <code>BigDecimal</code> value
     */
    public BigDecimal getStrikePrice()
    {
        return strikePrice;
    }
    /**
     * Sets the strike price value.
     *
     * @param inStrikePrice a <code>BigDecimal</code> value
     */
    public void setStrikePrice(BigDecimal inStrikePrice)
    {
        strikePrice = inStrikePrice;
    }
    /**
     * Gets the option type value.
     *
     * @return an <code>OptionType</code> value
     */
    public OptionType getOptionType()
    {
        return optionType;
    }
    /**
     * Sets the option type value.
     *
     * @param inOptionType an <code>OptionType</code> value
     */
    public void setOptionType(OptionType inOptionType)
    {
        optionType = inOptionType;
    }
    /**
     * Gets the account value.
     *
     * @return a <code>String</code> value
     */
    public String getAccount()
    {
        return account;
    }
    /**
     * Sets the account value.
     *
     * @param inAccount a <code>String</code> value
     */
    public void setAccount(String inAccount)
    {
        account = inAccount;
    }
    /**
     * Get the rootOrderId value.
     *
     * @return an <code>OrderID</code> value
     */
    public OrderID getRootOrderID()
    {
        return rootOrderId;
    }
    /**
     * Sets the rootOrderId value.
     *
     * @param inRootOrderId an <code>OrderID</code> value
     */
    public void setRootOrderID(OrderID inRootOrderId)
    {
        rootOrderId = inRootOrderId;
    }
    /**
     * Get the side value.
     *
     * @return a <code>Side</code> value
     */
    public Side getSide()
    {
        return side;
    }
    /**
     * Sets the side value.
     *
     * @param inSide a <code>Side</code> value
     */
    public void setSide(Side inSide)
    {
        side = inSide;
    }
    /**
     * Get the cumQuantity value.
     *
     * @return a <code>BigDecimal</code> value
     */
    public BigDecimal getCumQuantity()
    {
        return cumQuantity;
    }
    /**
     * Sets the cumQuantity value.
     *
     * @param inCumQuantity a <code>BigDecimal</code> value
     */
    public void setCumQuantity(BigDecimal inCumQuantity)
    {
        cumQuantity = inCumQuantity;
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
    /**
     * Get the avgPrice value.
     *
     * @return a <code>BigDecimal</code> value
     */
    public BigDecimal getAvgPrice()
    {
        return avgPrice;
    }
    /**
     * Sets the avgPrice value.
     *
     * @param inAvgPrice a <code>BigDecimal</code> value
     */
    public void setAvgPrice(BigDecimal inAvgPrice)
    {
        avgPrice = inAvgPrice;
    }
    /**
     * Get the lastQuantity value.
     *
     * @return a <code>BigDecimal</code> value
     */
    public BigDecimal getLastQuantity()
    {
        return lastQuantity;
    }
    /**
     * Sets the lastQuantity value.
     *
     * @param inLastQuantity a <code>BigDecimal</code> value
     */
    public void setLastQuantity(BigDecimal inLastQuantity)
    {
        lastQuantity = inLastQuantity;
    }
    /**
     * Get the lastPrice value.
     *
     * @return a <code>BigDecimal</code> value
     */
    public BigDecimal getLastPrice()
    {
        return lastPrice;
    }
    /**
     * Sets the lastPrice value.
     *
     * @param inLastPrice a <code>BigDecimal</code> value
     */
    public void setLastPrice(BigDecimal inLastPrice)
    {
        lastPrice = inLastPrice;
    }
    /**
     * Get the orderStatus value.
     *
     * @return an <code>OrderStatus</code> value
     */
    public OrderStatus getOrderStatus()
    {
        return orderStatus;
    }
    /**
     * Sets the orderStatus value.
     *
     * @param inOrderStatus an <code>OrderStatus</code> value
     */
    public void setOrderStatus(OrderStatus inOrderStatus)
    {
        orderStatus = inOrderStatus;
    }
    /**
     * Get the sendingTime value.
     *
     * @return a <code>Date</code> value
     */
    public Date getSendingTime()
    {
        return sendingTime;
    }
    /**
     * Sets the sendingTime value.
     *
     * @param inSendingTime a <code>Date</code> value
     */
    public void setSendingTime(Date inSendingTime)
    {
        sendingTime = inSendingTime;
    }
    /**
     * Get the viewer value.
     *
     * @return a <code>SimpleUser</code> value
     */
    public SimpleUser getViewer()
    {
        return viewer;
    }
    /**
     * Sets the viewer value.
     *
     * @param inViewer a <code>SimpleUser</code> value
     */
    public void setViewer(SimpleUser inViewer)
    {
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
     * Get the isOpen value.
     *
     * @return a <code>boolean</code> value
     */
    public boolean getIsOpen()
    {
        return isOpen;
    }
    /**
     * Sets the isOpen value.
     *
     * @param inIsOpen a <code>boolean</code> value
     */
    public void setIsOpen(boolean inIsOpen)
    {
        isOpen = inIsOpen;
    }
    /**
     * Get the report value.
     *
     * @return a <code>PersistentReport</code> value
     */
    public PersistentReport getReport()
    {
        return report;
    }
    /**
     * Sets the report value.
     *
     * @param inReport a <code>PersistentReport</code> value
     */
    public void setReport(PersistentReport inReport)
    {
        report = inReport;
    }
    /**
     * Gets the viewer ID value.
     *
     * @return a <code>UserID</code> value or <code>null</code>
     */
    public UserID getViewerID()
    {
        if (getViewer()==null) {
            return null;
        }
        return getViewer().getUserID();
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("ExecutionReportSummary [rootOrderId=").append(rootOrderId).append(", orderId=").append(orderId)
                .append(", origOrderID=").append(origOrderID).append(", symbol=").append(symbol)
                .append(", strikePrice=").append(strikePrice).append(", side=").append(side).append(", cumQuantity=")
                .append(cumQuantity).append(", effectiveCumQuantity=").append(effectiveCumQuantity)
                .append(", avgPrice=").append(avgPrice).append(", lastQuantity=").append(lastQuantity)
                .append(", lastPrice=").append(lastPrice).append(", orderStatus=").append(orderStatus)
                .append(", sendingTime=").append(sendingTime).append(", viewer=").append(viewer).append(", actor=")
                .append(actor).append(", isOpen=").append(isOpen).append(", securityType=").append(securityType)
                .append(", expiry=").append(expiry).append(", optionType=").append(optionType).append(", account=")
                .append(account).append(", report=").append(report).append(", getId()=").append(getId()).append("]");
        return builder.toString();
    }
    /**
     * Create a new ExecutionReportSummary instance.
     */
    public ExecutionReportSummary() {}
    /**
     * root order ID value
     */
    @Embedded
    @AttributeOverrides({@AttributeOverride(name="mValue",column=@Column(name="root_order_id",nullable=false))})
    private OrderID rootOrderId;
    /**
     * order ID value
     */
    @Embedded
    @AttributeOverrides({@AttributeOverride(name="mValue",column=@Column(name="order_id",nullable=false))})
    private OrderID orderId;
    /**
     * original order ID value, may be <code>null</code>
     */
    @Embedded
    @AttributeOverrides({@AttributeOverride(name="mValue",column=@Column(name="orig_order_id",nullable=true))})
    private OrderID origOrderID;
    /**
     * symbol value
     */
    @Column(name="symbol",nullable=false)
    private String symbol;
    /**
     * strike price value, <code>null</code> for non-option types
     */
    @Column(name="strike_price",precision=DECIMAL_PRECISION,scale=DECIMAL_SCALE,nullable=true)
    private BigDecimal strikePrice;
    /**
     * side value
     */
    @Column(name="side",nullable=false)
    private Side side;
    /**
     * cumulative quantity value
     */
    @Column(name="cum_qty",precision=DECIMAL_PRECISION,scale=DECIMAL_SCALE,nullable=false)
    private BigDecimal cumQuantity;
    /**
     * effective cumulative quantity value, which is the cumulative quantity adjusted for side
     */
    @Column(name="eff_cum_qty",precision=DECIMAL_PRECISION,scale=DECIMAL_SCALE,nullable=false)
    private BigDecimal effectiveCumQuantity;
    /**
     * average price value
     */
    @Column(name="avg_price",precision=DECIMAL_PRECISION,scale=DECIMAL_SCALE,nullable=false)
    private BigDecimal avgPrice;
    /**
     * last quantity value, may be <code>null</code>
     */
    @Column(name="last_qty",precision=DECIMAL_PRECISION,scale=DECIMAL_SCALE,nullable=true)
    private BigDecimal lastQuantity;
    /**
     * last price value, may be <code>null</code>
     */
    @Column(name="last_price",precision=DECIMAL_PRECISION,scale=DECIMAL_SCALE,nullable=true)
    private BigDecimal lastPrice;
    /**
     * order status value
     */
    @Column(name="ord_status",nullable=false)
    private OrderStatus orderStatus;
    /**
     * sending time value
     */
    @Column(name="send_time",nullable=false)
    private Date sendingTime;
    /**
     * viewer value
     */
    @ManyToOne
    @JoinColumn(name="viewer_id")
    private SimpleUser viewer; 
    /**
     * actor value
     */
    @ManyToOne
    @JoinColumn(name="actor_id")
    private SimpleUser actor; 
    /**
     * is open value
     */
    @Column(name="is_open",nullable=false)
    private boolean isOpen;
    /**
     * security type value
     */
    @Column(name="security_type",nullable=false)
    private SecurityType securityType;
    /**
     * expiry value, <code>null</code> for non-option types
     */
    @Column(name="expiry",nullable=true)
    private String expiry;
    /**
     * option type value, <code>null</code> for non-option types
     */
    @Column(name="option_type",nullable=true)
    private OptionType optionType;
    /**
     * account value, may be <code>null</code>
     */
    @Column(name="account",nullable=true)
    private String account;
    /**
     * linked report value
     */
    @OneToOne(optional=false)
    @JoinColumn(name="report_id")
    private PersistentReport report;
    /**
     * The scale used for storing all decimal values.
     */
    public static final int DECIMAL_SCALE = 7;
    /**
     * The precision used for storing all decimal values.
     */
    public static final int DECIMAL_PRECISION = 17;
    private static final long serialVersionUID = -2371447603392658986L;
}
