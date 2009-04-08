package org.marketcetera.core.position.impl;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.math.BigDecimal;

import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Implementation of {@link PositionMarketData}.
 * 
 * TODO: implement this class
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public class PositionMarketDataImpl implements PositionMarketData {

    @Override
    public BigDecimal getClosingPrice(String symbol) {
        return null;
    }

    @Override
    public BigDecimal getLastTradePrice(String symbol) {
        return null;
    }

    // Boiler plate property change code

    private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    /**
     * @return {@link PropertyChangeSupport} for this class
     */
    protected PropertyChangeSupport getPropertyChangeSupport() {
        return propertyChangeSupport;
    }

    @Override
    public void addSymbolTradeListener(String symbol, PropertyChangeListener listener) {
        getPropertyChangeSupport().addPropertyChangeListener(symbol, listener);
    }

    @Override
    public void removeSymbolTradeListener(String symbol, PropertyChangeListener listener) {
        getPropertyChangeSupport().removePropertyChangeListener(symbol, listener);
    }

}
