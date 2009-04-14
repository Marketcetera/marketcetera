package org.marketcetera.photon.internal.marketdata;

import org.marketcetera.event.TradeEvent;
import org.marketcetera.marketdata.MarketDataRequest;
import org.marketcetera.marketdata.MarketDataRequest.Content;
import org.marketcetera.module.ModuleManager;
import org.marketcetera.photon.model.marketdata.MDLatestTick;
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
public class LatestTickManager extends DataFlowManager<MDLatestTick, LatestTickKey> implements
		ILatestTickManager {

	@Inject
	public LatestTickManager(ModuleManager moduleManager) {
		super(moduleManager);
	}

	@Override
	protected MDLatestTick createItem(LatestTickKey key) {
		MDLatestTickImpl item = new MDLatestTickImpl();
		item.setSymbol(key.getSymbol());
		return item;
	}

	@Override
	protected void resetItem(MDLatestTick item) {
		MDLatestTickImpl impl = (MDLatestTickImpl) item;
		impl.setPrice(null);
		impl.setSize(null);
	}

	@Override
	protected Subscriber createSubscriber(final LatestTickKey key) {
		final String symbol = key.getSymbol();
		final MarketDataRequest request = MarketDataRequest.newRequest().withSymbols(symbol).withContent(Content.LATEST_TICK);
		return new Subscriber() {

		    @Override
		    public MarketDataRequest getRequest() {
		        return request;
		    }

		    @Override
		    public void receiveData(Object inData) {
		        synchronized (LatestTickManager.this) {
		            MDLatestTickImpl item = (MDLatestTickImpl) getItem(key);
		            if (inData instanceof TradeEvent) {
		                TradeEvent data = (TradeEvent) inData;
		                if (!validateSymbol(symbol, data))
		                    return;
		                item.setPrice(data.getPrice());
		                item.setSize(data.getSize());
		            } else {
		                reportUnexpectedData(inData);
		            }
		        }
		    }
		};
	}
}
