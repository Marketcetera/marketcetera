package org.marketcetera.photon.internal.marketdata;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItemInArray;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.marketcetera.core.position.impl.BigDecimalMatchers.comparesEqualTo;

import java.math.BigDecimal;
import java.util.Date;
import java.util.EnumSet;
import java.util.Map;
import java.util.concurrent.Executor;

import org.junit.Test;
import org.marketcetera.event.impl.MarketstatEventBuilder;
import org.marketcetera.marketdata.Capability;
import org.marketcetera.marketdata.Content;
import org.marketcetera.marketdata.MarketDataRequest;
import org.marketcetera.module.ModuleManager;
import org.marketcetera.options.ExpirationType;
import org.marketcetera.photon.model.marketdata.impl.MDMarketstatImpl;
import org.marketcetera.trade.Equity;
import org.marketcetera.trade.Instrument;
import org.marketcetera.trade.Option;
import org.marketcetera.trade.OptionType;

/* $License$ */

/**
 * Test {@link SharedOptionMarketstatManager}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 2.0.0
 */
public class SharedOptionMarketstatManagerTest
        extends
        DataFlowManagerTestBase<Map<Option, MDMarketstatImpl>, SharedOptionMarketstatKey> {

    private final SharedOptionMarketstatKey mKey1 = new SharedOptionMarketstatKey(
            new Equity("IBM"));
    private final SharedOptionMarketstatKey mKey2 = new SharedOptionMarketstatKey(
            new Equity("METC"));
    private final SharedOptionMarketstatKey mKey3 = new SharedOptionMarketstatKey(
            new Equity("JAVA"));
    private final Option mOption1a = new Option("IBM", "200901",
            BigDecimal.TEN, OptionType.Put);
    private final Option mOption1b = new Option("IBM", "200901",
            BigDecimal.TEN, OptionType.Call);
    private final Option mOption2a = new Option("METC", "200901",
            BigDecimal.TEN, OptionType.Put);
    private final Option mOption2b = new Option("METC", "200901",
            BigDecimal.TEN, OptionType.Call);
    private final Option mOption3a = new Option("JAVA", "200901",
            BigDecimal.TEN, OptionType.Put);
    private final Option mOption3b = new Option("JAVA", "200901",
            BigDecimal.TEN, OptionType.Call);

    @Override
    protected IDataFlowManager<Map<Option, MDMarketstatImpl>, SharedOptionMarketstatKey> createFixture(
            ModuleManager moduleManager, Executor marketDataExecutor,
            IMarketDataRequestSupport marketDataRequestSupport) {
        SharedOptionMarketstatManager manager = new SharedOptionMarketstatManager(
                moduleManager, marketDataExecutor, marketDataRequestSupport);
        /*
         * The manager doesn't start tracking options until get is called on the
         * map.
         */
        assertNotNull(manager.getItem(mKey1).get(mOption1a));
        assertNotNull(manager.getItem(mKey1).get(mOption1b));
        assertNotNull(manager.getItem(mKey2).get(mOption2a));
        assertNotNull(manager.getItem(mKey2).get(mOption2b));
        assertNotNull(manager.getItem(mKey3).get(mOption3a));
        assertNotNull(manager.getItem(mKey3).get(mOption3b));
        return manager;
    }

    @Override
    protected SharedOptionMarketstatKey createKey1() {
        return mKey1;
    }

    @Override
    protected SharedOptionMarketstatKey createKey2() {
        return mKey2;
    }

    @Override
    protected SharedOptionMarketstatKey createKey3() {
        return mKey3;
    }

    @Override
    protected EnumSet<Capability> getSupportedCapabilities() {
        return EnumSet.of(Capability.MARKET_STAT);
    }

    @Override
    protected void validateInitialConditions(
            Map<Option, MDMarketstatImpl> item, SharedOptionMarketstatKey key) {
        if (key == mKey1) {
            assertTicks(item, mOption1a, mOption1b, null, null, null, null,
                    null, null, null, null);
        } else if (key == mKey2) {
            assertTicks(item, mOption2a, mOption2b, null, null, null, null,
                    null, null, null, null);
        } else if (key == mKey3) {
            assertTicks(item, mOption3a, mOption3b, null, null, null, null,
                    null, null, null, null);
        }
    }

    private void assertTicks(Map<Option, MDMarketstatImpl> item,
            Option option1, Option option2, String closeDate1,
            Integer closePrice1, String previousCloseDate1,
            Integer previousClosePrice1, String closeDate2,
            Integer closePrice2, String previousCloseDate2,
            Integer previousClosePrice2) {
        assertThat(item.size(), is(2));
        /*
         * Use containsKey instead of hasItem matcher since it's a computing map
         * that will create the item when get is called.
         */
        assertThat(item.containsKey(option1), is(true));
        assertThat(item.containsKey(option2), is(true));
        assertTick(item.get(option1), option1, closeDate1, closePrice1,
                previousCloseDate1, previousClosePrice1);
        assertTick(item.get(option2), option2, closeDate2, closePrice2,
                previousCloseDate2, previousClosePrice2);
    }

    private void assertTick(MDMarketstatImpl tick, Option option,
            String closeDate, Integer closePrice, String previousCloseDate,
            Integer previousClosePrice) {
        assertThat(tick.getInstrument(), is((Instrument) option));
        assertThat(tick.getCloseDate(), is(closeDate));
        assertThat(tick.getClosePrice(), comparesEqualTo(closePrice));
        assertThat(tick.getPreviousCloseDate(), is(previousCloseDate));
        assertThat(tick.getPreviousClosePrice(),
                comparesEqualTo(previousClosePrice));
    }

    @Override
    protected Object createEvent1(SharedOptionMarketstatKey key) {
        if (key == mKey1) {
            return createEvent(mOption1a, "d3", 7, "d4", 9);
        } else if (key == mKey2) {
            return createEvent(mOption2a, "d4", 8, "d5", 10);
        } else if (key == mKey3) {
            return createEvent(mOption3a, "d5", 9, "d6", 11);
        } else {
            throw new AssertionError();
        }
    }

    @Override
    protected Object createEvent2(SharedOptionMarketstatKey key) {
        if (key == mKey1) {
            return createEvent(mOption1a, "d1", 5, "d2", 100);
        } else if (key == mKey2) {
            return createEvent(mOption2a, "d2", 6, "d3", 101);
        } else if (key == mKey3) {
            return createEvent(mOption3a, "d3", 7, "d4", 101);
        } else {
            throw new AssertionError();
        }
    }

    @Override
    protected void validateState1(Map<Option, MDMarketstatImpl> item,
            SharedOptionMarketstatKey key) {
        if (key == mKey1) {
            assertTicks(item, mOption1a, mOption1b, "d3", 7, "d4", 9, null,
                    null, null, null);
        } else if (key == mKey2) {
            assertTicks(item, mOption2a, mOption2b, "d4", 8, "d5", 10, null,
                    null, null, null);
        } else if (key == mKey3) {
            assertTicks(item, mOption3a, mOption3b, "d5", 9, "d6", 11, null,
                    null, null, null);
        }
    }

    @Override
    protected void validateState2(Map<Option, MDMarketstatImpl> item,
            SharedOptionMarketstatKey key) {
        if (key == mKey1) {
            assertTicks(item, mOption1a, mOption1b, "d1", 5, "d2", 100, null,
                    null, null, null);
        } else if (key == mKey2) {
            assertTicks(item, mOption2a, mOption2b, "d2", 6, "d3", 101, null,
                    null, null, null);
        } else if (key == mKey3) {
            assertTicks(item, mOption3a, mOption3b, "d3", 7, "d4", 102, null,
                    null, null, null);
        }
    }

    private Object createEvent(Instrument instrument, String closeDate,
            int closePrice, String previousCloseDate, int previousClosePrice) {
        return MarketstatEventBuilder.optionMarketstat().withInstrument(
                instrument).withUnderlyingInstrument(
                new Equity(instrument.getSymbol())).withTimestamp(new Date())
                .withClosePrice(new BigDecimal(closePrice))
                .withPreviousClosePrice(new BigDecimal(previousClosePrice))
                .withCloseDate(closeDate).withPreviousCloseDate(
                        previousCloseDate).withExpirationType(
                        ExpirationType.AMERICAN).create();
    }

    @Override
    protected void validateRequest(SharedOptionMarketstatKey key,
            MarketDataRequest request) {
        assertThat(request.getContent().size(), is(1));
        assertThat(request.getContent(), hasItem(Content.MARKET_STAT));
        assertThat(request.getUnderlyingSymbols().size(), is(1));
        assertThat(request.getUnderlyingSymbols().toArray(new String[0]), hasItemInArray(key
                .getInstrument().getSymbol()));
    }

    @Override
    public void testInvalidSymbolIgnoredAndReported() throws Exception {
        /*
         * Pass, this manager doesn't have a concept of invalid symbol.
         */
    }

    @Test
    public void testSharedFlow() {
        // start flow
        mFixture.startFlow(mKey1);
        // emit an event for each option
        emit(createEvent(mOption1a, "1", 1, "2", 2));
        emit(createEvent(mOption1b, "3", 3, "4", 4));
        assertTicks(mItem1, mOption1a, mOption1b, "1", 1, "2", 2, "3", 3, "4",
                4);
        // finish
        mFixture.stopFlow(mKey1);
    }

    @Test
    public void testRemoveOption() {
        mItem2.remove(mOption2a);
        mFixture.startFlow(mKey2);
        // emit an event for each option
        emit(createEvent(mOption2a, "1", 1, "2", 2));
        emit(createEvent(mOption2b, "3", 3, "4", 4));
        // emit an unrelated one
        emit(createEvent(mOption1a, "5", 5, "6", 6));
        assertThat(mItem2.size(), is(1));
        assertThat(mItem2.containsKey(mOption2a), is(false));
        assertThat(mItem2.containsKey(mOption2b), is(true));
        assertTick(mItem2.get(mOption2b), mOption2b, "3", 3, "4", 4);
        // finish
        mFixture.stopFlow(mKey1);
    }

    @Test
    public void testOptionEquivalence() {
        Option mOption1aWithDay = new Option("IBM", "20090117",
                BigDecimal.TEN, OptionType.Put);
        // start flow
        mFixture.startFlow(mKey1);
        // emit an event for each option
        emit(createEvent(mOption1aWithDay, "d3", 7, "d4", 9));
        assertTick(mItem1.get(mOption1a), mOption1a, "d3", 7, "d4", 9);
        // finish
        mFixture.stopFlow(mKey1);
    }
}
