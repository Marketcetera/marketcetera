package org.marketcetera.eventbus;

import java.io.Serializable;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface EsperEvent
        extends Serializable
{
    String getEventName();
}
