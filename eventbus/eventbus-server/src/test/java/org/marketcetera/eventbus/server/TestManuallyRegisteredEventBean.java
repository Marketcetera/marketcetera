package org.marketcetera.eventbus.server;

import java.util.UUID;

import org.marketcetera.eventbus.EsperEvent;

/* $License$ */

/**
 * Provides a test Esper event.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class TestManuallyRegisteredEventBean
        implements EsperEvent, HasId
{
    /* (non-Javadoc)
     * @see org.marketcetera.eventbus.EsperEvent#getEventName()
     */
    @Override
    public String getEventName()
    {
        return getClass().getSimpleName();
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("TestManuallyRegisteredEventBean [id=").append(id).append("]");
        return builder.toString();
    }
    /**
     * Get the id value.
     *
     * @return a <code>String</code> value
     */
    public String getId()
    {
        return id;
    }
    /**
     * uniquely identifies an event
     */
    private final String id = UUID.randomUUID().toString();
    private static final long serialVersionUID = -227089668872938854L;
}
