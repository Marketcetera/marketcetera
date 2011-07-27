package org.marketcetera.marketdata.csv;

import static org.marketcetera.marketdata.csv.Messages.INVALID_EVENT_TRANSLATOR;

import java.io.File;

import org.apache.commons.lang.Validate;
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
 * @since 2.1.0
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
        return String.format("CSVFeedCredentials [eventTranslator=%s, millisecondDelay=%s]", //$NON-NLS-1$
                             eventTranslator,
                             replayRate);
    }
    /**
     * Retrieves an instance of <code>CSVFeedCredentials</code>.
     * 
     * @param inReplayRate a <code>double</code> value containing the rate at which to replay marketdata
     * @param inMarketdataDirectory a <code>String</code> value containing the marketdata files
     * @param inEventTranslatorClassname a <code>String</code> value containing the fully-qualified name of the event translator class
     * @return a <code>CSVFeedCredentials</code> value
     * @throws FeedException if an error occurs while retrieving the credentials object
     */
    static CSVFeedCredentials getInstance(double inReplayRate,
                                          String inMarketdataDirectory,
                                          String inEventTranslatorClassname)
            throws FeedException
    {
        SLF4JLoggerProxy.debug(CSVFeedCredentials.class,
                               "Creating credentials at a replay rate of {}, marketdata directory {}, and event translator classname {}", //$NON-NLS-1$
                               inReplayRate,
                               inMarketdataDirectory,
                               inEventTranslatorClassname);
        try {
            return new CSVFeedCredentials(inReplayRate,
                                          inMarketdataDirectory,
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
     * @param inReplayRate a <code>double</code> value containing the rate at which to replay marketdata
     * @param inMarketdataDirectory a <code>String</code> value containing the marketdata files
     * @return a <code>CSVFeedCredentials</code> value
     * @throws FeedException if an error occurs while retrieving the credentials object
     */
    static CSVFeedCredentials getInstance(double inReplayRate,
                                          String inMarketdataDirectory,
                                          CSVFeedEventTranslator inEventTranslator)
            throws FeedException
    {
        SLF4JLoggerProxy.debug(CSVFeedCredentials.class,
                               "Creating credentials at a replay rate of {}, marketdata directory {}, and event translator classname {}", //$NON-NLS-1$
                               inReplayRate,
                               inMarketdataDirectory,
                               inEventTranslator);
        try {
            return new CSVFeedCredentials(inReplayRate,
                                          inMarketdataDirectory,
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
     * Get the marketdataDirectory value.
     *
     * @return a <code>File</code> value
     */
    public File getMarketdataDirectory()
    {
        return marketdataDirectory;
    }
    /**
     * Get the replayRate value.
     *
     * @return a <code>double</code> value
     */
    public double getReplayRate()
    {
        return replayRate;
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
     * @param inReplayRate a <code>double</code> value containing the rate at which to replay marketdata
     * @param inMarketdataDirectory a <code>String</code> value containing the marketdata files
     * @param inEventTranslatorClassname a <code>String</code> value containing the fully-qualified name of the event translator class
     * @throws ClassNotFoundException if the given classname does not exist in the classpath 
     * @throws IllegalAccessException if the class referred to by the classname is not accessible
     * @throws InstantiationException if the class referred to by the classname cannot be instantiated
     * @throws FeedException if the given delay is invalid 
     */
	private CSVFeedCredentials(double inReplayRate,
	                           String inMarketdataDirectory,
	                           String inEventTranslatorClassname)
	        throws InstantiationException, IllegalAccessException, ClassNotFoundException, FeedException
	{
        this(inReplayRate,
             inMarketdataDirectory,
             (CSVFeedEventTranslator)Class.forName(inEventTranslatorClassname).newInstance());
	}
    /**
     * Creates a new <code>CSVFeedCredentials</code> instance.
     * 
     * @param inReplayRate a <code>double</code> value containing the rate at which to replay marketdata
     * @param inMarketdataDirectory a <code>String</code> value containing the marketdata files
     * @param inEventTranslatorClassname a <code>String</code> value containing the fully-qualified name of the event translator class
     * @throws FeedException if an error occurs while constructing the credentials object
     */
    private CSVFeedCredentials(double inReplayRate,
                               String inMarketdataDirectory,
                               CSVFeedEventTranslator inEventTranslator)
            throws FeedException 
    {
        if(inEventTranslator == null) {
            throw new NullPointerException();
        }
        replayRate = inReplayRate;
        marketdataDirectory = new File(inMarketdataDirectory);
        Validate.isTrue(marketdataDirectory.exists(),
                        "Marketdata directory does not exist");
        Validate.isTrue(marketdataDirectory.canRead(),
                        "Marketdata directory is not readable");
        eventTranslator = inEventTranslator;
    }
    /**
     * the directory in which to find marketdata
     */
    private final File marketdataDirectory;
    /**
     * the number of milliseconds to delay between events
     */
    private final double replayRate;
    /**
     * the event translator to use 
     */
    private final CSVFeedEventTranslator eventTranslator;
}
