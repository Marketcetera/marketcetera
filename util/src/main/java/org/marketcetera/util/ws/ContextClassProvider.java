package org.marketcetera.util.ws;

import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Provides context classes with which to start a server.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 2.4.0
 */
@ClassVersion("$Id$")
public interface ContextClassProvider
{
    /**
     * Gets the context classes used to marshal/unmarshal objects.
     *
     * @return a <code>Class&lt;?&gt;[]</code> value
     */
    Class<?>[] getContextClasses();
}
