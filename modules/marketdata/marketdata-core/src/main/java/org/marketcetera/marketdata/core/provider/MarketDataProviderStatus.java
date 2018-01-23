package org.marketcetera.marketdata.core.provider;

import java.io.Serializable;

/* $License$ */

/**
 * Indicates the status of a market data provider.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class MarketDataProviderStatus
        implements Serializable
{
    /**
     * Get the provider value.
     *
     * @return a <code>String</code> value
     */
    public String getProvider()
    {
        return provider;
    }
    /**
     * Sets the provider value.
     *
     * @param a <code>String</code> value
     */
    public void setProvider(String inProvider)
    {
        provider = inProvider;
    }
    /**
     * Get the isAvailable value.
     *
     * @return a <code>boolean</code> value
     */
    public boolean getIsAvailable()
    {
        return isAvailable;
    }
    /**
     * Sets the isAvailable value.
     *
     * @param a <code>boolean</code> value
     */
    public void setIsAvailable(boolean inIsAvailable)
    {
        isAvailable = inIsAvailable;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("MarketDataProviderStatus [").append(provider).append(isAvailable?" is available":" is not available").append("]");
        return builder.toString();
    }
    /**
     * Create a new MarketDataProviderStatus instance.
     *
     * @param inProvider a <code>String</code> value
     * @param inIsAvailable a <code>boolean</code> value
     */
    public MarketDataProviderStatus(String inProvider,
                                    boolean inIsAvailable)
    {
        provider = inProvider;
        isAvailable = inIsAvailable;
    }
    /**
     * Create a new MarketDataProviderStatus instance.
     */
    public MarketDataProviderStatus() {}
    /**
     * indicates the provider
     */
    private String provider;
    /**
     * indicates if the provider is available
     */
    private boolean isAvailable;
    private static final long serialVersionUID = 2241394608997062350L;
}
