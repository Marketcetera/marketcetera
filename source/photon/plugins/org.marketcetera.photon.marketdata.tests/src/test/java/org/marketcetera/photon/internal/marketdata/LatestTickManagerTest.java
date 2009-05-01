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

import org.marketcetera.event.TradeEvent;
import org.marketcetera.marketdata.Capability;
import org.marketcetera.marketdata.MarketDataRequest;
import org.marketcetera.marketdata.MarketDataRequest.Content;
import org.marketcetera.module.ModuleManager;
import org.marketcetera.photon.model.marketdata.MDLatestTick;
import org.marketcetera.photon.model.marketdata.impl.MDLatestTickImpl;
import org.marketcetera.trade.MSymbol;

/* $License$ */

/**
 * Test {@link LatestTickManager}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.5.0
 */
public class LatestTickManagerTest extends DataFlowManagerTestBase<MDLatestTick, LatestTickKey> {

	@Override
	protected IDataFlowManager<MDLatestTickImpl, LatestTickKey> createFixture(
			ModuleManager moduleManager, Executor marketDataExecutor) {
		return new LatestTickManager(moduleManager, marketDataExecutor);
	}

	@Override
	protected LatestTickKey createKey1() {
		return new LatestTickKey("IBM");
	}

	@Override
	protected LatestTickKey createKey2() {
		return new LatestTickKey("METC");
	}

	@Override
	protected LatestTickKey createKey3() {
		return new LatestTickKey("JAVA");
	}

	@Override
	protected EnumSet<Capability> getSupportedCapabilities() {
		return EnumSet.of(Capability.LATEST_TICK);
	}

	@Override
	protected void validateInitialConditions(MDLatestTick item, LatestTickKey key) {
		assertThat(item.getSymbol(), is(key.getSymbol()));
		assertThat(item.getPrice(), nullValue());
	}

	@Override
	protected Object createEvent1(LatestTickKey key) {
		return createEvent(key.getSymbol(), 1, 6);
	}

	@Override
	protected Object createEvent2(LatestTickKey key) {
		return createEvent(key.getSymbol(), 10, 7);
	}

	@Override
	protected void validateState1(MDLatestTick item) {
		assertThat(item.getPrice(), comparesEqualTo(1));
		assertThat(item.getSize(), comparesEqualTo(6));
	}

	@Override
	protected void validateState2(MDLatestTick item) {
		assertThat(item.getPrice(), comparesEqualTo(10));
		assertThat(item.getSize(), comparesEqualTo(7));
	}

	private Object createEvent(String symbol, int price, int size) {
		return new TradeEvent(1L, System.currentTimeMillis(), new MSymbol(symbol), "Q",
				new BigDecimal(price), new BigDecimal(size));
	}

	@Override
	protected void validateRequest(LatestTickKey key, MarketDataRequest request) {
		assertThat(request.getContent().size(), is(1));
		assertThat(request.getContent(), hasItem(Content.LATEST_TICK));
		assertThat(request.getSymbols().length, is(1));
		assertThat(request.getSymbols(), hasItemInArray(key.getSymbol()));
	}

}
