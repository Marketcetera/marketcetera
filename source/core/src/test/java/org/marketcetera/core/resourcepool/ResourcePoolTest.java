package org.marketcetera.core.resourcepool;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.marketcetera.core.ExpectedTestFailure;
import org.marketcetera.core.LoggerAdapter;
import org.marketcetera.core.MarketceteraTestSuite;
import org.marketcetera.core.MessageKey;

public class ResourcePoolTest
        extends TestCase
{
    private TestResourcePool mTestPool;
    
    public ResourcePoolTest(String inArg0)
    {
        super(inArg0);
    }

    public static Test suite() 
    {
        TestSuite suite = new MarketceteraTestSuite(ResourcePoolTest.class);
//        suite.addTest(new ResourcePoolTest("testRequestResource"));
        return suite;
    }    

    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp()
            throws Exception
    {
        super.setUp();
        mTestPool = new TestResourcePool();
    }

    public void testConstructor()
        throws Exception
    {
        assertEquals(ResourcePool.STATUS.READY,
                     mTestPool.getStatus());
    }
    
    public void testShutdownEmpty()
        throws Exception
    {
        assertFalse(mTestPool.getPoolIterator().hasNext());
        doShutdownTest();
    }
    
    public void testShutdownPopulated()
        throws Exception
    {
        assertFalse(mTestPool.getPoolIterator().hasNext());
        TestResource r = mTestPool.requestResource(null);
        assertNotNull(r);
        mTestPool.returnResource(r);
        assertTrue(mTestPool.getPoolIterator().hasNext());
        assertEquals(TestResource.STATE.RETURNED,
                     r.getState());
        doShutdownTest();
        assertEquals(TestResource.STATE.SHUTDOWN,
                     r.getState());
    }
    
    public void testShutdownThrowsDuringStop()
        throws Exception
    {
        TestResource r = mTestPool.requestResource(null);
        assertNotNull(r);
        mTestPool.returnResource(r);
        r.setThrowDuringStop(true);
        doShutdownTest();
        assertEquals(TestResource.STATE.SHUTDOWN,
                     r.getState());
    }

    protected void doShutdownTest()
        throws Exception
    {
        assertFalse(mTestPool.rejectNewRequests());
        mTestPool.shutdown();
        assertTrue(mTestPool.rejectNewRequests());
        assertEquals(ResourcePool.STATUS.SHUT_DOWN,
                     mTestPool.getStatus());
        mTestPool.shutdown();
    }
    
    public void testRequestResource()
        throws Exception
    {
        assertFalse(mTestPool.rejectNewRequests());
        assertNull(mTestPool.getThrowDuringGetNextResource());
        doRequestTest();

        mTestPool.setThrowDuringGetNextResource(new ResourcePoolException("this exception is expected"));
        new ExpectedTestFailure(ResourcePoolException.class) {
            protected void execute()
                    throws Throwable
            {
                doRequestTest();
            }            
        }.run();

        mTestPool.setThrowDuringGetNextResource(new NullPointerException("this exception is expected"));
        new ExpectedTestFailure(ResourcePoolException.class) {
            protected void execute()
                    throws Throwable
            {
                doRequestTest();
            }            
        }.run();
        mTestPool.setThrowDuringGetNextResource(null);
        
        try {
            TestResource.setAllocateException(new NullPointerException("This exception is expected"));
            doRequestTest();
        } finally {
            TestResource.setAllocateException(null);
        }

        mTestPool.setThrowDuringGetNextResource(null);
        mTestPool.shutdown();
        assertTrue(mTestPool.rejectNewRequests());
        new ExpectedTestFailure(ResourcePoolShuttingDownException.class,
                                MessageKey.ERROR_RESOURCE_POOL_SHUTTING_DOWN.getLocalizedMessage()) {
            protected void execute()
                    throws Throwable
            {
                doRequestTest();
            }            
        }.run();        
    }
    
    protected void doRequestTest()
        throws Exception
    {
        TestResource r1 =  mTestPool.requestResource(null);
        assertNotNull(r1);
        TestResource r2 =  mTestPool.requestResource(this);
        assertNotNull(r2);
    }
    
    public void testReturnResource()
        throws Exception
    {
        for(int value1=0;value1<=1;value1++) {
            for(int value2=0;value2<=1;value2++) {
                for(int value3=0;value3<=1;value3++) {
                    for(int value4=0;value4<=1;value4++) {
                        for(int value5=0;value5<=1;value5++) {
                            for(int value6=0;value6<=2;value6++) {
                                Boolean addFlag = null;
                                if(value6 == 1) {
                                    addFlag = new Boolean(false);
                                }
                                if(value6 == 2) {
                                    addFlag = new Boolean(true);
                                }
                                if(LoggerAdapter.isDebugEnabled(this)) {
                                    LoggerAdapter.debug(value6 + " " + value1 + " " + value2 + " " + value3 + " " + value4 + " " + value5,
                                                        this);
                                }
                                doReturnTest(value1==1, 
                                             value2==1, 
                                             value3==1, 
                                             value4==1,
                                             value5==1, 
                                             addFlag);
                            }
                        }
                    }
                }
            }
        }
        
        // test with shutdown        
        assertEquals(ResourcePool.STATUS.READY,
                     mTestPool.getStatus());
        mTestPool.shutdown();
        assertEquals(ResourcePool.STATUS.SHUT_DOWN,
                     mTestPool.getStatus());
        assertTrue(mTestPool.rejectNewRequests());
        
        new ExpectedTestFailure(ResourcePoolShuttingDownException.class,
                                MessageKey.ERROR_RESOURCE_POOL_SHUTTING_DOWN.getLocalizedMessage()) {
            protected void execute()
                throws Throwable
            {
                doReturnTest(true,
                             false,
                             false, 
                             false, 
                             false, 
                             false);
            }}.run();
    }
    
    void doReturnTest(boolean inResourceFunctional, 
                      boolean inIsFunctionalThrows, 
                      boolean inReleaseThrows, 
                      boolean inReturnedThrows, 
                      boolean inVerify, 
                      Boolean inAddToPoolThrows)
        throws Exception
    {
        assertFalse(mTestPool.getPoolIterator().hasNext());
        TestResource r1 = mTestPool.requestResource(null);
        assertNotNull(r1);
        assertEquals(TestResource.STATE.ALLOCATED,
                     r1.getState());
        assertFalse(mTestPool.getPoolIterator().hasNext());
        r1.setIsFunctionalException(inIsFunctionalThrows);
        r1.setReleaseException(inReleaseThrows);
        r1.setState(inResourceFunctional ? r1.getState() : TestResource.STATE.DAMAGED);
        r1.setReturnException(inReturnedThrows);
        mTestPool.setThrowDuringVerify(inVerify);
        mTestPool.setThrowDuringAddToPool(inAddToPoolThrows);

        mTestPool.returnResource(r1);

        if(inResourceFunctional) {
            if(inIsFunctionalThrows) {
                assertFalse(mTestPool.getPoolIterator().hasNext());
                assertEquals(TestResource.STATE.RELEASED,
                             r1.getState());
            } else {
                if(inVerify) {
                    assertFalse(mTestPool.getPoolIterator().hasNext());
                    assertEquals(TestResource.STATE.RELEASED,
                                 r1.getState());
                } else {
                    if(inAddToPoolThrows != null &&
                       inAddToPoolThrows.equals(new Boolean(false))) {
                        assertTrue(mTestPool.getPoolIterator().hasNext());
                        assertEquals(TestResource.STATE.RETURNED,
                                     r1.getState());
                    }
                }
            }
        } else {
            assertFalse(mTestPool.getPoolIterator().hasNext());
            assertEquals(TestResource.STATE.RELEASED,
                         r1.getState());
        }
        
        mTestPool.emptyPool();
    }
    
    public void testReleaseResource()
        throws Exception
    {
        TestResource r1 = mTestPool.requestResource(null);
        assertNotNull(r1);
        r1.setReleaseException(false);
        assertEquals(TestResource.STATE.ALLOCATED,
                     r1.getState());
        mTestPool.releaseResource(r1);
        assertEquals(TestResource.STATE.RELEASED,
                     r1.getState());
        final TestResource r2 = mTestPool.requestResource(null);
        assertNotNull(r2);
        r2.setReleaseException(true);
        new ExpectedTestFailure(ReleasedResourceException.class) {
            protected void execute()
                throws Throwable
            {
                mTestPool.releaseResource(r2);
            }}.run();
    }
    
    public void testExecute()
        throws Exception
    {
        doExecuteTest(new TestExecutable(), 
                      true);
        
        mTestPool.setThrowDuringGetNextResource(new ResourcePoolException("This exception is expected"));
        new ExpectedTestFailure(ResourcePoolException.class,
                                MessageKey.ERROR_RESOURCE_POOL_COULD_NOT_ALLOCATE_NEW_RESOURCE.getLocalizedMessage()) {
            protected void execute()
                throws Throwable
            {
                doExecuteTest(new TestExecutable(), 
                              true);
            }            
        }.run();

        mTestPool.setThrowDuringGetNextResource(new NullPointerException("This exception is expected"));
        new ExpectedTestFailure(ResourcePoolException.class,
                                MessageKey.ERROR_RESOURCE_POOL_COULD_NOT_ALLOCATE_NEW_RESOURCE.getLocalizedMessage()) {
            protected void execute()
                throws Throwable
            {
                doExecuteTest(new TestExecutable(), 
                              true);
            }            
        }.run();
        
        mTestPool.setThrowDuringGetNextResource(null);
        new ExpectedTestFailure(NullPointerException.class) {
            protected void execute()
                throws Throwable
            {
                doExecuteTest(null, 
                              true);
            }            
        }.run();
        
        final TestExecutable block = new TestExecutable();
        block.setThrowable(new NullPointerException("This exception is expected"));
        new ExpectedTestFailure(ResourcePoolException.class,
                                MessageKey.ERROR_RESOURCE_POOL_EXECUTABLE_BLOCK_ERROR.getLocalizedMessage()) {
            protected void execute()
                throws Throwable
            {
                doExecuteTest(block, 
                              true);
            }            
        }.run();
        
        doExecuteTest(new TestExecutable(),
                      false);
        
        mTestPool.setThrowDuringReturnResource(new ResourcePoolException("This exception is expected"));
        new ExpectedTestFailure(ResourcePoolException.class) {
            protected void execute()
                throws Throwable
            {
                doExecuteTest(new TestExecutable(),
                              false);
            }            
        }.run();
        
        mTestPool.setThrowDuringReturnResource(new NullPointerException("This exception is expected"));
        new ExpectedTestFailure(ResourcePoolException.class) {
            protected void execute()
                throws Throwable
            {
                doExecuteTest(new TestExecutable(),
                              false);
            }            
        }.run();
        
    }
    
    protected void doExecuteTest(TestExecutable inBlock, 
                                 boolean inResourceFunctional)
        throws Exception
    {
        assertNull(inBlock.getResource());
        inBlock.setSabotageResource(!inResourceFunctional);
        
        mTestPool.execute(inBlock);
        
        assertNotNull(inBlock.getResource());
        assertEquals(inResourceFunctional ? TestResource.STATE.RETURNED : TestResource.STATE.RELEASED,
                     inBlock.getResource().getState());
        assertEquals(TestResource.STATE.ALLOCATED,
                     inBlock.getInternalState());

        inBlock.setResource(null);
        inBlock.setSabotageResource(!inResourceFunctional);

        mTestPool.execute(inBlock,
                          this);        

        assertNotNull(inBlock.getResource());
        assertEquals(inResourceFunctional ? TestResource.STATE.RETURNED : TestResource.STATE.RELEASED,
                     inBlock.getResource().getState());
        assertEquals(TestResource.STATE.ALLOCATED,
                     inBlock.getInternalState());
    }
    
    static class TestExecutable
        implements ExecutableBlock
    {
        private TestResource mResource;
        private Throwable mThrowable;
        private TestResource.STATE mInternalState;
        private boolean mSabotageResource;
        
        TestExecutable()
        {
            this(null);
        }

        TestExecutable(Throwable inThrowable)
        {
            setResource(null);
            setThrowable(inThrowable);
            setInternalState(null);
            setSabotageResource(false);
        }
        
        public void execute(Resource inResource)
                throws Throwable
        {
            setResource((TestResource)inResource);
            setInternalState(getResource().getState());
            if(getSabotageResource()) {
                getResource().setState(TestResource.STATE.DAMAGED);
            }
            Throwable t = getThrowable();
            if(t != null) {
                throw t;
            }
        }

        /**
         * @return the resource
         */
        TestResource getResource()
        {
            return mResource;
        }

        /**
         * @param inResource the resource to set
         */
        void setResource(TestResource inResource)
        {
            mResource = inResource;
        }

        /**
         * @return the throwable
         */
        Throwable getThrowable()
        {
            return mThrowable;
        }

        /**
         * @param inThrowable the throwable to set
         */
        void setThrowable(Throwable inThrowable)
        {
            mThrowable = inThrowable;
        }

        /**
         * @return the internalState
         */
        TestResource.STATE getInternalState()
        {
            return mInternalState;
        }

        /**
         * @param inInternalState the internalState to set
         */
        void setInternalState(TestResource.STATE inInternalState)
        {
            mInternalState = inInternalState;
        }

        /**
         * @return the sabotageResource
         */
        boolean getSabotageResource()
        {
            return mSabotageResource;
        }

        /**
         * @param inSabotageResource the sabotageResource to set
         */
        void setSabotageResource(boolean inSabotageResource)
        {
            mSabotageResource = inSabotageResource;
        }            
    }
}
