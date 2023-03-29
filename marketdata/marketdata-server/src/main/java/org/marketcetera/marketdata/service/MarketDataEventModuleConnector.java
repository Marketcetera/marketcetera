package org.marketcetera.marketdata.service;

import java.util.Set;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.marketcetera.event.Event;
import org.marketcetera.eventbus.EventBusService;
import org.marketcetera.marketdata.FeedStatus;
import org.marketcetera.marketdata.FeedStatusRequest;
import org.marketcetera.marketdata.MarketDataRequest;
import org.marketcetera.marketdata.MarketDataStatus;
import org.marketcetera.marketdata.NoMarketDataProvidersAvailable;
import org.marketcetera.marketdata.cache.MarketDataCacheModuleFactory;
import org.marketcetera.marketdata.core.manager.MarketDataManagerModuleFactory;
import org.marketcetera.marketdata.event.CancelMarketDataRequestEvent;
import org.marketcetera.marketdata.event.MarketDataRequestEvent;
import org.marketcetera.marketdata.event.SimpleGeneratedMarketDataEvent;
import org.marketcetera.module.AutowiredModule;
import org.marketcetera.module.DataFlowID;
import org.marketcetera.module.DataReceiver;
import org.marketcetera.module.DataRequest;
import org.marketcetera.module.Module;
import org.marketcetera.module.ModuleException;
import org.marketcetera.module.ModuleManager;
import org.marketcetera.module.ModuleURN;
import org.marketcetera.module.ReceiveDataException;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Sets;
import com.google.common.eventbus.Subscribe;

/* $License$ */

/**
 * Provides an interface between the {@link EventBusService} and the {@link ModuleManager}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@AutowiredModule
public class MarketDataEventModuleConnector
        extends Module
        implements DataReceiver
{
    /**
     * Receive market data request events from the {@link EventBusService}.
     *
     * @param inEvent a <code>MarketDataRequest</code> value
     */
    @Subscribe
    public void onMarketDataRequestEvent(MarketDataRequestEvent inEvent)
    {
        SLF4JLoggerProxy.debug(this,
                               "Received {}",
                               inEvent);
        String provider = inEvent.getMarketDataRequestProvider().orElse(null);
        String requestId = inEvent.getMarketDataRequestId();
        if(provider == null) {
            SLF4JLoggerProxy.debug(this,
                                   "No provider requested, issuing request to all providers");
            boolean atLeastOne = false;
            for(ModuleURN providerUrn : ModuleManager.getInstance().getProviders()) {
                String providerType = providerUrn.providerType();
                String providerName = providerUrn.providerName();
                if(providerType.equals("mdata") && !disallowedProviders.contains(providerName)) {
                    for(ModuleURN instanceUrn : ModuleManager.getInstance().getModuleInstances(providerUrn)) {
                        try {
                            doDataRequest(inEvent.getMarketDataRequest(),
                                          instanceUrn,
                                          requestId);
                            atLeastOne = true;
                        } catch (Exception e) {
                            SLF4JLoggerProxy.warn(this,
                                                  e,
                                                  "Unable to request market data from {}: {}",
                                                  instanceUrn,
                                                  ExceptionUtils.getRootCauseMessage(e));
                        }
                    }
                }
            }
            if(!atLeastOne) {
                throw new NoMarketDataProvidersAvailable(new IllegalArgumentException("No market data providers available for request " + requestId));
            }
        } else {
            ModuleURN sourceUrn = getInstanceUrn(provider);
            doDataRequest(inEvent.getMarketDataRequest(),
                          sourceUrn,
                          requestId);
        }
    }
    /**
     * Cancel the given market data request.
     *
     * @param inEvent a <code>CanceclMarketDataRequestEvent</code> value
     */
    @Subscribe
    public void onCancel(CancelMarketDataRequestEvent inEvent)
    {
        SLF4JLoggerProxy.debug(this,
                               "Received {}",
                               inEvent);
        String requestId = inEvent.getMarketDataRequestId();
        Set<DataFlowID> dataFlows = dataFlowsByRequestId.getUnchecked(requestId);
        SLF4JLoggerProxy.debug(this,
                               "Canceling data flows {} for request id {}",
                               dataFlows,
                               requestId);
        for(DataFlowID dataFlowId : dataFlows) {
            ModuleManager.getInstance().cancel(dataFlowId);
        }
        dataFlowsByRequestId.invalidate(requestId);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.module.DataReceiver#receiveData(org.marketcetera.module.DataFlowID, java.lang.Object)
     */
    @Override
    public void receiveData(DataFlowID inFlowId,
                            Object inData)
            throws ReceiveDataException
    {
        SLF4JLoggerProxy.warn(this,
                              "Received {} for data flow id: {}",
                              inData,
                              inFlowId);
        if(inData instanceof Event) {
            String marketDataRequestId = requestsByDataFlowId.getIfPresent(inFlowId);
            if(marketDataRequestId == null) {
                SLF4JLoggerProxy.warn(this,
                                      "Received event: {} for unknown data flow id: {}",
                                      inData,
                                      inFlowId);
                return;
            }
            Event event = (Event)inData;
            eventBusService.post(new SimpleGeneratedMarketDataEvent(marketDataRequestId,
                                                                    event));
        } else if(inData instanceof MarketDataStatus) {
            MarketDataStatus marketDataStatus = (MarketDataStatus)inData;
            marketDataService.reportMarketDataStatus(marketDataStatus);
        } else if(inData instanceof FeedStatus) {
            final FeedStatus feedStatus = (FeedStatus)inData;
            final String provider = requestsByDataFlowId.getIfPresent(inFlowId);
            marketDataService.reportMarketDataStatus(new MarketDataStatus() {
                @Override
                public FeedStatus getFeedStatus()
                {
                    return feedStatus;
                }
                @Override
                public String getProvider()
                {
                    return provider;
                }}
            );
        } else {
            SLF4JLoggerProxy.warn(this,
                                  "Received unexpected data: {}",
                                  inData);
        }
    }
    /**
     * Create a new MarketDataEventModuleConnector instance.
     *
     * @param inUrn a <code>ModuleURN</code> value
     */
    protected MarketDataEventModuleConnector(ModuleURN inUrn)
    {
        super(inUrn,
              true);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.module.Module#preStart()
     */
    @Override
    protected void preStart()
            throws ModuleException
    {
        eventBusService.register(this);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.module.Module#preStop()
     */
    @Override
    protected void preStop()
            throws ModuleException
    {
        eventBusService.unregister(this);
    }
    /**
     * Get the instance URN for the given market data provider name.
     *
     * @param inProviderName a <code>String</code> value
     * @return a <code>ModuleURN</code> value
     */
    private ModuleURN getInstanceUrn(String inProviderName)
    {
        ModuleURN instanceUrn = instanceUrnsByProviderName.getIfPresent(inProviderName);
        if(instanceUrn == null) {
            // this will be our guess in case we don't find something
            instanceUrn = new ModuleURN("metc:mdata:" + inProviderName+":single");
            for(ModuleURN moduleUrn : ModuleManager.getInstance().getProviders()) {
                String providerType = moduleUrn.providerType();
                if(providerType.equals("mdata") && moduleUrn.providerName().equals(inProviderName)) {
                    instanceUrn = new ModuleURN(moduleUrn,
                                                "single");
                    instanceUrnsByProviderName.put(inProviderName,
                                                   instanceUrn);
                    break;
                }
            }
        }
        return instanceUrn;
    }
    /**
     * Execute the given market data request.
     *
     * @param inMarketDataRequest a <code>MarketDataRequest</code> value
     * @param inSourceUrn a <code>ModuleURN</code> value
     * @param inRequestId a <code>long</code> value
     */
    private void doDataRequest(MarketDataRequest inMarketDataRequest,
                               ModuleURN inSourceUrn,
                               String inRequestId)
    {
        doStatusRequest(inSourceUrn,
                        inRequestId);
        DataRequest sourceRequest = new DataRequest(inSourceUrn,
                                                    inMarketDataRequest);
        ModuleManager.startModulesIfNecessary(ModuleManager.getInstance(),
                                              MarketDataCacheModuleFactory.INSTANCE_URN,
                                              inSourceUrn);
        DataRequest cacheRequest = new DataRequest(MarketDataCacheModuleFactory.INSTANCE_URN);
        DataRequest targetRequest = new DataRequest(MarketDataEventModuleConnectorFactory.INSTANCE_URN);
        DataFlowID dataFlowId = ModuleManager.getInstance().createDataFlow(new DataRequest[] { sourceRequest,cacheRequest,targetRequest });
        requestsByDataFlowId.put(dataFlowId,
                                 inRequestId);
        Set<DataFlowID> dataFlows = dataFlowsByRequestId.getUnchecked(inRequestId);
        dataFlows.add(dataFlowId);
        SLF4JLoggerProxy.debug(this,
                               "Submitting {} to {}: {}",
                               inMarketDataRequest,
                               inSourceUrn,
                               dataFlowId);
    }
    /**
     * Execute a market data status request.
     *
     * @param inSourceUrn a <code>ModuleURN</code> value
     * @param inRequestId a <code>String</code> value
     */
    private void doStatusRequest(ModuleURN inSourceUrn,
                                 String inRequestId)
    {
        DataRequest sourceRequest = new DataRequest(inSourceUrn,
                                                    new FeedStatusRequest());
        ModuleManager.startModulesIfNecessary(ModuleManager.getInstance(),
                                              inSourceUrn);
        DataRequest targetRequest = new DataRequest(MarketDataEventModuleConnectorFactory.INSTANCE_URN);
        DataFlowID dataFlowId = ModuleManager.getInstance().createDataFlow(new DataRequest[] { sourceRequest,targetRequest });
        requestsByDataFlowId.put(dataFlowId,
                                 inSourceUrn.instanceName());
        Set<DataFlowID> dataFlows = dataFlowsByRequestId.getUnchecked(inRequestId);
        dataFlows.add(dataFlowId);
        SLF4JLoggerProxy.debug(this,
                               "Submitting feed status request to {}: {}",
                               inSourceUrn,
                               dataFlowId);
    }
    /**
     * stores data flows tied to a given request id
     */
    private final LoadingCache<String,Set<DataFlowID>> dataFlowsByRequestId = CacheBuilder.newBuilder().build(new CacheLoader<String,Set<DataFlowID>>() {
        @Override
        public Set<DataFlowID> load(String inKey)
                throws Exception
        {
            return Sets.newHashSet();
        }}
    );
    /**
     * provides market data services
     */
    @Autowired
    private MarketDataService marketDataService;
    /**
     * provides access to event bus services
     */
    @Autowired
    private EventBusService eventBusService;
    /**
     * holds market data provider instances by provider name
     */
    private final Cache<String,ModuleURN> instanceUrnsByProviderName = CacheBuilder.newBuilder().build();
    /**
     * request data by request id
     */
    private final Cache<DataFlowID,String> requestsByDataFlowId = CacheBuilder.newBuilder().build();
    /**
     * providers not to ask market data of
     */
    private final Set<String> disallowedProviders = Sets.newHashSet(MarketDataManagerModuleFactory.PROVIDER_NAME,MarketDataEventModuleConnectorFactory.IDENTIFIER,MarketDataCacheModuleFactory.PROVIDER_NAME);
}
