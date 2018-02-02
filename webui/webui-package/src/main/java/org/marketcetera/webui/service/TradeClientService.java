package org.marketcetera.webui.service;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.marketcetera.admin.User;
import org.marketcetera.admin.UserFactory;
import org.marketcetera.brokers.BrokerStatus;
import org.marketcetera.brokers.BrokerStatusListener;
import org.marketcetera.brokers.BrokersStatus;
import org.marketcetera.fix.FixSessionFactory;
import org.marketcetera.persist.CollectionPageResponse;
import org.marketcetera.persist.PageRequest;
import org.marketcetera.trade.BrokerID;
import org.marketcetera.trade.MutableOrderSummary;
import org.marketcetera.trade.MutableOrderSummaryFactory;
import org.marketcetera.trade.MutableReport;
import org.marketcetera.trade.MutableReportFactory;
import org.marketcetera.trade.OrderSummary;
import org.marketcetera.trade.TradeMessage;
import org.marketcetera.trade.TradeMessageListener;
import org.marketcetera.trade.client.TradeClient;
import org.marketcetera.trade.client.TradeClientFactory;
import org.marketcetera.trade.client.TradeClientParameters;
import org.marketcetera.trading.rpc.TradeRpcClientParametersImpl;
import org.marketcetera.trading.rpc.TradeRpcUtil;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Lists;
import com.google.common.eventbus.EventBus;

/* $License$ */

/**
 * Provides services with the {@link TradeClient}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class TradeClientService
        implements ConnectableService
{
    /* (non-Javadoc)
     * @see org.marketcetera.webui.service.ConnectableService#connect(java.lang.String, java.lang.String, java.lang.String, int)
     */
    @Override
    public boolean connect(String inUsername,
                           String inPassword,
                           String inHostname,
                           int inPort)
            throws Exception
    {
        if(tradeClient != null) {
            try {
                tradeClient.stop();
            } catch (Exception e) {
                SLF4JLoggerProxy.warn(this,
                                      "Unable to stop existing trade client for {}: {}",
                                      inUsername,
                                      ExceptionUtils.getRootCauseMessage(e));
            } finally {
                tradeClient = null;
            }
        }
        SLF4JLoggerProxy.debug(this,
                               "Creating trade client for {} to {}:{}",
                               inUsername,
                               inHostname,
                               inPort);
        TradeRpcClientParametersImpl params = new TradeRpcClientParametersImpl();
        params.setHostname(inHostname);
        params.setPort(inPort);
        params.setUsername(inUsername);
        params.setPassword(inPassword);
        tradeClient = tradeClientFactory.create(params);
        tradeClient.start();
        if(tradeClient.isRunning()) {
            onTradeConnect();
        }
        return tradeClient.isRunning();
    }
    /**
     * Validate and start the object.
     */
    @PostConstruct
    public void start()
    {
        TradeRpcUtil.setFixSessionFactory(fixSessionFactory);
        TradeRpcUtil.setOrderSummaryFactory(orderSummaryFactory);
        TradeRpcUtil.setUserFactory(userFactory);
        TradeRpcUtil.setReportFactory(reportFactory);
    }
    /**
     *
     *
     * @param inOffset
     * @param inLimit
     * @param inSortOrder 
     * @return
     */
    public Collection<OrderSummary> findAllOpenOrders(int inOffset,
                                                      int inLimit,
                                                      Map<String,Boolean> inSortOrder)
    {
        SLF4JLoggerProxy.trace(this,
                               "Find all open orders: {} {} {}",
                               inOffset,
                               inLimit,
                               inSortOrder);
        // TODO sort order
        CollectionPageResponse<? extends OrderSummary> result = tradeClient.getOpenOrders(new PageRequest(inOffset,
                                                                                                          inLimit));
        return Lists.newArrayList(result.getElements());
    }
    /**
     *
     *
     * @return
     */
    public int countAllOpenOrders()
    {
        SLF4JLoggerProxy.trace(this,
                               "Count all open orders");
        // TODO make this a separate call!
        return tradeClient.getOpenOrders().size();
    }
    /**
     * Get the cached broker status for all brokers.
     *
     * @return a <code>Collection&lt;BrokerStatus&gt;</code> value
     */
    public Collection<BrokerStatus> getAllCachedBrokerStatus()
    {
        return Collections.unmodifiableCollection(brokerStatusCache.asMap().values());
    }
    /**
     * Get the cached broker status of the given broker, if available.
     *
     * @param inBrokerId a <code>BrokerID</code> value
     * @return an <code>Optional&lt;BrokerStatus&gt;</code> value
     */
    public Optional<BrokerStatus> getCachedBrokerStatus(BrokerID inBrokerId)
    {
        return Optional.of(brokerStatusCache.getIfPresent(inBrokerId));
    }
    /**
     * Get the tradeClient value.
     *
     * @return a <code>TradeClient</code> value
     */
    public TradeClient getTradeClient()
    {
        if(tradeClient == null || !tradeClient.isRunning()) {
            throw new IllegalStateException("Trade client is not running");
        }
        return tradeClient;
    }
    /**
     * Execute when the trade client connects.
     */
    private void onTradeConnect()
    {
        BrokersStatus brokerStatusSnapshot= tradeClient.getBrokersStatus();
        for(BrokerStatus brokerStatus : brokerStatusSnapshot.getBrokers()) {
            brokerStatusCache.put(brokerStatus.getId(),
                                  brokerStatus);
        }
        brokerStatusListener = new BrokerStatusListener() {
            @Override
            public void receiveBrokerStatus(BrokerStatus inStatus)
            {
                SLF4JLoggerProxy.debug(TradeClientService.this,
                                       "Received {}",
                                       inStatus);
                brokerStatusCache.put(inStatus.getId(),
                                      inStatus);
                eventBus.post(inStatus);
            }
        };
        tradeMessageListener = new TradeMessageListener() {
            @Override
            public void receiveTradeMessage(TradeMessage inTradeMessage)
            {
                SLF4JLoggerProxy.debug(this,
                                       "Received {}",
                                       inTradeMessage);
                eventBus.post(inTradeMessage);
            }
        };
        tradeClient.addBrokerStatusListener(brokerStatusListener);
        tradeClient.addTradeMessageListener(tradeMessageListener);
    }
    /**
     * listeners for broker status messages
     */
    private BrokerStatusListener brokerStatusListener;
    /**
     * listens for trade messages
     */
    private TradeMessageListener tradeMessageListener;
    /**
     * creates {@link MutableOrderSummary} objects
     */
    @Autowired
    private MutableOrderSummaryFactory orderSummaryFactory;
    /**
     * creates {@link FixSessionFactory} objects
     */
    @Autowired
    private FixSessionFactory fixSessionFactory;
    /**
     * creates {@link User} objects
     */
    @Autowired
    private UserFactory userFactory;
    /**
     * creates {@link MutableReport} objects
     */
    @Autowired
    private MutableReportFactory reportFactory;
    /**
     * provides access to the event bus
     */
    @Autowired
    private EventBus eventBus;
    /**
     * client object used to communicate with the server
     */
    private TradeClient tradeClient;
    /**
     * provides access to trade client services
     */
    @Autowired
    private TradeClientFactory<TradeClientParameters> tradeClientFactory;
    /**
     * caches broker status values
     */
    private final Cache<BrokerID,BrokerStatus> brokerStatusCache = CacheBuilder.newBuilder().build();
}
