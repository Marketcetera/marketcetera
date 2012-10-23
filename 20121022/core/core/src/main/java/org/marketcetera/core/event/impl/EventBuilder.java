package org.marketcetera.core.event.impl;

import org.marketcetera.core.event.Event;

/* $License$ */

/**
 * Constructs objects of the given type of {@link org.marketcetera.core.event.Event}.
 *
 * @version $Id: EventBuilder.java 16063 2012-01-31 18:21:55Z colin $
 * @since 2.0.0
 */
public interface EventBuilder<E extends Event>
{
    /**
     * Causes the <code>EventBuilder</code> to create an object of the given type.
     * 
     * <p>This method will invoke the object type's validation routine, if any.
     * 
     * @return an <code>E</code> value
     */
    public E create();
}
