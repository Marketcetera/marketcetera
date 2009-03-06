package org.marketcetera.util.auth;

import java.io.Console;
import org.marketcetera.util.log.I18NBoundMessage;
import org.marketcetera.util.misc.ClassVersion;

/**
 * A setter that obtains holder data via the console. It is supported
 * by a {@link ConsoleContext}.
 *
 * @author tlerios@marketcetera.com
 * @since 0.5.0
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$")
public abstract class ConsoleSetter<T extends Holder<?>>
    extends Setter<T>
{

    // INSTANCE DATA.

    private I18NBoundMessage mPrompt;


    // CONSTRUCTORS.

    /**
     * Constructor mirroring superclass constructor. The given prompt
     * is displayed on the console when the user is queried for the
     * holder's data.
     *
     * @param prompt The prompt.
     *
     * @see Setter#Setter(Holder,I18NBoundMessage)
     */

    public ConsoleSetter
        (T holder,
         I18NBoundMessage usage,
         I18NBoundMessage prompt)
    {
        super(holder,usage);
        mPrompt=prompt;
    }


    // INSTANCE METHODS.

    /**
     * Returns the receiver's prompt.
     *
     * @return The prompt.
     */

    public I18NBoundMessage getPrompt()
    {
        return mPrompt;
    }
    
    /**
     * Sets the holder's data by obtaining it from the given console,
     * after displaying the receiver's prompt. This method is called
     * by a {@link ConsoleContext}.
     *
     * @param console The console.
     */

    public abstract void setValue
        (Console console);
}
