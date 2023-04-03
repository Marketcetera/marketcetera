package org.marketcetera.marketdata.bogus;

import org.marketcetera.core.CoreException;
import org.marketcetera.core.NoMoreIDsException;
import org.marketcetera.marketdata.AbstractMarketDataFeedFactory;
import org.marketcetera.marketdata.FeedException;
import org.marketcetera.util.misc.ClassVersion;

 /* $License$ */

/**
 * {@link BogusFeed} constructor factory.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 0.5.0
 */
@ClassVersion("$Id$")  //$NON-NLS-1$
public class BogusFeedFactory 
    extends AbstractMarketDataFeedFactory<BogusFeed,BogusFeedCredentials> 
{
    /**
     * Get the <code>BogusFeedFactory</code> value.
     *
     * @return a <code>BogusFeedFactory</code> value
     */
    public static BogusFeedFactory getInstance()
    {
        return sInstance;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.IMarketDataFeedFactory#getProviderName()
     */
    @Override
    public String getProviderName()
    {
        return BogusFeedModuleFactory.IDENTIFIER;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.IMarketDataFeedFactory#getMarketDataFeed()
     */
    @Override
    public BogusFeed getMarketDataFeed()
            throws CoreException
    {
        try {
            return BogusFeed.getInstance(getProviderName());
        } catch (NoMoreIDsException e) {
            throw new FeedException(e);
        }
    }
    /**
     * creates new {@link BogusFeed} objects
     */
    private final static BogusFeedFactory sInstance = new BogusFeedFactory();
}
