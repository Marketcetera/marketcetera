package org.marketcetera.marketdata;

import org.marketcetera.core.IFeedComponent;
import org.marketcetera.core.MSymbol;
import org.springframework.context.Lifecycle;

public interface IMarketDataFeed extends IFeedComponent, Lifecycle{

	public void subscribe(IMessageSelector selector);
	public boolean unsubscribe(IMessageSelector selector);
    
    public void setMarketDataListener(IMarketDataListener listener);
    public IMarketDataListener getMarketDataListener();

    public MSymbol symbolFromString(String symbolString);
}
