package org.marketcetera.test;

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
import org.marketcetera.fix.store.HibernateMessageStoreConfiguration;
import org.marketcetera.module.ModuleManager;
import org.marketcetera.quickfix.QuickFIXSender;
import org.marketcetera.quickfix.QuickFIXSenderImpl;
import org.marketcetera.symbol.IterativeSymbolResolver;
import org.marketcetera.symbol.PatternSymbolResolver;
import org.marketcetera.symbol.SymbolResolverService;
import org.marketcetera.trade.AverageFillPriceFactory;
import org.marketcetera.trade.SimpleAverageFillPriceFactory;
import org.marketcetera.trade.client.DirectTradeClientFactory;
import org.marketcetera.trade.event.connector.IncomingTradeMessageBroadcastConnector;
import org.marketcetera.trade.event.connector.IncomingTradeMessageConverterConnector;
import org.marketcetera.trade.event.connector.IncomingTradeMessagePersistenceConnector;
import org.marketcetera.trade.event.connector.OrderConverterConnector;
import org.marketcetera.trade.event.connector.OutgoingMessageCachingConnector;
import org.marketcetera.trade.event.connector.OutgoingMessagePersistenceConnector;
import org.marketcetera.trade.service.MessageOwnerService;
import org.marketcetera.trade.service.impl.MessageOwnerServiceImpl;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;

import quickfix.MessageFactory;

/* $License$ */

/**
 * Provides DARE test configuration.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: FixServerTestConfiguration.java 17804 2018-12-03 00:22:03Z colin $
 * @since $Release$
 */
@SpringBootConfiguration
public class DareTestConfiguration
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
     * Get the direct trade client factory value.
     *
     * @return a <code>DirectTradeClientFactory</code> value
     */
    @Bean
    public DirectTradeClientFactory getDirectTradeClientFactory()
    {
        return new DirectTradeClientFactory();
    }
    /**
     * Get the Hibernate message store configuration value.
     *
     * @return a <code>HibernateMessageStoreConfiguration</code> value
     */
    @Bean
    public HibernateMessageStoreConfiguration getMessageStoreConfiguration()
    {
        HibernateMessageStoreConfiguration messageStoreConfiguration = new HibernateMessageStoreConfiguration();
        messageStoreConfiguration.getMessageTypeBlacklist().clear();
        return messageStoreConfiguration;
    }
    /**
     * Get the module manager value.
     *
     * @return a <code>ModuleManager</code> value
     */
    @Bean
    public ModuleManager getModuleManager()
    {
        ModuleManager moduleManager = ModuleManager.getInstance();
        if(moduleManager == null) {
            moduleManager = new ModuleManager();
            moduleManager.init();
        }
        return moduleManager;
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
     * Get the server FIX session factory value.
     *
     * @return a <code>ServerFixSessionFactory</code> value
     */
    @Bean
    public ServerFixSessionFactory getServerFixSessionFactory()
    {
        return new SimpleServerFixSessionFactory();
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
     * Get the message factory value.
     *
     * @return a <code>MessageFactory</code> value
     */
    @Bean
    public MessageFactory getMessageFactory()
    {
        return new quickfix.DefaultMessageFactory();
    }
    /**
     * Get the FIX session provider value.
     *
     * @return a <code>FixSessionProvider</code> value
     */
    @Bean
    public FixSessionProvider getFixSessionProvider()
    {
        return new PersistentFixSessionProvider();
    }
    /**
     * Get the FIX session factory value.
     *
     * @return a <code>MutableFixSessionFactory</code> value
     */
    @Bean
    public MutableFixSessionFactory getFixSessionFactory()
    {
        return new PersistentFixSessionFactory();
    }
    /**
     * Get the cluster service.
     *
     * @return a <code>ClusterService</code> value
     */
    @Bean
    public ClusterService getClusterService()
    {
        return new SimpleClusterService();
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
     * Get the message owner service value.
     *
     * @return a <code>MessageOwnerService</code> value
     */
    @Bean
    public MessageOwnerService getMessageOwnerService()
    {
        return new MessageOwnerServiceImpl();
    }
    // begin event connectors
    /**
     * Get the order converter connector value.
     *
     * @return an <code>OrderConverterConnector</code> value
     */
    @Bean
    public OrderConverterConnector getOrderConverterConnector()
    {
        return new OrderConverterConnector();
    }
    /**
     * Get the outgoing message caching connector value.
     *
     * @return an <code>OutgoingMessageCachingConnector</code> value
     */
    @Bean
    public OutgoingMessageCachingConnector getOutgoingMessageCachingConnector()
    {
        return new OutgoingMessageCachingConnector();
    }
    /**
     * Get the outgoing message persistence connector value.
     *
     * @return an <code>OutgoingMessagePersistenceConnector</code> value
     */
    @Bean
    public OutgoingMessagePersistenceConnector getOutgoingMessagePersistenceConnector()
    {
        return new OutgoingMessagePersistenceConnector();
    }
    /**
     * Get the incoming trade message converter connector value.
     *
     * @return an <code>IncomingTradeMessageConverterConnector</code> value
     */
    @Bean
    public IncomingTradeMessageConverterConnector getIncomingTradeMessageConverterConnector()
    {
        return new IncomingTradeMessageConverterConnector();
    }
    /**
     * Get the incoming trade message persistence connector value.
     *
     * @return an <code>IncomingTradeMessagePersistenceConnector</code> value
     */
    @Bean
    public IncomingTradeMessagePersistenceConnector getIncomingTradeMessagePersistenceConnector()
    {
        return new IncomingTradeMessagePersistenceConnector();
    }
    /**
     * Get the IncomingTradeMessageBroadcastConnector value.
     *
     * @return an <code>IncomingTradeMessageBroadcastConnector</code> value
     */
    @Bean
    public IncomingTradeMessageBroadcastConnector getIncomingTradeMessageBroadcastConnector()
    {
        return new IncomingTradeMessageBroadcastConnector();
    }
}
