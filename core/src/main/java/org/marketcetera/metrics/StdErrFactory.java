package org.marketcetera.metrics;

import org.marketcetera.util.misc.ClassVersion;

import java.io.PrintStream;
import java.io.IOException;

/**
 * A factory that summarizes the metrics to stderr. Before the metrics
 * are written out, a prefatory line, identifying the thread for which
 * the metrics are printed is added.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 2.0.0
 */
@ClassVersion("$Id$")
class StdErrFactory implements PrintStreamFactory {
    /**
     * The singleton instance.
     */
    public static final StdErrFactory INSTANCE = new StdErrFactory();

    @Override
    public PrintStream getStream(String inName) throws IOException {
        System.err.println(Messages.STDERR_STREAM_SUMMARY_HEADER.
                getText(inName));
        return System.err;
    }

    @Override
    public void done(PrintStream inStream) throws IOException {
        inStream.flush();
    }

    /**
     * Prevent instantiation of this class.
     */
    private StdErrFactory() {
    }
}
