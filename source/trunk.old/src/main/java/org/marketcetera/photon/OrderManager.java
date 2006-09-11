package org.marketcetera.photon;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import javax.jms.JMSException;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.apache.log4j.Logger;
import org.eclipse.swt.widgets.Display;
import org.marketcetera.core.ClassVersion;
import org.marketcetera.core.IDFactory;
import org.marketcetera.core.LoggerAdapter;
import org.marketcetera.core.MarketceteraException;
import org.marketcetera.core.NoMoreIDsException;
import org.marketcetera.photon.actions.CommandEvent;
import org.marketcetera.photon.actions.ICommandListener;
import org.marketcetera.photon.model.FIXMessageHistory;
import org.marketcetera.photon.parser.Parser;
import org.marketcetera.quickfix.FIXDataDictionaryManager;
import org.marketcetera.quickfix.FIXMessageUtil;
import org.marketcetera.quickfix.MarketceteraFIXException;

import quickfix.DataDictionary;
import quickfix.FieldNotFound;
import quickfix.InvalidMessage;
import quickfix.Message;
import quickfix.StringField;
import quickfix.field.ClOrdID;
import quickfix.field.CxlRejReason;
import quickfix.field.ExecID;
import quickfix.field.MsgType;
import quickfix.field.OrdStatus;
import quickfix.field.OrdType;
import quickfix.field.OrderID;
import quickfix.field.OrderQty;
import quickfix.field.OrigClOrdID;
import quickfix.field.Price;
import quickfix.field.Side;
import quickfix.field.Symbol;
import quickfix.field.Text;
import quickfix.field.TimeInForce;

/**
 * OrderManager is the main repository for business logic.  It can be considered
 * the "controller" in a standard model-view-controller architecture.  The main entry
 * points are the <code>handle*</code> methods for handling incoming and outgoing 
 * messages of various types.  
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
	

	/** Creates a new instance of OrderManager with the specified {@link IDFactory}--
	 * used to create new order id's among other things,
	 * and the specified {@link FIXMessageHistory} into which all fix messages
	 * will be placed for later use in the UI.
	 * @param idFactory the id factory for generating order ids and other FIX ids
	 * @param fixMessageHistory the message history object for storing histories of FIX messages
	 * 
	 */
	public OrderManager(IDFactory idFactory, FIXMessageHistory fixMessageHistory) {
		this.idFactory = idFactory;
		this.fixMessageHistory = fixMessageHistory;

		commandListener = new ICommandListener() {
			public void commandIssued(CommandEvent evt) {
				handleCommandIssued(evt);
			};
		};

	}

	/**Get the {@link MessageListener} to hand to the JMS subsystem for
	 * retrieving messages asynchronously off of the incoming topic.
	 * @return  the MessageListener that routes messages back into this OrderManager
	 */
	public MessageListener getMessageListener() {
		return new MessageListener() {
			public void onMessage(javax.jms.Message message) {
				if (message instanceof TextMessage) {
					TextMessage textMessage = (TextMessage) message;
					try {
						final quickfix.Message qfMessage = new Message(textMessage
								.getText());
						Display.getDefault().asyncExec(new Runnable() {
							public void run() {
								handleCounterpartyMessage(qfMessage);
							}
						});
					} catch (InvalidMessage e) {
						Application.getMainConsoleLogger().error("Exception processing incoming message", e);
					} catch (JMSException e) {
						Application.getMainConsoleLogger().error("Exception processing incoming message", e);
					}
				}
			}
		};

	}


	/**
	 * Handles messages coming in on the incoming topic from the counterparty.
	 * 
	 * @param messages the array of incoming messages
	 */
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
		fireOrderActionOccurred(aMessage);
		try {
			if (FIXMessageUtil.isExecutionReport(aMessage)) {
				handleExecutionReport(aMessage);
			} else if (FIXMessageUtil.isCancelReject(aMessage)) {
				handleCancelReject(aMessage);
			}
		} catch (FieldNotFound fnfEx) {
			MarketceteraFIXException mfix = MarketceteraFIXException.createFieldNotFoundException(fnfEx);
			internalMainLogger.error(
					"Error decoding incoming message "+mfix.getMessage(), mfix);
			mfix.printStackTrace();
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
			String rejectReason = "";
			if(aMessage.isSetField(Text.FIELD)) {
				rejectReason = ": "+aMessage.getString(Text.FIELD);
			}
			
			String rejectMsg = "Order rejected " + orderID + " "
					+ aMessage.getString(Symbol.FIELD) + rejectReason;
			internalMainLogger.info(rejectMsg);
		}
	}


	private void handleCancelReject(Message aMessage) throws FieldNotFound {
		String reason = null;
		try {
			reason = aMessage.getString(CxlRejReason.FIELD);
		} catch (FieldNotFound fnf){
			//do nothing
		}
		String text = aMessage.getString(Text.FIELD);
		String origClOrdID = aMessage.getString(OrigClOrdID.FIELD);
		String errorMsg = "Cancel rejected for order " + origClOrdID + ": "
				+ (text == null ? "" : text)
				+ (reason == null ? "" : " (" + reason + ")");
		internalMainLogger.error(errorMsg);
	}


	public void handleInternalMessage(Message aMessage) throws FieldNotFound,
			MarketceteraException, JMSException {
		fireOrderActionOccurred(aMessage);

		if (FIXMessageUtil.isOrderSingle(aMessage)) {
			addNewOrder(aMessage);
		} else if (FIXMessageUtil.isCancelRequest(aMessage)) {
			cancelOneOrder(aMessage);
		} else if (FIXMessageUtil.isCancelReplaceRequest(aMessage)) {
			cancelReplaceOneOrder(aMessage);
		}
	}

	protected void addNewOrder(Message aMessage) throws FieldNotFound,
			MarketceteraException {
		
		try {
			fixMessageHistory.addOutgoingMessage(aMessage);

			boolean sentToQueue = sendToApplicationQueue(aMessage);
			
			if (sentToQueue)
				logAddNewOrder(aMessage); 
		} catch (JMSException ex) {
			internalMainLogger.error(
					"Error sending message to JMS", ex);
		}
	}

	protected void cancelReplaceOneOrder(Message cancelMessage)
			throws NoMoreIDsException, FieldNotFound, JMSException {

		String clOrdId = (String) cancelMessage.getString(OrigClOrdID.FIELD);
		Message latestMessage = fixMessageHistory.getLatestMessage(clOrdId);
		if (latestMessage != null){
			cancelMessage.setField(new OrigClOrdID(clOrdId));
			cancelMessage.setField(new ClOrdID(this.idFactory.getNext()));
			fillFieldsFromExistingMessage(cancelMessage, latestMessage);

			fixMessageHistory.addOutgoingMessage(cancelMessage);
			try {
				boolean sentToQueue = sendToApplicationQueue(cancelMessage);
				
				if (sentToQueue)
					logCancelOneOrder(cancelMessage);  // TEMP reuse the one for pure cancel until we do cancel _and_ replace here 
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
	
	public void cancelOneOrderByClOrdID(String clOrdID) throws NoMoreIDsException, FieldNotFound {
		Message latestMessage = fixMessageHistory.getLatestExecutionReport(clOrdID);
		if (latestMessage == null){
			latestMessage = fixMessageHistory.getLatestMessage(clOrdID);
			if (latestMessage == null){
				internalMainLogger.error("Could not send cancel request for order ID "+clOrdID);
				return;
			}
		}
		try { LoggerAdapter.debug("Exec id for cancel execution report:"+latestMessage.getString(ExecID.FIELD), this); } catch (FieldNotFound e1) {	}
			Message cancelMessage = new quickfix.fix42.Message();
			cancelMessage.getHeader().setString(MsgType.FIELD, MsgType.ORDER_CANCEL_REQUEST);
			cancelMessage.setField(new OrigClOrdID(clOrdID));
			cancelMessage.setField(new ClOrdID(this.idFactory.getNext()));
			try {
				cancelMessage.setField(new OrderID(latestMessage.getString(OrderID.FIELD)));
			} catch (FieldNotFound e) {
				// do nothing
			}
			fillFieldsFromExistingMessage(cancelMessage, latestMessage);

			fixMessageHistory.addOutgoingMessage(cancelMessage);
			try {
				boolean sentToQueue = sendToApplicationQueue(cancelMessage);
				
				if (sentToQueue)
					logCancelOneOrder(cancelMessage);
			} catch (JMSException e) {
				internalMainLogger.error("Error sending cancel for order "+clOrdID, e);
			}
	}

	private void logAddNewOrder(Message message) throws FieldNotFound {

		String priceString = getPriceString(message);
		String command = MessageFormat.format(
				"{0} {1} {2} {3} {4}",  //$NON-NLS-1$
				new Object[] {
					toSide(message.getChar(Side.FIELD)),
					message.getString(OrderQty.FIELD),
					message.getString(Symbol.FIELD),
					priceString,
					toTimeInForce(message.getChar(TimeInForce.FIELD))});
		logOrderCommand(command);
	}

	private String getPriceString(Message message) {
		String priceString = "";
		try {
			priceString = message.getString(Price.FIELD);
		} catch (FieldNotFound e) {
			try {
				if (OrdType.MARKET == message.getChar(OrdType.FIELD)){
					priceString = Parser.PriceImage.MKT.toString();
				}
			} catch (FieldNotFound fnf){
				//do nothing
			}
		}
		return priceString;
	}

	private void logCancelOneOrder(Message message) throws FieldNotFound {
		String command = MessageFormat.format(
				"C {0}",  //$NON-NLS-1$
				new Object[] {
						message.getString(OrigClOrdID.FIELD)});
		logOrderCommand(command);
	}

	private void logOrderCommand(String command)
	{
		String logMsg = "Order sent: " + command; 
		internalMainLogger.info(logMsg);
	}
	
	private String toTimeInForce(char fixTimeInForce) {
		switch(fixTimeInForce) {
		case TimeInForce.DAY:
			return Parser.TimeInForceImage.DAY.image;
		case TimeInForce.GOOD_TILL_CANCEL:
			return Parser.TimeInForceImage.GTC.image;
		case TimeInForce.FILL_OR_KILL:
			return Parser.TimeInForceImage.FOK.image;
		case TimeInForce.AT_THE_CLOSE:
			return Parser.TimeInForceImage.CLO.image;
		case TimeInForce.AT_THE_OPENING:
			return Parser.TimeInForceImage.OPG.image;
		case TimeInForce.IMMEDIATE_OR_CANCEL:
			return Parser.TimeInForceImage.IOC.image;
		default:
			return "" + fixTimeInForce; 
		}
	}
	
	private String toSide(char fixSide) {
		switch(fixSide) {
		case Side.BUY:
			return Parser.CommandImage.BUY.image;
		case Side.SELL:
			return Parser.CommandImage.SELL.image;
		case Side.SELL_SHORT:
			return Parser.CommandImage.SELL_SHORT.image;
		case Side.SELL_SHORT_EXEMPT:
			return Parser.CommandImage.SELL_SHORT_EXEMPT.image;
		default:
			return "" + fixSide; 
		}
	}

	private void fillFieldsFromExistingMessage(Message outgoingMessage, Message existingMessage){
		try {
			String msgType = outgoingMessage.getHeader().getString(MsgType.FIELD);
		    DataDictionary dict = FIXDataDictionaryManager.getDictionary();
		    for (int fieldInt = 1; fieldInt < 2000; fieldInt++){
			    if (dict.isMsgField(msgType, fieldInt)
			    		&& dict.isRequiredField(msgType, fieldInt) 
			    		&& existingMessage.isSetField(fieldInt)
			    		&& !outgoingMessage.isSetField(fieldInt)){
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
			e.printStackTrace();
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


	protected boolean sendToApplicationQueue(Message message) throws JMSException
	{
		return Application.sendToQueue(message);
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
