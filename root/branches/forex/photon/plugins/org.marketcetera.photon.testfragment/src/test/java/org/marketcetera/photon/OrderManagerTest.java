package org.marketcetera.photon;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import junit.framework.TestCase;

import org.marketcetera.core.instruments.MockUnderlyingSymbolSupport;
import org.marketcetera.messagehistory.ReportHolder;
import org.marketcetera.messagehistory.TradeReportsHistory;
import org.marketcetera.messagehistory.TradeReportsHistoryTest;
import org.marketcetera.quickfix.FIXMessageFactory;
import org.marketcetera.quickfix.FIXVersion;
import org.marketcetera.trade.BrokerID;
import org.marketcetera.trade.Equity;
import org.marketcetera.trade.ExecutionReport;
import org.marketcetera.trade.Factory;
import org.marketcetera.trade.Instrument;
import org.marketcetera.trade.MessageCreationException;
import org.marketcetera.trade.NewOrReplaceOrder;
import org.marketcetera.trade.OrderCancel;
import org.marketcetera.trade.OrderID;
import org.marketcetera.trade.OrderReplace;
import org.marketcetera.trade.OrderSingle;
import org.marketcetera.trade.OrderType;
import org.marketcetera.trade.Originator;
import org.marketcetera.trade.SecurityType;
import org.marketcetera.trade.Side;
import org.marketcetera.trade.TimeInForce;
import org.marketcetera.trade.TypesTestBase;

import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.field.MsgType;
import quickfix.field.OrdStatus;
import quickfix.field.OrdType;
import quickfix.field.SenderCompID;
import quickfix.field.TargetCompID;
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
    public static Instrument INSTRUMENT = new Equity("SYMB");
    public static String CL_ORD_ID = "CLORDID";
   
    private static FIXMessageFactory msgFactory = FIXVersion.FIX_SYSTEM.getMessageFactory();
    public static Message getTestableExecutionReport() throws FieldNotFound {
            Message aMessage = msgFactory.newExecutionReport("456", CL_ORD_ID, "987", OrdStatus.PARTIALLY_FILLED, quickfix.field.Side.BUY, new BigDecimal(1000), new BigDecimal("12.3"), new BigDecimal(500),
                            new BigDecimal("12.3"), new BigDecimal(500), new BigDecimal("12.3"), INSTRUMENT, null, null);
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
	


    private OrderSingle newOrderSingle(Side side, String quantity,
            String symbol, OrderType type, String price, TimeInForce tif,
            String account) {
        OrderSingle order = Factory.getInstance().createOrderSingle();
        fill(order, side, quantity, symbol, type, price, tif, account);
        return order;
    }

    private void fill(NewOrReplaceOrder order, Side side, String quantity,
            String symbol, OrderType type, String price, TimeInForce tif,
            String account) {
        order.setSide(side);
        if (quantity != null) {
            order.setQuantity(new BigDecimal(quantity));
        }
        if (symbol != null) {
            order.setInstrument(createInstrument(symbol));
        }
        order.setOrderType(type);
        if (price != null) {
            order.setPrice(new BigDecimal(price));
        }
        order.setTimeInForce(tif);
        order.setAccount(account);
    }

    protected Instrument createInstrument(String symbol) {
        return new Equity(symbol);
    }
	
	private TradeReportsHistory messageHistory;
	private ImmediatePhotonController photonController;

	protected void setUp() throws Exception {
		super.setUp();
		messageHistory = new TradeReportsHistory(msgFactory, new MockUnderlyingSymbolSupport());
		photonController = new ImmediatePhotonController();
		photonController.setMessageHistory(messageHistory);
	}

	
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

	public void testSendAndCancel() throws Exception {
		EventList<ReportHolder> historyList = messageHistory.getAllMessagesList();
		assertEquals(0, historyList.size());
		OrderSingle order = newOrderSingle(Side.Buy, "1", "QWER", OrderType.Limit, "10", TimeInForce.Day, null);
		photonController.sendOrder(order);
        TypesTestBase.assertOrderSingle((OrderSingle) photonController.getLastOrder(), 
                TypesTestBase.NOT_NULL, 
                org.marketcetera.trade.Side.Buy, BigDecimal.ONE, BigDecimal.TEN, 
                org.marketcetera.trade.TimeInForce.Day, OrderType.Limit, 
                new Equity("QWER"), SecurityType.CommonStock, null, null, null, null, 
                PhotonController.DEFAULT_BROKER, null);
        Message exReport = msgFactory.newExecutionReport("456", "ASDF", "987",
                OrdStatus.PENDING_NEW, quickfix.field.Side.BUY, BigDecimal.ONE,
                BigDecimal.TEN, new BigDecimal(500), BigDecimal.TEN,
                BigDecimal.TEN, BigDecimal.TEN, new Equity("QWER"), null, null);
		ExecutionReport report = createReport(exReport);
        messageHistory.addIncomingMessage(report);
        OrderCancel cancel = Factory.getInstance().createOrderCancel(report);
        photonController.sendOrder(cancel);
		TypesTestBase.assertOrderCancel((OrderCancel)photonController.getLastOrder(), 
				TypesTestBase.NOT_NULL, new OrderID("ASDF"), 
				org.marketcetera.trade.Side.Buy, new Equity("QWER"), SecurityType.CommonStock, 
				BigDecimal.ONE, "456", null, null, new BrokerID("bogus"), 
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

	public void testCancelReplaceOneOrder() throws Exception {
		String myClOrdID = "MyClOrdID";
		OrderSingle order = newOrderSingle(Side.Buy, "1", "QWER", OrderType.Limit, "10", TimeInForce.Day, null);
		Message exReport = msgFactory.newExecutionReport("456", myClOrdID, "987", OrdStatus.PENDING_NEW, quickfix.field.Side.BUY, BigDecimal.ONE, BigDecimal.TEN, new BigDecimal(500),
				BigDecimal.TEN, BigDecimal.TEN, BigDecimal.TEN, new Equity("QWER"), null, null);
		exReport.setField(new OrdType(OrdType.LIMIT));
		exReport.setField(new quickfix.field.TimeInForce(quickfix.field.TimeInForce.DAY));
		photonController.sendOrder(order);
		TypesTestBase.assertOrderSingle((OrderSingle)photonController.getLastOrder(), 
				TypesTestBase.NOT_NULL, org.marketcetera.trade.Side.Buy, BigDecimal.ONE, 
				BigDecimal.TEN, org.marketcetera.trade.TimeInForce.Day, OrderType.Limit, 
				new Equity("QWER"), SecurityType.CommonStock, null, null, null, null, 
				PhotonController.DEFAULT_BROKER, null);

		ExecutionReport report = createReport(exReport);
        messageHistory.addIncomingMessage(report);
		photonController.sendOrder(Factory.getInstance().createOrderReplace(report));
		TypesTestBase.assertOrderReplace((OrderReplace)photonController.getLastOrder(), 
				TypesTestBase.NOT_NULL, new OrderID(myClOrdID), "456", 
				OrderType.Limit, org.marketcetera.trade.Side.Buy, 
				BigDecimal.ONE, BigDecimal.TEN, new Equity("QWER"), 
				SecurityType.CommonStock, org.marketcetera.trade.TimeInForce.Day, null, null, new BrokerID("bogus"), 
				null, null, null);
	}

	public void testCancelOneOrder() throws Exception {
	    OrderSingle order = newOrderSingle(Side.Buy, "1", "QWER", OrderType.Market, null, TimeInForce.Day, null);
		String myClOrdID = "MyClOrdID";
		Message exReport = msgFactory.newExecutionReport("456", myClOrdID, "987", OrdStatus.PENDING_NEW, quickfix.field.Side.BUY, BigDecimal.ONE, BigDecimal.TEN, new BigDecimal(500),
				BigDecimal.TEN, BigDecimal.TEN, BigDecimal.TEN, new Equity("QWER"), null, null);
		photonController.sendOrder(order);
		TypesTestBase.assertOrderSingle((OrderSingle)photonController.getLastOrder(), 
				TypesTestBase.NOT_NULL, org.marketcetera.trade.Side.Buy, 
				BigDecimal.ONE, null, org.marketcetera.trade.TimeInForce.Day, OrderType.Market, 
				new Equity("QWER"), SecurityType.CommonStock, null, null, null, null,
				PhotonController.DEFAULT_BROKER, null);

		ExecutionReport report = createReport(exReport);
        messageHistory.addIncomingMessage(report);
         OrderCancel cancel = Factory.getInstance().createOrderCancel(report);
		photonController.sendOrder(cancel);
		TypesTestBase.assertOrderCancel((OrderCancel)photonController.getLastOrder(),
				TypesTestBase.NOT_NULL, new OrderID(myClOrdID), 
				org.marketcetera.trade.Side.Buy, new Equity("QWER"), 
				SecurityType.CommonStock, BigDecimal.ONE, "456", null, null,
				new BrokerID("bogus"), null);
	}

	public void testCancelOneOrderByClOrdID() throws Exception {
	    OrderSingle order = newOrderSingle(Side.Buy, "1", "QWER", OrderType.Market, null, TimeInForce.Day, null);
		String myClOrdID = "MyClOrdID";
		BigDecimal orderQty = BigDecimal.TEN;
		BigDecimal cumQty = BigDecimal.ONE;
		Message exReport = msgFactory.newExecutionReport("456", myClOrdID, "987", OrdStatus.PENDING_NEW, quickfix.field.Side.BUY,
				orderQty, BigDecimal.TEN, BigDecimal.TEN,
				BigDecimal.TEN, cumQty, BigDecimal.TEN, new Equity("QWER"), null, null);
		photonController.sendOrder(order);
		TypesTestBase.assertOrderSingle((OrderSingle)photonController.getLastOrder(), 
				TypesTestBase.NOT_NULL, org.marketcetera.trade.Side.Buy, 
				BigDecimal.ONE, null, org.marketcetera.trade.TimeInForce.Day, 
				OrderType.Market, new Equity("QWER"), SecurityType.CommonStock, null, null, 
				null, null, PhotonController.DEFAULT_BROKER, null);

		messageHistory.addIncomingMessage(createReport(exReport));
		photonController.cancelOneOrderByClOrdID(myClOrdID);
		TypesTestBase.assertOrderCancel((OrderCancel)photonController.getLastOrder(), 
				TypesTestBase.NOT_NULL, new OrderID(myClOrdID), 
				org.marketcetera.trade.Side.Buy, new Equity("QWER"), SecurityType.CommonStock, 
				orderQty, null, null, null, new BrokerID("bogus"), null);
	}
	
	public static ExecutionReport createReport(Message message)
			throws MessageCreationException {
		return TradeReportsHistoryTest.createServerReport(message);
	}

}
