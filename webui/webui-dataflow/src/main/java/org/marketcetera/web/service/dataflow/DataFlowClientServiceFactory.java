package org.marketcetera.web.service.dataflow;

import org.marketcetera.web.config.dataflows.DataFlowConfiguration;
import org.marketcetera.web.service.ConnectableServiceFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

/* $License$ */

/**
 * Creates @{link DataFlowClientService} objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@Service
public class DataFlowClientServiceFactory
        implements ConnectableServiceFactory<DataFlowClientService>
{
    /* (non-Javadoc)
     * @see org.marketcetera.web.services.ConnectableServiceFactory#create()
     */
    @Override
    public DataFlowClientService create()
    {
        DataFlowClientService service = new DataFlowClientService();
        service.setApplicationContext(applicationContext);
        service.setDataFlowConfiguration(dataFlowConfiguration);
        return service;
    }
    /**
     * provides access to application configuration
     */
    @Autowired
    private ApplicationContext applicationContext;
    /**
     * provides configuration for connecting to data flow engines
     */
    @Autowired
    private DataFlowConfiguration dataFlowConfiguration;
}
