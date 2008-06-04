package org.marketcetera.bogusfeed;

import org.marketcetera.marketdata.AbstractMarketDataFeedCredentials;
import org.marketcetera.marketdata.FeedException;

public class BogusFeedCredentials
	extends AbstractMarketDataFeedCredentials
{
	protected BogusFeedCredentials() 
		throws FeedException 
	{
		super("http://bogusurl");
	}
}