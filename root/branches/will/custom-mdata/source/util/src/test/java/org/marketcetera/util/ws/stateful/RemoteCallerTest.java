package org.marketcetera.util.ws.stateful;

import java.util.Locale;
import org.junit.Test;
import org.marketcetera.util.log.ActiveLocale;
import org.marketcetera.util.ws.tags.TagFilter;

import static org.junit.Assert.*;

/**
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id$
 */

/* $License$ */

public class RemoteCallerTest
    extends RemoteCallTestBase
{
    private static final class IntCaller
        extends RemoteCaller<Object,Integer>
    {
        public IntCaller()
        {
            super(TEST_MANAGER);
        }

        @Override
        protected Integer call
            (ClientContext context,
             SessionHolder<Object> sessionHolder)
        {
            assertEquals(TEST_HOLDER,sessionHolder);
            return TEST_INT;
        }
    }

    private static final class LocaleCaller
        extends RemoteCaller<Object,Locale>
    {
        public LocaleCaller()
        {
            super(TEST_MANAGER);
        }

        @Override
        protected Locale call
            (ClientContext context,
             SessionHolder<Object> sessionHolder)
        {
            assertEquals(TEST_HOLDER,sessionHolder);
            return ActiveLocale.getLocale();
        }
    }

    private static final class ThrowCaller
        extends RemoteCaller<Object,Integer>
    {
        public ThrowCaller()
        {
            super(TEST_MANAGER);
        }

        @Override
        protected Integer call
            (ClientContext context,
             SessionHolder<Object> sessionHolder)
        {
            assertEquals(TEST_HOLDER,sessionHolder);
            throw TEST_EXCEPTION;
        }
    }

    private static final class ThrowFilterCaller
        extends RemoteCaller<Object,Integer>
    {
        public ThrowFilterCaller
            (TagFilter versionIdFilter,
             TagFilter appIdFilter,
             TagFilter clientIdFilter,
             SessionManager<Object> sessionManager,
             TagFilter sessionIdFilter)
        {
            super(versionIdFilter,appIdFilter,clientIdFilter,
                  sessionManager,sessionIdFilter);
        }

        public ThrowFilterCaller()
        {
            super(TEST_MANAGER);
        }

        @Override
        protected Integer call
            (ClientContext context,
             SessionHolder<Object> sessionHolder)
        {
            assertNull(sessionHolder);
            setRunnerData(TEST_INT);
            return TEST_INT;
        }
    }


    @Test
    public void all()
        throws Exception
    {
        single
            (new ThrowFilterCaller
             (TEST_VERSION_FILTER,TEST_APP_FILTER,TEST_CLIENT_FILTER,
              TEST_MANAGER,TEST_SESSION_FILTER),
             new ThrowFilterCaller(null,null,null,null,null),
             new ThrowFilterCaller());

        ClientContext context=new ClientContext();
        calls
            (context,
             new IntCaller(),
             new LocaleCaller(),
             new ThrowCaller(),
             new ThrowFilterCaller(TEST_VERSION_FILTER,null,null,null,null),
             new ThrowFilterCaller(null,TEST_APP_FILTER,null,null,null),
             new ThrowFilterCaller(null,null,TEST_CLIENT_FILTER,null,null),
             new ThrowFilterCaller(null,null,null,null,TEST_SESSION_FILTER));
    }
}
