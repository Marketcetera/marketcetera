package org.marketcetera.metrics;

import org.marketcetera.util.misc.ClassVersion;

import java.io.PrintStream;
import java.io.IOException;

/**
 * A factory that abstracts the location to which the instrumentation metrics
 * are summarized when {@link ThreadedMetric#summarizeResults(PrintStreamFactory)} 
 * is invoked.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
interface PrintStreamFactory {
    /**
     * Gets the print stream to write metrics for the thread identified by
     * the supplied name. Subclasses may choose to create a new stream or
     * return a reference to an existing stream.
     *
     * @param inName the metrics name. The name corresponds to the
     *  name of the thread that generated these metrics.
     *
     * @return the stream to which the metrics should be summarized.
     * 
     * @throws IOException if there were errors getting the stream.
     */
    public PrintStream getStream(String inName) throws IOException;

    /**
     * This method is invoked when the system is done writing metrics summary
     * to the stream. Subclasses may choose to either close or flush the stream.
     *
     * @param inStream the stream to which metrics have been summarized.
     * 
     * @throws IOException if there were errors closing the stream.
     */
    public void done(PrintStream inStream) throws IOException;
}
