package org.marketcetera.photon.internal.marketdata;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItemInArray;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.marketcetera.core.position.impl.BigDecimalMatchers.comparesEqualTo;

import java.math.BigDecimal;
import java.util.Date;
import java.util.EnumSet;
import java.util.concurrent.Executor;

import org.junit.Test;
import org.marketcetera.event.MarketstatEvent;
import org.marketcetera.marketdata.Capability;
import org.marketcetera.marketdata.MarketDataRequest;
import org.marketcetera.marketdata.MarketDataRequest.Content;
import org.marketcetera.module.ModuleManager;
import org.marketcetera.photon.model.marketdata.MDMarketstat;
import org.marketcetera.photon.model.marketdata.impl.MDMarketstatImpl;
import org.marketcetera.trade.MSymbol;

/* $License$ */

/**
 * Test {@link MarketstatManager}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.5.0
 */
public class MarketstatManagerTest extends DataFlowManagerTestBase<MDMarketstat, MarketstatKey> {

	private static final Date DATE1 = new Date(1239657332835L);
	private static final Date DATE2 = new Date(1239570932835L);

	@Override
	protected IDataFlowManager<MDMarketstatImpl, MarketstatKey> createFixture(
			ModuleManager moduleManager, Executor marketDataExecutor) {
		return new MarketstatManager(moduleManager, marketDataExecutor);
	}

	@Override
	protected MarketstatKey createKey1() {
		return new MarketstatKey("GOOG");
	}

	@Override
	protected MarketstatKey createKey2() {
		return new MarketstatKey("YHOO");
	}

	@Override
	protected MarketstatKey createKey3() {
		return new MarketstatKey("GIB");
	}

	@Override
	protected EnumSet<Capability> getSupportedCapabilities() {
		return EnumSet.of(Capability.MARKET_STAT);
	}

	@Override
	protected void validateInitialConditions(MDMarketstat item, MarketstatKey key) {
		assertThat(item.getSymbol(), is(key.getSymbol()));
		assertThat(item.getCloseDate(), nullValue());
		assertThat(item.getClosePrice(), nullValue());
		assertThat(item.getPreviousCloseDate(), nullValue());
		assertThat(item.getPreviousClosePrice(), nullValue());
	}

	@Override
	protected Object createEvent1(MarketstatKey key) {
		return createEvent(key.getSymbol(), 34, 33, DATE1, DATE2);
	}

	@Override
	protected Object createEvent2(MarketstatKey key) {
		return createEvent(key.getSymbol(), 1, 2, DATE2, DATE1);
	}

	@Override
	protected void validateState1(MDMarketstat item) {
		assertThat(item.getCloseDate(), is(DATE1));
		assertThat(item.getClosePrice(), comparesEqualTo(34));
		assertThat(item.getPreviousCloseDate(), is(DATE2));
		assertThat(item.getPreviousClosePrice(), comparesEqualTo(33));
	}

	@Override
	protected void validateState2(MDMarketstat item) {
		assertThat(item.getCloseDate(), is(DATE2));
		assertThat(item.getClosePrice(), comparesEqualTo(1));
		assertThat(item.getPreviousCloseDate(), is(DATE1));
		assertThat(item.getPreviousClosePrice(), comparesEqualTo(2));
	}

	private Object createEvent(String symbol, int close, int previousClose, Date closeDate,
			Date previousCloseDate) {
		return new MarketstatEvent(new MSymbol(symbol), new Date(), null, null, null,
				new BigDecimal(close), new BigDecimal(previousClose), null, closeDate,
				previousCloseDate, null, null, null, null, null, null);
	}

	@Override
	protected void validateRequest(MarketstatKey key, MarketDataRequest request) {
		assertThat(request.getContent().size(), is(1));
		assertThat(request.getContent(), hasItem(Content.MARKET_STAT));
		assertThat(request.getSymbols().length, is(1));
		assertThat(request.getSymbols(), hasItemInArray(key.getSymbol()));
	}

	@Test
	public void testNullEventIgnored() throws Exception {
		mFixture.startFlow(mKey1);
		validateInitialConditions(mItem1, mKey1);
		// emit data
		emit(createEvent1(mKey1));
		validateState1(mItem1);
		// emit data for wrong key
		emit(createNullEvent(mKey1));
		validateState1(mItem1);
	}

	protected Object createNullEvent(MarketstatKey key) {
		return new MarketstatEvent(new MSymbol(key.getSymbol()), new Date(), null, null, null,
				null, null, null, null, null, null, null, null, null, null, null);
	}

}
