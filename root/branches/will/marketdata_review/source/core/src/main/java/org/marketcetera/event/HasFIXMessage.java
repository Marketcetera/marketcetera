package org.marketcetera.event;

import org.marketcetera.core.ClassVersion;

import quickfix.Message;

/* $License$ */

/**
 * Indicates that the implementer has a FIX message.
 * 
 * <p>Implementers that contain multiple FIX messages must decide which represents its <em>default</em> message.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 0.6.0
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public interface HasFIXMessage
{
    /**
     * Retrieves the FIX message associated with this object.
     *
     * @return a <code>Message</code> value
     */
    public Message getMessage();
}
