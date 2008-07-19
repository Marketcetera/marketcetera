package org.marketcetera.util.unicode;

import java.util.Locale;
import org.junit.Test;
import org.marketcetera.util.log.I18NMessageProvider;
import org.marketcetera.util.test.TestCaseBase;

import static org.junit.Assert.*;

/**
 * @author tlerios@marketcetera.com
 * @since 0.6.0
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
        assertEquals
            ("The charset 'a' is unknown",
             Messages.UNKNOWN_CHARSET.getText("a"));
        assertEquals
            ("The stream is closed",
             Messages.STREAM_CLOSED.getText());
        assertEquals
            ("Encountered an error while accessing the stream",
             Messages.STREAM_ACCESS_ERROR.getText());
        assertEquals
            ("Cannot read size of file 'a'",
             Messages.CANNOT_GET_LENGTH.getText("a"));
        assertEquals
            ("No signature matches the given byte array",
             Messages.NO_SIGNATURE_MATCHES.getText());
    }
}
