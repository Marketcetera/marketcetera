package org.marketcetera.quotefeed;

import java.io.IOException;

import org.marketcetera.core.FeedComponent;
import org.marketcetera.core.MSymbol;
import org.springframework.context.Lifecycle;
import org.springframework.jms.core.JmsOperations;

public interface IQuoteFeed extends FeedComponent, Lifecycle{
    public void listenLevel2(MSymbol symbol);
    public void unlistenLevel2(MSymbol symbol);
    public void listenQuotes(MSymbol symbol);
    public void unlistenQuotes(MSymbol symbol);
    public void listenTrades(MSymbol symbol);
    public void unlistenTrades(MSymbol symbol);
    public void setQuoteJmsOperations(JmsOperations tradeOperations);
    public JmsOperations getQuoteJmsOperations();
    public void setTradeJmsOperations(JmsOperations tradeOperations);
    public JmsOperations getTradeJmsOperations();

}
