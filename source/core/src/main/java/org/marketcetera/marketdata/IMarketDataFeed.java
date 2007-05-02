package org.marketcetera.marketdata;

import java.util.IllegalFormatException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.marketcetera.core.IFeedComponent;
import org.marketcetera.core.MSymbol;
import org.marketcetera.core.MarketceteraException;
import org.springframework.context.Lifecycle;

import quickfix.Message;



public interface IMarketDataFeed extends IFeedComponent, Lifecycle {

	public ISubscription asyncQuery(Message query) throws MarketceteraException;

	public List<Message> syncQuery(Message query, long timeout, TimeUnit units) throws MarketceteraException, TimeoutException;

	public void asyncUnsubscribe(ISubscription subscription) throws MarketceteraException;
	
	public void setMarketDataListener(IMarketDataListener listener);
    public IMarketDataListener getMarketDataListener();
    
    /**
     * 
     * @param symbolString
     * @return
	 * @throws IllegalFormatException if the format of the symbol is not understood
     */
    public MSymbol symbolFromString(String symbolString);
}
