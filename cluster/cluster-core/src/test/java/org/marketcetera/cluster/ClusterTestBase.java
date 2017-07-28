package org.marketcetera.cluster;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.locks.Lock;

import org.apache.commons.lang.Validate;
import org.junit.Before;
import org.junit.Test;
import org.marketcetera.cluster.service.ClusterListener;
import org.marketcetera.cluster.service.ClusterMember;
import org.marketcetera.cluster.service.ClusterService;
import org.marketcetera.marketdata.MarketDataFeedTestBase;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Sets;

/* $License$ */

/**
 * Provides common test behavior for {@link ClusterService} implementations.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public abstract class ClusterTestBase<Clazz extends ClusterService>
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
        clusterService = createClusterService();
        completedTasks.clear();
    }
    /**
     * Test cluster queue operations.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testQueue()
            throws Exception
    {
        final QueueDescriptor<String> testDescriptor1 = new QueueDescriptor<>("test-queue1");
        final QueueDescriptor<Integer> testDescriptor2 = new QueueDescriptor<>("test-queue2");
        assertNull(clusterService.peekFromQueue(testDescriptor1));
        assertNull(clusterService.peekFromQueue(testDescriptor2));
        String testValue1 = "test-value1";
        String testValue2 = "test-value2";
        Integer testValue3 = 42;
        Integer testValue4 = 100;
        clusterService.addToQueue(testDescriptor2,
                                  testValue3);
        clusterService.addToQueue(testDescriptor2,
                                  testValue4);
        clusterService.addToQueue(testDescriptor1,
                                  testValue1);
        assertEquals(testValue1,
                     clusterService.peekFromQueue(testDescriptor1));
        assertEquals(testValue3,
                     clusterService.peekFromQueue(testDescriptor2));
        clusterService.addToQueue(testDescriptor1,
                                  testValue2);
        assertEquals(testValue1,
                     clusterService.peekFromQueue(testDescriptor1));
        assertEquals(testValue1,
                     clusterService.takeFromQueue(testDescriptor1));
        assertEquals(testValue2,
                     clusterService.peekFromQueue(testDescriptor1));
        assertEquals(testValue2,
                     clusterService.takeFromQueue(testDescriptor1));
        assertEquals(testValue3,
                     clusterService.takeFromQueue(testDescriptor2));
        assertEquals(testValue4,
                     clusterService.peekFromQueue(testDescriptor2));
        assertEquals(testValue4,
                     clusterService.takeFromQueue(testDescriptor2));
        assertNull(clusterService.peekFromQueue(testDescriptor1));
        assertNull(clusterService.peekFromQueue(testDescriptor2));
        // take from an empty queue should block
        ExecutorService testExecutorService = Executors.newSingleThreadExecutor();
        Future<String> token = testExecutorService.submit(new Callable<String>() {
            @Override
            public String call()
                    throws Exception
            {
                return clusterService.takeFromQueue(testDescriptor1);
            }});
        // pause for a little while to make sure that the queue doesn't immediately return a value
        assertFalse(token.isDone());
        Thread.sleep(1000);
        assertFalse(token.isDone());
        clusterService.addToQueue(testDescriptor1,
                                  testValue1);
        assertEquals(testValue1,
                     token.get());
    }
    /**
     * Test map operations.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testMap()
            throws Exception
    {
        String map1Name = "map1";
        String map2Name = "map2";
        String key1 = "key1";
        String value1 = "value1";
        String key2 = "key2";
        String value2 = "value2";
        assertNull(clusterService.removeFromMap(map1Name,key1));
        assertTrue(clusterService.getMap(map1Name).isEmpty());
        assertTrue(clusterService.getMap(map2Name).isEmpty());
        assertNull(clusterService.addToMap(map1Name,key1,value1));
        assertNull(clusterService.addToMap(map2Name,key2,value2));
        assertEquals(value1,
                     clusterService.getMap(map1Name).get(key1));
        assertEquals(value2,
                     clusterService.getMap(map2Name).get(key2));
        assertNull(clusterService.getMap(map1Name).get(key2));
        assertNull(clusterService.getMap(map2Name).get(key1));
        assertEquals(value1,
                     clusterService.addToMap(map1Name,key1,value2));
    }
    /**
     * Test instance data operations.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testInstanceData()
            throws Exception
    {
        ClusterData instanceData = clusterService.getInstanceData();
        assertNotNull(instanceData);
        assertNotNull(instanceData.getHostId());
        assertNotNull(instanceData.getUuid());
        Set<ClusterMember> members = clusterService.getClusterMembers();
        assertFalse(members.isEmpty());
        boolean thisMemberFound = false;
        for(ClusterMember member : members) {
            if(instanceData.getUuid().equals(member.getUuid())) {
                thisMemberFound = true;
                break;
            }
        }
        assertTrue(thisMemberFound);
    }
    /**
     * Test attribute operations.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testAttributes()
            throws Exception
    {
        String key1 = "key1";
        String value1 = "value1";
        String key2 = "key2";
        String value2 = "value2";
        assertNull(clusterService.getAttribute(key1));
        assertNull(clusterService.getAttribute(key2));
        clusterService.setAttribute(key1,value1);
        clusterService.setAttribute(key2,value2);
        assertEquals(value1,
                     clusterService.getAttribute(key1));
        assertEquals(value2,
                     clusterService.getAttribute(key2));
        clusterService.removeAttribute(key1);
        assertNull(clusterService.getAttribute(key1));
        assertEquals(value2,
                     clusterService.getAttribute(key2));
        clusterService.removeAttribute(key2);
        assertNull(clusterService.getAttribute(key1));
        assertNull(clusterService.getAttribute(key2));
        // test all member attribute operations
        ClusterData instanceData = clusterService.getInstanceData();
        clusterService.setAttribute(key1,
                                    value1);
        clusterService.setAttribute(key2,
                                    value2);
        Map<String,Map<String,String>> allAttributes = clusterService.getAttributes();
        Map<String,String> instanceAttributes = allAttributes.get(instanceData.getUuid());
        assertEquals(value1,
                     instanceAttributes.get(key1));
        assertEquals(value2,
                     instanceAttributes.get(key2));
        instanceAttributes = clusterService.getAttributes(instanceData.getUuid());
        assertEquals(value1,
                     instanceAttributes.get(key1));
        assertEquals(value2,
                     instanceAttributes.get(key2));
        clusterService.removeAttribute(instanceData.getUuid(),
                                       key1);
        allAttributes = clusterService.getAttributes();
        instanceAttributes = allAttributes.get(instanceData.getUuid());
        assertNull(instanceAttributes.get(key1));
        assertEquals(value2,
                     instanceAttributes.get(key2));
        instanceAttributes = clusterService.getAttributes(instanceData.getUuid());
        assertNull(instanceAttributes.get(key1));
        assertEquals(value2,
                     instanceAttributes.get(key2));
    }
    /**
     * Test that a change in attributes is detected.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testAttributeChangeDetected()
            throws Exception
    {
        final ClusterMember[] changedMember = new ClusterMember[1];
        final ClusterMember[] removedMember = new ClusterMember[1];
        final ClusterMember[] addedMember = new ClusterMember[1];
        assertNull(changedMember[0]);
        assertNull(removedMember[0]);
        assertNull(addedMember[0]);
        clusterService.addClusterListener(new ClusterListener() {
            @Override
            public void memberAdded(ClusterMember inAddedMember)
            {
                addedMember[0] = inAddedMember;
            }
            @Override
            public void memberRemoved(ClusterMember inRemovedMember)
            {
                removedMember[0] = inRemovedMember;
            }
            @Override
            public void memberChanged(ClusterMember inChangedMember)
            {
                changedMember[0] = inChangedMember;
            }}
        );
        String key1 = "key1";
        String value1 = "value1";
        clusterService.setAttribute(key1,
                                    value1);
        MarketDataFeedTestBase.wait(new Callable<Boolean>() {
            @Override
            public Boolean call()
                    throws Exception
            {
                return changedMember[0] != null;
            }}
        );
        assertNull(removedMember[0]);
        assertNull(addedMember[0]);
    }
    /**
     * Test lock operations.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testLock()
            throws Exception
    {
        ExecutorService testExecutorService = Executors.newSingleThreadExecutor();
        String lock1Name = "lock1";
        String lock2Name = "lock2";
        final Lock lock1 = clusterService.getLock(lock1Name);
        final Lock lock2 = clusterService.getLock(lock2Name);
        lock2.lock();
        Runnable task1 = new Runnable() {
            @Override
            public void run()
            {
                lock1.lock();
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    lock1.unlock();
                }
            }
        };
        testExecutorService.submit(task1);
        MarketDataFeedTestBase.wait(new Callable<Boolean>() {
            @Override
            public Boolean call()
                    throws Exception
            {
                return !lock1.tryLock();
            }}
        );
        lock1.lock();
        lock1.unlock();
        lock2.unlock();
    }
    /**
     * Test runnable tasks.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testNormalRunnableTask()
            throws Exception
    {
        TestRunnable normalTask = new TestRunnable();
        assertFalse(isTaskComplete(normalTask.token));
        clusterService.execute(normalTask);
        verifyTaskComplete(normalTask.token);
    }
    /**
     * Test multiple overlapping task execution.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testOverlappingRunnableTasks()
            throws Exception
    {
        TestRunnable longRunningTask = new TestRunnable();
        assertFalse(isTaskComplete(longRunningTask.token));
        longRunningTask.delay = 5000;
        TestRunnable shortRunningTask = new TestRunnable();
        assertFalse(isTaskComplete(shortRunningTask.token));
        clusterService.execute(longRunningTask);
        assertFalse(isTaskComplete(shortRunningTask.token));
        clusterService.execute(shortRunningTask);
        verifyTaskComplete(shortRunningTask.token);
        verifyTaskComplete(longRunningTask.token);
    }
    /**
     * Test that tasks are properly autowired.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testAutowiredRunnableTask()
            throws Exception
    {
        TestRunnable autowiredTask = new TestRunnable();
        autowiredTask.throwWithoutAutowire = true;
        assertFalse(isTaskComplete(autowiredTask.token));
        clusterService.execute(autowiredTask);
        verifyTaskComplete(autowiredTask.token);
    }
    /**
     * Test callable tasks.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testNormalCallableTask()
            throws Exception
    {
        String value = "value";
        TestCallable<String> normalTask = new TestCallable<>(value);
        assertFalse(isTaskComplete(normalTask.token));
        Map<Object,Future<String>> results = clusterService.execute(normalTask);
        verifyTaskComplete(normalTask.token);
        for(Map.Entry<Object,Future<String>> entry : results.entrySet()) {
            String resultValue = entry.getValue().get();
            assertEquals(value,
                         resultValue);
        }
    }
    /**
     * Test multiple overlapping task execution.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testOverlappingCallableTasks()
            throws Exception
    {
        String longValue = "long";
        String shortValue = "short";
        TestCallable<String> longRunningTask = new TestCallable<>(longValue);
        assertFalse(isTaskComplete(longRunningTask.token));
        longRunningTask.delay = 5000;
        TestCallable<String> shortRunningTask = new TestCallable<>(shortValue);
        assertFalse(isTaskComplete(shortRunningTask.token));
        clusterService.execute(longRunningTask);
        assertFalse(isTaskComplete(shortRunningTask.token));
        clusterService.execute(shortRunningTask);
        verifyTaskComplete(shortRunningTask.token);
        verifyTaskComplete(longRunningTask.token);
    }
    /**
     * Test that tasks are properly autowired.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testAutowiredCallableTask()
            throws Exception
    {
        TestCallable<String> autowiredTask = new TestCallable<>(UUID.randomUUID().toString());
        autowiredTask.throwWithoutAutowire = true;
        assertFalse(isTaskComplete(autowiredTask.token));
        clusterService.execute(autowiredTask);
        verifyTaskComplete(autowiredTask.token);
    }
    /**
     * Determine if the given task has completed or not.
     *
     * @param inToken a <code>String</code> value
     * @return a <code>boolean</code> value
     */
    private boolean isTaskComplete(String inToken)
    {
        return completedTasks.contains(inToken);
    }
    /**
     * Wait for the given task to complete successfully.
     *
     * @param inToken a <code>String</code> value
     * @throws Exception if the task does not complete successfully
     */
    private void verifyTaskComplete(final String inToken)
            throws Exception
    {
        MarketDataFeedTestBase.wait(new Callable<Boolean>() {
            @Override
            public Boolean call()
                    throws Exception
            {
                return completedTasks.contains(inToken);
            }
        });
    }
    /**
     * Get the clusterService value.
     *
     * @return a <code>Clazz</code> value
     */
    protected Clazz getClusterService()
    {
        return clusterService;
    }
    /**
     * Get the test cluster service object.
     *
     * @return a <code>Clazz</code> value
     */
    protected abstract Clazz createClusterService();
    /**
     * Provides a test runnable cluster task.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    private static class TestRunnable
            extends RunnableClusterTask
    {
        /* (non-Javadoc)
         * @see java.lang.Runnable#run()
         */
        @Override
        public void run()
        {
            if(throwWithoutAutowire) {
                Validate.notNull(clusterService,
                                 "Autowire failed");
            }
            if(delay > 0) {
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException e) {
                    SLF4JLoggerProxy.warn(ClusterTestBase.class,
                                          e);
                }
            }
            completedTasks.add(token);
        }
        /* (non-Javadoc)
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString()
        {
            StringBuilder builder = new StringBuilder();
            builder.append("TestRunnable [").append(token).append("]");
            return builder.toString();
        }
        /**
         * Create a new TestRunnable instance.
         */
        private TestRunnable()
        {
            token = UUID.randomUUID().toString();
            throwWithoutAutowire = false;
        }
        /**
         * uniquely identifies this task
         */
        private String token;
        /**
         * delay before marking task as complete
         */
        private long delay = 0;
        /**
         * cause the task to fail if autowire fails
         */
        private boolean throwWithoutAutowire;
        /**
         * test autowired component
         */
        @Autowired
        private transient ClusterService clusterService;
        private static final long serialVersionUID = -4729932197245597754L;
    }
    /**
     * Provides a test callable cluster task.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    private static class TestCallable<Clazz extends Serializable>
            extends CallableClusterTask<Clazz>
    {
        /* (non-Javadoc)
         * @see java.util.concurrent.Callable#call()
         */
        @Override
        public Clazz call()
                throws Exception
        {
            if(throwWithoutAutowire) {
                Validate.notNull(clusterService,
                                 "Autowire failed");
            }
            if(delay > 0) {
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException e) {
                    SLF4JLoggerProxy.warn(ClusterTestBase.class,
                                          e);
                }
            }
            completedTasks.add(token);
            return value;
        }
        /* (non-Javadoc)
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString()
        {
            StringBuilder builder = new StringBuilder();
            builder.append("TestCallable [").append(token).append("]");
            return builder.toString();
        }
        /**
         * Create a new TestCallable instance.
         */
        private TestCallable(Clazz inValue)
        {
            token = UUID.randomUUID().toString();
            throwWithoutAutowire = false;
            value = inValue;
        }
        /**
         * test value
         */
        private Clazz value;
        /**
         * uniquely identifies this task
         */
        private String token;
        /**
         * delay before marking task as complete
         */
        private long delay = 0;
        /**
         * cause the task to fail if autowire fails
         */
        private boolean throwWithoutAutowire;
        /**
         * test autowired component
         */
        @Autowired
        private transient ClusterService clusterService;
        private static final long serialVersionUID = 896626248143653072L;
    }
    /**
     * indicates if the task has completed (note that the variable is static)
     */
    protected static final Set<String> completedTasks = Sets.newHashSet();
    /**
     * test cluster service object
     */
    protected Clazz clusterService;
}
