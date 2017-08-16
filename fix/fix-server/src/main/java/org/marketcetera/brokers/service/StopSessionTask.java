package org.marketcetera.brokers.service;

import org.marketcetera.brokers.service.BrokerService;
import org.marketcetera.cluster.CallableClusterTask;
import org.marketcetera.fix.FixSession;
import org.marketcetera.fix.FixSessionListener;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Indicates to each cluster member that a particular session has been stopped.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class StopSessionTask
        extends CallableClusterTask<Boolean>
{
    /* (non-Javadoc)
     * @see java.util.concurrent.Callable#call()
     */
    @Override
    public Boolean call()
            throws Exception
    {
        SLF4JLoggerProxy.debug(this,
                               "Calling stop for {} on {}",
                               session,
                               getClusterService().getInstanceData());
        for(FixSessionListener fixSessionListener : brokerService.getFixSessionListeners()) {
            fixSessionListener.sessionStopped(session);
        }
        return true;
    }
    /**
     * Create a new StopSessionTask instance.
     *
     * @param inSession a <code>FixSession</code> value
     */
    public StopSessionTask(FixSession inSession)
    {
        // remember - this session is NOT attached
        session = inSession;
    }
    /**
     * cluster-local broker service value
     */
    @Autowired
    private transient BrokerService brokerService;
    /**
     * fix session to be disabled
     */
    private FixSession session;
    private static final long serialVersionUID = 5032644750164495565L;
}