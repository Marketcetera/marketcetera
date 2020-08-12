package org.marketcetera.trade.dao;

import static org.marketcetera.core.PlatformServices.DECIMAL_PRECISION;
import static org.marketcetera.core.PlatformServices.DECIMAL_SCALE;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.marketcetera.admin.User;
import org.marketcetera.admin.user.PersistentUser;
import org.marketcetera.persist.EntityBase;
import org.marketcetera.trade.ExecutionReport;
import org.marketcetera.trade.ExecutionType;
import org.marketcetera.trade.Instrument;
import org.marketcetera.trade.InstrumentSummaryFields;
import org.marketcetera.trade.MutableExecutionReportSummary;
import org.marketcetera.trade.Option;
import org.marketcetera.trade.OptionType;
import org.marketcetera.trade.OrderID;
import org.marketcetera.trade.OrderStatus;
import org.marketcetera.trade.OrderType;
import org.marketcetera.trade.Report;
import org.marketcetera.trade.SecurityType;
import org.marketcetera.trade.Side;
import org.marketcetera.trade.TimeInForce;
import org.marketcetera.trade.UserID;
import org.marketcetera.util.misc.ClassVersion;

import quickfix.FieldNotFound;
import quickfix.InvalidMessage;

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
@ClassVersion("$Id$")
public class PersistentExecutionReport
        extends EntityBase
        implements MutableExecutionReportSummary
{
    /**
     * Creates an instance.
     *
     * @param inReport The original execution report message.
     * @param inSavedReport the saved persistent report.
     */
    public PersistentExecutionReport(ExecutionReport inReport,
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
        setLeavesQuantity(inReport.getLeavesQuantity());
        setOrderQuantity(inReport.getOrderQuantity());
        setOrderType(inReport.getOrderType());
        setPrice(inReport.getPrice());
        setTimeInForce(inReport.getTimeInForce());
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
        execType = inReport.getExecutionType();
        if(execType == null) {
            execType = ExecutionType.Unknown;
        }
        sendingTime = inReport.getSendingTime();
        viewer = inSavedReport.getViewer();
        actor = inSavedReport.getActor();
        executionId = inReport.getExecutionID();
        try {
            if(inSavedReport.getFixMessage() != null) {
                quickfix.Message fixMessage = new quickfix.Message(inSavedReport.getFixMessage());
                if(fixMessage.isSetField(quickfix.field.OrderID.FIELD)) {
                    setBrokerOrderId(new OrderID(fixMessage.getString(quickfix.field.OrderID.FIELD)));
                }
            }
        } catch (InvalidMessage | FieldNotFound e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * Create a new ExecutionReportSummary instance.
     */
    public PersistentExecutionReport() {}
    /* (non-Javadoc)
     * @see org.marketcetera.trade.dao.ExecutionReport#getOrderID()
     */
    @Override
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
    /* (non-Javadoc)
     * @see org.marketcetera.trade.dao.ExecutionReport#getOrigOrderID()
     */
    @Override
    public OrderID getOriginalOrderID()
    {
        return origOrderID;
    }
    /**
     * Sets the original order id value.
     *
     * @param inOrigOrderID an <code>OrderID</code> value
     */
    public void setOriginalOrderID(OrderID inOrigOrderID)
    {
        origOrderID = inOrigOrderID;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.dao.ExecutionReport#getSecurityType()
     */
    @Override
    public SecurityType getSecurityType()
    {
        return securityType;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.MutableExecutionReportSummary#SetecurityType(org.marketcetera.trade.SecurityType)
     */
    @Override
    public void setSecurityType(SecurityType inSecurityType)
    {
        securityType = inSecurityType;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.dao.ExecutionReport#getSymbol()
     */
    @Override
    public String getSymbol()
    {
        return symbol;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.MutableExecutionReportSummary#Setymbol(java.lang.String)
     */
    @Override
    public void setSymbol(String inSymbol)
    {
        symbol = inSymbol;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.dao.ExecutionReport#getExpiry()
     */
    @Override
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
    /* (non-Javadoc)
     * @see org.marketcetera.trade.dao.ExecutionReport#getStrikePrice()
     */
    @Override
    public BigDecimal getStrikePrice()
    {
        return strikePrice;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.MutableExecutionReportSummary#SettrikePrice(java.math.BigDecimal)
     */
    @Override
    public void setStrikePrice(BigDecimal inStrikePrice)
    {
        strikePrice = inStrikePrice;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.dao.ExecutionReport#getOptionType()
     */
    @Override
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
    /* (non-Javadoc)
     * @see org.marketcetera.trade.dao.ExecutionReport#getAccount()
     */
    @Override
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
    /* (non-Javadoc)
     * @see org.marketcetera.trade.dao.ExecutionReport#getRootOrderID()
     */
    @Override
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
    /* (non-Javadoc)
     * @see org.marketcetera.trade.dao.ExecutionReport#getSide()
     */
    @Override
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
    /* (non-Javadoc)
     * @see org.marketcetera.trade.dao.ExecutionReport#getCumQuantity()
     */
    @Override
    public BigDecimal getCumulativeQuantity()
    {
        return cumQuantity;
    }
    /**
     * Sets the cumQuantity value.
     *
     * @param inCumQuantity a <code>BigDecimal</code> value
     */
    public void setCumulativeQuantity(BigDecimal inCumQuantity)
    {
        cumQuantity = inCumQuantity;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.dao.ExecutionReport#getEffectiveCumQuantity()
     */
    @Override
    public BigDecimal getEffectiveCumulativeQuantity()
    {
        return effectiveCumQuantity;
    }
    /**
     * Sets the effectiveCumQuantity value.
     *
     * @param inEffectiveCumQuantity a <code>BigDecimal</code> value
     */
    public void setEffectiveCumulativeQuantity(BigDecimal inEffectiveCumQuantity)
    {
        effectiveCumQuantity = inEffectiveCumQuantity;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.dao.ExecutionReport#getAvgPrice()
     */
    @Override
    public BigDecimal getAveragePrice()
    {
        return avgPrice;
    }
    /**
     * Sets the avgPrice value.
     *
     * @param inAvgPrice a <code>BigDecimal</code> value
     */
    public void setAveragePrice(BigDecimal inAvgPrice)
    {
        avgPrice = inAvgPrice;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.dao.ExecutionReport#getLastQuantity()
     */
    @Override
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
    /* (non-Javadoc)
     * @see org.marketcetera.trade.dao.ExecutionReport#getLastPrice()
     */
    @Override
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
    /* (non-Javadoc)
     * @see org.marketcetera.trade.dao.ExecutionReport#getOrderStatus()
     */
    @Override
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
    /* (non-Javadoc)
     * @see org.marketcetera.trade.dao.ExecutionReport#getExecType()
     */
    @Override
    public ExecutionType getExecutionType()
    {
        return execType;
    }
    /**
     * Sets the execType value.
     *
     * @param inExecType an <code>ExecutionType</code> value
     */
    public void setExecutionType(ExecutionType inExecType)
    {
        execType = inExecType;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.dao.ExecutionReport#getSendingTime()
     */
    @Override
    public java.time.LocalDateTime getSendingTime()
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
    /* (non-Javadoc)
     * @see org.marketcetera.trade.dao.ExecutionReport#getViewer()
     */
    @Override
    public PersistentUser getViewer()
    {
        return viewer;
    }
    /**
     * Sets the viewer value.
     *
     * @param inViewer a <code>SimpleUser</code> value
     */
    public void setViewer(PersistentUser inViewer)
    {
        viewer = inViewer;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.dao.ExecutionReport#getActor()
     */
    @Override
    public PersistentUser getActor()
    {
        return actor;
    }
    /**
     * Sets the actor value.
     *
     * @param inActor a <code>SimpleUser</code> value
     */
    public void setActor(PersistentUser inActor)
    {
        actor = inActor;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.dao.ExecutionReport#getReport()
     */
    @Override
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
    /* (non-Javadoc)
     * @see org.marketcetera.trade.dao.ExecutionReport#getViewerID()
     */
    @Override
    public UserID getViewerID()
    {
        if (getViewer()==null) {
            return null;
        }
        return getViewer().getUserID();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.ExecutionReportSummary#getBrokerOrderId()
     */
    @Override
    public OrderID getBrokerOrderId()
    {
        return brokerOrderId;
    }
    /**
     * Sets the brokerOrderId value.
     *
     * @param inBrokerOrderId an <code>OrderID</code> value
     */
    public void setBrokerOrderId(OrderID inBrokerOrderId)
    {
        brokerOrderId = inBrokerOrderId;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.ExecutionReportSummary#getExecutionId()
     */
    @Override
    public String getExecutionId()
    {
        return executionId;
    }
    /**
     * Sets the executionId value.
     *
     * @param inExecutionId a <code>String</code> value
     */
    public void setExecutionId(String inExecutionId)
    {
        executionId = inExecutionId;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.MutableExecutionReportSummary#setViewer(org.marketcetera.admin.User)
     */
    @Override
    public void setViewer(User inViewer)
    {
        if(inViewer instanceof PersistentUser) {
            viewer = (PersistentUser)inViewer;
        } else {
            throw new UnsupportedOperationException();
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.MutableExecutionReportSummary#setActor(org.marketcetera.admin.User)
     */
    @Override
    public void setActor(User inActor)
    {
        if(inActor instanceof PersistentUser) {
            actor = (PersistentUser)inActor;
        } else {
            throw new UnsupportedOperationException();
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.MutableExecutionReportSummary#setReport(org.marketcetera.trade.Report)
     */
    @Override
    public void setReport(Report inReport)
    {
        if(inReport instanceof PersistentReport) {
            report = (PersistentReport)inReport;
        } else {
            throw new UnsupportedOperationException();
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.ExecutionReportSummary#getLeavesQuantity()
     */
    @Override
    public BigDecimal getLeavesQuantity()
    {
        return leavesQuantity;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.MutableExecutionReportSummary#setLeavesQuantity(java.math.BigDecimal)
     */
    @Override
    public void setLeavesQuantity(BigDecimal inLeavesQuantity)
    {
        leavesQuantity = inLeavesQuantity==null?BigDecimal.ZERO:inLeavesQuantity;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.ExecutionReportSummary#getOrderQuantity()
     */
    @Override
    public BigDecimal getOrderQuantity()
    {
        return orderQuantity;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.MutableExecutionReportSummary#setOrderQuantity(java.math.BigDecimal)
     */
    @Override
    public void setOrderQuantity(BigDecimal inOrderQuantity)
    {
        orderQuantity = inOrderQuantity==null?BigDecimal.ZERO:inOrderQuantity;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.ExecutionReportSummary#getOrderType()
     */
    @Override
    public OrderType getOrderType()
    {
        return orderType;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.MutableExecutionReportSummary#setOrderType(org.marketcetera.trade.OrderType)
     */
    @Override
    public void setOrderType(OrderType inOrderType)
    {
        orderType = inOrderType;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.ExecutionReportSummary#getPrice()
     */
    @Override
    public BigDecimal getPrice()
    {
        return price;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.MutableExecutionReportSummary#setPrice(java.math.BigDecimal)
     */
    @Override
    public void setPrice(BigDecimal inPrice)
    {
        price = inPrice;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.ExecutionReportSummary#getTimeInForce()
     */
    @Override
    public TimeInForce getTimeInForce()
    {
        return timeInForce;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.MutableExecutionReportSummary#setTimeInForce(org.marketcetera.trade.TimeInForce)
     */
    @Override
    public void setTimeInForce(TimeInForce inTimeInForce)
    {
        timeInForce = inTimeInForce;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.MutableExecutionReportSummary#setInstrument(org.marketcetera.trade.Instrument)
     */
    @Override
    public void setInstrument(Instrument inInstrument)
    {
        symbol = inInstrument.getFullSymbol();
        setSecurityType(inInstrument.getSecurityType());
        switch(inInstrument.getSecurityType()) {
            case CommonStock:
            case ConvertibleBond:
            case Currency:
            case Future:
                break;
            case Option:
                Option option = (Option)inInstrument;
                symbol = option.getSymbol();
                expiry = option.getExpiry();
                strikePrice = option.getStrikePrice();
                optionType = option.getType();
                break;
            case Unknown:
            default:
                throw new UnsupportedOperationException();
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.ExecutionReportSummary#getMutableVersion()
     */
    @Override
    public MutableExecutionReportSummary getMutableVersion()
    {
        return this;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("PersistentExecutionReport [rootOrderId=").append(rootOrderId).append(", orderId=")
                .append(orderId).append(", origOrderID=").append(origOrderID).append(", symbol=").append(symbol)
                .append(", strikePrice=").append(strikePrice).append(", side=").append(side).append(", cumQuantity=")
                .append(cumQuantity).append(", effectiveCumQuantity=").append(effectiveCumQuantity)
                .append(", avgPrice=").append(avgPrice).append(", lastQuantity=").append(lastQuantity)
                .append(", lastPrice=").append(lastPrice).append(", orderStatus=").append(orderStatus)
                .append(", execType=").append(execType).append(", sendingTime=").append(sendingTime).append(", viewer=")
                .append(viewer).append(", actor=").append(actor).append(", securityType=").append(securityType)
                .append(", expiry=").append(expiry).append(", optionType=").append(optionType).append(", account=")
                .append(account).append(", executionId=").append(executionId).append(", brokerOrderId=")
                .append(brokerOrderId).append(", leavesQuantity=").append(leavesQuantity).append(", orderQuantity=")
                .append(orderQuantity).append(", orderType=").append(orderType).append(", price=").append(price)
                .append(", timeInForce=").append(timeInForce).append(", report=").append(report).append("]");
        return builder.toString();
    }
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
    @Enumerated(EnumType.STRING)
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
    @Enumerated(EnumType.STRING)
    @Column(name="ord_status",nullable=false)
    private OrderStatus orderStatus;
    /**
     * execution type value
     */
    @Enumerated(EnumType.STRING)
    @Column(name="exec_type",nullable=false)
    private ExecutionType execType;
    /**
     * sending time value
     */
    @Column(name="send_time",nullable=false)
    private java.time.LocalDateTime sendingTime;
    /**
     * viewer value
     */
    @ManyToOne
    @JoinColumn(name="viewer_id")
    private PersistentUser viewer; 
    /**
     * actor value
     */
    @ManyToOne
    @JoinColumn(name="actor_id")
    private PersistentUser actor; 
    /**
     * security type value
     */
    @Enumerated(EnumType.STRING)
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
     * execution id value
     */
    @Column(name="exec_id",nullable=false)
    private String executionId;
    /**
     * broker order id value
     */
    @Embedded
    @AttributeOverrides({@AttributeOverride(name="mValue",column=@Column(name="broker_order_id",nullable=false))})
    private OrderID brokerOrderId;
    /**
     * leaves quantity value
     */
    @Column(name="leaves_qty",precision=DECIMAL_PRECISION,scale=DECIMAL_SCALE,nullable=false)
    private BigDecimal leavesQuantity;
    /**
     * leaves quantity value
     */
    @Column(name="order_qty",precision=DECIMAL_PRECISION,scale=DECIMAL_SCALE,nullable=false)
    private BigDecimal orderQuantity;
    /**
     * order type value
     */
    @Enumerated(EnumType.STRING)
    @Column(name="order_type",nullable=true)
    private OrderType orderType;
    /**
     * price value
     */
    @Column(name="price",precision=DECIMAL_PRECISION,scale=DECIMAL_SCALE,nullable=true)
    private BigDecimal price;
    /**
     * time-in-force value
     */
    @Enumerated(EnumType.STRING)
    @Column(name="tif",nullable=true)
    private TimeInForce timeInForce;
    /**
     * linked report value
     */
    @OneToOne(optional=false)
    @JoinColumn(name="report_id")
    private PersistentReport report;
    private static final long serialVersionUID = -1401320100194224727L;
}
