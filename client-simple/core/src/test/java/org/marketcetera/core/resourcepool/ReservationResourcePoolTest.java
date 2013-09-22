package org.marketcetera.core.resourcepool;

import java.util.NoSuchElementException;
import java.util.concurrent.Semaphore;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.marketcetera.core.ExpectedTestFailure;
import org.marketcetera.core.MarketceteraTestSuite;
import org.marketcetera.core.Messages;
import org.marketcetera.core.resourcepool.ReservationResourcePool.ReservationEntry;
import org.marketcetera.core.resourcepool.MockReservationResourcePool.ReservationData;
import org.marketcetera.core.resourcepool.MockResource.STATE;
import org.marketcetera.util.log.SLF4JLoggerProxy;

public class ReservationResourcePoolTest
        extends TestCase
{
    protected MockReservationResourcePool mTestPool;
    protected MockReservationResourcePool.ReservationData mData;
    
    public ReservationResourcePoolTest(String inName)
    {
        super(inName);
    }

    public static Test suite() 
    {
        TestSuite suite = new MarketceteraTestSuite(ReservationResourcePoolTest.class);
//        suite.addTest(new ReservationResourcePoolTest("testGetAllocatedResourceByReservation"));
        return suite;
    }

    protected void setUp()
        throws Exception
    {
        super.setUp();

        mTestPool = new MockReservationResourcePool();
        mData = new MockReservationResourcePool.ReservationData("my_user-" + System.nanoTime(), //$NON-NLS-1$
                                                                "my_password-" + System.nanoTime()); //$NON-NLS-1$
    }
    
    public void testConstructor()
        throws Exception
    {
        assertFalse(mTestPool.getPoolIterator().hasNext());
    }
    
    public void testAddResourceToPool()
        throws Exception
    {
        assertEquals(0,
                     mTestPool.getPoolSize());
        new ExpectedTestFailure(NullPointerException.class){
            protected void execute()
                    throws Throwable
            {
                mTestPool.addResourceToPool(null);
            }
        }.run();
        assertEquals(0,
                     mTestPool.getPoolSize());
        MockResource r = new MockResource();
        mTestPool.addResourceToPool(r);
        assertEquals(1,
                     mTestPool.getPoolSize());
        assertEquals(r,
                     mTestPool.getNextResource(null));
        assertEquals(0,
                     mTestPool.getPoolSize());
        mTestPool.addResourceToPool(r);
        assertEquals(1,
                     mTestPool.getPoolSize());
        assertEquals(r,
                     mTestPool.getNextResource(null));
        assertEquals(0,
                     mTestPool.getPoolSize());
        
        mTestPool.setRenderThrows(true);
        mTestPool.addResourceToPool(r);
        // test getReservationKey returns null
        mTestPool.setRenderReturnsNull(true);
        new ExpectedTestFailure(NullPointerException.class){
            protected void execute()
                    throws Throwable
            {
                mTestPool.addResourceToPool(new MockResource());
            }
        }.run();
    }
    
    public void testReservationExistsFor()
        throws Exception
    {
        assertFalse(mTestPool.reservationExistsFor(null));
        MockResource r1 = new MockResource();
        mTestPool.addResourceToPool(r1);
        assertTrue(mTestPool.reservationExistsFor(mTestPool.getReservationKey(r1)));
        MockResource r2 = new MockResource();
        assertFalse(mTestPool.reservationExistsFor(mTestPool.getReservationKey(r2)));
    }
    
    public void testGetReservationByKey()
        throws Exception
    {
        assertNull(mTestPool.getReservationByKey(null));
        MockResource r1 = new MockResource();
        Object key = mTestPool.renderReservationKey(r1);
        assertNull(mTestPool.getReservationByKey(key));
        assertNull(mTestPool.getReservationByKey(this));
        mTestPool.addResourceToPool(r1);
        ReservationEntry entry = mTestPool.getReservationByKey(key);
        assertEquals(r1,
                     entry.getResource());
    }
    
    public void testAddToReservationBook()
        throws Exception
    {
        new ExpectedTestFailure(NullPointerException.class){
            protected void execute()
                    throws Throwable
            {
                mTestPool.addToReservationBook(null,
                                               null);
            }
        }.run();
        new ExpectedTestFailure(NullPointerException.class){
            protected void execute()
                    throws Throwable
            {
                mTestPool.addToReservationBook(this,
                                               null);
            }
        }.run();
        new ExpectedTestFailure(NullPointerException.class){
            protected void execute()
                    throws Throwable
            {
                mTestPool.addToReservationBook(null,
                                               new MockResource());
            }
        }.run();
        
        MockResource r1 = new MockResource();
        Object key = mTestPool.renderReservationKey(r1);
        assertNotNull(r1);
        assertNotNull(key);
        assertFalse(mTestPool.reservationExistsFor(key));
        
        mTestPool.addToReservationBook(key,
                                       r1);
        assertTrue(mTestPool.reservationExistsFor(key));
        ReservationEntry entry = mTestPool.getReservationByKey(key);
        assertEquals(r1,
                     entry.getResource());
        
        MockResource r2 = new MockResource();
        mTestPool.addToReservationBook(key,
                                       r2);
        assertTrue(mTestPool.reservationExistsFor(key));
        entry = mTestPool.getReservationByKey(key);
        assertEquals(r2,
                     entry.getResource());
    }
    
    public void testgetReservationKey()
        throws Exception
    {
        MockResource r1 = new MockResource();
        Object key = mTestPool.renderReservationKey(r1);
        
        assertNull(mTestPool.getReservationKey(null));
        assertEquals(key,
                     mTestPool.getReservationKey(r1));
        mTestPool.setRenderThrows(true);        
        // returns null because the reservation has not yet been added to the book, so a manual search fails
        assertFalse(mTestPool.reservationExistsFor(key));
        assertNull(mTestPool.getReservationKey(r1));
        // add it to the book
        mTestPool.setRenderThrows(false);        
        mTestPool.addResourceToPool(r1);
        // returns the key even if render throws an exception
        mTestPool.setRenderThrows(true);        
        assertTrue(mTestPool.reservationExistsFor(key));
        assertEquals(key,
                     mTestPool.getReservationKey(r1));
        // instead of throwing an exception, set render to return null
        mTestPool.setRenderThrows(false);
        mTestPool.setRenderReturnsNull(true);
        assertTrue(mTestPool.reservationExistsFor(key));
        assertEquals(key,
                     mTestPool.getReservationKey(r1));
    }
    
    public void testGetNextResource()
        throws Exception
    {
        assertEquals(0,
                     mTestPool.getPoolSize());
        // get "any" resource, that is, not one with a reservation
        MockResource r1 = mTestPool.getNextResource(null);
        assertNotNull(r1);
        assertEquals(0,
                     mTestPool.getPoolSize());
        // retrieve resource that matches credentials
        ReservationData data1 = mTestPool.renderReservationKey(r1);
        // put resource back in pool (so it can be retrieved)
        mTestPool.returnResource(r1);
        assertEquals(1,
                     mTestPool.getPoolSize());
        mTestPool.poolContains(r1);
        MockResource newR1 = mTestPool.getNextResource(data1);
        assertEquals(r1,
                     newR1);
        // request a specific resource that does not yet exist in the pool
        ReservationData data2 = new ReservationData("some_user", //$NON-NLS-1$
                                                    "some_password"); //$NON-NLS-1$
        assertFalse(mTestPool.reservationExistsFor(data2));
        MockResource r2 = mTestPool.getNextResource(data2);
        assertEquals(data2,
                     mTestPool.renderReservationKey(r2));        
    }
    
    public void testGetNextResourceExceptionHandling()
        throws Exception
    {
        // test empty pool with a walk-up request where create returns null
        assertEquals(0,
                     mTestPool.getPoolSize());
        mTestPool.setCreateResourceReturnsNull(true);
        doGetNextResourceExceptionHandlingTest(null);
        assertNull(mTestPool.getLastResourceCreated());
        
        // now create throws an exception
        assertEquals(0,
                     mTestPool.getPoolSize());
        mTestPool.setCreateResourceReturnsNull(false);
        mTestPool.setCreateResourceThrows(true);
        doGetNextResourceExceptionHandlingTest(null);
        assertNull(mTestPool.getLastResourceCreated());
        
        // add resource throws an exception (so the resource was created, make sure it was
        //  released)
        assertEquals(0,
                     mTestPool.getPoolSize());
        mTestPool.setCreateResourceReturnsNull(false);
        mTestPool.setCreateResourceThrows(false);
        mTestPool.setAddResourceThrows(true);
        doGetNextResourceExceptionHandlingTest(null);
        assertNotNull(mTestPool.getLastResourceCreated());
        assertEquals(MockResource.STATE.RELEASED,
                     mTestPool.getLastResourceCreated().getState());
        
        // repeat the same tests above, this time for a specific reservation
        ReservationData data = new ReservationData("some_user", //$NON-NLS-1$
                                                   "some_password"); //$NON-NLS-1$
        assertEquals(0,
                     mTestPool.getPoolSize());
        mTestPool.setCreateResourceReturnsNull(true);
        doGetNextResourceExceptionHandlingTest(data);
        assertNull(mTestPool.getLastResourceCreated());
        
        // now create throws an exception
        assertEquals(0,
                     mTestPool.getPoolSize());
        mTestPool.setCreateResourceReturnsNull(false);
        mTestPool.setCreateResourceThrows(true);
        doGetNextResourceExceptionHandlingTest(data);
        assertNull(mTestPool.getLastResourceCreated());
        
        // add resource throws an exception (so the resource was created, make sure it was
        //  released)
        assertEquals(0,
                     mTestPool.getPoolSize());
        mTestPool.setCreateResourceReturnsNull(false);
        mTestPool.setCreateResourceThrows(false);
        mTestPool.setAddResourceThrows(true);
        doGetNextResourceExceptionHandlingTest(data);
        assertNotNull(mTestPool.getLastResourceCreated());
        assertEquals(MockResource.STATE.RELEASED,
                     mTestPool.getLastResourceCreated().getState());
    }
    
    protected void doGetNextResourceExceptionHandlingTest(final Object inData)
        throws Exception
    {
        new ExpectedTestFailure(ResourcePoolException.class,
                                Messages.ERROR_RESOURCE_POOL_COULD_NOT_ALLOCATE_NEW_RESOURCE.getText()){
            protected void execute()
                    throws Throwable
            {
                mTestPool.getNextResource(inData);
            }
        }.run();
    }
    
    public void testRequestResource()
        throws Exception
    {
        // superclass returns a resource normall
        MockResource r1 = (MockResource)mTestPool.requestResource(null);
        assertNotNull(r1);
        assertEquals(MockResource.STATE.ALLOCATED,
                     r1.getState());
        // get superclass to throw a ResourcePoolException
        mTestPool.shutdown();
        assertEquals(ResourcePool.STATUS.SHUT_DOWN,
                     mTestPool.getStatus());
        new ExpectedTestFailure(ResourcePoolException.class,
                                Messages.ERROR_RESOURCE_POOL_SHUTTING_DOWN.getText()){
            protected void execute()
                    throws Throwable
            {
                mTestPool.requestResource(null);
            }
        }.run();
        // the contended resource tests are more complicated and set up below
    }
    
    public void testAllocateSpecificResource()
        throws Exception
    {
        assertEquals(0,
                     mTestPool.getPoolSize());
        assertNull(mTestPool.allocateResource(null));
        MockResource r1 = new MockResource();
        assertNull(mTestPool.allocateResource(r1));
        mTestPool.addResourceToPool(r1);
        assertEquals(1,
                     mTestPool.getPoolSize());
        MockResource r2 = mTestPool.allocateResource(r1);
        assertEquals(r1,
                     r2);
        assertEquals(0,
                     mTestPool.getPoolSize());
    }
    
    public void testAllocateNonspecificResource()
        throws Exception
    {
        assertEquals(0,
                     mTestPool.getPoolSize());
        new ExpectedTestFailure(NoSuchElementException.class){
            protected void execute()
                    throws Throwable
            {
                mTestPool.allocateResource();
            }
        }.run();
        MockResource r1 = new MockResource();
        MockResource r2 = new MockResource();
        MockResource r3 = new MockResource();
        mTestPool.addResourceToPool(r1);
        mTestPool.addResourceToPool(r3);
        mTestPool.addResourceToPool(r2);
        assertEquals(3,
                     mTestPool.getPoolSize());
        assertEquals(r1,
                     mTestPool.allocateResource());
        assertEquals(2,
                     mTestPool.getPoolSize());
        assertEquals(r3,
                     mTestPool.allocateResource());
        assertEquals(1,
                     mTestPool.getPoolSize());
        assertEquals(r2,
                     mTestPool.allocateResource());
        assertEquals(0,
                     mTestPool.getPoolSize());
    }
    
    public void testReleaseResource()
        throws Exception
    {
        new ExpectedTestFailure(ReleasedResourceException.class){
            protected void execute()
                    throws Throwable
            {
                mTestPool.releaseResource(null);
            }
        }.run();
        // create a resource with no reservations waiting
        MockResource r1 = mTestPool.requestResource(null);
        assertNotNull(r1);
        assertEquals(MockResource.STATE.ALLOCATED,
                     r1.getState());
        mTestPool.releaseResource(r1);
        assertEquals(MockResource.STATE.RELEASED,
                     r1.getState());
    }
    
    public void testReturnResource()
        throws Exception
    {
        new ExpectedTestFailure(ReleasedResourceException.class){
            protected void execute()
                    throws Throwable
            {
                mTestPool.returnResource(null);
            }
        }.run();

        // normal case
        MockResource r1 = mTestPool.requestResource(null);
        assertNotNull(r1);
        assertEquals(MockResource.STATE.ALLOCATED,
                     r1.getState());
        mTestPool.returnResource(r1);
        assertEquals(MockResource.STATE.RETURNED,
                     r1.getState());
        
        // make superclass throw an exception
        final MockResource r2 = mTestPool.requestResource(null);
        assertNotNull(r2);
        assertEquals(MockResource.STATE.ALLOCATED,
                     r2.getState());        
        mTestPool.shutdown();
        new ExpectedTestFailure(ResourcePoolShuttingDownException.class){
            protected void execute()
                    throws Throwable
            {
                mTestPool.returnResource(r2);
            }
        }.run();
        // reset test pool
        mTestPool = new MockReservationResourcePool();
        // render reservation key throws an exception, still able to function
        r1 = mTestPool.requestResource(null);
        assertEquals(MockResource.STATE.ALLOCATED,
                     r1.getState());
        mTestPool.setRenderThrows(true);
        mTestPool.returnResource(r1);
        assertEquals(MockResource.STATE.RETURNED,
                     r1.getState());
        // key does not exist
        final MockResource r3 = new MockResource();
        new ExpectedTestFailure(ReleasedResourceException.class){
            protected void execute()
                    throws Throwable
            {
                mTestPool.returnResource(r3);
            }
        }.run();
    }
    
    public void testGetAllocatedResourceByReservation()
        throws Exception
    {
        MockResource r1 = mTestPool.requestResource(null);
        assertEquals(MockResource.STATE.ALLOCATED,
                     r1.getState());
        Object r1Reservation = mTestPool.renderReservationKey(r1);
        assertTrue(r1Reservation instanceof MockReservationResourcePool.ReservationData);
        //      no contentions yet
        assertEquals(0,
                     r1.getContentionStamp());
        //      r1 is in the reservation system by this key
        assertEquals(r1,
                     mTestPool.getReservationByKey(r1Reservation).getResource());

        doContentionTest(r1Reservation,
                         r1,
                         r1, 
                         MockResource.STATE.ALLOCATED,
                         null);
    }

    public void testContentionThrowsResourcePoolException()
        throws Exception
    {
        MockResource r1 = mTestPool.requestResource(null);
        assertEquals(MockResource.STATE.ALLOCATED,
                     r1.getState());
        Object r1Reservation = mTestPool.renderReservationKey(r1);
        assertTrue(r1Reservation instanceof MockReservationResourcePool.ReservationData);
        //      no contentions yet
        assertEquals(0,
                     r1.getContentionStamp());
        //      r1 is in the reservation system by this key
        assertEquals(r1,
                     mTestPool.getReservationByKey(r1Reservation).getResource());
        mTestPool.setResourceContentionThrows(true);

        doContentionTest(r1Reservation,
                         r1,
                         null, 
                         MockResource.STATE.RETURNED,
                         ResourcePoolException.class);
    }

    public void testCancelledReservations()
        throws Exception
    {
        //      tests what happens when a non-functional resource is returned with outstanding requesters
        MockResource r1 = mTestPool.requestResource(null);
        assertEquals(0,
                     mTestPool.getPoolSize());
        assertEquals(MockResource.STATE.ALLOCATED,
                     r1.getState());
        Object r1Reservation = mTestPool.renderReservationKey(r1);
        assertTrue(r1Reservation instanceof MockReservationResourcePool.ReservationData);
        //      no contentions yet
        assertEquals(0,
                     r1.getContentionStamp());
        //      r1 is in the reservation system by this key
        assertEquals(r1,
                     mTestPool.getReservationByKey(r1Reservation).getResource());
        r1.setState(MockResource.STATE.DAMAGED);

        doContentionTest(r1Reservation,
                         r1,
                         null, 
                         MockResource.STATE.RELEASED,
                         ResourcePoolException.class);
    }

    protected void doContentionTest(Object inReservation,
                                    MockResource inResource,
                                    MockResource inReturnedResource,
                                    STATE inReturnedResourceState, 
                                    Class inExceptionClass)
    throws Exception
    {
        ResourceRequester requester = new ResourceRequester(inReservation);
        Thread requesterThread = new Thread(requester);
        requesterThread.start();
        requester.mStartSemaphore.acquire();
        // requester thread has started
        // the requester thread will soon block on its request for r1 (coz we have it, bwaaaahaahaahaa)        
        while(inResource.getContentionStamp() == 0) {
            Thread.sleep(100);
        }
        //      the requester thread is blocked on a request for r1
        //      let it stew for 10 seconds or so
        while(System.currentTimeMillis() - inResource.getContentionStamp() < 10000) {
            Thread.sleep(1000);
        }
        //      requester has been blocked for at least 10 seconds        
        //      return r1 so the requester thread can have it
        mTestPool.returnResource(inResource);
        //      wait for the requester to COMPLETE
        requesterThread.join();
        //      the requester should have gotten r1
        assertEquals(inReturnedResource,
                     requester.getResource());
        assertEquals(inReturnedResourceState,
                     inResource.getState());
        if(inExceptionClass != null) {
            assertEquals(inExceptionClass,
                         requester.getException().getClass());
        }
    }

    private class ResourceRequester
        implements Runnable
    {
        private Object mReservation;
        private MockResource mResource;
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
                setResource((MockResource)mTestPool.requestResource(getReservation()));
            } catch (Throwable t) {
                SLF4JLoggerProxy.debug(this, t, "Requester thread caught exception"); //$NON-NLS-1$
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
        private MockResource getResource()
        {
            return mResource;
        }

        /**
         * @param inResource the resource to set
         */
        private void setResource(MockResource inResource)
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
