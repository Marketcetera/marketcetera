package org.marketcetera.api.server;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface ContextValidator
{
    /**
     * 
     *
     *
     * @param inContext
     */
    public void validate(ClientContext inContext);
}
