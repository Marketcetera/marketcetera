package org.marketcetera.server.service;

import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public interface OrderValidatorFactory
{
    /**
     * 
     *
     *
     * @return
     */
    public OrderValidator getValidator();
}
