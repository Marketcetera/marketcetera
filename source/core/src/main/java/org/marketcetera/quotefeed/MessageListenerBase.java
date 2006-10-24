package org.marketcetera.quotefeed;

import org.marketcetera.core.ClassVersion;
import quickfix.Message;

/**
 * @author Graham Miller
 * @version $Id$
 */
@ClassVersion("$Id$")
public abstract class MessageListenerBase implements IMessageListener {

    public void onQuotes(Message [] quotes) {
        for (Message quoteMessage : quotes) {
            onQuote(quoteMessage);
        }
    }

    public void onTrades(Message [] trades) {
        for (Message trade : trades) {
            onTrade(trade);
        }
    }

}
