package org.marketcetera.eventbus.server;

import java.util.UUID;

import org.marketcetera.eventbus.EsperEvent;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/* $License$ */

/**
 * Provides a test Esper event.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class TestEventBean
        implements EsperEvent
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
        builder.append("TestEventBean [id=").append(id).append("]");
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
    private static final long serialVersionUID = -2451794740755514121L;
}