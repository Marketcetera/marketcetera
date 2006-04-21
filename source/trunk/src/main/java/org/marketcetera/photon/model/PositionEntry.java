package org.marketcetera.photon.model;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Date;

import org.marketcetera.core.AccountID;
import org.marketcetera.core.BigDecimalUtils;
import org.marketcetera.core.InternalID;

public class PositionEntry extends PositionProgress {
	private final String name;

	private final Portfolio portfolio;


	public String getName() {
		return name;
	}

	public Portfolio getParent() {
		return portfolio;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.marketcetera.photon.model.PositionProgress#getProgress()
	 */
	@Override
	public double getProgress() {
		BigDecimal result = BigDecimalUtils.ZERO;
		if (mQuantity.compareTo(BigDecimalUtils.ZERO)> 0){
			result = mCumQty.divide(mQuantity);
		}
		result.round(new MathContext(2, RoundingMode.HALF_UP));
		return result.doubleValue();
	}
	
    private final InternalID mInternalID;
    private final String mSymbol;
    private final char mSide;
    private final BigDecimal mQuantity;
    private char mOrdStatus;
    private BigDecimal mLeavesQty;
    private BigDecimal mCumQty;
    private final BigDecimal mOrderPrice;
    private BigDecimal mAvgPx;
    private final Date mStartTime;
    private Date mLastFillTime;
    private final AccountID mAccountID;
    private boolean mIsMarket;
    private BigDecimal mLastQty;
    private BigDecimal mLastPrice;
    private String mLastMarket;
    private String mCounterpartyID;


    public PositionEntry(
    		Portfolio portfolio, String name,
            InternalID internal)
    {
    	this(portfolio, name, internal,(char)0, (char)0, BigDecimal.ZERO, name, BigDecimal.ZERO,
    			false, null, new Date());
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
    		Portfolio portfolio, String name,
            InternalID internal,
            char ordStatus,
            char side,
            BigDecimal qty,
            String symbol,
            BigDecimal price,
            boolean isMarket,
            AccountID accountID,
            Date timeReceived
            ) {
		this.portfolio = portfolio;
		this.name = name;

		mInternalID = internal;
        mOrdStatus = ordStatus;
        mSymbol = symbol;
        mSide = side;
        mQuantity = qty;
        mOrderPrice = price;
        mAccountID = accountID;
        mStartTime = timeReceived;
        mIsMarket = isMarket;

        mLeavesQty = qty;
        mCumQty = BigDecimalUtils.ZERO;
        mAvgPx = BigDecimalUtils.ZERO;
        mLastQty = BigDecimalUtils.ZERO;
        mLastPrice = BigDecimalUtils.ZERO;
    }

    public InternalID getInternalID() {
        return mInternalID;
    }

    public char getOrdStatus() {
        return mOrdStatus;
    }

    public void setOrdStatus(char ordStatus) {
        this.mOrdStatus = ordStatus;
    }

    public String getSymbol() {
        return mSymbol;
    }

    public char getSide() {
        return mSide;
    }

    public BigDecimal getQuantity() {
        return mQuantity;
    }

    public BigDecimal getLeavesQty() {
        return mLeavesQty;
    }

    public void setLeavesQty(BigDecimal leavesQty) {
        this.mLeavesQty = leavesQty;
    }

    public BigDecimal getCumQty() {
        return mCumQty;
    }

    public void setCumQty(BigDecimal cumQty) {
        this.mCumQty = cumQty;
    }

    public BigDecimal getAvgPrice() {
        return mAvgPx;
    }

    public void setAvgPrice(BigDecimal avgPx) {
        this.mAvgPx = avgPx;
    }

    public Date getStartTime() {
        return mStartTime;
    }

    public Date getLastFillTime() {
        return mLastFillTime;
    }

    public void setLastFillTime(Date lastFillTime) {
        this.mLastFillTime = lastFillTime;
    }


    public void update(BigDecimal cumQty, BigDecimal avgPrice, BigDecimal leavesQty, char ordStatus, Date lastFillTime) {
        setCumQty(cumQty);
        setAvgPrice(avgPrice);
        setLeavesQty(leavesQty);
        setOrdStatus(ordStatus);
        setLastFillTime(lastFillTime);
    }

    public void updateLast(BigDecimal lastQuantity, BigDecimal lastPrice, String lastMarket)
    {
        setLastQty(lastQuantity);
        setLastPrice(lastPrice);
        setLastMarket(lastMarket);
    }

    public final static double newAveragePriceFill(int existingQty, double existingAvgPrice,
                                                   int newFillQty, double newFillPrice) {
        long newTotal = existingQty + newFillQty;
        double newAverage = ((existingQty * existingAvgPrice)
                             + (newFillQty * newFillPrice) ) / newTotal;
        return newAverage;
    }

    public final static double newAveragePriceBust(int existingQty, double existingAvgPrice,
                                                   int newBustQty, double newBustPrice) {
        long newTotal = existingQty - newBustQty;
        double newAverage = ((existingQty*existingAvgPrice) - (newBustQty*newBustPrice)) / newTotal;
        return newAverage;
    }

    public AccountID getAccountID() {
        return mAccountID;
    }

    public BigDecimal getOrderPrice() {
        return mOrderPrice;
    }

    public boolean isMarket() {
        return mIsMarket;
    }

    public BigDecimal getLastQty() {
        return mLastQty;
    }

    public void setLastQty(BigDecimal lastQty) {
        this.mLastQty = lastQty;
    }

    public BigDecimal getLastPrice() {
        return mLastPrice;
    }

    public void setLastPrice(BigDecimal lastPrice) {
        this.mLastPrice = lastPrice;
    }

    public String getLastMarket() {
        return mLastMarket;
    }

    public void setLastMarket(String lastMarket) {
        this.mLastMarket = lastMarket;
    }

    public String getCounterpartyID() {
        return mCounterpartyID;
    }

    public void setCounterpartyID(String counterpartyID) {
        this.mCounterpartyID = counterpartyID;
    }

    protected Object clone() throws CloneNotSupportedException {

        Object retValue;

        retValue = super.clone();
        return retValue;
    }


}
