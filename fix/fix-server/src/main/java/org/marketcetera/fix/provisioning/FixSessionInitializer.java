package org.marketcetera.fix.provisioning;

import java.util.Collection;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.marketcetera.brokers.service.BrokerService;
import org.marketcetera.core.PlatformServices;
import org.marketcetera.fix.FixSession;
import org.marketcetera.fix.FixSessionFactory;
import org.marketcetera.fix.FixSettingsProvider;
import org.marketcetera.fix.FixSettingsProviderFactory;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import quickfix.SessionFactory;
import quickfix.SessionID;
import quickfix.SessionSettings;

/* $License$ */

/**
 * Bootstraps {@link FixSession} values on start.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class FixSessionInitializer
{
    /**
     * Starts and validates the object.
     */
    @PostConstruct
    public void start()
    {
        Map<String,FixSession> fixSessionsByName = Maps.newHashMap();
        FixSettingsProvider fixSettingsProvider = fixSettingsProviderFactory.create();
        for(FixSessionsDescriptor fixSessionsDescriptor : fixSessionDescriptors) {
            Map<String,String> globalSettings = fixSessionsDescriptor.getSessionSettings().getSessionSettings();
            for(FixSessionDescriptor fixSessionDescriptor : fixSessionsDescriptor.getFixSessions()) {
                Map<String,String> sessionSettings = Maps.newHashMap();
                sessionSettings.putAll(globalSettings);
                sessionSettings.putAll(fixSessionDescriptor.getSessionSettings().getSessionSettings());
                FixSession fixSession = fixSessionFactory.create();
                fixSession.setAffinity(fixSessionDescriptor.getAffinity());
                fixSession.setBrokerId(fixSessionDescriptor.getBrokerId().getValue());
                if(fixSessionDescriptor.getMappedBrokerId() != null) {
                    fixSession.setMappedBrokerId(fixSessionDescriptor.getMappedBrokerId().getValue());
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
                try {
                    fixSessionsByName.put(fixSession.getName(),
                                          brokerService.save(fixSession));
                } catch (Exception e) {
                    PlatformServices.handleException(this,
                                                     "Cannot create session " + fixSession.getName(),
                                                     e);
                }
            }
        }
        SLF4JLoggerProxy.debug(this,
                               "Created brokers: {}",
                               fixSessionsByName);
    }
    /**
     * Get the fixSessionDescriptors value.
     *
     * @return a <code>Collection&lt;FixSessionsDescriptor&gt;</code> value
     */
    public Collection<FixSessionsDescriptor> getFixSessionDescriptors()
    {
        return fixSessionDescriptors;
    }
    /**
     * Sets the fixSessionDescriptors value.
     *
     * @param inFixSessionDescriptors a <code>Collection<FixSessionsDescriptor></code> value
     */
    public void setFixSessionDescriptors(Collection<FixSessionsDescriptor> inFixSessionDescriptors)
    {
        fixSessionDescriptors = inFixSessionDescriptors;
    }
    /**
     * FIX session attribute descriptors
     */
    private Collection<FixSessionsDescriptor> fixSessionDescriptors = Lists.newArrayList();
    /**
     * provides access to broker services
     */
    @Autowired
    private BrokerService brokerService;
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

