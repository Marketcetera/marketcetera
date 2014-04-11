package org.marketcetera.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.lang.Validate;
import org.marketcetera.client.RpcClient.BrokersStatusRequest;
import org.marketcetera.client.RpcClient.BrokersStatusResponse;
import org.marketcetera.client.RpcClient.Locale;
import org.marketcetera.client.RpcClient.LoginRequest;
import org.marketcetera.client.RpcClient.LoginResponse;
import org.marketcetera.client.RpcClient.LogoutRequest;
import org.marketcetera.client.RpcClient.NextOrderIdRequest;
import org.marketcetera.client.RpcClient.NextOrderIdResponse;
import org.marketcetera.client.RpcClient.OpenOrdersRequest;
import org.marketcetera.client.RpcClient.OpenOrdersResponse;
import org.marketcetera.client.RpcClient.ReportsSinceRequest;
import org.marketcetera.client.RpcClient.ReportsSinceResponse;
import org.marketcetera.client.RpcClient.RpcClientService;
import org.marketcetera.client.RpcClient.RpcClientService.BlockingInterface;
import org.marketcetera.client.brokers.BrokerStatus;
import org.marketcetera.client.brokers.BrokersStatus;
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

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id: UserInfo.java 16488 2013-02-26 02:54:25Z colin $")
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
            return clientService.getUnderlying(controller,
                                               request).getSymbol();
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
            return clientService.getOptionRoots(controller,
                                                request).getSymbolList();
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
        // TODO test Equity
        // TODO test Option
        // TODO test Future
        // TODO test Currency
        try {
            RpcClient.ResolveSymbolResponse response = clientService.resolveSymbol(controller,
                                                                                   RpcClient.ResolveSymbolRequest.newBuilder().setSessionId(sessionId.getValue()).setSymbol(inSymbol).build());
            synchronized(reportUnmarshaller) {
                return (Instrument)reportUnmarshaller.unmarshal(new StringReader(response.getInstrument().getPayload()));
            }
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
     * @see org.marketcetera.client.ClientImpl#addReport(org.marketcetera.trade.FIXMessageWrapper, org.marketcetera.trade.BrokerID)
     */
    @Override
    public void addReport(FIXMessageWrapper inReport,
                          BrokerID inBrokerID)
            throws ConnectionException
    {
        StringWriter output = new StringWriter();
        try {
            synchronized(reportMarshaller) {
                reportMarshaller.marshal(inReport,
                                         output);
            }
            RpcClient.AddReportRequest request = RpcClient.AddReportRequest.newBuilder()
                    .setBrokerId(inBrokerID.getValue())
                    .setMessage(output.toString()).build();
            SLF4JLoggerProxy.debug(this,
                                   "AddReport request: {}",
                                   request);
            RpcClient.AddReportResponse response = clientService.addReport(controller,
                                                                           request);
            SLF4JLoggerProxy.debug(this,
                                   "AddReport response: {}",
                                   response);
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
            synchronized(reportMarshaller) {
                reportMarshaller.marshal(inReport,
                                         output);
            }
            RpcClient.DeleteReportRequest request = RpcClient.DeleteReportRequest.newBuilder()
                    .setMessage(output.toString()).build();
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
        ReportsSinceRequest request = RpcClient.ReportsSinceRequest.newBuilder().setSessionId(sessionId.getValue()).setOrigin(inDate.getTime()).build(); 
        try {
            ReportsSinceResponse response = clientService.getReportsSince(controller,
                                                                          request);
            List<ReportBase> reports = Lists.newArrayList();
            for(String report : response.getReports().getReportsList()) {
                synchronized(reportUnmarshaller) {
                    reports.add((ReportBase)reportUnmarshaller.unmarshal(new StringReader(report)));
                }
            }
            // TODO test ER
            // TODO test order cancel reject
            // TODO test empty
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
        OpenOrdersRequest request = RpcClient.OpenOrdersRequest.newBuilder().setSessionId(sessionId.getValue()).build(); 
        try {
            OpenOrdersResponse response = clientService.getOpenOrders(controller,
                                                                      request);
            List<ReportBaseImpl> reports = Lists.newArrayList();
            for(String report : response.getReports().getReportsList()) {
                synchronized(reportUnmarshaller) {
                    reports.add((ReportBaseImpl)reportUnmarshaller.unmarshal(new StringReader(report)));
                }
            }
            // TODO test order cancel reject
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
                BrokerStatus brokerStatus = new BrokerStatus(rpcBrokerStatus.getName(),
                                                             new BrokerID(rpcBrokerStatus.getBrokerId()),
                                                             rpcBrokerStatus.getLoggedOn());
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
        super.close();
        try {
            try {
                clientService.logout(controller,
                                     LogoutRequest.newBuilder().setSessionId(sessionId.getValue()).build());
            } catch (Exception ignored) {}
            if(executor != null) {
                try {
                    executor.shutdownNow();
                } catch (Exception ignored) {}
            }
            if(channel != null) {
                try {
                    channel.close();
                } catch (Exception ignored) {}
            }
        } finally {
            executor = null;
            controller = null;
            clientService = null;
            channel = null;
            sessionId = null;
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.ClientImpl#getNextServerID()
     */
    @Override
    String getNextServerID()
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
        RpcClient.HeartbeatRequest heartbeatRequest = RpcClient.HeartbeatRequest.newBuilder().setSessionId(sessionId.getValue()).setId(System.nanoTime()).build();
        try {
            Validate.notNull(clientService.heartbeat(controller,
                                                     heartbeatRequest));
            return;
        } catch (ServiceException e) {
            throw new RemoteException(e);
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.ClientImpl#getSessionId()
     */
    @Override
    SessionId getSessionId()
    {
        return sessionId;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.ClientImpl#connectWebServices()
     */
    @Override
    protected void connectWebServices()
            throws I18NException, RemoteException
    {
        PeerInfo server = new PeerInfo(mParameters.getHostname(),
                                       mParameters.getPort());
        DuplexTcpClientPipelineFactory clientFactory = new DuplexTcpClientPipelineFactory();
        executor = new ThreadPoolCallExecutor(1,
                                              10);
        clientFactory.setRpcServerCallExecutor(executor);
        clientFactory.setConnectResponseTimeoutMillis(10000);
        clientFactory.setCompression(true);
        Bootstrap bootstrap = new Bootstrap();
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
        try {
            channel = clientFactory.peerWith(server,
                                             bootstrap);
            clientService = RpcClientService.newBlockingStub(channel);
            controller = channel.newRpcController();
            LoginRequest loginRequest = LoginRequest.newBuilder()
                    .setAppId("Client/2.4.0") // TODO
                    .setVersionId("1.0.0")
                    .setClientId(NodeId.generate().getValue())
                    .setLocale(Locale.newBuilder().setCountry("US").setLanguage("en").build()) // TODO
                    .setUsername(mParameters.getUsername())
                    .setPassword(new String(mParameters.getPassword())).build();
            LoginResponse loginResponse = clientService.login(controller,
                                                              loginRequest);
            sessionId = new SessionId(loginResponse.getSessionId());
        } catch (IOException | ServiceException e) {
            throw new RemoteException(e);
        }
    }
    /**
     * 
     *
     *
     * @param inDate
     * @param inInstrument
     * @return
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
     * 
     *
     *
     * @param inDate
     * @param inInstrumentType
     * @return
     */
    private <InstrumentClazz extends Instrument> Map<PositionKey<InstrumentClazz>,BigDecimal> getAllInstrumentPositionsAsOf(Date inDate,
                                                                                                                            RpcClient.InstrumentType inInstrumentType)
    {
        return getInstrumentPositionsAsOf(inDate,
                                          inInstrumentType,
                                          null);
    }
    /**
     * 
     *
     *
     * @param inDate
     * @param inOptionRoots
     * @return
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
     * 
     *
     *
     * @param inDate
     * @return
     * @throws ConnectionException
     */
    @SuppressWarnings("unchecked")
    private <InstrumentClazz extends Instrument> Map<PositionKey<InstrumentClazz>,BigDecimal> getInstrumentPositionsAsOf(Date inDate,
                                                                                                                         RpcClient.InstrumentType inInstrumentType,
                                                                                                                         Instrument inInstrument,
                                                                                                                         String...inOptionRoots)
            throws ConnectionException
    {
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
                    synchronized(reportMarshaller) {
                        reportMarshaller.marshal(inInstrument,
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
                synchronized(reportUnmarshaller) {
                    instrument = (Instrument)reportUnmarshaller.unmarshal(new StringReader(rpcInstrument.getPayload()));
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
            // TODO test equity position
            // TODO test future position
            // TODO test currency position
            // TODO test option position
            // TODO test no positions
            // TODO test one position
            // TODO test multiple positions
            return positions;
        } catch (ServiceException | JAXBException e) {
            throw new ConnectionException(e,
                                          Messages.ERROR_REMOTE_EXECUTION);
        }
    }
    /**
     * 
     */
    private RpcController controller;
    /**
     * 
     */
    private SessionId sessionId;
    /**
     * 
     */
    private BlockingInterface clientService;
    /**
     * 
     */
    private RpcServerCallExecutor executor;
    /**
     * 
     */
    private RpcClientChannel channel;
    /**
     * 
     */
    private static final JAXBContext reportContext;
    /**
     * 
     */
    private static final Marshaller reportMarshaller;
    /**
     * 
     */
    private static final Unmarshaller reportUnmarshaller;
    /**
     * performs static initialization
     */
    static {
        try {
            reportContext = JAXBContext.newInstance(ReportBaseImpl.class,Instrument.class);
            reportMarshaller = reportContext.createMarshaller();
            reportUnmarshaller = reportContext.createUnmarshaller();
        } catch (JAXBException e) {
            SLF4JLoggerProxy.error(RpcClientImpl.class,
                                   e);
            throw new RuntimeException(e);
        }
    }
}
