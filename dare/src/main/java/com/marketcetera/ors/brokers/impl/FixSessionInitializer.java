package com.marketcetera.ors.brokers.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.Validate;
import org.marketcetera.core.PlatformServices;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.springframework.beans.factory.annotation.Autowired;

import quickfix.Acceptor;
import quickfix.Dictionary;
import quickfix.Initiator;
import quickfix.SessionFactory;
import quickfix.SessionID;
import quickfix.SessionSettings;

import com.marketcetera.fix.FixSession;
import com.marketcetera.fix.FixSessionFactory;
import com.marketcetera.ors.brokers.Broker;
import com.marketcetera.ors.brokers.BrokerService;
import com.marketcetera.ors.brokers.Brokers;

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
        Validate.notNull(brokerService);
        Validate.notNull(fixSessionFactory);
        for(Brokers brokers : brokersList) {
            SessionSettings settings = brokers.getSettings().getQSettings();
            Dictionary commonSettings = settings.get();
            for(Broker broker : brokers.getBrokers()) {
                try {
                    Map<Object,Object> dictMap = new HashMap<>(commonSettings.toMap());
                    Dictionary dictionary = broker.getSpringBroker().getDescriptor().getQDictionary();
                    dictMap.putAll(dictionary.toMap());
                    Map<String,String> attributes = new HashMap<>();
                    for(Map.Entry<Object,Object> entry : dictMap.entrySet()) {
                        String key = String.valueOf(entry.getKey());
                        String value = String.valueOf(entry.getValue());
                        switch(key) {
                            case Acceptor.SETTING_SOCKET_ACCEPT_ADDRESS:
                            case "SocketAcceptHost" :
                            case Initiator.SETTING_SOCKET_CONNECT_HOST:
                                key = "host";
                                break;
                            case Acceptor.SETTING_SOCKET_ACCEPT_PORT:
                            case Initiator.SETTING_SOCKET_CONNECT_PORT:
                                key = "port";
                                break;
                            case SessionFactory.SETTING_CONNECTION_TYPE:
                                key = "acceptor";
                                value = String.valueOf(value.equals("acceptor") ? true : false);
                                break;
                        }
                        attributes.put(key,
                                       value);
                    }
                    attributes.put("id",
                                   broker.getBrokerID().getValue());
                    attributes.put("name",
                                   broker.getName());
                    attributes.put("enabled",
                                   String.valueOf(true));
                    attributes.put("affinity",
                                   String.valueOf(broker.getSpringBroker().getInstanceAffinity()));
                    SLF4JLoggerProxy.debug(this,
                                           "Creating {} -> {}",
                                           broker,
                                           attributes);
                    FixSession savedSession = brokerService.save(fixSessionFactory.create(attributes));
                    brokerService.enableSession(new SessionID(savedSession.getSessionId()));
                } catch (Exception e) {
                    PlatformServices.handleException(this,
                                                     "Unable to initialize a FIX session for " + broker,
                                                     e);
                }
            }
        }
    }
    /**
     * Get the brokers value.
     *
     * @return a <code>List&lt;Brokers&gt;</code> value
     */
    public List<Brokers> getBrokers()
    {
        return brokersList;
    }
    /**
     * Sets the brokers value.
     *
     * @param inBrokers a <code>List<Brokers></code> value
     */
    public void setBrokers(List<Brokers> inBrokers)
    {
        brokersList.clear();
        if(inBrokers != null) {
            brokersList.addAll(inBrokers);
        }
    }
    /**
     * Get the brokerService value.
     *
     * @return a <code>BrokerService</code> value
     */
    public BrokerService getBrokerService()
    {
        return brokerService;
    }
    /**
     * Sets the brokerService value.
     *
     * @param inBrokerService a <code>BrokerService</code> value
     */
    public void setBrokerService(BrokerService inBrokerService)
    {
        brokerService = inBrokerService;
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
     * creates {@link FixSession} objects
     */
    @Autowired
    private FixSessionFactory fixSessionFactory;
    /**
     * provides access to broker services
     */
    @Autowired
    private BrokerService brokerService;
    /**
     * sessions to add
     */
    private final List<Brokers> brokersList = new ArrayList<>();
}
