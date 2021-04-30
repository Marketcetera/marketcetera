package org.marketcetera.eventbus.server.esper;

/* $License$ */

/**
 * Indicates the implementor has an id value.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface HasId
{
    /**
     * Get the id value.
     *
     * @return a <code>String</code> value
     */
    String getId();
}
