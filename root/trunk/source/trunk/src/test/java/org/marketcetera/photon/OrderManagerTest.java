package org.marketcetera.photon;

import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedList;

import javax.jms.JMSException;
import javax.jms.MessageListener;

import junit.framework.TestCase;

import org.marketcetera.core.InMemoryIDFactory;
import org.marketcetera.core.InternalID;
import org.marketcetera.core.MarketceteraException;
import org.marketcetera.core.NoMoreIDsException;
import org.marketcetera.photon.actions.CommandEvent;
import org.marketcetera.photon.actions.ICommandListener;
import org.marketcetera.photon.model.FIXMessageHistory;
import org.marketcetera.photon.model.IncomingMessageHolder;
import org.marketcetera.photon.model.OutgoingMessageHolder;
import org.marketcetera.photon.model.Portfolio;
import org.marketcetera.photon.model.PositionEntryTest;
import org.marketcetera.quickfix.FIXDataDictionaryManager;
import org.marketcetera.quickfix.FIXMessageUtil;

import quickfix.FieldNotFound;
import quickfix.IncorrectTagValue;
import quickfix.Message;
import quickfix.StringField;
import quickfix.field.ClOrdID;
import quickfix.field.MsgType;
import quickfix.field.OrderQty;
import quickfix.field.OrigClOrdID;
import quickfix.field.Side;
import quickfix.field.Symbol;
import quickfix.field.TimeInForce;
import quickfix.field.TransactTime;

public class OrderManagerTest extends TestCase {

	private InMemoryIDFactory idFactory;
	private Portfolio portfolio;
	private FIXMessageHistory messageHistory;
	private OrderManager orderManager;
	private LinkedList<Message> queuedMessages;

	static {
		try {
			FIXDataDictionaryManager.loadDictionary(FIXDataDictionaryManager.FIX_4_2_BEGIN_STRING);
		} catch (Throwable th) {
			th.printStackTrace();
		}
	}
	
	protected void setUp() throws Exception {
		idFactory = new InMemoryIDFactory(999);
		portfolio = new Portfolio(null, "Warren Buffett's Portfolio");
		messageHistory = new FIXMessageHistory();
		queuedMessages = new LinkedList<Message>();
		orderManager = new OrderManager(idFactory, portfolio, messageHistory) {
			@Override
			protected void sendToApplicationQueue(Message message) throws JMSException {
				queuedMessages.add(message);
			}
		};
	}

	/*
	 * Test method for 'org.marketcetera.photon.OrderManager.getMessageListener()'
	 */
	public void testGetMessageListener() {
		MessageListener messageListener = orderManager.getMessageListener();
		assertNotNull(messageListener);
	}
	
	/*
	 * Test method for 'org.marketcetera.photon.OrderManager.handleCounterpartyMessages(Object[])'
	 */
	public void testHandleCounterpartyMessages() throws FieldNotFound {
		Object[] messages = new Object[2];
		messages[0] = PositionEntryTest.getTestableExecutionReport();
		messages[1] = PositionEntryTest.getTestableExecutionReport();
		orderManager.handleCounterpartyMessages(messages);
		Object[] historyArray = messageHistory.getHistory();
		assertEquals(2, historyArray.length);
		assertEquals(IncomingMessageHolder.class, historyArray[0].getClass());
		assertEquals(IncomingMessageHolder.class, historyArray[1].getClass());
		assertEquals(MsgType.EXECUTION_REPORT, ((IncomingMessageHolder)historyArray[0]).getMessage().getHeader().getString(MsgType.FIELD));
		assertEquals(MsgType.EXECUTION_REPORT, ((IncomingMessageHolder)historyArray[1]).getMessage().getHeader().getString(MsgType.FIELD));
	}

	/*
	 * Test method for 'org.marketcetera.photon.OrderManager.handleInternalMessages(Object[])'
	 */
	public void testHandleInternalMessages() throws FieldNotFound {
		Object[] messages = new Object[2];
		messages[0] = FIXMessageUtil.newLimitOrder(new InternalID("ASDF"), Side.BUY, BigDecimal.ONE, "QWER", BigDecimal.TEN, TimeInForce.DAY, null);
		messages[1] = FIXMessageUtil.newCancel(new InternalID("AQWE"), new InternalID("ASDF"), Side.BUY, BigDecimal.TEN, "SDF", "WERT");
		orderManager.handleInternalMessages(messages);
		Object[] historyArray = messageHistory.getHistory();
		assertEquals(2, historyArray.length);
		assertEquals(OutgoingMessageHolder.class, historyArray[0].getClass());
		assertEquals(OutgoingMessageHolder.class, historyArray[1].getClass());
		assertEquals(MsgType.ORDER_SINGLE, ((OutgoingMessageHolder)historyArray[0]).getMessage().getHeader().getString(MsgType.FIELD));
		assertEquals(MsgType.ORDER_CANCEL_REQUEST, ((OutgoingMessageHolder)historyArray[1]).getMessage().getHeader().getString(MsgType.FIELD));
	}

	/*
	 * Test method for 'org.marketcetera.photon.OrderManager.handleCounterpartyMessage(Message)'
	 */
	public void testHandleCounterpartyMessage() throws FieldNotFound {
		Message message = PositionEntryTest.getTestableExecutionReport();
		orderManager.handleCounterpartyMessage(message);
		Object[] historyArray = messageHistory.getHistory();
		assertEquals(1, historyArray.length);
		assertEquals(IncomingMessageHolder.class, historyArray[0].getClass());
		assertEquals(MsgType.EXECUTION_REPORT, ((IncomingMessageHolder)historyArray[0]).getMessage().getHeader().getString(MsgType.FIELD));
	}

	/*
	 * Test method for 'org.marketcetera.photon.OrderManager.handleInternalMessage(Message)'
	 */
	public void testHandleInternalMessage() throws FieldNotFound, MarketceteraException, JMSException {
		Message message = FIXMessageUtil.newLimitOrder(new InternalID("ASDF"), Side.BUY, BigDecimal.ONE, "QWER", BigDecimal.TEN, TimeInForce.DAY, null);
		orderManager.handleInternalMessage(message);
		Object[] historyArray = messageHistory.getHistory();
		assertEquals(1, historyArray.length);
		assertEquals(OutgoingMessageHolder.class, historyArray[0].getClass());
		assertEquals(MsgType.ORDER_SINGLE, ((OutgoingMessageHolder)historyArray[0]).getMessage().getHeader().getString(MsgType.FIELD));

	}

	/*
	 * Test method for 'org.marketcetera.photon.OrderManager.cancelReplaceOneOrder(Message)'
	 */
	public void testCancelReplaceOneOrder() throws FieldNotFound, JMSException, MarketceteraException, IncorrectTagValue {
		String myClOrdID = "MyClOrdID";
		Message message = FIXMessageUtil.newLimitOrder(new InternalID(myClOrdID), Side.BUY, BigDecimal.ONE, "QWER", BigDecimal.TEN, TimeInForce.DAY, null);
		orderManager.handleInternalMessage(message);
		Object[] history = messageHistory.getHistory();
		assertEquals(1, history.length);

		Message cancelReplaceMessage = new quickfix.fix42.Message();
		cancelReplaceMessage.getHeader().setField(new MsgType(MsgType.ORDER_CANCEL_REPLACE_REQUEST));
		cancelReplaceMessage.setField(new OrigClOrdID(myClOrdID));
		cancelReplaceMessage.setField(new Symbol("QWER"));
		cancelReplaceMessage.setField(new StringField(OrderQty.FIELD, "100"));
		orderManager.handleInternalMessage(cancelReplaceMessage);
		
		history = messageHistory.getHistory();
		assertEquals(2, history.length);
		assertEquals(OutgoingMessageHolder.class, history[1].getClass());
		OutgoingMessageHolder holder = (OutgoingMessageHolder) history[1];
		Message filledCancelReplace = holder.getMessage();
		assertEquals(MsgType.ORDER_CANCEL_REPLACE_REQUEST, filledCancelReplace.getHeader().getString(MsgType.FIELD));
		FIXDataDictionaryManager.getDictionary().validate(filledCancelReplace);
		assertEquals("100", filledCancelReplace.getString(OrderQty.FIELD));
	}

	/*
	 * Test method for 'org.marketcetera.photon.OrderManager.cancelOneOrder(Message)'
	 */
	public void testCancelOneOrder() throws FieldNotFound, MarketceteraException, JMSException, IncorrectTagValue {
		String myClOrdID = "MyClOrdID";
		Message message = FIXMessageUtil.newMarketOrder(new InternalID(myClOrdID), Side.BUY, BigDecimal.ONE, "QWER", TimeInForce.DAY, null);
		orderManager.handleInternalMessage(message);
		Object[] history = messageHistory.getHistory();
		assertEquals(1, history.length);

		Message cancelMessage = new quickfix.fix42.Message();
		cancelMessage.getHeader().setField(new MsgType(MsgType.ORDER_CANCEL_REQUEST));
		cancelMessage.setField(new OrigClOrdID(myClOrdID));
		cancelMessage.setField(new Symbol("QWER"));
		orderManager.handleInternalMessage(cancelMessage);
		
		history = messageHistory.getHistory();
		assertEquals(2, history.length);
		assertEquals(OutgoingMessageHolder.class, history[1].getClass());
		OutgoingMessageHolder holder = (OutgoingMessageHolder) history[1];
		Message filledCancel = holder.getMessage();

		assertEquals(MsgType.ORDER_CANCEL_REQUEST, filledCancel.getHeader().getString(MsgType.FIELD));
		FIXDataDictionaryManager.getDictionary().validate(filledCancel);
	}

	/*
	 * Test method for 'org.marketcetera.photon.OrderManager.getIDFactory()'
	 */
	public void testGetIDFactory() throws NoMoreIDsException {
		assertEquals("999", orderManager.getIDFactory().getNext());
	}

	/*
	 * Test method for 'org.marketcetera.photon.OrderManager.getRootPortfolio()'
	 */
	public void testGetRootPortfolio() {
		assertEquals("Warren Buffett's Portfolio", orderManager.getRootPortfolio().getName());
	}

	/*
	 * Test method for 'org.marketcetera.photon.OrderManager.handleCommandIssued(CommandEvent)'
	 */
	public void testHandleCommandIssued() {
		String myClOrdID = "MyClOrdID";
		Message message = FIXMessageUtil.newMarketOrder(new InternalID(myClOrdID), Side.BUY, BigDecimal.ONE, "QWER", TimeInForce.DAY, null);
		CommandEvent evt = new CommandEvent(message, CommandEvent.Destination.EDITOR);
		orderManager.handleCommandIssued(evt);
		
		assertEquals(0, messageHistory.getHistory().length);

		evt = new CommandEvent(message, CommandEvent.Destination.BROKER);
		orderManager.handleCommandIssued(evt);
		assertEquals(1, messageHistory.getHistory().length);
		
	}

	/*
	 * Test method for 'org.marketcetera.photon.OrderManager.getCommandListener()'
	 */
	public void testGetCommandListener() {
		ICommandListener commandListener = orderManager.getCommandListener();
		assertNotNull(commandListener);
	}

	/*
	 * Test method for 'org.marketcetera.photon.OrderManager.cancelOneOrderByClOrdID(String)'
	 */
	public void testCancelOneOrderByClOrdID() throws FieldNotFound, MarketceteraException, JMSException, IncorrectTagValue {
		String myClOrdID = "MyClOrdID";
		Message message = FIXMessageUtil.newMarketOrder(new InternalID(myClOrdID), Side.BUY, BigDecimal.ONE, "QWER", TimeInForce.DAY, null);
		orderManager.handleInternalMessage(message);
		Object[] history = messageHistory.getHistory();
		assertEquals(1, history.length);

		Message cancelMessage = new quickfix.fix42.Message();
		cancelMessage.getHeader().setField(new MsgType(MsgType.ORDER_CANCEL_REQUEST));
		cancelMessage.setField(new OrigClOrdID(myClOrdID));
		cancelMessage.setField(new Symbol("QWER"));
		orderManager.cancelOneOrderByClOrdID(myClOrdID);
		
		history = messageHistory.getHistory();
		assertEquals(2, history.length);
		assertEquals(OutgoingMessageHolder.class, history[1].getClass());
		OutgoingMessageHolder holder = (OutgoingMessageHolder) history[1];
		Message filledCancel = holder.getMessage();

		assertEquals(MsgType.ORDER_CANCEL_REQUEST, filledCancel.getHeader().getString(MsgType.FIELD));
		FIXDataDictionaryManager.getDictionary().validate(filledCancel);
		assertEquals(myClOrdID, filledCancel.getString(OrigClOrdID.FIELD));
		assertEquals("999", filledCancel.getString(ClOrdID.FIELD));
		assertEquals("QWER", filledCancel.getString(Symbol.FIELD));
		assertEquals(Side.BUY, filledCancel.getChar(Side.FIELD));
		assertEquals(new Date().getTime(), filledCancel.getUtcTimeStamp(TransactTime.FIELD).getTime(), 500);
	}

}
