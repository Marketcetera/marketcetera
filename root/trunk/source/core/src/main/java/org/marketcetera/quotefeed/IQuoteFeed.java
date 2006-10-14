package org.marketcetera.quotefeed;

import java.io.IOException;

import org.marketcetera.core.FeedComponent;
import org.marketcetera.core.MSymbol;

public interface IQuoteFeed extends FeedComponent{
	void connect() throws IOException;
	void disconnect();
	public void listenLevel2(MSymbol symbol);
	public void unListenLevel2(MSymbol symbol);
	public void addBookListener(ILevel2Listener listener);
	public void removeBookListener(ILevel2Listener listener);
}
