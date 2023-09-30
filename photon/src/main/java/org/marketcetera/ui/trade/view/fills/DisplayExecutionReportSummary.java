package org.marketcetera.ui.trade.view.fills;

import java.math.BigDecimal;
import java.util.Date;

import org.marketcetera.admin.User;
import org.marketcetera.core.instruments.InstrumentFromMessage;
import org.marketcetera.trade.BrokerID;
import org.marketcetera.trade.ExecutionReportSummary;
import org.marketcetera.trade.ExecutionType;
import org.marketcetera.trade.HasBrokerID;
import org.marketcetera.trade.HasInstrument;
import org.marketcetera.trade.Hierarchy;
import org.marketcetera.trade.Instrument;
import org.marketcetera.trade.OptionType;
import org.marketcetera.trade.OrderID;
import org.marketcetera.trade.OrderStatus;
import org.marketcetera.trade.OrderType;
import org.marketcetera.trade.Originator;
import org.marketcetera.trade.Report;
import org.marketcetera.trade.ReportID;
import org.marketcetera.trade.ReportType;
import org.marketcetera.trade.SecurityType;
import org.marketcetera.trade.Side;
import org.marketcetera.trade.TimeInForce;
import org.marketcetera.trade.UserID;
import org.marketcetera.ui.trade.view.DeletableFixMessageDisplayType;
import org.marketcetera.util.log.SLF4JLoggerProxy;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

/* $License$ */

/**
 * Provides a displayble, flattened {@link ExecutionReportSummary} implementation.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class DisplayExecutionReportSummary
        implements ExecutionReportSummary,Report,DeletableFixMessageDisplayType,HasInstrument,HasBrokerID
{
    /* (non-Javadoc)
     * @see org.marketcetera.trade.Report#getActorID()
     */
    @Override
    public UserID getActorID()
    {
        return executionReportSummary.getReport().getActorID();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.Report#getFixMessage()
     */
    @Override
    public String getFixMessage()
    {
        return executionReportSummary.getReport().getFixMessage();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.Report#getSessionID()
     */
    @Override
    public quickfix.SessionID getSessionId()
    {
        return executionReportSummary.getReport().getSessionId();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.Report#getMsgSeqNum()
     */
    @Override
    public int getMsgSeqNum()
    {
        return executionReportSummary.getReport().getMsgSeqNum();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.Report#getTransactTime()
     */
    @Override
    public Date getTransactTime()
    {
        return executionReportSummary.getReport().getTransactTime();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.Report#getReportType()
     */
    @Override
    public ReportType getReportType()
    {
        return executionReportSummary.getReport().getReportType();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.Report#getBrokerID()
     */
    @Override
    public BrokerID getBrokerID()
    {
        return executionReportSummary.getReport().getBrokerID();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.HasBrokerID#getBrokerId()
     */
    @Override
    public BrokerID getBrokerId()
    {
        return getBrokerID();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.Report#getReportID()
     */
    @Override
    public ReportID getReportID()
    {
        return executionReportSummary.getReport().getReportID();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.Report#getOriginator()
     */
    @Override
    public Originator getOriginator()
    {
        return executionReportSummary.getReport().getOriginator();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.Report#getHierarchy()
     */
    @Override
    public Hierarchy getHierarchy()
    {
        return executionReportSummary.getReport().getHierarchy();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.Report#getText()
     */
    @Override
    public String getText()
    {
        return executionReportSummary.getReport().getText();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.ExecutionReportSummary#getOrderID()
     */
    @Override
    public OrderID getOrderID()
    {
        return executionReportSummary.getOrderID();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.HasOrderId#setOrderId(org.marketcetera.trade.OrderID)
     */
    @Override
    public void setOrderId(OrderID inOrderId)
    {
        throw new UnsupportedOperationException();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.ExecutionReportSummary#getOriginalOrderID()
     */
    @Override
    public OrderID getOriginalOrderID()
    {
        return executionReportSummary.getOriginalOrderID();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.ExecutionReportSummary#getSecurityType()
     */
    @Override
    public SecurityType getSecurityType()
    {
        return executionReportSummary.getSecurityType();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.ExecutionReportSummary#getSymbol()
     */
    @Override
    public String getSymbol()
    {
        return executionReportSummary.getSymbol();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.ExecutionReportSummary#getExpiry()
     */
    @Override
    public String getExpiry()
    {
        return executionReportSummary.getExpiry();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.ExecutionReportSummary#getStrikePrice()
     */
    @Override
    public BigDecimal getStrikePrice()
    {
        return executionReportSummary.getStrikePrice();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.ExecutionReportSummary#getOptionType()
     */
    @Override
    public OptionType getOptionType()
    {
        return executionReportSummary.getOptionType();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.ExecutionReportSummary#getAccount()
     */
    @Override
    public String getAccount()
    {
        return executionReportSummary.getAccount();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.ExecutionReportSummary#getRootOrderID()
     */
    @Override
    public OrderID getRootOrderID()
    {
        return executionReportSummary.getRootOrderID();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.ExecutionReportSummary#getSide()
     */
    @Override
    public Side getSide()
    {
        return executionReportSummary.getSide();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.ExecutionReportSummary#getCumulativeQuantity()
     */
    @Override
    public BigDecimal getCumulativeQuantity()
    {
        return executionReportSummary.getCumulativeQuantity();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.ExecutionReportSummary#getEffectiveCumulativeQuantity()
     */
    @Override
    public BigDecimal getEffectiveCumulativeQuantity()
    {
        return executionReportSummary.getEffectiveCumulativeQuantity();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.ExecutionReportSummary#getAveragePrice()
     */
    @Override
    public BigDecimal getAveragePrice()
    {
        return executionReportSummary.getAveragePrice();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.ExecutionReportSummary#getLastQuantity()
     */
    @Override
    public BigDecimal getLastQuantity()
    {
        return executionReportSummary.getLastQuantity();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.ExecutionReportSummary#getLastPrice()
     */
    @Override
    public BigDecimal getLastPrice()
    {
        return executionReportSummary.getLastPrice();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.ExecutionReportSummary#getOrderStatus()
     */
    @Override
    public OrderStatus getOrderStatus()
    {
        return executionReportSummary.getOrderStatus();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.ExecutionReportSummary#getExecutionType()
     */
    @Override
    public ExecutionType getExecutionType()
    {
        return executionReportSummary.getExecutionType();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.ExecutionReportSummary#getSendingTime()
     */
    @Override
    public Date getSendingTime()
    {
        return executionReportSummary.getSendingTime();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.ExecutionReportSummary#getViewer()
     */
    @Override
    public User getViewer()
    {
        return executionReportSummary.getViewer();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.ExecutionReportSummary#getActor()
     */
    @Override
    public User getActor()
    {
        return executionReportSummary.getActor();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.ExecutionReportSummary#getReport()
     */
    @Override
    public Report getReport()
    {
        return executionReportSummary.getReport();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.ExecutionReportSummary#getViewerID()
     */
    @Override
    public UserID getViewerID()
    {
        return executionReportSummary.getViewerID();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.ExecutionReportSummary#getBrokerOrderId()
     */
    @Override
    public OrderID getBrokerOrderId()
    {
        return executionReportSummary.getBrokerOrderId();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.ExecutionReportSummary#getExecutionId()
     */
    @Override
    public String getExecutionId()
    {
        return executionReportSummary.getExecutionId();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.ExecutionReportSummary#getLeavesQuantity()
     */
    @Override
    public BigDecimal getLeavesQuantity()
    {
        return executionReportSummary.getLeavesQuantity();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.ExecutionReportSummary#getOrderQuantity()
     */
    @Override
    public BigDecimal getOrderQuantity()
    {
        return executionReportSummary.getOrderQuantity();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.ExecutionReportSummary#getOrderType()
     */
    @Override
    public OrderType getOrderType()
    {
        return executionReportSummary.getOrderType();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.ExecutionReportSummary#getPrice()
     */
    @Override
    public BigDecimal getPrice()
    {
        return executionReportSummary.getPrice();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.ExecutionReportSummary#getTimeInForce()
     */
    @Override
    public TimeInForce getTimeInForce()
    {
        return executionReportSummary.getTimeInForce();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.HasOrderId#getOrderId()
     */
    @Override
    public OrderID getOrderId()
    {
        return executionReportSummary.getOrderID();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.HasFIXMessage#getMessage()
     */
    @Override
    public quickfix.Message getMessage()
    {
        return fixMessage;
    }
    /**
     * Get the trader name value.
     *
     * @return a <code>String</code> value
     */
    public String getTrader()
    {
        return getReport().getActor().getName();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.HasInstrument#getInstrument()
     */
    @Override
    public Instrument getInstrument()
    {
        return instrument;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.HasInstrument#setInstrument(org.marketcetera.trade.Instrument)
     */
    @Override
    public void setInstrument(Instrument inInstrument)
    {
        throw new UnsupportedOperationException();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.ui.trade.executionreport.view.FixMessageDisplayType#isFillProperty()
     */
    @Override
    public BooleanProperty isFillProperty()
    {
        return fillProperty;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.ui.trade.executionreport.view.FixMessageDisplayType#isCancelProperty()
     */
    @Override
    public BooleanProperty isCancelProperty()
    {
        return cancelProperty;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("DisplayExecutionReportSummary [executionReportSummary=").append(executionReportSummary)
                .append("]");
        return builder.toString();
    }
    /**
     * Create a new DisplayExecutionReportSummary instance.
     *
     * @param inExecutionReportSummary an <code>ExecutionReportSummary</code> value
     */
    public DisplayExecutionReportSummary(ExecutionReportSummary inExecutionReportSummary)
    {
        executionReportSummary = inExecutionReportSummary;
        try {
            fixMessage = new quickfix.Message(executionReportSummary.getReport().getFixMessage());
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
        } catch (quickfix.InvalidMessage e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * Get the order id value.
     *
     * @return an <code>OrderID</code> value
     */
    public OrderID getOrdId()
    {
        return getOrderId();
    }
    /**
     * Get the order status value.
     *
     * @return an <code>OrderStatus</code> value
     */
    public OrderStatus getOrdStatus()
    {
        return getOrderStatus();
    }
    /**
     * Get the orderQuantity value.
     *
     * @return a <code>BigDecimal</code> value
     */
    public BigDecimal getOrdQty()
    {
        return getOrderQuantity();
    }
    /**
     * Get the cumulativeQuantity value.
     *
     * @return a <code>BigDecimal</code> value
     */
    public BigDecimal getCumQty()
    {
        return getCumulativeQuantity();
    }
    /**
     * Get the leavesQuantity value.
     *
     * @return a <code>BigDecimal</code> value
     */
    public BigDecimal getLeavesQty()
    {
        return getLeavesQuantity();
    }
    /**
     * Get the lastQuantity value.
     *
     * @return a <code>BigDecimal</code> value
     */
    public BigDecimal getLastQty()
    {
        return getLastQuantity();
    }
    /**
     * Get the lastPrice value.
     *
     * @return a <code>BigDecimal</code> value
     */
    public BigDecimal getLastPx()
    {
        return getLastPrice();
    }
    /**
     * Get the averagePrice value.
     *
     * @return a <code>BigDecimal</code> value
     */
    public BigDecimal getAvgPx()
    {
        return getAveragePrice();
    }
    /**
     * tracks whether this report was just canceled
     */
    private final BooleanProperty cancelProperty = new SimpleBooleanProperty(false);
    /**
     * tracks whether this report was just filled
     */
    private final BooleanProperty fillProperty = new SimpleBooleanProperty(false);
    /**
     * instrument value, if available, otherwise will be <code>null</code>
     */
    private final Instrument instrument;
    /**
     * FIX message value
     */
    private final quickfix.Message fixMessage;
    /**
     * execution report summary value
     */
    private final ExecutionReportSummary executionReportSummary;
}
