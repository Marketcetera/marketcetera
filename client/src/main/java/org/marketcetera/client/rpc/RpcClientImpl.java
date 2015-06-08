package org.marketcetera.client.rpc;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.lang.Validate;
import org.marketcetera.client.Client;
import org.marketcetera.client.ClientImpl;
import org.marketcetera.client.ClientParameters;
import org.marketcetera.client.ClientVersion;
import org.marketcetera.client.ConnectionException;
import org.marketcetera.client.Messages;
import org.marketcetera.client.brokers.BrokerStatus;
import org.marketcetera.client.brokers.BrokersStatus;
import org.marketcetera.client.rpc.RpcClient.BrokersStatusRequest;
import org.marketcetera.client.rpc.RpcClient.BrokersStatusResponse;
import org.marketcetera.client.rpc.RpcClient.Locale;
import org.marketcetera.client.rpc.RpcClient.LoginRequest;
import org.marketcetera.client.rpc.RpcClient.LoginResponse;
import org.marketcetera.client.rpc.RpcClient.LogoutRequest;
import org.marketcetera.client.rpc.RpcClient.NextOrderIdRequest;
import org.marketcetera.client.rpc.RpcClient.NextOrderIdResponse;
import org.marketcetera.client.rpc.RpcClient.OpenOrdersRequest;
import org.marketcetera.client.rpc.RpcClient.OpenOrdersResponse;
import org.marketcetera.client.rpc.RpcClient.ReportsSinceRequest;
import org.marketcetera.client.rpc.RpcClient.ReportsSinceResponse;
import org.marketcetera.client.rpc.RpcClient.RpcClientService;
import org.marketcetera.client.rpc.RpcClient.RpcClientService.BlockingInterface;
import org.marketcetera.client.users.UserInfo;
import org.marketcetera.core.Util;
import org.marketcetera.core.position.PositionKey;
import org.marketcetera.core.position.PositionKeyFactory;
import org.marketcetera.trade.BrokerID;
import org.marketcetera.trade.Currency;
import org.marketcetera.trade.Equity;
import org.marketcetera.trade.ExecutionReportImpl;
import org.marketcetera.trade.FIXMessageWrapper;
import org.marketcetera.trade.Future;
import org.marketcetera.trade.Hierarchy;
import org.marketcetera.trade.Instrument;
import org.marketcetera.trade.Option;
import org.marketcetera.trade.OrderID;
import org.marketcetera.trade.ReportBase;
import org.marketcetera.trade.ReportBaseImpl;
import org.marketcetera.trade.UserID;
import org.marketcetera.util.except.I18NException;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.ws.tags.NodeId;
import org.marketcetera.util.ws.tags.SessionId;
import org.marketcetera.util.ws.wrappers.RemoteException;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.protobuf.RpcController;
import com.google.protobuf.ServiceException;
import com.googlecode.protobuf.pro.duplex.PeerInfo;
import com.googlecode.protobuf.pro.duplex.RpcClientChannel;
import com.googlecode.protobuf.pro.duplex.client.DuplexTcpClientPipelineFactory;
import com.googlecode.protobuf.pro.duplex.execute.RpcServerCallExecutor;
import com.googlecode.protobuf.pro.duplex.execute.ThreadPoolCallExecutor;
import com.googlecode.protobuf.pro.duplex.logging.CategoryPerServiceLogger;

/* $License$ */

/**
 * Provides an RPC implementation of {@link Client}.
 * 
 * <p>This client replaces the web services component of the standard client,
 * not the JMS component.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 2.4.0
 */
@ThreadSafe
@ClassVersion("$Id$")
public class RpcClientImpl
        extends ClientImpl
{
    /**
     * Create a new RpcClientImpl instance.
     *
     * @param inParameters a <code>ClientParameters</code> value
     * @throws ConnectionException if an error occurs connecting to the RPC client
     */
    public RpcClientImpl(ClientParameters inParameters)
            throws ConnectionException
    {
        super(inParameters);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.ClientImpl#getAllEquityPositionsAsOf(java.util.Date)
     */
    @Override
    public Map<PositionKey<Equity>,BigDecimal> getAllEquityPositionsAsOf(Date inDate)
            throws ConnectionException
    {
        return getAllInstrumentPositionsAsOf(inDate,
                                             RpcClient.InstrumentType.EQUITY);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.ClientImpl#getEquityPositionAsOf(java.util.Date, org.marketcetera.trade.Equity)
     */
    @Override
    public BigDecimal getEquityPositionAsOf(Date inDate,
                                            Equity inEquity)
            throws ConnectionException
    {
        return getInstrumentPositionAsOf(inDate,
                                         inEquity);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.ClientImpl#getCurrencyPositionAsOf(java.util.Date, org.marketcetera.trade.Currency)
     */
    @Override
    public BigDecimal getCurrencyPositionAsOf(Date inDate,
                                              Currency inCurrency)
            throws ConnectionException
    {
        return getInstrumentPositionAsOf(inDate,
                                         inCurrency);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.ClientImpl#getAllCurrencyPositionsAsOf(java.util.Date)
     */
    @Override
    public Map<PositionKey<Currency>,BigDecimal> getAllCurrencyPositionsAsOf(Date inDate)
            throws ConnectionException
    {
        return getAllInstrumentPositionsAsOf(inDate,
                                             RpcClient.InstrumentType.CURRENCY);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.ClientImpl#getAllFuturePositionsAsOf(java.util.Date)
     */
    @Override
    public Map<PositionKey<Future>,BigDecimal> getAllFuturePositionsAsOf(Date inDate)
            throws ConnectionException
    {
        return getAllInstrumentPositionsAsOf(inDate,
                                             RpcClient.InstrumentType.FUTURE);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.ClientImpl#getFuturePositionAsOf(java.util.Date, org.marketcetera.trade.Future)
     */
    @Override
    public BigDecimal getFuturePositionAsOf(Date inDate,
                                            Future inFuture)
            throws ConnectionException
    {
        return getInstrumentPositionAsOf(inDate,
                                         inFuture);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.ClientImpl#getOptionPositionAsOf(java.util.Date, org.marketcetera.trade.Option)
     */
    @Override
    public BigDecimal getOptionPositionAsOf(Date inDate,
                                            Option inOption)
            throws ConnectionException
    {
        return getInstrumentPositionAsOf(inDate,
                                         inOption);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.ClientImpl#getAllOptionPositionsAsOf(java.util.Date)
     */
    @Override
    public Map<PositionKey<Option>,BigDecimal> getAllOptionPositionsAsOf(Date inDate)
            throws ConnectionException
    {
        return getAllInstrumentPositionsAsOf(inDate,
                                             RpcClient.InstrumentType.OPTION);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.ClientImpl#getOptionPositionsAsOf(java.util.Date, java.lang.String[])
     */
    @Override
    public Map<PositionKey<Option>,BigDecimal> getOptionPositionsAsOf(Date inDate,
                                                                      String... inSymbols)
            throws ConnectionException
    {
        return getAllInstrumentPositionsAsOf(inDate,
                                             inSymbols);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.ClientImpl#getUnderlying(java.lang.String)
     */
    @Override
    public String getUnderlying(String inOptionRoot)
            throws ConnectionException
    {
        RpcClient.UnderlyingRequest request = RpcClient.UnderlyingRequest.newBuilder().setSessionId(sessionId.getValue()).setSymbol(inOptionRoot).build();
        try {
            RpcClient.UnderlyingResponse response = clientService.getUnderlying(controller,
                                                                                request);
            return response.hasSymbol()?response.getSymbol():null;
        } catch (ServiceException e) {
            throw new ConnectionException(e,
                                          Messages.ERROR_REMOTE_EXECUTION);
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.ClientImpl#getOptionRoots(java.lang.String)
     */
    @Override
    public Collection<String> getOptionRoots(String inUnderlying)
            throws ConnectionException
    {
        RpcClient.OptionRootsRequest request = RpcClient.OptionRootsRequest.newBuilder().setSessionId(sessionId.getValue()).setSymbol(inUnderlying).build();
        try {
            RpcClient.OptionRootsResponse response = clientService.getOptionRoots(controller,
                                                                                  request);
            return response.getSymbolList();
        } catch (ServiceException e) {
            throw new ConnectionException(e,
                                          Messages.ERROR_REMOTE_EXECUTION);
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.ClientImpl#resolveSymbol(java.lang.String)
     */
    @Override
    public Instrument resolveSymbol(String inSymbol)
            throws ConnectionException
    {
        try {
            SLF4JLoggerProxy.debug(this,
                                   "Resolving {}",
                                   inSymbol);
            RpcClient.ResolveSymbolResponse response = clientService.resolveSymbol(controller,
                                                                                   RpcClient.ResolveSymbolRequest.newBuilder().setSessionId(sessionId.getValue()).setSymbol(inSymbol).build());
            Instrument instrument = null;
            if(response.hasInstrument()) {
                synchronized(contextLock) {
                    Unmarshaller unmarshaller = getUnmarshaller();
                    instrument = (Instrument)unmarshaller.unmarshal(new StringReader(response.getInstrument().getPayload()));
                }
                SLF4JLoggerProxy.debug(this,
                                       "Resolved {} to {}",
                                       inSymbol,
                                       instrument);
            }
            return instrument;
        } catch (ServiceException | JAXBException e) {
            throw new ConnectionException(e,
                                          Messages.ERROR_REMOTE_EXECUTION);
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.ClientImpl#findRootOrderIdFor(org.marketcetera.trade.OrderID)
     */
    @Override
    public OrderID findRootOrderIdFor(OrderID inOrderID)
    {
        RpcClient.RootOrderIdRequest request = RpcClient.RootOrderIdRequest.newBuilder().setSessionId(sessionId.getValue()).setOrderId(inOrderID.getValue()).build();
        try {
            return new OrderID(clientService.getRootOrderIdFor(controller,
                                                               request).getOrderId());
        } catch (ServiceException e) {
            throw new ConnectionException(e,
                                          Messages.ERROR_REMOTE_EXECUTION);
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.ClientImpl#addReport(org.marketcetera.trade.FIXMessageWrapper, org.marketcetera.trade.BrokerID, org.marketcetera.trade.Hierarchy)
     */
    @Override
    public void addReport(FIXMessageWrapper inReport,
                          BrokerID inBrokerID,
                          Hierarchy inHierarchy)
            throws ConnectionException
    {
        StringWriter output = new StringWriter();
        try {
            synchronized(contextLock) {
                Marshaller marshaller = getMarshaller();
                marshaller.marshal(inReport,
                                   output);
            }
            RpcClient.AddReportRequest request = RpcClient.AddReportRequest.newBuilder()
                    .setSessionId(sessionId.getValue())
                    .setBrokerId(inBrokerID.getValue())
                    .setMessage(output.toString())
                    .setHierarchy(RpcClient.Hierarchy.valueOf(inHierarchy.name())).build();
            SLF4JLoggerProxy.debug(this,
                                   "AddReport request: {}",
                                   request);
            RpcClient.AddReportResponse response = clientService.addReport(controller,
                                                                           request);
            SLF4JLoggerProxy.debug(this,
                                   "AddReport response: {}",
                                   response);
            if(!response.getStatus()) {
                throw new RuntimeException(response.getMessage());
            }
        } catch (JAXBException | ServiceException e) {
            throw new ConnectionException(e,
                                          Messages.ERROR_REMOTE_EXECUTION);
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.ClientImpl#deleteReport(org.marketcetera.trade.ExecutionReportImpl)
     */
    @Override
    public void deleteReport(ExecutionReportImpl inReport)
            throws ConnectionException
    {
        StringWriter output = new StringWriter();
        try {
            synchronized(contextLock) {
                Marshaller marshaller = getMarshaller();
                marshaller.marshal(inReport,
                                         output);
            }
            RpcClient.DeleteReportRequest request = RpcClient.DeleteReportRequest.newBuilder().setSessionId(sessionId.getValue()).setMessage(output.toString()).build();
            SLF4JLoggerProxy.debug(this,
                                   "DeleteReport request: {}",
                                   request);
            RpcClient.DeleteReportResponse response = clientService.deleteReport(controller,
                                                                                 request);
            SLF4JLoggerProxy.debug(this,
                                   "DeleteReport response: {}",
                                   response);
        } catch (JAXBException | ServiceException e) {
            throw new ConnectionException(e,
                                          Messages.ERROR_REMOTE_EXECUTION);
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.ClientImpl#getUserData()
     */
    @Override
    public Properties getUserData()
            throws ConnectionException
    {
        RpcClient.GetUserDataRequest request = RpcClient.GetUserDataRequest.newBuilder().setSessionId(sessionId.getValue()).build();
        try {
            RpcClient.GetUserDataResponse response = clientService.getUserData(controller,
                                                                               request);
            Properties userData;
            if(response.hasUserData()) {
                userData = Util.propertiesFromString(response.getUserData());
            } else {
                userData = new Properties();
            }
            return userData;
        } catch (ServiceException e) {
            throw new ConnectionException(e,
                                          Messages.ERROR_REMOTE_EXECUTION);
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.ClientImpl#setUserData(java.util.Properties)
     */
    @Override
    public void setUserData(Properties inProperties)
            throws ConnectionException
    {
        RpcClient.SetUserDataRequest.Builder requestBuilder = RpcClient.SetUserDataRequest.newBuilder().setSessionId(sessionId.getValue());
        if(inProperties != null) {
            requestBuilder.setUserData(Util.propertiesToString(inProperties));
        }
        try {
            clientService.setUserData(controller,
                                      requestBuilder.build());
        } catch (ServiceException e) {
            throw new ConnectionException(e,
                                          Messages.ERROR_REMOTE_EXECUTION);
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.ClientImpl#getUserInfo(org.marketcetera.trade.UserID, boolean)
     */
    @Override
    public UserInfo getUserInfo(UserID inId,
                                boolean inUseCache)
            throws ConnectionException
    {
        RpcClient.UserInfoRequest request = RpcClient.UserInfoRequest.newBuilder().setSessionId(sessionId.getValue()).setId(inId.getValue()).build();
        try {
            RpcClient.UserInfoResponse response = clientService.getUserInfo(controller,
                                                                            request);
            String userData = response.getUserInfo().getUserdata();
            Properties props = new Properties();
            if(userData != null) {
                props = Util.propertiesFromString(userData);
            }
            UserInfo userInfo = new UserInfo(response.getUserInfo().getName(),
                                             new UserID(response.getUserInfo().getId()),
                                             response.getUserInfo().getActive(),
                                             response.getUserInfo().getSuperuser(),
                                             props);
            return userInfo;
        } catch (ServiceException e) {
            throw new ConnectionException(e,
                                          Messages.ERROR_REMOTE_EXECUTION);
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.ClientImpl#getReportsSince(java.util.Date)
     */
    @Override
    public ReportBase[] getReportsSince(Date inDate)
            throws ConnectionException
    {
        SLF4JLoggerProxy.debug(this,
                               "Requesting reports since {}",
                               inDate);
        ReportsSinceRequest request = RpcClient.ReportsSinceRequest.newBuilder().setSessionId(sessionId.getValue()).setOrigin(inDate.getTime()).build(); 
        try {
            ReportsSinceResponse response = clientService.getReportsSince(controller,
                                                                          request);
            List<ReportBase> reports = Lists.newArrayList();
            for(String report : response.getReports().getReportsList()) {
                synchronized(contextLock) {
                    reports.add((ReportBase)getUnmarshaller().unmarshal(new StringReader(report)));
                }
            }
            SLF4JLoggerProxy.debug(this,
                                   "Retrieved reports: {}",
                                   reports);
            return reports.toArray(new ReportBase[reports.size()]);
        } catch (ServiceException | JAXBException e) {
            throw new ConnectionException(e,
                                          Messages.ERROR_REMOTE_EXECUTION);
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.ClientImpl#getOpenOrders()
     */
    @Override
    public List<ReportBaseImpl> getOpenOrders()
            throws ConnectionException
    {
        SLF4JLoggerProxy.debug(this,
                               "Requesting open orders");
        OpenOrdersRequest request = RpcClient.OpenOrdersRequest.newBuilder().setSessionId(sessionId.getValue()).build(); 
        try {
            OpenOrdersResponse response = clientService.getOpenOrders(controller,
                                                                      request);
            List<ReportBaseImpl> reports = Lists.newArrayList();
            for(String report : response.getReports().getReportsList()) {
                synchronized(contextLock) {
                    reports.add((ReportBaseImpl)getUnmarshaller().unmarshal(new StringReader(report)));
                }
            }
            SLF4JLoggerProxy.debug(this,
                                   "Retrieved open orders: {}",
                                   reports);
            return reports;
        } catch (ServiceException | JAXBException e) {
            throw new ConnectionException(e,
                                          Messages.ERROR_REMOTE_EXECUTION);
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.ClientImpl#getBrokersStatus()
     */
    @Override
    public BrokersStatus getBrokersStatus()
            throws ConnectionException
    {
        BrokersStatusRequest brokersStatusRequest = BrokersStatusRequest.newBuilder().setSessionId(sessionId.getValue()).build();
        try {
            BrokersStatusResponse brokersStatusResponse = clientService.getBrokersStatus(controller,
                                                                                         brokersStatusRequest);
            RpcClient.BrokersStatus rpcBrokersStatus = brokersStatusResponse.getBrokersStatus();
            List<BrokerStatus> brokers = Lists.newArrayList();
            for(RpcClient.BrokerStatus rpcBrokerStatus : rpcBrokersStatus.getBrokersList()) {
                Map<String,String> settings = new HashMap<>();
                for(RpcClient.SessionSetting settingEntry : rpcBrokerStatus.getSettingsList()) {
                    settings.put(settingEntry.getKey(),
                                 settingEntry.getValue());
                }
                BrokerStatus brokerStatus = new BrokerStatus(rpcBrokerStatus.getName(),
                                                             new BrokerID(rpcBrokerStatus.getBrokerId()),
                                                             rpcBrokerStatus.getLoggedOn(),
                                                             settings);
                brokers.add(brokerStatus);
            }
            BrokersStatus brokersStatus = new BrokersStatus(brokers);
            return brokersStatus;
        } catch (ServiceException e) {
            throw new ConnectionException(e,
                                          Messages.ERROR_REMOTE_EXECUTION);
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.ClientImpl#close()
     */
    @Override
    public synchronized void close()
    {
        try {
            super.close();
        } catch (Exception ignored) {}
        stopRpcServices();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.ClientImpl#getNextServerID()
     */
    @Override
    protected String getNextServerID()
            throws RemoteException
    {
        NextOrderIdRequest nextOrderIdRequest = NextOrderIdRequest.newBuilder().setSessionId(sessionId.getValue()).build();
        try {
            NextOrderIdResponse nextOrderIdResponse = clientService.getNextOrderID(controller,
                                                                                   nextOrderIdRequest);
            return nextOrderIdResponse.getOrderId();
        } catch (ServiceException e) {
            throw new RemoteException(e);
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.ClientImpl#heartbeat()
     */
    @Override
    protected void heartbeat()
            throws RemoteException
    {
        RpcClient.HeartbeatRequest request = RpcClient.HeartbeatRequest.newBuilder().setId(System.nanoTime()).build();
        try {
            if(clientService == null) {
                throw new IllegalStateException();
            }
            clientService.heartbeat(controller,
                                    request);
            return;
        } catch (Exception e) {
            throw new RemoteException(e);
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.ClientImpl#getSessionId()
     */
    @Override
    protected SessionId getSessionId()
    {
        return sessionId;
    }
    private boolean initialized = false;
    private DuplexTcpClientPipelineFactory clientFactory;
    private PeerInfo server;
    private Bootstrap bootstrap;
    /* (non-Javadoc)
     * @see org.marketcetera.client.ClientImpl#connectWebServices()
     */
    @Override
    protected void connectWebServices()
            throws I18NException, RemoteException
    {
        SLF4JLoggerProxy.debug(this,
                               "Connecting to RPC server at {}:{}",
                               mParameters.getHostname(),
                               mParameters.getPort());
        if(!initialized) {
            server = new PeerInfo(mParameters.getHostname(),
                                  mParameters.getPort());
            clientFactory = new DuplexTcpClientPipelineFactory();
            executor = new ThreadPoolCallExecutor(1,
                                                  10);
            clientFactory.setRpcServerCallExecutor(executor);
            clientFactory.setConnectResponseTimeoutMillis(10000);
            clientFactory.setCompression(true);
            bootstrap = new Bootstrap();
            bootstrap.group(new NioEventLoopGroup());
            bootstrap.handler(clientFactory);
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.option(ChannelOption.TCP_NODELAY,
                             true);
            bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS,
                             10000);
            bootstrap.option(ChannelOption.SO_SNDBUF,
                             1048576);
            bootstrap.option(ChannelOption.SO_RCVBUF,
                             1048576);
            CategoryPerServiceLogger logger = new CategoryPerServiceLogger();
            logger.setLogRequestProto(false);
            logger.setLogResponseProto(false);
            clientFactory.setRpcLogger(logger);
            initialized = true;
        }
        try {
            channel = clientFactory.peerWith(server,
                                             bootstrap);
            clientService = RpcClientService.newBlockingStub(channel);
            controller = channel.newRpcController();
            java.util.Locale currentLocale = java.util.Locale.getDefault();
            LoginRequest loginRequest = LoginRequest.newBuilder()
                    .setAppId(ClientVersion.APP_ID.getValue())
                    .setVersionId(ClientVersion.APP_ID_VERSION.getVersionInfo())
                    .setClientId(NodeId.generate().getValue())
                    .setLocale(Locale.newBuilder()
                               .setCountry(currentLocale.getCountry()==null?"":currentLocale.getCountry())
                               .setLanguage(currentLocale.getLanguage()==null?"":currentLocale.getLanguage())
                               .setVariant(currentLocale.getVariant()==null?"":currentLocale.getVariant()).build())
                    .setUsername(mParameters.getUsername())
                    .setPassword(new String(mParameters.getPassword())).build();
            LoginResponse loginResponse = clientService.login(controller,
                                                              loginRequest);
            sessionId = new SessionId(loginResponse.getSessionId());
        } catch (Exception e) {
            try {
                stopRpcServices();
            } catch (Exception ignored) {}
            throw new RemoteException(e);
        }
    }
    
    /* (non-Javadoc)
     * @see org.marketcetera.client.ClientImpl#reconnectWebServices()
     */
    @Override
    protected void reconnectWebServices()
            throws RemoteException
    {
        try {
            stopRpcServices();
        } catch (Exception ignored) {
        }
        connectWebServices();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.ClientImpl#closeWebServices()
     */
    @Override
    protected void closeWebServices()
            throws RemoteException
    {
        stopRpcServices();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.ClientImpl#connectJms()
     */
    @Override
    protected void connectJms()
            throws JAXBException
    {
        if(useJms()) {
            super.connectJms();
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.ClientImpl#startJms()
     */
    @Override
    protected void startJms()
            throws JAXBException
    {
        if(useJms()) {
            super.startJms();
        }
    }
    /**
     * Indicates if the client should active JMS or not.
     *
     * @return a <code>boolean</code> value
     */
    private boolean useJms()
    {
        if(mParameters instanceof RpcClientParameters) {
            RpcClientParameters rpcParms = (RpcClientParameters)mParameters;
            return rpcParms.getUseJms();
        }
        return true;
    }
    /**
     * Stops RPC services.
     */
    private void stopRpcServices()
    {
        try {
            try {
                clientService.logout(controller,
                                     LogoutRequest.newBuilder().setSessionId(sessionId.getValue()).build());
            } catch (Exception ignored) {}
            if(channel != null) {
                try {
                    channel.close();
                } catch (Exception ignored) {}
            }
            if(controller != null) {
                try {
                    controller.reset();
                } catch (Exception ignored) {}
            }
        } finally {
            controller = null;
            clientService = null;
            channel = null;
            sessionId = null;
        }
    }
    /**
     * Gets the position of the given instrument at the given point in time.
     *
     * @param inDate a <code>Date</code> value
     * @param inInstrument an <code>InstrumentClazz</code> value
     * @return a <code>BigDecimal</code> value
     */
    private <InstrumentClazz extends Instrument> BigDecimal getInstrumentPositionAsOf(Date inDate,
                                                                                      InstrumentClazz inInstrument)
    {
        Map<PositionKey<InstrumentClazz>,BigDecimal> positionMap = getInstrumentPositionsAsOf(inDate,
                                                                                              null,
                                                                                              inInstrument);
        if(positionMap == null || positionMap.isEmpty()) {
            return BigDecimal.ZERO;
        }
        Validate.isTrue(positionMap.size() == 1);
        return positionMap.values().iterator().next();
    }
    /**
     * Gets the positions of all instruments of the given type at the given point in time.
     *
     * @param inDate a <code>Date</code> value
     * @param inInstrumentType an <code>RpcClient.InstrumentType</code> value or <code>null</code>
     * @return a <code>Map&lt;PositionKey&lt;InstrumentClazz&gt;,BigDecimal&gt;</code> value
     * @throws ConnectionException if an error occurs connecting to the server
     */
    private <InstrumentClazz extends Instrument> Map<PositionKey<InstrumentClazz>,BigDecimal> getAllInstrumentPositionsAsOf(Date inDate,
                                                                                                                            RpcClient.InstrumentType inInstrumentType)
    {
        return getInstrumentPositionsAsOf(inDate,
                                          inInstrumentType,
                                          null);
    }
    /**
     * Gets the positions of all options of the given roots at the given point in time.
     *
     * @param inDate a <code>Date</code> value
     * @param inOptionRoots a <code>String...</code> value
     * @return a <code>Map&lt;PositionKey&lt;InstrumentClazz&gt;,BigDecimal&gt;</code> value
     * @throws ConnectionException if an error occurs connecting to the server
     */
    private <InstrumentClazz extends Instrument> Map<PositionKey<InstrumentClazz>,BigDecimal> getAllInstrumentPositionsAsOf(Date inDate,
                                                                                                                            String...inOptionRoots)
    {
        return getInstrumentPositionsAsOf(inDate,
                                          null,
                                          null,
                                          inOptionRoots);
    }
    /**
     * Gets the positions of instrument or instruments as described by the parameters.
     *
     * @param inDate a <code>Date</code> value
     * @param inInstrumentType an <code>RpcClient.InstrumentType</code> value or <code>null</code>
     * @param inInstrument an <code>Instrument</code> value or <code>null</code>
     * @param inOptionRoots a <code>String...</code> value, may be empty
     * @return a <code>Map&lt;PositionKey&lt;InstrumentClazz&gt;,BigDecimal&gt;</code> value
     * @throws ConnectionException if an error occurs connecting to the server
     */
    @SuppressWarnings("unchecked")
    private <InstrumentClazz extends Instrument> Map<PositionKey<InstrumentClazz>,BigDecimal> getInstrumentPositionsAsOf(Date inDate,
                                                                                                                         RpcClient.InstrumentType inInstrumentType,
                                                                                                                         Instrument inInstrument,
                                                                                                                         String...inOptionRoots)
    {
        SLF4JLoggerProxy.debug(this,
                               "Getting instrument position as of {} for {}/{}/{}",
                               inDate,
                               inInstrumentType,
                               inInstrument,
                               inOptionRoots == null?"":Arrays.toString(inOptionRoots));
        try {
            RpcClient.PositionRequest request; 
            if(inInstrument == null) {
                if(inInstrumentType == null) {
                    RpcClient.PositionRequest.Builder requestBuilder = RpcClient.PositionRequest.newBuilder().setSessionId(sessionId.getValue()).setOrigin(inDate.getTime());
                    for(String optionRoot : inOptionRoots) {
                        requestBuilder.addRoot(optionRoot);
                    }
                    request = requestBuilder.build();
                } else {
                    request = RpcClient.PositionRequest.newBuilder().setSessionId(sessionId.getValue()).setInstrumentType(inInstrumentType).setOrigin(inDate.getTime()).build(); 
                }
            } else {
                StringWriter output = new StringWriter();
                try {
                    synchronized(contextLock) {
                        getMarshaller().marshal(inInstrument,
                                                output);
                    }
                } catch (JAXBException e) {
                    throw new ServiceException(e);
                }
                request = RpcClient.PositionRequest.newBuilder()
                        .setSessionId(sessionId.getValue())
                        .setInstrument(RpcClient.Instrument.newBuilder().setPayload(output.toString()))
                        .setOrigin(inDate.getTime()).build(); 
            }
            RpcClient.PositionResponse response = clientService.getPositions(controller,
                                                                             request);
            Map<PositionKey<InstrumentClazz>,BigDecimal> positions = Maps.newHashMap();
            Validate.isTrue(response.getKeysCount() == response.getValuesCount());
            for(int index=0;index<response.getKeysCount();index++) {
                RpcClient.PositionKey rpcKey = response.getKeys(index);
                RpcClient.Instrument rpcInstrument = rpcKey.getInstrument();
                String rpcAccount = rpcKey.getAccount();
                String rpcTraderId = rpcKey.getTraderId();
                Instrument instrument;
                synchronized(contextLock) {
                    instrument = (Instrument)getUnmarshaller().unmarshal(new StringReader(rpcInstrument.getPayload()));
                }
                PositionKey<? extends Instrument> positionKey = null;
                if(instrument instanceof Equity) {
                    positionKey = PositionKeyFactory.createEquityKey(instrument.getSymbol(),
                                                                     rpcAccount,
                                                                     rpcTraderId);
                } else if(instrument instanceof Option) {
                    Option option = (Option)instrument;
                    positionKey = PositionKeyFactory.createOptionKey(option.getSymbol(),
                                                                     option.getExpiry(),
                                                                     option.getStrikePrice(),
                                                                     option.getType(),
                                                                     rpcAccount,
                                                                     rpcTraderId);
                } else if(instrument instanceof Future) {
                    Future future = (Future)instrument;
                    positionKey = PositionKeyFactory.createFutureKey(future.getSymbol(),
                                                                     future.getExpiryAsString(),
                                                                     rpcAccount,
                                                                     rpcTraderId);
                } else if(instrument instanceof Currency) {
                    Currency currency = (Currency)instrument;
                    positionKey = PositionKeyFactory.createCurrencyKey(currency.getSymbol(),
                                                                       rpcAccount,
                                                                       rpcTraderId);
                } else {
                    throw new UnsupportedOperationException();
                }
                positions.put((PositionKey<InstrumentClazz>)positionKey,
                              new BigDecimal(response.getValues(index)));
            }
            SLF4JLoggerProxy.debug(this,
                                   "Returning positions: {}",
                                   positions);
            return positions;
        } catch (ServiceException | JAXBException e) {
            throw new ConnectionException(e,
                                          Messages.ERROR_REMOTE_EXECUTION);
        }
    }
    /**
     * Gets the list of context classes to use with the JAXB context.
     *
     * @return a <code>Class&lt;?&gt;[]</code> value
     */
    private Class<?>[] getContextClasses()
    {
        if(mParameters instanceof RpcClientParameters) {
            RpcClientParameters params = (RpcClientParameters)mParameters;
            if(params.getContextClassProvider() != null) {
                return params.getContextClassProvider().getContextClasses();
            }
        }
        return new Class<?>[0];
    }
    /**
     * Gets the context marshaller.
     *
     * @return a <code>Marshaller</code> value
     * @throws JAXBException if an error occurs creating the marshaller 
     */
    private Marshaller getMarshaller()
            throws JAXBException
    {
        synchronized(contextLock) {
            if(context == null) {
                initContext();
            }
            return marshaller;
        }
    }
    /**
     * Gets the context unmarshaller.
     *
     * @return an <code>Unmarshaller</code> value
     * @throws JAXBException if an error occurs creating the unmarshaller 
     */
    private Unmarshaller getUnmarshaller()
            throws JAXBException
    {
        synchronized(contextLock) {
            if(context == null) {
                initContext();
            }
            return unmarshaller;
        }
    }
    /**
     * Initializes the marshalling/unmarshalling context.
     *
     * @throws JAXBException if an error occurs creating the unmarshaller 
     */
    private void initContext()
            throws JAXBException
    {
        synchronized(contextLock) {
            context = JAXBContext.newInstance(getContextClasses());
            marshaller = context.createMarshaller();
            unmarshaller = context.createUnmarshaller();
        }
    }
    /**
     * controller responsible for the RPC connection
     */
    private RpcController controller;
    /**
     * session ID value for this connection, may be <code>null</code> if the connection is inactive
     */
    private SessionId sessionId;
    /**
     * provides access to RPC services
     */
    private BlockingInterface clientService;
    /**
     * executes the nitty-gritty of the calls
     */
    private RpcServerCallExecutor executor;
    /**
     * channel over which calls are made
     */
    private RpcClientChannel channel;
    /**
     * guards access to JAXB context objects
     */
    private final Object contextLock = new Object();
    /**
     * context used to serialize and unserialize messages as necessary
     */
    @GuardedBy("contextLock")
    private JAXBContext context;
    /**
     * marshals messages
     */
    @GuardedBy("contextLock")
    private Marshaller marshaller;
    /**
     * unmarshals messages
     */
    @GuardedBy("contextLock")
    private Unmarshaller unmarshaller;
}
