package org.marketcetera;

import org.marketcetera.fix.provisioning.FixSessionDescriptor;
import org.marketcetera.fix.provisioning.FixSessionsDescriptor;
import org.marketcetera.fix.provisioning.SessionSettingsDescriptor;
import org.marketcetera.trade.BrokerID;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import quickfix.FixVersions;
import quickfix.Initiator;
import quickfix.MessageFactory;
import quickfix.Session;
import quickfix.SessionSettings;

/* $License$ */

/**
 * Provides test configuration for FIX server tests.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@SpringBootConfiguration
@ComponentScan
public class FixServerTestConfiguration
{
    /**
     * Get the message factory value.
     *
     * @return a <code>MessageFactory</code> value
     */
    @Bean
    public MessageFactory getMessageFactory()
    {
        return new quickfix.DefaultMessageFactory();
    }
    /**
     * Get the FIX acceptor sessions value.
     *
     * @return a <code>FixSessionsDescriptor</code> value
     */
    @Bean
    public FixSessionsDescriptor getFixAcceptorSessions()
    {
        FixSessionsDescriptor acceptorFixSessions = new FixSessionsDescriptor();
        SessionSettingsDescriptor settingsDescriptor = new SessionSettingsDescriptor();
        acceptorFixSessions.setSessionSettings(settingsDescriptor);
        // generate settings for all acceptor sessions
        settingsDescriptor.getSessionSettings().put(quickfix.SessionFactory.SETTING_CONNECTION_TYPE,
                                                    "acceptor");
        settingsDescriptor.getSessionSettings().put("SLF4JLogHeartbeats",
                                                    "N");
        settingsDescriptor.getSessionSettings().put(Session.SETTING_START_TIME,
                                                    "00:00:00");
        settingsDescriptor.getSessionSettings().put(Session.SETTING_END_TIME,
                                                    "00:00:00");
        settingsDescriptor.getSessionSettings().put(Session.SETTING_TIMEZONE,
                                                    "US/Pacific");
        settingsDescriptor.getSessionSettings().put(Session.SETTING_USE_DATA_DICTIONARY,
                                                    "Y");
        settingsDescriptor.getSessionSettings().put(Session.SETTING_RESET_ON_LOGON,
                                                    "Y");
        settingsDescriptor.getSessionSettings().put(Session.SETTING_RESET_ON_LOGOUT,
                                                    "Y");
        settingsDescriptor.getSessionSettings().put(Session.SETTING_RESET_ON_DISCONNECT,
                                                    "Y");
        settingsDescriptor.getSessionSettings().put(Session.SETTING_RESET_ON_ERROR,
                                                    "Y");
        settingsDescriptor.getSessionSettings().put(Session.SETTING_HEARTBTINT,
                                                    "1");
        FixSessionDescriptor session1Descriptor = new FixSessionDescriptor();
        session1Descriptor.setName("Acceptor1");
        session1Descriptor.setBrokerId(new BrokerID("acceptor1"));
        session1Descriptor.setDescription("Test Acceptor 1");
        SessionSettingsDescriptor acceptor1SessionSettings = new SessionSettingsDescriptor();
        session1Descriptor.setSessionSettings(acceptor1SessionSettings);
        session1Descriptor.getSessionSettings().getSessionSettings().put(SessionSettings.BEGINSTRING,
                                                                         FixVersions.BEGINSTRING_FIX42);
        session1Descriptor.getSessionSettings().getSessionSettings().put(Session.SETTING_DATA_DICTIONARY,
                                                                         "FIX42.xml");
        session1Descriptor.getSessionSettings().getSessionSettings().put(SessionSettings.SENDERCOMPID,
                                                                         "TARGET1");
        session1Descriptor.getSessionSettings().getSessionSettings().put(SessionSettings.TARGETCOMPID,
                                                                         "MATP");
        FixSessionDescriptor session2Descriptor = new FixSessionDescriptor();
        SessionSettingsDescriptor acceptor2SessionSettings = new SessionSettingsDescriptor();
        session2Descriptor.setSessionSettings(acceptor2SessionSettings);
        session2Descriptor.setName("Acceptor2");
        session2Descriptor.setBrokerId(new BrokerID("acceptor2"));
        session2Descriptor.setDescription("Test Acceptor 2");
        session2Descriptor.getSessionSettings().getSessionSettings().put(SessionSettings.BEGINSTRING,
                                                                         FixVersions.BEGINSTRING_FIX42);
        session2Descriptor.getSessionSettings().getSessionSettings().put(Session.SETTING_DATA_DICTIONARY,
                                                                         "FIX42.xml");
        session2Descriptor.getSessionSettings().getSessionSettings().put(SessionSettings.SENDERCOMPID,
                                                                         "TARGET2");
        session2Descriptor.getSessionSettings().getSessionSettings().put(SessionSettings.TARGETCOMPID,
                                                                         "MATP");
        acceptorFixSessions.getFixSessions().add(session1Descriptor);
        acceptorFixSessions.getFixSessions().add(session2Descriptor);
        return acceptorFixSessions;
    }
    /**
     * Get the FIX initiator sessions value.
     *
     * @return a <code>FixSessionsDescriptor</code> value
     */
    @Bean
    public FixSessionsDescriptor getFixInitiatorSessions()
    {
        FixSessionsDescriptor initiatorFixSessions = new FixSessionsDescriptor();
        SessionSettingsDescriptor settingsDescriptor = new SessionSettingsDescriptor();
        initiatorFixSessions.setSessionSettings(settingsDescriptor);
        // generate settings for all initiator sessions
        settingsDescriptor.getSessionSettings().put(quickfix.SessionFactory.SETTING_CONNECTION_TYPE,
                                                    "initiator");
        settingsDescriptor.getSessionSettings().put("SLF4JLogHeartbeats",
                                                    "N");
        settingsDescriptor.getSessionSettings().put(Session.SETTING_START_TIME,
                                                    "00:00:00");
        settingsDescriptor.getSessionSettings().put(Session.SETTING_END_TIME,
                                                    "00:00:00");
        settingsDescriptor.getSessionSettings().put(Session.SETTING_TIMEZONE,
                                                    "US/Pacific");
        settingsDescriptor.getSessionSettings().put(Session.SETTING_USE_DATA_DICTIONARY,
                                                    "Y");
        settingsDescriptor.getSessionSettings().put(Session.SETTING_RESET_ON_LOGON,
                                                    "Y");
        settingsDescriptor.getSessionSettings().put(Session.SETTING_RESET_ON_LOGOUT,
                                                    "Y");
        settingsDescriptor.getSessionSettings().put(Session.SETTING_RESET_ON_DISCONNECT,
                                                    "Y");
        settingsDescriptor.getSessionSettings().put(Session.SETTING_RESET_ON_ERROR,
                                                    "Y");
        settingsDescriptor.getSessionSettings().put(Initiator.SETTING_RECONNECT_INTERVAL,
                                                    "1");
        settingsDescriptor.getSessionSettings().put(Session.SETTING_HEARTBTINT,
                                                    "1");
        FixSessionDescriptor session1Descriptor = new FixSessionDescriptor();
        session1Descriptor.setName("Initiator1");
        session1Descriptor.setBrokerId(new BrokerID("initiator1"));
        session1Descriptor.setDescription("Test Initiator 1");
        session1Descriptor.setHost("localhost");
        session1Descriptor.setPort(21345);
        SessionSettingsDescriptor acceptor1SessionSettings = new SessionSettingsDescriptor();
        session1Descriptor.setSessionSettings(acceptor1SessionSettings);
        session1Descriptor.getSessionSettings().getSessionSettings().put(SessionSettings.BEGINSTRING,
                                                                         FixVersions.BEGINSTRING_FIX42);
        session1Descriptor.getSessionSettings().getSessionSettings().put(Session.SETTING_DATA_DICTIONARY,
                                                                         "FIX42.xml");
        session1Descriptor.getSessionSettings().getSessionSettings().put(SessionSettings.SENDERCOMPID,
                                                                         "MATP");
        session1Descriptor.getSessionSettings().getSessionSettings().put(SessionSettings.TARGETCOMPID,
                                                                         "TARGET1");
        FixSessionDescriptor session2Descriptor = new FixSessionDescriptor();
        SessionSettingsDescriptor initiator2SessionSettings = new SessionSettingsDescriptor();
        session2Descriptor.setSessionSettings(initiator2SessionSettings);
        session2Descriptor.setName("Initiator2");
        session2Descriptor.setBrokerId(new BrokerID("initiator2"));
        session2Descriptor.setDescription("Test Initiator 2");
        session2Descriptor.setHost("localhost");
        session2Descriptor.setPort(21345);
        session2Descriptor.getSessionSettings().getSessionSettings().put(SessionSettings.BEGINSTRING,
                                                                         FixVersions.BEGINSTRING_FIX42);
        session2Descriptor.getSessionSettings().getSessionSettings().put(Session.SETTING_DATA_DICTIONARY,
                                                                         "FIX42.xml");
        session2Descriptor.getSessionSettings().getSessionSettings().put(SessionSettings.SENDERCOMPID,
                                                                         "MATP");
        session2Descriptor.getSessionSettings().getSessionSettings().put(SessionSettings.TARGETCOMPID,
                                                                         "TARGET2");
        initiatorFixSessions.getFixSessions().add(session1Descriptor);
        initiatorFixSessions.getFixSessions().add(session2Descriptor);
        return initiatorFixSessions;
    }
}
