package org.marketcetera.quotefeed;

import org.marketcetera.core.ClassVersion;

/**
 * @author Graham Miller
 * @version $Id$
 */
@ClassVersion("$Id$")
public abstract class MessageListenerBase implements MessageListener {

    public void onQuotes(QuoteMessage [] quotes) {
        for (QuoteMessage quoteMessage : quotes) {
            onQuote(quoteMessage);
        }
    }

    public void onTrades(TradeMessage [] trades) {
        for (TradeMessage trade : trades) {
            onTrade(trade);
        }
    }

    public void onAdmins(AdminMessage [] adminMessages) {
        for (AdminMessage adminMessage : adminMessages) {
            onAdmin(adminMessage);
        }
    }
}
