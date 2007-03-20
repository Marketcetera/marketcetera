package org.marketcetera.marketdata;

import quickfix.Message;

public interface IMarketDataListener {

	public void onMessage(Message aMessage);
	public void onMessages(Message [] messages);
	
    public void onQuote(Message aQuote);
    public void onQuotes(Message [] messages);

    public void onTrade(Message aTrade);
    public void onTrades(Message [] trades);
    
    public void onLevel2Quote(Message aQuote);
    public void onLevel2Quotes(Message [] quotes);

}
