package org.marketcetera.brokers.service;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import org.apache.commons.lang.Validate;
import org.marketcetera.brokers.Broker;
import org.marketcetera.brokers.BrokerFactory;
import org.marketcetera.brokers.MessageModifier;
import org.marketcetera.brokers.Selector;
import org.marketcetera.brokers.config.BrokerDescriptor;
import org.marketcetera.brokers.config.BrokersDescriptor;
import org.marketcetera.client.BrokerStatusListener;
import org.marketcetera.client.brokers.BrokerStatus;
import org.marketcetera.core.CoreException;
import org.marketcetera.core.PlatformServices;
import org.marketcetera.fix.FixSession;
import org.marketcetera.fix.FixSessionFactory;
import org.marketcetera.fix.FixSettingsProvider;
import org.marketcetera.fix.FixSettingsProviderFactory;
import org.marketcetera.fix.core.Messages;
import org.marketcetera.quickfix.FIXMessageUtil;
import org.marketcetera.trade.BrokerID;
import org.marketcetera.trade.FIXConverter;
import org.marketcetera.trade.Order;
import org.marketcetera.trade.TradeMessage;
import org.marketcetera.util.log.I18NBoundMessage1P;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Maps;

import quickfix.Message;
import quickfix.SessionFactory;
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
public class BrokerServiceImpl
        implements BrokerService,BrokerStatusListener
{
    /* (non-Javadoc)
     * @see org.marketcetera.brokers.service.BrokerService#getBroker(quickfix.SessionID)
     */
    @Override
    public Broker getBroker(SessionID inSessionId)
    {
        return brokersBySessionId.getIfPresent(inSessionId);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.brokers.service.BrokerService#getBrokers()
     */
    @Override
    public Collection<Broker> getBrokers()
    {
        return Collections.unmodifiableCollection(brokersByBrokerId.asMap().values());
    }
    /* (non-Javadoc)
     * @see org.marketcetera.brokers.service.BrokerService#selectBroker(org.marketcetera.trade.Order)
     */
    @Override
    public Broker selectBroker(Order inOrder)
    {
        Broker broker = null;
        if(inOrder.getBrokerID() != null) {
            broker = brokersByBrokerId.getIfPresent(inOrder.getBrokerID());
        }
        if(broker == null) {
            BrokerID brokerId = brokerSelector.chooseBroker(inOrder);
            if(brokerId != null) {
                broker = brokersByBrokerId.getIfPresent(brokerId);
            }
        }
        // TODO mapped/virtual broker stuff?
        if(broker == null) {
            Messages.NO_BROKER_SELECTED.warn(this,
                                             inOrder);
            throw new CoreException(new I18NBoundMessage1P(Messages.NO_BROKER_SELECTED,
                                                           inOrder));
        } else {
            SLF4JLoggerProxy.debug(this,
                                   "Selected {} for {}",
                                   broker,
                                   inOrder.getBrokerID());
        }
        return broker;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.brokers.service.BrokerService#convertOrder(org.marketcetera.trade.Order, org.marketcetera.brokers.Broker)
     */
    @Override
    public Message convertOrder(Order inOrder,
                                Broker inBroker)
    {
        // verify the broker is available
        BrokerStatus brokerStatus = getBrokerStatus(inBroker.getBrokerId());
        Validate.isTrue(brokerStatus.getLoggedOn(),
                        inBroker.getBrokerId() + " is not available"); // TODO
        // TODO broker algos
        // TODO reprice
        // create the FIX message
        Message message = FIXConverter.toQMessage(inBroker.getFixVersion().getMessageFactory(),
                                                  FIXMessageUtil.getDataDictionary(inBroker.getFixVersion()),
                                                  inOrder);
        // apply modifiers
        for(MessageModifier orderModifier : inBroker.getOrderModifiers()) {
            try {
                orderModifier.modify(inBroker,
                                     message);
                // TODO catch OrderIntercepted
            } catch (Exception e) {
                PlatformServices.handleException(this,
                                                 "Unable to modify order",
                                                 e);
            }
        }
        return message;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.brokers.service.BrokerService#convertResponse(quickfix.Message, org.marketcetera.brokers.Broker)
     */
    @Override
    public TradeMessage convertResponse(Message inMessage,
                                        Broker inBroker)
    {
        throw new UnsupportedOperationException(); // TODO
    }
    /* (non-Javadoc)
     * @see org.marketcetera.brokers.service.BrokerService#getBrokerStatus(org.marketcetera.trade.BrokerID)
     */
    @Override
    public BrokerStatus getBrokerStatus(BrokerID inBrokerId)
    {
        return brokerStatusValues.getIfPresent(inBrokerId);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.BrokerStatusListener#receiveBrokerStatus(org.marketcetera.client.brokers.BrokerStatus)
     */
    @Override
    public void receiveBrokerStatus(BrokerStatus inStatus)
    {
        brokerStatusValues.put(inStatus.getId(),
                               inStatus);
    }
    /**
     * Set the brokers value.
     *
     * @param inBrokerDescriptors a <code>Collection&lt;BrokersDescriptor&gt;</code> value
     */
    public void setBrokers(Collection<BrokersDescriptor> inBrokersDescriptors)
    {
        brokersByBrokerId.invalidateAll();
        brokersBySessionId.invalidateAll();
        FixSettingsProvider fixSettingsProvider = fixSettingsProviderFactory.create();
        for(BrokersDescriptor brokersDescriptor : inBrokersDescriptors) {
            for(BrokerDescriptor brokerDescriptor : brokersDescriptor.getBrokers()) {
                Map<String,String> sessionSettings = Maps.newHashMap();
                // add global default settings
                sessionSettings.putAll(brokersDescriptor.getSessionSettings().getSessionSettings());
                // add/override with local settings
                sessionSettings.putAll(brokerDescriptor.getSessionSettings().getSessionSettings());
                FixSession fixSession = fixSessionFactory.create();
                fixSession.setAffinity(brokerDescriptor.getAffinity());
                fixSession.setBrokerId(brokerDescriptor.getId().getValue());
                fixSession.setDescription(brokerDescriptor.getDescription());
                String connectionType = sessionSettings.get(SessionFactory.SETTING_CONNECTION_TYPE);
                fixSession.setIsAcceptor(SessionFactory.ACCEPTOR_CONNECTION_TYPE.equals(connectionType));
                fixSession.setIsEnabled(true);
                if(fixSession.isAcceptor()) {
                    fixSession.setHost(fixSettingsProvider.getAcceptorHost());
                    fixSession.setPort(fixSettingsProvider.getAcceptorPort());
                } else {
                    fixSession.setHost(brokerDescriptor.getHost());
                    fixSession.setPort(brokerDescriptor.getPort());
                }
                fixSession.setName(brokerDescriptor.getName());
                SessionID sessionId = new SessionID(brokerDescriptor.getSessionSettings().getSessionSettings().get(SessionSettings.BEGINSTRING),
                                                    brokerDescriptor.getSessionSettings().getSessionSettings().get(SessionSettings.SENDERCOMPID),
                                                    brokerDescriptor.getSessionSettings().getSessionSettings().get(SessionSettings.TARGETCOMPID));
                fixSession.setSessionId(sessionId.toString());
                fixSession.getSessionSettings().putAll(sessionSettings);
                Broker broker = brokerFactory.create(fixSession,
                                                     brokerDescriptor.getOrderModifiers(),
                                                     brokerDescriptor.getResponseModifiers(),
                                                     brokerDescriptor.getBrokerAlgos());
                brokersByBrokerId.put(brokerDescriptor.getId(),
                                      broker);
                brokersBySessionId.put(sessionId,
                                       broker);
            }
        }
        SLF4JLoggerProxy.debug(this,
                               "Created brokers: {}",
                               brokersByBrokerId.asMap());
    }
    /**
     * optional broker selector
     */
    @Autowired(required=false)
    private Selector brokerSelector;
    /**
     * provides FIX settings
     */
    @Autowired
    private FixSettingsProviderFactory fixSettingsProviderFactory;
    /**
     * creates {@link Broker} objects
     */
    @Autowired
    private BrokerFactory brokerFactory;
    /**
     * creates {@link FixSession} objects
     */
    @Autowired
    private FixSessionFactory fixSessionFactory;
    /**
     * stores brokers keyed by {@link BrokerID}
     */
    private final Cache<BrokerID,Broker> brokersByBrokerId = CacheBuilder.newBuilder().build();
    /**
     * stores brokers keyed by {@link SessionID}
     */
    private final Cache<SessionID,Broker> brokersBySessionId = CacheBuilder.newBuilder().build();
    /**
     * caches broker status values
     */
    private final Cache<BrokerID,BrokerStatus> brokerStatusValues = CacheBuilder.newBuilder().build();
}
