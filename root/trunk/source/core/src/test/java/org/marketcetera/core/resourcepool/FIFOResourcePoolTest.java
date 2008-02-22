package org.marketcetera.core.resourcepool;

import java.util.ArrayList;
import java.util.Random;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.marketcetera.core.ExpectedTestFailure;
import org.marketcetera.core.MarketceteraTestSuite;
import org.marketcetera.core.MessageKey;

/**
 * Tests {@link ResourcePool}. 
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: $
 */
public class FIFOResourcePoolTest 
	extends TestCase 
{
    protected TestFIFOResourcePool mTestPool;
    
	public FIFOResourcePoolTest(String inName) 
	{
		super(inName);
	}
	
    public static Test suite() 
    {
        TestSuite suite = new MarketceteraTestSuite(FIFOResourcePoolTest.class);
//        suite.addTest(new FIFOResourcePoolTest("testDuringShutdown"));
        return suite;
    }
    
    protected void setUp()
            throws Exception
    {
        super.setUp();
        
        mTestPool = new TestFIFOResourcePool();
        TestResource.setInitializeException(null);
        TestResource.setAllocateException(null);
    }
    
    public void testResourcePoolConfiguration()
        throws Exception
    {
        verifyPoolSize(0);
        mTestPool.setMinResources(0);
        new ExpectedTestFailure(ResourcePoolConfigurationException.class,
                                MessageKey.ERROR_RESOURCE_POOL_RESOURCE_MINIMUM_CONFIGURATION.getLocalizedMessage()) 
        {
            protected void execute() 
                throws Exception 
            {
                mTestPool.getResource();
            }
        }.run();

        verifyPoolSize(0);
        mTestPool.setMinResources(-1);
        new ExpectedTestFailure(ResourcePoolConfigurationException.class,
                                MessageKey.ERROR_RESOURCE_POOL_RESOURCE_MINIMUM_CONFIGURATION.getLocalizedMessage()) 
        {
            protected void execute() 
                throws Exception 
            {
                mTestPool.getResource();
            }
        }.run();

        verifyPoolSize(0);
        mTestPool.setMinResources(1);
        mTestPool.setMaxResources(0);
        new ExpectedTestFailure(ResourcePoolConfigurationException.class,
                                MessageKey.ERROR_RESOURCE_POOL_RESOURCE_MAXIMUM_CONFIGURATION.getLocalizedMessage()) 
        {
            protected void execute() 
                throws Exception 
            {
                mTestPool.getResource();
            }
        }.run();
    }
    
    public void testSerialResourceCheckOut()
        throws Exception
    {
        // test sequential checkout of two resources
        mTestPool.setMinResources(1);
        mTestPool.setMaxResources(1);
        verifyPoolSize(0);
        TestResource resource1 = (TestResource)mTestPool.getResource();
        assertEquals(0,
                     mTestPool.getActualFailureCounter());
        verifyPoolSize(1);
        TestResource resource2 = (TestResource)mTestPool.getResource();
        verifyPoolSize(1);
        assertFalse(resource1.equals(resource2));
    }
    
    public void testResourceCreationFailure()
        throws Exception
    {        
        mTestPool.setMinResources(1);
        // let creation fail twice before giving up
        mTestPool.setMaxCreationFailures(2);
        // set the pool to fail once before succeeding
        mTestPool.setTimesToFailCreationBeforeSuccess(1);
        // check the starting state of the pool
        verifyPoolSize(0);        
        // allocate a resource
        mTestPool.getResource();
        // verify the pool is populated
        verifyPoolSize(1);
        // verify it failed twice (created min + 1 or 2 resources, failed once each time)        
        assertEquals(2,
                     mTestPool.getActualFailureCounter());
        // reset the failure counter
        mTestPool.resetActualFailureCounter();
        // increase the number of times between successes
        // still OK, because it will fail twice and the maximum allowed value is 2
        mTestPool.setTimesToFailCreationBeforeSuccess(2);
        mTestPool.getResource();
        // verify the pool is populated
        verifyPoolSize(1);
        // verify it failed twice (created min + 1 or 2 resources, failed once each time)        
        assertEquals(2,
                     mTestPool.getActualFailureCounter());
        // reset the failure counter
        mTestPool.resetActualFailureCounter();
        // empty the pool to create a condition where a resource can't be allocated
        mTestPool.emptyParentPool();
        // increase the number of times between successes
        // now should exceed the maximum of 2
        mTestPool.setTimesToFailCreationBeforeSuccess(3);
        // should now fail to allocate a resource
        new ExpectedTestFailure(NoResourceException.class,
                                MessageKey.ERROR_RESOURCE_POOL_COULD_NOT_ALLOCATE_NEW_RESOURCE.getLocalizedMessage()) 
        {
            protected void execute() 
                throws Exception 
            {
                mTestPool.getResource();
            }
        }.run();
    }
    
    public void testMaxResources()
        throws Exception
    {
        // tests resource return around the max pool limit
        mTestPool.setMinResources(2);
        mTestPool.setMaxResources(5);
        verifyPoolSize(0);
        // request one resource
        TestResource r1 = (TestResource)mTestPool.getResource();
        // check new pool size
        verifyPoolSize(2);
        // request a second, third, and fourth resource
        TestResource r2 = (TestResource)mTestPool.getResource();
        verifyPoolSize(2);
        TestResource r3 = (TestResource)mTestPool.getResource();
        verifyPoolSize(2);
        TestResource r4 = (TestResource)mTestPool.getResource();
        verifyPoolSize(2);

        // start putting the resources back
        mTestPool.returnResource(r1);
        verifyPoolSize(3);
        assertEquals(TestResource.STATE.RETURNED,
                     r1.getState());

        mTestPool.returnResource(r2);
        verifyPoolSize(4);
        assertEquals(TestResource.STATE.RETURNED,
                     r2.getState());

        mTestPool.returnResource(r3);
        verifyPoolSize(5);
        assertEquals(TestResource.STATE.RETURNED,
                     r3.getState());
        // here's the interesting one
        mTestPool.returnResource(r4);
        verifyPoolSize(5);
        assertEquals(TestResource.STATE.RELEASED,
                     r4.getState());
    }
    
    public void testReturnBrokenResource()
        throws Exception
    {
        // this test verifies that a broken resource will not be added back to the pool
        mTestPool.setMaxCreationFailures(100);
        mTestPool.setMaxResources(100);
        mTestPool.setMinResources(1);
        verifyPoolSize(0);
        TestResource r1 = (TestResource)mTestPool.getResource();
        verifyPoolSize(1);
        // verify it's not broken
        assertTrue(r1.isFunctional());
        // put it back
        mTestPool.returnResource(r1);
        verifyPoolSize(2);
        assertEquals(TestResource.STATE.RETURNED,
                     r1.getState());
        // get the resource back
        r1 = (TestResource)mTestPool.getResource();
        verifyPoolSize(1);
        // "break" it
        r1.setState(TestResource.STATE.DAMAGED);
        assertFalse(r1.isFunctional());
        // put back broken resource
        mTestPool.returnResource(r1);
        // pool count is unchanged
        verifyPoolSize(1);
        assertEquals(TestResource.STATE.RELEASED,
                     r1.getState());
        // get "other" resource from pool and verify it is not our first one
        TestResource r2 = (TestResource)mTestPool.getResource();
        assertFalse(r1.equals(r2));
        // repeat the test with a smaller max, just to be sure
        mTestPool.emptyParentPool();
        mTestPool.setMaxResources(1);
        r1 = (TestResource)mTestPool.getResource();
        verifyPoolSize(1);
        r1.setState(TestResource.STATE.DAMAGED);
        assertFalse(r1.isFunctional());
        // two reasons now it shouldn't go back in the pool
        mTestPool.returnResource(r1);
        verifyPoolSize(1);
        assertEquals(TestResource.STATE.RELEASED,
                     r1.getState());
    }
    
    public void testResourceInitializationFailure()
        throws Exception
    {
        // tests that an otherwise functional pool cannot return resources because of a
        //  failure during resource initialization
        mTestPool.setMaxCreationFailures(100);
        mTestPool.setMaxResources(100);
        mTestPool.setMinResources(1);
        verifyPoolSize(0);
        // set all resources to fail initialize
        TestResource.setInitializeException(new ResourceCreationException("This error is expected"));
        // try to create a resource
        new ExpectedTestFailure(NoResourceException.class,
                                MessageKey.ERROR_RESOURCE_POOL_COULD_NOT_ALLOCATE_NEW_RESOURCE.getLocalizedMessage()) 
        {
            protected void execute() 
                throws Exception 
            {
                mTestPool.getResource();
            }
        }.run();
        // set resources to use a more pernicious exception
        TestResource.setInitializeException(new NullPointerException("This error is expected"));
        // try to create a resource
        new ExpectedTestFailure(NoResourceException.class,
                                MessageKey.ERROR_RESOURCE_POOL_COULD_NOT_ALLOCATE_NEW_RESOURCE.getLocalizedMessage()) 
        {
            protected void execute() 
                throws Exception 
            {
                mTestPool.getResource();
            }
        }.run();
    }

    public void testResourceAllocationFailure()
        throws Exception
    {
        // tests that an otherwise functional pool cannot return resources because of a
        //  failure during resource allocation
        mTestPool.setMaxCreationFailures(100);
        mTestPool.setMaxResources(100);
        mTestPool.setMinResources(1);
        verifyPoolSize(0);
        // set all resources to fail allocate
        TestResource.setAllocateException(new NullPointerException("This error is expected"));
        // try to create a resource
        new ExpectedTestFailure(NoResourceException.class,
                               MessageKey.ERROR_RESOURCE_POOL_COULD_NOT_ALLOCATE_NEW_RESOURCE.getLocalizedMessage()) 
        {
            protected void execute() 
                throws Exception 
            {
                mTestPool.getResource();
            }
        }.run();
    }
    
    public void testFIFOAllocation()
        throws Exception
    {
        verifyPoolSize(0);
        mTestPool.setMinResources(2);
        mTestPool.setMaxCreationFailures(10);
        // get two resources
        mTestPool.getResource();
        TestResource r2 = (TestResource)mTestPool.getResource();
        // retrieve one other
        mTestPool.getResource();
        verifyPoolSize(2);
        // we now have 5 resources in existance
        // r4-r5 are currently in the pool
        // if we put back r2, and take out two resources, the second should be r2 again
        mTestPool.returnResource(r2);
        // the pool now contains r4, r5, r2
        verifyPoolSize(3);
        mTestPool.getResource();
        verifyPoolSize(2);
        // the pool now contains r5, r2
        mTestPool.getResource();
        verifyPoolSize(2);
        // the pool now contains r2, r6      
        TestResource newR2 = (TestResource)mTestPool.getResource();
        verifyPoolSize(2);
        assertEquals(r2,
                     newR2);
    }
    
    /**
     * Verifies proper handling of a resource throwing an exception upon being returned.
     */
    public void testReturnException()
        throws Exception
    {
        verifyPoolSize(0);
        mTestPool.setMinResources(2);
        final TestResource r1 = (TestResource)mTestPool.getResource();
        verifyPoolSize(2);
        r1.setReturnException(new NullPointerException("This exception is supposed to be thrown"));
        new ExpectedTestFailure(ReturnedResourceException.class) 
        {
            protected void execute() 
                throws Exception 
            {
                mTestPool.returnResource(r1);
            }
        }.run();        
    }
    
    /**
     * Verifies proper handling of a resource throwing an exception upon being released.
     */
    public void testReleaseException()
        throws Exception
    {
        verifyPoolSize(0);
        mTestPool.setMinResources(2);
        final TestResource r1 = (TestResource)mTestPool.getResource();
        verifyPoolSize(2);
        r1.setReleaseException(new NullPointerException("This exception is supposed to be thrown"));
        r1.setState(TestResource.STATE.DAMAGED);
        new ExpectedTestFailure(ReleasedResourceException.class) 
        {
            protected void execute() 
                throws Exception 
            {
                mTestPool.returnResource(r1);
            }
        }.run();        
    }
    
    public void testReturnResourceTwice()
        throws Exception
    {
        verifyPoolSize(0);
        mTestPool.setMinResources(2);
        final TestResource r1 = (TestResource)mTestPool.getResource();
        verifyPoolSize(2);
        mTestPool.returnResource(r1);
        new ExpectedTestFailure(DuplicateResourceReturnException.class) 
        {
            protected void execute() 
                throws Exception 
            {
                mTestPool.returnResource(r1);
            }
        }.run();        
    }
    
    public void testResourceCreationFailureRecovery()
        throws Exception
    {
        mTestPool.setMinResources(1);
        mTestPool.setMaxCreationFailures(2);
        mTestPool.setMaxResources(1);
        // test pool can't create new resources, can't be fixed
        mTestPool.setPoolBroken(true);
        mTestPool.setFixPool(false);
        new ExpectedTestFailure(NoResourceException.class) 
        {
            protected void execute() 
                throws Exception 
            {
                mTestPool.getResource();
            }
        }.run();
        // test pool can't create new resources, can be fixed
        mTestPool.setFixPool(true);
        mTestPool.getResource();
        // now make test pool throw an exception, make sure it gets handled
        mTestPool.setResourceCreationFailedException(true);
        mTestPool.setPoolBroken(true);
        mTestPool.setFixPool(true);
        // exception should be swallowed
        mTestPool.getResource();
    }
    
    public void testIsFunctionalThrowsException()
        throws Exception
    {
        TestResource r = (TestResource)mTestPool.getResource();
        r.setIsFunctionalException(true);
        mTestPool.returnResource(r);
        assertEquals(TestResource.STATE.RELEASED,
                     r.getState());
    }
    
    public void testDuringShutdown()
        throws Exception
    {
        assertEquals(ResourcePool.STATUS.READY,
                     mTestPool.getParentStatus());
        final TestResource r1 = (TestResource)mTestPool.getResource();
        final TestResource r2 = (TestResource)mTestPool.getResource();
        mTestPool.returnResource(r2);
        mTestPool.shutdown();
        assertEquals(ResourcePool.STATUS.SHUT_DOWN,
                     mTestPool.getParentStatus());
        // can request another shutdown
        mTestPool.shutdown();
        // can't get new resource
        new ExpectedTestFailure(ResourcePoolShuttingDownException.class,
                                MessageKey.ERROR_RESOURCE_POOL_SHUTTING_DOWN.getLocalizedMessage()) 
        {
            protected void execute() 
                throws Exception 
            {
                mTestPool.getResource();
            }
        }.run();
        // can't return existing resource
        new ExpectedTestFailure(ResourcePoolShuttingDownException.class,
                                MessageKey.ERROR_RESOURCE_POOL_SHUTTING_DOWN.getLocalizedMessage()) 
        {
            protected void execute() 
                throws Exception 
            {
                mTestPool.returnResource(r1);
            }
        }.run();
        // resource in pool is shutdown
        assertEquals(TestResource.STATE.SHUTDOWN,
                     r2.getState());
    }
    
    public void testParallel()
        throws Exception
    {
        mTestPool.setMinResources(10);
        mTestPool.setMaxResources(100);
        // create 100 ActionThreads and fire them off
        // each one will perform 1000 iterations
        ArrayList<Thread> threads = new ArrayList<Thread>();
        for(int i=1;i<100;i++) {
            threads.add(new Thread(new ActionThread(1000,
                                                    mTestPool)));
        }
        // now, start the Threads (do it as a separate loop in order to
        //  allow the heavier-weight object initialization to happen serially
        for(Thread thread : threads) {
            thread.start();
        }
        // good, now wait for all the threads to complete
        for(Thread thread : threads) {
            thread.join();
        }
        // last, make sure no exceptions were thrown
        assertTrue(ActionThread.sExceptions.isEmpty());
    }
     
    private static class ActionThread
        implements Runnable
    {
        private final int mIterations;
        private final ResourcePool mPool;
        private static final ArrayList<Throwable> sExceptions;
        private static final Random sRandom;
        
        static 
        {
            sRandom = new Random(System.nanoTime());
            sExceptions = new ArrayList<Throwable>();
        }
        
        private ActionThread(int inIterations,
                             ResourcePool inPool)        
        {
            mIterations = inIterations;
            mPool = inPool;
        }
        
        public void run()        
        {
            int iterationCount = 0;
            while(iterationCount < mIterations) {
                try {
                    Resource r = mPool.getResource();
                    // sleep for as much as 1/100th of a second
                    Thread.sleep(sRandom.nextInt(10));
                    mPool.returnResource(r);
                    iterationCount += 1;
                } catch (Throwable t) {
                    synchronized(sExceptions) {
                        sExceptions.add(t);
                    }
                }
            }
//            mPool.shutdown();
        }        
    }

    protected void verifyPoolSize(int inSize)
    {
        assertEquals(inSize,
                     mTestPool.getResourcePoolSize());
    }
}
