package org.marketcetera.client.rpc;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.lang.Validate;
import org.marketcetera.algo.BrokerAlgoSpec;
import org.marketcetera.algo.BrokerAlgoTagSpec;
import org.marketcetera.client.brokers.BrokerStatus;
import org.marketcetera.client.brokers.BrokersStatus;
import org.marketcetera.client.rpc.RpcClient.AddReportRequest;
import org.marketcetera.client.rpc.RpcClient.AddReportResponse;
import org.marketcetera.client.rpc.RpcClient.BrokersStatusRequest;
import org.marketcetera.client.rpc.RpcClient.BrokersStatusResponse;
import org.marketcetera.client.rpc.RpcClient.DeleteReportRequest;
import org.marketcetera.client.rpc.RpcClient.DeleteReportResponse;
import org.marketcetera.client.rpc.RpcClient.GetUserDataRequest;
import org.marketcetera.client.rpc.RpcClient.GetUserDataResponse;
import org.marketcetera.client.rpc.RpcClient.HeartbeatRequest;
import org.marketcetera.client.rpc.RpcClient.HeartbeatResponse;
import org.marketcetera.client.rpc.RpcClient.LoginRequest;
import org.marketcetera.client.rpc.RpcClient.LoginResponse;
import org.marketcetera.client.rpc.RpcClient.LogoutRequest;
import org.marketcetera.client.rpc.RpcClient.LogoutResponse;
import org.marketcetera.client.rpc.RpcClient.NextOrderIdRequest;
import org.marketcetera.client.rpc.RpcClient.NextOrderIdResponse;
import org.marketcetera.client.rpc.RpcClient.OpenOrdersRequest;
import org.marketcetera.client.rpc.RpcClient.OpenOrdersResponse;
import org.marketcetera.client.rpc.RpcClient.OptionRootsRequest;
import org.marketcetera.client.rpc.RpcClient.OptionRootsResponse;
import org.marketcetera.client.rpc.RpcClient.PositionRequest;
import org.marketcetera.client.rpc.RpcClient.PositionResponse;
import org.marketcetera.client.rpc.RpcClient.ReportsSinceRequest;
import org.marketcetera.client.rpc.RpcClient.ReportsSinceResponse;
import org.marketcetera.client.rpc.RpcClient.ResolveSymbolRequest;
import org.marketcetera.client.rpc.RpcClient.ResolveSymbolResponse;
import org.marketcetera.client.rpc.RpcClient.RootOrderIdRequest;
import org.marketcetera.client.rpc.RpcClient.RootOrderIdResponse;
import org.marketcetera.client.rpc.RpcClient.RpcClientService;
import org.marketcetera.client.rpc.RpcClient.SetUserDataRequest;
import org.marketcetera.client.rpc.RpcClient.SetUserDataResponse;
import org.marketcetera.client.rpc.RpcClient.UnderlyingRequest;
import org.marketcetera.client.rpc.RpcClient.UnderlyingResponse;
import org.marketcetera.client.rpc.RpcClient.UserInfoRequest;
import org.marketcetera.client.rpc.RpcClient.UserInfoResponse;
import org.marketcetera.client.users.UserInfo;
import org.marketcetera.core.Util;
import org.marketcetera.core.position.PositionKey;
import org.marketcetera.trade.BrokerID;
import org.marketcetera.trade.Currency;
import org.marketcetera.trade.Equity;
import org.marketcetera.trade.ExecutionReport;
import org.marketcetera.trade.FIXMessageWrapper;
import org.marketcetera.trade.Future;
import org.marketcetera.trade.Instrument;
import org.marketcetera.trade.Option;
import org.marketcetera.trade.OrderID;
import org.marketcetera.trade.ReportBaseImpl;
import org.marketcetera.trade.UserID;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.ws.ContextClassProvider;
import org.marketcetera.util.ws.stateful.Authenticator;
import org.marketcetera.util.ws.stateful.SessionHolder;
import org.marketcetera.util.ws.stateful.SessionManager;
import org.marketcetera.util.ws.stateless.StatelessClientContext;
import org.marketcetera.util.ws.tags.AppId;
import org.marketcetera.util.ws.tags.NodeId;
import org.marketcetera.util.ws.tags.SessionId;
import org.marketcetera.util.ws.wrappers.LocaleWrapper;
import org.marketcetera.util.ws.wrappers.MapWrapper;
import org.springframework.context.Lifecycle;

import quickfix.Message;

import com.google.common.collect.Maps;
import com.google.protobuf.BlockingService;
import com.google.protobuf.RpcController;
import com.google.protobuf.ServiceException;
import com.googlecode.protobuf.pro.duplex.PeerInfo;
import com.googlecode.protobuf.pro.duplex.execute.RpcServerCallExecutor;
import com.googlecode.protobuf.pro.duplex.execute.ThreadPoolCallExecutor;
import com.googlecode.protobuf.pro.duplex.server.DuplexTcpServerPipelineFactory;
import com.googlecode.protobuf.pro.duplex.util.RenamingThreadFactoryProxy;

/* $License$ */

/**
 * Provides an RPC server implementation for {@link RpcClient} objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public class RpcServer<SessionClazz>
        implements Lifecycle,RpcClientService.BlockingInterface
{
    /* (non-Javadoc)
     * @see org.springframework.context.Lifecycle#isRunning()
     */
    @Override
    public boolean isRunning()
    {
        return running.get();
    }
    /* (non-Javadoc)
     * @see org.springframework.context.Lifecycle#start()
     */
    @Override
    @PostConstruct
    public synchronized void start()
    {
        Validate.notNull(rpcHostname);
        Validate.isTrue(rpcPort > 0 && rpcPort < 65536);
        Validate.notNull(sessionManager);
        Validate.notNull(authenticator);
        Validate.notNull(serverAdapter);
        Validate.isTrue(threadPoolCore > 0);
        Validate.isTrue(threadPoolMax > 0);
        Validate.isTrue(threadPoolMax >= threadPoolCore);
        Validate.isTrue(sendBufferSize > 0);
        Validate.isTrue(receiveBufferSize > 0);
        Messages.SERVER_STARTING.info(this,
                                      rpcHostname,
                                      rpcPort);
        if(isRunning()) {
            stop();
        }
        try {
            reportContext = JAXBContext.newInstance(contextClassProvider==null?new Class<?>[0]:contextClassProvider.getContextClasses());
            marshaller = reportContext.createMarshaller();
            unmarshaller = reportContext.createUnmarshaller();
        } catch (JAXBException e) {
            SLF4JLoggerProxy.error(this,
                                   e);
            throw new RuntimeException(e);
        }
        PeerInfo serverInfo = new PeerInfo(getRpcHostname(),
                                           getRpcPort());
        executor = new ThreadPoolCallExecutor(threadPoolCore,
                                              threadPoolMax);
        DuplexTcpServerPipelineFactory serverFactory = new DuplexTcpServerPipelineFactory(serverInfo);
        serverFactory.setRpcServerCallExecutor(executor);
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(new NioEventLoopGroup(0,
                                              new RenamingThreadFactoryProxy("boss",
                                                                             Executors.defaultThreadFactory())),
                        new NioEventLoopGroup(0,
                                              new RenamingThreadFactoryProxy("worker",
                                                                             Executors.defaultThreadFactory())));
        bootstrap.channel(NioServerSocketChannel.class);
        bootstrap.childHandler(serverFactory);
        bootstrap.localAddress(serverInfo.getPort());
        bootstrap.option(ChannelOption.SO_SNDBUF,
                         sendBufferSize);
        bootstrap.option(ChannelOption.SO_RCVBUF,
                         receiveBufferSize);
        bootstrap.childOption(ChannelOption.SO_RCVBUF,
                              receiveBufferSize);
        bootstrap.childOption(ChannelOption.SO_SNDBUF,
                              sendBufferSize);
        bootstrap.option(ChannelOption.TCP_NODELAY,
                         noDelay);
        BlockingService service = RpcClientService.newReflectiveBlockingService(this);
        serverFactory.getRpcServiceRegistry().registerService(service);
        channelToken = bootstrap.bind();
        while(!channelToken.isDone()) {
            try {
                Thread.sleep(250);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        // TODO throw exception?
        running.set(channelToken.isSuccess());
        //RpcClientConnectionRegistry clientRegistry = new RpcClientConnectionRegistry();
        //serverFactory.registerConnectionEventListener(clientRegistry);
    }
    /* (non-Javadoc)
     * @see org.springframework.context.Lifecycle#stop()
     */
    @Override
    @PreDestroy
    public synchronized void stop()
    {
        Messages.SERVER_STOPPING.info(this);
        try {
            try {
                if(executor != null) {
                    executor.shutdownNow();
                }
            } catch (Exception ignored) {}
            try {
                if(channelToken != null && channelToken.channel() != null) {
                    channelToken.channel().close();
                }
            } catch (Exception ignored) {}
            for(SessionId session : rpcSessions.keySet()) {
                try {
                    sessionManager.remove(session);
                } catch (Exception ignored) {}
            }
        } finally {
            rpcSessions.clear();
            channelToken = null;
            executor = null;
            reportContext = null;
            marshaller = null;
            unmarshaller = null;
            running.set(false);
        }
    }
    /**
     * Get the sendBufferSize value.
     *
     * @return an <code>int</code> value
     */
    public int getSendBufferSize()
    {
        return sendBufferSize;
    }
    /**
     * Sets the sendBufferSize value.
     *
     * @param inSendBufferSize an <code>int</code> value
     */
    public void setSendBufferSize(int inSendBufferSize)
    {
        sendBufferSize = inSendBufferSize;
    }
    /**
     * Get the receiveBufferSize value.
     *
     * @return an <code>int</code> value
     */
    public int getReceiveBufferSize()
    {
        return receiveBufferSize;
    }
    /**
     * Sets the receiveBufferSize value.
     *
     * @param inReceiveBufferSize an <code>int</code> value
     */
    public void setReceiveBufferSize(int inReceiveBufferSize)
    {
        receiveBufferSize = inReceiveBufferSize;
    }
    /**
     * Get the noDelay value.
     *
     * @return a <code>boolean</code> value
     */
    public boolean getNoDelay()
    {
        return noDelay;
    }
    /**
     * Sets the noDelay value.
     *
     * @param inNoDelay a <code>boolean</code> value
     */
    public void setNoDelay(boolean inNoDelay)
    {
        noDelay = inNoDelay;
    }
    /**
     * Get the threadPoolCore value.
     *
     * @return an <code>int</code> value
     */
    public int getThreadPoolCore()
    {
        return threadPoolCore;
    }
    /**
     * Sets the threadPoolCore value.
     *
     * @param inThreadPoolCore an <code>int</code> value
     */
    public void setThreadPoolCore(int inThreadPoolCore)
    {
        threadPoolCore = inThreadPoolCore;
    }
    /**
     * Get the threadPoolMax value.
     *
     * @return an <code>int</code> value
     */
    public int getThreadPoolMax()
    {
        return threadPoolMax;
    }
    /**
     * Sets the threadPoolMax value.
     *
     * @param inThreadPoolMax an <code>int</code> value
     */
    public void setThreadPoolMax(int inThreadPoolMax)
    {
        threadPoolMax = inThreadPoolMax;
    }
    /**
     * Get the contextClassProvider value.
     *
     * @return a <code>ContextClassProvider</code> value
     */
    public ContextClassProvider getContextClassProvider()
    {
        return contextClassProvider;
    }
    /**
     * Sets the contextClassProvider value.
     *
     * @param inContextClassProvider a <code>ContextClassProvider</code> value
     */
    public void setContextClassProvider(ContextClassProvider inContextClassProvider)
    {
        contextClassProvider = inContextClassProvider;
    }
    /**
     * Get the serverAdapter value.
     *
     * @return an <code>RpcServerAdapter</code> value
     */
    public RpcServerAdapter getServerAdapter()
    {
        return serverAdapter;
    }
    /**
     * Sets the serverAdapter value.
     *
     * @param inServerAdapter an <code>RpcServerAdapter</code> value
     */
    public void setServerAdapter(RpcServerAdapter inServerAdapter)
    {
        serverAdapter = inServerAdapter;
    }
    /**
     * Get the authenticator value.
     *
     * @return an <code>Authenticator</code> value
     */
    public Authenticator getAuthenticator()
    {
        return authenticator;
    }
    /**
     * Sets the authenticator value.
     *
     * @param inAuthenticator an <code>Authenticator</code> value
     */
    public void setAuthenticator(Authenticator inAuthenticator)
    {
        authenticator = inAuthenticator;
    }
    /**
     * Get the sessionManager value.
     *
     * @return a <code>SessionManager&lt;SessionClazz&gt;</code> value
     */
    public SessionManager<SessionClazz> getSessionManager()
    {
        return sessionManager;
    }
    /**
     * Sets the sessionManager value.
     *
     * @param inSessionManager a <code>SessionManager&lt;SessionClazz&gt;</code> value
     */
    public void setSessionManager(SessionManager<SessionClazz> inSessionManager)
    {
        sessionManager = inSessionManager;
    }
    /**
     * Get the rpcHostname value.
     *
     * @return a <code>String</code> value
     */
    public String getRpcHostname()
    {
        return rpcHostname;
    }
    /**
     * Sets the rpcHostname value.
     *
     * @param inRpcHostname a <code>String</code> value
     */
    public void setHostname(String inRpcHostname)
    {
        rpcHostname = inRpcHostname;
    }
    /**
     * Get the rpcPort value.
     *
     * @return an <code>int</code> value
     */
    public int getRpcPort()
    {
        return rpcPort;
    }
    /**
     * Sets the rpcPort value.
     *
     * @param inRpcPort an <code>int</code> value
     */
    public void setPort(int inRpcPort)
    {
        rpcPort = inRpcPort;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.RpcClient.RpcClientService.BlockingInterface#login(com.google.protobuf.RpcController, org.marketcetera.client.RpcClient.LoginRequest)
     */
    @Override
    public LoginResponse login(RpcController inController,
                               LoginRequest inRequest)
            throws ServiceException
    {
        SLF4JLoggerProxy.debug(this,
                               "RpcClient Service received authentication request for {}",
                               inRequest.getUsername());
        StatelessClientContext context = new StatelessClientContext();
        context.setAppId(new AppId(inRequest.getAppId()));
        context.setClientId(new NodeId(inRequest.getClientId()));
        LocaleWrapper locale = new LocaleWrapper(new Locale(inRequest.getLocale().getLanguage(),
                                                            inRequest.getLocale().getCountry(),
                                                            inRequest.getLocale().getVariant()));
        context.setLocale(locale);
        try {
            authenticator.shouldAllow(context,
                                      inRequest.getUsername(),
                                      inRequest.getPassword().toCharArray());
            SessionId sessionId = SessionId.generate();
            SessionHolder<SessionClazz> sessionHolder = new SessionHolder<SessionClazz>(inRequest.getUsername(),
                                                                                        context);
            sessionManager.put(sessionId,
                               sessionHolder);
            rpcSessions.put(sessionId,
                            inRequest.getUsername());
            return LoginResponse.newBuilder().setSessionId(sessionId.getValue()).build();
        } catch (Exception e) {
            SLF4JLoggerProxy.warn(this,
                                  e);
            throw new ServiceException(e);
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.RpcClient.RpcClientService.BlockingInterface#logout(com.google.protobuf.RpcController, org.marketcetera.client.RpcClient.LogoutRequest)
     */
    @Override
    public LogoutResponse logout(RpcController inController,
                                 LogoutRequest inRequest)
            throws ServiceException
    {
        SLF4JLoggerProxy.debug(this,
                               "RpcClient Service received logout request for {}",
                               inRequest.getSessionId());
        invalidateSession(inRequest.getSessionId());
        return LogoutResponse.newBuilder().setStatus(true).build();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.RpcClient.RpcClientService.BlockingInterface#getNextOrderID(com.google.protobuf.RpcController, org.marketcetera.client.RpcClient.NextOrderIdRequest)
     */
    @Override
    public NextOrderIdResponse getNextOrderID(RpcController inController,
                                              NextOrderIdRequest inRequest)
            throws ServiceException
    {
        validateAndReturnSession(inRequest.getSessionId());
        String nextOrderId = serverAdapter.getNextOrderID();
        return NextOrderIdResponse.newBuilder().setOrderId(nextOrderId).build();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.RpcClient.RpcClientService.BlockingInterface#getBrokersStatus(com.google.protobuf.RpcController, org.marketcetera.client.RpcClient.BrokersStatusRequest)
     */
    @Override
    public BrokersStatusResponse getBrokersStatus(RpcController inController,
                                                  BrokersStatusRequest inRequest)
            throws ServiceException
    {
        SessionHolder<SessionClazz> sessionInfo = validateAndReturnSession(inRequest.getSessionId());
        BrokersStatus brokersStatus = serverAdapter.getBrokersStatus(sessionInfo.getUser());
        BrokersStatusResponse.Builder responseBuilder = BrokersStatusResponse.newBuilder();
        RpcClient.BrokersStatus.Builder rpcBrokersStatusBuilder = RpcClient.BrokersStatus.newBuilder();
        for(BrokerStatus broker : brokersStatus.getBrokers()) {
            RpcClient.BrokerStatus.Builder rpcBrokerBuilder = RpcClient.BrokerStatus.newBuilder();
            rpcBrokerBuilder.setName(broker.getName())
                .setBrokerId(broker.getId().getValue())
                .setLoggedOn(broker.getLoggedOn());
            Set<BrokerAlgoSpec> algos = broker.getBrokerAlgos();
            if(algos != null) {
                for(BrokerAlgoSpec algoSpec : algos) {
                    RpcClient.BrokerAlgoSpec.Builder algoSpecBuilder = RpcClient.BrokerAlgoSpec.newBuilder();
                    algoSpecBuilder.setName(algoSpec.getName());
                    for(BrokerAlgoTagSpec algoTagSpec : algoSpec.getAlgoTagSpecs()) {
                        RpcClient.BrokerAlgoTagSpec.Builder algoTagSpecBuilder = RpcClient.BrokerAlgoTagSpec.newBuilder();
                        algoTagSpecBuilder.setDescription(algoTagSpec.getDescription()==null?"":algoTagSpec.getDescription())
                            .setMandatory(algoTagSpec.getIsMandatory())
                            .setLabel(algoTagSpec.getLabel()==null?"":algoTagSpec.getLabel())
                            .setPattern(algoTagSpec.getPattern()==null?"":algoTagSpec.getPattern())
                            .setTag(algoTagSpec.getTag());
                        if(algoTagSpec.getOptions() != null) {
                            Properties props = new Properties();
                            props.putAll(algoTagSpec.getOptions());
                            algoTagSpecBuilder.setOptions(Util.propertiesToString(props));
                        }
                        algoSpecBuilder.addAlgoTagSpecs(algoTagSpecBuilder.build());
                    }
                    rpcBrokerBuilder.addBrokerAlgos(algoSpecBuilder.build());
                }
            }
            rpcBrokersStatusBuilder.addBrokers(rpcBrokerBuilder.build());
        }
        responseBuilder.setBrokersStatus(rpcBrokersStatusBuilder.build());
        BrokersStatusResponse response = responseBuilder.build();
        return response;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.RpcClient.RpcClientService.BlockingInterface#getOpenOrders(com.google.protobuf.RpcController, org.marketcetera.client.RpcClient.OpenOrdersRequest)
     */
    @Override
    public OpenOrdersResponse getOpenOrders(RpcController inController,
                                            OpenOrdersRequest inRequest)
            throws ServiceException
    {
        SessionHolder<SessionClazz> sessionInfo = validateAndReturnSession(inRequest.getSessionId());
        List<ReportBaseImpl> reports = serverAdapter.getOpenOrders(sessionInfo.getUser());
        RpcClient.ReportList.Builder rpcReportListBuilder = RpcClient.ReportList.newBuilder();
        if(reports != null) {
            for(ReportBaseImpl report : reports) {
                StringWriter output = new StringWriter();
                try {
                    synchronized(marshaller) {
                        marshaller.marshal(report,
                                           output);
                    }
                } catch (JAXBException e) {
                    throw new ServiceException(e);
                }
                rpcReportListBuilder.addReports(output.toString());
            }
        }
        return RpcClient.OpenOrdersResponse.newBuilder().setReports(rpcReportListBuilder.build()).build();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.RpcClient.RpcClientService.BlockingInterface#getReportsSince(com.google.protobuf.RpcController, org.marketcetera.client.RpcClient.ReportsSinceRequest)
     */
    @Override
    public ReportsSinceResponse getReportsSince(RpcController inController,
                                                ReportsSinceRequest inRequest)
            throws ServiceException
    {
        SessionHolder<SessionClazz> sessionInfo = validateAndReturnSession(inRequest.getSessionId());
        ReportBaseImpl[] reports = serverAdapter.getReportsSince(sessionInfo.getUser(),
                                                                 new Date(inRequest.getOrigin()));
        RpcClient.ReportList.Builder rpcReportListBuilder = RpcClient.ReportList.newBuilder();
        if(reports != null) {
            for(ReportBaseImpl report : reports) {
                StringWriter output = new StringWriter();
                try {
                    synchronized(marshaller) {
                        marshaller.marshal(report,
                                           output);
                    }
                } catch (JAXBException e) {
                    throw new ServiceException(e);
                }
                rpcReportListBuilder.addReports(output.toString());
            }
        }
        return RpcClient.ReportsSinceResponse.newBuilder().setReports(rpcReportListBuilder.build()).build();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.RpcClient.RpcClientService.BlockingInterface#getPositions(com.google.protobuf.RpcController, org.marketcetera.client.RpcClient.PositionRequest)
     */
    @Override
    public PositionResponse getPositions(RpcController inController,
                                         PositionRequest inRequest)
            throws ServiceException
    {
        SessionHolder<SessionClazz> sessionInfo = validateAndReturnSession(inRequest.getSessionId());
        RpcClient.PositionResponse.Builder rpcPositionResponseBuilder = RpcClient.PositionResponse.newBuilder();
        if(inRequest.hasInstrument()) {
            Instrument instrument;
            Date origin = new Date(inRequest.getOrigin());
            BigDecimal position;
            synchronized(unmarshaller) {
                try {
                    instrument = (Instrument)unmarshaller.unmarshal(new StringReader(inRequest.getInstrument().getPayload()));
                } catch (JAXBException e) {
                    throw new ServiceException(e);
                }
            }
            if(instrument instanceof Equity) {
                position = serverAdapter.getEquityPositionAsOf(sessionInfo.getUser(),
                                                               origin,
                                                               (Equity)instrument);
            } else if(instrument instanceof Option) {
                position = serverAdapter.getOptionPositionAsOf(sessionInfo.getUser(),
                                                                   origin,
                                                                   (Option)instrument);
            } else if(instrument instanceof Future) {
                position = serverAdapter.getFuturePositionAsOf(sessionInfo.getUser(),
                                                                   origin,
                                                                   (Future)instrument);
            } else if(instrument instanceof Currency) {
                position = serverAdapter.getCurrencyPositionAsOf(sessionInfo.getUser(),
                                                                     origin,
                                                                     (Currency)instrument);
            } else {
                throw new UnsupportedOperationException();
            }
            RpcClient.PositionKey rpcPositionKey = RpcClient.PositionKey.newBuilder().setInstrument(inRequest.getInstrument()).build();
            rpcPositionResponseBuilder.addKeys(rpcPositionKey);
            rpcPositionResponseBuilder.addValues(position.toPlainString());
        } else {
            if(inRequest.hasInstrumentType()) {
                switch(inRequest.getInstrumentType()) {
                    case CURRENCY:
                    {
                        MapWrapper<PositionKey<Currency>,BigDecimal> positions = serverAdapter.getAllCurrencyPositionsAsOf(sessionInfo.getUser(),
                                                                                                                           new Date(inRequest.getOrigin()));
                        for(Map.Entry<PositionKey<Currency>,BigDecimal> entry : positions.getMap().entrySet()) {
                            PositionKey<Currency> positionKey = entry.getKey();
                            StringWriter output = new StringWriter();
                            try {
                                synchronized(marshaller) {
                                    marshaller.marshal(positionKey.getInstrument(),
                                                             output);
                                }
                            } catch (JAXBException e) {
                                throw new ServiceException(e);
                            }
                            RpcClient.PositionKey rpcPositionKey = RpcClient.PositionKey.newBuilder()
                                    .setAccount(positionKey.getAccount()==null?"":positionKey.getAccount())
                                    .setInstrument(RpcClient.Instrument.newBuilder().setPayload(output.toString()))
                                    .setTraderId(positionKey.getTraderId()==null?"":positionKey.getTraderId()).build();
                            rpcPositionResponseBuilder.addKeys(rpcPositionKey);
                            rpcPositionResponseBuilder.addValues(entry.getValue().toPlainString());
                        }
                        break;
                    }
                    case EQUITY:
                    {
                        MapWrapper<PositionKey<Equity>,BigDecimal> positions = serverAdapter.getAllEquityPositionsAsOf(sessionInfo.getUser(),
                                                                                                                       new Date(inRequest.getOrigin()));
                        for(Map.Entry<PositionKey<Equity>,BigDecimal> entry : positions.getMap().entrySet()) {
                            PositionKey<Equity> positionKey = entry.getKey();
                            StringWriter output = new StringWriter();
                            try {
                                synchronized(marshaller) {
                                    marshaller.marshal(positionKey.getInstrument(),
                                                             output);
                                }
                            } catch (JAXBException e) {
                                throw new ServiceException(e);
                            }
                            RpcClient.PositionKey rpcPositionKey = RpcClient.PositionKey.newBuilder()
                                    .setAccount(positionKey.getAccount()==null?"":positionKey.getAccount())
                                    .setInstrument(RpcClient.Instrument.newBuilder().setPayload(output.toString()))
                                    .setTraderId(positionKey.getTraderId()==null?"":positionKey.getTraderId()).build();
                            rpcPositionResponseBuilder.addKeys(rpcPositionKey);
                            rpcPositionResponseBuilder.addValues(entry.getValue().toPlainString());
                        }
                        break;
                    }
                    case FUTURE:
                    {
                        MapWrapper<PositionKey<Future>,BigDecimal> positions = serverAdapter.getAllFuturePositionsAsOf(sessionInfo.getUser(),
                                                                                                                       new Date(inRequest.getOrigin()));
                        for(Map.Entry<PositionKey<Future>,BigDecimal> entry : positions.getMap().entrySet()) {
                            PositionKey<Future> positionKey = entry.getKey();
                            StringWriter output = new StringWriter();
                            try {
                                synchronized(marshaller) {
                                    marshaller.marshal(positionKey.getInstrument(),
                                                             output);
                                }
                            } catch (JAXBException e) {
                                throw new ServiceException(e);
                            }
                            RpcClient.PositionKey rpcPositionKey = RpcClient.PositionKey.newBuilder()
                                    .setAccount(positionKey.getAccount()==null?"":positionKey.getAccount())
                                    .setInstrument(RpcClient.Instrument.newBuilder().setPayload(output.toString()))
                                    .setTraderId(positionKey.getTraderId()==null?"":positionKey.getTraderId()).build();
                            rpcPositionResponseBuilder.addKeys(rpcPositionKey);
                            rpcPositionResponseBuilder.addValues(entry.getValue().toPlainString());
                        }
                        break;
                    }
                    case OPTION:
                    {
                        MapWrapper<PositionKey<Option>,BigDecimal> positions = serverAdapter.getAllOptionPositionsAsOf(sessionInfo.getUser(),
                                                                                                                       new Date(inRequest.getOrigin()));
                        for(Map.Entry<PositionKey<Option>,BigDecimal> entry : positions.getMap().entrySet()) {
                            PositionKey<Option> positionKey = entry.getKey();
                            StringWriter output = new StringWriter();
                            try {
                                synchronized(marshaller) {
                                    marshaller.marshal(positionKey.getInstrument(),
                                                             output);
                                }
                            } catch (JAXBException e) {
                                throw new ServiceException(e);
                            }
                            RpcClient.PositionKey rpcPositionKey = RpcClient.PositionKey.newBuilder()
                                    .setAccount(positionKey.getAccount()==null?"":positionKey.getAccount())
                                    .setInstrument(RpcClient.Instrument.newBuilder().setPayload(output.toString()))
                                    .setTraderId(positionKey.getTraderId()==null?"":positionKey.getTraderId()).build();
                            rpcPositionResponseBuilder.addKeys(rpcPositionKey);
                            rpcPositionResponseBuilder.addValues(entry.getValue().toPlainString());
                        }
                        break;
                    }
                    default:
                        throw new UnsupportedOperationException();
                }
            } else {
                MapWrapper<PositionKey<Option>,BigDecimal> positions = serverAdapter.getOptionPositionsAsOf(sessionInfo.getUser(),
                                                                                                            new Date(inRequest.getOrigin()),
                                                                                                            inRequest.getRootList().toArray(new String[inRequest.getRootCount()]));
                for(Map.Entry<PositionKey<Option>,BigDecimal> entry : positions.getMap().entrySet()) {
                    PositionKey<Option> positionKey = entry.getKey();
                    StringWriter output = new StringWriter();
                    try {
                        synchronized(marshaller) {
                            marshaller.marshal(positionKey.getInstrument(),
                                                     output);
                        }
                    } catch (JAXBException e) {
                        throw new ServiceException(e);
                    }
                    RpcClient.PositionKey rpcPositionKey = RpcClient.PositionKey.newBuilder()
                            .setAccount(positionKey.getAccount()==null?"":positionKey.getAccount())
                            .setInstrument(RpcClient.Instrument.newBuilder().setPayload(output.toString()))
                            .setTraderId(positionKey.getTraderId()==null?"":positionKey.getTraderId()).build();
                    rpcPositionResponseBuilder.addKeys(rpcPositionKey);
                    rpcPositionResponseBuilder.addValues(entry.getValue().toPlainString());
                }
            }
        }
        return rpcPositionResponseBuilder.build();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.RpcClient.RpcClientService.BlockingInterface#heartbeat(com.google.protobuf.RpcController, org.marketcetera.client.RpcClient.HeartbeatRequest)
     */
    @Override
    public HeartbeatResponse heartbeat(RpcController inController,
                                       HeartbeatRequest inRequest)
            throws ServiceException
    {
        return RpcClient.HeartbeatResponse.newBuilder().setId(inRequest.getId()).build();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.RpcClient.RpcClientService.BlockingInterface#getUserInfo(com.google.protobuf.RpcController, org.marketcetera.client.RpcClient.UserInfoRequest)
     */
    @Override
    public UserInfoResponse getUserInfo(RpcController inController,
                                        UserInfoRequest inRequest)
            throws ServiceException
    {
        validateAndReturnSession(inRequest.getSessionId());
        UserInfo userInfo = serverAdapter.getUserInfo(new UserID(inRequest.getId()));
        String userData = "";
        if(userInfo.getUserData() != null) {
            userData = Util.propertiesToString(userInfo.getUserData());
        }
        RpcClient.UserInfo rpcUserInfo = RpcClient.UserInfo.newBuilder()
                .setActive(userInfo.getActive())
                .setId(userInfo.getId().getValue())
                .setName(userInfo.getName())
                .setSuperuser(userInfo.getSuperuser())
                .setUserdata(userData).build();
        return RpcClient.UserInfoResponse.newBuilder().setUserInfo(rpcUserInfo).build();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.RpcClient.RpcClientService.BlockingInterface#getUnderlying(com.google.protobuf.RpcController, org.marketcetera.client.RpcClient.UnderlyingRequest)
     */
    @Override
    public UnderlyingResponse getUnderlying(RpcController inController,
                                            UnderlyingRequest inRequest)
            throws ServiceException
    {
        validateAndReturnSession(inRequest.getSessionId());
        return RpcClient.UnderlyingResponse.newBuilder().setSymbol(serverAdapter.getUnderlying(inRequest.getSymbol())).build();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.RpcClient.RpcClientService.BlockingInterface#getOptionRoots(com.google.protobuf.RpcController, org.marketcetera.client.RpcClient.OptionRootsRequest)
     */
    @Override
    public OptionRootsResponse getOptionRoots(RpcController inController,
                                              OptionRootsRequest inRequest)
            throws ServiceException
    {
        validateAndReturnSession(inRequest.getSessionId());
        return RpcClient.OptionRootsResponse.newBuilder().addAllSymbol(serverAdapter.getOptionRoots(inRequest.getSymbol())).build();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.RpcClient.RpcClientService.BlockingInterface#resolveSymbol(com.google.protobuf.RpcController, org.marketcetera.client.RpcClient.ResolveSymbolRequest)
     */
    @Override
    public ResolveSymbolResponse resolveSymbol(RpcController inController,
                                               ResolveSymbolRequest inRequest)
            throws ServiceException
    {
        validateAndReturnSession(inRequest.getSessionId());
        Instrument instrument = serverAdapter.resolveSymbol(inRequest.getSymbol());
        RpcClient.ResolveSymbolResponse.Builder responseBuilder = RpcClient.ResolveSymbolResponse.newBuilder();
        if(instrument != null) {
            StringWriter output = new StringWriter();
            try {
                synchronized(marshaller) {
                    marshaller.marshal(instrument,
                                             output);
                }
            } catch (JAXBException e) {
                throw new ServiceException(e);
            }
            responseBuilder.setInstrument(RpcClient.Instrument.newBuilder().setPayload(output.toString()));
        }
        return responseBuilder.build();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.RpcClient.RpcClientService.BlockingInterface#getRootOrderIdFor(com.google.protobuf.RpcController, org.marketcetera.client.RpcClient.RootOrderIdRequest)
     */
    @Override
    public RootOrderIdResponse getRootOrderIdFor(RpcController inController,
                                                 RootOrderIdRequest inRequest)
            throws ServiceException
    {
        validateAndReturnSession(inRequest.getSessionId());
        return RpcClient.RootOrderIdResponse.newBuilder().setOrderId(serverAdapter.getRootOrderIdFor(new OrderID(inRequest.getOrderId())).getValue()).build();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.RpcClient.RpcClientService.BlockingInterface#getUserData(com.google.protobuf.RpcController, org.marketcetera.client.RpcClient.GetUserDataRequest)
     */
    @Override
    public GetUserDataResponse getUserData(RpcController inController,
                                           GetUserDataRequest inRequest)
            throws ServiceException
    {
        SessionHolder<SessionClazz> sessionInfo = validateAndReturnSession(inRequest.getSessionId());
        String userData = serverAdapter.getUserData(sessionInfo.getUser());
        RpcClient.GetUserDataResponse.Builder responseBuilder = RpcClient.GetUserDataResponse.newBuilder();
        if(userData != null) {
            responseBuilder.setUserData(userData);
        }
        return responseBuilder.build();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.RpcClient.RpcClientService.BlockingInterface#setUserData(com.google.protobuf.RpcController, org.marketcetera.client.RpcClient.SetUserDataRequest)
     */
    @Override
    public SetUserDataResponse setUserData(RpcController inController,
                                           SetUserDataRequest inRequest)
            throws ServiceException
    {
        SessionHolder<SessionClazz> sessionInfo = validateAndReturnSession(inRequest.getSessionId());
        serverAdapter.setUserData(sessionInfo.getUser(),
                                  inRequest.getUserData());
        RpcClient.SetUserDataResponse response = RpcClient.SetUserDataResponse.newBuilder().build();
        return response;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.RpcClient.RpcClientService.BlockingInterface#addReport(com.google.protobuf.RpcController, org.marketcetera.client.RpcClient.AddReportRequest)
     */
    @Override
    public AddReportResponse addReport(RpcController inController,
                                       AddReportRequest inRequest)
            throws ServiceException
    {
        SessionHolder<SessionClazz> sessionInfo = validateAndReturnSession(inRequest.getSessionId());
        Message message;
        synchronized(unmarshaller) {
            try {
                message = ((FIXMessageWrapper)unmarshaller.unmarshal(new StringReader(inRequest.getMessage()))).getMessage();
            } catch (JAXBException e) {
                throw new ServiceException(e);
            }
        }
        serverAdapter.addReport(sessionInfo.getUser(),
                                message,
                                new BrokerID(inRequest.getBrokerId()));
        RpcClient.AddReportResponse response = RpcClient.AddReportResponse.newBuilder().build();
        return response;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.RpcClient.RpcClientService.BlockingInterface#deleteReport(com.google.protobuf.RpcController, org.marketcetera.client.RpcClient.DeleteReportRequest)
     */
    @Override
    public DeleteReportResponse deleteReport(RpcController inController,
                                             DeleteReportRequest inRequest)
            throws ServiceException
    {
        SessionHolder<SessionClazz> sessionInfo = validateAndReturnSession(inRequest.getSessionId());
        ExecutionReport message;
        synchronized(unmarshaller) {
            try {
                message = (ExecutionReport)unmarshaller.unmarshal(new StringReader(inRequest.getMessage()));
            } catch (JAXBException e) {
                throw new ServiceException(e);
            }
        }
        serverAdapter.deleteReport(sessionInfo.getUser(),
                                   message);
        RpcClient.DeleteReportResponse response = RpcClient.DeleteReportResponse.newBuilder().build();
        return response;
    }
    /**
     * 
     *
     *
     * @param inSessionIdValue
     */
    private void invalidateSession(String inSessionIdValue)
    {
        SessionId session = new SessionId(inSessionIdValue);
        rpcSessions.remove(session);
        sessionManager.remove(session);
    }
    /**
     * 
     *
     *
     * @param inSessionIdValue
     * @return
     * @throws ServiceException
     */
    private SessionHolder<SessionClazz> validateAndReturnSession(String inSessionIdValue)
            throws ServiceException
    {
        SessionId session = new SessionId(inSessionIdValue);
        SessionHolder<SessionClazz> sessionInfo = sessionManager.get(session);
        if(sessionInfo == null) {
            throw new ServiceException("Invalid session: " + inSessionIdValue);
        }
        return sessionInfo;
    }
    /**
     * 
     */
    private ContextClassProvider contextClassProvider;
    /**
     * 
     */
    private Authenticator authenticator;
    /**
     * 
     */
    private SessionManager<SessionClazz> sessionManager;
    /**
     * 
     */
    private RpcServerAdapter serverAdapter;
    /**
     * 
     */
    private final AtomicBoolean running = new AtomicBoolean(false);
    /**
     * 
     */
    private RpcServerCallExecutor executor;
    /**
     * 
     */
    private ChannelFuture channelToken;
    /**
     * 
     */
    private final Map<SessionId,String> rpcSessions = Maps.newConcurrentMap();
    /**
     * 
     */
    private String rpcHostname;
    /**
     * 
     */
    private int rpcPort;
    /**
     * 
     */
    private int sendBufferSize = 1048576;
    /**
     * 
     */
    private int receiveBufferSize = 1048576;
    /**
     * 
     */
    private boolean noDelay = true;
    /**
     * 
     */
    private int threadPoolCore = 10;
    /**
     * 
     */
    private int threadPoolMax = 200;
    // TODO add final lock for the jaxb stuff
    /**
     * 
     */
    private JAXBContext reportContext;
    /**
     * 
     */
    private Marshaller marshaller;
    /**
     * 
     */
    private Unmarshaller unmarshaller;
}
