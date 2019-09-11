package org.marketcetera.server;

import java.util.Collections;
import java.util.List;

import javax.jms.ServerSession;

import org.marketcetera.admin.UserAttributeFactory;
import org.marketcetera.admin.auth.DBAuthenticator;
import org.marketcetera.admin.dao.PersistentUserAttributeFactory;
import org.marketcetera.admin.rpc.AdminRpcService;
import org.marketcetera.admin.service.UserAttributeService;
import org.marketcetera.admin.service.UserService;
import org.marketcetera.admin.service.impl.UserAttributeServiceImpl;
import org.marketcetera.admin.service.impl.UserServiceImpl;
import org.marketcetera.brokers.service.FixSessionProvider;
import org.marketcetera.cluster.ClusterDataFactory;
import org.marketcetera.cluster.SimpleClusterDataFactory;
import org.marketcetera.cluster.SimpleClusterService;
import org.marketcetera.cluster.service.ClusterService;
import org.marketcetera.fix.MutableActiveFixSessionFactory;
import org.marketcetera.fix.MutableFixSessionFactory;
import org.marketcetera.fix.ServerFixSessionFactory;
import org.marketcetera.fix.dao.PersistentFixSessionFactory;
import org.marketcetera.fix.dao.PersistentFixSessionProvider;
import org.marketcetera.fix.impl.SimpleActiveFixSessionFactory;
import org.marketcetera.fix.impl.SimpleServerFixSessionFactory;
import org.marketcetera.quickfix.QuickFIXSender;
import org.marketcetera.quickfix.QuickFIXSenderImpl;
import org.marketcetera.rpc.server.RpcServer;
import org.marketcetera.trade.service.MessageOwnerService;
import org.marketcetera.trade.service.impl.MessageOwnerServiceImpl;
import org.marketcetera.util.ws.stateful.Authenticator;
import org.marketcetera.util.ws.stateful.PortUserProxy;
import org.marketcetera.util.ws.stateful.SessionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import io.grpc.BindableService;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

/* $License$ */

/**
 * Application entry point for the web UI.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@EnableAutoConfiguration
@SpringBootConfiguration
@EnableTransactionManagement
@EntityScan(basePackages={"org.marketcetera","com.marketcetera"})
@SpringBootApplication(scanBasePackages={"org.marketcetera","com.marketcetera"})
@EnableJpaRepositories(basePackages={"org.marketcetera","com.marketcetera"})
public class ServerApplication
{
    /**
     * Main application entry.
     *
     * @param inArgs a <code>String[]</code> value
     */
    public static void main(String[] inArgs)
    {
        SpringApplication.run(ServerApplication.class,
                              inArgs);
    }
    /**
     * Get the user service bean.
     *
     * @return a <code>UserService</code> value
     */
    @Bean
    public UserService getUserService()
    {
        return new UserServiceImpl();
    }
    /**
     * Get the FIX session provider bean.
     *
     * @return a <code>FixSessionProvider</code> value
     */
    @Bean
    public FixSessionProvider getFixSessionProvider()
    {
        return new PersistentFixSessionProvider();
    }
    /**
     * Get the message factory bean.
     *
     * @return a <code>quickfix.MessageFactory</code> value
     */
    @Bean
    public quickfix.MessageFactory getMessageFactory()
    {
        return new quickfix.DefaultMessageFactory();
    }
    /**
     * Get the cluster service bean.
     *
     * @return a <code>ClusterService</code> value
     */
    @Bean
    public ClusterService getClusterService()
    {
        return new SimpleClusterService();
    }
    /**
     * Get the cluster data factory bean.
     *
     * @return a <code>ClusterDataFactory</code> value
     */
    @Bean
    public ClusterDataFactory getClusterDataFactory()
    {
        return new SimpleClusterDataFactory();
    }
    /**
     * Get the mutable FIX session factory bean.
     *
     * @return a <code>MutableFixSessionFactory</code> value
     */
    @Bean
    public MutableFixSessionFactory getMutableFixSessionFactory()
    {
        return new PersistentFixSessionFactory();
    }
    /**
     * Get the mutable active FIX session factory bean.
     *
     * @return a <code>MutableActiveFixSessionFactory</code> value
     */
    @Bean
    public MutableActiveFixSessionFactory getMutableActiveFixSessionFactory()
    {
        return new SimpleActiveFixSessionFactory();
    }
    /**
     * Get the server FIX session factory bean.
     *
     * @return a <code>ServerFixSessionFactory</code> value
     */
    @Bean
    public ServerFixSessionFactory getServerFixSessionFactory()
    {
        return new SimpleServerFixSessionFactory();
    }
    /**
     * Get the message owner service value.
     *
     * @return a <code>MessageOwnerService</code> value
     */
    @Bean
    public MessageOwnerService getMessageOwnerService()
    {
        return new MessageOwnerServiceImpl();
    }
    /**
     * Get the QFJ sender implementation for DARE.
     *
     * @return a <code>QuickFIXSender</code> value
     */
    @Bean
    public QuickFIXSender getQuickFixSender()
    {
        return new QuickFIXSenderImpl();
    }
    /**
     * Create the Swagger API component.
     *
     * @return a <code>Docket</code> value
     */
    @Bean
    public Docket api()
    { 
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.ant("/matp/*"))
                .build()
                .apiInfo(apiInfo());
    }
    /**
     * Get the port user proxy for the embedded web server.
     *
     * @return a <code>PortUserProxy</code> value
     */
    @Bean
    public PortUserProxy getEmbeddedWebServerPortUserProxy()
    {
        PortUserProxy proxy = new PortUserProxy();
        proxy.setPort(webServerPort);
        proxy.setDescription("DARE Web Server");
        return proxy;
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
        rpcServer.setHostname(serverHostname);
        rpcServer.setPort(rpcPort);
        if(inServiceSpecs != null) {
            for(BindableService service : inServiceSpecs) {
                rpcServer.getServerServiceDefinitions().add(service);
            }
        }
        return rpcServer;
    }
    /**
     * Get the admin RPC service.
     *
     * @param inAuthenticator an <code>Authenticator</code> value
     * @param inSessionManager&lt;ServerSession&gt;</code> value
     * @return an <code>AdminRpcService&lt;ServerSession&gt;</code> value
     */
    @Bean
    public AdminRpcService<ServerSession> getAdminRpcService(@Autowired Authenticator inAuthenticator,
                                                             @Autowired SessionManager<ServerSession> inSessionManager)
    {
        AdminRpcService<ServerSession> adminRpcService = new AdminRpcService<>();
        adminRpcService.setAuthenticator(inAuthenticator);
        adminRpcService.setSessionManager(inSessionManager);
        return adminRpcService;
    }
    /**
     * Get the authenticator service.
     *
     * @return an <code>Authenticator</code> value
     */
    @Bean
    public Authenticator getAuthenticator()
    {
        return new DBAuthenticator();
    }
    /**
     * Get the session manager service.
     *
     * @return a <code>SessionManager&lt;ServerSession&gt;</code> value
     */
    @Bean
    public SessionManager<ServerSession> getSessionManager()
    {
        return new SessionManager<ServerSession>();
    }
    /**
     * Get the user attribute factory value.
     *
     * @return a <code>UserAttribute</code> value
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
     * Get the API info (REST Swagger) for DARE.
     *
     * @return an <code>ApiInfo</code> value
     */
    private ApiInfo apiInfo()
    {
        return new ApiInfo(
          "Marketcetera Automated Trading Engine REST API", 
          "REST API for MATP", 
          "API TOS", 
          "Terms of service", 
          new Contact("Colin DuPlantis", 
                      "www.marketcetera.com",
                      "info@marketcetera.com"), 
          "License of API",
          "API license URL",
          Collections.emptyList());
    }
    /**
     * web services port
     */
    @Value("${metc.ws.port}")
    private int webServerPort;
    /**
     * server hostname
     */
    @Value("${metc.ws.hostname:0.0.0.0}")
    private String serverHostname;
    /**
     * RPC services port
     */
    @Value("${metc.rpc.port}")
    private int rpcPort;
}
