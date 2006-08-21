package org.marketcetera.photon;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;

import javax.jms.JMSException;
import javax.jms.MessageListener;

import junit.framework.TestCase;

import org.marketcetera.core.AccountID;
import org.marketcetera.core.InMemoryIDFactory;
import org.marketcetera.core.InternalID;
import org.marketcetera.core.MSymbol;
import org.marketcetera.core.MarketceteraException;
import org.marketcetera.core.NoMoreIDsException;
import org.marketcetera.photon.actions.CommandEvent;
import org.marketcetera.photon.actions.ICommandListener;
import org.marketcetera.photon.model.FIXMessageHistory;
import org.marketcetera.photon.model.IncomingMessageHolder;
import org.marketcetera.photon.model.MessageHolder;
import org.marketcetera.photon.model.OutgoingMessageHolder;
import org.marketcetera.quickfix.FIXDataDictionaryManager;
import org.marketcetera.quickfix.FIXMessageUtil;

import quickfix.FieldNotFound;
import quickfix.IncorrectTagValue;
import quickfix.Message;
import quickfix.StringField;
import quickfix.field.ClOrdID;
import quickfix.field.ExecTransType;
import quickfix.field.ExecType;
import quickfix.field.MsgType;
import quickfix.field.OrdStatus;
import quickfix.field.OrderQty;
import quickfix.field.OrigClOrdID;
import quickfix.field.Side;
import quickfix.field.Symbol;
import quickfix.field.TimeInForce;
import quickfix.field.TransactTime;
import ca.odell.glazedlists.EventList;

public class OrderManagerTest extends TestCase {

    private static final String ROOT_PORTFOLIO_NAME = "Root portfolio";
    public static Date THE_TRANSACT_TIME;
    private static Date THE_DATE;
    private static String POSITION_NAME = "Testable position entry";
    private static InternalID INTERNAL_ID = new InternalID("123");
    private static char SIDE_BUY = Side.BUY;
    public static MSymbol SYMBOL = new MSymbol("SYMB");
    private static AccountID ACCOUNT_ID = new AccountID("asdf");
    public static InternalID CL_ORD_ID = new InternalID("CLORDID");
   
    
    	 	
    public static Message getTestableExecutionReport() {
            Message aMessage = FIXMessageUtil.newExecutionReport(new InternalID("456"), CL_ORD_ID, "987", ExecTransType.STATUS,
                            ExecType.PARTIAL_FILL, OrdStatus.PARTIALLY_FILLED, Side.BUY, new BigDecimal(1000), new BigDecimal("12.3"), new BigDecimal(500),
                            new BigDecimal("12.3"), new BigDecimal(500), new BigDecimal(500), new BigDecimal("12.3"), SYMBOL, null);
            aMessage.setUtcTimeStamp(TransactTime.FIELD, THE_TRANSACT_TIME);
            return aMessage;
    }
    
    static {
            try {
                    THE_DATE = new SimpleDateFormat("yyyy-MM-dd").parse("1974-12-24");
                    THE_TRANSACT_TIME = new SimpleDateFormat("yyyy-MM-dd").parse("2006-10-04");
            } catch (ParseException e) {
            }
    }
	
	
	private InMemoryIDFactory idFactory;
	private FIXMessageHistory messageHistory;
	private OrderManager orderManager;
	private LinkedList<Message> queuedMessages;

	static {
		try {
			FIXDataDictionaryManager.setFIXVersion(FIXDataDictionaryManager.FIX_4_2_BEGIN_STRING);
		} catch (Throwable th) {
			th.printStackTrace();
		}
	}
	
	protected void setUp() throws Exception {
		idFactory = new InMemoryIDFactory(999);
		messageHistory = new FIXMessageHistory();
		queuedMessages = new LinkedList<Message>();
		orderManager = new OrderManager(idFactory, messageHistory) {
			@Override
			protected boolean sendToApplicationQueue(Message message) throws JMSException {
				queuedMessages.add(message);
				return true;
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
		messages[0] = getTestableExecutionReport();
		messages[1] = getTestableExecutionReport();
		orderManager.handleCounterpartyMessages(messages);
		EventList<MessageHolder> historyList = messageHistory.getAllMessages();
		assertEquals(2, historyList.size());
		assertEquals(IncomingMessageHolder.class, historyList.get(0).getClass());
		assertEquals(IncomingMessageHolder.class, historyList.get(1).getClass());
		assertEquals(MsgType.EXECUTION_REPORT, ((IncomingMessageHolder)historyList.get(0)).getMessage().getHeader().getString(MsgType.FIELD));
		assertEquals(MsgType.EXECUTION_REPORT, ((IncomingMessageHolder)historyList.get(1)).getMessage().getHeader().getString(MsgType.FIELD));
	}

	/*
	 * Test method for 'org.marketcetera.photon.OrderManager.handleInternalMessages(Object[])'
	 */
	public void testHandleInternalMessages() throws FieldNotFound {
		EventList<MessageHolder> historyList = messageHistory.getAllMessages();
		assertEquals(0, historyList.size());
		Object[] messages = new Object[2];
		messages[0] = FIXMessageUtil.newLimitOrder(new InternalID("ASDF"), Side.BUY, BigDecimal.ONE, new MSymbol("QWER"), BigDecimal.TEN, TimeInForce.DAY, null);
		messages[1] = FIXMessageUtil.newCancel(new InternalID("AQWE"), new InternalID("ASDF"), Side.BUY, BigDecimal.TEN, new MSymbol("SDF"), "WERT");
		orderManager.handleInternalMessages(messages);
		assertNotNull(messageHistory.getLatestMessage("ASDF"));
		historyList = messageHistory.getAllMessages();
		assertEquals(2, historyList.size());
		assertEquals(OutgoingMessageHolder.class, historyList.get(0).getClass());
		assertEquals(OutgoingMessageHolder.class, historyList.get(0).getClass());
		assertEquals(MsgType.ORDER_SINGLE, ((OutgoingMessageHolder)historyList.get(0)).getMessage().getHeader().getString(MsgType.FIELD));
		assertEquals(MsgType.ORDER_CANCEL_REQUEST, ((OutgoingMessageHolder)historyList.get(1)).getMessage().getHeader().getString(MsgType.FIELD));
	}

	/*
	 * Test method for 'org.marketcetera.photon.OrderManager.handleCounterpartyMessage(Message)'
	 */
	public void testHandleCounterpartyMessage() throws FieldNotFound {
		Message message = getTestableExecutionReport();
		orderManager.handleCounterpartyMessage(message);
		EventList<MessageHolder> historyList = messageHistory.getAllMessages();
		assertEquals(1, historyList.size());
		assertEquals(IncomingMessageHolder.class, historyList.get(0).getClass());
		assertEquals(MsgType.EXECUTION_REPORT, ((IncomingMessageHolder)historyList.get(0)).getMessage().getHeader().getString(MsgType.FIELD));
	}

	/*
	 * Test method for 'org.marketcetera.photon.OrderManager.handleInternalMessage(Message)'
	 */
	public void testHandleInternalMessage() throws FieldNotFound, MarketceteraException, JMSException {
		Message message = FIXMessageUtil.newLimitOrder(new InternalID("ASDF"), Side.BUY, BigDecimal.ONE, new MSymbol("QWER"), BigDecimal.TEN, TimeInForce.DAY, null);
		orderManager.handleInternalMessage(message);
		EventList<MessageHolder> historyList = messageHistory.getAllMessages();
		assertEquals(1, historyList.size());
		assertEquals(OutgoingMessageHolder.class, historyList.get(0).getClass());
		assertEquals(MsgType.ORDER_SINGLE, ((OutgoingMessageHolder)historyList.get(0)).getMessage().getHeader().getString(MsgType.FIELD));

	}

	/*
	 * Test method for 'org.marketcetera.photon.OrderManager.cancelReplaceOneOrder(Message)'
	 */
	public void testCancelReplaceOneOrder() throws FieldNotFound, JMSException, MarketceteraException, IncorrectTagValue {
		String myClOrdID = "MyClOrdID";
		Message message = FIXMessageUtil.newLimitOrder(new InternalID(myClOrdID), Side.BUY, BigDecimal.ONE, new MSymbol("QWER"), BigDecimal.TEN, TimeInForce.DAY, null);
		orderManager.handleInternalMessage(message);
		EventList<MessageHolder> history = messageHistory.getAllMessages();
		assertEquals(1, history.size());

		Message cancelReplaceMessage = new quickfix.fix42.Message();
		cancelReplaceMessage.getHeader().setField(new MsgType(MsgType.ORDER_CANCEL_REPLACE_REQUEST));
		cancelReplaceMessage.setField(new OrigClOrdID(myClOrdID));
		cancelReplaceMessage.setField(new Symbol("QWER"));
		cancelReplaceMessage.setField(new StringField(OrderQty.FIELD, "100"));
		orderManager.handleInternalMessage(cancelReplaceMessage);
		
		history = messageHistory.getAllMessages();
		assertEquals(2, history.size());
		assertEquals(OutgoingMessageHolder.class, history.get(1).getClass());
		OutgoingMessageHolder holder = (OutgoingMessageHolder) history.get(1);
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
		Message message = FIXMessageUtil.newMarketOrder(new InternalID(myClOrdID), Side.BUY, BigDecimal.ONE, new MSymbol("QWER"), TimeInForce.DAY, null);
		orderManager.handleInternalMessage(message);
		EventList<MessageHolder> history = messageHistory.getAllMessages();
		assertEquals(1, history.size());

		Message cancelMessage = new quickfix.fix42.Message();
		cancelMessage.getHeader().setField(new MsgType(MsgType.ORDER_CANCEL_REQUEST));
		cancelMessage.setField(new OrigClOrdID(myClOrdID));
		cancelMessage.setField(new Symbol("QWER"));
		orderManager.handleInternalMessage(cancelMessage);
		
		history = messageHistory.getAllMessages();
		assertEquals(2, history.size());
		assertEquals(OutgoingMessageHolder.class, history.get(1).getClass());
		OutgoingMessageHolder holder = (OutgoingMessageHolder) history.get(1);
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
	 * Test method for 'org.marketcetera.photon.OrderManager.handleCommandIssued(CommandEvent)'
	 */
	public void testHandleCommandIssued() {
		String myClOrdID = "MyClOrdID";
		Message message = FIXMessageUtil.newMarketOrder(new InternalID(myClOrdID), Side.BUY, BigDecimal.ONE, new MSymbol("QWER"), TimeInForce.DAY, null);
		CommandEvent evt = new CommandEvent(message, CommandEvent.Destination.EDITOR);
		orderManager.handleCommandIssued(evt);
		
		assertEquals(0, messageHistory.getAllMessages().size());

		evt = new CommandEvent(message, CommandEvent.Destination.BROKER);
		orderManager.handleCommandIssued(evt);
		assertEquals(1, messageHistory.getAllMessages().size());
		
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
		Message message = FIXMessageUtil.newMarketOrder(new InternalID(myClOrdID), Side.BUY, BigDecimal.ONE, new MSymbol("QWER"), TimeInForce.DAY, null);
		orderManager.handleInternalMessage(message);
		EventList<MessageHolder> history = messageHistory.getAllMessages();
		assertEquals(1, history.size());

		Message cancelMessage = new quickfix.fix42.Message();
		cancelMessage.getHeader().setField(new MsgType(MsgType.ORDER_CANCEL_REQUEST));
		cancelMessage.setField(new OrigClOrdID(myClOrdID));
		cancelMessage.setField(new Symbol("QWER"));
		orderManager.cancelOneOrderByClOrdID(myClOrdID);
		
		history = messageHistory.getAllMessages();
		assertEquals(2, history.size());
		assertEquals(OutgoingMessageHolder.class, history.get(1).getClass());
		OutgoingMessageHolder holder = (OutgoingMessageHolder) history.get(1);
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
