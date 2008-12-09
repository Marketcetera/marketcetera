package org.marketcetera.util.ws.stateless;

import org.junit.Test;
import org.marketcetera.util.log.ActiveLocale;
import org.marketcetera.util.ws.tags.TagFilter;

/**
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id$
 */

/* $License$ */

public class StatelessRemoteRunnerTest
    extends StatelessRemoteCallTestBase
{
    private static final class IntRunner
        extends StatelessRemoteRunner
    {
        @Override
        protected void run
            (StatelessClientContext context)
        {
            setRunnerData(TEST_INT);
        }
    }

    private static final class LocaleRunner
        extends StatelessRemoteRunner
    {
        @Override
        protected void run
            (StatelessClientContext context)
        {
            setRunnerData(ActiveLocale.getLocale());
        }
    }

    private static final class ThrowRunner
        extends StatelessRemoteRunner
    {
        @Override
        protected void run
            (StatelessClientContext context)
        {
            throw TEST_EXCEPTION;
        }
    }

    private static final class ThrowFilterRunner
        extends StatelessRemoteRunner
    {
        public ThrowFilterRunner
            (TagFilter versionIdFilter,
             TagFilter appIdFilter,
             TagFilter clientIdFilter)
        {
            super(versionIdFilter,appIdFilter,clientIdFilter);
        }

        public ThrowFilterRunner() {}

        @Override
        protected void run
            (StatelessClientContext context)
        {
            setRunnerData(TEST_INT);
        }
    }


    @Test
    public void all()
        throws Exception
    {
        single
            (new ThrowFilterRunner
             (TEST_VERSION_FILTER,TEST_APP_FILTER,TEST_CLIENT_FILTER),
             new ThrowFilterRunner(null,null,null),
             new ThrowFilterRunner());

        StatelessClientContext context=new StatelessClientContext();
        calls
            (context,
             new IntRunner(),
             new LocaleRunner(),
             new ThrowRunner(),
             new ThrowFilterRunner(TEST_VERSION_FILTER,null,null),
             new ThrowFilterRunner(null,TEST_APP_FILTER,null),
             new ThrowFilterRunner(null,null,TEST_CLIENT_FILTER));
    }
}
