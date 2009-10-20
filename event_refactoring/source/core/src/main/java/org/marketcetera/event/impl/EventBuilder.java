package org.marketcetera.event.impl;

import org.marketcetera.event.Event;


/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface EventBuilder<E extends Event>
{
    public E create();
}
