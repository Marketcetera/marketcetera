package org.marketcetera.messagehistory;

import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicLong;

import junit.framework.Test;

import org.marketcetera.core.FIXVersionTestSuite;
import org.marketcetera.core.FIXVersionedTestCase;
import org.marketcetera.quickfix.FIXVersion;
import org.marketcetera.trade.BrokerID;
import org.marketcetera.trade.Equity;
import org.marketcetera.trade.ExecutionReport;
import org.marketcetera.trade.Factory;
import org.marketcetera.trade.MessageCreationException;
import org.marketcetera.trade.OrderCancelReject;
import org.marketcetera.trade.Originator;
import org.marketcetera.trade.ReportBase;
import org.marketcetera.trade.ReportBaseImpl;
import org.marketcetera.trade.ReportID;

import quickfix.Message;
import quickfix.field.ClOrdID;
import quickfix.field.CxlRejReason;
import quickfix.field.LeavesQty;
import quickfix.field.OrdStatus;
import quickfix.field.OrderID;
import quickfix.field.OrigClOrdID;
import quickfix.field.Side;

public class ReportFillMatcherTest extends FIXVersionedTestCase{

    public ReportFillMatcherTest(String inName, FIXVersion version) {
        super(inName, version);
        
    }
    public static Test suite() {
        return new FIXVersionTestSuite(ReportFillMatcherTest.class,
                FIXVersionTestSuite.ALL_FIX_VERSIONS);
    }
    
   
    private ReportFillMatcher repFillMatcher;
    private ReportHolder holder;
    
    
    public void testServerReportFill() throws Exception{
        repFillMatcher=new ReportFillMatcher();
        Message message = msgFactory.newExecutionReport("clordid1",
                "clordid1", "execido1", OrdStatus.PENDING_NEW, Side.BUY,
                new BigDecimal(1000), null, new BigDecimal(0), null,
                new BigDecimal(100), new BigDecimal(3), new Equity("IBM"), "account", "text");
        message.setField(new LeavesQty(0));
        holder = new ReportHolder(createServerReport(message), "IBM");
        assertFalse(repFillMatcher.matches(holder));
        
        message = msgFactory.newExecutionReport("clordid1",
                "clordid1", "execido1", OrdStatus.PENDING_REPLACE, Side.BUY,
                new BigDecimal(1000), null, new BigDecimal(0), null,
                new BigDecimal(100), new BigDecimal(3), new Equity("IBM"), "account", "text");
        message.setField(new LeavesQty(0));
        holder = new ReportHolder(createServerReport(message), "IBM");
        assertFalse(repFillMatcher.matches(holder));
        
        message = msgFactory.newExecutionReport("clordid1",
                "clordid1", "execido1", OrdStatus.PENDING_CANCEL, Side.BUY,
                new BigDecimal(1000), null, new BigDecimal(0), null,
                new BigDecimal(100), new BigDecimal(3), new Equity("IBM"), "account", "text");
        message.setField(new LeavesQty(0));
        holder = new ReportHolder(createServerReport(message), "IBM");
        assertFalse(repFillMatcher.matches(holder));
        
        
        
    }
    
    public void testBrokerReportFill() throws Exception{
        repFillMatcher=new ReportFillMatcher();
         Message message = msgFactory.newExecutionReport("clordid1",
                "clordid1", "execido1", OrdStatus.NEW, Side.BUY,
                new BigDecimal(1000), null, new BigDecimal(0), null,
                new BigDecimal(100), new BigDecimal(3), new Equity("IBM"), "account", "text");
        message.setField(new LeavesQty(0));
        holder = new ReportHolder(createBrokerReport(message), "IBM");
        assertFalse(repFillMatcher.matches(holder));
       
        
        message = msgFactory.newExecutionReport("clordid1",
                "clordid1", "execido1", OrdStatus.REPLACED, Side.BUY,
                new BigDecimal(1000), null, new BigDecimal(0), null,
                new BigDecimal(100), new BigDecimal(3), new Equity("IBM"), "account", "text");
        message.setField(new LeavesQty(0));
        holder = new ReportHolder(createBrokerReport(message), "IBM");
        assertFalse(repFillMatcher.matches(holder));
        

        message = msgFactory.newExecutionReport("clordid1",
                "clordid1", "execido1", OrdStatus.PARTIALLY_FILLED, Side.BUY,
                new BigDecimal(1000), null, new BigDecimal(10), new BigDecimal(10),
                new BigDecimal(100), new BigDecimal(3), new Equity("IBM"), "account", "text");
        message.setField(new LeavesQty(0));
        holder = new ReportHolder(createBrokerReport(message), "IBM");
        assertTrue(repFillMatcher.matches(holder));
        
        message = msgFactory.newExecutionReport("clordid1",
                "clordid1", "execido1", OrdStatus.FILLED, Side.BUY,
                new BigDecimal(100), null, new BigDecimal(100), new BigDecimal(3),
                new BigDecimal(100), new BigDecimal(3), new Equity("IBM"), "account", "text");
        message.setField(new LeavesQty(0));
        holder = new ReportHolder(createBrokerReport(message), "IBM");
        assertTrue(repFillMatcher.matches(holder));
        
        
    }
    
    public void testUnrecognizableER() throws Exception{
    	repFillMatcher=new ReportFillMatcher();
        Message message = msgFactory.newExecutionReport("clordid1",
                "clordid1", "execido1", OrdStatus.FILLED, Side.BUY,
                new BigDecimal(100), null, null, null,
                new BigDecimal(100), new BigDecimal(3), new Equity("IBM"), "account", "text");
        
        holder = new ReportHolder(createBrokerReport(message), "IBM");
        assertFalse(repFillMatcher.matches(holder));
        
        message = msgFactory.newExecutionReport("clordid1",
                "clordid1", "execido1", OrdStatus.EXPIRED, Side.BUY,
                new BigDecimal(100), null, new BigDecimal(0), new BigDecimal(0),
                new BigDecimal(0), new BigDecimal(0), new Equity("IBM"), "account", "text");
       
        message.removeField(OrdStatus.FIELD);
        holder = new ReportHolder(createBrokerReport(message), "IBM");
        assertFalse(repFillMatcher.matches(holder));
        
        holder = new ReportHolder(createBrokerReject(createCancelReject("orderId", "origOrdId")),"IBM");
        assertFalse(repFillMatcher.matches(holder));
        
        holder = new ReportHolder(createServerReject(createCancelReject("orderId", "origOrdId")),"IBM");
        assertFalse(repFillMatcher.matches(holder));
    }
    
    private Message createCancelReject(String orderId, String origOrdId) {
        return msgFactory.newOrderCancelReject(new OrderID("orderId"), new ClOrdID(orderId), new OrigClOrdID(origOrdId), "", new CxlRejReason());
    }
    
    private ExecutionReport createServerReport(Message message)  throws MessageCreationException {
        return Factory.getInstance().createExecutionReport(message,
        new BrokerID("null"), Originator.Server, null, null);
    }
    
    private ExecutionReport createBrokerReport(Message message)  throws MessageCreationException {
        return Factory.getInstance().createExecutionReport(message,
        new BrokerID("null"), Originator.Broker, null, null);
    }
    
    public static OrderCancelReject createBrokerReject(Message message)
            throws MessageCreationException {
        return Factory.getInstance().createOrderCancelReject(message,
                new BrokerID("bogus"), Originator.Broker, null, null);
    }
    
    public static OrderCancelReject createServerReject(Message message)
            throws MessageCreationException {
        return Factory.getInstance().createOrderCancelReject(message,
                new BrokerID("bogus"), Originator.Server, null, null);
    }
   
}
