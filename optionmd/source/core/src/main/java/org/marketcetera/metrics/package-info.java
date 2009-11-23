/**
 * Utilities to instrument system latency and throughput.
 *
 * <p>
 * This package provides classes that can be used to instrument code to
 * be able to measure its latency and throughput.
 * <h3>Main Classes</h3>
 * The following are the main classes in this package that can be used
 * when adding instrumentation to the code.
 * <h4>ThreadedMetric</h4>
 * This class is used by the code being instrumented to instrument itself.
 * <p>
 * The code that needs to be instrumented should call various methods
 * in the class {@link org.marketcetera.metrics.ThreadedMetric} to allow
 * itself to be instrumented.
 *
 * <h4>Configurator</h4>
 * <p>
 * The instrumentation code can be configured via the
 * {@link org.marketcetera.metrics.Configurator} class. If not explicitly
 * configured, the default configuration mechanism depends on a properties
 * file that should be available in the classpath.
 *
 * <h4>JMX Management</h4>
 * <p>
 * The instrumentation code can also be managed via JMX by connecting to
 * the JVM via jconsole. Refer to
 * {@link org.marketcetera.metrics.ThreadedMetricMXBean} for more details on
 * features that can be managed via JMX. {@link org.marketcetera.metrics.JmxUtils}
 * class can be used to export the management interface for the instrumentation
 * utilities.
 *
 * <h3>Output</h3>
 * By default, the utility will generate csv (comma separated values) files,
 * that contain the metrics output, when the application is terminated.
 * A unique csv file is generated for each thread that had a non-empty set
 * of collected metrics.
 * <p>
 * The utility can be forced to dump the metrics output prior to
 * application termination by invoking the jmx operation
 * {@link org.marketcetera.metrics.ThreadedMetricMXBean#summarize(boolean)}.
 *
 * @see org.marketcetera.metrics.ThreadedMetric
 * @see org.marketcetera.metrics.Configurator
 * @see org.marketcetera.metrics.ThreadedMetricMXBean
 */
package org.marketcetera.metrics;