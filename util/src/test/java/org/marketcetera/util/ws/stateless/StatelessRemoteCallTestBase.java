package org.marketcetera.util.ws.stateless;

import java.util.Iterator;
import java.util.Locale;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggingEvent;
import org.junit.Before;
import org.marketcetera.util.except.I18NException;
import org.marketcetera.util.log.ActiveLocale;
import org.marketcetera.util.log.I18NBoundMessage2P;
import org.marketcetera.util.test.TestCaseBase;
import org.marketcetera.util.ws.tags.AppId;
import org.marketcetera.util.ws.tags.EqualsTagFilter;
import org.marketcetera.util.ws.tags.NodeId;
import org.marketcetera.util.ws.tags.VersionId;
import org.marketcetera.util.ws.tags.VersionIdTest;
import org.marketcetera.util.ws.wrappers.LocaleWrapper;
import org.marketcetera.util.ws.wrappers.RemoteException;

import static org.junit.Assert.*;

/**
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id$
 */

/* $License$ */

public class StatelessRemoteCallTestBase
    extends TestCaseBase
{
    protected static final EqualsTagFilter TEST_VERSION_FILTER=
        new EqualsTagFilter(null,TestMessages.MESSAGE);
    protected static final EqualsTagFilter TEST_APP_FILTER=
        new EqualsTagFilter(null,TestMessages.MESSAGE);
    protected static final EqualsTagFilter TEST_CLIENT_FILTER=
        new EqualsTagFilter(null,TestMessages.MESSAGE);

    private static final VersionId TEST_VERSION=
        VersionId.SELF;
    protected static final VersionId TEST_VERSION_D=
        VersionIdTest.TEST_VERSION_D;
    private static final AppId TEST_APP=
        new AppId("testApp");
    private static final NodeId TEST_CLIENT=
        NodeId.generate(); 
    protected static final Locale TEST_LOCALE=
        new Locale("la","CO","va");

    protected static final Integer TEST_INT=
        NumberUtils.INTEGER_ONE;
    protected static final RuntimeException TEST_EXCEPTION=
        new IllegalArgumentException();

    private static final String TEST_LOCATION=
        StatelessRemoteCall.class.getName();


    protected static Object sSetByRunner;


    @Before
    public void setupStatelessRemoteCallTestBase()
    {
        ActiveLocale.setProcessLocale(Locale.ROOT);
    }


    protected static void single
        (StatelessRemoteCall call,
         StatelessRemoteCall empty,
         StatelessRemoteCall defaults)
    {
        assertEquals(TEST_VERSION_FILTER,call.getVersionIdFilter());
        assertEquals(TEST_APP_FILTER,call.getAppIdFilter());
        assertEquals(TEST_CLIENT_FILTER,call.getClientIdFilter());

        assertNull(empty.getVersionIdFilter());
        assertNull(empty.getAppIdFilter());
        assertNull(empty.getClientIdFilter());

        assertEquals(StatelessRemoteCall.DEFAULT_VERSION_FILTER,
                     defaults.getVersionIdFilter());
        assertNull(defaults.getAppIdFilter());
        assertNull(defaults.getClientIdFilter());
    }

    protected static void fillContext
        (StatelessClientContext context)
    {
        context.setVersionId(TEST_VERSION);
        context.setAppId(TEST_APP);
        context.setClientId(TEST_CLIENT);
        context.setLocale(new LocaleWrapper(TEST_LOCALE));
    }

    protected void checkEvents
        (StatelessClientContext context,
         StatelessRemoteCall call,
         boolean success)
    {
        String contextStr=context.toString();
        Iterator<LoggingEvent> events=getAppender().getEvents().iterator();
        assertEvent
            (events.next(),Level.DEBUG,call.getClass().getName(),
             "Remote call is starting; context: "+contextStr,
             TEST_LOCATION);
        if (success) {
            assertEvent
                (events.next(),Level.DEBUG,call.getClass().getName(),
                 "Remote call ended successfully; context: "+contextStr,
                 TEST_LOCATION);
        } else {
            assertEvent
                (events.next(),Level.DEBUG,call.getClass().getName(),
                 "Remote call ended with failure; context: "+contextStr,
                 TEST_LOCATION);
        }
        assertFalse(events.hasNext());
        getAppender().clear();
    }

    private <T> void singleSuccess
        (StatelessClientContext context,
         StatelessRemoteCaller<T> caller,
         T value)
        throws Exception
    {
        setLevel(caller.getClass().getName(),Level.DEBUG);
        assertEquals(value,caller.execute(context));
        checkEvents(context,caller,true);
    }

    private void singleFailure
        (StatelessClientContext context,
         StatelessRemoteCaller<?> caller,
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
        (StatelessClientContext context,
         StatelessRemoteRunner runner,
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
        (StatelessClientContext context,
         StatelessRemoteRunner runner,
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
        (StatelessClientContext context,
         StatelessRemoteCaller<Integer> intCall,
         StatelessRemoteCaller<Locale> localeCall,
         StatelessRemoteCaller<?> throwCall,
         StatelessRemoteCaller<?> throwVersionIdCall,
         StatelessRemoteCaller<?> throwAppIdCall,
         StatelessRemoteCaller<?> throwClientIdCall)
        throws Exception
    {
        fillContext(context);
        singleSuccess(context,intCall,TEST_INT);
        singleSuccess(context,localeCall,TEST_LOCALE);
        singleFailure(context,throwCall,TEST_EXCEPTION.getClass());
        singleFailure(context,throwVersionIdCall,I18NException.class);
        singleFailure(context,throwAppIdCall,I18NException.class);
        singleFailure(context,throwClientIdCall,I18NException.class);

        context.setVersionId(TEST_VERSION_D);
        try {
            intCall.execute(context);
            fail();
        } catch (RemoteException ex) {
            assertEquals
                (new I18NBoundMessage2P
                 (Messages.VERSION_MISMATCH,VersionId.SELF,TEST_VERSION_D),
                 ((I18NException)(ex.getCause())).getI18NBoundMessage());
        }
    }

    protected void calls
        (StatelessClientContext context,
         StatelessRemoteRunner intRun,
         StatelessRemoteRunner localeRun,
         StatelessRemoteRunner throwRun,
         StatelessRemoteRunner throwVersionIdRun,
         StatelessRemoteRunner throwAppIdRun,
         StatelessRemoteRunner throwClientIdRun)
        throws Exception
    {
        fillContext(context);
        singleSuccess(context,intRun,TEST_INT);
        singleSuccess(context,localeRun,TEST_LOCALE);
        singleFailure(context,throwRun,TEST_EXCEPTION.getClass());
        singleFailure(context,throwVersionIdRun,I18NException.class);
        singleFailure(context,throwAppIdRun,I18NException.class);
        singleFailure(context,throwClientIdRun,I18NException.class);

        context.setVersionId(TEST_VERSION_D);
        try {
            intRun.execute(context);
            fail();
        } catch (RemoteException ex) {
            assertEquals
                (new I18NBoundMessage2P
                 (Messages.VERSION_MISMATCH,VersionId.SELF,TEST_VERSION_D),
                 ((I18NException)(ex.getCause())).getI18NBoundMessage());
        }
    }
}
