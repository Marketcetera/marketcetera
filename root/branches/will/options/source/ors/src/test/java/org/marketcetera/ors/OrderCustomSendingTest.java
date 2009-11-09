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
import org.marketcetera.trade.Side;
import quickfix.Message;
import quickfix.field.ClOrdID;
import quickfix.field.MessageEncoding;
import quickfix.field.Rule80A;
import quickfix.field.SecondaryClOrdID;

import static org.junit.Assert.*;
import static org.marketcetera.trade.TypesTestBase.*;

/**
 * @author tlerios@marketcetera.com
 * @since $Release$
 * @version $Id$
 */

/* $License$ */

public class OrderCustomSendingTest
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
        HashMap<String,String> map=new HashMap<String,String>();
        map.put(Integer.toString(SecondaryClOrdID.FIELD),
                "ID2"); // FIX4.3+ only.
        order.setCustomFields(map);

        // Send order to first broker who cannot process it (FIX 4.2).
        order.setOrderID(new OrderID("ID1"));
        order.setBrokerID(new BrokerID("broker1"));
        c.getClient().sendOrder(order);
        Message msg=((HasFIXMessage)(c.getReportListener().getNext())).
            getMessage();
        // ORS ack will not contain FIX 4.3-specific fields because
        // its dictionary does not support it.
        assertFalse(msg.isSetField(SecondaryClOrdID.FIELD));
        
        // Send order to second broker who can process it (FIX 4.3).
        order.setOrderID(new OrderID("ID2"));
        order.setBrokerID(new BrokerID("broker2"));
        c.getClient().sendOrder(order);
        msg=((HasFIXMessage)(c.getReportListener().getNext())).
            getMessage();
        // ORS ack will contain FIX 4.3-specific field because its
        // dictionary supports it.
        assertEquals("ID2",msg.getString(SecondaryClOrdID.FIELD));
        getNextExchangeMessage();
    }
}
