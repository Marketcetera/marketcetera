package org.marketcetera.util.except;

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
            ("Thread execution was interrupted",
             Messages.THREAD_INTERRUPTED.getText());
        assertEquals
            ("Caught throwable was not propagated",
             Messages.THROWABLE_IGNORED.getText());
    }
}
