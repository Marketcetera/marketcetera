package org.marketcetera.ors;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Arrays;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.marketcetera.event.HasFIXMessage;
import org.marketcetera.trade.*;
import quickfix.Message;
import quickfix.field.ClOrdID;
import quickfix.field.MessageEncoding;
import quickfix.field.Text;

import static org.junit.Assert.*;
import static org.marketcetera.trade.TypesTestBase.*;
import org.marketcetera.core.instruments.InstrumentFromMessage;

/**
 * Tests sending of orders for various instrument types.
 *
 * @author tlerios@marketcetera.com
 * @since 2.0.0
 * @version $Id$
 */

/* $License$ */

@RunWith(Parameterized.class)
public class OrderSendingTest
    extends ORSTestBase
{
    public OrderSendingTest(Instrument inInstrument)
    {
        mInstrument = inInstrument;
    }

    /**
     * The test parameters that this test iterates through.
     *
     * @return the test parameters.
     */
    @Parameterized.Parameters
    public static Collection<Object[]> data()
    {
        return Arrays.asList(
                new Object[]{new Equity("sym")},
                new Object[]{new Option("sym","20101010",BigDecimal.TEN,OptionType.Put)},
                new Object[]{new Option("sym","20101010",BigDecimal.TEN,OptionType.Call)},
                new Object[]{new Option("sym","201010",BigDecimal.TEN,OptionType.Put)}
        );
    }

    @Test(timeout=300000)
    public void orderSending()
        throws Exception
    {
        ORSTestClient c=getAdminClient();

        // Compose order.
        Instrument instrument=getInstrument();
        OrderSingle order=Factory.getInstance().createOrderSingle();
        order.setOrderID(new OrderID("ID1"));
        order.setOrderType(OrderType.Limit);
        order.setQuantity(new BigDecimal("1"));
        order.setSide(Side.Buy);
        order.setInstrument(instrument);
        order.setPrice(BigDecimal.TEN);
        c.getClient().sendOrder(order);

        // Consume and test ORS ack.
        ExecutionReport er=(ExecutionReport)(c.getReportListener().getNext());
        assertExecReportValues(er,
                               null,
                               new BigDecimal("0"),
                               new BigDecimal("0"),
                               ExecutionType.PendingNew,
                               null,
                               new BigDecimal("0"),
                               new BigDecimal("0"),
                               new BigDecimal("1"),
                               new BigDecimal("1"),
                               OrderType.Limit,
                               Side.Buy,
                               instrument,
                               null,
                               null,
                               null,
                               true);
        Message msg=((HasFIXMessage)er).getMessage();
        // Test sending message modifiers.
        assertFalse(msg.isSetField(Text.FIELD));

        // Consume and test exchange's receipt of order.
        msg=getNextExchangeMessage();
        assertEquals("ID1",msg.getString(ClOrdID.FIELD));
        // Test message modifiers.
        assertEquals(MessageEncoding.UTF_8,
                     msg.getHeader().getString(MessageEncoding.FIELD));
        // Test sending message modifiers.
        assertEquals("Test Text",
                     msg.getString(Text.FIELD));
        //Verify that the sent message has the correct instrument in it
        assertEquals(instrument,InstrumentFromMessage.SELECTOR.forValue(msg).extract(msg));


        //Test replace order
        OrderReplace replaceOrder = Factory.getInstance().createOrderReplace(er);
        replaceOrder.setOrderID(new OrderID("ID2"));
        replaceOrder.setSide(Side.Sell);
        replaceOrder.setBrokerOrderID(null);
        c.getClient().sendOrder(replaceOrder);

        //Consume and test ORS ack.
        er=(ExecutionReport)(c.getReportListener().getNext());
        assertExecReportValues(er,
                               null,
                               new BigDecimal("0"),
                               new BigDecimal("0"),
                               ExecutionType.PendingReplace,
                               null,
                               new BigDecimal("0"),
                               new BigDecimal("0"),
                               new BigDecimal("1"),
                               new BigDecimal("1"),
                               OrderType.Limit,
                               Side.Sell,
                               instrument,
                               null,
                               null,
                               null,
                               true);

        // Consume and test exchange's receipt of order.
        msg=getNextExchangeMessage();
        assertEquals("ID2",msg.getString(ClOrdID.FIELD));
        // Test message modifiers.
        assertEquals(MessageEncoding.UTF_8,
                     msg.getHeader().getString(MessageEncoding.FIELD));
        // Test sending message modifiers.
        assertEquals("Test Text",
                     msg.getString(Text.FIELD));
        //Verify that the sent message has the correct instrument in it
        assertEquals(instrument,InstrumentFromMessage.SELECTOR.forValue(msg).extract(msg));


        //Test cancel order
        OrderCancel cancelOrder = Factory.getInstance().createOrderCancel(er);
        cancelOrder.setOrderID(new OrderID("ID3"));
        cancelOrder.setBrokerOrderID(null);
        c.getClient().sendOrder(cancelOrder);

        //Consume and test ORS ack.
        er=(ExecutionReport)(c.getReportListener().getNext());
        assertExecReportValues(er,
                               null,
                               new BigDecimal("0"),
                               new BigDecimal("0"),
                               ExecutionType.PendingCancel,
                               null,
                               new BigDecimal("0"),
                               new BigDecimal("0"),
                               new BigDecimal("1"),
                               new BigDecimal("1"),
                               null,
                               Side.Sell,
                               instrument,
                               null,
                               null,
                               null,
                               true);

        // Consume and test exchange's receipt of order.
        msg=getNextExchangeMessage();
        assertEquals("ID3",msg.getString(ClOrdID.FIELD));
        // Test message modifiers.
        assertEquals(MessageEncoding.UTF_8,
                     msg.getHeader().getString(MessageEncoding.FIELD));
        // Test sending message modifiers.
        assertEquals("Test Text",
                     msg.getString(Text.FIELD));
        //Verify that the sent message has the correct instrument in it
        assertEquals(instrument,InstrumentFromMessage.SELECTOR.forValue(msg).extract(msg));
    }

    private Instrument getInstrument()
    {
        return mInstrument;
    }

    private final Instrument mInstrument;
}
