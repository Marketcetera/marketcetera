package org.marketcetera.client.jms;

import java.math.BigDecimal;
import org.marketcetera.trade.Factory;
import org.marketcetera.trade.OrderSingle;
import org.marketcetera.trade.TradeMessage;

/**
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id$
 */

/* $License$ */

public class SampleOrderReplyHandler
    extends SampleReplyHandler<TradeMessage>
{

    // SampleReplyHandler.

    @Override
    TradeMessage create
        (int i)
    {
        OrderSingle msg=Factory.getInstance().createOrderSingle();
        msg.setPrice(new BigDecimal(i));
        return msg;
    }

    @Override
    boolean isEqual
        (int i,
         TradeMessage msg)
    {
        return (i==((OrderSingle)msg).getPrice().intValue());
    }

    @Override
    protected TradeMessage getReply
        (TradeMessage msg)
    {
        return create(((OrderSingle)msg).getPrice().intValue()+1);
    }
}
