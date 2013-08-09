package org.marketcetera.service;

import java.util.List;
import java.util.Set;

import org.springframework.context.SmartLifecycle;

/* $License$ */

/**
 * Provides access to all data flows running in this <code>Agent</code>.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface ServiceManager
        extends SmartLifecycle
{
    public Set<DataFlow> getRunningDataFlows();
    public Set<DataFlow> getAllDataFlows();
    public Set<Service> getRunningServices();
    public Set<Service> getAllServices();
    public void addDataFlow(DataFlow inDataFlow);
    public void removeDataFlow(DataFlow inDataFlow);
    public void startDataFlow(DataFlow inDataFlow);
    public void stopDataFlow(DataFlow inDataFlow);
    public void setDataFlows(List<DataFlow> inDataFlows);
    public void startService(Service inService);
    public void stopService(Service inService);
    public void addService(Service inService);
    public void removeService(Service inService);
    public void setServices(List<Service> inServices);
}
