package org.marketcetera.fix.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import jakarta.annotation.PostConstruct;

import org.marketcetera.brokers.service.FixSessionProvider;
import org.marketcetera.fix.FixSession;
import org.marketcetera.fix.FixSessionAttributeDescriptor;
import org.marketcetera.persist.CollectionPageResponse;
import org.marketcetera.persist.PageRequest;
import org.marketcetera.trade.BrokerID;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.springframework.stereotype.Service;

import quickfix.SessionID;

/**
 * Stub implementation of PersistentFixSessionProvider for Spring Boot 3 migration.
 * 
 * <p>Original implementation required QueryDSL, which is not compatible with Jakarta EE.
 * 
 * <p>See PersistentFixSessionProvider.java.disabled for the original implementation.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 */
@Service
public class PersistentFixSessionProvider
        implements FixSessionProvider
{
    /**
     * Create a new PersistentFixSessionProvider instance.
     */
    public PersistentFixSessionProvider()
    {
        SLF4JLoggerProxy.warn(this, "Using stub implementation for Spring Boot 3 migration");
    }
    
    @PostConstruct
    public void start()
    {
        SLF4JLoggerProxy.warn(this, "Stub implementation - start method not fully implemented");
    }
    
    @Override
    public FixSession findFixSessionByName(String inFixSessionName) {
        SLF4JLoggerProxy.warn(this, "Stub implementation - findFixSessionByName not implemented");
        return null;
    }

    @Override
    public FixSession findFixSessionBySessionId(SessionID inSessionId) {
        SLF4JLoggerProxy.warn(this, "Stub implementation - findFixSessionBySessionId not implemented");
        return null;
    }

    @Override
    public Collection<FixSessionAttributeDescriptor> getFixSessionAttributeDescriptors() {
        SLF4JLoggerProxy.warn(this, "Stub implementation - getFixSessionAttributeDescriptors not implemented");
        return new ArrayList<>();
    }

    @Override
    public List<FixSession> findFixSessions() {
        SLF4JLoggerProxy.warn(this, "Stub implementation - findFixSessions not implemented");
        return new ArrayList<>();
    }

    @Override
    public CollectionPageResponse<FixSession> findFixSessions(PageRequest inPageRequest) {
        SLF4JLoggerProxy.warn(this, "Stub implementation - findFixSessions(PageRequest) not implemented");
        return new CollectionPageResponse<>();
    }

    @Override
    public FixSession findFixSessionByBrokerId(BrokerID inBrokerId) {
        SLF4JLoggerProxy.warn(this, "Stub implementation - findFixSessionByBrokerId not implemented");
        return null;
    }

    @Override
    public List<FixSession> findFixSessions(boolean isAcceptor, int inInstance, int inTotalInstances) {
        SLF4JLoggerProxy.warn(this, "Stub implementation - findFixSessions(boolean, int, int) not implemented");
        return new ArrayList<>();
    }

    @Override
    public List<FixSession> findFixSessions(int inInstance, int inTotalInstances) {
        SLF4JLoggerProxy.warn(this, "Stub implementation - findFixSessions(int, int) not implemented");
        return new ArrayList<>();
    }

    @Override
    public FixSession save(FixSession inFixSession) {
        SLF4JLoggerProxy.warn(this, "Stub implementation - save(FixSession) not implemented");
        return inFixSession;
    }

    @Override
    public FixSession save(String inFixSessionName, FixSession inFixSession) {
        SLF4JLoggerProxy.warn(this, "Stub implementation - save(String, FixSession) not implemented");
        return inFixSession;
    }

    @Override
    public void delete(SessionID inFixSessionId) {
        SLF4JLoggerProxy.warn(this, "Stub implementation - delete not implemented");
    }

    @Override
    public void disableSession(SessionID inSessionId) {
        SLF4JLoggerProxy.warn(this, "Stub implementation - disableSession not implemented");
    }

    @Override
    public void enableSession(SessionID inSessionId) {
        SLF4JLoggerProxy.warn(this, "Stub implementation - enableSession not implemented");
    }

    @Override
    public FixSessionAttributeDescriptor save(FixSessionAttributeDescriptor inFixSessionAttributeDescriptor) {
        SLF4JLoggerProxy.warn(this, "Stub implementation - save(FixSessionAttributeDescriptor) not implemented");
        return inFixSessionAttributeDescriptor;
    }

    @Override
    public void stopSession(SessionID inSessionID) {
        SLF4JLoggerProxy.warn(this, "Stub implementation - stopSession not implemented");
    }

    @Override
    public void startSession(SessionID inSessionID) {
        SLF4JLoggerProxy.warn(this, "Stub implementation - startSession not implemented");
    }
}