package org.marketcetera.marketdata.csv;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.marketdata.AbstractMarketDataModule;
import org.marketcetera.core.CoreException;

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
     * @see org.marketcetera.marketdata.csv.CSVFeedMXBean#getDelay()
     */
    @Override
    public String getDelay()
    {
        return Long.toString(delay);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.csv.CSVFeedMXBean#setDelay(java.lang.String)
     */
    @Override
    public void setDelay(String inDelay)
    {
        delay = Long.parseLong(inDelay);
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
        return CSVFeedCredentials.getInstance(delay,
                                              getEventTranslatorClassName());
    }
    /**
     * the delay value to use 
     */
    private volatile long delay = 0;
    /**
     * the event translator classname to use
     */
    private volatile String eventTranslatorClassname = BasicCSVFeedEventTranslator.class.getName();
}
