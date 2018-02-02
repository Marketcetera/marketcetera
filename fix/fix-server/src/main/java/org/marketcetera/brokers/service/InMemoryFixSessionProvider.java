package org.marketcetera.brokers.service;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.Validate;
import org.marketcetera.fix.FixSession;
import org.marketcetera.fix.FixSessionAttributeDescriptor;
import org.marketcetera.fix.FixSessionFactory;
import org.marketcetera.fix.FixSettingsProvider;
import org.marketcetera.fix.FixSettingsProviderFactory;
import org.marketcetera.fix.provisioning.FixSessionsConfiguration;
import org.marketcetera.persist.CollectionPageResponse;
import org.marketcetera.persist.PageRequest;
import org.marketcetera.trade.BrokerID;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import quickfix.SessionFactory;
import quickfix.SessionID;
import quickfix.SessionSettings;

/* $License$ */

/**
 * Provides FIX messages via an in-memory store.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class InMemoryFixSessionProvider
        implements FixSessionProvider
{
    /* (non-Javadoc)
     * @see org.marketcetera.brokers.service.FixSessionProvider#findFixSessionByName(java.lang.String)
     */
    @Override
    public FixSession findFixSessionByName(String inFixSessionName)
    {
        return fixSessionsByName.getIfPresent(inFixSessionName);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.brokers.service.FixSessionProvider#findFixSessionBySessionId(quickfix.SessionID)
     */
    @Override
    public FixSession findFixSessionBySessionId(SessionID inSessionId)
    {
        return fixSessionsBySessionId.getIfPresent(inSessionId);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.brokers.service.FixSessionProvider#getFixSessionAttributeDescriptors()
     */
    @Override
    public Collection<FixSessionAttributeDescriptor> getFixSessionAttributeDescriptors()
    {
        return fixSessionAttributeDescriptors;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.brokers.service.FixSessionProvider#findFixSessions()
     */
    @Override
    public List<FixSession> findFixSessions()
    {
        return Collections.unmodifiableList(Lists.newArrayList(fixSessionsBySessionId.asMap().values()));
    }
    /* (non-Javadoc)
     * @see org.marketcetera.brokers.service.FixSessionProvider#findFixSessionByBrokerId(org.marketcetera.trade.BrokerID)
     */
    @Override
    public FixSession findFixSessionByBrokerId(BrokerID inBrokerId)
    {
        return fixSessionsByBrokerId.getIfPresent(inBrokerId);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.brokers.service.FixSessionProvider#findFixSessions(boolean, int, int)
     */
    @Override
    public List<FixSession> findFixSessions(boolean inIsAcceptor,
                                            int inInstance,
                                            int inTotalInstances)
    {
        List<FixSession> allFixSessions = Lists.newArrayList(findFixSessions());
        Iterator<FixSession> allFixSessionIterator = allFixSessions.iterator();
        while(allFixSessionIterator.hasNext()) {
            FixSession session = allFixSessionIterator.next();
            if(inIsAcceptor == session.isAcceptor()) {
                int brokerInstanceAffinity = session.getAffinity();
                while(brokerInstanceAffinity > inTotalInstances) {
                    brokerInstanceAffinity -= inTotalInstances;
                }
                if(brokerInstanceAffinity != inInstance) {
                    allFixSessionIterator.remove();
                }
            } else {
                allFixSessionIterator.remove();
            }
        }
        return allFixSessions;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.brokers.service.FixSessionProvider#findFixSessions(org.marketcetera.persist.PageRequest)
     */
    @Override
    public CollectionPageResponse<FixSession> findFixSessions(PageRequest inPageRequest)
    {
        CollectionPageResponse<FixSession> result = new CollectionPageResponse<>();
        // TODO fill other parts of the result
        result.setElements(findFixSessions());
        return result;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.brokers.service.FixSessionProvider#save(org.marketcetera.fix.FixSession)
     */
    @Override
    public FixSession save(FixSession inFixSession)
    {
        fixSessionsByName.put(inFixSession.getName(),
                              inFixSession);
        fixSessionsBySessionId.put(new SessionID(inFixSession.getSessionId()),
                                   inFixSession);
        fixSessionsByBrokerId.put(new BrokerID(inFixSession.getBrokerId()),
                                  inFixSession);
        return inFixSession;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.brokers.service.FixSessionProvider#delete(quickfix.SessionID)
     */
    @Override
    public void delete(SessionID inFixSessionId)
    {
        FixSession fixSession = fixSessionsBySessionId.getIfPresent(inFixSessionId);
        if(fixSession != null) {
            fixSessionsByName.invalidate(fixSession.getName());
            fixSessionsBySessionId.invalidate(inFixSessionId);
            fixSessionsByBrokerId.invalidate(new BrokerID(fixSession.getBrokerId()));
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.brokers.service.FixSessionProvider#disableSession(quickfix.SessionID)
     */
    @Override
    public void disableSession(SessionID inSessionId)
    {
        SLF4JLoggerProxy.warn(this,
                              "Unable to disable session, dynamic sessions are not supported");
    }
    /* (non-Javadoc)
     * @see org.marketcetera.brokers.service.FixSessionProvider#enableSession(quickfix.SessionID)
     */
    @Override
    public void enableSession(SessionID inSessionId)
    {
        SLF4JLoggerProxy.warn(this,
                              "Unable to enable session, dynamic sessions are not supported");
    }
    /* (non-Javadoc)
     * @see org.marketcetera.brokers.service.FixSessionProvider#save(org.marketcetera.fix.FixSessionAttributeDescriptor)
     */
    @Override
    public FixSessionAttributeDescriptor save(FixSessionAttributeDescriptor inFixSessionAttributeDescriptor)
    {
        fixSessionAttributeDescriptors.add(inFixSessionAttributeDescriptor);
        return inFixSessionAttributeDescriptor;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.brokers.service.FixSessionProvider#stopSession(quickfix.SessionID)
     */
    @Override
    public void stopSession(SessionID inSessionID)
    {
        SLF4JLoggerProxy.warn(this,
                              "Unable to stop session, dynamic sessions are not supported");
    }
    /* (non-Javadoc)
     * @see org.marketcetera.brokers.service.FixSessionProvider#startSession(quickfix.SessionID)
     */
    @Override
    public void startSession(SessionID inSessionID)
    {
        SLF4JLoggerProxy.warn(this,
                              "Unable to start session, dynamic sessions are not supported");
    }
    /**
     * Set the FIX sessions value.
     *
     * @param inFixSessionsConfiguration a <code>FixSessionsConfiguration</code> value
     */
    @Autowired(required=false)
    public void setFixSessions(FixSessionsConfiguration inFixSessionsConfiguration)
    {
        FixSettingsProvider fixSettingsProvider = fixSettingsProviderFactory.create();
        fixSessionsByName.invalidateAll();
        fixSessionsBySessionId.invalidateAll();
        fixSessionsByBrokerId.invalidateAll();
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
            }
        }
        SLF4JLoggerProxy.debug(this,
                               "Created brokers: {}",
                               fixSessionsByName.asMap());
    }
    /**
     * Validate and start the object.
     */
    @PostConstruct
    public void start()
    {
        Validate.notNull(fixSettingsProviderFactory);
        Validate.notNull(fixSessionFactory);
    }
    /**
     * cache FIX sessions by name
     */
    private final Cache<String,FixSession> fixSessionsByName = CacheBuilder.newBuilder().build();
    /**
     * cache FIX sessions by session ID
     */
    private final Cache<SessionID,FixSession> fixSessionsBySessionId = CacheBuilder.newBuilder().build();
    /**
     * cache FIX sessions by broker ID
     */
    private final Cache<BrokerID,FixSession> fixSessionsByBrokerId = CacheBuilder.newBuilder().build();
    /**
     * FIX session attribute descriptors
     */
    private Collection<FixSessionAttributeDescriptor> fixSessionAttributeDescriptors = Lists.newArrayList();
    /**
     * provides FIX settings
     */
    @Autowired
    private FixSettingsProviderFactory fixSettingsProviderFactory;
    /**
     * creates {@link FixSession} objects
     */
    @Autowired
    private FixSessionFactory fixSessionFactory;
}
