package org.marketcetera.photon.internal.marketdata;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Date;
import java.util.EnumSet;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Level;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.marketcetera.core.ImmediateExecutorService;
import org.marketcetera.event.AskEvent;
import org.marketcetera.event.BidEvent;
import org.marketcetera.event.QuoteAction;
import org.marketcetera.event.impl.QuoteEventBuilder;
import org.marketcetera.marketdata.Capability;
import org.marketcetera.marketdata.DateUtils;
import org.marketcetera.marketdata.FeedStatus;
import org.marketcetera.marketdata.MarketDataRequest;
import org.marketcetera.module.ExpectedFailure;
import org.marketcetera.module.ModuleManager;
import org.marketcetera.module.ModuleURN;
import org.marketcetera.options.ExpirationType;
import org.marketcetera.options.OptionUtils;
import org.marketcetera.photon.internal.marketdata.MockMarketDataModuleFactory.MockMarketDataModule;
import org.marketcetera.photon.marketdata.IMarketDataFeed;
import org.marketcetera.photon.module.ModuleSupport;
import org.marketcetera.photon.test.PhotonTestBase;
import org.marketcetera.trade.Equity;
import org.marketcetera.trade.Instrument;
import org.marketcetera.trade.Option;
import org.marketcetera.util.except.I18NException;

/* $License$ */

/**
 * Base class for {@link DataFlowManager} tests.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id: DataFlowManagerTestBase.java 10842 2009-11-03 22:35:51Z anshul
 *          $
 * @since 1.5.0
 */
public abstract class DataFlowManagerTestBase<T, K extends Key> extends
        PhotonTestBase {

    /**
     * @return the test fixture
     */
    protected abstract IDataFlowManager<? extends T, K> createFixture(
            ModuleManager moduleManager, Executor marketDataExecutor,
            IMarketDataRequestSupport marketDataRequestSupport);

    /**
     * @return a key
     */
    protected abstract K createKey1();

    /**
     * @return another key
     */
    protected abstract K createKey2();

    /**
     * @return a third key
     */
    protected abstract K createKey3();
    
    /**
     * @return a fourth key
     */
    protected abstract K createKey4();

    /**
     * Verify item for given key was initialized properly
     */
    protected abstract void validateInitialConditions(T item, K key);

    /**
     * @return an event to send
     */
    protected abstract Object createEvent1(K key);

    /**
     * @return another event to send
     */
    protected abstract Object createEvent2(K key);

    /**
     * @return capabilities that this feed supports
     */
    protected abstract EnumSet<Capability> getSupportedCapabilities();

    /**
     * Verify item received event returned by {@link #createEvent1(Key)}
     */
    protected abstract void validateState1(T item, K key);

    /**
     * Verify item received event returned by {@link #createEvent2(Key)}
     */
    protected abstract void validateState2(T item, K key);

    /**
     * Verify request is correct for given key
     */
    protected abstract void validateRequest(K key, MarketDataRequest request);

    protected IDataFlowManager<? extends T, K> mFixture;
    protected K mKey1;
    protected T mItem1;
    protected K mKey2;
    protected T mItem2;
    protected MockMarketDataModule mMockMarketDataModule;
    protected K mKey3;
    protected K mKey4;
    protected T mItem4;
    protected AtomicInteger mMessageIds = new AtomicInteger();
    protected IMarketDataFeed mMockModuleFeed;

    @Before
    public void before() throws Exception {
        mMockMarketDataModule = MockMarketDataModuleFactory.sInstance;
        mMockMarketDataModule.setStatus("AVAILABLE");
        Activator.getMarketDataManager().reconnectFeed(
                MockMarketDataModuleFactory.PROVIDER_URN.toString());
        while (Activator.getMarketDataManager().getActiveFeedStatus() != FeedStatus.AVAILABLE) {
            Thread.sleep(250);
        }
        mFixture = createFixture(ModuleSupport.getModuleManager(),
                new ImmediateExecutorService(), new MarketDataRequestSupport(
                        true));
        mMockModuleFeed = createMockModuleFeed(getSupportedCapabilities());
        mFixture.setSourceFeed(mMockModuleFeed);
        mKey1 = createKey1();
        mItem1 = mFixture.getItem(mKey1);
        validateInitialConditions(mItem1, mKey1);
        mKey2 = createKey2();
        mItem2 = mFixture.getItem(mKey2);
        validateInitialConditions(mItem2, mKey2);
        mKey4 = createKey4();
        mItem4 = mFixture.getItem(mKey4);
        validateInitialConditions(mItem4, mKey4);
        mKey3 = createKey3();
        setLevel(mFixture.getClass().getName(), Level.ALL);
    }

    private IMarketDataFeed createMockModuleFeed(
            EnumSet<Capability> capabilities) throws I18NException {
        return createMockFeed(MockMarketDataModuleFactory.INSTANCE_URN,
                capabilities);
    }

    private IMarketDataFeed createMockFeed(ModuleURN urn,
            EnumSet<Capability> capabilities) throws I18NException {
        IMarketDataFeed mock = mock(IMarketDataFeed.class);
        when(mock.getURN()).thenReturn(urn);
        when(mock.getCapabilities()).thenReturn(capabilities);
        return mock;
    }

    @After
    public void after() {
        if (mFixture != null) {
            if (mKey1 != null)
                mFixture.stopFlow(mKey1);
            if (mKey2 != null)
                mFixture.stopFlow(mKey2);
            if (mKey3 != null)
                mFixture.stopFlow(mKey3);
        }
    }

    @Test
    public void testNullParameters() throws Exception {
        new ExpectedFailure<IllegalArgumentException>() {
            @Override
            protected void run() throws Exception {
                createFixture(null, mock(Executor.class),
                        new MarketDataRequestSupport(true));
            }
        };
        new ExpectedFailure<IllegalArgumentException>() {
            @Override
            protected void run() throws Exception {
                createFixture(ModuleSupport.getModuleManager(), null,
                        new MarketDataRequestSupport(true));
            }
        };
        new ExpectedFailure<IllegalArgumentException>() {
            @Override
            protected void run() throws Exception {
                createFixture(ModuleSupport.getModuleManager(),
                        mock(Executor.class), null);
            }
        };
    }

    @Test
    public void testSuccessiveStartsAndStops() {
        // stop before start, make sure nothing bad happens
        mFixture.stopFlow(mKey1);
        // start
        mFixture.startFlow(mKey1);
        // try again, make sure nothing bad happens
        mFixture.startFlow(mKey1);
        // stop
        mFixture.stopFlow(mKey1);
        // try again, make sure nothing bad happens
        mFixture.stopFlow(mKey1);
        // start again
        mFixture.startFlow(mKey1);
    }

    @Test
    public void testDataOnlyReceivedWhenStarted() {
        // emit first event
        emit(createEvent1(mKey1));
        // item should not have changed
        validateInitialConditions(mItem1, mKey1);

        // start flow
        mFixture.startFlow(mKey1);
        // emit second event
        emit(createEvent2(mKey1));
        // item should have changed
        validateState2(mItem1, mKey1);

        // stop flow
        mFixture.stopFlow(mKey1);
        // data should be reset
        validateInitialConditions(mItem1, mKey1);
        // emit first event
        emit(createEvent1(mKey1));
        // item should not have changed
        validateInitialConditions(mItem1, mKey1);

        // start again
        mFixture.startFlow(mKey1);
        // emit first event again
        emit(createEvent1(mKey1));
        // should get it this time
        validateState1(mItem1, mKey1);

        // finish
        mFixture.stopFlow(mKey1);
    }

    @Test
    public void testDataStopsWhenSourceSetToNull() {
        // start flow
        mFixture.startFlow(mKey1);
        // emit second event
        emit(createEvent2(mKey1));
        // item should have changed
        validateState2(mItem1, mKey1);

        // null out the source
        mFixture.setSourceFeed(null);
        // item should have been reset
        validateInitialConditions(mItem1, mKey1);
        // emit first event
        emit(createEvent1(mKey1));
        // item should not have changed
        validateInitialConditions(mItem1, mKey1);

        // set the source back
        mFixture.setSourceFeed(mMockModuleFeed);
        // item should still be reset
        validateInitialConditions(mItem1, mKey1);
        // emit first event again
        emit(createEvent1(mKey1));
        // should get it this time
        validateState1(mItem1, mKey1);

        // finish
        mFixture.stopFlow(mKey1);
    }

    @Test
    public void testSeparateDataRequests() {
        mFixture.startFlow(mKey1);
        validateRequest(mKey1);
        mFixture.startFlow(mKey2);
        validateRequest(mKey2);
        mFixture.startFlow(mKey3);
        validateRequest(mKey3);
    }

    @Test
    public void testUnexpectedDataReported() throws Exception {
        setLevel(mFixture.getClass().getName(), Level.WARN);
        mFixture.startFlow(mKey1);
        Object unexpected = new Object();
        emit(unexpected);
        assertLastEvent(Level.WARN, mFixture.getClass().getName(),
                Messages.DATA_FLOW_MANAGER_UNEXPECTED_DATA.getText(unexpected,
                        getLastRequest()), null);
    }

    @Test
    public void testInvalidSymbolIgnoredAndReported() throws Exception {
        setLevel(mFixture.getClass().getName(), Level.WARN);
        mFixture.startFlow(mKey1);
        validateInitialConditions(mItem1, mKey1);
        // emit data for wrong key
        emit(createEvent1(mKey2));
        validateInitialConditions(mItem1, mKey1);
        assertLastEvent(Level.WARN, mFixture.getClass().getName(),
                Messages.DATA_FLOW_MANAGER_EVENT_INSTRUMENT_MISMATCH.getText(
                        mKey2.getInstrument(), mKey1.getInstrument()), null);
    }

    @Test
    public void testStartFlowBeforeGetItem() throws Exception {
        mFixture.startFlow(mKey3);
        emit(createEvent1(mKey3));
        validateState1(mFixture.getItem(mKey3), mKey3);
    }

    @Test
    public void nullKeys() throws Exception {
        new ExpectedFailure<IllegalArgumentException>() {
            @Override
            protected void run() throws Exception {
                mFixture.getItem(null);
            }
        };
        new ExpectedFailure<IllegalArgumentException>() {
            @Override
            protected void run() throws Exception {
                mFixture.startFlow(null);
            }
        };
        new ExpectedFailure<IllegalArgumentException>() {
            @Override
            protected void run() throws Exception {
                mFixture.stopFlow(null);
            }
        };
    }

    @Test
    public void testFeedDoesNotRequestUnsupportedData() throws Exception {
        EnumSet<Capability> supported = getSupportedCapabilities();
        EnumSet<Capability> unsupported = EnumSet.complementOf(supported);
        unsupported.remove(Capability.UNKNOWN);
        // start flow
        mFixture.startFlow(mKey1);

        for (Capability capability : unsupported) {
            mFixture.setSourceFeed(mMockModuleFeed);
            // item should still be reset
            validateInitialConditions(mItem1, mKey1);
            // emit second event
            emit(createEvent2(mKey1));
            // item should have changed
            validateState2(mItem1, mKey1);
            // set unsupported source, bogus urn is okay since the feed should
            // never be contacted
            mFixture.setSourceFeed(createMockFeed(new ModuleURN(
                    "abc:abc:abc:abc"), EnumSet.of(capability)));
            // item should have been reset
            validateInitialConditions(mItem1, mKey1);
            // emit first event
            emit(createEvent1(mKey1));
            // item should not have changed
            validateInitialConditions(mItem1, mKey1);
        }
        // make sure a feed with some unsupported capabilities works
        mFixture.setSourceFeed(createMockModuleFeed(EnumSet
                .complementOf(EnumSet.of(Capability.UNKNOWN))));
        // item should still be reset
        validateInitialConditions(mItem1, mKey1);
        // emit first event again
        emit(createEvent1(mKey1));
        // should get it this time
        validateState1(mItem1, mKey1);

        // finish
        mFixture.stopFlow(mKey1);
    }

    private void validateRequest(K key) {
        MarketDataRequest request = getLastRequest();
        validateRequest(key, request);
    }

    protected MarketDataRequest getLastRequest() {
        return (MarketDataRequest) mMockMarketDataModule.getLastRequest()
                .getData();
    }

    protected void emit(Object object) {
        mMockMarketDataModule.emitData(object);
    }

    protected AskEvent createAskEvent(Instrument instrument, String exchange,
            int price, int size) {
        QuoteEventBuilder<AskEvent> builder = QuoteEventBuilder.askEvent(
                instrument).withMessageId(mMessageIds.incrementAndGet())
                .withTimestamp(new Date()).withExchange(exchange).withPrice(
                        new BigDecimal(price)).withSize(new BigDecimal(size))
                .withAction(QuoteAction.ADD).withQuoteDate(
                        DateUtils.dateToString(new Date()));
        if (instrument instanceof Option) {
            builder = builder.withUnderlyingInstrument(
                    new Equity(instrument.getSymbol())).withExpirationType(
                    ExpirationType.AMERICAN);
        }
        return builder.create();
    }

    protected BidEvent createBidEvent(Instrument instrument, String exchange,
            int price, int size) {
        QuoteEventBuilder<BidEvent> builder = QuoteEventBuilder.bidEvent(instrument).withMessageId(
                        mMessageIds.incrementAndGet()).withTimestamp(new Date())
                        .withExchange(exchange).withPrice(
                                new BigDecimal(price)).withSize(new BigDecimal(size))
                        .withAction(QuoteAction.ADD).withQuoteDate(
                                DateUtils.dateToString(new Date()));
        if (instrument instanceof Option) {
            builder = builder.withUnderlyingInstrument(
                    new Equity(instrument.getSymbol())).withExpirationType(
                    ExpirationType.AMERICAN);
        }
        return builder.create();
    }

    protected String getOsiSymbol(Key key) {
        Instrument instrument = key.getInstrument();
        String symbol = instrument.getSymbol();
        if (instrument instanceof Option) {
            symbol = OptionUtils.getOsiSymbolFromOption((Option) instrument);
        }
        return symbol;
    }
}
