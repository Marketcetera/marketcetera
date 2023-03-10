package org.marketcetera.ui;

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
import org.marketcetera.cluster.SimpleClusterDataFactory;
import org.marketcetera.core.XmlService;
import org.marketcetera.fix.FixAdminRpcClientFactory;
import org.marketcetera.fix.FixSessionAttributeDescriptorFactory;
import org.marketcetera.fix.MutableActiveFixSessionFactory;
import org.marketcetera.fix.MutableFixSessionFactory;
import org.marketcetera.fix.impl.SimpleActiveFixSessionFactory;
import org.marketcetera.fix.impl.SimpleFixSessionAttributeDescriptorFactory;
import org.marketcetera.fix.impl.SimpleFixSessionFactory;
import org.marketcetera.marketdata.rpc.client.MarketDataRpcClientFactory;
import org.marketcetera.strategy.SimpleStrategyInstanceFactory;
import org.marketcetera.strategy.StrategyInstanceFactory;
import org.marketcetera.strategy.StrategyRpcClientFactory;
import org.marketcetera.symbol.IterativeSymbolResolver;
import org.marketcetera.symbol.PatternSymbolResolver;
import org.marketcetera.symbol.SymbolResolverService;
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
import org.marketcetera.ui.service.ServiceManager;
import org.marketcetera.ui.service.SessionUser;
import org.marketcetera.ui.service.admin.AdminClientService;
import org.marketcetera.util.except.I18NException;
import org.marketcetera.util.ws.stateful.Authenticator;
import org.marketcetera.util.ws.stateless.StatelessClientContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@Configuration
@PropertySource("file:conf/application.properties")
public class UiConfiguration
{
    /**
     * Get the symbol resolver service value.
     *
     * @return a <code>SymbolResolverService</code> value
     */
    @Bean
    public SymbolResolverService getSymbolResolverService()
    {
        IterativeSymbolResolver symbolResolverService = new IterativeSymbolResolver();
        symbolResolverService.getSymbolResolvers().add(new PatternSymbolResolver());
        return symbolResolverService;
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
    /**
     * Get the strategy instance factory value.
     *
     * @return a <code>StraetgyInstanceFactory</code> value
     */
    @Bean
    public StrategyInstanceFactory getStrategyInstanceFactory()
    {
        return new SimpleStrategyInstanceFactory();
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
     * Get the strategy client factory value.
     *
     * @return a <code>StrategyRpcClientFactory</code> value
     */
    @Bean
    public StrategyRpcClientFactory getStrategylientFactory()
    {
        return new StrategyRpcClientFactory();
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
     * Get the FIX admin client factory value.
     *
     * @return a <code>FixAdminClientFactory</code> value
     */
    @Bean
    public MarketDataRpcClientFactory getMarketDataClientFactory()
    {
        return new MarketDataRpcClientFactory();
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
                SessionUser.getCurrent().getPermissions().addAll(adminClientService.getPermissionsForUser());
                return true;
            }
        };
        return authenticator;
    }
}
