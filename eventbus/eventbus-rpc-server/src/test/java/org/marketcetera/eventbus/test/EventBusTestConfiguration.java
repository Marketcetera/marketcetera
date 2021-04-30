package org.marketcetera.eventbus.test;

import javax.jms.ServerSession;

import org.marketcetera.eventbus.data.event.DataEventFactory;
import org.marketcetera.eventbus.data.event.DataEventRpcClientFactory;
import org.marketcetera.eventbus.data.event.DataEventRpcServer;
import org.marketcetera.eventbus.data.event.SimpleDataEventFactory;
import org.marketcetera.symbol.IterativeSymbolResolver;
import org.marketcetera.symbol.PatternSymbolResolver;
import org.marketcetera.symbol.SymbolResolverService;
import org.marketcetera.util.except.I18NException;
import org.marketcetera.util.ws.stateful.Authenticator;
import org.marketcetera.util.ws.stateful.SessionManager;
import org.marketcetera.util.ws.stateless.StatelessClientContext;
import org.marketcetera.util.ws.tags.SessionId;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;

/* $License$ */

/**
 * Provides test configuration for Event Bus tests.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@SpringBootConfiguration
public class EventBusTestConfiguration
{
    /**
     * Gets the data event RPC client factory value.
     *
     * @return a <code>DataEventRpcClientFactory</code> value
     */
    @Bean
    public DataEventRpcClientFactory getDataEventRpcClientFactory()
    {
        return new DataEventRpcClientFactory();
    }
    /**
     * Gets the DataEventFactory value.
     *
     * @return a <code>DataEventFactory</code> value
     */
    @Bean
    public DataEventFactory getDataEventFactory()
    {
        return new SimpleDataEventFactory();
    }
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
    @Bean
    public DataEventRpcServer<SessionId> getDataEventRpcServer()
    {
        return new DataEventRpcServer<SessionId>();
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
     * Get the authenticator service.
     *
     * @return an <code>Authenticator</code> value
     */
    @Bean
    public Authenticator getAuthenticator()
    {
        return new Authenticator() {
            @Override
            public boolean shouldAllow(StatelessClientContext inContext,
                                       String inUser,
                                       char[] inPassword)
                    throws I18NException
            {
                return true;
            }
        };
    }
}
