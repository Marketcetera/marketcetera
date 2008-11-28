package org.marketcetera.ors.jms;

/**
 * @author tlerios@marketcetera.com
 * @since $Release$
 * @version $Id$
 */

/* $License$ */

public class SampleIntegerReplyHandler
    extends SampleReplyHandler<Integer>
{

    // SampleReplyHandler.

    @Override
    Integer create
        (int i)
    {
        return new Integer(i);
    }

    @Override
    boolean isEqual
        (int i,
         Integer msg)
    {
        return (i==msg);
    }

    @Override
    protected Integer getReply
        (Integer msg)
    {
        return (msg+1);
    }
}
