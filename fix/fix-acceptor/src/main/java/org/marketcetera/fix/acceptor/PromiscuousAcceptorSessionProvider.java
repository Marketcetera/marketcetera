package org.marketcetera.fix.acceptor;

import java.io.IOException;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.Validate;
import org.marketcetera.brokers.service.BrokerService;
import org.marketcetera.cluster.ClusterData;
import org.marketcetera.cluster.service.ClusterService;
import org.marketcetera.fix.FixSession;
import org.marketcetera.fix.FixSessionFactory;
import org.marketcetera.fix.FixSettingsProvider;
import org.marketcetera.fix.FixSettingsProviderFactory;
import org.marketcetera.fix.store.MessageStoreSession;
import org.marketcetera.fix.store.MessageStoreSessionDao;
import org.marketcetera.quickfix.FIXMessageUtil;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import quickfix.Acceptor;
import quickfix.Application;
import quickfix.ConfigError;
import quickfix.DefaultSessionFactory;
import quickfix.FixVersions;
import quickfix.Session;
import quickfix.SessionFactory;
import quickfix.SessionID;
import quickfix.SessionSettings;
import quickfix.mina.SessionConnector;
import quickfix.mina.acceptor.AcceptorSessionProvider;

/* $License$ */

/**
 * Dynamically creates new sessions based on incoming requests.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class PromiscuousAcceptorSessionProvider
        implements AcceptorSessionProvider
{
    /* (non-Javadoc)
     * @see quickfix.mina.acceptor.AcceptorSessionProvider#getSession(quickfix.SessionID, quickfix.mina.SessionConnector)
     */
    @Override
    public Session getSession(SessionID inSessionId,
                              SessionConnector inConnector)
    {
        if(targetCompId != null) {
            Validate.isTrue(targetCompId.equals(inSessionId.getSenderCompID()),
                            "Invalid target comp id in " + FIXMessageUtil.getReversedSessionId(inSessionId) + " expected: " + targetCompId + " to equal " + inSessionId.getSenderCompID());
        }
        Session fixSession = Session.lookupSession(inSessionId);
        if(fixSession != null) {
            // TODO this might need to return null! returning null causes the connection to close w/o sending logout
            SLF4JLoggerProxy.debug(this,
                                   "Returning existing session {} for {}",
                                   fixSession,
                                   inSessionId);
            return fixSession;
        }
        // check to see if we already have a session by this name
        MessageStoreSession existingSession = sessionDao.findBySessionId(inSessionId.toString());
        FixSession session = fixSessionFactory.create();
        session.setAffinity(clusterData.getInstanceNumber());
        session.setBrokerId(inSessionId.toString());
        session.setHost(fixSettingsProvider.getAcceptorHost());
        session.setIsAcceptor(true);
        session.setIsEnabled(true);
        session.setName(inSessionId.toString());
        session.setPort(fixSettingsProvider.getAcceptorPort());
        session.setSessionId(inSessionId.toString());
        for(Map.Entry<String,String> entry : newSessionSettings.entrySet()) {
            session.getSessionSettings().put(entry.getKey(),entry.getValue());
        }
        SessionSettings fixSessionSettings = brokerService.generateSessionSettings(Lists.newArrayList(session));
        fixSessionSettings.setString(Acceptor.SETTING_SOCKET_ACCEPT_ADDRESS,
                                     String.valueOf(fixSettingsProvider.getAcceptorHost()));
        fixSessionSettings.setString(Acceptor.SETTING_SOCKET_ACCEPT_PORT,
                                     String.valueOf(fixSettingsProvider.getAcceptorPort()));
        if(inSessionId.isFIXT()) {
            fixSessionSettings.setString(Session.SETTING_DEFAULT_APPL_VER_ID,
                                         FixVersions.FIX50SP2);
        }
        SessionFactory factory = new DefaultSessionFactory(application,
                                                           fixSettingsProvider.getMessageStoreFactory(fixSessionSettings),
                                                           fixSettingsProvider.getLogFactory(fixSessionSettings),
                                                           fixSettingsProvider.getMessageFactory());
        try {
            fixSession = factory.create(inSessionId,
                                        fixSessionSettings);
            if(existingSession != null) {
                fixSession.setNextSenderMsgSeqNum(existingSession.getSenderSeqNum());
                fixSession.setNextTargetMsgSeqNum(existingSession.getTargetSeqNum());
            }
        } catch (ConfigError | IOException e) {
            SLF4JLoggerProxy.warn(this,
                                  e);
        }
        SLF4JLoggerProxy.debug(this,
                               "Returning new session {} for {}",
                               fixSession,
                               inSessionId);
        return fixSession;
    }
    /**
     * Validate and start the object.
     */
    @PostConstruct
    public void start()
    {
        clusterData = clusterService.getInstanceData();
        fixSettingsProvider = fixSettingsProviderFactory.create();
    }
    /**
     * Get the application value.
     *
     * @return an <code>Application</code> value
     */
    public Application getApplication()
    {
        return application;
    }
    /**
     * Sets the application value.
     *
     * @param inApplication an <code>Application</code> value
     */
    public void setApplication(Application inApplication)
    {
        application = inApplication;
    }
    /**
     * Get the targetCompId value.
     *
     * @return a <code>String</code> value
     */
    public String getTargetCompId()
    {
        return targetCompId;
    }
    /**
     * Sets the targetCompId value.
     *
     * @param inTargetCompId a <code>String</code> value
     */
    public void setTargetCompId(String inTargetCompId)
    {
        targetCompId = inTargetCompId;
    }
    /**
     * Get the newSessionSettings value.
     *
     * @return a <code>Map&lt;String,String&gt;</code> value
     */
    public Map<String,String> getNewSessionSettings()
    {
        return newSessionSettings;
    }
    /**
     * Sets the newSessionSettings value.
     *
     * @param inNewSessionSettings a <code>Map&lt;String,String&gt;</code> value
     */
    public void setNewSessionSettings(Map<String,String> inNewSessionSettings)
    {
        newSessionSettings = inNewSessionSettings;
    }
    /**
     * target comp id to use to filter incoming sessions, if desired
     */
    private String targetCompId;
    /**
     * session settings to apply to new sessions
     */
    private Map<String,String> newSessionSettings = Maps.newHashMap();
    /**
     * creates FixSession objects
     */
    @Autowired
    private FixSessionFactory fixSessionFactory;
    /**
     * provides access to existing sessions
     */
    @Autowired
    private MessageStoreSessionDao sessionDao;
    /**
     * provides access to cluster services
     */
    @Autowired
    private ClusterService clusterService;
    /**
     * provides access to broker services
     */
    @Autowired
    private BrokerService brokerService;
    /**
     * constructs a FIX settings provider object
     */
    @Autowired
    private FixSettingsProviderFactory fixSettingsProviderFactory;
    /**
     * FIX application to provide to new sessions
     */
    private Application application;
    /**
     * provides FIX settings
     */
    private FixSettingsProvider fixSettingsProvider;
    /**
     * identifies instance
     */
    private ClusterData clusterData;
}
