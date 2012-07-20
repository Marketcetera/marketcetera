package org.marketcetera.metrics;

import org.marketcetera.util.misc.ClassVersion;

import javax.management.MXBean;
import java.io.IOException;
import java.util.Map;

/* $License$ */
/**
 * JMX Management interface for instrumentation provided by
 * {@link ThreadedMetric}.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 2.0.0
 */
@ClassVersion("$Id$")
@MXBean(true)
public interface ThreadedMetricMXBean {
    /**
     * Returns true if the performance instrumentation is enabled,
     * false otherwise.
     *
     * @return true, if the performance instrumentation is enabled.
     */
    public boolean isEnabled();

    /**
     * Enable/Disable the performance instrumentation. When disabled,
     * the invocation of checkpointing methods in {@link ThreadedMetric}
     * return immediately, without recording/computing any instrumentation
     * information.
     *
     * @param inValue true, if the instrumentation should be enabled.
     */
    public void setEnabled(boolean inValue);

    /**
     * Clears all the saved performance instrumentation data collected.
     */
    public void clear();

    /**
     * Summarizes the instrumentation metrics collected so far.
     * <p>
     * Note that this method does not clear the metrics collected so far.
     * If you need to clear the metrics, invoke the {@link #clear()} method.
     *
     * @param inIsTempFile true, if the metrics should be summarized to
     * the temporary directory location (as specified by
     * {@link java.io.File#createTempFile(String, String)}). 
     * false, if the metrics should be summarized to the application's stderr.
     *
     * @throws IOException if there were errors summarizing the metrics.
     */
    public void summarize(boolean inIsTempFile) throws IOException;

    /**
     * Returns the set of properties configured via {@link Configurator}
     * and their current values.
     *
     * @return the set of configured properties and their values.
     */
    public Map<String,String> getConfiguredProperties();
}

