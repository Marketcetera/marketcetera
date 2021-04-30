package org.marketcetera.eventbus.test;

import org.marketcetera.eventbus.data.event.DataEventRpcClientFactory;
import org.marketcetera.eventbus.data.event.DataEventRpcServer;
import org.marketcetera.util.ws.tags.SessionId;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;

/* $License$ */

/**
 * Provides test configuration for Event Bus RPC tests.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@SpringBootConfiguration
public class EventBusRpcTestConfiguration
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
     * 
     *
     *
     * @return
     */
    @Bean
    public DataEventRpcServer<SessionId> getDataEventRpcServer()
    {
        return new DataEventRpcServer<SessionId>();
    }
}
