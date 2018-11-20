package org.marketcetera.trade;

import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.annotation.PostConstruct;
import javax.annotation.concurrent.GuardedBy;

import org.apache.commons.lang3.Validate;
import org.marketcetera.brokers.Selector;
import org.marketcetera.brokers.service.BrokerService;
import org.marketcetera.brokers.service.FixSessionProvider;
import org.marketcetera.cluster.ClusterData;
import org.marketcetera.cluster.service.ClusterService;
import org.marketcetera.core.CloseableLock;
import org.marketcetera.fix.FixSession;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.springframework.beans.factory.annotation.Autowired;

/* $License$ */

/**
 * Makes a basic choice for a broker.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class BasicSelector
        implements Selector
{
    /* (non-Javadoc)
     * @see com.marketcetera.ors.brokers.Selector#chooseBroker(org.marketcetera.trade.Order)
     */
    @Override
    public BrokerID chooseBroker(Order inOrder)
    {
        SLF4JLoggerProxy.debug(this,
                               "Choosing broker for {}",
                               inOrder);
        if(defaultBroker == null) {
            try(CloseableLock brokerLock = CloseableLock.create(defaultBrokerLock.writeLock())) {
                brokerLock.lock();
                if(defaultBroker == null) {
                    SLF4JLoggerProxy.debug(this,
                                           "No default broker yet, checking for brokers accessible to {}",
                                           instanceData);
                    List<FixSession> initiatorSessions = fixSessionProvider.findFixSessions(false,
                                                                                            instanceData.getInstanceNumber(),
                                                                                            instanceData.getTotalInstances());
                    SLF4JLoggerProxy.debug(this,
                                           "Retrieve sessions to choose from: {}",
                                           initiatorSessions);
                    if(initiatorSessions != null && !initiatorSessions.isEmpty()) {
                        FixSession chosenSession = initiatorSessions.get(0);
                        defaultBroker = new BrokerID(chosenSession.getBrokerId());
                    }
                }
            }
        }
        SLF4JLoggerProxy.debug(this,
                               "Chose {} for {}",
                               defaultBroker,
                               inOrder);
        return defaultBroker;
    }
    /**
     * Validate and start the object.
     */
    @PostConstruct
    public void start()
    {
        Validate.notNull(clusterService);
        Validate.notNull(brokerService);
        instanceData = clusterService.getInstanceData();
    }
    /**
     * Get the defaultBroker value.
     *
     * @return a <code>BrokerID</code> value
     */
    public BrokerID getDefaultBroker()
    {
        return defaultBroker;
    }
    /**
     * Sets the defaultBroker value.
     *
     * @param inDefaultBroker a <code>BrokerID</code> value
     */
    public void setDefaultBroker(BrokerID inDefaultBroker)
    {
        defaultBroker = inDefaultBroker;
    }
    /**
     * Get the brokerService value.
     *
     * @return a <code>BrokerService</code> value
     */
    public BrokerService getBrokerService()
    {
        return brokerService;
    }
    /**
     * Sets the brokerService value.
     *
     * @param inBrokerService a <code>BrokerService</code> value
     */
    public void setBrokerService(BrokerService inBrokerService)
    {
        brokerService = inBrokerService;
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
     * identifies the instance data for this instance
     */
    private ClusterData instanceData;
    /**
     * provides access to cluster services
     */
    @Autowired
    private ClusterService clusterService;
    /**
     * protects access to {@link #defaultBroker}
     */
    private final ReadWriteLock defaultBrokerLock = new ReentrantReadWriteLock();
    /**
     * current default broker
     */
    @GuardedBy("defaultBrokerLock")
    private volatile BrokerID defaultBroker;
    /**
     * provides access to FIX sessions
     */
    @Autowired
    private FixSessionProvider fixSessionProvider;
    /**
     * provides access to broker services
     */
    @Autowired
    private BrokerService brokerService;
}
