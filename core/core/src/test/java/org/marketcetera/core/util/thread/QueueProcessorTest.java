package org.marketcetera.core.util.thread;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.Callable;
import java.util.concurrent.LinkedBlockingDeque;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.marketcetera.core.util.except.ExpectedFailure;
import org.marketcetera.core.util.log.LoggerConfiguration;

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
     * Runs once before all tests.
     *
     * @throws Exception if an unexpected error occurs
     */
    @BeforeClass
    public static void once()
            throws Exception
    {
        LoggerConfiguration.logSetup();
    }
    /**
     * Run before each test.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Before
    public void setup()
            throws Exception
    {
        queue = new LinkedBlockingDeque<MockObject>();
        processor = new MockQueueProcessor(queue);
    }
    /**
     * Tests processor start and stop behavior.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testStartStop()
            throws Exception
    {
        assertFalse(processor.isRunning());
        processor.stop();
        assertFalse(processor.isRunning());
        start(processor);
        start(processor);
        assertTrue(processor.isRunning());
        processor.stop();
        assertFalse(processor.isRunning());
        Exception testException = new RuntimeException("This exception is expected");
        processor.startExceptionToThrow = testException;
        new ExpectedFailure<RuntimeException>() {
            @Override
            protected void run()
                    throws Exception
            {
                processor.start();
            }
        };
        assertFalse(processor.isRunning());
        processor.startExceptionToThrow = null;
        processor.stopExceptionToThrow = testException;
        start(processor);
        assertTrue(processor.isRunning());
        processor.stop();
        assertFalse(processor.isRunning());
    }
    /**
     * Tests the processing of received data.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testReceiveData()
            throws Exception
    {
        start(processor);
        final List<MockObject> testObjects = new ArrayList<MockObject>();
        for(int i=0;i<1000;i++) {
            MockObject data = new MockObject();
            testObjects.add(data);
            queue.add(data);
        }
        wait(new Callable<Boolean>() {
            @Override
            public Boolean call()
                    throws Exception
            {
                return processor.dataToProcess.size() == testObjects.size();
            }
        });
        assertEquals(testObjects,
                     processor.dataToProcess);
    }
    /**
     * Tests exception handling during data processing.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testReceiveThrowsException()
            throws Exception
    {
        start(processor);
        Exception dataException = new RuntimeException("this exception is expected");
        processor.dataExceptionToThrow = dataException;
        processor.shutdownOnException = false;
        MockObject testData = new MockObject();
        assertTrue(processor.dataToProcess.isEmpty());
        queue.add(testData);
        wait(new Callable<Boolean>() {
            @Override
            public Boolean call()
                    throws Exception
            {
                return !processor.dataToProcess.isEmpty();
            }
        });
        assertSame(testData,
                   processor.dataToProcess.get(0));
        assertTrue(processor.isRunning());
        processor.dataToProcess.clear();
        processor.shutdownOnException = true;
        queue.add(testData);
        wait(new Callable<Boolean>() {
            @Override
            public Boolean call()
                    throws Exception
            {
                return !processor.dataToProcess.isEmpty();
            }
        });
        assertSame(testData,
                   processor.dataToProcess.get(0));
        wait(new Callable<Boolean>() {
            @Override
            public Boolean call()
                    throws Exception
            {
                return !processor.isRunning();
            }
            
        });
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
        int iterationCount = 0;
        while(iterationCount++ < 600) {
            if(inBlock.call()) {
                return;
            }
            Thread.sleep(100);
        }
        fail("Condition not reached in 60s");
    }
    /**
     * Starts the given processor and verifies that it started properly.
     *
     * @param inProcessor a <code>QueueProcessor</code> value
     * @throws Exception if an error occurs
     */
    private void start(final QueueProcessor<?> inProcessor)
            throws Exception
    {
        inProcessor.start();
        wait(new Callable<Boolean>() {
            @Override
            public Boolean call()
                    throws Exception
            {
                return inProcessor.isRunning();
            }
        });
    }
    /**
     * Test object.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    private class MockObject
    {
    }
    /**
     * Test queue processor.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    private class MockQueueProcessor
            extends QueueProcessor<MockObject>
    {
        /* (non-Javadoc)
         * @see org.marketcetera.core.util.thread.QueueProcessor#onStart()
         */
        @Override
        protected void onStart()
                throws Exception
        {
            if(startExceptionToThrow != null) {
                throw startExceptionToThrow;
            }
        }
        /* (non-Javadoc)
         * @see org.marketcetera.core.util.thread.QueueProcessor#onStop()
         */
        @Override
        protected void onStop()
                throws Exception
        {
            if(stopExceptionToThrow != null) {
                throw stopExceptionToThrow;
            }
        }
        /**
         * Create a new MockQueueProcessor instance.
         *
         * @param inQueue a <code>BlockingDeque&lt;MockObject&gt;</code> value
         */
        protected MockQueueProcessor(BlockingDeque<MockObject> inQueue)
        {
            super(inQueue);
        }
        /* (non-Javadoc)
         * @see org.marketcetera.core.util.thread.QueueProcessor#processData(java.lang.Object)
         */
        @Override
        protected void processData(MockObject inData)
                throws Exception
        {
            dataToProcess.add(inData);
            if(dataExceptionToThrow != null) {
                throw dataExceptionToThrow;
            }
        }
        /* (non-Javadoc)
         * @see org.marketcetera.core.util.thread.QueueProcessor#shutdownOnException(java.lang.Exception)
         */
        @Override
        protected boolean shutdownOnException(Exception inException)
        {
            return shutdownOnException;
        }
        /**
         * holds the exception that will be thrown when data is received (if non-null)
         */
        private Exception dataExceptionToThrow;
        /**
         * holds the exception that will be thrown with the processor is started (if non-null)
         */
        private Exception startExceptionToThrow;
        /**
         * holds the exception that will be thrown with the processor is stopped (if non-null)
         */
        private Exception stopExceptionToThrow;
        /**
         * data received during the test in the order it was received
         */
        private List<MockObject> dataToProcess = new ArrayList<MockObject>();
        /**
         * indicates if the processor should shutdown when an exception occurs
         */
        private boolean shutdownOnException = false;
    }
    /**
     * test object used for testing
     */
    private MockQueueProcessor processor;
    /**
     * queue which holds test data to be processed
     */
    private BlockingDeque<MockObject> queue;
}
