package org.marketcetera.util.ws.stateless;

import java.util.Locale;
import org.junit.Before;
import org.marketcetera.util.log.ActiveLocale;
import org.marketcetera.util.test.TestCaseBase;
import org.marketcetera.util.ws.tags.AppId;
import org.marketcetera.util.ws.tags.NodeId;
import org.marketcetera.util.ws.tags.VersionId;
import org.marketcetera.util.ws.tags.VersionIdTest;
import org.marketcetera.util.ws.wrappers.LocaleWrapper;

import static org.junit.Assert.*;
import static org.marketcetera.util.test.EqualityAssert.*;

/**
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id$
 */

/* $License$ */

public class ClientContextTestBase
    extends TestCaseBase
{
    private static final VersionId TEST_VERSION=
        VersionIdTest.TEST_VERSION;
    private static final VersionId TEST_VERSION_D=
        VersionIdTest.TEST_VERSION_D;
    private static final AppId TEST_APP=
        new AppId("testApp");
    private static final AppId TEST_APP_D=
        new AppId("testAppD");
    private static final NodeId TEST_CLIENT=
        NodeId.generate();
    private static final NodeId TEST_CLIENT_D=
        NodeId.generate();
    private static final LocaleWrapper TEST_LOCALE=
        new LocaleWrapper(new Locale("la","CO","va"));
    private static final LocaleWrapper TEST_LOCALE_D=
        new LocaleWrapper(new Locale("lad","COD","vad"));


    @Before
    public void setupClientContextTestBase()
    {
        ActiveLocale.setProcessLocale(Locale.ROOT);
    }


    protected static void fillContext
        (StatelessClientContext context)
    {
        context.setVersionId(TEST_VERSION);
        context.setAppId(TEST_APP);
        context.setClientId(TEST_CLIENT);
        context.setLocale(TEST_LOCALE);
    }

    protected static void single
        (StatelessClientContext context,
         StatelessClientContext copy,
         StatelessClientContext empty,
         String suffix)
    {
        assertEquality(context,copy,empty);

        assertEquals(TEST_VERSION,context.getVersionId());
        assertEquals(TEST_APP,context.getAppId());
        assertEquals(TEST_CLIENT,context.getClientId());
        assertEquals(TEST_LOCALE,context.getLocale());
        assertEquals("Protocol version testVersion; application testApp; "+
                     "client "+TEST_CLIENT+"; locale 'la_CO_va'"+suffix,
                     context.toString());

        assertNull(empty.getVersionId());
        assertNull(empty.getAppId());
        assertNull(empty.getClientId());
        assertNull(empty.getLocale());

        context.setVersionId(TEST_VERSION_D);
        assertEquals(TEST_VERSION_D,context.getVersionId());
        assertEquals("Protocol version testVersionD; application testApp; "+
                     "client "+TEST_CLIENT+"; locale 'la_CO_va'"+suffix,
                     context.toString());

        context.setVersionId(null);
        assertNull(context.getVersionId());
        assertEquals("Protocol version null; application testApp; "+
                     "client "+TEST_CLIENT+"; locale 'la_CO_va'"+suffix,
                     context.toString());
        
        context.setAppId(TEST_APP_D);
        assertEquals(TEST_APP_D,context.getAppId());
        assertEquals("Protocol version null; application testAppD; "+
                     "client "+TEST_CLIENT+"; locale 'la_CO_va'"+suffix,
                     context.toString());

        context.setAppId(null);
        assertNull(context.getAppId());
        assertEquals("Protocol version null; application null; "+
                     "client "+TEST_CLIENT+"; locale 'la_CO_va'"+suffix,
                     context.toString());

        context.setClientId(TEST_CLIENT_D);
        assertEquals(TEST_CLIENT_D,context.getClientId());
        assertEquals("Protocol version null; application null; "+
                     "client "+TEST_CLIENT_D+"; locale 'la_CO_va'"+suffix,
                     context.toString());

        context.setClientId(null);
        assertNull(context.getClientId());
        assertEquals("Protocol version null; application null; "+
                     "client null; locale 'la_CO_va'"+suffix,
                     context.toString());

        context.setLocale(TEST_LOCALE_D);
        assertEquals(TEST_LOCALE_D,context.getLocale());
        assertEquals("Protocol version null; application null; "+
                     "client null; locale 'lad_COD_vad'"+suffix,
                     context.toString());

        LocaleWrapper wrapper=new LocaleWrapper(null);
        context.setLocale(wrapper);
        assertEquals(wrapper,context.getLocale());
        assertEquals("Protocol version null; application null; "+
                     "client null; locale ''"+suffix,
                     context.toString());

        context.setLocale(null);
        assertNull(context.getLocale());
        assertEquals("Protocol version null; application null; "+
                     "client null; locale ''"+suffix,
                     context.toString());
    }
}
