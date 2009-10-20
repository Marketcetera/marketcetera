package org.marketcetera.event;

import java.util.List;

import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * An aggregation of {@link EventBase} objects that represents an {@link Exchange} data output. 
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 1.5.0
 */
@ClassVersion("$Id$")
public interface AggregateEvent
        extends Event
{
    /**
     * Produces a list of <code>Event</code> objects that describe
     * this <code>AggregateEvent</code>.
     * 
     * @return a <code>List&lt;Event&gt;</code> value
     */
    public List<Event> decompose();
}
