package org.marketcetera.photon.internal.marketdata;

import java.util.EnumSet;
import java.util.concurrent.Executor;

import org.marketcetera.event.AskEvent;
import org.marketcetera.event.BidEvent;
import org.marketcetera.event.MarketDataEvent;
import org.marketcetera.marketdata.Capability;
import org.marketcetera.marketdata.MarketDataRequest;
import org.marketcetera.marketdata.MarketDataRequest.Content;
import org.marketcetera.module.ModuleManager;
import org.marketcetera.photon.model.marketdata.impl.MDTopOfBookImpl;
import org.marketcetera.util.misc.ClassVersion;

import com.google.inject.Inject;

/* $License$ */

/**
 * Manages Top of Book flows.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.5.0
 */
@ClassVersion("$Id$")
public class TopOfBookManager extends DataFlowManager<MDTopOfBookImpl, TopOfBookKey> implements
		ITopOfBookManager {

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
	public TopOfBookManager(final ModuleManager moduleManager,
			@MarketDataExecutor final Executor marketDataExecutor) {
		super(moduleManager, EnumSet.of(Capability.TOP_OF_BOOK), marketDataExecutor);
	}

	@Override
	protected MDTopOfBookImpl createItem(final TopOfBookKey key) {
		assert key != null;
		MDTopOfBookImpl item = new MDTopOfBookImpl();
		item.setSymbol(key.getSymbol());
		return item;
	}

	@Override
	protected void resetItem(final TopOfBookKey key, final MDTopOfBookImpl item) {
		assert key != null;
		assert item != null;
		synchronized (item) {
			item.setAskPrice(null);
			item.setAskSize(null);
			item.setBidPrice(null);
			item.setBidSize(null);
		}
	}

	@Override
	protected Subscriber createSubscriber(final TopOfBookKey key) {
		assert key != null;
		final String symbol = key.getSymbol();
		final MarketDataRequest request = MarketDataRequest.newRequest().withSymbols(symbol)
				.withContent(Content.TOP_OF_BOOK);
		return new Subscriber() {

			@Override
			public MarketDataRequest getRequest() {
				return request;
			}

			@Override
			public void receiveData(final Object inData) {
				final MDTopOfBookImpl item = getItem(key);
				synchronized (item) {
					if (inData instanceof MarketDataEvent
							&& !validateSymbol(symbol, (MarketDataEvent) inData)) {
						return;
					}
					if (inData instanceof BidEvent) {
						BidEvent event = (BidEvent) inData;
						item.setBidPrice(event.getPrice());
						item.setBidSize(event.getSize());
					} else if (inData instanceof AskEvent) {
						AskEvent event = (AskEvent) inData;
						item.setAskPrice(event.getPrice());
						item.setAskSize(event.getSize());
					} else {
						reportUnexpectedData(inData);
					}
				}
			}
		};
	}
}
