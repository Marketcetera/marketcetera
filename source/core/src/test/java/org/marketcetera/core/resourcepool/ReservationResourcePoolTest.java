package org.marketcetera.core.resourcepool;

import java.util.concurrent.Semaphore;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.marketcetera.core.ExpectedTestFailure;
import org.marketcetera.core.MarketceteraTestSuite;
import org.marketcetera.core.MessageKey;

public class ReservationResourcePoolTest
        extends TestCase
{
    protected TestReservationResourcePool mTestPool;
    protected TestReservationResourcePool.ReservationData mData;
    
    public ReservationResourcePoolTest(String inName)
    {
        super(inName);
    }

    public static Test suite() 
    {
        TestSuite suite = new MarketceteraTestSuite(ReservationResourcePoolTest.class);
//        TestSuite suite = new MarketceteraTestSuite();
//        suite.addTest(new ReservationResourcePoolTest("testGetAllocatedResourceByReservation"));
        return suite;
    }
    
    protected void setUp()
            throws Exception
    {
        super.setUp();
        
        mTestPool = new TestReservationResourcePool();
        mData = new TestReservationResourcePool.ReservationData("my_user-" + System.nanoTime(),
                                                                "my_password-" + System.nanoTime());
    }

    public void testRenderReservationKey()
        throws Exception
    {
        // render returns null
        mTestPool.setReturnNullRenderKey(true);
        // pool is initially empty
        assertEquals(0,
                     mTestPool.getCurrentPoolSize());
        // should not be able to allocate a new resource
        new ExpectedTestFailure(NoResourceException.class,
                                MessageKey.ERROR_RESOURCE_POOL_COULD_NOT_ALLOCATE_NEW_RESOURCE.getLocalizedMessage()) 
        {
            protected void execute() 
                throws Exception 
            {
                mTestPool.getResource();
            }
        }.run();        
        // pool is still empty
        assertEquals(0,
                     mTestPool.getCurrentPoolSize());
        // render throws during create
        mTestPool.setReturnNullRenderKey(false);
        mTestPool.setRenderThrowsDuringCreate(true);
        new ExpectedTestFailure(NoResourceException.class,
                                MessageKey.ERROR_RESOURCE_POOL_COULD_NOT_ALLOCATE_NEW_RESOURCE.getLocalizedMessage()) 
        {
            protected void execute() 
                throws Exception 
            {
                mTestPool.getResource();
            }
        }.run();
        // pool is still empty
        assertEquals(0,
                     mTestPool.getCurrentPoolSize());

        // render throws during return (means that render does not throw during create)
        mTestPool.setReturnNullRenderKey(false);
        mTestPool.setRenderThrowsDuringCreate(false);
        mTestPool.setRenderThrowsDuringReturn(true);
        final TestResource r = (TestResource)mTestPool.getResource();
        assertEquals(mTestPool.getMinResources(),
                     mTestPool.getCurrentPoolSize());
        assertEquals(TestResource.STATE.ALLOCATED,
                     r.getState());
        // can't return it
        new ExpectedTestFailure(ResourcePoolException.class) 
        {
            protected void execute() 
                throws Exception 
            {
                mTestPool.returnResource(r);
            }
        }.run();
        // pool size has not changed
        assertEquals(mTestPool.getMinResources(),
                     mTestPool.getCurrentPoolSize());
        // verify that the pool size has not changed as a result of the max set to the min
        assertFalse(mTestPool.getMinResources() == mTestPool.getMaxResourceCreationFailures());
        // resource could not be returned
        assertEquals(TestResource.STATE.ALLOCATED,
                     r.getState());
        // now, return the resource properly
        mTestPool.setRenderThrowsDuringReturn(false);
        mTestPool.returnResource(r);
        // resource was returned
        assertEquals(mTestPool.getMinResources()+1,
                     mTestPool.getCurrentPoolSize());
        assertEquals(TestResource.STATE.RETURNED,
                     r.getState());
    }
    
    public void testGetExistingReadyResourceByReservation()
        throws Exception
    {
        // request a reservation for a resource in the pool
        // get a resource from the pool
        mTestPool.dumpReservationTable();
        mTestPool.dumpResourcePool();
        TestResource r1 = (TestResource)mTestPool.getResource();
        assertEquals(mTestPool.getMinResources(),
                     mTestPool.getCurrentPoolSize());
        Object key = mTestPool.renderReservationKey(r1);
        mTestPool.dumpReservationTable();
        mTestPool.dumpResourcePool();
        assertEquals(r1,
                     mTestPool.lookupParentReservation(key));
        // resource r1 is allocated, its reservation is in the pool table, and its key is in the variable: key
        assertEquals(TestResource.STATE.ALLOCATED,
                     r1.getState());
        // return r1 to the pool
        mTestPool.returnResource(r1);
        // check the state
        assertEquals(mTestPool.getMinResources()+1,
                     mTestPool.getCurrentPoolSize());
        assertEquals(TestResource.STATE.RETURNED,
                     r1.getState());
        mTestPool.dumpReservationTable();
        mTestPool.dumpResourcePool();
        // request a READY resource (r1)
        TestResource newR1 = (TestResource)mTestPool.getResource(key);
        // make sure we got back the resource we wanted
        assertEquals(r1,
                     newR1);
        // check the new state
        assertEquals(TestResource.STATE.ALLOCATED,
                     r1.getState());
        assertEquals(mTestPool.getMinResources(),
                     mTestPool.getCurrentPoolSize());
        mTestPool.dumpReservationTable();
        mTestPool.dumpResourcePool();        
    }

    public void testGetNonexistingReadyResourceByReservation()
        throws Exception
    {
        // request a reservation for a resource not in the pool
        assertEquals(0,
                     mTestPool.getCurrentPoolSize());
        // create a reservation data object
        // this reservation should not exist
        mTestPool.dumpReservationTable();
        mTestPool.dumpResourcePool();
        assertNull(mTestPool.lookupParentReservation(mData));
        // request a resource with the given credentials (doesn't exist now, should be created)
        TestResource r1 = (TestResource)mTestPool.getResource(mData);
        mTestPool.dumpReservationTable();
        mTestPool.dumpResourcePool();
        assertNotNull(r1);
        // r1 was created as we suggested - others were created, too, to round up to min (actually, the number of resources
        //  will be min +1 because the pool is filled with generic resources up to min, then the specific one requested
        //  makes min +1)
        assertEquals(mTestPool.getMinResources()+1,
                     mTestPool.getCurrentPoolSize());
    }

    public void testGetNonexistingResourceByReservationPoolAtMax()
        throws Exception
    {
        // request a reservation for a resource not in the pool
        assertEquals(0,
                     mTestPool.getCurrentPoolSize());
        // set the max capacity at 2
        mTestPool.setTestMinResources(1);
        mTestPool.setTestMaxResources(2);
        // request a walkup resource and put it back, thus putting the pool at maxium
        TestResource r1 = (TestResource)mTestPool.getResource();
        assertEquals(1,
                     mTestPool.getCurrentPoolSize());
        mTestPool.returnResource(r1);
        assertEquals(mTestPool.getMaxResources(),
                     mTestPool.getCurrentPoolSize());
        // now, request a resource by reservation, and the resource does not exist
        assertNull(mTestPool.lookupParentReservation(mData));
        // make the request, which should cause the pool to try to create a new resource to match the request
        // since we're at max already, the request should fail
        new ExpectedTestFailure(NoResourceException.class,
                                MessageKey.ERROR_RESOURCE_POOL_COULD_NOT_ALLOCATE_NEW_RESOURCE.getLocalizedMessage()) 
        {
            protected void execute() 
                throws Exception 
            {
                mTestPool.getResource(mData);
            }
        }.run();
        
        // set the pool max 1 higher
        mTestPool.setTestMaxResources(mTestPool.getMaxResources() + 1);
        r1 = (TestResource)mTestPool.getResource(mData);
        assertEquals(mData,
                     mTestPool.renderReservationKey(r1));
    }
    
    public void testResourceCreationThrowsException()
        throws Exception
    {
        mTestPool.setCreateResourceException(new ResourceCreationException("this exception is expected"));
        new ExpectedTestFailure(NoResourceException.class,
                                MessageKey.ERROR_RESOURCE_POOL_COULD_NOT_ALLOCATE_NEW_RESOURCE.getLocalizedMessage()) 
        {
            protected void execute() 
                throws Exception 
            {
                mTestPool.getResource(mData);
            }
        }.run();        

        mTestPool.setCreateResourceException(new NullPointerException("this exception is expected"));
        new ExpectedTestFailure(NoResourceException.class,
                                MessageKey.ERROR_RESOURCE_POOL_COULD_NOT_ALLOCATE_NEW_RESOURCE.getLocalizedMessage()) 
        {
            protected void execute() 
                throws Exception 
            {
                mTestPool.getResource(mData);
            }
        }.run();        
    }
    
    public void testGetAllocatedResourceByReservation()
        throws Exception
    {
        TestResource r1 = (TestResource)mTestPool.getResource();
        assertEquals(mTestPool.getMinResources(),
                     mTestPool.getCurrentPoolSize());
        assertEquals(TestResource.STATE.ALLOCATED,
                     r1.getState());
        Object r1Reservation = mTestPool.renderReservationKey(r1);
        assertTrue(r1Reservation instanceof TestReservationResourcePool.ReservationData);
        // no contentions yet
        assertEquals(0,
                     r1.getContentionCounter());
        // r1 is in the reservation system by this key
        assertEquals(r1,
                     mTestPool.lookupParentReservation(r1Reservation));

        ResourceRequester requester = new ResourceRequester(r1Reservation);
        Thread requesterThread = new Thread(requester);
        requesterThread.start();
        requester.mStartSemaphore.acquire();
        // requester thread has started
        // the requester thread is now running through iterations asking for r1 over and over
        // the requester thread can't have it until we let it go
        // let the requester thread ask for r1 10 times before we take pity
        while(r1.getContentionCounter() <= 10) {
            Thread.sleep(100);
        }
        // r1 has been requested at least 10 times
        // return r1 so the requester thread can have it
        mTestPool.returnResource(r1);
        // wait for the requester to complete
        requesterThread.join();
        // the requester should have gotten r1
        assertEquals(r1,
                     requester.getResource());
        assertEquals(TestResource.STATE.ALLOCATED,
                     r1.getState());
        assertNull(requester.getException());
    }
    
    private class ResourceRequester
        implements Runnable
    {
        private Object mReservation;
        private TestResource mResource;
        private Throwable mException;
        private Semaphore mStartSemaphore;
        
        private ResourceRequester(Object inReservation) 
            throws InterruptedException
        {
            setReservation(inReservation);
            setResource(null);
            setException(null);
            mStartSemaphore = new Semaphore(1);
            mStartSemaphore.acquire();
        }
        
        public void run()
        {
            mStartSemaphore.release();
            try {
                setResource((TestResource)mTestPool.getResource(getReservation()));
            } catch (Throwable t) {
                setException(t);
            }
            // we have the resource now
        }
        
        /**
         * @return the reservation
         */
        private Object getReservation()
        {
            return mReservation;
        }
        /**
         * @param inReservation the reservation to set
         */
        private void setReservation(Object inReservation)
        {
            mReservation = inReservation;
        }

        /**
         * @return the resource
         */
        private TestResource getResource()
        {
            return mResource;
        }

        /**
         * @param inResource the resource to set
         */
        private void setResource(TestResource inResource)
        {
            mResource = inResource;
        }

        /**
         * @return the exception
         */
        private Throwable getException()
        {
            return mException;
        }

        /**
         * @param inException the exception to set
         */
        private void setException(Throwable inException)
        {
            mException = inException;
        }
    }
}
