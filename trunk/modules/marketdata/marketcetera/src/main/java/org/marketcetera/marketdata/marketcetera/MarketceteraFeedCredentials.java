package org.marketcetera.marketdata.marketcetera;

import static org.marketcetera.marketdata.marketcetera.Messages.TARGET_COMP_ID_REQUIRED;
import static org.marketcetera.marketdata.marketcetera.Messages.URL_REQUIRED;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.marketdata.AbstractMarketDataFeed;
import org.marketcetera.marketdata.AbstractMarketDataFeedURLCredentials;
import org.marketcetera.marketdata.FeedException;

/* $License$ */

/**
 * Credentials instance for <code>MarketceteraFeed</code>.
 * 
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 0.6.0
*/
@ClassVersion("$Id$") //$NON-NLS-1$
public class MarketceteraFeedCredentials
    extends AbstractMarketDataFeedURLCredentials
{
	/**
	 * the sender comp id
	 */
    private final String mSenderCompID;
    /**
     * the target comp id
     */
    private final String mTargetCompID;
    /**
     * Gets a <code>MarketceteraFeedCredentials</code> instance.
     *
     * @param inURL a <code>String</code> value
     * @param inSenderCompID a <code>String</code> value
     * @param inTargetCompID a <code>String</code> value
     * @return a <code>MarketceteraFeedCredentials</code> value
     * @throws FeedException if an error occurs construction the credentials object
     */
    public static MarketceteraFeedCredentials getInstance(String inURL,
                                                          String inSenderCompID,
                                                          String inTargetCompID)
        throws FeedException
    {
        return new MarketceteraFeedCredentials(inURL,
                                               inSenderCompID,
                                               inTargetCompID);
    }
    /**
     * Constructs a new <code>MarketceteraFeedCredentials</code> object.
     * 
     * @param inURL a <code>String</code> value
     * @param inSenderCompID a <code>String</code> value
     * @param inTargetCompID a <code>String</code> value
     * @throws IllegalArgumentException if <code>inTargetCompID</code> is null or is of zero length
     * @throws FeedException if an error occurs
     */
    private MarketceteraFeedCredentials(String inURL,
                                        String inSenderCompID,
                                        String inTargetCompID)
        throws FeedException
    {
        super(inURL);
        mSenderCompID = inSenderCompID;
        mTargetCompID = inTargetCompID;
        if(mTargetCompID == null ||
           mTargetCompID.trim().isEmpty()) {
            TARGET_COMP_ID_REQUIRED.error(AbstractMarketDataFeed.DATAFEED_STATUS_MESSAGES);
            throw new FeedException(TARGET_COMP_ID_REQUIRED);
        }
    }
    /**
     * Gets the sender comp ID for this credentials object.
     * 
     * @return a <code>String</code> value
     */
    public String getSenderCompID()
    {
        return mSenderCompID;
    }
    /**
     * Gets the target comp ID for this credentials object.
     * 
     * @return a <code>String</code> value
     */
    public String getTargetCompID()
    {
        return mTargetCompID;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder output = new StringBuilder();
        output.append("Marketcetera Feed Credentials: URL=").append(getURL()).append(" SenderCompID=").append(getSenderCompID()).append(" TargetCompID=").append(getTargetCompID()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        return output.toString();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.AbstractMarketDataFeedCredentials#validateURL(java.lang.String)
     */
    @Override
    protected void validateURL(String inURL)
            throws FeedException
    {
        super.validateURL(inURL);
        if(inURL == null ||
           inURL.isEmpty()) {
            URL_REQUIRED.error(AbstractMarketDataFeed.DATAFEED_STATUS_MESSAGES);
            throw new FeedException(URL_REQUIRED);
        }
    }
}
