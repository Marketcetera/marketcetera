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
import org.marketcetera.event.impl.MarketstatEventBuilder;
import org.marketcetera.marketdata.Capability;
import org.marketcetera.marketdata.MarketDataRequest;
import org.marketcetera.marketdata.MarketDataRequest.Content;
import org.marketcetera.module.ModuleManager;
import org.marketcetera.options.ExpirationType;
import org.marketcetera.photon.model.marketdata.MDMarketstat;
import org.marketcetera.photon.model.marketdata.impl.MDMarketstatImpl;
import org.marketcetera.trade.Equity;
import org.marketcetera.trade.Instrument;
import org.marketcetera.trade.Option;
import org.marketcetera.trade.OptionType;

/* $License$ */

/**
 * Test {@link MarketstatManager}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.5.0
 */
public class MarketstatManagerTest extends DataFlowManagerTestBase<MDMarketstat, MarketstatKey> {

	private static final String DATE1 = "20091010";
	private static final String DATE2 = "20091011";

	@Override
	protected IDataFlowManager<MDMarketstatImpl, MarketstatKey> createFixture(
			ModuleManager moduleManager, Executor marketDataExecutor, IMarketDataRequestSupport marketDataRequestSupport) {
		return new MarketstatManager(moduleManager, marketDataExecutor, marketDataRequestSupport);
	}

	@Override
	protected MarketstatKey createKey1() {
		return new MarketstatKey(new Equity("GOOG"));
	}

	@Override
	protected MarketstatKey createKey2() {
		return new MarketstatKey(new Option("YHOO", "20100101", new BigDecimal("1.11"), OptionType.Put));
	}

	@Override
	protected MarketstatKey createKey3() {
		return new MarketstatKey(new Equity("GIB"));
	}

	@Override
	protected EnumSet<Capability> getSupportedCapabilities() {
		return EnumSet.of(Capability.MARKET_STAT);
	}

	@Override
	protected void validateInitialConditions(MDMarketstat item, MarketstatKey key) {
		assertThat(item.getInstrument(), is(key.getInstrument()));
		assertThat(item.getCloseDate(), nullValue());
		assertThat(item.getClosePrice(), nullValue());
		assertThat(item.getPreviousCloseDate(), nullValue());
		assertThat(item.getPreviousClosePrice(), nullValue());
	}

	@Override
	protected Object createEvent1(MarketstatKey key) {
		return createEvent(key.getInstrument(), 34, 33, DATE1, DATE2);
	}

	@Override
	protected Object createEvent2(MarketstatKey key) {
		return createEvent(key.getInstrument(), 1, 2, DATE2, DATE1);
	}

	@Override
	protected void validateState1(MDMarketstat item, MarketstatKey key) {
		assertThat(item.getCloseDate(), is(DATE1));
		assertThat(item.getClosePrice(), comparesEqualTo(34));
		assertThat(item.getPreviousCloseDate(), is(DATE2));
		assertThat(item.getPreviousClosePrice(), comparesEqualTo(33));
	}

	@Override
	protected void validateState2(MDMarketstat item, MarketstatKey key) {
		assertThat(item.getCloseDate(), is(DATE2));
		assertThat(item.getClosePrice(), comparesEqualTo(1));
		assertThat(item.getPreviousCloseDate(), is(DATE1));
		assertThat(item.getPreviousClosePrice(), comparesEqualTo(2));
	}

	private Object createEvent(Instrument instrument, int close, int previousClose, String closeDate,
			String previousCloseDate) {
		MarketstatEventBuilder builder = MarketstatEventBuilder.marketstat(instrument)
                		.withTimestamp(new Date()).withClosePrice(new BigDecimal(close))
                		.withPreviousClosePrice(new BigDecimal(previousClose)).withCloseDate(closeDate)
                		.withPreviousCloseDate(previousCloseDate);
		if (instrument instanceof Option) {
            builder = builder.withUnderlyingInstrument(new Equity(instrument
                    .getSymbol())).withExpirationType(ExpirationType.AMERICAN);
        }
        return builder.create();
	}

	@Override
	protected void validateRequest(MarketstatKey key, MarketDataRequest request) {
		assertThat(request.getContent().size(), is(1));
		assertThat(request.getContent(), hasItem(Content.MARKET_STAT));
		assertThat(request.getSymbols().length, is(1));
        assertThat(request.getSymbols(), hasItemInArray(getOsiSymbol(key)));
	}

    @Test
	public void testNullEventIgnored() throws Exception {
		mFixture.startFlow(mKey1);
		validateInitialConditions(mItem1, mKey1);
		// emit data
		emit(createEvent1(mKey1));
		validateState1(mItem1, mKey1);
		// emit an event without close data
		emit(createNullEvent(mKey1));
		validateState1(mItem1, mKey1);
	}

	protected Object createNullEvent(MarketstatKey key) throws Exception {
		return MarketstatEventBuilder.equityMarketstat().withInstrument(key.getInstrument());
	}

}
