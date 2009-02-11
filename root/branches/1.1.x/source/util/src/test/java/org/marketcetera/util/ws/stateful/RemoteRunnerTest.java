package org.marketcetera.util.ws.stateful;

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

public class RemoteRunnerTest
    extends RemoteCallTestBase
{
    private static final class IntRunner
        extends RemoteRunner<Object>
    {
        public IntRunner()
        {
            super(TEST_MANAGER);
        }

        @Override
        protected void run
            (ClientContext context,
             SessionHolder<Object> sessionHolder)
        {
            assertEquals(TEST_HOLDER,sessionHolder);
            setRunnerData(TEST_INT);
        }
    }

    private static final class LocaleRunner
        extends RemoteRunner<Object>
    {
        public LocaleRunner()
        {
            super(TEST_MANAGER);
        }

        @Override
        protected void run
            (ClientContext context,
             SessionHolder<Object> sessionHolder)
        {
            assertEquals(TEST_HOLDER,sessionHolder);
            setRunnerData(ActiveLocale.getLocale());
        }
    }

    private static final class ThrowRunner
        extends RemoteRunner<Object>
    {
        public ThrowRunner()
        {
            super(TEST_MANAGER);
        }

        @Override
        protected void run
            (ClientContext context,
             SessionHolder<Object> sessionHolder)
        {
            assertEquals(TEST_HOLDER,sessionHolder);
            throw TEST_EXCEPTION;
        }
    }

    private static final class ThrowFilterRunner
        extends RemoteRunner<Object>
    {
        public ThrowFilterRunner
            (TagFilter versionIdFilter,
             TagFilter appIdFilter,
             TagFilter clientIdFilter,
             SessionManager<Object> sessionManager,
             TagFilter sessionIdFilter)
        {
            super(versionIdFilter,appIdFilter,clientIdFilter,
                  sessionManager,sessionIdFilter);
        }

        public ThrowFilterRunner()
        {
            super(TEST_MANAGER);
        }

        @Override
        protected void run
            (ClientContext context,
             SessionHolder<Object> sessionHolder)
        {
            assertNull(sessionHolder);
            setRunnerData(TEST_INT);
        }
    }


    @Test
    public void all()
        throws Exception
    {
        single
            (new ThrowFilterRunner
             (TEST_VERSION_FILTER,TEST_APP_FILTER,TEST_CLIENT_FILTER,
              TEST_MANAGER,TEST_SESSION_FILTER),
             new ThrowFilterRunner(null,null,null,null,null),
             new ThrowFilterRunner());

        ClientContext context=new ClientContext();
        calls
            (context,
             new IntRunner(),
             new LocaleRunner(),
             new ThrowRunner(),
             new ThrowFilterRunner(TEST_VERSION_FILTER,null,null,null,null),
             new ThrowFilterRunner(null,TEST_APP_FILTER,null,null,null),
             new ThrowFilterRunner(null,null,TEST_CLIENT_FILTER,null,null),
             new ThrowFilterRunner(null,null,null,null,TEST_SESSION_FILTER));
    }
}
