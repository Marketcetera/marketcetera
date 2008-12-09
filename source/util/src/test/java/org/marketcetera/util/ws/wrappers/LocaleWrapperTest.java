package org.marketcetera.util.ws.wrappers;

import java.util.Locale;
import org.junit.Test;

/**
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id$
 */

/* $License$ */

public class LocaleWrapperTest
    extends WrapperTestBase
{
    private static final Locale TEST_LOCALE=
        MarshalledLocaleTest.TEST_LOCALE;


    @Test
    public void all()
    {
        dual(new LocaleWrapper(TEST_LOCALE),
             new LocaleWrapper(TEST_LOCALE),
             new LocaleWrapper(),
             new LocaleWrapper(null),
             TEST_LOCALE.toString(),
             TEST_LOCALE,new MarshalledLocale(TEST_LOCALE));
    }
}
