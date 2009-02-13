package org.marketcetera.marketdata;

import static org.marketcetera.marketdata.Messages.NULL_URL;

import org.marketcetera.core.ClassVersion;

/* $License$ */

/**
 * Default implementation of <code>MarketDataFeedURLCredentials</code>.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 1.1.0
 */
@ClassVersion("$Id$")
public abstract class AbstractMarketDataFeedURLCredentials
        implements MarketDataFeedURLCredentials
{
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.MarketDataFeedURLCredentials#getURL()
     */
    @Override
    public String getURL()
    {
        return mURL;
    }
    /**
     * Create a new <code>AbstractMarketDataFeedCredentials</code> instance.
     *
     * @param inURL a <code>String</code> value
     * @throws FeedException if the credentials object could not be constructed
     */
    protected AbstractMarketDataFeedURLCredentials(String inURL) 
        throws FeedException
    {
        mURL = inURL;
        validateURL(inURL);
    }  
    /**
     * Perform necessary validation on the given URL.
     * 
     * @param inURL a <code>String</code> value
     * @throws FeedException if the URL is not valid for the Market Data Feed
     */
    protected void validateURL(String inURL)
        throws FeedException
    {
        if(inURL == null ||
           inURL.trim().isEmpty()) {
            NULL_URL.error(AbstractMarketDataFeed.DATAFEED_STATUS_MESSAGES);
            throw new FeedException(NULL_URL);
        }
    }
    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    public int hashCode()
    {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + ((mURL == null) ? 0 : mURL.hashCode());
        return result;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final AbstractMarketDataFeedURLCredentials other = (AbstractMarketDataFeedURLCredentials) obj;
        if (mURL == null) {
            if (other.mURL != null)
                return false;
        } else if (!mURL.equals(other.mURL))
            return false;
        return true;
    }
    /**
     * the URL describing the server resource or resources to which to connect
     */
    private final String mURL;
}
