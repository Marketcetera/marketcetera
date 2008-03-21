package org.marketcetera.marketdata;

import org.marketcetera.core.MessageKey;

/**
 * Encapsulates the credentials necessary to authenticate a connection with an 
 * {@link IMarketDataFeed} instance.
 * 
 * <p>Subclasses should override and add attributes and methods appropriate for
 * the data feed.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: $
 * @since 0.43-SNAPSHOT
 */
public abstract class MarketDataFeedCredentials
{
    /**
     * the username with which to connect
     */
    private final String mUsername;
    /**
     * the password with which to connect
     */
    private final String mPassword;
    /**
     * the URL describing the server resource or resources to which to connect
     */
    private final String mURL;

    /**
     * Create a new <code>MarketDataFeedCredentials</code> instance.
     *
     * @param inURL a <code>String</code> value
     * @param inUsername a <code>String</code> value
     * @param inPassword a <code>String</code> value
     * @throws FeedException if the credentials object could not be constructed
     */
    protected MarketDataFeedCredentials(String inURL,
                                        String inUsername,
                                        String inPassword) 
        throws FeedException
    {
        try {
            validateURL(inURL);
        } catch (Throwable t) {
            throw new FeedException(MessageKey.ERROR_CREDENTIAL_URL_VALIDATION_FAILED.getLocalizedMessage(),
                                    t);
        }
        mURL = inURL;
        mUsername = inUsername;
        mPassword = inPassword;
    }
    
    /**
     * Perform necessary validation on the given URL.
     *
     * @param inURL a <code>String</code> value
     * @throws FeedException if the URL is not valid for the Market Data Feed
     */
    protected abstract void validateURL(String inURL)
        throws FeedException;

    /**
     * @return the password
     */
    protected String getPassword()
    {
        return mPassword;
    }

    /**
     * @return the uRL
     */
    protected String getURL()
    {
        return mURL;
    }

    /**
     * @return the username
     */
    protected String getUsername()
    {
        return mUsername;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    public int hashCode()
    {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + ((mPassword == null) ? 0 : mPassword.hashCode());
        result = PRIME * result + ((mURL == null) ? 0 : mURL.hashCode());
        result = PRIME * result + ((mUsername == null) ? 0 : mUsername.hashCode());
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
        final MarketDataFeedCredentials other = (MarketDataFeedCredentials) obj;
        if (mPassword == null) {
            if (other.mPassword != null)
                return false;
        } else if (!mPassword.equals(other.mPassword))
            return false;
        if (mURL == null) {
            if (other.mURL != null)
                return false;
        } else if (!mURL.equals(other.mURL))
            return false;
        if (mUsername == null) {
            if (other.mUsername != null)
                return false;
        } else if (!mUsername.equals(other.mUsername))
            return false;
        return true;
    }
}
