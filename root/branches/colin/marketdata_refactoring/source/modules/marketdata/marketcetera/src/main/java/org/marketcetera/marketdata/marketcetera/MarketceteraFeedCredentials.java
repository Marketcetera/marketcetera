package org.marketcetera.marketdata.marketcetera;

import org.marketcetera.marketdata.AbstractMarketDataFeedCredentials;
import org.marketcetera.marketdata.FeedException;

/**
 * Credentials instance for <code>MarketceteraFeed</code>.
 * 
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 */
public class MarketceteraFeedCredentials
    extends AbstractMarketDataFeedCredentials
{
	/**
	 * the sender comp id
	 */
    private final String mSenderCompID;
    /**
     * the target comp id
     */
    private final String mTargetCompID;
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
           mTargetCompID.length() == 0) {
            throw new IllegalArgumentException();
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
}
