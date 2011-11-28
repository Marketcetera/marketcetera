package org.marketcetera.trade;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;
import org.marketcetera.event.HasFIXMessage;
import org.marketcetera.module.ExpectedFailure;
import org.marketcetera.quickfix.*;
import org.marketcetera.util.misc.ClassVersion;

import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.field.*;
import quickfix.field.converter.BooleanConverter;
import quickfix.field.converter.UtcTimestampConverter;
import quickfix.fix44.OrderCancelReplaceRequest;

/* $License$ */
/**
 * Tests {@link org.marketcetera.trade.OrderReplace}
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public class OrderReplaceTest extends TypesTestBase {
    /**
     * Verifies default attributes of objects returned via
     * {@link Factory#createOrderReplace(ExecutionReport)}
     *
     * @throws Exception if there were errors
     */
    @Test
    public void pojoDefaults() throws Exception {
        //Null report parameter defaults.
        OrderReplace order = sFactory.createOrderReplace(null);
        assertOrderReplace(order, NOT_NULL, null, null, null, null, null,
                null, null, null, null, null, null, null, null, null, null);
        //Verify toString() doesn't fail
        order.toString();

        //Test an empty report.
        Message report = createEmptyExecReport();
        order = sFactory.createOrderReplace(
                sFactory.createExecutionReport
                (report, null, Originator.Server, null, null));
        assertOrderReplace(order, NOT_NULL, null, null, null, null, null,
                null, null, null, null, null, null, null, null, null, null);
        //Verify toString() doesn't fail
        order.toString();

        //Test a report with all values in it
        String orderID = "clorder-2";
        String destOrderID = "brokerd1";
        Side side = Side.Buy;
        BigDecimal orderQty = new BigDecimal("100");
        BigDecimal price = new BigDecimal("67.45");
        BigDecimal lastPrice = new BigDecimal("23.43");
        Instrument instrument = new Equity("IBM");
        String account = "what?";
        String text = "shaddup";
        OrderType orderType = OrderType.Limit;
        TimeInForce fillOrKill = TimeInForce.FillOrKill;
        BrokerID cID = new BrokerID("iam");
        //Create an exec report.
        report = createExecReport(orderID, side, orderQty, price,
                lastPrice, instrument, account, text, orderType, fillOrKill,
                destOrderID, OrderCapacity.Agency, PositionEffect.Open);
        //Create the order from the report.
        order = sFactory.createOrderReplace(
                sFactory.createExecutionReport
                (report, cID, Originator.Server, null, null));
        assertOrderReplace(order, NOT_NULL, new OrderID(orderID),
                destOrderID, OrderType.Limit, side,
                orderQty, price,
                instrument, instrument.getSecurityType(), fillOrKill, account, text,
                cID, PositionEffect.Open, OrderCapacity.Agency, null);
        //Verify toString() doesn't fail
        order.toString();
        
        assertNotSame(order, sFactory.createOrderReplace(
                sFactory.createExecutionReport
                (report, cID, Originator.Server, null, null)));
        
        //Test a replace for a partial fill
        //Create an exec report.
        report = createExecReport(orderID, side, orderQty, price,
                lastPrice, instrument, account, text, orderType, fillOrKill,
                destOrderID, OrderCapacity.Agency, PositionEffect.Open);
        report.setDecimal(AvgPx.FIELD, new BigDecimal("23.2"));
        report.setDecimal(CumQty.FIELD, new BigDecimal("10"));
        report.setDecimal(LeavesQty.FIELD, new BigDecimal("9"));
        report.setField(new OrdStatus(OrdStatus.PARTIALLY_FILLED));
        
        //Create the order from the report.
        order = sFactory.createOrderReplace(
                sFactory.createExecutionReport
                (report, cID, Originator.Server, null, null));
        assertOrderReplace(order, NOT_NULL, new OrderID(orderID),
                destOrderID, OrderType.Limit, side,
                orderQty, price,
                instrument, instrument.getSecurityType(), fillOrKill, account, text,
                cID, PositionEffect.Open, OrderCapacity.Agency, null);
        //Verify toString() doesn't fail
        order.toString();
        
        assertNotSame(order, sFactory.createOrderReplace(
                sFactory.createExecutionReport
                (report, cID, Originator.Server, null, null)));
    }

    /**
     * Verifies setters of objects returned via
     * {@link Factory#createOrderReplace(ExecutionReport)}
     *
     * @throws Exception if there were errors
     */
    @Test
    public void pojoSetters()throws Exception {
        //Test with non-null exec report
        OrderReplace order = sFactory.createOrderReplace(
                sFactory.createExecutionReport
                (createEmptyExecReport(), null, Originator.Server, null, null));
        checkSetters(order);

        //Test with null exec report
        order = sFactory.createOrderReplace(null);
        checkSetters(order);
    }

    /**
     * Verifies conversion of System FIX message to OrderReplace via
     * {@link org.marketcetera.trade.Factory#createOrderReplace(quickfix.Message, BrokerID)}
     *
     * @throws Exception if there were errors.
     */
    @Test
    public void systemFIXWrap() throws Exception {
        FIXMessageFactory factory = getSystemMessageFactory();
        //An order with no fields set
        Message msg = factory.getUnderlyingMessageFactory().create(
                factory.getBeginString(), MsgType.ORDER_CANCEL_REPLACE_REQUEST);
        OrderReplace order = sFactory.createOrderReplace(msg, null);
        assertOrderValues(order, null, null);
        OrderID expectedOrderID = NOT_NULL;
        assertOrderBaseValues(order, expectedOrderID, null, null, null, null, null, null);
        assertNROrderValues(order, null, null, null, null, null);
        assertRelatedOrderValues(order, null, null);
        //Verify toString() doesn't fail
        order.toString();
        checkSetters(order);

        //A limit order with all the fields set.
        BrokerID brokerID = new BrokerID("meh");
        OrderID orderID = new OrderID("testOrderID");
        String destOrderID = "brokerd1";
        BigDecimal qty = new BigDecimal("23434.56989");
        BigDecimal price = new BigDecimal("98923.2345");
        SecurityType securityType = SecurityType.CommonStock;
        Instrument instrument = new Equity("IBM");
        String account = "nonplus";
        String text = "prominus";
        PositionEffect positionEffect = PositionEffect.Close;
        msg = factory.newCancelReplaceFromMessage(createExecReport(
                orderID.getValue(), Side.Buy, qty, new BigDecimal("45.67"), price, instrument,
                account, text, OrderType.Limit, TimeInForce.AtTheClose,
                destOrderID, OrderCapacity.Individual, positionEffect));
        order = sFactory.createOrderReplace(msg, brokerID);
        assertOrderReplace(order, expectedOrderID, orderID, destOrderID,
                OrderType.Limit, Side.Buy, qty,
                msg.getField(new Price()).getValue(), instrument, securityType,
                TimeInForce.AtTheClose, account, text, brokerID, positionEffect,
                OrderCapacity.Individual, null);
        //Verify toString() doesn't fail
        order.toString();
        checkSetters(order);

        //A market order with all fields set.
        Side side = Side.Sell;
        OrderType orderType = OrderType.Market;
        TimeInForce tif = TimeInForce.FillOrKill;
        OrderCapacity orderCapacity = OrderCapacity.Proprietary;
        msg = factory.newCancelReplaceFromMessage(createExecReport(
                orderID.getValue(), side, qty, new BigDecimal("45.67"), null, instrument,
                account, text, orderType, tif,
                destOrderID, orderCapacity, positionEffect));
        order = sFactory.createOrderReplace(msg, null);
        assertOrderReplace(order, expectedOrderID, orderID, destOrderID,
                orderType, side, qty, msg.getField(new Price()).getValue(),
                instrument, securityType, tif, account, text, null, positionEffect,
                orderCapacity, null);
        //Verify toString() doesn't fail
        order.toString();
        checkSetters(order);

        //Check custom fields
        //Set fields of every type.
        Map<String,String> expectedMap = new HashMap<String, String>();
        Date date = new Date();
        BigDecimal bigDecimal = new BigDecimal("35234.35989");
        char charValue = '0';
        int intValue = 1;
        boolean boolValue = false;

        msg.setField(new ExpireTime(date));
        expectedMap.put(String.valueOf(ExpireTime.FIELD),
                UtcTimestampConverter.convert(date, true));

        msg.setField(new AllocQty(bigDecimal));
        expectedMap.put(String.valueOf(AllocQty.FIELD), bigDecimal.toString());

        msg.setField(new SettlType(charValue));
        expectedMap.put(String.valueOf(SettlType.FIELD),
                String.valueOf(charValue));

        msg.setField(new Product(intValue));
        expectedMap.put(String.valueOf(Product.FIELD), String.valueOf(intValue));

        msg.setField(new SolicitedFlag(boolValue));
        expectedMap.put(String.valueOf(SolicitedFlag.FIELD),
                BooleanConverter.convert(boolValue));

        order = sFactory.createOrderReplace(msg, brokerID);
        BigDecimal expectedPrice = msg.getField(new Price()).getValue();
        assertOrderReplace(order, expectedOrderID, orderID, destOrderID,
                orderType, side, qty, expectedPrice, instrument, securityType,
                tif, account, text, brokerID, positionEffect, orderCapacity,
                expectedMap);
    }

    /**
     * Verifies failures when wrapping a FIX message in an order.
     *
     * @throws Exception if there were errors.
     */
    @Test
    public void systemFIXWrapFailures() throws Exception {
        final BrokerID brokerID = new BrokerID("meh");
        //Null check for message parameter
        new ExpectedFailure<NullPointerException>() {
            protected void run() throws Exception {
                sFactory.createOrderReplace(null, brokerID);
            }
        };
        final Message msg = createEmptyExecReport();

        //Non System FIX Versions.
        for (String string: new String[]{
                FIXDataDictionary.FIX_4_0_BEGIN_STRING,
                FIXDataDictionary.FIX_4_1_BEGIN_STRING,
                FIXDataDictionary.FIX_4_2_BEGIN_STRING,
                FIXDataDictionary.FIX_4_3_BEGIN_STRING,
                FIXDataDictionary.FIX_4_4_BEGIN_STRING}) {
            msg.getHeader().setField(new BeginString(string));
            new ExpectedFailure<MessageCreationException>(
                    Messages.NON_SYSTEM_FIX_MESSAGE, string){
                @Override
                protected void run() throws Exception {
                    sFactory.createOrderReplace(msg,null);
                }
            };
        }

        //No version field
        msg.getHeader().removeField(BeginString.FIELD);
        MessageCreationException exception =
                new ExpectedFailure<MessageCreationException>(
                Messages.SYSTEM_FIX_MESSAGE_NO_BEGIN_STRING, msg.toString()){
            @Override
            protected void run() throws Exception {
                sFactory.createOrderReplace(msg,null);
            }
        }.getException();
        assertNotNull(exception.getCause());
        assertTrue(exception.getCause() instanceof FieldNotFound);

        //Has groups
        msg.getHeader().setField(new BeginString(
                FIXDataDictionary.FIX_SYSTEM_BEGIN_STRING));
        OrderCancelReplaceRequest.NoAllocs group =
                new OrderCancelReplaceRequest.NoAllocs();
        group.setField(new AllocAccount("blah"));
        msg.addGroup(group);
        new ExpectedFailure<MessageCreationException>(
                Messages.MESSAGE_HAS_GROUPS, group.getFieldTag(),
                msg.toString()){
            @Override
            protected void run() throws Exception {
                sFactory.createOrderReplace(msg,null);
            }
        };

        //Incorrect message type
        final Message incorrectType = getSystemMessageFactory().newBasicOrder();
        new ExpectedFailure<MessageCreationException>(
                Messages.NOT_CANCEL_REPLACE_ORDER, incorrectType.toString()){
            @Override
            protected void run() throws Exception {
                sFactory.createOrderReplace(incorrectType, null);
            }
        };
    }
    
    /**
     * Set the current data dictionary so that various FIXMessageFactory
     * methods work.
     *
     * @throws Exception if there were errors.
     */
    @BeforeClass
    public static void setupCurrentFIXDictionary() throws Exception {
        FIXDataDictionaryManager.initialize(FIXVersion.FIX44,
                FIXVersion.FIX44.getDataDictionaryURL());
        CurrentFIXDataDictionary.setCurrentFIXDataDictionary(
                FIXDataDictionaryManager.getFIXDataDictionary(FIXVersion.FIX44));
    }

    private Message createExecReport(String inOrderID,
                                     Side inSide,
                                     BigDecimal inQty,
                                     BigDecimal inPrice,
                                     BigDecimal inLastPrice,
                                     Instrument inInstrument,
                                     String inAccount,
                                     String inText,
                                     OrderType inOrderType,
                                     TimeInForce inFillOrKill,
                                     String inDestOrderID,
                                     OrderCapacity inOrderCapacity,
                                     PositionEffect inPositionEffect)
            throws FieldNotFound {
        Message report = getSystemMessageFactory().newExecutionReport(
                inDestOrderID,
                inOrderID, "exec-2", OrderStatus.New.getFIXValue(),
                inSide.getFIXValue(), inQty,
                inPrice, new BigDecimal("23.53"),
                inLastPrice, new BigDecimal("98.34"),
                new BigDecimal("34.32"), inInstrument, inAccount, inText);
        report.setField(new OrdType(inOrderType.getFIXValue()));
        report.setField(new quickfix.field.TimeInForce(
                inFillOrKill.getFIXValue()));
        report.setField(new quickfix.field.OrderCapacity(
                inOrderCapacity.getFIXValue()));
        report.setField(new quickfix.field.PositionEffect(
                inPositionEffect.getFIXValue()));
        return report;
    }

    private void checkSetters(OrderReplace inOrder) {
        checkOrderSetters(inOrder);
        checkOrderBaseSetters(inOrder);
        checkNRSetters(inOrder);
        checkRelatedOrderSetters(inOrder);
    }
    /** verify custom fields are preserved, and that nothing else is added from the execution report */
    @Test
    public void testSecurityExchangePreserved() throws Exception {
        Message erMsg = FIXVersion.FIX42.getMessageFactory().newExecutionReport("orderID", "clOrderID", "execID", //$NON-NLS-1$
                OrdStatus.NEW, Side.Buy.getFIXValue(), new BigDecimal("10"), new BigDecimal("100.23"),
                BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, new Equity("IBM"), //$NON-NLS-1$
                "accountName", null); //$NON-NLS-1$
        erMsg.setString(SecurityExchange.FIELD, "box");

        ExecutionReport er = sFactory.createExecutionReport(erMsg, null, Originator.Server, null, null);

        OrderReplace replace = sFactory.createOrderReplace(er);
        assertNotNull("didn't get custom fields", replace.getCustomFields());
        // basically, should only have 1 field: SecurityExchange
        assertEquals("has extra fields: "+ Arrays.toString(replace.getCustomFields().keySet().toArray()), 1, replace.getCustomFields().size());
    }

    @Test
    /** Create ER for previous order that has OrderID of 12345
     * Set an OrigClOrdID on it of 12222
     * Create a replace form this ER, the OrigClOrdID should be 12345, not the "very original" of 12222
     */
    public void testCreateOrderReplace() throws Exception {
        Message erMsg = FIXVersion.FIX42.getMessageFactory().newExecutionReport("7600", "12345", "execID", //$NON-NLS-1$
                OrdStatus.NEW, Side.Buy.getFIXValue(), new BigDecimal("10"), new BigDecimal("100.23"),
                BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, new Equity("IBM"), //$NON-NLS-1$
                "accountName", null); //$NON-NLS-1$
        erMsg.setString(OrigClOrdID.FIELD, "12222");
        erMsg.setInt(HandlInst.FIELD, HandlInst.AUTOMATED_EXECUTION_ORDER_PUBLIC);
        ExecutionReport er = Factory.getInstance().createExecutionReport(erMsg, new BrokerID("broker"), Originator.Server, new UserID(7600L), new UserID(7500L));
        assertEquals("12222", er.getOriginalOrderID().getValue());
        assertEquals("12222", ((HasFIXMessage) er).getMessage().getString(OrigClOrdID.FIELD));

        // now create a replace, shouldn't have the "very original" order id of 12222
        OrderReplace replace = Factory.getInstance().createOrderReplace(er);
        assertEquals("12345", replace.getOriginalOrderID().getValue());
        assertEquals("shouldn have only 1 custom fields", 1, replace.getCustomFields().size());
        assertNotNull("HandlInst not set", replace.getCustomFields().get(HandlInst.FIELD+""));
    }

}
