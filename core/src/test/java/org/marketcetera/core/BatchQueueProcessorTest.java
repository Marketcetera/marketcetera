package org.marketcetera.core;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.Test;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class BatchQueueProcessorTest
{
    /**
     * Tests handling of multiple events.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testManyEvents()
            throws Exception
    {
        testQueueProcessor = new TestQueueProcessor();
        testQueueProcessor.start();
        for(int i=0;i<10000;i++) {
            Thread.sleep(1);
            testQueueProcessor.add(new TestObject());
        }
        while(testQueueProcessor.objects.size() < 10000) {
            Thread.sleep(100);
        }
        TestObject last = null;
        for(TestObject testObject : testQueueProcessor.objects) {
            if(last == null) {
                assertEquals(1,
                             testObject.value);
            } else {
                assertEquals(last.value+1,
                             testObject.value);
            }
            last = testObject;
        }
    }
    /**
     * Test object.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    private static class TestObject
    {
        /**
         * uniquely identifies this event
         */
        private long value = counter.incrementAndGet();
        /**
         * provides a means to uniqely identify events
         */
        private static final AtomicLong counter = new AtomicLong(0);
    }
    /**
     * Processes multiple elements.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    private static class TestQueueProcessor
            extends BatchQueueProcessor<TestObject>
    {
        /* (non-Javadoc)
         * @see org.marketcetera.core.BatchQueueProcessor#processData(java.util.Deque)
         */
        @Override
        protected void processData(Deque<TestObject> inData)
                throws Exception
        {
            Thread.sleep(10);
            objects.addAll(inData);
        }
        /**
         * holds objects received
         */
        private List<TestObject> objects = new ArrayList<>();
    }
    /**
     * test processor
     */
    private TestQueueProcessor testQueueProcessor;
}
