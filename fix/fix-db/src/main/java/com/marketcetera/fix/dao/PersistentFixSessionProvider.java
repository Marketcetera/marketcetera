package com.marketcetera.fix.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeSet;
import java.util.concurrent.Future;

import org.apache.commons.lang3.Validate;
import org.marketcetera.brokers.service.DisableSessionTask;
import org.marketcetera.brokers.service.EnableSessionTask;
import org.marketcetera.brokers.service.FixSessionProvider;
import org.marketcetera.brokers.service.ReportBrokerStatusTask;
import org.marketcetera.brokers.service.StartSessionTask;
import org.marketcetera.brokers.service.StopSessionTask;
import org.marketcetera.cluster.service.ClusterService;
import org.marketcetera.fix.FixSession;
import org.marketcetera.fix.FixSessionAttributeDescriptor;
import org.marketcetera.fix.FixSessionAttributeDescriptorFactory;
import org.marketcetera.fix.FixSessionFactory;
import org.marketcetera.fix.FixSessionStatus;
import org.marketcetera.fix.FixSettingsProvider;
import org.marketcetera.fix.FixSettingsProviderFactory;
import org.marketcetera.fix.provisioning.FixSessionsConfiguration;
import org.marketcetera.persist.CollectionPageResponse;
import org.marketcetera.persist.PageRequest;
import org.marketcetera.persist.SortDirection;
import org.marketcetera.trade.BrokerID;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.google.common.collect.Maps;
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
        return fixSessionDao.findByNameAndIsDeletedFalse(inFixSessionName);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.brokers.service.FixSessionProvider#findFixSessionBySessionId(quickfix.SessionID)
     */
    @Override
    @Transactional(readOnly=true,propagation=Propagation.REQUIRED)
    public FixSession findFixSessionBySessionId(SessionID inSessionId)
    {
        return fixSessionDao.findBySessionIdAndIsDeletedFalse(inSessionId.toString());
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
        return fixSessionDao.findByBrokerIdAndIsDeletedFalse(inBrokerId.getValue());
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
     * @see org.marketcetera.brokers.service.FixSessionProvider#save(org.marketcetera.fix.FixSession)
     */
    @Override
    @Transactional(readOnly=false,propagation=Propagation.REQUIRED)
    public FixSession save(FixSession inFixSession)
    {
        SLF4JLoggerProxy.debug(this,
                               "Saving {}",
                               inFixSession);
        BooleanBuilder where = new BooleanBuilder();
        where = where.and(QPersistentFixSession.persistentFixSession.isDeleted.isFalse());
        where = where.and(QPersistentFixSession.persistentFixSession.id.eq(inFixSession.getId()));
        Optional<PersistentFixSession> existingSessionOption = fixSessionDao.findOne(where);
        PersistentFixSession existingSession;
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
        existingSession.validate();
        // do not allow the session to enabled via the backdoor
        existingSession.setIsEnabled(false);
        existingSession = fixSessionDao.save(existingSession);
        // have each cluster member report disabled status for this session
        ReportBrokerStatusTask reportStatusTask = new ReportBrokerStatusTask(existingSession,
                                                                             FixSessionStatus.DISABLED);
        clusterService.execute(reportStatusTask);
        return existingSession;
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
        FixSession stoppedSession = fixSessionDao.findBySessionIdAndIsDeletedFalse(inSessionId.toString());
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
        FixSession startedSession = fixSessionDao.findBySessionIdAndIsDeletedFalse(inSessionId.toString());
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
     * Set the FIX sessions value.
     *
     * @param inFixSessionsConfiguration a <code>FixSessionsConfiguration</code> value
     */
    @Autowired(required=false)
    public void setFixSessions(FixSessionsConfiguration inFixSessionsConfiguration)
    {
        Map<String,FixSession> fixSessionsByName = Maps.newHashMap();
        FixSettingsProvider fixSettingsProvider = fixSettingsProviderFactory.create();
        for(FixSessionsConfiguration.FixSessionDescriptor fixSessionsDescriptor : inFixSessionsConfiguration.getSessionDescriptors()) {
            Map<String,String> globalSettings = fixSessionsDescriptor.getSettings();
            for(FixSessionsConfiguration.Session fixSessionDescriptor : fixSessionsDescriptor.getSessions()) {
                Map<String,String> sessionSettings = Maps.newHashMap();
                sessionSettings.putAll(globalSettings);
                sessionSettings.putAll(fixSessionDescriptor.getSettings());
                FixSession fixSession = fixSessionFactory.create();
                fixSession.setAffinity(fixSessionDescriptor.getAffinity());
                fixSession.setBrokerId(fixSessionDescriptor.getBrokerId());
                if(fixSessionDescriptor.getMappedBrokerId() != null) {
                    fixSession.setMappedBrokerId(fixSessionDescriptor.getMappedBrokerId());
                }
                fixSession.setDescription(fixSessionDescriptor.getDescription());
                String connectionType = sessionSettings.get(SessionFactory.SETTING_CONNECTION_TYPE);
                fixSession.setIsAcceptor(SessionFactory.ACCEPTOR_CONNECTION_TYPE.equals(connectionType));
                fixSession.setIsEnabled(true);
                if(fixSession.isAcceptor()) {
                    fixSession.setHost(fixSettingsProvider.getAcceptorHost());
                    fixSession.setPort(fixSettingsProvider.getAcceptorPort());
                } else {
                    fixSession.setHost(fixSessionDescriptor.getHost());
                    fixSession.setPort(fixSessionDescriptor.getPort());
                }
                fixSession.setName(fixSessionDescriptor.getName());
                SessionID sessionId = new SessionID(sessionSettings.get(SessionSettings.BEGINSTRING),
                                                    sessionSettings.get(SessionSettings.SENDERCOMPID),
                                                    sessionSettings.get(SessionSettings.TARGETCOMPID));
                fixSession.setSessionId(sessionId.toString());
                fixSession.getSessionSettings().putAll(sessionSettings);
                save(fixSession);
                fixSessionsByName.put(fixSession.getName(),
                                      fixSession);
            }
        }
        SLF4JLoggerProxy.debug(this,
                               "Created brokers: {}",
                               fixSessionsByName);
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
    private FixSessionFactory fixSessionFactory;
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
