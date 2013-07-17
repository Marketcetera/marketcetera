package org.marketcetera.photon.commons;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;
import org.marketcetera.photon.test.ExpectedFailure;

import com.google.common.collect.Lists;

/* $License$ */

/**
 * Tests {@link SynchronizedProxy}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 2.0.0
 */
public class SynchronizedProxyTest {

    @SuppressWarnings("unchecked")
    @Test
    public void testProxy() throws Exception {
        List<String> list = Lists.newArrayList();
        List<String> proxy = (List<String>) SynchronizedProxy.proxy(list, List.class);
        proxy.add("ABC");
        assertThat(list.get(0), is("ABC"));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testRuntimeException() throws Exception {
        List<String> list = Lists.newArrayList();
        final List<String> proxy = (List<String>) SynchronizedProxy.proxy(list, List.class);
        new ExpectedFailure<IndexOutOfBoundsException>(null) {
            @Override
            protected void run() throws Exception {
                proxy.get(1);
            }
        };
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testCheckedException() throws Exception {
        Callable<Void> delegate = new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                throw new IOException();
            }
        };
        final Callable<Void> proxy = (Callable<Void>) SynchronizedProxy
                .proxy(delegate, Callable.class);
        new ExpectedFailure<IOException>(null) {
            @Override
            protected void run() throws Exception {
                proxy.call();
            }
        };
    }

    @Test
    public void testSynchronization() throws Exception {
        new SynchronizationTest(10, 100000).run();
    }

    @SuppressWarnings("unchecked")
    private static class SynchronizationTest {
        private final ExecutorService mPool = Executors.newCachedThreadPool();
        private final AtomicInteger mAddSum = new AtomicInteger();
        private final List<Integer> mList = new ArrayList<Integer>();
        private final List<Integer> mProxy = (List<Integer>) SynchronizedProxy
                .proxy(mList, List.class);
        private final CyclicBarrier mBarrier;
        private final int mTrials, mThreads;

        public SynchronizationTest(int threads, int trials) {
            mTrials = trials;
            mThreads = threads;
            mBarrier = new CyclicBarrier(threads + 1);
        }

        public void run() throws Exception {
            /*
             * Run conncurrent threads that add to the underlying list.
             */
            for (int i = 0; i < mThreads; i++) {
                mPool.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            int seed = this.hashCode()
                                    ^ (int) System.nanoTime();
                            int sum = 0;
                            mBarrier.await();
                            for (int i = 0; i < mTrials; i++) {
                                try {
                                    mProxy.add(seed);
                                } catch (Exception e) {
                                    // ignore, test will fail
                                }
                                sum += seed;
                                seed = xorShift(seed);
                            }
                            mAddSum.getAndAdd(sum);
                            mBarrier.await();
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
            }
            mBarrier.await(); // wait for all threads to be ready
            mBarrier.await(); // wait for all threads to finish
            /*
             * If synchronization occurred, the sum of items in the list is
             * equal to the sum of individual thread sums.
             */
            int sum = 0;
            for (Integer i : mList) {
                sum += i;
            }
            assertThat(sum, is(mAddSum.get()));
        }

        static int xorShift(int y) {
            /*
             * Cheap and effective pseudo random number generator (from Java
             * Concurrency in Practice).
             */
            y ^= y << 6;
            y ^= y >>> 21;
            y ^= y << 7;
            return y;
        }
    }
}
