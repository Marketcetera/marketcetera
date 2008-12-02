package org.marketcetera.ors;

import org.marketcetera.trade.TradeMessage;
import org.marketcetera.util.misc.ClassVersion;

/**
 * A persister of trade messages (replies) sent by the ORS to clients.
 *
 * @author tlerios@marketcetera.com
 * @since $Release$
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$") //$NON-NLS-1$
public class ReplyPersister
{

    // INSTANCE DATA.
    // Add system model class as instance 


    // CONSTRUCTORS.

    public ReplyPersister
        (/* Add system model. */)
    {
        // Retain system model
    }


    // INSTANCE METHODS.

    /**
     * Persists the given message, which, while doing so, may be
     * modified.
     *
     * @param msg The message.
     */

    public void persistReply
        (TradeMessage msg)
    {
        // Persist reply.

        Messages.RP_PERSISTED_REPLY.info(this,msg);        
    }
}
