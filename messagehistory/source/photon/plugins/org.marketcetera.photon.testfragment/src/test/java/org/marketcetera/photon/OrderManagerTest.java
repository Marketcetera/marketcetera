package org.marketcetera.photon;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import junit.framework.TestCase;

import org.marketcetera.core.CoreException;
import org.marketcetera.core.InMemoryIDFactory;
import org.marketcetera.core.MSymbol;
import org.marketcetera.messagehistory.FIXMessageHistory;
import org.marketcetera.messagehistory.IncomingMessageHolder;
import org.marketcetera.messagehistory.MessageHolder;
import org.marketcetera.quickfix.FIXDataDictionary;
import org.marketcetera.quickfix.FIXDataDictionaryManager;
import org.marketcetera.quickfix.FIXMessageFactory;
import org.marketcetera.quickfix.FIXVersion;

import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.field.MsgType;
import quickfix.field.OrdStatus;
import quickfix.field.OrigClOrdID;
import quickfix.field.SenderCompID;
import quickfix.field.Side;
import quickfix.field.Symbol;
import quickfix.field.TargetCompID;
import quickfix.field.TimeInForce;
import quickfix.field.TransactTime;
import ca.odell.glazedlists.EventList;

/*
 * $License$
 */

/**
 * Tests the order ticket screens.
 * @version $Id$
 */
public class OrderManagerTest extends TestCase {

    public static Date THE_TRANSACT_TIME;
    public static MSymbol SYMBOL = new MSymbol("SYMB");
    public static String CL_ORD_ID = "CLORDID";
   
    private static FIXMessageFactory msgFactory = FIXVersion.FIX42.getMessageFactory();
    private static FIXDataDictionary dataDictionary = FIXDataDictionaryManager.getFIXDataDictionary(FIXVersion.FIX42);

    public static Message getTestableExecutionReport() throws FieldNotFound {
            Message aMessage = msgFactory.newExecutionReport("456", CL_ORD_ID, "987", OrdStatus.PARTIALLY_FILLED, Side.BUY, new BigDecimal(1000), new BigDecimal("12.3"), new BigDecimal(500),
                            new BigDecimal("12.3"), new BigDecimal(500), new BigDecimal("12.3"), SYMBOL, null);
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

	protected void setUp() throws Exception {
		super.setUp();
		idFactory = new InMemoryIDFactory(999);
		messageHistory = new FIXMessageHistory(FIXVersion.FIX42.getMessageFactory());
		photonController = new ImmediatePhotonController();
		photonController.setMessageHistory(messageHistory);
		photonController.setIDFactory(idFactory);
		photonController.setMessageFactory(msgFactory);
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
		Message order = msgFactory.newLimitOrder("ASDF", Side.BUY, BigDecimal.ONE, new MSymbol("QWER"), BigDecimal.TEN, TimeInForce.DAY, null);
		Message cancel = msgFactory.newCancel("AQWE", "ASDF", Side.BUY, BigDecimal.TEN, new MSymbol("SDF"), "WERT");
		Message exReport = msgFactory.newExecutionReport("456", "ASDF", "987", OrdStatus.PENDING_NEW, Side.BUY, BigDecimal.ONE, BigDecimal.TEN, new BigDecimal(500),
				BigDecimal.TEN, BigDecimal.TEN, BigDecimal.TEN, new MSymbol("QWER"), null);
		photonController.handleInternalMessage(order);
		assertSame(order, photonController.getLastMessage());
		messageHistory.addIncomingMessage(exReport);
		photonController.handleInternalMessage(cancel);
		assertEquals(MsgType.ORDER_CANCEL_REQUEST, photonController.getLastMessage().getHeader().getString(MsgType.FIELD));
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
	public void testHandleInternalMessage() throws FieldNotFound, CoreException {
		Message message = msgFactory.newLimitOrder("ASDF", Side.BUY, BigDecimal.ONE, new MSymbol("QWER"), BigDecimal.TEN, TimeInForce.DAY, null);
		photonController.handleInternalMessage(message);
		assertSame(message, photonController.getLastMessage());

	}

	/*
	 * Test method for 'org.marketcetera.photon.OrderManager.cancelReplaceOneOrder(Message)'
	 */
	public void testCancelReplaceOneOrder() throws Exception {
		String myClOrdID = "MyClOrdID";
		Message message = msgFactory.newLimitOrder(myClOrdID, Side.BUY, BigDecimal.ONE, new MSymbol("QWER"), BigDecimal.TEN, TimeInForce.DAY, null);
		Message exReport = msgFactory.newExecutionReport("456", "ASDF", "987", OrdStatus.PENDING_NEW, Side.BUY, BigDecimal.ONE, BigDecimal.TEN, new BigDecimal(500),
				BigDecimal.TEN, BigDecimal.TEN, BigDecimal.TEN, new MSymbol("QWER"), null);
		photonController.handleInternalMessage(message);
		assertSame(message, photonController.getLastMessage());

		messageHistory.addIncomingMessage(exReport);
		Message cancelReplaceMessage = msgFactory.newCancelReplaceFromMessage(message);
		cancelReplaceMessage.setField(new OrigClOrdID(myClOrdID));

		photonController.handleInternalMessage(cancelReplaceMessage);
		assertSame(cancelReplaceMessage, photonController.getLastMessage());
	}

	/*
	 * Test method for 'org.marketcetera.photon.OrderManager.cancelOneOrder(Message)'
	 */
	public void testCancelOneOrder() throws Exception {
		String myClOrdID = "MyClOrdID";
		Message message = msgFactory.newMarketOrder(myClOrdID, Side.BUY, BigDecimal.ONE, new MSymbol("QWER"), TimeInForce.DAY, null);
		Message exReport = msgFactory.newExecutionReport("456", myClOrdID, "987", OrdStatus.PENDING_NEW, Side.BUY, BigDecimal.ONE, BigDecimal.TEN, new BigDecimal(500),
				BigDecimal.TEN, BigDecimal.TEN, BigDecimal.TEN, new MSymbol("QWER"), null);
		photonController.handleInternalMessage(message);
		assertSame(message, photonController.getLastMessage());

		messageHistory.addIncomingMessage(exReport);
		Message cancelMessage = new quickfix.fix42.Message();
		cancelMessage.getHeader().setField(new MsgType(MsgType.ORDER_CANCEL_REQUEST));
		cancelMessage.setField(new OrigClOrdID(myClOrdID));
		cancelMessage.setField(new Symbol("QWER"));
		photonController.handleInternalMessage(cancelMessage);
		assertEquals(MsgType.ORDER_CANCEL_REQUEST, photonController.getLastMessage().getHeader().getString(MsgType.FIELD));
	}



	/*
	 * Test method for 'org.marketcetera.photon.OrderManager.cancelOneOrderByClOrdID(String)'
	 */
	public void testCancelOneOrderByClOrdID() throws Exception {
		String myClOrdID = "MyClOrdID";
		Message message = msgFactory.newMarketOrder(myClOrdID, Side.BUY, BigDecimal.ONE, new MSymbol("QWER"), TimeInForce.DAY, null);
		Message exReport = msgFactory.newExecutionReport("456", myClOrdID, "987", OrdStatus.PENDING_NEW, Side.BUY, BigDecimal.ONE, BigDecimal.TEN, new BigDecimal(500),
				BigDecimal.TEN, BigDecimal.TEN, BigDecimal.TEN, new MSymbol("QWER"), null);
		photonController.handleInternalMessage(message);
		assertSame(message, photonController.getLastMessage());

		messageHistory.addIncomingMessage(exReport);
		photonController.cancelOneOrderByClOrdID(myClOrdID);
		assertEquals(MsgType.ORDER_CANCEL_REQUEST, photonController.getLastMessage().getHeader().getString(MsgType.FIELD));
	}

}
