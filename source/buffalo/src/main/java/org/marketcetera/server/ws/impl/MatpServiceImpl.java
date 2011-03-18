package org.marketcetera.server.ws.impl;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

import javax.annotation.concurrent.ThreadSafe;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.marketcetera.client.brokers.BrokersStatus;
import org.marketcetera.client.users.UserInfo;
import org.marketcetera.core.position.PositionKey;
import org.marketcetera.module.ModuleInfo;
import org.marketcetera.module.ModuleURN;
import org.marketcetera.saclient.CreateStrategyParameters;
import org.marketcetera.server.security.MetcAuthentication;
import org.marketcetera.server.service.OrderDestinationManager;
import org.marketcetera.server.service.OrderManagerSelector;
import org.marketcetera.server.service.PositionManager;
import org.marketcetera.server.service.StrategyAgentManager;
import org.marketcetera.server.ws.MatpService;
import org.marketcetera.trade.*;
import org.marketcetera.util.except.I18NException;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.ws.stateful.*;
import org.marketcetera.util.ws.stateless.ServiceInterface;
import org.marketcetera.util.ws.stateless.StatelessClientContext;
import org.marketcetera.util.ws.wrappers.DateWrapper;
import org.marketcetera.util.ws.wrappers.MapWrapper;
import org.marketcetera.util.ws.wrappers.RemoteException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.Lifecycle;
import org.springframework.security.authentication.AuthenticationProvider;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ThreadSafe
@ClassVersion("$Id$")
public class MatpServiceImpl
        implements MatpService, Lifecycle
{
    /**
     * Create a new MatpServiceImpl instance.
     *
     * @param inHost
     * @param inPort
     */
    public MatpServiceImpl(String inHost,
                           int inPort)
    {
        host = StringUtils.trimToNull(inHost);
        Validate.notNull(host,
                         "MATP WebServices host property missing");
        port = inPort;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.saclient.SAService#createStrategy(org.marketcetera.util.ws.stateful.ClientContext, org.marketcetera.saclient.CreateStrategyParameters)
     */
    @Override
    public ModuleURN createStrategy(final ClientContext inContext,
                                    CreateStrategyParameters inParameters)
            throws RemoteException
    {
        return wsImpl.createStrategy(inContext,
                                     inParameters);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.saclient.SAService#delete(org.marketcetera.util.ws.stateful.ClientContext, org.marketcetera.module.ModuleURN)
     */
    @Override
    public void delete(final ClientContext inContext,
                       ModuleURN inURN)
            throws RemoteException
    {
        wsImpl.delete(inContext,
                      inURN);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.saclient.SAService#getInstances(org.marketcetera.util.ws.stateful.ClientContext, org.marketcetera.module.ModuleURN)
     */
    @Override
    public List<ModuleURN> getInstances(final ClientContext inContext,
                                        ModuleURN inURN)
            throws RemoteException
    {
        return wsImpl.getInstances(inContext,
                                   inURN);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.saclient.SAService#getModuleInfo(org.marketcetera.util.ws.stateful.ClientContext, org.marketcetera.module.ModuleURN)
     */
    @Override
    public ModuleInfo getModuleInfo(final ClientContext inContext,
                                    ModuleURN inURN)
            throws RemoteException
    {
        return wsImpl.getModuleInfo(inContext,
                                    inURN);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.saclient.SAService#getProperties(org.marketcetera.util.ws.stateful.ClientContext, org.marketcetera.module.ModuleURN)
     */
    @Override
    public MapWrapper<String,Object> getProperties(final ClientContext inContext,
                                                   ModuleURN inURN)
            throws RemoteException
    {
        return wsImpl.getProperties(inContext,
                                    inURN);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.saclient.SAService#getProviders(org.marketcetera.util.ws.stateful.ClientContext)
     */
    @Override
    public List<ModuleURN> getProviders(final ClientContext inContext)
            throws RemoteException
    {
        return wsImpl.getProviders(inContext);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.saclient.SAService#getStrategyCreateParms(org.marketcetera.util.ws.stateful.ClientContext, org.marketcetera.module.ModuleURN)
     */
    @Override
    public CreateStrategyParameters getStrategyCreateParms(final ClientContext inContext,
                                                           ModuleURN inURN)
            throws RemoteException
    {
        return wsImpl.getStrategyCreateParms(inContext,
                                             inURN);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.saclient.SAService#setProperties(org.marketcetera.util.ws.stateful.ClientContext, org.marketcetera.module.ModuleURN, org.marketcetera.util.ws.wrappers.MapWrapper)
     */
    @Override
    public MapWrapper<String,Object> setProperties(final ClientContext inContext,
                                                    ModuleURN inURN,
                                                    MapWrapper<String,Object> inProperties)
            throws RemoteException
    {
        return wsImpl.setProperties(inContext,
                                    inURN,
                                    inProperties);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.saclient.SAService#start(org.marketcetera.util.ws.stateful.ClientContext, org.marketcetera.module.ModuleURN)
     */
    @Override
    public void start(final ClientContext inContext,
                      ModuleURN inURN)
            throws RemoteException
    {
        wsImpl.start(inContext,
                     inURN);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.saclient.SAService#stop(org.marketcetera.util.ws.stateful.ClientContext, org.marketcetera.module.ModuleURN)
     */
    @Override
    public void stop(final ClientContext inContext,
                     ModuleURN inURN)
            throws RemoteException
    {
        wsImpl.start(inContext,
                     inURN);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.Service#getBrokersStatus(org.marketcetera.util.ws.stateful.ClientContext)
     */
    @Override
    public BrokersStatus getBrokersStatus(final ClientContext inContext)
            throws RemoteException
    {
        return wsImpl.getBrokersStatus(inContext);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.Service#getUserInfo(org.marketcetera.util.ws.stateful.ClientContext, org.marketcetera.trade.UserID)
     */
    @Override
    public UserInfo getUserInfo(final ClientContext inContext,
                                UserID inId)
            throws RemoteException
    {
        return wsImpl.getUserInfo(inContext,
                                  inId);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.Service#getReportsSince(org.marketcetera.util.ws.stateful.ClientContext, org.marketcetera.util.ws.wrappers.DateWrapper)
     */
    @Override
    public ReportBaseImpl[] getReportsSince(final ClientContext inContext,
                                            DateWrapper inDate)
            throws RemoteException
    {
        return wsImpl.getReportsSince(inContext,
                                      inDate);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.Service#getEquityPositionAsOf(org.marketcetera.util.ws.stateful.ClientContext, org.marketcetera.util.ws.wrappers.DateWrapper, org.marketcetera.trade.Equity)
     */
    @Override
    public BigDecimal getEquityPositionAsOf(final ClientContext inContext,
                                            DateWrapper inDate,
                                            Equity inEquity)
            throws RemoteException
    {
        return wsImpl.getEquityPositionAsOf(inContext,
                                            inDate,
                                            inEquity);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.Service#getAllEquityPositionsAsOf(org.marketcetera.util.ws.stateful.ClientContext, org.marketcetera.util.ws.wrappers.DateWrapper)
     */
    @Override
    public MapWrapper<PositionKey<Equity>,BigDecimal> getAllEquityPositionsAsOf(final ClientContext inContext,
                                                                                DateWrapper inDate)
            throws RemoteException
    {
        return wsImpl.getAllEquityPositionsAsOf(inContext,
                                                inDate);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.Service#getFuturePositionAsOf(org.marketcetera.util.ws.stateful.ClientContext, org.marketcetera.util.ws.wrappers.DateWrapper, org.marketcetera.trade.Future)
     */
    @Override
    public BigDecimal getFuturePositionAsOf(final ClientContext inContext,
                                            DateWrapper inDate,
                                            Future inFuture)
            throws RemoteException
    {
        return wsImpl.getFuturePositionAsOf(inContext,
                                            inDate,
                                            inFuture);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.Service#getAllFuturePositionsAsOf(org.marketcetera.util.ws.stateful.ClientContext, org.marketcetera.util.ws.wrappers.DateWrapper)
     */
    @Override
    public MapWrapper<PositionKey<Future>,BigDecimal> getAllFuturePositionsAsOf(final ClientContext inContext,
                                                                                DateWrapper inDate)
            throws RemoteException
    {
        return wsImpl.getAllFuturePositionsAsOf(inContext,
                                                inDate);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.Service#getOptionPositionAsOf(org.marketcetera.util.ws.stateful.ClientContext, org.marketcetera.util.ws.wrappers.DateWrapper, org.marketcetera.trade.Option)
     */
    @Override
    public BigDecimal getOptionPositionAsOf(final ClientContext inContext,
                                            DateWrapper inDate,
                                            Option inOption)
            throws RemoteException
    {
        return wsImpl.getOptionPositionAsOf(inContext,
                                            inDate,
                                            inOption);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.Service#getAllOptionPositionsAsOf(org.marketcetera.util.ws.stateful.ClientContext, org.marketcetera.util.ws.wrappers.DateWrapper)
     */
    @Override
    public MapWrapper<PositionKey<Option>,BigDecimal> getAllOptionPositionsAsOf(final ClientContext inContext,
                                                                                DateWrapper inDate)
            throws RemoteException
    {
        return wsImpl.getAllOptionPositionsAsOf(inContext,
                                                inDate);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.Service#getOptionPositionsAsOf(org.marketcetera.util.ws.stateful.ClientContext, org.marketcetera.util.ws.wrappers.DateWrapper, java.lang.String[])
     */
    @Override
    public MapWrapper<PositionKey<Option>,BigDecimal> getOptionPositionsAsOf(final ClientContext inContext,
                                                                             DateWrapper inDate,
                                                                             String... inRootSymbols)
            throws RemoteException
    {
        return wsImpl.getOptionPositionsAsOf(inContext,
                                             inDate,
                                             inRootSymbols);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.Service#getNextOrderID(org.marketcetera.util.ws.stateful.ClientContext)
     */
    @Override
    public String getNextOrderID(final ClientContext inContext)
            throws RemoteException
    {
        return wsImpl.getNextOrderID(inContext);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.Service#getUnderlying(org.marketcetera.util.ws.stateful.ClientContext, java.lang.String)
     */
    @Override
    public String getUnderlying(final ClientContext inContext,
                                String inOptionRoot)
            throws RemoteException
    {
        return wsImpl.getUnderlying(inContext,
                                    inOptionRoot);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.Service#getOptionRoots(org.marketcetera.util.ws.stateful.ClientContext, java.lang.String)
     */
    @Override
    public Collection<String> getOptionRoots(final ClientContext inContext,
                                             String inUnderlying)
            throws RemoteException
    {
        return wsImpl.getOptionRoots(inContext,
                                     inUnderlying);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.Service#heartbeat(org.marketcetera.util.ws.stateful.ClientContext)
     */
    @Override
    public void heartbeat(final ClientContext inContext)
            throws RemoteException
    {
        wsImpl.heartbeat(inContext);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.Service#getUserData(org.marketcetera.util.ws.stateful.ClientContext)
     */
    @Override
    public String getUserData(final ClientContext inContext)
            throws RemoteException
    {
        return wsImpl.getUserData(inContext);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.Service#setUserData(org.marketcetera.util.ws.stateful.ClientContext, java.lang.String)
     */
    @Override
    public void setUserData(final ClientContext inContext,
                            String inData)
            throws RemoteException
    {
        wsImpl.setUserData(inContext,
                           inData);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.server.ws.MatpService#sendOrder(org.marketcetera.trade.OrderSingle)
     */
    @Override
    public void sendOrderSingle(ClientContext inContext,
                                OrderSingleImpl inOrderSingle)
    {
        wsImpl.sendOrderSingle(inContext,
                               inOrderSingle);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.server.ws.MatpService#sendOrder(org.marketcetera.trade.OrderReplace)
     */
    @Override
    public void sendOrderReplace(ClientContext inContext,
                                 OrderReplaceImpl inOrderReplace)
    {
        wsImpl.sendOrderReplace(inContext,
                                inOrderReplace);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.server.ws.MatpService#sendOrder(org.marketcetera.trade.OrderCancel)
     */
    @Override
    public void sendOrderCancel(ClientContext inContext,
                                OrderCancelImpl inOrderCancel)
    {
        wsImpl.sendOrderCancel(inContext,
                               inOrderCancel);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.server.ws.MatpService#sendOrderRaw(org.marketcetera.trade.FIXOrder)
     */
    @Override
    public void sendOrderRaw(ClientContext inContext,
                             FIXOrderImpl inFIXOrder)
    {
        wsImpl.sendOrderRaw(inContext,
                            inFIXOrder);
    }
    /* (non-Javadoc)
     * @see org.springframework.context.Lifecycle#start()
     */
    @Override
    public void start()
    {
        SessionManager<ClientSession> sessionManager = new SessionManager<ClientSession>(new ClientSessionFactory(),
                                                                                         SessionManager.INFINITE_SESSION_LIFESPAN);
        wsImpl = new WSImpl(sessionManager);
        server = new Server<ClientSession>(getHost(),
                                           getPort(),
                                           new Authenticator() {
            @Override
            public boolean shouldAllow(StatelessClientContext context,
                                       final String inUsername,
                                       char[] password)
                    throws I18NException
            {
                return authenticationProvider.authenticate(new MetcAuthentication(inUsername,
                                                                                  password)).isAuthenticated();
//                return authenticate(context,
//                                    user,
//                                    password);
            }
        },
                                           sessionManager);
        SLF4JLoggerProxy.info(MatpServiceImpl.class,
                              "Starting MATP Service on {}:{}",
                              getHost(),
                              getPort());
        try {
            service = server.publish(this,
                                     MatpService.class);
        } catch (Exception e) {
            SLF4JLoggerProxy.error(MatpServiceImpl.class,
                                   e,
                                   "Error starting WS");
            throw new RuntimeException(e);
        }
        // set up security/signing for in-bound SOAP messages and signing for outbound SOAP messages  
//        Map<String,Object> inProps = new HashMap<String,Object>();
////        inProps.put(WSHandlerConstants.ACTION,
////                    WSHandlerConstants.USERNAME_TOKEN);
//        // Password type : plain text
////        inProps.put(WSHandlerConstants.PASSWORD_TYPE,
////                    WSConstants.PW_TEXT);
//        // for hashed password use:
//        //properties.put(WSHandlerConstants.PASSWORD_TYPE, WSConstants.PW_DIGEST);
//        // Callback used to retrieve password for given user.
////        inProps.put(WSHandlerConstants.PW_CALLBACK_CLASS,
////                    ServerPasswordHandler.class.getName());
//        Map<String,Object> outProps = new HashMap<String,Object>();
//        Endpoint cxfEndpoint = service.getServer().getEndpoint();
//        WSS4JInInterceptor wssIn = new WSS4JInInterceptor(inProps);
//        cxfEndpoint.getInInterceptors().add(wssIn);
//        WSS4JOutInterceptor wssOut = new WSS4JOutInterceptor();
//        outProps.put(WSHandlerConstants.ACTION,
//                     WSHandlerConstants.NO_SECURITY);
//        inProps.put(WSHandlerConstants.ACTION,
//                    WSHandlerConstants.NO_SECURITY);
//        wssOut.setProperties(outProps);
//        cxfEndpoint.getOutInterceptors().add(wssOut);
    }
    /* (non-Javadoc)
     * @see org.springframework.context.Lifecycle#stop()
     */
    @Override
    public void stop()
    {
        SLF4JLoggerProxy.info(MatpServiceImpl.class,
                              "Stopping MATP Service");
        if(service != null) {
            service.stop();
        }
    }
    /* (non-Javadoc)
     * @see org.springframework.context.Lifecycle#isRunning()
     */
    @Override
    public boolean isRunning()
    {
        // TODO
        return true;
    }
    /**
     * Get the host value.
     *
     * @return a <code>String</code> value
     */
    public String getHost()
    {
        return host;
    }
    /**
     * Get the port value.
     *
     * @return a <code>int</code> value
     */
    public int getPort()
    {
        return port;
    }
    /**
     *
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    private class WSImpl
            extends ServiceBaseImpl<ClientSession>
            implements MatpService
    {
        /**
         * Create a new WSImpl instance.
         *
         * @param inSessionManager
         */
        public WSImpl(SessionManager<ClientSession> inSessionManager)
        {
            super(inSessionManager);
        }
        /* (non-Javadoc)
         * @see org.marketcetera.saclient.SAService#getProviders(org.marketcetera.util.ws.stateful.ClientContext)
         */
        @Override
        public List<ModuleURN> getProviders(final ClientContext inContext)
                throws RemoteException
        {
            return new RemoteCaller<ClientSession,
                                    List<ModuleURN>>(getSessionManager()) {
                @Override
                protected List<ModuleURN> call(ClientContext context,
                                               SessionHolder<ClientSession> sessionHolder)
                        throws Exception
                {
                    return strategyAgentManager.getStrategyAgent().getProviders(inContext);
                }
            }.execute(inContext);
        }
        /* (non-Javadoc)
         * @see org.marketcetera.saclient.SAService#getInstances(org.marketcetera.util.ws.stateful.ClientContext, org.marketcetera.module.ModuleURN)
         */
        @Override
        public List<ModuleURN> getInstances(final ClientContext inContext,
                                            final ModuleURN inProviderURN)
                throws RemoteException
        {
            return new RemoteCaller<ClientSession,List<ModuleURN>>(getSessionManager()) {
                @Override
                protected List<ModuleURN> call(ClientContext context,
                                               SessionHolder<ClientSession> sessionHolder)
                        throws Exception
                {
                    return strategyAgentManager.getStrategyAgent().getInstances(inContext,
                                                                                inProviderURN);
                }
            }.execute(inContext);
        }
        /* (non-Javadoc)
         * @see org.marketcetera.saclient.SAService#getModuleInfo(org.marketcetera.util.ws.stateful.ClientContext, org.marketcetera.module.ModuleURN)
         */
        @Override
        public ModuleInfo getModuleInfo(final ClientContext inContext,
                                        final ModuleURN inURN)
                throws RemoteException
        {
            return new RemoteCaller<ClientSession,ModuleInfo>(getSessionManager()) {
                @Override
                protected ModuleInfo call(ClientContext context,
                                          SessionHolder<ClientSession> sessionHolder)
                        throws Exception
                {
                    return strategyAgentManager.getStrategyAgent().getModuleInfo(inContext,
                                                                                 inURN);
                }
            }.execute(inContext);
        }
        /* (non-Javadoc)
         * @see org.marketcetera.saclient.SAService#start(org.marketcetera.util.ws.stateful.ClientContext, org.marketcetera.module.ModuleURN)
         */
        @Override
        public void start(final ClientContext inContext,
                          final ModuleURN inURN)
                throws RemoteException
        {
            new RemoteCaller<ClientSession,Object>(getSessionManager()) {
                @Override
                protected Object call(ClientContext context,
                                      SessionHolder<ClientSession> sessionHolder)
                        throws Exception
                {
                    strategyAgentManager.getStrategyAgent().start(inContext,
                                                                  inURN);
                    return null;
                }
            }.execute(inContext);
        }
        /* (non-Javadoc)
         * @see org.marketcetera.saclient.SAService#stop(org.marketcetera.util.ws.stateful.ClientContext, org.marketcetera.module.ModuleURN)
         */
        @Override
        public void stop(final ClientContext inContext,
                         final ModuleURN inURN)
                throws RemoteException
        {
            new RemoteCaller<ClientSession,Object>(getSessionManager()) {
                @Override
                protected Object call(ClientContext context,
                                      SessionHolder<ClientSession> sessionHolder)
                        throws Exception
                {
                    strategyAgentManager.getStrategyAgent().stop(inContext,
                                                                 inURN);
                    return null;
                }
            }.execute(inContext);
        }
        /* (non-Javadoc)
         * @see org.marketcetera.saclient.SAService#delete(org.marketcetera.util.ws.stateful.ClientContext, org.marketcetera.module.ModuleURN)
         */
        @Override
        public void delete(final ClientContext inContext,
                           final ModuleURN inURN)
                throws RemoteException
        {
            new RemoteCaller<ClientSession,Object>(getSessionManager()) {
                @Override
                protected Object call(ClientContext context,
                                      SessionHolder<ClientSession> sessionHolder)
                        throws Exception
                {
                    strategyAgentManager.getStrategyAgent().delete(inContext,
                                                                   inURN);
                    return null;
                }
            }.execute(inContext);
        }
        /* (non-Javadoc)
         * @see org.marketcetera.saclient.SAService#getProperties(org.marketcetera.util.ws.stateful.ClientContext, org.marketcetera.module.ModuleURN)
         */
        @Override
        public MapWrapper<String,Object> getProperties(final ClientContext inContext,
                                                       final ModuleURN inURN)
                throws RemoteException
        {
            return new RemoteCaller<ClientSession,MapWrapper<String,Object>>(getSessionManager()) {
                @Override
                protected MapWrapper<String,Object> call(ClientContext context,
                                                         SessionHolder<ClientSession> sessionHolder)
                        throws Exception
                {
                    return strategyAgentManager.getStrategyAgent().getProperties(inContext,
                                                                                 inURN);
                }
            }.execute(inContext);
        }
        /* (non-Javadoc)
         * @see org.marketcetera.saclient.SAService#setProperties(org.marketcetera.util.ws.stateful.ClientContext, org.marketcetera.module.ModuleURN, org.marketcetera.util.ws.wrappers.MapWrapper)
         */
        @Override
        public MapWrapper<String,Object> setProperties(final ClientContext inContext,
                                                       final ModuleURN inURN,
                                                       final MapWrapper<String,Object> inProperties)
                throws RemoteException
        {
            return new RemoteCaller<ClientSession,MapWrapper<String,Object>>(getSessionManager()) {
                @Override
                protected MapWrapper<String,Object> call(ClientContext context,
                                                         SessionHolder<ClientSession> sessionHolder)
                        throws Exception
                {
                    return strategyAgentManager.getStrategyAgent().setProperties(inContext,
                                                                                 inURN,
                                                                                 inProperties);
                }
            }.execute(inContext);
        }
        /* (non-Javadoc)
         * @see org.marketcetera.saclient.SAService#createStrategy(org.marketcetera.util.ws.stateful.ClientContext, org.marketcetera.saclient.CreateStrategyParameters)
         */
        @Override
        public ModuleURN createStrategy(final ClientContext inContext,
                                        final CreateStrategyParameters inParameters)
                throws RemoteException
        {
            return new RemoteCaller<ClientSession,ModuleURN>(getSessionManager()) {
                @Override
                protected ModuleURN call(ClientContext context,
                                         SessionHolder<ClientSession> sessionHolder)
                        throws Exception
                {
                    return strategyAgentManager.getStrategyAgent().createStrategy(inContext,
                                                                                  inParameters);
                }
            }.execute(inContext);
        }
        /* (non-Javadoc)
         * @see org.marketcetera.saclient.SAService#getStrategyCreateParms(org.marketcetera.util.ws.stateful.ClientContext, org.marketcetera.module.ModuleURN)
         */
        @Override
        public CreateStrategyParameters getStrategyCreateParms(final ClientContext inServiceContext,
                                                               final ModuleURN inURN)
                throws RemoteException
        {
            return new RemoteCaller<ClientSession,CreateStrategyParameters>(getSessionManager()) {
                @Override
                protected CreateStrategyParameters call(ClientContext context,
                                                        SessionHolder<ClientSession> sessionHolder)
                        throws Exception
                {
                    return strategyAgentManager.getStrategyAgent().getStrategyCreateParms(inServiceContext,
                                                                                          inURN);
                }
            }.execute(inServiceContext);
        }
        /* (non-Javadoc)
         * @see org.marketcetera.client.Service#getBrokersStatus(org.marketcetera.util.ws.stateful.ClientContext)
         */
        @Override
        public BrokersStatus getBrokersStatus(final ClientContext inContext)
                throws RemoteException
        {
            return null; // TODO
        }
        /* (non-Javadoc)
         * @see org.marketcetera.client.Service#getUserInfo(org.marketcetera.util.ws.stateful.ClientContext, org.marketcetera.trade.UserID)
         */
        @Override
        public UserInfo getUserInfo(final ClientContext inContext,
                                    final UserID inId)
                throws RemoteException
        {
            // TODO Auto-generated method stub
            return null;
        }
        /* (non-Javadoc)
         * @see org.marketcetera.client.Service#getReportsSince(org.marketcetera.util.ws.stateful.ClientContext, org.marketcetera.util.ws.wrappers.DateWrapper)
         */
        @Override
        public ReportBaseImpl[] getReportsSince(final ClientContext inContext,
                                                DateWrapper inDate)
                throws RemoteException
        {
            // TODO Auto-generated method stub
            return null;
        }
        /* (non-Javadoc)
         * @see org.marketcetera.client.Service#getEquityPositionAsOf(org.marketcetera.util.ws.stateful.ClientContext, org.marketcetera.util.ws.wrappers.DateWrapper, org.marketcetera.trade.Equity)
         */
        @Override
        public BigDecimal getEquityPositionAsOf(final ClientContext inContext,
                                                DateWrapper inDate,
                                                Equity inEquity)
                throws RemoteException
        {
            return positionManager.getPositionAsOf(inEquity,
                                                   inDate.getRaw());
        }
        /* (non-Javadoc)
         * @see org.marketcetera.client.Service#getAllEquityPositionsAsOf(org.marketcetera.util.ws.stateful.ClientContext, org.marketcetera.util.ws.wrappers.DateWrapper)
         */
        @Override
        public MapWrapper<PositionKey<Equity>,BigDecimal> getAllEquityPositionsAsOf(final ClientContext inContext,
                                                                                    DateWrapper inDate)
                throws RemoteException
        {
            // TODO Auto-generated method stub
            return null;
        }
        /* (non-Javadoc)
         * @see org.marketcetera.client.Service#getFuturePositionAsOf(org.marketcetera.util.ws.stateful.ClientContext, org.marketcetera.util.ws.wrappers.DateWrapper, org.marketcetera.trade.Future)
         */
        @Override
        public BigDecimal getFuturePositionAsOf(final ClientContext inContext,
                                                DateWrapper inDate,
                                                Future inFuture)
                throws RemoteException
        {
            return positionManager.getPositionAsOf(inFuture,
                                                   inDate.getRaw());
        }
        /* (non-Javadoc)
         * @see org.marketcetera.client.Service#getAllFuturePositionsAsOf(org.marketcetera.util.ws.stateful.ClientContext, org.marketcetera.util.ws.wrappers.DateWrapper)
         */
        @Override
        public MapWrapper<PositionKey<Future>,BigDecimal> getAllFuturePositionsAsOf(final ClientContext inContext,
                                                                                    DateWrapper inDate)
                throws RemoteException
        {
            // TODO Auto-generated method stub
            return null;
        }
        /* (non-Javadoc)
         * @see org.marketcetera.client.Service#getOptionPositionAsOf(org.marketcetera.util.ws.stateful.ClientContext, org.marketcetera.util.ws.wrappers.DateWrapper, org.marketcetera.trade.Option)
         */
        @Override
        public BigDecimal getOptionPositionAsOf(final ClientContext inContext,
                                                DateWrapper inDate,
                                                Option inOption)
                throws RemoteException
        {
            return positionManager.getPositionAsOf(inOption,
                                                   inDate.getRaw());
        }
        /* (non-Javadoc)
         * @see org.marketcetera.client.Service#getAllOptionPositionsAsOf(org.marketcetera.util.ws.stateful.ClientContext, org.marketcetera.util.ws.wrappers.DateWrapper)
         */
        @Override
        public MapWrapper<PositionKey<Option>,BigDecimal> getAllOptionPositionsAsOf(final ClientContext inContext,
                                                                                    DateWrapper inDate)
                throws RemoteException
        {
            // TODO Auto-generated method stub
            return null;
        }
        /* (non-Javadoc)
         * @see org.marketcetera.client.Service#getOptionPositionsAsOf(org.marketcetera.util.ws.stateful.ClientContext, org.marketcetera.util.ws.wrappers.DateWrapper, java.lang.String[])
         */
        @Override
        public MapWrapper<PositionKey<Option>,BigDecimal> getOptionPositionsAsOf(final ClientContext inContext,
                                                                                 DateWrapper inDate,
                                                                                 String... inRootSymbols)
                throws RemoteException
        {
            // TODO Auto-generated method stub
            return null;
        }
        /* (non-Javadoc)
         * @see org.marketcetera.client.Service#getNextOrderID(org.marketcetera.util.ws.stateful.ClientContext)
         */
        @Override
        public String getNextOrderID(final ClientContext inContext)
                throws RemoteException
        {
            // TODO Auto-generated method stub
            return null;
        }
        /* (non-Javadoc)
         * @see org.marketcetera.client.Service#getUnderlying(org.marketcetera.util.ws.stateful.ClientContext, java.lang.String)
         */
        @Override
        public String getUnderlying(final ClientContext inContext,
                                    String inOptionRoot)
                throws RemoteException
        {
            // TODO Auto-generated method stub
            return null;
        }
        /* (non-Javadoc)
         * @see org.marketcetera.client.Service#getOptionRoots(org.marketcetera.util.ws.stateful.ClientContext, java.lang.String)
         */
        @Override
        public Collection<String> getOptionRoots(final ClientContext inContext,
                                                 String inUnderlying)
                throws RemoteException
        {
            // TODO Auto-generated method stub
            return null;
        }
        /* (non-Javadoc)
         * @see org.marketcetera.client.Service#heartbeat(org.marketcetera.util.ws.stateful.ClientContext)
         */
        @Override
        public void heartbeat(final ClientContext inContext)
                throws RemoteException
        {
            // TODO Auto-generated method stub
        }
        /* (non-Javadoc)
         * @see org.marketcetera.client.Service#getUserData(org.marketcetera.util.ws.stateful.ClientContext)
         */
        @Override
        public String getUserData(final ClientContext inContext)
                throws RemoteException
        {
            // TODO Auto-generated method stub
            return null;
        }
        /* (non-Javadoc)
         * @see org.marketcetera.client.Service#setUserData(org.marketcetera.util.ws.stateful.ClientContext, java.lang.String)
         */
        @Override
        public void setUserData(final ClientContext inContext,
                                final String inData)
                throws RemoteException
        {
            // TODO Auto-generated method stub
            
        }
        /* (non-Javadoc)
         * @see org.marketcetera.server.ws.MatpService#sendOrder(org.marketcetera.trade.OrderSingle)
         */
        @Override
        public void sendOrderSingle(ClientContext inContext,
                                    OrderSingleImpl inOrderSingle)
        {
            orderManagerSelector.send(inOrderSingle);
        }
        /* (non-Javadoc)
         * @see org.marketcetera.server.ws.MatpService#sendOrder(org.marketcetera.trade.OrderReplace)
         */
        @Override
        public void sendOrderReplace(ClientContext inContext,
                                     OrderReplaceImpl inOrderReplace)
        {
            orderManagerSelector.send(inOrderReplace);
        }
        /* (non-Javadoc)
         * @see org.marketcetera.server.ws.MatpService#sendOrder(org.marketcetera.trade.OrderCancel)
         */
        @Override
        public void sendOrderCancel(ClientContext inContext,
                                    OrderCancelImpl inOrderCancel)
        {
            orderManagerSelector.send(inOrderCancel);
        }
        /* (non-Javadoc)
         * @see org.marketcetera.server.ws.MatpService#sendOrderRaw(org.marketcetera.trade.FIXOrder)
         */
        @Override
        public void sendOrderRaw(ClientContext inContext,
                                 FIXOrderImpl inFIXOrder)
        {
            orderManagerSelector.send(inFIXOrder);
        }
    }
    /**
     * 
     */
    private Server<ClientSession> server;
    /**
     * 
     */
    private ServiceInterface service;
    /**
     * 
     */
    private final String host;
    /**
     * 
     */
    private final int port;
    /**
     * 
     */
    private WSImpl wsImpl;
    /**
     * 
     */
    @Autowired
    private AuthenticationProvider authenticationProvider;
    /**
     * 
     */
    @Autowired
    private StrategyAgentManager strategyAgentManager;
    /**
     * 
     */
    @Autowired
    private OrderManagerSelector orderManagerSelector;
    /**
     * 
     */
//    @Autowired
    private OrderDestinationManager orderDestinationManager;
    /**
     * 
     */
    @Autowired
    private PositionManager positionManager;
}
