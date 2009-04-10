package org.marketcetera.photon.internal.marketdata;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItemInArray;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.marketcetera.core.position.impl.BigDecimalMatchers.comparesEqualTo;

import java.math.BigDecimal;

import org.marketcetera.event.AskEvent;
import org.marketcetera.event.BidEvent;
import org.marketcetera.marketdata.MarketDataRequest;
import org.marketcetera.marketdata.MarketDataRequest.Content;
import org.marketcetera.module.ModuleManager;
import org.marketcetera.photon.model.marketdata.MDTopOfBook;
import org.marketcetera.trade.MSymbol;

/* $License$ */

/**
 * Test {@link TopOfBookManager}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
public class TopOfBookManagerTest extends DataFlowManagerTestBase<MDTopOfBook, TopOfBookKey> {

    @Override
    protected IDataFlowManager<MDTopOfBook, TopOfBookKey> createFixture(
            ModuleManager moduleManager) {
        return new TopOfBookManager(moduleManager);
    }

    @Override
    protected TopOfBookKey createKey1() {
        return new TopOfBookKey("GOOG");
    }

    @Override
    protected TopOfBookKey createKey2() {
        return new TopOfBookKey("YHOO");
    }

    @Override
    protected TopOfBookKey createKey3() {
        return new TopOfBookKey("GIB");
    }

    @Override
    protected void validateInitialConditions(MDTopOfBook item, TopOfBookKey key) {
        assertThat(item.getSymbol(), is(key.getSymbol()));
        assertThat(item.getAskPrice(), nullValue());
        assertThat(item.getAskSize(), nullValue());
        assertThat(item.getBidPrice(), nullValue());
        assertThat(item.getBidSize(), nullValue());
    }

    @Override
    protected Object createEvent1(TopOfBookKey key) {
        return createAskEvent(key.getSymbol(), 34, 500);
    }

    @Override
    protected Object createEvent2(TopOfBookKey key) {
        return createBidEvent(key.getSymbol(), 6, 30);
    }

    @Override
    protected void validateState1(MDTopOfBook item) {
        assertThat(item.getAskPrice(), comparesEqualTo(34));
        assertThat(item.getAskSize(), comparesEqualTo(500));
    }

    @Override
    protected void validateState2(MDTopOfBook item) {
        assertThat(item.getBidPrice(), comparesEqualTo(6));
        assertThat(item.getBidSize(), comparesEqualTo(30));
    }

    private Object createAskEvent(String symbol, int price, int size) {
        return new AskEvent(1L, System.currentTimeMillis(), new MSymbol(symbol), "Q", new BigDecimal(price),
                new BigDecimal(size));
    }

    private Object createBidEvent(String symbol, int price, int size) {
        return new BidEvent(1L, System.currentTimeMillis(), new MSymbol(symbol), "Q", new BigDecimal(price),
                new BigDecimal(size));
    }

    @Override
    protected void validateRequest(TopOfBookKey key, MarketDataRequest request) {
        assertThat(request.getContent().size(), is(1));
        assertThat(request.getContent(), hasItem(Content.TOP_OF_BOOK));
        assertThat(request.getSymbols().length, is(1));
        assertThat(request.getSymbols(), hasItemInArray(key.getSymbol()));
    }

}
