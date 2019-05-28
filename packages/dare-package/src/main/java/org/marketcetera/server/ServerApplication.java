package org.marketcetera.server;

import org.marketcetera.admin.service.UserService;
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
import org.marketcetera.trade.service.MessageOwnerService;
import org.marketcetera.trade.service.impl.MessageOwnerServiceImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

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
@EntityScan(basePackages={"org.marketcetera"})
@SpringBootApplication(scanBasePackages={"org.marketcetera"})
@EnableJpaRepositories(basePackages={"org.marketcetera"})
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
     * web services port
     */
    @Value("${server.port:8999}")
    private int webServerPort;
}
