package org.marketcetera.photon.marketdata.mock;

import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;

import org.marketcetera.core.NoMoreIDsException;
import org.marketcetera.marketdata.AbstractMarketDataFeed;
import org.marketcetera.marketdata.FeedException;
import org.marketcetera.marketdata.FeedStatus;
import org.marketcetera.marketdata.IMarketDataFeed;
import org.marketcetera.marketdata.MarketDataFeedTokenSpec;
import org.marketcetera.photon.PhotonPlugin;
import org.marketcetera.photon.marketdata.MarketDataFeedService;
import org.osgi.framework.BundleContext;

import quickfix.Message;

public class MockMarketDataFeed extends AbstractMarketDataFeed<MockMarketDataFeedToken, MockMarketDataFeedCredentials, MockMarketDataFeedMessageTranslator, MockMarketDataFeedEventTranslator, Object, MockMarketDataFeed> {
	private static final String SOME_IDENTIFIER = "some identifier";

	private static final String SOME_DSL_IDENTIFIER = "some dsl identifier";

	private static Message messageToSend;

	public MockMarketDataFeed()
			throws NoMoreIDsException, FeedException {
		super(FeedType.SIMULATED, "MOCK", new MockMarketDataFeedCredentials(""));
		setFeedStatus(FeedStatus.AVAILABLE);
	}

	@Override
	protected void doCancel(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected boolean doLogin(MockMarketDataFeedCredentials credentials) {
		return true;
	}

	@Override
	protected void doLogout() {
	}


	@Override
	protected MockMarketDataFeedToken generateToken(
			MarketDataFeedTokenSpec<MockMarketDataFeedCredentials> tokenSpec)
			throws FeedException {
		return new MockMarketDataFeedToken(tokenSpec, this);
	}

	@Override
	protected MockMarketDataFeedEventTranslator getEventTranslator() {
		return new MockMarketDataFeedEventTranslator();
	}

	@Override
	protected MockMarketDataFeedMessageTranslator getMessageTranslator() {
		return new MockMarketDataFeedMessageTranslator();
	}

	@Override
	protected boolean isLoggedIn(MockMarketDataFeedCredentials arg0) {
		return true;
	}

	@Override
	protected void afterDoExecute(MockMarketDataFeedToken inToken) {
		dataReceived(SOME_IDENTIFIER, messageToSend);
	}
	
	@Override
	protected List<String> doMarketDataRequest(Object obj)
			throws FeedException {
		return Arrays.asList(SOME_IDENTIFIER);
	}

	@Override
	protected List<String> doDerivativeSecurityListRequest(Object arg0)
			throws FeedException {
		return Arrays.asList(SOME_DSL_IDENTIFIER);
	}

	
	public void setMessageToSend(Message message){
		messageToSend = message;
	}

	public static MarketDataFeedService<?> registerMockMarketDataFeed() throws FeedException, NoMoreIDsException {
		BundleContext bundleContext = PhotonPlugin.getDefault()
				.getBundleContext();
		MarketDataFeedService<?> feedService = new MarketDataFeedService<MockMarketDataFeedCredentials>(
				new MockMarketDataFeed(), new MockMarketDataFeedCredentials(""));
		bundleContext.registerService(MarketDataFeedService.class.getName(),
				feedService, null);
		return feedService;
	}

	public static MockMarketDataFeed getMockMarketDataFeed(
			MarketDataFeedService<?> feedService) {
		IMarketDataFeed<?,?> feed = feedService.getMarketDataFeed();
		if (!(feed instanceof MockMarketDataFeed)) {
			TestCase.fail("Feed was not a " + MockMarketDataFeed.class
					+ " but was: " + (feed != null ? feed.getClass() : null)
					+ "   Service was: " + feedService);
		}
		return (MockMarketDataFeed) feed;
	}

	@Override
	protected List<String> doSecurityListRequest(Object arg0)
			throws FeedException {
		throw new UnsupportedOperationException("Not yet implemented");
	}

}
