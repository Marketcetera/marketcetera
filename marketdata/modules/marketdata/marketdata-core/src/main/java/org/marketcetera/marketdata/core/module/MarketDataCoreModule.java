package org.marketcetera.marketdata.core.module;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.marketcetera.core.publisher.ISubscriber;
import org.marketcetera.marketdata.MarketDataRequest;
import org.marketcetera.marketdata.MarketDataRequestBuilder;
import org.marketcetera.marketdata.core.manager.MarketDataManager;
import org.marketcetera.marketdata.core.manager.MarketDataProviderRegistry;
import org.marketcetera.marketdata.core.manager.impl.MarketDataManagerImpl;
import org.marketcetera.module.*;
import org.marketcetera.util.log.SLF4JLoggerProxy;

import com.google.common.collect.Maps;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class MarketDataCoreModule
        extends Module
        implements DataEmitter
{
    /* (non-Javadoc)
     * @see org.marketcetera.module.DataEmitter#requestData(org.marketcetera.module.DataRequest, org.marketcetera.module.DataEmitterSupport)
     */
    @Override
    public void requestData(DataRequest inRequest,
                            DataEmitterSupport inSupport)
            throws RequestDataException
    {
        Object requestPayload = inRequest.getData();
        if(requestPayload == null) {
            throw new IllegalRequestParameterValue(MarketDataCoreModuleFactory.INSTANCE_URN,
                                                   null);
        }
        MarketDataRequest request = null;
        if(requestPayload instanceof String) {
            try {
                request = MarketDataRequestBuilder.newRequestFromString((String)requestPayload);
            } catch (Exception e) {
                throw new IllegalRequestParameterValue(MarketDataCoreModuleFactory.INSTANCE_URN,
                                                       requestPayload,
                                                       e);
            }
        } else if (requestPayload instanceof MarketDataRequest) {
            request = (MarketDataRequest)requestPayload;
        } else {
            throw new UnsupportedRequestParameterType(MarketDataCoreModuleFactory.INSTANCE_URN,
                                                      requestPayload);
        }
        SLF4JLoggerProxy.debug(this,
                               "Received: {}",
                               request);
        InternalRequest internalRequest = new InternalRequest(inSupport,
                                                              request);
        requestsByRequestID.put(inSupport.getRequestID(),
                                internalRequest);
        internalRequest.internalRequestId = marketDataManager.requestMarketData(request,
                                                                                internalRequest);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.module.DataEmitter#cancel(org.marketcetera.module.DataFlowID, org.marketcetera.module.RequestID)
     */
    @Override
    public void cancel(DataFlowID inFlowID,
                       RequestID inRequestID)
    {
        InternalRequest internalRequest = requestsByRequestID.remove(inRequestID);
        if(internalRequest != null) {
            marketDataManager.cancelMarketDataRequest(internalRequest.internalRequestId);
        }
    }
    /**
     * Create a new MarketDataCoreModule instance.
     */
    MarketDataCoreModule()
    {
        super(MarketDataCoreModuleFactory.INSTANCE_URN,
              true);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.module.Module#preStart()
     */
    @Override
    protected void preStart()
            throws ModuleException
    {
        marketDataManager = new MarketDataManagerImpl();
        new MarketDataProviderMonitor().run();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.module.Module#preStop()
     */
    @Override
    protected void preStop()
            throws ModuleException
    {
        // TODO cancel all market data requests
    }
    /**
     *
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    private static class InternalRequest
            implements ISubscriber
    {
        /* (non-Javadoc)
         * @see org.marketcetera.core.publisher.ISubscriber#isInteresting(java.lang.Object)
         */
        @Override
        public boolean isInteresting(Object inData)
        {
            return true;
        }
        /* (non-Javadoc)
         * @see org.marketcetera.core.publisher.ISubscriber#publishTo(java.lang.Object)
         */
        @Override
        public void publishTo(Object inData)
        {
            liason.send(inData);
        }
        /**
         * Create a new InternalRequest instance.
         *
         * @param inLiason
         * @param inOriginalRequest
         */
        private InternalRequest(DataEmitterSupport inLiason,
                                MarketDataRequest inOriginalRequest)
        {
            liason = inLiason;
            originalRequest = inOriginalRequest;
        }
        private long internalRequestId;
        private final DataEmitterSupport liason;
        private final MarketDataRequest originalRequest;
    }
    private class MarketDataProviderMonitor
            implements Runnable
    {
        /* (non-Javadoc)
         * @see java.lang.Runnable#run()
         */
        @Override
        public void run()
        {
            List<ModuleURN> marketDataProviderUrns = ModuleManager.getInstance().getProviders();
            for(ModuleURN marketDataProviderUrn : marketDataProviderUrns) {
                if(MDATA_PROVIDER.matcher(marketDataProviderUrn.getValue()).matches()) {
                    List<ModuleURN> marketDataInstanceUrns = ModuleManager.getInstance().getModuleInstances(marketDataProviderUrn);
                    for(ModuleURN marketDataInstanceUrn : marketDataInstanceUrns) {
                        if(ModuleManager.getInstance().getModuleInfo(marketDataInstanceUrn).getState() == ModuleState.STARTED) {
                            System.out.println(marketDataProviderUrn + " is available");
                        } else {
                            System.out.println(marketDataProviderUrn + " is not available");
                        }
                    }
                }
            }
        }
        private final Pattern MDATA_PROVIDER = Pattern.compile("^metc:mdata:\\w+$");
    }
    /**
     * 
     */
    private MarketDataManager marketDataManager;
    /**
     * 
     */
    private final Map<RequestID,InternalRequest> requestsByRequestID = Maps.newHashMap();
}
