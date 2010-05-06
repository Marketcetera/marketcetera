package org.marketcetera.marketdata.csv;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.marketdata.AbstractMarketDataFeedFactory;
import org.marketcetera.marketdata.FeedException;
import org.marketcetera.core.CoreException;
import org.marketcetera.core.NoMoreIDsException;

/**
 * {@link CSVFeed} contructor factory
 * @author toli kuznets
 * @version $Id: CSVFeedFactory.java 4241 2009-06-11 01:26:00Z toli $
 */
@ClassVersion("$Id: CSVFeedFactory.java 4241 2009-06-11 01:26:00Z toli $")
public class CSVFeedFactory  
        extends AbstractMarketDataFeedFactory<CSVFeed,CSVFeedCredentials>
{
    /**
     * 
     *
     *
     * @return
     */
    public static CSVFeedFactory getInstance()
    {
        return sInstance;
    }
    /**
     * 
     */
	public String getProviderName()
	{
		return "Marketcetera (CSV)"; //$NON-NLS-1$
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
     * 
     */
    private final static CSVFeedFactory sInstance = new CSVFeedFactory();
}
