package org.marketcetera.photon.internal.marketdata;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItemInArray;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.marketcetera.core.position.impl.BigDecimalMatchers.comparesEqualTo;

import java.math.BigDecimal;
import java.util.EnumSet;
import java.util.concurrent.Executor;

import org.junit.Test;
import org.marketcetera.event.TradeEvent;
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
 * Test {@link LatestTickManager}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.5.0
 */
public class LatestTickManagerTest extends
        DataFlowManagerTestBase<MDLatestTick, LatestTickKey> {

    @Override
    protected IDataFlowManager<MDLatestTickImpl, LatestTickKey> createFixture(
            ModuleManager moduleManager, Executor marketDataExecutor, IMarketDataRequestSupport marketDataRequestSupport) {
        return new LatestTickManager(moduleManager, marketDataExecutor, marketDataRequestSupport);
    }

    @Override
    protected LatestTickKey createKey1() {
        return new LatestTickKey(new Equity("IBM"));
    }

    @Override
    protected LatestTickKey createKey2() {
        return new LatestTickKey(new Option("IBM", "20091001", BigDecimal.TEN,
                OptionType.Put));
    }

    @Override
    protected LatestTickKey createKey3() {
        return new LatestTickKey(new Equity("JAVA"));
    }

    @Override
    protected EnumSet<Capability> getSupportedCapabilities() {
        return EnumSet.of(Capability.LATEST_TICK);
    }

    @Override
    protected void validateInitialConditions(MDLatestTick item,
            LatestTickKey key) {
        assertThat(item.getInstrument(), is(key.getInstrument()));
        assertThat(item.getSize(), nullValue());
        assertThat(item.getPrice(), nullValue());
        assertThat(item.getMultiplier(), nullValue());
    }

    @Override
    protected Object createEvent1(LatestTickKey key) {
        return createEvent(key.getInstrument(), 1, 6);
    }

    @Override
    protected Object createEvent2(LatestTickKey key) {
        return createEvent(key.getInstrument(), 10, 7);
    }

    @Override
    protected void validateState1(MDLatestTick item, LatestTickKey key) {
        assertThat(item.getPrice(), comparesEqualTo(1));
        assertThat(item.getSize(), comparesEqualTo(6));
        assertThat(item.getMultiplier(), nullValue());
    }

    @Override
    protected void validateState2(MDLatestTick item, LatestTickKey key) {
        assertThat(item.getPrice(), comparesEqualTo(10));
        assertThat(item.getSize(), comparesEqualTo(7));
        assertThat(item.getMultiplier(), nullValue());
    }

    private Object createEvent(Instrument instrument, int price, int size) {
        TradeEventBuilder<TradeEvent> builder = TradeEventBuilder.tradeEvent(
                instrument).withExchange("Q").withTradeDate("bogus").withPrice(
                new BigDecimal(price)).withSize(new BigDecimal(size));
        if (instrument instanceof Option) {
            builder = builder.withUnderlyingInstrument(new Equity(instrument
                    .getSymbol())).withExpirationType(ExpirationType.AMERICAN);
        }
        return builder.create();
    }

    private Object createOptionEvent(Instrument instrument, int price, int size, int multiplier) {
        TradeEventBuilder<TradeEvent> builder = TradeEventBuilder.tradeEvent(
                instrument).withExchange("Q").withTradeDate("bogus").withPrice(
                new BigDecimal(price)).withSize(new BigDecimal(size));
        if (instrument instanceof Option) {
            builder = builder.withUnderlyingInstrument(
                    new Equity(instrument.getSymbol())).withExpirationType(
                    ExpirationType.AMERICAN).withMultiplier(
                    new BigDecimal(multiplier));
        }
        return builder.create();
    }

    @Override
    protected void validateRequest(LatestTickKey key, MarketDataRequest request) {
        assertThat(request.getContent().size(), is(1));
        assertThat(request.getContent(), hasItem(Content.LATEST_TICK));
        assertThat(request.getSymbols().length, is(1));
        assertThat(request.getSymbols(), hasItemInArray(getOsiSymbol(key)));
    }
    
    @Test
    public void optionMultiplier() throws Exception {
        // start flow
        mFixture.startFlow(mKey2);
        // emit event with multiplier
        emit(createOptionEvent(mKey2.getInstrument(), 1, 2, 3));
        // item should have changed
        assertThat(mItem2.getPrice(), comparesEqualTo(1));
        assertThat(mItem2.getSize(), comparesEqualTo(2));
        assertThat(mItem2.getMultiplier(), comparesEqualTo(3));
        // emit another event
        emit(createOptionEvent(mKey2.getInstrument(), 4, 5, 6));
        assertThat(mItem2.getPrice(), comparesEqualTo(4));
        assertThat(mItem2.getSize(), comparesEqualTo(5));
        assertThat(mItem2.getMultiplier(), comparesEqualTo(6));
        // finish
        mFixture.stopFlow(mKey2);
    }

}
