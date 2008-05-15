package org.marketcetera.util.log;

import java.util.Locale;
import org.junit.Test;
import org.marketcetera.util.test.TestCaseBase;

import static org.junit.Assert.*;

public class MessagesTest
    extends TestCaseBase
{
    @Test
    public void messagesExist()
    {
        Messages.PROVIDER.setLocale(Locale.US);
        assertEquals
            ("Message not found: provider 'a'; id 'b'; entry 'c'; "+
             "parameters d",
             Messages.MESSAGE_NOT_FOUND.getText("a","b","c","d"));
        assertEquals
            ("Unexpected exception: provider 'a'; id 'b'; entry 'c'; "+
             "parameters d",
             Messages.UNEXPECTED_EXCEPTION.getText("a","b","c","d"));
    }
}
