package org.marketcetera.eventbus;

import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.marketcetera.metrics.MetricService;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.Histogram;
import com.codahale.metrics.Slf4jReporter;
import com.google.common.eventbus.Subscribe;

/* $License$ */

/**
 * Provides common test routines for the event bus.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public abstract class EventBusTestBase<ServiceClazz extends EventBusService>
{
    /**
     * Run before each test.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Before
    public void setup()
            throws Exception
    {
        eventBusService = createService();
        metricService = new MetricService();
        eventBusService.register(this);
        testEventHistogram = metricService.getMetrics().histogram("testEventHistogram");
    }
    /**
     * Run after each test.
     *
     * @throws Exception if an unexpected error occurs
     */
    @After
    public void cleanup()
            throws Exception
    {
        eventBusService.unregister(this);
    }
    /**
     * Test event bus throughput.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testThroughput()
            throws Exception
    {
        // warm up
        int testCount = 100000;
        for(int i=0;i<testCount;i++) {
            eventBusService.post(new EventBusTestEvent().post());
        }
        // reset the histogram
        testEventHistogram = metricService.getMetrics().histogram("testEventHistogram");
        // test run
        for(int i=0;i<testCount;i++) {
            eventBusService.post(new EventBusTestEvent().post());
        }
         Slf4jReporter reporter = Slf4jReporter.forRegistry(metricService.getMetrics())
                .outputTo(LoggerFactory.getLogger(EventBusTestBase.class.getName()))
                .convertRatesTo(TimeUnit.MILLISECONDS)
                .convertDurationsTo(TimeUnit.MILLISECONDS)
                .build();
         reporter.start(1,
                        TimeUnit.SECONDS);
         Thread.sleep(1500);
    }
    /**
     * Receive test event values.
     *
     * @param inTestEvent a <code>TestEvent</code> value
     */
    @Subscribe
    public void receiveTestEvent(EventBusTestEvent inTestEvent)
    {
        testEventHistogram.update(inTestEvent.receive());
    }
    /**
     * metric service for evaluating tests
     */
    protected MetricService metricService;
    /**
     * records test event data
     */
    protected Histogram testEventHistogram;
    /**
     * Create a service implementation for testing.
     *
     * @return a <code>ServiceClazz</code> value
     */
    protected abstract ServiceClazz createService();
    /**
     * test service implementation
     */
    protected ServiceClazz eventBusService;
}
