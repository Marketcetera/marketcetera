package com.marketcetera.fix;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.joda.time.DateTime;
import org.marketcetera.cluster.ClusterData;
import org.marketcetera.core.fix.FixSettingsProvider;
import org.marketcetera.core.fix.FixSettingsProviderFactory;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import quickfix.Acceptor;
import quickfix.ConfigError;
import quickfix.FieldConvertError;
import quickfix.Initiator;
import quickfix.Session;
import quickfix.SessionFactory;
import quickfix.SessionID;
import quickfix.SessionSettings;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Lists;
import com.marketcetera.fix.dao.FixSessionDao;
import com.marketcetera.fix.dao.PersistentFixSession;

/* $License$ */

/**
 * Provides FIX session services.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class SessionServiceImpl
        implements SessionService
{
    /**
     * Validates and starts the object.
     */
    @PostConstruct
    public void start()
    {
        Validate.notNull(fixSessionDao);
    }
    /* (non-Javadoc)
     * @see com.marketcetera.fix.SessionService#getSessionName(quickfix.SessionID)
     */
    @Override
    public String getSessionName(SessionID inSessionId)
    {
        String value = sessionNamesBySessionId.get(inSessionId);
        if(value == null) {
            FixSession session = null;
            try {
                session = findFixSessionBySessionId(inSessionId);
            } catch (Exception e) {
                SLF4JLoggerProxy.debug(this,
                                       e,
                                       "Unable to retrieve session for {}",
                                       inSessionId);
            }
            if(session == null) {
                value = inSessionId.toString();
            } else {
                value = session.getName();
                sessionNamesBySessionId.put(inSessionId,
                                            session.getName());
            }
        }
        return value;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.ors.brokers.BrokerService#generateSessionSettings(java.util.Collection)
     */
    @Override
    public SessionSettings generateSessionSettings(Collection<FixSession> inFixSessions)
    {
        return generateSessionSettings(inFixSessions,
                                       false);
    }
    /* (non-Javadoc)
     * @see com.marketcetera.fix.SessionService#findFixSessionBySessionId(quickfix.SessionID)
     */
    @Override
    @Transactional(readOnly=true,propagation=Propagation.REQUIRED)
    public FixSession findFixSessionBySessionId(SessionID inSessionId)
    {
        FixSession session = fixSessionDao.findBySessionIdAndIsDeletedFalse(inSessionId.toString());
        if(session != null) {
            sessionNamesBySessionId.put(new SessionID(session.getSessionId()),
                                        session.getName());
        }
        return session;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.fix.SessionService#isAffinityMatch(com.marketcetera.fix.FixSession, com.marketcetera.matp.cluster.ClusterData)
     */
    @Override
    public boolean isAffinityMatch(FixSession inFixSession,
                                   ClusterData inClusterData)
    {
        return isAffinityMatch(inClusterData,
                               inFixSession.getAffinity());
    }
    /* (non-Javadoc)
     * @see com.marketcetera.fix.SessionService#findFixSessions(boolean, int, int)
     */
    @Override
    @Transactional(readOnly=true,propagation=Propagation.REQUIRED)
    public List<FixSession> findFixSessions(boolean inIsAcceptor,
                                            int inInstance,
                                            int inMaxInstances)
    {
        List<FixSession> sessionsToReturn = new ArrayList<>();
        List<PersistentFixSession> allSessionsByConnectionType = fixSessionDao.findByIsAcceptorAndIsDeletedFalseOrderByAffinityAsc(inIsAcceptor);
        SLF4JLoggerProxy.debug(this,
                               "Determining sessions for connection type {}, instance {} of {}",
                               inIsAcceptor?"acceptor":"initiator",
                               inInstance,
                               inMaxInstances);
        for(FixSession session : allSessionsByConnectionType) {
            sessionNamesBySessionId.put(new SessionID(session.getSessionId()),
                                        session.getName());
            // need to sort out which sessions should be returned based on affinity
            int brokerInstanceAffinity = session.getAffinity();
            while(brokerInstanceAffinity > inMaxInstances) {
                brokerInstanceAffinity -= inMaxInstances;
            }
            if(brokerInstanceAffinity == inInstance) {
                // we'll keep this broker
                SLF4JLoggerProxy.debug(this,
                                       "Retaining {}",
                                       session);
                sessionsToReturn.add(session);
            } else {
                SLF4JLoggerProxy.debug(this,
                                       "Discarding {}",
                                       session);
            }
        }
        SLF4JLoggerProxy.debug(this,
                               "Returning {}",
                               sessionsToReturn);
        return sessionsToReturn;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.fix.SessionService#getSessionStart(quickfix.SessionID)
     */
    @Override
    public Date getSessionStart(SessionID inSessionId)
    {
        FixSession fixSession = findFixSessionBySessionId(inSessionId);
        Date returnValue = new DateTime().withTimeAtStartOfDay().toDate();
        if(fixSession == null) {
            SLF4JLoggerProxy.debug(this,
                                   "No fix session for {}, using {} instead",
                                   inSessionId,
                                   returnValue);
            return returnValue;
        }
        try {
            SessionSettings settings = generateSessionSettings(Lists.newArrayList(fixSession),
                                                               true);
            SessionSchedule sessionSchedule = new SessionSchedule(settings,
                                                                  inSessionId);
            returnValue = sessionSchedule.getMostRecentStartTime();
        } catch (ConfigError | FieldConvertError e) {
            SLF4JLoggerProxy.info(this,
                                  e,
                                  "Cannot calculate session start for {}, using {} instead",
                                  inSessionId,
                                  returnValue);
        }
        SLF4JLoggerProxy.debug(this,
                               "Session start for {} calculated as: {}",
                               inSessionId,
                               returnValue);
        return returnValue;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.ors.brokers.BrokerService#isSessionTime(quickfix.SessionID)
     */
    @Override
    @Transactional(readOnly=true,propagation=Propagation.REQUIRED)
    public boolean isSessionTime(SessionID inSessionId)
    {
        Boolean result = isSessionTimeCache.getIfPresent(inSessionId);
        if(result != null) {
            return result;
        }
        try {
            FixSession session = findFixSessionBySessionId(inSessionId);
            if(session == null) {
                result = false;
                return result;
            }
            String rawDaysValue = StringUtils.trimToNull(session.getSessionSettings().get(sessionDaysKey));
            if(rawDaysValue == null) {
                SLF4JLoggerProxy.debug(this,
                                       "{} has no specified active days",
                                       inSessionId);
            } else {
                Date startOfSession = getSessionStart(inSessionId);
                if(startOfSession == null) {
                    SLF4JLoggerProxy.debug(this,
                                           "Unable to calculate start of session for {}, using now",
                                           inSessionId);
                    startOfSession = new Date();
                }
                DateTime now = new DateTime(startOfSession);
                int today = now.getDayOfWeek();
                int daysValue = Integer.parseInt(rawDaysValue);
                FixSessionDay fixSessionDay = FixSessionDay.values()[today-1];
                if(fixSessionDay.isActiveToday(daysValue)) {
                    SLF4JLoggerProxy.debug(this,
                                           "{} is active {} from {}",
                                           inSessionId,
                                           fixSessionDay,
                                           daysValue);
                } else {
                    SLF4JLoggerProxy.debug(this,
                                           "{} is *not* active on {} from {}",
                                           inSessionId,
                                           fixSessionDay,
                                           daysValue);
                    result = false;
                    return result;
                }
            }
            Session activeSession = Session.lookupSession(inSessionId);
            if(activeSession == null) {
                result = false;
                return result;
            }
            result = activeSession.isSessionTime();
            return result;
        } finally {
            isSessionTimeCache.put(inSessionId,
                                   result);
        }
    }
    /**
     * Generate session settings from the given FIX sessions.
     *
     * @param inFixSessions a <code>Collection&lt;FixSession&gt;</code> value
     * @param inIncludeDisabledSessions a <code>boolean</code> value
     * @return a <code>SessionSettings</code> value
     */
    private SessionSettings generateSessionSettings(Collection<FixSession> inFixSessions,
                                                    boolean inIncludeDisabledSessions)
    {
        SessionSettings settings = new SessionSettings();
        Properties defaultProperties = settings.getDefaultProperties();
        boolean acceptorFound = false;
        boolean initiatorFound = false;
        FixSettingsProvider fixSettingsProvider = fixSettingsProviderFactory.create();
        int acceptorPort = fixSettingsProvider.getAcceptorPort();
        String acceptorHost = fixSettingsProvider.getAcceptorHost();
        for(FixSession session : inFixSessions) {
            if(!session.isEnabled() && !inIncludeDisabledSessions) {
                SLF4JLoggerProxy.debug(this,
                                       "Skipping disabled session: {}",
                                       session);
                continue;
            }
            SessionID sessionId = new SessionID(session.getSessionId());
            String host = session.getHost();
            int port = session.getPort();
            if(session.isAcceptor()) {
                if(port != acceptorPort) {
                    SLF4JLoggerProxy.debug(this,
                                           "Acceptor session {} moved to instance port {}",
                                           session,
                                           acceptorPort);
                }
                if(!host.equals(acceptorHost)) {
                    SLF4JLoggerProxy.debug(this,
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
    /**
     * Indicates if the given cluster instance is a match for the given affinity.
     *
     * @param inClusterData a <code>ClusterData</code> value
     * @param inAffinity an <code>int</code> value
     * @return a <code>boolean</code> value
     */
    private static boolean isAffinityMatch(ClusterData inClusterData,
                                           int inAffinity)
    {
        while(inAffinity > inClusterData.getTotalInstances()) {
            inAffinity -= inClusterData.getTotalInstances();
        }
        return inAffinity == inClusterData.getInstanceNumber();
    }
    /**
     * provides access to the FIX session data store
     */
    @Autowired
    private FixSessionDao fixSessionDao;
    /**
     * constructs a FIX settings provider object
     */
    @Autowired
    private FixSettingsProviderFactory fixSettingsProviderFactory;
    /**
     * caches session names by session id
     */
    private final Map<SessionID,String> sessionNamesBySessionId = new HashMap<>();
    /**
     * cache is session time calculation
     */
    private final Cache<SessionID,Boolean> isSessionTimeCache = CacheBuilder.newBuilder().expireAfterWrite(10,TimeUnit.SECONDS).build();
    /**
     * key used to indicate active days for a session
     */
    public static final String sessionDaysKey = "org.marketcetera.sessiondays";
}
