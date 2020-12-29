package org.marketcetera.eventbus;

/* $License$ */

/**
 * Indicates that the implementor has an {@link EsperEvent} value.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface HasEsperEvent
{
    /**
     * Get the Esper event value.
     *
     * @return an <code>EsperEvent</code> value
     */
    EsperEvent getEsperEvent();
}
