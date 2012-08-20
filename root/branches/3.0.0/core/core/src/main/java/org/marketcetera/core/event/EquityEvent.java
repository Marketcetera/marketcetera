package org.marketcetera.core.event;

import org.marketcetera.api.attributes.ClassVersion;

/* $License$ */

/**
 * Indicates that the implementing class represents an equity event.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: EquityEvent.java 16063 2012-01-31 18:21:55Z colin $
 * @since 2.0.0
 */
@ClassVersion("$Id: EquityEvent.java 16063 2012-01-31 18:21:55Z colin $")
public interface EquityEvent
        extends HasEquity, Event
{
}
