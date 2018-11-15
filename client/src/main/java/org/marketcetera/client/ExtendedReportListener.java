package org.marketcetera.client;

import org.marketcetera.trade.FIXResponse;

/* $License$ */

/**
 * Clients that implement this interface can receive FIX messages that are not covered by {@link ReportListener}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface ExtendedReportListener
        extends ReportListener
{
    /**
     * Receives a FIX message.
     *
     * @param inReport a <code>FIXReponse</code> value
     */
    void receiveFixResponse(FIXResponse inReport);
}
