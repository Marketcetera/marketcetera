package org.marketcetera.marketdata.csv;

import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.marketcetera.marketdata.AssetClass;
import org.marketcetera.marketdata.Capability;
import org.marketcetera.marketdata.MarketDataListener;
import org.marketcetera.marketdata.MarketDataRequest;
import org.marketcetera.marketdata.NewAbstractMarketDataFeed;
import org.marketcetera.util.misc.ClassVersion;

/**
 * StrategyAgent module for {@link CSVFeed}.
 * 
 * @author toli kuznets
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @since 2.1.0
 * @version $Id$
 */
@ClassVersion("$Id$")
public class CSVFeedModule
        extends NewAbstractMarketDataFeed
        implements CSVFeedMXBean
{
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.NewAbstractMarketDataFeed#doMarketDataRequest(org.marketcetera.marketdata.MarketDataRequest, java.lang.String, org.marketcetera.marketdata.MarketDataListener)
     */
    @Override
    protected void doMarketDataRequest(MarketDataRequest inMarketDataRequest,
                                       String inMarketDataRequestId,
                                       MarketDataListener inMarketDataListener)
    {
        throw new UnsupportedOperationException(); // TODO
        
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.NewAbstractMarketDataFeed#doCancel(java.lang.String)
     */
    @Override
    protected void doCancel(String inMarketDataRequestId)
    {
        throw new UnsupportedOperationException(); // TODO
        
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.NewAbstractMarketDataFeed#doGetCapabilities()
     */
    @Override
    protected Set<Capability> doGetCapabilities()
    {
        throw new UnsupportedOperationException(); // TODO
        
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.NewAbstractMarketDataFeed#doGetAssetClasses()
     */
    @Override
    protected Set<AssetClass> doGetAssetClasses()
    {
        throw new UnsupportedOperationException(); // TODO
        
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.NewAbstractMarketDataFeed#getProviderName()
     */
    @Override
    protected String getProviderName()
    {
        throw new UnsupportedOperationException(); // TODO
        
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.csv.CSVFeedMXBean#getReplayRate()
     */
    @Override
    public String getReplayRate()
    {
        return String.valueOf(replayRate);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.csv.CSVFeedMXBean#setReplayRate(double)
     */
    @Override
    public void setReplayRate(String inReplayRate)
    {
        replayRate = Long.parseLong(StringUtils.trimToNull(inReplayRate));
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.csv.CSVFeedMXBean#getMarketdataDirectory()
     */
    @Override
    public String getMarketdataDirectory()
    {
        return marketdataDirectory;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.csv.CSVFeedMXBean#setMarketdataDirectory(java.lang.String)
     */
    @Override
    public void setMarketdataDirectory(String inDirectory)
    {
        marketdataDirectory = StringUtils.trimToNull(inDirectory);
    }
//    /* (non-Javadoc)
//     * @see org.marketcetera.marketdata.AbstractMarketDataModule#getCredentials()
//     */
//    @Override
//    protected CSVFeedCredentials getCredentials()
//        throws CoreException
//    {
//        return CSVFeedCredentials.getInstance(replayRate,
//                                              replayEvents,
//                                              marketdataDirectory,
//                                              getEventTranslatorClassName());
//    }
    /**
     * delay between each event
     */
    private volatile long replayRate = 1000;
    /**
     * the directory in which to find marketdata
     */
    private volatile String marketdataDirectory;
}
