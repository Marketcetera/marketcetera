package org.marketcetera.metrics;


import java.util.*;
import java.util.concurrent.Callable;
import java.io.PrintStream;
import java.io.IOException;
import java.lang.management.ManagementFactory;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.core.Pair;

import javax.management.*;

/* $License$ */
/**
 * An instrumentation class to capture system latency/throughput metrics
 * when processing requests or data.
 * <p>
 * The class captures the metrics on a per thread basis. The captured metrics
 * include the time taken by the system between certain checkpoints in code and
 * the total number of times the system performed certain computation. 
 * <p>
 * The computed data is dumped into a per-thread csv file in the jvm's
 * {@link java.io.File#createTempFile(String, String)}  temporary} directory
 * location. 
 * <p>
 * <h4>Usage</h4>
 * The code that needs to be instrumented should be modified to invoke
 * <code>ThreadedMetric</code> as follows.
 * <pre>
 * //The Request / data enters the system here
 * ThreadedMetric.begin();
 * try {
 *     ...
 *     ...
 *     //The first checkpoint in request / data processing has been reached.
 *     ThreadedMetric.event("first");
 *     ...
 *     ...
 *     //The second checkpoint in request / data processing has been reached.
 *     ThreadedMetric.event("second");
 *     ...
 *     ...
 *     //The third checkpoint in request / data processing has been reached.
 *     ThreadedMetric.event("third");
 *     ...
 *     ...
 * } finally {
 *     //The processing is complete
 *     //Sample every 10th iteration
 *     ThreadedMetric.end(ConditionsFactory.getSamplingCondition(10, null));
 * }
 * ...
 *
 * </pre>
 * <p>
 *   The best practice is that any thread that runs processing that
 *   may need to be instrumented, starts off with a {@link #begin(Object[])}
 *   invocation and <b>always</b> ends with a {@link #end(Callable, Object[])}
 *   invocation.
 * </p>
 * <p>
 *   When carrying out processing, {@link #event(String, Object[])} can be
 *   invoked at various checkpoints to let the instrumentation code record
 *   the time at which that checkpoint was reached.
 * </p>
 * <p>
 *   The {@link #end(Callable, Object[])} method is invoked with a condition
 *   that determines if the metrics for the given iteration be recorded
 *   for reporting. {@link ConditionsFactory} provides methods to create
 *   useful conditions.
 * </p>
 * <p>
 *   Since these checkpoint metrics are recorded for every thread individually,
 *   be careful when using the same condition instance for <code>end()</code>
 *   invocations across threads. Unless the shared condition instance uses
 *   thread local variables to keep track of iterations, you might get
 *   non-intuitive results. 
 * </p>
 * <h4>Output</h4>
 * <p>
 *   The class will dump the collected metrics as a CSV file, in the temp
 *   directory location with the name
 *   <code>&lt;thread_name&gt;-metrics*.csv</code>, when the application
 *   is terminated.
 *   The subsequent line contains the headers for the CSV file and the lines
 *   below that contain the recorded data for invocations.  
 * </p>
 * <p>
 *   Here's example output generated from the sample code above.
 * </p>
 * <pre>
 * BEGIN,first,second,third,END,ITERATIONS
 * 1326706169490256,100673130,100654972,100559149,100717829,10
 * 1326710193115326,100780965,100346832,100537358,100508305,20
 * 1326714216602389,100942718,100135631,100571721,100493778,30
 * 1326718239831040,100515847,100613626,100567530,100632343,40
 * 1326722263327881,100578146,100874552,100324483,100645194,50
 * 1326726286714093,100525346,100785994,100606641,100642680,60
 * 1326730310238871,100549651,100731238,100398794,100723136,70
 * 1326734333578150,100540990,100763924,100520318,100672013,80
 * 1326738357107956,100563061,100606920,100635416,100609994,90
 * 1326742380622397,100429524,100744927,100468915,100701625,100
 * </pre>
 * <p>
 *   The first column, BEGIN, has the timestamp of when that checkpoint was
 *   reached in nano seconds. Every subsequent column after that, except for
 *   the last one, has the amount of time it took to reach that checkpoint from
 *   the previous one, in nanoseconds. The last column has the cumulative number
 *   of iterations at the time the iteration was recorded.
 * </p>
 * <p>
 *   For every row, the sum of values in all the columns except the first and
 *   the last one, will give the total time / latency between the BEGIN and
 *   END checkpoints.
 * </p>
 * <p>
 *   Throughput can computed for any two rows by dividing the difference between
 *   their ITERATION column values by the difference between their BEGIN column
 *   values.
 * </p>
 * <p>
 *   Under certain circumstances, the rows may contain extra columns at the end.
 *   These extra columns either contain any extra data that was supplied to any of
 *   the checkpoint calls: <code>begin(), event() & end()</code> for the
 *   iteration that the row represents. OR they may contain extra checkpoint
 *   time deltas resulting from <code>event()</code> invocations within a loop. 
 * </p>
 * <h4>Configuration & Management</h4>
 * <p>
 *   The instrumentation is disabled by default. It can be turned on via JMX
 *   or via a properties file placed in the classpath. For more information
 *   on JMX, see {@link JmxUtils}. For more information on using a properties
 *   file, see {@link Configurator}
 * </p>
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 2.0.0
 */
@ClassVersion("$Id$")
public final class ThreadedMetric {

    /**
     * Invoke this method when the request / data processing starts.
     *
     * @param inParams any extra information to include in the summary.
     */
    public static void begin(Object ... inParams) {
        if (isEnabled()) {
            DEFAULT.first(inParams);
        }
    }
    /**
     * Invoke this method when an intermediate milestone in the processing
     * has been reached.
     *
     * @param inIdentifier the name of the milestone. The milestone name
     * should be different from the reserved identifier names,
     * {@link #BEGIN_IDENTIFIER} & {@link #END_IDENTIFIER}.
     * @param inParams any extra information to include in the summary.
     */
    public static void event(String inIdentifier, Object... inParams) {
        if (isEnabled()) {
            DEFAULT.checkpoint(inIdentifier, inParams);
        }
    }
    /**
     * Invoke this method when the request / data processing is complete.
     *
     * @param inRecordCondition a condition to determine if the collected
     * metrics for this request should be recorded. The metrics are discarded
     * if the supplied condition returns false.
     * @param inParams any extra information to include in the summary.
     *
     * @see ConditionsFactory
     */
    public static void end(Callable<Boolean> inRecordCondition,
                           Object... inParams) {
        if (isEnabled()) {
            DEFAULT.last(inRecordCondition, inParams);
        }
    }

    /**
     * Returns true if the instrumentation is enabled.
     *
     * @return true, if the instrumentation is enabled.
     */
    static boolean isEnabled() {
        return sEnabled;
    }

    /**
     * Sets whether the instrumentation should be enabled.
     *
     * @param inEnabled whether the instrumentation should be enabled.
     */
    static void setEnabled(boolean inEnabled) {
        sEnabled = inEnabled;
    }
    /**
     * Summarizes the collected metrics onto the supplied stream as a set
     * of comma separated values.
     *
     * @param inStreamFactory the factory that determines which stream the
     * metrics should be written out to.
     * 
     * @throws java.io.IOException if there were I/O errors when writing
     * the summary out.
     */
    static void summarizeResults(PrintStreamFactory inStreamFactory)
            throws IOException {
        DEFAULT.summarize(inStreamFactory);
    }

    /**
     * Clears out all the saved metrics data.
     */
    static void clear() {
        DEFAULT.reset();
    }

    /**
     * Invoke this method when the request / data processing starts.
     *
     * @param inParams any extra information to include in the summary.
     */
    private void first(Object ...inParams) {
        getThreadInfo().clearCurrent();
        checkpoint(BEGIN_IDENTIFIER, inParams);
    }

    /**
     * Invoke this method when an intermediate milestone in the processing
     * has been reached.
     *
     * @param inIdentifier the name of the milestone. This milestone should
     * have the same name for every milestone.
     * @param inParams any extra information to include in the summary.
     */
    private void checkpoint(String inIdentifier, Object ...inParams) {
        getThreadInfo().addCurrent(new CheckpointInfo(inIdentifier,
                System.nanoTime(), inParams));
    }

    /**
     * Invoke this method when the request / data processing is complete.
     *
     * @param inRecordCondition a condition to determine if the collected
     * metrics for this request should be recorded. The metrics are discarded
     * if the supplied condition returns false.
     * @param inParams any extra information to include in the summary.
     */
    private void last(Callable<Boolean> inRecordCondition, Object ...inParams) {
        PerThreadInfo info = getThreadInfo();
        info.addIteration();
        try {
            //Save the metrics if the condition evaluates to true.
            if (inRecordCondition.call()) {
                checkpoint(END_IDENTIFIER, inParams);
                info.saveCurrent();
                return;
            }
        } catch (Exception ignore) {
        }
        //Clear the metrics otherwise.
        info.clearCurrent();
    }

    /**
     * Summarizes the saved data for all the instrumented threads.
     *
     * @param inFactory the print stream factory.
     *
     * @throws IOException if there were errors summarizing the metrics.
     */
    private void summarize(PrintStreamFactory inFactory) throws IOException {
        for (PerThreadInfo info : mAllInfos) {
            if (!info.isSavedEmpty()) {
                summarize(inFactory, info);
            }
        }
    }

    /**
     * Summarizes the collected metrics onto the supplied stream as a set
     * of comma separated values.
     *
     * @param inFactory the stream factory that provides the location to
     * print the summary to.
     * @param inThreadInfo the threadInfo that contains the information on the
     * thread that needs to be summarized.
     * 
     * @throws IOException if there were I/O errors when writing out
     * the summary.
     */
    private void summarize(PrintStreamFactory inFactory,
                           PerThreadInfo inThreadInfo)
            throws IOException {
        PrintStream stream = inFactory.getStream(inThreadInfo.getName());
        try {
            // maintain a hashmap of row data already processed in case we
            // find duplicate rows, which happens when we have nested calls
            // to this class in a loop
            final Map<String,Long> row = new HashMap<String,Long>();
            //List of duplicates
            final List<Pair<String,Long>> duplicates =
                    new LinkedList<Pair<String, Long>>();
            //List of extra data.
            final List<Pair<String,Object[]>> extra =
                    new LinkedList<Pair<String,Object[]>>();
            //Use an ordered set to keep track of headers and get rid of
            //duplicate headers resulting from nested calls within loops.
            final Set<String> headers = new LinkedHashSet<String>();
            boolean headerProcessed = false;

            for (IterationInfo iterationInfo : inThreadInfo) {
                //Process header if not processed already
                if (!headerProcessed) {
                    //Add all headers to the set to weed out duplicates
                    //while maintaining their order
                    for (CheckpointInfo checkpointInfo : iterationInfo) {
                        headers.add(checkpointInfo.getIdentifier());
                    }
                    //Now print out the headers
                    for (String header : headers) {
                        stream.print(header);
                        stream.print(SEPARATOR);
                    }
                    //Add iteration count header
                    stream.print(ITERATIONS_HEADER);
                    stream.println();
                    headerProcessed = true;
                }

                //Now process the row
                long lastValue = -1;
                for (CheckpointInfo checkpointInfo : iterationInfo) {
                    long value;
                    if (lastValue > 0) {
                        //the value is delta for every column other than first
                        value = checkpointInfo.getTimestamp() - lastValue;
                    } else {
                        //record the absolute time stamp value for the first entry.
                        value = checkpointInfo.getTimestamp();
                    }
                    //record the value only if there's a header for it and
                    //if an entry with that header has not been seen already.
                    if (headers.contains(checkpointInfo.getIdentifier()) &&
                            (!row.containsKey(checkpointInfo.getIdentifier()))) {
                        row.put(checkpointInfo.getIdentifier(), value);
                    } else {
                        //Add to duplicates if there's no header for this record
                        //Or if a value with this header has been seen already.
                        duplicates.add(new Pair<String, Long>(
                                checkpointInfo.getIdentifier(), value));
                    }
                    //Record extra parameters, if any.
                    if (checkpointInfo.getData() != null &&
                            checkpointInfo.getData().length > 0) {
                        extra.add(new Pair<String, Object[]>(
                                checkpointInfo.getIdentifier(),
                                checkpointInfo.getData()));
                    }
                    //Record the last value to compute deltas
                    lastValue = checkpointInfo.getTimestamp();
                }

                //Print the row of data in the same order as the headers
                for(String header: headers) {
                    Long value = row.get(header);
                    //A value might be null if the checkpoint corresponding
                    //to the header wasn't reached in this specific iteration.
                    if (value != null) {
                        stream.print(value);
                    }
                    stream.print(SEPARATOR);
                }
                row.clear();
                //Append Iteration count value
                stream.print(iterationInfo.getNumIterations());

                //Append duplicates, if any.
                if (!duplicates.isEmpty()) {
                    stream.print(SEPARATOR);
                    printDuplicates(stream, duplicates);
                    duplicates.clear();
                }

                //Append extra, if any
                if (!extra.isEmpty()) {
                    stream.print(SEPARATOR);
                    printExtra(stream, extra);
                    extra.clear();
                }
                stream.println();
            }
        } finally {
            inFactory.done(stream);
        }
    }

    /**
     * Prints any extra parameters for a results row.
     *
     * @param inStream the stream to print onto.
     * @param inExtras the list of extra items.
     */
    private void printExtra(final PrintStream inStream,
                            final List<Pair<String, Object[]>> inExtras) {
        inStream.print('{');  //$NON-NLS-1$
        boolean isFirst = true;
        for(Pair<String,Object[]> extra: inExtras) {
            if (isFirst) {
                isFirst = false;
            } else {
                inStream.print(SECONDARY_SEPARATOR);
            }
            inStream.print(extra.getFirstMember());
            inStream.print('=');  //$NON-NLS-1$
            inStream.print(toString(extra.getSecondMember()));
        }
        inStream.print('}');  //$NON-NLS-1$
    }

    private String toString(Object[] extra) {
        if(extra == null) {
            return "";  //$NON-NLS-1$
        }
        StringBuilder sb = new StringBuilder("[");  //$NON-NLS-1$
        boolean isFirst = true;
        for(Object o: extra) {
            if(!isFirst) {
                sb.append(';');  //$NON-NLS-1$
            }
            sb.append(String.valueOf(o));
            isFirst = false;
        }
        sb.append(']');
        return sb.toString();
    }

    /**
     * Prints any duplicate metrics for a results row.
     *
     * @param inStream the steram to print onto.
     * @param inDuplicates the list of duplicates.
     */
    private static void printDuplicates(PrintStream inStream,
                                 List<Pair<String, Long>> inDuplicates) {
        inStream.print('[');  //$NON-NLS-1$
        boolean isFirst = true;
        for(Pair<String,Long> pair: inDuplicates) {
            if (isFirst) {
                isFirst = false;
            } else {
                inStream.print(SECONDARY_SEPARATOR);
            }
            inStream.print(pair.getFirstMember());
            inStream.print('=');  //$NON-NLS-1$
            inStream.print(pair.getSecondMember());
        }
        inStream.print(']');  //$NON-NLS-1$
    }

    /**
     * Clears all the metrics collected so far.
     */
    private void reset() {
        for (PerThreadInfo info : mAllInfos) {
            info.clearSaved();
        }
    }
    /**
     * Creates an instance.
     * Currently there will be only a singleton instance of this class. However,
     * it's not too hard to be able to create multiple instances of this class.
     */
    private ThreadedMetric() {
        //Figure out if we need to register the JMX management interface.
        final boolean configureJMX = Boolean.parseBoolean(
                Configurator.getProperty(JmxUtils.METC_METRICS_JMX_ENABLE,
                        "false"));  //$NON-NLS-1$
        final MBeanServer mbServer = ManagementFactory.getPlatformMBeanServer();
        //Add a shutdown hook to summarize the results.
        Runtime.getRuntime().addShutdownHook(new Thread(){
            @Override
            public void run() {
                try {
                    summarize(FileStreamFactory.INSTANCE);
                } catch (Exception e) {
                    Messages.LOG_ERROR_SUMMARIZING.warn(this, e);
                }
                if(configureJMX) {
                    try {
                        JmxUtils.unregisterMgmtInterface(mbServer);
                    } catch (Exception e) {
                        Messages.LOG_ERROR_UNREGISTER_MXBEAN.debug(this, e);
                    }
                }
            }
        });
        if (configureJMX) {
            try {
                JmxUtils.registerMgmtInterface(mbServer);
            } catch (Exception e) {
                Messages.LOG_ERROR_REGISTER_MXBEAN.warn(this, e);
            }
        }
    }


    /**
     * Returns the data structure that holds instrumentation information
     * for the current thread.
     *
     * @return the data structure for instrumentating the current thread.
     */
    private PerThreadInfo getThreadInfo() {
        return mThreadInfo.get();
    }

    /**
     * The header used to identify the end of processing.
     */
    public static final String END_IDENTIFIER = "END";  //$NON-NLS-1$
    /**
     * The header used to identify the beginning of processing.
     */
    public static final String BEGIN_IDENTIFIER = "BEGIN";  //$NON-NLS-1$
    /**
     * The header used to identify the column containing the number of iterations.
     */
    public static final String ITERATIONS_HEADER = "ITERATIONS";  //$NON-NLS-1$

    /**
     * The singleton instance.
     */
    private static final ThreadedMetric DEFAULT = new ThreadedMetric();
    /**
     * The separator used in the CSV file output.
     */
    private static final char SEPARATOR = ',';  //$NON-NLS-1$
    /**
     * The separator used for extra data and duplicates.
     */
    private static final char SECONDARY_SEPARATOR = ':';  //$NON-NLS-1$
    /**
     * The variable that indicates if metrics are enabled. 
     */
    private static volatile boolean sEnabled = Boolean.parseBoolean(
            Configurator.getProperty(Configurator.PROPERTY_METRICS_ENABLE,
                    "false"));  //$NON-NLS-1$
    /**
     * A list of all per-thread data structures in existence..
     */
    private final List<PerThreadInfo> mAllInfos = Collections.synchronizedList(
            new LinkedList<PerThreadInfo>());
    /**
     * Provides a per-thread data structure to keep track of instrumentation
     * metrics for every thread.
     */
    private final ThreadLocal<PerThreadInfo> mThreadInfo =
            new ThreadLocal<PerThreadInfo>(){
        @Override
        protected PerThreadInfo initialValue() {
            PerThreadInfo info = new PerThreadInfo();
            // Note that there's a memory leak here. If the owner thread
            // for this thread local goes away, the corresponding PerThreadInfo
            // instance is not removed from mAllInfos.
            // However, this is necessary as we do need to keep the per thread
            // metrics around for publishing later.
            // Note that the negative impact of this can be reduced by invoking
            // the clear() method as it will empty out all these data structures.
            // The clear() method is also accessible via JMX, in case, there's
            // a need to invoke it on a long running system.
            mAllInfos.add(info);
            return info;
        }
    };
}
