package org.marketcetera.core.event;

/* $License$ */

/**
 * Indicates the implementer may have access to an original symbol from the provider.
 *
 * @version $Id: HasProviderSymbol.java 16063 2012-01-31 18:21:55Z colin $
 * @since 2.1.0
 */
public interface HasProviderSymbol
{
    /**
     * Returns the original provider symbol of the instrument, if available. 
     *
     * @return a <code>String</code> value or <code>null</code> if the event
     *  did not have a provider symbol
     */
    public String getProviderSymbol();
}
