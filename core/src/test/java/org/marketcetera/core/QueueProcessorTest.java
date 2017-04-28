package org.marketcetera.core;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.Before;
import org.junit.Test;
import org.marketcetera.util.log.SLF4JLoggerProxy;

/* $License$ */

/**
 * Tests {@link QueueProcessor}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class QueueProcessorTest
{
    /**
     * Runs before each test.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Before
    public void setup()
            throws Exception
    {
        idSource.set(0);
    }
    /**
     * Tests processing order of the multi queue.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testMultiProcessing()
            throws Exception
    {
        testQueue = new LinkedBlockingDeque<>();
        int counter = 10000;
        MockMultiProcessor processor = new MockMultiProcessor();
        processor.start();
        List<MockData> elements = doLatencyIteration(counter,
                                                     processor);
        assertEquals(counter,
                     elements.size());
        counter = 1;
        for(MockData element : elements) {
            assertEquals(counter++,
                         element.elementCounter);
        }
    }
    /**
     * Tests queue processing latency.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testLatency()
            throws Exception
    {
        int count = 1000000;
        testQueue = new ArrayBlockingQueue<>(count);
        MockProcessor processor = new MockProcessor();
        doLatencyTest(count,
                      processor);
        testQueue = new LinkedBlockingDeque<>();
        processor = new MockProcessor();
        doLatencyTest(count,
                      processor);
        testQueue = new ArrayBlockingQueue<>(count);
        MockMultiProcessor multiProcessor = new MockMultiProcessor();
        doLatencyTest(count,
                      multiProcessor);
        testQueue = new LinkedBlockingDeque<>();
        multiProcessor = new MockMultiProcessor();
        doLatencyTest(count,
                      multiProcessor);
    }
    /**
     * Performs the latency test.
     *
     * @param inCounter an <code>int</code> value
     * @param processor a <code>MockProcessorType</code> value
     * @throws InterruptedException if the test is interrupted
     */
    private void doLatencyTest(int inCounter,
                               MockProcessorType processor)
            throws InterruptedException
    {
        processor.start();
        doLatencyIteration(inCounter,
                           processor);
        List<MockData> elements = doLatencyIteration(inCounter,
                                                     processor);
        long min = Long.MAX_VALUE;
        long max = Long.MIN_VALUE;
        long sum = 0;
        List<Long> dataPoints = new ArrayList<>();
        for(MockData element : elements) {
            long diff = element.processTimestamp - element.submitTimestamp;
            sum += diff;
            min = Math.min(min,diff);
            max = Math.max(max,diff);
            dataPoints.add(diff);
        }
        Collections.sort(dataPoints);
        SLF4JLoggerProxy.info(this,
                              "Mean: {}ms Median: {}ms Min: {}ms Max: {}ms",
                              new BigDecimal(sum/inCounter).movePointLeft(6).toPlainString(),
                              new BigDecimal(dataPoints.get((int)inCounter/2)).movePointLeft(6).toPlainString(),
                              new BigDecimal(min).movePointLeft(6).toPlainString(),
                              new BigDecimal(max).movePointLeft(6).toPlainString());
    }
    /**
     * Executes a latency test iteration.
     *
     * @param inCount an <code>int</code> value
     * @param inProcessor a <code>MockProcessorType</code> value
     * @return a <code>List&lt;MockData&gt;</code> value
     * @throws InterruptedException if the test is interrupted
     */
    private List<MockData> doLatencyIteration(int inCount,
                                              MockProcessorType inProcessor)
            throws InterruptedException
    {
        counter = 0;
        List<MockData> elements = new ArrayList<>();
        for(int i=1;i<=inCount;i++) {
            MockData element = new MockData();
            elements.add(element);
            inProcessor.add(element);
        }
        while(counter != inCount) {
            Thread.sleep(250);
        }
        return elements;
    }
    /**
     * Defines a common type for the mock processor.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    private interface MockProcessorType
    {
        /**
         * Adds the data to the queue.
         *
         * @param inData a <code>MockData</code> value
         */
        void add(MockData inData);
        /**
         * Starts the processor.
         */
        void start();
    }
    /**
     * Provides a test implementation of {@link QueueMultiProcessor}.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    private class MockMultiProcessor
            extends QueueMultiProcessor<MockData>
            implements MockProcessorType
    {
        /* (non-Javadoc)
         * @see org.marketcetera.core.QueueMultiProcessor#processData(java.util.List)
         */
        @Override
        protected void processData(List<MockData> inData)
        {
            for(MockData data : inData) {
                data.processTimestamp = System.nanoTime();
                counter += 1;
            }
        }
        /**
         * Adds the given item to the processor queue.
         *
         * @param inData a <code>MockData</code> value
         */
        public void add(MockData inData)
        {
            inData.submitTimestamp = System.nanoTime();
            getQueue().add(inData);
        }
        /**
         * Create a new MockMultiProcessor instance.
         */
        private MockMultiProcessor()
        {
            super("MockMultiProcessorQueue",
                  testQueue);
        }
    }
    /**
     * Provides a test implementation of {@link QueueProcessor}.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    private class MockProcessor
            extends QueueProcessor<MockData>
            implements MockProcessorType
    {
        /* (non-Javadoc)
         * @see org.marketcetera.core.QueueProcessor#processData(java.lang.Object)
         */
        @Override
        protected void processData(MockData inData)
                throws Exception
        {
            inData.processTimestamp = System.nanoTime();
            counter += 1;
        }
        /**
         * Create a new MockProcessor instance.
         */
        private MockProcessor()
        {
            super("MockProcessorQueue",
                  testQueue);
        }
        /**
         * Adds the given item to the processor queue.
         *
         * @param inData a <code>MockData</code> value
         */
        public void add(MockData inData)
        {
            inData.submitTimestamp = System.nanoTime();
            getQueue().add(inData);
        }
    }
    /**
     * Holds test data.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    private static class MockData
    {
        /**
         * nano timestamp when the data was submitted
         */
        private long submitTimestamp;
        /**
         * nano timestamp when the data was processed
         */
        private long processTimestamp;
        /**
         * uniquely and sequentially identifies each object
         */
        private long elementCounter = idSource.incrementAndGet();
    }
    /**
     * provides unique identifies for data
     */
    private static final AtomicLong idSource = new AtomicLong(0);
    /**
     * counts the number of data items processed
     */
    private volatile long counter;
    /**
     * test queue value
     */
    private BlockingQueue<MockData> testQueue;
}
