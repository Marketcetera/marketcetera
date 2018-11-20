package org.marketcetera.core;

import org.marketcetera.util.ws.ContextClassProvider;

/* $License$ */

/**
 * Indicates that the implementing class has a contextual class provider.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface ContextualClientParameters
{
    /**
     * Get the contextClassProvider value.
     *
     * @return a <code>ContextClassProvider</code> value
     */
    ContextClassProvider getContextClassProvider();
}
