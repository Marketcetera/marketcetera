package org.marketcetera.trade;

import org.marketcetera.util.misc.ClassVersion;
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
import quickfix.fix44.OrderCancelRequest;
import quickfix.field.*;
import quickfix.field.converter.UtcTimestampConverter;
import quickfix.field.converter.BooleanConverter;

/* $License$ */
/**
 * Tests {@link OrderReplace}
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public class OrderCancelTest extends TypesTestBase {
    /**
     * Verifies default attributes of objects returned via
     * {@link org.marketcetera.trade.Factory#createOrderReplace(org.marketcetera.trade.ExecutionReport)}
     *
     * @throws Exception if there were errors
     */
    @Test
    public void pojoDefaults() throws Exception {
        //Null report parameter defaults.
        OrderCancel order = sFactory.createOrderCancel(null);
        assertOrderCancel(order, NOT_NULL, null, null, null,
                null, null, null, null, null, null);
        //Verify toString() doesn't fail
        order.toString();

        //Test an empty report.
        Message report = createEmptyExecReport();
        order = sFactory.createOrderCancel(
                sFactory.createExecutionReport
                (report, null, Originator.Server, null, null));
        assertOrderCancel(order, NOT_NULL, null, null, null,
                null, null, null, null, null, null);
        //Verify toString() doesn't fail
        order.toString();

        //Test a report with all values in it
        String orderID = "clorder-2";
        String destOrderID = "brokerder-2";
        Side side = Side.Buy;
        MSymbol symbol = new MSymbol("IBM",
                SecurityType.Option);
        String account = "what?";
        BigDecimal orderQty = new BigDecimal("34.5");
        BrokerID cID = new BrokerID("iam");
        //Create an exec report.
        report = createExecReport(orderID, side,
                symbol, account, destOrderID, orderQty);
        //Create the order from the report.
        order = sFactory.createOrderCancel(
                sFactory.createExecutionReport
                (report, cID, Originator.Server, null, null));
        assertOrderCancel(order, NOT_NULL, new OrderID(orderID), side,
                symbol, symbol.getSecurityType(),
                orderQty,
                destOrderID, account, cID, null);
        //Verify toString() doesn't fail
        order.toString();

        assertNotSame(order, sFactory.createOrderCancel(
                sFactory.createExecutionReport
                (report, cID, Originator.Server, null, null)));
        

        
        //Test a cancel for a partial fill
        //Create an exec report.
        report = createExecReport(orderID, side,
                symbol, account, destOrderID, orderQty);
        report.setDecimal(AvgPx.FIELD, new BigDecimal("23.2"));
        report.setDecimal(CumQty.FIELD, new BigDecimal("10"));
        report.setDecimal(LeavesQty.FIELD, new BigDecimal("9"));
        report.setField(new OrdStatus(OrdStatus.PARTIALLY_FILLED));
        
        //Create the order from the report.
        order = sFactory.createOrderCancel(
                sFactory.createExecutionReport
                (report, cID, Originator.Server, null, null));
        assertOrderCancel(order, NOT_NULL, new OrderID(orderID), side,
                symbol, symbol.getSecurityType(),
                orderQty,
                destOrderID, account, cID, null);
        //Verify toString() doesn't fail
        order.toString();

        assertNotSame(order, sFactory.createOrderCancel(
                sFactory.createExecutionReport
                (report, cID, Originator.Server, null, null)));
    }

    /**
     * Verifies setters of objects returned via
     * {@link org.marketcetera.trade.Factory#createOrderCancel(org.marketcetera.trade.ExecutionReport)}
     *
     * @throws Exception if there were errors
     */
    @Test
    public void pojoSetters()throws Exception {
        //Test with non-null exec report
        OrderCancel order = sFactory.createOrderCancel(
                sFactory.createExecutionReport
                (createEmptyExecReport(), null, Originator.Server, null, null));
        checkSetters(order);

        //Test with null exec report
        order = sFactory.createOrderCancel(null);
        checkSetters(order);
    }

    /**
     * Verifies conversion of System FIX message to OrderCancel via
     * {@link Factory#createOrderCancel(quickfix.Message, BrokerID)}
     *
     * @throws Exception if there were errors.
     */
    @Test
    public void systemFIXWrap() throws Exception {
        FIXMessageFactory factory = getSystemMessageFactory();
        //An order with no fields set
        Message msg = factory.getUnderlyingMessageFactory().create(
                factory.getBeginString(), MsgType.ORDER_CANCEL_REQUEST);
        OrderCancel order = sFactory.createOrderCancel(msg, null);
        assertOrderValues(order, null, null);
        OrderID expectedOrderID = NOT_NULL;
        assertOrderBaseValues(order, expectedOrderID, null, null, null, null, null);
        assertRelatedOrderValues(order, null, null);
        //Verify toString() doesn't fail
        order.toString();
        checkSetters(order);

        //An order with all fields set.
        BrokerID brokerID = new BrokerID("meh");
        String destOrderID = "bord1";
        String origOrderID = "testOrderID";
        BigDecimal qty = new BigDecimal("23434.56989");
        SecurityType securityType = SecurityType.CommonStock;
        MSymbol symbol = new MSymbol("IBM", securityType);
        String account = "nonplus";
        Side side = Side.Buy;
        msg = factory.newCancel("order",origOrderID,
                side.getFIXValue(), qty, symbol, null);
        msg.setField(new Account(account));
        msg.setField(new quickfix.field.OrderID(destOrderID));
        order = sFactory.createOrderCancel(msg, brokerID);
        assertOrderValues(order, brokerID, securityType);
        assertOrderBaseValues(order, expectedOrderID, account, null,
                qty, side, symbol);
        OrderID originalOrderID = new OrderID(origOrderID);
        assertRelatedOrderValues(order, originalOrderID, destOrderID);
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
        expectedMap.put(String.valueOf(Product.FIELD),
                String.valueOf(intValue));

        msg.setField(new SolicitedFlag(boolValue));
        expectedMap.put(String.valueOf(SolicitedFlag.FIELD),
                BooleanConverter.convert(boolValue));
        
        order = sFactory.createOrderCancel(msg, brokerID);

        assertOrderCancel(order, expectedOrderID, originalOrderID, side,
                symbol, securityType, qty, destOrderID, account,
                brokerID, expectedMap);

        assertNotSame(order, sFactory.createOrderCancel(msg, brokerID));
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
        new ExpectedFailure<NullPointerException>(null) {
            protected void run() throws Exception {
                sFactory.createOrderCancel(null, brokerID);
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
                    sFactory.createOrderCancel(msg,null);
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
                sFactory.createOrderCancel(msg,null);
            }
        }.getException();
        assertNotNull(exception.getCause());
        assertTrue(exception.getCause() instanceof FieldNotFound);

        //Has groups
        msg.getHeader().setField(new BeginString(FIXDataDictionary.FIX_SYSTEM_BEGIN_STRING));
        OrderCancelRequest.NoEvents group = new OrderCancelRequest.NoEvents();
        group.set(new EventDate());
        msg.addGroup(group);
        new ExpectedFailure<MessageCreationException>(
                Messages.MESSAGE_HAS_GROUPS, group.getFieldTag(),
                msg.toString()){
            @Override
            protected void run() throws Exception {
                sFactory.createOrderCancel(msg,null);
            }
        };
        
        //Incorrect message type
        final Message incorrectType = getSystemMessageFactory().newBasicOrder();
        new ExpectedFailure<MessageCreationException>(
                Messages.NOT_CANCEL_ORDER, incorrectType.toString()){
            @Override
            protected void run() throws Exception {
                sFactory.createOrderCancel(incorrectType, null);
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
                                     MSymbol inSymbol,
                                     String inAccount,
                                     String inDestOrderID,
                                     BigDecimal inQty)
            throws FieldNotFound {
        return getSystemMessageFactory().newExecutionReport(
                inDestOrderID, inOrderID,
                "exec-2", OrderStatus.New.getFIXValue(),
                inSide.getFIXValue(), inQty,
                new BigDecimal("45.67"), new BigDecimal("23.53"),
                new BigDecimal("983.43"), new BigDecimal("98.34"),
                new BigDecimal("34.32"), inSymbol, inAccount);
    }

    private void checkSetters(OrderCancel inOrder) {
        checkOrderSetters(inOrder);
        checkOrderBaseSetters(inOrder);
        checkRelatedOrderSetters(inOrder);
    }
}
