package org.marketcetera.brokers.service;

import org.marketcetera.cluster.AbstractCallableClusterTask;
import org.marketcetera.eventbus.EventBusService;
import org.marketcetera.fix.FixSession;
import org.marketcetera.fix.event.FixSessionStartedEvent;
import org.marketcetera.fix.event.SimpleFixSessionStartedEvent;
import org.marketcetera.trade.BrokerID;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Indicates to each cluster member that a particular session has been started.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class StartSessionTask
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
                               "Calling start for {} on {}",
                               session,
                               getClusterService().getInstanceData());
        FixSessionStartedEvent sessionStoppedEvent = new SimpleFixSessionStartedEvent(new quickfix.SessionID(session.getSessionId()),
                                                                                      new BrokerID(session.getBrokerId()));
        eventBusService.post(sessionStoppedEvent);
        return true;
    }
    /**
     * Create a new StartSessionTask instance.
     *
     * @param inSession a <code>FixSession</code> value
     */
    public StartSessionTask(FixSession inSession)
    {
        // remember - this session is NOT attached
        session = inSession;
    }
    /**
     * provides access to event bus services
     */
    @Autowired
    private transient EventBusService eventBusService;
    /**
     * fix session to be disabled
     */
    private FixSession session;
    private static final long serialVersionUID = -3799735073665308159L;
}