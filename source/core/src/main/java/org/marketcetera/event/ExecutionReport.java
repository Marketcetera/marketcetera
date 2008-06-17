package org.marketcetera.event;

import java.math.BigDecimal;

import org.marketcetera.quickfix.FIXMessageUtil;
import org.marketcetera.core.MessageKey;

import quickfix.Message;
import quickfix.field.*;

public class ExecutionReport extends EventBase {

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
	
	public ExecutionReport(long messageId, long timestamp, Message executionReport) throws IllegalArgumentException {
		super(messageId, timestamp, executionReport);
		
		if (!FIXMessageUtil.isExecutionReport(executionReport)){
			throw new IllegalArgumentException(MessageKey.ERROR_MSG_NOT_EXEC_REPORT.getLocalizedMessage());
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
			return new BigDecimal(message.getString(field));
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
}
