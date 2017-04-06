package org.marketcetera.client.jms;

import java.math.BigDecimal;
import org.marketcetera.trade.Factory;
import org.marketcetera.trade.OrderSingle;
import org.marketcetera.util.ws.tags.SessionId;

/**
 * @author tlerios@marketcetera.com
 * @since 1.5.0
 * @version $Id$
 */

/* $License$ */

public class SampleEnvelopeReplyHandler
    extends SampleReplyHandler<DataEnvelope>
{

    // CLASS DATA.

    private static final SessionId SESSION_ID=SessionId.generate();


    // SampleReplyHandler.

    @Override
    DataEnvelope create
        (int i)
    {
        OrderSingle msg=Factory.getInstance().createOrderSingle();
        msg.setPrice(new BigDecimal(i));
        return new DataEnvelope(msg,SESSION_ID);
    }

    @Override
    boolean isEqual
        (int i,
         DataEnvelope msg)
    {
        return (i==((OrderSingle)msg.getOrder()).getPrice().intValue());
    }

    @Override
    protected DataEnvelope getReply
        (DataEnvelope msg)
    {
        return create(((OrderSingle)msg.getOrder()).getPrice().intValue()+1);
    }
}
