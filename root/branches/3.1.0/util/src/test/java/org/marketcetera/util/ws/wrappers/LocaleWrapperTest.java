package org.marketcetera.util.ws.wrappers;

import java.util.Locale;
import org.junit.Test;

/**
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id: LocaleWrapperTest.java 82384 2012-07-20 19:09:59Z colin $
 */

/* $License$ */

public class LocaleWrapperTest
    extends WrapperTestBase
{
    private static final Locale TEST_LOCALE=
        MarshalledLocaleTest.TEST_LOCALE;


    @Test
    public void all()
        throws Exception
    {
        dual(new LocaleWrapper(TEST_LOCALE),
             new LocaleWrapper(TEST_LOCALE),
             new LocaleWrapper(),
             new LocaleWrapper(null),
             TEST_LOCALE.toString(),
             TEST_LOCALE,new MarshalledLocale(TEST_LOCALE));
    }
}
