package com.marketcetera.admin;

import java.util.List;

import org.marketcetera.admin.UserAttributeFactory;
import org.marketcetera.admin.dao.PersistentUserAttributeFactory;
import org.marketcetera.admin.service.UserAttributeService;
import org.marketcetera.admin.service.impl.UserAttributeServiceImpl;
import org.marketcetera.rpc.server.RpcServer;
import org.marketcetera.util.ws.stateful.SessionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import com.marketcetera.admin.rpc.AdminRpcService;

import io.grpc.BindableService;

/* $License$ */

/**
 * Provides configuration for admin server tests.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@SpringBootConfiguration
@EnableAutoConfiguration
@ComponentScan(basePackages={"org.marketcetera","com.marketcetera"})
public class AdminTestConfiguration
{
    /**
     * Get the admin client factory value.
     *
     * @return an <code>AdminRpcClientFactory</code> value
     */
    @Bean
    public AdminRpcClientFactory getAdminClientFactory()
    {
        return new AdminRpcClientFactory();
    }
    /**
     * Get the user attribute factory value.
     *
     * @return a <code>UserAttributeFactory</code> value
     */
    @Bean
    public UserAttributeFactory getUserAttributeFactory()
    {
        return new PersistentUserAttributeFactory();
    }
    /**
     * Get the user attribute service value.
     *
     * @return a <code>UserAttributeService</code> value
     */
    @Bean
    public UserAttributeService getUserAttributeService()
    {
        return new UserAttributeServiceImpl();
    }
    /**
     * Get the admin RPC service value.
     *
     * @return an <code>RpcServiceSpec&lt;MockSession&gt;</code> value
     */
    @Bean
    public AdminRpcService<MockSession> getAdminRpcService()
    {
        return new AdminRpcService<>();
    }
    /**
     * Get the RPC server value.
     *
     * @param inServiceSpecs a <code>List&lt;BindableService&gt;</code> value
     * @return an <code>RpcServer</code> value
     * @throws Exception if the server cannot be created 
     */
    @Bean
    public RpcServer getRpcServer(@Autowired(required=false) List<BindableService> inServiceSpecs)
            throws Exception
    {
        RpcServer rpcServer = new RpcServer();
        rpcServer.setHostname(rpcHostname);
        rpcServer.setPort(rpcPort);
        if(inServiceSpecs != null) {
            for(BindableService service : inServiceSpecs) {
                rpcServer.getServerServiceDefinitions().add(service);
            }
        }
        return rpcServer;
    }
    /**
     * Get the client session factory value.
     *
     * @return a <code>MockSessionFactory</code> value
     */
    @Bean
    public MockSessionFactory getMockSessionFactory()
    {
        return new MockSessionFactory();
    }
    /**
     * Get the session manager value.
     *
     * @param inMockSessionFactory a <code>MockSessionFactory</code>value
     * @return a <code>SessionManager&lt;MockSession&gt;</code> value
     */
    @Bean
    public SessionManager<MockSession> getSessionManager(@Autowired MockSessionFactory inMockSessionFactory)
    {
        SessionManager<MockSession> sessionManager = new SessionManager<>(inMockSessionFactory,
                                                                          sessionLife);
        return sessionManager;
    }
    /**
     * RPC hostname
     */
    @Value("${metc.rpc.hostname}")
    private String rpcHostname = "127.0.0.1";
    /**
     * RPC port
     */
    @Value("${metc.rpc.port}")
    private int rpcPort = 8999;
    /**
     * session life value in millis
     */
    @Value("${metc.session.life.mills}")
    private long sessionLife = SessionManager.INFINITE_SESSION_LIFESPAN;
}
