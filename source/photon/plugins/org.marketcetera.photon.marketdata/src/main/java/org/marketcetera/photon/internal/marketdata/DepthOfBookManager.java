package org.marketcetera.photon.internal.marketdata;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;

import org.marketcetera.event.AskEvent;
import org.marketcetera.event.BidEvent;
import org.marketcetera.event.QuoteEvent;
import org.marketcetera.marketdata.Capability;
import org.marketcetera.marketdata.MarketDataRequest;
import org.marketcetera.marketdata.MarketDataRequest.Content;
import org.marketcetera.module.ModuleManager;
import org.marketcetera.photon.model.marketdata.MDQuote;
import org.marketcetera.photon.model.marketdata.impl.MDDepthOfBookImpl;
import org.marketcetera.photon.model.marketdata.impl.MDQuoteImpl;
import org.marketcetera.trade.Instrument;
import org.marketcetera.util.misc.ClassVersion;

import com.google.common.base.Function;
import com.google.common.collect.MapMaker;
import com.google.inject.Inject;

/* $License$ */

/**
 * Manages market depth flows.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.5.0
 */
@ClassVersion("$Id$")
public class DepthOfBookManager extends DataFlowManager<MDDepthOfBookImpl, DepthOfBookKey>
		implements IDepthOfBookManager {

	/**
	 * Creates instances of this class.
	 */
	@ClassVersion("$Id$")
	public static class FactoryImpl implements Factory {

		private final ModuleManager mModuleManager;

		private final Executor mMarketDataExecutor;

		/**
		 * Constructor.
		 * 
		 * @param moduleManager
		 *            the module manager
		 * @param marketDataExecutor
		 *            an executor for long running module operations that <strong>must</strong>
		 *            execute tasks sequentially
		 */
		@Inject
		public FactoryImpl(final ModuleManager moduleManager,
				@MarketDataExecutor final Executor marketDataExecutor) {
			mModuleManager = moduleManager;
			mMarketDataExecutor = marketDataExecutor;
		}

		@Override
		public IDepthOfBookManager create(final Set<Capability> capabilities) {
			return new DepthOfBookManager(mModuleManager, capabilities, mMarketDataExecutor);
		}
	}

	private final Map<MDDepthOfBookImpl, Map<Object, MDQuoteImpl>> mIdMap = new MapMaker()
			.makeComputingMap(new Function<MDDepthOfBookImpl, Map<Object, MDQuoteImpl>>() {
				@Override
				public Map<Object, MDQuoteImpl> apply(final MDDepthOfBookImpl from) {
					return new MapMaker().makeMap();
				}
			});
	private final QuoteEventToMDQuote mFunction = new QuoteEventToMDQuote();

	/**
	 * Constructor.
	 * 
	 * @param moduleManager
	 *            the module manager
	 * @param requiredCapabilities
	 *            the capabilities this manager requires, cannot be empty
	 * @param marketDataExecutor
	 *            an executor for long running module operations that <strong>must</strong> execute
	 *            tasks sequentially
	 * @throws IllegalArgumentException
	 *             if moduleManager is null
	 */
	public DepthOfBookManager(final ModuleManager moduleManager,
			final Set<Capability> requiredCapabilities, final Executor marketDataExecutor) {
		super(moduleManager, requiredCapabilities, marketDataExecutor);
	}

	@Override
	protected MDDepthOfBookImpl createItem(final DepthOfBookKey key) {
		assert key != null;
		MDDepthOfBookImpl item = new MDDepthOfBookImpl();
		item.setSymbol(key.getSymbol());
		item.setProduct(key.getProduct());
		return item;
	}

	@Override
	protected void resetItem(final DepthOfBookKey key, final MDDepthOfBookImpl item) {
		assert key != null;
		assert item != null;
		synchronized (item) {
			mIdMap.remove(item);
			item.getBids().clear();
			item.getAsks().clear();
		}
	}

	@Override
	protected Subscriber createSubscriber(final DepthOfBookKey key) {
		assert key != null;
		final String symbol = key.getSymbol();
		final MarketDataRequest request = MarketDataRequest.newRequest().withSymbols(symbol)
				.withContent(key.getProduct());
		final boolean isLevel2 = key.getProduct() == Content.LEVEL_2;
		return new Subscriber() {

			@Override
			public MarketDataRequest getRequest() {
				return request;
			}

			@Override
			public void receiveData(final Object inData) {
				if (inData instanceof QuoteEvent) {
					QuoteEvent data = (QuoteEvent) inData;
					Instrument instrument = data.getInstrument();
					if (!validateSymbol(symbol, instrument)) {
						return;
					}
					final MDDepthOfBookImpl item = getItem(key);
					synchronized (item) {
						List<MDQuote> list;
						if (data instanceof BidEvent) {
							list = item.getBids();
						} else if (data instanceof AskEvent) {
							list = item.getAsks();
						} else {
							// a new type of QuoteEvent is really unexpected,
							// but we can ignore it
							assert false : inData;
							reportUnexpectedData(inData);
							return;
						}
						Map<Object, MDQuoteImpl> map = mIdMap.get(item);
						switch (data.getAction()) {
						case ADD:
							// add new item and map it
							if (isLevel2) {
								// for Level 2, updates can come in an ADD event
								MDQuoteImpl changed = getFromMap(map, data);
								if (changed != null) {
									updateItem(data, changed);
									break;
								}
							}
							MDQuoteImpl added = mFunction.apply(data);
							addToMap(map, data, added);
							list.add(added);
							break;
						case CHANGE:
							MDQuoteImpl changed = getFromMap(map, data);
							if (changed == null) {
								reportUnexpectedMessageId(data);
							} else {
								// update item
								updateItem(data, changed);
							}
							break;
						case DELETE:
							MDQuoteImpl deleted = getFromMap(map, data);
							if (deleted == null) {
								reportUnexpectedMessageId(data);
							} else {
								// remove item
								list.remove(deleted);
								removeFromMap(map, data);
							}
							break;
						default:
							// new action is not expected, but we can ignore it
							assert false : data.getAction();
							reportUnexpectedData(data);
						}
					}
				} else {
					reportUnexpectedData(inData);
				}
			}

			private void removeFromMap(final Map<Object, MDQuoteImpl> map, final QuoteEvent data) {
				if (isLevel2) {
					// for level 2, the exchange is the key
					map.remove(encodeLevel2(data));
				} else {
					map.remove(data.getMessageId());
				}
			}

			private void updateItem(final QuoteEvent data, final MDQuoteImpl changed) {
				changed.setSource(data.getExchange());
				changed.setTime(data.getTimeMillis());
				changed.setSize(data.getSize());
				changed.setPrice(data.getPrice());
			}

			private void addToMap(final Map<Object, MDQuoteImpl> map, final QuoteEvent data,
					final MDQuoteImpl toAdd) {
				if (isLevel2) {
					// for level 2, the exchange is the key
					map.put(encodeLevel2(data), toAdd);
				} else {
					map.put(data.getMessageId(), toAdd);
				}
			}

			private MDQuoteImpl getFromMap(final Map<Object, MDQuoteImpl> map, final QuoteEvent data) {
				if (isLevel2) {
					// for level 2, the exchange is the key
					return map.get(encodeLevel2(data));
				} else {
					return map.get(data.getMessageId());
				}
			}

			private String encodeLevel2(QuoteEvent data) {
				StringBuilder builder = new StringBuilder();
				builder.append(data instanceof BidEvent ? "bid" : "ask"); //$NON-NLS-1$ //$NON-NLS-2$
				builder.append('-');
				builder.append(data.getInstrument().getSymbol());
				builder.append('-');
				builder.append(data.getExchange());
				return builder.toString();
			}
		};
	}

	/**
	 * Transforms {@link QuoteEvent} into {@link MDQuoteImpl}.
	 */
	@ClassVersion("$Id$")
	private static class QuoteEventToMDQuote implements Function<QuoteEvent, MDQuoteImpl> {

		@Override
		public MDQuoteImpl apply(final QuoteEvent from) {
			MDQuoteImpl quote = new MDQuoteImpl();
			quote.setSource(from.getExchange());
			quote.setTime(from.getTimeMillis());
			quote.setPrice(from.getPrice());
			quote.setSize(from.getSize());
			return quote;
		}
	}
}
