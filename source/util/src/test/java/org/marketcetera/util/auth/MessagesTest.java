package org.marketcetera.util.auth;

import java.util.Locale;
import org.junit.Test;
import org.marketcetera.util.log.I18NMessageProvider;
import org.marketcetera.util.test.TestCaseBase;

import static org.junit.Assert.*;

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
        assertEquals
            ("Context failed to authenticate",
             Messages.CONTEXT_FAILED.getText());
        assertEquals
            ("(anonymous context)",
             Messages.CONTEXT_ANONYMOUS.getText());
        assertEquals
            ("(overriding context)",
             Messages.CONTEXT_OVERRIDES.getText());
        assertEquals
            ("The console is unavailable",
             Messages.CONSOLE_UNAVAILABLE.getText());
        assertEquals
            ("Parsing of the command line failed",
             Messages.PARSING_FAILED.getText());

        assertEquals
            ("Spring framework",
             Messages.SPRING_NAME.getText());
        assertEquals
            ("Command-line options",
             Messages.CLI_NAME.getText());
        assertEquals
            ("Console terminal",
             Messages.CONSOLE_NAME.getText());

        assertEquals
            ("User: ",
             Messages.USER_PROMPT.getText());
        assertEquals
            ("user name",
             Messages.USER_DESCRIPTION.getText());
        assertEquals
            ("Set 'a' to username in properties file",
             Messages.USER_SPRING_USAGE.getText("a"));
        assertEquals
            ("-a or -b followed by username",
             Messages.USER_CLI_USAGE.getText("a","b"));
        assertEquals
            ("Type username when prompted",
             Messages.USER_CONSOLE_USAGE.getText());
        assertEquals
            ("No user was specified",
             Messages.NO_USER.getText());

        assertEquals
            ("Password: ",
             Messages.PASSWORD_PROMPT.getText());
        assertEquals
            ("password",
             Messages.PASSWORD_DESCRIPTION.getText());
        assertEquals
            ("Set 'a' to password in properties file",
             Messages.PASSWORD_SPRING_USAGE.getText("a"));
        assertEquals
            ("-a or -b followed by password",
             Messages.PASSWORD_CLI_USAGE.getText("a","b"));
        assertEquals
            ("Type password when prompted (password won't echo)",
             Messages.PASSWORD_CONSOLE_USAGE.getText());
        assertEquals
            ("No password was specified",
             Messages.NO_PASSWORD.getText());
    }
}
