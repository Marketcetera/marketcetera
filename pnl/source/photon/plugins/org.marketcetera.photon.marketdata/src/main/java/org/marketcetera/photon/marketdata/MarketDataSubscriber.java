package org.marketcetera.photon.marketdata;

import java.util.EventListener;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.Assert;
import org.marketcetera.util.misc.ClassVersion;


/**
 * Abstract base class of market data receiver module subscribers. 
 *
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$")
public abstract class MarketDataSubscriber implements EventListener {

	private final String[] mSymbols;

	/**
	 * Constructor.
	 * 
	 * @param symbols symbols for market data request
	 */
	public MarketDataSubscriber(String... symbols) {
		Assert.isLegal(symbols.length > 0);
		for (String symbol : symbols) {
			Assert.isLegal(StringUtils.isNotBlank(symbol));			
		}
		mSymbols = symbols;
	}

	/**
	 * Returns the symbols for the market data request.
	 * 
	 * @return the symbols for the market data request
	 */
	public final String[] getSymbols() {
		return mSymbols;
	}

	/**
	 * Callback to provide market data to be processed.
	 * 
	 * @param inData data from the market data flow
	 */
	public abstract void receiveData(Object inData);
}