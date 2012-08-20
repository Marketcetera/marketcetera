package org.marketcetera.security;

import java.io.Console;

import org.marketcetera.core.util.except.I18NException;
import org.marketcetera.core.util.log.I18NBoundMessage;
import org.marketcetera.api.attributes.ClassVersion;

/**
 * A context for console setters ({@link ConsoleSetter}).
 *
 * @author tlerios@marketcetera.com
 * @since 0.5.0
 * @version $Id: ConsoleContext.java 82324 2012-04-09 20:56:08Z colin $
 */

/* $License$ */

@ClassVersion("$Id: ConsoleContext.java 82324 2012-04-09 20:56:08Z colin $")
public class ConsoleContext
    extends Context<ConsoleSetter<?>>
{

    // CONSTRUCTORS.

    /**
     * Constructor mirroring superclass constructor.
     *
     * @see Context#Context(I18NBoundMessage,boolean)
     */

    public ConsoleContext
        (I18NBoundMessage name,
         boolean override)
    {
        super(name,override);
    }

    /**
     * Constructor mirroring superclass constructor. The context name
     * is set automatically to a default value.
     *
     * @see Context#Context(I18NBoundMessage,boolean)
     */

    public ConsoleContext
        (boolean override)
    {
        this(Messages.CONSOLE_NAME,override);
    }


    // Context.

    @Override
    public void setValues()
        throws I18NException
    {
        Console console=System.console();
        if (console==null) {
            throw new I18NException(Messages.CONSOLE_UNAVAILABLE);
        }
        for (ConsoleSetter<?> setter:getSetters()) {
            if (shouldProcess(setter)) {
                setter.setValue(console);
            }
        }
    }
}
