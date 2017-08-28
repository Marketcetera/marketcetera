package org.marketcetera.client.rpc.server;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.marketcetera.rpc.base.BaseRpc.HeartbeatRequest;
import org.marketcetera.rpc.base.BaseRpc.HeartbeatResponse;
import org.marketcetera.rpc.base.BaseRpc.LoginRequest;
import org.marketcetera.rpc.base.BaseRpc.LoginResponse;
import org.marketcetera.rpc.base.BaseRpc.LogoutRequest;
import org.marketcetera.rpc.base.BaseRpc.LogoutResponse;
import org.marketcetera.rpc.server.AbstractRpcService;
import org.marketcetera.trade.service.TradeService;
import org.marketcetera.trading.rpc.TradingRpc.OpenOrdersRequest;
import org.marketcetera.trading.rpc.TradingRpc.OpenOrdersResponse;
import org.marketcetera.trading.rpc.TradingRpc.SendOrderRequest;
import org.marketcetera.trading.rpc.TradingRpc.SendOrderResponse;
import org.marketcetera.trading.rpc.TradingRpcServiceGrpc;
import org.marketcetera.trading.rpc.TradingRpcServiceGrpc.TradingRpcServiceImplBase;
import org.springframework.beans.factory.annotation.Autowired;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;

/* $License$ */

/**
 * Provides trade client RPC server services.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class TradeClientRpcService<SessionClazz>
        extends AbstractRpcService<SessionClazz,TradingRpcServiceGrpc.TradingRpcServiceImplBase>
{
    /**
     * Validate and start the object.
     */
    @PostConstruct
    public void start()
            throws Exception
    {
        service = new Service();
        super.start();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.rpc.server.AbstractRpcService#getServiceDescription()
     */
    @Override
    protected String getServiceDescription()
    {
        return description;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.rpc.server.AbstractRpcService#getService()
     */
    @Override
    protected TradingRpcServiceImplBase getService()
    {
        return service;
    }
    /**
     * Trade RPC Service implementation.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id: MarketDataRpcService.java 17251 2016-09-08 23:18:29Z colin $
     * @since $Release$
     */
    private class Service
            extends TradingRpcServiceGrpc.TradingRpcServiceImplBase
    {
        /* (non-Javadoc)
         * @see org.marketcetera.trading.rpc.TradingRpcServiceGrpc.TradingRpcServiceImplBase#login(org.marketcetera.rpc.base.BaseRpc.LoginRequest, io.grpc.stub.StreamObserver)
         */
        @Override
        public void login(LoginRequest inRequest,
                          StreamObserver<LoginResponse> inResponseObserver)
        {
            TradeClientRpcService.this.doLogin(inRequest,
                                               inResponseObserver);
        }
        /* (non-Javadoc)
         * @see org.marketcetera.trading.rpc.TradingRpcServiceGrpc.TradingRpcServiceImplBase#logout(org.marketcetera.rpc.base.BaseRpc.LogoutRequest, io.grpc.stub.StreamObserver)
         */
        @Override
        public void logout(LogoutRequest inRequest,
                           StreamObserver<LogoutResponse> inResponseObserver)
        {
            TradeClientRpcService.this.doLogout(inRequest,
                                                inResponseObserver);
        }
        /* (non-Javadoc)
         * @see org.marketcetera.trading.rpc.TradingRpcServiceGrpc.TradingRpcServiceImplBase#heartbeat(org.marketcetera.rpc.base.BaseRpc.HeartbeatRequest, io.grpc.stub.StreamObserver)
         */
        @Override
        public void heartbeat(HeartbeatRequest inRequest,
                              StreamObserver<HeartbeatResponse> inResponseObserver)
        {
            TradeClientRpcService.this.doHeartbeat(inRequest,
                                                   inResponseObserver);
        }
        /* (non-Javadoc)
         * @see org.marketcetera.trading.rpc.TradingRpcServiceGrpc.TradingRpcServiceImplBase#getOpenOrders(org.marketcetera.trading.rpc.TradingRpc.OpenOrdersRequest, io.grpc.stub.StreamObserver)
         */
        @Override
        public void getOpenOrders(OpenOrdersRequest inRequest,
                                  StreamObserver<OpenOrdersResponse> inResponseObserver)
        {
            try {
                validateAndReturnSession(inRequest.getSessionId());
//                MarketdataRpc.MarketDataResponse.Builder responseBuilder = MarketdataRpc.MarketDataResponse.newBuilder();
//                MarketdataRpc.MarketDataResponse response = responseBuilder.setId(marketDataService.request(org.marketcetera.marketdata.MarketDataRequestBuilder.newRequestFromString(inRequest.getRequest()),
//                                                                                                         inRequest.getStreamEvents())).build();
//                inResponseObserver.onNext(response);
//                inResponseObserver.onCompleted();
            } catch (Exception e) {
                if(e instanceof StatusRuntimeException) {
                    throw (StatusRuntimeException)e;
                }
                throw new StatusRuntimeException(Status.INVALID_ARGUMENT.withCause(e).withDescription(ExceptionUtils.getRootCauseMessage(e)));
            }
        }
        /* (non-Javadoc)
         * @see org.marketcetera.trading.rpc.TradingRpcServiceGrpc.TradingRpcServiceImplBase#sendOrders(org.marketcetera.trading.rpc.TradingRpc.SendOrderRequest, io.grpc.stub.StreamObserver)
         */
        @Override
        public void sendOrders(SendOrderRequest inRequest,
                               StreamObserver<SendOrderResponse> inResponseObserver)
        {
            throw new UnsupportedOperationException(); // TODO
        }
    }
    /**
     * provides trade services
     */
    @Autowired
    private TradeService tradeServer;
    /**
     * provides the RPC service
     */
    private Service service;
    /**
     * description of this service
     */
    private final static String description = "Marketcetera Trading Service";
}
