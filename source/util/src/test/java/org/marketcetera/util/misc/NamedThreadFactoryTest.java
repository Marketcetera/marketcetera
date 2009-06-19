package org.marketcetera.util.misc;

import org.junit.Test;
import static org.junit.Assert.*;

import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/* $License$ */
/**
 * Tests {@link NamedThreadFactory}.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public class NamedThreadFactoryTest {
    /**
     * Verifies the class behavior when supplied a null prefix.
     */
    @Test(expected = NullPointerException.class)
    public void nullPrefix() {
        new NamedThreadFactory(null);
    }

    /**
     * Verifies the class' behavior when supplied a valid prefix.
     *
     * @throws Exception if there were test failures.
     */
    @Test
    public void prefix() throws Exception {
        final String prefix = "prefix";
        //The named factory and the factory to compare it with.
        final ThreadFactory testFactory = new NamedThreadFactory(prefix);
        final ThreadFactory expectedFactory = Executors.defaultThreadFactory();
        
        //Test our factory
        TestRunnable actualRunnable = new TestRunnable();
        assertFalse(actualRunnable.isRan());
        //Create a thread and test its properties.
        Thread actual = testFactory.newThread(actualRunnable);
        
        //Initialize the factory compare the results with.
        TestRunnable expectedRunnable = new TestRunnable();
        assertFalse(expectedRunnable.isRan());
        Thread expected = expectedFactory.newThread(expectedRunnable);
        //Verify that the thread created by our factory has the same properties
        //as the default factory.
        assertThreadProperties(expected, actual);
        //Verify that the thread has a custom name.
        assertEquals(prefix+1, actual.getName());
        //Start the thread and verifies that it does the job.
        actual.start();
        actual.join();
        assertTrue(actualRunnable.isRan());
        //Verify that the thread created by expected factory does the same.
        expected.start();
        expected.join();
        assertTrue(expectedRunnable.isRan());

        //Create another thread and verify its properties.
        actualRunnable = new TestRunnable();
        actual = testFactory.newThread(actualRunnable);
        assertThreadProperties(expectedFactory.newThread(new TestRunnable()), actual);
        assertEquals(prefix+2, actual.getName());

        //Test that the thread group of the new thread matches the threadgroup
        //of the threads created by the default factory, even when the thread
        //creating new thread has a different thread group.
        final ThreadGroup tg = new ThreadGroup("mygroup");
        final TestRunnable actualRunnable2 = new TestRunnable();
        final TestRunnable expectedRunnable2 = new TestRunnable();
        final List<Exception> failures = new ArrayList<Exception>();
        //request a thread within a thread with a different thread group
        actual = new Thread(tg, "testThread"){
            @Override
            public void run() {
                try {
                    Thread actual = testFactory.newThread(actualRunnable2);
                    Thread expected = expectedFactory.newThread(expectedRunnable2);
                    assertThreadProperties(expected, actual);
                    assertEquals(prefix+3, actual.getName());
                    assertEquals(tg, Thread.currentThread().getThreadGroup());
                } catch (Exception e) {
                    failures.add(e);
                }
            }
        };
        actual.start();
        actual.join();
        assertTrue(failures.toString(), failures.isEmpty());
    }

    /**
     * Verifies that the supplied thread's property is similar to that
     * created by the {@link java.util.concurrent.Executors#defaultThreadFactory()}.
     *
     * @param inExpected the thread with expected attributes.
     * @param inActual the actual thread.
     */
    private static void assertThreadProperties(Thread inExpected, Thread inActual) {
        assertEquals(inExpected.isDaemon(), inActual.isDaemon());
        assertEquals(inExpected.isAlive(), inActual.isAlive());
        assertEquals(inExpected.getPriority(), inActual.getPriority());
        assertEquals(inExpected.getThreadGroup(), inActual.getThreadGroup());
    }

    private static class TestRunnable implements Runnable {
        public boolean isRan() {
            return mRan;
        }

        @Override
        public void run() {
            mRan = true;
        }
        private volatile boolean mRan;
    }
}
