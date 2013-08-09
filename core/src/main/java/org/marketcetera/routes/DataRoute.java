package org.marketcetera.routes;

import java.io.Serializable;

import org.marketcetera.service.Service;


/* $License$ */

/**
 * Provides access to a {@link Service}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface DataRoute
{
    public void route(Serializable inData);
}
