package org.marketcetera.ors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.marketcetera.trade.TypesTestBase.assertExecReportValues;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;

import org.joda.time.LocalDate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.marketcetera.core.instruments.InstrumentFromMessage;
import org.marketcetera.event.HasFIXMessage;
import org.marketcetera.trade.Currency;
import org.marketcetera.trade.ExecutionReport;
import org.marketcetera.trade.ExecutionType;
import org.marketcetera.trade.Factory;
import org.marketcetera.trade.Instrument;
import org.marketcetera.trade.OrderCancel;
import org.marketcetera.trade.OrderID;
import org.marketcetera.trade.OrderReplace;
import org.marketcetera.trade.OrderSingle;
import org.marketcetera.trade.OrderType;
import org.marketcetera.trade.Side;

import quickfix.Message;
import quickfix.field.ClOrdID;
import quickfix.field.MessageEncoding;
import quickfix.field.Text;

/**
 * Tests sending of New, Replace & Cancel orders for Currency.
 *
 */

/* $License$ */

@RunWith(Parameterized.class)
public class OrderCurrencySendingTest
    extends ORSTestBase
{
    public OrderCurrencySendingTest(Instrument inInstrument)
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
    	LocalDate expiry = new LocalDate (2012,9,30);
        return Arrays.asList(
                new Object[]{new Currency("USD","INR",expiry)},
                new Object[]{new Currency("USD","JPY",expiry)},
                new Object[]{new Currency("GBP","INR",expiry)}
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
        assertEquals("UTF-8",
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
        assertEquals("UTF-8",
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
        cancelOrder.setCustomFields(null);
        c.getClient().sendOrder(cancelOrder);

        //Consume and test ORS ack.
        er=(ExecutionReport)(c.getReportListener().getNext());
        msg=((HasFIXMessage)er).getMessage();


        // Consume and test exchange's receipt of order.
        msg=getNextExchangeMessage();
        assertEquals("ID3",msg.getString(ClOrdID.FIELD));
        // Test message modifiers.
        assertEquals("UTF-8",
                     msg.getHeader().getString(MessageEncoding.FIELD));
        // Test sending message modifiers.
        assertEquals("Test Text",
                     msg.getString(Text.FIELD));
        //Verify that the sent message has the correct instrument in it    
        assertEquals(instrument.getSecurityType(),InstrumentFromMessage.SELECTOR.forValue(msg).extract(msg).getSecurityType());
        assertEquals(instrument.getSymbol(),InstrumentFromMessage.SELECTOR.forValue(msg).extract(msg).getSymbol());
    }

    private Instrument getInstrument()
    {
        return mInstrument;
    }

    private final Instrument mInstrument;
}
