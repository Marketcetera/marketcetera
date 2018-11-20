package org.marketcetera.core;

import org.marketcetera.util.ws.ContextClassProvider;

/* $License$ */

/**
 * Provides client parameters that include an XML context provider.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class BaseClientContextualParameters
        extends BaseClientParameters
        implements ContextualClientParameters
{
    /* (non-Javadoc)
     * @see org.marketcetera.core.ContextualClientParameters#getContextClassProvider()
     */
    @Override
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
