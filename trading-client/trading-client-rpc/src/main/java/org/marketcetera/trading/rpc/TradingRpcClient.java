package org.marketcetera.trading.rpc;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Callable;

import org.apache.commons.lang.StringUtils;
import org.marketcetera.core.Util;
import org.marketcetera.core.Version;
import org.marketcetera.core.VersionInfo;
import org.marketcetera.rpc.BaseRpcClient;
import org.marketcetera.trade.ExecutionReport;
import org.marketcetera.trade.OrderCancel;
import org.marketcetera.trade.OrderID;
import org.marketcetera.trade.OrderReplace;
import org.marketcetera.trade.OrderSingle;
import org.marketcetera.trading.rpc.TradingRpc.TradingRpcService.BlockingInterface;
import org.marketcetera.tradingclient.SendOrderResponse;
import org.marketcetera.tradingclient.TradingClient;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.rpc.BaseRpc.HeartbeatRequest;
import org.marketcetera.util.rpc.BaseRpc.HeartbeatResponse;
import org.marketcetera.util.rpc.BaseRpc.LoginRequest;
import org.marketcetera.util.rpc.BaseRpc.LoginResponse;
import org.marketcetera.util.rpc.BaseRpc.LogoutRequest;
import org.marketcetera.util.rpc.BaseRpc.LogoutResponse;
import org.marketcetera.util.rpc.BaseUtil;
import org.marketcetera.util.rpc.PagingUtil;
import org.marketcetera.util.ws.tags.AppId;

import com.google.protobuf.RpcController;
import com.google.protobuf.ServiceException;
import com.googlecode.protobuf.pro.duplex.RpcClientChannel;

/* $License$ */

/**
 * Provides an RPC {@link TradingClient} interface.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class TradingRpcClient
        extends BaseRpcClient<TradingRpc.TradingRpcService.BlockingInterface>
        implements TradingClient
{
    /* (non-Javadoc)
     * @see org.marketcetera.tradingclient.TradingClient#getOpenOrders(int, int)
     */
    @Override
    public List<ExecutionReport> getOpenOrders(final int inPageNumber,
                                               final int inPageSize)
    {
        return executeCall(new Callable<List<ExecutionReport>>(){
            @Override
            public List<ExecutionReport> call()
                    throws Exception
            {
                SLF4JLoggerProxy.trace(this,
                                       "{} requesting open orders",
                                       getSessionId());
                TradingRpc.OpenOrdersRequest.Builder requestBuilder = TradingRpc.OpenOrdersRequest.newBuilder();
                requestBuilder.setSessionId(getSessionId().getValue());
                requestBuilder.setPageRequest(PagingUtil.buildPageRequest(inPageNumber,
                                                                          inPageSize));
                TradingRpc.OpenOrdersResponse response = getClientService().getOpenOrders(getController(),
                                                                                                requestBuilder.build());
                List<ExecutionReport> results = new ArrayList<>();
                if(response.getStatus().getFailed()) {
                    throw new RuntimeException(response.getStatus().getMessage());
                }
                for(TradingRpc.OpenOrder rpcOpenOrder : response.getOrdersList()) {
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
     * @see org.marketcetera.tradingclient.TradingClient#sendOrders(java.util.List)
     */
    @Override
    public List<SendOrderResponse> sendOrders(final List<OrderSingle> inOrders)
    {
        return executeCall(new Callable<List<SendOrderResponse>>(){
            @Override
            public List<SendOrderResponse> call()
                    throws Exception
            {
                SLF4JLoggerProxy.trace(this,
                                       "{} sending",
                                       getSessionId(),
                                       inOrders);
                TradingRpc.SendOrderRequest.Builder requestBuilder = TradingRpc.SendOrderRequest.newBuilder();
                requestBuilder.setSessionId(getSessionId().getValue());
                TradingRpc.OrderSingle.Builder orderBuilder = TradingRpc.OrderSingle.newBuilder();
                for(OrderSingle order : inOrders) {
                    String account = StringUtils.trimToNull(order.getAccount());
                    if(account != null) {
                        orderBuilder.setAccount(account);
                    }
                    if(order.getBrokerID() != null) {
                        orderBuilder.setBrokerId(order.getBrokerID().getValue());
                    }
                    if(order.getCustomFields() != null && !order.getCustomFields().isEmpty()) {
                        Properties customFieldsProperties = new Properties();
                        customFieldsProperties.putAll(order.getCustomFields());
                        String compactProperties = Util.propertiesToString(customFieldsProperties);
                        orderBuilder.setCustomFields(compactProperties);
                    }
                    if(order.getDisplayQuantity() != null) {
                        orderBuilder.setDisplayQty(BaseUtil.getQtyValueFrom(order.getDisplayQuantity()));
                    }
                    if(order.getInstrument() != null) {
                        orderBuilder.setSymbol(order.getInstrument().getFullSymbol());
                    }
                    if(order.getOrderCapacity() != null) {
                        // TODO
                    }
                    if(order.getOrderType() != null) {
                        orderBuilder.setOrderType(TradingUtil.getOrderTypeFromOrderType(order.getOrderType()));
                    }
                    if(order.getPositionEffect() != null) {
                        // TODO
                    }
                    if(order.getPrice() != null) {
                        orderBuilder.setPrice(BaseUtil.getQtyValueFrom(order.getPrice()));
                    }
                    if(order.getQuantity() != null) {
                        orderBuilder.setQuantity(BaseUtil.getQtyValueFrom(order.getQuantity()));
                    }
                    if(order.getSecurityType() != null) {
                        // TODO ?
                    }
                    if(order.getSide() != null) {
                        orderBuilder.setSide(TradingUtil.getSideTypeFromSide(order.getSide()));
                    }
                    if(order.getText() != null) {
                        // TODO
                    }
                    if(order.getTimeInForce() != null) {
                        // TODO
                    }
                    requestBuilder.addOrder(orderBuilder.build());
                    orderBuilder.clear();
                }
                TradingRpc.SendOrderResponse response = getClientService().sendOrders(getController(),
                                                                                      requestBuilder.build());
                List<SendOrderResponse> results = new ArrayList<>();
                if(response.getStatus().getFailed()) {
                    throw new RuntimeException(response.getStatus().getMessage());
                }
                for(TradingRpc.OrderSingleResponse rpcResponse : response.getOrderResponseList()) {
                    SendOrderResponse orderResponse = new SendOrderResponse();
                    orderResponse.setFailed(rpcResponse.getStatus().getFailed());
                    if(rpcResponse.getStatus().hasMessage()) {
                        orderResponse.setMessage(rpcResponse.getStatus().getMessage());
                    }
                    if(rpcResponse.hasOrderid()) {
                        orderResponse.setOrderId(new OrderID(rpcResponse.getOrderid()));
                    }
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
    /* (non-Javadoc)
     * @see org.marketcetera.rpc.BaseRpcClient#getAppId()
     */
    @Override
    protected AppId getAppId()
    {
        return APP_ID;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.rpc.BaseRpcClient#getVersionInfo()
     */
    @Override
    protected VersionInfo getVersionInfo()
    {
        return APP_ID_VERSION;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.rpc.BaseRpcClient#createClient(com.googlecode.protobuf.pro.duplex.RpcClientChannel)
     */
    @Override
    protected BlockingInterface createClient(RpcClientChannel inChannel)
    {
        return TradingRpc.TradingRpcService.newBlockingStub(inChannel);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.rpc.BaseRpcClient#executeLogin(com.google.protobuf.RpcController, org.marketcetera.util.rpc.BaseRpc.LoginRequest)
     */
    @Override
    protected LoginResponse executeLogin(RpcController inController,
                                         LoginRequest inRequest)
            throws ServiceException
    {
        return getClientService().login(inController,
                                        inRequest);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.rpc.BaseRpcClient#executeLogout(com.google.protobuf.RpcController, org.marketcetera.util.rpc.BaseRpc.LogoutRequest)
     */
    @Override
    protected LogoutResponse executeLogout(RpcController inController,
                                           LogoutRequest inRequest)
            throws ServiceException
    {
        return getClientService().logout(inController,
                                         inRequest);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.rpc.BaseRpcClient#executeHeartbeat(com.google.protobuf.RpcController, org.marketcetera.util.rpc.BaseRpc.HeartbeatRequest)
     */
    @Override
    protected HeartbeatResponse executeHeartbeat(RpcController inController,
                                                 HeartbeatRequest inRequest)
            throws ServiceException
    {
        return getClientService().heartbeat(inController,
                                            inRequest);
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
