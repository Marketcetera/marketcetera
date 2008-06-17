package org.marketcetera.marketdata;

/**
 * Encapsulates the credentials necessary to authenticate a connection with an 
 * {@link IMarketDataFeed} instance.
 * 
 * <p>Subclasses should override and add attributes and methods appropriate for
 * the data feed.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 0.43-SNAPSHOT
 */
public abstract class AbstractMarketDataFeedCredentials
    implements IMarketDataFeedCredentials
{
    /**
     * the URL describing the server resource or resources to which to connect
     */
    private final String mURL;
    /**
     * Create a new <code>AbstractMarketDataFeedCredentials</code> instance.
     *
     * @param inURL a <code>String</code> value
     * @throws FeedException if the credentials object could not be constructed
     */
    protected AbstractMarketDataFeedCredentials(String inURL) 
        throws FeedException
    {
        mURL = inURL;
        try {
            validateURL(inURL);
        } catch (Throwable t) {
            throw new FeedException(t);
        }
    }  
    /**
     * Perform necessary validation on the given URL.
     * 
     * <p>This implementation does nothing.
     *
     * @param inURL a <code>String</code> value
     * @throws FeedException if the URL is not valid for the Market Data Feed
     */
    protected void validateURL(String inURL)
        throws FeedException
    {
        // do nothing
    }
    /**
     * @return the uRL
     */
    public final String getURL()
    {
        return mURL;
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
        final AbstractMarketDataFeedCredentials other = (AbstractMarketDataFeedCredentials) obj;
        if (mURL == null) {
            if (other.mURL != null)
                return false;
        } else if (!mURL.equals(other.mURL))
            return false;
        return true;
    }
}
