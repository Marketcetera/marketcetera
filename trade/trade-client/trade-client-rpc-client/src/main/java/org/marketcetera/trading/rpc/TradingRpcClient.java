package org.marketcetera.trading.rpc;

import java.beans.ExceptionListener;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import org.marketcetera.brokers.BrokerStatusListener;
import org.marketcetera.brokers.BrokersStatus;
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
import org.marketcetera.trade.Instrument;
import org.marketcetera.trade.Option;
import org.marketcetera.trade.Order;
import org.marketcetera.trade.OrderCancel;
import org.marketcetera.trade.OrderID;
import org.marketcetera.trade.OrderReplace;
import org.marketcetera.trade.OrderSummary;
import org.marketcetera.trade.client.ReportListener;
import org.marketcetera.trade.client.SendOrderResponse;
import org.marketcetera.trade.client.TradingClient;
import org.marketcetera.trading.rpc.TradingRpcServiceGrpc.TradingRpcServiceBlockingStub;
import org.marketcetera.trading.rpc.TradingRpcServiceGrpc.TradingRpcServiceStub;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.ws.tags.AppId;

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
     * @see org.marketcetera.trade.client.TradingClient#addReportListener(org.marketcetera.trade.client.ReportListener)
     */
    @Override
    public void addReportListener(ReportListener inReportListener)
    {
        throw new UnsupportedOperationException(); // TODO
        
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.client.TradingClient#removeReportListener(org.marketcetera.trade.client.ReportListener)
     */
    @Override
    public void removeReportListener(ReportListener inReportListener)
    {
        throw new UnsupportedOperationException(); // TODO
        
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
                SLF4JLoggerProxy.trace(this,
                                       "{} sending {} order(s)",
                                       getSessionId(),
                                       inOrders.size());
                TradingRpc.SendOrderRequest.Builder requestBuilder = TradingRpc.SendOrderRequest.newBuilder();
                requestBuilder.setSessionId(getSessionId().getValue());
                TradingRpc.OrderSingle.Builder orderBuilder = TradingRpc.OrderSingle.newBuilder();
//                for(Order order : inOrders) {
//                    String account = StringUtils.trimToNull(order.getAccount());
//                    if(account != null) {
//                        orderBuilder.setAccount(account);
//                    }
//                    if(order.getBrokerID() != null) {
//                        orderBuilder.setBrokerId(order.getBrokerID().getValue());
//                    }
//                    if(order.getCustomFields() != null && !order.getCustomFields().isEmpty()) {
//                        Properties customFieldsProperties = new Properties();
//                        customFieldsProperties.putAll(order.getCustomFields());
//                        String compactProperties = Util.propertiesToString(customFieldsProperties);
//                        orderBuilder.setCustomFields(compactProperties);
//                    }
//                    if(order.getDisplayQuantity() != null) {
//                        orderBuilder.setDisplayQty(BaseUtil.getQtyValueFrom(order.getDisplayQuantity()));
//                    }
//                    if(order.getInstrument() != null) {
//                        orderBuilder.setSymbol(order.getInstrument().getFullSymbol());
//                    }
//                    if(order.getOrderCapacity() != null) {
//                        // TODO
//                    }
//                    if(order.getOrderID() != null) {
//                        orderBuilder.setOrderId(order.getOrderID().getValue());
//                    }
//                    if(order.getOrderType() != null) {
//                        orderBuilder.setOrderType(TradingUtil.getOrderTypeFromOrderType(order.getOrderType()));
//                    }
//                    if(order.getPositionEffect() != null) {
//                        // TODO
//                    }
//                    if(order.getPrice() != null) {
//                        orderBuilder.setPrice(BaseUtil.getQtyValueFrom(order.getPrice()));
//                    }
//                    if(order.getQuantity() != null) {
//                        orderBuilder.setQuantity(BaseUtil.getQtyValueFrom(order.getQuantity()));
//                    }
//                    if(order.getSecurityType() != null) {
//                        // TODO ?
//                    }
//                    if(order.getSide() != null) {
//                        orderBuilder.setSide(TradingUtil.getSideTypeFromSide(order.getSide()));
//                    }
//                    if(order.getText() != null) {
//                        // TODO
//                    }
//                    if(order.getTimeInForce() != null) {
//                        // TODO
//                    }
//                    requestBuilder.addOrder(orderBuilder.build());
//                    orderBuilder.clear();
//                }
                TradingRpc.SendOrderResponse response = getBlockingStub().sendOrders(requestBuilder.build());
                List<SendOrderResponse> results = new ArrayList<>();
                for(TradingRpc.OrderSingleResponse rpcResponse : response.getOrderResponseList()) {
                    SendOrderResponse orderResponse = new SendOrderResponse();
                    orderResponse.setFailed(rpcResponse.getFailed());
                    orderResponse.setMessage(rpcResponse.getMessage());
                    orderResponse.setOrderId(rpcResponse.getOrderid()==null?null:new OrderID(rpcResponse.getOrderid()));
                    results.add(orderResponse);
                }
                SLF4JLoggerProxy.trace(this,
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
}
