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
     * <p>
     * Do note that this method returns the reference to the FIX message
     * contained in this object. Any changes to the returned reference
     * affects the contained FIX message.
     * <p>
     * In case the returned message is used from multiple threads, it's
     * recommended that a lock on the <code>this</code> be used to
     * synchronize operations on the returned message.
     *
     * @return a <code>Message</code> value
     */
    public Message getMessage();
}
