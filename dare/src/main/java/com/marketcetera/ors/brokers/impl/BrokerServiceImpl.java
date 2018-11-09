package com.marketcetera.ors.brokers.impl;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Queue;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Future;

import javax.annotation.PostConstruct;
import javax.annotation.concurrent.GuardedBy;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.joda.time.DateTime;
import org.marketcetera.client.brokers.BrokerStatus;
import org.marketcetera.client.brokers.BrokersStatus;
import org.marketcetera.cluster.AbstractCallableClusterTask;
import org.marketcetera.cluster.AbstractRunnableClusterTask;
import org.marketcetera.cluster.ClusterData;
import org.marketcetera.cluster.service.ClusterListener;
import org.marketcetera.cluster.service.ClusterMember;
import org.marketcetera.cluster.service.ClusterService;
import org.marketcetera.core.fix.FixSettingsProvider;
import org.marketcetera.core.fix.FixSettingsProviderFactory;
import org.marketcetera.persist.CollectionPageResponse;
import org.marketcetera.persist.PageRequest;
import org.marketcetera.persist.SortDirection;
import org.marketcetera.trade.BrokerID;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.quickfix.SpringSessionDescriptor;
import org.nocrala.tools.texttablefmt.BorderStyle;
import org.nocrala.tools.texttablefmt.CellStyle;
import org.nocrala.tools.texttablefmt.CellStyle.HorizontalAlign;
import org.nocrala.tools.texttablefmt.ShownBorders;
import org.nocrala.tools.texttablefmt.Table;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import quickfix.Acceptor;
import quickfix.ConfigError;
import quickfix.FieldConvertError;
import quickfix.Initiator;
import quickfix.Session;
import quickfix.SessionFactory;
import quickfix.SessionID;
import quickfix.SessionSettings;

import com.google.common.collect.Lists;
import com.hazelcast.core.Member;
import com.hazelcast.core.MemberAttributeEvent;
import com.hazelcast.core.MembershipEvent;
import com.hazelcast.core.MembershipListener;
import com.marketcetera.fix.AcceptorSessionAttributes;
import com.marketcetera.fix.ClusteredBrokerStatus;
import com.marketcetera.fix.FixSession;
import com.marketcetera.fix.FixSessionAttributeDescriptor;
import com.marketcetera.fix.FixSessionAttributeDescriptorFactory;
import com.marketcetera.fix.FixSessionFactory;
import com.marketcetera.fix.FixSessionListener;
import com.marketcetera.fix.FixSessionStatus;
import com.marketcetera.fix.SessionSchedule;
import com.marketcetera.fix.SessionService;
import com.marketcetera.fix.dao.FixSessionAttributeDescriptorDao;
import com.marketcetera.fix.dao.FixSessionDao;
import com.marketcetera.fix.dao.PersistentFixSession;
import com.marketcetera.fix.dao.PersistentFixSessionAttributeDescriptor;
import com.marketcetera.fix.dao.QPersistentFixSession;
import com.marketcetera.ors.brokers.Broker;
import com.marketcetera.ors.brokers.BrokerConstants;
import com.marketcetera.ors.brokers.BrokerService;
import com.marketcetera.ors.brokers.Brokers;
import com.marketcetera.ors.brokers.SessionCustomization;
import com.marketcetera.ors.brokers.SpringBroker;
import com.querydsl.core.BooleanBuilder;

/* $License$ */

/**
 * Provides broker services.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class BrokerServiceImpl
        implements BrokerService,MembershipListener,ClusterListener
{
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
     * @see com.marketcetera.fix.SessionService#generateSessionSettings(java.util.Collection)
     */
    @Override
    public SessionSettings generateSessionSettings(Collection<FixSession> inFixSessions)
    {
        return sessionService.generateSessionSettings(inFixSessions);
    }
    /* (non-Javadoc)
     * @see com.marketcetera.fix.SessionService#findFixSessionBySessionId(quickfix.SessionID)
     */
    @Override
    public FixSession findFixSessionBySessionId(SessionID inSessionId)
    {
        return sessionService.findFixSessionBySessionId(inSessionId);
    }
    /* (non-Javadoc)
     * @see com.marketcetera.fix.SessionService#isAffinityMatch(com.marketcetera.fix.FixSession, com.marketcetera.matp.cluster.ClusterData)
     */
    @Override
    public boolean isAffinityMatch(FixSession inFixSession,
                                   ClusterData inClusterData)
    {
        return sessionService.isAffinityMatch(inFixSession,
                                              inClusterData);
    }
    /* (non-Javadoc)
     * @see com.marketcetera.fix.SessionService#findFixSessions(boolean, int, int)
     */
    @Override
    public List<FixSession> findFixSessions(boolean inIsAcceptor,
                                            int inInstance,
                                            int inTotalInstances)
    {
        return sessionService.findFixSessions(inIsAcceptor,
                                              inInstance,
                                              inTotalInstances);
    }
    /* (non-Javadoc)
     * @see com.hazelcast.core.MembershipListener#memberAdded(com.hazelcast.core.MembershipEvent)
     */
    @Override
    public void memberAdded(MembershipEvent inArg0)
    {
        updateBrokerStatus();
    }
    /* (non-Javadoc)
     * @see com.hazelcast.core.MembershipListener#memberAttributeChanged(com.hazelcast.core.MemberAttributeEvent)
     */
    @Override
    public void memberAttributeChanged(MemberAttributeEvent inChangeEvent)
    {
        updateBrokerStatus();
    }
    /* (non-Javadoc)
     * @see com.hazelcast.core.MembershipListener#memberRemoved(com.hazelcast.core.MembershipEvent)
     */
    @Override
    public void memberRemoved(MembershipEvent inArg0)
    {
        updateBrokerStatus();
    }
    /* (non-Javadoc)
     * @see com.marketcetera.ors.brokers.BrokerService#reportBrokerStatus(org.marketcetera.client.brokers.BrokerStatus)
     */
    @Override
    public void reportBrokerStatus(BrokerStatus inBrokerStatus)
    {
        SLF4JLoggerProxy.trace(this,
                               "Reporting {}",
                               inBrokerStatus);
//        HazelcastClusterService hzClusterService = (HazelcastClusterService)clusterService;
//        try {
//            String xmlStatus = marshall(inBrokerStatus);
//            String key = brokerStatusPrefix+inBrokerStatus.getId()+inBrokerStatus.getHost();
//            hzClusterService.getInstance().getCluster().getLocalMember().setStringAttribute(key,
//                                                                                            xmlStatus);
//        } catch (JAXBException e) {
//            SLF4JLoggerProxy.warn(this,
//                                  e,
//                                  "Unable to update broker status");
//            return;
//        } catch (HazelcastInstanceNotActiveException | NullPointerException ignored) {
//            // these can happen on shutdown and can be safely ignored
//        } catch (Exception e) {
//            SLF4JLoggerProxy.warn(this,
//                                  e,
//                                  "Unable to update broker status");
//        }
        // TODO
        throw new UnsupportedOperationException();
    }
    /* (non-Javadoc)
     * @see com.marketcetera.ors.brokers.BrokerService#reportBrokerStatusFromAll(com.marketcetera.ors.brokers.FixSession, com.marketcetera.ors.brokers.ClusteredBrokerStatus.Status)
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
     * @see com.marketcetera.ors.brokers.BrokerService#getBrokerStatus(org.marketcetera.trade.BrokerID)
     */
    @Override
    public ClusteredBrokerStatus getBrokerStatus(BrokerID inBrokerId)
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
     * @see com.marketcetera.ors.brokers.BrokerService#getBrokersStatus()
     */
    @Override
    public BrokersStatus getBrokersStatus()
    {
        synchronized(clusterBrokerStatus) {
            List<BrokerStatus> statuses = new ArrayList<>();
            for(ClusteredBrokerStatus status : clusterBrokerStatus) {
                statuses.add(status);
            }
            return new BrokersStatus(statuses);
        }
    }
    /* (non-Javadoc)
     * @see com.marketcetera.ors.brokers.BrokerService#findFixSessionById(long)
     */
    @Override
    @Transactional(readOnly=true,propagation=Propagation.REQUIRED)
    public FixSession findFixSessionById(long inId)
    {
        BooleanBuilder where = new BooleanBuilder();
        where = where.and(QPersistentFixSession.persistentFixSession.isDeleted.isFalse());
        where = where.and(QPersistentFixSession.persistentFixSession.id.eq(inId));
        FixSession session = fixSessionDao.findOne(where);
        if(session != null) {
            sessionNamesBySessionId.put(new SessionID(session.getSessionId()),
                                        session.getName());
        }
        return session;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.ors.brokers.BrokerService#findFixSessionByBrokerId(org.marketcetera.trade.BrokerID)
     */
    @Override
    @Transactional(readOnly=true,propagation=Propagation.REQUIRED)
    public FixSession findFixSessionByBrokerId(BrokerID inBrokerId)
    {
        FixSession session = fixSessionDao.findByBrokerIdAndIsDeletedFalse(inBrokerId.getValue());
        if(session != null) {
            sessionNamesBySessionId.put(new SessionID(session.getSessionId()),
                                        session.getName());
        }
        return session;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.ors.brokers.BrokerService#getFixSessionByName(java.lang.String)
     */
    @Override
    @Transactional(readOnly=true,propagation=Propagation.REQUIRED)
    public FixSession findFixSessionByName(String inFixSessionName)
    {
        FixSession session = fixSessionDao.findByNameAndIsDeletedFalse(inFixSessionName);
        if(session != null) {
            sessionNamesBySessionId.put(new SessionID(session.getSessionId()),
                                        session.getName());
        }
        return session;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.ors.brokers.BrokerService#findFixSessions()
     */
    @Override
    @Transactional(readOnly=true,propagation=Propagation.REQUIRED)
    public List<FixSession> findFixSessions()
    {
        List<FixSession> sessionsToReturn = new ArrayList<>();
        BooleanBuilder where = new BooleanBuilder();
        where = where.and(QPersistentFixSession.persistentFixSession.isDeleted.isFalse());
        Iterable<PersistentFixSession> allSessionsByConnectionType = fixSessionDao.findAll(where);
        for(PersistentFixSession session : allSessionsByConnectionType) {
            sessionNamesBySessionId.put(new SessionID(session.getSessionId()),
                                        session.getName());
            sessionsToReturn.add(session);
        }
        return sessionsToReturn;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.ors.brokers.BrokerService#findFixSessions(org.marketcetera.core.PageRequest)
     */
    @Override
    @Transactional(readOnly=true,propagation=Propagation.REQUIRED)
    public CollectionPageResponse<FixSession> findFixSessions(PageRequest inPageRequest)
    {
        List<FixSession> sessions = new ArrayList<>();
        BooleanBuilder where = new BooleanBuilder();
        where = where.and(QPersistentFixSession.persistentFixSession.isDeleted.isFalse());
        Sort jpaSort = null;
        if(inPageRequest.getSortOrder() == null || inPageRequest.getSortOrder().isEmpty()) {
            jpaSort = new Sort(new Sort.Order(Sort.Direction.ASC,
                                              QPersistentFixSession.persistentFixSession.name.getMetadata().getName()));
        } else {
            for(org.marketcetera.persist.Sort sort : inPageRequest.getSortOrder()) {
                Sort.Direction jpaSortDirection = sort.getDirection()==SortDirection.ASCENDING?Sort.Direction.ASC:Sort.Direction.DESC;
                String property = getFixSessionPropertyFor(sort);
                if(jpaSort == null) {
                    jpaSort = new Sort(new Sort.Order(jpaSortDirection,
                                                      property));
                } else {
                    jpaSort = jpaSort.and(new Sort(new Sort.Order(jpaSortDirection,
                                                                  property)));
                }
            }
        }
        SLF4JLoggerProxy.trace(this,
                               "Applying page sort: {}",
                               jpaSort);
        org.springframework.data.domain.PageRequest pageRequest = new org.springframework.data.domain.PageRequest(inPageRequest.getPageNumber(),
                                                                                                                  inPageRequest.getPageSize(),
                                                                                                                  jpaSort);
        Page<PersistentFixSession> result = fixSessionDao.findAll(where,
                                                                  pageRequest);
        CollectionPageResponse<FixSession> response = new CollectionPageResponse<>();
        response.setPageMaxSize(result.getSize());
        response.setPageNumber(result.getNumber());
        response.setPageSize(result.getNumberOfElements());
        response.setTotalPages(result.getTotalPages());
        response.setTotalSize(result.getTotalElements());
        for(PersistentFixSession session : result.getContent()) {
            sessions.add(session);
        }
        response.setSortOrder(inPageRequest.getSortOrder());
        response.setElements(sessions);
        return response;
    }
    /**
     *
     *
     * @param inSort
     * @return
     */
    private String getFixSessionPropertyFor(org.marketcetera.persist.Sort inSort)
    {
        String lcaseProperty = inSort.getProperty().toLowerCase();
        if(QPersistentFixSession.persistentFixSession.name.getMetadata().getName().toLowerCase().equals(lcaseProperty)) {
            return QPersistentFixSession.persistentFixSession.name.getMetadata().getName();
        } else if(QPersistentFixSession.persistentFixSession.sessionId.getMetadata().getName().toLowerCase().equals(lcaseProperty)) {
            return QPersistentFixSession.persistentFixSession.sessionId.getMetadata().getName();
        } else {
            throw new UnsupportedOperationException("Unsupported sort property for FIX Sessions: " + inSort.getProperty());
        }
    }
    /* (non-Javadoc)
     * @see com.marketcetera.ors.brokers.BrokerService#getFixSettingsFor(int)
     */
    @Override
    public AcceptorSessionAttributes getFixSettingsFor(int inAffinity)
    {
        try {
            GetSessionAttributesTask stopSessionTask = new GetSessionAttributesTask(inAffinity);
            Map<Object,Future<AcceptorSessionAttributes>> results = clusterService.execute(stopSessionTask);
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
     * @see com.marketcetera.ors.brokers.BrokerService#delete(quickfix.SessionID)
     */
    @Override
    @Transactional(readOnly=false,propagation=Propagation.REQUIRED)
    public void delete(SessionID inSessionId)
    {
        SLF4JLoggerProxy.debug(this,
                               "Deleting {}",
                               inSessionId);
        PersistentFixSession existingSession = fixSessionDao.findBySessionIdAndIsDeletedFalse(inSessionId.toString());
        Validate.notNull(existingSession,
                         "Cannot delete nonexistent session " + inSessionId);
        Validate.isTrue(!existingSession.isEnabled(),
                        "Cannot delete enabled session " + inSessionId);
        existingSession.delete();
        existingSession = fixSessionDao.save(existingSession);
        sessionNamesBySessionId.remove(new SessionID(existingSession.getSessionId()));
        ReportBrokerStatusTask reportStatusTask = new ReportBrokerStatusTask(existingSession,
                                                                             FixSessionStatus.DELETED);
        clusterService.execute(reportStatusTask);
    }
    /* (non-Javadoc)
     * @see com.marketcetera.ors.brokers.BrokerService#stopSession(quickfix.SessionID)
     */
    @Override
    public void stopSession(SessionID inSessionId)
    {
        SLF4JLoggerProxy.debug(this,
                               "Stopping {}",
                               inSessionId);
        FixSession stoppedSession = fixSessionDao.findBySessionIdAndIsDeletedFalse(inSessionId.toString());
        if(stoppedSession == null) {
            // somebody went and deleted this session, nothing to do, just slink away quietly
            SLF4JLoggerProxy.debug(this,
                                   "Not calling stop task for deleted session {}",
                                   inSessionId);
            return;
        }
        sessionNamesBySessionId.put(new SessionID(stoppedSession.getSessionId()),
                                    stoppedSession.getName());
        SLF4JLoggerProxy.debug(this,
                               "Calling stop task for {}",
                               stoppedSession);
        try {
            StopSessionTask stopSessionTask = new StopSessionTask(stoppedSession);
            Map<Object,Future<Boolean>> results = clusterService.execute(stopSessionTask);
            for(Map.Entry<Object,Future<Boolean>> entry : results.entrySet()) {
                entry.getValue().get();
            }
            SLF4JLoggerProxy.debug(this,
                                   "Stop {} task completed successfully",
                                   stoppedSession);
        } catch (Exception e) {
            SLF4JLoggerProxy.warn(this,
                                  e,
                                  "Session {} failed to notify all listeners",
                                  stoppedSession);
            if(e instanceof RuntimeException) {
                throw (RuntimeException)e;
            }
            throw new RuntimeException(e);
        }
    }
    /* (non-Javadoc)
     * @see com.marketcetera.ors.brokers.BrokerService#startSession(quickfix.SessionID)
     */
    @Override
    public void startSession(SessionID inSessionId)
    {
        SLF4JLoggerProxy.debug(this,
                               "Starting {}",
                               inSessionId);
        FixSession startedSession = fixSessionDao.findBySessionIdAndIsDeletedFalse(inSessionId.toString());
        if(startedSession == null) {
            // somebody went and deleted this session, nothing to do, just slink away quietly
            SLF4JLoggerProxy.debug(this,
                                   "Not calling start task for deleted session {}",
                                   inSessionId);
            return;
        }
        sessionNamesBySessionId.put(new SessionID(startedSession.getSessionId()),
                                    startedSession.getName());
        SLF4JLoggerProxy.debug(this,
                               "Calling start task for {}",
                               startedSession);
        try {
            StartSessionTask startSessionTask = new StartSessionTask(startedSession);
            Map<Object,Future<Boolean>> results = clusterService.execute(startSessionTask);
            for(Map.Entry<Object,Future<Boolean>> entry : results.entrySet()) {
                entry.getValue().get();
            }
            SLF4JLoggerProxy.debug(this,
                                   "Start {} task completed successfully",
                                   startedSession);
        } catch (Exception e) {
            SLF4JLoggerProxy.warn(this,
                                  e,
                                  "Session {} failed to notify all listeners",
                                  startedSession);
            if(e instanceof RuntimeException) {
                throw (RuntimeException)e;
            }
            throw new RuntimeException(e);
        }
    }
    /* (non-Javadoc)
     * @see com.marketcetera.ors.brokers.BrokerService#disableSession(quickfix.SessionID)
     */
    @Override
    public void disableSession(SessionID inSessionId)
    {
        SLF4JLoggerProxy.debug(this,
                               "Disabling {}",
                               inSessionId);
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("disableSessionTransaction");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setReadOnly(false);
        TransactionStatus status = txManager.getTransaction(def);
        PersistentFixSession disabledSession;
        try {
            disabledSession = fixSessionDao.findBySessionIdAndIsDeletedFalse(inSessionId.toString());
            Validate.notNull(disabledSession,
                             "Cannot disable nonexistent session " + inSessionId);
            sessionNamesBySessionId.put(new SessionID(disabledSession.getSessionId()),
                                        disabledSession.getName());
            if(!disabledSession.isEnabled()) {
                SLF4JLoggerProxy.debug(this,
                                       "Session {} is already disabled, nothing to do",
                                       inSessionId);
                txManager.rollback(status);
                return;
            }
            disabledSession.setIsEnabled(false);
            disabledSession = fixSessionDao.save(disabledSession);
            txManager.commit(status);
        } catch (Exception e) {
            // unable to commit the initial change, rollback
            if(status != null) {
                txManager.rollback(status);
            }
            if(e instanceof RuntimeException) {
                throw (RuntimeException)e;
            }
            throw new RuntimeException(e);
        }
        // at this point, we know the database transaction has been committed and the save was successful. start a new transaction and refresh the object.
        // this transaction is for the session-disable task. if it fails, mark the session as disabled and commit
        status = txManager.getTransaction(def);
        try {
            disabledSession = fixSessionDao.findBySessionIdAndIsDeletedFalse(inSessionId.toString());
            if(disabledSession == null) {
                // somebody went and deleted this session, nothing to do, just slink away quietly
                SLF4JLoggerProxy.debug(this,
                                       "Not calling disable task for deleted session {}",
                                       inSessionId);
                txManager.rollback(status);
                return;
            }
            if(disabledSession.isEnabled()) {
                // somebody enabled this session while we were working on it, still, nothing to do
                SLF4JLoggerProxy.debug(this,
                                       "Not calling disable task for enabled session {}",
                                       inSessionId);
                txManager.rollback(status);
                return;
            }
            SLF4JLoggerProxy.debug(this,
                                   "Calling disable task for {}",
                                   disabledSession);
             DisableSessionTask disableSessionTask = new DisableSessionTask(disabledSession);
             Map<Object,Future<Boolean>> results = clusterService.execute(disableSessionTask);
             for(Map.Entry<Object,Future<Boolean>> entry : results.entrySet()) {
                 entry.getValue().get();
             }
             SLF4JLoggerProxy.debug(this,
                                    "Disable {} task completed successfully",
                                    disabledSession);
        } catch(Exception e) {
            SLF4JLoggerProxy.warn(this,
                                  e,
                                  "Session {} failed to notify all listeners",
                                  disabledSession);
            // TODO need status update? not sure how to report this one...
            disabledSession.setIsEnabled(true);
            disabledSession = fixSessionDao.save(disabledSession);
            if(e instanceof RuntimeException) {
                throw (RuntimeException)e;
            }
            throw new RuntimeException(e);
        } finally {
            txManager.commit(status);
        }
    }
    /* (non-Javadoc)
     * @see com.marketcetera.ors.brokers.BrokerService#enableSession(quickfix.SessionID)
     */
    @Override
    public void enableSession(SessionID inSessionId)
    {
        SLF4JLoggerProxy.debug(this,
                               "Enabling {}",
                               inSessionId);
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("enableSessionTransaction");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setReadOnly(false);
        TransactionStatus status = txManager.getTransaction(def);
        PersistentFixSession enabledSession;
        try {
            enabledSession = fixSessionDao.findBySessionIdAndIsDeletedFalse(inSessionId.toString());
            Validate.notNull(enabledSession,
                             "Cannot enable nonexistent session " + inSessionId);
            sessionNamesBySessionId.put(new SessionID(enabledSession.getSessionId()),
                                        enabledSession.getName());
            if(enabledSession.isEnabled()) {
                SLF4JLoggerProxy.debug(this,
                                       "Session {} is already enabled, nothing to do",
                                       inSessionId);
                txManager.rollback(status);
                return;
            }
            enabledSession.setIsEnabled(true);
            enabledSession = fixSessionDao.save(enabledSession);
            txManager.commit(status);
        } catch (Exception e) {
            // unable to commit the initial change, rollback
            if(status != null) {
                txManager.rollback(status);
            }
            if(e instanceof RuntimeException) {
                throw (RuntimeException)e;
            }
            throw new RuntimeException(e);
        }
        // at this point, we know the database transaction has been committed and the save was successful. start a new transaction and refresh the object.
        // this transaction is for the session-enable task. if it fails, mark the session as disabled and commit
        status = txManager.getTransaction(def);
        try {
            enabledSession = fixSessionDao.findBySessionIdAndIsDeletedFalse(inSessionId.toString());
            if(enabledSession == null) {
                // somebody went and deleted this session, nothing to do, just slink away quietly
                SLF4JLoggerProxy.debug(this,
                                       "Not calling enable task for deleted session {}",
                                       inSessionId);
                txManager.rollback(status);
                return;
            }
            if(!enabledSession.isEnabled()) {
                // somebody disabled this session while we were working on it, still, nothing to do
                SLF4JLoggerProxy.debug(this,
                                       "Not calling enable task for disabled session {}",
                                       inSessionId);
                txManager.rollback(status);
                return;
            }
            // proceed with the enable call. if it fails, disable the session
            SLF4JLoggerProxy.debug(this,
                                   "Calling enable task for {}",
                                   enabledSession);
             EnableSessionTask enableSessionTask = new EnableSessionTask(enabledSession);
             Map<Object,Future<Boolean>> results = clusterService.execute(enableSessionTask);
             // if any of the cluster members throws an exception, the session will not be enabled!
             for(Map.Entry<Object,Future<Boolean>> entry : results.entrySet()) {
                 entry.getValue().get();
                 SLF4JLoggerProxy.debug(this,
                                        "Enable {} task complete on {}",
                                        enabledSession,
                                        entry.getKey());
             }
             SLF4JLoggerProxy.debug(this,
                                    "Enable {} task completed successfully",
                                    enabledSession);
        } catch(Exception e) {
            SLF4JLoggerProxy.warn(this,
                                  e,
                                  "Unable to enable {}",
                                  enabledSession);
            reportBrokerStatusFromAll(enabledSession,
                                      FixSessionStatus.DISABLED);
            enabledSession.setIsEnabled(false);
            enabledSession = fixSessionDao.save(enabledSession);
            if(e instanceof RuntimeException) {
                throw (RuntimeException)e;
            }
            throw new RuntimeException(e);
        } finally {
            txManager.commit(status);
        }
    }
    /* (non-Javadoc)
     * @see com.marketcetera.ors.brokers.BrokerService#save(com.marketcetera.ors.brokers.FixSession)
     */
    @Override
    @Transactional(readOnly=false,propagation=Propagation.REQUIRED)
    public FixSession save(FixSession inFixSession)
    {
        SLF4JLoggerProxy.debug(this,
                               "Saving {}",
                               inFixSession);
        sessionNamesBySessionId.remove(new SessionID(inFixSession.getSessionId()));
        BooleanBuilder where = new BooleanBuilder();
        where = where.and(QPersistentFixSession.persistentFixSession.isDeleted.isFalse());
        where = where.and(QPersistentFixSession.persistentFixSession.id.eq(inFixSession.getId()));
        PersistentFixSession existingSession = fixSessionDao.findOne(where);
        if(existingSession == null) {
            // these checks need to be done manually instead of relying on database integrity because of the "deleted" feature for sessions
            // check for duplicate name
            Validate.isTrue(findFixSessionByName(inFixSession.getName()) == null,
                            "Session with name \"" + inFixSession.getName() + "\" already exists");
            // check for duplicate session id
            Validate.isTrue(findFixSessionBySessionId(new SessionID(inFixSession.getSessionId())) == null,
                            "Session with session ID \"" + inFixSession.getSessionId() + "\" already exists");
            // check for duplicate broker id
            Validate.isTrue(findFixSessionByBrokerId(new BrokerID(inFixSession.getBrokerId())) == null,
                            "Session with broker ID \"" + inFixSession.getBrokerId() + "\" already exists");
            existingSession = (PersistentFixSession)fixSessionFactory.create(inFixSession);
        } else {
            // in order to change the session, it must be disabled
            Validate.isTrue(!existingSession.isEnabled(),
                            "Session " + existingSession.getSessionId() + " must be disabled before it can be modified");
            existingSession.update(inFixSession);
        }
        existingSession.validate();
        // do not allow the session to enabled via the backdoor
        existingSession.setIsEnabled(false);
        existingSession = fixSessionDao.save(existingSession);
        sessionNamesBySessionId.put(new SessionID(existingSession.getSessionId()),
                                    existingSession.getName());
        // have each cluster member report disabled status for this session
        ReportBrokerStatusTask reportStatusTask = new ReportBrokerStatusTask(existingSession,
                                                                             FixSessionStatus.DISABLED);
        clusterService.execute(reportStatusTask);
        return existingSession;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.ors.brokers.BrokerService#getFixSessionAttributeDescriptors()
     */
    @Override
    @Transactional(readOnly=true,propagation=Propagation.REQUIRED)
    public Collection<FixSessionAttributeDescriptor> getFixSessionAttributeDescriptors()
    {
        Collection<PersistentFixSessionAttributeDescriptor> allDescriptors = fixSessionAttributeDescriptorDao.findAll();
        Comparator<FixSessionAttributeDescriptor> nameComparator = new Comparator<FixSessionAttributeDescriptor>() {
            @Override
            public int compare(FixSessionAttributeDescriptor inO1,
                               FixSessionAttributeDescriptor inO2)
            {
                return inO1.getName().compareTo(inO2.getName());
            }
        };
        Collection<FixSessionAttributeDescriptor> descriptorsToReturn = new TreeSet<FixSessionAttributeDescriptor>(nameComparator);
        for(PersistentFixSessionAttributeDescriptor descriptor : allDescriptors) {
            descriptorsToReturn.add(descriptor);
        }
        return descriptorsToReturn;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.ors.brokers.BrokerService#save(com.marketcetera.ors.brokers.FixSessionAttributeDescriptor)
     */
    @Override
    @Transactional(readOnly=false,propagation=Propagation.REQUIRED)
    public FixSessionAttributeDescriptor save(FixSessionAttributeDescriptor inFixSessionAttributeDescriptor)
    {
        PersistentFixSessionAttributeDescriptor pFixSessionAttributeDescriptor;
        if(inFixSessionAttributeDescriptor instanceof PersistentFixSessionAttributeDescriptor) {
            pFixSessionAttributeDescriptor = (PersistentFixSessionAttributeDescriptor)inFixSessionAttributeDescriptor;
        } else {
            pFixSessionAttributeDescriptor = (PersistentFixSessionAttributeDescriptor)fixSessionAttributeDescriptorFactory.create(inFixSessionAttributeDescriptor);
        }
        return fixSessionAttributeDescriptorDao.save(pFixSessionAttributeDescriptor);
    }
    /* (non-Javadoc)
     * @see com.marketcetera.ors.brokers.BrokerService#getActualSessionStart(quickfix.SessionID)
     */
    @Override
    public Date getActualSessionStart(SessionID inSessionId)
    {
        try {
            GetSessionStartTask stopSessionTask = new GetSessionStartTask(inSessionId);
            Map<Object,Future<Date>> results = clusterService.execute(stopSessionTask);
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
     * @see com.marketcetera.ors.brokers.BrokerService#getNextSessionStart(quickfix.SessionID)
     */
    @Override
    public Date getNextSessionStart(SessionID inSessionId)
    {
        FixSession fixSession = findFixSessionBySessionId(inSessionId);
        Date returnValue = new DateTime().withTimeAtStartOfDay().plusDays(1).toDate();
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
     * @see com.marketcetera.ors.brokers.BrokerService#getSessionStart(quickfix.SessionID)
     */
    @Override
    @Transactional(readOnly=true,propagation=Propagation.REQUIRED)
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
     * @see com.marketcetera.ors.brokers.BrokerService#addFixSessionListener(com.marketcetera.ors.brokers.FixSessionListener)
     */
    @Override
    public void addFixSessionListener(FixSessionListener inFixSessionListener)
    {
        fixSessionListeners.add(inFixSessionListener);
    }
    /* (non-Javadoc)
     * @see com.marketcetera.ors.brokers.BrokerService#removeFixSessionListener(com.marketcetera.ors.brokers.FixSessionListener)
     */
    @Override
    public void removeFixSessionListener(FixSessionListener inFixSessionListener)
    {
        fixSessionListeners.remove(inFixSessionListener);
    }
    /* (non-Javadoc)
     * @see com.marketcetera.ors.brokers.BrokerService#getFixSessionListeners()
     */
    @Override
    public Collection<FixSessionListener> getFixSessionListeners()
    {
        return fixSessionListeners;
    }
    /**
     * Validates and starts the object.
     */
    @PostConstruct
    public void start()
    {
        Validate.notNull(clusterService);
        Validate.notNull(fixSessionDao);
        Validate.notNull(fixSessionAttributeDescriptorDao);
        clusterService.addClusterListener(this);
        sessionCustomizationsByName.clear();
        if(sessionCustomizations != null) {
            for(SessionCustomization sessionCustomization : sessionCustomizations) {
                if(sessionCustomizationsByName.containsKey(sessionCustomization.getName())) {
                    SLF4JLoggerProxy.info(this,
                                          "More than one session customization exists with the name {} - one will replace the other, possibly yielding unexpected results",
                                          sessionCustomization.getName());
                }
                sessionCustomizationsByName.put(sessionCustomization.getName(),
                                                sessionCustomization);
            }
        }
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
     * Get the fixSessionDao value.
     *
     * @return a <code>FixSessionDao</code> value
     */
    public FixSessionDao getFixSessionDao()
    {
        return fixSessionDao;
    }
    /**
     * Sets the fixSessionDao value.
     *
     * @param inFixSessionDao a <code>FixSessionDao</code> value
     */
    public void setFixSessionDao(FixSessionDao inFixSessionDao)
    {
        fixSessionDao = inFixSessionDao;
    }
    /**
     * Get the fixSessionFactory value.
     *
     * @return a <code>FixSessionFactory</code> value
     */
    public FixSessionFactory getFixSessionFactory()
    {
        return fixSessionFactory;
    }
    /**
     * Sets the fixSessionFactory value.
     *
     * @param inFixSessionFactory a <code>FixSessionFactory</code> value
     */
    public void setFixSessionFactory(FixSessionFactory inFixSessionFactory)
    {
        fixSessionFactory = inFixSessionFactory;
    }
    /**
     * Get the fixSessionAttributeDescriptorDao value.
     *
     * @return a <code>FixSessionAttributeDescriptorDao</code> value
     */
    public FixSessionAttributeDescriptorDao getFixSessionAttributeDescriptorDao()
    {
        return fixSessionAttributeDescriptorDao;
    }
    /**
     * Sets the fixSessionAttributeDescriptorDao value.
     *
     * @param inFixSessionAttributeDescriptorDao a <code>FixSessionAttributeDescriptorDao</code> value
     */
    public void setFixSessionAttributeDescriptorDao(FixSessionAttributeDescriptorDao inFixSessionAttributeDescriptorDao)
    {
        fixSessionAttributeDescriptorDao = inFixSessionAttributeDescriptorDao;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.ors.brokers.BrokerService#generateBrokerStatus(com.marketcetera.ors.brokers.FixSession, com.marketcetera.matp.cluster.ClusterData, com.marketcetera.ors.brokers.ClusteredBrokerStatus.Status, boolean)
     */
    @Override
    public ClusteredBrokerStatus generateBrokerStatus(FixSession inFixSession,
                                                      ClusterData inClusterData,
                                                      FixSessionStatus inStatus,
                                                      boolean inIsLoggedOn)
    {
        Broker broker = generateBroker(inFixSession);
        ClusteredBrokerStatus status = new ClusteredBrokerStatus(inFixSession,
                                                                 inClusterData,
                                                                 inStatus,
                                                                 inIsLoggedOn);
        if(broker.getSpringBroker() != null && broker.getSpringBroker().getBrokerAlgoSpecs() != null) {
            status.getBrokerAlgos().addAll(broker.getSpringBroker().getBrokerAlgoSpecs());
        }
        return status;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.ors.brokers.BrokerService#getBroker(com.marketcetera.ors.brokers.FixSession)
     */
    @Override
    public Broker generateBroker(FixSession inFixSession)
    {
        SpringSessionDescriptor springSessionDescriptor = new SpringSessionDescriptor();
        Map<String,String> dictionary = new HashMap<>();
        dictionary.putAll(inFixSession.getSessionSettings());
        if(inFixSession.isAcceptor()) {
            dictionary.put(SessionFactory.SETTING_CONNECTION_TYPE,
                           SessionFactory.ACCEPTOR_CONNECTION_TYPE);
            dictionary.put(Acceptor.SETTING_SOCKET_ACCEPT_ADDRESS,
                           inFixSession.getHost());
            dictionary.put(Acceptor.SETTING_SOCKET_ACCEPT_PORT,
                           String.valueOf(inFixSession.getPort()));
        } else {
            dictionary.put(SessionFactory.SETTING_CONNECTION_TYPE,
                           SessionFactory.INITIATOR_CONNECTION_TYPE);
            dictionary.put(Initiator.SETTING_SOCKET_CONNECT_HOST,
                           inFixSession.getHost());
            dictionary.put(Initiator.SETTING_SOCKET_CONNECT_PORT,
                           String.valueOf(inFixSession.getPort()));
        }
        SessionID sessionId = new SessionID(inFixSession.getSessionId());
        dictionary.put(SessionSettings.BEGINSTRING,
                       sessionId.getBeginString());
        dictionary.put(SessionSettings.SENDERCOMPID,
                       sessionId.getSenderCompID());
        dictionary.put(SessionSettings.TARGETCOMPID,
                       sessionId.getTargetCompID());
        springSessionDescriptor.setDictionary(dictionary);
        SpringBroker springBroker = new SpringBroker();
        springBroker.setDescriptor(springSessionDescriptor);
        springBroker.setId(inFixSession.getBrokerId());
        springBroker.setInstanceAffinity(inFixSession.getAffinity());
        springBroker.setName(inFixSession.getName());
        String customizationName = StringUtils.trimToNull(inFixSession.getSessionSettings().get(sessionCustomizationKey));
        if(customizationName == null) {
            SLF4JLoggerProxy.debug(this,
                                   "No session customization defined for {}",
                                   inFixSession.getBrokerId());
        } else {
            SessionCustomization sessionCustomization = sessionCustomizationsByName.get(customizationName);
            if(sessionCustomization == null) {
                SLF4JLoggerProxy.debug(this,
                                       "Session {} specifies a session customization by name {}, but no customization exists by that name",
                                       inFixSession.getBrokerId(),
                                       customizationName);
            } else {
                SLF4JLoggerProxy.debug(this,
                                       "Using session customization {} for {}",
                                       sessionCustomization,
                                       inFixSession.getBrokerId());
              springBroker.setLogonActions(sessionCustomization.getLogonActions());
              springBroker.setLogoutActions(sessionCustomization.getLogoutActions());
              springBroker.setModifiers(sessionCustomization.getMessageModifiers());
              springBroker.setPreSendModifiers(sessionCustomization.getPreSendModifiers());
              springBroker.setResponseModifiers(sessionCustomization.getResponseModifiers());
              springBroker.setRoutes(sessionCustomization.getRoutes());
              springBroker.setUserBlacklist(sessionCustomization.getUserBlacklist());
              springBroker.setUserWhitelist(sessionCustomization.getUserWhitelist());
              springBroker.setBrokerAlgoSpecs(sessionCustomization.getBrokerAlgos());
            }
        }
        Broker broker = new Broker(springBroker);
        ClusteredBrokerStatus brokerStatus = getBrokerStatus(broker.getBrokerID()); 
        broker.setLoggedOn(brokerStatus == null ? false : brokerStatus.getLoggedOn());
        return broker;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.ors.brokers.BrokerService#getBroker(org.marketcetera.trade.BrokerID)
     */
    @Override
    @Transactional(readOnly=true,propagation=Propagation.REQUIRED)
    public Broker getBroker(BrokerID inBrokerId)
    {
        // TODO efficiency!
        FixSession session = fixSessionDao.findByBrokerIdAndIsDeletedFalse(inBrokerId.getValue());
        if(session == null) {
            return null;
        }
        Broker broker = generateBroker(session);
        return broker;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.ors.brokers.BrokerService#getBroker(quickfix.SessionID)
     */
    @Override
    @Transactional(readOnly=true,propagation=Propagation.REQUIRED)
    public Broker getBroker(SessionID inSessionId)
    {
        // TODO efficiency!
        FixSession session = fixSessionDao.findBySessionIdAndIsDeletedFalse(inSessionId.toString());
        if(session == null) {
            return null;
        }
        Broker broker = generateBroker(session);
        return broker;
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
     * Updates all broker status
     */
    private void updateBrokerStatus()
    {
        synchronized(clusterBrokerStatus) {
//            HazelcastClusterService hzClusterService = (HazelcastClusterService)clusterService;
//            List<ClusteredBrokerStatus> updatedStatus = new ArrayList<>();
//            for(Member member : hzClusterService.getInstance().getCluster().getMembers()) {
//                for(Map.Entry<String,Object> attributeEntry : member.getAttributes().entrySet()) {
//                    String key = attributeEntry.getKey();
//                    if(key.startsWith(brokerStatusPrefix)) {
//                        String rawValue = String.valueOf(attributeEntry.getValue());
//                        ClusteredBrokerStatus status;
//                        try {
//                            status = (ClusteredBrokerStatus)unmarshall(rawValue);
//                            updatedStatus.add(status);
//                        } catch (Exception e) {
//                            SLF4JLoggerProxy.warn(this,
//                                                  e,
//                                                  "Unable to update broker status");
//                            return;
//                        }
//                    }
//                }
//            }
//            clusterBrokerStatus.clear();
//            clusterBrokerStatus.addAll(updatedStatus);
//            logBrokerInstanceData();
        }
        // TODO
        throw new UnsupportedOperationException();
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
            SLF4JLoggerProxy.info(brokerStatusCategory,
                                  "{}{}",
                                  System.lineSeparator(),
                                  thisBrokerLog);
        }
        lastBrokerLog = thisBrokerLog;
    }
    /**
     * Collect session attributes from the appropriate cluster member.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    private static class GetSessionAttributesTask
            extends AbstractCallableClusterTask<AcceptorSessionAttributes>
    {
        /* (non-Javadoc)
         * @see java.util.concurrent.Callable#call()
         */
        @Override
        public AcceptorSessionAttributes call()
                throws Exception
        {
            ClusterData clusterData = getClusterService().getInstanceData();
            AcceptorSessionAttributes attributes = null;
            if(isAffinityMatch(clusterData,
                               affinity)) {
                FixSettingsProvider settingsProvider = fixSettingsProviderFactory.create();
                attributes = new AcceptorSessionAttributes();
                attributes.setAffinity(affinity);
                attributes.setHost(settingsProvider.getAcceptorHost());
                attributes.setPort(settingsProvider.getAcceptorPort());
                SLF4JLoggerProxy.debug(BrokerServiceImpl.class,
                                       "{} is an affinity match for {}, returning {}",
                                       clusterData,
                                       affinity,
                                       attributes);
            } else {
                SLF4JLoggerProxy.debug(BrokerServiceImpl.class,
                                       "{} is not an affinity match for {}, returning {}",
                                       clusterData,
                                       affinity,
                                       attributes);
            }
            return attributes;
        }
        /**
         * Create a new StopSessionTask instance.
         *
         * @param inAffinity a <code>FixSession</code> value
         */
        private GetSessionAttributesTask(int inAffinity)
        {
            affinity = inAffinity;
        }
        /**
         * fix settings provider factory value
         */
        @Autowired
        private transient FixSettingsProviderFactory fixSettingsProviderFactory;
        /**
         * affinity to be checked
         */
        private int affinity;
        private static final long serialVersionUID = 5032644750164495565L;
    }
    /**
     * Gets the session start from a given session.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    private static class GetSessionStartTask
            extends AbstractCallableClusterTask<Date>
    {
        /* (non-Javadoc)
         * @see java.util.concurrent.Callable#call()
         */
        @Override
        public Date call()
                throws Exception
        {
            SLF4JLoggerProxy.debug(BrokerServiceImpl.class,
                                   "Calling get session start for {} on {}",
                                   sessionId,
                                   getClusterService().getInstanceData());
            Session session = Session.lookupSession(sessionId);
            if(session == null) {
                return null;
            }
            return session.getStartTime();
        }
        /**
         * Create a new GetSessionStartTask instance.
         *
         * @param inSessionId a <code>SessionID</code> value
         */
        private GetSessionStartTask(SessionID inSessionId)
        {
            sessionId = inSessionId;
        }
        /**
         * session id value
         */
        private SessionID sessionId;
        private static final long serialVersionUID = 7182143924518821961L;
    }
    /**
     * Indicates to each cluster member that a particular session has been stopped.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    private static class StopSessionTask
            extends AbstractCallableClusterTask<Boolean>
    {
        /* (non-Javadoc)
         * @see java.util.concurrent.Callable#call()
         */
        @Override
        public Boolean call()
                throws Exception
        {
            SLF4JLoggerProxy.debug(BrokerServiceImpl.class,
                                   "Calling stop for {} on {}",
                                   session,
                                   getClusterService().getInstanceData());
            for(FixSessionListener fixSessionListener : brokerService.getFixSessionListeners()) {
                fixSessionListener.sessionStopped(session);
            }
            return true;
        }
        /**
         * Create a new StopSessionTask instance.
         *
         * @param inSession a <code>FixSession</code> value
         */
        private StopSessionTask(FixSession inSession)
        {
            // remember - this session is NOT attached
            session = inSession;
        }
        /**
         * cluster-local broker service value
         */
        @Autowired
        private transient BrokerService brokerService;
        /**
         * fix session to be disabled
         */
        private FixSession session;
        private static final long serialVersionUID = 5032644750164495565L;
    }
    /**
     * Indicates to each cluster member that a particular session has been started.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    private static class StartSessionTask
            extends AbstractCallableClusterTask<Boolean>
    {
        /* (non-Javadoc)
         * @see java.util.concurrent.Callable#call()
         */
        @Override
        public Boolean call()
                throws Exception
        {
            SLF4JLoggerProxy.debug(BrokerServiceImpl.class,
                                   "Calling start for {} on {}",
                                   session,
                                   getClusterService().getInstanceData());
            for(FixSessionListener fixSessionListener : brokerService.getFixSessionListeners()) {
                fixSessionListener.sessionStarted(session);
            }
            return true;
        }
        /**
         * Create a new StartSessionTask instance.
         *
         * @param inSession a <code>FixSession</code> value
         */
        private StartSessionTask(FixSession inSession)
        {
            // remember - this session is NOT attached
            session = inSession;
        }
        /**
         * cluster-local broker service value
         */
        @Autowired
        private transient BrokerService brokerService;
        /**
         * fix session to be disabled
         */
        private FixSession session;
        private static final long serialVersionUID = -3799735073665308159L;
    }
    /**
     * Indicates to each cluster member that a particular session has been disabled.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    private static class DisableSessionTask
            extends AbstractCallableClusterTask<Boolean>
    {
        /* (non-Javadoc)
         * @see java.util.concurrent.Callable#call()
         */
        @Override
        public Boolean call()
                throws Exception
        {
            SLF4JLoggerProxy.debug(BrokerServiceImpl.class,
                                   "Calling disable for {} on {}",
                                   session,
                                   getClusterService().getInstanceData());
            for(FixSessionListener fixSessionListener : brokerService.getFixSessionListeners()) {
                fixSessionListener.sessionDisabled(session);
            }
            return true;
        }
        /**
         * Create a new DisableSessionTask instance.
         *
         * @param inSession a <code>FixSession</code> value
         */
        private DisableSessionTask(FixSession inSession)
        {
            // remember - this session is NOT attached
            session = inSession;
        }
        /**
         * cluster-local broker service value
         */
        @Autowired
        private transient BrokerService brokerService;
        /**
         * fix session to be disabled
         */
        private FixSession session;
        private static final long serialVersionUID = -7717222888266945739L;
    }
    /**
     * Indicates to each cluster member that a particular session has been enabled.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    private static class EnableSessionTask
            extends AbstractCallableClusterTask<Boolean>
    {
        /* (non-Javadoc)
         * @see java.util.concurrent.Callable#call()
         */
        @Override
        public Boolean call()
                throws Exception
        {
            SLF4JLoggerProxy.debug(BrokerServiceImpl.class,
                                   "Calling enable for {} on {}",
                                   session,
                                   getClusterService().getInstanceData());
            for(FixSessionListener fixSessionListener : brokerService.getFixSessionListeners()) {
                try {
                    fixSessionListener.sessionEnabled(session);
                } catch (Exception e) {
                    SLF4JLoggerProxy.warn(BrokerServiceImpl.class,
                                          e,
                                          "Enable session listener failed for {}: {}",
                                          brokerService.getSessionName(new SessionID(session.getSessionId())),
                                          ExceptionUtils.getRootCauseMessage(e));
                }
            }
            return true;
        }
        /**
         * Create a new EnableSessionTask instance.
         *
         * @param inSession a <code>FixSession</code> value
         */
        private EnableSessionTask(FixSession inSession)
        {
            // remember - this session is NOT attached
            session = inSession;
        }
        /**
         * cluster-local broker service value
         */
        @Autowired
        private transient BrokerService brokerService;
        /**
         * fix session to be enabled
         */
        private final FixSession session;
        private static final long serialVersionUID = -7107454502447518827L;
    }
    /**
     * Reports the a specified broker status from each cluster member.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    private static class ReportBrokerStatusTask
            extends AbstractRunnableClusterTask
    {
        /* (non-Javadoc)
         * @see java.lang.Runnable#run()
         */
        @Override
        public void run()
        {
            ClusteredBrokerStatus brokerStatus = new ClusteredBrokerStatus(session,
                                                                           getClusterService().getInstanceData(),
                                                                           status,
                                                                           false);
            if(status == FixSessionStatus.DELETED) {
                removeBrokerStatus(brokerStatus);
            } else {
                brokerService.reportBrokerStatus(brokerStatus);
            }
        }
        /**
         * Create a new ReportBrokerStatusTask instance.
         *
         * @param inSession a <code>FixSession</code> value
         * @param inStatus a <code>FixSessionStatus</code> value
         */
        private ReportBrokerStatusTask(FixSession inSession,
                                       FixSessionStatus inStatus)
        {
            session = inSession;
            status = inStatus;
        }
        /**
         * Remove broker status for the given session.
         */
        private void removeBrokerStatus(ClusteredBrokerStatus brokerStatus)
        {
            SLF4JLoggerProxy.trace(BrokerServiceImpl.class,
                                   "Removing status for {}",
                                   session);
            try {
                getClusterService().removeAttribute(BrokerConstants.brokerStatusPrefix+session.getBrokerId()+session.getHost());
            } catch (NullPointerException ignored) {
                // these can happen on shutdown and can be safely ignored
            } catch (Exception e) {
                SLF4JLoggerProxy.warn(this,
                                      e,
                                      "Unable to remove broker status");
            }
        }
        /**
         * cluster-local broker service value
         */
        @Autowired
        private transient BrokerService brokerService;
        /**
         * fix session to be enabled
         */
        private final FixSession session;
        /**
         * status to report
         */
        private final FixSessionStatus status;
        private static final long serialVersionUID = 181147680348143737L;
    }
    /**
     * stores broker status for the whole cluster
     */
    private final List<ClusteredBrokerStatus> clusterBrokerStatus = new ArrayList<>();
    /**
     * provides access to cluster services
     */
    @Autowired
    private ClusterService clusterService;
    /**
     * provides access to the FIX session data store
     */
    @Autowired
    private FixSessionDao fixSessionDao;
    /**
     * create {@link FixSession} objects
     */
    @Autowired
    private FixSessionFactory fixSessionFactory;
    /**
     * create {@link FixSessionAttributeDescriptor} objects
     */
    @Autowired
    private FixSessionAttributeDescriptorFactory fixSessionAttributeDescriptorFactory;
    /**
     * provides data store access to {@link FixSessionAttributeDescriptor} objects
     */
    @Autowired
    private FixSessionAttributeDescriptorDao fixSessionAttributeDescriptorDao;
    /**
     * constructs a FIX settings provider object
     */
    @Autowired
    private FixSettingsProviderFactory fixSettingsProviderFactory;
    /**
     * session customizations by name
     */
    private final Map<String,SessionCustomization> sessionCustomizationsByName = new HashMap<>();
    /**
     * holds all session customizations known to the system
     */
    @Autowired(required=false)
    private List<SessionCustomization> sessionCustomizations;
    /**
     * transaction manager value
     */
    @Autowired
    private JpaTransactionManager txManager;
    /**
     * provides access to session services
     */
    @Autowired
    private SessionService sessionService;
    /**
     * cached brokers value
     */
    @GuardedBy("brokersLock")
    private volatile Brokers brokers;
    /**
     * caches the last broker status
     */
    private String lastBrokerLog = null;
    /**
     * holds fix session listeners
     */
    private final Queue<FixSessionListener> fixSessionListeners = new ConcurrentLinkedQueue<>();
    /**
     * caches session names by session id
     */
    private final Map<SessionID,String> sessionNamesBySessionId = new HashMap<>();
    /**
     * describes the style of the table cell
     */
    private static final CellStyle cellStyle = new CellStyle(HorizontalAlign.center);
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
     * cluster attribute key used to indicate broker status
     */
    private static final String brokerStatusPrefix = "metc.broker.status-";
    /**
     * key used to indicate session customization for a session
     */
    public static final String sessionCustomizationKey = "org.marketcetera.sessioncustomization";
    /**
     * key used to indicate active days for a session
     */
    public static final String sessionDaysKey = "org.marketcetera.sessiondays";
    /**
     * logging category to use for broker status
     */
    private static final String brokerStatusCategory = "metc.brokers";
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
