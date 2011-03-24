package org.marketcetera.server.service;

import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Allows a caller to update the status of this {@link OrderDestination}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public interface UpdatableStatus
{
    /**
     * 
     *
     *
     * @param inNewStatus
     */
    public void setStatus(DestinationStatus inNewStatus);
}
