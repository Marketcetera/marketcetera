package org.marketcetera.marketdata.recorder;

import static com.codahale.metrics.MetricRegistry.name;
import static org.marketcetera.core.time.TimeFactoryImpl.COLON;
import static org.marketcetera.core.time.TimeFactoryImpl.DASH;
import static org.marketcetera.core.time.TimeFactoryImpl.DAY;
import static org.marketcetera.core.time.TimeFactoryImpl.HOUR;
import static org.marketcetera.core.time.TimeFactoryImpl.MILLISECOND;
import static org.marketcetera.core.time.TimeFactoryImpl.MINUTE;
import static org.marketcetera.core.time.TimeFactoryImpl.MONTH;
import static org.marketcetera.core.time.TimeFactoryImpl.PERIOD;
import static org.marketcetera.core.time.TimeFactoryImpl.SECOND;
import static org.marketcetera.core.time.TimeFactoryImpl.YEAR;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;
import org.marketcetera.event.QuoteEvent;
import org.marketcetera.event.TimestampGenerator;
import org.marketcetera.metrics.MetricService;
import org.marketcetera.module.DataFlowID;
import org.marketcetera.module.DataReceiver;
import org.marketcetera.module.Module;
import org.marketcetera.module.ModuleException;
import org.marketcetera.module.ModuleURN;
import org.marketcetera.module.ReceiveDataException;
import org.marketcetera.module.StopDataFlowException;
import org.marketcetera.util.log.I18NBoundMessage3P;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.springframework.context.ApplicationContext;

import com.codahale.metrics.Histogram;
import com.codahale.metrics.MetricRegistry;

/* $License$ */

/**
 * Provides an implementation that writes market data to files in a given directory.
 * <p>
 * Module Features
 * <table>
 * <tr><th>Capabilities</th><td>Data Receiver</td></tr>
 * <tr><th>Stops data flows</th><td>No</td></tr>
 * <tr><th>Start Operation</th><td>Checks that the directory exists and is writable</td></tr>
 * <tr><th>Stop Operation</th><td>None</td></tr>
 * <tr><th>Management Interface</th><td>None</td></tr>
 * <tr><th>MX Notification</th><td>None</td></tr>
 * <tr><th>Factory</th><td>{@link MarketDataRecorderModule}</td></tr>
 * </table>
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class MarketDataRecorderModule
        extends Module
        implements DataReceiver
{
    /* (non-Javadoc)
     * @see org.marketcetera.module.DataReceiver#receiveData(org.marketcetera.module.DataFlowID, java.lang.Object)
     */
    @Override
    public void receiveData(DataFlowID inFlowID,
                            Object inData)
            throws ReceiveDataException
    {
        try {
            if(SLF4JLoggerProxy.isTraceEnabled(this)) {
                SLF4JLoggerProxy.trace(this,
                                       "Received {} from {}", //$NON-NLS-1$
                                       inData,
                                       inFlowID);
            }
            if(inData instanceof QuoteEvent) {
                processQuoteEvent((QuoteEvent)inData);
            } else {
                Messages.FILERECORDER_IGNORING_UNEXPECTED_DATA.warn(this,
                                                                    inData.getClass().getSimpleName());
            }
        } catch (Exception e) {
            throw new StopDataFlowException(e,
                                            new I18NBoundMessage3P(Messages.STOPPING_DATA_FLOW,
                                                                   inFlowID,
                                                                   String.valueOf(inData),
                                                                   ExceptionUtils.getRootCauseMessage(e)));
        }
    }
    /**
     * Processes the given quote event.
     *
     * @param inQuote a <code>QuoteEvent</code> value
     * @throws IOException if an error occurs processing the event
     */
    private void processQuoteEvent(QuoteEvent inQuote)
            throws IOException
    {
        StringBuilder builder = new StringBuilder();
        File outputFile = getOutputFile(inQuote);
        writeQuoteEvent(inQuote,
                        builder);
        FileUtils.write(outputFile,
                        builder.toString(),
                        true);
        builder.setLength(0);
        eventCounterMetric.update(1);
    }
    /**
     * Writes the given quote event to the given buffer.
     *
     * @param inQuote a <code>QuoteEvent</code> value
     * @param inBuffer a <code>StringBuilder</code> value
     */
    private void writeQuoteEvent(QuoteEvent inQuote,
                                 StringBuilder inBuffer)
    {
        inBuffer.append(inQuote.getAction()).append(',');
        inBuffer.append(inQuote.getInstrument().getFullSymbol()).append(',');
        inBuffer.append(inQuote.getMessageId()).append(',');
        inBuffer.append(inQuote.getEventType()).append(',');
        inBuffer.append(inQuote.getExchange()).append(',');
        inBuffer.append(marketDataTimestampFormatter.print(timestampGenerator.generateTimestamp(inQuote.getExchangeTimestamp()))).append(',');
        inBuffer.append(inQuote.getPrice().toPlainString()).append(',');
        inBuffer.append(marketDataTimestampFormatter.print(inQuote.getProcessedTimestamp())).append(',');
        inBuffer.append(marketDataTimestampFormatter.print(inQuote.getReceivedTimestamp())).append(',');
        inBuffer.append(inQuote.getSize().toPlainString()).append(',');
        inBuffer.append(System.lineSeparator());
    }
    /**
     * Gets the filename to use for the given quote event.
     *
     * @param inQuote a <code>QuoteEvent</code> value
     * @return a <code>File</code> value
     */
    private File getOutputFile(QuoteEvent inQuote)
    {
        StringBuilder filename = new StringBuilder();
        String symbolKey = getSymbolKey(inQuote);
        filename.append(symbolKey).append('-').append(timestampFormatter.print(getFileTimestamp())).append('-');
        int ordinal = 1;
        if(currentOrdinal.containsKey(symbolKey)) {
            ordinal = currentOrdinal.get(symbolKey);
        } else {
            currentOrdinal.put(symbolKey,
                               ordinal);
        }
        if(inQuote.getEventType().isSnapshot()) {
            // if this is a snapshot part, bump the ordinal
            if(!snapshotInProgress.containsKey(symbolKey)) {
                currentOrdinal.put(symbolKey,
                                   ++ordinal);
            }
            snapshotInProgress.put(symbolKey,
                                   true);
        } else {
            snapshotInProgress.put(symbolKey,
                                   false);
        }
        filename.append(ordinal);
        filename.append(suffix);
        return new File(outputDirectoryFile,
                        filename.toString());
    }
    /**
     * Determine the timestamp to use as part of the current session.
     *
     * @return a <code>DateTime</code> value
     */
    private DateTime getFileTimestamp()
    {
        // there is a "session reset" time. if we're before the session reset, we use today's date.
        //  if we're after the session reset, we use tomorrow's date.
        DateTime timestamp = new DateTime();
        if(timestamp.isBefore(sessionResetTimestamp)) {
            return timestamp;
        } else {
            return timestamp.plusDays(1);
        }
    }
    /**
     * Get the key to use for the given quote.
     *
     * @param inQuote a <code>QuoteEvent</code> value
     * @return a <code>String</code> value
     */
    private String getSymbolKey(QuoteEvent inQuote)
    {
        StringBuilder symbolKey = new StringBuilder();
        symbolKey.append(inQuote.getInstrument().getFullSymbol()).append('-').append(inQuote.getExchange());
        return symbolKey.toString();
    }
    /**
     * Create a new MarketDataRecorderModule instance.
     *
     * @param inDirectoryName a <code>String</code> value
     * @param inApplicationContext an <code>ApplicationContext</code> value
     */
    MarketDataRecorderModule(String inDirectoryName,
                             ApplicationContext inApplicationContext)
    {
        super(new ModuleURN(MarketDataRecorderModuleFactory.PROVIDER_URN,
                            instance + counter.incrementAndGet()),
              false);
        directoryName = StringUtils.trimToNull(inDirectoryName);
        applicationContext = inApplicationContext;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.module.Module#preStart()
     */
    @Override
    protected void preStart()
            throws ModuleException
    {
        outputDirectoryFile = new File(directoryName);
        Validate.isTrue(outputDirectoryFile.isDirectory(),
                        Messages.NOT_A_DIRECTORY.getText(directoryName));
        Validate.isTrue(outputDirectoryFile.canWrite(),
                        Messages.NOT_A_DIRECTORY.getText(directoryName));
        MarketDataRecorderModuleConfiguration config = applicationContext.getBean(MarketDataRecorderModuleConfiguration.class);
        timestampGenerator = config.getTimestampGenerator();
        sessionResetTimestamp = config.getSessionResetTimestamp();
        SLF4JLoggerProxy.debug(this,
                               "Session reset is {}", //$NON-NLS-1$
                               sessionResetTimestamp);
        currentOrdinal.clear();
        snapshotInProgress.clear();
        MetricRegistry metrics = MetricService.getInstance().getMetrics();
        eventCounterMetricName = name(getURN().getValue(),
                                      "recordedEvents", //$NON-NLS-1$
                                      "count"); //$NON-NLS-1$
        eventCounterMetric = metrics.histogram(eventCounterMetricName);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.module.Module#preStop()
     */
    @Override
    protected void preStop()
            throws ModuleException
    {
        snapshotInProgress.clear();
        currentOrdinal.clear();
        MetricRegistry metrics = MetricService.getInstance().getMetrics();
        metrics.remove(eventCounterMetricName);
    }
    /**
     * an application context value
     */
    private final ApplicationContext applicationContext;
    /**
     * indicates which data streams by key have a snapshot in progress
     */
    private final Map<String,Boolean> snapshotInProgress = new HashMap<>();
    /**
     * indicates the current ordinal in use to identify output files
     */
    private final Map<String,Integer> currentOrdinal = new HashMap<>();
    /**
     * directory to which to write event files to
     */
    private final String directoryName;
    /**
     * output directory
     */
    private File outputDirectoryFile;
    /**
     * timestamp value for the session reset of today
     */
    private DateTime sessionResetTimestamp;
    /**
     * generates timestamps
     */
    private TimestampGenerator timestampGenerator;
    /**
     * counts events
     */
    private Histogram eventCounterMetric;
    /**
     * name of {@link #eventCounterMetric}
     */
    private String eventCounterMetricName;
    /**
     * provides unique instance names
     */
    private static final AtomicInteger counter = new AtomicInteger(0);
    /**
     * output timestamp for filenames
     */
    private static final DateTimeFormatter timestampFormatter = new DateTimeFormatterBuilder().append(YEAR).append(MONTH).append(DAY).toFormatter();
    /**
     * output timestamp for market data timestamps
     */
    private static final DateTimeFormatter marketDataTimestampFormatter = new DateTimeFormatterBuilder().append(YEAR).append(MONTH).append(DAY).append(DASH)
            .append(HOUR).append(COLON).append(MINUTE).append(COLON).append(SECOND).append(PERIOD).append(MILLISECOND).toFormatter();
    /**
     * suffix to use for output files
     */
    private static final String suffix = ".csv"; //$NON-NLS-1$
    /**
     * identifier to use for URNs
     */
    private static final String instance = "instance"; //$NON-NLS-1$
}
