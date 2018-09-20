package org.marketcetera.web.view.cluster;

import java.util.Collection;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.marketcetera.cluster.ClusterClient;
import org.marketcetera.cluster.ClusterRpcClientFactory;
import org.marketcetera.cluster.ClusterRpcClientParameters;
import org.marketcetera.cluster.service.ClusterMember;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.web.services.ConnectableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/* $License$ */

/**
 * Provides client client services.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@Service
public class ClusterClientService
        implements ConnectableService
{
    /* (non-Javadoc)
     * @see org.marketcetera.web.services.ConnectableService#connect(java.lang.String, java.lang.String, java.lang.String, int)
     */
    @Override
    public boolean connect(String inUsername,
                           String inPassword,
                           String inHostname,
                           int inPort)
            throws Exception
    {
        if(clusterClient != null) {
            try {
                clusterClient.stop();
            } catch (Exception e) {
                SLF4JLoggerProxy.warn(this,
                                      "Unable to stop existing cluster client for {}: {}",
                                      inUsername,
                                      ExceptionUtils.getRootCauseMessage(e));
            } finally {
                clusterClient = null;
            }
        }
        SLF4JLoggerProxy.debug(this,
                               "Creating cluster client for {} to {}:{}",
                               inUsername,
                               inHostname,
                               inPort);
        ClusterRpcClientParameters params = new ClusterRpcClientParameters();
        params.setHostname(inHostname);
        params.setPort(inPort);
        params.setUsername(inUsername);
        params.setPassword(inPassword);
        clusterClient = clusterClientFactory.create(params);
        clusterClient.start();
        return clusterClient.isRunning();
    }
    /**
     * Get the cluster members.
     *
     * @return a <code>Collection&lt;ClusterMember&gt;</code> value
     */
    public Collection<ClusterMember> getClusterMembers()
    {
        return clusterClient.getClusterMembers();
    }
    /**
     * Validate and start the object.
     */
    @PostConstruct
    public void start()
    {
        SLF4JLoggerProxy.info(this,
                              "Starting cluster client service");
        instance = this;
    }
    /**
     * Get the instance value.
     *
     * @return a <code>ClusterClientService</code> value
     */
    public static ClusterClientService getInstance()
    {
        return instance;
    }
    /**
     * creates {@link ClusterClient} objects
     */
    @Autowired
    private ClusterRpcClientFactory clusterClientFactory;
    /**
     * instance value
     */
    private static ClusterClientService instance;
    /**
     * provides access to cluster services
     */
    private ClusterClient clusterClient;
}
