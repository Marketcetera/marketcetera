package org.marketcetera.marketdata.csv;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.marketdata.AbstractMarketDataFeedFactory;
import org.marketcetera.marketdata.FeedException;
import org.marketcetera.core.CoreException;
import org.marketcetera.core.NoMoreIDsException;

/**
 * Provides instances of {@link CSVFeed}.
 * 
 * @author toli kuznets
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @since 2.1.0
 * @version $Id$
 */
@ClassVersion("$Id$")
public class CSVFeedFactory  
        extends AbstractMarketDataFeedFactory<CSVFeed,CSVFeedCredentials>
{
    /**
     * Gets an instance of <code>CSVFeedFactory</code>. 
     *
     * @return a <code>CSVFeedFactory</code> instance
     */
    public static CSVFeedFactory getInstance()
    {
        return sInstance;
    }
    /**
     * Gets the provider name of <code>CSVFeed</code>.
     * 
     * @return a <code>String</code> value
     */
	public String getProviderName()
	{
		return provider;
	}
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.IMarketDataFeedFactory#getMarketDataFeed()
     */
    @Override
    public CSVFeed getMarketDataFeed()
            throws CoreException
    {
        try {
            return CSVFeed.getInstance(getProviderName());
        } catch (NoMoreIDsException e) {
            throw new FeedException(e);
        }
    }
    /**
     * the singleton instance
     */
    private final static CSVFeedFactory sInstance = new CSVFeedFactory();
    /**
     * the provider name
     */
    private final static String provider = "Marketcetera (CSV)"; //$NON-NLS-1$
}
