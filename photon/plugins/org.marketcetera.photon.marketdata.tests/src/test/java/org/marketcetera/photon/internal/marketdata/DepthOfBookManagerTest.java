package org.marketcetera.photon.internal.marketdata;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.marketcetera.core.position.impl.BigDecimalMatchers.comparesEqualTo;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.Executor;

import org.apache.log4j.Level;
import org.junit.Test;
import org.marketcetera.event.AskEvent;
import org.marketcetera.event.BidEvent;
import org.marketcetera.event.impl.QuoteEventBuilder;
import org.marketcetera.marketdata.Capability;
import org.marketcetera.marketdata.Content;
import org.marketcetera.marketdata.MarketDataRequest;
import org.marketcetera.module.ModuleManager;
import org.marketcetera.photon.model.marketdata.MDDepthOfBook;
import org.marketcetera.photon.model.marketdata.MDQuote;
import org.marketcetera.photon.model.marketdata.impl.MDDepthOfBookImpl;
import org.marketcetera.trade.Equity;
import org.marketcetera.trade.Future;
import org.marketcetera.trade.FutureExpirationMonth;
import org.marketcetera.trade.Instrument;
import org.marketcetera.trade.Option;
import org.marketcetera.trade.OptionType;

/* $License$ */

/**
 * Test {@link LatestTickManager}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.5.0
 */
public class DepthOfBookManagerTest extends
        DataFlowManagerTestBase<MDDepthOfBook, DepthOfBookKey> {

    @Override
    protected IDataFlowManager<MDDepthOfBookImpl, DepthOfBookKey> createFixture(
            ModuleManager moduleManager, Executor marketDataExecutor,
            IMarketDataRequestSupport marketDataRequestSupport) {
        return new DepthOfBookManager(moduleManager,
                getSupportedCapabilities(), marketDataExecutor,
                marketDataRequestSupport);
    }

    @Override
    protected DepthOfBookKey createKey1() {
        return new DepthOfBookKey(new Equity("IBM"), Content.LEVEL_2);
    }

    @Override
	protected DepthOfBookKey createKey2() {
		return new DepthOfBookKey(new Option("YHOO", "20100101", new BigDecimal("1.11"), OptionType.Put), Content.OPEN_BOOK);
	}

    @Override
    protected DepthOfBookKey createKey3() {
        return new DepthOfBookKey(new Equity("JAVA"), Content.TOTAL_VIEW);
    }

    @Override
	protected DepthOfBookKey createKey4() {
		return new DepthOfBookKey(new Future("YHOO", FutureExpirationMonth.DECEMBER, 2014), Content.OPEN_BOOK);
	}
    @Override
    protected EnumSet<Capability> getSupportedCapabilities() {
        return EnumSet.of(Capability.LEVEL_2, Capability.OPEN_BOOK,
                Capability.TOTAL_VIEW);
    }

    @Override
    protected void validateInitialConditions(MDDepthOfBook item,
            DepthOfBookKey key) {
        assertThat(item.getInstrument(), is(key.getInstrument()));
        assertThat(item.getProduct(), is(key.getProduct()));
        assertThat(item.getBids().size(), is(0));
        assertThat(item.getAsks().size(), is(0));
    }

    @Override
    protected Object createEvent1(DepthOfBookKey key) {
        return createAskEvent(key.getInstrument(), "B", 6, 3);
    }

    @Override
    protected Object createEvent2(DepthOfBookKey key) {
        return createBidEvent(key.getInstrument(), "A", 1, 2);
    }

    @Override
    protected void validateState1(MDDepthOfBook item, DepthOfBookKey key) {
        validateQuotes(item.getBids());
        validateQuotes(item.getAsks(), "B", "6", "3");
    }

    @Override
    protected void validateState2(MDDepthOfBook item, DepthOfBookKey key) {
        validateQuotes(item.getBids(), "A", "1", "2");
        validateQuotes(item.getAsks());
    }

    private void validateQuotes(List<MDQuote> quotes, String... expected) {
        // expected is source, price, size
        assertThat(3 * quotes.size(), is(expected.length));
        int i = 0;
        String desc = MessageFormat.format("Quote {0} ", i);
        for (MDQuote quote : quotes) {
            assertThat(desc + "source", quote.getSource(), is(expected[i++]));
            assertThat(desc + "price", quote.getPrice(),
                    comparesEqualTo(expected[i++]));
            assertThat(desc + "size", quote.getSize(),
                    comparesEqualTo(expected[i++]));
        }
    }

    @Override
    protected void validateRequest(DepthOfBookKey key, MarketDataRequest request) {
        assertThat(request.getContent().size(), is(1));
        assertThat(request.getContent(), hasItem(key.getProduct()));
        assertThat(request.getSymbols().size(), is(1));
        assertThat(request.getSymbols(), hasItem(getOsiSymbol(key)));
    }

    @Test
    public void testMultipleEventsLevel2() throws Exception {
        // with level 2, rows get replaced if they have the same symbol-exchange
        mFixture.startFlow(mKey1);
        Instrument instrument = mKey1.getInstrument();
        emit(createBidEvent(instrument, "A", 1, 2));
        emit(createAskEvent(instrument, "C", 10, 10));
        validateQuotes(mItem1.getBids(), "A", "1", "2");
        validateQuotes(mItem1.getAsks(), "C", "10", "10");
        emit(createBidEvent(instrument, "A", 4, 5));
        emit(createBidEvent(instrument, "B", 5, 5));
        emit(createAskEvent(instrument, "B", 6, 3));
        emit(createAskEvent(instrument, "A", 7, 100));
        validateQuotes(mItem1.getBids(), "A", "4", "5", "B", "5", "5");
        validateQuotes(mItem1.getAsks(), "C", "10", "10", "B", "6", "3", "A",
                "7", "100");
    }

    @Test
    public void testMultipleEvents() throws Exception {
        mFixture.startFlow(mKey2);
        Instrument instrument = mKey2.getInstrument();
        emit(createBidEvent(instrument, "A", 1, 2));
        emit(createAskEvent(instrument, "C", 10, 10));
        validateQuotes(mItem2.getBids(), "A", "1", "2");
        validateQuotes(mItem2.getAsks(), "C", "10", "10");
        emit(createBidEvent(instrument, "A", 4, 5));
        emit(createBidEvent(instrument, "B", 5, 5));
        emit(createAskEvent(instrument, "B", 6, 3));
        emit(createAskEvent(instrument, "A", 7, 100));
        validateQuotes(mItem2.getBids(), "A", "1", "2", "A", "4", "5", "B",
                "5", "5");
        validateQuotes(mItem2.getAsks(), "C", "10", "10", "B", "6", "3", "A",
                "7", "100");
    }

    @Test
    public void testChangeAndDelete() throws Exception {
        // change and delete don't apply to level 2, so we use mKey2
        mFixture.startFlow(mKey2);
        Instrument instrument = mKey2.getInstrument();
        BidEvent bid1 = createBidEvent(instrument, "B", 1, 2);
        AskEvent ask1 = createAskEvent(instrument, "A", 3, 4);
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
        emit(QuoteEventBuilder.delete(ask1));
        validateQuotes(mItem2.getBids(), "B", "1", "3");
        validateQuotes(mItem2.getAsks());
        emit(QuoteEventBuilder.delete(bid1));
        validateQuotes(mItem2.getBids());
        validateQuotes(mItem2.getAsks());
    }

    @Test
    public void testLevel2Keys() throws Exception {
        Equity goog = new Equity("GOOG");
        DepthOfBookKey key1 = new DepthOfBookKey(goog, Content.LEVEL_2);
        MDDepthOfBook item1 = mFixture.getItem(key1);
        Equity msft = new Equity("MSFT");
        DepthOfBookKey key2 = new DepthOfBookKey(msft, Content.LEVEL_2);
        MDDepthOfBook item2 = mFixture.getItem(key2);
        mFixture.startFlow(key1);
        emit(createBidEvent(goog, "B", 1, 2));
        emit(createAskEvent(goog, "A", 3, 4));
        validateQuotes(item1.getBids(), "B", "1", "2");
        validateQuotes(item1.getAsks(), "A", "3", "4");
        validateQuotes(item2.getBids());
        validateQuotes(item2.getAsks());
        mFixture.startFlow(key2);
        emit(createBidEvent(msft, "B", 5, 6));
        emit(createAskEvent(msft, "A", 7, 8));
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
        Instrument instrument = mKey2.getInstrument();
        BidEvent bid1 = createBidEvent(instrument, "B", 1, 2);
        AskEvent ask1 = createAskEvent(instrument, "A", 3, 4);
        emit(bid1);
        emit(ask1);
        BidEvent unexpectedBid = createBidEvent(instrument, "B", 1, 2);
        AskEvent unexpectedAsk = createAskEvent(instrument, "A", 3, 4);
        Object unexpected = changeBid(unexpectedBid, 3);
        emit(unexpected);
        assertLastEvent(Level.WARN, mFixture.getClass().getName(),
                Messages.DATA_FLOW_MANAGER_UNEXPECTED_MESSAGE_ID.getText(
                        unexpected, getLastRequest()), null);
        unexpected = changeAsk(unexpectedAsk, 3);
        emit(unexpected);
        assertLastEvent(Level.WARN, mFixture.getClass().getName(),
                Messages.DATA_FLOW_MANAGER_UNEXPECTED_MESSAGE_ID.getText(
                        unexpected, getLastRequest()), null);
        unexpected = QuoteEventBuilder.delete(unexpectedBid);
        emit(unexpected);
        assertLastEvent(Level.WARN, mFixture.getClass().getName(),
                Messages.DATA_FLOW_MANAGER_UNEXPECTED_MESSAGE_ID.getText(
                        unexpected, getLastRequest()), null);
        unexpected = QuoteEventBuilder.delete(unexpectedAsk);
        emit(unexpected);
        assertLastEvent(Level.WARN, mFixture.getClass().getName(),
                Messages.DATA_FLOW_MANAGER_UNEXPECTED_MESSAGE_ID.getText(
                        unexpected, getLastRequest()), null);

    }

    private BidEvent changeBid(BidEvent bid, int size) {
        return QuoteEventBuilder.change(bid, new Date(), new BigDecimal(size));
    }

    private AskEvent changeAsk(AskEvent ask, int size) {
        return QuoteEventBuilder.change(ask, new Date(), new BigDecimal(size));
    }

}
