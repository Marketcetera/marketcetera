package org.marketcetera.photon.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.jface.util.ListenerList;
import org.marketcetera.core.MSymbol;
import org.marketcetera.quickfix.FIXMessageUtil;

import quickfix.Field;
import quickfix.FieldMap;
import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.field.Account;
import quickfix.field.AvgPx;
import quickfix.field.ClOrdID;
import quickfix.field.CumQty;
import quickfix.field.LastPx;
import quickfix.field.LastQty;
import quickfix.field.LeavesQty;
import quickfix.field.OrderQty;
import quickfix.field.Side;
import quickfix.field.Symbol;

public class FIXMessageHistory extends PlatformObject {

	List<MessageHolder> messageList;

	private ListenerList listeners;

	public FIXMessageHistory() {
		messageList = new LinkedList<MessageHolder>();
	}
	
	public FIXMessageHistory(List<MessageHolder> messages){
		this();
		messageList.addAll(messages);
	}

	public void addIncomingMessage(quickfix.Message fixMessage) {
		messageList.add(new IncomingMessageHolder(fixMessage));
		fireIncomingMessage(fixMessage);
	}

	public void addOutgoingMessage(quickfix.Message fixMessage) {
		messageList.add(new OutgoingMessageHolder(fixMessage));
		fireOutgoingMessage(fixMessage);
	}

	public Object[] getHistory() {
		return messageList.toArray();
	}

	public Object[] getLatestExecutionReports() {
		Map<String, Message> messageMap = new TreeMap<String, Message>();
		for (MessageHolder holder : messageList) {
			if (holder instanceof IncomingMessageHolder) {
				IncomingMessageHolder inHolder = (IncomingMessageHolder) holder;
				Message message = inHolder.getMessage();
				if (FIXMessageUtil.isExecutionReport(message)) {
					try {
						messageMap.put(message.getString(ClOrdID.FIELD),
								message);
					} catch (FieldNotFound e) {
					}
				}
			}
		}
		Set<String> keySet = messageMap.keySet();
		Object[] messages = new Object[keySet.size()];
		int i = 0;
		for (String aMessageString : keySet) {
			messages[i++] = messageMap.get(aMessageString);
		}
		return messages;
	}

	public Message getLatestMessageForFields(FieldMap fields) {
		for (int i = messageList.size() - 1; i >= 0; i--) {
			MessageHolder holder = messageList.get(i);
			Message message = holder.getMessage();
			Iterator fieldMapIterator = fields.iterator();
			boolean found = true;
			while (fieldMapIterator.hasNext()) {
				Field specifiedField = (Field) fieldMapIterator.next();
				try {
					String messageFieldValue = message.getString(specifiedField.getField());
					if (!messageFieldValue.equals(
							specifiedField.getObject().toString())) {
						found = false;
						break;
					}
				} catch (FieldNotFound e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (found){
				return message;
			}
		}
		return null;
	}

	public Object[] getFills() {
		ArrayList<Message> messages = new ArrayList<Message>();
		for (MessageHolder holder : messageList) {
			if (holder instanceof IncomingMessageHolder) {
				IncomingMessageHolder inHolder = (IncomingMessageHolder) holder;
				Message message = inHolder.getMessage();
				try {
					// NOTE: generally you should get this field as
					// a BigDecimal, but because we're just comparing
					// to zero, it's ok
					if (message.getDouble(LastQty.FIELD) > 0) {
						messages.add(message);
					}
				} catch (FieldNotFound e) {
					// do nothing
				}
			}
		}
		return messages.toArray();
	}
	
	public FIXMessageHistory getAveragePriceHistory()
	{
		ArrayList<MessageHolder> messages = new ArrayList<MessageHolder>();
		Map<MSymbol, Message> tempMap = new HashMap<MSymbol, Message>();

		for (MessageHolder holder : messageList) {
			if (holder instanceof IncomingMessageHolder) {
				IncomingMessageHolder inHolder = (IncomingMessageHolder) holder;
				Message message = inHolder.getMessage();
				try {
					// NOTE: generally you should get this field as
					// a BigDecimal, but because we're just comparing
					// to zero, it's ok
					if (message.getDouble(LastQty.FIELD) > 0) {
						MSymbol symbol = new MSymbol(message.getString(Symbol.FIELD));
						tempMap.put(symbol, computeAveragePrice(tempMap.get(symbol), message));
					}
				} catch (FieldNotFound e) {
					// do nothing
				}
			}
		}
		for (MSymbol aKey : tempMap.keySet()) {
			messages.add(new IncomingMessageHolder(tempMap.get(aKey)));
		}
		return new FIXMessageHistory(messages);
	}
	
	private Message computeAveragePrice(Message avgPriceMessage, Message fillMessage) throws FieldNotFound {
		Message returnMessage = null;
		if (avgPriceMessage == null){
			returnMessage = new Message();
			returnMessage.setField(fillMessage.getField(new Side()));
			returnMessage.setField(fillMessage.getField(new Symbol()));
			returnMessage.setField(fillMessage.getField(new OrderQty()));
			returnMessage.setField(fillMessage.getField(new CumQty()));
			returnMessage.setField(fillMessage.getField(new LeavesQty()));
			returnMessage.setField(fillMessage.getField(new AvgPx()));
			returnMessage.setField(fillMessage.getField(new Account()));
		} else {
			BigDecimal existingCumQty = new BigDecimal(avgPriceMessage.getString(CumQty.FIELD));
			BigDecimal existingAvgPx = new BigDecimal(avgPriceMessage.getString(AvgPx.FIELD));
			BigDecimal newLastQty = new BigDecimal(avgPriceMessage.getString(LastQty.FIELD));
			BigDecimal newLastPx = new BigDecimal(avgPriceMessage.getString(LastPx.FIELD));
			BigDecimal newTotal = existingCumQty.add(newLastQty);
			if (newTotal.compareTo(BigDecimal.ZERO) != 0){
				BigDecimal numerator = existingCumQty.multiply(existingAvgPx).add(newLastQty.multiply(newLastPx));
				BigDecimal newAvgPx = numerator.divide(newTotal);
				avgPriceMessage.setString(AvgPx.FIELD, newAvgPx.toPlainString());
				avgPriceMessage.setString(CumQty.FIELD, newTotal.toPlainString());
			}
			returnMessage = avgPriceMessage;
		}
		return returnMessage;
	}

	public void addFIXMessageListener(IFIXMessageListener listener) {
		if (listeners == null)
			listeners = new ListenerList();
		listeners.add(listener);
	}

	public void removeFIXMessageListener(IFIXMessageListener listener) {
		if (listeners != null) {
			listeners.remove(listener);
			if (listeners.isEmpty())
				listeners = null;
		}
	}

	protected void fireOutgoingMessage(Message message) {
		if (listeners == null)
			return;
		Object[] rls = listeners.getListeners();
		for (int i = 0; i < rls.length; i++) {
			IFIXMessageListener listener = (IFIXMessageListener) rls[i];
			listener.outgoingMessage(message);
		}
	}

	protected void fireIncomingMessage(Message message) {
		if (listeners == null)
			return;
		Object[] rls = listeners.getListeners();
		for (int i = 0; i < rls.length; i++) {
			IFIXMessageListener listener = (IFIXMessageListener) rls[i];
			listener.incomingMessage(message);
		}
	}

	public int size() {
		return messageList.size();
	}
}
