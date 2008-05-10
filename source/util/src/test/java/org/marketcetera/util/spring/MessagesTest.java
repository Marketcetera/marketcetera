package org.marketcetera.util.spring;

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
    }
}
