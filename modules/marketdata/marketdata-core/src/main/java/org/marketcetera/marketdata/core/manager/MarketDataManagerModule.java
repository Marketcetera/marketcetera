package org.marketcetera.marketdata.core.manager;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.marketcetera.core.publisher.ISubscriber;
import org.marketcetera.event.Event;
import org.marketcetera.event.HasInstrument;
import org.marketcetera.event.MarketDataEvent;
import org.marketcetera.marketdata.Content;
import org.marketcetera.marketdata.MarketDataRequest;
import org.marketcetera.marketdata.core.Messages;
import org.marketcetera.marketdata.core.provider.MarketdataCacheElement;
import org.marketcetera.module.AbstractDataReemitterModule;
import org.marketcetera.module.DataFlowID;
import org.marketcetera.module.DataFlowRequester;
import org.marketcetera.module.DataFlowSupport;
import org.marketcetera.module.DataReceiver;
import org.marketcetera.module.DataRequest;
import org.marketcetera.module.ModuleException;
import org.marketcetera.module.ModuleManager;
import org.marketcetera.module.ModuleURN;
import org.marketcetera.module.ReceiveDataException;
import org.marketcetera.module.StopDataFlowException;
import org.marketcetera.trade.Instrument;
import org.marketcetera.util.log.I18NBoundMessage1P;
import org.marketcetera.util.log.I18NBoundMessage2P;
import org.marketcetera.util.log.SLF4JLoggerProxy;

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
        try {
            while(subscriber == null && System.currentTimeMillis() < (timestamp+subscriberTimeout)) {
                synchronized(subscribersByDataFlowId) {
                    subscribersByDataFlowId.wait();
                }
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
                    MarketdataCacheElement marketdataCache = cachedMarketdata.get(eventInstrument);
                    if(marketdataCache == null) {
                        marketdataCache = new MarketdataCacheElement(eventInstrument);
                        cachedMarketdata.put(eventInstrument,
                                             marketdataCache);
                    }
                    Collection<Content> requestContent = contentByDataFlowId.get(inFlowId);
                    if(requestContent != null) {
                        for(Content content : requestContent) {
                            if(content.isRelevantTo(event.getClass())) {
                                marketdataCache.update(content,
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
        String provider = inRequest.getProvider();
        SLF4JLoggerProxy.debug(this,
                               "Requesting market data: {} from {}",
                               inRequest,
                               provider);
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
    }
    /**
     * Get a market data snapshot with the given attributes.
     *
     * @param inInstrument an <code>Instrument</code> value
     * @param inContent a <code>Content</code> value
     * @param inProvider a <code>String</code> value
     * @return an <code>Event</code> value
     */
    public Event requestMarketDataSnapshot(Instrument inInstrument,
                                           Content inContent,
                                           String inProvider)
    {
        MarketdataCacheElement cachedData = cachedMarketdata.get(inInstrument);
        if(cachedData != null) {
            return cachedData.getSnapshot(inContent);
        }
        return null;
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
            // this will be our guess in case we don't find something
            instanceUrn = new ModuleURN("metc:mdata:" + inProviderName+":single");
            for(ModuleURN moduleUrn : ModuleManager.getInstance().getProviders()) {
                String providerType = moduleUrn.providerType();
                if(providerType.equals("mdata") && moduleUrn.providerName().equals(inProviderName)) {
                    instanceUrnsByProviderName.put(inProviderName,
                                                   moduleUrn);
                    instanceUrn = moduleUrn;
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
     * @param inSubscriber an <code>ISubscriber</code> value
     */
    private void doDataRequest(MarketDataRequest inMarketDataRequest,
                               ModuleURN inSourceUrn,
                               ISubscriber inSubscriber)
    {
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
    private long subscriberTimeout = 500;
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
     * tracks cached market data by the instrument
     */
    private final Map<Instrument,MarketdataCacheElement> cachedMarketdata = new HashMap<Instrument,MarketdataCacheElement>();
}
