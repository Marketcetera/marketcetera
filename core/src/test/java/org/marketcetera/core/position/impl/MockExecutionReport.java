package org.marketcetera.core.position.impl;

import java.math.BigDecimal;
import java.util.Date;

import org.marketcetera.trade.BrokerID;
import org.marketcetera.trade.ExecutionReport;
import org.marketcetera.trade.ExecutionType;
import org.marketcetera.trade.Instrument;
import org.marketcetera.trade.OrderCapacity;
import org.marketcetera.trade.OrderID;
import org.marketcetera.trade.OrderStatus;
import org.marketcetera.trade.OrderType;
import org.marketcetera.trade.Originator;
import org.marketcetera.trade.PositionEffect;
import org.marketcetera.trade.ReportID;
import org.marketcetera.trade.Side;
import org.marketcetera.trade.TimeInForce;
import org.marketcetera.trade.UserID;

/* $License$ */

/**
 * Mock execution report implementing only what is necessary for {@link PositionEngineImpl}.
 *
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.5.0
 */
public class MockExecutionReport implements ExecutionReport {

	private static final long serialVersionUID = 1L;
	private final String mAccount;
	private final Instrument mInstrument;
	private final Side mSide;
	private final BigDecimal mPrice;
	private final BigDecimal mQuantity;
	private final OrderStatus mStatus;
	private final ExecutionType executionType;
	private final Originator originator;
	private final ReportID mId;
	private UserID mViewer;	
	/**
	 * Create a new MockExecutionReport instance.
	 *
	 * @param account a <code>String</code> value
	 * @param instrument an <code>Instrument</code> value
	 * @param trader a <code>long</code> value
	 * @param side a <code>Side</code> value
	 * @param price a <code>String</code> value
	 * @param quantity a <code>String</code> value
	 * @param sequence a <code>long</code> value
	 * @param status an <code>OrderStatus</code> value
	 * @param inExecutionType an <code>ExecutionType</code> value
	 * @param inOriginator an <code>Originator</code> value
	 */
	public MockExecutionReport(String account,
	                           Instrument instrument,
	                           long trader,
	                           Side side,
	                           String price,
	                           String quantity,
	                           long sequence,
	                           OrderStatus status,
	                           ExecutionType inExecutionType,
	                           Originator inOriginator)
	{
		mAccount = account;
		mInstrument = instrument;
		mSide = side;
		mPrice = new BigDecimal(price);
		mQuantity = new BigDecimal(quantity);
		mStatus = status;
		mId = new ReportID(sequence);
		mViewer = new UserID(trader);
		executionType = inExecutionType;
		originator = inOriginator;
	}

	@Override
	public BigDecimal getPrice() {
		return null;
	}
	
	@Override
	public String getAccount() {
		return mAccount;
	}

	@Override
	public Instrument getInstrument() {
		return mInstrument;
	}

	@Override
	public Side getSide() {
		return mSide;
	}

	@Override
	public BigDecimal getLastPrice() {
		return mPrice;
	}

	@Override
	public BigDecimal getLastQuantity() {
		return mQuantity;
	}

	@Override
	public OrderStatus getOrderStatus() {
		return mStatus;
	}

	@Override
	public ReportID getReportID() {
		return mId;
	}

	@Override
	public UserID getViewerID() {
		return mViewer;
	}

	@Override
	public Date getTransactTime() {
		return null;
	}

	@Override
	public BigDecimal getAveragePrice() {
		return null;
	}

	@Override
	public BigDecimal getCumulativeQuantity() {
		return null;
	}

	@Override
	public String getExecutionID() {
		return null;
	}

	@Override
	public ExecutionType getExecutionType() {
		return executionType;
	}

	@Override
	public String getLastMarket() {
		return null;
	}

	@Override
	public BigDecimal getLeavesQuantity() {
		return null;
	}

	@Override
	public OrderCapacity getOrderCapacity() {
		return null;
	}

	@Override
	public BigDecimal getOrderQuantity() {
		return null;
	}
	
	@Override
	public BigDecimal getOrderDisplayQuantity() {
		return null;
	}

	@Override
	public OrderType getOrderType() {
		return null;
	}

	@Override
	public Originator getOriginator() {
		return originator;
	}

	@Override
	public UserID getActorID() {
		return null;
	}

	@Override
	public PositionEffect getPositionEffect() {
		return null;
	}

	@Override
	public TimeInForce getTimeInForce() {
		return null;
	}

	@Override
	public boolean isCancelable() {
		return false;
	}

	@Override
	public BrokerID getBrokerID() {
		return null;
	}

	@Override
	public String getBrokerOrderID() {
		return null;
	}

	@Override
	public OrderID getOrderID() {
		return null;
	}

	@Override
	public OrderID getOriginalOrderID() {
		return null;
	}

	@Override
	public Date getSendingTime() {
		return null;
	}

	@Override
	public String getText() {
		return null;
	}
}