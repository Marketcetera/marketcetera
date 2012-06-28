package org.marketcetera.metrics;

import org.marketcetera.core.attributes.ClassVersion;

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
 * @version $Id: ThreadedMetricBeanImpl.java 16063 2012-01-31 18:21:55Z colin $
 * @since 2.0.0
 */
@ClassVersion("$Id: ThreadedMetricBeanImpl.java 16063 2012-01-31 18:21:55Z colin $")
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
