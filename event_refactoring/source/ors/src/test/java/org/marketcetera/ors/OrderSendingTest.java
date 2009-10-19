package org.marketcetera.ors;

import java.math.BigDecimal;
import org.junit.Test;
import org.marketcetera.event.HasFIXMessage;
import org.marketcetera.trade.ExecutionReport;
import org.marketcetera.trade.ExecutionType;
import org.marketcetera.trade.Factory;
import org.marketcetera.trade.Equity;
import org.marketcetera.trade.OrderID;
import org.marketcetera.trade.OrderSingle;
import org.marketcetera.trade.OrderType;
import org.marketcetera.trade.Side;
import quickfix.Message;
import quickfix.field.ClOrdID;
import quickfix.field.MessageEncoding;
import quickfix.field.Rule80A;

import static org.junit.Assert.*;
import static org.marketcetera.trade.TypesTestBase.*;

/**
 * @author tlerios@marketcetera.com
 * @since $Release$
 * @version $Id$
 */

/* $License$ */

public class OrderSendingTest
    extends ORSTestBase
{
    @Test(timeout=300000)
    public void orderSending()
        throws Exception
    {
        ORSTestClient c=getAdminClient();

        // Compose order.
        Equity ibm=new Equity("IBM");
        OrderSingle order=Factory.getInstance().createOrderSingle();
        order.setOrderID(new OrderID("ID1"));
        order.setOrderType(OrderType.Limit);
        order.setQuantity(new BigDecimal("1"));
        order.setSide(Side.Buy);
        order.setInstrument(ibm);
        order.setPrice(new BigDecimal("10"));
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
                               ibm,
                               null,
                               null,
                               null,
                               true);
        Message msg=((HasFIXMessage)er).getMessage();
        // Test sending message modifiers.
        assertFalse(msg.isSetField(Rule80A.FIELD));

        // Consume and test exchange's receipt of order.
        msg=getNextExchangeMessage();
        assertEquals("ID1",msg.getString(ClOrdID.FIELD));
        // Test message modifiers.
        assertEquals(MessageEncoding.UTF_8,
                     msg.getHeader().getString(MessageEncoding.FIELD));
        // Test sending message modifiers.
        assertEquals(Rule80A.AGENCY_SINGLE_ORDER,
                     msg.getChar(Rule80A.FIELD));
    }
}
