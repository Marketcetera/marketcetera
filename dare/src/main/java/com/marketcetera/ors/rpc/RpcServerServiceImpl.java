package com.marketcetera.ors.rpc;

import java.math.BigDecimal;
import java.util.*;

import javax.xml.bind.JAXBException;

import org.apache.commons.lang.StringUtils;
import org.marketcetera.algo.BrokerAlgoSpec;
import org.marketcetera.algo.BrokerAlgoTagSpec;
import org.marketcetera.client.brokers.BrokerStatus;
import org.marketcetera.client.brokers.BrokersStatus;
import org.marketcetera.client.rpc.*;
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
import org.marketcetera.trade.*;
import org.marketcetera.trade.Currency;
import org.marketcetera.util.except.ExceptUtils;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.rpc.RpcCredentials;
import org.marketcetera.util.rpc.RpcServerServices;
import org.marketcetera.util.rpc.RpcServiceSpec;
import org.marketcetera.util.ws.stateful.SessionHolder;
import org.marketcetera.util.ws.tags.SessionId;
import org.marketcetera.util.ws.wrappers.MapWrapper;
import org.springframework.beans.factory.annotation.Autowired;

import quickfix.Message;

import com.google.protobuf.BlockingService;
import com.google.protobuf.RpcController;
import com.google.protobuf.ServiceException;
import com.marketcetera.admin.service.AuthorizationService;
import com.marketcetera.ors.TradingPermissions;

/* $License$ */

/**
 * Provides MATP server RPC services.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: RpcServerServiceImpl.java 17266 2017-04-28 14:58:00Z colin $
 * @since 2.4.2
 */
@ClassVersion("$Id: RpcServerServiceImpl.java 17266 2017-04-28 14:58:00Z colin $")
public class RpcServerServiceImpl<SessionClazz>
        implements RpcServiceSpec<SessionClazz>,RpcClientService.BlockingInterface
{
    /* (non-Javadoc)
     * @see org.marketcetera.client.rpc.RpcServiceSpec#getDescription()
     */
    @Override
    public String getDescription()
    {
        return description;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.rpc.RpcServiceSpec#generateService()
     */
    @Override
    public BlockingService generateService()
    {
        return RpcClientService.newReflectiveBlockingService(this);
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
        try {
            SessionId sessionId = serverServices.login(new RpcCredentials(inRequest.getUsername(),
                                                                       inRequest.getPassword(),
                                                                       inRequest.getAppId(),
                                                                       inRequest.getClientId(),
                                                                       inRequest.getVersionId(),
                                                                       new Locale(inRequest.getLocale().getLanguage(),
                                                                                  inRequest.getLocale().getCountry(),
                                                                                  inRequest.getLocale().getVariant())));
            return LoginResponse.newBuilder().setSessionId(sessionId.getValue()).build();
        } catch (Exception e) {
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
        serverServices.logout(inRequest.getSessionId());
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
        serverServices.validateAndReturnSession(inRequest.getSessionId());
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
        SessionHolder<SessionClazz> sessionInfo = serverServices.validateAndReturnSession(inRequest.getSessionId());
        authzService.authorize(sessionInfo.getUser(),
                               TradingPermissions.ViewBrokerStatusAction.name());
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
                            .setReadOnly(algoTagSpec.isReadOnly())
                            .setDefaultValue(algoTagSpec.getDefaultValue()==null?"":algoTagSpec.getDefaultValue())
                            .setLabel(algoTagSpec.getLabel()==null?"":algoTagSpec.getLabel())
                            .setPattern(algoTagSpec.getPattern()==null?"":algoTagSpec.getPattern())
                            .setAdvice(algoTagSpec.getAdvice()==null?"":algoTagSpec.getAdvice())
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
            Map<String,String> settings = broker.getSettings();
            if(settings != null) {
                RpcClient.SessionSetting.Builder settingsBuilder = RpcClient.SessionSetting.newBuilder();
                for(Map.Entry<String,String> setting : settings.entrySet()) {
                    rpcBrokerBuilder.addSettings(settingsBuilder.setKey(setting.getKey()).setValue(setting.getValue()).build());
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
        SessionHolder<SessionClazz> sessionInfo = serverServices.validateAndReturnSession(inRequest.getSessionId());
        authzService.authorize(sessionInfo.getUser(),
                               TradingPermissions.ViewOpenOrdersAction.name());
        List<ReportBaseImpl> reports = serverAdapter.getOpenOrders(sessionInfo.getUser());
        RpcClient.ReportList.Builder rpcReportListBuilder = RpcClient.ReportList.newBuilder();
        if(reports != null) {
            for(ReportBaseImpl report : reports) {
                try {
                    rpcReportListBuilder.addReports(serverServices.marshal(report));
                } catch (JAXBException e) {
                    throw new ServiceException(e);
                }
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
        SessionHolder<SessionClazz> sessionInfo = serverServices.validateAndReturnSession(inRequest.getSessionId());
        authzService.authorize(sessionInfo.getUser(),
                               TradingPermissions.ViewReportAction.name());
        ReportBaseImpl[] reports = serverAdapter.getReportsSince(sessionInfo.getUser(),
                                                                 new Date(inRequest.getOrigin()));
        RpcClient.ReportList.Builder rpcReportListBuilder = RpcClient.ReportList.newBuilder();
        if(reports != null) {
            for(ReportBaseImpl report : reports) {
                try {
                    rpcReportListBuilder.addReports(serverServices.marshal(report));
                } catch (JAXBException e) {
                    throw new ServiceException(e);
                }
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
        SessionHolder<SessionClazz> sessionInfo = serverServices.validateAndReturnSession(inRequest.getSessionId());
        authzService.authorize(sessionInfo.getUser(),
                               TradingPermissions.ViewPositionAction.name());
        RpcClient.PositionResponse.Builder rpcPositionResponseBuilder = RpcClient.PositionResponse.newBuilder();
        try {
            if(inRequest.hasInstrument()) {
                Instrument instrument;
                Date origin = new Date(inRequest.getOrigin());
                BigDecimal position;
                try {
                    instrument = serverServices.unmarshall(inRequest.getInstrument().getPayload());
                } catch (JAXBException e) {
                    throw new ServiceException(e);
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
                                RpcClient.PositionKey rpcPositionKey = RpcClient.PositionKey.newBuilder()
                                        .setAccount(positionKey.getAccount()==null?"":positionKey.getAccount())
                                        .setInstrument(RpcClient.Instrument.newBuilder().setPayload(serverServices.marshal(positionKey.getInstrument())))
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
                                RpcClient.PositionKey rpcPositionKey = RpcClient.PositionKey.newBuilder()
                                        .setAccount(positionKey.getAccount()==null?"":positionKey.getAccount())
                                        .setInstrument(RpcClient.Instrument.newBuilder().setPayload(serverServices.marshal(positionKey.getInstrument())))
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
                                RpcClient.PositionKey rpcPositionKey = RpcClient.PositionKey.newBuilder()
                                        .setAccount(positionKey.getAccount()==null?"":positionKey.getAccount())
                                        .setInstrument(RpcClient.Instrument.newBuilder().setPayload(serverServices.marshal(positionKey.getInstrument())))
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
                                RpcClient.PositionKey rpcPositionKey = RpcClient.PositionKey.newBuilder()
                                        .setAccount(positionKey.getAccount()==null?"":positionKey.getAccount())
                                        .setInstrument(RpcClient.Instrument.newBuilder().setPayload(serverServices.marshal(positionKey.getInstrument())))
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
                        RpcClient.PositionKey rpcPositionKey = RpcClient.PositionKey.newBuilder()
                                .setAccount(positionKey.getAccount()==null?"":positionKey.getAccount())
                                .setInstrument(RpcClient.Instrument.newBuilder().setPayload(serverServices.marshal(positionKey.getInstrument())))
                                .setTraderId(positionKey.getTraderId()==null?"":positionKey.getTraderId()).build();
                        rpcPositionResponseBuilder.addKeys(rpcPositionKey);
                        rpcPositionResponseBuilder.addValues(entry.getValue().toPlainString());
                    }
                }
            }
        } catch (JAXBException e) {
            throw new ServiceException(e);
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
        SessionHolder<SessionClazz> sessionInfo = serverServices.validateAndReturnSession(inRequest.getSessionId());
        authzService.authorize(sessionInfo.getUser(),
                               TradingPermissions.ViewUserDataAction.name());
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
        serverServices.validateAndReturnSession(inRequest.getSessionId());
        RpcClient.UnderlyingResponse.Builder responseBuilder = RpcClient.UnderlyingResponse.newBuilder();
        String underlying = StringUtils.trimToNull(serverAdapter.getUnderlying(inRequest.getSymbol()));
        if(underlying != null) {
            responseBuilder.setSymbol(underlying);
        }
        return responseBuilder.build();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.RpcClient.RpcClientService.BlockingInterface#getOptionRoots(com.google.protobuf.RpcController, org.marketcetera.client.RpcClient.OptionRootsRequest)
     */
    @Override
    public OptionRootsResponse getOptionRoots(RpcController inController,
                                              OptionRootsRequest inRequest)
            throws ServiceException
    {
        serverServices.validateAndReturnSession(inRequest.getSessionId());
        Collection<String> roots = serverAdapter.getOptionRoots(inRequest.getSymbol());
        RpcClient.OptionRootsResponse.Builder responseBuilder = RpcClient.OptionRootsResponse.newBuilder();
        if(roots != null) {
            responseBuilder.addAllSymbol(roots);
        }
        return responseBuilder.build();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.RpcClient.RpcClientService.BlockingInterface#resolveSymbol(com.google.protobuf.RpcController, org.marketcetera.client.RpcClient.ResolveSymbolRequest)
     */
    @Override
    public ResolveSymbolResponse resolveSymbol(RpcController inController,
                                               ResolveSymbolRequest inRequest)
            throws ServiceException
    {
        try {
            serverServices.validateAndReturnSession(inRequest.getSessionId());
            Instrument instrument = serverAdapter.resolveSymbol(inRequest.getSymbol());
            RpcClient.ResolveSymbolResponse.Builder responseBuilder = RpcClient.ResolveSymbolResponse.newBuilder();
            if(instrument != null) {
                responseBuilder.setInstrument(RpcClient.Instrument.newBuilder().setPayload(serverServices.marshal(instrument)));
            }
            return responseBuilder.build();
        } catch (JAXBException e) {
            throw new ServiceException(e);
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.RpcClient.RpcClientService.BlockingInterface#getRootOrderIdFor(com.google.protobuf.RpcController, org.marketcetera.client.RpcClient.RootOrderIdRequest)
     */
    @Override
    public RootOrderIdResponse getRootOrderIdFor(RpcController inController,
                                                 RootOrderIdRequest inRequest)
            throws ServiceException
    {
        SessionHolder<SessionClazz> sessionInfo = serverServices.validateAndReturnSession(inRequest.getSessionId());
        authzService.authorize(sessionInfo.getUser(),
                               TradingPermissions.ViewOpenOrdersAction.name());
        RpcClient.RootOrderIdResponse.Builder responseBuilder = RpcClient.RootOrderIdResponse.newBuilder();
        OrderID rootOrderID = serverAdapter.getRootOrderIdFor(new OrderID(inRequest.getOrderId()));
        if(rootOrderID != null) {
            responseBuilder.setOrderId(rootOrderID.getValue());
        }
        return responseBuilder.build();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.RpcClient.RpcClientService.BlockingInterface#getUserData(com.google.protobuf.RpcController, org.marketcetera.client.RpcClient.GetUserDataRequest)
     */
    @Override
    public GetUserDataResponse getUserData(RpcController inController,
                                           GetUserDataRequest inRequest)
            throws ServiceException
    {
        SessionHolder<SessionClazz> sessionInfo = serverServices.validateAndReturnSession(inRequest.getSessionId());
        authzService.authorize(sessionInfo.getUser(),
                               TradingPermissions.ViewUserDataAction.name());
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
        SessionHolder<SessionClazz> sessionInfo = serverServices.validateAndReturnSession(inRequest.getSessionId());
        authzService.authorize(sessionInfo.getUser(),
                               TradingPermissions.WriteUserDataAction.name());
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
        SessionHolder<SessionClazz> sessionInfo = serverServices.validateAndReturnSession(inRequest.getSessionId());
        authzService.authorize(sessionInfo.getUser(),
                               TradingPermissions.AddReportAction.name());
        Message message;
        RpcClient.AddReportResponse.Builder response = RpcClient.AddReportResponse.newBuilder();
        try {
            message = ((FIXMessageWrapper)serverServices.unmarshall(inRequest.getMessage())).getMessage();
            serverAdapter.addReport(sessionInfo.getUser(),
                                    message,
                                    new BrokerID(inRequest.getBrokerId()),
                                    Hierarchy.valueOf(inRequest.getHierarchy().name()));
            response.setStatus(true);
        } catch (Exception e) {
            response.setStatus(false);
            response.setMessage(ExceptUtils.getRootCauseMessage(e));
        }
        return response.build();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.RpcClient.RpcClientService.BlockingInterface#deleteReport(com.google.protobuf.RpcController, org.marketcetera.client.RpcClient.DeleteReportRequest)
     */
    @Override
    public DeleteReportResponse deleteReport(RpcController inController,
                                             DeleteReportRequest inRequest)
            throws ServiceException
    {
        SessionHolder<SessionClazz> sessionInfo = serverServices.validateAndReturnSession(inRequest.getSessionId());
        authzService.authorize(sessionInfo.getUser(),
                               TradingPermissions.DeleteReportAction.name());
        ExecutionReport message;
        try {
            message = serverServices.unmarshall(inRequest.getMessage());
        } catch (JAXBException e) {
            throw new ServiceException(e);
        }
        serverAdapter.deleteReport(sessionInfo.getUser(),
                                   message);
        RpcClient.DeleteReportResponse response = RpcClient.DeleteReportResponse.newBuilder().build();
        return response;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.rpc.RpcServiceSpec#setRpcServerServices(org.marketcetera.client.rpc.RpcServerServices)
     */
    @Override
    public void setRpcServerServices(RpcServerServices<SessionClazz> inServerServices)
    {
        serverServices = inServerServices;
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
     * RPC server to which to bind
     */
    private RpcServerServices<SessionClazz> serverServices;
    /**
     * provides access to protocol-neutral services
     */
    private RpcServerAdapter serverAdapter;
    /**
     * human-readable description of the service
     */
    private static final String description = "MATP Trading RPC Service";
    /**
     * provides access to authorization services
     */
    @Autowired
    private AuthorizationService authzService;
}
