package org.marketcetera.util.ws.stateless;

import java.util.Locale;
import org.junit.Test;
import org.marketcetera.util.log.ActiveLocale;
import org.marketcetera.util.ws.tags.TagFilter;

/**
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id$
 */

/* $License$ */

public class StatelessRemoteCallerTest
    extends StatelessRemoteCallTestBase
{
    private static final class IntCaller
        extends StatelessRemoteCaller<Integer>
    {
        @Override
        protected Integer call
            (StatelessClientContext context)
        {
            return TEST_INT;
        }
    }

    private static final class LocaleCaller
        extends StatelessRemoteCaller<Locale>
    {
        @Override
        protected Locale call
            (StatelessClientContext context)
        {
            return ActiveLocale.getLocale();
        }
    }

    private static final class ThrowCaller
        extends StatelessRemoteCaller<Integer>
    {
        @Override
        protected Integer call
            (StatelessClientContext context)
        {
            throw TEST_EXCEPTION;
        }
    }

    private static final class ThrowFilterCaller
        extends StatelessRemoteCaller<Integer>
    {
        public ThrowFilterCaller
            (TagFilter versionIdFilter,
             TagFilter appIdFilter,
             TagFilter clientIdFilter)
        {
            super(versionIdFilter,appIdFilter,clientIdFilter);
        }

        public ThrowFilterCaller() {}

        @Override
        protected Integer call
            (StatelessClientContext context)
        {
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
             (TEST_VERSION_FILTER,TEST_APP_FILTER,TEST_CLIENT_FILTER),
             new ThrowFilterCaller(null,null,null),
             new ThrowFilterCaller());

        StatelessClientContext context=new StatelessClientContext();
        calls
            (context,
             new IntCaller(),
             new LocaleCaller(),
             new ThrowCaller(),
             new ThrowFilterCaller(TEST_VERSION_FILTER,null,null),
             new ThrowFilterCaller(null,TEST_APP_FILTER,null),
             new ThrowFilterCaller(null,null,TEST_CLIENT_FILTER));
    }
}
