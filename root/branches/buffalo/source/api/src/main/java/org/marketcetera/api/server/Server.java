package org.marketcetera.api.server;

import org.springframework.context.Lifecycle;

/* $License$ */

/**
 * Provides Marketcetera server services to Marketcetera components.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface Server
        extends Lifecycle
{
    /**
     * Gets the <code>ContextValidator</code> to use.
     *
     * @return a <code>ContextValidator</code> value
     */
    public ContextValidator getContextValidator();
}
