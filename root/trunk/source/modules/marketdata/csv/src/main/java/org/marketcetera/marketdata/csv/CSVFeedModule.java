package org.marketcetera.marketdata.csv;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.marketdata.AbstractMarketDataModule;
import org.marketcetera.core.CoreException;

/**
 * StrategyAgent module for {@link CSVFeed}.
 * 
 * @author toli kuznets
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: CSVFeedModule.java 4348 2009-09-24 02:33:11Z toli $
 */
@ClassVersion("$Id: CSVFeedModule.java 4348 2009-09-24 02:33:11Z toli $")
public class CSVFeedModule 
        extends AbstractMarketDataModule<CSVFeedToken,
                                         CSVFeedCredentials>
        implements CSVFeedMXBean
{
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
        return CSVFeedCredentials.getInstance(getDelay(),
                                              getEventTranslatorClassName());
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.csv.CSVFeedMXBean#getDelay()
     */
    @Override
    public long getDelay()
    {
        return delay;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.csv.CSVFeedMXBean#setDelay(long)
     */
    @Override
    public void setDelay(long inDelay)
    {
        delay = inDelay;
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
    /**
     * 
     */
    private volatile long delay = 0;
    /**
     * 
     */
    private volatile String eventTranslatorClassname = CSVFeedEventTranslator.class.getName();
}
