package org.marketcetera.trade.rpc.server;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.marketcetera.admin.User;
import org.marketcetera.admin.UserFactory;
import org.marketcetera.admin.service.AuthorizationService;
import org.marketcetera.admin.service.UserService;
import org.marketcetera.brokers.service.BrokerService;
import org.marketcetera.core.PlatformServices;
import org.marketcetera.core.position.PositionKey;
import org.marketcetera.fix.ActiveFixSession;
import org.marketcetera.fix.FixPermissions;
import org.marketcetera.fix.FixRpcUtil;
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
import org.marketcetera.trade.AverageFillPrice;
import org.marketcetera.trade.BrokerID;
import org.marketcetera.trade.ExecutionReport;
import org.marketcetera.trade.ExecutionReportSummary;
import org.marketcetera.trade.FIXMessageWrapper;
import org.marketcetera.trade.Instrument;
import org.marketcetera.trade.Option;
import org.marketcetera.trade.Order;
import org.marketcetera.trade.OrderBase;
import org.marketcetera.trade.OrderID;
import org.marketcetera.trade.OrderSummary;
import org.marketcetera.trade.Report;
import org.marketcetera.trade.ReportID;
import org.marketcetera.trade.Suggestion;
import org.marketcetera.trade.SuggestionListener;
import org.marketcetera.trade.TradeMessage;
import org.marketcetera.trade.TradeMessageListener;
import org.marketcetera.trade.TradePermissions;
import org.marketcetera.trade.UserID;
import org.marketcetera.trade.rpc.TradeRpc;
import org.marketcetera.trade.rpc.TradeRpc.AddReportRequest;
import org.marketcetera.trade.rpc.TradeRpc.AddReportResponse;
import org.marketcetera.trade.rpc.TradeRpc.AddTradeMessageListenerRequest;
import org.marketcetera.trade.rpc.TradeRpc.DeleteReportRequest;
import org.marketcetera.trade.rpc.TradeRpc.DeleteReportResponse;
import org.marketcetera.trade.rpc.TradeRpc.FindRootOrderIdRequest;
import org.marketcetera.trade.rpc.TradeRpc.FindRootOrderIdResponse;
import org.marketcetera.trade.rpc.TradeRpc.GetAllPositionsAsOfRequest;
import org.marketcetera.trade.rpc.TradeRpc.GetAllPositionsAsOfResponse;
import org.marketcetera.trade.rpc.TradeRpc.GetAllPositionsByRootAsOfRequest;
import org.marketcetera.trade.rpc.TradeRpc.GetAllPositionsByRootAsOfResponse;
import org.marketcetera.trade.rpc.TradeRpc.GetAverageFillPricesRequest;
import org.marketcetera.trade.rpc.TradeRpc.GetAverageFillPricesResponse;
import org.marketcetera.trade.rpc.TradeRpc.GetFillsRequest;
import org.marketcetera.trade.rpc.TradeRpc.GetFillsResponse;
import org.marketcetera.trade.rpc.TradeRpc.GetLatestExecutionReportForOrderChainRequest;
import org.marketcetera.trade.rpc.TradeRpc.GetLatestExecutionReportForOrderChainResponse;
import org.marketcetera.trade.rpc.TradeRpc.GetPositionAsOfRequest;
import org.marketcetera.trade.rpc.TradeRpc.GetPositionAsOfResponse;
import org.marketcetera.trade.rpc.TradeRpc.GetReportsRequest;
import org.marketcetera.trade.rpc.TradeRpc.GetReportsResponse;
import org.marketcetera.trade.rpc.TradeRpc.OpenOrdersRequest;
import org.marketcetera.trade.rpc.TradeRpc.OpenOrdersResponse;
import org.marketcetera.trade.rpc.TradeRpc.ReadAvailableFixInitiatorSessionsRequest;
import org.marketcetera.trade.rpc.TradeRpc.ReadAvailableFixInitiatorSessionsResponse;
import org.marketcetera.trade.rpc.TradeRpc.RemoveTradeMessageListenerRequest;
import org.marketcetera.trade.rpc.TradeRpc.RemoveTradeMessageListenerResponse;
import org.marketcetera.trade.rpc.TradeRpc.ResolveSymbolRequest;
import org.marketcetera.trade.rpc.TradeRpc.ResolveSymbolResponse;
import org.marketcetera.trade.rpc.TradeRpc.TradeMessageListenerResponse;
import org.marketcetera.trade.rpc.TradeRpcServiceGrpc;
import org.marketcetera.trade.rpc.TradeRpcServiceGrpc.TradeRpcServiceImplBase;
import org.marketcetera.trade.rpc.TradeTypesRpc;
import org.marketcetera.trade.service.OrderSummaryService;
import org.marketcetera.trade.service.ReportService;
import org.marketcetera.trade.service.TradeService;
import org.marketcetera.trading.rpc.TradeRpcUtil;
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
 * Provides trade RPC server services.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class TradeRpcService<SessionClazz>
        extends AbstractRpcService<SessionClazz,TradeRpcServiceGrpc.TradeRpcServiceImplBase>
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
    protected TradeRpcServiceImplBase getService()
    {
        return service;
    }
    /**
     * Trade RPC Service implementation.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    private class Service
            extends TradeRpcServiceGrpc.TradeRpcServiceImplBase
    {
        /* (non-Javadoc)
         * @see org.marketcetera.trade.rpc.TradeRpcServiceGrpc.TradeRpcServiceImplBase#login(org.marketcetera.rpc.base.BaseRpc.LoginRequest, io.grpc.stub.StreamObserver)
         */
        @Override
        public void login(LoginRequest inRequest,
                          StreamObserver<LoginResponse> inResponseObserver)
        {
            TradeRpcService.this.doLogin(inRequest,
                                               inResponseObserver);
        }
        /* (non-Javadoc)
         * @see org.marketcetera.trade.rpc.TradeRpcServiceGrpc.TradeRpcServiceImplBase#logout(org.marketcetera.rpc.base.BaseRpc.LogoutRequest, io.grpc.stub.StreamObserver)
         */
        @Override
        public void logout(LogoutRequest inRequest,
                           StreamObserver<LogoutResponse> inResponseObserver)
        {
            TradeRpcService.this.doLogout(inRequest,
                                                inResponseObserver);
        }
        /* (non-Javadoc)
         * @see org.marketcetera.trade.rpc.TradeRpcServiceGrpc.TradeRpcServiceImplBase#heartbeat(org.marketcetera.rpc.base.BaseRpc.HeartbeatRequest, io.grpc.stub.StreamObserver)
         */
        @Override
        public void heartbeat(HeartbeatRequest inRequest,
                              StreamObserver<HeartbeatResponse> inResponseObserver)
        {
            TradeRpcService.this.doHeartbeat(inRequest,
                                                   inResponseObserver);
        }
        /* (non-Javadoc)
         * @see org.marketcetera.trade.rpc.TradeRpcServiceGrpc.TradeRpcServiceImplBase#getReports(org.marketcetera.trade.rpc.TradeRpc.GetReportsRequest, io.grpc.stub.StreamObserver)
         */
        @Override
        public void getReports(GetReportsRequest inRequest,
                               StreamObserver<GetReportsResponse> inResponseObserver)
        {
            try {
                SessionHolder<SessionClazz> sessionHolder = validateAndReturnSession(inRequest.getSessionId());
                authzService.authorize(sessionHolder.getUser(),
                                       TradePermissions.ViewReportAction.name());
                SLF4JLoggerProxy.trace(TradeRpcService.this,
                                       "Received {}",
                                       inRequest);
                TradeRpc.GetReportsResponse.Builder responseBuilder = TradeRpc.GetReportsResponse.newBuilder();
                PageRequest pageRequest = inRequest.hasPageRequest()?PagingRpcUtil.getPageRequest(inRequest.getPageRequest()):PageRequest.ALL;
                CollectionPageResponse<Report> reportPage = reportService.getReports(pageRequest);
                reportPage.getElements().forEach(report->responseBuilder.addReports(TradeRpcUtil.getRpcReport(report)));
                responseBuilder.setPageResponse(PagingRpcUtil.getPageResponse(pageRequest,
                                                                              reportPage));
                TradeRpc.GetReportsResponse response = responseBuilder.build();
                SLF4JLoggerProxy.trace(TradeRpcService.this,
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
         * @see org.marketcetera.trade.rpc.TradeRpcServiceGrpc.TradeRpcServiceImplBase#getFills(org.marketcetera.trade.rpc.TradeRpc.GetFillsRequest, io.grpc.stub.StreamObserver)
         */
        @Override
        public void getFills(GetFillsRequest inRequest,
                             StreamObserver<GetFillsResponse> inResponseObserver)
        {
            try {
                SessionHolder<SessionClazz> sessionHolder = validateAndReturnSession(inRequest.getSessionId());
                authzService.authorize(sessionHolder.getUser(),
                                       TradePermissions.ViewReportAction.name());
                SLF4JLoggerProxy.trace(TradeRpcService.this,
                                       "Received {}",
                                       inRequest);
                TradeRpc.GetFillsResponse.Builder responseBuilder = TradeRpc.GetFillsResponse.newBuilder();
                PageRequest pageRequest = inRequest.hasPageRequest()?PagingRpcUtil.getPageRequest(inRequest.getPageRequest()):PageRequest.ALL;
                CollectionPageResponse<ExecutionReportSummary> reportPage = reportService.getFills(pageRequest);
                reportPage.getElements().forEach(fill->responseBuilder.addFills(TradeRpcUtil.getRpcExecutionReportSummary(fill)));
                responseBuilder.setPageResponse(PagingRpcUtil.getPageResponse(pageRequest,
                                                                              reportPage));
                TradeRpc.GetFillsResponse response = responseBuilder.build();
                SLF4JLoggerProxy.trace(TradeRpcService.this,
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
         * @see org.marketcetera.trade.rpc.TradeRpcServiceGrpc.TradeRpcServiceImplBase#getAverageFillPrices(org.marketcetera.trade.rpc.TradeRpc.GetAverageFillPricesRequest, io.grpc.stub.StreamObserver)
         */
        @Override
        public void getAverageFillPrices(GetAverageFillPricesRequest inRequest,
                                         StreamObserver<GetAverageFillPricesResponse> inResponseObserver)
        {
            try {
                SessionHolder<SessionClazz> sessionHolder = validateAndReturnSession(inRequest.getSessionId());
                authzService.authorize(sessionHolder.getUser(),
                                       TradePermissions.ViewReportAction.name());
                SLF4JLoggerProxy.trace(TradeRpcService.this,
                                       "Received {}",
                                       inRequest);
                TradeRpc.GetAverageFillPricesResponse.Builder responseBuilder = TradeRpc.GetAverageFillPricesResponse.newBuilder();
                PageRequest pageRequest = inRequest.hasPageRequest()?PagingRpcUtil.getPageRequest(inRequest.getPageRequest()):PageRequest.ALL;
                CollectionPageResponse<AverageFillPrice> reportPage = reportService.getAverageFillPrices(pageRequest);
                reportPage.getElements().forEach(averageFillPrice->responseBuilder.addAverageFillPrices(TradeRpcUtil.getRpcAverageFillPrice(averageFillPrice)));
                responseBuilder.setPageResponse(PagingRpcUtil.getPageResponse(pageRequest,
                                                                              reportPage));
                TradeRpc.GetAverageFillPricesResponse response = responseBuilder.build();
                SLF4JLoggerProxy.trace(TradeRpcService.this,
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
         * @see org.marketcetera.trade.rpc.TradeRpcServiceGrpc.TradeRpcServiceImplBase#getOpenOrders(org.marketcetera.trade.rpc.TradeRpc.OpenOrdersRequest, io.grpc.stub.StreamObserver)
         */
        @Override
        public void getOpenOrders(OpenOrdersRequest inRequest,
                                  StreamObserver<OpenOrdersResponse> inResponseObserver)
        {
            try {
                SessionHolder<SessionClazz> sessionHolder = validateAndReturnSession(inRequest.getSessionId());
                authzService.authorize(sessionHolder.getUser(),
                                       TradePermissions.ViewOpenOrdersAction.name());
                SLF4JLoggerProxy.trace(TradeRpcService.this,
                                       "Received open order request {}",
                                       inRequest);
                TradeRpc.OpenOrdersResponse.Builder responseBuilder = TradeRpc.OpenOrdersResponse.newBuilder();
                PageRequest pageRequest = inRequest.hasPageRequest()?PagingRpcUtil.getPageRequest(inRequest.getPageRequest()):PageRequest.ALL;
                CollectionPageResponse<? extends OrderSummary> orderSummaryPage = orderSummaryService.findOpenOrders(pageRequest);
                orderSummaryPage.getElements().forEach(value->responseBuilder.addOrders(TradeRpcUtil.getRpcOrderSummary(value)));
                responseBuilder.setPageResponse(PagingRpcUtil.getPageResponse(pageRequest,
                                                                              orderSummaryPage));
                TradeRpc.OpenOrdersResponse response = responseBuilder.build();
                SLF4JLoggerProxy.trace(TradeRpcService.this,
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
         * @see org.marketcetera.trade.rpc.TradeRpcServiceGrpc.TradeRpcServiceImplBase#getLatestExecutionReportForOrderChain(org.marketcetera.trade.rpc.TradeRpc.GetLatestExecutionReportForOrderChainRequest, io.grpc.stub.StreamObserver)
         */
        @Override
        public void getLatestExecutionReportForOrderChain(GetLatestExecutionReportForOrderChainRequest inRequest,
                                                          StreamObserver<GetLatestExecutionReportForOrderChainResponse> inResponseObserver)
        {
            try {
                SessionHolder<SessionClazz> sessionHolder = validateAndReturnSession(inRequest.getSessionId());
                authzService.authorize(sessionHolder.getUser(),
                                       TradePermissions.ViewReportAction.name());
                SLF4JLoggerProxy.trace(TradeRpcService.this,
                                       "Received {}",
                                       inRequest);
                TradeRpc.GetLatestExecutionReportForOrderChainResponse.Builder responseBuilder = TradeRpc.GetLatestExecutionReportForOrderChainResponse.newBuilder();
                Optional<ExecutionReport> executionReportOption = reportService.getLatestExecutionReportForOrderChain(new OrderID(inRequest.getOrderId()));
                executionReportOption.ifPresent(executionReport -> responseBuilder.setExecutionReport(TradeRpcUtil.getRpcTradeMessage(executionReport)));
                TradeRpc.GetLatestExecutionReportForOrderChainResponse response = responseBuilder.build();
                SLF4JLoggerProxy.trace(TradeRpcService.this,
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
         * @see org.marketcetera.trade.rpc.TradeRpcServiceGrpc.TradeRpcServiceImplBase#sendSuggestion(org.marketcetera.trade.rpc.TradeRpc.SendSuggestionRequest, io.grpc.stub.StreamObserver)
         */
        @Override
        public void sendSuggestion(TradeRpc.SendSuggestionRequest inRequest,
                                   StreamObserver<TradeRpc.SendSuggestionResponse> inResponseObserver)
        {
            try {
                SessionHolder<SessionClazz> sessionHolder = validateAndReturnSession(inRequest.getSessionId());
                SLF4JLoggerProxy.trace(TradeRpcService.this,
                                       "Received send suggestion request {} from {}",
                                       inRequest,
                                       sessionHolder);
                authzService.authorize(sessionHolder.getUser(),
                                       TradePermissions.SendSuggestionAction.name());
                TradeRpc.SendSuggestionResponse.Builder responseBuilder = TradeRpc.SendSuggestionResponse.newBuilder();
                for(TradeTypesRpc.Suggestion rpcSuggestion : inRequest.getSuggestionList()) {
                    try {
                        Suggestion matpSuggestion = TradeRpcUtil.getSuggestion(rpcSuggestion,
                                                                               userFactory);
                        // override the user that might (or might not) be set in the suggestion and set the owner to the caller
                        User user = userService.findByName(sessionHolder.getUser());
                        matpSuggestion.setUser(user);
                        tradeService.reportSuggestion(matpSuggestion);
                    } catch (Exception e) {
                        SLF4JLoggerProxy.warn(TradeRpcService.this,
                                              e,
                                              "Unable to submit suggestion {}",
                                              rpcSuggestion);
                    }
                }
                TradeRpc.SendSuggestionResponse response = responseBuilder.build();
                inResponseObserver.onNext(response);
                inResponseObserver.onCompleted();
            } catch (Exception e) {
                handleError(e,
                            inResponseObserver);
            }
        }
        /* (non-Javadoc)
         * @see org.marketcetera.trade.rpc.TradeRpcServiceGrpc.TradeRpcServiceImplBase#sendSuggestions(org.marketcetera.trade.rpc.TradeRpc.SendOrderRequest, io.grpc.stub.StreamObserver)
         */
        @Override
        public void sendOrders(TradeRpc.SendOrderRequest inRequest,
                               StreamObserver<TradeRpc.SendOrderResponse> inResponseObserver)
        {
            try {
                SessionHolder<SessionClazz> sessionHolder = validateAndReturnSession(inRequest.getSessionId());
                SLF4JLoggerProxy.trace(TradeRpcService.this,
                                       "Received send order request {} from {}",
                                       inRequest,
                                       sessionHolder);
                authzService.authorize(sessionHolder.getUser(),
                                       TradePermissions.SendOrderAction.name());
                TradeRpc.SendOrderResponse.Builder responseBuilder = TradeRpc.SendOrderResponse.newBuilder();
                TradeRpc.OrderResponse.Builder orderResponseBuilder = TradeRpc.OrderResponse.newBuilder();
                for(TradeTypesRpc.Order rpcOrder : inRequest.getOrderList()) {
                    try {
                        Order matpOrder = TradeRpcUtil.getOrder(rpcOrder);
                        TradeRpcUtil.setOrderId(matpOrder,
                                                orderResponseBuilder);
                        User user = userService.findByName(sessionHolder.getUser());
                        tradeService.sendOrder(user,
                                               matpOrder);
                        String orderId = unknownOrderId;
                        if(matpOrder instanceof OrderBase) {
                            OrderID matpOrderId = ((OrderBase)matpOrder).getOrderID();
                            if(matpOrderId != null) {
                                orderId = matpOrderId.getValue();
                            }
                        }
                        orderResponseBuilder.setOrderid(orderId);
                    } catch (Exception e) {
                        SLF4JLoggerProxy.warn(TradeRpcService.this,
                                              e,
                                              "Unable to submit order {}",
                                              rpcOrder);
                        
                    }
                    responseBuilder.addOrderResponse(orderResponseBuilder.build());
                    orderResponseBuilder.clear();
                }
                TradeRpc.SendOrderResponse response = responseBuilder.build();
                inResponseObserver.onNext(response);
                inResponseObserver.onCompleted();
            } catch (Exception e) {
                handleError(e,
                            inResponseObserver);
            }
        }
        /* (non-Javadoc)
         * @see org.marketcetera.trade.rpc.TradeRpcServiceGrpc.TradeRpcServiceImplBase#resolveSymbol(org.marketcetera.trade.rpc.TradeRpc.ResolveSymbolRequest, io.grpc.stub.StreamObserver)
         */
        @Override
        public void resolveSymbol(ResolveSymbolRequest inRequest,
                                  StreamObserver<ResolveSymbolResponse> inResponseObserver)
        {
            try {
                SessionHolder<SessionClazz> sessionHolder = validateAndReturnSession(inRequest.getSessionId());
                TradeRpc.ResolveSymbolResponse.Builder responseBuilder = TradeRpc.ResolveSymbolResponse.newBuilder();
                SLF4JLoggerProxy.trace(TradeRpcService.this,
                                       "Received resolve symbol request {} from {}",
                                       inRequest,
                                       sessionHolder);
                Instrument instrument = symbolResolverService.resolveSymbol(inRequest.getSymbol());
                TradeRpcUtil.setInstrument(instrument,
                                          responseBuilder);
                TradeRpc.ResolveSymbolResponse response = responseBuilder.build();
                inResponseObserver.onNext(response);
                inResponseObserver.onCompleted();
            } catch (Exception e) {
                handleError(e,
                            inResponseObserver);
            }
        }
        /* (non-Javadoc)
         * @see org.marketcetera.trade.rpc.TradeRpcServiceGrpc.TradeRpcServiceImplBase#addSuggestionListener(org.marketcetera.trade.rpc.TradeRpc.AddSuggestionListenerRequest, io.grpc.stub.StreamObserver)
         */
        @Override
        public void addSuggestionListener(TradeRpc.AddSuggestionListenerRequest inRequest,
                                          StreamObserver<TradeRpc.SuggestionListenerResponse> inResponseObserver)
        {
            try {
                validateAndReturnSession(inRequest.getSessionId());
                SLF4JLoggerProxy.trace(TradeRpcService.this,
                                       "Received add suggestion listener request {}",
                                       inRequest);
                String listenerId = inRequest.getListenerId();
                BaseRpcUtil.AbstractServerListenerProxy<?> suggestionListenerProxy = listenerProxiesById.getIfPresent(listenerId);
                if(suggestionListenerProxy == null) {
                    suggestionListenerProxy = new SuggestionListenerProxy(listenerId,
                                                                          inResponseObserver);
                    listenerProxiesById.put(suggestionListenerProxy.getId(),
                                            suggestionListenerProxy);
                    tradeService.addSuggestionListener((SuggestionListener)suggestionListenerProxy);
                }
            } catch (Exception e) {
                handleError(e,
                            inResponseObserver);
            }
        }
        /* (non-Javadoc)
         * @see org.marketcetera.trade.rpc.TradeRpcServiceGrpc.TradeRpcServiceImplBase#removeSuggestionListener(org.marketcetera.trade.rpc.TradeRpc.RemoveSuggestionListenerRequest, io.grpc.stub.StreamObserver)
         */
        @Override
        public void removeSuggestionListener(TradeRpc.RemoveSuggestionListenerRequest inRequest,
                                             StreamObserver<TradeRpc.RemoveSuggestionListenerResponse> inResponseObserver)
        {
            try {
                validateAndReturnSession(inRequest.getSessionId());
                SLF4JLoggerProxy.trace(TradeRpcService.this,
                                       "Received remove suggestion listener request {}",
                                       inRequest);
                String listenerId = inRequest.getListenerId();
                BaseRpcUtil.AbstractServerListenerProxy<?> suggestionListenerProxy = listenerProxiesById.getIfPresent(listenerId);
                listenerProxiesById.invalidate(listenerId);
                if(suggestionListenerProxy != null) {
                    tradeService.removeSuggestionListener((SuggestionListener)suggestionListenerProxy);
                    suggestionListenerProxy.close();
                }
                TradeRpc.RemoveSuggestionListenerResponse.Builder responseBuilder = TradeRpc.RemoveSuggestionListenerResponse.newBuilder();
                TradeRpc.RemoveSuggestionListenerResponse response = responseBuilder.build();
                SLF4JLoggerProxy.trace(TradeRpcService.this,
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
         * @see org.marketcetera.trade.rpc.TradeRpcServiceGrpc.TradeRpcServiceImplBase#addTradeMessageListener(org.marketcetera.trade.rpc.TradeRpc.AddTradeMessageListenerRequest, io.grpc.stub.StreamObserver)
         */
        @Override
        public void addTradeMessageListener(AddTradeMessageListenerRequest inRequest,
                                            StreamObserver<TradeMessageListenerResponse> inResponseObserver)
        {
            try {
                validateAndReturnSession(inRequest.getSessionId());
                SLF4JLoggerProxy.trace(TradeRpcService.this,
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
         * @see org.marketcetera.trade.rpc.TradeRpcServiceGrpc.TradeRpcServiceImplBase#removeTradeMessageListener(org.marketcetera.trade.rpc.TradeRpc.RemoveTradeMessageListenerRequest, io.grpc.stub.StreamObserver)
         */
        @Override
        public void removeTradeMessageListener(RemoveTradeMessageListenerRequest inRequest,
                                               StreamObserver<RemoveTradeMessageListenerResponse> inResponseObserver)
        {
            try {
                validateAndReturnSession(inRequest.getSessionId());
                SLF4JLoggerProxy.trace(TradeRpcService.this,
                                       "Received remove trade message listener request {}",
                                       inRequest);
                String listenerId = inRequest.getListenerId();
                BaseRpcUtil.AbstractServerListenerProxy<?> tradeMessageListenerProxy = listenerProxiesById.getIfPresent(listenerId);
                listenerProxiesById.invalidate(listenerId);
                if(tradeMessageListenerProxy != null) {
                    tradeService.removeTradeMessageListener((TradeMessageListener)tradeMessageListenerProxy);
                    tradeMessageListenerProxy.close();
                }
                TradeRpc.RemoveTradeMessageListenerResponse.Builder responseBuilder = TradeRpc.RemoveTradeMessageListenerResponse.newBuilder();
                TradeRpc.RemoveTradeMessageListenerResponse response = responseBuilder.build();
                SLF4JLoggerProxy.trace(TradeRpcService.this,
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
         * @see org.marketcetera.trade.rpc.TradeRpcServiceGrpc.TradeRpcServiceImplBase#findRootOrderId(org.marketcetera.trade.rpc.TradeRpc.FindRootOrderIdRequest, io.grpc.stub.StreamObserver)
         */
        @Override
        public void findRootOrderId(FindRootOrderIdRequest inRequest,
                                    StreamObserver<FindRootOrderIdResponse> inResponseObserver)
        {
            try {
                SessionHolder<SessionClazz> sessionHolder = validateAndReturnSession(inRequest.getSessionId());
                SLF4JLoggerProxy.trace(TradeRpcService.this,
                                       "Received find root order id request {} from {}",
                                       inRequest,
                                       sessionHolder);
                authzService.authorize(sessionHolder.getUser(),
                                       TradePermissions.ViewOpenOrdersAction.name());
                TradeRpc.FindRootOrderIdResponse.Builder responseBuilder = TradeRpc.FindRootOrderIdResponse.newBuilder();
                OrderID orderId = new OrderID(inRequest.getOrderId());
                OrderID rootOrderId = reportService.getRootOrderIdFor(orderId);
                if(rootOrderId != null) {
                    responseBuilder.setRootOrderId(rootOrderId.getValue());
                }
                TradeRpc.FindRootOrderIdResponse response = responseBuilder.build();
                inResponseObserver.onNext(response);
                inResponseObserver.onCompleted();
            } catch (Exception e) {
                handleError(e,
                            inResponseObserver);
            }
        }
        /* (non-Javadoc)
         * @see org.marketcetera.trade.rpc.TradeRpcServiceGrpc.TradeRpcServiceImplBase#getPositionAsOf(org.marketcetera.trade.rpc.TradeRpc.GetPositionAsOfRequest, io.grpc.stub.StreamObserver)
         */
        @Override
        public void getPositionAsOf(GetPositionAsOfRequest inRequest,
                                    StreamObserver<GetPositionAsOfResponse> inResponseObserver)
        {
            try {
                SessionHolder<SessionClazz> sessionHolder = validateAndReturnSession(inRequest.getSessionId());
                TradeRpc.GetPositionAsOfResponse.Builder responseBuilder = TradeRpc.GetPositionAsOfResponse.newBuilder();
                SLF4JLoggerProxy.trace(TradeRpcService.this,
                                       "Received get position as of request {} from {}",
                                       inRequest,
                                       sessionHolder);
                authzService.authorize(sessionHolder.getUser(),
                                       TradePermissions.ViewPositionAction.name());
                Instrument instrument = TradeRpcUtil.getInstrument(inRequest.getInstrument()).orElse(null);
                Date timestamp = null;
                if(inRequest.hasTimestamp()) {
                    timestamp = Date.from(Instant.ofEpochSecond(inRequest.getTimestamp().getSeconds(),
                                                                inRequest.getTimestamp().getNanos()));
                }
                User user = userService.findByName(sessionHolder.getUser());
                BigDecimal result = reportService.getPositionAsOf(user,
                                                                  timestamp,
                                                                  instrument);
                SLF4JLoggerProxy.trace(TradeRpcService.this,
                                       "{} position for {}: {} as of {}",
                                       user,
                                       instrument,
                                       result,
                                       timestamp);
                BaseRpcUtil.getRpcQty(result).ifPresent(qty->responseBuilder.setPosition(qty));
                TradeRpc.GetPositionAsOfResponse response = responseBuilder.build();
                SLF4JLoggerProxy.trace(TradeRpcService.this,
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
         * @see org.marketcetera.trade.rpc.TradeRpcServiceGrpc.TradeRpcServiceImplBase#getAllPositionsAsOf(org.marketcetera.trade.rpc.TradeRpc.GetAllPositionsAsOfRequest, io.grpc.stub.StreamObserver)
         */
        @Override
        public void getAllPositionsAsOf(GetAllPositionsAsOfRequest inRequest,
                                        StreamObserver<GetAllPositionsAsOfResponse> inResponseObserver)
        {
            try {
                SessionHolder<SessionClazz> sessionHolder = validateAndReturnSession(inRequest.getSessionId());
                SLF4JLoggerProxy.trace(TradeRpcService.this,
                                       "Received get all positions as of request {} from {}",
                                       inRequest,
                                       sessionHolder);
                authzService.authorize(sessionHolder.getUser(),
                                       TradePermissions.ViewPositionAction.name());
                TradeRpc.GetAllPositionsAsOfResponse.Builder responseBuilder = TradeRpc.GetAllPositionsAsOfResponse.newBuilder();
                Date timestamp = null;
                if(inRequest.hasTimestamp()) {
                    timestamp = Date.from(Instant.ofEpochSecond(inRequest.getTimestamp().getSeconds(),
                                                                inRequest.getTimestamp().getNanos()));
                }
                User user = userService.findByName(sessionHolder.getUser());
                Map<PositionKey<? extends Instrument>,BigDecimal> result = reportService.getAllPositionsAsOf(user,
                                                                                                             timestamp);
                SLF4JLoggerProxy.trace(TradeRpcService.this,
                                       "{} all positions as of {}: {}",
                                       user,
                                       timestamp,
                                       result);
                TradeTypesRpc.Position.Builder positionBuilder = TradeTypesRpc.Position.newBuilder();
                TradeTypesRpc.PositionKey.Builder positionKeyBuilder = TradeTypesRpc.PositionKey.newBuilder();
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
                            PlatformServices.handleException(TradeRpcService.this,
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
                TradeRpc.GetAllPositionsAsOfResponse response = responseBuilder.build();
                SLF4JLoggerProxy.trace(TradeRpcService.this,
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
         * @see org.marketcetera.trade.rpc.TradeRpcServiceGrpc.TradeRpcServiceImplBase#getAllPositionsByRootAsOf(org.marketcetera.trade.rpc.TradeRpc.GetAllPositionsByRootAsOfRequest, io.grpc.stub.StreamObserver)
         */
        @Override
        public void getAllPositionsByRootAsOf(GetAllPositionsByRootAsOfRequest inRequest,
                                              StreamObserver<GetAllPositionsByRootAsOfResponse> inResponseObserver)
        {
            try {
                SessionHolder<SessionClazz> sessionHolder = validateAndReturnSession(inRequest.getSessionId());
                SLF4JLoggerProxy.trace(TradeRpcService.this,
                                       "Received get all positions by root as of request {} from {}",
                                       inRequest,
                                       sessionHolder);
                authzService.authorize(sessionHolder.getUser(),
                                       TradePermissions.ViewPositionAction.name());
                TradeRpc.GetAllPositionsByRootAsOfResponse.Builder responseBuilder = TradeRpc.GetAllPositionsByRootAsOfResponse.newBuilder();
                Date timestamp = null;
                if(inRequest.hasTimestamp()) {
                    timestamp = Date.from(Instant.ofEpochSecond(inRequest.getTimestamp().getSeconds(),
                                                                inRequest.getTimestamp().getNanos()));
                }
                User user = userService.findByName(sessionHolder.getUser());
                Map<PositionKey<Option>,BigDecimal> result = reportService.getOptionPositionsAsOf(user,
                                                                                                  timestamp,
                                                                                                  inRequest.getRootList().toArray(new String[0]));
                SLF4JLoggerProxy.trace(TradeRpcService.this,
                                       "{} all positions as of {}: {}",
                                       user,
                                       timestamp,
                                       result);
                TradeTypesRpc.Position.Builder positionBuilder = TradeTypesRpc.Position.newBuilder();
                TradeTypesRpc.PositionKey.Builder positionKeyBuilder = TradeTypesRpc.PositionKey.newBuilder();
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
                            PlatformServices.handleException(TradeRpcService.this,
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
                TradeRpc.GetAllPositionsByRootAsOfResponse response = responseBuilder.build();
                SLF4JLoggerProxy.trace(TradeRpcService.this,
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
         * @see org.marketcetera.trade.rpc.TradeRpcServiceGrpc.TradeRpcServiceImplBase#addReport(org.marketcetera.trade.rpc.TradeRpc.AddReportRequest, io.grpc.stub.StreamObserver)
         */
        @Override
        public void addReport(AddReportRequest inRequest,
                              StreamObserver<AddReportResponse> inResponseObserver)
        {
            try {
                SessionHolder<SessionClazz> sessionHolder = validateAndReturnSession(inRequest.getSessionId());
                SLF4JLoggerProxy.trace(TradeRpcService.this,
                                       "Received add report request {} from {}",
                                       inRequest,
                                       sessionHolder);
                authzService.authorize(sessionHolder.getUser(),
                                       TradePermissions.AddReportAction.name());
                TradeRpc.AddReportResponse.Builder responseBuilder = TradeRpc.AddReportResponse.newBuilder();
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
                SLF4JLoggerProxy.trace(TradeRpcService.this,
                                       "{} added for {}/{}",
                                       report,
                                       user,
                                       brokerId);
                TradeRpc.AddReportResponse response = responseBuilder.build();
                SLF4JLoggerProxy.trace(TradeRpcService.this,
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
         * @see org.marketcetera.trade.rpc.TradeRpcServiceGrpc.TradeRpcServiceImplBase#deleteReport(org.marketcetera.trade.rpc.TradeRpc.DeleteReportRequest, io.grpc.stub.StreamObserver)
         */
        @Override
        public void deleteReport(DeleteReportRequest inRequest,
                                 StreamObserver<DeleteReportResponse> inResponseObserver)
        {
            try {
                SessionHolder<SessionClazz> sessionHolder = validateAndReturnSession(inRequest.getSessionId());
                SLF4JLoggerProxy.trace(TradeRpcService.this,
                                       "Received delete report request {} from {}",
                                       inRequest,
                                       sessionHolder);
                authzService.authorize(sessionHolder.getUser(),
                                       TradePermissions.DeleteReportAction.name());
                TradeRpc.DeleteReportResponse.Builder responseBuilder = TradeRpc.DeleteReportResponse.newBuilder();
                ReportID reportId = new ReportID(Long.valueOf(inRequest.getReportId()));
                reportService.delete(reportId);
                SLF4JLoggerProxy.trace(TradeRpcService.this,
                                       "{} deleted for {}",
                                       reportId,
                                       sessionHolder.getUser());
                TradeRpc.DeleteReportResponse response = responseBuilder.build();
                SLF4JLoggerProxy.trace(TradeRpcService.this,
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
         * @see org.marketcetera.trade.rpc.TradeRpcServiceGrpc.TradeRpcServiceImplBase#readAvailableFixInitiatorSessions(org.marketcetera.trade.rpc.TradeRpc.ReadAvailableFixInitiatorSessionsRequest, io.grpc.stub.StreamObserver)
         */
        @Override
        public void readAvailableFixInitiatorSessions(ReadAvailableFixInitiatorSessionsRequest inRequest,
                                                      StreamObserver<ReadAvailableFixInitiatorSessionsResponse> inResponseObserver)
        {
            try {
                SessionHolder<SessionClazz> sessionHolder = validateAndReturnSession(inRequest.getSessionId());
                SLF4JLoggerProxy.trace(TradeRpcService.this,
                                       "Received read available FIX initiator sessions request {} from {}",
                                       inRequest,
                                       sessionHolder);
                authzService.authorize(sessionHolder.getUser(),
                                       FixPermissions.ViewBrokerStatusAction.name());
                TradeRpc.ReadAvailableFixInitiatorSessionsResponse.Builder responseBuilder = TradeRpc.ReadAvailableFixInitiatorSessionsResponse.newBuilder();
                Collection<ActiveFixSession> pagedResponse = brokerService.getAvailableFixInitiatorSessions();
                SLF4JLoggerProxy.trace(TradeRpcService.this,
                                       "Query returned {}",
                                       pagedResponse);
                if(pagedResponse != null) {
                    for(ActiveFixSession activeFixSession : pagedResponse) {
                        FixRpcUtil.getRpcActiveFixSession(activeFixSession).ifPresent(rpcFixSession->responseBuilder.addFixSession(rpcFixSession));
                    }
                }
                TradeRpc.ReadAvailableFixInitiatorSessionsResponse response = responseBuilder.build();
                SLF4JLoggerProxy.trace(TradeRpcService.this,
                                       "Returning {}",
                                       response);
                inResponseObserver.onNext(response);
                inResponseObserver.onCompleted();
            } catch (Exception e) {
                if(e instanceof StatusRuntimeException) {
                    throw (StatusRuntimeException)e;
                }
                throw new StatusRuntimeException(Status.INVALID_ARGUMENT.withCause(e).withDescription(ExceptionUtils.getRootCauseMessage(e)));
            }
        }
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
            SLF4JLoggerProxy.trace(TradeRpcService.class,
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
        private final TradeRpc.TradeMessageListenerResponse.Builder responseBuilder = TradeRpc.TradeMessageListenerResponse.newBuilder();
    }
    /**
     * Wraps a {@link SuggestionListener} with the RPC call from the client.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    private static class SuggestionListenerProxy
            extends BaseRpcUtil.AbstractServerListenerProxy<TradeRpc.SuggestionListenerResponse>
            implements SuggestionListener
    {
        /* (non-Javadoc)
         * @see org.marketcetera.trade.TradeMessageListener#receiveTradeMessage(org.marketcetera.trade.TradeMessage)
         */
        @Override
        public void receiveSuggestion(Suggestion inSuggestion)
        {
            TradeRpcUtil.setSuggestion(inSuggestion,
                                       responseBuilder);
            TradeRpc.SuggestionListenerResponse response = responseBuilder.build();
            SLF4JLoggerProxy.trace(TradeRpcService.class,
                                   "{} received suggestion {}, sending {}",
                                   getId(),
                                   inSuggestion,
                                   response);
            // TODO does the user have permissions (including supervisor) to view this report?
            getObserver().onNext(response);
            responseBuilder.clear();
        }
        /**
         * Create a new SuggestionListenerProxy instance.
         *
         * @param inId a <code>String</code> value
         * @param inObserver a <code>StreamObserver&lt;TradeRpc.SuggestionListenerResponse&gt;</code> value
         */
        private SuggestionListenerProxy(String inId,
                                        StreamObserver<TradeRpc.SuggestionListenerResponse> inObserver)
        {
            super(inId,
                  inObserver);
        }
        /**
         * builder used to construct messages
         */
        private final TradeRpc.SuggestionListenerResponse.Builder responseBuilder = TradeRpc.SuggestionListenerResponse.newBuilder();
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
     * privates access to user services
     */
    @Autowired
    private UserService userService;
    /**
     * creates {@link User} objects
     */
    @Autowired
    private UserFactory userFactory;
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
     * provides access to core broker services
     */
    @Autowired
    private BrokerService brokerService;
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
    /**
     * order id for unknown orders
     */
    private final static String unknownOrderId = "unknown";
}
