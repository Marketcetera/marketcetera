package org.marketcetera.util.ws.stateful;

import java.util.Locale;
import org.apache.log4j.Level;
import org.junit.Before;
import org.junit.Test;
import org.marketcetera.util.log.ActiveLocale;
import org.marketcetera.util.test.TestCaseBase;
import org.marketcetera.util.ws.stateless.StatelessClientContext;
import org.marketcetera.util.ws.tags.NodeId;
import org.marketcetera.util.ws.tags.SessionId;

import static org.junit.Assert.*;

/**
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id$
 */

/* $License$ */

public class SessionManagerTest
    extends TestCaseBase
{
    private static final long TEST_LIFESPAN=
        500;
    private static final NodeId TEST_SERVER_ID=
        NodeId.generate();
    private static final SessionId TEST_SESSION_ID=
        SessionId.generate();
    private static final SessionId TEST_SESSION_ID_D=
        SessionId.generate();
    private static final String TEST_USER=
        "metc";
    private static final StatelessClientContext TEST_CONTEXT=
        new StatelessClientContext();
    private static final Integer TEST_SESSION=
        new Integer(1);
    private static final Integer TEST_SESSION_D=
        new Integer(2);
    private static final String TEST_CATEGORY=
        SessionManager.Reaper.class.getName();


    private static class TestFactory
        implements SessionFactory<Integer>
    {
        private SessionId mLastSessionId;
        private Integer mLastRemovedSession;

        public void setLastSessionId
            (SessionId lastSessionId)
        {
            mLastSessionId=lastSessionId;
        }

        public SessionId getLastSessionId()
        {
            return mLastSessionId;
        }

        public void setLastRemovedSession
            (Integer lastRemovedSession)
        {
            mLastRemovedSession=lastRemovedSession;
        }

        public Object getLastRemovedSession()
        {
            return mLastRemovedSession;
        }

        @Override
        public Integer createSession
            (StatelessClientContext context,
             String user,
             SessionId id)
        {
            assertEquals(TEST_CONTEXT,context);
            assertEquals(TEST_USER,user);
            setLastSessionId(id);
            if (id==TEST_SESSION_ID) {
                return TEST_SESSION;
            }
            return TEST_SESSION_D;
        }

        @Override
        public void removedSession(Integer session)
        {
            setLastRemovedSession(session);
        }
    }


    @Before
    public void setupSessionManagerTest()
    {
        ActiveLocale.setProcessLocale(Locale.ROOT);
        setLevel(TEST_CATEGORY,Level.INFO);
    }


    @Test
    public void basics()
        throws Exception
    {
        TestFactory f=new TestFactory();
        SessionManager<Integer> s=new SessionManager<Integer>(f);
        assertSame(f,s.getSessionFactory());
        assertEquals(SessionManager.INFINITE_SESSION_LIFESPAN,s.getLifespan());

        assertNull(s.getServerId());
        s.setServerId(TEST_SERVER_ID);
        assertEquals(TEST_SERVER_ID,s.getServerId());
        s.setServerId(null);
        assertNull(s.getServerId());

        assertNull(s.get(TEST_SESSION_ID));

        SessionHolder<Integer> hD=
            new SessionHolder<Integer>(TEST_USER,TEST_CONTEXT);
        long time=hD.getLastAccess();
        Thread.sleep(100);
        s.put(TEST_SESSION_ID,hD);
        assertTrue(hD.getLastAccess()>time);
        assertEquals(TEST_SESSION_ID,f.getLastSessionId());

        time=hD.getLastAccess();
        Thread.sleep(100);
        assertSame(hD,s.get(TEST_SESSION_ID));
        assertTrue(hD.getLastAccess()>time);

        SessionHolder<Integer> h=
            new SessionHolder<Integer>(TEST_USER,TEST_CONTEXT);
        s.put(TEST_SESSION_ID,h);
        assertSame(h,s.get(TEST_SESSION_ID));

        assertNull(s.get(TEST_SESSION_ID_D));
        s.put(TEST_SESSION_ID_D,hD);
        assertSame(hD,s.get(TEST_SESSION_ID_D));
        assertSame(h,s.get(TEST_SESSION_ID));
        assertEquals(TEST_SESSION_ID_D,f.getLastSessionId());

        s.remove(TEST_SESSION_ID);
        assertNull(s.get(TEST_SESSION_ID));
        assertSame(hD,s.get(TEST_SESSION_ID_D));
        assertEquals(TEST_SESSION,f.getLastRemovedSession());

        // Removal of nonexistent session ID.

        s.remove(TEST_SESSION_ID);
    }

    @Test
    public void basicsNoFactory()
        throws Exception
    {
        SessionManager<Integer> s=new SessionManager<Integer>();
        assertEquals(SessionManager.INFINITE_SESSION_LIFESPAN,s.getLifespan());
        SessionHolder<Integer> h=
            new SessionHolder<Integer>(TEST_USER,TEST_CONTEXT);
        s.put(TEST_SESSION_ID,h);
        assertSame(h,s.get(TEST_SESSION_ID));
        s.remove(TEST_SESSION_ID);
        assertNull(s.get(TEST_SESSION_ID));

        // Removal of nonexistent session ID.

        s.remove(TEST_SESSION_ID);
    }

    @Test
    public void timeout()
        throws Exception
    {
        TestFactory f=new TestFactory();
        SessionManager<Integer> s=new SessionManager<Integer>(f,TEST_LIFESPAN);
        assertEquals(TEST_LIFESPAN,s.getLifespan());

        SessionHolder<Integer> h=
            new SessionHolder<Integer>(TEST_USER,TEST_CONTEXT);
        s.put(TEST_SESSION_ID,h);
        assertSame(h,s.get(TEST_SESSION_ID));
        assertEquals(TEST_SESSION_ID,f.getLastSessionId());
        for (int i=0;i<10;i++) {
            Thread.sleep(TEST_LIFESPAN/2);
            assertSame(h,s.get(TEST_SESSION_ID));
        }
        Thread.sleep(TEST_LIFESPAN*2);
        assertSingleEvent
            (Level.INFO,TEST_CATEGORY,
             "Session "+TEST_SESSION_ID.toString()+
             " has expired; creation context: "+TEST_CONTEXT.toString(),
             TEST_CATEGORY);
        assertNull(s.get(TEST_SESSION_ID));
        assertEquals(TEST_SESSION,f.getLastRemovedSession());
    }

    @Test
    public void timeoutNoFactory()
        throws Exception
    {
        SessionManager<Integer> s=new SessionManager<Integer>(TEST_LIFESPAN);
        assertEquals(TEST_LIFESPAN,s.getLifespan());

        SessionHolder<Integer> h=
            new SessionHolder<Integer>(TEST_USER,TEST_CONTEXT);
        s.put(TEST_SESSION_ID,h);
        assertSame(h,s.get(TEST_SESSION_ID));
        for (int i=0;i<10;i++) {
            Thread.sleep(TEST_LIFESPAN/2);
            assertSame(h,s.get(TEST_SESSION_ID));
        }
        Thread.sleep(TEST_LIFESPAN*2);
        assertSingleEvent
            (Level.INFO,TEST_CATEGORY,
             "Session "+TEST_SESSION_ID.toString()+
             " has expired; creation context: "+TEST_CONTEXT.toString(),
             TEST_CATEGORY);
        assertNull(s.get(TEST_SESSION_ID));
    }

    @Test
    public void termination()
        throws Exception
    {
        ThreadGroup group=new ThreadGroup("group");
        Thread t=new Thread(group,"testThread") {
            @Override
            public void run() {
                (new SessionManager<Integer>(TEST_LIFESPAN)).setServerId
                    (TEST_SERVER_ID);
            }
        };
        t.start();
        Thread.sleep(TEST_LIFESPAN);
        assertEquals(1,group.activeCount());
        group.interrupt();
        Thread.sleep(TEST_LIFESPAN*2);
        assertEquals(0,group.activeCount());
        assertSingleEvent
            (Level.INFO,TEST_CATEGORY,
             "Reaper for server "+TEST_SERVER_ID.toString()+" was terminated",
             TEST_CATEGORY);
    }
}
