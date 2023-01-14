//
// this file is automatically generated
//
package org.marketcetera.eventbus.event;

import org.marketcetera.core.BaseClient;
import org.marketcetera.core.Preserve;

/* $License$ */

/**
 * Provides a client interface for EventbusRpc services.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@Preserve
public interface EventbusClient
        extends BaseClient
{
    /**
     * Requests events as they are created.
     *
     * @param inEventListener an <code>EventbusEventListener</code> value
     */
    void getEvents(EventbusEventListener inEventListener);
    /**
     * Cancels an event request.
     *
     * @param inEventListener an <code>EventbusEventListener</code> value
     */
    void cancelEventRequest(EventbusEventListener inEventListener);
}
