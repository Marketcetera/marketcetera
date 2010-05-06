package org.marketcetera.marketdata.csv;

import static org.marketcetera.marketdata.csv.Messages.INVALID_EVENT_DELAY;
import static org.marketcetera.marketdata.csv.Messages.INVALID_EVENT_TRANSLATOR;

import org.marketcetera.marketdata.AbstractMarketDataFeedCredentials;
import org.marketcetera.marketdata.FeedException;
import org.marketcetera.util.log.I18NBoundMessage1P;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.misc.ClassVersion;

/**
 * Encapsulates the data necessary to initialize an instance of {@link CSVFeed}.
 * 
 * @author toli kuznets
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @since $Release$
 * @version $Id: CSVFeedCredentials.java 4348 2009-09-24 02:33:11Z toli $
 */
@ClassVersion("$Id: CSVFeedCredentials.java 4348 2009-09-24 02:33:11Z toli $")
public final class CSVFeedCredentials 
	    extends AbstractMarketDataFeedCredentials
{
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return String.format("CSVFeedCredentials [eventTranslator=%s, millisecondDelay=%s]",
                             eventTranslator,
                             millisecondDelay);
    }
    /**
     * Retrieves an instance of <code>CSVFeedCredentials</code>.
     * 
     * @param inDelay a <code>long</code> value containing the number of milliseconds to delay between events
     * @param inEventTranslatorClassname a <code>String</code> value containing the fully-qualified name of the event translator class
     * @return a <code>CSVFeedCredentials</code> value
     * @throws FeedException if an error occurs while retrieving the credentials object
     */
    static CSVFeedCredentials getInstance(long inDelay,
                                          String inEventTranslatorClassname)
            throws FeedException
    {
        SLF4JLoggerProxy.debug(CSVFeedCredentials.class,
                               "Creating credentials with delay of {}ms and event translator classname {}",
                               inDelay,
                               inEventTranslatorClassname);
        try {
            return new CSVFeedCredentials(inDelay,
                                          inEventTranslatorClassname);
        } catch (FeedException e) {
            throw e;
        } catch (Exception e) {
            INVALID_EVENT_TRANSLATOR.error(CSVFeedCredentials.class,
                                           e,
                                           inEventTranslatorClassname);
            throw new FeedException(e,
                                    new I18NBoundMessage1P(INVALID_EVENT_TRANSLATOR,
                                                           inEventTranslatorClassname));
        }
    }
    /**
     * Retrieves an instance of <code>CSVFeedCredentials</code>.
     * 
     * @param inDelay a <code>long</code> value containing the number of milliseconds to delay between events
     * @param inEventTranslatorClassname a <code>CSVFeedEventTranslator</code> value containing the event translator to use
     * @return a <code>CSVFeedCredentials</code> value
     * @throws FeedException if an error occurs while retrieving the credentials object
     */
    static CSVFeedCredentials getInstance(long inDelay,
                                          CSVFeedEventTranslator inEventTranslator)
            throws FeedException
    {
        SLF4JLoggerProxy.debug(CSVFeedCredentials.class,
                               "Creating credentials with delay of {}ms and event translator {}",
                               inDelay,
                               inEventTranslator);
        try {
            return new CSVFeedCredentials(inDelay,
                                          inEventTranslator);
        } catch (Exception e) {
            INVALID_EVENT_TRANSLATOR.error(CSVFeedCredentials.class,
                                           e,
                                           inEventTranslator);
            throw new FeedException(e,
                                    new I18NBoundMessage1P(INVALID_EVENT_TRANSLATOR,
                                                           String.valueOf(inEventTranslator)));
        }
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
     * @throws ClassNotFoundException if the given classname does not exist in the classpath 
     * @throws IllegalAccessException if the class referred to by the classname is not accessible
     * @throws InstantiationException if the class referred to by the classname cannot be instantiated
     * @throws FeedException if the given delay is invalid 
     */
	private CSVFeedCredentials(long inDelay,
	                           String inEventTranslatorClassname)
	        throws InstantiationException, IllegalAccessException, ClassNotFoundException, FeedException
	{
        this(inDelay,
             (CSVFeedEventTranslator)Class.forName(inEventTranslatorClassname).newInstance());
	}
    /**
     * Creates a new <code>CSVFeedCredentials</code> instance.
     * 
     * @param inDelay a <code>long</code> value containing the number of milliseconds to delay between market data events
     * @param inEventTranslatorClassname a <code>String</code> value containing the fully-qualified name of the event translator class
     * @throws FeedException if an error occurs while constructing the credentials object
     */
    private CSVFeedCredentials(long inDelay,
                               CSVFeedEventTranslator inEventTranslator)
            throws FeedException 
    {
        if(inDelay < 0) {
            throw new FeedException(INVALID_EVENT_DELAY);
        }
        if(inEventTranslator == null) {
            throw new NullPointerException();
        }
        millisecondDelay = inDelay;
        eventTranslator = inEventTranslator;
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