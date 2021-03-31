package org.marketcetera.web;

import java.util.Arrays;

import org.marketcetera.admin.AdminRpcClientFactory;
import org.marketcetera.admin.PermissionFactory;
import org.marketcetera.admin.RoleFactory;
import org.marketcetera.admin.UserAttributeFactory;
import org.marketcetera.admin.UserFactory;
import org.marketcetera.admin.impl.SimplePermissionFactory;
import org.marketcetera.admin.impl.SimpleRoleFactory;
import org.marketcetera.admin.impl.SimpleUserAttributeFactory;
import org.marketcetera.admin.impl.SimpleUserFactory;
import org.marketcetera.cluster.ClusterDataFactory;
import org.marketcetera.cluster.ClusterMemberFactory;
import org.marketcetera.cluster.ClusterRpcClientFactory;
import org.marketcetera.cluster.SimpleClusterDataFactory;
import org.marketcetera.cluster.SimpleClusterMemberFactory;
import org.marketcetera.core.ContextClassAggregator;
import org.marketcetera.core.XmlService;
import org.marketcetera.dataflow.client.rpc.DataFlowRpcClientFactory;
import org.marketcetera.fix.FixAdminRpcClientFactory;
import org.marketcetera.fix.FixSessionAttributeDescriptorFactory;
import org.marketcetera.fix.MutableActiveFixSessionFactory;
import org.marketcetera.fix.MutableFixSessionFactory;
import org.marketcetera.fix.impl.SimpleActiveFixSessionFactory;
import org.marketcetera.fix.impl.SimpleFixSessionAttributeDescriptorFactory;
import org.marketcetera.fix.impl.SimpleFixSessionFactory;
import org.marketcetera.marketdata.rpc.client.MarketDataRpcClientFactory;
import org.marketcetera.trade.AverageFillPriceFactory;
import org.marketcetera.trade.MutableExecutionReportSummaryFactory;
import org.marketcetera.trade.MutableOrderSummaryFactory;
import org.marketcetera.trade.MutableReportFactory;
import org.marketcetera.trade.SimpleAverageFillPrice;
import org.marketcetera.trade.SimpleAverageFillPriceFactory;
import org.marketcetera.trade.SimpleExecutionReportSummaryFactory;
import org.marketcetera.trade.SimpleOrderSummaryFactory;
import org.marketcetera.trade.SimpleReportFactory;
import org.marketcetera.trade.TradeContextClassProvider;
import org.marketcetera.trading.rpc.TradeRpcClientFactory;
import org.marketcetera.util.except.I18NException;
import org.marketcetera.util.ws.ContextClassProvider;
import org.marketcetera.util.ws.stateful.Authenticator;
import org.marketcetera.util.ws.stateless.StatelessClientContext;
import org.marketcetera.web.service.ServiceManager;
import org.marketcetera.web.service.admin.AdminClientService;
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
public class WebUiConfiguration
        implements ApplicationContextAware
{
    /**
     * Get the authenticator for the web application.
     *
     * @param inServiceManager a <code>ServiceManager</code> value
     * @return an <code>Authenticator</code> value
     */
    @Bean
    public Authenticator getAuthenticator(ServiceManager inServiceManager)
    {
        Authenticator authenticator = new Authenticator() {
            @Override
            public boolean shouldAllow(StatelessClientContext inContext,
                                       String inUser,
                                       char[] inPassword)
                    throws I18NException
            {
                AdminClientService adminClientService = inServiceManager.getService(AdminClientService.class);
                SessionUser.getCurrentUser().getPermissions().addAll(adminClientService.getPermissionsForUser());
                return true;
            }
        };
        return authenticator;
    }
    /**
     * Get the execution report summary factory value.
     *
     * @return a <code>MutableExecutionReportSummaryFactory</code> value
     */
    @Bean
    public MutableExecutionReportSummaryFactory getExecutionReportSummaryFactory()
    {
        return new SimpleExecutionReportSummaryFactory();
    }
    /**
     * Get the average fill price factory value.
     *
     * @return an <code>AverageFillPriceFactory</code> value
     */
    @Bean
    public AverageFillPriceFactory getAverageFillPriceFactory()
    {
        return new SimpleAverageFillPriceFactory();
    }
    /**
     * Get the admin client factory value.
     *
     * @return an <code>AdminClientFactory</code> value
     */
    @Bean
    public AdminRpcClientFactory getAdminClientFactory()
    {
        return new AdminRpcClientFactory();
    }
    /**
     * Get the FIX admin client factory value.
     *
     * @return a <code>FixAdminClientFactory</code> value
     */
    @Bean
    public FixAdminRpcClientFactory getFixAdminClientFactory()
    {
        return new FixAdminRpcClientFactory();
    }
    /**
     * Get the trade client factory value.
     *
     * @return a <code>TradeRpcClientFactory</code> value
     */
    @Bean
    public TradeRpcClientFactory getTradeClientFactory()
    {
        return new TradeRpcClientFactory();
    }
    /**
     * Get the cluster client factory value.
     *
     * @return a <code>ClusterClientFactory</code> value
     */
    @Bean
    public ClusterRpcClientFactory getClusterClientFactory()
    {
        return new ClusterRpcClientFactory();
    }
    /**
     * Get the data flow client factory value.
     *
     * @return a <code>DataFlowClientFactory</code> value
     */
    @Bean
    public DataFlowRpcClientFactory getDataFlowClientFactory()
    {
        return new DataFlowRpcClientFactory();
    }
    /**
     * Get the market data client factory value.
     *
     * @return a <code>MarketDataClientFactory</code> value
     */
    @Bean
    public MarketDataRpcClientFactory getMarketDataClientFactory()
    {
        return new MarketDataRpcClientFactory();
    }
    /**
     * Get the cluster data factory value.
     *
     * @return a <code>ClusterDataFactory</code> value
     */
    @Bean
    public ClusterDataFactory getClusterDataFactory()
    {
        return new SimpleClusterDataFactory();
    }
    /**
     * Get the cluster member factory value.
     *
     * @return a <code>ClusterMemberFactory</code> value
     */
    @Bean
    public ClusterMemberFactory getClusterMemberFactory()
    {
        return new SimpleClusterMemberFactory();
    }
//    /**
//     * Get the SA client factory value.
//     *
//     * @return an <code>SAClientFactory</code> value
//     */
//    @Bean
//    public SAClientFactory getSAClientFactory()
//    {
//        return new RpcSAClientFactory();
//    }
    /**
     * Get the permission factory value.
     *
     * @return a <code>PermissionFactory</code> value
     */
    @Bean
    public PermissionFactory getPermissionFactory()
    {
        return new SimplePermissionFactory();
    }
    /**
     * Get the user factory value.
     *
     * @return a <code>UserFactory</code> value
     */
    @Bean
    public UserFactory getUserFactory()
    {
        return new SimpleUserFactory();
    }
    /**
     * Get the role factory value.
     *
     * @return a <code>RoleFactory</code> value
     */
    @Bean
    public RoleFactory getRoleFactory()
    {
        return new SimpleRoleFactory();
    }
    /**
     * Get the event bus value.
     *
     * @return an <code>EventBus</code> value
     */
    @Bean
    public EventBus getEventBus()
    {
        return new EventBus();
    }
    /**
     * Get the user attribute factory value.
     *
     * @return a <code>UserAttributeFactory</code> value
     */
    @Bean
    public UserAttributeFactory getUserAttributeFactory()
    {
        return new SimpleUserAttributeFactory();
    }
    /**
     * Get the active FIX session factory value.
     *
     * @return a <code>MutableActiveFixSessionFactory</code> value
     */
    @Bean
    public MutableActiveFixSessionFactory getActiveFixSessionFactory()
    {
        return new SimpleActiveFixSessionFactory();
    }
    /**
     * Get the FIX session factory value.
     *
     * @return a <code>MutableFixSessionFactory</code> value
     */
    @Bean
    public MutableFixSessionFactory getFixSessionFactory()
    {
        return new SimpleFixSessionFactory();
    }
    /**
     * Get the FIX session attribute descriptor factory value.
     *
     * @return a <code>FixSessionAttributeDescriptoFactory</code> value
     */
    @Bean
    public FixSessionAttributeDescriptorFactory getFixSessionAttributeDescriptorFactory()
    {
        return new SimpleFixSessionAttributeDescriptorFactory();
    }
    /**
     * Get the order summary factory value.
     *
     * @return a <code>MutableOrderSummayFactory</code> value
     */
    @Bean
    public MutableOrderSummaryFactory getOrderSummaryFactory()
    {
        return new SimpleOrderSummaryFactory();
    }
    /**
     * Get the report factory value.
     *
     * @return a <code>SimpleReportFactory</code> value
     */
    @Bean
    public MutableReportFactory getReportFactory()
    {
        return new SimpleReportFactory();
    }
    /**
     * Get the XML context provider for strategy engines.
     *
     * @return a <code>ContextClassProvider</code> value
     */
    @Bean
    public ContextClassProvider getStrategyEngineContextProvider()
    {
        ContextClassAggregator saContextProvider = new ContextClassAggregator();
//        saContextProvider.setContextClassProviders(Lists.newArrayList(new SAClientContextClassProvider()));
        return saContextProvider;
    }
    /**
     * Get the XML service value.
     *
     * @return an <code>XmlService</code> value
     */
    @Bean
    public XmlService getXmlService()
    {
        XmlService xmlService = new XmlService();
        xmlService.getContextPath().addAll(Arrays.asList(new TradeContextClassProvider().getContextClasses()));
        xmlService.getContextPath().add(SimpleAverageFillPrice.class);
        return xmlService;
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
    public ApplicationContext getApplicationContext()
    {
        return applicationContext;
    }
    /**
     * application context value
     */
    private ApplicationContext applicationContext;
}
