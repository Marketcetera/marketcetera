package org.marketcetera.util.ws.stateful;

import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Provides access to a {@link Server} object.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 2.4.0
 */
@ClassVersion("$Id$")
public interface ServerProvider<T>
{
    /**
     * Gets the server value.
     *
     * @return a <code>Server&lt;T&gt;</code> value
     */
    Server<T> getServer();
}
