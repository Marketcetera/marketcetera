package org.marketcetera.photon;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import junit.framework.TestCase;

import org.marketcetera.core.CoreException;
import org.marketcetera.messagehistory.ReportHolder;
import org.marketcetera.messagehistory.TradeReportsHistory;
import org.marketcetera.messagehistory.TradeReportsHistoryTest;
import org.marketcetera.quickfix.FIXMessageFactory;
import org.marketcetera.quickfix.FIXVersion;
import org.marketcetera.trade.BrokerID;
import org.marketcetera.trade.ExecutionReport;
import org.marketcetera.trade.Factory;
import org.marketcetera.trade.MSymbol;
import org.marketcetera.trade.MessageCreationException;
import org.marketcetera.trade.OrderCancel;
import org.marketcetera.trade.OrderID;
import org.marketcetera.trade.OrderReplace;
import org.marketcetera.trade.OrderSingle;
import org.marketcetera.trade.OrderType;
import org.marketcetera.trade.Originator;
import org.marketcetera.trade.TypesTestBase;

import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.field.MsgType;
import quickfix.field.OrdStatus;
import quickfix.field.OrdType;
import quickfix.field.SenderCompID;
import quickfix.field.Side;
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
   
    private static FIXMessageFactory msgFactory = FIXVersion.FIX_SYSTEM.getMessageFactory();
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
	
	
	private TradeReportsHistory messageHistory;
	private ImmediatePhotonController photonController;

	protected void setUp() throws Exception {
		super.setUp();
		messageHistory = new TradeReportsHistory(msgFactory);
		photonController = new ImmediatePhotonController();
		photonController.setMessageHistory(messageHistory);
	}

	
	/*
	 * Test method for 'org.marketcetera.photon.PhotonController.receiveExecutionReport(ExecutionReport)'
	 */
	public void testReceiveExecutionReports() throws Exception {
		Message[] messages = new Message[2];
		messages[0] = getTestableExecutionReport();
		messages[1] = getTestableExecutionReport();
		for (Message aMessage : messages) {
			photonController.receiveExecutionReport(createReport(aMessage));
		}
		EventList<ReportHolder> historyList = messageHistory.getAllMessagesList();
		assertEquals(2, historyList.size());
		assertEquals(ReportHolder.class, historyList.get(0).getClass());
		assertEquals(ReportHolder.class, historyList.get(1).getClass());
		assertEquals(MsgType.EXECUTION_REPORT, ((ReportHolder)historyList.get(0)).getMessage().getHeader().getString(MsgType.FIELD));
		assertEquals(MsgType.EXECUTION_REPORT, ((ReportHolder)historyList.get(1)).getMessage().getHeader().getString(MsgType.FIELD));
	}

	/*
	 * Test method for 'org.marketcetera.photon.PhotonController.handleInternalMessage(Message)'
	 */
	public void testHandleInternalMessages() throws Exception {
		EventList<ReportHolder> historyList = messageHistory.getAllMessagesList();
		assertEquals(0, historyList.size());
		Message order = msgFactory.newLimitOrder("ASDF", Side.BUY, BigDecimal.ONE, new MSymbol("QWER"), BigDecimal.TEN, TimeInForce.DAY, null);
		Message cancel = msgFactory.newCancel("AQWE", "ASDF", Side.BUY, BigDecimal.TEN, new MSymbol("SDF"), "WERT");
		Message exReport = msgFactory.newExecutionReport("456", "ASDF", "987", OrdStatus.PENDING_NEW, Side.BUY, BigDecimal.ONE, BigDecimal.TEN, new BigDecimal(500),
				BigDecimal.TEN, BigDecimal.TEN, BigDecimal.TEN, new MSymbol("QWER"), null);
		photonController.handleInternalMessage(order);
		TypesTestBase.assertOrderSingle((OrderSingle) photonController.getLastOrder(), 
				TypesTestBase.NOT_NULL, 
				org.marketcetera.trade.Side.Buy, BigDecimal.ONE, BigDecimal.TEN, 
				org.marketcetera.trade.TimeInForce.Day, OrderType.Limit, 
				new MSymbol("QWER"), null, null, null, null, 
				PhotonController.DEFAULT_BROKER, null);
		messageHistory.addIncomingMessage(createReport(exReport));
		photonController.handleInternalMessage(cancel);
		TypesTestBase.assertOrderCancel((OrderCancel)photonController.getLastOrder(), 
				TypesTestBase.NOT_NULL, new OrderID("ASDF"), 
				org.marketcetera.trade.Side.Buy, new MSymbol("SDF"), null, 
				BigDecimal.TEN, "WERT", null, PhotonController.DEFAULT_BROKER, 
				null);
	}

	/*
	 * Test method for 'org.marketcetera.photon.PhotonController.receiveExecutionReport(Message)'
	 */
	public void testReceiveExecutionReport() throws Exception {
		Message message = getTestableExecutionReport();
		photonController.receiveExecutionReport(Factory.getInstance().
				createExecutionReport(message,new BrokerID("bro"), 
						Originator.Server, null, null)); 
		EventList<ReportHolder> historyList = messageHistory.getAllMessagesList();
		assertEquals(1, historyList.size());
		assertEquals(ReportHolder.class, historyList.get(0).getClass());
		assertEquals(MsgType.EXECUTION_REPORT, ((ReportHolder)historyList.get(0)).getMessage().getHeader().getString(MsgType.FIELD));
	}

	/*
	 * Test method for 'org.marketcetera.photon.OrderManager.handleInternalMessage(Message)'
	 */
	public void testHandleInternalMessage() throws FieldNotFound, CoreException {
		Message message = msgFactory.newLimitOrder("ASDF", Side.BUY, BigDecimal.ONE, new MSymbol("QWER"), BigDecimal.TEN, TimeInForce.DAY, null);
		photonController.handleInternalMessage(message);
		TypesTestBase.assertOrderSingle((OrderSingle)photonController.getLastOrder(), 
				TypesTestBase.NOT_NULL, org.marketcetera.trade.Side.Buy, 
				BigDecimal.ONE, BigDecimal.TEN, org.marketcetera.trade.TimeInForce.Day, 
				OrderType.Limit, new MSymbol("QWER"), null, null, null, null, 
				PhotonController.DEFAULT_BROKER, null);

	}

	/*
	 * Test method for 'org.marketcetera.photon.OrderManager.cancelReplaceOneOrder(Message)'
	 */
	public void testCancelReplaceOneOrder() throws Exception {
		String myClOrdID = "MyClOrdID";
		Message message = msgFactory.newLimitOrder(myClOrdID, Side.BUY, BigDecimal.ONE, new MSymbol("QWER"), BigDecimal.TEN, TimeInForce.DAY, null);
		Message exReport = msgFactory.newExecutionReport("456", myClOrdID, "987", OrdStatus.PENDING_NEW, Side.BUY, BigDecimal.ONE, BigDecimal.TEN, new BigDecimal(500),
				BigDecimal.TEN, BigDecimal.TEN, BigDecimal.TEN, new MSymbol("QWER"), null);
		exReport.setField(new OrdType(OrdType.LIMIT));
		exReport.setField(new TimeInForce(TimeInForce.DAY));
		photonController.handleInternalMessage(message);
		TypesTestBase.assertOrderSingle((OrderSingle)photonController.getLastOrder(), 
				TypesTestBase.NOT_NULL, org.marketcetera.trade.Side.Buy, BigDecimal.ONE, 
				BigDecimal.TEN, org.marketcetera.trade.TimeInForce.Day, OrderType.Limit, 
				new MSymbol("QWER"), null, null, null, null, 
				PhotonController.DEFAULT_BROKER, null);

		messageHistory.addIncomingMessage(createReport(exReport));
		Message cancelReplaceMessage = msgFactory.newCancelReplaceFromMessage(exReport);

		photonController.handleInternalMessage(cancelReplaceMessage);
		
		TypesTestBase.assertOrderReplace((OrderReplace)photonController.getLastOrder(), 
				TypesTestBase.NOT_NULL, new OrderID(myClOrdID), "456", 
				OrderType.Limit, org.marketcetera.trade.Side.Buy, 
				BigDecimal.ONE, BigDecimal.TEN, new MSymbol("QWER"), 
				null, org.marketcetera.trade.TimeInForce.Day, null, PhotonController.DEFAULT_BROKER, 
				null, null, null);
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
		TypesTestBase.assertOrderSingle((OrderSingle)photonController.getLastOrder(), 
				TypesTestBase.NOT_NULL, org.marketcetera.trade.Side.Buy, 
				BigDecimal.ONE, null, org.marketcetera.trade.TimeInForce.Day, OrderType.Market, 
				new MSymbol("QWER"), null, null, null, null, 
				PhotonController.DEFAULT_BROKER, null);

		messageHistory.addIncomingMessage(createReport(exReport));
		Message cancelMessage = msgFactory.newCancel("clOrd1", myClOrdID, Side.BUY, 
				BigDecimal.ONE, new MSymbol("QWER"), "ord1");
		photonController.handleInternalMessage(cancelMessage);
		TypesTestBase.assertOrderCancel((OrderCancel)photonController.getLastOrder(),
				TypesTestBase.NOT_NULL, new OrderID(myClOrdID), 
				org.marketcetera.trade.Side.Buy, new MSymbol("QWER"), 
				null, BigDecimal.ONE, "ord1", null, 
				PhotonController.DEFAULT_BROKER, null);
	}



	/*
	 * Test method for 'org.marketcetera.photon.OrderManager.cancelOneOrderByClOrdID(String)'
	 */
	public void testCancelOneOrderByClOrdID() throws Exception {
		String myClOrdID = "MyClOrdID";
		Message message = msgFactory.newMarketOrder(myClOrdID, Side.BUY, BigDecimal.ONE, new MSymbol("QWER"), TimeInForce.DAY, null);
		BigDecimal orderQty = BigDecimal.TEN;
		BigDecimal cumQty = BigDecimal.ONE;
		Message exReport = msgFactory.newExecutionReport("456", myClOrdID, "987", OrdStatus.PENDING_NEW, Side.BUY,
				orderQty, BigDecimal.TEN, BigDecimal.TEN,
				BigDecimal.TEN, cumQty, BigDecimal.TEN, new MSymbol("QWER"), null);
		photonController.handleInternalMessage(message);
		TypesTestBase.assertOrderSingle((OrderSingle)photonController.getLastOrder(), 
				TypesTestBase.NOT_NULL, org.marketcetera.trade.Side.Buy, 
				BigDecimal.ONE, null, org.marketcetera.trade.TimeInForce.Day, 
				OrderType.Market, new MSymbol("QWER"), null, null, null, 
				null, PhotonController.DEFAULT_BROKER, null);

		messageHistory.addIncomingMessage(createReport(exReport));
		photonController.cancelOneOrderByClOrdID(myClOrdID);
		TypesTestBase.assertOrderCancel((OrderCancel)photonController.getLastOrder(), 
				TypesTestBase.NOT_NULL, new OrderID(myClOrdID), 
				org.marketcetera.trade.Side.Buy, new MSymbol("QWER"), null, 
				orderQty, null, null, new BrokerID("bogus"), null);
	}
	
	public static ExecutionReport createReport(Message message)
			throws MessageCreationException {
		return TradeReportsHistoryTest.createServerReport(message);
	}

}
