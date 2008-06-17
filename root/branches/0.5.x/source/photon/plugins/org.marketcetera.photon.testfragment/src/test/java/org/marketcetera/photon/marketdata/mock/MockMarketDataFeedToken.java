package org.marketcetera.photon.marketdata.mock;

import org.marketcetera.marketdata.AbstractMarketDataFeedToken;
import org.marketcetera.marketdata.FeedException;
import org.marketcetera.marketdata.MarketDataFeedTokenSpec;

public class MockMarketDataFeedToken extends AbstractMarketDataFeedToken<MockMarketDataFeed, MockMarketDataFeedCredentials> {
	protected MockMarketDataFeedToken(
			MarketDataFeedTokenSpec<MockMarketDataFeedCredentials> tokenSpec,
			MockMarketDataFeed dataFeed) throws FeedException {
		super(tokenSpec, dataFeed);
		setSynchronousPublications(true);
	}
}
