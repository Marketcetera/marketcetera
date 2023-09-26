package org.marketcetera.fix.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeSet;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.Validate;
import org.marketcetera.brokers.service.DisableSessionTask;
import org.marketcetera.brokers.service.EnableSessionTask;
import org.marketcetera.brokers.service.FixSessionProvider;
import org.marketcetera.brokers.service.ReportBrokerStatusTask;
import org.marketcetera.brokers.service.StartSessionTask;
import org.marketcetera.brokers.service.StopSessionTask;
import org.marketcetera.cluster.service.ClusterService;
import org.marketcetera.core.PlatformServices;
import org.marketcetera.fix.FixSession;
import org.marketcetera.fix.FixSessionAttributeDescriptor;
import org.marketcetera.fix.FixSessionAttributeDescriptorFactory;
import org.marketcetera.fix.FixSessionStatus;
import org.marketcetera.fix.FixSettingsProvider;
import org.marketcetera.fix.FixSettingsProviderFactory;
import org.marketcetera.fix.MutableFixSession;
import org.marketcetera.fix.MutableFixSessionFactory;
import org.marketcetera.fix.provisioning.FixSessionsConfiguration;
import org.marketcetera.persist.CollectionPageResponse;
import org.marketcetera.persist.PageRequest;
import org.marketcetera.persist.SortDirection;
import org.marketcetera.trade.BrokerID;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.CacheLoader.InvalidCacheLoadException;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.querydsl.core.BooleanBuilder;

import quickfix.SessionFactory;
import quickfix.SessionID;
import quickfix.SessionSettings;

/* $License$ */

/**
 * Provides a persistent {@link FixSessionProvider} implementation.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@EnableAutoConfiguration
public class PersistentFixSessionProvider
        implements FixSessionProvider
{
    /* (non-Javadoc)
     * @see org.marketcetera.brokers.service.FixSessionProvider#findFixSessionByName(java.lang.String)
     */
    @Override
    @Transactional(readOnly=true,propagation=Propagation.REQUIRED)
    public FixSession findFixSessionByName(String inFixSessionName)
    {
        try {
            return fixSessionsByName.getUnchecked(inFixSessionName);
        } catch (InvalidCacheLoadException e) {
            return null;
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.brokers.service.FixSessionProvider#findFixSessionBySessionId(quickfix.SessionID)
     */
    @Override
    @Transactional(readOnly=true,propagation=Propagation.REQUIRED)
    public FixSession findFixSessionBySessionId(SessionID inSessionId)
    {
        try {
            return fixSessionsBySessionId.getUnchecked(inSessionId);
        } catch (InvalidCacheLoadException e) {
            return null;
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.brokers.service.FixSessionProvider#getFixSessionAttributeDescriptors()
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
     * @see org.marketcetera.brokers.service.FixSessionProvider#findFixSessions()
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
            sessionsToReturn.add(session);
        }
        return sessionsToReturn;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.brokers.service.FixSessionProvider#findFixSessions(org.marketcetera.persist.PageRequest)
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
            jpaSort = Sort.by(new Sort.Order(Sort.Direction.ASC,
                                             QPersistentFixSession.persistentFixSession.name.getMetadata().getName()));
        } else {
            for(org.marketcetera.persist.Sort sort : inPageRequest.getSortOrder()) {
                Sort.Direction jpaSortDirection = sort.getDirection()==SortDirection.ASCENDING?Sort.Direction.ASC:Sort.Direction.DESC;
                String property = getFixSessionPropertyFor(sort);
                if(jpaSort == null) {
                    jpaSort = Sort.by(new Sort.Order(jpaSortDirection,
                                                     property));
                } else {
                    jpaSort = jpaSort.and(Sort.by(new Sort.Order(jpaSortDirection,
                                                                 property)));
                }
            }
        }
        SLF4JLoggerProxy.trace(this,
                               "Applying page sort: {}",
                               jpaSort);
        org.springframework.data.domain.PageRequest pageRequest = org.springframework.data.domain.PageRequest.of(inPageRequest.getPageNumber(),
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
    /* (non-Javadoc)
     * @see org.marketcetera.brokers.service.FixSessionProvider#findFixSessionByBrokerId(org.marketcetera.trade.BrokerID)
     */
    @Override
    @Transactional(readOnly=true,propagation=Propagation.REQUIRED)
    public FixSession findFixSessionByBrokerId(BrokerID inBrokerId)
    {
        try {
            return fixSessionsByBrokerId.getUnchecked(inBrokerId);
        } catch (InvalidCacheLoadException e) {
            return null;
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.brokers.service.FixSessionProvider#findFixSessions(boolean, int, int)
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
     * @see org.marketcetera.brokers.service.FixSessionProvider#findFixSessions(int, int)
     */
    @Override
    @Transactional(readOnly=true,propagation=Propagation.REQUIRED)
    public List<FixSession> findFixSessions(int inInstance,
                                            int inTotalInstances)
    {
        List<FixSession> sessionsToReturn = new ArrayList<>();
        List<PersistentFixSession> allSessionsByConnectionType = fixSessionDao.findByIsDeletedFalseOrderByAffinityAsc();
        SLF4JLoggerProxy.debug(this,
                               "Determining sessions for instance {} of {}",
                               inInstance,
                               inTotalInstances);
        for(FixSession session : allSessionsByConnectionType) {
            // need to sort out which sessions should be returned based on affinity
            int brokerInstanceAffinity = session.getAffinity();
            while(brokerInstanceAffinity > inTotalInstances) {
                brokerInstanceAffinity -= inTotalInstances;
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
     * @see org.marketcetera.brokers.service.FixSessionProvider#save(java.lang.String, org.marketcetera.fix.FixSession)
     */
    @Override
    public FixSession save(String inFixSessionName,
                           FixSession inFixSession)
    {
        // need a manual transaction to allow the session to be flushed to the db before reporting status
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("saveSessionTransaction");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setReadOnly(false);
        TransactionStatus status = txManager.getTransaction(def);
        PersistentFixSession existingSession;
        try {
            SLF4JLoggerProxy.debug(this,
                                   "Saving {} -> {}",
                                   inFixSessionName,
                                   inFixSession);
            BooleanBuilder where = new BooleanBuilder();
            where = where.and(QPersistentFixSession.persistentFixSession.isDeleted.isFalse());
            where = where.and(QPersistentFixSession.persistentFixSession.name.eq(inFixSessionName));
            Optional<PersistentFixSession> existingSessionOption = fixSessionDao.findOne(where);
            if(!existingSessionOption.isPresent()) {
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
                existingSession = existingSessionOption.get();
                // in order to change the session, it must be disabled
                Validate.isTrue(!existingSession.isEnabled(),
                                "Session " + existingSession.getSessionId() + " must be disabled before it can be modified");
                existingSession.update(inFixSession);
            }
            existingSession.validateSession();
            // do not allow the session to enabled via the backdoor
            existingSession.setIsEnabled(false);
            existingSession = fixSessionDao.save(existingSession);
            txManager.commit(status);
            clearCache(existingSession);
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
        // have each cluster member report disabled status for this session
        ReportBrokerStatusTask reportStatusTask = new ReportBrokerStatusTask(existingSession,
                                                                             FixSessionStatus.DISABLED);
        clusterService.execute(reportStatusTask);
        return existingSession;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.brokers.service.FixSessionProvider#save(org.marketcetera.fix.FixSession)
     */
    @Override
    public FixSession save(FixSession inFixSession)
    {
        return save(inFixSession.getName(),
                    inFixSession);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.brokers.service.FixSessionProvider#delete(quickfix.SessionID)
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
        clearCache(existingSession);
        ReportBrokerStatusTask reportStatusTask = new ReportBrokerStatusTask(existingSession,
                                                                             FixSessionStatus.DELETED);
        clusterService.execute(reportStatusTask);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.brokers.service.FixSessionProvider#disableSession(quickfix.SessionID)
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
            clearCache(disabledSession);
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
     * @see org.marketcetera.brokers.service.FixSessionProvider#enableSession(quickfix.SessionID)
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
            clearCache(enabledSession);
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
            if(!PlatformServices.isShutdown(e)) {
                SLF4JLoggerProxy.warn(this,
                                      e,
                                      "Unable to enable {}",
                                      enabledSession);
            }
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
     * @see org.marketcetera.brokers.service.FixSessionProvider#save(org.marketcetera.fix.FixSessionAttributeDescriptor)
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
     * @see org.marketcetera.brokers.service.FixSessionProvider#stopSession(quickfix.SessionID)
     */
    @Override
    @Transactional(readOnly=true,propagation=Propagation.REQUIRED)
    public void stopSession(SessionID inSessionId)
    {
        SLF4JLoggerProxy.debug(this,
                               "Stopping {}",
                               inSessionId);
        FixSession stoppedSession = findFixSessionBySessionId(inSessionId);
        if(stoppedSession == null) {
            // somebody went and deleted this session, nothing to do, just slink away quietly
            SLF4JLoggerProxy.debug(this,
                                   "Not calling stop task for deleted session {}",
                                   inSessionId);
            return;
        }
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
     * @see org.marketcetera.brokers.service.FixSessionProvider#startSession(quickfix.SessionID)
     */
    @Override
    @Transactional(readOnly=true,propagation=Propagation.REQUIRED)
    public void startSession(SessionID inSessionId)
    {
        SLF4JLoggerProxy.debug(this,
                               "Starting {}",
                               inSessionId);
        FixSession startedSession = findFixSessionBySessionId(inSessionId);
        if(startedSession == null) {
            // somebody went and deleted this session, nothing to do, just slink away quietly
            SLF4JLoggerProxy.debug(this,
                                   "Not calling start task for deleted session {}",
                                   inSessionId);
            return;
        }
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
    /**
     * Validate and start the object.
     */
    @PostConstruct
    public void start()
    {
        // create and enable FIX sessions provided via configuration, but, this can cause a race condition on startup
        //  with certain cluster implementations. therefore, allow, via config, the option to delay FIX session creation
        if(fixSessionCreationDelay > 0) {
            scheduledService.schedule(new Runnable() {
                @Override
                public void run()
                {
                    try {
                        createFixSessionsFromConfig();
                    } catch (Exception e) {
                        PlatformServices.handleException(PersistentFixSessionProvider.this,
                                                         "Unable to provision FIX messages",
                                                         e);
                    }
                }},fixSessionCreationDelay,TimeUnit.MILLISECONDS);
        } else {
            createFixSessionsFromConfig();
        }
    }
    /**
     * Clear the given session from all session caches.
     *
     * @param inFixSession a <code>FixSession</code> value
     */
    private void clearCache(FixSession inFixSession)
    {
        fixSessionsByBrokerId.invalidate(new BrokerID(inFixSession.getBrokerId()));
        fixSessionsBySessionId.invalidate(new quickfix.SessionID(inFixSession.getSessionId()));
        fixSessionsByName.invalidate(inFixSession.getName());
    }
    /**
     * Process FIX session info provided from config
     */
    private void createFixSessionsFromConfig()
    {
        SLF4JLoggerProxy.info(PersistentFixSessionProvider.this,
                              "Beginning FIX session provisioning");
        Map<String,FixSession> fixSessionsByName = Maps.newHashMap();
        FixSettingsProvider fixSettingsProvider = fixSettingsProviderFactory.create();
        for(FixSessionsConfiguration.FixSessionDescriptor fixSessionsDescriptor : fixSessionsConfiguration.getSessionDescriptors()) {
            Map<String,String> globalSettings = fixSessionsDescriptor.getSettings();
            for(FixSessionsConfiguration.Session fixSessionDescriptor : fixSessionsDescriptor.getSessions()) {
                try {
                    Map<String,String> sessionSettings = Maps.newHashMap();
                    sessionSettings.putAll(globalSettings);
                    sessionSettings.putAll(fixSessionDescriptor.getSettings());
                    String fixSessionName = fixSessionDescriptor.getName();
                    FixSession existingFixSession = findFixSessionByName(fixSessionName);
                    if(existingFixSession != null) {
                        SLF4JLoggerProxy.info(this,
                                              "Skipping existing FIX session: {}",
                                              fixSessionName);
                        continue;
                    }
                    MutableFixSession fixSession = fixSessionFactory.create();
                    fixSession.setAffinity(fixSessionDescriptor.getAffinity());
                    fixSession.setBrokerId(fixSessionDescriptor.getBrokerId());
                    if(fixSessionDescriptor.getMappedBrokerId() != null) {
                        fixSession.setMappedBrokerId(fixSessionDescriptor.getMappedBrokerId());
                    }
                    fixSession.setDescription(fixSessionDescriptor.getDescription());
                    String connectionType = sessionSettings.get(SessionFactory.SETTING_CONNECTION_TYPE);
                    fixSession.setIsAcceptor(SessionFactory.ACCEPTOR_CONNECTION_TYPE.equals(connectionType));
                    fixSession.setIsEnabled(fixSessionDescriptor.isEnabled());
                    if(fixSession.isAcceptor()) {
                        fixSession.setHost(fixSettingsProvider.getAcceptorHost());
                        fixSession.setPort(fixSettingsProvider.getAcceptorPort());
                    } else {
                        fixSession.setHost(fixSessionDescriptor.getHost());
                        fixSession.setPort(fixSessionDescriptor.getPort());
                    }
                    fixSession.setName(fixSessionName);
                    SessionID sessionId = new SessionID(sessionSettings.get(SessionSettings.BEGINSTRING),
                                                        sessionSettings.get(SessionSettings.SENDERCOMPID),
                                                        sessionSettings.get(SessionSettings.TARGETCOMPID));
                    fixSession.setSessionId(sessionId.toString());
                    fixSession.getSessionSettings().putAll(sessionSettings);
                    save(fixSession);
                    fixSessionsByName.put(fixSession.getName(),
                                          fixSession);
                    if(fixSessionDescriptor.isEnabled()) {
                        enableSession(new SessionID(fixSession.getSessionId()));
                    }
                    SLF4JLoggerProxy.info(this,
                                          "Created: {}",
                                          fixSession.getName());
                } catch (Exception e) {
                    SLF4JLoggerProxy.info(PersistentFixSessionProvider.this,
                                          e,
                                          "Unable to create session: {}",
                                          fixSessionDescriptor.getName());
                }
            }
        }
    }
    /**
     * Get the FIX session property for the given sort.
     *
     * @param inSort an <code>org.marketcetera.persist.Sort</code> value
     * @return a <code>String</code> value
     */
    private String getFixSessionPropertyFor(org.marketcetera.persist.Sort inSort)
    {
        String lcaseProperty = inSort.getProperty().toLowerCase();
        if(QPersistentFixSession.persistentFixSession.name.getMetadata().getName().toLowerCase().equals(lcaseProperty)) {
            return QPersistentFixSession.persistentFixSession.name.getMetadata().getName();
        } else if(QPersistentFixSession.persistentFixSession.sessionId.getMetadata().getName().toLowerCase().equals(lcaseProperty)) {
            return QPersistentFixSession.persistentFixSession.sessionId.getMetadata().getName();
        } else if(QPersistentFixSession.persistentFixSession.brokerId.getMetadata().getName().toLowerCase().equals(lcaseProperty)) {
            return QPersistentFixSession.persistentFixSession.brokerId.getMetadata().getName();
        } else {
            throw new UnsupportedOperationException("Unsupported sort property for FIX Sessions: " + inSort.getProperty());
        }
    }
    /**
     * Report broker status from all nodes.
     *
     * @param inFixSession a <code>FixSession</code> value
     * @param inStatusToReport a <code>FixSessionStatus</code> value
     */
    private void reportBrokerStatusFromAll(FixSession inFixSession,
                                           FixSessionStatus inStatusToReport)
    {
        ReportBrokerStatusTask reportStatusTask = new ReportBrokerStatusTask(inFixSession,
                                                                             inStatusToReport);
        clusterService.execute(reportStatusTask);
    }
    /**
     * stores fix sessions by session id
     */
    private final LoadingCache<SessionID,FixSession> fixSessionsBySessionId = CacheBuilder.newBuilder().build(new CacheLoader<SessionID,FixSession>() {
        @Override
        public FixSession load(SessionID inKey)
                throws Exception
        {
            return fixSessionDao.findBySessionIdAndIsDeletedFalse(inKey.toString());
        }}
    );
    /**
     * stores fix sessions by name
     */
    private final LoadingCache<String,FixSession> fixSessionsByName = CacheBuilder.newBuilder().build(new CacheLoader<String,FixSession>() {
        @Override
        public FixSession load(String inKey)
                throws Exception
        {
            return fixSessionDao.findByNameAndIsDeletedFalse(inKey);
        }}
    );
    /**
     * stores fix sessions by broker id
     */
    private final LoadingCache<BrokerID,FixSession> fixSessionsByBrokerId = CacheBuilder.newBuilder().build(new CacheLoader<BrokerID,FixSession>() {
        @Override
        public FixSession load(BrokerID inKey)
                throws Exception
        {
            return fixSessionDao.findByBrokerIdAndIsDeletedFalse(inKey.getValue());
        }}
    );
    /**
     * FIX session config provided in application start up
     */
    @Autowired
    private FixSessionsConfiguration fixSessionsConfiguration;
    /**
     * schedules tasks
     */
    private ScheduledExecutorService scheduledService = Executors.newSingleThreadScheduledExecutor(new ThreadFactoryBuilder().setNameFormat(getClass().getSimpleName()+"FixSessionConfigCreator").build());
    /**
     * indicates how long, if at all, to delay FIX session creation from config
     */
    @Value("${metc.fix.session.creation.delay:0}")
    private long fixSessionCreationDelay;
    /**
     * provides FIX settings
     */
    @Autowired
    private FixSettingsProviderFactory fixSettingsProviderFactory;
    /**
     * provides access to the FIX session data store
     */
    @Autowired
    private FixSessionDao fixSessionDao;
    /**
     * provides access to cluster services
     */
    @Autowired
    private ClusterService clusterService;
    /**
     * provides data store access to {@link FixSessionAttributeDescriptor} objects
     */
    @Autowired
    private FixSessionAttributeDescriptorDao fixSessionAttributeDescriptorDao;
    /**
     * create {@link FixSession} objects
     */
    @Autowired
    private MutableFixSessionFactory fixSessionFactory;
    /**
     * transaction manager value
     */
    @Autowired
    private PlatformTransactionManager txManager;
    /**
     * create {@link FixSessionAttributeDescriptor} objects
     */
    @Autowired
    private FixSessionAttributeDescriptorFactory fixSessionAttributeDescriptorFactory;
}
