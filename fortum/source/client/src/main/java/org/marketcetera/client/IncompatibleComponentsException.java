package org.marketcetera.client;

import org.marketcetera.util.except.I18NException;
import org.marketcetera.util.log.I18NBoundMessage;
import org.marketcetera.util.misc.ClassVersion;

/**
 * Represents errors encountered by the client when communicating with
 * an incompatible server.
 *
 * @author tlerios@marketcetera.com
 * @since $Release$
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$")
public class IncompatibleComponentsException
    extends I18NException
{

    // CLASS DATA.

    private static final long serialVersionUID=1L;


    // INSTANCE DATA.

    private final String mServerVersion;


    // CONSTRUCTORS.

    /**
     * Constructs a new exception with the given message and referring
     * to a server with the given version.
     *
     * @param message The message.
     * @param serverVersion The version.
     */

    public IncompatibleComponentsException
        (I18NBoundMessage message,
         String serverVersion)
    {
        super(message);
        mServerVersion=serverVersion;
    }


    // INSTANCE METHODS.

    /**
     * Returns the receiver's server version.
     *
     * @return The version.
     */

    public String getServerVersion()
    {
        return mServerVersion;
    }
}
