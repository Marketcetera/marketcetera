package org.marketcetera.eventbus;

import java.io.Serializable;

/* $License$ */

/**
 * Marks an event as a suitable candidate for Esper queries.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface EsperEvent
        extends Serializable
{
    /**
     * Get the event name value.
     *
     * @return a <code>String</code> value
     */
    String getEventName();
}
