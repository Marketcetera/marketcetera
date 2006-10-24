package org.marketcetera.quotefeed;

import org.marketcetera.core.ClassVersion;
import quickfix.Message;

/**
 * @author graham miller
 * @version $Id$
 */
@ClassVersion("$Id$")
public interface IMessageListener {
    // MarketDataSnapshotFullRefresh
    public void onQuote(Message aQuote);
    public void onQuotes(Message [] aQuote);

    public void onTrade(Message aTrade);
    public void onTrades(Message [] aTrade);

}
