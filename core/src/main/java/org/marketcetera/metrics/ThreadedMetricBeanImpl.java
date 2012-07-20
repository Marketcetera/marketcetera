package org.marketcetera.metrics;

import org.marketcetera.util.misc.ClassVersion;

import java.io.IOException;
import java.util.Map;

/* $License$ */
/**
 * An implementation of {@link ThreadedMetricMXBean} that invokes
 * {@link ThreadedMetric} methods in response to all MXBean operations.
 * <p>
 * This class is meant to be only used by {@link JmxUtils}.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 2.0.0
 */
@ClassVersion("$Id$")
class ThreadedMetricBeanImpl implements ThreadedMetricMXBean {

    @Override
    public boolean isEnabled() {
        return ThreadedMetric.isEnabled();
    }

    @Override
    public void setEnabled(boolean inValue) {
        ThreadedMetric.setEnabled(inValue);
    }

    @Override
    public void clear() {
        ThreadedMetric.clear();
    }

    @Override
    public void summarize(boolean inIsTempFile) throws IOException {
        ThreadedMetric.summarizeResults(inIsTempFile
                ? FileStreamFactory.INSTANCE
                : StdErrFactory.INSTANCE);
    }

    @Override
    public Map<String, String> getConfiguredProperties() {
        return Configurator.getReportedValues();
    }

    /**
     * Creates an instance.
     */
    ThreadedMetricBeanImpl() {
    }
}
