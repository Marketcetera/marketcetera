package org.marketcetera.fix.acceptor;

import org.marketcetera.brokers.service.BrokerService;
import org.marketcetera.brokers.service.FixSessionProvider;
import org.marketcetera.cluster.ClusterData;
import org.marketcetera.fix.FixSession;
import org.marketcetera.fix.FixSettingsProvider;
import org.marketcetera.fix.SessionNameProvider;
import org.marketcetera.util.log.SLF4JLoggerProxy;

import com.google.common.collect.Lists;

import quickfix.mina.SessionConnector;
import quickfix.mina.acceptor.AcceptorSessionProvider;

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
     * @param inApplication a <code>quickfix.Application</code> value
     * @param inBrokerService a <code>BrokerService</code> value
     * @param inFixSessionProvider a <code>FixSessionProvider</code> value
     * @param inSessionNameProvider a <code>SessionNameProvider</code> value
     * @param inFixSettingsProvider a <code>FixSettingsProvider</code> value
     * @param inClusterData a <code>ClusterData</code> value
     */
    public DynamicAcceptorSessionProvider(quickfix.Application inApplication,
                                          BrokerService inBrokerService,
                                          FixSessionProvider inFixSessionProvider,
                                          SessionNameProvider inSessionNameProvider,
                                          FixSettingsProvider inFixSettingsProvider,
                                          ClusterData inClusterData)
    {
        application = inApplication;
        brokerService = inBrokerService;
        fixSessionProvider = inFixSessionProvider;
        sessionNameProvider = inSessionNameProvider;
        fixSettingsProvider = inFixSettingsProvider;
        clusterData = inClusterData;
    }
    /* (non-Javadoc)
     * @see quickfix.mina.acceptor.AcceptorSessionProvider#getSession(quickfix.SessionID, quickfix.mina.SessionConnector)
     */
    @Override
    public quickfix.Session getSession(quickfix.SessionID inSessionId,
                                       SessionConnector inConnector)
    {
        String sessionName = sessionNameProvider.getSessionName(inSessionId);
        // first, check to see if the session already exists
        quickfix.Session session = quickfix.Session.lookupSession(inSessionId);
        if(session != null) {
            SLF4JLoggerProxy.debug(this,
                                   "Returning existing session {} for {}",
                                   session,
                                   sessionName);
            return session;
        }
        // session doesn't exist yet, check to see if there's a session that matches in the DB
        FixSession fixSession = fixSessionProvider.findFixSessionBySessionId(inSessionId);
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
        if(!brokerService.isAffinityMatch(fixSession,
                                          clusterData)) {
            SLF4JLoggerProxy.warn(this,
                                  "Rejecting session {} because no affinity match to {}",
                                  sessionName,
                                  clusterData);
            return null;
        }
        // session exists in the DB, generate FIX settings for it
        quickfix.SessionSettings fixSessionSettings = brokerService.generateSessionSettings(Lists.newArrayList(fixSession));
        if(fixSession.isAcceptor()) {
            // inject the acceptor port here, if available
            fixSessionSettings.setString(quickfix.Acceptor.SETTING_SOCKET_ACCEPT_ADDRESS,
                                         String.valueOf(fixSettingsProvider.getAcceptorHost()));
            fixSessionSettings.setString(quickfix.Acceptor.SETTING_SOCKET_ACCEPT_PORT,
                                         String.valueOf(fixSettingsProvider.getAcceptorPort()));
        }
        quickfix.SessionFactory factory = new quickfix.DefaultSessionFactory(application,
                                                                             fixSettingsProvider.getMessageStoreFactory(fixSessionSettings),
                                                                             fixSettingsProvider.getLogFactory(fixSessionSettings),
                                                                             fixSettingsProvider.getMessageFactory());
        try {
            session = factory.create(inSessionId,
                                     fixSessionSettings);
        } catch (quickfix.ConfigError e) {
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
    private final BrokerService brokerService;
    /**
     * FIX session provider value
     */
    private final FixSessionProvider fixSessionProvider;
    /**
     * provides access to session names
     */
    private final SessionNameProvider sessionNameProvider;
    /**
     * provides FIX settings
     */
    private final FixSettingsProvider fixSettingsProvider;
    /**
     * FIX application to provide to new sessions
     */
    private quickfix.Application application;
}
