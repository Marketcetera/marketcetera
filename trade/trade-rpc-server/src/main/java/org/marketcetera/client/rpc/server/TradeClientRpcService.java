package org.marketcetera.client.rpc.server;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.marketcetera.admin.HasUser;
import org.marketcetera.admin.User;
import org.marketcetera.admin.service.AuthorizationService;
import org.marketcetera.admin.service.UserService;
import org.marketcetera.brokers.BrokerStatus;
import org.marketcetera.brokers.BrokerStatusListener;
import org.marketcetera.brokers.BrokersStatus;
import org.marketcetera.brokers.service.BrokerService;
import org.marketcetera.core.PlatformServices;
import org.marketcetera.core.position.PositionKey;
import org.marketcetera.module.HasMutableStatus;
import org.marketcetera.module.HasStatus;
import org.marketcetera.persist.CollectionPageResponse;
import org.marketcetera.persist.PageRequest;
import org.marketcetera.rpc.base.BaseRpc.HeartbeatRequest;
import org.marketcetera.rpc.base.BaseRpc.HeartbeatResponse;
import org.marketcetera.rpc.base.BaseRpc.LoginRequest;
import org.marketcetera.rpc.base.BaseRpc.LoginResponse;
import org.marketcetera.rpc.base.BaseRpc.LogoutRequest;
import org.marketcetera.rpc.base.BaseRpc.LogoutResponse;
import org.marketcetera.rpc.base.BaseRpcUtil;
import org.marketcetera.rpc.paging.PagingRpcUtil;
import org.marketcetera.rpc.server.AbstractRpcService;
import org.marketcetera.symbol.SymbolResolverService;
import org.marketcetera.trade.BrokerID;
import org.marketcetera.trade.FIXMessageWrapper;
import org.marketcetera.trade.HasOrder;
import org.marketcetera.trade.Instrument;
import org.marketcetera.trade.Option;
import org.marketcetera.trade.Order;
import org.marketcetera.trade.OrderID;
import org.marketcetera.trade.OrderSummary;
import org.marketcetera.trade.ReportID;
import org.marketcetera.trade.TradeMessage;
import org.marketcetera.trade.TradeMessageListener;
import org.marketcetera.trade.TradePermissions;
import org.marketcetera.trade.UserID;
import org.marketcetera.trade.service.OrderSummaryService;
import org.marketcetera.trade.service.ReportService;
import org.marketcetera.trade.service.TradeService;
import org.marketcetera.trading.rpc.TradeRpcUtil;
import org.marketcetera.trading.rpc.TradingRpc;
import org.marketcetera.trading.rpc.TradingRpc.AddBrokerStatusListenerRequest;
import org.marketcetera.trading.rpc.TradingRpc.AddReportRequest;
import org.marketcetera.trading.rpc.TradingRpc.AddReportResponse;
import org.marketcetera.trading.rpc.TradingRpc.AddTradeMessageListenerRequest;
import org.marketcetera.trading.rpc.TradingRpc.BrokerStatusListenerResponse;
import org.marketcetera.trading.rpc.TradingRpc.BrokersStatusRequest;
import org.marketcetera.trading.rpc.TradingRpc.BrokersStatusResponse;
import org.marketcetera.trading.rpc.TradingRpc.DeleteReportRequest;
import org.marketcetera.trading.rpc.TradingRpc.DeleteReportResponse;
import org.marketcetera.trading.rpc.TradingRpc.FindRootOrderIdRequest;
import org.marketcetera.trading.rpc.TradingRpc.FindRootOrderIdResponse;
import org.marketcetera.trading.rpc.TradingRpc.GetAllPositionsAsOfRequest;
import org.marketcetera.trading.rpc.TradingRpc.GetAllPositionsAsOfResponse;
import org.marketcetera.trading.rpc.TradingRpc.GetAllPositionsByRootAsOfRequest;
import org.marketcetera.trading.rpc.TradingRpc.GetAllPositionsByRootAsOfResponse;
import org.marketcetera.trading.rpc.TradingRpc.GetPositionAsOfRequest;
import org.marketcetera.trading.rpc.TradingRpc.GetPositionAsOfResponse;
import org.marketcetera.trading.rpc.TradingRpc.OpenOrdersRequest;
import org.marketcetera.trading.rpc.TradingRpc.OpenOrdersResponse;
import org.marketcetera.trading.rpc.TradingRpc.RemoveBrokerStatusListenerRequest;
import org.marketcetera.trading.rpc.TradingRpc.RemoveBrokerStatusListenerResponse;
import org.marketcetera.trading.rpc.TradingRpc.RemoveTradeMessageListenerRequest;
import org.marketcetera.trading.rpc.TradingRpc.RemoveTradeMessageListenerResponse;
import org.marketcetera.trading.rpc.TradingRpc.ResolveSymbolRequest;
import org.marketcetera.trading.rpc.TradingRpc.ResolveSymbolResponse;
import org.marketcetera.trading.rpc.TradingRpc.SendOrderRequest;
import org.marketcetera.trading.rpc.TradingRpc.SendOrderResponse;
import org.marketcetera.trading.rpc.TradingRpc.TradeMessageListenerResponse;
import org.marketcetera.trading.rpc.TradingRpcServiceGrpc;
import org.marketcetera.trading.rpc.TradingRpcServiceGrpc.TradingRpcServiceImplBase;
import org.marketcetera.trading.rpc.TradingTypesRpc;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.ws.stateful.SessionHolder;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.protobuf.util.Timestamps;

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
                SessionHolder<SessionClazz> sessionHolder = validateAndReturnSession(inRequest.getSessionId());
                authzService.authorize(sessionHolder.getUser(),
                                       TradePermissions.ViewOpenOrdersAction.name());
                SLF4JLoggerProxy.trace(TradeClientRpcService.this,
                                       "Received open order request {}",
                                       inRequest);
                TradingRpc.OpenOrdersResponse.Builder responseBuilder = TradingRpc.OpenOrdersResponse.newBuilder();
                PageRequest pageRequest = inRequest.hasPageRequest()?PagingRpcUtil.getPageRequest(inRequest.getPageRequest()):PageRequest.ALL;
                CollectionPageResponse<? extends OrderSummary> orderSummaryPage = orderSummaryService.findOpenOrders(pageRequest);
                orderSummaryPage.getElements().forEach(value->responseBuilder.addOrders(TradeRpcUtil.getRpcOrderSummary(value)));
                responseBuilder.setPageResponse(PagingRpcUtil.getPageResponse(pageRequest,
                                                                              orderSummaryPage));
                TradingRpc.OpenOrdersResponse response = responseBuilder.build();
                SLF4JLoggerProxy.trace(TradeClientRpcService.this,
                                       "Responding: {}",
                                       response);
                inResponseObserver.onNext(response);
                inResponseObserver.onCompleted();
            } catch (Exception e) {
                handleError(e,
                            inResponseObserver);
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
                SLF4JLoggerProxy.trace(TradeClientRpcService.this,
                                       "Received send order request {} from {}",
                                       inRequest,
                                       sessionHolder);
                authzService.authorize(sessionHolder.getUser(),
                                       TradePermissions.SendOrderAction.name());
                TradingRpc.SendOrderResponse.Builder responseBuilder = TradingRpc.SendOrderResponse.newBuilder();
                TradingRpc.OrderResponse.Builder orderResponseBuilder = TradingRpc.OrderResponse.newBuilder();
                for(TradingTypesRpc.Order rpcOrder : inRequest.getOrderList()) {
                    try {
                        Order matpOrder = TradeRpcUtil.getOrder(rpcOrder);
                        TradeRpcUtil.setOrderId(matpOrder,
                                               orderResponseBuilder);
                        User user = userService.findByName(sessionHolder.getUser());
                        Object result = tradeService.submitOrderToOutgoingDataFlow(new RpcOrderWrapper(user,
                                                                                                       matpOrder));
                        SLF4JLoggerProxy.debug(TradeClientRpcService.this,
                                               "Order submission returned {}",
                                               result);
                        if(result instanceof HasStatus) {
                            HasStatus hasStatus = (HasStatus)result;
                            if(hasStatus.getFailed()) {
                                throw new RuntimeException(hasStatus.getErrorMessage());
                            }
                        }
                    } catch (Exception e) {
                        SLF4JLoggerProxy.warn(TradeClientRpcService.this,
                                              e,
                                              "Unable to submit order {}",
                                              rpcOrder);
                        throw e;
                    }
                    responseBuilder.addOrderResponse(orderResponseBuilder.build());
                    orderResponseBuilder.clear();
                }
                TradingRpc.SendOrderResponse response = responseBuilder.build();
                inResponseObserver.onNext(response);
                inResponseObserver.onCompleted();
            } catch (Exception e) {
                handleError(e,
                            inResponseObserver);
            }
        }
        /* (non-Javadoc)
         * @see org.marketcetera.trading.rpc.TradingRpcServiceGrpc.TradingRpcServiceImplBase#resolveSymbol(org.marketcetera.trading.rpc.TradingRpc.ResolveSymbolRequest, io.grpc.stub.StreamObserver)
         */
        @Override
        public void resolveSymbol(ResolveSymbolRequest inRequest,
                                  StreamObserver<ResolveSymbolResponse> inResponseObserver)
        {
            try {
                SessionHolder<SessionClazz> sessionHolder = validateAndReturnSession(inRequest.getSessionId());
                TradingRpc.ResolveSymbolResponse.Builder responseBuilder = TradingRpc.ResolveSymbolResponse.newBuilder();
                SLF4JLoggerProxy.trace(TradeClientRpcService.this,
                                       "Received resolve symbol request {} from {}",
                                       inRequest,
                                       sessionHolder);
                Instrument instrument = symbolResolverService.resolveSymbol(inRequest.getSymbol());
                TradeRpcUtil.setInstrument(instrument,
                                          responseBuilder);
                TradingRpc.ResolveSymbolResponse response = responseBuilder.build();
                inResponseObserver.onNext(response);
                inResponseObserver.onCompleted();
            } catch (Exception e) {
                handleError(e,
                            inResponseObserver);
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
                BaseRpcUtil.AbstractServerListenerProxy<?> tradeMessageListenerProxy = listenerProxiesById.getIfPresent(listenerId);
                if(tradeMessageListenerProxy == null) {
                    tradeMessageListenerProxy = new TradeMessageListenerProxy(listenerId,
                                                                              inResponseObserver);
                    listenerProxiesById.put(tradeMessageListenerProxy.getId(),
                                            tradeMessageListenerProxy);
                    tradeService.addTradeMessageListener((TradeMessageListener)tradeMessageListenerProxy);
                }
            } catch (Exception e) {
                handleError(e,
                            inResponseObserver);
            }
        }
        /* (non-Javadoc)
         * @see org.marketcetera.trading.rpc.TradingRpcServiceGrpc.TradingRpcServiceImplBase#removeTradeMessageListener(org.marketcetera.trading.rpc.TradingRpc.RemoveTradeMessageListenerRequest, io.grpc.stub.StreamObserver)
         */
        @Override
        public void removeTradeMessageListener(RemoveTradeMessageListenerRequest inRequest,
                                               StreamObserver<RemoveTradeMessageListenerResponse> inResponseObserver)
        {
            try {
                validateAndReturnSession(inRequest.getSessionId());
                SLF4JLoggerProxy.trace(TradeClientRpcService.this,
                                       "Received remove trade message listener request {}",
                                       inRequest);
                String listenerId = inRequest.getListenerId();
                BaseRpcUtil.AbstractServerListenerProxy<?> tradeMessageListenerProxy = listenerProxiesById.getIfPresent(listenerId);
                listenerProxiesById.invalidate(listenerId);
                if(tradeMessageListenerProxy != null) {
                    tradeService.removeTradeMessageListener((TradeMessageListener)tradeMessageListenerProxy);
                    tradeMessageListenerProxy.close();
                }
                TradingRpc.RemoveTradeMessageListenerResponse.Builder responseBuilder = TradingRpc.RemoveTradeMessageListenerResponse.newBuilder();
                TradingRpc.RemoveTradeMessageListenerResponse response = responseBuilder.build();
                SLF4JLoggerProxy.trace(TradeClientRpcService.this,
                                       "Returning {}",
                                       response);
                inResponseObserver.onNext(response);
                inResponseObserver.onCompleted();
            } catch (Exception e) {
                handleError(e,
                            inResponseObserver);
            }
        }
        /* (non-Javadoc)
         * @see org.marketcetera.trading.rpc.TradingRpcServiceGrpc.TradingRpcServiceImplBase#addBrokerStatusListener(org.marketcetera.trading.rpc.TradingRpc.AddBrokerStatusListenerRequest, io.grpc.stub.StreamObserver)
         */
        @Override
        public void addBrokerStatusListener(AddBrokerStatusListenerRequest inRequest,
                                            StreamObserver<BrokerStatusListenerResponse> inResponseObserver)
        {
            try {
                SessionHolder<SessionClazz> sessionHolder = validateAndReturnSession(inRequest.getSessionId());
                SLF4JLoggerProxy.trace(TradeClientRpcService.this,
                                       "Received add broker status listener request {}",
                                       inRequest);
                authzService.authorize(sessionHolder.getUser(),
                                       TradePermissions.ViewBrokerStatusAction.name());
                String listenerId = inRequest.getListenerId();
                BaseRpcUtil.AbstractServerListenerProxy<?> brokerStatusListenerProxy = listenerProxiesById.getIfPresent(listenerId);
                if(brokerStatusListenerProxy == null) {
                    brokerStatusListenerProxy = new BrokerStatusListenerProxy(listenerId,
                                                                              inResponseObserver);
                    listenerProxiesById.put(brokerStatusListenerProxy.getId(),
                                            brokerStatusListenerProxy);
                    brokerService.addBrokerStatusListener((BrokerStatusListener)brokerStatusListenerProxy);
                }
            } catch (Exception e) {
                handleError(e,
                            inResponseObserver);
            }
        }
        /* (non-Javadoc)
         * @see org.marketcetera.trading.rpc.TradingRpcServiceGrpc.TradingRpcServiceImplBase#removeBrokerStatusListener(org.marketcetera.trading.rpc.TradingRpc.RemoveBrokerStatusListenerRequest, io.grpc.stub.StreamObserver)
         */
        @Override
        public void removeBrokerStatusListener(RemoveBrokerStatusListenerRequest inRequest,
                                               StreamObserver<RemoveBrokerStatusListenerResponse> inResponseObserver)
        {
            try {
                validateAndReturnSession(inRequest.getSessionId());
                SLF4JLoggerProxy.trace(TradeClientRpcService.this,
                                       "Received remove broker status listener request {}",
                                       inRequest);
                String listenerId = inRequest.getListenerId();
                BaseRpcUtil.AbstractServerListenerProxy<?> brokerStatusListenerProxy = listenerProxiesById.getIfPresent(listenerId);
                listenerProxiesById.invalidate(listenerId);
                if(brokerStatusListenerProxy != null) {
                    brokerService.removeBrokerStatusListener((BrokerStatusListener)brokerStatusListenerProxy);
                    brokerStatusListenerProxy.close();
                }
                TradingRpc.RemoveBrokerStatusListenerResponse.Builder responseBuilder = TradingRpc.RemoveBrokerStatusListenerResponse.newBuilder();
                TradingRpc.RemoveBrokerStatusListenerResponse response = responseBuilder.build();
                SLF4JLoggerProxy.trace(TradeClientRpcService.this,
                                       "Returning {}",
                                       response);
                inResponseObserver.onNext(response);
                inResponseObserver.onCompleted();
            } catch (Exception e) {
                handleError(e,
                            inResponseObserver);
            }
        }
        /* (non-Javadoc)
         * @see org.marketcetera.trading.rpc.TradingRpcServiceGrpc.TradingRpcServiceImplBase#getBrokersStatus(org.marketcetera.trading.rpc.TradingRpc.BrokersStatusRequest, io.grpc.stub.StreamObserver)
         */
        @Override
        public void getBrokersStatus(BrokersStatusRequest inRequest,
                                     StreamObserver<BrokersStatusResponse> inResponseObserver)
        {
            try {
                SessionHolder<SessionClazz> sessionHolder = validateAndReturnSession(inRequest.getSessionId());
                SLF4JLoggerProxy.trace(TradeClientRpcService.this,
                                       "Received get brokers status request {} from {}",
                                       inRequest,
                                       sessionHolder);
                authzService.authorize(sessionHolder.getUser(),
                                       TradePermissions.ViewBrokerStatusAction.name());
                TradingRpc.BrokersStatusResponse.Builder responseBuilder = TradingRpc.BrokersStatusResponse.newBuilder();
                BrokersStatus brokersStatus = brokerService.getBrokersStatus();
                TradeRpcUtil.setBrokersStatus(brokersStatus,
                                             responseBuilder);
                TradingRpc.BrokersStatusResponse response = responseBuilder.build();
                SLF4JLoggerProxy.trace(TradeClientRpcService.this,
                                       "Returning {}",
                                       response);
                inResponseObserver.onNext(response);
                inResponseObserver.onCompleted();
            } catch (Exception e) {
                handleError(e,
                            inResponseObserver);
            }
        }
        /* (non-Javadoc)
         * @see org.marketcetera.trading.rpc.TradingRpcServiceGrpc.TradingRpcServiceImplBase#findRootOrderId(org.marketcetera.trading.rpc.TradingRpc.FindRootOrderIdRequest, io.grpc.stub.StreamObserver)
         */
        @Override
        public void findRootOrderId(FindRootOrderIdRequest inRequest,
                                    StreamObserver<FindRootOrderIdResponse> inResponseObserver)
        {
            try {
                SessionHolder<SessionClazz> sessionHolder = validateAndReturnSession(inRequest.getSessionId());
                SLF4JLoggerProxy.trace(TradeClientRpcService.this,
                                       "Received find root order id request {} from {}",
                                       inRequest,
                                       sessionHolder);
                authzService.authorize(sessionHolder.getUser(),
                                       TradePermissions.ViewOpenOrdersAction.name());
                TradingRpc.FindRootOrderIdResponse.Builder responseBuilder = TradingRpc.FindRootOrderIdResponse.newBuilder();
                OrderID orderId = new OrderID(inRequest.getOrderId());
                OrderID rootOrderId = reportService.getRootOrderIdFor(orderId);
                if(rootOrderId != null) {
                    responseBuilder.setRootOrderId(rootOrderId.getValue());
                }
                TradingRpc.FindRootOrderIdResponse response = responseBuilder.build();
                inResponseObserver.onNext(response);
                inResponseObserver.onCompleted();
            } catch (Exception e) {
                handleError(e,
                            inResponseObserver);
            }
        }
        /* (non-Javadoc)
         * @see org.marketcetera.trading.rpc.TradingRpcServiceGrpc.TradingRpcServiceImplBase#getPositionAsOf(org.marketcetera.trading.rpc.TradingRpc.GetPositionAsOfRequest, io.grpc.stub.StreamObserver)
         */
        @Override
        public void getPositionAsOf(GetPositionAsOfRequest inRequest,
                                    StreamObserver<GetPositionAsOfResponse> inResponseObserver)
        {
            try {
                SessionHolder<SessionClazz> sessionHolder = validateAndReturnSession(inRequest.getSessionId());
                TradingRpc.GetPositionAsOfResponse.Builder responseBuilder = TradingRpc.GetPositionAsOfResponse.newBuilder();
                SLF4JLoggerProxy.trace(TradeClientRpcService.this,
                                       "Received get position as of request {} from {}",
                                       inRequest,
                                       sessionHolder);
                authzService.authorize(sessionHolder.getUser(),
                                       TradePermissions.ViewPositionAction.name());
                Instrument instrument = TradeRpcUtil.getInstrument(inRequest.getInstrument()).orElse(null);
                Date timestamp = null;
                if(inRequest.hasTimestamp()) {
                    timestamp = new Date(Timestamps.toMillis(inRequest.getTimestamp()));
                }
                User user = userService.findByName(sessionHolder.getUser());
                BigDecimal result = reportService.getPositionAsOf(user,
                                                                  timestamp,
                                                                  instrument);
                SLF4JLoggerProxy.trace(TradeClientRpcService.this,
                                       "{} position for {}: {} as of {}",
                                       user,
                                       instrument,
                                       result,
                                       timestamp);
                BaseRpcUtil.getRpcQty(result).ifPresent(qty->responseBuilder.setPosition(qty));
                TradingRpc.GetPositionAsOfResponse response = responseBuilder.build();
                SLF4JLoggerProxy.trace(TradeClientRpcService.this,
                                       "Returning {}",
                                       response);
                inResponseObserver.onNext(response);
                inResponseObserver.onCompleted();
            } catch (Exception e) {
                handleError(e,
                            inResponseObserver);
            }
        }
        /* (non-Javadoc)
         * @see org.marketcetera.trading.rpc.TradingRpcServiceGrpc.TradingRpcServiceImplBase#getAllPositionsAsOf(org.marketcetera.trading.rpc.TradingRpc.GetAllPositionsAsOfRequest, io.grpc.stub.StreamObserver)
         */
        @Override
        public void getAllPositionsAsOf(GetAllPositionsAsOfRequest inRequest,
                                        StreamObserver<GetAllPositionsAsOfResponse> inResponseObserver)
        {
            try {
                SessionHolder<SessionClazz> sessionHolder = validateAndReturnSession(inRequest.getSessionId());
                SLF4JLoggerProxy.trace(TradeClientRpcService.this,
                                       "Received get all positions as of request {} from {}",
                                       inRequest,
                                       sessionHolder);
                authzService.authorize(sessionHolder.getUser(),
                                       TradePermissions.ViewPositionAction.name());
                TradingRpc.GetAllPositionsAsOfResponse.Builder responseBuilder = TradingRpc.GetAllPositionsAsOfResponse.newBuilder();
                Date timestamp = null;
                if(inRequest.hasTimestamp()) {
                    timestamp = new Date(Timestamps.toMillis(inRequest.getTimestamp()));
                }
                User user = userService.findByName(sessionHolder.getUser());
                Map<PositionKey<? extends Instrument>,BigDecimal> result = reportService.getAllPositionsAsOf(user,
                                                                                                             timestamp);
                SLF4JLoggerProxy.trace(TradeClientRpcService.this,
                                       "{} all positions as of {}: {}",
                                       user,
                                       timestamp,
                                       result);
                TradingTypesRpc.Position.Builder positionBuilder = TradingTypesRpc.Position.newBuilder();
                TradingTypesRpc.PositionKey.Builder positionKeyBuilder = TradingTypesRpc.PositionKey.newBuilder();
                for(Map.Entry<PositionKey<? extends Instrument>,BigDecimal> entry : result.entrySet()) {
                    PositionKey<? extends Instrument> key = entry.getKey();
                    BigDecimal value = entry.getValue();
                    if(key.getAccount() != null) {
                        positionKeyBuilder.setAccount(key.getAccount());
                    }
                    TradeRpcUtil.getRpcInstrument(key.getInstrument()).ifPresent(instrument->positionKeyBuilder.setInstrument(instrument));
                    if(key.getTraderId() != null) {
                        String traderName = String.valueOf(key.getTraderId());
                        try {
                            long traderId = Long.parseLong(key.getTraderId());
                            traderName = String.valueOf(traderId);
                            User trader = userService.findByUserId(new UserID(traderId));
                            if(trader != null) {
                                traderName = trader.getName();
                            }
                        } catch (NumberFormatException e) {
                            PlatformServices.handleException(TradeClientRpcService.this,
                                                             "Cannot convert trader id " + key.getTraderId() + " to a numerical ID",
                                                             e);
                        }
                        positionKeyBuilder.setTraderId(traderName);
                    }
                    positionBuilder.setPositionKey(positionKeyBuilder.build());
                    BaseRpcUtil.getRpcQty(value).ifPresent(qty->positionBuilder.setPosition(qty));
                    responseBuilder.addPosition(positionBuilder.build());
                    positionKeyBuilder.clear();
                    positionBuilder.clear();
                }
                TradingRpc.GetAllPositionsAsOfResponse response = responseBuilder.build();
                SLF4JLoggerProxy.trace(TradeClientRpcService.this,
                                       "Returning {}",
                                       response);
                inResponseObserver.onNext(response);
                inResponseObserver.onCompleted();
            } catch (Exception e) {
                handleError(e,
                            inResponseObserver);
            }
        }
        /* (non-Javadoc)
         * @see org.marketcetera.trading.rpc.TradingRpcServiceGrpc.TradingRpcServiceImplBase#getAllPositionsByRootAsOf(org.marketcetera.trading.rpc.TradingRpc.GetAllPositionsByRootAsOfRequest, io.grpc.stub.StreamObserver)
         */
        @Override
        public void getAllPositionsByRootAsOf(GetAllPositionsByRootAsOfRequest inRequest,
                                              StreamObserver<GetAllPositionsByRootAsOfResponse> inResponseObserver)
        {
            try {
                SessionHolder<SessionClazz> sessionHolder = validateAndReturnSession(inRequest.getSessionId());
                SLF4JLoggerProxy.trace(TradeClientRpcService.this,
                                       "Received get all positions by root as of request {} from {}",
                                       inRequest,
                                       sessionHolder);
                authzService.authorize(sessionHolder.getUser(),
                                       TradePermissions.ViewPositionAction.name());
                TradingRpc.GetAllPositionsByRootAsOfResponse.Builder responseBuilder = TradingRpc.GetAllPositionsByRootAsOfResponse.newBuilder();
                Date timestamp = null;
                if(inRequest.hasTimestamp()) {
                    timestamp = new Date(Timestamps.toMillis(inRequest.getTimestamp()));
                }
                User user = userService.findByName(sessionHolder.getUser());
                Map<PositionKey<Option>,BigDecimal> result = reportService.getOptionPositionsAsOf(user,
                                                                                                  timestamp,
                                                                                                  inRequest.getRootList().toArray(new String[0]));
                SLF4JLoggerProxy.trace(TradeClientRpcService.this,
                                       "{} all positions as of {}: {}",
                                       user,
                                       timestamp,
                                       result);
                TradingTypesRpc.Position.Builder positionBuilder = TradingTypesRpc.Position.newBuilder();
                TradingTypesRpc.PositionKey.Builder positionKeyBuilder = TradingTypesRpc.PositionKey.newBuilder();
                for(Map.Entry<PositionKey<Option>,BigDecimal> entry : result.entrySet()) {
                    PositionKey<Option> key = entry.getKey();
                    BigDecimal value = entry.getValue();
                    if(key.getAccount() != null) {
                        positionKeyBuilder.setAccount(key.getAccount());
                    }
                    TradeRpcUtil.getRpcInstrument(key.getInstrument()).ifPresent(instrument->positionKeyBuilder.setInstrument(instrument));
                    if(key.getTraderId() != null) {
                        String traderName = String.valueOf(key.getTraderId());
                        try {
                            long traderId = Long.parseLong(key.getTraderId());
                            traderName = String.valueOf(traderId);
                            User trader = userService.findByUserId(new UserID(traderId));
                            if(trader != null) {
                                traderName = trader.getName();
                            }
                        } catch (NumberFormatException e) {
                            PlatformServices.handleException(TradeClientRpcService.this,
                                                             "Cannot convert trader id " + key.getTraderId() + " to a numerical ID",
                                                             e);
                        }
                        positionKeyBuilder.setTraderId(traderName);
                    }
                    positionBuilder.setPositionKey(positionKeyBuilder.build());
                    BaseRpcUtil.getRpcQty(value).ifPresent(qty->positionBuilder.setPosition(qty));
                    responseBuilder.addPosition(positionBuilder.build());
                    positionKeyBuilder.clear();
                    positionBuilder.clear();
                }
                TradingRpc.GetAllPositionsByRootAsOfResponse response = responseBuilder.build();
                SLF4JLoggerProxy.trace(TradeClientRpcService.this,
                                       "Returning {}",
                                       response);
                inResponseObserver.onNext(response);
                inResponseObserver.onCompleted();
            } catch (Exception e) {
                handleError(e,
                            inResponseObserver);
            }
        }
        /* (non-Javadoc)
         * @see org.marketcetera.trading.rpc.TradingRpcServiceGrpc.TradingRpcServiceImplBase#addReport(org.marketcetera.trading.rpc.TradingRpc.AddReportRequest, io.grpc.stub.StreamObserver)
         */
        @Override
        public void addReport(AddReportRequest inRequest,
                              StreamObserver<AddReportResponse> inResponseObserver)
        {
            try {
                SessionHolder<SessionClazz> sessionHolder = validateAndReturnSession(inRequest.getSessionId());
                SLF4JLoggerProxy.trace(TradeClientRpcService.this,
                                       "Received add report request {} from {}",
                                       inRequest,
                                       sessionHolder);
                authzService.authorize(sessionHolder.getUser(),
                                       TradePermissions.AddReportAction.name());
                TradingRpc.AddReportResponse.Builder responseBuilder = TradingRpc.AddReportResponse.newBuilder();
                FIXMessageWrapper report = null;
                if(inRequest.hasMessage()) {
                    report = new FIXMessageWrapper(TradeRpcUtil.getFixMessage(inRequest.getMessage()));
                }
                BrokerID brokerId = TradeRpcUtil.getBrokerId(inRequest).orElse(null);
                User user = userService.findByName(sessionHolder.getUser());
                if(user == null) {
                    throw new IllegalArgumentException("Unknown user: " + user);
                }
                reportService.addReport(report,
                                        brokerId,
                                        user.getUserID());
                SLF4JLoggerProxy.trace(TradeClientRpcService.this,
                                       "{} added for {}/{}",
                                       report,
                                       user,
                                       brokerId);
                TradingRpc.AddReportResponse response = responseBuilder.build();
                SLF4JLoggerProxy.trace(TradeClientRpcService.this,
                                       "Returning {}",
                                       response);
                inResponseObserver.onNext(response);
                inResponseObserver.onCompleted();
            } catch (Exception e) {
                handleError(e,
                            inResponseObserver);
            }
        }
        /* (non-Javadoc)
         * @see org.marketcetera.trading.rpc.TradingRpcServiceGrpc.TradingRpcServiceImplBase#deleteReport(org.marketcetera.trading.rpc.TradingRpc.DeleteReportRequest, io.grpc.stub.StreamObserver)
         */
        @Override
        public void deleteReport(DeleteReportRequest inRequest,
                                 StreamObserver<DeleteReportResponse> inResponseObserver)
        {
            try {
                SessionHolder<SessionClazz> sessionHolder = validateAndReturnSession(inRequest.getSessionId());
                SLF4JLoggerProxy.trace(TradeClientRpcService.this,
                                       "Received delete report request {} from {}",
                                       inRequest,
                                       sessionHolder);
                authzService.authorize(sessionHolder.getUser(),
                                       TradePermissions.DeleteReportAction.name());
                TradingRpc.DeleteReportResponse.Builder responseBuilder = TradingRpc.DeleteReportResponse.newBuilder();
                ReportID reportId = new ReportID(Long.valueOf(inRequest.getReportId()));
                reportService.delete(reportId);
                SLF4JLoggerProxy.trace(TradeClientRpcService.this,
                                       "{} deleted for {}",
                                       reportId,
                                       sessionHolder.getUser());
                TradingRpc.DeleteReportResponse response = responseBuilder.build();
                SLF4JLoggerProxy.trace(TradeClientRpcService.this,
                                       "Returning {}",
                                       response);
                inResponseObserver.onNext(response);
                inResponseObserver.onCompleted();
            } catch (Exception e) {
                handleError(e,
                            inResponseObserver);
            }
        }
    }
    /**
     * Provides a connection between broker status requests and the server interface.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    private static class BrokerStatusListenerProxy
            extends BaseRpcUtil.AbstractServerListenerProxy<BrokerStatusListenerResponse>
            implements BrokerStatusListener
    {
        /* (non-Javadoc)
         * @see org.marketcetera.brokers.BrokerStatusListener#receiveBrokerStatus(org.marketcetera.brokers.BrokerStatus)
         */
        @Override
        public void receiveBrokerStatus(BrokerStatus inStatus)
        {
            TradeRpcUtil.setBrokerStatus(inStatus,
                                        responseBuilder);
            BrokerStatusListenerResponse response = responseBuilder.build();
            SLF4JLoggerProxy.trace(TradeClientRpcService.class,
                                   "{} received broker status {}, sending {}",
                                   getId(),
                                   inStatus,
                                   response);
            // TODO does the user have permissions to view this broker?
            getObserver().onNext(response);
            responseBuilder.clear();
        }
        /**
         * Create a new BrokerStatusListenerProxy instance.
         *
         * @param inId a <code>String</code> value
         * @param inObserver a <code>StreamObserver&lt;BrokerStatusListenerResponse&gt;</code> value
         */
        private BrokerStatusListenerProxy(String inId,
                                          StreamObserver<BrokerStatusListenerResponse> inObserver)
        {
            super(inId,
                  inObserver);
        }
        /**
         * builder used to construct messages
         */
        private final TradingRpc.BrokerStatusListenerResponse.Builder responseBuilder = TradingRpc.BrokerStatusListenerResponse.newBuilder();
    }
    /**
     * Wraps a {@link TradeMessageListener} with the RPC call from the client.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    private static class TradeMessageListenerProxy
            extends BaseRpcUtil.AbstractServerListenerProxy<TradeMessageListenerResponse>
            implements TradeMessageListener
    {
        /* (non-Javadoc)
         * @see org.marketcetera.trade.TradeMessageListener#receiveTradeMessage(org.marketcetera.trade.TradeMessage)
         */
        @Override
        public void receiveTradeMessage(TradeMessage inTradeMessage)
        {
            TradeRpcUtil.setTradeMessage(inTradeMessage,
                                        responseBuilder);
            TradeMessageListenerResponse response = responseBuilder.build();
            SLF4JLoggerProxy.trace(TradeClientRpcService.class,
                                   "{} received trade message {}, sending {}",
                                   getId(),
                                   inTradeMessage,
                                   response);
            // TODO does the user have permissions (including supervisor) to view this report?
            getObserver().onNext(response);
            responseBuilder.clear();
        }
        /**
         * Create a new TradeMessageListenerProxy instance.
         *
         * @param inId a <code>String</code> value
         * @param inObserver a <code>StreamObserver&lt;TradeMessageListenerResponse&gt;</code> value
         */
        private TradeMessageListenerProxy(String inId,
                                          StreamObserver<TradeMessageListenerResponse> inObserver)
        {
            super(inId,
                  inObserver);
        }
        /**
         * builder used to construct messages
         */
        private final TradingRpc.TradeMessageListenerResponse.Builder responseBuilder = TradingRpc.TradeMessageListenerResponse.newBuilder();
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
     * provides authorization services
     */
    @Autowired
    private AuthorizationService authzService;
    /**
     * provides report services
     */
    @Autowired
    private ReportService reportService;
    /**
     * provides symbol resolution services
     */
    @Autowired
    private SymbolResolverService symbolResolverService;
    /**
     * provides access to broker services
     */
    @Autowired
    private BrokerService brokerService;
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
     * provides access to order summary services
     */
    @Autowired
    private OrderSummaryService orderSummaryService;
    /**
     * provides the RPC service
     */
    private Service service;
    /**
     * description of this service
     */
    private final static String description = "Trade RPC Service";
    /**
     * holds trade message listeners by id
     */
    private final Cache<String,BaseRpcUtil.AbstractServerListenerProxy<?>> listenerProxiesById = CacheBuilder.newBuilder().build();
}
