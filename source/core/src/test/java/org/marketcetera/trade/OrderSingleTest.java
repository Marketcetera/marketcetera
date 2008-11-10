package org.marketcetera.trade;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.core.MSymbol;
import org.marketcetera.quickfix.FIXMessageFactory;
import org.marketcetera.quickfix.FIXDataDictionary;
import org.marketcetera.module.ExpectedFailure;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.Map;
import java.util.HashMap;
import java.util.Date;
import java.math.BigDecimal;

import quickfix.Message;
import quickfix.FieldNotFound;
import quickfix.fix44.NewOrderSingle;
import quickfix.field.*;
import quickfix.field.converter.UtcTimestampConverter;
import quickfix.field.converter.BooleanConverter;

/* $License$ */
/**
 * Tests {@link OrderSingle}
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public class OrderSingleTest extends TypesTestBase {
    
    /**
     * Verifies default attributes of objects returned via
     * {@link org.marketcetera.trade.Factory#createOrderSingle()}
     */
    @Test
    public void pojoDefaults() {
        OrderSingle order = sFactory.createOrderSingle();
        assertOrderValues(order, null, null);
        assertOrderBaseValues(order, null, null, null, null, null, null);
        assertNROrderValues(order,null, null, null);
        assertNotSame(order, sFactory.createOrderSingle());
    }
    
    /**
     * Verifies setters of objects returned via 
     * {@link org.marketcetera.trade.Factory#createOrderSingle()}
     */
    @Test
    public void pojoSetters() {
        OrderSingle order = sFactory.createOrderSingle();
        checkSetters(order);
    }

    /**
     * Verifies conversion of System FIX message to OrderSingle via
     * {@link Factory#createOrderSingle(quickfix.Message, DestinationID)}
     *
     * @throws Exception if there were errors.
     */
    @Test
    public void systemFIXWrap() throws Exception {
        FIXMessageFactory factory = getSystemMessageFactory();

        //an order with no fields set
        Message msg = factory.newBasicOrder();
        OrderSingle order = sFactory.createOrderSingle(msg, null);
        assertOrderValues(order, null, null);
        assertOrderBaseValues(order, null, null, null, null, null, null);
        assertNROrderValues(order, null, null, null);
        checkSetters(order);

        //A limit order with all the fields set.
        DestinationID destinationID = new DestinationID("meh");
        OrderID orderID = new OrderID("testOrderID");
        BigDecimal qty = new BigDecimal("23434.56989");
        BigDecimal price = new BigDecimal("98923.2345");
        MSymbol symbol = new MSymbol("IBM",SecurityType.CommonStock);
        String account = "walloween";
        msg = factory.newLimitOrder(orderID.getValue(),
                Side.Buy.getFIXValue(), qty, symbol, price,
                TimeInForce.AtTheClose.getFIXValue(), account);
        order = sFactory.createOrderSingle(msg, destinationID);
        assertOrderValues(order, destinationID, SecurityType.CommonStock);
        assertOrderBaseValues(order, orderID, account, null, qty, Side.Buy, symbol);
        assertNROrderValues(order, OrderType.Limit, price, TimeInForce.AtTheClose);
        checkSetters(order);

        //A market order with all fields set.
        msg = factory.newMarketOrder(orderID.getValue(),
                Side.Sell.getFIXValue(), qty, symbol,
                TimeInForce.FillOrKill.getFIXValue(), account);
        order = sFactory.createOrderSingle(msg, null);
        assertOrderValues(order, null, SecurityType.CommonStock);
        assertOrderBaseValues(order, orderID, account, null, qty, Side.Sell, symbol);
        assertNROrderValues(order, OrderType.Market, null, TimeInForce.FillOrKill);
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

        order = sFactory.createOrderSingle(msg, destinationID);
        
        assertOrderValues(order, destinationID, SecurityType.CommonStock);
        assertOrderBaseValues(order, orderID, account, expectedMap, qty, Side.Sell, symbol);
        assertNROrderValues(order, OrderType.Market, null, TimeInForce.FillOrKill);
        
        assertNotSame(order, sFactory.createOrderSingle(msg, destinationID));
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
                sFactory.createOrderSingle(null, destinationID);
            }
        };

        //Non System FIX Versions.
        FIXMessageFactory factory = getSystemMessageFactory();
        final Message msg = factory.newBasicOrder();
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
                    sFactory.createOrderSingle(msg,null);
                }
            };
        }

        //No version field
        msg.getHeader().removeField(BeginString.FIELD);
        MessageCreationException exception =
                new ExpectedFailure<MessageCreationException>(
                Messages.SYSTEM_FIX_MESSAGE_NO_BEGIN_STRING, msg.toString()) {
            @Override
            protected void run() throws Exception {
                sFactory.createOrderSingle(msg, null);
            }
        }.getException();
        assertNotNull(exception.getCause());
        assertTrue(exception.getCause() instanceof FieldNotFound);

        //Has groups
        msg.getHeader().setField(new BeginString(
                FIXDataDictionary.FIX_SYSTEM_BEGIN_STRING));
        NewOrderSingle.NoAllocs group = new NewOrderSingle.NoAllocs();
        group.setField(new AllocAccount("blah"));
        msg.addGroup(group);
        new ExpectedFailure<MessageCreationException>(
                Messages.MESSAGE_HAS_GROUPS, group.getFieldTag(),
                msg.toString()){
            @Override
            protected void run() throws Exception {
                sFactory.createOrderSingle(msg,null);
            }
        };

        //Incorrect message type
        final Message incorrectType = factory.newCancelReplacePrice(
                "i1", "i2", new BigDecimal("52.14"));
        new ExpectedFailure<MessageCreationException>(
                Messages.NOT_SINGLE_ORDER, incorrectType.toString()){
            @Override
            protected void run() throws Exception {
                sFactory.createOrderSingle(incorrectType, null);
            }
        };
    }

    private void checkSetters(OrderSingle inOrder) {
        checkOrderSetters(inOrder);
        checkOrderBaseSetters(inOrder);
        checkNRSetters(inOrder);
    }
}
