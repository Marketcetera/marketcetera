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
import org.marketcetera.core.instruments.InstrumentFromMessage;
import org.marketcetera.persist.EntityBase;
import org.marketcetera.trade.BrokerID;
import org.marketcetera.trade.Equity;
import org.marketcetera.trade.ExecutionReport;
import org.marketcetera.trade.FIXMessageWrapper;
import org.marketcetera.trade.Instrument;
import org.marketcetera.trade.InstrumentSummaryFields;
import org.marketcetera.trade.OptionType;
import org.marketcetera.trade.OrderID;
import org.marketcetera.trade.OrderSummary;
import org.marketcetera.trade.ReportBase;
import org.marketcetera.trade.SecurityType;
import org.marketcetera.trade.Side;

import quickfix.FieldNotFound;
import quickfix.InvalidMessage;
import quickfix.Message;
import quickfix.field.AvgPx;
import quickfix.field.LastPx;
import quickfix.field.LastQty;
import quickfix.field.LeavesQty;
import quickfix.field.OrdStatus;
import quickfix.field.OrderQty;
import quickfix.field.Price;
import quickfix.field.SendingTime;
import quickfix.field.TransactTime;

/* $License$ */

/**
 * Describes last known status for orders.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@Entity(name="OrderStatus")
@Table(name="order_status")
public class PersistentOrderSummary
        extends EntityBase
        implements OrderSummary
{
    /**
     * Create a new PersistentOrderSummary instance.
     */
    public PersistentOrderSummary()
    {
    }
    /**
     * Create a new PersistentOrderSummary instance.
     *
     * @param inOrderStatus an <code>OrderStatus</code> value
     */
    public PersistentOrderSummary(OrderSummary inOrderStatus)
    {
        account = inOrderStatus.getAccount();
        averagePrice = inOrderStatus.getAveragePrice();
        brokerId = inOrderStatus.getBrokerId();
        cumulativeQuantity = inOrderStatus.getCumulativeQuantity();
        instrument = inOrderStatus.getInstrument();
        setInstrumentFields(instrument);
        lastPrice = inOrderStatus.getLastPrice();
        lastQuantity = inOrderStatus.getLastQuantity();
        leavesQuantity = inOrderStatus.getLeavesQuantity();
        orderId = inOrderStatus.getOrderId();
        orderPrice = inOrderStatus.getOrderPrice();
        orderQuantity = inOrderStatus.getOrderQuantity();
        orderStatus = inOrderStatus.getOrderStatus();
        report = (PersistentReport)inOrderStatus.getReport();
        rootOrderId = inOrderStatus.getRootOrderId();
        sendingTime = inOrderStatus.getSendingTime();
        side = inOrderStatus.getSide();
        transactTime = inOrderStatus.getTransactTime();
        actor = inOrderStatus.getActor();
        viewer = inOrderStatus.getViewer();
    }
    /**
     * Create a new PersistentOrderSummary instance.
     *
     * @param inReport a <code>PersistentReport</code> value
     * @param inReportBase a <code>ReportBase</code> value
     * @param inRootOrderId an <code>OrderID</code> value
     */
    public PersistentOrderSummary(PersistentReport inReport,
                                 ReportBase inReportBase,
                                 OrderID inRootOrderId)
    {
        setInstrumentFields(new Equity("unknown"));
        orderStatus = org.marketcetera.trade.OrderStatus.Unknown;
        side = Side.Unknown;
        report = inReport;
        rootOrderId = inRootOrderId;
        orderId = inReport.getOrderID();
        cumulativeQuantity = BigDecimal.ZERO;
        averagePrice = BigDecimal.ZERO;
        lastQuantity = BigDecimal.ZERO;
        leavesQuantity = BigDecimal.ZERO;
        orderQuantity = BigDecimal.ZERO;
        lastPrice = BigDecimal.ZERO;
        brokerId = inReport.getBrokerID();
        actor = inReport.getActor();
        viewer = inReport.getViewer();
        if(inReportBase instanceof ExecutionReport) {
            ExecutionReport executionReport = (ExecutionReport)inReportBase;
            setFieldsFromExecutionReport(executionReport);
        } else {
            try {
                Message message = getMessageFromInputs(inReport,
                                                       inReportBase);
                setFieldsFromMessage(message);
            } catch (InvalidMessage | FieldNotFound e) {
                throw new RuntimeException(e);
            }
        }
    }
    /**
     * Update the object from the given inputs.
     *
     * @param inReport a <code>PersistentReport</code> value
     * @param inReportBase a <code>ReportBase</code> value
     */
    public void update(PersistentReport inReport,
                       ReportBase inReportBase)
    {
        orderId = inReport.getOrderID();
        report = inReport;
        if(inReportBase instanceof ExecutionReport) {
            ExecutionReport executionReport = (ExecutionReport)inReportBase;
            setFieldsFromExecutionReport(executionReport);
        } else {
            try {
                Message message = getMessageFromInputs(inReport,
                                                       inReportBase);
                setFieldsFromMessage(message);
            } catch (InvalidMessage | FieldNotFound e) {
                throw new RuntimeException(e);
            }
        }
    }
    /* (non-Javadoc)
     * @see com.marketcetera.ors.history.OrderStatus#getOrderStatus()
     */
    @Override
    public org.marketcetera.trade.OrderStatus getOrderStatus()
    {
        return orderStatus;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.ors.history.OrderStatus#getReport()
     */
    @Override
    public PersistentReport getReport()
    {
        return report;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.ors.history.OrderStatus#getRootOrderId()
     */
    @Override
    public OrderID getRootOrderId()
    {
        return rootOrderId;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.ors.history.OrderStatus#getOrderId()
     */
    @Override
    public OrderID getOrderId()
    {
        return orderId;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.ors.history.OrderStatus#getCumulativeQuantity()
     */
    @Override
    public BigDecimal getCumulativeQuantity()
    {
        return cumulativeQuantity;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.ors.history.OrderStatus#getAveragePrice()
     */
    @Override
    public BigDecimal getAveragePrice()
    {
        return averagePrice;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.ors.history.OrderStatus#getLastQuantity()
     */
    @Override
    public BigDecimal getLastQuantity()
    {
        return lastQuantity;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.ors.history.OrderStatus#getLastPrice()
     */
    @Override
    public BigDecimal getLastPrice()
    {
        return lastPrice;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.ors.history.OrderStatus#getLeavesQuantity()
     */
    @Override
    public BigDecimal getLeavesQuantity()
    {
        return leavesQuantity;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.ors.history.OrderStatus#getOrderQuantity()
     */
    @Override
    public BigDecimal getOrderQuantity()
    {
        return orderQuantity;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.ors.history.OrderStatus#getAccount()
     */
    @Override
    public String getAccount()
    {
        return account;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.ors.history.OrderStatus#getBrokerId()
     */
    @Override
    public BrokerID getBrokerId()
    {
        return brokerId;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.ors.history.OrderStatus#getSide()
     */
    @Override
    public Side getSide()
    {
        return side;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.ors.history.OrderStatus#getInstrument()
     */
    @Override
    public Instrument getInstrument()
    {
        if(instrument == null) {
            try {
                Message message = new Message(report.getFixMessage());
                instrument = InstrumentFromMessage.SELECTOR.forValue(message).extract(message);
            } catch (InvalidMessage e) {
                throw new RuntimeException(e);
            }
        }
        return instrument;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.ors.history.OrderStatus#getOrderPrice()
     */
    @Override
    public BigDecimal getOrderPrice()
    {
        return orderPrice;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.ors.history.OrderStatus#getSendingTime()
     */
    @Override
    public Date getSendingTime()
    {
        return sendingTime;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.ors.history.OrderStatus#getTransactTime()
     */
    @Override
    public Date getTransactTime()
    {
        return transactTime;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.ors.history.OrderStatus#getActor()
     */
    @Override
    public User getActor()
    {
        return actor;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.ors.history.OrderStatus#getViewer()
     */
    @Override
    public User getViewer()
    {
        return viewer;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("PersistentOrderSummary [instrument=").append(getInstrument()).append(", rootOrderId=")
                .append(rootOrderId).append(", orderId=").append(orderId).append(", orderStatus=").append(orderStatus)
                .append(", cumulativeQuantity=").append(cumulativeQuantity).append(", averagePrice=")
                .append(averagePrice).append(", leavesQuantity=").append(leavesQuantity).append(", lastQuantity=")
                .append(lastQuantity).append(", lastPrice=").append(lastPrice).append(", orderQuantity=").append(orderQuantity).append(", account=")
                .append(account).append(", side=").append(side).append(", brokerId=").append(brokerId)
                .append(", report=").append(report).append("]");
        return builder.toString();
    }
    /**
     * Set instrument fields from the given instrument.
     *
     * @param inInstrument an <code>Instrument</code> value
     */
    private void setInstrumentFields(Instrument inInstrument)
    {
        if(inInstrument != null) {
            securityType = inInstrument.getSecurityType();
            symbol = inInstrument.getSymbol();
            InstrumentSummaryFields<?> summaryFields = InstrumentSummaryFields.SELECTOR.forInstrument(inInstrument);
            optionType = summaryFields.getOptionType(inInstrument);
            strikePrice = summaryFields.getStrikePrice(inInstrument);
            expiry = summaryFields.getExpiry(inInstrument);
        }
    }
    /**
     * Set object fields from the given execution report.
     *
     * @param inExecutionReport an <code>ExecutionReport</code> value
     */
    private void setFieldsFromExecutionReport(ExecutionReport inExecutionReport)
    {
        setInstrumentFields(inExecutionReport.getInstrument());
        orderStatus = inExecutionReport.getOrderStatus();
        side = inExecutionReport.getSide();
        account = inExecutionReport.getAccount();
        brokerId = inExecutionReport.getBrokerID();
        if(inExecutionReport.getCumulativeQuantity() != null) {
            cumulativeQuantity = inExecutionReport.getCumulativeQuantity();
        }
        if(inExecutionReport.getAveragePrice() != null) {
            averagePrice = inExecutionReport.getAveragePrice();
        }
        if(inExecutionReport.getLeavesQuantity() != null) {
            leavesQuantity = inExecutionReport.getLeavesQuantity();
        }
        if(inExecutionReport.getLastQuantity() != null) {
            lastQuantity = inExecutionReport.getLastQuantity();
        }
        if(inExecutionReport.getOrderQuantity() != null) {
            orderQuantity = inExecutionReport.getOrderQuantity();
        }
        if(inExecutionReport.getLastPrice() != null) {
            lastPrice = inExecutionReport.getLastPrice();
        }
        sendingTime = inExecutionReport.getSendingTime();
        transactTime = inExecutionReport.getTransactTime();
        orderPrice = inExecutionReport.getPrice();
    }
    /**
     * Set the fields from the given FIX message.
     *
     * @param inMessage a <code>Message</code> value
     * @throws FieldNotFound if the fields cannot be set
     */
    private void setFieldsFromMessage(Message inMessage)
            throws FieldNotFound
    {
        if(inMessage.isSetField(OrdStatus.FIELD)) {
            orderStatus = org.marketcetera.trade.OrderStatus.getInstanceForFIXValue(inMessage.getChar(OrdStatus.FIELD));
        }
        if(inMessage.isSetField(quickfix.field.Side.FIELD)) {
            side = Side.getInstanceForFIXValue(inMessage.getChar(quickfix.field.Side.FIELD));
        }
        if(inMessage.isSetField(AvgPx.FIELD)) {
            averagePrice = inMessage.getDecimal(AvgPx.FIELD);
        }
        if(inMessage.isSetField(LastQty.FIELD)) {
            lastQuantity = inMessage.getDecimal(LastQty.FIELD);
        }
        if(inMessage.isSetField(LastPx.FIELD)) {
            lastPrice = inMessage.getDecimal(LastPx.FIELD);
        }
        if(inMessage.isSetField(LeavesQty.FIELD)) {
            leavesQuantity = inMessage.getDecimal(LeavesQty.FIELD);
        }
        if(inMessage.isSetField(OrderQty.FIELD)) {
            orderQuantity = inMessage.getDecimal(OrderQty.FIELD);
        }
        if(inMessage.getHeader().isSetField(SendingTime.FIELD)) {
            sendingTime = inMessage.getHeader().getUtcTimeStamp(SendingTime.FIELD);
        }
        if(inMessage.isSetField(TransactTime.FIELD)) {
            transactTime = inMessage.getUtcTimeStamp(TransactTime.FIELD);
        }
        if(inMessage.isSetField(Price.FIELD)) {
            orderPrice = inMessage.getDecimal(Price.FIELD);
        }
        Instrument instrument = InstrumentFromMessage.SELECTOR.forValue(inMessage).extract(inMessage);
        setInstrumentFields(instrument);
    }
    /**
     * Get the FIX message from the given inputs.
     *
     * @param inReport a <code>PersistentReport</code> value
     * @param inReportBase a <code>ReportBase</code> value
     * @return a <code>Message</code> value
     * @throws InvalidMessage if the underlying FIX message is invalid
     */
    private Message getMessageFromInputs(PersistentReport inReport,
                                         ReportBase inReportBase)
            throws InvalidMessage
    {
        if(inReportBase instanceof FIXMessageWrapper) {
            return ((FIXMessageWrapper)inReportBase).getMessage();
        } else {
            return new Message(inReport.getFixMessage());
        }
    }
    /**
     * cached instrument value
     */
    private transient Instrument instrument;
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
     * order status value
     */
    @Enumerated(EnumType.STRING)
    @Column(name="ord_status",nullable=false)
    private org.marketcetera.trade.OrderStatus orderStatus;
    /**
     * cumulative quantity value
     */
    @Column(name="cum_qty",precision=DECIMAL_PRECISION,scale=DECIMAL_SCALE,nullable=false)
    private BigDecimal cumulativeQuantity;
    /**
     * average price value
     */
    @Column(name="avg_px",precision=DECIMAL_PRECISION,scale=DECIMAL_SCALE,nullable=false)
    private BigDecimal averagePrice;
    /**
     * leaves qty value
     */
    @Column(name="leaves_qty",precision=DECIMAL_PRECISION,scale=DECIMAL_SCALE,nullable=false)
    private BigDecimal leavesQuantity;
    /**
     * last qty value
     */
    @Column(name="last_qty",precision=DECIMAL_PRECISION,scale=DECIMAL_SCALE,nullable=false)
    private BigDecimal lastQuantity;
    /**
     * last px value
     */
    @Column(name="last_px",precision=DECIMAL_PRECISION,scale=DECIMAL_SCALE,nullable=false)
    private BigDecimal lastPrice;
    /**
     * order px value
     */
    @Column(name="order_px",precision=DECIMAL_PRECISION,scale=DECIMAL_SCALE,nullable=true)
    private BigDecimal orderPrice;
    /**
     * last qty value
     */
    @Column(name="order_qty",precision=DECIMAL_PRECISION,scale=DECIMAL_SCALE,nullable=false)
    private BigDecimal orderQuantity;
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
     * sending time value
     */
    @Column(name="sending_time",nullable=false)
    private Date sendingTime;
    /**
     * execution time value
     */
    @Column(name="execution_time",nullable=true)
    private Date transactTime;
    /**
     * broker ID value
     */
    @Embedded
    @AttributeOverrides({@AttributeOverride(name="mValue",column=@Column(name="broker_id"))})
    private BrokerID brokerId;
    /**
     * linked report value
     */
    @OneToOne(optional=false)
    @JoinColumn(name="report_id")
    private PersistentReport report;
    /**
     * actor value
     */
    @ManyToOne(targetEntity=PersistentUser.class)
    @JoinColumn(name="actor_id")
    private User actor; 
    /**
     * viewer value
     */
    @ManyToOne(targetEntity=PersistentUser.class)
    @JoinColumn(name="viewer_id")
    private User viewer; 
    private static final long serialVersionUID = 8419928349132467501L;
}
