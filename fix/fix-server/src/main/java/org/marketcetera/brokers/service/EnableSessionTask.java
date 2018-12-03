package org.marketcetera.brokers.service;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.marketcetera.cluster.AbstractCallableClusterTask;
import org.marketcetera.eventbus.EventBusService;
import org.marketcetera.fix.FixSession;
import org.marketcetera.fix.SessionNameProvider;
import org.marketcetera.fix.event.FixSessionEnabledEvent;
import org.marketcetera.fix.event.SimpleFixSessionEnabledEvent;
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
        FixSessionEnabledEvent fixSessionEnabledEvent = new SimpleFixSessionEnabledEvent(new quickfix.SessionID(session.getSessionId()));
        try {
            eventBusService.post(fixSessionEnabledEvent);
            SLF4JLoggerProxy.debug(this,
                                   "Posting {} for {}",
                                   fixSessionEnabledEvent,
                                   getClusterService().getInstanceData());
            return true;
        } catch (Exception e) {
            SLF4JLoggerProxy.warn(this,
                                  e,
                                  "Enable session listener failed for {}: {}",
                                  sessionNameProvider.getSessionName(new SessionID(session.getSessionId())),
                                  ExceptionUtils.getRootCauseMessage(e));
            return false;
        }
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
     * provides access to session names
     */
    @Autowired
    private transient SessionNameProvider sessionNameProvider;
    /**
     * provides access to event bus services
     */
    @Autowired
    private transient EventBusService eventBusService;
    /**
     * fix session to be enabled
     */
    private final FixSession session;
    private static final long serialVersionUID = 3859791872967122363L;
}
