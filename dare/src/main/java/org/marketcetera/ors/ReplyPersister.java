package org.marketcetera.ors;

import org.marketcetera.ors.history.ReportHistoryServices;
import org.marketcetera.ors.history.ReportSavedListener;
import org.marketcetera.persist.PersistenceException;
import org.marketcetera.trade.ExecutionReport;
import org.marketcetera.trade.ReportBase;
import org.marketcetera.trade.TradeMessage;
import org.marketcetera.util.misc.ClassVersion;


/**
 * A persister of trade messages (replies) sent by the ORS to
 * clients. It also handles mapping of messages to actors/viewers, via
 * either replies previously persisted, or via an in-memory cache.
 *
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id: ReplyPersister.java 17266 2017-04-28 14:58:00Z colin $
 */

/* $License$ */

@ClassVersion("$Id: ReplyPersister.java 17266 2017-04-28 14:58:00Z colin $")
public class ReplyPersister
    implements ReportSavedListener
{
    private final ReportHistoryServices mHistoryServices; 
    /**
     * Creates a new persister which relies on the given report
     * history services provider for persistence operations.
     *
     * @param historyServices The report history services provider.
     */
    public ReplyPersister(ReportHistoryServices historyServices)
    {
        mHistoryServices=historyServices;
    }
    @Override
    public void reportSaved(ReportBase report,
                            boolean status)
    {
    }
    /**
     * Returns the receiver's report history services provider.
     *
     * @return The provider.
     */
    private ReportHistoryServices getHistoryServices()
    {
        return mHistoryServices;
    }
    /**
     * Persists the given message, which, while doing so, may be
     * modified. Persistence may be effected synchronously or
     * asynchronously.
     *
     * @param msg The message.
     */

    public void persistReply
        (TradeMessage msg)
    {
        if (!(msg instanceof ReportBase)) {
            return;
        }
        if(msg instanceof ExecutionReport) {
            ReportCache.INSTANCE.cache((ExecutionReport)msg);
        }
        try {
            getHistoryServices().save((ReportBase)msg);
        } catch (PersistenceException ex) {
            Messages.RP_PERSIST_ERROR.error(this,ex,msg);
            return;
        }
    }
    /**
     * Deletes the given message.
     *
     * @param inMessage a <code>ReportBase</code> value
     */
    public void deleteMessage(ReportBase inMessage)
    {
        if(inMessage instanceof ExecutionReport) {
            ReportCache.INSTANCE.clear((ExecutionReport)inMessage);
        }
        try {
            getHistoryServices().delete(inMessage);
        } catch (PersistenceException e) {
            Messages.RP_PERSIST_ERROR.error(this,
                                            e,
                                            inMessage);
        }
    }
}
