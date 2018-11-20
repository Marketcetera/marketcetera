package org.marketcetera.marketdata.manual;

import java.util.Collection;
import java.util.EnumSet;
import java.util.Map;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.marketcetera.core.PlatformServices;
import org.marketcetera.event.Event;
import org.marketcetera.marketdata.Capability;
import org.marketcetera.marketdata.FeedStatus;
import org.marketcetera.marketdata.MarketDataCapabilityBroadcaster;
import org.marketcetera.marketdata.MarketDataRequest;
import org.marketcetera.marketdata.MarketDataRequestBuilder;
import org.marketcetera.marketdata.MarketDataStatus;
import org.marketcetera.marketdata.MarketDataStatusBroadcaster;
import org.marketcetera.module.DataEmitter;
import org.marketcetera.module.DataEmitterSupport;
import org.marketcetera.module.DataFlowID;
import org.marketcetera.module.DataRequest;
import org.marketcetera.module.Module;
import org.marketcetera.module.ModuleException;
import org.marketcetera.module.ModuleURN;
import org.marketcetera.module.RequestDataException;
import org.marketcetera.module.RequestID;
import org.marketcetera.util.log.I18NBoundMessage1P;
import org.marketcetera.util.log.I18NBoundMessage2P;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Lists;

/* $License$ */

/**
 * Supplies market data supplied by an upstream module.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class ManualFeedModule
        extends Module
        implements DataEmitter
{
    /**
     * Get the instance value.
     *
     * @return a <code>ManualFeedModule</code> value
     */
    public static ManualFeedModule getInstance()
    {
        return instance;
    }
    /**
     * Emit the given events to the data flow for the given request id.
     *
     * @param inRequestId a <code>String</code> value or <code>null</code> to submit to all data flows
     * @param inEvents a <code>Collection&lt;Event&gt;</code> value
     */
    public void emit(String inRequestId,
                     Collection<Event> inEvents)
    {
        if(inRequestId == null) {
            SLF4JLoggerProxy.debug(this,
                                   "No request id specified, submitting to all data flows");
            for(RequestData request : requestsByRequestId.asMap().values()) {
                for(Event event : inEvents) {
                    emit(request.getDataEmitterSupport(),
                         event);
                }
            }
        } else {
            RequestData requestData = requestsByRequestId.getIfPresent(inRequestId);
            if(requestData == null) {
                SLF4JLoggerProxy.warn(this,
                                      "No request with id {}, cannot emit events",
                                      inRequestId);
            } else {
                for(Event event : inEvents) {
                    emit(requestData.getDataEmitterSupport(),
                         event);
                }
            }
        }
    }
    /**
     * Emit the given event to the data flow for the given request id.
     *
     * @param inRequestId a <code>String</code> value or <code>null</code> to submit to all data flows
     * @param inEvent a <code>Event</code> value
     */
    public void emit(String inRequestId,
                     Event inEvent)
    {
        if(inRequestId == null) {
            SLF4JLoggerProxy.debug(this,
                                   "No request id specified, submitting to all data flows");
            for(RequestData request : requestsByRequestId.asMap().values()) {
                emit(request.getDataEmitterSupport(),
                     inEvent);
            }
        } else {
            RequestData requestData = requestsByRequestId.getIfPresent(inRequestId);
            if(requestData == null) {
                SLF4JLoggerProxy.warn(this,
                                      "No request with id {}, cannot emit events",
                                      inRequestId);
            } else {
                emit(requestData.getDataEmitterSupport(),
                     inEvent);
            }
        }
    }
    /**
     * Get the known market data requests indexed by the request id associated with the request.
     *
     * @return a <code>BiMap&lt;String,MarketDataRequest&gt;</code> value
     */
    public BiMap<String,MarketDataRequest> getRequests()
    {
        BiMap<String,MarketDataRequest> requests = HashBiMap.create();
        for(Map.Entry<String,RequestData> entry : requestsByRequestId.asMap().entrySet()) {
            requests.put(entry.getKey(),
                         entry.getValue().getMarketDataRequest());
        }
        return requests;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.module.DataEmitter#requestData(org.marketcetera.module.DataRequest, org.marketcetera.module.DataEmitterSupport)
     */
    @Override
    public void requestData(DataRequest inRequest,
                            DataEmitterSupport inSupport)
            throws RequestDataException
    {
        SLF4JLoggerProxy.debug(this,
                               "Received a data flow request: {}", //$NON-NLS-1$
                               inRequest);
        Object payload = inRequest.getData();
        try {
            if(payload == null) {
                throw new RequestDataException(Messages.DATA_REQUEST_PAYLOAD_REQUIRED);
            }
            if(payload instanceof String) {
                String stringPayload = (String)payload;
                try {
                    doMarketDataRequest(MarketDataRequestBuilder.newRequestFromString(stringPayload),
                                        inRequest,
                                        inSupport);
                } catch (Exception e) {
                    throw new RequestDataException(new I18NBoundMessage2P(Messages.INVALID_DATA_REQUEST_PAYLOAD,
                                                                          stringPayload,
                                                                          ExceptionUtils.getRootCause(e)));
                }
            } else if(payload instanceof MarketDataRequest) {
                doMarketDataRequest((MarketDataRequest)payload,
                                    inRequest,
                                    inSupport);
            } else {
                throw new RequestDataException(new I18NBoundMessage1P(Messages.UNSUPPORTED_DATA_REQUEST_PAYLOAD,
                                                                      payload.getClass().getSimpleName()));
            }
        } catch (Exception e) {
            PlatformServices.handleException(this,
                                             "Market data request failed",
                                             e);
            throw new RequestDataException(e);
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.module.DataEmitter#cancel(org.marketcetera.module.DataFlowID, org.marketcetera.module.RequestID)
     */
    @Override
    public void cancel(DataFlowID inFlowId,
                       RequestID inRequestID)
    {
        RequestData requestData = requestsByDataFlowId.getIfPresent(inFlowId);
        requestsByDataFlowId.invalidate(inFlowId);
        if(requestData != null) {
            SLF4JLoggerProxy.debug(this,
                                   "Canceling data flow {} with market data request id {}", //$NON-NLS-1$
                                   inFlowId,
                                   requestData);
            requestsByRequestId.invalidate(requestData.requestId);
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.module.Module#preStart()
     */
    @Override
    protected void preStart()
            throws ModuleException
    {
        for(MarketDataCapabilityBroadcaster broadcaster : capabilityBroadcasters) {
            broadcaster.reportCapability(EnumSet.allOf(Capability.class));
        }
        MarketDataStatus status = new MarketDataStatus() {
            @Override
            public FeedStatus getFeedStatus()
            {
                return FeedStatus.AVAILABLE;
            }
            @Override
            public String getProvider()
            {
                return ManualFeedModuleFactory.IDENTIFIER;
            }
        };
        for(MarketDataStatusBroadcaster broadcaster : statusBroadcasters) {
            broadcaster.reportMarketDataStatus(status);
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.module.Module#preStop()
     */
    @Override
    protected void preStop()
            throws ModuleException
    {
        requestsByDataFlowId.invalidateAll();
        requestsByRequestId.invalidateAll();
        MarketDataStatus status = new MarketDataStatus() {
            @Override
            public FeedStatus getFeedStatus()
            {
                return FeedStatus.OFFLINE;
            }
            @Override
            public String getProvider()
            {
                return ManualFeedModuleFactory.IDENTIFIER;
            }
        };
        for(MarketDataStatusBroadcaster broadcaster : statusBroadcasters) {
            broadcaster.reportMarketDataStatus(status);
        }
    }
    /**
     * Create a new ManualFeedModule instance.
     *
     * @param inUrn a <code>ModuleURN</code> value
     */
    ManualFeedModule(ModuleURN inUrn)
    {
        super(inUrn,
              false);
        instance = this;
    }
    /**
     * Emit the given event to the given data flow.
     *
     * @param inDataEmitter a <code>DataEmitterSupport</code> value
     * @param inEvent an <code>Event</code> value
     */
    private void emit(DataEmitterSupport inDataEmitter,
                      Event inEvent)
    {
        SLF4JLoggerProxy.trace(this,
                               "Sending {} to {}",
                               inEvent,
                               inDataEmitter.getFlowID());
        inDataEmitter.send(inEvent);
    }
    /**
     * Perform the market data request
     *
     * @param inPayload a <code>MarketDataRequest</code> value
     * @param inRequest a <code>DataRequest</code> value
     * @param inSupport a <code>DataEmitterSupport</code> value
     */
    private void doMarketDataRequest(MarketDataRequest inPayload,
                                     DataRequest inRequest,
                                     DataEmitterSupport inSupport)
    {
        String id = inPayload.getRequestId();
        RequestData requestData = new RequestData(inSupport,
                                                  id,
                                                  inPayload);
        requestsByRequestId.put(id,
                                requestData);
        requestsByDataFlowId.put(inSupport.getFlowID(),
                                 requestData);
    }
    /**
     * Holds data relevant to a market data request as part of a module data flow.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    private static class RequestData
    {
        /* (non-Javadoc)
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString()
        {
            return description;
        }
        /**
         * Get the dataEmitterSupport value.
         *
         * @return a <code>DataEmitterSupport</code> value
         */
        private DataEmitterSupport getDataEmitterSupport()
        {
            return dataEmitterSupport;
        }
        /**
         * Get the requestId value.
         *
         * @return a <code>String</code> value
         */
        @SuppressWarnings("unused")
        private String getRequestId()
        {
            return requestId;
        }
        /**
         * Get the marketDataRequest value.
         *
         * @return a <code>MarketDataRequest</code> value
         */
        private MarketDataRequest getMarketDataRequest()
        {
            return marketDataRequest;
        }
        /**
         * Create a new RequestData instance.
         *
         * @param inDataEmitterSupport a <code>DataEmitterSupport</code> value
         * @param inRequestId a <code>String</code> value
         * @param inMarketDataRequest a <code>MarketDataRequest</code> value
         */
        private RequestData(DataEmitterSupport inDataEmitterSupport,
                            String inRequestId,
                            MarketDataRequest inMarketDataRequest)
        {
            dataEmitterSupport = inDataEmitterSupport;
            description = RequestData.class.getSimpleName() + " [" + inDataEmitterSupport.getFlowID() + "]"; //$NON-NLS-1$ //$NON-NLS-2$
            requestId = inRequestId;
            marketDataRequest = inMarketDataRequest;
        }
        /**
         * human-readable description of the object
         */
        private final String description;
        /**
         * information about the data flow requester
         */
        private final DataEmitterSupport dataEmitterSupport;
        /**
         * request id of the request
         */
        private final String requestId;
        /**
         * original market data request
         */
        private final MarketDataRequest marketDataRequest;
    }
    /**
     * receivers of capabilities of this module
     */
    @Autowired(required=false)
    private Collection<MarketDataCapabilityBroadcaster> capabilityBroadcasters = Lists.newArrayList();
    /**
     * receivers of status of this module
     */
    @Autowired(required=false)
    private Collection<MarketDataStatusBroadcaster> statusBroadcasters = Lists.newArrayList();
    /**
     * holds data request info keyed by request id
     */
    private final Cache<String,RequestData> requestsByRequestId = CacheBuilder.newBuilder().build();
    /**
     * holds market data request info by data flow id
     */
    private final Cache<DataFlowID,RequestData> requestsByDataFlowId = CacheBuilder.newBuilder().build();
    /**
     * singleton reference value
     */
    private static ManualFeedModule instance;
}
