package org.marketcetera.trading.rpc;

import java.beans.ExceptionListener;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;

import org.marketcetera.brokers.BrokerStatusListener;
import org.marketcetera.brokers.BrokersStatus;
import org.marketcetera.core.PlatformServices;
import org.marketcetera.core.Util;
import org.marketcetera.core.Version;
import org.marketcetera.core.VersionInfo;
import org.marketcetera.core.notifications.ServerStatusListener;
import org.marketcetera.core.position.PositionKey;
import org.marketcetera.rpc.base.BaseRpc;
import org.marketcetera.rpc.base.BaseRpc.HeartbeatRequest;
import org.marketcetera.rpc.base.BaseRpc.LoginResponse;
import org.marketcetera.rpc.base.BaseRpc.LogoutResponse;
import org.marketcetera.rpc.client.AbstractRpcClient;
import org.marketcetera.trade.FIXOrder;
import org.marketcetera.trade.Instrument;
import org.marketcetera.trade.NewOrReplaceOrder;
import org.marketcetera.trade.Option;
import org.marketcetera.trade.Order;
import org.marketcetera.trade.OrderBase;
import org.marketcetera.trade.OrderCancel;
import org.marketcetera.trade.OrderID;
import org.marketcetera.trade.OrderReplace;
import org.marketcetera.trade.OrderSummary;
import org.marketcetera.trade.RelatedOrder;
import org.marketcetera.trade.TradeMessageListener;
import org.marketcetera.trade.client.SendOrderResponse;
import org.marketcetera.trade.client.TradingClient;
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

import io.grpc.Channel;
import io.grpc.stub.StreamObserver;

/* $License$ */

/**
 * Provides an RPC {@link TradingClient} implementation.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class TradingRpcClient
        extends AbstractRpcClient<TradingRpcServiceBlockingStub,TradingRpcServiceStub,TradingRpcClientParameters>
        implements TradingClient
{
    /* (non-Javadoc)
     * @see org.marketcetera.trade.client.TradingClient#addTradeMessageListener(org.marketcetera.trade.client.TradeMessageListener)
     */
    @Override
    public void addTradeMessageListener(TradeMessageListener inTradeMessageListener)
    {
        if(tradeMessageListenerWrappers.asMap().containsKey(inTradeMessageListener)) {
            return;
        }
        final TradeMessageListenerWrapper wrapper = tradeMessageListenerWrappers.getUnchecked(inTradeMessageListener);
        if(wrapper == null) {
            return;
        }
        executeCall(new Callable<Void>() {
            @Override
            public Void call()
                    throws Exception
            {
                SLF4JLoggerProxy.trace(TradingRpcClient.this,
                                       "{} adding report listener",
                                       getSessionId());
                TradingRpc.AddTradeMessageListenerRequest.Builder requestBuilder = TradingRpc.AddTradeMessageListenerRequest.newBuilder();
                requestBuilder.setSessionId(getSessionId().getValue());
                requestBuilder.setListenerId(wrapper.getId());
                TradingRpc.AddTradeMessageListenerRequest addTradeMessageListenerRequest = requestBuilder.build();
                SLF4JLoggerProxy.trace(TradingRpcClient.this,
                                       "{} sending {}",
                                       getSessionId(),
                                       addTradeMessageListenerRequest);
                getAsyncStub().addTradeMessageListener(addTradeMessageListenerRequest,
                                                       wrapper);
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
        final TradeMessageListenerWrapper wrapper = tradeMessageListenerWrappers.getIfPresent(inTradeMessageListener);
        tradeMessageListenerWrappers.invalidate(inTradeMessageListener);
        if(wrapper == null) {
            return;
        }
        tradeMessageListenerWrappersById.invalidate(wrapper.getId());
        executeCall(new Callable<Void>() {
            @Override
            public Void call()
                    throws Exception
            {
                SLF4JLoggerProxy.trace(TradingRpcClient.this,
                                       "{} removing report listener",
                                       getSessionId());
                TradingRpc.RemoveTradeMessageListenerRequest.Builder requestBuilder = TradingRpc.RemoveTradeMessageListenerRequest.newBuilder();
                requestBuilder.setSessionId(getSessionId().getValue());
                requestBuilder.setListenerId(wrapper.getId());
                TradingRpc.RemoveTradeMessageListenerRequest removeTradeMessageListenerRequest = requestBuilder.build();
                SLF4JLoggerProxy.trace(TradingRpcClient.this,
                                       "{} sending {}",
                                       getSessionId(),
                                       removeTradeMessageListenerRequest);
                TradingRpc.RemoveTradeMessageListenerResponse response = getBlockingStub().removeTradeMessageListener(removeTradeMessageListenerRequest);
                SLF4JLoggerProxy.trace(TradingRpcClient.this,
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
        throw new UnsupportedOperationException(); // TODO
        
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.client.TradingClient#removeBrokerStatusListener(org.marketcetera.brokers.BrokerStatusListener)
     */
    @Override
    public void removeBrokerStatusListener(BrokerStatusListener inBrokerStatusListener)
    {
        throw new UnsupportedOperationException(); // TODO
        
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.client.TradingClient#addServerStatusListener(org.marketcetera.core.notifications.ServerStatusListener)
     */
    @Override
    public void addServerStatusListener(ServerStatusListener inListener)
    {
        throw new UnsupportedOperationException(); // TODO
        
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.client.TradingClient#removeServerStatusListener(org.marketcetera.core.notifications.ServerStatusListener)
     */
    @Override
    public void removeServerStatusListener(ServerStatusListener inListener)
    {
        throw new UnsupportedOperationException(); // TODO
        
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.client.TradingClient#getPositionAsOf(java.util.Date, org.marketcetera.trade.Instrument)
     */
    @Override
    public BigDecimal getPositionAsOf(Date inDate,
                                      Instrument inInstrument)
    {
        throw new UnsupportedOperationException(); // TODO
        
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.client.TradingClient#getAllPositionsAsOf(java.util.Date)
     */
    @Override
    public Map<PositionKey<Instrument>,BigDecimal> getAllPositionsAsOf(Date inDate)
    {
        throw new UnsupportedOperationException(); // TODO
        
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.client.TradingClient#getOptionPositionsAsOf(java.util.Date, java.lang.String[])
     */
    @Override
    public Map<PositionKey<Option>,BigDecimal> getOptionPositionsAsOf(Date inDate,
                                                                      String... inRootSymbols)
    {
        throw new UnsupportedOperationException(); // TODO
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.client.TradingClient#addExceptionListener(java.beans.ExceptionListener)
     */
    @Override
    public void addExceptionListener(ExceptionListener inListener)
    {
        throw new UnsupportedOperationException(); // TODO
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.client.TradingClient#removeExceptionListener(java.beans.ExceptionListener)
     */
    @Override
    public void removeExceptionListener(ExceptionListener inListener)
    {
        throw new UnsupportedOperationException(); // TODO
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.client.TradingClient#getBrokersStatus()
     */
    @Override
    public BrokersStatus getBrokersStatus()
    {
        throw new UnsupportedOperationException(); // TODO
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.client.TradingClient#resolveSymbol(java.lang.String)
     */
    @Override
    public Instrument resolveSymbol(String inSymbol)
    {
        throw new UnsupportedOperationException(); // TODO
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.client.TradingClient#findRootOrderIdFor(org.marketcetera.trade.OrderID)
     */
    @Override
    public OrderID findRootOrderIdFor(OrderID inOrderID)
    {
        throw new UnsupportedOperationException(); // TODO
        
    }
    /* (non-Javadoc)
     * @see org.marketcetera.tradingclient.TradingClient#getOpenOrders(int, int)
     */
    @Override
    public List<OrderSummary> getOpenOrders(final int inPageNumber,
                                            final int inPageSize)
    {
        return executeCall(new Callable<List<OrderSummary>>() {
            @Override
            public List<OrderSummary> call()
                    throws Exception
            {
                SLF4JLoggerProxy.trace(this,
                                       "{} requesting open orders",
                                       getSessionId());
//                TradingRpc.OpenOrdersRequest.Builder requestBuilder = TradingRpc.OpenOrdersRequest.newBuilder();
//                requestBuilder.setSessionId(getSessionId().getValue());
//                requestBuilder.setPageRequest(PagingUtil.buildPageRequest(inPageNumber,
//                                                                          inPageSize));
//                TradingRpc.OpenOrdersResponse response = getBlockingStub().getOpenOrders(requestBuilder.build());
//                List<ExecutionReport> results = new ArrayList<>();
//                for(TradingRpc.OpenOrder rpcOpenOrder : response.getOrdersList()) {
//                    // TODO
//                }
//                SLF4JLoggerProxy.trace(this,
//                                       "{} returning {}",
//                                       getSessionId(),
//                                       results);
//                return results;
                throw new UnsupportedOperationException();
            }
        });
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.client.TradingClient#getOpenOrders()
     */
    @Override
    public List<OrderSummary> getOpenOrders()
    {
        throw new UnsupportedOperationException(); // TODO
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
                SLF4JLoggerProxy.trace(TradingRpcClient.this,
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
                            TradingUtil.setBrokerId(fixOrder,
                                                    fixOrderBuilder);
//                            fixOrderBuilder.setMessage(mapBuilder.build());
                            // TODO
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
                            TradingUtil.setAccount(orderBase,
                                                   orderBaseBuilder);
                            TradingUtil.setBrokerId(orderBase,
                                                    orderBaseBuilder);
                            TradingUtil.setRpcCustomFields(orderBase,
                                                        orderBaseBuilder);
                            TradingUtil.setInstrument(orderBase,
                                                      orderBaseBuilder);
                            TradingUtil.setOrderId(orderBase,
                                                   orderBaseBuilder);
                            TradingUtil.setQuantity(orderBase,
                                                    orderBaseBuilder);
                            TradingUtil.setSide(orderBase,
                                                orderBaseBuilder);
                            TradingUtil.setText(orderBase,
                                                orderBaseBuilder);
                            // now, check for various special order types
                            if(orderBase instanceof NewOrReplaceOrder) {
                                NewOrReplaceOrder newOrReplaceOrder = (NewOrReplaceOrder)orderBase;
                                TradingUtil.setDisplayQuantity(newOrReplaceOrder,
                                                               orderBaseBuilder);
                                TradingUtil.setOrderCapacity(newOrReplaceOrder,
                                                             orderBaseBuilder);
                                TradingUtil.setOrderType(newOrReplaceOrder,
                                                         orderBaseBuilder);
                                TradingUtil.setPositionEffect(newOrReplaceOrder,
                                                              orderBaseBuilder);
                                TradingUtil.setPrice(newOrReplaceOrder,
                                                     orderBaseBuilder);
                                TradingUtil.setTimeInForce(newOrReplaceOrder,
                                                           orderBaseBuilder);
                            }
                            TradingTypesRpc.OrderBase rpcOrderBase = orderBaseBuilder.build();
                            if(order instanceof RelatedOrder) {
                                RelatedOrder relatedOrder = (RelatedOrder)order;
                                TradingUtil.setOriginalOrderId(relatedOrder,
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
                        PlatformServices.handleException(TradingRpcClient.this,
                                                         "Unable to send " + order,
                                                         e);
                    }
                }
                TradingRpc.SendOrderRequest sendOrderRequest = requestBuilder.build();
                SLF4JLoggerProxy.trace(TradingRpcClient.this,
                                       "{} sending {}",
                                       getSessionId(),
                                       sendOrderRequest);
                TradingRpc.SendOrderResponse response = getBlockingStub().sendOrders(sendOrderRequest);
                List<SendOrderResponse> results = new ArrayList<>();
                for(TradingRpc.OrderResponse rpcResponse : response.getOrderResponseList()) {
                    SendOrderResponse orderResponse = new SendOrderResponse();
                    orderResponse.setFailed(rpcResponse.getFailed());
                    orderResponse.setMessage(rpcResponse.getMessage());
                    orderResponse.setOrderId(rpcResponse.getOrderid()==null?null:new OrderID(rpcResponse.getOrderid()));
                    results.add(orderResponse);
                }
                SLF4JLoggerProxy.trace(TradingRpcClient.this,
                                       "{} returning {}",
                                       getSessionId(),
                                       results);
                return results;
            }
        });
    }
    /* (non-Javadoc)
     * @see org.marketcetera.tradingclient.TradingClient#cancelOrder(org.marketcetera.trade.OrderCancel)
     */
    @Override
    public OrderID cancelOrder(OrderCancel inOrderCancel)
    {
        throw new UnsupportedOperationException(); // TODO
    }
    /* (non-Javadoc)
     * @see org.marketcetera.tradingclient.TradingClient#modifyOrder(org.marketcetera.trade.OrderReplace)
     */
    @Override
    public OrderID modifyOrder(OrderReplace inOrderReplace)
    {
        throw new UnsupportedOperationException(); // TODO
    }
    /**
     * Create a new TradingRpcClient instance.
     *
     * @param inParameters a <code>TradingRpcClientParameters</code> value
     */
    TradingRpcClient(TradingRpcClientParameters inParameters)
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
    protected void executeHeartbeat(HeartbeatRequest inRequest,
                                    StreamObserver<BaseRpc.HeartbeatResponse> inObserver)
    {
        getAsyncStub().heartbeat(inRequest,
                                 inObserver);
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
     *
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    private static class TradeMessageListenerWrapper
            implements StreamObserver<TradeMessageListenerResponse>
    {
        /* (non-Javadoc)
         * @see io.grpc.stub.StreamObserver#onNext(java.lang.Object)
         */
        @Override
        public void onNext(TradeMessageListenerResponse inValue)
        {
            SLF4JLoggerProxy.trace(TradingRpcClient.class,
                                   "{} received {}",
                                   id,
                                   inValue);
        }
        /* (non-Javadoc)
         * @see io.grpc.stub.StreamObserver#onError(java.lang.Throwable)
         */
        @Override
        public void onError(Throwable inT)
        {
            SLF4JLoggerProxy.trace(TradingRpcClient.class,
                                   "{} received {}",
                                   id,
                                   inT);
        }
        /* (non-Javadoc)
         * @see io.grpc.stub.StreamObserver#onCompleted()
         */
        @Override
        public void onCompleted()
        {
            SLF4JLoggerProxy.trace(TradingRpcClient.class,
                                   "{} completed",
                                   id);
        }
        /**
         * Get the id value.
         *
         * @return a <code>String</code> value
         */
        private String getId()
        {
            return id;
        }
        /**
         * Create a new TradeMessageListenerWrapper instance.
         *
         * @param inTradeMessageListener a <code>TradeMessageListener</code> value
         */
        private TradeMessageListenerWrapper(TradeMessageListener inTradeMessageListener)
        {
            tradeMessageListener = inTradeMessageListener;
        }
        /**
         * report listener value
         */
        private final TradeMessageListener tradeMessageListener;
        /**
         * unique id value
         */
        private final String id = UUID.randomUUID().toString();
    }
    /**
     * The client's application ID: the application name.
     */
    private static final String APP_ID_NAME = TradingRpcClient.class.getSimpleName();
    /**
     * The client's application ID: the version.
     */
    private static final VersionInfo APP_ID_VERSION = new VersionInfo(Version.pomversion);
    /**
     * The client's application ID: the ID.
     */
    private static final AppId APP_ID = Util.getAppId(APP_ID_NAME,APP_ID_VERSION.getVersionInfo());
    /**
     * holds report listeners by their id
     */
    private final Cache<String,TradeMessageListenerWrapper> tradeMessageListenerWrappersById = CacheBuilder.newBuilder().build();
    /**
     * holds report listeners by the original listener
     */
    private final LoadingCache<TradeMessageListener,TradeMessageListenerWrapper> tradeMessageListenerWrappers = CacheBuilder.newBuilder().build(new CacheLoader<TradeMessageListener,TradeMessageListenerWrapper>() {
        @Override
        public TradeMessageListenerWrapper load(TradeMessageListener inKey)
                throws Exception
        {
            TradeMessageListenerWrapper wrapper = new TradeMessageListenerWrapper(inKey);
            tradeMessageListenerWrappersById.put(wrapper.getId(),
                                           wrapper);
            return wrapper;
        }}
    );
}
