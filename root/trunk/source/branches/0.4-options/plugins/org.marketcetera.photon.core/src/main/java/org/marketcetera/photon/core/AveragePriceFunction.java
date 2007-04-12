/**
 * 
 */
package org.marketcetera.photon.core;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.marketcetera.core.ClassVersion;

import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.StringField;
import quickfix.field.Account;
import quickfix.field.AvgPx;
import quickfix.field.CumQty;
import quickfix.field.LastPx;
import quickfix.field.LastShares;
import quickfix.field.LeavesQty;
import quickfix.field.MsgType;
import quickfix.field.OrderQty;
import quickfix.field.Side;
import quickfix.field.Symbol;
import quickfix.fix42.ExecutionReport;
import ca.odell.glazedlists.FunctionList.Function;

/**
 * The lambda-expression-like {@link Function} implementation
 * that given a list of FIX {@link Message}s representing
 * ExecutionReports, returns a single execution report (wrapped
 * in an {@link IncomingMessageHolder} representing
 * the average price and total quantity.
 * 
 * Note that this implementation assumes that the list passed
 * to it in {@link #evaluate(List)} have already been filtered
 * to group by symbol, side, and any other criteria such
 * that a single average price and quantity result make sense.
 * That is it doesn't make much sense to calculate the average
 * price of a buy of 300 IBM and a sale of 600 MSFT.
 * 
 * @author gmiller
 *
 */
@ClassVersion("$Id$")
public class AveragePriceFunction implements Function<List<MessageHolder>, MessageHolder> {
	/**
	 * Given the list of {@link quickfix.Message}s representing execution
	 * reports for a single security on a single side of the market, calculates
	 * the average price, and total quantity
	 * 
	 * @return an execution report--wrapped in an
	 *         IncomingMessageHolder--representing the average price and total
	 *         quantity
	 * @see ca.odell.glazedlists.FunctionList$Function#evaluate(java.lang.Object)
	 */
	public IncomingMessageHolder evaluate(List<MessageHolder> arg0) {

		Message avgPriceMessage = null;
		for (MessageHolder holder : arg0) {
			if (holder instanceof IncomingMessageHolder) {
				IncomingMessageHolder incoming = (IncomingMessageHolder) holder;
				Message message = incoming.getMessage();
				// NOTE: generally you should get this field as
				// a BigDecimal, but because we're just comparing
				// to zero, it's ok
				try {
					if (message.getDouble(LastShares.FIELD) > 0) {
						avgPriceMessage = computeAveragePrice(avgPriceMessage, message);
					}
				} catch (FieldNotFound fnf){
					// do nothing
				}
			}
		}
		return avgPriceMessage==null ? null : new IncomingMessageHolder(avgPriceMessage);
	}

	/**
	 * The helper method responsible for updating the 
	 * current statistics for the average price and total
	 * quantity.  
	 * 
	 * @param avgPriceMessage the execution report representing the current view of the average price and total quantity
	 * @param fillMessage the new execution report to incorporate into the statistics
	 * @return the message passed in as avgPriceMessage, but modified to incorporate the fill in fillMessage
	 * @throws FieldNotFound if any of the required fields are not found.
	 */
	private Message computeAveragePrice(Message avgPriceMessage, Message fillMessage) throws FieldNotFound {
		Message returnMessage = null;
		if (avgPriceMessage == null){
			returnMessage = new ExecutionReport();
			returnMessage.setField(fillMessage.getField(new Side()));
			returnMessage.setField(fillMessage.getField(new Symbol()));
			returnMessage.setField(fillMessage.getField(new OrderQty()));
			returnMessage.setField(fillMessage.getField(new LeavesQty()));
			returnMessage.setField(new StringField(CumQty.FIELD, fillMessage.getString(LastShares.FIELD)));
			returnMessage.setField(new StringField(AvgPx.FIELD, fillMessage.getString(LastPx.FIELD)));
			try { returnMessage.setField(fillMessage.getField(new Account())); } catch (FieldNotFound ex) { /* do nothing */ }
			returnMessage.getHeader().setField(new MsgType(MsgType.EXECUTION_REPORT));
		} else {
			BigDecimal existingCumQty = new BigDecimal(avgPriceMessage.getString(CumQty.FIELD));
			BigDecimal existingAvgPx = new BigDecimal(avgPriceMessage.getString(AvgPx.FIELD));
			BigDecimal newLastQty = new BigDecimal(fillMessage.getString(LastShares.FIELD));
			BigDecimal newLastPx = new BigDecimal(fillMessage.getString(LastPx.FIELD));
			BigDecimal newTotal = existingCumQty.add(newLastQty);
			if (newTotal.compareTo(BigDecimal.ZERO) != 0){
				BigDecimal numerator = existingCumQty.multiply(existingAvgPx).add(newLastQty.multiply(newLastPx));
				numerator = numerator.setScale(10);
				BigDecimal newAvgPx = numerator.divide(newTotal, RoundingMode.HALF_UP);
				avgPriceMessage.setString(AvgPx.FIELD, newAvgPx.toPlainString());
				avgPriceMessage.setString(CumQty.FIELD, newTotal.toPlainString());
			}
			returnMessage = avgPriceMessage;
		}
		return returnMessage;
	}

}

