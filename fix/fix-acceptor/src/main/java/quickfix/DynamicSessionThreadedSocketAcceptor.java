package quickfix;

import org.marketcetera.fix.SessionNameProvider;
import org.marketcetera.util.log.SLF4JLoggerProxy;

import com.google.common.collect.Lists;

/* $License$ */

/**
 * Provides a <code>ThreadedSocketAcceptor</code> implementation that properly handles dynamic sessions.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class DynamicSessionThreadedSocketAcceptor
        extends ThreadedSocketAcceptor
{
    /**
     * Create a new DynamicSessionThreadedSocketAcceptor instance.
     *
     * @param inApplication an <code>Application</code> value
     * @param inMessageStoreFactory a <code>MessageStoreFactory</code> value
     * @param inSettings a <code>SessionSettings</code> value
     * @param inLogFactory a <code>LogFactory</code> value
     * @param inMessageFactory a <code>MessageFactory</code> value
     * @param inQueueCapacity an <code>int</code> value
     * @param inSessionNameProvider a <code>SessionNameProvider</code> value
     * @throws ConfigError if an error occurs creating the object
     */
    public DynamicSessionThreadedSocketAcceptor(Application inApplication,
                                                MessageStoreFactory inMessageStoreFactory,
                                                SessionSettings inSettings,
                                                LogFactory inLogFactory,
                                                MessageFactory inMessageFactory,
                                                int inQueueCapacity,
                                                SessionNameProvider inSessionNameProvider)
            throws ConfigError
    {
        super(inApplication,
              inMessageStoreFactory,
              inSettings,
              inLogFactory,
              inMessageFactory,
              inQueueCapacity);
        sessionNameProvider = inSessionNameProvider;
    }
    /**
     * Create a new DynamicSessionThreadedSocketAcceptor instance.
     *
     * @param inApplication an <code>Application</code> value
     * @param inMessageStoreFactory a <code>MessageStoreFactory</code> value
     * @param inSettings a <code>SessionSettings</code> value
     * @param inLogFactory a <code>LogFactory</code> value
     * @param inMessageFactory a <code>MessageFactory</code> value
     * @param inSessionNameProvider a <code>SessionNameProvider</code> value
     * @throws ConfigError if an error occurs creating the object
     */
    public DynamicSessionThreadedSocketAcceptor(Application inApplication,
                                                MessageStoreFactory inMessageStoreFactory,
                                                SessionSettings inSettings,
                                                LogFactory inLogFactory,
                                                MessageFactory inMessageFactory,
                                                SessionNameProvider inSessionNameProvider)
            throws ConfigError
    {
        super(inApplication,
              inMessageStoreFactory,
              inSettings,
              inLogFactory,
              inMessageFactory);
        sessionNameProvider = inSessionNameProvider;
    }
    /**
     * Create a new DynamicSessionThreadedSocketAcceptor instance.
     *
     * @param inApplication an <code>Application</code> value
     * @param inMessageStoreFactory a <code>MessageStoreFactory</code> value
     * @param inSettings a <code>SessionSettings</code> value
     * @param inMessageFactory a <code>MessageFactory</code> value
     * @param inQueueCapacity an <code>int</code> value
     * @param inSessionNameProvider a <code>SessionNameProvider</code> value
     * @throws ConfigError if an error occurs creating the object
     */
    public DynamicSessionThreadedSocketAcceptor(Application inApplication,
                                                MessageStoreFactory inMessageStoreFactory,
                                                SessionSettings inSettings,
                                                MessageFactory inMessageFactory,
                                                SessionNameProvider inSessionNameProvider,
                                                int inQueueCapacity)
            throws ConfigError
    {
        super(inApplication,
              inMessageStoreFactory,
              inSettings,
              inMessageFactory,
              inQueueCapacity);
        sessionNameProvider = inSessionNameProvider;
    }
    /**
     * Create a new DynamicSessionThreadedSocketAcceptor instance.
     *
     * @param inApplication an <code>Application</code> value
     * @param inMessageStoreFactory a <code>MessageStoreFactory</code> value
     * @param inSettings a <code>SessionSettings</code> value
     * @param inMessageFactory a <code>MessageFactory</code> value
     * @param inSessionNameProvider a <code>SessionNameProvider</code> value
     * @throws ConfigError if an error occurs creating the object
     */
    public DynamicSessionThreadedSocketAcceptor(Application inApplication,
                                                MessageStoreFactory inMessageStoreFactory,
                                                SessionSettings inSettings,
                                                MessageFactory inMessageFactory,
                                                SessionNameProvider inSessionNameProvider)
            throws ConfigError
    {
        super(inApplication,
              inMessageStoreFactory,
              inSettings,
              inMessageFactory);
        sessionNameProvider = inSessionNameProvider;
    }
    /**
     * Create a new DynamicSessionThreadedSocketAcceptor instance.
     *
     * @param inSessionFactory a <code>SessionFactory</code> value
     * @param inSettings a <code>SessionSettings</code> value
     * @param inQueueCapacity an <code>int</code> value
     * @param inSessionNameProvider a <code>SessionNameProvider</code> value
     * @throws ConfigError if an error occurs creating the object
     */
    public DynamicSessionThreadedSocketAcceptor(SessionFactory inSessionFactory,
                                                SessionSettings inSettings,
                                                int inQueueCapacity,
                                                SessionNameProvider inSessionNameProvider)
            throws ConfigError
    {
        super(inSessionFactory,
              inSettings,
              inQueueCapacity);
        sessionNameProvider = inSessionNameProvider;
    }
    /**
     * Create a new DynamicSessionThreadedSocketAcceptor instance.
     *
     * @param inSessionFactory a <code>SessionFactory</code> value
     * @param inSettings a <code>SessionSettings</code> value
     * @param inSessionNameProvider a <code>SessionNameProvider</code> value
     * @throws ConfigError if an error occurs creating the object
     */
    public DynamicSessionThreadedSocketAcceptor(SessionFactory inSessionFactory,
                                                SessionSettings inSettings,
                                                SessionNameProvider inSessionNameProvider)
            throws ConfigError
    {
        super(inSessionFactory,
              inSettings);
        sessionNameProvider = inSessionNameProvider;
    }
    /* (non-Javadoc)
     * @see quickfix.mina.SessionConnector#removeDynamicSession(quickfix.SessionID)
     */
    @Override
    public void removeDynamicSession(SessionID inSessionId)
    {
        String sessionName = sessionNameProvider.getSessionName(inSessionId);
        Session existingSession = Session.lookupSession(inSessionId);
        SLF4JLoggerProxy.debug(this,
                               "Removing dynamic session {}, existing session: {}",
                               sessionName,
                               existingSession);
        super.removeDynamicSession(inSessionId);
        if(existingSession != null) {
            SLF4JLoggerProxy.debug(this,
                                   "Logging {} out",
                                   sessionName);
            try {
                existingSession.logout();
            } catch (Exception e) {
                SLF4JLoggerProxy.warn(this,
                                      e);
            }
            if(existingSession.isLoggedOn()) {
                SLF4JLoggerProxy.debug(this,
                                       "Forcing {} disconnect",
                                       sessionName);
                try {
                    existingSession.disconnect("Session removed",
                                               false);
                } catch (Exception e) {
                    SLF4JLoggerProxy.warn(this,
                                          e);
                }
            }
            SLF4JLoggerProxy.debug(this,
                                   "Unregistering {}",
                                   sessionName);
            Session.unregisterSessions(Lists.newArrayList(inSessionId));
            if(Session.lookupSession(inSessionId) == null) {
                SLF4JLoggerProxy.debug(this,
                                       "{} succesfully unregistered",
                                       sessionName);
            } else {
                SLF4JLoggerProxy.debug(this,
                                       "{} unsuccesfully unregistered",
                                       sessionName);
            }
        }
    }
    /**
     * provides access to session names
     */
    private final SessionNameProvider sessionNameProvider;
}
