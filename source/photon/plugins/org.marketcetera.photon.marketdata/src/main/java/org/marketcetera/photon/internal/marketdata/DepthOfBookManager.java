package org.marketcetera.photon.internal.marketdata;

import java.util.List;
import java.util.Map;

import org.marketcetera.event.AskEvent;
import org.marketcetera.event.BidEvent;
import org.marketcetera.event.QuoteEvent;
import org.marketcetera.marketdata.MarketDataRequest;
import org.marketcetera.module.ModuleManager;
import org.marketcetera.photon.model.marketdata.MDQuote;
import org.marketcetera.photon.model.marketdata.impl.MDDepthOfBookImpl;
import org.marketcetera.photon.model.marketdata.impl.MDQuoteImpl;
import org.marketcetera.trade.MSymbol;
import org.marketcetera.util.misc.ClassVersion;

import com.google.common.base.Function;
import com.google.common.collect.Maps;
import com.google.inject.Inject;

/* $License$ */

/**
 * Manages market depth flows.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id: DepthOfBookManager.java 10495 2009-04-15 21:37:09Z will $
 * @since 1.5.0
 */
@ClassVersion("$Id$")
public class DepthOfBookManager extends DataFlowManager<MDDepthOfBookImpl, DepthOfBookKey>
		implements IDepthOfBookManager {

	private final Map<Long, MDQuoteImpl> mIdMap = Maps.newHashMap();
	private final QuoteEventToMDQuote mFunction = new QuoteEventToMDQuote();

	/**
	 * Constructor.
	 * 
	 * @param moduleManager
	 *            the module manager
	 * @throws IllegalArgumentException
	 *             if moduleManager is null
	 */
	@Inject
	public DepthOfBookManager(ModuleManager moduleManager) {
		super(moduleManager);
	}

	@Override
	protected MDDepthOfBookImpl createItem(DepthOfBookKey key) {
		assert key != null;
		MDDepthOfBookImpl item = new MDDepthOfBookImpl();
		item.setSymbol(key.getSymbol());
		item.setProduct(key.getProduct());
		return item;
	}

	@Override
	protected void resetItem(DepthOfBookKey key, MDDepthOfBookImpl item) {
		assert key != null;
		assert item != null;
		mIdMap.remove(key);
		item.getBids().clear();
		item.getAsks().clear();
	}

	@Override
	protected Subscriber createSubscriber(final DepthOfBookKey key) {
		assert key != null;
		final String symbol = key.getSymbol();
		final MarketDataRequest request = MarketDataRequest.newRequest().withSymbols(symbol)
				.withContent(key.getProduct());
		return new Subscriber() {

			@Override
			public MarketDataRequest getRequest() {
				return request;
			}

			@Override
			public void receiveData(Object inData) {
				synchronized (DepthOfBookManager.this) {
					if (inData instanceof QuoteEvent) {
						QuoteEvent data = (QuoteEvent) inData;
						MSymbol msymbol = data.getSymbol();
						if (!validateSymbol(symbol, msymbol)) {
							return;
						}
						MDDepthOfBookImpl item = getItem(key);
						List<MDQuote> list;
						if (data instanceof BidEvent) {
							list = item.getBids();
						} else if (data instanceof AskEvent) {
							list = item.getAsks();
						} else {
							// a new type of QuoteEvent is really unexpected, but we can ignore it
							assert false : inData;
							reportUnexpectedData(inData);
							return;
						}
						switch (data.getAction()) {
						case ADD:
							// add new item and map it
							MDQuoteImpl added = mFunction.apply(data);
							mIdMap.put(data.getMessageId(), added);
							list.add(added);
							break;
						case CHANGE:
							MDQuoteImpl changed = mIdMap.get(data.getMessageId());
							if (changed == null) {
								reportUnexpectedMessageId(data);
							} else {
								// update item
								changed.setSource(data.getExchange());
								changed.setTime(data.getTimeMillis());
								changed.setSize(data.getSize());
								changed.setPrice(data.getPrice());
							}
							break;
						case DELETE:
							MDQuoteImpl deleted = mIdMap.get(data.getMessageId());
							if (deleted == null) {
								reportUnexpectedMessageId(data);
							} else {
								// remove item
								list.remove(deleted);
							}
							break;
						default:
							// new action is not expected, but we can ignore it
							assert false : data.getAction();
							reportUnexpectedData(data);
							return;
						}
					} else {
						reportUnexpectedData(inData);
					}
				}
			}
		};
	}

	/**
	 * Transforms {@link QuoteEvent} into {@link MDQuoteImpl}.
	 */
	@ClassVersion("$Id$")
	private static class QuoteEventToMDQuote implements Function<QuoteEvent, MDQuoteImpl> {

		@Override
		public MDQuoteImpl apply(QuoteEvent from) {
			MDQuoteImpl quote = new MDQuoteImpl();
			quote.setSource(from.getExchange());
			quote.setTime(from.getTimeMillis());
			quote.setPrice(from.getPrice());
			quote.setSize(from.getSize());
			return quote;
		}
	}
}
