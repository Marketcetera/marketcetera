package org.marketcetera.marketdata.core.module;

import java.util.Map;

import org.marketcetera.core.publisher.ISubscriber;
import org.marketcetera.marketdata.MarketDataRequest;
import org.marketcetera.marketdata.MarketDataRequestBuilder;
import org.marketcetera.marketdata.core.manager.MarketDataManager;
import org.marketcetera.marketdata.core.manager.impl.MarketDataManagerImpl;
import org.marketcetera.module.*;
import org.marketcetera.util.misc.ClassVersion;

import com.google.common.collect.Maps;

/* $License$ */

/**
 * Provides centralized access to all market data adapters in a single data flow.
 *
 * <p>Module Features
 * <table>
 * <tr><th>Capabilities</th><td>Data Emitter</td></tr>
 * <tr><th>DataFlow Request Parameters</th><td>{@link MarketDataRequest} or <code>String</code></td></tr>
 * <tr><th>Stops data flows</th><td>no</td></tr>
 * <tr><th>Start Operation</th><td>none</td></tr>
 * <tr><th>Stop Operation</th><td>none</td></tr>
 * <tr><th>Management Interface</th><td>none</td></tr>
 * <tr><th>Factory</th><td>{@link MarketDataCoreModuleFactory}</td></tr>
 * </table>
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 2.4.0
 */
@ClassVersion("$Id$")
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
        InternalRequest internalRequest = new InternalRequest(inSupport);
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
        marketDataManager = MarketDataManagerImpl.getInstance();
        if(marketDataManager == null) {
            marketDataManager = new MarketDataManagerImpl();
        }
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
     * Represents a request forwarded to the market data manager.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since 2.4.0
     */
    @ClassVersion("$Id$")
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
         * @param inLiason a <code>DataEmitterSupport</code> value
         */
        private InternalRequest(DataEmitterSupport inLiason)
        {
            liason = inLiason;
        }
        /**
         * unique identifier assigned to a forwarded request
         */
        private long internalRequestId;
        /**
         * provides access to the module framework for this request
         */
        private final DataEmitterSupport liason;
    }
    /**
     * provides access to the centralized market data manager
     */
    private MarketDataManager marketDataManager;
    /**
     * tracks internal market data requests by module manager request ID
     */
    private final Map<RequestID,InternalRequest> requestsByRequestID = Maps.newHashMap();
}
