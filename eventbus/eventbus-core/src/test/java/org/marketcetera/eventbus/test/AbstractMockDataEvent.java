package org.marketcetera.eventbus.test;

import java.util.concurrent.atomic.AtomicLong;

import org.marketcetera.eventbus.data.event.DataEvent;
import org.marketcetera.eventbus.data.event.SimpleDataEvent;

/* $License$ */

/**
 * Provides a test {@link DataEvent} implementation.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class AbstractMockDataEvent
        extends SimpleDataEvent
{
    /**
     * Create a new AbstractMockDataEvent instance.
     */
    protected AbstractMockDataEvent()
    {
        setId(counter.incrementAndGet());
    }
    /**
     * provides unique id values
     */
    private static final AtomicLong counter = new AtomicLong(0);
}