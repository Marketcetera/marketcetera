package org.marketcetera.trade;

import java.math.BigDecimal;
import java.util.HashMap;
import org.junit.Before;
import org.junit.Test;
import org.marketcetera.quickfix.CurrentFIXDataDictionary;
import org.marketcetera.util.except.I18NException;
import org.marketcetera.util.log.I18NBoundMessage1P;
import org.marketcetera.util.log.I18NBoundMessage;
import org.marketcetera.util.misc.ClassVersion;
import quickfix.DataDictionary;
import quickfix.Message;
import quickfix.field.*;

import static org.junit.Assert.*;

/**
 * Tests {@link FIXConverter}.
 *
 * @author tlerios@marketcetera.com
 * @since 2.0.0
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$")
public class FIXConverterTest
    extends TypesTestBase
{

    /**
     * The FIX agnostic classes supported by the converter.
     */

    private static enum OrderClass
    {
        OrderSingle,OrderCancel,OrderReplace
    }

    /**
     * A data dictionary that does not support any fields except symbol.
     */

    private static class SymbolOnlyDictionary
        extends DataDictionary
    {
        public static SymbolOnlyDictionary INSTANCE=
            new SymbolOnlyDictionary();

        public SymbolOnlyDictionary()
        {
            super(getSystemMessageDictionary().getDictionary());
        }

        @Override
        public boolean isMsgField(String msgType,int field)
        {
            //Make instrument supported
            return Symbol.FIELD==field;
        }
    }
    /**
     * A data dictionary that does not support any fields.
     */

    private static class EmptyDictionary
        extends DataDictionary
    {
        public static EmptyDictionary INSTANCE=
            new EmptyDictionary();

        public EmptyDictionary()
        {
            super(getSystemMessageDictionary().getDictionary());
        }

        @Override
        public boolean isMsgField(String msgType,int field)
        {
            return false;
        }
    }

    /**
     * Sets all the fields of an {@link OrderBase}, and its FIX
     * representation, to known test values.
     */

    private void addOrderBaseFields
        (OrderBase o,
         Message msg,
         boolean orderID,
         boolean instrument,
         boolean side,
         boolean quantity,
         boolean account,
         boolean customFields)
    {
        if (orderID) {
            o.setOrderID(new OrderID("1"));
            msg.setField
                (new quickfix.field.ClOrdID
                 ("1"));
        }
        if (instrument) {
            o.setInstrument(new Equity("IBM"));
            msg.setField
                (new quickfix.field.Symbol
                 ("IBM"));
            msg.setField
                (new quickfix.field.SecurityType
                 (SecurityType.CommonStock.getFIXValue()));
        }
        if (side) {
            o.setSide(Side.Buy);
            msg.setField
                (new quickfix.field.Side
                 (Side.Buy.getFIXValue()));
        }
        if (quantity) {
            o.setQuantity(BigDecimal.ONE);
            msg.setField
                (new quickfix.field.OrderQty
                 (BigDecimal.ONE));
        }
        if (account) {
            o.setAccount("ACT");
            msg.setField
                (new quickfix.field.Account
                 ("ACT"));
        }
        if (customFields) {
            HashMap<String,String> cf=new HashMap<String,String>();
            cf.put("342","b");
            o.setCustomFields(cf);
            msg.setString(342,"b");
        }
    }

    /**
     * Sets all the fields of a {@link NewOrReplaceOrder}, and its FIX
     * representation, to known test values.
     */

    private void addNewOrReplaceOrderFields
        (NewOrReplaceOrder o,
         Message msg,
         boolean orderType,
         boolean timeInForce,
         boolean positionEffect,
         boolean orderCapacity,
         boolean price,
         boolean displayQuantity)
    {
        if (orderType) {
            o.setOrderType(OrderType.Limit);
            msg.setField
                (new quickfix.field.OrdType
                 (OrderType.Limit.getFIXValue()));
        }
        if (timeInForce) {
            o.setTimeInForce(TimeInForce.Day);
            msg.setField
                (new quickfix.field.TimeInForce
                 (TimeInForce.Day.getFIXValue()));
        }
        if (positionEffect) {
            o.setPositionEffect(PositionEffect.Open);
            msg.setField
                (new quickfix.field.PositionEffect
                 (PositionEffect.Open.getFIXValue()));
        }
        if (orderCapacity) {
            o.setOrderCapacity(OrderCapacity.Individual);
            msg.setField
                (new quickfix.field.OrderCapacity
                 (OrderCapacity.Individual.getFIXValue()));
        }
        if (price) {
            o.setPrice(BigDecimal.ZERO);
            msg.setField
                (new quickfix.field.Price
                 (BigDecimal.ZERO));
        }
        if (displayQuantity) {
            o.setDisplayQuantity(BigDecimal.ZERO);
            msg.setField
                (new quickfix.field.MaxFloor
                 (BigDecimal.ZERO));
        }
    }

    /**
     * Sets all the fields of a {@link RelatedOrder}, and its FIX
     * representation, to known test values.
     */

    private void addRelatedOrderFields
        (RelatedOrder o,
         Message msg,
         boolean originalOrderID,
         boolean brokerOrderID)
    {
        if (originalOrderID) {
            o.setOriginalOrderID(new OrderID("2"));
            msg.setField
                (new quickfix.field.OrigClOrdID
                 ("2"));
        }
        if (brokerOrderID) {
            o.setBrokerOrderID("3");
            msg.setField
                (new quickfix.field.OrderID
                 ("3"));
        }
    }

    /**
     * Tests an order.
     */

    private void testOrder
        (OrderClass oClass,
         DataDictionary dataDictionary,
         boolean orderID,
         boolean originalOrderID,
         boolean brokerOrderID,
         boolean instrument,
         boolean side,
         boolean orderType,
         boolean quantity,
         boolean displayQuantity,
         boolean timeInForce,
         boolean account,
         boolean positionEffect,
         boolean orderCapacity,
         boolean price,
         boolean customFields,
         I18NBoundMessage expectedExceptionMsg)
    {

        // Create empty order, in both the FIX agnostic and FIX forms.

        Order o=null;
        Message expectedMsg=null;
        if (oClass==OrderClass.OrderSingle) {
            o=new OrderSingleImpl();
            expectedMsg=getSystemMessageFactory().newOrderEmpty();
        } else if (oClass==OrderClass.OrderCancel) {
            o=new OrderCancelImpl();
            expectedMsg=getSystemMessageFactory().newCancelEmpty();
        } else if (oClass==OrderClass.OrderReplace) {
            o=new OrderReplaceImpl();
            expectedMsg=getSystemMessageFactory().newCancelReplaceEmpty();
        }

        // Add known fields.

        if (o instanceof OrderBase) {
            addOrderBaseFields
                ((OrderBase)o,expectedMsg,orderID,instrument,side,
                 quantity,account,customFields);
        }
        if (o instanceof NewOrReplaceOrder) {
            addNewOrReplaceOrderFields
                ((NewOrReplaceOrder)o,expectedMsg,orderType,timeInForce,
                 positionEffect,orderCapacity,price,displayQuantity);
        }
        if (o instanceof RelatedOrder) {
            addRelatedOrderFields
                ((RelatedOrder)o,expectedMsg,originalOrderID,brokerOrderID);
        }

        // Finalize FIX order using augmenters.

        if (oClass==OrderClass.OrderSingle) {
            getSystemMessageFactory().getMsgAugmentor().
                newOrderSingleAugment(expectedMsg);
        } else if (oClass==OrderClass.OrderCancel) {
            getSystemMessageFactory().getMsgAugmentor().
                cancelRequestAugment(expectedMsg);
        } else if (oClass==OrderClass.OrderReplace) {
            getSystemMessageFactory().getMsgAugmentor().
                cancelReplaceRequestAugment(expectedMsg);
        }

        // Convert FIX agnostic order to its FIX version.

        Message actualMsg=null;
        I18NBoundMessage actualExceptionMsg=null;
        try {
            actualMsg=FIXConverter.toQMessage
                (getSystemMessageFactory(),dataDictionary,o);
        } catch (I18NException ex) {
            actualExceptionMsg=ex.getI18NBoundMessage();
        }

        // Compare expected to actual results.

        if (expectedExceptionMsg!=null) {
            assertEquals(expectedExceptionMsg,actualExceptionMsg);
        } else if (actualExceptionMsg!=null) {
            fail(actualExceptionMsg.toString());
        } else {
            assertEquals(expectedMsg.toString(),actualMsg.toString());
        }
    }

    /**
     * Shorthand for {@link OrderSingle} testing.
     */

    private void testOrderSingle
        (DataDictionary dataDictionary,
         boolean orderID,
         boolean instrument,
         boolean side,
         boolean orderType,
         boolean quantity,
         boolean displayQuantity,
         boolean timeInForce,
         boolean account,
         boolean positionEffect,
         boolean orderCapacity,
         boolean price,
         boolean customFields,
         I18NBoundMessage expectedExceptionMsg)
    {
        testOrder
            (OrderClass.OrderSingle,dataDictionary,orderID,
             false,false,instrument,side,
             orderType,quantity,displayQuantity,timeInForce,account,
             positionEffect,orderCapacity,price,customFields,
             expectedExceptionMsg);
    }

    /**
     * Shorthand for {@link OrderSingle} testing using the system
     * dictionary.
     */

    private void testOrderSingleSupported
        (boolean orderID,
         boolean instrument,
         boolean side,
         boolean orderType,
         boolean quantity,
         boolean displayQuantity,
         boolean timeInForce,
         boolean account,
         boolean positionEffect,
         boolean orderCapacity,
         boolean price,
         boolean customFields,
         I18NBoundMessage expectedExceptionMsg)
    {
        testOrderSingle
            (getSystemMessageDictionary().getDictionary(),orderID,
             instrument,side,orderType,quantity,displayQuantity,timeInForce,account,
             positionEffect,orderCapacity,price,customFields,
             expectedExceptionMsg);
    }

    /**
     * Shorthand for {@link OrderSingle} testing using the empty
     * dictionary.
     */

    private void testOrderSingleUnsupported
        (boolean orderID,
         boolean instrument,
         boolean side,
         boolean orderType,
         boolean quantity,
         boolean displayQuantity,
         boolean timeInForce,
         boolean account,
         boolean positionEffect,
         boolean orderCapacity,
         boolean price,
         boolean customFields,
         I18NBoundMessage expectedExceptionMsg)
    {
        testOrderSingle
            (SymbolOnlyDictionary.INSTANCE,orderID,
             instrument,side,orderType,quantity,displayQuantity,timeInForce,account,
             positionEffect,orderCapacity,price,customFields,
             expectedExceptionMsg);
    }

    /**
     * Shorthand for {@link OrderCancel} testing.
     */

    private void testOrderCancel
        (DataDictionary dataDictionary,
         boolean orderID,
         boolean originalOrderID,
         boolean brokerOrderID,
         boolean instrument,
         boolean side,
         boolean quantity,
         boolean displayQuantity,
         boolean account,
         boolean customFields,
         I18NBoundMessage expectedExceptionMsg)
    {
        testOrder
            (OrderClass.OrderCancel,dataDictionary,orderID,
             originalOrderID,brokerOrderID,instrument,side,
             false,quantity,displayQuantity,false,account,
             false,false,false,customFields,
             expectedExceptionMsg);
    }

    /**
     * Shorthand for {@link OrderCancel} testing using the system
     * dictionary.
     */

    private void testOrderCancelSupported
        (boolean orderID,
         boolean originalOrderID,
         boolean brokerOrderID,
         boolean instrument,
         boolean side,
         boolean quantity,
         boolean displayQuantity,
         boolean account,
         boolean customFields,
         I18NBoundMessage expectedExceptionMsg)
    {
        testOrderCancel
            (getSystemMessageDictionary().getDictionary(),orderID,
             originalOrderID,brokerOrderID,instrument,side,
             quantity,displayQuantity,account,customFields,
             expectedExceptionMsg);
    }

    /**
     * Shorthand for {@link OrderCancel} testing using the empty
     * dictionary.
     */

    private void testOrderCancelUnsupported
        (boolean orderID,
         boolean originalOrderID,
         boolean brokerOrderID,
         boolean instrument,
         boolean side,
         boolean quantity,
         boolean displayQuantity,
         boolean account,
         boolean customFields,
         I18NBoundMessage expectedExceptionMsg)
    {
        testOrderCancel
            (SymbolOnlyDictionary.INSTANCE,orderID,
             originalOrderID,brokerOrderID,instrument,side,
             quantity,displayQuantity,account,customFields,
             expectedExceptionMsg);
    }

    /**
     * Shorthand for {@link OrderReplace} testing.
     */

    private void testOrderReplace
        (DataDictionary dataDictionary,
         boolean orderID,
         boolean originalOrderID,
         boolean brokerOrderID,
         boolean instrument,
         boolean side,
         boolean orderType,
         boolean quantity,
         boolean displayQuantity,
         boolean timeInForce,
         boolean account,
         boolean positionEffect,
         boolean orderCapacity,
         boolean price,
         boolean customFields,
         I18NBoundMessage expectedExceptionMsg)
    {
        testOrder
            (OrderClass.OrderReplace,dataDictionary,orderID,
             originalOrderID,brokerOrderID,instrument,side,
             orderType,quantity,displayQuantity,timeInForce,account,
             positionEffect,orderCapacity,price,customFields,
             expectedExceptionMsg);
    }

    /**
     * Shorthand for {@link OrderReplace} testing using the system
     * dictionary.
     */

    private void testOrderReplaceSupported
        (boolean orderID,
         boolean originalOrderID,
         boolean brokerOrderID,
         boolean instrument,
         boolean side,
         boolean orderType,
         boolean quantity,
         boolean displayQuantity,
         boolean timeInForce,
         boolean account,
         boolean positionEffect,
         boolean orderCapacity,
         boolean price,
         boolean customFields,
         I18NBoundMessage expectedExceptionMsg)
    {
        testOrderReplace
            (getSystemMessageDictionary().getDictionary(),orderID,
             originalOrderID,brokerOrderID,instrument,side,
             orderType,quantity,displayQuantity,timeInForce,account,
             positionEffect,orderCapacity,price,customFields,
             expectedExceptionMsg);
    }

    /**
     * Shorthand for {@link OrderReplace} testing using the empty
     * dictionary.
     */

    private void testOrderReplaceUnsupported
        (boolean orderID,
         boolean originalOrderID,
         boolean brokerOrderID,
         boolean instrument,
         boolean side,
         boolean orderType,
         boolean quantity,
         boolean displayQuantity,
         boolean timeInForce,
         boolean account,
         boolean positionEffect,
         boolean orderCapacity,
         boolean price,
         boolean customFields,
         I18NBoundMessage expectedExceptionMsg)
    {
        testOrderReplace
            (SymbolOnlyDictionary.INSTANCE,orderID,
             originalOrderID,brokerOrderID,instrument,side,
             orderType,quantity,displayQuantity,timeInForce,account,
             positionEffect,orderCapacity,price,customFields,
             expectedExceptionMsg);
    }


    @Before
    public void setUpFIXConverterTest()
    {
        // Set current FIX dictionary (required by augmentors).

        CurrentFIXDataDictionary.setCurrentFIXDataDictionary
            (getSystemMessageDictionary());
    }


    @Test
    public void fromQMessage()
        throws Exception
    {
        BrokerID brokerID=new BrokerID("blah");
        UserID actorID=new UserID(2);
        UserID viewerID=new UserID(3);

        Message msg=createEmptyExecReport();
        assertExecReportEquals
            (Factory.getInstance().createExecutionReport
             (msg,brokerID,Originator.Broker,actorID,viewerID),
             (ExecutionReport)FIXConverter.fromQMessage
             (msg,Originator.Broker,brokerID,actorID,viewerID));

        msg=getSystemMessageFactory().newOrderCancelReject();
        assertCancelRejectEquals
            (Factory.getInstance().createOrderCancelReject
             (msg,brokerID,Originator.Broker,actorID,viewerID),
             (OrderCancelReject)FIXConverter.fromQMessage
             (msg,Originator.Broker,brokerID,actorID,viewerID));

        msg=getSystemMessageFactory().newBusinessMessageReject
            ("QQ",BusinessRejectReason.UNSUPPORTED_MESSAGE_TYPE,
             "Bad message type");
        assertFIXResponseEquals
            (Factory.getInstance().createFIXResponse
             (msg,brokerID,Originator.Server,actorID,viewerID),
             (FIXResponse)FIXConverter.fromQMessage
             (msg,Originator.Server,brokerID,actorID,viewerID));
    }

    @Test
    public void toQMessageOrderSingle()
        throws Exception
    {
        testOrderSingleSupported
            (true,true,true,true,true,true,true,true,true,true,true,true,
             null);
        testOrderSingleSupported
            (false,true,true,true,true,true,true,true,true,true,true,true,
             Messages.NO_ORDER_ID);
        testOrderSingleSupported
            (true,false,true,true,true,true,true,true,true,true,true,true,
             new I18NBoundMessage1P(Messages.NO_INSTRUMENT,null));
        testOrderSingleSupported
            (true,true,false,true,true,true,true,true,true,true,true,true,
             new I18NBoundMessage1P(Messages.NO_SIDE,null));
        testOrderSingleSupported
            (true,true,true,false,true,true,true,true,true,true,true,true,
             new I18NBoundMessage1P(Messages.NO_ORDER_TYPE,null));
        testOrderSingleSupported
            (true,true,true,true,true,true,true,true,true,true,false,true,
             Messages.NO_PRICE);
        testOrderSingleSupported
            (true,true,true,true,false,false,false,false,false,false,true,false,
             null);

        testOrderSingleUnsupported
            (true,false,false,false,false,false,false,false,false,false,false,false,
             Messages.UNSUPPORTED_ORDER_ID);
        testOrderSingle
            (EmptyDictionary.INSTANCE,false,true,false,false,false,false,false,false,false,false,false,false,
             Messages.UNSUPPORTED_INSTRUMENT);
        testOrderSingleUnsupported
            (false,true,true,false,false,false,false,false,false,false,false,false,
             Messages.UNSUPPORTED_SIDE);
        testOrderSingleUnsupported
            (false,true,false,true,false,false,false,false,false,false,false,false,
             Messages.UNSUPPORTED_ORDER_TYPE);
        testOrderSingleUnsupported
            (false,true,false,false,true,false,false,false,false,false,false,false,
             Messages.UNSUPPORTED_QUANTITY);
        testOrderSingleUnsupported
        	(false,true,false,false,false,true,false,false,false,false,false,false,
        			Messages.UNSUPPORTED_DISPLAY_QUANTITY);
        testOrderSingleUnsupported
            (false,true,false,false,false,false,true,false,false,false,false,false,
             Messages.UNSUPPORTED_TIME_IN_FORCE);
        testOrderSingleUnsupported
            (false,true,false,false,false,false,false,true,false,false,false,false,
             Messages.UNSUPPORTED_ACCOUNT);
        testOrderSingleUnsupported
            (false,true,false,false,false,false,false,false,true,false,false,false,
             Messages.UNSUPPORTED_POSITION_EFFECT);
        testOrderSingleUnsupported
            (false,true,false,false,false,false,false,false,false,true,false,false,
             Messages.UNSUPPORTED_ORDER_CAPACITY);
    }

    @Test
    public void toQMessageOrderCancel()
        throws Exception
    {
        testOrderCancelSupported
            (true,true,true,true,true,true,true,true,true,
             null);
        testOrderCancelSupported
            (false,true,true,true,true,true,true,true,true,
             Messages.NO_ORDER_ID);
        testOrderCancelSupported
            (true,false,true,true,true,true,true,true,true,
             Messages.NO_ORIGINAL_ORDER_ID);
        testOrderCancelSupported
            (true,true,true,false,true,true,true,true,true,
             new I18NBoundMessage1P(Messages.NO_INSTRUMENT,null));
        testOrderCancelSupported
            (true,true,true,true,false,true,true,true,true,
             new I18NBoundMessage1P(Messages.NO_SIDE,null));
        testOrderCancelSupported
            (true,true,false,true,true,false,false,false,false,
             null);

        testOrderCancelUnsupported
            (true,false,false,false,false,false,false,false,false,
             Messages.UNSUPPORTED_ORDER_ID);
        testOrderCancelUnsupported
            (false,true,false,false,false,false,false,false,false,
             Messages.UNSUPPORTED_ORIGINAL_ORDER_ID);
        testOrderCancelUnsupported
            (false,false,true,true,false,false,false,false,false,
             Messages.UNSUPPORTED_BROKER_ORDER_ID);
        testOrderCancel
            (EmptyDictionary.INSTANCE,false,false,false,true,false,false,false,false,false,
             Messages.UNSUPPORTED_INSTRUMENT);
        testOrderCancelUnsupported
            (false,false,false,true,true,false,false,false,false,
             Messages.UNSUPPORTED_SIDE);
        testOrderCancelUnsupported
            (false,false,false,true,false,true,false,false,false,
             Messages.UNSUPPORTED_QUANTITY);
        testOrderCancelUnsupported
            (false,false,false,true,false,false,false,true,false,
             Messages.UNSUPPORTED_ACCOUNT);
    }

    @Test
    public void toQMessageOrderReplace()
        throws Exception
    {
        testOrderReplaceSupported
            (true,true,true,true,true,true,true,true,true,true,true,true,true,true,
             null);
        testOrderReplaceSupported
            (false,true,true,true,true,true,true,true,true,true,true,true,true,true,
             Messages.NO_ORDER_ID);
        testOrderReplaceSupported
            (true,false,true,true,true,true,true,true,true,true,true,true,true,true,
             Messages.NO_ORIGINAL_ORDER_ID);
        testOrderReplaceSupported
            (true,true,true,false,true,true,true,true,true,true,true,true,true,true,
             new I18NBoundMessage1P(Messages.NO_INSTRUMENT,null));
        testOrderReplaceSupported
            (true,true,true,true,false,true,true,true,true,true,true,true,true,true,
             new I18NBoundMessage1P(Messages.NO_SIDE,null));
        testOrderReplaceSupported
            (true,true,true,true,true,false,true,true,true,true,true,true,true,true,
             new I18NBoundMessage1P(Messages.NO_ORDER_TYPE,null));
        testOrderReplaceSupported
            (true,true,false,true,true,true,false,false,false,false,false,false,
             false,false,
             null);

        testOrderReplaceUnsupported
            (true,false,false,false,false,false,false,false,false,false,false,false,
             false,false,
             Messages.UNSUPPORTED_ORDER_ID);
        testOrderReplaceUnsupported
            (false,true,false,false,false,false,false,false,false,false,false,false,
             false,false,
             Messages.UNSUPPORTED_ORIGINAL_ORDER_ID);
        testOrderReplaceUnsupported
            (false,false,true,true,false,false,false,false,false,false,false,false,
             false,false,
             Messages.UNSUPPORTED_BROKER_ORDER_ID);
        testOrderReplace
            (EmptyDictionary.INSTANCE,false,false,false,true,true,false,false,false,false,false,false,false,
             false,false,
             Messages.UNSUPPORTED_INSTRUMENT);
        testOrderReplaceUnsupported
            (false,false,false,true,true,true,false,false,false,false,false,false,
             false,false,
             Messages.UNSUPPORTED_SIDE);
        testOrderReplaceUnsupported
            (false,false,false,true,false,true,false,false,false,false,false,false,
             false,false,
             Messages.UNSUPPORTED_ORDER_TYPE);
        testOrderReplaceUnsupported
            (false,false,false,true,false,false,true,false,false,false,false,false,
             false,false,
             Messages.UNSUPPORTED_QUANTITY);
        testOrderReplaceUnsupported
        	(false,false,false,true,false,false,false,true,false,false,false,false,
        	false,false,
         Messages.UNSUPPORTED_DISPLAY_QUANTITY);
        testOrderReplaceUnsupported
            (false,false,false,true,false,false,false,false,true,false,false,false,
             false,false,
             Messages.UNSUPPORTED_TIME_IN_FORCE);
        testOrderReplaceUnsupported
            (false,false,false,true,false,false,false,false,false,true,false,false,
             false,false,
             Messages.UNSUPPORTED_ACCOUNT);
        testOrderReplaceUnsupported
            (false,false,false,true,false,false,false,false,false,false,true,false,
             false,false,
             Messages.UNSUPPORTED_POSITION_EFFECT);
        testOrderReplaceUnsupported
            (false,false,false,true,false,false,false,false,false,false,false,true,
             false,false,
             Messages.UNSUPPORTED_ORDER_CAPACITY);
    }

    @Test
    public void toQMessageFIXOrder()
        throws Exception
    {
        Message msgIn=createEmptyExecReport();
        Message msgOut=FIXConverter.toQMessage
            (getSystemMessageFactory(),
             getSystemMessageDictionary().getDictionary(),
             Factory.getInstance().createOrder(msgIn,new BrokerID("blah")));
        assertEquals(msgIn.toString(),msgOut.toString());
    }
}
