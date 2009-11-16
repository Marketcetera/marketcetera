package org.marketcetera.photon.internal.marketdata;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItemInArray;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.marketcetera.core.position.impl.BigDecimalMatchers.comparesEqualTo;

import java.math.BigDecimal;
import java.util.EnumSet;
import java.util.Map;
import java.util.concurrent.Executor;

import org.junit.Test;
import org.marketcetera.event.impl.TradeEventBuilder;
import org.marketcetera.marketdata.Capability;
import org.marketcetera.marketdata.MarketDataRequest;
import org.marketcetera.marketdata.MarketDataRequest.Content;
import org.marketcetera.module.ModuleManager;
import org.marketcetera.options.ExpirationType;
import org.marketcetera.photon.model.marketdata.MDLatestTick;
import org.marketcetera.photon.model.marketdata.impl.MDLatestTickImpl;
import org.marketcetera.trade.Equity;
import org.marketcetera.trade.Instrument;
import org.marketcetera.trade.Option;
import org.marketcetera.trade.OptionType;

/* $License$ */

/**
 * Test {@link SharedOptionLatestTickManager}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
public class SharedOptionLatestTickManagerTest
        extends
        DataFlowManagerTestBase<Map<Option, MDLatestTickImpl>, SharedOptionLatestTickKey> {

    private final SharedOptionLatestTickKey mKey1 = new SharedOptionLatestTickKey(
            new Equity("IBM"));
    private final SharedOptionLatestTickKey mKey2 = new SharedOptionLatestTickKey(
            new Equity("METC"));
    private final SharedOptionLatestTickKey mKey3 = new SharedOptionLatestTickKey(
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
    protected IDataFlowManager<Map<Option, MDLatestTickImpl>, SharedOptionLatestTickKey> createFixture(
            ModuleManager moduleManager, Executor marketDataExecutor, IMarketDataRequestSupport marketDataRequestSupport) {
        SharedOptionLatestTickManager manager = new SharedOptionLatestTickManager(
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
    protected SharedOptionLatestTickKey createKey1() {
        return mKey1;
    }

    @Override
    protected SharedOptionLatestTickKey createKey2() {
        return mKey2;
    }

    @Override
    protected SharedOptionLatestTickKey createKey3() {
        return mKey3;
    }

    @Override
    protected EnumSet<Capability> getSupportedCapabilities() {
        return EnumSet.of(Capability.LATEST_TICK);
    }

    @Override
    protected void validateInitialConditions(
            Map<Option, MDLatestTickImpl> item, SharedOptionLatestTickKey key) {
        if (key == mKey1) {
            assertTicks(item, mOption1a, mOption1b, null, null, null, null, null, null);
        } else if (key == mKey2) {
            assertTicks(item, mOption2a, mOption2b, null, null, null, null, null, null);
        } else if (key == mKey3) {
            assertTicks(item, mOption3a, mOption3b, null, null, null, null, null, null);
        }
    }

    private void assertTicks(Map<Option, MDLatestTickImpl> item,
            Option option1, Option option2, Integer price1, Integer size1,
            Integer multiplier1, Integer price2, Integer size2, Integer multiplier2) {
        assertThat(item.size(), is(2));
        /*
         * Use containsKey instead of hasItem matcher since it's a computing map
         * that will create the item when get is called.
         */
        assertThat(item.containsKey(option1), is(true));
        assertThat(item.containsKey(option2), is(true));
        assertTick(item.get(option1), option1, price1, size1, multiplier1);
        assertTick(item.get(option2), option2, price2, size2, multiplier2);
    }

    private void assertTick(MDLatestTick tick, Option option, Integer price,
            Integer size, Integer multiplier) {
        assertThat(tick.getInstrument(), is((Instrument) option));
        assertThat(tick.getPrice(), comparesEqualTo(price));
        assertThat(tick.getSize(), comparesEqualTo(size));
        assertThat(tick.getMultiplier(), comparesEqualTo(multiplier));
    }

    @Override
    protected Object createEvent1(SharedOptionLatestTickKey key) {
        if (key == mKey1) {
            return createEvent(mOption1a, 1, 6, 2);
        } else if (key == mKey2) {
            return createEvent(mOption2a, 1, 6, 2);
        } else if (key == mKey3) {
            return createEvent(mOption3a, 1, 6, 2);
        } else {
            throw new AssertionError();
        }
    }

    @Override
    protected Object createEvent2(SharedOptionLatestTickKey key) {
        if (key == mKey1) {
            return createEvent(mOption1a, 10, 7, 4);
        } else if (key == mKey2) {
            return createEvent(mOption2a, 10, 7, 4);
        } else if (key == mKey3) {
            return createEvent(mOption3a, 10, 7, 4);
        } else {
            throw new AssertionError();
        }
    }

    @Override
    protected void validateState1(Map<Option, MDLatestTickImpl> item,
            SharedOptionLatestTickKey key) {
        if (key == mKey1) {
            assertTicks(item, mOption1a, mOption1b, 1, 6, 2, null, null, null);
        } else if (key == mKey2) {
            assertTicks(item, mOption2a, mOption2b, 1, 6, 2, null, null, null);
        } else if (key == mKey3) {
            assertTicks(item, mOption3a, mOption3b, 1, 6, 2, null, null, null);
        }
    }

    @Override
    protected void validateState2(Map<Option, MDLatestTickImpl> item,
            SharedOptionLatestTickKey key) {
        if (key == mKey1) {
            assertTicks(item, mOption1a, mOption1b, 10, 7, 4, null, null, null);
        } else if (key == mKey2) {
            assertTicks(item, mOption2a, mOption2b, 10, 7, 4, null, null, null);
        } else if (key == mKey3) {
            assertTicks(item, mOption3a, mOption3b, 10, 7, 4, null, null, null);
        }
    }

    private Object createEvent(Instrument instrument, int price, int size,
            int multiplier) {
        return TradeEventBuilder.optionTradeEvent().withInstrument(instrument)
                .withUnderlyingInstrument(new Equity(instrument.getSymbol()))
                .withExpirationType(ExpirationType.AMERICAN).withExchange("Q")
                .withTradeDate("bogus").withPrice(new BigDecimal(price))
                .withSize(new BigDecimal(size)).withMultiplier(
                        new BigDecimal(multiplier)).create();
    }

    @Override
    protected void validateRequest(SharedOptionLatestTickKey key,
            MarketDataRequest request) {
        assertThat(request.getContent().size(), is(1));
        assertThat(request.getContent(), hasItem(Content.LATEST_TICK));
        assertThat(request.getUnderlyingSymbols().length, is(1));
        assertThat(request.getUnderlyingSymbols(), hasItemInArray(key
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
        emit(createEvent(mOption1a, 10, 7, 4));
        emit(createEvent(mOption1b, 1, 6, 12));
        assertTicks(mItem1, mOption1a, mOption1b, 10, 7, 4, 1, 6, 12);
        // finish
        mFixture.stopFlow(mKey1);
    }

    @Test
    public void testRemoveOption() {
        mItem2.remove(mOption2a);
        mFixture.startFlow(mKey2);
        // emit an event for each option
        emit(createEvent(mOption2a, 10, 7, 4));
        emit(createEvent(mOption2b, 1, 6, 12));
        // emit an unrelated one
        emit(createEvent(mOption1a, 5, 8, 9));
        assertThat(mItem2.size(), is(1));
        assertThat(mItem2.containsKey(mOption2a), is(false));
        assertThat(mItem2.containsKey(mOption2b), is(true));
        assertTick(mItem2.get(mOption2b), mOption2b, 1, 6, 12);
        // finish
        mFixture.stopFlow(mKey1);
    }

}
