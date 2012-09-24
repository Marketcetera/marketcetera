package org.marketcetera.ors.history;

import java.math.BigDecimal;
import java.util.Date;

import org.marketcetera.trade.Side;

public class TradePosition {
	private Date sendingTime;
	private BigDecimal quantity=BigDecimal.ZERO;

	public TradePosition(ExecutionReportSummary e) {
		this.sendingTime = e.getSendingTime();
		if(e.getSide()==Side.Buy){
			quantity=quantity.add(e.getLastQuantity());
		}else{
			quantity=quantity.subtract(e.getLastQuantity());
		}
		
	}
	
	public Date getSendingTime() {
		return sendingTime;
	}
	public BigDecimal getQuantity() {
		return quantity;
	}

	@Override
	public String toString() {
		return "Data [sendingTime=" + sendingTime + ", quantity="
				+ quantity + "]";
	}
	
}
