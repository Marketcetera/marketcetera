package org.marketcetera.util.auth;

import java.io.Console;
import org.apache.commons.lang.StringUtils;
import org.marketcetera.util.log.I18NBoundMessage;
import org.marketcetera.util.misc.ClassVersion;

/**
 * A setter for a string holder that obtains the data via the
 * console. This setter echoes the characters typed by the user.
 *
 * @author tlerios@marketcetera.com
 * @since 0.5.0
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$")
public class ConsoleSetterString
    extends ConsoleSetter<Holder<String>>
{

    // CONSTRUCTORS.

    /**
     * Constructor mirroring superclass constructor.
     *
     * @see ConsoleSetter#ConsoleSetter(Holder,I18NBoundMessage,I18NBoundMessage)
     */

    public ConsoleSetterString
        (Holder<String> holder,
         I18NBoundMessage usage,
         I18NBoundMessage prompt)
    {
        super(holder,usage,prompt);
    }


    // ConsoleSetter.

    @Override
    public void setValue
        (Console console)
    {
        String value=console.readLine
            ("%s",getPrompt().getText()); //$NON-NLS-1$
        if ((value!=null) && !StringUtils.EMPTY.equals(value)) {
            getHolder().setValue(value);
        }
    }
}
