package org.marketcetera.ors;

import org.marketcetera.ors.history.ReportHistoryServices;
import org.marketcetera.persist.PersistenceException;
import org.marketcetera.trade.ReportBase;
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

    private final ReportHistoryServices mHistoryServices; 


    // CONSTRUCTORS.

    /**
     * Creates a new persister which relies on the given report
     * history services provider for persistence operations.
     *
     * @param historyServices The report history services provider.
     */    

    public ReplyPersister
        (ReportHistoryServices historyServices)
    {
        mHistoryServices=historyServices;
    }


    // INSTANCE METHODS.

    /**
     * Returns the receiver's report history services provider.
     *
     * @return The provider.
     */

    public ReportHistoryServices getHistoryServices()
    {
        return mHistoryServices;
    }

    /**
     * Persists the given message, which, while doing so, may be
     * modified.
     *
     * @param msg The message.
     */

    public void persistReply
        (TradeMessage msg)
    {
        if (!(msg instanceof ReportBase)) {
            return;
        }
        try {
            getHistoryServices().save((ReportBase)msg);
        } catch (PersistenceException ex) {
            Messages.RP_PERSIST_ERROR.error(this,ex,msg);
            return;
        }
        Messages.RP_PERSISTED_REPLY.info(this,msg);        
    }
}
