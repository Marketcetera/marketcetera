package org.marketcetera.quotefeed;

import java.io.IOException;

import org.marketcetera.core.FeedComponent;
import org.marketcetera.core.MSymbol;

public interface IQuoteFeed extends FeedComponent{
    void connect() throws IOException;
    void disconnect();
    public void listenLevel2(MSymbol symbol, IMessageListener list);
    public void unlistenLevel2(MSymbol symbol, IMessageListener list);
    public void listenQuotes(MSymbol symbol, IMessageListener list);
    public void unlistenQuotes(MSymbol symbol, IMessageListener list);
    public void listenTrades(MSymbol symbol, IMessageListener list);
    public void unlistenTrades(MSymbol symbol, IMessageListener list);
}
