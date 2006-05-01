package org.marketcetera.photon.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.jface.util.ListenerList;
import org.marketcetera.quickfix.FIXMessageUtil;

import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.field.ClOrdID;
import quickfix.field.LastQty;

public class FIXMessageHistory extends PlatformObject {

	public class MessageHolder {
		Message message;

		public MessageHolder(Message message) {
			this.message = message;
		}

		public Message getMessage() {
			return message;
		}
	}

	public class IncomingMessageHolder extends MessageHolder {
		public IncomingMessageHolder(Message message) {
			super(message);
		}
	}

	public class OutgoingMessageHolder extends MessageHolder {
		public OutgoingMessageHolder(Message message) {
			super(message);
		}
	}

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
	
	public Object [] getFills() {
		ArrayList<Message> messages = new ArrayList<Message>();
		for (MessageHolder holder : messageList) {
			if (holder instanceof IncomingMessageHolder) {
				IncomingMessageHolder inHolder = (IncomingMessageHolder) holder;
				Message message = inHolder.getMessage();
				try {
					// NOTE: generally you should get this field as
					// a BigDecimal, but because we're just comparing
					// to zero, it's ok
					if (message.getDouble(LastQty.FIELD) > 0){
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
