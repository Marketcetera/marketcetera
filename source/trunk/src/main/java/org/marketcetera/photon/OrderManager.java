package org.marketcetera.photon;

import java.util.ArrayList;
import java.util.List;

import javax.jms.JMSException;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.apache.log4j.Logger;
import org.marketcetera.core.ClassVersion;
import org.marketcetera.core.IDFactory;
import org.marketcetera.core.MarketceteraException;
import org.marketcetera.core.NoMoreIDsException;
import org.marketcetera.photon.actions.CommandEvent;
import org.marketcetera.photon.actions.ICommandListener;
import org.marketcetera.photon.model.FIXMessageHistory;
import org.marketcetera.quickfix.FIXDataDictionaryManager;
import org.marketcetera.quickfix.FIXMessageUtil;

import quickfix.DataDictionary;
import quickfix.FieldMap;
import quickfix.FieldNotFound;
import quickfix.Group;
import quickfix.InvalidMessage;
import quickfix.Message;
import quickfix.StringField;
import quickfix.field.ClOrdID;
import quickfix.field.CxlRejReason;
import quickfix.field.MsgType;
import quickfix.field.OrdStatus;
import quickfix.field.OrigClOrdID;
import quickfix.field.Symbol;
import quickfix.field.Text;

/**
 * $Id$
 * 
 * @author gmiller
 */
@ClassVersion("$Id$")
public class OrderManager {
	private IDFactory idFactory;
	
	private Logger internalMainLogger = Application.getMainConsoleLogger();


	private ICommandListener commandListener;

	private List<IOrderActionListener> orderActionListeners = new ArrayList<IOrderActionListener>();

	private FIXMessageHistory fixMessageHistory;
	

	/** Creates a new instance of OrderManager 
	 * @param fixMessageHistory */
	public OrderManager(IDFactory idFactory, FIXMessageHistory fixMessageHistory) {
		this.idFactory = idFactory;
		this.fixMessageHistory = fixMessageHistory;

		commandListener = new ICommandListener() {
			public void commandIssued(CommandEvent evt) {
				handleCommandIssued(evt);
			};
		};

	}

	public MessageListener getMessageListener() {
		return new MessageListener() {
			public void onMessage(javax.jms.Message message) {
				if (message instanceof TextMessage) {
					TextMessage textMessage = (TextMessage) message;
					try {
						quickfix.Message qfMessage = new Message(textMessage
								.getText());
						handleCounterpartyMessage(qfMessage);
					} catch (InvalidMessage e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (JMSException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		};

	}


	public void handleCounterpartyMessages(Object[] messages) {
		for (int i = 0; i < messages.length; i++) {
			Object object = messages[i];
			if (object instanceof Message) {
				Message aMessage = (Message) object;
				handleCounterpartyMessage(aMessage);
			}
		}
	}

	public void handleInternalMessages(Object[] messages) {
		for (int i = 0; i < messages.length; i++) {
			try {
				Object object = messages[i];
				if (object instanceof Message) {
					Message aMessage = (Message) object;
					handleInternalMessage(aMessage);
				}
			} catch (NoMoreIDsException ex) {
				internalMainLogger
						.error(
								"Could not get new ID's from database. Really bad.",
								ex);
			} catch (FieldNotFound e) {
				// TODO: fix this
				internalMainLogger.error("Error doing stuff", e);
			} catch (MarketceteraException e) {
				// TODO: fix this
				internalMainLogger.error("Error doing stuff", e);
			} catch (JMSException e) {
				// TODO: fix this
				internalMainLogger.error("Error doing stuff", e);
			}
		}
	}


	public void handleCounterpartyMessage(Message aMessage) {
		fixMessageHistory.addIncomingMessage(aMessage);
		try {
			if (FIXMessageUtil.isExecutionReport(aMessage)) {
				handleExecutionReport(aMessage);
			} else if (FIXMessageUtil.isCancelReject(aMessage)) {
				handleCancelReject(aMessage);
			}
		} catch (Throwable ex) {
			internalMainLogger.error(
					"Error decoding incoming message "+ex.getMessage(), ex);
			ex.printStackTrace();
		}
	}

	private void handleExecutionReport(Message aMessage) throws FieldNotFound, NoMoreIDsException {
		String orderID;
		ClOrdID clOrdID = new ClOrdID();
		aMessage.getField(clOrdID);
		orderID = clOrdID.getValue();
		char ordStatus = aMessage.getChar(OrdStatus.FIELD);

		if (ordStatus == OrdStatus.REJECTED) {
			String rejectMsg = "Order rejected " + orderID + " "
					+ aMessage.getString(Symbol.FIELD);
			internalMainLogger.info(rejectMsg);
		}
	}


	private void handleCancelReject(Message aMessage) throws FieldNotFound {
		String reason = aMessage.getString(CxlRejReason.FIELD);
		String text = aMessage.getString(Text.FIELD);
		String origClOrdID = aMessage.getString(OrigClOrdID.FIELD);
		String errorMsg = "Cancel rejected for order " + origClOrdID + ": "
				+ (text == null ? "" : text)
				+ (reason == null ? "" : " (" + reason + ")");
		internalMainLogger.error(errorMsg);
	}





	public void handleInternalMessage(Message aMessage) throws FieldNotFound,
			MarketceteraException, JMSException {

		if (FIXMessageUtil.isOrderSingle(aMessage)) {
			addNewOrder(aMessage);
		} else if (FIXMessageUtil.isCancelRequest(aMessage)) {
			try {
				aMessage.getString(OrigClOrdID.FIELD);
				cancelOneOrder(aMessage);
			} catch (FieldNotFound e) {
			}
		} else if (FIXMessageUtil.isCancelReplaceRequest(aMessage)) {
			cancelReplaceOneOrder(aMessage);
		} else if (FIXMessageUtil.isCancelRequest(aMessage)) {
			cancelOneOrder(aMessage);
		}

	}

	protected void addNewOrder(Message aMessage) throws FieldNotFound,
			MarketceteraException {
		
		try {
			fixMessageHistory.addOutgoingMessage(aMessage);

			sendToApplicationQueue(aMessage);
		} catch (JMSException ex) {
			internalMainLogger.error(
					"Error sending message to JMS", ex);
		}
	}



	protected void cancelReplaceOneOrder(Message cancelMessage)
			throws NoMoreIDsException, FieldNotFound, JMSException {

		String clOrdId = (String) cancelMessage.getString(OrigClOrdID.FIELD);
		FieldMap fields = new Group();
		fields.setString(ClOrdID.FIELD, clOrdId);
		Message latestMessage = fixMessageHistory.getLatestMessageForFields(fields);
		if (latestMessage != null){
			cancelMessage.setField(new OrigClOrdID(clOrdId));
			cancelMessage.setField(new ClOrdID(this.idFactory.getNext()));
			fillFieldsFromExistingMessage(cancelMessage, latestMessage);

			fixMessageHistory.addOutgoingMessage(cancelMessage);
			try {
				sendToApplicationQueue(cancelMessage);
			} catch (JMSException e) {
				internalMainLogger.error("Error sending cancel/replace for order "+clOrdId, e);
			}
		} else {
			internalMainLogger.error("Could not send cancel/replace request for order ID "+clOrdId);
		}
	}

	protected void cancelOneOrder(Message cancelMessage)
			throws NoMoreIDsException, FieldNotFound, JMSException {
		String clOrdId = (String) cancelMessage.getString(OrigClOrdID.FIELD);
		cancelOneOrderByClOrdID(clOrdId);
	}
	
	public void cancelOneOrderByClOrdID(String clOrdID) throws NoMoreIDsException {
		FieldMap fields = new Group();
		fields.setString(ClOrdID.FIELD, clOrdID);
		Message latestMessage = fixMessageHistory.getLatestMessageForFields(fields);
		if (latestMessage != null){
			Message cancelMessage = new quickfix.fix42.Message();
			cancelMessage.getHeader().setString(MsgType.FIELD, MsgType.ORDER_CANCEL_REQUEST);
			cancelMessage.setField(new OrigClOrdID(clOrdID));
			cancelMessage.setField(new ClOrdID(this.idFactory.getNext()));
			fillFieldsFromExistingMessage(cancelMessage, latestMessage);

			fixMessageHistory.addOutgoingMessage(cancelMessage);
			try {
				sendToApplicationQueue(cancelMessage);
			} catch (JMSException e) {
				internalMainLogger.error("Error sending cancel for order "+clOrdID, e);
			}
		} else {
			internalMainLogger.error("Could not send cancel request for order ID "+clOrdID);
		}
		
	}

	private void fillFieldsFromExistingMessage(Message outgoingMessage, Message existingMessage){
		try {
			String msgType = outgoingMessage.getHeader().getString(MsgType.FIELD);
		    DataDictionary dict = FIXDataDictionaryManager.getDictionary();
		    for (int fieldInt = 1; fieldInt < 2000; fieldInt++){
			    if (dict.isRequiredField(msgType, fieldInt) && existingMessage.isSetField(fieldInt) &&
			    		!outgoingMessage.isSetField(fieldInt)){
			    	try {
			    		outgoingMessage.setField(existingMessage.getField(new StringField(fieldInt)));
			    	} catch (FieldNotFound e) {
						// do nothing
					}
			    }
		    }

		} catch (FieldNotFound ex) {
			internalMainLogger.error(
					"Outgoing message did not have valid MsgType ", ex);
		}
	}
	


	public IDFactory getIDFactory() {
		return idFactory;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see org.marketcetera.photon.actions.ICommandListener#commandIssued(org.marketcetera.photon.actions.CommandEvent)
	 */
	public void handleCommandIssued(CommandEvent evt) {
		try {
			if (evt.getDestination() == CommandEvent.Destination.BROKER){
				handleInternalMessage(evt.getMessage());
			}
		} catch (Exception e) {
			this.internalMainLogger.error("Error processing command", e);
		}
	}

	/**
	 * @return Returns the commandListener.
	 */
	public ICommandListener getCommandListener() {
		return commandListener;
	}

	/* (non-Javadoc)
	 * @see java.util.List#add(E)
	 */
	public boolean addOrderActionListener(IOrderActionListener arg0) {
		return orderActionListeners.add(arg0);
	}

	/* (non-Javadoc)
	 * @see java.util.List#remove(int)
	 */
	public boolean removeOrderActionListener(IOrderActionListener arg0) {
		return orderActionListeners.remove(arg0);
	}
	
	protected void fireOrderActionOccurred(Message fixMessage){
		for (IOrderActionListener listener : orderActionListeners) {
			try {
			listener.orderActionTaken(fixMessage);
			} catch (Exception ex){
				internalMainLogger.error("Error notifying IOrderActionListener", ex);
			}
		}
	}


	protected void sendToApplicationQueue(Message message) throws JMSException
	{
		Application.sendToQueue(message);
	}
	
	/**
	 * @return Returns the mainConsoleLogger.
	 */
	public Logger getMainConsoleLogger() {
		return internalMainLogger;
	}

	/**
	 * @param mainConsoleLogger The mainConsoleLogger to set.
	 */
	public void setMainConsoleLogger(Logger mainConsoleLogger) {
		this.internalMainLogger = mainConsoleLogger;
	}

}
