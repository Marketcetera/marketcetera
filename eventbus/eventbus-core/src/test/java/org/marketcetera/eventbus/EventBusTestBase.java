package org.marketcetera.eventbus;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import java.util.Deque;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.marketcetera.metrics.MetricService;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.Histogram;
import com.codahale.metrics.Slf4jReporter;
import com.google.common.collect.Lists;
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
        int testCount = 1000000;
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
                .build();
         reporter.start(1,
                        TimeUnit.SECONDS);
         Thread.sleep(1500);
    }
    /**
     * Test register and post.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testRegister()
            throws Exception
    {
        EventBusTestEvent testEvent1 = new EventBusTestEvent();
        eventBusService.post(testEvent1);
        verifySameTestEvent(testEvent1);
        eventBusService.unregister(this);
    }
    /**
     * Receive test event values.
     *
     * @param inTestEvent a <code>TestEvent</code> value
     */
    @Subscribe
    public void receiveTestEvent(EventBusTestEvent inTestEvent)
    {
        if(testEventHistogram == null) {
            synchronized(testEvents) {
                testEvents.addLast(inTestEvent);
                testEvents.notifyAll();
            }
        } else {
            testEventHistogram.update(inTestEvent.receive());
        }
    }
    /**
     * Verify the next test event to be received is the same object as the given event.
     *
     * @param inExpectedEvent an <code>EventBusTestEvent</code> value
     * @throws Exception if no event is received or its the wrong event
     */
    protected void verifySameTestEvent(EventBusTestEvent inExpectedEvent)
            throws Exception
    {
        assertSame(inExpectedEvent,
                   waitForTestEvent());
    }
    /**
     * Wait for and return the next test event.
     *
     * @return an <code>EventBusTestEvent</code> value
     * @throws Exception if no event has been received within a reasonable amount of time
     */
    protected EventBusTestEvent waitForTestEvent()
            throws Exception
    {
        final EventBusTestEvent[] testEventToReturn = new EventBusTestEvent[1];
        try {
            wait(new Callable<Boolean>() {
                @Override
                public Boolean call()
                        throws Exception
                {
                    synchronized(testEvents) {
                        if(testEvents.isEmpty()) {
                            return false;
                        }
                        testEventToReturn[0] = testEvents.removeFirst();
                        return true;
                    }
                }}
            );
        } catch (AssertionError ignored) {}
        assertNotNull("No event bus event",
                      testEventToReturn[0]);
        return testEventToReturn[0];
    }
    /**
     * Reset test objects.
     */
    protected void reset()
    {
        synchronized(testEvents) {
            testEvents.clear();
        }
    }
    /**
     * Waits for the given block to return true.
     *
     * <p>This method is guaranteed to wait for the passed block
     * to evaluate to true.  It is also guaranteed to wait in a
     * fashion that allows other threads to receive sufficient
     * cycles to work.  The block will wait for a maximum of
     * 60 seconds before throwing an exception.
     * 
     * @param inBlock a <code>Callable&lt;Boolean&gt;</code> value containing the condition to be evaluated.  If the
     *   block evaluates to true, the wait method returns immediately.
     * @throws Exception if the block throws an exception
     */
    public static void wait(Callable<Boolean> inBlock)
        throws Exception
    {
        wait(inBlock,
             10);
    }
    /**
     * Waits for the given block to return true.
     *
     * <p>This method is guaranteed to wait for the passed block
     * to evaluate to true.  It is also guaranteed to wait in a
     * fashion that allows other threads to receive sufficient
     * cycles to work.  The block will wait for a maximum of
     * 60 seconds before throwing an exception.
     * 
     * @param inBlock a <code>Callable&lt;Boolean&gt;</code> value containing the condition to be evaluated.  If the
     *   block evaluates to true, the wait method returns immediately.
     * @param inSeconds an <code>int</code> value
     * @throws Exception if the block throws an exception
     */
    public static void wait(Callable<Boolean> inBlock,
                            int inSeconds)
        throws Exception
    {
        int iterationCount = 0;
        while(iterationCount++ < (inSeconds*10)) {
            if(inBlock.call()) {
                return;
            }
            Thread.sleep(100);
        }
        fail("Condition not reached in "+ inSeconds +"s");
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
    /**
     * holds test events
     */
    private final Deque<EventBusTestEvent> testEvents = Lists.newLinkedList();
}
