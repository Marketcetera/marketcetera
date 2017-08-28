package org.marketcetera.rpc.client;

import org.marketcetera.util.ws.ContextClassProvider;

/* $License$ */

/**
 * Provides a context-aware {@link BaseRpcClientParameters} implementation.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public abstract class AbstractContextAwareRpcClientParameters
        extends BaseRpcClientParameters
{
    /**
     * Get the contextClassProvider value.
     *
     * @return a <code>ContextClassProvider</code> value
     */
    public ContextClassProvider getContextClassProvider()
    {
        return contextClassProvider;
    }
    /**
     * Sets the contextClassProvider value.
     *
     * @param a <code>ContextClassProvider</code> value
     */
    public void setContextClassProvider(ContextClassProvider inContextClassProvider)
    {
        contextClassProvider = inContextClassProvider;
    }
    /**
     * provides needed context classes
     */
    private ContextClassProvider contextClassProvider;
}
