package org.marketcetera.photon;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.jms.JMSException;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.apache.log4j.Logger;
import org.marketcetera.core.AccountID;
import org.marketcetera.core.ClassVersion;
import org.marketcetera.core.IDFactory;
import org.marketcetera.core.InternalID;
import org.marketcetera.core.MarketceteraException;
import org.marketcetera.core.NoMoreIDsException;
import org.marketcetera.photon.actions.CommandEvent;
import org.marketcetera.photon.actions.ICommandListener;
import org.marketcetera.photon.model.Portfolio;
import org.marketcetera.photon.model.PositionEntry;
import org.marketcetera.photon.model.PositionProgress;
import org.marketcetera.photon.parser.Parser;
import org.marketcetera.quickfix.FIXDataDictionaryManager;
import org.marketcetera.quickfix.FIXMessageUtil;

import quickfix.DataDictionary;
import quickfix.FieldNotFound;
import quickfix.InvalidMessage;
import quickfix.Message;
import quickfix.StringField;
import quickfix.field.Account;
import quickfix.field.ClOrdID;
import quickfix.field.CxlRejReason;
import quickfix.field.ExecType;
import quickfix.field.MsgType;
import quickfix.field.OrdStatus;
import quickfix.field.OrdType;
import quickfix.field.OrderID;
import quickfix.field.OrderQty;
import quickfix.field.OrigClOrdID;
import quickfix.field.Side;
import quickfix.field.Symbol;
import quickfix.field.Text;
import quickfix.field.TimeInForce;

/**
 * $Id$
 * 
 * @author gmiller
 */
@ClassVersion("$Id$")
public class OrderManager {
	private IDFactory idFactory;

	private Portfolio rootPortfolio;
	
	private Logger internalMainLogger = Application.getMainConsoleLogger();

	public static final String ORDER_MANAGER_NAME = "OrderManager";

	ICommandListener commandListener;

	private Parser commandParser;

	private MessageListener jmsListener;

	List<IOrderActionListener> orderActionListeners = new ArrayList<IOrderActionListener>();

	/** Creates a new instance of OrderManager */
	public OrderManager(IDFactory idFactory, Portfolio rootPortfolio) {
		this.rootPortfolio = rootPortfolio;
		this.idFactory = idFactory;

		commandListener = new ICommandListener() {
			public void commandIssued(CommandEvent evt) {
				handleCommandIssued(evt);
			};
		};

		commandParser = new Parser();
		commandParser.init(idFactory);
	}

	public void init() {
		jmsListener = new MessageListener() {
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

		Application.setTopicListener(jmsListener);
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
		Application.getFIXMessageHistory().addIncomingMessage(aMessage);
		try {
			if (FIXMessageUtil.isExecutionReport(aMessage)) {
				handleExecutionReport(aMessage);
			} else if (FIXMessageUtil.isCancelReject(aMessage)) {
				handleCancelReject(aMessage);
			}
		} catch (Exception ex) {
			Application.getMainConsoleLogger().error(
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

		InternalID internalID = new InternalID(orderID);
		PositionEntry position = getPosition(rootPortfolio, internalID);

		if (position == null) {
			// it's a new order, start tracking it
			position = handleUnknownExecutionReport(aMessage, internalID);
		}

		position.addIncomingMessage(aMessage);

		if (ordStatus == OrdStatus.REJECTED) {
			String rejectMsg = "Order rejected " + orderID + " "
					+ aMessage.getString(Symbol.FIELD);
			internalMainLogger.info(rejectMsg);
		}
		rootPortfolio.updateEntry(position);
	}

	private PositionEntry getPosition(Portfolio aPortfolio, InternalID internalID) {
		PositionProgress[] entries = aPortfolio.getEntries();
		for (PositionProgress progress : entries) {
			if (progress instanceof PositionEntry) {
				PositionEntry entry = (PositionEntry) progress;
				if (entry.getLastMessageForClOrdID(internalID)!=null) {
					return entry;
				}
			}
			if (progress instanceof Portfolio) {
				Portfolio subPortfolio = (Portfolio) progress;
				return getPosition(subPortfolio, internalID);
			}
		}
		return null;
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


	private PositionEntry handleUnknownExecutionReport(Message aMessage,
			InternalID internalID) throws NoMoreIDsException, FieldNotFound {

		String symbol = aMessage.getString(Symbol.FIELD);
		PositionEntry newPosition = new PositionEntry(null, symbol, new InternalID(idFactory.getNext()));
		newPosition.addIncomingMessage(aMessage);

		this.rootPortfolio.addEntry(newPosition);
		return newPosition;
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
		String id;
		char side;
		String symbol;
		String account = null;

		id = aMessage.getString(ClOrdID.FIELD).toString();
		side = aMessage.getChar(Side.FIELD);
		symbol = aMessage.getString(Symbol.FIELD).toString();
		try {
			account = aMessage.getString(Account.FIELD);
		} catch (FieldNotFound ex) { /* do nothing */
		}
		AccountID accountID = account == null ? null : new AccountID(account);

		PositionEntry entry = new PositionEntry(rootPortfolio,symbol, new InternalID(id), side, symbol, accountID, new Date());
		entry.addOutgoingMessage(aMessage);
		this.rootPortfolio.addEntry(entry);
		
		try {
			Application.sendToQueue(aMessage);
		} catch (JMSException ex) {
			Application.getMainConsoleLogger().error(
					"Error sending message to JMS", ex);
		}
	}


//	// TODO: cache the list of open orders in the OrderTableModel
//	public void cancelAllOrders() throws NoMoreIDsException, JMSException {
//		PositionProgress[] entries = rootPortfolio.getEntries();
//		for (int i = 0; i < entries.length; i++) {
//			PositionEntry aSummary = (PositionEntry) entries[i];
//			if (aSummary.getOrdStatus() == OrdStatus.ACCEPTED_FOR_BIDDING
//					|| aSummary.getOrdStatus() == OrdStatus.CALCULATED
//					|| aSummary.getOrdStatus() == OrdStatus.NEW
//					|| aSummary.getOrdStatus() == OrdStatus.PARTIALLY_FILLED
//					|| aSummary.getOrdStatus() == OrdStatus.PENDING_CANCEL
//					|| aSummary.getOrdStatus() == OrdStatus.PENDING_NEW
//					|| aSummary.getOrdStatus() == OrdStatus.PENDING_REPLACE
//					|| aSummary.getOrdStatus() == OrdStatus.REPLACED
//					|| aSummary.getOrdStatus() == OrdStatus.SUSPENDED) {
//				Message aMessage = FIXMessageUtil.newCancel(new InternalID(
//						idFactory.getNext()), aSummary.getInternalID(),
//						aSummary.getSide(), aSummary.getQuantity(), aSummary
//								.getSymbol(), aSummary.getCounterpartyID());
//				Application.sendToQueue(aMessage);
//				aSummary.setOrdStatus(OrdStatus.PENDING_CANCEL);
//				rootPortfolio.updateEntry(aSummary);
//			}
//		}
//	}

	public void cancelReplaceOneOrder(Message cancelMessage)
			throws NoMoreIDsException, FieldNotFound, JMSException {
		String clOrdId = (String) cancelMessage.getString(OrigClOrdID.FIELD);
		InternalID clOrdIDInternal = new InternalID(clOrdId);
		PositionEntry anEntry = getPosition(rootPortfolio, clOrdIDInternal);
		if (anEntry != null) {
			Message lastMessageForClOrdID = anEntry
					.getLastMessageForClOrdID(clOrdIDInternal);
			fillFieldsFromExistingMessage(cancelMessage, lastMessageForClOrdID);
		} else {
			internalMainLogger.error("Could not send cancel for order "
					+ clOrdId);
		}
	}

	public void cancelOneOrder(Message cancelMessage)
			throws NoMoreIDsException, FieldNotFound, JMSException {
		String clOrdId = (String) cancelMessage.getString(OrigClOrdID.FIELD);
		InternalID clOrdIDInternal = new InternalID(clOrdId);
		PositionEntry anEntry = getPosition(rootPortfolio, clOrdIDInternal);
		if (anEntry != null) {
			Message lastMessageForClOrdID = anEntry.getLastMessageForClOrdID(clOrdIDInternal);
			fillFieldsFromExistingMessage(cancelMessage, lastMessageForClOrdID);
		} else {
			internalMainLogger.error("Could not send cancel for order "+ clOrdId);
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
			Application.getMainConsoleLogger().error(
					"Outgoing message did not have valid MsgType ", ex);
		}
	}
	


	public IDFactory getIDFactory() {
		return idFactory;
	}

	public Portfolio getRootPortfolio() {
		return rootPortfolio;
	}

	public String getName() {
		return ORDER_MANAGER_NAME;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.marketcetera.photon.actions.ICommandListener#commandIssued(org.marketcetera.photon.actions.CommandEvent)
	 */
	public void handleCommandIssued(CommandEvent evt) {
		try {
			commandParser.setInput(evt.getStringValue());
			Parser.Command aCommand;
			aCommand = commandParser.command();

			for (Object messageObj : aCommand.mResults) {
				Message message = (Message) messageObj;
				handleInternalMessage(message);
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

	public void cancelOneOrderByClOrdID(String clOrdID) throws NoMoreIDsException {
		Object[] latestExecutionReports = Application.getFIXMessageHistory().getLatestExecutionReports();
		for (Object object : latestExecutionReports) {
			Message aMessage = (Message) object;
			try {
				String foundClOrdID = aMessage.getString(ClOrdID.FIELD);
				if(foundClOrdID.equals(clOrdID)){
					char side = aMessage.getChar(Side.FIELD);
					BigDecimal quantity = new BigDecimal(aMessage.getString(OrderQty.FIELD));
					String symbol = aMessage.getString(Symbol.FIELD);
					String counterpartyID = aMessage.getString(OrderID.FIELD);
					FIXMessageUtil.newCancel(new InternalID(this.idFactory.getNext()), new InternalID(clOrdID),
							side, quantity, symbol, counterpartyID);
					
				}
			} catch (FieldNotFound ex){
				internalMainLogger.error("Could not send cancel request for order ID "+clOrdID, ex);
			}
		}
		
	}

}
