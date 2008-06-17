package org.marketcetera.util.misc;

import java.util.Locale;
import org.junit.Test;
import org.marketcetera.util.log.I18NMessageProvider;
import org.marketcetera.util.test.TestCaseBase;

/**
 * @author tlerios@marketcetera.com
 * @since 0.5.0
 * @version $Id$
 */

/* $License$ */

public class MessagesTest
    extends TestCaseBase
{
    @Test
    public void messagesExist()
    {
        I18NMessageProvider.setLocale(Locale.US);
    }
}
