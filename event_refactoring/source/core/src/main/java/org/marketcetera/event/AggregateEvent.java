package org.marketcetera.event;

import java.util.List;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface AggregateEvent
        extends Event
{
    /**
     * 
     *
     *
     * @return
     */
    public List<Event> decompose();
}
