package org.marketcetera.quotefeed;

import org.marketcetera.core.ClassVersion;

/**
 * @author graham miller
 * @version $Id$
 */
@ClassVersion("$Id$")
public interface MessageListener {
    public void onQuote(QuoteMessage aQuote);
    public void onQuotes(QuoteMessage [] aQuote);

    public void onTrade(TradeMessage aTrade);
    public void onTrades(TradeMessage [] aTrade);

    public void onAdmin(AdminMessage anAdminMessage);
    public void onAdmins(AdminMessage [] anAdminMessage);
}