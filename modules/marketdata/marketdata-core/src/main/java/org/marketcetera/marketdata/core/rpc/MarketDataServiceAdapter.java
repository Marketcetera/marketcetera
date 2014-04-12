package org.marketcetera.marketdata.core.rpc;

import java.util.Deque;

import org.marketcetera.event.Event;
import org.marketcetera.marketdata.MarketDataRequest;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public interface MarketDataServiceAdapter
{
    /**
     *
     *
     * @param inRequest
     * @param inStreamEvents
     * @return
     */
    long request(MarketDataRequest inRequest,
                 boolean inStreamEvents);
    /**
     *
     *
     * @param inId
     * @return
     */
    long getLastUpdate(long inId);
    /**
     *
     *
     * @param inId
     */
    void cancel(long inId);
    /**
     *
     *
     * @param inId
     * @return
     */
    Deque<Event> getEvents(long inId);
}
