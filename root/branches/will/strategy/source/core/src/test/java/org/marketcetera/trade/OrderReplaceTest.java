package org.marketcetera.trade;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.core.MSymbol;
import org.marketcetera.quickfix.*;
import org.marketcetera.module.ExpectedFailure;
import org.junit.Test;
import org.junit.BeforeClass;
import static org.junit.Assert.*;

import java.util.Map;
import java.util.HashMap;
import java.util.Date;
import java.math.BigDecimal;

import quickfix.Message;
import quickfix.FieldNotFound;
import quickfix.fix44.OrderCancelReplaceRequest;
import quickfix.field.*;
import quickfix.field.converter.UtcTimestampConverter;
import quickfix.field.converter.BooleanConverter;

/* $License$ */
/**
 * Tests {@link org.marketcetera.trade.OrderReplace}
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since $Release$
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
        assertOrderValues(order, null, null);
        assertOrderBaseValues(order, null, null, null, null, null, null);
        assertNROrderValues(order, null, null, null);
        assertRelatedOrderValues(order,null);

        //Test an empty report.
        Message report = createEmptyExecReport();
        order = sFactory.createOrderReplace(
                sFactory.createExecutionReport(report, null));
        assertOrderValues(order, null, null);
        assertOrderBaseValues(order, null, null, null, null, null, null);
        assertNROrderValues(order, null, null, null);
        assertRelatedOrderValues(order,null);

        //Test a report with all values in it
        String orderID = "clorder-2";
        Side side = Side.Buy;
        BigDecimal orderQty = new BigDecimal("100");
        BigDecimal lastPrice = new BigDecimal("23.43");
        MSymbol symbol = new MSymbol("IBM",
                SecurityType.Option);
        String account = "what?";
        OrderType orderType = OrderType.Limit;
        TimeInForce fillOrKill = TimeInForce.FillOrKill;
        DestinationID cID = new DestinationID("iam");
        //Create an exec report.
        report = createExecReport(orderID, side, orderQty, lastPrice,
                symbol, account, orderType, fillOrKill);
        //Create the order from the report.
        order = sFactory.createOrderReplace(
                sFactory.createExecutionReport(report, cID));
        assertOrderValues(order, cID, symbol.getSecurityType());
        assertOrderBaseValues(order, null, account, null,
                report.getField(new LeavesQty()).getValue(), side, symbol);
        assertNROrderValues(order, OrderType.Limit, lastPrice, fillOrKill);
        assertRelatedOrderValues(order,new OrderID(orderID));
        
        assertNotSame(order, sFactory.createOrderReplace(
                sFactory.createExecutionReport(report, cID)));
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
                sFactory.createExecutionReport(createEmptyExecReport(), null));
        checkSetters(order);

        //Test with null exec report
        order = sFactory.createOrderReplace(null);
        checkSetters(order);
    }

    /**
     * Verifies conversion of System FIX message to OrderReplace via
     * {@link org.marketcetera.trade.Factory#createOrderReplace(quickfix.Message, DestinationID)}
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
        assertOrderBaseValues(order, null, null, null, null, null, null);
        assertNROrderValues(order, null, null, null);
        assertRelatedOrderValues(order, null);
        checkSetters(order);

        //A limit order with all the fields set.
        DestinationID destinationID = new DestinationID("meh");
        OrderID orderID = new OrderID("testOrderID");
        BigDecimal qty = new BigDecimal("23434.56989");
        BigDecimal price = new BigDecimal("98923.2345");
        MSymbol symbol = new MSymbol("IBM",SecurityType.CommonStock);
        String account = "nonplus";
        msg = factory.newCancelReplaceFromMessage(createExecReport(
                orderID.getValue(), Side.Buy, qty, price, symbol, account,
                OrderType.Limit, TimeInForce.AtTheClose));
        order = sFactory.createOrderReplace(msg, destinationID);
        assertOrderValues(order, destinationID, SecurityType.CommonStock);
        assertOrderBaseValues(order, orderID, account, null,
                qty, Side.Buy, symbol);
        assertNROrderValues(order, OrderType.Limit,
                msg.getField(new Price()).getValue(), TimeInForce.AtTheClose);
        assertRelatedOrderValues(order, orderID);
        checkSetters(order);

        //A market order with all fields set.
        msg = factory.newCancelReplaceFromMessage(createExecReport(
                orderID.getValue(), Side.Sell, qty, null, symbol, account,
                OrderType.Market, TimeInForce.FillOrKill));
        order = sFactory.createOrderReplace(msg, null);
        assertOrderValues(order, null, SecurityType.CommonStock);
        assertOrderBaseValues(order, orderID, account, null,
                qty, Side.Sell, symbol);
        assertNROrderValues(order, OrderType.Market,
                //Price is copied even though it's a market order
                //No validation is carried out. If the execution report
                //has the value it's copied over.
                msg.getField(new Price()).getValue(),
                TimeInForce.FillOrKill);
        assertRelatedOrderValues(order, orderID);
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

        order = sFactory.createOrderReplace(msg, destinationID);
        assertOrderValues(order, destinationID, SecurityType.CommonStock);
        assertOrderBaseValues(order, orderID, account, expectedMap, qty, Side.Sell, symbol);
        assertNROrderValues(order, OrderType.Market,
                msg.getField(new Price()).getValue(),
                TimeInForce.FillOrKill);
        assertRelatedOrderValues(order, orderID);
    }
    /**
     * Verifies failures when wrapping a FIX message in an order.
     *
     * @throws Exception if there were errors.
     */
    @Test
    public void systemFIXWrapFailures() throws Exception {
        final DestinationID destinationID = new DestinationID("meh");
        //Null check for message parameter
        new ExpectedFailure<NullPointerException>(null) {
            protected void run() throws Exception {
                sFactory.createOrderReplace(null, destinationID);
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
                                     BigDecimal inLastPrice,
                                     MSymbol inSymbol,
                                     String inAccount,
                                     OrderType inOrderType,
                                     TimeInForce inFillOrKill)
            throws FieldNotFound {
        Message report = getSystemMessageFactory().newExecutionReport(
                null,
                inOrderID, "exec-2", OrderStatus.New.getFIXValue(),
                inSide.getFIXValue(), inQty,
                new BigDecimal("45.67"), new BigDecimal("23.53"),
                inLastPrice, new BigDecimal("98.34"),
                new BigDecimal("34.32"), inSymbol, inAccount);
        report.setField(new OrdType(inOrderType.getFIXValue()));
        report.setField(new quickfix.field.TimeInForce(
                inFillOrKill.getFIXValue()));
        return report;
    }

    private void checkSetters(OrderReplace inOrder) {
        checkOrderSetters(inOrder);
        checkOrderBaseSetters(inOrder);
        checkNRSetters(inOrder);
        checkRelatedOrderSetters(inOrder);
    }
}