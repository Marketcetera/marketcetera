package org.marketcetera.photon.model;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Date;

import org.marketcetera.core.AccountID;
import org.marketcetera.core.InternalID;
import org.marketcetera.core.MSymbol;

import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.field.AvgPx;
import quickfix.field.ClOrdID;
import quickfix.field.CumQty;
import quickfix.field.LastMkt;
import quickfix.field.LastPx;
import quickfix.field.LastQty;
import quickfix.field.LeavesQty;
import quickfix.field.OrdStatus;
import quickfix.field.OrderQty;
import quickfix.field.Side;
import quickfix.field.TransactTime;

public class PositionEntry extends PositionProgress {
	private final String name;

	private Portfolio portfolio;


	public String getName() {
		return name;
	}

	public Portfolio getParent() {
		return portfolio;
	}
	public void setParent(Portfolio newParent) {
		portfolio = newParent;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.marketcetera.photon.model.PositionProgress#getProgress()
	 */
	@Override
	public double getProgress() {
		BigDecimal result = BigDecimal.ZERO;
		BigDecimal targetQuantity = getTargetQuantity();
		if (targetQuantity.compareTo(BigDecimal.ZERO) > 0){
			result = getCumQty().divide(targetQuantity);
		}
		result.round(new MathContext(2, RoundingMode.HALF_UP));
		return result.doubleValue();
	}
	
    private final InternalID internalID;
    private final MSymbol symbol;
    private final char side;
    private final Date startTime;
    private final AccountID accountID;
    
    private FIXMessageHistory history;


    public PositionEntry(
    		Portfolio portfolio, MSymbol symbol,
            InternalID internal)
    {
    	this(portfolio, symbol.getFullSymbol(), internal, Side.UNDISCLOSED, symbol, null, new Date());
    }

	/**
     * Creates a new instance of OrderProgressSummary
     * @param internal
     * @param ordStatus
     * @param side
     * @param qty
     * @param symbol
     * @param price
     * @param isMarket
     * @param accountID
     * @param timeReceived
     */
    public PositionEntry(
    		Portfolio portfolio, 
    		String name,
            InternalID internal,
            char side,
            MSymbol symbol,
            AccountID accountID,
            Date startTime
            ) {
		this.portfolio = portfolio;
		this.name = name;

		this.internalID = internal;
		this.symbol = symbol;
        this.side = side;
        this.accountID = accountID;
        this.startTime = startTime;
        
        history = new FIXMessageHistory();
    }

    public InternalID getInternalID() {
        return internalID;
    }

    public char getOrdStatus() {
        try {
			Message lastExecutionReport = getLastExecutionReport();
			if (lastExecutionReport == null) {
				return '\0';
			} else {
				return lastExecutionReport.getChar(OrdStatus.FIELD);
			}
		} catch (FieldNotFound e) {
			return '\0';
		}
	}

    private Message getLastExecutionReport() {
		Object[] latestExecutionReports = history.getLatestExecutionReports();
		if (latestExecutionReports.length > 0){
			return (Message)latestExecutionReports[0];
		} else {
			return null;
		}
	}
    
    public Message getLastMessageForClOrdID(InternalID clOrdID)
    {
    	Object[] theHistory = history.getHistory();
    	for (int i = theHistory.length -1; i >=0 ; i--)
    	{
    		MessageHolder aMessageHolder = (MessageHolder) theHistory[i];
    		Message message = aMessageHolder.getMessage();
			String clOrdIDString = clOrdID.toString();
			try {
				if (message.getString(ClOrdID.FIELD).equals(clOrdIDString)){
					return message;
				}
			} catch (FieldNotFound e) {
			}
    	}
    	return null;
    }


    public MSymbol getSymbol() {
        return symbol;
    }

    public char getSide() {
        return side;
    }

    public BigDecimal getTargetQuantity() {
    	Object [] messages = history.getLatestExecutionReports();
    	BigDecimal totalTarget = BigDecimal.ZERO;
    	for (Object aMessageObj : messages) {
    		Message aMessage = (Message) aMessageObj;
    		try {
	    		BigDecimal orderQty = new BigDecimal(aMessage.getString(OrderQty.FIELD));
	    		totalTarget = totalTarget.add(orderQty);
    		} catch (FieldNotFound e) {
    		}
    	}
    	return totalTarget;
    }

    public BigDecimal getLeavesQty() {
    	Object [] messages = history.getLatestExecutionReports();
    	BigDecimal totalLeaves = BigDecimal.ZERO;
    	for (Object aMessageObj : messages) {
    		Message aMessage = (Message) aMessageObj;
    		try {
	    		BigDecimal cumQty = new BigDecimal(aMessage.getString(LeavesQty.FIELD));
	    		totalLeaves = totalLeaves.add(cumQty);
    		} catch (FieldNotFound e) {
    		}
    	}
    	return totalLeaves;
    }

    public BigDecimal getCumQty() {
    	Object [] messages = history.getLatestExecutionReports();
    	BigDecimal totalShares = BigDecimal.ZERO;
    	for (Object aMessageObj : messages) {
    		Message aMessage = (Message) aMessageObj;
    		try {
	    		BigDecimal cumQty = new BigDecimal(aMessage.getString(CumQty.FIELD));
	    		totalShares = totalShares.add(cumQty);
    		} catch (FieldNotFound e) {
    		}
    	}
    	return totalShares;
    }

    public BigDecimal getAvgPrice() {
    	Object [] messages = history.getLatestExecutionReports();
    	BigDecimal totalShares = BigDecimal.ZERO;
    	BigDecimal totalDollarVolume = BigDecimal.ZERO;
    	for (Object aMessageObj : messages) {
    		Message aMessage = (Message) aMessageObj;
    		try {
	    		BigDecimal cumQty = new BigDecimal(aMessage.getString(CumQty.FIELD));
	    		BigDecimal avgPrice = new BigDecimal(aMessage.getString(AvgPx.FIELD));
	    		totalShares = totalShares.add(cumQty);
	    		totalDollarVolume = totalDollarVolume.add(cumQty.multiply(avgPrice));
    		} catch (FieldNotFound e) {
    		}
    	}
    	if (BigDecimal.ZERO.compareTo(totalShares) != 0)
    	{
    		return totalDollarVolume.divide(totalShares, MathContext.DECIMAL32);
    	} else { 
    		return BigDecimal.ZERO;
    	}
    }

    public Date getStartTime() {
        return startTime;
    }

    public Date getLastFillTime() {
        try {
			Message lastExecutionReport = getLastExecutionReport();
			return lastExecutionReport == null ? null : lastExecutionReport.getUtcTimeStamp(TransactTime.FIELD);
		} catch (FieldNotFound e) {
			return null;
		}
    }


    public AccountID getAccountID() {
        return accountID;
    }

    public BigDecimal getLastQty() {
        try {
			return new BigDecimal(getLastExecutionReport().getString(LastQty.FIELD));
		} catch (FieldNotFound e) {
			return BigDecimal.ZERO;
		}
    }

    public BigDecimal getLastPrice() {
        try {
			return new BigDecimal(getLastExecutionReport().getString(LastPx.FIELD));
		} catch (FieldNotFound e) {
			return BigDecimal.ZERO;
		}
    }

    public String getLastMarket() {
        try {
			return getLastExecutionReport().getString(LastMkt.FIELD);
		} catch (FieldNotFound e) {
			return "";
		}
    }

	/* (non-Javadoc)
	 * @see org.marketcetera.photon.model.FIXMessageHistory#addIncomingMessage(quickfix.Message)
	 */
	public void addIncomingMessage(Message fixMessage) {
		history.addIncomingMessage(fixMessage);
	}

	/* (non-Javadoc)
	 * @see org.marketcetera.photon.model.FIXMessageHistory#addOutgoingMessage(quickfix.Message)
	 */
	public void addOutgoingMessage(Message fixMessage) {
		history.addOutgoingMessage(fixMessage);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object arg0) {
		if (arg0 instanceof PositionEntry) {
			PositionEntry other = (PositionEntry) arg0;
			return safeEquals(this.symbol, other.symbol) && safeEquals(this.accountID, other.accountID) &&
				safeEquals(this.side, other.side);
		} else {
			return false;
		}
	}

	private boolean safeEquals(Object ptr1, Object ptr2){
		if (ptr1 == null){
			return ptr2 == null;
		} else {
			return ptr1.equals(ptr2);
		}
	}

	/**
	 * @return Returns the history.
	 */
	public FIXMessageHistory getHistory() {
		return history;
	}

}
