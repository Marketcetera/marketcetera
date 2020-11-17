package org.marketcetera.util.ws;

import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Provides context classes with which to start a server.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: ContextClassProvider.java 16901 2014-05-11 16:14:11Z colin $
 * @since 2.4.0
 */
@ClassVersion("$Id: ContextClassProvider.java 16901 2014-05-11 16:14:11Z colin $")
public interface ContextClassProvider
{
    /**
     * Gets the context classes used to marshal/unmarshal objects.
     *
     * @return a <code>Class&lt;?&gt;[]</code> value
     */
    Class<?>[] getContextClasses();
}
