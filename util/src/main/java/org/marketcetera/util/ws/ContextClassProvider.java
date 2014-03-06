package org.marketcetera.util.ws;

import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Provides context classes with which to start a server.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public interface ContextClassProvider
{
    /**
     * 
     *
     *
     * @return
     */
    Class<?>[] getContextClasses();
}
