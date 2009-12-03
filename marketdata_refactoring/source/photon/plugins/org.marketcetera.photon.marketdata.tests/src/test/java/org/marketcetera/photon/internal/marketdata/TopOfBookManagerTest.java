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

import org.marketcetera.marketdata.Capability;
import org.marketcetera.marketdata.Content;
import org.marketcetera.marketdata.MarketDataRequest;
import org.marketcetera.module.ModuleManager;
import org.marketcetera.photon.model.marketdata.MDTopOfBook;
import org.marketcetera.photon.model.marketdata.impl.MDTopOfBookImpl;
import org.marketcetera.trade.Equity;
import org.marketcetera.trade.Option;
import org.marketcetera.trade.OptionType;

/* $License$ */

/**
 * Test {@link TopOfBookManager}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.5.0
 */
public class TopOfBookManagerTest extends
        DataFlowManagerTestBase<MDTopOfBook, TopOfBookKey> {

    @Override
    protected IDataFlowManager<MDTopOfBookImpl, TopOfBookKey> createFixture(
            ModuleManager moduleManager, Executor marketDataExecutor, IMarketDataRequestSupport marketDataRequestSupport) {
        return new TopOfBookManager(moduleManager, marketDataExecutor, marketDataRequestSupport);
    }

    @Override
    protected TopOfBookKey createKey1() {
        return new TopOfBookKey(new Equity("GOOG"));
    }

    @Override
    protected TopOfBookKey createKey2() {
        return new TopOfBookKey(new Option("YHOO", "20100101", new BigDecimal("1.11"), OptionType.Put));
    }

    @Override
    protected TopOfBookKey createKey3() {
        return new TopOfBookKey(new Equity("GIB"));
    }

    @Override
    protected EnumSet<Capability> getSupportedCapabilities() {
        return EnumSet.of(Capability.TOP_OF_BOOK);
    }

    @Override
    protected void validateInitialConditions(MDTopOfBook item, TopOfBookKey key) {
        assertThat(item.getInstrument(), is(key.getInstrument()));
        assertThat(item.getAskPrice(), nullValue());
        assertThat(item.getAskSize(), nullValue());
        assertThat(item.getBidPrice(), nullValue());
        assertThat(item.getBidSize(), nullValue());
    }

    @Override
    protected Object createEvent1(TopOfBookKey key) {
        return createAskEvent(key.getInstrument(), "Q", 34, 500);
    }

    @Override
    protected Object createEvent2(TopOfBookKey key) {
        return createBidEvent(key.getInstrument(), "Q", 6, 30);
    }

    @Override
    protected void validateState1(MDTopOfBook item, TopOfBookKey key) {
        assertThat(item.getAskPrice(), comparesEqualTo(34));
        assertThat(item.getAskSize(), comparesEqualTo(500));
    }

    @Override
    protected void validateState2(MDTopOfBook item, TopOfBookKey key) {
        assertThat(item.getBidPrice(), comparesEqualTo(6));
        assertThat(item.getBidSize(), comparesEqualTo(30));
    }

    @Override
    protected void validateRequest(TopOfBookKey key, MarketDataRequest request) {
        assertThat(request.getContent().size(), is(1));
        assertThat(request.getContent(), hasItem(Content.TOP_OF_BOOK));
        assertThat(request.getSymbols().size(), is(1));
        assertThat(request.getSymbols().toArray(new String[0]), hasItemInArray(getOsiSymbol(key)));
    }

}
