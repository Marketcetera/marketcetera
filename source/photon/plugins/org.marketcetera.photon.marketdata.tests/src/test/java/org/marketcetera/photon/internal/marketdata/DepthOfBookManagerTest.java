package org.marketcetera.photon.internal.marketdata;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItemInArray;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.marketcetera.core.position.impl.BigDecimalMatchers.comparesEqualTo;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.Executor;

import org.apache.log4j.Level;
import org.junit.Test;
import org.marketcetera.event.AskEvent;
import org.marketcetera.event.BidEvent;
import org.marketcetera.marketdata.Capability;
import org.marketcetera.marketdata.MarketDataRequest;
import org.marketcetera.marketdata.MarketDataRequest.Content;
import org.marketcetera.module.ModuleManager;
import org.marketcetera.photon.model.marketdata.MDDepthOfBook;
import org.marketcetera.photon.model.marketdata.MDQuote;
import org.marketcetera.photon.model.marketdata.impl.MDDepthOfBookImpl;

/* $License$ */

/**
 * Test {@link LatestTickManager}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.5.0
 */
public class DepthOfBookManagerTest extends DataFlowManagerTestBase<MDDepthOfBook, DepthOfBookKey> {

	@Override
	protected IDataFlowManager<MDDepthOfBookImpl, DepthOfBookKey> createFixture(
			ModuleManager moduleManager, Executor marketDataExecutor) {
		return new DepthOfBookManager(moduleManager, getSupportedCapabilities(), marketDataExecutor);
	}

	@Override
	protected DepthOfBookKey createKey1() {
		return new DepthOfBookKey("IBM", Content.LEVEL_2);
	}

	@Override
	protected DepthOfBookKey createKey2() {
		return new DepthOfBookKey("METC", Content.OPEN_BOOK);
	}

	@Override
	protected DepthOfBookKey createKey3() {
		return new DepthOfBookKey("JAVA", Content.TOTAL_VIEW);
	}

	@Override
	protected EnumSet<Capability> getSupportedCapabilities() {
		return EnumSet.of(Capability.LEVEL_2, Capability.OPEN_BOOK, Capability.TOTAL_VIEW);
	}

	@Override
	protected void validateInitialConditions(MDDepthOfBook item, DepthOfBookKey key) {
		assertThat(item.getSymbol(), is(key.getSymbol()));
		assertThat(item.getProduct(), is(key.getProduct()));
		assertThat(item.getBids().size(), is(0));
		assertThat(item.getAsks().size(), is(0));
	}

	@Override
	protected Object createEvent1(DepthOfBookKey key) {
		return createAskEvent(key.getSymbol(), "B", 6, 3);
	}

	@Override
	protected Object createEvent2(DepthOfBookKey key) {
		return createBidEvent(key.getSymbol(), "A", 1, 2);
	}

	@Override
	protected void validateState1(MDDepthOfBook item) {
		validateQuotes(item.getBids());
		validateQuotes(item.getAsks(), "B", "6", "3");
	}

	@Override
	protected void validateState2(MDDepthOfBook item) {
		validateQuotes(item.getBids(), "A", "1", "2");
		validateQuotes(item.getAsks());
	}
	
	private void validateQuotes(List<MDQuote> quotes, String... expected) {
		// expected is source, price, size
		assertThat(3*quotes.size(), is(expected.length));
		int i=0;
		String desc = MessageFormat.format("Quote {0} ", i);
		for (MDQuote quote : quotes) {
			assertThat(desc + "source", quote.getSource(), is(expected[i++]));
			assertThat(desc + "price", quote.getPrice(), comparesEqualTo(expected[i++]));
			assertThat(desc + "size", quote.getSize(), comparesEqualTo(expected[i++]));
		}
	}

	@Override
	protected void validateRequest(DepthOfBookKey key, MarketDataRequest request) {
		assertThat(request.getContent().size(), is(1));
		assertThat(request.getContent(), hasItem(key.getProduct()));
		assertThat(request.getSymbols().length, is(1));
		assertThat(request.getSymbols(), hasItemInArray(key.getSymbol()));
	}
	
	@Test
	public void testMultipleEventsLevel2() throws Exception {
		// with level 2, rows get replaced if they have the same symbol-exchange
		mFixture.startFlow(mKey1);
		String symbol = mKey1.getSymbol();
		emit(createBidEvent(symbol, "A", 1, 2));
		emit(createAskEvent(symbol, "C", 10, 10));
		validateQuotes(mItem1.getBids(), "A", "1", "2");
		validateQuotes(mItem1.getAsks(), "C", "10", "10");
		emit(createBidEvent(symbol, "A", 4, 5));
		emit(createBidEvent(symbol, "B", 5, 5));
		emit(createAskEvent(symbol, "B", 6, 3));
		emit(createAskEvent(symbol, "A", 7, 100));
		validateQuotes(mItem1.getBids(), "A", "4", "5", "B", "5", "5");
		validateQuotes(mItem1.getAsks(), "C", "10", "10", "B", "6", "3", "A", "7", "100");
	}
	
	@Test
	public void testMultipleEvents() throws Exception {
		mFixture.startFlow(mKey2);
		String symbol = mKey2.getSymbol();
		emit(createBidEvent(symbol, "A", 1, 2));
		emit(createAskEvent(symbol, "C", 10, 10));
		validateQuotes(mItem2.getBids(), "A", "1", "2");
		validateQuotes(mItem2.getAsks(), "C", "10", "10");
		emit(createBidEvent(symbol, "A", 4, 5));
		emit(createBidEvent(symbol, "B", 5, 5));
		emit(createAskEvent(symbol, "B", 6, 3));
		emit(createAskEvent(symbol, "A", 7, 100));
		validateQuotes(mItem2.getBids(), "A", "1", "2", "A", "4", "5", "B", "5", "5");
		validateQuotes(mItem2.getAsks(), "C", "10", "10", "B", "6", "3", "A", "7", "100");
	}
	
	@Test
	public void testChangeAndDelete() throws Exception {
		// change and delete don't apply to level 2, so we use mKey2
		mFixture.startFlow(mKey2);
		String symbol = mKey2.getSymbol();
		BidEvent bid1 = createBidEvent(symbol, "B", 1, 2);
		AskEvent ask1 = createAskEvent(symbol, "A", 3, 4);
		emit(bid1);
		validateQuotes(mItem2.getBids(), "B", "1", "2");
		validateQuotes(mItem2.getAsks());
		emit(ask1);
		validateQuotes(mItem2.getBids(), "B", "1", "2");
		validateQuotes(mItem2.getAsks(), "A", "3", "4");
		emit(changeBid(bid1, 3));
		validateQuotes(mItem2.getBids(), "B", "1", "3");
		validateQuotes(mItem2.getAsks(), "A", "3", "4");
		emit(changeAsk(ask1, 6));
		validateQuotes(mItem2.getBids(), "B", "1", "3");
		validateQuotes(mItem2.getAsks(), "A", "3", "6");
		emit(AskEvent.deleteEvent(ask1));
		validateQuotes(mItem2.getBids(), "B", "1", "3");
		validateQuotes(mItem2.getAsks());
		emit(BidEvent.deleteEvent(bid1));
		validateQuotes(mItem2.getBids());
		validateQuotes(mItem2.getAsks());
	}
	
	@Test
	public void testLevel2Keys() throws Exception {
		DepthOfBookKey key1 = new DepthOfBookKey("GOOG", Content.LEVEL_2);
		MDDepthOfBook item1 = mFixture.getItem(key1);
		DepthOfBookKey key2 = new DepthOfBookKey("MSFT", Content.LEVEL_2);
		MDDepthOfBook item2 = mFixture.getItem(key2);
		mFixture.startFlow(key1);
		emit(createBidEvent("GOOG", "B", 1, 2));
		emit(createAskEvent("GOOG", "A", 3, 4));
		validateQuotes(item1.getBids(), "B", "1", "2");
		validateQuotes(item1.getAsks(), "A", "3", "4");
		validateQuotes(item2.getBids());
		validateQuotes(item2.getAsks());
		mFixture.startFlow(key2);
		emit(createBidEvent("MSFT", "B", 5, 6));
		emit(createAskEvent("MSFT", "A", 7, 8));
		validateQuotes(item1.getBids(), "B", "1", "2");
		validateQuotes(item1.getAsks(), "A", "3", "4");
		validateQuotes(item2.getBids(), "B", "5", "6");
		validateQuotes(item2.getAsks(), "A", "7", "8");
		mFixture.stopFlow(key1);
		mFixture.stopFlow(key2);
	}
	
	@Test
	public void testUnexpectedId() throws Exception {
		setLevel(mFixture.getClass().getName(), Level.WARN);
		mFixture.startFlow(mKey2);
		String symbol = mKey2.getSymbol();
		BidEvent bid1 = createBidEvent(symbol, "B", 1, 2);
		AskEvent ask1 = createAskEvent(symbol, "A", 3, 4);
		emit(bid1);
		emit(ask1);
		BidEvent unexpectedBid = createBidEvent(symbol, "B", 1, 2);
		AskEvent unexpectedAsk = createAskEvent(symbol, "A", 3, 4);
		Object unexpected = changeBid(unexpectedBid, 3);
		emit(unexpected);
		assertLastEvent(Level.WARN, mFixture.getClass().getName(),
				Messages.DATA_FLOW_MANAGER_UNEXPECTED_MESSAGE_ID.getText(unexpected, getLastRequest()), null);
		unexpected = changeAsk(unexpectedAsk, 3);
		emit(unexpected);
		assertLastEvent(Level.WARN, mFixture.getClass().getName(),
				Messages.DATA_FLOW_MANAGER_UNEXPECTED_MESSAGE_ID.getText(unexpected, getLastRequest()), null);
		unexpected = BidEvent.deleteEvent(unexpectedBid);
		emit(unexpected);
		assertLastEvent(Level.WARN, mFixture.getClass().getName(),
				Messages.DATA_FLOW_MANAGER_UNEXPECTED_MESSAGE_ID.getText(unexpected, getLastRequest()), null);
		unexpected = AskEvent.deleteEvent(unexpectedAsk);
		emit(unexpected);
		assertLastEvent(Level.WARN, mFixture.getClass().getName(),
				Messages.DATA_FLOW_MANAGER_UNEXPECTED_MESSAGE_ID.getText(unexpected, getLastRequest()), null);
		
	}

	private BidEvent changeBid(BidEvent bid, int size) {
		return BidEvent.changeEvent(bid, System.currentTimeMillis(), new BigDecimal(size));
	}

	private AskEvent changeAsk(AskEvent ask, int size) {
		return AskEvent.changeEvent(ask, System.currentTimeMillis(), new BigDecimal(size));
	}

}
