package org.marketcetera.marketdata;

import java.util.Collection;

import org.marketcetera.marketdata.events.Event;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface MarketDataRepository
{
    public void store(Collection<Event> inEvents);
    public Collection<Event> retrieve(MarketDataRequestAtom inRequestAtom);
}
