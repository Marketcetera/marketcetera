package org.marketcetera.server.config;

import org.marketcetera.marketdata.core.rpc.MarketDataRpcService;
import org.marketcetera.marketdata.core.rpc.MarketDataServiceAdapter;
import org.marketcetera.marketdata.core.webservice.impl.MarketDataServiceImpl;
import org.marketcetera.module.ModuleManager;
import org.marketcetera.server.session.ServerSession;
import org.marketcetera.server.session.ServerSessionManager;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import quickfix.MessageFactory;

/* $License$ */

/**
 * Provides application configuration for Spring.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@Configuration
@ConfigurationProperties(prefix="metc")
public class ServerAppConfiguration
        implements ApplicationContextAware
{
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
     * Get the module manager value.
     *
     * @return a <code>ModuleManager</code> value
     */
    @Bean
    public ModuleManager getModuleManager()
    {
        ModuleManager moduleManager = new ModuleManager();
        moduleManager.init();
        return moduleManager;
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
     * Get the market data RPC service.
     *
     * @return a <code>MarketDataRpcService&lt;ServerSession&gt;</code> value
     */
    @Bean
    @Autowired
    public MarketDataRpcService<ServerSession> getMarketDataRpcService(ServerSessionManager inSessionManager)
    {
        MarketDataRpcService<ServerSession> marketDataRpcService = new MarketDataRpcService<>();
        MarketDataServiceAdapter serviceAdapter = new MarketDataServiceImpl<ServerSession>(inSessionManager);
        marketDataRpcService.setServiceAdapter(serviceAdapter);
        return marketDataRpcService;
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
