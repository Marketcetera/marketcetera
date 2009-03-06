package org.marketcetera.core.position.impl;

import java.beans.PropertyChangeListener;
import java.math.BigDecimal;

import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Interface providing market data for position-related calculations.
 * 
 * Property change listeners can subscribe to be notified when the last trade
 * price changes for a symbol. Note that {@link PropertyChangeListener} is being
 * re-used here outside its original intent as a Java Bean property change
 * notification mechanism. The "property" in this case is the symbol and the
 * value is the symbol's last trade price.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public interface PositionMarketData {

    /**
     * Returns the price of the last trade for the given symbol.
     * 
     * @param symbol
     *            the symbol in question, cannot be null
     * @return the last trade price, or null if unknown
     * @throws IllegalArgumentException
     *             if symbol is null
     */
    BigDecimal getLastTradePrice(String symbol);

    /**
     * Returns the closing price for the given symbol.
     * 
     * TODO: closing price for which day?
     * 
     * @param symbol
     *            the symbol in question, cannot be null
     * @return the closing price, or null if unknown
     * @throws IllegalArgumentException
     *             if symbol is null
     */
    BigDecimal getClosingPrice(String symbol);

    /**
     * Add a PropertyChangeListener for a specific market data symbol. The
     * listener will be notified when the last trade price is updated for the
     * given symbol. The same listener object may be added more than once. For
     * each symbol, the listener will be invoked the number of times it was
     * added for that symbol. If <code>symbol</code> or <code>listener</code> is
     * null, no exception is thrown and no action is taken.
     * 
     * @param symbol
     *            the name of the symbol to listen on
     * @param listener
     *            the PropertyChangeListener to be added
     */
    void addSymbolTradeListener(String symbol, PropertyChangeListener listener);

    /**
     * Remove a PropertyChangeListener for a specific symbol. If
     * <code>listener</code> was added more than once for the specified
     * property, it will be notified one less time after being removed. If
     * <code>symbol</code> is null, no exception is thrown and no action is
     * taken. If <code>listener</code> is null, or was never added for the
     * specified property, no exception is thrown and no action is taken.
     * 
     * @param symbol
     *            the name of the symbol that was listened on
     * @param listener
     *            the PropertyChangeListener to be removed
     */
    void removeSymbolTradeListener(String symbol, PropertyChangeListener listener);

}
