package org.marketcetera.trade;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.core.MSymbol;

import java.util.Date;
import java.math.BigDecimal;

import quickfix.Message;

/* $License$ */
/**
 * Execution Report implementation that wraps a FIX Message.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$") //$NON-NLS-1$
class ExecutionReportImpl extends ReportBaseImpl implements ExecutionReport {

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

    /**
     * Creates an instance.
     *
     * @param inMessage The FIX Message of type execution report.
     * @param inDestinationID the destinationID from which this
     * @param inOriginator the originator of this message.
     */
    ExecutionReportImpl(Message inMessage, DestinationID inDestinationID,
                        Originator inOriginator) {
        super(inMessage, inDestinationID);
        mOriginator = inOriginator;
    }

    private static final long serialVersionUID = 1L;
    private final Originator mOriginator;
}
