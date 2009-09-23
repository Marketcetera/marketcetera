package org.marketcetera.core.position.impl;

import java.math.BigDecimal;
import java.util.Date;

import org.marketcetera.trade.BrokerID;
import org.marketcetera.trade.ExecutionReport;
import org.marketcetera.trade.ExecutionType;
import org.marketcetera.trade.MSymbol;
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
	private final String account;
	private final MSymbol symbol;
	private final Side side;
	private final BigDecimal price;
	private final BigDecimal quantity;
	private final OrderStatus status;
	private final ReportID id;
	private UserID viewer;	
	
	public MockExecutionReport(String account, String symbol, long trader, Side side,
			String price, String quantity, long sequence, OrderStatus status) {
		this.account = account;
		this.symbol = new MSymbol(symbol);
		this.side = side;
		this.price = new BigDecimal(price);
		this.quantity = new BigDecimal(quantity);
		this.status = status;
		this.id = new ReportID(sequence);
		this.viewer = new UserID(trader);
	}

	@Override
	public String getAccount() {
		return account;
	}

	@Override
	public MSymbol getSymbol() {
		return symbol;
	}

	@Override
	public Side getSide() {
		return side;
	}

	@Override
	public BigDecimal getLastPrice() {
		return price;
	}

	@Override
	public BigDecimal getLastQuantity() {
		return quantity;
	}

	@Override
	public OrderStatus getOrderStatus() {
		return status;
	}

	@Override
	public ReportID getReportID() {
		return id;
	}

	@Override
	public UserID getViewerID() {
		return viewer;
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
		return null;
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
	public OrderType getOrderType() {
		return null;
	}

	@Override
	public Originator getOriginator() {
		return null;
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