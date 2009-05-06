package org.marketcetera.photon.internal.marketdata;

import java.math.BigDecimal;
import java.util.EnumSet;
import java.util.concurrent.Executor;

import org.marketcetera.event.TradeEvent;
import org.marketcetera.marketdata.Capability;
import org.marketcetera.marketdata.MarketDataRequest;
import org.marketcetera.marketdata.MarketDataRequest.Content;
import org.marketcetera.module.ModuleManager;
import org.marketcetera.photon.model.marketdata.impl.MDLatestTickImpl;
import org.marketcetera.util.misc.ClassVersion;

import com.google.inject.Inject;

/* $License$ */

/**
 * Manages latest tick flows.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.5.0
 */
@ClassVersion("$Id$")
public class LatestTickManager extends DataFlowManager<MDLatestTickImpl, LatestTickKey> implements
		ILatestTickManager {

	/**
	 * Constructor.
	 * 
	 * @param moduleManager
	 *            the module manager
	 * @param marketDataExecutor
	 *            an executor for long running module operations that <strong>must</strong> execute
	 *            tasks sequentially
	 * @throws IllegalArgumentException
	 *             if moduleManager is null
	 */
	@Inject
	public LatestTickManager(ModuleManager moduleManager,
			@MarketDataExecutor Executor marketDataExecutor) {
		super(moduleManager, EnumSet.of(Capability.LATEST_TICK), marketDataExecutor);
	}

	@Override
	protected MDLatestTickImpl createItem(LatestTickKey key) {
		assert key != null;
		MDLatestTickImpl item = new MDLatestTickImpl();
		item.setSymbol(key.getSymbol());
		return item;
	}

	@Override
	protected void resetItem(LatestTickKey key, MDLatestTickImpl item) {
		assert key != null;
		assert item != null;
		synchronized (item) {
			item.setPrice(null);
			item.setSize(null);
		}
	}

	@Override
	protected Subscriber createSubscriber(final LatestTickKey key) {
		assert key != null;
		final String symbol = key.getSymbol();
		final MarketDataRequest request = MarketDataRequest.newRequest().withSymbols(symbol)
				.withContent(Content.LATEST_TICK);
		return new Subscriber() {

			@Override
			public MarketDataRequest getRequest() {
				return request;
			}

			@Override
			public void receiveData(Object inData) {
				final MDLatestTickImpl item = getItem(key);
				synchronized (item) {
					if (inData instanceof TradeEvent) {
						TradeEvent data = (TradeEvent) inData;
						if (!validateSymbol(symbol, data)) {
							return;
						}
						BigDecimal price = data.getPrice();
						if (price != null) item.setPrice(price);
						BigDecimal size = data.getSize();
						if (size != null) item.setSize(size);
					} else {
						reportUnexpectedData(inData);
					}
				}
			}
		};
	}
}
