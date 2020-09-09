package org.marketcetera.trade;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.marketcetera.module.ExpectedFailure;
import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.time.DateService;

/* $License$ */
/**
 * Tests {@link ExecutionReport}
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public class ExecutionReportTest extends TypesTestBase {
    /**
     * Tests various failures when creating execution reports.
     *
     * @throws Exception if there were errors.
     */
    @Test
    public void failures() throws Exception {
        final BrokerID cID = new BrokerID("blah");
        // null message
        new ExpectedFailure<NullPointerException>(){
             protected void run() throws Exception {
                 sFactory.createExecutionReport
                     (null, cID, Originator.Server, null, null);
             }
         };
        // null originator
        final quickfix.Message execReport = createEmptyExecReport();
        new ExpectedFailure<NullPointerException>(){
             protected void run() throws Exception {
                 sFactory.createExecutionReport
                     (execReport, cID, null, null, null);
             }
         };
        // wrong quickfix message type
        final quickfix.Message message = getSystemMessageFactory().newBasicOrder();
        new ExpectedFailure<MessageCreationException>(
                Messages.NOT_EXECUTION_REPORT, message.toString()){
            protected void run() throws Exception {
                sFactory.createExecutionReport
                    (message, null, Originator.Server, null, null);
            }
        };
    }

    /**
     * Tests various getters.
     *
     * @throws Exception if there were errors.
     */
    @Test
    public void getters() throws Exception {
        // report with all empty fields
        quickfix.Message msg = createEmptyExecReport();
        ExecutionReport report = sFactory.createExecutionReport
            (msg, null, Originator.Server, null, null);
        assertReportBaseValues(report, null, null, null, null, null, null,
                               null, Originator.Server, null, null);
        assertExecReportValues(report, null, null, null, null, null, null,
                null, null, null, null, null, null, null, null, null,
                null, null, false);
        assertNull(report.getReportID());
        //Verify toString, doesn't fail.
        report.toString();
        //validate it is null
        assertEquals(null, report.getPrice());
        //report with all fields filled in
        BrokerID cID = new BrokerID("bro1");
        OrderID orderID = new OrderID("or2");
        String destOrderID = "brokOrd1";
        OrderStatus orderStatus = OrderStatus.PartiallyFilled;
        String execID = "kj3";
        Side side = Side.Buy;
        BigDecimal orderQty = new BigDecimal("34.546");
        BigDecimal orderPrice = new BigDecimal("7945.234");
        BigDecimal lastShares = new BigDecimal("2.7534");
        BigDecimal lastPrice = new BigDecimal("9.234");
        BigDecimal cumQty = new BigDecimal("984.34");
        BigDecimal avgPrice = new BigDecimal("34.234");
        Instrument instrument = new Equity("METC");
        String account= "yes";
        String text= "some text";
        UserID actorID = new UserID(2);
        UserID viewerID = new UserID(3);

        //Verify returning FIX fields in a map.
        msg = getSystemMessageFactory().newExecutionReport(destOrderID,
                orderID.getValue(), execID, orderStatus.getFIXValue(),
                side.getFIXValue(), orderQty, orderPrice, lastShares,
                lastPrice, cumQty, avgPrice, instrument, account, text );
        OrderID origOrderID = new OrderID("or1");
        ExecutionType execType = ExecutionType.PendingNew;
        String lastMarket = "XDES";
        BigDecimal leavesQty = new BigDecimal("343.53");
        OrderType orderType = OrderType.Limit;
        java.time.LocalDateTime sendingTime = java.time.LocalDateTime.now();
        TimeInForce timeInForce = TimeInForce.Day;
        java.time.LocalDateTime transactTime = java.time.LocalDateTime.now();
        text = "show me the money";
        msg.setField(new quickfix.field.OrigClOrdID(origOrderID.getValue()));
        msg.setField(new quickfix.field.ExecType(execType.getFIXValue()));
        msg.setField(new quickfix.field.LastMkt(lastMarket));
        msg.setField(new quickfix.field.LeavesQty(leavesQty));
        msg.setField(new quickfix.field.OrdType(orderType.getFIXValue()));
        msg.getHeader().setField(new quickfix.field.SendingTime(sendingTime));
        msg.setField(new quickfix.field.TimeInForce(timeInForce.getFIXValue()));
        msg.setField(new quickfix.field.TransactTime(transactTime));
        msg.setField(new quickfix.field.Text(text));
        msg.setField(new quickfix.field.OrderCapacity(
                quickfix.field.OrderCapacity.PROPRIETARY));
        msg.setField(new quickfix.field.PositionEffect(
                quickfix.field.PositionEffect.CLOSE));
        
        //Verify the regular factory method
        report = sFactory.createExecutionReport(msg, cID,
                                                Originator.Broker, actorID, viewerID);
        assertReportBaseValues(report, cID, orderID, orderStatus,
                               origOrderID, sendingTime, text, destOrderID,
                               Originator.Broker, actorID, viewerID);
        assertExecReportValues(report, account, avgPrice, cumQty, execID,
                execType, lastMarket, lastPrice, lastShares, leavesQty,
                orderQty, orderType, side, instrument, timeInForce,
                transactTime,
                OrderCapacity.Proprietary, PositionEffect.Close, true);
        assertNull(report.getReportID());
        //Verify toString() doesn't fail.
        report.toString();
        //validate orderprice is equal to getprices
        assertEquals(orderPrice , report.getPrice());
        //Verify the map
        Map<Integer,String> expected = new HashMap<Integer, String>();
        expected.put(quickfix.field.Account.FIELD, account);
        expected.put(quickfix.field.LastShares.FIELD,
                     quickfix.field.converter.DecimalConverter.convert(lastShares));
        expected.put(quickfix.field.OrderQty.FIELD,
                     quickfix.field.converter.DecimalConverter.convert(orderQty));
        expected.put(quickfix.field.LastShares.FIELD,
                     quickfix.field.converter.DecimalConverter.convert(lastShares));
        expected.put(quickfix.field.OrdStatus.FIELD,
                String.valueOf(orderStatus.getFIXValue()));
        expected.put(quickfix.field.AvgPx.FIELD,
                     quickfix.field.converter.DecimalConverter.convert(avgPrice));
        expected.put(quickfix.field.OrderID.FIELD, destOrderID);
        expected.put(quickfix.field.OrdType.FIELD, String.valueOf(orderType.getFIXValue()));
        expected.put(quickfix.field.ClOrdID.FIELD, orderID.getValue());
        expected.put(quickfix.field.OrigClOrdID.FIELD, origOrderID.getValue());
        expected.put(quickfix.field.SecurityType.FIELD,
                instrument.getSecurityType().getFIXValue());
        expected.put(quickfix.field.Price.FIELD,
                     quickfix.field.converter.DecimalConverter.convert(orderPrice));
        expected.put(quickfix.field.CumQty.FIELD,
                     quickfix.field.converter.DecimalConverter.convert(cumQty));
        expected.put(quickfix.field.ExecID.FIELD, execID);
        expected.put(quickfix.field.Symbol.FIELD, instrument.getSymbol());
        expected.put(quickfix.field.Side.FIELD,
                String.valueOf(side.getFIXValue()));
        expected.put(quickfix.field.TimeInForce.FIELD,
                String.valueOf(timeInForce.getFIXValue()));
        expected.put(quickfix.field.Text.FIELD, text);
        expected.put(quickfix.field.ExecType.FIELD,
                     String.valueOf(execType.getFIXValue()));
        expected.put(quickfix.field.LastPx.FIELD,
                     quickfix.field.converter.DecimalConverter.convert(lastPrice));
        expected.put(quickfix.field.LeavesQty.FIELD,
                     quickfix.field.converter.DecimalConverter.convert(leavesQty));
        expected.put(quickfix.field.TransactTime.FIELD,
                     quickfix.field.converter.UtcTimestampConverter.convert(DateService.toDate(transactTime),
                                                                            true));
        expected.put(quickfix.field.LastMkt.FIELD, lastMarket);
        expected.put(quickfix.field.OrderCapacity.FIELD, String.valueOf(
                quickfix.field.OrderCapacity.PROPRIETARY));
        expected.put(quickfix.field.PositionEffect.FIELD, String.valueOf(
                quickfix.field.PositionEffect.CLOSE));

        final Map<Integer, String> actual = ((FIXMessageSupport) report).getFields();
        assertEquals(expected, actual);
        //Verify that the map is not modifiable
        new ExpectedFailure<UnsupportedOperationException>(){
            protected void run() throws Exception {
                actual.clear();
            }
        };

        //Verify the wrapped FIX Message.
        assertSame(msg, ((FIXMessageSupport)report).getMessage());
    }

    /**
     * Verifies translation of ExecTransType into ExecType.
     *
     * @throws Exception if there were errors.
     */
    @Test
    public void execTransTypeTranslation() throws Exception {
        quickfix.Message msg = createEmptyExecReport();
        assertEquals(null, sFactory.createExecutionReport(msg,
                null, Originator.Server, null, null).getExecutionType());

        msg.setField(new quickfix.field.ExecTransType(quickfix.field.ExecTransType.NEW));
        msg.setField(new quickfix.field.OrdStatus(quickfix.field.OrdStatus.NEW));
        assertEquals(ExecutionType.New,
                sFactory.createExecutionReport(msg, null,
                        Originator.Server, null, null).getExecutionType());

        msg.setField(new quickfix.field.ExecTransType(quickfix.field.ExecTransType.CANCEL));
        assertEquals(ExecutionType.TradeCancel,
                sFactory.createExecutionReport(msg, null,
                        Originator.Server, null, null).getExecutionType());

        msg.setField(new quickfix.field.ExecTransType(quickfix.field.ExecTransType.CORRECT));
        assertEquals(ExecutionType.TradeCorrect,
                sFactory.createExecutionReport(msg, null,
                        Originator.Server, null, null).getExecutionType());

        msg.setField(new quickfix.field.ExecTransType(quickfix.field.ExecTransType.STATUS));
        assertEquals(ExecutionType.OrderStatus,
                sFactory.createExecutionReport(msg, null,
                        Originator.Server, null, null).getExecutionType());
    }
    /**
     * Tests the exec type field.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void execTypeTest()
            throws Exception
    {
        quickfix.Message msg = createEmptyExecReport();
        msg.setField(new quickfix.field.ExecTransType(quickfix.field.ExecTransType.STATUS));
        assertNull(sFactory.createExecutionReport(msg,
                                                  null,
                                                  Originator.Broker,
                                                  null,
                                                  null).getExecutionType());
    }
}
