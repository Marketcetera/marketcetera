package org.marketcetera.marketdata.csv;

import org.apache.commons.lang.StringUtils;
import org.marketcetera.core.CoreException;
import org.marketcetera.marketdata.AbstractMarketDataModule;
import org.marketcetera.util.misc.ClassVersion;

/**
 * StrategyAgent module for {@link CSVFeed}.
 * 
 * @author toli kuznets
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @since 2.1.0
 * @version $Id: CSVFeedModule.java 4348 2009-09-24 02:33:11Z toli $
 */
@ClassVersion("$Id: CSVFeedModule.java 4348 2009-09-24 02:33:11Z toli $")
public class CSVFeedModule
        extends AbstractMarketDataModule<CSVFeedToken,
                                         CSVFeedCredentials>
        implements CSVFeedMXBean
{
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
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.csv.CSVFeedMXBean#getEventTranslatorClassName()
     */
    @Override
    public String getEventTranslatorClassName()
    {
        return eventTranslatorClassname;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.csv.CSVFeedMXBean#setEventTranslatorClassName(java.lang.String)
     */
    @Override
    public void setEventTranslatorClassName(String inEventTranslatorClassname)
    {
        eventTranslatorClassname = inEventTranslatorClassname;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.csv.CSVFeedMXBean#getReplayEvents()
     */
    @Override
    public String getReplayEvents()
    {
        return String.valueOf(replayEvents);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.csv.CSVFeedMXBean#setReplayEvents(String)
     */
    @Override
    public void setReplayEvents(String inReplayEvents)
    {
        replayEvents = Boolean.valueOf(StringUtils.trimToNull(inReplayEvents));
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return String.format("CSVFeedModule [translator: %s]", //$NON-NLS-1$
                             eventTranslatorClassname);
    }
    /**
     * Create a new CSVFeedModule instance.
     * 
     * @throws org.marketcetera.core.CoreException 
     */
    CSVFeedModule()
            throws CoreException
    {
        super(CSVFeedModuleFactory.INSTANCE_URN,
              CSVFeedFactory.getInstance().getMarketDataFeed());
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.AbstractMarketDataModule#getCredentials()
     */
    @Override
    protected CSVFeedCredentials getCredentials()
        throws CoreException
    {
        return CSVFeedCredentials.getInstance(replayRate,
                                              replayEvents,
                                              marketdataDirectory,
                                              getEventTranslatorClassName());
    }
    /**
     * the event translator classname to use
     */
    private volatile String eventTranslatorClassname = BasicCSVFeedEventTranslator.class.getName();
    /**
     * delay between each event
     */
    private volatile long replayRate = 1000;
    /**
     * the directory in which to find marketdata
     */
    private volatile String marketdataDirectory;
    /**
     * indicates if events should be replayed upon completion
     */
    private volatile boolean replayEvents = false;
}
