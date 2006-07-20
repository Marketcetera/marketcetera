package org.marketcetera.quotefeed;

/**
 * @author Graham Miller
 * @version $Id$
 */
public abstract class MessageListenerBase implements MessageListener {

    public void onQuotes(QuoteMessage [] quotes) {
        for (int i = 0; i < quotes.length; i++) {
            QuoteMessage quoteMessage = quotes[i];
            onQuote(quoteMessage);
        }
    }

    public void onTrades(TradeMessage [] trades) {
        for (int i = 0; i < trades.length; i++) {
            TradeMessage trade = trades[i];
            onTrade(trade);
        }
    }

    public void onAdmins(AdminMessage [] adminMessages) {
        for (int i = 0; i < adminMessages.length; i++) {
            AdminMessage adminMessage = adminMessages[i];
            onAdmin(adminMessage);
        }
    }
}
