package org.marketcetera.util.ws.stateful;

import java.util.Locale;
import org.apache.log4j.Level;
import org.marketcetera.util.except.I18NException;
import org.marketcetera.util.log.I18NBoundMessage1P;
import org.marketcetera.util.log.I18NBoundMessage2P;
import org.marketcetera.util.ws.stateless.StatelessClientContext;
import org.marketcetera.util.ws.stateless.StatelessRemoteCall;
import org.marketcetera.util.ws.stateless.StatelessRemoteCallTestBase;
import org.marketcetera.util.ws.tags.EqualsTagFilter;
import org.marketcetera.util.ws.tags.SessionId;
import org.marketcetera.util.ws.tags.ValidSessionTagFilter;
import org.marketcetera.util.ws.tags.VersionId;
import org.marketcetera.util.ws.wrappers.RemoteException;

import static org.junit.Assert.*;

/**
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id$
 */

/* $License$ */

public class RemoteCallTestBase
    extends StatelessRemoteCallTestBase
{
    protected static final EqualsTagFilter TEST_SESSION_FILTER=
        new EqualsTagFilter(null,TestMessages.MESSAGE);
    protected static final SessionManager<Object> TEST_MANAGER=
        new SessionManager<Object>();

    private static final SessionId TEST_SESSION=
        SessionId.generate(); 
    private static final SessionId TEST_SESSION_D=
        SessionId.generate(); 
    private static final String TEST_USER=
        "metc";
    private static final StatelessClientContext TEST_CONTEXT=
        new StatelessClientContext();
    protected static final SessionHolder<Object> TEST_HOLDER=
        new SessionHolder<Object>(TEST_USER,TEST_CONTEXT);


    static {
        TEST_MANAGER.put(TEST_SESSION,TEST_HOLDER);
    }


    protected static void single
        (RemoteCall<?> call,
         RemoteCall<?> empty,
         RemoteCall<?> defaults)
    {
        single((StatelessRemoteCall)call,
               (StatelessRemoteCall)empty,
               (StatelessRemoteCall)defaults);

        assertEquals(TEST_SESSION_FILTER,call.getSessionIdFilter());
        assertEquals(TEST_MANAGER,call.getSessionManager());

        assertNull(empty.getSessionIdFilter());
        assertNull(empty.getSessionManager());

        assertEquals(TEST_MANAGER,
                     (((ValidSessionTagFilter<?>)
                       (defaults.getSessionIdFilter())).getSessionManager()));
        assertEquals(TEST_MANAGER,defaults.getSessionManager());
    }

    private static void fillContext
        (ClientContext context)
    {
        fillContext((StatelessClientContext)context);
        context.setSessionId(TEST_SESSION);
    }

    private <T> void singleSuccess
        (ClientContext context,
         RemoteCaller<?,T> caller,
         T value)
        throws Exception
    {
        setLevel(caller.getClass().getName(),Level.DEBUG);
        assertEquals(value,caller.execute(context));
        checkEvents(context,caller,true);
    }

    private void singleFailure
        (ClientContext context,
         RemoteCaller<?,?> caller,
         Class<?> exceptionClass)
    {
        setLevel(caller.getClass().getName(),Level.DEBUG);
        setRunnerData(null);
        try {
            caller.execute(context);
            fail();
        } catch (RemoteException ex) {
            assertNull(sSetByRunner);
            assertEquals(exceptionClass,ex.getCause().getClass());
        }
        checkEvents(context,caller,false);
    }

    protected static void setRunnerData
        (Object value)
    {
        sSetByRunner=value;
    }
    
    private void singleSuccess
        (ClientContext context,
         RemoteRunner<?> runner,
         Object value)
        throws Exception
    {
        setLevel(runner.getClass().getName(),Level.DEBUG);
        setRunnerData(null);
        runner.execute(context);
        assertEquals(value,sSetByRunner);
        checkEvents(context,runner,true);
    }

    private void singleFailure
        (ClientContext context,
         RemoteRunner<?> runner,
         Class<?> exceptionClass)
    {
        setLevel(runner.getClass().getName(),Level.DEBUG);
        setRunnerData(null);
        try {
            runner.execute(context);
            fail();
        } catch (RemoteException ex) {
            assertNull(sSetByRunner);
            assertEquals(exceptionClass,ex.getCause().getClass());
        }
        checkEvents(context,runner,false);
    }

    protected void calls
        (ClientContext context,
         RemoteCaller<?,Integer> intCall,
         RemoteCaller<?,Locale> localeCall,
         RemoteCaller<?,?> throwCall,
         RemoteCaller<?,?> throwVersionIdCall,
         RemoteCaller<?,?> throwAppIdCall,
         RemoteCaller<?,?> throwClientIdCall,
         RemoteCaller<?,?> throwSessionIdCall)
        throws Exception
    {
        fillContext(context);
        singleSuccess(context,intCall,TEST_INT);
        singleSuccess(context,localeCall,TEST_LOCALE);
        singleFailure(context,throwCall,TEST_EXCEPTION.getClass());
        singleFailure(context,throwVersionIdCall,I18NException.class);
        singleFailure(context,throwAppIdCall,I18NException.class);
        singleFailure(context,throwClientIdCall,I18NException.class);
        singleFailure(context,throwSessionIdCall,I18NException.class);

        context.setVersionId(TEST_VERSION_D);
        try {
            intCall.execute(context);
            fail();
        } catch (RemoteException ex) {
            assertEquals
                (new I18NBoundMessage2P
                 (org.marketcetera.util.ws.stateless.Messages.VERSION_MISMATCH,
                  VersionId.SELF,TEST_VERSION_D),
                 ((I18NException)(ex.getCause())).getI18NBoundMessage());
        }

        fillContext(context);
        context.setSessionId(TEST_SESSION_D);
        try {
            intCall.execute(context);
            fail();
        } catch (RemoteException ex) {
            assertEquals
                (new I18NBoundMessage1P
                 (org.marketcetera.util.ws.tags.Messages.SESSION_EXPIRED,
                  TEST_SESSION_D),
                 ((I18NException)(ex.getCause())).getI18NBoundMessage());
        }

        // Null sessions can go through.

        context.setSessionId(null);
        setRunnerData(null);
        throwSessionIdCall.execute(context);
        assertEquals(TEST_INT,sSetByRunner);
    }

    protected void calls
        (ClientContext context,
         RemoteRunner<?> intRun,
         RemoteRunner<?> localeRun,
         RemoteRunner<?> throwRun,
         RemoteRunner<?> throwVersionIdRun,
         RemoteRunner<?> throwAppIdRun,
         RemoteRunner<?> throwClientIdRun,
         RemoteRunner<?> throwSessionIdRun)
        throws Exception
    {
        fillContext(context);
        singleSuccess(context,intRun,TEST_INT);
        singleSuccess(context,localeRun,TEST_LOCALE);
        singleFailure(context,throwRun,TEST_EXCEPTION.getClass());
        singleFailure(context,throwVersionIdRun,I18NException.class);
        singleFailure(context,throwAppIdRun,I18NException.class);
        singleFailure(context,throwClientIdRun,I18NException.class);
        singleFailure(context,throwSessionIdRun,I18NException.class);

        context.setVersionId(TEST_VERSION_D);
        try {
            intRun.execute(context);
            fail();
        } catch (RemoteException ex) {
            assertEquals
                (new I18NBoundMessage2P
                 (org.marketcetera.util.ws.stateless.Messages.VERSION_MISMATCH,
                  VersionId.SELF,TEST_VERSION_D),
                 ((I18NException)(ex.getCause())).getI18NBoundMessage());
        }

        fillContext(context);
        context.setSessionId(TEST_SESSION_D);
        try {
            intRun.execute(context);
            fail();
        } catch (RemoteException ex) {
            assertEquals
                (new I18NBoundMessage1P
                 (org.marketcetera.util.ws.tags.Messages.SESSION_EXPIRED,
                  TEST_SESSION_D),
                 ((I18NException)(ex.getCause())).getI18NBoundMessage());
        }

        // Null sessions can go through.

        context.setSessionId(null);
        setRunnerData(null);
        throwSessionIdRun.execute(context);
        assertEquals(TEST_INT,sSetByRunner);
    }
}
