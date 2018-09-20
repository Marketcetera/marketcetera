package org.marketcetera.brokers.service;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.marketcetera.admin.User;
import org.marketcetera.brokers.BrokerConstants;
import org.marketcetera.brokers.BrokerStatusListener;
import org.marketcetera.brokers.SessionCustomization;
import org.marketcetera.cluster.AbstractCallableClusterTask;
import org.marketcetera.cluster.ClusterData;
import org.marketcetera.cluster.service.ClusterListener;
import org.marketcetera.cluster.service.ClusterMember;
import org.marketcetera.cluster.service.ClusterService;
import org.marketcetera.core.ApplicationContextProvider;
import org.marketcetera.fix.AcceptorSessionAttributes;
import org.marketcetera.fix.ActiveFixSession;
import org.marketcetera.fix.FixSession;
import org.marketcetera.fix.FixSessionDay;
import org.marketcetera.fix.FixSessionListener;
import org.marketcetera.fix.FixSessionStatus;
import org.marketcetera.fix.FixSettingsProvider;
import org.marketcetera.fix.FixSettingsProviderFactory;
import org.marketcetera.fix.MutableActiveFixSession;
import org.marketcetera.fix.MutableActiveFixSessionFactory;
import org.marketcetera.fix.ServerFixSession;
import org.marketcetera.fix.ServerFixSessionFactory;
import org.marketcetera.fix.SessionNameProvider;
import org.marketcetera.fix.SessionSchedule;
import org.marketcetera.fix.SessionSettingsGenerator;
import org.marketcetera.fix.impl.SimpleActiveFixSession;
import org.marketcetera.persist.CollectionPageResponse;
import org.marketcetera.persist.PageRequest;
import org.marketcetera.trade.BrokerID;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.nocrala.tools.texttablefmt.BorderStyle;
import org.nocrala.tools.texttablefmt.CellStyle;
import org.nocrala.tools.texttablefmt.CellStyle.HorizontalAlign;
import org.nocrala.tools.texttablefmt.ShownBorders;
import org.nocrala.tools.texttablefmt.Table;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Lists;
import com.google.common.eventbus.EventBus;

import quickfix.ConfigError;
import quickfix.FieldConvertError;
import quickfix.Session;
import quickfix.SessionID;
import quickfix.SessionSettings;

/* $License$ */

/**
 * Provides broker services.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@Service
public class BrokerServiceImpl
        implements BrokerService,ClusterListener,SessionNameProvider
{
    /* (non-Javadoc)
     * @see org.marketcetera.brokers.service.BrokerService#getBroker(org.marketcetera.trade.BrokerID)
     */
    @Override
    public ActiveFixSession getActiveFixSession(BrokerID inBrokerId)
    {
        FixSession underlyingFixSession = fixSessionProvider.findFixSessionByBrokerId(inBrokerId);
        if(underlyingFixSession == null) {
            return null;
        }
        SessionCustomization sessionCustomization = getSessionCustomization(underlyingFixSession);
        return activeFixSessionFactory.create(underlyingFixSession,
                                              getClusterData(new SessionID(underlyingFixSession.getSessionId())),
                                              getFixSessionStatus(inBrokerId),
                                              sessionCustomization);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.brokers.service.BrokerService#getBroker(quickfix.SessionID)
     */
    @Override
    public ActiveFixSession getActiveFixSession(SessionID inSessionId)
    {
        FixSession underlyingFixSession = fixSessionProvider.findFixSessionBySessionId(inSessionId);
        if(underlyingFixSession == null) {
            return null;
        }
        return getActiveFixSession(new BrokerID(underlyingFixSession.getBrokerId()));
    }
    /* (non-Javadoc)
     * @see org.marketcetera.brokers.service.BrokerService#getBrokers()
     */
    @Override
    public Collection<ActiveFixSession> getActiveFixSessions()
    {
        List<FixSession> fixSessions = fixSessionProvider.findFixSessions();
        Collection<ActiveFixSession> activeFixSessions = Lists.newArrayList();
        for(FixSession fixSession : fixSessions) {
            BrokerID brokerId = new BrokerID(fixSession.getBrokerId());
            SessionID sessionId = new SessionID(fixSession.getSessionId());
            FixSessionStatus sessionStatus = getFixSessionStatus(brokerId);
            activeFixSessions.add(activeFixSessionFactory.create(fixSession,
                                                                 getClusterData(sessionId),
                                                                 sessionStatus,
                                                                 getSessionCustomization(fixSession)));
        }
        return activeFixSessions;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.brokers.service.BrokerService#getActiveFixSessions(org.marketcetera.persist.PageRequest)
     */
    @Override
    public CollectionPageResponse<ActiveFixSession> getActiveFixSessions(PageRequest inPageRequest)
    {
        CollectionPageResponse<ActiveFixSession> result = new CollectionPageResponse<>();
        CollectionPageResponse<FixSession> intermediateResult = fixSessionProvider.findFixSessions(inPageRequest);
        for(FixSession fixSession : intermediateResult.getElements()) {
            BrokerID brokerId = new BrokerID(fixSession.getBrokerId());
            SessionID sessionId = new SessionID(fixSession.getSessionId());
            FixSessionStatus sessionStatus = getFixSessionStatus(brokerId);
            result.getElements().add(activeFixSessionFactory.create(fixSession,
                                                                    getClusterData(sessionId),
                                                                    sessionStatus,
                                                                    getSessionCustomization(fixSession)));
        }
        result.setHasContent(intermediateResult.hasContent());
        result.setPageMaxSize(intermediateResult.getPageMaxSize());
        result.setPageNumber(intermediateResult.getPageNumber());
        result.setPageSize(intermediateResult.getPageSize());
        result.setSortOrder(intermediateResult.getSortOrder());
        result.setTotalPages(intermediateResult.getTotalPages());
        result.setTotalSize(intermediateResult.getTotalSize());
        return result;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.brokers.service.BrokerService#getServerFixSession(quickfix.SessionID)
     */
    @Override
    public ServerFixSession getServerFixSession(SessionID inSessionId)
    {
        ActiveFixSession activeFixSession = getActiveFixSession(inSessionId);
        if(activeFixSession == null) {
            SLF4JLoggerProxy.warn(this,
                                  "No session for {}",
                                  inSessionId);
            return null;
        }
        return serverFixSessionFactory.create(activeFixSession,
                                              getSessionCustomization(activeFixSession.getFixSession()));
    }
    /* (non-Javadoc)
     * @see org.marketcetera.brokers.service.BrokerService#getServerFixSession(org.marketcetera.trade.BrokerID)
     */
    @Override
    public ServerFixSession getServerFixSession(BrokerID inBrokerId)
    {
        ActiveFixSession activeFixSession = getActiveFixSession(inBrokerId);
        if(activeFixSession == null) {
            SLF4JLoggerProxy.warn(this,
                                  "No session for {}",
                                  inBrokerId);
            return null;
        }
        return serverFixSessionFactory.create(activeFixSession,
                                              getSessionCustomization(activeFixSession.getFixSession()));
    }
    /* (non-Javadoc)
     * @see org.marketcetera.brokers.service.BrokerService#getServerFixSessions()
     */
    @Override
    public Collection<ServerFixSession> getServerFixSessions()
    {
        Collection<ServerFixSession> results = Lists.newArrayList();
        for(ActiveFixSession activeFixSession : getActiveFixSessions()) {
            results.add(serverFixSessionFactory.create(activeFixSession,
                                                       getSessionCustomization(activeFixSession.getFixSession())));
        }
        return results;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.brokers.service.BrokerService#getBrokerStatus(org.marketcetera.trade.BrokerID)
     */
    @Override
    public FixSessionStatus getFixSessionStatus(BrokerID inBrokerId)
    {
        synchronized(clusterBrokerStatus) {
            // search for the best status available
            ActiveFixSession bestStatus = null;
            for(ActiveFixSession brokerStatus : clusterBrokerStatus) {
                if(brokerStatus.getFixSession().getBrokerId().equals(inBrokerId.getValue())) {
                    if(brokerStatus.getStatus().isPrimary()) {
                        // if this is the primary for this broker, return this status immediately
                        return brokerStatus.getStatus();
                    }
                    // record this one, but keep looking for the primary (best status might be on another node)
                    bestStatus = brokerStatus;
                }
            }
            return bestStatus == null?FixSessionStatus.UNKNOWN:bestStatus.getStatus();
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.brokers.BrokerStatusPublisher#addBrokerStatusListener(org.marketcetera.brokers.BrokerStatusListener)
     */
    @Override
    public void addBrokerStatusListener(BrokerStatusListener inListener)
    {
        brokerStatusEventBus.register(inListener);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.brokers.BrokerStatusPublisher#removeBrokerStatusListener(org.marketcetera.brokers.BrokerStatusListener)
     */
    @Override
    public void removeBrokerStatusListener(BrokerStatusListener inListener)
    {
        brokerStatusEventBus.unregister(inListener);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.brokers.service.BrokerService#getSessionStart(quickfix.SessionID)
     */
    @Override
    public Date getSessionStart(SessionID inSessionId)
    {
        FixSession fixSession = fixSessionProvider.findFixSessionBySessionId(inSessionId);
        Date returnValue = new DateTime().withTimeAtStartOfDay().toDate();
        if(fixSession == null) {
            SLF4JLoggerProxy.debug(this,
                                   "No fix session for {}, using {} instead",
                                   inSessionId,
                                   returnValue);
            return returnValue;
        }
        try {
            SessionSettings settings = SessionSettingsGenerator.generateSessionSettings(Lists.newArrayList(fixSession),
                                                                                        fixSettingsProviderFactory,
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
     * @see org.marketcetera.brokers.service.BrokerService#getNextSessionStart(quickfix.SessionID)
     */
    @Override
    public Date getNextSessionStart(SessionID inSessionId)
    {
        FixSession fixSession = fixSessionProvider.findFixSessionBySessionId(inSessionId);
        Date returnValue = new DateTime().withTimeAtStartOfDay().plusDays(1).toDate();
        if(fixSession == null) {
            SLF4JLoggerProxy.debug(this,
                                   "No fix session for {}, using {} instead",
                                   inSessionId,
                                   returnValue);
            return returnValue;
        }
        try {
            SessionSettings settings = SessionSettingsGenerator.generateSessionSettings(Lists.newArrayList(fixSession),
                                                                                        fixSettingsProviderFactory,
                                                                                        true);
            SessionSchedule sessionSchedule = new SessionSchedule(settings,
                                                                  inSessionId);
            returnValue = sessionSchedule.getNextStartTime();
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
     * @see org.marketcetera.fix.SessionNameProvider#getSessionName(quickfix.SessionID)
     */
    @Override
    public String getSessionName(SessionID inSessionId)
    {
        String value = sessionNamesBySessionId.getIfPresent(inSessionId);
        if(value == null) {
            FixSession session = null;
            try {
                session = fixSessionProvider.findFixSessionBySessionId(inSessionId);
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
     * @see org.marketcetera.brokers.service.BrokerService#reportBrokerStatus(org.marketcetera.trade.BrokerID, org.marketcetera.fix.FixSessionStatus)
     */
    @Override
    public void reportBrokerStatus(BrokerID inBrokerId,
                                   FixSessionStatus inFixSessionStatus)
    {
        SLF4JLoggerProxy.trace(this,
                               "Reporting {} for {}",
                               inFixSessionStatus,
                               inBrokerId);
        try {
            FixSession fixSession = findFixSessionByBrokerId(inBrokerId);
            if(fixSession == null) {
                SLF4JLoggerProxy.warn(this,
                                      "Cannot report broker status for {}: no FIX session with that broker id",
                                      inBrokerId);
                return;
            }
            ActiveFixSession activeFixSession = generateBrokerStatus(fixSession,
                                                                     inFixSessionStatus);
            String xmlStatus = marshall(activeFixSession);
            String key = BrokerConstants.brokerStatusPrefix+inBrokerId+fixSession.getHost();
            clusterService.setAttribute(key,
                                        xmlStatus);
            brokerStatusEventBus.post(activeFixSession);
        } catch (JAXBException e) {
            SLF4JLoggerProxy.warn(this,
                                  e,
                                  "Unable to update broker status");
        } catch (NullPointerException ignored) {
            // these can happen on shutdown and can be safely ignored
        } catch (Exception e) {
            SLF4JLoggerProxy.warn(this,
                                  e,
                                  "Unable to update broker status");
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.brokers.service.BrokerService#reportBrokerStatusFromAll(org.marketcetera.fix.FixSession, org.marketcetera.fix.FixSessionStatus)
     */
    @Override
    public void reportBrokerStatusFromAll(FixSession inFixSession,
                                          FixSessionStatus inStatusToReport)
    {
        ReportBrokerStatusTask reportStatusTask = new ReportBrokerStatusTask(inFixSession,
                                                                             inStatusToReport);
        clusterService.execute(reportStatusTask);
    }
//    /* (non-Javadoc)
//     * @see org.marketcetera.brokers.service.BrokerService#findActiveFixSessions(org.marketcetera.persist.PageRequest)
//     */
//    @Override
//    public CollectionPageResponse<ActiveFixSession> findActiveFixSessions(PageRequest inPageRequest)
//    {
//        CollectionPageResponse<FixSession> fixSessionResponse = fixSessionProvider.findFixSessions(inPageRequest);
//        CollectionPageResponse<ActiveFixSession> response = new CollectionPageResponse<>();
//        response.setPageMaxSize(fixSessionResponse.getPageMaxSize());
//        response.setPageNumber(fixSessionResponse.getPageNumber());
//        response.setPageSize(fixSessionResponse.getPageSize());
//        response.setTotalPages(fixSessionResponse.getTotalPages());
//        response.setTotalSize(fixSessionResponse.getTotalSize());
//        for(FixSession session : fixSessionResponse.getElements()) {
//            response.getElements().add(getActiveFixSession(session));
//        }
//        response.setSortOrder(inPageRequest.getSortOrder());
//        return response;
//    }
//  /* (non-Javadoc)
//  * @see org.marketcetera.brokers.service.BrokerService#getActiveFixSession(org.marketcetera.fix.FixSession)
//  */
// @Override
// public ActiveFixSession getActiveFixSession(FixSession inFixSession)
// {
//     ClusteredBrokerStatus brokerStatus = (ClusteredBrokerStatus)getBrokerStatus(new BrokerID(inFixSession.getBrokerId()));
//     MutableActiveFixSession activeFixSession = activeFixSessionFactory.create();
//     activeFixSession.setAffinity(inFixSession.getAffinity());
//     activeFixSession.setBrokerId(inFixSession.getBrokerId());
//     activeFixSession.setDescription(inFixSession.getDescription());
//     activeFixSession.setHost(inFixSession.getHost());
//     activeFixSession.setInstance(brokerStatus.getClusterData().getHostId());
//     activeFixSession.setIsAcceptor(inFixSession.isAcceptor());
//     activeFixSession.setIsEnabled(inFixSession.isEnabled());
//     activeFixSession.setMappedBrokerId(inFixSession.getMappedBrokerId());
//     activeFixSession.setName(inFixSession.getName());
//     activeFixSession.setPort(inFixSession.getPort());
////     activeFixSession.setSenderSequenceNumber(brokerStatus.);
//     
//     return activeFixSession;
// }
//    /**
//     * creates {@link MutableActiveFixSession} objects
//     */
//    @Autowired
//    private MutableActiveFixSessionFactory activeFixSessionFactory;
    /**
     * 
     *
     *
     * @param inFixSession
     * @param inStatus
     * @return
     */
    private ActiveFixSession generateBrokerStatus(FixSession inFixSession,
                                                  FixSessionStatus inStatus)
    {
        return activeFixSessionFactory.create(inFixSession,
                                              instanceData,
                                              inStatus,
                                              getSessionCustomization(inFixSession));
    }
    /* (non-Javadoc)
     * @see org.marketcetera.brokers.service.BrokerService#generateBroker(org.marketcetera.fix.FixSession)
     */
    @Override
    public ActiveFixSession generateBroker(FixSession inFixSession)
    {
        return activeFixSessionFactory.create(inFixSession,
                                              instanceData,
                                              getFixSessionStatus(new BrokerID(inFixSession.getBrokerId())),
                                              getSessionCustomization(inFixSession));
    }
    /* (non-Javadoc)
     * @see org.marketcetera.brokers.service.BrokerService#addFixSessionListener(org.marketcetera.fix.FixSessionListener)
     */
    @Override
    public void addFixSessionListener(FixSessionListener inFixSessionListener)
    {
        SLF4JLoggerProxy.debug(this,
                               "Adding FIX session listener: {}",
                               inFixSessionListener);
        fixSessionListeners.add(inFixSessionListener);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.brokers.service.BrokerService#removeFixSessionListener(org.marketcetera.fix.FixSessionListener)
     */
    @Override
    public void removeFixSessionListener(FixSessionListener inFixSessionListener)
    {
        fixSessionListeners.remove(inFixSessionListener);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.brokers.service.BrokerService#getFixSessionListeners()
     */
    @Override
    public Collection<FixSessionListener> getFixSessionListeners()
    {
        return fixSessionListeners;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.brokers.service.BrokerService#findFixSessionByBrokerId(org.marketcetera.trade.BrokerID)
     */
    @Override
    public FixSession findFixSessionByBrokerId(BrokerID inBrokerId)
    {
        return fixSessionProvider.findFixSessionByBrokerId(inBrokerId);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.brokers.service.BrokerService#getActualSessionStart(quickfix.SessionID)
     */
    @Override
    public Date getActualSessionStart(SessionID inSessionId)
    {
        try {
            GetSessionStartTask getSessionStartTask = new GetSessionStartTask(inSessionId);
            Map<Object,Future<Date>> results = clusterService.execute(getSessionStartTask);
            Date value = null;
            for(Map.Entry<Object,Future<Date>> entry : results.entrySet()) {
                Date returnedValue = entry.getValue().get();
                if(returnedValue != null) {
                    value = returnedValue;
                }
            }
            SLF4JLoggerProxy.debug(this,
                                   "Get session start task {} completed successfully: {}",
                                   inSessionId,
                                   value);
            if(value != null) {
                return value;
            }
        } catch (Exception e) {
            SLF4JLoggerProxy.warn(this,
                                  e,
                                  "Unable to retrieve session start for {}",
                                  inSessionId);
            if(e instanceof RuntimeException) {
                throw (RuntimeException)e;
            }
            throw new RuntimeException(e);
        }
        SLF4JLoggerProxy.debug(this,
                               "No session start for {}",
                               inSessionId);
        return null;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.brokers.service.BrokerService#getFixSettingsFor(int)
     */
    @Override
    public AcceptorSessionAttributes getFixSettingsFor(int inAffinity)
    {
        try {
            GetSessionAttributesTask getSessionAttributesTask = new GetSessionAttributesTask(inAffinity);
            Map<Object,Future<AcceptorSessionAttributes>> results = clusterService.execute(getSessionAttributesTask);
            AcceptorSessionAttributes attributes = null;
            for(Map.Entry<Object,Future<AcceptorSessionAttributes>> entry : results.entrySet()) {
                attributes = entry.getValue().get();
                if(attributes != null) {
                    break;
                }
            }
            if(attributes == null) {
                attributes = new AcceptorSessionAttributes();
                attributes.setAffinity(inAffinity);
                FixSettingsProvider provider = fixSettingsProviderFactory.create();
                attributes.setPort(provider.getAcceptorPort());
                attributes.setHost(provider.getAcceptorHost());
                SLF4JLoggerProxy.debug(this,
                                       "No cluster member claimed affinity {}, using defaults: {}",
                                       attributes);
            } else {
                SLF4JLoggerProxy.debug(this,
                                       "Retrieved session attributes {}",
                                       attributes);
            }
            return attributes;
        } catch (Exception e) {
            SLF4JLoggerProxy.warn(this,
                                  e,
                                  "Failed to retrieve session attributes for {}",
                                  inAffinity);
            if(e instanceof RuntimeException) {
                throw (RuntimeException)e;
            }
            throw new RuntimeException(e);
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.brokers.service.BrokerService#generateSessionSettings(java.util.Collection)
     */
    @Override
    public SessionSettings generateSessionSettings(Collection<FixSession> inFixSessions)
    {
        return SessionSettingsGenerator.generateSessionSettings(inFixSessions,
                                                                fixSettingsProviderFactory);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.brokers.service.BrokerService#isAffinityMatch(org.marketcetera.fix.FixSession, org.marketcetera.cluster.ClusterData)
     */
    @Override
    public boolean isAffinityMatch(FixSession inFixSession,
                                   ClusterData inClusterData)
    {
        return isAffinityMatch(inClusterData,
                               inFixSession.getAffinity());
    }
    /* (non-Javadoc)
     * @see org.marketcetera.brokers.service.BrokerService#isAffinityMatch(org.marketcetera.cluster.ClusterData, int)
     */
    @Override
    public boolean isAffinityMatch(ClusterData inClusterData,
                                   int inAffinity)
    {
        while(inAffinity > inClusterData.getTotalInstances()) {
            inAffinity -= inClusterData.getTotalInstances();
        }
        return inAffinity == inClusterData.getInstanceNumber();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.brokers.service.BrokerService#isUserAllowed(org.marketcetera.trade.BrokerID, org.marketcetera.admin.User)
     */
    @Override
    public boolean isUserAllowed(BrokerID inBrokerId,
                                 User inUser)
    {
        ServerFixSession fixSession = getServerFixSession(inBrokerId);
        if(fixSession == null) {
            SLF4JLoggerProxy.warn(this,
                                  "No session for {}",
                                  inBrokerId);
            return false;
        }
        if(!fixSession.getUserBlacklist().isEmpty() && fixSession.getUserBlacklist().contains(inUser)) {
            return false;
        }
        return fixSession.getUserWhitelist().isEmpty() || fixSession.getUserWhitelist().contains(inUser);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.brokers.service.BrokerService#isSessionTime(quickfix.SessionID)
     */
    @Override
    public boolean isSessionTime(SessionID inSessionId)
    {
        Boolean result = isSessionTimeCache.getIfPresent(inSessionId);
        if(result != null) {
            return result;
        }
        try {
            FixSession session = fixSessionProvider.findFixSessionBySessionId(inSessionId);
            if(session == null) {
                result = false;
                return result;
            }
            String rawDaysValue = StringUtils.trimToNull(session.getSessionSettings().get(BrokerConstants.sessionDaysKey));
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
    /* (non-Javadoc)
     * @see org.marketcetera.brokers.service.BrokerService#getSessionCustomization(org.marketcetera.fix.FixSession)
     */
    @Override
    public SessionCustomization getSessionCustomization(FixSession inFixSession)
    {
        String sessionCustomizationName = inFixSession.getSessionSettings().get(BrokerConstants.sessionCustomizationKey);
        if(sessionCustomizationName == null) {
            return null;
        }
        return sessionCustomizationsByName.getIfPresent(sessionCustomizationName);
    }
    /* (non-Javadoc)
     * @see com.marketcetera.matp.service.ClusterListener#memberAdded(com.marketcetera.matp.service.ClusterMember)
     */
    @Override
    public void memberAdded(ClusterMember inAddedMember)
    {
        updateBrokerStatus();
    }
    /* (non-Javadoc)
     * @see com.marketcetera.matp.service.ClusterListener#memberRemoved(com.marketcetera.matp.service.ClusterMember)
     */
    @Override
    public void memberRemoved(ClusterMember inRemovedMember)
    {
        updateBrokerStatus();
    }
    /* (non-Javadoc)
     * @see com.marketcetera.matp.service.ClusterListener#memberChanged(com.marketcetera.matp.service.ClusterMember)
     */
    @Override
    public void memberChanged(ClusterMember inChangedMember)
    {
        updateBrokerStatus();
    }
    /**
     * Get the clusterService value.
     *
     * @return a <code>ClusterService</code> value
     */
    public ClusterService getClusterService()
    {
        return clusterService;
    }
    /**
     * Sets the clusterService value.
     *
     * @param inClusterService a <code>ClusterService</code> value
     */
    public void setClusterService(ClusterService inClusterService)
    {
        clusterService = inClusterService;
    }
    /**
     * Validates and starts the object.
     */
    @PostConstruct
    public void start()
    {
        instanceData = clusterService.getInstanceData();
        ApplicationContextProvider tmpAppCxProvider = new ApplicationContextProvider();
        tmpAppCxProvider.setApplicationContext(applicationContext);
        clusterService.addClusterListener(this);
        sessionCustomizationsByName.invalidateAll();
        if(sessionCustomizations != null) {
            for(SessionCustomization sessionCustomization : sessionCustomizations) {
                if(sessionCustomizationsByName.asMap().containsKey(sessionCustomization.getName())) {
                    SLF4JLoggerProxy.info(this,
                                          "More than one session customization exists with the name {} - one will replace the other, possibly yielding unexpected results",
                                          sessionCustomization.getName());
                }
                sessionCustomizationsByName.put(sessionCustomization.getName(),
                                                sessionCustomization);
            }
        }
        SLF4JLoggerProxy.info(this,
                              "Broker service started");
    }
    /**
     * Updates all broker status
     */
    private void updateBrokerStatus()
    {
        synchronized(clusterBrokerStatus) {
            List<ActiveFixSession> updatedStatus = new ArrayList<>();
            for(ClusterMember member : clusterService.getClusterMembers()) {
                for(Map.Entry<String,String> attributeEntry : clusterService.getAttributes(member.getUuid()).entrySet()) {
                    String key = attributeEntry.getKey();
                    if(key.startsWith(BrokerConstants.brokerStatusPrefix)) {
                        String rawValue = String.valueOf(attributeEntry.getValue());
                        ActiveFixSession status;
                        try {
                            status = (ActiveFixSession)unmarshall(rawValue);
                            updatedStatus.add(status);
                        } catch (Exception e) {
                            SLF4JLoggerProxy.warn(this,
                                                  e,
                                                  "Unable to update broker status");
                            return;
                        }
                    }
                }
            }
            clusterBrokerStatus.clear();
            clusterBrokerStatus.addAll(updatedStatus);
            logBrokerInstanceData();
        }
    }
    /**
     * Marshals the given value as XML.
     *
     * @param inData a <code>Clazz</code> value
     * @return a <code>String</code> value
     * @throws JAXBException if the given object cannot be marshaled
     */
    private static <Clazz> String marshall(Clazz inData)
            throws JAXBException
    {
        synchronized(marshaller) {
            StringWriter output = new StringWriter();
            marshaller.marshal(inData,
                               output);
            return output.getBuffer().toString();
        }
    }
    /**
     * Unmarshals the given data from XML.
     *
     * @param inData a <code>String</code> value
     * @return an <code>Object</code> value
     * @throws JAXBException if an error occurs unmarshalling the data
     */
    private static Object unmarshall(String inData)
            throws JAXBException
    {
        synchronized(unmarshaller) {
            return unmarshaller.unmarshal(new InputStreamReader(new ByteArrayInputStream(inData.getBytes())));
        }
    }
    /**
     * Logs the broker instance data.
     */
    private void logBrokerInstanceData()
    {
        Collection<ActiveFixSession> activeFixSessions = Lists.newArrayList(clusterBrokerStatus);
        // x-axis is the number of instances total, across all hosts
        SortedSet<String> sortedClusterData = new TreeSet<>();
        // y-axis is the sessions across all hosts
        SortedSet<BrokerID> sortedBrokers = new TreeSet<>();
        Map<String,ActiveFixSession> statusByKey = new HashMap<>();
        for(ActiveFixSession activeFixSession : activeFixSessions) {
            sortedClusterData.add(activeFixSession.getClusterData().toString());
            sortedBrokers.add(new BrokerID(activeFixSession.getFixSession().getBrokerId()));
            statusByKey.put(activeFixSession.getFixSession().getBrokerId()+"-"+activeFixSession.getClusterData(),
                            activeFixSession);
        }
        Table table = new Table(sortedClusterData.size()+1,
                                BorderStyle.CLASSIC_COMPATIBLE_WIDE,
                                ShownBorders.ALL,
                                false);
        table.addCell("Sessions",
                      cellStyle);
        for(String data : sortedClusterData) {
            table.addCell(data,
                          cellStyle);
        }
        SLF4JLoggerProxy.debug(this,
                               "Sorted data is {}, sorted sessions is {}, session status is {}",
                               sortedClusterData,
                               sortedBrokers,
                               activeFixSessions);
        for(BrokerID brokerId : sortedBrokers) {
            table.addCell(brokerId.getValue(),
                          cellStyle);
            for(String host : sortedClusterData) {
                // determine what the status is on this host for this broker
                String key = brokerId.getValue()+"-"+host;
                ActiveFixSession activeFixSession = statusByKey.get(key);
                if(activeFixSession == null) {
                    table.addCell("--",
                                  cellStyle);
                } else {
                    switch(activeFixSession.getStatus()) {
                        case AFFINITY_MISMATCH:
                            table.addCell("--",
                                          cellStyle);
                            break;
                        case BACKUP:
                            table.addCell("backup",
                                          cellStyle);
                            break;
                        case CONNECTED:
                            table.addCell("available",
                                          cellStyle);
                            break;
                        case DELETED:
                            table.addCell("deleted",
                                          cellStyle);
                            break;
                        case DISABLED:
                            table.addCell("disabled",
                                          cellStyle);
                            break;
                        case DISCONNECTED:
                            table.addCell("disconnected",
                                          cellStyle);
                            break;
                        case NOT_CONNECTED:
                            table.addCell("not connected",
                                          cellStyle);
                            break;
                        case STOPPED:
                            table.addCell("stopped",
                                          cellStyle);
                            break;
                        default:
                            throw new UnsupportedOperationException("Cannot display status " + activeFixSession.getStatus());
                    }
                }
            }
        }
        String thisBrokerLog = table.render();
        if(!thisBrokerLog.equals(lastBrokerLog)) {
            SLF4JLoggerProxy.info(BrokerConstants.brokerStatusCategory,
                                  "{}{}",
                                  System.lineSeparator(),
                                  thisBrokerLog);
        }
        lastBrokerLog = thisBrokerLog;
    }
    /**
     * Get the cluster data for the given session.
     *
     * @param inSessionId a <code>SessionID</code> value
     * @return a <code>ClusterData</code> value or <code>null</code>
     */
    private ClusterData getClusterData(SessionID inSessionId)
    {
        try {
            Map<Object,Future<ClusterData>> results = clusterService.execute(new FindClusterDataTask(inSessionId));
            ClusterData clusterData = null;
            for(Map.Entry<Object,Future<ClusterData>> entry : results.entrySet()) {
                ClusterData returnedValue = entry.getValue().get();
                if(returnedValue != null) {
                    clusterData = returnedValue;
                    break;
                }
            }
            return clusterData;
        } catch (Exception e) {
            SLF4JLoggerProxy.warn(this,
                                  e,
                                  "Unable to determine cluster data for {}",
                                  inSessionId);
            return null;
        }
    }
    /**
     * Finds the cluster data for a given session.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    private static class FindClusterDataTask
            extends AbstractCallableClusterTask<ClusterData>
    {
        /* (non-Javadoc)
         * @see java.util.concurrent.Callable#call()
         */
        @Override
        public ClusterData call()
                throws Exception
        {
            if(Session.doesSessionExist(sessionId)) {
                return getClusterService().getInstanceData();
            }
            return null;
        }
        /**
         * Create a new FindClusterDataTask instance.
         *
         * @param inSessionId a <code>SessionID</code> value
         */
        private FindClusterDataTask(SessionID inSessionId)
        {
            sessionId = inSessionId;
        }
        /**
         * session id value
         */
        private final SessionID sessionId;
        private static final long serialVersionUID = -8786699688513719193L;
    }
    /**
     * cluster data for this instance
     */
    private ClusterData instanceData;
    /**
     * provides fix sessions
     */
    @Autowired
    private FixSessionProvider fixSessionProvider;
    /**
     * provides access to cluster services
     */
    @Autowired
    private ClusterService clusterService;
    /**
     * holds all session customizations known to the system
     */
    @Autowired(required=false)
    private List<SessionCustomization> sessionCustomizations;
    /**
     * provides access to the application context
     */
    @Autowired
    private ApplicationContext applicationContext;
    /**
     * provides FIX settings
     */
    @Autowired
    private FixSettingsProviderFactory fixSettingsProviderFactory;
    /**
     * creates {@link MutableActiveFixSession} objects
     */
    @Autowired
    private MutableActiveFixSessionFactory activeFixSessionFactory;
    /**
     * creates {@link ServerFixSession} objects
     */
    @Autowired
    private ServerFixSessionFactory serverFixSessionFactory;
    /**
     * publishes broker status changes
     */
    private final EventBus brokerStatusEventBus = new EventBus();
    /**
     * context used to marshal and unmarshal messages
     */
    private static final JAXBContext context;
    /**
     * marshals objects to XML
     */
    private static final Marshaller marshaller;
    /**
     * unmarshals objects from XML
     */
    private static final Unmarshaller unmarshaller;
    /**
     * caches the last broker status
     */
    private volatile String lastBrokerLog = null;
    /**
     * holds fix session listeners
     */
    private final Queue<FixSessionListener> fixSessionListeners = new ConcurrentLinkedQueue<>();
    /**
     * describes the style of the table cell
     */
    private static final CellStyle cellStyle = new CellStyle(HorizontalAlign.center);
    /**
     * session customizations by name
     */
    private final Cache<String,SessionCustomization> sessionCustomizationsByName = CacheBuilder.newBuilder().build();
    /**
     * cache is session time calculation
     */
    private final Cache<SessionID,Boolean> isSessionTimeCache = CacheBuilder.newBuilder().expireAfterWrite(10,TimeUnit.SECONDS).build();
    /**
     * stores broker status for the whole cluster
     */
    private final List<ActiveFixSession> clusterBrokerStatus = new ArrayList<>();
    /**
     * caches session names by session id
     */
    private final Cache<SessionID,String> sessionNamesBySessionId = CacheBuilder.newBuilder().expireAfterAccess(1, TimeUnit.MINUTES).build();
    /**
     * Performs static initialization for this class
     * 
     * @throws RuntimeException if the initialization fails
     */
    static {
        try {
            context = JAXBContext.newInstance(SimpleActiveFixSession.class);
            marshaller = context.createMarshaller();
            unmarshaller = context.createUnmarshaller();
            unmarshaller.setEventHandler(new ValidationEventHandler() {
                @Override
                public boolean handleEvent(ValidationEvent inEvent)
                {
                    throw new RuntimeException(inEvent.getMessage(),
                                               inEvent.getLinkedException());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
