package org.marketcetera.ors;

import org.marketcetera.trade.TradeMessage;
import org.marketcetera.trade.ReportBase;
import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.ors.history.ReportHistoryServices;
import org.marketcetera.persist.PersistenceException;

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
    private final ReportHistoryServices mServices; 


    // CONSTRUCTORS.

    public ReplyPersister
        (ReportHistoryServices inServices)
    {
        mServices = inServices;
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
        try {
            mServices.save((ReportBase) msg);
        } catch (Exception e) {
            Messages.RP_PERSIST_ERROR.error(this, e, msg);
            return;
        }

        Messages.RP_PERSISTED_REPLY.info(this,msg);        
    }
}
