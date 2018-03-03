package org.marketcetera.marketdata.core.manager;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.marketcetera.core.CoreException;
import org.marketcetera.core.publisher.ISubscriber;
import org.marketcetera.event.Event;
import org.marketcetera.event.HasInstrument;
import org.marketcetera.event.MarketDataEvent;
import org.marketcetera.marketdata.Content;
import org.marketcetera.marketdata.MarketDataRequest;
import org.marketcetera.marketdata.core.Messages;
import org.marketcetera.marketdata.core.cache.MarketDataCache;
import org.marketcetera.marketdata.core.cache.MarketDataCacheImpl;
import org.marketcetera.module.AbstractDataReemitterModule;
import org.marketcetera.module.DataFlowID;
import org.marketcetera.module.DataFlowRequester;
import org.marketcetera.module.DataFlowSupport;
import org.marketcetera.module.DataReceiver;
import org.marketcetera.module.DataRequest;
import org.marketcetera.module.ModuleException;
import org.marketcetera.module.ModuleInfo;
import org.marketcetera.module.ModuleManager;
import org.marketcetera.module.ModuleURN;
import org.marketcetera.module.ReceiveDataException;
import org.marketcetera.module.StopDataFlowException;
import org.marketcetera.trade.Instrument;
import org.marketcetera.util.log.I18NBoundMessage1P;
import org.marketcetera.util.log.I18NBoundMessage2P;
import org.marketcetera.util.log.SLF4JLoggerProxy;

import com.google.common.base.Optional;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

/* $License$ */

/**
 * Provides a common module for market data flows.
 *
 * <p>
 * Module Features
 * <table>
 * <tr><th>Capabilities</th><td>Data Requester</td></tr>
 * <tr><th>Stops data flows</th><td>Yes</td></tr>
 * <tr><th>Start Operation</th><td>None</td></tr>
 * <tr><th>Stop Operation</th><td>None</td></tr>
 * <tr><th>Management Interface</th><td>&nbsp;</td></tr>
 * <tr><th>MX Notification</th><td>&nbsp;</td></tr>
 * </table>
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class MarketDataManagerModule
        extends AbstractDataReemitterModule
        implements DataReceiver,DataFlowRequester
{
    /* (non-Javadoc)
     * @see org.marketcetera.module.DataFlowRequester#setFlowSupport(org.marketcetera.module.DataFlowSupport)
     */
    @Override
    public void setFlowSupport(DataFlowSupport inSupport)
    {
        dataFlowSupport = inSupport;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.module.DataReceiver#receiveData(org.marketcetera.module.DataFlowID, java.lang.Object)
     */
    @Override
    public void receiveData(DataFlowID inFlowId,
                            Object inData)
            throws ReceiveDataException
    {
        RequestWrapper subscriber = subscribersByDataFlowId.get(inFlowId);
        long timestamp = System.currentTimeMillis();
        long timeout = timestamp+subscriberTimeout;
        try {
            while(subscriber == null && System.currentTimeMillis() < timeout) {
                synchronized(subscribersByDataFlowId) {
                    subscribersByDataFlowId.wait(100);
                }
                subscriber = subscribersByDataFlowId.get(inFlowId);
            }
            if(subscriber == null) {
                throw new StopDataFlowException(new I18NBoundMessage1P(Messages.NO_SUBSCRIBER,
                                                                       inFlowId));
            }
            try {
                Instrument eventInstrument = null;
                Event event = null;
                if(inData instanceof HasInstrument) {
                    eventInstrument = ((HasInstrument)inData).getInstrument();
                }
                if(inData instanceof Event) {
                    event = (Event)inData;
                    if(inData instanceof MarketDataEvent) {
                        ((MarketDataEvent)event).setRequestId(subscriber.request.getRequestId());
                    }
                }
                if(eventInstrument == null || event == null) {
                    Messages.NO_INSTRUMENT.warn(this,
                                                inFlowId,
                                                inData);
                } else {
                    Collection<Content> requestContent = contentByDataFlowId.get(inFlowId);
                    if(requestContent != null) {
                        for(Content content : requestContent) {
                            if(content.isRelevantTo(event.getClass())) {
                                cachedMarketdata.update(content,
                                                        event);
                            }
                        }
                    }
                    if(subscriber.isInteresting(event)) {
                        subscriber.publishTo(event);
                    }
                }
            } catch (Exception e) {
                SLF4JLoggerProxy.warn(this,
                                      e,
                                      "An error occurred processing {} for {}: {}",
                                      inData,
                                      inFlowId,
                                      ExceptionUtils.getRootCauseMessage(e));
            } finally {
                super.receiveData(inFlowId,
                                  inData);
            }
        } catch (StopDataFlowException e) {
            throw e;
        } catch (Exception e) {
            throw new StopDataFlowException(new I18NBoundMessage2P(Messages.PUBLISHING_ERROR,
                                                                   inFlowId,
                                                                   ExceptionUtils.getRootCauseMessage(e)));
        }
    }
    /**
     * Get the subscriberTimeout value.
     *
     * @return a <code>long</code> value
     */
    public long getSubscriberTimeout()
    {
        return subscriberTimeout;
    }
    /**
     * Sets the subscriberTimeout value.
     *
     * @param a <code>long</code> value
     */
    public void setSubscriberTimeout(long inSubscriberTimeout)
    {
        subscriberTimeout = inSubscriberTimeout;
    }
    /**
     * Cancel the market data request with the given request id.
     *
     * @param inRequestId a <code>long</code> value
     */
    public void cancelMarketDataRequest(long inRequestId)
    {
        Collection<DataFlowID> dataFlows = dataFlowsByRequestId.removeAll(inRequestId);
        if(dataFlows != null) {
            for(DataFlowID dataFlowId : dataFlows) {
                try {
                    contentByDataFlowId.removeAll(dataFlowId);
                    subscribersByDataFlowId.remove(dataFlowId);
                    SLF4JLoggerProxy.debug(this,
                                           "Canceling data flow {} owned by {}",
                                           dataFlowId,
                                           inRequestId);
                    dataFlowSupport.cancel(dataFlowId);
                } catch (Exception e) {
                    SLF4JLoggerProxy.warn(this,
                                          "Unable to cancel data flow {}: {}",
                                          inRequestId,
                                          ExceptionUtils.getRootCauseMessage(e));
                }
            }
        }
    }
    /**
     * Request market data and delivers the market data to the given subscriber.
     *
     * @param inRequest a <code>MarketDataRequest</code> value
     * @param inSubscriber an <code>ISubscriber</code> value
     * @return a <code>long</code> value
     */
    public long requestMarketData(MarketDataRequest inRequest,
                                  ISubscriber inSubscriber)
    {
        try {
            String provider = inRequest.getProvider();
            SLF4JLoggerProxy.debug(this,
                                   "Requesting market data: {} from {}",
                                   inRequest,
                                   provider);
            if(provider == null) {
                provider = defaultProvider;
            }
            if(provider == null) {
                SLF4JLoggerProxy.debug(this,
                                       "No provider requested, issuing request to all providers");
                for(ModuleURN providerUrn : ModuleManager.getInstance().getProviders()) {
                    String providerType = providerUrn.providerType();
                    String providerName = providerUrn.providerName();
                    if(providerType.equals("mdata") && !providerName.equals(MarketDataManagerModuleFactory.PROVIDER_NAME)) {
                        for(ModuleURN instanceUrn : ModuleManager.getInstance().getModuleInstances(providerUrn)) {
                            try {
                                doDataRequest(inRequest,
                                              instanceUrn,
                                              inSubscriber);
                            } catch (Exception e) {
                                SLF4JLoggerProxy.warn(this,
                                                      "Unable to request market data from {}: {}",
                                                      instanceUrn,
                                                      ExceptionUtils.getRootCauseMessage(e));
                            }
                        }
                    }
                }
            } else {
                ModuleURN sourceUrn = getInstanceUrn(provider);
                doDataRequest(inRequest,
                              sourceUrn,
                              inSubscriber);
            }
            return inRequest.getRequestId();
        } catch (Exception e) {
            if(SLF4JLoggerProxy.isDebugEnabled(this)) {
                SLF4JLoggerProxy.warn(this,
                                      e,
                                      "Unable to request market data {}: {}",
                                      inRequest,
                                      ExceptionUtils.getRootCauseMessage(e));
            } else {
                SLF4JLoggerProxy.warn(this,
                                      "Unable to request market data {}: {}",
                                      inRequest,
                                      ExceptionUtils.getRootCauseMessage(e));
            }
            if(e instanceof RuntimeException) {
                throw (RuntimeException)e;
            } else {
                throw new CoreException(e);
            }
        }
    }
    /**
     * Get a market data snapshot with the given attributes.
     *
     * @param inInstrument an <code>Instrument</code> value
     * @param inContent a <code>Content</code> value
     * @param inExchange a <code>String</code> value or <code>null</code>
     * @param inProvider a <code>String</code> value or <code>null</code>
     * @return an <code>Optional&lt;Event&gt;</code> value
     */
    public Optional<Event> requestMarketDataSnapshot(Instrument inInstrument,
                                                     Content inContent,
                                                     String inExchange,
                                                     String inProvider)
    {
        return cachedMarketdata.getSnapshot(inInstrument,
                                            inContent,
                                            inExchange);
    }
    /**
     * Get the singleton instance.
     *
     * @return a <code>MarketDataManagerModule</code> value
     */
    public static MarketDataManagerModule getInstance()
    {
        return instance;
    }
    /**
     * Get the defaultProvider value.
     *
     * @return a <code>String</code> value
     */
    public String getDefaultProvider()
    {
        return defaultProvider;
    }
    /**
     * Sets the defaultProvider value.
     *
     * @param a <code>String</code> value
     */
    public void setDefaultProvider(String inDefaultProvider)
    {
        defaultProvider = inDefaultProvider;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.module.Module#preStart()
     */
    @Override
    protected void preStart()
            throws ModuleException
    {
    }
    /* (non-Javadoc)
     * @see org.marketcetera.module.Module#preStop()
     */
    @Override
    protected void preStop()
            throws ModuleException
    {
    }
    /**
     * Create a new MarketDataManagerModule instance.
     *
     * @param inURN a <code>ModuleURN</code> value
     */
    MarketDataManagerModule(ModuleURN inURN)
    {
        super(inURN,
              true);
        instance = this;
        cachedMarketdata = new MarketDataCacheImpl();
    }
    /**
     * Get the instance URN for the given market data provider name.
     *
     * @param inProviderName a <code>String</code> value
     * @return a <code>ModuleURN</code> value
     */
    private ModuleURN getInstanceUrn(String inProviderName)
    {
        ModuleURN instanceUrn = instanceUrnsByProviderName.get(inProviderName);
        if(instanceUrn == null) {
            instanceUrn = new ModuleURN("metc:mdata:" + inProviderName+":single");
            instanceUrnsByProviderName.put(inProviderName,
                                           instanceUrn);
        }
        return instanceUrn;
    }
    /**
     * Execute the given market data request.
     *
     * @param inMarketDataRequest a <code>MarketDataRequest</code> value
     * @param inSourceUrn a <code>ModuleURN</code> value
     * @param inSubscriber an <code>ISubscriber</code> value
     */
    private void doDataRequest(MarketDataRequest inMarketDataRequest,
                               ModuleURN inSourceUrn,
                               ISubscriber inSubscriber)
    {
        startProviderIfNecessary(inSourceUrn);
        DataRequest sourceRequest = new DataRequest(inSourceUrn,
                                                    inMarketDataRequest);
        DataRequest targetRequest = new DataRequest(getURN());
        DataFlowID dataFlowId = dataFlowSupport.createDataFlow(new DataRequest[] { sourceRequest, targetRequest },
                                                               false);
        SLF4JLoggerProxy.debug(this,
                               "Submitting {} to {}: {}",
                               inMarketDataRequest,
                               inSourceUrn,
                               dataFlowId);
        dataFlowsByRequestId.put(inMarketDataRequest.getRequestId(), 
                                 dataFlowId);
        subscribersByDataFlowId.put(dataFlowId,
                                    new RequestWrapper(inMarketDataRequest,
                                                       inSubscriber));
        contentByDataFlowId.putAll(dataFlowId,
                                   inMarketDataRequest.getContent());
        synchronized(dataFlowsByRequestId) {
            dataFlowsByRequestId.notifyAll();
        }
        synchronized(subscribersByDataFlowId) {
            subscribersByDataFlowId.notifyAll();
        }
    }
    /**
     * Start the given provider if necessary.
     *
     * @param inProviderUrn a <code>ModuleURN</code> value
     */
    private void startProviderIfNecessary(ModuleURN inProviderUrn)
    {
        ModuleManager moduleManager = ModuleManager.getInstance();
        ModuleInfo moduleInfo = moduleManager.getModuleInfo(inProviderUrn);
        SLF4JLoggerProxy.debug(this,
                               "{} is {}",
                               inProviderUrn,
                               moduleInfo);
        if(!moduleInfo.getState().isStarted()) {
            moduleManager.start(inProviderUrn);
        }
    }
    /**
     * Contains the information for a market data request.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    private static class RequestWrapper
            implements ISubscriber
    {
        /* (non-Javadoc)
         * @see org.marketcetera.core.publisher.ISubscriber#isInteresting(java.lang.Object)
         */
        @Override
        public boolean isInteresting(Object inData)
        {
            return subscriber.isInteresting(inData);
        }
        /* (non-Javadoc)
         * @see org.marketcetera.core.publisher.ISubscriber#publishTo(java.lang.Object)
         */
        @Override
        public void publishTo(Object inData)
        {
            subscriber.publishTo(inData);
        }
        /**
         * Create a new RequestWrapper instance.
         *
         * @param inRequest a <code>MarketDataRequest</code> value
         * @param inSubscriber an <code>ISubscriber</code> value
         */
        private RequestWrapper(MarketDataRequest inRequest,
                               ISubscriber inSubscriber)
        {
            request = inRequest;
            subscriber = inSubscriber;
        }
        /**
         * request value
         */
        private final MarketDataRequest request;
        /**
         * subscriber value
         */
        private final ISubscriber subscriber;
    }
    /**
     * default market data provider
     */
    private String defaultProvider = "exsim";
    /**
     * provides access to data flow services
     */
    private DataFlowSupport dataFlowSupport;
    /**
     * singleton instance
     */
    private static MarketDataManagerModule instance;
    /**
     * time to wait for a subscriber to become available before timing out
     */
    private long subscriberTimeout = 5000;
    /**
     * holds active subscribers by data flow id
     */
    private final Map<DataFlowID,RequestWrapper> subscribersByDataFlowId = new HashMap<>();
    /**
     * holds active data flows by request id
     */
    private final Multimap<Long,DataFlowID> dataFlowsByRequestId = HashMultimap.create();
    /**
     * holds content types by data flow id
     */
    private final Multimap<DataFlowID,Content> contentByDataFlowId = HashMultimap.create();
    /**
     * holds market data provider instances by provider name
     */
    private final Map<String,ModuleURN> instanceUrnsByProviderName = new HashMap<>();
    /**
     * holds cached market data
     */
    private MarketDataCache cachedMarketdata;
}
