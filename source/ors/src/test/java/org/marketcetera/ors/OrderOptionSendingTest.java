package org.marketcetera.ors;

import java.math.BigDecimal;
import java.util.HashMap;
import org.junit.Test;
import org.marketcetera.event.HasFIXMessage;
import org.marketcetera.trade.BrokerID;
import org.marketcetera.trade.Equity;
import org.marketcetera.trade.ExecutionReport;
import org.marketcetera.trade.ExecutionType;
import org.marketcetera.trade.Factory;
import org.marketcetera.trade.OrderID;
import org.marketcetera.trade.OrderSingle;
import org.marketcetera.trade.OrderType;
import org.marketcetera.trade.PositionEffect;
import org.marketcetera.trade.Side;
import quickfix.Message;
import quickfix.field.ClOrdID;
import quickfix.field.MessageEncoding;
import quickfix.field.OpenClose;
import quickfix.field.Rule80A;

import static org.junit.Assert.*;
import static org.marketcetera.trade.TypesTestBase.*;

/**
 * @author tlerios@marketcetera.com
 * @since $Release$
 * @version $Id: OrderSendingTest.java 10808 2009-10-12 21:33:18Z anshul $
 */

/* $License$ */

public class OrderOptionSendingTest
    extends ORSTestBase
{
    @Test(timeout=300000)
    public void orderSending()
        throws Exception
    {
        ORSTestClient c=getAdminClient();

        // Compose order.
        Equity ibm=new Equity("IBM");
        // todo test must be updated to send an option.
        OrderSingle order=Factory.getInstance().createOrderSingle();
        order.setOrderID(new OrderID("ID1"));
        order.setOrderType(OrderType.Limit);
        order.setQuantity(new BigDecimal("1"));
        order.setSide(Side.Buy);
        order.setInstrument(ibm);
        order.setPrice(new BigDecimal("10"));
        HashMap<String,String> map=new HashMap<String,String>();
        map.put(Integer.toString(OpenClose.FIELD),
                Character.toString(quickfix.field.OpenClose.OPEN));
        order.setCustomFields(map);
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
                               PositionEffect.Open,
                               true);
        Message msg=((HasFIXMessage)er).getMessage();
        assertEquals(quickfix.field.OpenClose.OPEN,
                     msg.getChar(OpenClose.FIELD));

        // Consume and test exchange's receipt of order.
        msg=getNextExchangeMessage();
        assertEquals("ID1",msg.getString(ClOrdID.FIELD));
    }
}
