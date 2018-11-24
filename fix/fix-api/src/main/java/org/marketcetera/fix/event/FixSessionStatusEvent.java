package org.marketcetera.fix.event;

import org.marketcetera.fix.HasFixSessionStatus;

/* $License$ */

/**
 * Indicates that the status of a FIX session has changed.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface FixSessionStatusEvent
        extends HasFixSessionStatus
{
}
