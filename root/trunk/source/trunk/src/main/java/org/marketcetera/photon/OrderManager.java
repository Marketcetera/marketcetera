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
import org.marketcetera.core.BasketID;
import org.marketcetera.core.BigDecimalUtils;
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
import org.marketcetera.quickfix.FIXField2StringConverter;
import org.marketcetera.quickfix.FIXMessageUtil;

import quickfix.FieldNotFound;
import quickfix.InvalidMessage;
import quickfix.Message;
import quickfix.field.Account;
import quickfix.field.AvgPx;
import quickfix.field.ClOrdID;
import quickfix.field.CumQty;
import quickfix.field.CxlRejReason;
import quickfix.field.ExecType;
import quickfix.field.LastMkt;
import quickfix.field.LastPx;
import quickfix.field.LastShares;
import quickfix.field.LeavesQty;
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
 * $Id$
 * 
 * @author gmiller
 */
@ClassVersion("$Id$")
public class OrderManager {
	private IDFactory mIDFactory;

	private Portfolio rootPortfolio;

	private Logger mInternalDebugLogger = Application.getDebugConsoleLogger();

	public static final String ORDER_MANAGER_NAME = "OrderManager";

	public static final String ORDER_MANAGER_FIX_CONDUIT_NAME = "OrderManagerFIXConduit";

	public static final String INTERNAL_FIX_CONDUIT_NAME = "InternalFIXConduit";

	ICommandListener commandListener;

	private Parser mCommandParser;

	private MessageListener jmsListener;

	List<IOrderActionListener> mOrderActionListeners = new ArrayList<IOrderActionListener>();

	/** Creates a new instance of OrderManager */
	public OrderManager(IDFactory idFactory) {
		rootPortfolio = new Portfolio(null, "Root portfolio");
		mIDFactory = idFactory;

		commandListener = new ICommandListener() {
			public void commandIssued(CommandEvent evt) {
				handleCommandIssued(evt);
			};
		};

		mCommandParser = new Parser();
		mCommandParser.init(mIDFactory);
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

	public void handleEvents(Object[] messages) {
		try {
			for (int i = 0; i < messages.length; i++) {
				Message aMessage = (Message) messages[i];
				addMessage(aMessage);
			}
		} catch (NoMoreIDsException ex) {
			mInternalDebugLogger.error(
					"Could not get new ID's from database. Really bad.", ex);
		} catch (FieldNotFound e) {
			// TODO: fix this
			mInternalDebugLogger.error("Error doing stuff", e);
		} catch (MarketceteraException e) {
			// TODO: fix this
			mInternalDebugLogger.error("Error doing stuff", e);
		} catch (JMSException e) {
			// TODO: fix this
			mInternalDebugLogger.error("Error doing stuff", e);
		}
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

	public void handleCounterpartyMessage(Message aMessage) {
		Application.getFIXMessageHistory().addIncomingMessage(aMessage);
		try {
			if (FIXMessageUtil.isExecutionReport(aMessage)) {
				handleExecutionReport(aMessage);
			} else if (FIXMessageUtil.isCancelReject(aMessage)) {
				handleCancelReject(aMessage);
			}
		} catch (Exception ex) {
			Application.getDebugConsoleLogger().error(
					"Error decoding incoming message", ex);
		}
	}

	public void handleInternalMessages(Object[] messages) {
		for (int i = 0; i < messages.length; i++) {
			Object object = messages[i];
			if (object instanceof Message) {
				Message aMessage = (Message) object;
				handleInternalMessage(aMessage);
			}
		}
	}

	public void handleInternalMessage(Message aMessage) {
		try {
			if (FIXMessageUtil.isCancelRequest(aMessage)) {
				cancelOneOrder(aMessage);
			}
		} catch (Exception ex) {
			Application.getDebugConsoleLogger().error(
					"Error decoding incoming message", ex);
		}
	}

	private void handleExecutionReport(Message aMessage) throws FieldNotFound {
		String orderID;
		String origOrderID = null;
		ClOrdID clOrdID = new ClOrdID();
		aMessage.getField(clOrdID);
		orderID = clOrdID.getValue();
		try {
			origOrderID = aMessage.getString(OrigClOrdID.FIELD);
		} catch (FieldNotFound ex) { /* do nothing */
		}

		PositionEntry position = getPosition(rootPortfolio, new InternalID(
				orderID));

		if (aMessage.getChar(ExecType.FIELD) == OrdStatus.REPLACED) {
			handleReplaced(new InternalID(origOrderID), position);
		} else if (position == null) {
			// it's a new order, start tracking it
			position = handleUnknownExecutionReport(aMessage, new InternalID(
					orderID));
		}

		BigDecimal cumQty = new BigDecimal(aMessage.getString(CumQty.FIELD));
		BigDecimal avgPx = new BigDecimal(aMessage.getString(AvgPx.FIELD));
		BigDecimal leavesQty = new BigDecimal(aMessage
				.getString(LeavesQty.FIELD));
		char ordStatus = aMessage.getChar(OrdStatus.FIELD);
		position.update(cumQty, avgPx, leavesQty, ordStatus, new Date());

		// update our internal record with a counterparty ID
		try {
			OrderID idField = new OrderID();
			aMessage.getField(idField);
			if (position.getCounterpartyID() == null) {
				position.setCounterpartyID(idField.getValue());
			}
		} catch (FieldNotFound ex) { /* do nothing */
		}

		try {
			BigDecimal lastQty = new BigDecimal(aMessage
					.getString(LastShares.FIELD));
			BigDecimal lastPx = new BigDecimal(aMessage.getString(LastPx.FIELD));
			String lastMarket = "";
			try {
				lastMarket = (String) aMessage.getString(LastMkt.FIELD);
			} catch (FieldNotFound ex) { /* do nothing */
			}
			position.updateLast(lastQty, lastPx, lastMarket);

			if (lastQty.compareTo(BigDecimal.ZERO) > 0) {
				
				String side = FIXField2StringConverter.getHumanFieldValue(Side.FIELD, aMessage
						.getString(Side.FIELD));
				String message = side + " " + lastQty + " "
						+ aMessage.getString(Symbol.FIELD) + " " + lastPx;
				Application.getDebugConsoleLogger().debug(
						"OMSClient should display message " + message);
				mInternalDebugLogger.info(message);
			}
		} catch (FieldNotFound ex) { /* do nothing */
		}

		if (ordStatus == OrdStatus.REJECTED) {
			String rejectMsg = "Order rejected " + orderID + " "
					+ aMessage.getString(Symbol.FIELD);
			mInternalDebugLogger.info(rejectMsg);
		}
		rootPortfolio.updateEntry(position);
	}

	private PositionEntry getPosition(Portfolio aPortfolio,
			InternalID internalID) {
		PositionProgress[] entries = aPortfolio.getEntries();
		for (PositionProgress progress : entries) {
			if (progress instanceof PositionEntry) {
				PositionEntry entry = (PositionEntry) progress;
				if (internalID.equals(entry.getInternalID())) {
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
		mInternalDebugLogger.error(errorMsg);
	}

	private void handleReplaced(InternalID oldID, PositionEntry newPosition) {
		PositionEntry oldSumm = getPosition(rootPortfolio, oldID);
		if (oldSumm != null) {
			oldSumm.setOrdStatus(OrdStatus.REPLACED);
			oldSumm.setCumQty(BigDecimal.ZERO);
			oldSumm.setLeavesQty(BigDecimal.ZERO);
			rootPortfolio.updateEntry(oldSumm);
		}
	}

	private PositionEntry handleUnknownExecutionReport(Message aMessage,
			InternalID internalID) throws FieldNotFound {

		String symbol = aMessage.getString(Symbol.FIELD);
		char side = aMessage.getChar(Side.FIELD);
		BigDecimal quantity = new BigDecimal(aMessage.getString(OrderQty.FIELD));
		BigDecimal price = BigDecimal.ZERO;
		Object account = aMessage.getString(Account.FIELD);
		AccountID accountID = (account == null) ? null : new AccountID(account
				.toString());
		char ordType = OrdType.LIMIT;
		char timeInForce = TimeInForce.DAY;
		try {
			ordType = aMessage.getChar(OrdType.FIELD);
		} catch (FieldNotFound ex) { /* do nothing */
		}
		try {
			timeInForce = aMessage.getChar(TimeInForce.FIELD);
		} catch (FieldNotFound ex) { /* do nothing */
		}

		if (ordType == OrdType.LIMIT || ordType == OrdType.LIMIT_ON_CLOSE
				|| ordType == OrdType.LIMIT_OR_BETTER) {
			try {
				price = new BigDecimal(aMessage.getString(Price.FIELD));
			} catch (FieldNotFound ex) {
				price = BigDecimal.ZERO;
			}
			addLimitOrder(internalID, side, quantity, symbol, price,
					timeInForce, accountID, null);
		} else if (ordType == OrdType.MARKET
				|| ordType == OrdType.MARKET_ON_CLOSE) {
			addMarketOrder(internalID, side, quantity, symbol, timeInForce,
					accountID, null);
		}
		PositionEntry newPosition = getPosition(rootPortfolio, internalID);
		try {
			if (newPosition.getCounterpartyID() == null) {
				newPosition
						.setCounterpartyID(aMessage.getString(OrderID.FIELD));
			}
		} catch (FieldNotFound e) { /* do nothing */
		}
		try {
			newPosition.update(
					new BigDecimal(aMessage.getString(CumQty.FIELD)),
					new BigDecimal(aMessage.getString(AvgPx.FIELD)),
					new BigDecimal(aMessage.getString(LeavesQty.FIELD)),
					aMessage.getChar(OrdStatus.FIELD), new Date());
		} catch (FieldNotFound e) { /* do nothing */
		}

		return newPosition;

	}

	public void addMessage(Message aMessage) throws FieldNotFound,
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
			doCancelReplace(aMessage);
		}
	}

	protected void addNewOrder(Message aMessage) throws FieldNotFound,
			MarketceteraException {
		String id;
		char side;
		BigDecimal quantity;
		String symbol;
		char timeInForce;
		String account = null;
		char ordType;

		id = aMessage.getString(ClOrdID.FIELD).toString();
		side = aMessage.getChar(Side.FIELD);
		quantity = new BigDecimal(aMessage.getString(OrderQty.FIELD));
		symbol = aMessage.getString(Symbol.FIELD).toString();
		timeInForce = aMessage.getChar(TimeInForce.FIELD);
		try {
			account = aMessage.getString(Account.FIELD);
		} catch (FieldNotFound ex) { /* do nothing */
		}
		ordType = aMessage.getChar(OrdType.FIELD);
		AccountID accountID = account == null ? null : new AccountID(account);

		if (ordType == OrdType.LIMIT || ordType == OrdType.LIMIT_ON_CLOSE
				|| ordType == OrdType.LIMIT_OR_BETTER) {
			BigDecimal price;
			price = new BigDecimal(aMessage.getString(Price.FIELD));
			addLimitOrder(new InternalID(id), side, quantity, symbol, price,
					timeInForce, accountID, null);
		} else if (ordType == OrdType.MARKET
				|| ordType == OrdType.MARKET_ON_CLOSE) {
			addMarketOrder(new InternalID(id), side, quantity, symbol,
					timeInForce, accountID, null);
		}
		try {
			Application.sendToQueue(aMessage);
		} catch (JMSException ex) {
			Application.getDebugConsoleLogger().error(
					"Error sending message to JMS", ex);
		}
	}

	protected void addLimitOrder(InternalID id, char side, BigDecimal quantity,
			String symbol, BigDecimal price, char timeInForce,
			AccountID accountID, BasketID basketID) {
		PositionEntry summary = new PositionEntry(null, "NEW ORDER", id,
				OrdStatus.PENDING_NEW, side, quantity, symbol, price, false,
				accountID, new Date());
		rootPortfolio.addEntry(summary);
	}

	protected void addMarketOrder(InternalID id, char side,
			BigDecimal quantity, String symbol, char timeInForce,
			AccountID accountID, BasketID basketID) {
		PositionEntry summary = new PositionEntry(null, "NEW ORDER", id,
				OrdStatus.PENDING_NEW, side, quantity, symbol,
				BigDecimal.ZERO, true, accountID, new Date());
		rootPortfolio.addEntry(summary);
	}

	// TODO: cache the list of open orders in the OrderTableModel
	public void cancelAllOrders() throws NoMoreIDsException, JMSException {
		PositionProgress[] entries = rootPortfolio.getEntries();
		for (int i = 0; i < entries.length; i++) {
			PositionEntry aSummary = (PositionEntry) entries[i];
			if (aSummary.getOrdStatus() == OrdStatus.ACCEPTED_FOR_BIDDING
					|| aSummary.getOrdStatus() == OrdStatus.CALCULATED
					|| aSummary.getOrdStatus() == OrdStatus.NEW
					|| aSummary.getOrdStatus() == OrdStatus.PARTIALLY_FILLED
					|| aSummary.getOrdStatus() == OrdStatus.PENDING_CANCEL
					|| aSummary.getOrdStatus() == OrdStatus.PENDING_NEW
					|| aSummary.getOrdStatus() == OrdStatus.PENDING_REPLACE
					|| aSummary.getOrdStatus() == OrdStatus.REPLACED
					|| aSummary.getOrdStatus() == OrdStatus.SUSPENDED) {
				Message aMessage = FIXMessageUtil.newCancel(new InternalID(
						mIDFactory.getNext()), aSummary.getInternalID(),
						aSummary.getSide(), aSummary.getQuantity(), aSummary
								.getSymbol(), aSummary.getCounterpartyID());
				Application.sendToQueue(aMessage);
				aSummary.setOrdStatus(OrdStatus.PENDING_CANCEL);
				rootPortfolio.updateEntry(aSummary);
			}
		}
	}

	public void cancelOneOrder(Message cancelMessage)
			throws NoMoreIDsException, FieldNotFound, JMSException {
		String orderID = (String) cancelMessage.getString(OrigClOrdID.FIELD);
		cancelOneOrder(orderID);
	}

	public void cancelOneOrder(String orderID) throws NoMoreIDsException,
			JMSException {
		PositionEntry aSummary = getPosition(rootPortfolio, new InternalID(
				orderID));
		if (aSummary != null) {
			Message aMessage = FIXMessageUtil.newCancel(new InternalID(
					mIDFactory.getNext()), aSummary.getInternalID(), aSummary
					.getSide(), aSummary.getQuantity(), aSummary.getSymbol(),
					aSummary.getCounterpartyID());
			Application.sendToQueue(aMessage);
		} else {
			mInternalDebugLogger.error("Could not send cancel for order "
					+ orderID);
		}
	}

	public void doCancelReplace(Message aMessage) throws FieldNotFound,
			JMSException {
		String origOrderID = (String) aMessage.getString(OrigClOrdID.FIELD);
		// String newOrderID = (String)aMessage.get(ClOrdID.FIELD);
		PositionEntry aPosition = getPosition(rootPortfolio, new InternalID(
				origOrderID));
		if (aPosition != null) {
			try {
				aMessage.getString(Symbol.FIELD);
			} catch (FieldNotFound e) {
				aMessage.setString(Symbol.FIELD, aPosition.getSymbol());
			}
			try {
				aMessage.getString(Side.FIELD);
			} catch (FieldNotFound e) {
				aMessage.setChar(Side.FIELD, aPosition.getSide());
			}
			try {
				aMessage.getString(OrdType.FIELD);
			} catch (FieldNotFound e) {
				aMessage.setChar(OrdType.FIELD,
						aPosition.isMarket() ? OrdType.MARKET : OrdType.LIMIT);
			}
			try {
				aMessage.getString(OrderID.FIELD);
			} catch (FieldNotFound e) {
				aMessage
						.setString(OrderID.FIELD, aPosition.getCounterpartyID());
			}
			try {
				aMessage.getString(OrderQty.FIELD);
			} catch (FieldNotFound e) {
				aMessage.setString(OrderQty.FIELD, aPosition.getQuantity()
						.toString());
			}
			try {
				aMessage.getString(Price.FIELD);
			} catch (FieldNotFound e) {
				aMessage.setString(Price.FIELD, aPosition.getOrderPrice()
						.toString());
			}
			Application.sendToQueue(aMessage);
			aPosition.setOrdStatus(OrdStatus.PENDING_REPLACE);
			rootPortfolio.updateEntry(aPosition);
		} else {
			mInternalDebugLogger.error("Could not send replace for order "
					+ origOrderID);
		}
	}

	public IDFactory getIDFactory() {
		return mIDFactory;
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
			mCommandParser.setInput(evt.getStringValue());
			Parser.Command aCommand;
			aCommand = mCommandParser.command();

			for (Object messageObj : aCommand.mResults) {
				Message message = (Message) messageObj;
				addMessage(message);
			}
		} catch (Exception e) {
			this.mInternalDebugLogger.error("Error processing command", e);
		}
	}

	/**
	 * @return Returns the commandListener.
	 */
	public ICommandListener getCommandListener() {
		return commandListener;
	}

}
