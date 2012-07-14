package org.marketcetera.client.jms;

import java.util.ArrayList;
import java.util.List;
import org.marketcetera.util.except.ExceptUtils;

/**
 * A sample message handler. It cannot be an inner class.
 *
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id$
 */

/* $License$ */

public abstract class SampleReplyHandler<T>
    implements ReplyHandler<T>
{

    // INSTANCE DATA.

    private List<T> mReceived=new ArrayList<T>(JmsManagerTest.TEST_COUNT);
    private List<T> mReplies=new ArrayList<T>(JmsManagerTest.TEST_COUNT);


    // INSTANCE METHODS.

    List<T> getReceived()
    {
        return mReceived;
    }

    List<T> getReplies()
    {
        return mReplies;
    }

    protected abstract T getReply
        (T msg)
        throws Exception;

    abstract T create
        (int i);

    abstract boolean isEqual
        (int i,
         T msg);


    // ReplyHandler.

    @Override
    public T replyToMessage
        (T msg)
    {
        getReceived().add(msg);
        T rMsg;
        try {
            rMsg=getReply(msg);
        } catch (Exception ex) {
            ExceptUtils.interrupt(ex);
            throw new IllegalArgumentException(msg.toString(),ex);
        }
        getReplies().add(rMsg);
        return rMsg;
    }
}
