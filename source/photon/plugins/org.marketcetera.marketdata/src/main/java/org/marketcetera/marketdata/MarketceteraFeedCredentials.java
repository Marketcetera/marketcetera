package org.marketcetera.marketdata;

import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.marketcetera.quickfix.ConnectionConstants;

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
    /**
     * Constructs a new <code>MarketceteraFeedCredentials</code> object.
     * 
     * @param inURL a <code>String</code> value
     * @throws FeedException if an error occurs
     */
    @Deprecated
    public MarketceteraFeedCredentials(String inURL) 
        throws FeedException
    {
        this(inURL,
             null,
             null);
    }
    /**
     * Constructs a new <code>MarketceteraFeedCredentials</code> object.
     * 
     * @param inURL a <code>String</code> value
     * @param inSenderCompID a <code>String</code> value
     * @param inTargetCompID a <code>String</code> value
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
            throw new NullPointerException();
        }
    }
    /**
     * Gets a <code>MarketceteraFeedCredentials</code> value.
     * 
     * @param inPreferences a <code>ScopedPreferenceStore</code> value
     * @return a <code>MarketceteraFeedCredentials</code> value
     * @throws FeedException if an error occurs retrieving the credentials
     */
    public static MarketceteraFeedCredentials getInstance(ScopedPreferenceStore inPreferences) 
        throws FeedException
    {
        String url = inPreferences.getString(ConnectionConstants.MARKETDATA_URL_SUFFIX);
        String senderCompID = inPreferences.getString(MarketceteraFeed.SETTING_SENDER_COMP_ID);
        String targetCompID = inPreferences.getString(MarketceteraFeed.SETTING_TARGET_COMP_ID);
        return new MarketceteraFeedCredentials(url,
                                               senderCompID,
                                               targetCompID);
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
}
