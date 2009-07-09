package org.marketcetera.metrics;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.misc.NamedThreadFactory;
import org.marketcetera.core.LoggerConfiguration;
import org.marketcetera.module.ExpectedFailure;
import org.junit.Test;
import org.junit.BeforeClass;
import static org.junit.Assert.assertEquals;

import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.HashMap;

/* $License$ */
/**
 * Tests {@link ConditionsFactory}
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public class ConditionsFactoryTest {
    
    @BeforeClass
    public static void setup() {
        LoggerConfiguration.logSetup();
    }

    /**
     * Tests expected failures.
     * @throws Exception if there were errors.
     */
    @Test
    public void invalidInterval() throws Exception {
        new ExpectedFailure<IllegalArgumentException>("0 <= 0"){
            @Override
            protected void run() throws Exception {
                ConditionsFactory.createSamplingCondition(0,"dontmatter");
            }
        };
        new ExpectedFailure<IllegalArgumentException>("-1 <= 0"){
            @Override
            protected void run() throws Exception {
                ConditionsFactory.createSamplingCondition(-1,"dontmatter");
            }
        };
    }

    /**
     * Tests sampling in a single thread.
     *
     * @throws Exception if there were errors.
     */
    @Test
    public void simpleSampling() throws Exception {
        for (int interval = 1; interval < 50; interval++) {
            Callable<Boolean> condition = ConditionsFactory.
                    createSamplingCondition(interval, "sample");
            for(int i = 1; i < 100; i++) {
                assertEquals("Interval " + interval + " iteration " + i,
                        i % interval == 0, condition.call());
            }
        }
    }

    /**
     * Tests using the sampling condition instance from multiple threads.
     *
     * @throws Exception if there were errors.
     */
    @Test
    public void multiThreadSampling() throws Exception {
        ExecutorService exec = Executors.newCachedThreadPool(
                new NamedThreadFactory("TestConditionsFactory"));

        for (int j = 1; j < 37; j++) {
            final int interval = j;
            final Callable<Boolean> condition = ConditionsFactory.
                    createSamplingCondition(interval, "sample");
            List<Future<?>> futures = new LinkedList<Future<?>>();
            for(int i = 0; i < 20; i++) {
                futures.add(exec.submit(new Callable<Object>(){
                    @Override
                    public Object call() throws Exception {
                        for(int i = 1; i < 100; i++) {
                            assertEquals("Interval " + interval +
                                    " iteration " + i, i % interval == 0,
                                    condition.call());
                            //slow them down to encourage concurrency.
                            Thread.sleep(1);
                        }
                        return null;
                    }
                }));
            }
            for(Future<?> future: futures) {
                future.get();
            }
        }
        exec.shutdown();
    }

    /**
     * Tests integration of the sampling condition with {@link Configurator}.
     *
     * @throws Exception if there were errors.
     */
    @Test
    public void samplingConfiguration() throws Exception {
        //Configure a mock configurator.
        Map<String,String> properties = new HashMap<String, String>();
        final String validInterval = "sample.interval";
        properties.put(validInterval, "7");
        final String invalidInterval = "sample.invalid.interval";
        properties.put(invalidInterval, "value");
        Configurator.setInstance(new ConfiguratorTest.MockConfigurator(properties));
        //Test a sampler with a valid default configuration.
        Callable<Boolean> c = ConditionsFactory.createSamplingCondition(10, validInterval);
        for(int i = 1; i < 100; i++) {
            assertEquals(" iteration " + i, i % 7 == 0, c.call());
        }
        //Test a sampler with invalid default configuration.
        c = ConditionsFactory.createSamplingCondition(13, invalidInterval);
        for(int i = 1; i < 100; i++) {
            assertEquals(" iteration " + i, i % 13 == 0, c.call());
        }
    }
}
