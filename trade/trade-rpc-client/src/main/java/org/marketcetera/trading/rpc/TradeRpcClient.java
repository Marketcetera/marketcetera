package org.marketcetera.trading.rpc;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import org.marketcetera.brokers.BrokerStatus;
import org.marketcetera.brokers.BrokerStatusListener;
import org.marketcetera.brokers.BrokersStatus;
import org.marketcetera.brokers.ClusteredBrokerStatus;
import org.marketcetera.brokers.ClusteredBrokersStatus;
import org.marketcetera.core.PlatformServices;
import org.marketcetera.core.Util;
import org.marketcetera.core.Version;
import org.marketcetera.core.VersionInfo;
import org.marketcetera.core.position.PositionKey;
import org.marketcetera.event.HasFIXMessage;
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
import org.marketcetera.trade.FIXOrder;
import org.marketcetera.trade.Instrument;
import org.marketcetera.trade.NewOrReplaceOrder;
import org.marketcetera.trade.Option;
import org.marketcetera.trade.Order;
import org.marketcetera.trade.OrderBase;
import org.marketcetera.trade.OrderID;
import org.marketcetera.trade.OrderSummary;
import org.marketcetera.trade.RelatedOrder;
import org.marketcetera.trade.ReportID;
import org.marketcetera.trade.TradeMessage;
import org.marketcetera.trade.TradeMessageListener;
import org.marketcetera.trade.client.SendOrderResponse;
import org.marketcetera.trade.client.TradeClient;
import org.marketcetera.trading.rpc.TradingRpc.BrokerStatusListenerResponse;
import org.marketcetera.trading.rpc.TradingRpc.TradeMessageListenerResponse;
import org.marketcetera.trading.rpc.TradingRpcServiceGrpc.TradingRpcServiceBlockingStub;
import org.marketcetera.trading.rpc.TradingRpcServiceGrpc.TradingRpcServiceStub;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.ws.tags.AppId;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.protobuf.util.Timestamps;

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
        extends AbstractRpcClient<TradingRpcServiceBlockingStub,TradingRpcServiceStub,TradingRpcClientParameters>
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
                TradingRpc.AddTradeMessageListenerRequest.Builder requestBuilder = TradingRpc.AddTradeMessageListenerRequest.newBuilder();
                requestBuilder.setSessionId(getSessionId().getValue());
                requestBuilder.setListenerId(listener.getId());
                TradingRpc.AddTradeMessageListenerRequest addTradeMessageListenerRequest = requestBuilder.build();
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
                TradingRpc.RemoveTradeMessageListenerRequest.Builder requestBuilder = TradingRpc.RemoveTradeMessageListenerRequest.newBuilder();
                requestBuilder.setSessionId(getSessionId().getValue());
                requestBuilder.setListenerId(proxy.getId());
                TradingRpc.RemoveTradeMessageListenerRequest removeTradeMessageListenerRequest = requestBuilder.build();
                SLF4JLoggerProxy.trace(TradeRpcClient.this,
                                       "{} sending {}",
                                       getSessionId(),
                                       removeTradeMessageListenerRequest);
                TradingRpc.RemoveTradeMessageListenerResponse response = getBlockingStub().removeTradeMessageListener(removeTradeMessageListenerRequest);
                SLF4JLoggerProxy.trace(TradeRpcClient.this,
                                       "{} received {}",
                                       getSessionId(),
                                       response);
                return null;
            }
        });
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.client.TradingClient#addBrokerStatusListener(org.marketcetera.brokers.BrokerStatusListener)
     */
    @Override
    public void addBrokerStatusListener(BrokerStatusListener inBrokerStatusListener)
    {
        // check to see if this listener is already registered
        if(listenerProxies.asMap().containsKey(inBrokerStatusListener)) {
            return;
        }
        // make sure that this listener wasn't just whisked out from under us
        final AbstractClientListenerProxy<?,?,?> listener = listenerProxies.getUnchecked(inBrokerStatusListener);
        if(listener == null) {
            return;
        }
        executeCall(new Callable<Void>() {
            @Override
            public Void call()
                    throws Exception
            {
                SLF4JLoggerProxy.trace(TradeRpcClient.this,
                                       "{} adding broker status listener",
                                       getSessionId());
                TradingRpc.AddBrokerStatusListenerRequest.Builder requestBuilder = TradingRpc.AddBrokerStatusListenerRequest.newBuilder();
                requestBuilder.setSessionId(getSessionId().getValue());
                requestBuilder.setListenerId(listener.getId());
                TradingRpc.AddBrokerStatusListenerRequest addBrokerStatusListenerRequest = requestBuilder.build();
                SLF4JLoggerProxy.trace(TradeRpcClient.this,
                                       "{} sending {}",
                                       getSessionId(),
                                       addBrokerStatusListenerRequest);
                getAsyncStub().addBrokerStatusListener(addBrokerStatusListenerRequest,
                                                       (BrokerStatusListenerProxy)listener);
                return null;
            }
        });
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.client.TradingClient#removeBrokerStatusListener(org.marketcetera.brokers.BrokerStatusListener)
     */
    @Override
    public void removeBrokerStatusListener(BrokerStatusListener inBrokerStatusListener)
    {
        final AbstractClientListenerProxy<?,?,?> proxy = listenerProxies.getIfPresent(inBrokerStatusListener);
        listenerProxies.invalidate(inBrokerStatusListener);
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
                                       "{} removing broker status listener",
                                       getSessionId());
                TradingRpc.RemoveBrokerStatusListenerRequest.Builder requestBuilder = TradingRpc.RemoveBrokerStatusListenerRequest.newBuilder();
                requestBuilder.setSessionId(getSessionId().getValue());
                requestBuilder.setListenerId(proxy.getId());
                TradingRpc.RemoveBrokerStatusListenerRequest removeBrokerStatusListenerRequest = requestBuilder.build();
                SLF4JLoggerProxy.trace(TradeRpcClient.this,
                                       "{} sending {}",
                                       getSessionId(),
                                       removeBrokerStatusListenerRequest);
                TradingRpc.RemoveBrokerStatusListenerResponse response = getBlockingStub().removeBrokerStatusListener(removeBrokerStatusListenerRequest);
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
                TradingRpc.FindRootOrderIdRequest.Builder requestBuilder = TradingRpc.FindRootOrderIdRequest.newBuilder();
                requestBuilder.setSessionId(getSessionId().getValue());
                requestBuilder.setOrderId(inOrderId.getValue());
                TradingRpc.FindRootOrderIdResponse response = getBlockingStub().findRootOrderId(requestBuilder.build());
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
                TradingRpc.GetPositionAsOfRequest.Builder requestBuilder = TradingRpc.GetPositionAsOfRequest.newBuilder();
                requestBuilder.setSessionId(getSessionId().getValue());
                TradeRpcUtil.getRpcInstrument(inInstrument).ifPresent(instrument->requestBuilder.setInstrument(instrument));
                requestBuilder.setTimestamp(Timestamps.fromMillis(inDate.getTime()));
                TradingRpc.GetPositionAsOfResponse response = getBlockingStub().getPositionAsOf(requestBuilder.build());
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
                TradingRpc.GetAllPositionsAsOfRequest.Builder requestBuilder = TradingRpc.GetAllPositionsAsOfRequest.newBuilder();
                requestBuilder.setSessionId(getSessionId().getValue());
                requestBuilder.setTimestamp(Timestamps.fromMillis(inDate.getTime()));
                TradingRpc.GetAllPositionsAsOfResponse response = getBlockingStub().getAllPositionsAsOf(requestBuilder.build());
                SLF4JLoggerProxy.trace(TradeRpcClient.this,
                                       "{} received {}",
                                       getSessionId(),
                                       response);
                Map<PositionKey<? extends Instrument>,BigDecimal> result = Maps.newHashMap();
                for(TradingTypesRpc.Position rpcPosition : response.getPositionList()) {
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
                TradingRpc.GetAllPositionsByRootAsOfRequest.Builder requestBuilder = TradingRpc.GetAllPositionsByRootAsOfRequest.newBuilder();
                requestBuilder.setSessionId(getSessionId().getValue());
                requestBuilder.setTimestamp(Timestamps.fromMillis(inDate.getTime()));
                TradingRpc.GetAllPositionsByRootAsOfResponse response = getBlockingStub().getAllPositionsByRootAsOf(requestBuilder.build());
                SLF4JLoggerProxy.trace(TradeRpcClient.this,
                                       "{} received {}",
                                       getSessionId(),
                                       response);
                Map<PositionKey<Option>,BigDecimal> result = Maps.newHashMap();
                for(TradingTypesRpc.Position rpcPosition : response.getPositionList()) {
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
     * @see org.marketcetera.trade.client.TradingClient#getBrokersStatus()
     */
    @Override
    public BrokersStatus getBrokersStatus()
    {
        return executeCall(new Callable<BrokersStatus>() {
            @Override
            public BrokersStatus call()
                    throws Exception
            {
                SLF4JLoggerProxy.trace(TradeRpcClient.this,
                                       "{} getting brokers status",
                                       getSessionId());
                TradingRpc.BrokersStatusRequest.Builder requestBuilder = TradingRpc.BrokersStatusRequest.newBuilder();
                requestBuilder.setSessionId(getSessionId().getValue());
                TradingRpc.BrokersStatusResponse response = getBlockingStub().getBrokersStatus(requestBuilder.build());
                SLF4JLoggerProxy.trace(TradeRpcClient.this,
                                       "{} received {}",
                                       getSessionId(),
                                       response);
                List<ClusteredBrokerStatus> brokers = Lists.newArrayList();
                for(TradingTypesRpc.BrokerStatus rpcBrokerStatus : response.getBrokerStatusList()) {
                    Optional<BrokerStatus> brokerStatus = TradeRpcUtil.getBrokerStatus(rpcBrokerStatus);
                    if(brokerStatus.isPresent()) {
                        brokers.add((ClusteredBrokerStatus)brokerStatus.get());
                    }
                }
                BrokersStatus brokersStatus = new ClusteredBrokersStatus(brokers);
                SLF4JLoggerProxy.trace(TradeRpcClient.this,
                                       "{} returning {}",
                                       getSessionId(),
                                       brokersStatus);
                return brokersStatus;
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
                TradingRpc.ResolveSymbolRequest.Builder requestBuilder = TradingRpc.ResolveSymbolRequest.newBuilder();
                requestBuilder.setSessionId(getSessionId().getValue());
                requestBuilder.setSymbol(inSymbol);
                TradingRpc.ResolveSymbolResponse response = getBlockingStub().resolveSymbol(requestBuilder.build());
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
    public CollectionPageResponse<? extends OrderSummary> getOpenOrders(PageRequest inPageRequest)
    {
        return executeCall(new Callable<CollectionPageResponse<? extends OrderSummary>>() {
            @Override
            public CollectionPageResponse<? extends OrderSummary> call()
                    throws Exception
            {
                SLF4JLoggerProxy.trace(TradeRpcClient.this,
                                       "{} requesting open orders",
                                       getSessionId());
                TradingRpc.OpenOrdersRequest.Builder requestBuilder = TradingRpc.OpenOrdersRequest.newBuilder();
                requestBuilder.setSessionId(getSessionId().getValue());
                requestBuilder.setPageRequest(PagingRpcUtil.buildPageRequest(inPageRequest));
                TradingRpc.OpenOrdersResponse response = getBlockingStub().getOpenOrders(requestBuilder.build());
                CollectionPageResponse<OrderSummary> results = new CollectionPageResponse<>();
                for(TradingTypesRpc.OrderSummary rpcOrderSummary : response.getOrdersList()) {
                    Optional<OrderSummary> value = TradeRpcUtil.getOrderSummary(rpcOrderSummary);
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
     * @see org.marketcetera.trade.client.TradingClient#getOpenOrders()
     */
    @Override
    public Collection<? extends OrderSummary> getOpenOrders()
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
                TradingRpc.SendOrderRequest.Builder requestBuilder = TradingRpc.SendOrderRequest.newBuilder();
                TradingTypesRpc.Order.Builder orderBuilder = TradingTypesRpc.Order.newBuilder();
                TradingTypesRpc.OrderBase.Builder orderBaseBuilder = null;
                TradingTypesRpc.FIXOrder.Builder fixOrderBuilder = null;
                BaseRpc.Map.Builder mapBuilder = null;
                BaseRpc.KeyValuePair.Builder keyValuePairBuilder = null;
                requestBuilder.setSessionId(getSessionId().getValue());
                for(Order order : inOrders) {
                    try {
                        if(order instanceof FIXOrder) {
                            FIXOrder fixOrder = (FIXOrder)order;
                            if(fixOrderBuilder == null) {
                                fixOrderBuilder = TradingTypesRpc.FIXOrder.newBuilder();
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
                                keyValuePairBuilder.setKey(entry.getValue());
                                mapBuilder.addKeyValuePairs(keyValuePairBuilder.build());
                            }
                            TradeRpcUtil.setBrokerId(fixOrder,
                                                    fixOrderBuilder);
                            // TODO
//                            fixOrderBuilder.setMessage(mapBuilder.build());
                            orderBuilder.setFixOrder(fixOrderBuilder.build());
                        } else if(order instanceof OrderBase) {
                            if(orderBaseBuilder == null) {
                                orderBaseBuilder = TradingTypesRpc.OrderBase.newBuilder();
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
                            TradingTypesRpc.OrderBase rpcOrderBase = orderBaseBuilder.build();
                            if(order instanceof RelatedOrder) {
                                RelatedOrder relatedOrder = (RelatedOrder)order;
                                TradeRpcUtil.setOriginalOrderId(relatedOrder,
                                                               orderBaseBuilder);
                            } else {
                                orderBuilder.setOrderBase(rpcOrderBase);
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
                TradingRpc.SendOrderRequest sendOrderRequest = requestBuilder.build();
                SLF4JLoggerProxy.trace(TradeRpcClient.this,
                                       "{} sending {}",
                                       getSessionId(),
                                       sendOrderRequest);
                TradingRpc.SendOrderResponse response = getBlockingStub().sendOrders(sendOrderRequest);
                List<SendOrderResponse> results = new ArrayList<>();
                for(TradingRpc.OrderResponse rpcResponse : response.getOrderResponseList()) {
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
                TradingRpc.AddReportRequest.Builder requestBuilder = TradingRpc.AddReportRequest.newBuilder();
                requestBuilder.setSessionId(getSessionId().getValue());
                requestBuilder.setBrokerId(inBrokerID.getValue());
                requestBuilder.setMessage(TradeRpcUtil.getFixMessage(inReport.getMessage()));
                TradingRpc.AddReportRequest request = requestBuilder.build();
                SLF4JLoggerProxy.trace(TradeRpcClient.this,
                                       "{} sending {}",
                                       getSessionId(),
                                       request);
                TradingRpc.AddReportResponse response = getBlockingStub().addReport(request);
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
                TradingRpc.DeleteReportRequest.Builder requestBuilder = TradingRpc.DeleteReportRequest.newBuilder();
                requestBuilder.setSessionId(getSessionId().getValue());
                requestBuilder.setReportId(String.valueOf(inReportId.longValue()));
                TradingRpc.DeleteReportRequest request = requestBuilder.build();
                SLF4JLoggerProxy.trace(TradeRpcClient.this,
                                       "{} sending {}",
                                       getSessionId(),
                                       request);
                TradingRpc.DeleteReportResponse response = getBlockingStub().deleteReport(request);
                SLF4JLoggerProxy.trace(TradeRpcClient.this,
                                       "{} received {}",
                                       getSessionId(),
                                       response);
                return null;
            }}
        );
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
     * Create a new TradingRpcClient instance.
     *
     * @param inParameters a <code>TradingRpcClientParameters</code> value
     */
    TradeRpcClient(TradingRpcClientParameters inParameters)
    {
        super(inParameters);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.rpc.client.AbstractRpcClient#getBlockingStub(io.grpc.Channel)
     */
    @Override
    protected TradingRpcServiceBlockingStub getBlockingStub(Channel inChannel)
    {
        return TradingRpcServiceGrpc.newBlockingStub(inChannel);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.rpc.client.AbstractRpcClient#getAsyncStub(io.grpc.Channel)
     */
    @Override
    protected TradingRpcServiceStub getAsyncStub(Channel inChannel)
    {
        return TradingRpcServiceGrpc.newStub(inChannel);
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
        } else if(inListener instanceof BrokerStatusListener) {
            return new BrokerStatusListenerProxy((BrokerStatusListener)inListener);
        } else {
            throw new UnsupportedOperationException();
        }
    }
    /**
     * Provides an interface between broker message stream listeners and their handlers.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    private static class BrokerStatusListenerProxy
            extends BaseRpcUtil.AbstractClientListenerProxy<BrokerStatusListenerResponse,BrokerStatus,BrokerStatusListener>
    {
        /**
         * Create a new BrokerStatusListenerProxy instance.
         *
         * @param inMessageListener
         */
        protected BrokerStatusListenerProxy(BrokerStatusListener inMessageListener)
        {
            super(inMessageListener);
        }
        /* (non-Javadoc)
         * @see org.marketcetera.trading.rpc.TradingRpcClient.AbstractListenerProxy#translateMessage(java.lang.Object)
         */
        @Override
        protected BrokerStatus translateMessage(BrokerStatusListenerResponse inResponse)
        {
            return TradeRpcUtil.getBrokerStatus(inResponse).orElse(null);
        }
        /* (non-Javadoc)
         * @see org.marketcetera.trading.rpc.TradingRpcClient.AbstractListenerProxy#sendMessage(java.lang.Object, java.lang.Object)
         */
        @Override
        protected void sendMessage(BrokerStatusListener inMessageListener,
                                   BrokerStatus inMessage)
        {
            inMessageListener.receiveBrokerStatus(inMessage);
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
         * @see org.marketcetera.trading.rpc.TradingRpcClient.AbstractListenerProxy#translateMessage(java.lang.Object)
         */
        @Override
        protected TradeMessage translateMessage(TradeMessageListenerResponse inResponse)
        {
            return TradeRpcUtil.getTradeMessage(inResponse);
        }
        /* (non-Javadoc)
         * @see org.marketcetera.trading.rpc.TradingRpcClient.AbstractListenerProxy#sendMessage(java.lang.Object, java.lang.Object)
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
