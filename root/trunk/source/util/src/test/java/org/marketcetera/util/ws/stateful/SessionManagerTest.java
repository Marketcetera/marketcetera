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
 * @since $Release$
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
    private static final SessionId TEST_SESSION=
        SessionId.generate();
    private static final SessionId TEST_SESSION_D=
        SessionId.generate();
    private static final StatelessClientContext TEST_CONTEXT=
        new StatelessClientContext();
    private static final SessionHolder<Object> TEST_HOLDER=
        new SessionHolder<Object>(TEST_CONTEXT);
    private static final SessionHolder<Object> TEST_HOLDER_D=
        new SessionHolder<Object>(TEST_CONTEXT);
    private static final String TEST_CATEGORY=
        SessionManager.Reaper.class.getName();


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
        SessionManager<Object> s=new SessionManager<Object>();
        assertEquals(SessionManager.INFINITE_SESSION_LIFESPAN,s.getLifespan());

        assertNull(s.getServerId());
        s.setServerId(TEST_SERVER_ID);
        assertEquals(TEST_SERVER_ID,s.getServerId());
        s.setServerId(null);
        assertNull(s.getServerId());

        assertNull(s.get(TEST_SESSION));

        long time=TEST_HOLDER_D.getLastAccess();
        Thread.sleep(100);
        s.put(TEST_SESSION,TEST_HOLDER_D);
        assertTrue(TEST_HOLDER_D.getLastAccess()>time);

        time=TEST_HOLDER_D.getLastAccess();
        Thread.sleep(100);
        assertEquals(TEST_HOLDER_D,s.get(TEST_SESSION));
        assertTrue(TEST_HOLDER_D.getLastAccess()>time);

        s.put(TEST_SESSION,TEST_HOLDER);
        assertEquals(TEST_HOLDER,s.get(TEST_SESSION));

        assertNull(s.get(TEST_SESSION_D));
        s.put(TEST_SESSION_D,TEST_HOLDER_D);
        assertEquals(TEST_HOLDER_D,s.get(TEST_SESSION_D));
        assertEquals(TEST_HOLDER,s.get(TEST_SESSION));

        s.remove(TEST_SESSION);
        assertNull(s.get(TEST_SESSION));
        assertEquals(TEST_HOLDER_D,s.get(TEST_SESSION_D));

        // Removal of nonexistent session ID.

        s.remove(TEST_SESSION);
    }

    @Test
    public void timeout()
        throws Exception
    {
        SessionManager<Object> s=new SessionManager<Object>(TEST_LIFESPAN);
        assertEquals(TEST_LIFESPAN,s.getLifespan());

        s.put(TEST_SESSION,TEST_HOLDER);
        assertEquals(TEST_HOLDER,s.get(TEST_SESSION));
        for (int i=0;i<10;i++) {
            Thread.sleep(TEST_LIFESPAN/2);
            assertEquals(TEST_HOLDER,s.get(TEST_SESSION));
        }
        Thread.sleep(TEST_LIFESPAN*2);
        assertSingleEvent
            (Level.INFO,TEST_CATEGORY,
             "Session "+TEST_SESSION.toString()+
             " has expired; creation context: "+TEST_CONTEXT.toString(),
             TEST_CATEGORY);
        assertNull(s.get(TEST_SESSION));
    }

    @Test
    public void termination()
        throws Exception
    {
        ThreadGroup group=new ThreadGroup("group");
        Thread t=new Thread(group,"testThread") {
            @Override
            public void run() {
                (new SessionManager<Object>(TEST_LIFESPAN)).setServerId
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
