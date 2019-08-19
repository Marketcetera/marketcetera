package org.marketcetera.brokers.service;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.marketcetera.cluster.AbstractCallableClusterTask;
import org.marketcetera.eventbus.EventBusService;
import org.marketcetera.fix.FixSession;
import org.marketcetera.fix.SessionNameProvider;
import org.marketcetera.fix.event.FixSessionDisabledEvent;
import org.marketcetera.fix.event.SimpleFixSessionDisabledEvent;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.springframework.beans.factory.annotation.Autowired;

import quickfix.SessionID;

/**
 * Indicates to each cluster member that a particular session has been disabled.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class DisableSessionTask
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
                               "Calling disable for {} on {}",
                               session,
                               getClusterService().getInstanceData());
        FixSessionDisabledEvent fixSessionDisabledEvent = new SimpleFixSessionDisabledEvent(new quickfix.SessionID(session.getSessionId()));
        try {
            eventBusService.post(fixSessionDisabledEvent);
            return true;
        } catch (Exception e) {
            SLF4JLoggerProxy.warn(this,
                                  e,
                                  "Disable session listener failed for {}: {}",
                                  sessionNameProvider.getSessionName(new SessionID(session.getSessionId())),
                                  ExceptionUtils.getRootCauseMessage(e));
            return false;
        }
    }
    /**
     * Create a new DisableSessionTask instance.
     *
     * @param inSession a <code>FixSession</code> value
     */
    public DisableSessionTask(FixSession inSession)
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
     * fix session to be disabled
     */
    private FixSession session;
    private static final long serialVersionUID = -7717222888266945739L;
}