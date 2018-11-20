package org.marketcetera.brokers.service;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.marketcetera.cluster.AbstractCallableClusterTask;
import org.marketcetera.fix.FixSession;
import org.marketcetera.fix.FixSessionListener;
import org.marketcetera.fix.SessionNameProvider;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.springframework.beans.factory.annotation.Autowired;

import quickfix.SessionID;

/**
 * Indicates to each cluster member that a particular session has been enabled.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class EnableSessionTask
        extends AbstractCallableClusterTask<Boolean>
{
    /* (non-Javadoc)
     * @see java.util.concurrent.Callable#call()
     */
    @Override
    public Boolean call()
            throws Exception
    {
        SLF4JLoggerProxy.debug(this,
                               "Calling enable for {} on {} with listeners: {}",
                               session,
                               getClusterService().getInstanceData(),
                               brokerService.getFixSessionListeners());
        for(FixSessionListener fixSessionListener : brokerService.getFixSessionListeners()) {
            try {
                fixSessionListener.sessionEnabled(session);
            } catch (Exception e) {
                SLF4JLoggerProxy.warn(this,
                                      e,
                                      "Enable session listener failed for {}: {}",
                                      sessionNameProvider.getSessionName(new SessionID(session.getSessionId())),
                                      ExceptionUtils.getRootCauseMessage(e));
            }
        }
        return true;
    }
    /**
     * Create a new EnableSessionTask instance.
     *
     * @param inSession a <code>FixSession</code> value
     */
    public EnableSessionTask(FixSession inSession)
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
     * provides access to session names
     */
    @Autowired
    private transient SessionNameProvider sessionNameProvider;
    /**
     * fix session to be enabled
     */
    private final FixSession session;
    private static final long serialVersionUID = -7107454502447518827L;
}