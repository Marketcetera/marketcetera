package org.marketcetera.core.resourcepool;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.marketcetera.core.ExpectedTestFailure;
import org.marketcetera.core.MarketceteraTestSuite;
import org.marketcetera.core.Messages;
import org.marketcetera.util.log.I18NBoundMessage0P;
import org.marketcetera.util.log.I18NLoggerProxy;
import org.marketcetera.util.log.I18NMessage0P;
import org.marketcetera.util.log.I18NMessageProvider;
import org.marketcetera.util.log.SLF4JLoggerProxy;

public class ResourcePoolTest
        extends TestCase
{
    /*
     * I18N messaging
     */
    static final I18NMessageProvider PROVIDER =
        new I18NMessageProvider("core"); //$NON-NLS-1$
    static final I18NLoggerProxy LOGGER = 
        new I18NLoggerProxy(PROVIDER);
    static final I18NMessage0P I18N_EXPECTED_EXCEPTION_MSG = 
        new I18NMessage0P(LOGGER, "i18n_expected_exception_msg", "this exception is expected"); //$NON-NLS-1$ //$NON-NLS-2$

    private MockResourcePool mTestPool;
    
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
        mTestPool = new MockResourcePool();
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
        MockResource r = mTestPool.requestResource(null);
        assertNotNull(r);
        mTestPool.returnResource(r);
        assertTrue(mTestPool.getPoolIterator().hasNext());
        assertEquals(MockResource.STATE.RETURNED,
                     r.getState());
        doShutdownTest();
        assertEquals(MockResource.STATE.SHUTDOWN,
                     r.getState());
    }
    
    public void testShutdownThrowsDuringStop()
        throws Exception
    {
        MockResource r = mTestPool.requestResource(null);
        assertNotNull(r);
        mTestPool.returnResource(r);
        r.setThrowDuringStop(true);
        doShutdownTest();
        assertEquals(MockResource.STATE.SHUTDOWN,
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

        mTestPool.setThrowDuringGetNextResource(new ResourcePoolException(new I18NBoundMessage0P(I18N_EXPECTED_EXCEPTION_MSG)));
        new ExpectedTestFailure(ResourcePoolException.class) {
            protected void execute()
                    throws Throwable
            {
                doRequestTest();
            }            
        }.run();

        mTestPool.setThrowDuringGetNextResource(new NullPointerException("this exception is expected")); //$NON-NLS-1$
        new ExpectedTestFailure(ResourcePoolException.class) {
            protected void execute()
                    throws Throwable
            {
                doRequestTest();
            }            
        }.run();
        mTestPool.setThrowDuringGetNextResource(null);
        
        try {
            MockResource.setAllocateException(new NullPointerException("This exception is expected")); //$NON-NLS-1$
            doRequestTest();
        } finally {
            MockResource.setAllocateException(null);
        }

        mTestPool.setThrowDuringGetNextResource(null);
        mTestPool.shutdown();
        assertTrue(mTestPool.rejectNewRequests());
        new ExpectedTestFailure(ResourcePoolShuttingDownException.class,
                                Messages.ERROR_RESOURCE_POOL_SHUTTING_DOWN.getText()) {
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
        MockResource r1 =  mTestPool.requestResource(null);
        assertNotNull(r1);
        MockResource r2 =  mTestPool.requestResource(this);
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
                                SLF4JLoggerProxy.debug(this, "{} {} {} {} {} {}", value6, value1, value2, value3, value4, value5); //$NON-NLS-1$
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
                                Messages.ERROR_RESOURCE_POOL_SHUTTING_DOWN.getText()) {
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
        MockResource r1 = mTestPool.requestResource(null);
        assertNotNull(r1);
        assertEquals(MockResource.STATE.ALLOCATED,
                     r1.getState());
        assertFalse(mTestPool.getPoolIterator().hasNext());
        r1.setIsFunctionalException(inIsFunctionalThrows);
        r1.setReleaseException(inReleaseThrows);
        r1.setState(inResourceFunctional ? r1.getState() : MockResource.STATE.DAMAGED);
        r1.setReturnException(inReturnedThrows);
        mTestPool.setThrowDuringVerify(inVerify);
        mTestPool.setThrowDuringAddToPool(inAddToPoolThrows);

        mTestPool.returnResource(r1);

        if(inResourceFunctional) {
            if(inIsFunctionalThrows) {
                assertFalse(mTestPool.getPoolIterator().hasNext());
                assertEquals(MockResource.STATE.RELEASED,
                             r1.getState());
            } else {
                if(inVerify) {
                    assertFalse(mTestPool.getPoolIterator().hasNext());
                    assertEquals(MockResource.STATE.RELEASED,
                                 r1.getState());
                } else {
                    if(inAddToPoolThrows != null &&
                       inAddToPoolThrows.equals(new Boolean(false))) {
                        assertTrue(mTestPool.getPoolIterator().hasNext());
                        assertEquals(MockResource.STATE.RETURNED,
                                     r1.getState());
                    }
                }
            }
        } else {
            assertFalse(mTestPool.getPoolIterator().hasNext());
            assertEquals(MockResource.STATE.RELEASED,
                         r1.getState());
        }
        
        mTestPool.emptyPool();
    }
    
    public void testReleaseResource()
        throws Exception
    {
        MockResource r1 = mTestPool.requestResource(null);
        assertNotNull(r1);
        r1.setReleaseException(false);
        assertEquals(MockResource.STATE.ALLOCATED,
                     r1.getState());
        mTestPool.releaseResource(r1);
        assertEquals(MockResource.STATE.RELEASED,
                     r1.getState());
        final MockResource r2 = mTestPool.requestResource(null);
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
        
        mTestPool.setThrowDuringGetNextResource(new ResourcePoolException(new I18NBoundMessage0P(I18N_EXPECTED_EXCEPTION_MSG)));
        new ExpectedTestFailure(ResourcePoolException.class,
                                Messages.ERROR_RESOURCE_POOL_COULD_NOT_ALLOCATE_NEW_RESOURCE.getText()) {
            protected void execute()
                throws Throwable
            {
                doExecuteTest(new TestExecutable(), 
                              true);
            }            
        }.run();

        mTestPool.setThrowDuringGetNextResource(new NullPointerException("This exception is expected")); //$NON-NLS-1$
        new ExpectedTestFailure(ResourcePoolException.class,
                                Messages.ERROR_RESOURCE_POOL_COULD_NOT_ALLOCATE_NEW_RESOURCE.getText()) {
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
        block.setThrowable(new NullPointerException("This exception is expected")); //$NON-NLS-1$
        new ExpectedTestFailure(ResourcePoolException.class,
                                Messages.ERROR_RESOURCE_POOL_EXECUTABLE_BLOCK_ERROR.getText()) {
            protected void execute()
                throws Throwable
            {
                doExecuteTest(block, 
                              true);
            }            
        }.run();
        
        doExecuteTest(new TestExecutable(),
                      false);
        
        mTestPool.setThrowDuringReturnResource(new ResourcePoolException(new I18NBoundMessage0P(I18N_EXPECTED_EXCEPTION_MSG)));
        new ExpectedTestFailure(ResourcePoolException.class) {
            protected void execute()
                throws Throwable
            {
                doExecuteTest(new TestExecutable(),
                              false);
            }            
        }.run();
        
        mTestPool.setThrowDuringReturnResource(new NullPointerException("This exception is expected")); //$NON-NLS-1$
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
        assertEquals(inResourceFunctional ? MockResource.STATE.RETURNED : MockResource.STATE.RELEASED,
                     inBlock.getResource().getState());
        assertEquals(MockResource.STATE.ALLOCATED,
                     inBlock.getInternalState());

        inBlock.setResource(null);
        inBlock.setSabotageResource(!inResourceFunctional);

        mTestPool.execute(inBlock,
                          this);        

        assertNotNull(inBlock.getResource());
        assertEquals(inResourceFunctional ? MockResource.STATE.RETURNED : MockResource.STATE.RELEASED,
                     inBlock.getResource().getState());
        assertEquals(MockResource.STATE.ALLOCATED,
                     inBlock.getInternalState());
    }
    
    static class TestExecutable
        implements ExecutableBlock
    {
        private MockResource mResource;
        private Throwable mThrowable;
        private MockResource.STATE mInternalState;
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
        
        public Object execute(Resource inResource)
                throws Throwable
        {
            setResource((MockResource)inResource);
            setInternalState(getResource().getState());
            if(getSabotageResource()) {
                getResource().setState(MockResource.STATE.DAMAGED);
            }
            Throwable t = getThrowable();
            if(t != null) {
                throw t;
            }
            return inResource;
        }

        /**
         * @return the resource
         */
        MockResource getResource()
        {
            return mResource;
        }

        /**
         * @param inResource the resource to set
         */
        void setResource(MockResource inResource)
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
        MockResource.STATE getInternalState()
        {
            return mInternalState;
        }

        /**
         * @param inInternalState the internalState to set
         */
        void setInternalState(MockResource.STATE inInternalState)
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
