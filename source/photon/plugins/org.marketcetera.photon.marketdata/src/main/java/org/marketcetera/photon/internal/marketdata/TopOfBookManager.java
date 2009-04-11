package org.marketcetera.photon.internal.marketdata;

import org.marketcetera.event.AskEvent;
import org.marketcetera.event.BidEvent;
import org.marketcetera.event.SymbolExchangeEvent;
import org.marketcetera.marketdata.MarketDataRequest;
import org.marketcetera.marketdata.MarketDataRequest.Content;
import org.marketcetera.module.ModuleManager;
import org.marketcetera.photon.model.marketdata.MDTopOfBook;
import org.marketcetera.photon.model.marketdata.impl.MDTopOfBookImpl;
import org.marketcetera.util.misc.ClassVersion;

import com.google.inject.Inject;

/* $License$ */

/**
 * Manages Top of Book flows.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public class TopOfBookManager extends DataFlowManager<MDTopOfBook, TopOfBookKey> implements
		ITopOfBookManager {

	@Inject
	public TopOfBookManager(ModuleManager moduleManager) {
		super(moduleManager);
	}

	@Override
	protected MDTopOfBook createItem(TopOfBookKey key) {
		MDTopOfBookImpl item = new MDTopOfBookImpl();
		item.setSymbol(key.getSymbol());
		return item;
	}

	@Override
	protected void resetItem(MDTopOfBook item) {
		MDTopOfBookImpl impl = (MDTopOfBookImpl) item;
		impl.setAskPrice(null);
		impl.setAskSize(null);
		impl.setBidPrice(null);
		impl.setBidSize(null);
	}

	@Override
	protected Subscriber createSubscriber(final TopOfBookKey key) {
		final String symbol = key.getSymbol();
		final MarketDataRequest request = MarketDataRequest.newRequest().withSymbols(symbol).withContent(Content.TOP_OF_BOOK);
		return new Subscriber() {

		    @Override
		    public MarketDataRequest getRequest() {
		        return request;
		    }

		    @Override
		    public void receiveData(Object inData) {
		        synchronized (TopOfBookManager.this) {
		            MDTopOfBookImpl item = (MDTopOfBookImpl) getItem(key);
		            if (inData instanceof SymbolExchangeEvent
		                    && !validateSymbol(symbol, (SymbolExchangeEvent) inData)) {
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
