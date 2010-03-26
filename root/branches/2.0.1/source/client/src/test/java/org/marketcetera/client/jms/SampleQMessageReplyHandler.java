package org.marketcetera.client.jms;

import quickfix.FieldNotFound;
import quickfix.IntField;
import quickfix.Message;
import quickfix.fix42.NewOrderSingle;

/**
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id$
 */

/* $License$ */

public class SampleQMessageReplyHandler
    extends SampleReplyHandler<Message>
{

    // SampleReplyHandler.

    @Override
    Message create
        (int i)
    {
        NewOrderSingle msg=new NewOrderSingle();
        msg.setField(new IntField(0,i));
        return msg;
    }

    @Override
    boolean isEqual
        (int i,
         Message msg)
    {
        try {
            return (i==msg.getInt(0));
        } catch (FieldNotFound ex) {
            throw new IllegalArgumentException(ex);
        }
    }

    @Override
    protected Message getReply
        (Message msg)
        throws FieldNotFound
    {
        return create(msg.getInt(0)+1);
    }
}
