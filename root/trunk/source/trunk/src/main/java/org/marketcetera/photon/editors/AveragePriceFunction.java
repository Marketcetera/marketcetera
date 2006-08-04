/**
 * 
 */
package org.marketcetera.photon.editors;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.photon.model.IncomingMessageHolder;
import org.marketcetera.photon.model.MessageHolder;

import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.field.Account;
import quickfix.field.AvgPx;
import quickfix.field.CumQty;
import quickfix.field.LastPx;
import quickfix.field.LastQty;
import quickfix.field.LeavesQty;
import quickfix.field.MsgType;
import quickfix.field.OrderQty;
import quickfix.field.Side;
import quickfix.field.Symbol;
import quickfix.fix42.ExecutionReport;
import ca.odell.glazedlists.FunctionList.Function;

@ClassVersion("$Id$")
public class AveragePriceFunction implements Function<List<MessageHolder>, MessageHolder> {
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
					if (message.getDouble(LastQty.FIELD) > 0) {
						avgPriceMessage = computeAveragePrice(avgPriceMessage, message);
					}
				} catch (FieldNotFound fnf){
					// do nothing
				}
			}
		}
		return avgPriceMessage==null ? null : new IncomingMessageHolder(avgPriceMessage);
	}

	private Message computeAveragePrice(Message avgPriceMessage, Message fillMessage) throws FieldNotFound {
		Message returnMessage = null;
		if (avgPriceMessage == null){
			returnMessage = new ExecutionReport();
			returnMessage.setField(fillMessage.getField(new Side()));
			returnMessage.setField(fillMessage.getField(new Symbol()));
			returnMessage.setField(fillMessage.getField(new OrderQty()));
			returnMessage.setField(fillMessage.getField(new CumQty()));
			returnMessage.setField(fillMessage.getField(new LeavesQty()));
			returnMessage.setField(fillMessage.getField(new AvgPx()));
			try { returnMessage.setField(fillMessage.getField(new Account())); } catch (FieldNotFound ex) { /* do nothing */ }
			returnMessage.getHeader().setField(new MsgType(MsgType.EXECUTION_REPORT));
		} else {
			BigDecimal existingCumQty = new BigDecimal(avgPriceMessage.getString(CumQty.FIELD));
			BigDecimal existingAvgPx = new BigDecimal(avgPriceMessage.getString(AvgPx.FIELD));
			BigDecimal newLastQty = new BigDecimal(fillMessage.getString(LastQty.FIELD));
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

