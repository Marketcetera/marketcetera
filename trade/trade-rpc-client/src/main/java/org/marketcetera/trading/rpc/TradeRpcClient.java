package org.marketcetera.trading.rpc;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import org.marketcetera.admin.User;
import org.marketcetera.admin.UserFactory;
import org.marketcetera.cluster.ClusterData;
import org.marketcetera.cluster.ClusterDataFactory;
import org.marketcetera.core.PlatformServices;
import org.marketcetera.core.Util;
import org.marketcetera.core.Version;
import org.marketcetera.core.VersionInfo;
import org.marketcetera.core.position.PositionKey;
import org.marketcetera.event.HasFIXMessage;
import org.marketcetera.fix.ActiveFixSession;
import org.marketcetera.fix.FixAdminRpc;
import org.marketcetera.fix.FixRpcUtil;
import org.marketcetera.fix.FixSession;
import org.marketcetera.fix.MutableActiveFixSessionFactory;
import org.marketcetera.fix.MutableFixSessionFactory;
import org.marketcetera.persist.CollectionPageResponse;
import org.marketcetera.persist.PageRequest;
import org.marketcetera.rpc.base.BaseRpc;
import org.marketcetera.rpc.base.BaseRpc.HeartbeatRequest;
import org.marketcetera.rpc.base.BaseRpc.LoginResponse;
import org.marketcetera.rpc.base.BaseRpc.LogoutResponse;
import org.marketcetera.rpc.base.BaseRpcUtil;
import org.marketcetera.rpc.base.BaseRpcUtil.AbstractClientListenerProxy;
import org.marketcetera.rpc.client.AbstractRpcClient;
import org.marketcetera.rpc.paging.PagingRpcUtil;
import org.marketcetera.trade.BrokerID;
import org.marketcetera.trade.ExecutionReport;
import org.marketcetera.trade.FIXOrder;
import org.marketcetera.trade.Instrument;
import org.marketcetera.trade.MutableOrderSummary;
import org.marketcetera.trade.MutableOrderSummaryFactory;
import org.marketcetera.trade.MutableReport;
import org.marketcetera.trade.MutableReportFactory;
import org.marketcetera.trade.NewOrReplaceOrder;
import org.marketcetera.trade.Option;
import org.marketcetera.trade.Order;
import org.marketcetera.trade.OrderBase;
import org.marketcetera.trade.OrderCancel;
import org.marketcetera.trade.OrderID;
import org.marketcetera.trade.OrderReplace;
import org.marketcetera.trade.OrderSingle;
import org.marketcetera.trade.OrderSummary;
import org.marketcetera.trade.RelatedOrder;
import org.marketcetera.trade.Report;
import org.marketcetera.trade.ReportID;
import org.marketcetera.trade.TradeMessage;
import org.marketcetera.trade.TradeMessageListener;
import org.marketcetera.trade.client.SendOrderResponse;
import org.marketcetera.trade.client.TradeClient;
import org.marketcetera.trade.rpc.TradeRpc;
import org.marketcetera.trade.rpc.TradeRpc.TradeMessageListenerResponse;
import org.marketcetera.trade.rpc.TradeRpcServiceGrpc;
import org.marketcetera.trade.rpc.TradeRpcServiceGrpc.TradeRpcServiceBlockingStub;
import org.marketcetera.trade.rpc.TradeRpcServiceGrpc.TradeRpcServiceStub;
import org.marketcetera.trade.rpc.TradeTypesRpc;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.ws.tags.AppId;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.protobuf.Timestamp;

import io.grpc.Channel;

/* $License$ */

/**
 * Provides an RPC {@link TradeClient} implementation.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class TradeRpcClient
        extends AbstractRpcClient<TradeRpcServiceBlockingStub,TradeRpcServiceStub,TradeRpcClientParameters>
        implements TradeClient
{
    /* (non-Javadoc)
     * @see org.marketcetera.trade.client.TradingClient#addTradeMessageListener(org.marketcetera.trade.client.TradeMessageListener)
     */
    @Override
    public void addTradeMessageListener(TradeMessageListener inTradeMessageListener)
    {
        // check to see if this listener is already registered
        if(listenerProxies.asMap().containsKey(inTradeMessageListener)) {
            return;
        }
        // make sure that this listener wasn't just whisked out from under us
        final AbstractClientListenerProxy<?,?,?> listener = listenerProxies.getUnchecked(inTradeMessageListener);
        if(listener == null) {
            return;
        }
        executeCall(new Callable<Void>() {
            @Override
            public Void call()
                    throws Exception
            {
                SLF4JLoggerProxy.trace(TradeRpcClient.this,
                                       "{} adding report listener",
                                       getSessionId());
                TradeRpc.AddTradeMessageListenerRequest.Builder requestBuilder = TradeRpc.AddTradeMessageListenerRequest.newBuilder();
                requestBuilder.setSessionId(getSessionId().getValue());
                requestBuilder.setListenerId(listener.getId());
                TradeRpc.AddTradeMessageListenerRequest addTradeMessageListenerRequest = requestBuilder.build();
                SLF4JLoggerProxy.trace(TradeRpcClient.this,
                                       "{} sending {}",
                                       getSessionId(),
                                       addTradeMessageListenerRequest);
                getAsyncStub().addTradeMessageListener(addTradeMessageListenerRequest,
                                                       (TradeMessageListenerProxy)listener);
                return null;
            }
        });
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.client.TradingClient#removeTradeMessageListener(org.marketcetera.trade.client.TradeMessageListener)
     */
    @Override
    public void removeTradeMessageListener(TradeMessageListener inTradeMessageListener)
    {
        final AbstractClientListenerProxy<?,?,?> proxy = listenerProxies.getIfPresent(inTradeMessageListener);
        listenerProxies.invalidate(inTradeMessageListener);
        if(proxy == null) {
            return;
        }
        listenerProxiesById.invalidate(proxy.getId());
        executeCall(new Callable<Void>() {
            @Override
            public Void call()
                    throws Exception
            {
                SLF4JLoggerProxy.trace(TradeRpcClient.this,
                                       "{} removing report listener",
                                       getSessionId());
                TradeRpc.RemoveTradeMessageListenerRequest.Builder requestBuilder = TradeRpc.RemoveTradeMessageListenerRequest.newBuilder();
                requestBuilder.setSessionId(getSessionId().getValue());
                requestBuilder.setListenerId(proxy.getId());
                TradeRpc.RemoveTradeMessageListenerRequest removeTradeMessageListenerRequest = requestBuilder.build();
                SLF4JLoggerProxy.trace(TradeRpcClient.this,
                                       "{} sending {}",
                                       getSessionId(),
                                       removeTradeMessageListenerRequest);
                TradeRpc.RemoveTradeMessageListenerResponse response = getBlockingStub().removeTradeMessageListener(removeTradeMessageListenerRequest);
                SLF4JLoggerProxy.trace(TradeRpcClient.this,
                                       "{} received {}",
                                       getSessionId(),
                                       response);
                return null;
            }
        });
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.client.TradingClient#findRootOrderIdFor(org.marketcetera.trade.OrderID)
     */
    @Override
    public OrderID findRootOrderIdFor(OrderID inOrderId)
    {
        OrderID result = rootOrderIdCache.getIfPresent(inOrderId);
        if(result != null) {
            return result;
        }
        result = executeCall(new Callable<OrderID>() {
            @Override
            public OrderID call()
                    throws Exception
            {
                SLF4JLoggerProxy.trace(TradeRpcClient.this,
                                       "{} finding root order ID for: {}",
                                       getSessionId(),
                                       inOrderId);
                TradeRpc.FindRootOrderIdRequest.Builder requestBuilder = TradeRpc.FindRootOrderIdRequest.newBuilder();
                requestBuilder.setSessionId(getSessionId().getValue());
                requestBuilder.setOrderId(inOrderId.getValue());
                TradeRpc.FindRootOrderIdResponse response = getBlockingStub().findRootOrderId(requestBuilder.build());
                SLF4JLoggerProxy.trace(TradeRpcClient.this,
                                       "{} received {}",
                                       getSessionId(),
                                       response);
                OrderID result = null;
                if(response.getRootOrderId() != null) {
                    result = new OrderID(response.getRootOrderId());
                }
                SLF4JLoggerProxy.trace(TradeRpcClient.this,
                                       "{} returning {}",
                                       getSessionId(),
                                       result);
                return result;
            }}
        );
        if(result != null) {
            rootOrderIdCache.put(inOrderId,
                                 result);
        }
        return result;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.client.TradingClient#getPositionAsOf(java.util.Date, org.marketcetera.trade.Instrument)
     */
    @Override
    public BigDecimal getPositionAsOf(Date inDate,
                                      Instrument inInstrument)
    {
        return executeCall(new Callable<BigDecimal>() {
            @Override
            public BigDecimal call()
                    throws Exception
            {
                SLF4JLoggerProxy.trace(TradeRpcClient.this,
                                       "{} getting position of {} as of {}",
                                       getSessionId(),
                                       inInstrument,
                                       inDate);
                TradeRpc.GetPositionAsOfRequest.Builder requestBuilder = TradeRpc.GetPositionAsOfRequest.newBuilder();
                requestBuilder.setSessionId(getSessionId().getValue());
                TradeRpcUtil.getRpcInstrument(inInstrument).ifPresent(instrument->requestBuilder.setInstrument(instrument));
                Instant time = inDate.toInstant();
                requestBuilder.setTimestamp(Timestamp.newBuilder().setSeconds(time.getEpochSecond()).setNanos(time.getNano()).build());
                TradeRpc.GetPositionAsOfResponse response = getBlockingStub().getPositionAsOf(requestBuilder.build());
                SLF4JLoggerProxy.trace(TradeRpcClient.this,
                                       "{} received {}",
                                       getSessionId(),
                                       response);
                BigDecimal result = BaseRpcUtil.getScaledQuantity(response.getPosition()).orElse(BigDecimal.ZERO);
                SLF4JLoggerProxy.trace(TradeRpcClient.this,
                                       "{} returning {}",
                                       getSessionId(),
                                       result);
                return result;
            }}
        );
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.client.TradingClient#getAllPositionsAsOf(java.util.Date)
     */
    @Override
    public Map<PositionKey<? extends Instrument>,BigDecimal> getAllPositionsAsOf(Date inDate)
    {
        return executeCall(new Callable<Map<PositionKey<? extends Instrument>,BigDecimal>>() {
            @Override
            public Map<PositionKey<? extends Instrument>,BigDecimal> call()
                    throws Exception
            {
                SLF4JLoggerProxy.trace(TradeRpcClient.this,
                                       "{} getting all positions as of {}",
                                       getSessionId(),
                                       inDate);
                TradeRpc.GetAllPositionsAsOfRequest.Builder requestBuilder = TradeRpc.GetAllPositionsAsOfRequest.newBuilder();
                requestBuilder.setSessionId(getSessionId().getValue());
                Instant time = inDate.toInstant();
                requestBuilder.setTimestamp(Timestamp.newBuilder().setSeconds(time.getEpochSecond()).setNanos(time.getNano()).build());
                TradeRpc.GetAllPositionsAsOfResponse response = getBlockingStub().getAllPositionsAsOf(requestBuilder.build());
                SLF4JLoggerProxy.trace(TradeRpcClient.this,
                                       "{} received {}",
                                       getSessionId(),
                                       response);
                Map<PositionKey<? extends Instrument>,BigDecimal> result = Maps.newHashMap();
                for(TradeTypesRpc.Position rpcPosition : response.getPositionList()) {
                    PositionKey<? extends Instrument> positionKey = null;
                    if(rpcPosition.hasPositionKey()) {
                        positionKey = TradeRpcUtil.getPositionKey(rpcPosition.getPositionKey());
                    }
                    BigDecimal position = BaseRpcUtil.getScaledQuantity(rpcPosition.getPosition()).orElse(BigDecimal.ZERO);
                    if(positionKey != null) {
                        result.put(positionKey,
                                   position);
                    }
                }
                SLF4JLoggerProxy.trace(TradeRpcClient.this,
                                       "{} returning {}",
                                       getSessionId(),
                                       result);
                return result;
            }}
        );
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.client.TradingClient#getOptionPositionsAsOf(java.util.Date, java.lang.String[])
     */
    @Override
    public Map<PositionKey<Option>,BigDecimal> getOptionPositionsAsOf(Date inDate,
                                                                      String... inRootSymbols)
    {
        return executeCall(new Callable<Map<PositionKey<Option>,BigDecimal>>() {
            @Override
            @SuppressWarnings("unchecked")
            public Map<PositionKey<Option>,BigDecimal> call()
                    throws Exception
            {
                SLF4JLoggerProxy.trace(TradeRpcClient.this,
                                       "{} getting all option positions as of {}",
                                       getSessionId(),
                                       inDate);
                TradeRpc.GetAllPositionsByRootAsOfRequest.Builder requestBuilder = TradeRpc.GetAllPositionsByRootAsOfRequest.newBuilder();
                requestBuilder.setSessionId(getSessionId().getValue());
                Instant time = inDate.toInstant();
                requestBuilder.setTimestamp(Timestamp.newBuilder().setSeconds(time.getEpochSecond()).setNanos(time.getNano()).build());
                TradeRpc.GetAllPositionsByRootAsOfResponse response = getBlockingStub().getAllPositionsByRootAsOf(requestBuilder.build());
                SLF4JLoggerProxy.trace(TradeRpcClient.this,
                                       "{} received {}",
                                       getSessionId(),
                                       response);
                Map<PositionKey<Option>,BigDecimal> result = Maps.newHashMap();
                for(TradeTypesRpc.Position rpcPosition : response.getPositionList()) {
                    PositionKey<Option> positionKey = null;
                    if(rpcPosition.hasPositionKey()) {
                        positionKey = (PositionKey<Option>)TradeRpcUtil.getPositionKey(rpcPosition.getPositionKey());
                    }
                    BigDecimal position = BaseRpcUtil.getScaledQuantity(rpcPosition.getPosition()).orElse(BigDecimal.ZERO);
                    if(positionKey != null) {
                        result.put(positionKey,
                                   position);
                    }
                }
                SLF4JLoggerProxy.trace(TradeRpcClient.this,
                                       "{} returning {}",
                                       getSessionId(),
                                       result);
                return result;
            }}
        );
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.client.TradingClient#resolveSymbol(java.lang.String)
     */
    @Override
    public Instrument resolveSymbol(String inSymbol)
    {
        Instrument result = symbolCache.getIfPresent(inSymbol);
        if(result != null) {
            return result;
        }
        result = executeCall(new Callable<Instrument>() {
            @Override
            public Instrument call()
                    throws Exception
            {
                SLF4JLoggerProxy.trace(TradeRpcClient.this,
                                       "{} resolving symbol: {}",
                                       getSessionId(),
                                       inSymbol);
                TradeRpc.ResolveSymbolRequest.Builder requestBuilder = TradeRpc.ResolveSymbolRequest.newBuilder();
                requestBuilder.setSessionId(getSessionId().getValue());
                requestBuilder.setSymbol(inSymbol);
                TradeRpc.ResolveSymbolResponse response = getBlockingStub().resolveSymbol(requestBuilder.build());
                SLF4JLoggerProxy.trace(TradeRpcClient.this,
                                       "{} received {}",
                                       getSessionId(),
                                       response);
                Instrument result = TradeRpcUtil.getInstrument(response.getInstrument()).orElse(null);
                SLF4JLoggerProxy.trace(TradeRpcClient.this,
                                       "{} returning {}",
                                       getSessionId(),
                                       result);
                return result;
            }}
        );
        if(result != null) {
            symbolCache.put(inSymbol,
                            result);
        }
        return result;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.client.TradeClient#getOpenOrders(org.marketcetera.persist.PageRequest)
     */
    @Override
    public CollectionPageResponse<OrderSummary> getOpenOrders(PageRequest inPageRequest)
    {
        return executeCall(new Callable<CollectionPageResponse<OrderSummary>>() {
            @Override
            public CollectionPageResponse<OrderSummary> call()
                    throws Exception
            {
                SLF4JLoggerProxy.trace(TradeRpcClient.this,
                                       "{} requesting open orders",
                                       getSessionId());
                TradeRpc.OpenOrdersRequest.Builder requestBuilder = TradeRpc.OpenOrdersRequest.newBuilder();
                requestBuilder.setSessionId(getSessionId().getValue());
                requestBuilder.setPageRequest(PagingRpcUtil.buildPageRequest(inPageRequest));
                TradeRpc.OpenOrdersResponse response = getBlockingStub().getOpenOrders(requestBuilder.build());
                CollectionPageResponse<OrderSummary> results = new CollectionPageResponse<>();
                for(TradeTypesRpc.OrderSummary rpcOrderSummary : response.getOrdersList()) {
                    Optional<OrderSummary> value = TradeRpcUtil.getOrderSummary(rpcOrderSummary,
                                                                                orderSummaryFactory,
                                                                                userFactory,
                                                                                reportFactory);
                    if(value.isPresent()) {
                        results.getElements().add(value.get());
                    }
                }
                PagingRpcUtil.setPageResponse(inPageRequest,
                                              response.getPageResponse(),
                                              results);
                SLF4JLoggerProxy.trace(TradeRpcClient.this,
                                       "{} returning {}",
                                       getSessionId(),
                                       results);
                return results;
            }
        });
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.client.TradeClient#getReports(org.marketcetera.persist.PageRequest)
     */
    @Override
    public CollectionPageResponse<Report> getReports(PageRequest inPageRequest)
    {
        return executeCall(new Callable<CollectionPageResponse<Report>>(){
            @Override
            public CollectionPageResponse<Report> call()
                    throws Exception
            {
                SLF4JLoggerProxy.trace(TradeRpcClient.this,
                                       "{} requesting reports: {}",
                                       getSessionId(),
                                       inPageRequest);
                TradeRpc.GetReportsRequest.Builder requestBuilder = TradeRpc.GetReportsRequest.newBuilder();
                requestBuilder.setSessionId(getSessionId().getValue());
                requestBuilder.setPageRequest(PagingRpcUtil.buildPageRequest(inPageRequest));
                TradeRpc.GetReportsResponse response = getBlockingStub().getReports(requestBuilder.build());
                CollectionPageResponse<Report> results = new CollectionPageResponse<>();
                response.getReportsList().forEach(rpcReport->results.getElements().add(TradeRpcUtil.getReport(rpcReport,
                                                                                                              reportFactory,
                                                                                                              userFactory)));
                PagingRpcUtil.setPageResponse(inPageRequest,
                                              response.getPageResponse(),
                                              results);
                SLF4JLoggerProxy.trace(TradeRpcClient.this,
                                       "{} returning {}",
                                       getSessionId(),
                                       results);
                return results;
            }
        });
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.client.TradeClient#getFills(org.marketcetera.persist.PageRequest)
     */
    @Override
    public CollectionPageResponse<ExecutionReport> getFills(PageRequest inPageRequest)
    {
        return executeCall(new Callable<CollectionPageResponse<ExecutionReport>>(){
            @Override
            public CollectionPageResponse<ExecutionReport> call()
                    throws Exception
            {
                SLF4JLoggerProxy.trace(TradeRpcClient.this,
                                       "{} requesting fills: {}",
                                       getSessionId(),
                                       inPageRequest);
                TradeRpc.GetFillsRequest.Builder requestBuilder = TradeRpc.GetFillsRequest.newBuilder();
                requestBuilder.setSessionId(getSessionId().getValue());
                requestBuilder.setPageRequest(PagingRpcUtil.buildPageRequest(inPageRequest));
                TradeRpc.GetFillsResponse response = getBlockingStub().getFills(requestBuilder.build());
                CollectionPageResponse<ExecutionReport> results = new CollectionPageResponse<>();
                response.getFillsList().forEach(rpcExecutionReport->results.getElements().add((ExecutionReport)TradeRpcUtil.getTradeMessage(rpcExecutionReport)));
                PagingRpcUtil.setPageResponse(inPageRequest,
                                              response.getPageResponse(),
                                              results);
                SLF4JLoggerProxy.trace(TradeRpcClient.this,
                                       "{} returning {}",
                                       getSessionId(),
                                       results);
                return results;
            }
        });
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.client.TradingClient#getOpenOrders()
     */
    @Override
    public Collection<OrderSummary> getOpenOrders()
    {
        return getOpenOrders(PageRequest.ALL).getElements();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.client.TradingClient#sendOrder(org.marketcetera.trade.Order)
     */
    @Override
    public SendOrderResponse sendOrder(Order inOrder)
    {
        return sendOrders(Lists.newArrayList(inOrder)).get(0);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.client.TradeClient#getLatestExecutionReportForOrderChain(org.marketcetera.trade.OrderID)
     */
    @Override
    public ExecutionReport getLatestExecutionReportForOrderChain(OrderID inOrderId)
    {
        return executeCall(new Callable<ExecutionReport>(){
            @Override
            public ExecutionReport call()
                    throws Exception
            {
                SLF4JLoggerProxy.trace(TradeRpcClient.this,
                                       "{} retrieving latest execution report for order chain {}",
                                       getSessionId(),
                                       inOrderId);
                TradeRpc.GetLatestExecutionReportForOrderChainRequest.Builder requestBuilder = TradeRpc.GetLatestExecutionReportForOrderChainRequest.newBuilder();
                requestBuilder.setSessionId(getSessionId().getValue());
                requestBuilder.setOrderId(inOrderId.getValue());
                TradeRpc.GetLatestExecutionReportForOrderChainRequest getExecutionReportRequest = requestBuilder.build();
                SLF4JLoggerProxy.trace(TradeRpcClient.this,
                                       "{} sending {}",
                                       getSessionId(),
                                       getExecutionReportRequest);
                TradeRpc.GetLatestExecutionReportForOrderChainResponse response = getBlockingStub().getLatestExecutionReportForOrderChain(getExecutionReportRequest);
                ExecutionReport executionReport = (ExecutionReport)TradeRpcUtil.getTradeMessage(response.getExecutionReport());
                SLF4JLoggerProxy.trace(TradeRpcClient.this,
                                       "{} received {}: {}",
                                       getSessionId(),
                                       response,
                                       executionReport);
                return executionReport;
            }
        });
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.client.TradingClient#sendOrders(java.util.List)
     */
    @Override
    public List<SendOrderResponse> sendOrders(List<Order> inOrders)
    {
        return executeCall(new Callable<List<SendOrderResponse>>(){
            @Override
            public List<SendOrderResponse> call()
                    throws Exception
            {
                SLF4JLoggerProxy.trace(TradeRpcClient.this,
                                       "{} sending {} order(s)",
                                       getSessionId(),
                                       inOrders.size());
                TradeRpc.SendOrderRequest.Builder requestBuilder = TradeRpc.SendOrderRequest.newBuilder();
                TradeTypesRpc.Order.Builder orderBuilder = TradeTypesRpc.Order.newBuilder();
                TradeTypesRpc.OrderBase.Builder orderBaseBuilder = null;
                TradeTypesRpc.FIXOrder.Builder fixOrderBuilder = null;
                BaseRpc.Map.Builder mapBuilder = null;
                BaseRpc.KeyValuePair.Builder keyValuePairBuilder = null;
                requestBuilder.setSessionId(getSessionId().getValue());
                for(Order order : inOrders) {
                    try {
                        if(order instanceof FIXOrder) {
                            FIXOrder fixOrder = (FIXOrder)order;
                            if(fixOrderBuilder == null) {
                                fixOrderBuilder = TradeTypesRpc.FIXOrder.newBuilder();
                            } else {
                                fixOrderBuilder.clear();
                            }
                            if(mapBuilder == null) {
                                mapBuilder = BaseRpc.Map.newBuilder();
                            } else {
                                mapBuilder.clear();
                            }
                            for(Map.Entry<Integer,String> entry : fixOrder.getFields().entrySet()) {
                                if(keyValuePairBuilder == null) {
                                    keyValuePairBuilder = BaseRpc.KeyValuePair.newBuilder();
                                } else {
                                    keyValuePairBuilder.clear();
                                }
                                keyValuePairBuilder.setKey(String.valueOf(entry.getKey()));
                                keyValuePairBuilder.setValue(entry.getValue());
                                mapBuilder.addKeyValuePairs(keyValuePairBuilder.build());
                            }
                            orderBuilder.setMatpOrderType(TradeTypesRpc.MatpOrderType.FIXOrderType);
                            TradeRpcUtil.setBrokerId(fixOrder,
                                                     fixOrderBuilder);
                            // TODO
//                            fixOrderBuilder.setMessage(mapBuilder.build());
                            orderBuilder.setFixOrder(fixOrderBuilder.build());
                        } else if(order instanceof OrderBase) {
                            if(orderBaseBuilder == null) {
                                orderBaseBuilder = TradeTypesRpc.OrderBase.newBuilder();
                            } else {
                                orderBaseBuilder.clear();
                            }
                            // either an OrderSingle, OrderReplace, or OrderCancel
                            // the types overlap some, first, set all the common fields on OrderBase
                            OrderBase orderBase = (OrderBase)order;
                            TradeRpcUtil.setAccount(orderBase,
                                                    orderBaseBuilder);
                            TradeRpcUtil.setBrokerId(orderBase,
                                                     orderBaseBuilder);
                            TradeRpcUtil.setRpcCustomFields(orderBase,
                                                            orderBaseBuilder);
                            TradeRpcUtil.setInstrument(orderBase,
                                                       orderBaseBuilder);
                            TradeRpcUtil.setOrderId(orderBase,
                                                    orderBaseBuilder);
                            TradeRpcUtil.setQuantity(orderBase,
                                                     orderBaseBuilder);
                            TradeRpcUtil.setSide(orderBase,
                                                 orderBaseBuilder);
                            TradeRpcUtil.setText(orderBase,
                                                 orderBaseBuilder);
                            // now, check for various special order types
                            if(orderBase instanceof NewOrReplaceOrder) {
                                NewOrReplaceOrder newOrReplaceOrder = (NewOrReplaceOrder)orderBase;
                                TradeRpcUtil.setDisplayQuantity(newOrReplaceOrder,
                                                                orderBaseBuilder);
                                TradeRpcUtil.setExecutionDestination(newOrReplaceOrder,
                                                                     orderBaseBuilder);
                                TradeRpcUtil.setOrderCapacity(newOrReplaceOrder,
                                                              orderBaseBuilder);
                                TradeRpcUtil.setOrderType(newOrReplaceOrder,
                                                          orderBaseBuilder);
                                TradeRpcUtil.setPositionEffect(newOrReplaceOrder,
                                                               orderBaseBuilder);
                                TradeRpcUtil.setPrice(newOrReplaceOrder,
                                                      orderBaseBuilder);
                                TradeRpcUtil.setTimeInForce(newOrReplaceOrder,
                                                            orderBaseBuilder);
                            }
                            if(order instanceof RelatedOrder) {
                                RelatedOrder relatedOrder = (RelatedOrder)order;
                                TradeRpcUtil.setOriginalOrderId(relatedOrder,
                                                                orderBaseBuilder);
                            }
                            TradeTypesRpc.OrderBase rpcOrderBase = orderBaseBuilder.build();
                            orderBuilder.setOrderBase(rpcOrderBase);
                            if(order instanceof OrderCancel) {
                                orderBuilder.setMatpOrderType(TradeTypesRpc.MatpOrderType.OrderCancelType);
                            } else if(order instanceof OrderSingle) {
                                orderBuilder.setMatpOrderType(TradeTypesRpc.MatpOrderType.OrderSingleType);
                            } else if(order instanceof OrderReplace) {
                                orderBuilder.setMatpOrderType(TradeTypesRpc.MatpOrderType.OrderReplaceType);
                            } else {
                                throw new UnsupportedOperationException("Unsupported order type: " + order.getClass().getSimpleName());
                            }
                        } else {
                            throw new UnsupportedOperationException("Unsupported order type: " + order.getClass().getSimpleName());
                        }
                        requestBuilder.addOrder(orderBuilder.build());
                        orderBuilder.clear();
                        if(orderBaseBuilder != null) {
                            orderBaseBuilder.clear();
                        }
                    } catch (Exception e) {
                        PlatformServices.handleException(TradeRpcClient.this,
                                                         "Unable to send " + order,
                                                         e);
                    }
                }
                TradeRpc.SendOrderRequest sendOrderRequest = requestBuilder.build();
                SLF4JLoggerProxy.trace(TradeRpcClient.this,
                                       "{} sending {}",
                                       getSessionId(),
                                       sendOrderRequest);
                TradeRpc.SendOrderResponse response = getBlockingStub().sendOrders(sendOrderRequest);
                List<SendOrderResponse> results = new ArrayList<>();
                for(TradeRpc.OrderResponse rpcResponse : response.getOrderResponseList()) {
                    SendOrderResponse orderResponse = new SendOrderResponse();
                    orderResponse.setOrderId(rpcResponse.getOrderid()==null?null:new OrderID(rpcResponse.getOrderid()));
                    results.add(orderResponse);
                }
                SLF4JLoggerProxy.trace(TradeRpcClient.this,
                                       "{} returning {}",
                                       getSessionId(),
                                       results);
                return results;
            }
        });
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.client.TradingClient#addReport(org.marketcetera.event.HasFIXMessage, org.marketcetera.trade.BrokerID)
     */
    @Override
    public void addReport(HasFIXMessage inReport,
                          BrokerID inBrokerID)
    {
        executeCall(new Callable<Void>() {
            @Override
            public Void call()
                    throws Exception
            {
                TradeRpc.AddReportRequest.Builder requestBuilder = TradeRpc.AddReportRequest.newBuilder();
                requestBuilder.setSessionId(getSessionId().getValue());
                requestBuilder.setBrokerId(inBrokerID.getValue());
                requestBuilder.setMessage(TradeRpcUtil.getRpcFixMessage(inReport.getMessage()));
                TradeRpc.AddReportRequest request = requestBuilder.build();
                SLF4JLoggerProxy.trace(TradeRpcClient.this,
                                       "{} sending {}",
                                       getSessionId(),
                                       request);
                TradeRpc.AddReportResponse response = getBlockingStub().addReport(request);
                SLF4JLoggerProxy.trace(TradeRpcClient.this,
                                       "{} received {}",
                                       getSessionId(),
                                       response);
                return null;
            }}
        );
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.client.TradingClient#deleteReport(org.marketcetera.trade.ReportID)
     */
    @Override
    public void deleteReport(ReportID inReportId)
    {
        executeCall(new Callable<Void>() {
            @Override
            public Void call()
                    throws Exception
            {
                TradeRpc.DeleteReportRequest.Builder requestBuilder = TradeRpc.DeleteReportRequest.newBuilder();
                requestBuilder.setSessionId(getSessionId().getValue());
                requestBuilder.setReportId(String.valueOf(inReportId.longValue()));
                TradeRpc.DeleteReportRequest request = requestBuilder.build();
                SLF4JLoggerProxy.trace(TradeRpcClient.this,
                                       "{} sending {}",
                                       getSessionId(),
                                       request);
                TradeRpc.DeleteReportResponse response = getBlockingStub().deleteReport(request);
                SLF4JLoggerProxy.trace(TradeRpcClient.this,
                                       "{} received {}",
                                       getSessionId(),
                                       response);
                return null;
            }}
        );
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.client.TradeClient#readAvailableFixInitiatorSessions()
     */
    @Override
    public List<ActiveFixSession> readAvailableFixInitiatorSessions()
    {
        return executeCall(new Callable<List<ActiveFixSession>>() {
            @Override
            public List<ActiveFixSession> call()
                    throws Exception
            {
                SLF4JLoggerProxy.trace(TradeRpcClient.this,
                                       "{} read available FIX initiator sessions",
                                       getSessionId());
                TradeRpc.ReadAvailableFixInitiatorSessionsRequest.Builder requestBuilder = TradeRpc.ReadAvailableFixInitiatorSessionsRequest.newBuilder();
                requestBuilder.setSessionId(getSessionId().getValue());
                TradeRpc.ReadAvailableFixInitiatorSessionsRequest request = requestBuilder.build();
                SLF4JLoggerProxy.trace(TradeRpcClient.this,
                                       "{} sending {}",
                                       getSessionId(),
                                       request);
                TradeRpc.ReadAvailableFixInitiatorSessionsResponse response = getBlockingStub().readAvailableFixInitiatorSessions(request);
                SLF4JLoggerProxy.trace(TradeRpcClient.this,
                                       "{} received {}",
                                       getSessionId(),
                                       response);
                List<ActiveFixSession> results = Lists.newArrayList();
                for(FixAdminRpc.ActiveFixSession rpcFixSession : response.getFixSessionList()) {
                    FixRpcUtil.getActiveFixSession(rpcFixSession,
                                                   activeFixSessionFactory,
                                                   fixSessionFactory,
                                                   clusterDataFactory).ifPresent(activeFixSession->results.add(activeFixSession));
                }
                SLF4JLoggerProxy.trace(TradeRpcClient.this,
                                       "{} returning {}",
                                       getSessionId(),
                                       results);
                return results;
            }
        });
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.client.TradeClient#getOptionRoots(java.lang.String)
     */
    @Override
    public Collection<String> getOptionRoots(String inUnderlying)
    {
        throw new UnsupportedOperationException(); // TODO
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.client.TradeClient#getUnderlying(java.lang.String)
     */
    @Override
    public String getUnderlying(String inOptionRoot)
    {
        throw new UnsupportedOperationException(); // TODO
    }
    /**
     * Get the clusterDataFactory value.
     *
     * @return a <code>ClusterDataFactory</code> value
     */
    public ClusterDataFactory getClusterDataFactory()
    {
        return clusterDataFactory;
    }
    /**
     * Sets the clusterDataFactory value.
     *
     * @param inClusterDataFactory a <code>ClusterDataFactory</code> value
     */
    public void setClusterDataFactory(ClusterDataFactory inClusterDataFactory)
    {
        clusterDataFactory = inClusterDataFactory;
    }
    /**
     * Get the activeFixSessionFactory value.
     *
     * @return a <code>MutableActiveFixSessionFactory</code> value
     */
    public MutableActiveFixSessionFactory getActiveFixSessionFactory()
    {
        return activeFixSessionFactory;
    }
    /**
     * Sets the activeFixSessionFactory value.
     *
     * @param inActiveFixSessionFactory a <code>MutableActiveFixSessionFactory</code> value
     */
    public void setActiveFixSessionFactory(MutableActiveFixSessionFactory inActiveFixSessionFactory)
    {
        activeFixSessionFactory = inActiveFixSessionFactory;
    }
    /**
     * Get the fixSessionFactory value.
     *
     * @return a <code>MutableFixSessionFactory</code> value
     */
    public MutableFixSessionFactory getFixSessionFactory()
    {
        return fixSessionFactory;
    }
    /**
     * Sets the fixSessionFactory value.
     *
     * @param inFixSessionFactory a <code>MutableFixSessionFactory</code> value
     */
    public void setFixSessionFactory(MutableFixSessionFactory inFixSessionFactory)
    {
        fixSessionFactory = inFixSessionFactory;
    }
    /**
     * Get the orderSummaryFactory value.
     *
     * @return a <code>MutableOrderSummaryFactory</code> value
     */
    public MutableOrderSummaryFactory getOrderSummaryFactory()
    {
        return orderSummaryFactory;
    }
    /**
     * Sets the orderSummaryFactory value.
     *
     * @param inOrderSummaryFactory a <code>MutableOrderSummaryFactory</code> value
     */
    public void setOrderSummaryFactory(MutableOrderSummaryFactory inOrderSummaryFactory)
    {
        orderSummaryFactory = inOrderSummaryFactory;
    }
    /**
     * Get the userFactory value.
     *
     * @return a <code>UserFactory</code> value
     */
    public UserFactory getUserFactory()
    {
        return userFactory;
    }
    /**
     * Sets the userFactory value.
     *
     * @param inUserFactory a <code>UserFactory</code> value
     */
    public void setUserFactory(UserFactory inUserFactory)
    {
        userFactory = inUserFactory;
    }
    /**
     * Get the reportFactory value.
     *
     * @return a <code>MutableReportFactory</code> value
     */
    public MutableReportFactory getReportFactory()
    {
        return reportFactory;
    }
    /**
     * Sets the reportFactory value.
     *
     * @param inReportFactory a <code>MutableReportFactory</code> value
     */
    public void setReportFactory(MutableReportFactory inReportFactory)
    {
        reportFactory = inReportFactory;
    }
    /**
     * Create a new TradeRpcClient instance.
     *
     * @param inParameters a <code>TradeRpcClientParameters</code> value
     */
    TradeRpcClient(TradeRpcClientParameters inParameters)
    {
        super(inParameters);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.rpc.client.AbstractRpcClient#getBlockingStub(io.grpc.Channel)
     */
    @Override
    protected TradeRpcServiceBlockingStub getBlockingStub(Channel inChannel)
    {
        return TradeRpcServiceGrpc.newBlockingStub(inChannel);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.rpc.client.AbstractRpcClient#getAsyncStub(io.grpc.Channel)
     */
    @Override
    protected TradeRpcServiceStub getAsyncStub(Channel inChannel)
    {
        return TradeRpcServiceGrpc.newStub(inChannel);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.rpc.client.AbstractRpcClient#executeLogin(org.marketcetera.rpc.base.BaseRpc.LoginRequest)
     */
    @Override
    protected LoginResponse executeLogin(BaseRpc.LoginRequest inRequest)
    {
        return getBlockingStub().login(inRequest);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.rpc.client.AbstractRpcClient#executeLogout(org.marketcetera.rpc.base.BaseRpc.LogoutRequest)
     */
    @Override
    protected LogoutResponse executeLogout(BaseRpc.LogoutRequest inRequest)
    {
        return getBlockingStub().logout(inRequest);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.rpc.client.AbstractRpcClient#executeHeartbeat(org.marketcetera.rpc.base.BaseRpc.HeartbeatRequest, io.grpc.stub.StreamObserver)
     */
    @Override
    protected BaseRpc.HeartbeatResponse executeHeartbeat(HeartbeatRequest inRequest)
    {
        return getBlockingStub().heartbeat(inRequest);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.rpc.client.AbstractRpcClient#getAppId()
     */
    @Override
    protected AppId getAppId()
    {
        return APP_ID;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.rpc.client.AbstractRpcClient#getVersionInfo()
     */
    @Override
    protected VersionInfo getVersionInfo()
    {
        return APP_ID_VERSION;
    }
    /**
     * Creates the appropriate proxy for the given listener.
     *
     * @param inListener an <code>Object</code> value
     * @return an <code>AbstractListenerProxy&lt;?,?,?&gt;</code> value
     */
    private static AbstractClientListenerProxy<?,?,?> getListenerFor(Object inListener)
    {
        if(inListener instanceof TradeMessageListener) {
            return new TradeMessageListenerProxy((TradeMessageListener)inListener);
        } else {
            throw new UnsupportedOperationException();
        }
    }
    /**
     * Provides an interface between trade message stream listeners and their handlers.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    private static class TradeMessageListenerProxy
            extends BaseRpcUtil.AbstractClientListenerProxy<TradeMessageListenerResponse,TradeMessage,TradeMessageListener>
    {
        /* (non-Javadoc)
         * @see org.marketcetera.trade.rpc.TradeRpcClient.AbstractListenerProxy#translateMessage(java.lang.Object)
         */
        @Override
        protected TradeMessage translateMessage(TradeMessageListenerResponse inResponse)
        {
            return TradeRpcUtil.getTradeMessage(inResponse);
        }
        /* (non-Javadoc)
         * @see org.marketcetera.trade.rpc.TradeRpcClient.AbstractListenerProxy#sendMessage(java.lang.Object, java.lang.Object)
         */
        @Override
        protected void sendMessage(TradeMessageListener inMessageListener,
                                   TradeMessage inMessage)
        {
            inMessageListener.receiveTradeMessage(inMessage);
        }
        /**
         * Create a new TradeMessageListenerProxy instance.
         *
         * @param inTradeMessageListener a <code>TradeMessageListener</code> value
         */
        protected TradeMessageListenerProxy(TradeMessageListener inTradeMessageListener)
        {
            super(inTradeMessageListener);
        }
    }
    /**
     * creates {@link MutableOrderSummary} objects
     */
    private MutableOrderSummaryFactory orderSummaryFactory;
    /**
     * creates {@link User} objects
     */
    private UserFactory userFactory;
    /**
     * creates {@link MutableReport} objects
     */
    private MutableReportFactory reportFactory;
    /**
     * creates {@link ClusterData} objects
     */
    private ClusterDataFactory clusterDataFactory;
    /**
     * creates {@link ActiveFixSession} objects
     */
    private MutableActiveFixSessionFactory activeFixSessionFactory;
    /**
     * creates {@link FixSession} objects
     */
    private MutableFixSessionFactory fixSessionFactory;
    /**
     * The client's application ID: the application name.
     */
    private static final String APP_ID_NAME = TradeRpcClient.class.getSimpleName();
    /**
     * The client's application ID: the version.
     */
    private static final VersionInfo APP_ID_VERSION = new VersionInfo(Version.pomversion);
    /**
     * The client's application ID: the ID.
     */
    private static final AppId APP_ID = Util.getAppId(APP_ID_NAME,APP_ID_VERSION.getVersionInfo());
    /**
     * symbol to instrument cache
     */
    private final Cache<String,Instrument> symbolCache = CacheBuilder.newBuilder().expireAfterAccess(10,TimeUnit.SECONDS).build();
    /**
     * root order ID cache
     */
    private final Cache<OrderID,OrderID> rootOrderIdCache = CacheBuilder.newBuilder().expireAfterAccess(10,TimeUnit.MINUTES).build();
    /**
     * holds report listeners by their id
     */
    private final Cache<String,BaseRpcUtil.AbstractClientListenerProxy<?,?,?>> listenerProxiesById = CacheBuilder.newBuilder().build();
    /**
     * holds listener proxies keyed by the listener
     */
    private final LoadingCache<Object,BaseRpcUtil.AbstractClientListenerProxy<?,?,?>> listenerProxies = CacheBuilder.newBuilder().build(new CacheLoader<Object,AbstractClientListenerProxy<?,?,?>>() {
        @Override
        public BaseRpcUtil.AbstractClientListenerProxy<?,?,?> load(Object inKey)
                throws Exception
        {
            BaseRpcUtil.AbstractClientListenerProxy<?,?,?> proxy = getListenerFor(inKey);
            listenerProxiesById.put(proxy.getId(),
                                    proxy);
            return proxy;
        }}
    );
}
