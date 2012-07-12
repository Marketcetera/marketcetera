package org.marketcetera.messagehistory;

import java.math.BigDecimal;

import junit.framework.Test;

import org.marketcetera.core.ExpectedTestFailure;
import org.marketcetera.core.FIXVersionTestSuite;
import org.marketcetera.core.FIXVersionedTestCase;
import org.marketcetera.core.position.impl.ExpectedListChanges;
import org.marketcetera.quickfix.FIXVersion;
import org.marketcetera.trade.BrokerID;
import org.marketcetera.trade.Equity;
import org.marketcetera.trade.ExecutionReport;
import org.marketcetera.trade.Factory;
import org.marketcetera.trade.MessageCreationException;
import org.marketcetera.trade.Option;
import org.marketcetera.trade.OptionType;
import org.marketcetera.trade.Originator;

import quickfix.Message;
import quickfix.field.AvgPx;
import quickfix.field.CumQty;
import quickfix.field.ExecTransType;
import quickfix.field.ExecType;
import quickfix.field.LeavesQty;
import quickfix.field.MsgType;
import quickfix.field.OrdStatus;
import quickfix.field.Side;
import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.event.ListEvent;

/* $License$ */

/**
 * Test {@link AveragePriceReportListTest}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.0.0
 */
public class AveragePriceReportListTest extends FIXVersionedTestCase {
    public AveragePriceReportListTest(String inName, FIXVersion version) {
        super(inName, version);
    }

    public static Test suite() {
        return new FIXVersionTestSuite(AveragePriceReportListTest.class,
                FIXVersionTestSuite.ALL_FIX_VERSIONS);
    }

   public void testInsertExecutionReport() throws Exception {
        EventList<ReportHolder> source = new BasicEventList<ReportHolder>();
        AveragePriceReportList averagePriceList = new AveragePriceReportList(
                FIXVersion.FIX_SYSTEM.getMessageFactory(), source);
        Message message = msgFactory.newExecutionReport("clordid1", "clordid1",
                "execido1", OrdStatus.NEW, Side.BUY, new BigDecimal(300), null,
                new BigDecimal(0), new BigDecimal(0), new BigDecimal(0),
                new BigDecimal(0), new Equity("IBM"), "account", "text");
        message.setField(new LeavesQty(300.0));
        
        
        source.add(new ReportHolder(createReport(message), "IBM"));

        assertEquals(0, averagePriceList.size());
       
        Message message2 = msgFactory.newExecutionReport("clordid1",
                "clordid1", "execido1", OrdStatus.PARTIALLY_FILLED, Side.BUY,
                new BigDecimal(300), new BigDecimal(111),null, new BigDecimal(
                        111), new BigDecimal(110), new BigDecimal(111),
                new Equity("IBM"), "account", "text");
        message2.setField(new LeavesQty(190.0));
        source.add(new ReportHolder(createReport(message2), "IBM"));
        message2.setField(new ExecTransType(ExecTransType.NEW));
        message2.setField(new ExecType(ExecType.PARTIAL_FILL));
        
        assertEquals(0, averagePriceList.size());
        
        Message message5 = msgFactory.newExecutionReport("clordid1",
                "clordid1", "execido1", OrdStatus.PARTIALLY_FILLED, Side.BUY,
                new BigDecimal(300), null, new BigDecimal(110), null,
                new BigDecimal(110), new BigDecimal(111),
                new Equity("IBM"), "account", "text");
        message5.setField(new LeavesQty(190.0));
        source.add(new ReportHolder(createReport(message5), "IBM"));
        message5.setField(new ExecTransType(ExecTransType.NEW));
        message5.setField(new ExecType(ExecType.PARTIAL_FILL));
        
        assertEquals(0, averagePriceList.size());
        
        Message message3 = msgFactory.newExecutionReport("clordid1",
                "clordid1", "execido1", OrdStatus.PARTIALLY_FILLED, Side.BUY,
                new BigDecimal(300), new BigDecimal(111), new BigDecimal(110), new BigDecimal(
                        111), new BigDecimal(110), new BigDecimal(111),
                new Equity("IBM"), "account", "text");
        message3.setField(new LeavesQty(190.0));
        
        source.add(new ReportHolder(createReport(message3), "IBM"));
        assertEquals(1, averagePriceList.size());
        
        Message avgPriceMessage = averagePriceList.get(0).getMessage();
        assertEquals(MsgType.EXECUTION_REPORT, avgPriceMessage.getHeader()
                .getString(MsgType.FIELD));
        assertEquals(110.0, avgPriceMessage.getDouble(CumQty.FIELD), .00001);
        assertEquals(111, avgPriceMessage.getDouble(AvgPx.FIELD), .0001);

        Message message4 = msgFactory.newExecutionReport("clordid1",
                "clordid1", "execido1", OrdStatus.FILLED, Side.BUY,
                new BigDecimal(300), new BigDecimal(55), new BigDecimal(190), new BigDecimal(55),
                new BigDecimal(300), new BigDecimal(55), new Equity("IBM"),
                "account", "text");
        
        message4.setField(new ExecTransType(ExecTransType.NEW));
        message4.setField(new ExecType(ExecType.FILL));
        
        source.add(new ReportHolder(createReport(message4), "IBM"));

        assertEquals(1, averagePriceList.size());
        avgPriceMessage = averagePriceList.get(0).getMessage();
        assertEquals(MsgType.EXECUTION_REPORT, avgPriceMessage.getHeader()
                .getString(MsgType.FIELD));
        assertEquals(300.0, avgPriceMessage.getDouble(CumQty.FIELD), .0001);
        assertEquals(75.5333, avgPriceMessage.getDouble(AvgPx.FIELD), .0001);
        

    }

     public void testAddOrderFirst() throws Exception {
        EventList<ReportHolder> source = new BasicEventList<ReportHolder>();
        AveragePriceReportList averagePriceList = new AveragePriceReportList(
                FIXVersion.FIX_SYSTEM.getMessageFactory(), source);
        Message message = msgFactory.newExecutionReport("clordid1", "clordid1",
                "execido1", OrdStatus.NEW, Side.BUY, new BigDecimal(
                        1000), null, new BigDecimal(0), null, new BigDecimal(
                        100), new BigDecimal(3), new Equity("IBM"), "account", "text");
        message.setField(new LeavesQty(1000));
        message.setField(new ExecTransType(ExecTransType.NEW));
        message.setField(new ExecType(ExecType.NEW));
        source.add(new ReportHolder(createReport(message), "IBM"));

        assertEquals(0, averagePriceList.size());
        
       

    }

      public void testRemove() throws Exception {
        final EventList<ReportHolder> source = new BasicEventList<ReportHolder>();
        AveragePriceReportList averagePriceList = new AveragePriceReportList(
                FIXVersion.FIX_SYSTEM.getMessageFactory(), source);
        Message message = msgFactory.newExecutionReport("clordid1", "clordid1",
                "execido1", OrdStatus.PARTIALLY_FILLED, Side.BUY, new BigDecimal(10), new BigDecimal(5), new BigDecimal(2), new BigDecimal(5), new BigDecimal(
                        2), new BigDecimal(5), new Equity("IBM"), "account", "text");
        Message message2 = msgFactory.newExecutionReport("clordid1", "clordid1",
                "execido1", OrdStatus.PARTIALLY_FILLED, Side.BUY, new BigDecimal(20), new BigDecimal(5), new BigDecimal(10), new BigDecimal(5), new BigDecimal(
                        10), new BigDecimal(5), new Equity("MSFT"), "account", "text");
        
        source.add(new ReportHolder(createReport(message), "IBM"));
        source.add(new ReportHolder(createReport(message2), "MSFT"));

        assertEquals(2, averagePriceList.size());

        // any remove empties the list (since TradeReportsHistory only supports clear())
        source.addListEventListener(new ExpectedListChanges<ReportHolder>("avg", new int[] {
                ListEvent.DELETE, 0, ListEvent.DELETE, 0 }));
        source.remove(0);
        assertEquals(0, averagePriceList.size());
    }

        public void testUpdate() throws Exception {
        final EventList<ReportHolder> source = new BasicEventList<ReportHolder>();
        AveragePriceReportList averagePriceList = new AveragePriceReportList(
                FIXVersion.FIX_SYSTEM.getMessageFactory(), source);
        final Message message = msgFactory.newExecutionReport("clordid1", "clordid1",
                "execido1", OrdStatus.PARTIALLY_FILLED, Side.BUY, new BigDecimal(0), null, new BigDecimal(10), new BigDecimal(11), new BigDecimal(
                        10), new BigDecimal(11), new Equity("IBM"), "account", "text");
        message.setField(new LeavesQty(90.0));
        source.add(new ReportHolder(createReport(message), "IBM"));

        assertEquals(1, averagePriceList.size());

        new ExpectedTestFailure(UnsupportedOperationException.class) {
            @Override
            protected void execute() throws Throwable {
                source.set(0, new ReportHolder(createReport(message), "IBM"));
            }
        }.run();
    }

          public void testInstrument() throws Exception {
        if (fixVersion != FIXVersion.FIX40) {
            EventList<ReportHolder> source = new BasicEventList<ReportHolder>();
            AveragePriceReportList averagePriceList = new AveragePriceReportList(
                    FIXVersion.FIX_SYSTEM.getMessageFactory(), source);
            Option option = new Option("IBM", "200912", BigDecimal.ONE,
                    OptionType.Put);
            Message message = msgFactory.newExecutionReport("clordid1",
                    "clordid1", "execido1", OrdStatus.PARTIALLY_FILLED, Side.BUY,
                    new BigDecimal(1000), new BigDecimal(1), new BigDecimal(100), new BigDecimal(1),
                    new BigDecimal(100), new BigDecimal(3), option, "account", "text");
            message.setField(new LeavesQty(900));
            message.setField(new ExecTransType(ExecTransType.NEW));
            message.setField(new ExecType(ExecType.PARTIAL_FILL));
            source.add(new ReportHolder(createReport(message), "IBM"));
            assertEquals(1, averagePriceList.size());
            ReportHolder holder = averagePriceList.get(0);
            ExecutionReport avgPriceReport = (ExecutionReport) holder
                    .getReport();
            assertEquals(option, avgPriceReport.getInstrument());
            assertEquals("IBM", holder.getUnderlying());
        }
    }
          
      public void testPendingStatusMessage() throws Exception {
              final EventList<ReportHolder> source = new BasicEventList<ReportHolder>();
              AveragePriceReportList averagePriceList = new AveragePriceReportList(
                      FIXVersion.FIX_SYSTEM.getMessageFactory(), source);
              Message message = msgFactory.newExecutionReport("clordid1", "clordid1",
                      "execido1", OrdStatus.PENDING_NEW, Side.BUY, new BigDecimal(
                              10), null, new BigDecimal(0), null, new BigDecimal(
                              0), new BigDecimal(0), new Equity("IBM"), "account", "text");
              
              message.setField(new LeavesQty(0));
              message.setField(new ExecTransType(ExecTransType.NEW));
              message.setField(new ExecType(ExecType.NEW));
              
              source.add(new ReportHolder(createReport(message), "IBM"));

              assertEquals(0, averagePriceList.size());
              
              Message message2 = msgFactory.newExecutionReport("clordid1", "clordid1",
                      "execido1", OrdStatus.PENDING_REPLACE, Side.BUY, new BigDecimal(
                              10), null, new BigDecimal(0), null, new BigDecimal(
                              0), new BigDecimal(0), new Equity("IBM"), "account", "text");
              
              message.setField(new LeavesQty(0));
              message.setField(new ExecTransType(ExecTransType.NEW));
              message.setField(new ExecType(ExecType.NEW));
              
              source.add(new ReportHolder(createReport(message2), "IBM"));

              assertEquals(0, averagePriceList.size());
             
             
              Message message3 = msgFactory.newExecutionReport("clordid1", "clordid1",
                      "execido1", OrdStatus.PENDING_CANCEL, Side.BUY, new BigDecimal(
                              10), null, new BigDecimal(0), null, new BigDecimal(
                              0), new BigDecimal(0), new Equity("IBM"), "account", "text");
              
              message.setField(new LeavesQty(0));
              message.setField(new ExecTransType(ExecTransType.NEW));
              message.setField(new ExecType(ExecType.NEW));
              
              source.add(new ReportHolder(createReport(message3), "IBM"));

              assertEquals(0, averagePriceList.size());
             
          }
      
      public void testOriginatorMessage() throws Exception{
          final EventList<ReportHolder> source = new BasicEventList<ReportHolder>();
          AveragePriceReportList averagePriceList = new AveragePriceReportList(
                  FIXVersion.FIX_SYSTEM.getMessageFactory(), source);
          final Message message = msgFactory.newExecutionReport("clordid1", "clordid1",
                  "execido1", OrdStatus.PENDING_NEW, Side.BUY, new BigDecimal(
                          10), null, new BigDecimal(0), null, new BigDecimal(
                          0), new BigDecimal(0), new Equity("IBM"), "account", "text");
          message.setField(new LeavesQty(10));
          
          source.add(new ReportHolder(Factory.getInstance().createExecutionReport(message,
                  new BrokerID("null"), Originator.Server, null, null), "IBM"));

          assertEquals(0, averagePriceList.size());
          
          
          Message message3 = msgFactory.newExecutionReport("clordid1", "clordid1",
                  "execido1", OrdStatus.PENDING_REPLACE, Side.BUY, new BigDecimal(
                          10), null, new BigDecimal(0), null, new BigDecimal(
                          0), new BigDecimal(0), new Equity("IBM"), "account", "text");
          
          message3.setField(new LeavesQty(10));
          source.add(new ReportHolder(Factory.getInstance().createExecutionReport(message,
                  new BrokerID("null"), Originator.Server, null, null), "IBM"));

          assertEquals(0, averagePriceList.size());
          
           Message message5 = msgFactory.newExecutionReport("clordid1", "clordid1",
                  "execido1", OrdStatus.PENDING_CANCEL, Side.BUY, new BigDecimal(
                          10), null, new BigDecimal(0), null, new BigDecimal(
                          0), new BigDecimal(0), new Equity("IBM"), "account", "text");
          
          message5.setField(new LeavesQty(10));
          source.add(new ReportHolder(Factory.getInstance().createExecutionReport(message,
                  new BrokerID("null"), Originator.Server, null, null), "IBM"));

          assertEquals(0, averagePriceList.size());
          
          Message message6 = msgFactory.newExecutionReport("clordid1", "clordid1",
                  "execido1", OrdStatus.PENDING_CANCEL, Side.BUY, new BigDecimal(
                          10), null, new BigDecimal(0), null, new BigDecimal(
                          0), new BigDecimal(0), new Equity("IBM"), "account", "text");
          
          message6.setField(new LeavesQty(10));
          source.add(new ReportHolder(Factory.getInstance().createExecutionReport(message,
                  new BrokerID("null"), Originator.Broker, null, null), "IBM"));

          assertEquals(0, averagePriceList.size());
      }
      
      private ExecutionReport createReport(Message message)
            throws MessageCreationException {
        return Factory.getInstance().createExecutionReport(message,
                new BrokerID("null"), Originator.Broker, null, null);
    }
}
