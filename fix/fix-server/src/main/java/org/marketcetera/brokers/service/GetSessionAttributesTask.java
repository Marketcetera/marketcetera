package org.marketcetera.brokers.service;

import org.marketcetera.cluster.AbstractCallableClusterTask;
import org.marketcetera.cluster.ClusterData;
import org.marketcetera.fix.AcceptorSessionAttributes;
import org.marketcetera.fix.FixSettingsProvider;
import org.marketcetera.fix.FixSettingsProviderFactory;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Collect session attributes from the appropriate cluster member.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class GetSessionAttributesTask
        extends AbstractCallableClusterTask<AcceptorSessionAttributes>
{
    /* (non-Javadoc)
     * @see java.util.concurrent.Callable#call()
     */
    @Override
    public AcceptorSessionAttributes call()
            throws Exception
    {
        ClusterData clusterData = getClusterService().getInstanceData();
        AcceptorSessionAttributes attributes = null;
        if(brokerService.isAffinityMatch(clusterData,
                                         affinity)) {
            FixSettingsProvider settingsProvider = fixSettingsProviderFactory.create();
            attributes = new AcceptorSessionAttributes();
            attributes.setAffinity(affinity);
            attributes.setHost(settingsProvider.getAcceptorHost());
            attributes.setPort(settingsProvider.getAcceptorPort());
            SLF4JLoggerProxy.debug(this,
                                   "{} is an affinity match for {}, returning {}",
                                   clusterData,
                                   affinity,
                                   attributes);
        } else {
            SLF4JLoggerProxy.debug(this,
                                   "{} is not an affinity match for {}, returning {}",
                                   clusterData,
                                   affinity,
                                   attributes);
        }
        return attributes;
    }
    /**
     * Create a new StopSessionTask instance.
     *
     * @param inAffinity a <code>FixSession</code> value
     */
    public GetSessionAttributesTask(int inAffinity)
    {
        affinity = inAffinity;
    }
    /**
     * fix settings provider factory value
     */
    @Autowired
    private transient FixSettingsProviderFactory fixSettingsProviderFactory;
    /**
     * provides access to broker services
     */
    @Autowired
    private transient BrokerService brokerService;
    /**
     * affinity to be checked
     */
    private int affinity;
    private static final long serialVersionUID = 5032644750164495565L;
}