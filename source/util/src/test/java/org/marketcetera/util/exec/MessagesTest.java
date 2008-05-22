package org.marketcetera.util.exec;

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
            ("Cannot copy output of command 'a'",
             Messages.CANNOT_COPY_OUTPUT.getText("a"));
        assertEquals
            ("Cannot execute command 'a'",
             Messages.CANNOT_EXECUTE.getText("a"));
        assertEquals
            ("Unexpected termination of command 'a'",
             Messages.UNEXPECTED_TERMINATION.getText("a"));
    }
}
