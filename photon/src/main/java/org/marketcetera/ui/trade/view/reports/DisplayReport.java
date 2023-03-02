package org.marketcetera.ui.trade.view.reports;

import java.math.BigDecimal;
import java.util.Date;

import org.marketcetera.core.instruments.InstrumentFromMessage;
import org.marketcetera.trade.BrokerID;
import org.marketcetera.trade.Hierarchy;
import org.marketcetera.trade.Instrument;
import org.marketcetera.trade.OrderID;
import org.marketcetera.trade.OrderStatus;
import org.marketcetera.trade.Originator;
import org.marketcetera.trade.Report;
import org.marketcetera.trade.ReportID;
import org.marketcetera.trade.ReportType;
import org.marketcetera.trade.Side;
import org.marketcetera.ui.trade.executionreport.view.FixMessageDisplayType;
import org.marketcetera.util.log.SLF4JLoggerProxy;

import quickfix.FieldNotFound;
import quickfix.InvalidMessage;

/* $License$ */

/**
 * Provides a display version of a {@link Report} with sortable attributes.
 * 
 * <p>This class is necessary because foreign type {@link quickfix.SessionID} is
 * not {@link Comparable}, which disables sorting in the UI.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class DisplayReport
        implements FixMessageDisplayType
{
    /**
     * Get the order id value.
     *
     * @return an <code>OrderID</code> value
     */
    public OrderID getOrderID()
    {
        return report.getOrderID();
    }
    /**
     * Get the order id value.
     *
     * @return an <code>OrderID</code> value
     */
    public OrderID getOriginalOrderId()
    {
        return originalOrderId;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.HasOrderId#setOrderId(org.marketcetera.trade.OrderID)
     */
    @Override
    public void setOrderId(OrderID inOrderId)
    {
        throw new UnsupportedOperationException();
    }
    /**
     * Get the trader name value.
     *
     * @return a <code>String</code> value
     */
    public String getTrader()
    {
        return report.getActor().getName();
    }
    /**
     * Get the FIX message value.
     *
     * @return a <code>String</code> value
     */
    public String getFixMessage()
    {
        return report.getFixMessage();
    }
    /**
     * Get the session ID value.
     *
     * @return a <code>String</code> value
     */
    public String getSessionID()
    {
        return report.getSessionId().toString();
    }
    /**
     * Get the message sequence number value.
     *
     * @return an <code>int</code> value
     */
    public int getMsgSeqNum()
    {
        return report.getMsgSeqNum();
    }
    /**
     * Get the sending time value.
     *
     * @return a <code>Date</code> value
     */
    public Date getSendingTime()
    {
        return report.getSendingTime();
    }
    /**
     * Get the transact time value.
     *
     * @return a <code>Date</code> value
     */
    public Date getTransactTime()
    {
        return report.getTransactTime();
    }
    /**
     * Get the message type value.
     *
     * @return a <code>ReportType</code> value
     */
    public ReportType getMsgType()
    {
        return report.getReportType();
    }
    /**
     * Get the broker ID value.
     *
     * @return a <code>BrokerID</code> value
     */
    public BrokerID getBrokerID()
    {
        return report.getBrokerID();
    }
    /**
     * Get the report ID value.
     *
     * @return a <code>ReportID</code> value
     */
    public ReportID getReportID()
    {
        return report.getReportID();
    }
    /**
     * Get the originator value.
     *
     * @return an <code>Originator</code> value
     */
    public Originator getOriginator()
    {
        return report.getOriginator();
    }
    /**
     * Get the hierarchy value.
     *
     * @return a <code>Hierarchy</code> value
     */
    public Hierarchy getHierarchy()
    {
        return report.getHierarchy();
    }
    /**
     * Get the text value.
     *
     * @return a <code>String</code> value
     */
    public String getText()
    {
        return report.getText();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.HasFIXMessage#getMessage()
     */
    @Override
    public quickfix.Message getMessage()
    {
        return fixMessage;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.HasOrderId#getOrderId()
     */
    @Override
    public OrderID getOrderId()
    {
        return report.getOrderID();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.HasOrderStatus#getOrderStatus()
     */
    @Override
    public OrderStatus getOrderStatus()
    {
        return orderStatus;
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
     * Get the instrument value.
     *
     * @return an <code>Instrument</code> value
     */
    public Instrument getInstrument()
    {
        return instrument;
    }
    /**
     * Get the orderQuantity value.
     *
     * @return a <code>BigDecimal</code> value
     */
    public BigDecimal getOrderQuantity()
    {
        return orderQuantity;
    }
    /**
     * Get the cumulativeQuantity value.
     *
     * @return a <code>BigDecimal</code> value
     */
    public BigDecimal getCumulativeQuantity()
    {
        return cumulativeQuantity;
    }
    /**
     * Get the leavesQuantity value.
     *
     * @return a <code>BigDecimal</code> value
     */
    public BigDecimal getLeavesQuantity()
    {
        return leavesQuantity;
    }
    /**
     * Get the orderPrice value.
     *
     * @return a <code>BigDecimal</code> value
     */
    public BigDecimal getOrderPrice()
    {
        return orderPrice;
    }
    /**
     * Get the averagePrice value.
     *
     * @return a <code>BigDecimal</code> value
     */
    public BigDecimal getAveragePrice()
    {
        return averagePrice;
    }
    /**
     * Get the account value.
     *
     * @return a <code>String</code> value
     */
    public String getAccount()
    {
        return account;
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
     * Get the lastPrice value.
     *
     * @return a <code>BigDecimal</code> value
     */
    public BigDecimal getLastPrice()
    {
        return lastPrice;
    }
    /**
     * Get the exchange value.
     *
     * @return a <code>String</code> value
     */
    public String getExchange()
    {
        return exchange;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("DisplayReport [report=").append(report).append("]");
        return builder.toString();
    }
    /**
     * Create a new DisplayReport instance.
     *
     * @param inReport a <code>Report</code> value
     */
    public DisplayReport(Report inReport)
    {
        report = inReport;
        try {
            fixMessage = new quickfix.Message(report.getFixMessage());
            if(fixMessage.isSetField(quickfix.field.OrdStatus.FIELD)) {
                orderStatus = OrderStatus.getInstanceForFIXMessage(fixMessage);
            } else {
                orderStatus = null;
            }
            if(fixMessage.isSetField(quickfix.field.Side.FIELD)) {
                side = Side.getInstanceForFIXValue(fixMessage.getChar(quickfix.field.Side.FIELD));
            } else {
                side = null;
            }
            if(fixMessage.isSetField(quickfix.field.OrigClOrdID.FIELD)) {
                originalOrderId = new OrderID(fixMessage.getString(quickfix.field.OrigClOrdID.FIELD));
            } else {
                originalOrderId = null;
            }
            Instrument tmpInstrument = null;
            if(fixMessage.isSetField(quickfix.field.Symbol.FIELD)) {
                try {
                    tmpInstrument = InstrumentFromMessage.SELECTOR.forValue(fixMessage).extract(fixMessage);
                } catch (Exception e) {
                    SLF4JLoggerProxy.warn(this,
                                          e);
                }
            } else {
                tmpInstrument = null;
            }
            instrument = tmpInstrument;
            if(fixMessage.isSetField(quickfix.field.OrderQty.FIELD)) {
                orderQuantity = fixMessage.getDecimal(quickfix.field.OrderQty.FIELD);
            } else {
                orderQuantity = null;
            }
            if(fixMessage.isSetField(quickfix.field.CumQty.FIELD)) {
                cumulativeQuantity = fixMessage.getDecimal(quickfix.field.CumQty.FIELD);
            } else {
                cumulativeQuantity = null;
            }
            if(fixMessage.isSetField(quickfix.field.LeavesQty.FIELD)) {
                leavesQuantity = fixMessage.getDecimal(quickfix.field.LeavesQty.FIELD);
            } else {
                leavesQuantity = null;
            }
            if(fixMessage.isSetField(quickfix.field.LastQty.FIELD)) {
                lastQuantity = fixMessage.getDecimal(quickfix.field.LastQty.FIELD);
            } else {
                lastQuantity = null;
            }
            if(fixMessage.isSetField(quickfix.field.Price.FIELD)) {
                orderPrice = fixMessage.getDecimal(quickfix.field.Price.FIELD);
            } else {
                orderPrice = null;
            }
            if(fixMessage.isSetField(quickfix.field.AvgPx.FIELD)) {
                averagePrice = fixMessage.getDecimal(quickfix.field.AvgPx.FIELD);
            } else {
                averagePrice = null;
            }
            if(fixMessage.isSetField(quickfix.field.LastPx.FIELD)) {
                lastPrice = fixMessage.getDecimal(quickfix.field.LastPx.FIELD);
            } else {
                lastPrice = null;
            }
            if(fixMessage.isSetField(quickfix.field.Account.FIELD)) {
                account = fixMessage.getString(quickfix.field.Account.FIELD);
            } else {
                account = null;
            }
            if(fixMessage.isSetField(quickfix.field.SecurityExchange.FIELD)) {
                exchange = fixMessage.getString(quickfix.field.SecurityExchange.FIELD);
            } else {
                exchange = null;
            }
        } catch (InvalidMessage | FieldNotFound e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * original order id value, may be <code>null</code>
     */
    private final OrderID originalOrderId;
    /**
     * order status of underlying FIX message, if available, otherwise will be <code>null</code>
     */
    private final OrderStatus orderStatus;
    /**
     * side of underlying FIX message, if available, otherwise will be <code>null</code>
     */
    private final Side side;
    /**
     * report value
     */
    private final Report report;
    /**
     * instrument value, if available, otherwise will be <code>null</code>
     */
    private final Instrument instrument;
    /**
     * FIX message value
     */
    private final quickfix.Message fixMessage;
    /**
     * order quantity, if available
     */
    private final BigDecimal orderQuantity;
    /**
     * cumulative quantity, if available
     */
    private final BigDecimal cumulativeQuantity;
    /**
     * leaves quantity, if available
     */
    private final BigDecimal leavesQuantity;
    /**
     * last quantity, if available
     */
    private final BigDecimal lastQuantity;
    /**
     * order price, if available
     */
    private final BigDecimal orderPrice;
    /**
     * average price, if available
     */
    private final BigDecimal averagePrice;
    /**
     * last price, if available
     */
    private final BigDecimal lastPrice;
    /**
     * account value, if available
     */
    private final String account;
    /**
     * exchange value, if available
     */
    private final String exchange;
}
