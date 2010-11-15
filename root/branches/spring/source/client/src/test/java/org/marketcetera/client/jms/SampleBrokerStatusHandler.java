package org.marketcetera.client.jms;

import org.marketcetera.client.brokers.BrokerStatus;
import org.marketcetera.trade.BrokerID;

/**
 * @author tlerios@marketcetera.com
 * @since 1.5.0
 * @version $Id$
 */

/* $License$ */

public class SampleBrokerStatusHandler
    extends SampleReplyHandler<BrokerStatus>
{

    // SampleReplyHandler.

    @Override
    BrokerStatus create
        (int i)
    {
        return new BrokerStatus(String.valueOf(i),new BrokerID("ID"),true);
    }

    @Override
    boolean isEqual
        (int i,
         BrokerStatus msg)
    {
        return (i==Integer.valueOf(msg.getName()));
    }

    @Override
    protected BrokerStatus getReply
        (BrokerStatus msg)
    {
        return create(Integer.valueOf(msg.getName())+1);
    }
}
