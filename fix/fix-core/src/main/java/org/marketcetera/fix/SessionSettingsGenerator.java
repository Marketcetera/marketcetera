package org.marketcetera.fix;

import java.util.Collection;
import java.util.Map;
import java.util.Properties;

import org.marketcetera.util.log.SLF4JLoggerProxy;

import quickfix.Acceptor;
import quickfix.Initiator;
import quickfix.SessionFactory;
import quickfix.SessionID;
import quickfix.SessionSettings;

/* $License$ */

/**
 * Generates FIX {@link SessionSettings} objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public abstract class SessionSettingsGenerator
{
    /**
     * Generate session settings from the given FIX sessions.
     *
     * @param inFixSessions a <code>Collection&lt;FixSession&gt;</code> value
     * @param inFixSettingsProviderFactory a <code>FixSettingsProviderFactory</code> value
     * @return a <code>SessionSettings</code> value
     */
    public static SessionSettings generateSessionSettings(Collection<FixSession> inFixSessions,
                                                          FixSettingsProviderFactory inFixSettingsProviderFactory)
    {
        return generateSessionSettings(inFixSessions,
                                       inFixSettingsProviderFactory,
                                       false);
    }
    /**
     * Generate session settings from the given FIX sessions.
     *
     * @param inFixSessions a <code>Collection&lt;FixSession&gt;</code> value
     * @param inFixSettingsProviderFactory a <code>FixSettingsProviderFactory</code> value
     * @param inIncludeDisabledSessions a <code>boolean</code> value
     * @return a <code>SessionSettings</code> value
     */
    public static SessionSettings generateSessionSettings(Collection<FixSession> inFixSessions,
                                                          FixSettingsProviderFactory inFixSettingsProviderFactory,
                                                          boolean inIncludeDisabledSessions)
    {
        SessionSettings settings = new SessionSettings();
        Properties defaultProperties = settings.getDefaultProperties();
        boolean acceptorFound = false;
        boolean initiatorFound = false;
        FixSettingsProvider fixSettingsProvider = inFixSettingsProviderFactory.create();
        int acceptorPort = fixSettingsProvider.getAcceptorPort();
        String acceptorHost = fixSettingsProvider.getAcceptorHost();
        for(FixSession session : inFixSessions) {
            if(!session.isEnabled() && !inIncludeDisabledSessions) {
                SLF4JLoggerProxy.debug(SessionSettingsGenerator.class,
                                       "Skipping disabled session: {}",
                                       session);
                continue;
            }
            SessionID sessionId = new SessionID(session.getSessionId());
            String host = session.getHost();
            int port = session.getPort();
            if(session.isAcceptor()) {
                if(port != acceptorPort) {
                    SLF4JLoggerProxy.debug(SessionSettingsGenerator.class,
                                           "Acceptor session {} moved to instance port {}",
                                           session,
                                           acceptorPort);
                }
                if(!host.equals(acceptorHost)) {
                    SLF4JLoggerProxy.debug(SessionSettingsGenerator.class,
                                           "Acceptor session {} moved to instance host {}",
                                           session,
                                           acceptorHost);
                }
                defaultProperties.setProperty(Acceptor.SETTING_SOCKET_ACCEPT_ADDRESS,
                                              fixSettingsProvider.getAcceptorHost());
                defaultProperties.setProperty(Acceptor.SETTING_SOCKET_ACCEPT_PORT,
                                              String.valueOf(fixSettingsProvider.getAcceptorPort()));
                defaultProperties.setProperty(SessionFactory.SETTING_CONNECTION_TYPE,
                                              SessionFactory.ACCEPTOR_CONNECTION_TYPE);
                defaultProperties.setProperty(Acceptor.SETTING_SOCKET_ACCEPT_PROTOCOL,
                                              fixSettingsProvider.getAcceptorProtocol());
                acceptorFound = true;
                if(initiatorFound) {
                    throw new UnsupportedOperationException("All sessions of the same session settings must be of the same ConnectionType");
                }
            } else {
                defaultProperties.setProperty(SessionFactory.SETTING_CONNECTION_TYPE,
                                              SessionFactory.INITIATOR_CONNECTION_TYPE);
                settings.setString(Initiator.SETTING_SOCKET_CONNECT_HOST,
                                   host);
                settings.setString(Initiator.SETTING_SOCKET_CONNECT_PORT,
                                   String.valueOf(port));
                initiatorFound = true;
                if(acceptorFound) {
                    throw new UnsupportedOperationException("All sessions of the same session settings must be of the same ConnectionType");
                }
            }
            settings.setString(sessionId,
                               SessionSettings.BEGINSTRING,
                               sessionId.getBeginString());
            settings.setString(sessionId,
                               SessionSettings.SENDERCOMPID,
                               sessionId.getSenderCompID());
            settings.setString(sessionId,
                               SessionSettings.TARGETCOMPID,
                               sessionId.getTargetCompID());
            for(Map.Entry<String,String> entry : session.getSessionSettings().entrySet()) {
                settings.setString(sessionId,
                                   entry.getKey(),
                                   entry.getValue());
            }
        }
        return settings;
    }
}
