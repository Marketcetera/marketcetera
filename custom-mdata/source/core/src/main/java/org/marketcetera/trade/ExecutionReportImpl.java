package org.marketcetera.trade;

import org.marketcetera.quickfix.FIXMessageUtil;
import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.core.MSymbol;

import java.util.Date;
import java.math.BigDecimal;

import quickfix.Message;

/* $License$ */
/**
 * Execution Report implementation that wraps a FIX Message. This class
 * is public for the sake of JAXB and is not intended for general use.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public class ExecutionReportImpl extends ReportBaseImpl implements ExecutionReport {

    @Override
    public Date getTransactTime() {
        return FIXUtil.getTransactTime(getMessage());
    }

    @Override
    public ExecutionType getExecutionType() {
        return FIXUtil.getExecOrExecTransType(getMessage());
    }

    @Override
    public String getExecutionID() {
        return FIXUtil.getExecutionID(getMessage());
    }

    @Override
    public Side getSide() {
        return FIXUtil.getSide(getMessage());
    }

    @Override
    public MSymbol getSymbol() {
        return FIXUtil.getSymbol(getMessage());
    }

    @Override
    public BigDecimal getLastQuantity() {
        return FIXUtil.getLastQuantity(getMessage());
    }

    @Override
    public BigDecimal getLastPrice() {
        return FIXUtil.getLastPrice(getMessage());
    }

    @Override
    public String getLastMarket() {
        return FIXUtil.getLastMarket(getMessage());
    }

    @Override
    public BigDecimal getOrderQuantity() {
        return FIXUtil.getOrderQuantity(getMessage());
    }

    @Override
    public BigDecimal getLeavesQuantity() {
        return FIXUtil.getLeavesQuantity(getMessage());
    }

    @Override
    public BigDecimal getCumulativeQuantity() {
        return FIXUtil.getCumulativeQuantity(getMessage());
    }

    @Override
    public BigDecimal getAveragePrice() {
        return FIXUtil.getAveragePrice(getMessage());
    }

    @Override
    public String getAccount() {
        return FIXUtil.getAccount(getMessage());
    }

    @Override
    public OrderType getOrderType() {
        return FIXUtil.getOrderType(getMessage());
    }

    @Override
    public OrderCapacity getOrderCapacity() {
        return FIXUtil.getOrderCapacity(getMessage());
    }

    @Override
    public PositionEffect getPositionEffect() {
        return FIXUtil.getPositionEffect(getMessage());
    }

    @Override
    public TimeInForce getTimeInForce() {
        return FIXUtil.getTimeInForce(getMessage());
    }

    @Override
    public Originator getOriginator() {
        return mOriginator;
    }
    
    @Override
    public boolean isCancelable() {
        return FIXMessageUtil.isCancellable(getMessage());
    }

    @Override
    public String toString() {
        return Messages.EXECUTION_REPORT_TO_STRING.getText(
                String.valueOf(getAccount()),
                String.valueOf(getAveragePrice()),
                String.valueOf(getCumulativeQuantity()),
                String.valueOf(getBrokerID()),
                String.valueOf(getExecutionID()),
                String.valueOf(getExecutionType()),
                String.valueOf(getLastMarket()),
                String.valueOf(getLastPrice()),
                String.valueOf(getLastQuantity()),
                String.valueOf(getLeavesQuantity()),
                String.valueOf(getOrderCapacity()),
                String.valueOf(getOrderID()),
                String.valueOf(getOrderQuantity()),
                String.valueOf(getOrderStatus()),
                String.valueOf(getOrderType()),
                String.valueOf(getOriginalOrderID()),
                String.valueOf(getOriginator()),
                String.valueOf(getPositionEffect()),
                String.valueOf(getReportID()),
                String.valueOf(getSendingTime()),
                String.valueOf(getSide()),
                String.valueOf(getSymbol()),
                String.valueOf(getText()),
                String.valueOf(getTimeInForce()),
                String.valueOf(getTransactTime()),
                String.valueOf(getBrokerOrderID()),
                String.valueOf(getMessage())
        );
    }

    /**
     * Creates an instance.
     *
     * @param inMessage The FIX Message of type execution report.
     * @param inBrokerID the brokerID from which this report originated.
     * @param inOriginator the originator of this message.
     */
    ExecutionReportImpl(Message inMessage,
                        BrokerID inBrokerID,
                        Originator inOriginator) {
        super(inMessage, inBrokerID);
        mOriginator = inOriginator;
    }

    /**
     * Creates an instance. This empty constructor is intended for use
     * by JAXB.
     */

    protected ExecutionReportImpl() {
        mOriginator = null;
    }

    private static final long serialVersionUID = 1L;
    private final Originator mOriginator;
}
