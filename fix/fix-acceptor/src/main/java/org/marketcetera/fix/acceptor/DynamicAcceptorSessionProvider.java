package org.marketcetera.fix.acceptor;

import org.marketcetera.cluster.ClusterData;
import org.marketcetera.core.fix.FixSettingsProvider;
import org.marketcetera.core.fix.FixSettingsProviderFactory;
import org.marketcetera.fix.FixSession;
import org.marketcetera.fix.SessionService;
import org.marketcetera.util.log.SLF4JLoggerProxy;

import quickfix.Acceptor;
import quickfix.Application;
import quickfix.ConfigError;
import quickfix.DefaultSessionFactory;
import quickfix.Session;
import quickfix.SessionFactory;
import quickfix.SessionID;
import quickfix.SessionSettings;
import quickfix.mina.SessionConnector;
import quickfix.mina.acceptor.AcceptorSessionProvider;

import com.google.common.collect.Lists;

/* $License$ */

/**
 * Provides dynamic acceptor sessions based on system criteria.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class DynamicAcceptorSessionProvider
        implements AcceptorSessionProvider
{
    /**
     * Create a new DynamicAcceptorSessionProvider instance.
     *
     * @param inApplication an <code>Application</code> value
     * @param inSessionService a <code>SessionService</code> value
     * @param inFixSettingsProviderFactory a <code>FixSettingsProviderFactory</code> value
     * @param inClusterData a <code>ClusterData</code> value
     */
    public DynamicAcceptorSessionProvider(Application inApplication,
                                          SessionService inSessionService,
                                          FixSettingsProviderFactory inFixSettingsProviderFactory,
                                          ClusterData inClusterData)
    {
        application = inApplication;
        sessionService = inSessionService;
        fixSettingsProviderFactory = inFixSettingsProviderFactory;
        clusterData = inClusterData;
    }
    /* (non-Javadoc)
     * @see quickfix.mina.acceptor.AcceptorSessionProvider#getSession(quickfix.SessionID, quickfix.mina.SessionConnector)
     */
    @Override
    public Session getSession(SessionID inSessionId,
                              SessionConnector inConnector)
    {
        String sessionName = sessionService.getSessionName(inSessionId);
        // first, check to see if the session already exists
        Session session = Session.lookupSession(inSessionId);
        if(session != null) {
            SLF4JLoggerProxy.debug(this,
                                   "Returning existing session {} for {}",
                                   session,
                                   sessionName);
            return session;
        }
        // session doesn't exist yet, check to see if there's a session that matches in the DB
        FixSession fixSession = sessionService.findFixSessionBySessionId(inSessionId);
        if(fixSession == null) {
            SLF4JLoggerProxy.warn(this,
                                  "Rejecting unknown session {}",
                                  sessionName);
            return null;
        }
        if(!fixSession.isEnabled()) {
            SLF4JLoggerProxy.warn(this,
                                  "Rejecting disabled session {}",
                                  sessionName);
            return null;
        }
        if(!sessionService.isAffinityMatch(fixSession,
                                          clusterData)) {
            SLF4JLoggerProxy.warn(this,
                                  "Rejecting session {} because no affinity match to {}",
                                  sessionName,
                                  clusterData);
            return null;
        }
        // session exists in the DB, generate FIX settings for it
        SessionSettings fixSessionSettings = sessionService.generateSessionSettings(Lists.newArrayList(fixSession));
        FixSettingsProvider fixSettingsProvider = fixSettingsProviderFactory.create();
        if(fixSession.isAcceptor()) {
            // inject the acceptor port here, if available
            fixSessionSettings.setString(Acceptor.SETTING_SOCKET_ACCEPT_ADDRESS,
                                         String.valueOf(fixSettingsProvider.getAcceptorHost()));
            fixSessionSettings.setString(Acceptor.SETTING_SOCKET_ACCEPT_PORT,
                                         String.valueOf(fixSettingsProvider.getAcceptorPort()));
        }
        SessionFactory factory = new DefaultSessionFactory(application,
                                                           fixSettingsProvider.getMessageStoreFactory(fixSessionSettings),
                                                           fixSettingsProvider.getLogFactory(fixSessionSettings),
                                                           fixSettingsProvider.getMessageFactory());
        try {
            session = factory.create(inSessionId,
                                     fixSessionSettings);
        } catch (ConfigError e) {
            SLF4JLoggerProxy.warn(this,
                                  e);
        }
        SLF4JLoggerProxy.debug(this,
                               "Returning new session {} for {}",
                               session,
                               sessionName);
        return session;
    }
    /**
     * identifies instance
     */
    private final ClusterData clusterData;
    /**
     * provides access to session services
     */
    private final SessionService sessionService;
    /**
     * constructs a FIX settings provider object
     */
    private final FixSettingsProviderFactory fixSettingsProviderFactory;
    /**
     * FIX application to provide to new sessions
     */
    private Application application;
}
