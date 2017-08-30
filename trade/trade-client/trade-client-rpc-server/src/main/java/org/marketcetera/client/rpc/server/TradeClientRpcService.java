package org.marketcetera.client.rpc.server;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.marketcetera.admin.HasUser;
import org.marketcetera.admin.User;
import org.marketcetera.admin.service.UserService;
import org.marketcetera.module.HasMutableStatus;
import org.marketcetera.module.HasStatus;
import org.marketcetera.rpc.base.BaseRpc.HeartbeatRequest;
import org.marketcetera.rpc.base.BaseRpc.HeartbeatResponse;
import org.marketcetera.rpc.base.BaseRpc.LoginRequest;
import org.marketcetera.rpc.base.BaseRpc.LoginResponse;
import org.marketcetera.rpc.base.BaseRpc.LogoutRequest;
import org.marketcetera.rpc.base.BaseRpc.LogoutResponse;
import org.marketcetera.rpc.server.AbstractRpcService;
import org.marketcetera.trade.HasOrder;
import org.marketcetera.trade.Order;
import org.marketcetera.trade.TradeMessage;
import org.marketcetera.trade.TradeMessageListener;
import org.marketcetera.trade.service.TradeService;
import org.marketcetera.trading.rpc.TradingRpc;
import org.marketcetera.trading.rpc.TradingRpc.AddTradeMessageListenerRequest;
import org.marketcetera.trading.rpc.TradingRpc.OpenOrdersRequest;
import org.marketcetera.trading.rpc.TradingRpc.OpenOrdersResponse;
import org.marketcetera.trading.rpc.TradingRpc.RemoveTradeMessageListenerRequest;
import org.marketcetera.trading.rpc.TradingRpc.RemoveTradeMessageListenerResponse;
import org.marketcetera.trading.rpc.TradingRpc.SendOrderRequest;
import org.marketcetera.trading.rpc.TradingRpc.SendOrderResponse;
import org.marketcetera.trading.rpc.TradingRpc.TradeMessageListenerResponse;
import org.marketcetera.trading.rpc.TradingRpcServiceGrpc;
import org.marketcetera.trading.rpc.TradingRpcServiceGrpc.TradingRpcServiceImplBase;
import org.marketcetera.trading.rpc.TradingTypesRpc;
import org.marketcetera.trading.rpc.TradingUtil;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.ws.stateful.SessionHolder;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

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
            try {
                SessionHolder<SessionClazz> sessionHolder = validateAndReturnSession(inRequest.getSessionId());
                TradingRpc.SendOrderResponse.Builder responseBuilder = TradingRpc.SendOrderResponse.newBuilder();
                TradingRpc.OrderResponse.Builder orderResponseBuilder = TradingRpc.OrderResponse.newBuilder();
                SLF4JLoggerProxy.trace(TradeClientRpcService.this,
                                       "Received send order request {} from {}",
                                       inRequest,
                                       sessionHolder);
                for(TradingTypesRpc.Order rpcOrder : inRequest.getOrderList()) {
                    try {
                        Order matpOrder = TradingUtil.getOrder(rpcOrder);
                        TradingUtil.setOrderId(matpOrder,
                                               orderResponseBuilder);
                        User user = userService.findByName(sessionHolder.getUser());
                        Object result = tradeService.submitOrderToOutgoingDataFlow(new RpcOrderWrapper(user,
                                                                                                       matpOrder));
                        SLF4JLoggerProxy.debug(TradeClientRpcService.this,
                                               "Order submission returned {}",
                                               result);
                        if(result instanceof HasStatus) {
                            HasStatus hasStatusResult = (HasStatus)result;
                            orderResponseBuilder.setFailed(hasStatusResult.getFailed());
                            if(hasStatusResult.getFailed()) {
                                orderResponseBuilder.setMessage(hasStatusResult.getErrorMessage());
                                SLF4JLoggerProxy.warn(TradeClientRpcService.this,
                                                      "Order submission failed: {}",
                                                      result);
                            }
                        } else {
                            orderResponseBuilder.setFailed(false);
                        }
                    } catch (Exception e) {
                        orderResponseBuilder.setFailed(true);
                        orderResponseBuilder.setMessage(ExceptionUtils.getRootCauseMessage(e));
                    }
                    responseBuilder.addOrderResponse(orderResponseBuilder.build());
                    orderResponseBuilder.clear();
                }
                TradingRpc.SendOrderResponse response = responseBuilder.build();
                inResponseObserver.onNext(response);
                inResponseObserver.onCompleted();
            } catch (Exception e) {
                if(e instanceof StatusRuntimeException) {
                    throw (StatusRuntimeException)e;
                }
                throw new StatusRuntimeException(Status.INVALID_ARGUMENT.withCause(e).withDescription(ExceptionUtils.getRootCauseMessage(e)));
            }
        }
        
        /* (non-Javadoc)
         * @see org.marketcetera.trading.rpc.TradingRpcServiceGrpc.TradingRpcServiceImplBase#addTradeMessageListener(org.marketcetera.trading.rpc.TradingRpc.AddTradeMessageListenerRequest, io.grpc.stub.StreamObserver)
         */
        @Override
        public void addTradeMessageListener(AddTradeMessageListenerRequest inRequest,
                                            StreamObserver<TradeMessageListenerResponse> inResponseObserver)
        {
            try {
                validateAndReturnSession(inRequest.getSessionId());
                SLF4JLoggerProxy.trace(TradeClientRpcService.this,
                                       "Received add trade message listener request {}",
                                       inRequest);
                String listenerId = inRequest.getListenerId();
                TradeMessageListenerProxy tradeMessageListenerProxy = tradeMessageListenersById.getIfPresent(listenerId);
                if(tradeMessageListenerProxy == null) {
                    tradeMessageListenerProxy = new TradeMessageListenerProxy(listenerId,
                                                                              inResponseObserver);
                    tradeMessageListenersById.put(tradeMessageListenerProxy.id,
                                                  tradeMessageListenerProxy);
                    tradeService.addTradeMessageListener(tradeMessageListenerProxy);
                }
            } catch (Exception e) {
                if(e instanceof StatusRuntimeException) {
                    throw (StatusRuntimeException)e;
                }
                throw new StatusRuntimeException(Status.INVALID_ARGUMENT.withCause(e).withDescription(ExceptionUtils.getRootCauseMessage(e)));
            }
        }
        /* (non-Javadoc)
         * @see org.marketcetera.trading.rpc.TradingRpcServiceGrpc.TradingRpcServiceImplBase#removeTradeMessageListener(org.marketcetera.trading.rpc.TradingRpc.RemoveTradeMessageListenerRequest, io.grpc.stub.StreamObserver)
         */
        @Override
        public void removeTradeMessageListener(RemoveTradeMessageListenerRequest inRequest,
                                               StreamObserver<RemoveTradeMessageListenerResponse> inResponseObserver)
        {
            throw new UnsupportedOperationException(); // TODO
            
        }
    }
    private static class TradeMessageListenerProxy
            implements TradeMessageListener
    {
        /* (non-Javadoc)
         * @see org.marketcetera.trade.TradeMessageListener#receiveTradeMessage(org.marketcetera.trade.TradeMessage)
         */
        @Override
        public void receiveTradeMessage(TradeMessage inTradeMessage)
        {
            SLF4JLoggerProxy.trace(TradeClientRpcService.class,
                                   "{} received trade message {}",
                                   id,
                                   inTradeMessage);
            responseBuilder.setTemp(inTradeMessage.toString());
            observer.onNext(responseBuilder.build());
            responseBuilder.clear();
        }
        /**
         * Create a new TradeMessageListenerProxy instance.
         *
         * @param inId
         * @param inObserver
         */
        private TradeMessageListenerProxy(String inId,
                                          StreamObserver<TradeMessageListenerResponse> inObserver)
        {
            id = inId;
            observer = inObserver;
        }
        private final TradingRpc.TradeMessageListenerResponse.Builder responseBuilder = TradingRpc.TradeMessageListenerResponse.newBuilder();
        private final String id;
        private final StreamObserver<TradeMessageListenerResponse> observer;
    }
    /**
     * Wraps submitted orders.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    private static class RpcOrderWrapper
            implements HasOrder,HasUser,HasMutableStatus
    {
        /* (non-Javadoc)
         * @see org.marketcetera.trade.HasOrder#getOrder()
         */
        @Override
        public Order getOrder()
        {
            return order;
        }
        /* (non-Javadoc)
         * @see org.marketcetera.admin.HasUser#getUser()
         */
        @Override
        public User getUser()
        {
            return user;
        }
        /* (non-Javadoc)
         * @see org.marketcetera.trade.HasStatus#getFailed()
         */
        @Override
        public boolean getFailed()
        {
            return failed;
        }
        /* (non-Javadoc)
         * @see org.marketcetera.trade.HasStatus#setFailed(boolean)
         */
        @Override
        public void setFailed(boolean inFailed)
        {
            failed = inFailed;
        }
        /* (non-Javadoc)
         * @see org.marketcetera.trade.HasStatus#getMessage()
         */
        @Override
        public String getErrorMessage()
        {
            return message;
        }
        /* (non-Javadoc)
         * @see org.marketcetera.trade.HasStatus#setMessage(java.lang.String)
         */
        @Override
        public void setErrorMessage(String inMessage)
        {
            message = inMessage;
        }
        /* (non-Javadoc)
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString()
        {
            StringBuilder builder = new StringBuilder();
            builder.append("RpcOrderWrapper [order=").append(order).append(", user=").append(user).append(", failed=")
                    .append(failed).append(", message=").append(message).append(", start=").append(start).append("]");
            return builder.toString();
        }
        /**
         * Create a new RpcHasOrder instance.
         *
         * @param inUser a <code>User</code> value
         * @param inOrder an <code>Order</code> value
         */
        private RpcOrderWrapper(User inUser,
                                Order inOrder)
        {
            user = inUser;
            order = inOrder;
            failed = false;
        }
        /**
         * message value
         */
        private volatile String message;
        /**
         * failed value
         */
        private volatile boolean failed;
        /**
         * user value
         */
        private final User user;
        /**
         * order value
         */
        private final Order order;
        /**
         * start time stamp
         */
        private final long start = System.nanoTime();
    }
    /**
     * privates access to user services
     */
    @Autowired
    private UserService userService;
    /**
     * provides access to trade services
     */
    @Autowired
    private TradeService tradeService;
    /**
     * provides the RPC service
     */
    private Service service;
    /**
     * description of this service
     */
    private final static String description = "Marketcetera Trading Service";
    /**
     * holds trade message listeners by id
     */
    private final Cache<String,TradeMessageListenerProxy> tradeMessageListenersById = CacheBuilder.newBuilder().build();
}
