package org.marketcetera.webui.config;

import org.marketcetera.admin.AdminClientFactory;
import org.marketcetera.admin.AdminRpcClientFactory;
import org.marketcetera.admin.PermissionFactory;
import org.marketcetera.admin.RoleFactory;
import org.marketcetera.admin.UserAttributeFactory;
import org.marketcetera.admin.UserFactory;
import org.marketcetera.admin.impl.SimplePermissionFactory;
import org.marketcetera.admin.impl.SimpleRoleFactory;
import org.marketcetera.admin.impl.SimpleUserAttributeFactory;
import org.marketcetera.admin.impl.SimpleUserFactory;
import org.marketcetera.core.ContextClassAggregator;
import org.marketcetera.fix.FixSessionFactory;
import org.marketcetera.fix.impl.SimpleFixSessionFactory;
import org.marketcetera.trade.MutableOrderSummaryFactory;
import org.marketcetera.trade.MutableReportFactory;
import org.marketcetera.trade.SimpleOrderSummaryFactory;
import org.marketcetera.trade.SimpleReportFactory;
import org.marketcetera.trade.client.TradeClientFactory;
import org.marketcetera.trading.rpc.TradeRpcClientFactory;
import org.marketcetera.util.ws.ContextClassProvider;
import org.marketcetera.webui.service.TradeClientService;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.common.eventbus.EventBus;

/* $License$ */

/**
 * Provides application configuration for Spring.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@Configuration
public class AppConfiguration
        implements ApplicationContextAware
{
    /**
     * Get the admin client factory value.
     *
     * @return an <code>AdminClientFactory</code> value
     */
    @Bean
    public static AdminClientFactory<?> getAdminClientFactory()
    {
        return (AdminClientFactory<?>)new AdminRpcClientFactory();
    }
    /**
     * Get the trade client factory value.
     *
     * @return a <code>TradeClientFactory</code> value
     */
    @Bean
    public static TradeClientFactory<?> getTradeClientFactory()
    {
        return (TradeClientFactory<?>)new TradeRpcClientFactory();
    }
    /**
     * Get the trade client service value.
     *
     * @return a <code>TradeClientService</code> value
     */
    @Bean
    public static TradeClientService getTradeClientService()
    {
        return new TradeClientService();
    }
    /**
     * Get the mutable order summary factory value.
     *
     * @return a <code>MutableOrderSummaryFactory</code> value
     */
    @Bean
    public MutableOrderSummaryFactory getMutableOrderSummaryFactory()
    {
        return new SimpleOrderSummaryFactory();
    }
    /**
     * Get the FIX session factory value.
     *
     * @return a <code>FixSessionFactory</code> value
     */
    @Bean
    public FixSessionFactory getFixSessionFactory()
    {
        return new SimpleFixSessionFactory();
    }
    /**
     * Get the report factory value.
     *
     * @return a <code>MutableReportFactory</code> value
     */
    @Bean
    public MutableReportFactory getReportFactory()
    {
        return new SimpleReportFactory();
    }
//    /**
//     * Get the SA client factory value.
//     *
//     * @return an <code>SAClientFactory</code> value
//     */
//    @Bean
//    public static SAClientFactory getSAClientFactory()
//    {
//        return new RpcSAClientFactory();
//    }
    /**
     * Get the permission factory value.
     *
     * @return a <code>PermissionFactory</code> value
     */
    @Bean
    public static PermissionFactory getPermissionFactory()
    {
        return new SimplePermissionFactory();
    }
    /**
     * Get the user factory value.
     *
     * @return a <code>UserFactory</code> value
     */
    @Bean
    public static UserFactory getUserFactory()
    {
        return new SimpleUserFactory();
    }
    /**
     * Get the role factory value.
     *
     * @return a <code>RoleFactory</code> value
     */
    @Bean
    public static RoleFactory getRoleFactory()
    {
        return new SimpleRoleFactory();
    }
    /**
     * Get the event bus value.
     *
     * @return an <code>EventBus</code> value
     */
    @Bean
    public static EventBus getEventBus()
    {
        return new EventBus();
    }
    /**
     * Get the user attribute factory value.
     *
     * @return a <code>UserAttributeFactory</code> value
     */
    @Bean
    public static UserAttributeFactory getUserAttributeFactory()
    {
        return new SimpleUserAttributeFactory();
    }
    /**
     * Get the XML context provider for strategy engines.
     *
     * @return a <code>ContextClassProvider</code> value
     */
    @Bean
    public static ContextClassProvider getStrategyEngineContextProvider()
    {
        ContextClassAggregator saContextProvider = new ContextClassAggregator();
//        saContextProvider.setContextClassProviders(Lists.newArrayList(new SAClientContextClassProvider()));
        return saContextProvider;
    }
    /* (non-Javadoc)
     * @see org.springframework.context.ApplicationContextAware#setApplicationContext(org.springframework.context.ApplicationContext)
     */
    @Override
    public void setApplicationContext(ApplicationContext inApplicationContext)
            throws BeansException
    {
        applicationContext = inApplicationContext;
    }
    /**
     * Get the applicationContext value.
     *
     * @return a <code>ApplicationContext</code> value
     */
    public static ApplicationContext getApplicationContext()
    {
        return applicationContext;
    }
    /**
     * application context value
     */
    private static ApplicationContext applicationContext;
}
