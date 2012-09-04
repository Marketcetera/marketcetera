package org.marketcetera.core.ws.sample;

import org.marketcetera.core.ws.stateful.SessionHolder;

/**
 * A sample session. It simply counts the number of calls made to the
 * {@link #getCallCount()} method.
 * 
 * @since 1.0.0
 * @version $Id: SampleSession.java 16063 2012-01-31 18:21:55Z colin $
 */

/* $License$ */

public class SampleSession
{

    // INSTANCE DATA.

    private int mCallCount;


    // INSTANCE METHODS.

    public synchronized int getCallCount()
    {
        return ++mCallCount;
    }


    // CLASS METHODS.

    public static SampleSession getSession
        (SessionHolder<SampleSession> sessionHolder)
    {
        SampleSession s=sessionHolder.getSession();
        if (s==null) {
            s=new SampleSession();
            sessionHolder.setSession(s);
        }
        return s;
    }
}
