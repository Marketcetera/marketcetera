package org.marketcetera.core.trade;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.marketcetera.core.ExpectedFailure;
import quickfix.Message;
import quickfix.field.*;
import quickfix.field.converter.DecimalConverter;
import quickfix.field.converter.UtcTimestampConverter;

import static org.junit.Assert.*;

/* $License$ */
/**
 * Tests {@link ExecutionReport}
 *
 * @author anshul@marketcetera.com
 * @version $Id: ExecutionReportTest.java 16063 2012-01-31 18:21:55Z colin $
 * @since 1.0.0
 */
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
        final Message execReport = createEmptyExecReport();
        new ExpectedFailure<NullPointerException>(){
             protected void run() throws Exception {
                 sFactory.createExecutionReport
                     (execReport, cID, null, null, null);
             }
         };
        // wrong quickfix message type
        final Message message = getSystemMessageFactory().newBasicOrder();
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
        Message msg = createEmptyExecReport();
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
        org.marketcetera.core.trade.OrderID orderID = new org.marketcetera.core.trade.OrderID("or2");
        String destOrderID = "brokOrd1";
        OrderStatus orderStatus = OrderStatus.PartiallyFilled;
        String execID = "kj3";
        org.marketcetera.core.trade.Side side = org.marketcetera.core.trade.Side.Buy;
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
        org.marketcetera.core.trade.OrderID origOrderID = new org.marketcetera.core.trade.OrderID("or1");
        ExecutionType execType = ExecutionType.PendingNew;
        String lastMarket = "XDES";
        BigDecimal leavesQty = new BigDecimal("343.53");
        OrderType orderType = OrderType.Limit;
        Date sendingTime = new Date();
        org.marketcetera.core.trade.TimeInForce timeInForce = org.marketcetera.core.trade.TimeInForce.Day;
        Date transactTime = new Date();
        text = "show me the money";
        msg.setField(new OrigClOrdID(origOrderID.getValue()));
        msg.setField(new ExecType(execType.getFIXValue()));
        msg.setField(new LastMkt(lastMarket));
        msg.setField(new LeavesQty(leavesQty));
        msg.setField(new OrdType(orderType.getFIXValue()));
        msg.getHeader().setField(new SendingTime(sendingTime));
        msg.setField(new quickfix.field.TimeInForce(timeInForce.getFIXValue()));
        msg.setField(new TransactTime(transactTime));
        msg.setField(new Text(text));
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
                org.marketcetera.core.trade.OrderCapacity.Proprietary, org.marketcetera.core.trade.PositionEffect.Close, true);
        assertNull(report.getReportID());
        //Verify toString() doesn't fail.
        report.toString();
        //validate orderprice is equal to getprices
        assertEquals(orderPrice , report.getPrice());
        //Verify the map
        Map<Integer,String> expected = new HashMap<Integer, String>();
        expected.put(Account.FIELD, account);
        expected.put(LastShares.FIELD, DecimalConverter.convert(lastShares));
        expected.put(OrderQty.FIELD, DecimalConverter.convert(orderQty));
        expected.put(LastShares.FIELD, DecimalConverter.convert(lastShares));
        expected.put(OrdStatus.FIELD,
                String.valueOf(orderStatus.getFIXValue()));
        expected.put(AvgPx.FIELD, DecimalConverter.convert(avgPrice));
        expected.put(quickfix.field.OrderID.FIELD, destOrderID);
        expected.put(OrdType.FIELD, String.valueOf(orderType.getFIXValue()));
        expected.put(ClOrdID.FIELD, orderID.getValue());
        expected.put(OrigClOrdID.FIELD, origOrderID.getValue());
        expected.put(quickfix.field.SecurityType.FIELD,
                instrument.getSecurityType().getFIXValue());
        expected.put(Price.FIELD, DecimalConverter.convert(orderPrice));
        expected.put(CumQty.FIELD, DecimalConverter.convert(cumQty));
        expected.put(ExecID.FIELD, execID);
        expected.put(Symbol.FIELD, instrument.getSymbol());
        expected.put(quickfix.field.Side.FIELD,
                String.valueOf(side.getFIXValue()));
        expected.put(quickfix.field.TimeInForce.FIELD,
                String.valueOf(timeInForce.getFIXValue()));
        expected.put(Text.FIELD, text);
        expected.put(ExecType.FIELD, String.valueOf(execType.getFIXValue()));
        expected.put(LastPx.FIELD, DecimalConverter.convert(lastPrice));
        expected.put(LeavesQty.FIELD, DecimalConverter.convert(leavesQty));
        expected.put(TransactTime.FIELD, UtcTimestampConverter.convert(
                transactTime, true));
        expected.put(LastMkt.FIELD, lastMarket);
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
        Message msg = createEmptyExecReport();
        assertEquals(null, sFactory.createExecutionReport(msg,
                null, Originator.Server, null, null).getExecutionType());

        msg.setField(new ExecTransType(ExecTransType.NEW));
        assertEquals(ExecutionType.New,
                sFactory.createExecutionReport(msg, null,
                        Originator.Server, null, null).getExecutionType());

        msg.setField(new ExecTransType(ExecTransType.CANCEL));
        assertEquals(ExecutionType.TradeCancel,
                sFactory.createExecutionReport(msg, null,
                        Originator.Server, null, null).getExecutionType());

        msg.setField(new ExecTransType(ExecTransType.CORRECT));
        assertEquals(ExecutionType.TradeCorrect,
                sFactory.createExecutionReport(msg, null,
                        Originator.Server, null, null).getExecutionType());

        msg.setField(new ExecTransType(ExecTransType.STATUS));
        assertEquals(ExecutionType.OrderStatus,
                sFactory.createExecutionReport(msg, null,
                        Originator.Server, null, null).getExecutionType());
    }
}
