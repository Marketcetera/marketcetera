package org.marketcetera.bogusfeed;

import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.marketcetera.core.ClassVersion;
import org.marketcetera.marketdata.AbstractMarketDataFeedCredentials;
import org.marketcetera.marketdata.FeedException;

/* $License$ */

/**
 * Credentials implementation for {@link BogusFeed}.
 * 
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @since 0.5.0
 */
@ClassVersion("$Id$")
public class BogusFeedCredentials
	extends AbstractMarketDataFeedCredentials
{
    /**
     * Retrieves an instance of <code>BogusFeedCredentials</code>.
     * 
     * @param inPreferenceStore a <code>ScopedPreferenceStore</code> value
     * @return a <code>BogusFeedCredentials</code> value
     * @throws FeedException if an error occurs while retrieving the credentials object
     */
    public static BogusFeedCredentials getInstance(ScopedPreferenceStore inPreferenceStore) 
        throws FeedException
    {
        return new BogusFeedCredentials();
    }
    /**
     * Creates a new <code>BogusFeedCredentials</code> instance.
     * 
     * @throws FeedException if an error occurs while constructing the credentials object
     */
	protected BogusFeedCredentials()
		throws FeedException 
	{
		super("http://bogusurl");
	}
}