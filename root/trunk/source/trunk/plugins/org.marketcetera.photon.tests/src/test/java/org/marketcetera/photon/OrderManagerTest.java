package org.marketcetera.photon;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import junit.framework.TestCase;

import org.marketcetera.core.InMemoryIDFactory;
import org.marketcetera.core.MSymbol;
import org.marketcetera.core.MarketceteraException;
import org.marketcetera.photon.core.FIXMessageHistory;
import org.marketcetera.photon.core.IncomingMessageHolder;
import org.marketcetera.photon.core.MessageHolder;
import org.marketcetera.photon.core.OutgoingMessageHolder;
import org.marketcetera.quickfix.FIXDataDictionaryManager;
import org.marketcetera.quickfix.FIXMessageFactory;
import org.marketcetera.quickfix.FIXVersion;

import quickfix.FieldNotFound;
import quickfix.IncorrectTagValue;
import quickfix.Message;
import quickfix.field.ClOrdID;
import quickfix.field.ExecTransType;
import quickfix.field.ExecType;
import quickfix.field.MsgType;
import quickfix.field.OrdStatus;
import quickfix.field.OrderQty;
import quickfix.field.OrigClOrdID;
import quickfix.field.SenderCompID;
import quickfix.field.Side;
import quickfix.field.Symbol;
import quickfix.field.TargetCompID;
import quickfix.field.TimeInForce;
import quickfix.field.TransactTime;
import ca.odell.glazedlists.EventList;

public class OrderManagerTest extends TestCase {

    public static Date THE_TRANSACT_TIME;
    public static MSymbol SYMBOL = new MSymbol("SYMB");
    public static String CL_ORD_ID = "CLORDID";
   
    private static FIXMessageFactory msgFactory = FIXVersion.FIX42.getMessageFactory();

    public static Message getTestableExecutionReport() {
            Message aMessage = msgFactory.newExecutionReport("1", CL_ORD_ID, "987", ExecTransType.STATUS,
                            ExecType.PARTIAL_FILL, OrdStatus.PARTIALLY_FILLED, Side.BUY, new BigDecimal(1000), new BigDecimal("12.3"), new BigDecimal(500),
                            new BigDecimal("12.3"), new BigDecimal(500), new BigDecimal(500), new BigDecimal("12.3"), SYMBOL, null);
            aMessage.setUtcTimeStamp(TransactTime.FIELD, THE_TRANSACT_TIME);
            aMessage.getHeader().setField(new SenderCompID("send-dude"));
            aMessage.getHeader().setField(new TargetCompID("target-dude"));
            return aMessage;
    }
    
    static {
        try {
            THE_TRANSACT_TIME = new SimpleDateFormat("yyyy-MM-dd").parse("2006-10-04");
        } catch (ParseException e) {
        }
    }
	
	
	private InMemoryIDFactory idFactory;
	private FIXMessageHistory messageHistory;
	private ImmediatePhotonController photonController;

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
		photonController = new ImmediatePhotonController();
		photonController.setMessageHistory(messageHistory);
		photonController.setIDFactory(idFactory);
	}

	
	/*
	 * Test method for 'org.marketcetera.photon.OrderManager.handleCounterpartyMessages(Object[])'
	 */
	public void testHandleCounterpartyMessages() throws FieldNotFound {
		Message[] messages = new Message[2];
		messages[0] = getTestableExecutionReport();
		messages[1] = getTestableExecutionReport();
		for (Message aMessage : messages) {
			photonController.handleCounterpartyMessage(aMessage);
		}
		EventList<MessageHolder> historyList = messageHistory.getAllMessagesList();
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
		EventList<MessageHolder> historyList = messageHistory.getAllMessagesList();
		assertEquals(0, historyList.size());
		Message[] messages = new Message[2];
		messages[0] = msgFactory.newLimitOrder("1", Side.BUY, BigDecimal.ONE, new MSymbol("QWER"), BigDecimal.TEN, TimeInForce.DAY, null);
		messages[1] = msgFactory.newCancel("1", "1", Side.BUY, BigDecimal.TEN, new MSymbol("SDF"), "WERT");
		for (Message message : messages) {
			photonController.handleInternalMessage(message);
		}
		assertNotNull(messageHistory.getLatestMessage("ASDF"));
		historyList = messageHistory.getAllMessagesList();
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
		photonController.handleCounterpartyMessage(message);
		EventList<MessageHolder> historyList = messageHistory.getAllMessagesList();
		assertEquals(1, historyList.size());
		assertEquals(IncomingMessageHolder.class, historyList.get(0).getClass());
		assertEquals(MsgType.EXECUTION_REPORT, ((IncomingMessageHolder)historyList.get(0)).getMessage().getHeader().getString(MsgType.FIELD));
	}

	/*
	 * Test method for 'org.marketcetera.photon.OrderManager.handleInternalMessage(Message)'
	 */
	public void testHandleInternalMessage() throws FieldNotFound, MarketceteraException {
		Message message = msgFactory.newLimitOrder("1", Side.BUY, BigDecimal.ONE, new MSymbol("QWER"), BigDecimal.TEN, TimeInForce.DAY, null);
		photonController.handleInternalMessage(message);
		EventList<MessageHolder> historyList = messageHistory.getAllMessagesList();
		assertEquals(1, historyList.size());
		assertEquals(OutgoingMessageHolder.class, historyList.get(0).getClass());
		assertEquals(MsgType.ORDER_SINGLE, ((OutgoingMessageHolder)historyList.get(0)).getMessage().getHeader().getString(MsgType.FIELD));

	}

	/*
	 * Test method for 'org.marketcetera.photon.OrderManager.cancelReplaceOneOrder(Message)'
	 */
	public void testCancelReplaceOneOrder() throws FieldNotFound, MarketceteraException, IncorrectTagValue {
		String myClOrdID = "MyClOrdID";
		Message message = msgFactory.newLimitOrder(myClOrdID, Side.BUY, BigDecimal.ONE, new MSymbol("QWER"), BigDecimal.TEN, TimeInForce.DAY, null);
		photonController.handleInternalMessage(message);
		EventList<MessageHolder> history = messageHistory.getAllMessagesList();
		assertEquals(1, history.size());

		Message cancelReplaceMessage = msgFactory.newCancelReplaceFromMessage(message);
		cancelReplaceMessage.setField(new OrigClOrdID(myClOrdID));

		photonController.handleInternalMessage(cancelReplaceMessage);
		
		history = messageHistory.getAllMessagesList();
		assertEquals(2, history.size());
		assertEquals(OutgoingMessageHolder.class, history.get(1).getClass());
		OutgoingMessageHolder holder = (OutgoingMessageHolder) history.get(1);
		Message filledCancelReplace = holder.getMessage();
		assertEquals(MsgType.ORDER_CANCEL_REPLACE_REQUEST, filledCancelReplace.getHeader().getString(MsgType.FIELD));
		FIXDataDictionaryManager.getDictionary().validate(filledCancelReplace);
		assertEquals("1", filledCancelReplace.getString(OrderQty.FIELD));
	}

	/*
	 * Test method for 'org.marketcetera.photon.OrderManager.cancelOneOrder(Message)'
	 */
	public void testCancelOneOrder() throws FieldNotFound, MarketceteraException, IncorrectTagValue {
		String myClOrdID = "MyClOrdID";
		Message message = msgFactory.newMarketOrder(myClOrdID, Side.BUY, BigDecimal.ONE, new MSymbol("QWER"), TimeInForce.DAY, null);
		photonController.handleInternalMessage(message);
		EventList<MessageHolder> history = messageHistory.getAllMessagesList();
		assertEquals(1, history.size());

		Message cancelMessage = new quickfix.fix42.Message();
		cancelMessage.getHeader().setField(new MsgType(MsgType.ORDER_CANCEL_REQUEST));
		cancelMessage.setField(new OrigClOrdID(myClOrdID));
		cancelMessage.setField(new Symbol("QWER"));
		photonController.handleInternalMessage(cancelMessage);
		
		history = messageHistory.getAllMessagesList();
		assertEquals(2, history.size());
		assertEquals(OutgoingMessageHolder.class, history.get(1).getClass());
		OutgoingMessageHolder holder = (OutgoingMessageHolder) history.get(1);
		Message filledCancel = holder.getMessage();

		assertEquals(MsgType.ORDER_CANCEL_REQUEST, filledCancel.getHeader().getString(MsgType.FIELD));
		FIXDataDictionaryManager.getDictionary().validate(filledCancel);
	}



	/*
	 * Test method for 'org.marketcetera.photon.OrderManager.cancelOneOrderByClOrdID(String)'
	 */
	public void testCancelOneOrderByClOrdID() throws FieldNotFound, MarketceteraException, IncorrectTagValue {
		String myClOrdID = "MyClOrdID";
		Message message = msgFactory.newMarketOrder(myClOrdID, Side.BUY, BigDecimal.ONE, new MSymbol("QWER"), TimeInForce.DAY, null);
		photonController.handleInternalMessage(message);
		EventList<MessageHolder> history = messageHistory.getAllMessagesList();
		assertEquals(1, history.size());

		Message cancelMessage = new quickfix.fix42.Message();
		cancelMessage.getHeader().setField(new MsgType(MsgType.ORDER_CANCEL_REQUEST));
		cancelMessage.setField(new OrigClOrdID(myClOrdID));
		cancelMessage.setField(new Symbol("QWER"));
		photonController.cancelOneOrderByClOrdID(myClOrdID);
		
		history = messageHistory.getAllMessagesList();
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
