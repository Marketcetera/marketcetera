package com.marketcetera.ors.brokers.impl;

import javax.annotation.PostConstruct;
import javax.annotation.concurrent.NotThreadSafe;

import org.apache.commons.lang.Validate;
import org.springframework.beans.factory.annotation.Autowired;

import com.marketcetera.fix.FixSession;
import com.marketcetera.fix.SessionConnector;
import com.marketcetera.fix.SessionConnectorFactory;
import com.marketcetera.matp.service.ClusterService;

/* $License$ */

/**
 * Constructs <code>SessionConnector</code> values from <code>Brokers</code> values
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: ClusteredSessionConnectorFactory.java 85105 2016-01-22 20:43:42Z colin $
 * @since 1.0.1
 */
@NotThreadSafe
public class ClusteredSessionConnectorFactory
        implements SessionConnectorFactory
{
    /**
     * Validates and starts the object.
     */
    @PostConstruct
    public void start()
    {
        Validate.notNull(clusterService,
                         "Cluster service is required");
    }
    /* (non-Javadoc)
     * @see com.marketcetera.tiaacref.irouter.broker.SessionConnectorFactory#create(com.marketcetera.ors.brokers.FixSession)
     */
    @Override
    public SessionConnector create(FixSession inSession)
    {
        ClusteredSessionConnector connector = new ClusteredSessionConnector(inSession,
                                                                            clusterService);
        return connector;
    }
    /**
     * Get the clusterService value.
     *
     * @return a <code>ClusterService</code> value
     */
    public ClusterService getClusterService()
    {
        return clusterService;
    }
    /**
     * Sets the clusterService value.
     *
     * @param inClusterService a <code>ClusterService</code> value
     */
    public void setClusterService(ClusterService inClusterService)
    {
        clusterService = inClusterService;
    }
    /**
     * provides access to cluster services
     */
    @Autowired
    private ClusterService clusterService;
}
