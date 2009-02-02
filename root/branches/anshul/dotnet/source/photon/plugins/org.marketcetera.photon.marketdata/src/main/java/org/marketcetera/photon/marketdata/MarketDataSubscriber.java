package org.marketcetera.photon.marketdata;

import java.util.EventListener;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.Assert;
import org.marketcetera.util.misc.ClassVersion;


/**
 * Abstract base class of market data receiver module subscribers. 
 *
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id: MarketDataReceiverModule.java 10267 2008-12-24 16:25:11Z colin $
 * @since 1.0.0
 */
@ClassVersion("$Id$")
public abstract class MarketDataSubscriber implements EventListener {

	private final String mSymbol;

	/**
	 * Constructor.
	 * 
	 * @param symbol symbol for market data request
	 */
	public MarketDataSubscriber(String symbol) {
		Assert.isLegal(StringUtils.isNotBlank(symbol));
		mSymbol = symbol;
	}

	/**
	 * Returns the symbol for the market data request.
	 * 
	 * @return the symbol for the market data request
	 */
	public final String getSymbol() {
		return mSymbol;
	}

	/**
	 * Callback to provide market data to be processed.
	 * 
	 * @param inData data from the market data flow
	 */
	public abstract void receiveData(Object inData);
}