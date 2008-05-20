package org.marketcetera.util.misc;

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
            ("User: ",
             Messages.USER_PROMPT.getText());
        assertEquals
            ("user name",
             Messages.USER_DESCRIPTION.getText());
        assertEquals
            ("Password: ",
             Messages.PASSWORD_PROMPT.getText());
        assertEquals
            ("password",
             Messages.PASSWORD_DESCRIPTION.getText());
        assertEquals
            ("Parsing of the command line has failed",
             Messages.PARSING_FAILED.getText());
        assertEquals
            ("No user was specified",
             Messages.NO_USER.getText());
        assertEquals
            ("No password was specified",
             Messages.NO_PASSWORD.getText());
    }
}
