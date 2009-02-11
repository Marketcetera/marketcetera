package org.marketcetera.marketdata.marketcetera;

import org.marketcetera.core.CoreException;
import org.marketcetera.marketdata.AbstractMarketDataModule;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * StrategyAgent module for {@link MarketceteraFeed}.
 * 
 * <p>Note that in case of a credentials change via {@link #setSenderCompID(String)},
 * {@link #setTargetCompID(String)}, or {@link #setURL(String)}, this module must be
 * restarted for the changes to take effect.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$")  //$NON-NLS-1$
public class MarketceteraFeedModule
        extends AbstractMarketDataModule<MarketceteraFeedToken,
                                         MarketceteraFeedCredentials>
        implements MarketceteraFeedMXBean
{
    /**
     * Create a new MarketceteraFeedEmitter instance.
     * @throws CoreException 
     */
    MarketceteraFeedModule()
        throws CoreException
    {
        super(MarketceteraFeedModuleFactory.INSTANCE_URN,
              MarketceteraFeedFactory.getInstance().getMarketDataFeed());
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.marketcetera.MarketceteraFeedMXBean#getSenderCompID()
     */
    @Override
    public final String getSenderCompID()
    {
        return senderCompID;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.marketcetera.MarketceteraFeedMXBean#getTargetCompID()
     */
    @Override
    public final String getTargetCompID()
    {
        return targetCompID;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.marketcetera.MarketceteraFeedMXBean#getURL()
     */
    @Override
    public final String getURL()
    {
        return url;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.marketcetera.MarketceteraFeedMXBean#setSenderCompID(java.lang.String)
     */
    @Override
    public final void setSenderCompID(String inSenderCompID)
    {
        senderCompID = inSenderCompID;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.marketcetera.MarketceteraFeedMXBean#setTargetCompID(java.lang.String)
     */
    @Override
    public final void setTargetCompID(String inTargetCompID)
    {
        targetCompID = inTargetCompID;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.marketcetera.MarketceteraFeedMXBean#setURL(java.lang.String)
     */
    @Override
    public final void setURL(String inURL)
    {
        url = inURL;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.AbstractMarketDataModule#getCredentials()
     */
    @Override
    protected final MarketceteraFeedCredentials getCredentials()
            throws CoreException
    {
      return MarketceteraFeedCredentials.getInstance(getURL(),
                                                     getSenderCompID(),
                                                     getTargetCompID());
    }
    private String url;
    private String senderCompID;
    private String targetCompID;
}
