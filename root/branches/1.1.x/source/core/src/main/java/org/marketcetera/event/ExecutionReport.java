package org.marketcetera.event;

import java.math.BigDecimal;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.quickfix.FIXMessageUtil;

import quickfix.Message;
import quickfix.field.AvgPx;
import quickfix.field.ClOrdID;
import quickfix.field.CumQty;
import quickfix.field.ExecID;
import quickfix.field.ExecTransType;
import quickfix.field.ExecType;
import quickfix.field.LastMkt;
import quickfix.field.LastPx;
import quickfix.field.LastShares;
import quickfix.field.LeavesQty;
import quickfix.field.OrdStatus;
import quickfix.field.Side;
import quickfix.field.Symbol;

/* $License$ */

/**
 * Represents a market Execution Report.
 *
 * @author gmiller
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 0.5.0
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public class ExecutionReport 
    extends EventBase
    implements HasFIXMessage
{
	private String execID;
	private String clOrdID;
	private Character execTransType;
	private Character execType;
	private Character ordStatus;
	private String symbol;
	private Character side;
	private BigDecimal leavesQty;
	private BigDecimal lastShares;
	private BigDecimal lastPx;
	private BigDecimal cumQty;
	private BigDecimal avgPx;
	private String lastMkt;
	/**
	 * the underlying FIX message for the execution report
	 */
	private Message mExecutionReport;
	
	public ExecutionReport(long messageId, long timestamp,
			String execID, String clOrdID, Character execTransType,
			Character execType, Character ordStatus, String symbol,
			Character side, BigDecimal leavesQty, BigDecimal cumQty,
			BigDecimal avgPx, BigDecimal lastShares, BigDecimal lastPx, String lastMkt) {
		super(messageId, timestamp);
		this.execID = execID;
		this.clOrdID = clOrdID;
		this.execTransType = execTransType;
		this.execType = execType;
		this.ordStatus = ordStatus;
		this.symbol = symbol;
		this.side = side;
		this.leavesQty = leavesQty;
		this.cumQty = cumQty;
		this.avgPx = avgPx;
		this.lastShares = lastShares;
		this.lastPx = lastPx;
		this.lastMkt = lastMkt;
	}
	/**
	 * Create a new ExecutionReport instance.
	 *
	 * @param messageId a <code>long</code> value
	 * @param timestamp a <code>long</code> value containing the number of milliseconds since <code>EPOCH</code> in UTC
	 * @param executionReport
	 * @throws NullPointerException if the given FIX message is null
	 * @throws IllegalArgumentException if the given FIX message is not an Execution Report
	 */
	public ExecutionReport(long messageId, 
	                       long timestamp, 
	                       Message executionReport) 
	    throws IllegalArgumentException 
	{
		super(messageId, 
		      timestamp);
		if(executionReport == null) {
		    throw new NullPointerException();
		}
		if(!FIXMessageUtil.isExecutionReport(executionReport)){
			throw new IllegalArgumentException(Messages.ERROR_MSG_NOT_EXEC_REPORT.getText());
		}

		execID = safeGetString(ExecID.FIELD, executionReport);
		clOrdID = safeGetString(ClOrdID.FIELD, executionReport);
		execTransType = safeGetCharacter(ExecTransType.FIELD, executionReport);
		execType = safeGetCharacter(ExecType.FIELD, executionReport);
		ordStatus = safeGetCharacter(OrdStatus.FIELD, executionReport);
		symbol = safeGetString(Symbol.FIELD, executionReport);
		side = safeGetCharacter(Side.FIELD, executionReport);
		leavesQty = safeGetBigDecimal(LeavesQty.FIELD, executionReport);
		cumQty = safeGetBigDecimal(CumQty.FIELD, executionReport);
		avgPx = safeGetBigDecimal(AvgPx.FIELD, executionReport);
		lastShares = safeGetBigDecimal(LastShares.FIELD, executionReport);
		lastPx = safeGetBigDecimal(LastPx.FIELD, executionReport);
		lastMkt = safeGetString(LastMkt.FIELD, executionReport);
		mExecutionReport = executionReport;
	}

	private String safeGetString(int field, Message message) {
		try {
			return message.getString(field);
		} catch (Exception ex){}
		return null;
	}

	private Character safeGetCharacter(int field, Message message) {
		try {
			return message.getChar(field);
		} catch (Exception ex){}
		return null;
	}

	private BigDecimal safeGetBigDecimal(int field, Message message) {
		try {
			return new BigDecimal(message.getString(field)); //non-i18n
		} catch (Exception ex){}
		return null;
	}
	
	public String getExecID() {
		return execID;
	}
	public Character getExecTransType() {
		return execTransType;
	}
	public Character getExecType() {
		return execType;
	}
	public Character getOrdStatus() {
		return ordStatus;
	}
	public String getSymbol() {
		return symbol;
	}
	public Character getSide() {
		return side;
	}
	public BigDecimal getLeavesQty() {
		return leavesQty;
	}
	public BigDecimal getCumQty() {
		return cumQty;
	}
	public BigDecimal getAvgPx() {
		return avgPx;
	}
	
	public BigDecimal getLastShares() {
		return lastShares;
	}
	
	public BigDecimal getLastPx() {
		return lastPx;
	}

	public String getLastMkt() {
		return lastMkt;
	}
	
	public String getClOrdID() {
		return clOrdID;
	}
	/**
	 * Returns the underlying FIX message for this execution report.
	 *
	 * @return a <code>Message</code> value
	 */
	public Message getExecutionReport()
	{
	    return mExecutionReport;
	}
    /* (non-Javadoc)
     * @see org.marketcetera.event.HasFIXMessage#getMessage()
     */
    @Override
    public Message getMessage()
    {
        return getExecutionReport();
    }
}
