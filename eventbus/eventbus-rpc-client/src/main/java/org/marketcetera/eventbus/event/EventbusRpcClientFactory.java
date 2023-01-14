//
// this file is automatically generated
//
package org.marketcetera.eventbus.event;

/* $License$ */

/**
 * Provides an RPC RpcClientFactory for EventbusRpc services.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class EventbusRpcClientFactory
        implements EventbusClientFactory<EventbusRpcClientParameters>
{
    /* (non-Javadoc)
     * @see org.marketcetera.eventbus.event.EventbusClientFactory#create(java.lang.Object)
     */
    @Override
    public org.marketcetera.eventbus.event.EventbusClient create(org.marketcetera.eventbus.event.EventbusRpcClientParameters inParameterClazz)
    {
        return applicationContext.getBean(org.marketcetera.eventbus.event.EventbusClient.class,inParameterClazz);
    }
    /**
     * provides access to the application context
     */
    @org.springframework.beans.factory.annotation.Autowired
    private org.springframework.context.ApplicationContext applicationContext;
}
