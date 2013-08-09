package org.marketcetera.service;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface ServiceLocator<ServiceType extends Service>
{
    public ServiceType locate();
}
