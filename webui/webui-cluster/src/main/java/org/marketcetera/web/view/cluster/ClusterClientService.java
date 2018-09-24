package org.marketcetera.web.view.cluster;

import java.util.Collection;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.marketcetera.cluster.ClusterClient;
import org.marketcetera.cluster.ClusterRpcClientFactory;
import org.marketcetera.cluster.ClusterRpcClientParameters;
import org.marketcetera.cluster.service.ClusterMember;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.web.service.ConnectableService;

import com.vaadin.server.VaadinSession;

/* $License$ */

/**
 * Provides client client services.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
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
        if(clusterClient.isRunning()) {
            VaadinSession.getCurrent().setAttribute(ClusterClientService.class,
                                                    this);
        }
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
     * Get the instance value.
     *
     * @return a <code>ClusterClientService</code> value
     */
    public static ClusterClientService getInstance()
    {
        return VaadinSession.getCurrent().getAttribute(ClusterClientService.class);
    }
    /**
     * Get the clusterClientFactory value.
     *
     * @return a <code>ClusterRpcClientFactory</code> value
     */
    public ClusterRpcClientFactory getClusterClientFactory()
    {
        return clusterClientFactory;
    }
    /**
     * Sets the clusterClientFactory value.
     *
     * @param inClusterClientFactory a <code>ClusterRpcClientFactory</code> value
     */
    public void setClusterClientFactory(ClusterRpcClientFactory inClusterClientFactory)
    {
        clusterClientFactory = inClusterClientFactory;
    }
    /**
     * creates {@link ClusterClient} objects
     */
    private ClusterRpcClientFactory clusterClientFactory;
    /**
     * provides access to cluster services
     */
    private ClusterClient clusterClient;
}
