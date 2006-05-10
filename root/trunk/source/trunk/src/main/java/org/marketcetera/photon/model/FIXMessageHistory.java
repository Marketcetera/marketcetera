package org.marketcetera.photon.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.jface.util.ListenerList;
import org.marketcetera.quickfix.FIXMessageUtil;

import quickfix.Field;
import quickfix.FieldMap;
import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.field.ClOrdID;
import quickfix.field.LastQty;

public class FIXMessageHistory extends PlatformObject {

	List<MessageHolder> messageList;

	private ListenerList listeners;

	public FIXMessageHistory() {
		messageList = new ArrayList<MessageHolder>();
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
