package org.marketcetera.web.service;

/* $License$ */

/**
 * Creates {@link ConnectableService} objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface ConnectableServiceFactory<ServiceClazz extends ConnectableService>
{
    /**
     * Create a <code>ServiceClazz</code> object.
     *
     * @return a <code>ServiceClazz</code> value
     */
    ServiceClazz create();
}
