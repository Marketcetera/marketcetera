package org.marketcetera.marketdata.csv;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.marketdata.AbstractMarketDataFeedCredentials;
import org.marketcetera.marketdata.FeedException;

/**
 * Encapsulates the data necessary to initialize an instance of {@link CSVFeed}.
 * 
 * @author toli kuznets
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: CSVFeedCredentials.java 4348 2009-09-24 02:33:11Z toli $
 */
@ClassVersion("$Id: CSVFeedCredentials.java 4348 2009-09-24 02:33:11Z toli $")
public final class CSVFeedCredentials 
	    extends AbstractMarketDataFeedCredentials
{
    /**
     * Retrieves an instance of <code>CSVFeedCredentials</code>.
     * 
     * @param inDelay a <code>long</code> value containing the number of milliseconds to delay between events
     * @param inEventTranslatorClassname a <code>String</code> value containing the fully-qualified name of the event translator class
     * @return a <code>CSVFeedCredentials</code> value
     * @throws FeedException if an error occurs while retrieving the credentials object
     */
    public static CSVFeedCredentials getInstance(long inDelay,
                                                 String inEventTranslatorClassname)
            throws FeedException
    {
        SLF4JLoggerProxy.debug(CSVFeedCredentials.class,
                               "Creating credentials with delay of {}ms and event translator {}",
                               inDelay,
                               inEventTranslatorClassname);
        return new CSVFeedCredentials(inDelay,
                                      inEventTranslatorClassname);
    }
    /**
     * Gets the number of milliseconds to delay between market data events. 
     *
     * @return a <code>long</code> value
     */
    public long getMillisecondDelay()
    {
        return millisecondDelay;
    }
    /**
     * Get the eventTranslator value.
     *
     * @return a <code>CSVFeedEventTranslator</code> value
     */
    public CSVFeedEventTranslator getEventTranslator()
    {
        return eventTranslator;
    }
    /**
     * Creates a new <code>CSVFeedCredentials</code> instance.
     * 
     * @param inDelay a <code>long</code> value containing the number of milliseconds to delay between market data events
     * @param inEventTranslatorClassname a <code>String</code> value containing the fully-qualified name of the event translator class
     * @throws FeedException if an error occurs while constructing the credentials object
     */
	private CSVFeedCredentials(long inDelay,
	                           String inEventTranslatorClassname)
		    throws FeedException 
	{
        millisecondDelay = inDelay;
        try {
            Class<?> translatorClass = Class.forName(inEventTranslatorClassname);
            eventTranslator = (CSVFeedEventTranslator)translatorClass.newInstance();
        } catch (Exception e) {
            // TODO message
            throw new FeedException(e);
        }
	}
    /**
     * the number of milliseconds to delay between events
     */
    private final long millisecondDelay;
    /**
     * the event translator to use 
     */
    private final CSVFeedEventTranslator eventTranslator;
}