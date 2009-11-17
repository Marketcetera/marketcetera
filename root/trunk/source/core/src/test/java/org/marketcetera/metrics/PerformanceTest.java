package org.marketcetera.metrics;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.core.LoggerConfiguration;
import org.junit.Test;
import org.junit.Before;
import org.junit.BeforeClass;

import java.util.concurrent.Callable;

/* $License$ */
/**
 * Computes the performance impact of {@link ThreadedMetric}.
 * <p>
 * {@link #jitCompilation()} is a test that is ran to allow JIT compilation
 * of the code exercised in this unit test.
 * <p>
 * {@link #enabled()} is a test that measures the performance when the
 * instrumentation is enabled.
 * <p>
 * {@link #disabled()} is a test that measures the performance when
 * the instrumentation is disabled.
 * <p>
 * {@link #noMetric()} is a test that measures the performance when the
 * metrics are not used.
 * <p>
 * The difference of time taken by the methods above can be used to ascertain
 * the impact of the instrumentation code.
 * <p>
 * The difference between run times of {@link #noMetric()} and
 * {@link #disabled()} gives us the overhead that the instrumentation code
 * adds to the production code even when it's not in use.
 * <p>
 * The different of time between run times of {@link #enabled()} and
 * {@link #disabled()} gives us the overhead that the instrumentation code
 * adds to the production code when it's in use. 
 * <p>
 * You can change the {@link #NUM_ITERATIONS} value to change the number
 * of iterations that are run in the test.
 * You can change the {@link #NUM_CHECKPOINTS} value to change the number
 * of checkpoints that are recorded when the test is run. The actual number
 * of checkpoints are this number + 2 (for begin and end checkpoints.)
 * You can change {@link #SAMPLING_INTERVAL} value to change how often are
 * the performance metrics sampled.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 2.0.0
 */
@ClassVersion("$Id$")
public class PerformanceTest {
    
    @BeforeClass
    public static void setup() throws Exception {
        LoggerConfiguration.logSetup();
    }
    @Before
    public void clear() {
        ThreadedMetric.clear();
    }

    /**
     * This test does not test anything. It's just executed first
     * to let the jit compilation kick in.
     *
     * @throws Exception if there were errors.
     */
    @Test
    public void jitCompilation() throws Exception {
        ThreadedMetric.setEnabled(true);
        runIterations("jitCompile");
        runIterationsNoMetric("jitCompile");
    }

    /**
     * Figures out how much time it takes an example code to run with the
     * metrics enabled.
     *
     * @throws Exception if there were errors.
     */
    @Test
    public void enabled() throws Exception {
        ThreadedMetric.setEnabled(true);
        runIterations("enabled");
    }

    /**
     * Figures out how much it takes an example code to run with the metrics
     * disabled.
     *
     * @throws Exception if there were errors.
     */
    @Test
    public void disabled() throws Exception {
        ThreadedMetric.setEnabled(false);
        runIterations("disabled");
    }

    /**
     * Figures out how much time it takes an example code to run with
     * no metrics code.
     *
     * @throws Exception if there were errors.
     */
    @Test
    public void noMetric() throws Exception {
        runIterationsNoMetric("noMetric");
    }

    /**
     * Runs test iterations.
     *
     * @param inLabel the label to use when logging time.
     *
     * @throws InterruptedException if the run was interrupted.
     */
    private static void runIterations(String inLabel) throws InterruptedException {
        long time = System.nanoTime();
        for(int i = 0; i < NUM_ITERATIONS; i++) {
            oneIteration();
        }
        time = System.nanoTime() - time;
        SLF4JLoggerProxy.info(PerformanceTest.class, "{}: Iterations {}, time {}ns",
                inLabel,  NUM_ITERATIONS, time);
    }

    /**
     * Runs a single test iteration.
     *
     * @throws InterruptedException if the run was interrupted.
     */
    private static void oneIteration() throws InterruptedException {
        ThreadedMetric.begin();
        for(int i = 0; i < NUM_CHECKPOINTS; i++) {
            sleep();
            ThreadedMetric.event("checkpoint" + i);
        }
        sleep();
        ThreadedMetric.end(SAMPLING_CONDITION);
    }

    /**
     * Runs test iterations without invoking performance instrumentation code.
     *
     * @param inLabel the label to use when logging time.
     *
     * @throws InterruptedException if the run was interrupted.
     */
    private static void runIterationsNoMetric(String inLabel)
            throws InterruptedException {
        long time = System.nanoTime();
        for(int i = 0; i < NUM_ITERATIONS; i++) {
            oneIterationNoMetric();
        }
        time = System.nanoTime() - time;
        SLF4JLoggerProxy.info(PerformanceTest.class, "{}: Iterations {}, time {}ns",
                inLabel, NUM_ITERATIONS, time);
    }

    /**
     * Runs a single test iteration without invoking performance
     * instrumentation code.
     *
     * @throws InterruptedException if the run was interrupted.
     */
    private static void oneIterationNoMetric() throws InterruptedException {
        for(int i = 0; i < NUM_CHECKPOINTS; i++) {
            sleep();
        }
        sleep();
    }

    /**
     * Sleeps for a significant amount of time.
     *
     * @throws InterruptedException if the sleep was interrupted.
     */
    private static void sleep() throws InterruptedException {
        Thread.sleep(1);
    }

    private static final int SAMPLING_INTERVAL = 10;
    private static final int NUM_ITERATIONS = 1000;
    private static final int NUM_CHECKPOINTS = 8;
    
    private static final Callable<Boolean> SAMPLING_CONDITION =
            ConditionsFactory.createSamplingCondition(SAMPLING_INTERVAL,
                "doesnotmatter");
}
