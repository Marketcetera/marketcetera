package org.marketcetera.util.except;

/**
 * @author tlerios@marketcetera.com
 * @since 0.5.0
 * @version $Id$
 */

/* $License$ */

import java.util.Locale;
import org.junit.Test;
import org.marketcetera.util.log.I18NMessageProvider;
import org.marketcetera.util.test.TestCaseBase;

import static org.junit.Assert.*;

public class MessagesTest
    extends TestCaseBase
{
    @Test
    public void messagesExist()
    {
        I18NMessageProvider.setLocale(Locale.US);
        assertEquals
            ("Thread execution was interrupted",
             Messages.THREAD_INTERRUPTED.getText());
        assertEquals
            ("Caught throwable was not propagated",
             Messages.THROWABLE_IGNORED.getText());
    }
}
