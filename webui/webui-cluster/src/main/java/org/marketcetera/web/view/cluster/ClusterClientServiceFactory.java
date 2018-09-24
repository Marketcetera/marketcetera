package org.marketcetera.web.view.cluster;

import org.marketcetera.cluster.ClusterClient;
import org.marketcetera.cluster.ClusterRpcClientFactory;
import org.marketcetera.web.service.ConnectableServiceFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/* $License$ */

/**
 * Creates {@link ClusterClientService} objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@Service
public class ClusterClientServiceFactory
        implements ConnectableServiceFactory<ClusterClientService>
{
    /* (non-Javadoc)
     * @see org.marketcetera.web.services.ConnectableServiceFactory#create()
     */
    @Override
    public ClusterClientService create()
    {
        ClusterClientService service = new ClusterClientService();
        service.setClusterClientFactory(clusterClientFactory);
        return service;
    }
    /**
     * creates {@link ClusterClient} objects
     */
    @Autowired
    private ClusterRpcClientFactory clusterClientFactory;
}
