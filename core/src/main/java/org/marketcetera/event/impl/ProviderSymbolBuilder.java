package org.marketcetera.event.impl;

/* $License$ */

/**
 * Builds market data events that have a provider symbol.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface ProviderSymbolBuilder<B>
{
    /**
     * Set the market data symbol used by the market data provider.
     *
     * @param inProviderSymbol a <code>String</code> value
     * @return a <code>B</code> value
     */
    B withProviderSymbol(String inProviderSymbol);
}
