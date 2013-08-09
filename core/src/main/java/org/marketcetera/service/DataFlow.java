package org.marketcetera.service;

import java.util.List;

import org.marketcetera.routes.DataRoute;



/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface DataFlow
{
    public long getId();
    public String getName();
    public void setRoutes(List<DataRoute> inRoutes);
    public List<DataRoute> getRoutes();
}
