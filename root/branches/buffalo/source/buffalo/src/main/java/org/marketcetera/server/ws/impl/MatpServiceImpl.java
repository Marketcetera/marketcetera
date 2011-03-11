package org.marketcetera.server.ws.impl;

import java.util.List;

import javax.annotation.concurrent.ThreadSafe;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.marketcetera.module.ModuleInfo;
import org.marketcetera.module.ModuleURN;
import org.marketcetera.saclient.CreateStrategyParameters;
import org.marketcetera.server.security.MetcAuthentication;
import org.marketcetera.server.service.StrategyAgentManager;
import org.marketcetera.server.ws.MatpService;
import org.marketcetera.util.except.I18NException;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.ws.stateful.*;
import org.marketcetera.util.ws.stateless.ServiceInterface;
import org.marketcetera.util.ws.stateless.StatelessClientContext;
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
    public ModuleURN createStrategy(ClientContext inContext,
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
    public void delete(ClientContext inContext,
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
    public List<ModuleURN> getInstances(ClientContext inContext,
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
    public ModuleInfo getModuleInfo(ClientContext inContext,
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
    public MapWrapper<String,Object> getProperties(ClientContext inContext,
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
    public List<ModuleURN> getProviders(ClientContext inContext)
            throws RemoteException
    {
        return wsImpl.getProviders(inContext);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.saclient.SAService#getStrategyCreateParms(org.marketcetera.util.ws.stateful.ClientContext, org.marketcetera.module.ModuleURN)
     */
    @Override
    public CreateStrategyParameters getStrategyCreateParms(ClientContext inContext,
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
    public MapWrapper<String,Object> setProperties(ClientContext inContext,
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
    public void start(ClientContext inContext,
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
    public void stop(ClientContext inContext,
                     ModuleURN inURN)
            throws RemoteException
    {
        wsImpl.start(inContext,
                     inURN);
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
        service = server.publish(this,
                                 MatpService.class);
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
        service.stop();
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
//    public static class ServerPasswordHandler
//            implements CallbackHandler
//    {
//        /* (non-Javadoc)
//         * @see javax.security.auth.callback.CallbackHandler#handle(javax.security.auth.callback.Callback[])
//         */
//        @Override
//        public void handle(Callback[] inCallbacks)
//                throws IOException, UnsupportedCallbackException
//        {
//            WSPasswordCallback pc = (WSPasswordCallback)inCallbacks[0];
//            SLF4JLoggerProxy.debug(MatpServiceImpl.class,
//                                   "Handling WS password callback for {}",
//                                   pc.getIdentifier());
//            System.out.println("In WS password callback for " + pc.getIdentifier());
//            if(pc.getIdentifier().equals("joe")) {
//                if(!pc.getPassword().equals("password")) {
//                    throw new IOException("Wrong password!");
//                }
//            }
//        }
//    }
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
        public void stop(ClientContext inCtx,
                         ModuleURN inURN)
                throws RemoteException
        {
            // TODO Auto-generated method stub
            
        }
        /* (non-Javadoc)
         * @see org.marketcetera.saclient.SAService#delete(org.marketcetera.util.ws.stateful.ClientContext, org.marketcetera.module.ModuleURN)
         */
        @Override
        public void delete(ClientContext inCtx,
                           ModuleURN inURN)
                throws RemoteException
        {
            // TODO Auto-generated method stub
            
        }
        /* (non-Javadoc)
         * @see org.marketcetera.saclient.SAService#getProperties(org.marketcetera.util.ws.stateful.ClientContext, org.marketcetera.module.ModuleURN)
         */
        @Override
        public MapWrapper<String, Object> getProperties(ClientContext inCtx,
                                                        ModuleURN inURN)
                throws RemoteException
        {
            // TODO Auto-generated method stub
            return null;
        }
        /* (non-Javadoc)
         * @see org.marketcetera.saclient.SAService#setProperties(org.marketcetera.util.ws.stateful.ClientContext, org.marketcetera.module.ModuleURN, org.marketcetera.util.ws.wrappers.MapWrapper)
         */
        @Override
        public MapWrapper<String, Object> setProperties(ClientContext inCtx,
                                                        ModuleURN inURN,
                                                        MapWrapper<String, Object> inProperties)
                throws RemoteException
        {
            // TODO Auto-generated method stub
            return null;
        }
        /* (non-Javadoc)
         * @see org.marketcetera.saclient.SAService#createStrategy(org.marketcetera.util.ws.stateful.ClientContext, org.marketcetera.saclient.CreateStrategyParameters)
         */
        @Override
        public ModuleURN createStrategy(ClientContext inCtx,
                                        CreateStrategyParameters inParameters)
                throws RemoteException
        {
            // TODO Auto-generated method stub
            return null;
        }
        /* (non-Javadoc)
         * @see org.marketcetera.saclient.SAService#getStrategyCreateParms(org.marketcetera.util.ws.stateful.ClientContext, org.marketcetera.module.ModuleURN)
         */
        @Override
        public CreateStrategyParameters getStrategyCreateParms(ClientContext inServiceContext,
                                                               ModuleURN inURN)
                throws RemoteException
        {
            // TODO Auto-generated method stub
            return null;
        }
    }
    private Server<ClientSession> server;
    private ServiceInterface service;
    private final String host;
    private final int port;
    @Autowired
    private StrategyAgentManager strategyAgentManager;
    private WSImpl wsImpl;
    @Autowired
    private AuthenticationProvider authenticationProvider;
}
