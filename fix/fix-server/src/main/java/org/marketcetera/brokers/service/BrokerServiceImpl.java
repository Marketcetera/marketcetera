package org.marketcetera.brokers.service;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
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
import org.marketcetera.brokers.Broker;
import org.marketcetera.brokers.BrokerConstants;
import org.marketcetera.brokers.BrokerFactory;
import org.marketcetera.brokers.BrokerStatus;
import org.marketcetera.brokers.BrokerStatusBroadcaster;
import org.marketcetera.brokers.BrokerStatusListener;
import org.marketcetera.brokers.BrokersStatus;
import org.marketcetera.brokers.ClusteredBrokerStatus;
import org.marketcetera.brokers.ClusteredBrokersStatus;
import org.marketcetera.brokers.SessionCustomization;
import org.marketcetera.cluster.ClusterData;
import org.marketcetera.cluster.service.ClusterListener;
import org.marketcetera.cluster.service.ClusterMember;
import org.marketcetera.cluster.service.ClusterService;
import org.marketcetera.core.ApplicationContextProvider;
import org.marketcetera.core.PlatformServices;
import org.marketcetera.fix.AcceptorSessionAttributes;
import org.marketcetera.fix.FixSession;
import org.marketcetera.fix.FixSessionAttributeDescriptor;
import org.marketcetera.fix.FixSessionDay;
import org.marketcetera.fix.FixSessionListener;
import org.marketcetera.fix.FixSessionStatus;
import org.marketcetera.fix.FixSettingsProvider;
import org.marketcetera.fix.FixSettingsProviderFactory;
import org.marketcetera.fix.SessionNameProvider;
import org.marketcetera.fix.SessionSchedule;
import org.marketcetera.fix.SessionSettingsGenerator;
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
import com.google.common.collect.Sets;

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
        implements BrokerService,ClusterListener,SessionNameProvider,BrokerStatusBroadcaster
{
    /* (non-Javadoc)
     * @see org.marketcetera.brokers.service.BrokerService#getBroker(quickfix.SessionID)
     */
    @Override
    public Broker getBroker(SessionID inSessionId)
    {
        FixSession underlyingFixSession = fixSessionProvider.findFixSessionBySessionId(inSessionId);
        if(underlyingFixSession == null) {
            return null;
        }
        return generateBroker(underlyingFixSession);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.brokers.service.BrokerService#getBrokers()
     */
    @Override
    public Collection<Broker> getBrokers()
    {
        Collection<Broker> brokers = Lists.newArrayList();
        for(FixSession underlyingFixSession : fixSessionProvider.findFixSessions()) {
            brokers.add(generateBroker(underlyingFixSession));
        }
        return brokers;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.brokers.service.BrokerService#getBrokerStatus(org.marketcetera.trade.BrokerID)
     */
    @Override
    public BrokerStatus getBrokerStatus(BrokerID inBrokerId)
    {
        synchronized(clusterBrokerStatus) {
            // search for the best status available
            ClusteredBrokerStatus bestStatus = null;
            for(ClusteredBrokerStatus brokerStatus : clusterBrokerStatus) {
                if(brokerStatus.getId().equals(inBrokerId)) {
                    if(brokerStatus.getStatus().isPrimary()) {
                        // if this is the primary for this broker, return this status immediately
                        return brokerStatus;
                    }
                    // record this one, but keep looking for the primary (best status might be on another node)
                    bestStatus = brokerStatus;
                }
            }
            return bestStatus;
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.brokers.BrokerStatusPublisher#addBrokerStatusListener(org.marketcetera.brokers.BrokerStatusListener)
     */
    @Override
    public void addBrokerStatusListener(BrokerStatusListener inListener)
    {
        brokerStatusListeners.add(inListener);
        for(BrokerStatus brokerStatus : clusterBrokerStatus) {
            inListener.receiveBrokerStatus(brokerStatus);
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.brokers.BrokerStatusPublisher#removeBrokerStatusListener(org.marketcetera.brokers.BrokerStatusListener)
     */
    @Override
    public void removeBrokerStatusListener(BrokerStatusListener inListener)
    {
        brokerStatusListeners.remove(inListener);
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
     * @see org.marketcetera.brokers.service.BrokerService#findFixSessionBySessionId(quickfix.SessionID)
     */
    @Override
    public FixSession findFixSessionBySessionId(SessionID inSessionId)
    {
        return fixSessionProvider.findFixSessionBySessionId(inSessionId);
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
     * @see org.marketcetera.brokers.service.BrokerService#reportBrokerStatus(org.marketcetera.brokers.BrokerStatus)
     */
    @Override
    public void reportBrokerStatus(BrokerStatus inBrokerStatus)
    {
        SLF4JLoggerProxy.trace(this,
                               "Reporting {}",
                               inBrokerStatus);
        try {
            String xmlStatus = marshall(inBrokerStatus);
            String key = BrokerConstants.brokerStatusPrefix+inBrokerStatus.getId()+inBrokerStatus.getHost();
            clusterService.setAttribute(key,
                                        xmlStatus);
        } catch (JAXBException e) {
            SLF4JLoggerProxy.warn(this,
                                  e,
                                  "Unable to update broker status");
            return;
        } catch (NullPointerException ignored) {
            // these can happen on shutdown and can be safely ignored
        } catch (Exception e) {
            SLF4JLoggerProxy.warn(this,
                                  e,
                                  "Unable to update broker status");
        }
        for(BrokerStatusListener brokerStatusListener : brokerStatusListeners) {
            try {
                brokerStatusListener.receiveBrokerStatus(inBrokerStatus);
            } catch (Exception e) {
                PlatformServices.handleException(this,
                                                 "Error reporting broker status",
                                                 e);
            }
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
    /* (non-Javadoc)
     * @see org.marketcetera.brokers.service.BrokerService#getBrokersStatus()
     */
    @Override
    public BrokersStatus getBrokersStatus()
    {
        synchronized(clusterBrokerStatus) {
            List<ClusteredBrokerStatus> statuses = new ArrayList<>();
            for(ClusteredBrokerStatus status : clusterBrokerStatus) {
                statuses.add(status);
            }
            return new ClusteredBrokersStatus(statuses);
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.brokers.service.BrokerService#findFixSessionByName(java.lang.String)
     */
    @Override
    public FixSession findFixSessionByName(String inFixSessionName)
    {
        return fixSessionProvider.findFixSessionByName(inFixSessionName);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.brokers.service.BrokerService#getFixSessionAttributeDescriptors()
     */
    @Override
    public Collection<FixSessionAttributeDescriptor> getFixSessionAttributeDescriptors()
    {
        return fixSessionProvider.getFixSessionAttributeDescriptors();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.brokers.service.BrokerService#findFixSessions()
     */
    @Override
    public List<FixSession> findFixSessions()
    {
        return fixSessionProvider.findFixSessions();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.brokers.service.BrokerService#findFixSessions(org.marketcetera.persist.PageRequest)
     */
    @Override
    public CollectionPageResponse<FixSession> findFixSessions(PageRequest inPageRequest)
    {
        return fixSessionProvider.findFixSessions(inPageRequest);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.brokers.service.BrokerService#generateBrokerStatus(org.marketcetera.fix.FixSession, org.marketcetera.cluster.ClusterData, org.marketcetera.fix.FixSessionStatus, boolean)
     */
    @Override
    public BrokerStatus generateBrokerStatus(FixSession inFixSession,
                                             ClusterData inClusterData,
                                             FixSessionStatus inStatus,
                                             boolean inIsLoggedOn)
    {
        ClusteredBrokerStatus status = new ClusteredBrokerStatus(inFixSession,
                                                                 inClusterData,
                                                                 inStatus,
                                                                 inIsLoggedOn);
        Broker broker = generateBroker(inFixSession);
        if(broker.getBrokerAlgos() != null) {
            status.getBrokerAlgos().addAll(broker.getBrokerAlgos());
        }
        return status;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.brokers.service.BrokerService#generateBroker(org.marketcetera.fix.FixSession)
     */
    @Override
    public Broker generateBroker(FixSession inFixSession)
    {
        BrokerID key = new BrokerID(inFixSession.getBrokerId());
        Broker broker = brokerCache.getIfPresent(key);
        if(broker == null) {
            broker = brokerFactory.create(inFixSession,
                                          getSessionCustomization(inFixSession));
            brokerCache.put(key,
                            broker);
        }
        return broker;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.brokers.service.BrokerService#getBroker(org.marketcetera.trade.BrokerID)
     */
    @Override
    public Broker getBroker(BrokerID inBrokerId)
    {
        FixSession underlyingFixSession = fixSessionProvider.findFixSessionByBrokerId(inBrokerId);
        if(underlyingFixSession == null) {
            return null;
        }
        return generateBroker(underlyingFixSession);
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
     * @see org.marketcetera.brokers.service.BrokerService#findFixSessions(boolean, int, int)
     */
    @Override
    public List<FixSession> findFixSessions(boolean inIsAcceptor,
                                            int inInstance,
                                            int inTotalInstances)
    {
        return fixSessionProvider.findFixSessions(inIsAcceptor,
                                                  inInstance,
                                                  inTotalInstances);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.brokers.service.BrokerService#isUserAllowed(org.marketcetera.brokers.Broker, org.marketcetera.admin.User)
     */
    @Override
    public boolean isUserAllowed(Broker inBroker,
                                 User inUser)
    {
        if(!inBroker.getUserBlacklist().isEmpty() && inBroker.getUserBlacklist().contains(inUser)) {
            return false;
        }
        return inBroker.getUserWhitelist().isEmpty() || inBroker.getUserWhitelist().contains(inUser);
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
            FixSession session = findFixSessionBySessionId(inSessionId);
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
    /* (non-Javadoc)
     * @see org.marketcetera.brokers.service.FixSessionProvider#save(org.marketcetera.fix.FixSession)
     */
    @Override
    public FixSession save(FixSession inFixSession)
    {
        return fixSessionProvider.save(inFixSession);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.brokers.service.FixSessionProvider#delete(quickfix.SessionID)
     */
    @Override
    public void delete(SessionID inFixSessionId)
    {
        fixSessionProvider.delete(inFixSessionId);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.brokers.service.FixSessionProvider#disableSession(quickfix.SessionID)
     */
    @Override
    public void disableSession(SessionID inSessionId)
    {
        fixSessionProvider.disableSession(inSessionId);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.brokers.service.FixSessionProvider#enableSession(quickfix.SessionID)
     */
    @Override
    public void enableSession(SessionID inSessionId)
    {
        fixSessionProvider.enableSession(inSessionId);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.brokers.service.FixSessionProvider#save(org.marketcetera.fix.FixSessionAttributeDescriptor)
     */
    @Override
    public FixSessionAttributeDescriptor save(FixSessionAttributeDescriptor inFixSessionAttributeDescriptor)
    {
        return fixSessionProvider.save(inFixSessionAttributeDescriptor);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.brokers.service.FixSessionProvider#stopSession(quickfix.SessionID)
     */
    @Override
    public void stopSession(SessionID inSessionId)
    {
        fixSessionProvider.stopSession(inSessionId);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.brokers.service.FixSessionProvider#startSession(quickfix.SessionID)
     */
    @Override
    public void startSession(SessionID inSessionId)
    {
        fixSessionProvider.startSession(inSessionId);
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
            List<ClusteredBrokerStatus> updatedStatus = new ArrayList<>();
            for(ClusterMember member : clusterService.getClusterMembers()) {
                for(Map.Entry<String,String> attributeEntry : clusterService.getAttributes(member.getUuid()).entrySet()) {
                    String key = attributeEntry.getKey();
                    if(key.startsWith(BrokerConstants.brokerStatusPrefix)) {
                        String rawValue = String.valueOf(attributeEntry.getValue());
                        ClusteredBrokerStatus status;
                        try {
                            status = (ClusteredBrokerStatus)unmarshall(rawValue);
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
    private static <Clazz extends Serializable> String marshall(Clazz inData)
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
        BrokersStatus allStatus = getBrokersStatus();
        // x-axis is the number of instances total, across all hosts
        SortedSet<String> sortedClusterData = new TreeSet<>();
        // y-axis is the sessions across all hosts
        SortedSet<BrokerID> sortedBrokers = new TreeSet<>();
        Map<String,ClusteredBrokerStatus> statusByKey = new HashMap<>();
        for(BrokerStatus status : allStatus.getBrokers()) {
            ClusteredBrokerStatus cStatus = (ClusteredBrokerStatus)status;
            sortedClusterData.add(cStatus.getClusterData().toString());
            sortedBrokers.add(status.getId());
            statusByKey.put(status.getId().getValue()+"-"+cStatus.getClusterData(),
                            cStatus);
        }
        Table table = new Table(sortedClusterData.size()+1,
                                BorderStyle.CLASSIC_COMPATIBLE_WIDE,
                                ShownBorders.ALL,
                                false);
        table.addCell("Brokers",
                      cellStyle);
        for(String data : sortedClusterData) {
            table.addCell(data,
                          cellStyle);
        }
        SLF4JLoggerProxy.debug(this,
                               "Sorted data is {}, sorted sessions is {}, broker status is {}",
                               sortedClusterData,
                               sortedBrokers,
                               allStatus);
        for(BrokerID brokerId : sortedBrokers) {
            table.addCell(brokerId.getValue(),
                          cellStyle);
            for(String host : sortedClusterData) {
                // determine what the status is on this host for this broker
                String key = brokerId.getValue()+"-"+host;
                ClusteredBrokerStatus cBrokerStatus = statusByKey.get(key);
                if(cBrokerStatus == null) {
                    table.addCell("--",
                                  cellStyle);
                } else {
                    switch(cBrokerStatus.getStatus()) {
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
                            throw new UnsupportedOperationException("Cannot display status " + cBrokerStatus.getStatus());
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
     * holds broker status listener subscribers
     */
    private final Set<BrokerStatusListener> brokerStatusListeners = Sets.newConcurrentHashSet();
    /**
     * provides fix sessions
     */
    @Autowired
    private FixSessionProvider fixSessionProvider;
    /**
     * creates {@link Broker} objects
     */
    @Autowired
    private BrokerFactory brokerFactory;
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
    private final List<ClusteredBrokerStatus> clusterBrokerStatus = new ArrayList<>();
    /**
     * caches session names by session id
     */
    private final Cache<SessionID,String> sessionNamesBySessionId = CacheBuilder.newBuilder().expireAfterAccess(1, TimeUnit.MINUTES).build();
    /**
     * caches constructed broker objects
     */
    private final Cache<BrokerID,Broker> brokerCache = CacheBuilder.newBuilder().expireAfterAccess(10,TimeUnit.SECONDS).build();
    /**
     * Performs static initialization for this class
     * 
     * @throws RuntimeException if the initialization fails
     */
    static {
        try {
            context = JAXBContext.newInstance(ClusteredBrokerStatus.class);
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
