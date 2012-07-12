package org.marketcetera.client.jms;

import org.marketcetera.trade.ExecutionReport;
import org.marketcetera.trade.Factory;
import org.marketcetera.trade.MessageCreationException;
import org.marketcetera.trade.Originator;
import org.marketcetera.trade.TradeMessage;
import quickfix.field.LastPx;

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
        quickfix.fix42.ExecutionReport msg=
            new quickfix.fix42.ExecutionReport();
        msg.setField(new LastPx(i));
        try {
            return Factory.getInstance().createExecutionReport
                (msg,null,Originator.Server,null,null);
        } catch (MessageCreationException ex) {
            throw new IllegalArgumentException(ex);
        }
    }

    @Override
    boolean isEqual
        (int i,
         TradeMessage msg)
    {
        return (i==((ExecutionReport)msg).getLastPrice().intValue());
    }

    @Override
    protected TradeMessage getReply
        (TradeMessage msg)
    {
        return create(((ExecutionReport)msg).getLastPrice().intValue()+1);
    }
}
