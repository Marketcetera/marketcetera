package org.marketcetera.core.resourcepool;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.marketcetera.core.ExpectedTestFailure;
import org.marketcetera.core.LoggerConfiguration;
import org.marketcetera.core.Messages;
import org.marketcetera.util.log.*;

/* $License$ */

/**
 * Tests {@link ResourcePool}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class ResourcePoolTest
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
     * Runs before each test.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Before
    public void setUp()
            throws Exception
    {
        testPool = new MockResourcePool();
    }
    /**
     * Tests {@link ResourcePool#ResourcePool()}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testConstructor()
            throws Exception
    {
        assertEquals(ResourcePool.Status.READY,
                     testPool.getStatus());
    }
    /**
     * Tests shutdown of an empty pool.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testShutdownEmpty()
            throws Exception
    {
        assertFalse(testPool.getPoolIterator().hasNext());
        doShutdownTest();
    }
    /**
     * Tests shutdown of a populated pool.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testShutdownPopulated()
            throws Exception
    {
        assertFalse(testPool.getPoolIterator().hasNext());
        MockResource r = testPool.requestResource(null);
        assertNotNull(r);
        testPool.returnResource(r);
        assertTrue(testPool.getPoolIterator().hasNext());
        assertEquals(MockResource.State.RETURNED,
                     r.getState());
        doShutdownTest();
        assertEquals(MockResource.State.SHUTDOWN,
                     r.getState());
    }
    /**
     * Tests shutdown of a pool during pool stop.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testShutdownThrowsDuringStop()
            throws Exception
    {
        MockResource r = testPool.requestResource(null);
        assertNotNull(r);
        testPool.returnResource(r);
        r.setThrowDuringStop(true);
        doShutdownTest();
        assertEquals(MockResource.State.SHUTDOWN,
                     r.getState());
    }
    /**
     * Tests requesting a resource.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testRequestResource()
            throws Exception
    {
        assertFalse(testPool.rejectNewRequests());
        assertNull(testPool.getThrowDuringGetNextResource());
        doRequestTest();
        testPool.setThrowDuringGetNextResource(new ResourcePoolException(new I18NBoundMessage0P(I18N_EXPECTED_EXCEPTION_MSG)));
        new ExpectedTestFailure(ResourcePoolException.class) {
            protected void execute()
                    throws Exception
            {
                doRequestTest();
            }
        }.run();
        testPool.setThrowDuringGetNextResource(new NullPointerException("this exception is expected"));
        new ExpectedTestFailure(ResourcePoolException.class) {
            protected void execute()
                    throws Exception
            {
                doRequestTest();
            }
        }.run();
        testPool.setThrowDuringGetNextResource(null);
        try {
            MockResource.setAllocateException(new NullPointerException("This exception is expected"));
            doRequestTest();
        } finally {
            MockResource.setAllocateException(null);
        }
        testPool.setThrowDuringGetNextResource(null);
        testPool.shutdown();
        assertTrue(testPool.rejectNewRequests());
        new ExpectedTestFailure(ResourcePoolShuttingDownException.class,
                                Messages.ERROR_RESOURCE_POOL_SHUTTING_DOWN.getText()) {
            protected void execute()
                    throws Exception
            {
                doRequestTest();
            }
        }.run();
    }
    /**
     * Tests returning a resource from the pool.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
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
                                SLF4JLoggerProxy.debug(this, "{} {} {} {} {} {}", value6, value1, value2, value3, value4, value5);
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
        assertEquals(ResourcePool.Status.READY,
                     testPool.getStatus());
        testPool.shutdown();
        assertEquals(ResourcePool.Status.SHUT_DOWN,
                     testPool.getStatus());
        assertTrue(testPool.rejectNewRequests());
        new ExpectedTestFailure(ResourcePoolShuttingDownException.class,
                                Messages.ERROR_RESOURCE_POOL_SHUTTING_DOWN.getText()) {
            protected void execute()
                throws Exception
            {
                doReturnTest(true,
                             false,
                             false, 
                             false, 
                             false, 
                             false);
            }}.run();
    }
    /**
     * Tests releasing resources.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testReleaseResource()
            throws Exception
    {
        MockResource r1 = testPool.requestResource(null);
        assertNotNull(r1);
        r1.setReleaseException(false);
        assertEquals(MockResource.State.ALLOCATED,
                     r1.getState());
        testPool.releaseResource(r1);
        assertEquals(MockResource.State.RELEASED,
                     r1.getState());
        final MockResource r2 = testPool.requestResource(null);
        assertNotNull(r2);
        r2.setReleaseException(true);
        new ExpectedTestFailure(ReleasedResourceException.class) {
            protected void execute()
                throws Exception
            {
                testPool.releaseResource(r2);
            }}.run();
    }
    /**
     * Tests executing a request with a resource.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testExecute()
            throws Exception
    {
        doExecuteTest(new MockExecutable(), 
                      true);
        testPool.setThrowDuringGetNextResource(new ResourcePoolException(new I18NBoundMessage0P(I18N_EXPECTED_EXCEPTION_MSG)));
        new ExpectedTestFailure(ResourcePoolException.class,
                                Messages.ERROR_RESOURCE_POOL_COULD_NOT_ALLOCATE_NEW_RESOURCE.getText()) {
            protected void execute()
                    throws Exception
            {
                doExecuteTest(new MockExecutable(), 
                              true);
            }
        }.run();
        testPool.setThrowDuringGetNextResource(new NullPointerException("This exception is expected"));
        new ExpectedTestFailure(ResourcePoolException.class,
                                Messages.ERROR_RESOURCE_POOL_COULD_NOT_ALLOCATE_NEW_RESOURCE.getText()) {
            protected void execute()
                    throws Exception
            {
                doExecuteTest(new MockExecutable(), 
                              true);
            }
        }.run();
        testPool.setThrowDuringGetNextResource(null);
        new ExpectedTestFailure(NullPointerException.class) {
            protected void execute()
                    throws Exception
            {
                doExecuteTest(null, 
                              true);
            }
        }.run();
        final MockExecutable block = new MockExecutable();
        block.setException(new NullPointerException("This exception is expected"));
        new ExpectedTestFailure(ResourcePoolException.class,
                                Messages.ERROR_RESOURCE_POOL_EXECUTABLE_BLOCK_ERROR.getText()) {
            protected void execute()
                    throws Exception
            {
                doExecuteTest(block, 
                              true);
            }
        }.run();
        doExecuteTest(new MockExecutable(),
                      false);
        testPool.setThrowDuringReturnResource(new ResourcePoolException(new I18NBoundMessage0P(I18N_EXPECTED_EXCEPTION_MSG)));
        new ExpectedTestFailure(ResourcePoolException.class) {
            protected void execute()
                    throws Exception
            {
                doExecuteTest(new MockExecutable(),
                              false);
            }
        }.run();
        testPool.setThrowDuringReturnResource(new NullPointerException("This exception is expected"));
        new ExpectedTestFailure(ResourcePoolException.class) {
            protected void execute()
                    throws Exception
            {
                doExecuteTest(new MockExecutable(),
                              false);
            }
        }.run();
    }
    /**
     * Executes a single resource request test.
     *
     * @throws Exception if an unexpected error occurs
     */
    protected void doRequestTest()
            throws Exception
    {
        MockResource r1 =  testPool.requestResource(null);
        assertNotNull(r1);
        MockResource r2 =  testPool.requestResource(this);
        assertNotNull(r2);
    }
    /**
     * Executes a single shutdown test.
     *
     * @throws Exception if an unexpected error occurs
     */
    protected void doShutdownTest()
            throws Exception
    {
        assertFalse(testPool.rejectNewRequests());
        testPool.shutdown();
        assertTrue(testPool.rejectNewRequests());
        assertEquals(ResourcePool.Status.SHUT_DOWN,
                     testPool.getStatus());
        testPool.shutdown();
    }
    /**
     * Get the testPool value.
     *
     * @return a <code>MockResourcePool</code> value
     */
    protected MockResourcePool getTestPool()
    {
        return testPool;
    }
    /**
     * Executes a single return of resource test.
     *
     * @param inResourceFunctional a <code>boolean</code> value
     * @param inIsFunctionalThrows a <code>boolean</code> value
     * @param inReleaseThrows a <code>boolean</code> value
     * @param inReturnedThrows a <code>boolean</code> value
     * @param inVerify a <code>boolean</code> value
     * @param inAddToPoolThrows a <code>Boolean</code> value
     * @throws Exception if an unexpected error occurs
     */
    protected void doReturnTest(boolean inResourceFunctional, 
                                boolean inIsFunctionalThrows, 
                                boolean inReleaseThrows, 
                                boolean inReturnedThrows, 
                                boolean inVerify, 
                                Boolean inAddToPoolThrows)
            throws Exception
    {
        assertFalse(testPool.getPoolIterator().hasNext());
        MockResource r1 = testPool.requestResource(null);
        assertNotNull(r1);
        assertEquals(MockResource.State.ALLOCATED,
                     r1.getState());
        assertFalse(testPool.getPoolIterator().hasNext());
        r1.setIsFunctionalException(inIsFunctionalThrows);
        r1.setReleaseException(inReleaseThrows);
        r1.setState(inResourceFunctional ? r1.getState() : MockResource.State.DAMAGED);
        r1.setReturnException(inReturnedThrows);
        testPool.setThrowDuringVerify(inVerify);
        testPool.setThrowDuringAddToPool(inAddToPoolThrows);
        testPool.returnResource(r1);
        if(inResourceFunctional) {
            if(inIsFunctionalThrows) {
                assertFalse(testPool.getPoolIterator().hasNext());
                assertEquals(MockResource.State.RELEASED,
                             r1.getState());
            } else {
                if(inVerify) {
                    assertFalse(testPool.getPoolIterator().hasNext());
                    assertEquals(MockResource.State.RELEASED,
                                 r1.getState());
                } else {
                    if(inAddToPoolThrows != null && !inAddToPoolThrows) {
                        assertTrue(testPool.getPoolIterator().hasNext());
                        assertEquals(MockResource.State.RETURNED,
                                     r1.getState());
                    }
                }
            }
        } else {
            assertFalse(testPool.getPoolIterator().hasNext());
            assertEquals(MockResource.State.RELEASED,
                         r1.getState());
        }
        testPool.emptyPool();
    }
    /**
     * Executes a single execute test.
     *
     * @param inBlock a <code>TestExecutable</code> value
     * @param inResourceFunctional a <code>boolean</code> value
     * @throws Exception if an unexpected error occurs
     */
    protected void doExecuteTest(MockExecutable inBlock, 
                                 boolean inResourceFunctional)
            throws Exception
    {
        assertNull(inBlock.getResource());
        inBlock.setSabotageResource(!inResourceFunctional);
        testPool.execute(inBlock);
        assertNotNull(inBlock.getResource());
        assertEquals(inResourceFunctional ? MockResource.State.RETURNED : MockResource.State.RELEASED,
                     inBlock.getResource().getState());
        assertEquals(MockResource.State.ALLOCATED,
                     inBlock.getInternalState());
        inBlock.setResource(null);
        inBlock.setSabotageResource(!inResourceFunctional);
        testPool.execute(inBlock,
                          this);
        assertNotNull(inBlock.getResource());
        assertEquals(inResourceFunctional ? MockResource.State.RETURNED : MockResource.State.RELEASED,
                     inBlock.getResource().getState());
        assertEquals(MockResource.State.ALLOCATED,
                     inBlock.getInternalState());
    }
    /**
     * Provides a mock {@link ExecutableBlock} implementation for testing.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    protected static class MockExecutable
            implements ExecutableBlock<MockResource,MockResource>
    {
        /* (non-Javadoc)
         * @see org.marketcetera.core.resourcepool.ExecutableBlock#execute(org.marketcetera.core.resourcepool.Resource)
         */
        @Override
        public MockResource execute(MockResource inResource)
                throws Exception
        {
            setResource((MockResource)inResource);
            setInternalState(getResource().getState());
            if(getSabotageResource()) {
                getResource().setState(MockResource.State.DAMAGED);
            }
            Exception e = getException();
            if(e != null) {
                throw e;
            }
            return inResource;
        }
        /**
         * Create a new MockExecutable instance.
         */
        protected MockExecutable()
        {
            this(null);
        }
        /**
         * Create a new MockExecutable instance.
         *
         * @param inException an <code>Exception</code> value to throw during execution
         */
        protected MockExecutable(Exception inException)
        {
            setResource(null);
            setException(inException);
            setInternalState(null);
            setSabotageResource(false);
        }
        /**
         * Gets the resource value.
         *
         * @return a <code>MockResource</code> value
         */
        protected MockResource getResource()
        {
            return resource;
        }
        /**
         * Sets the resource value.
         *
         * @param inResource a <code>MockResource</code> value
         */
        protected void setResource(MockResource inResource)
        {
            resource = inResource;
        }
        /**
         * Gets the exception value.
         *
         * @return an <code>Exception</code> value
         */
        protected Exception getException()
        {
            return exception;
        }
        /**
         * Sets the exception value.
         *
         * @param inException an <code>Exception</code> value
         */
        protected void setException(Exception inException)
        {
            exception = inException;
        }
        /**
         * Gets the internal state of the resource.
         *
         * @return a <code>MockResource.State</code> value
         */
        protected MockResource.State getInternalState()
        {
            return internalState;
        }
        /**
         * Sets the internal state of the resource.
         *
         * @param inInternalState a <code>MockResource.State</code> value
         */
        protected void setInternalState(MockResource.State inInternalState)
        {
            internalState = inInternalState;
        }
        /**
         * Gets the sabotage resource value.
         *
         * @return a <code>boolean</code> value
         */
        protected boolean getSabotageResource()
        {
            return sabotageResource;
        }
        /**
         * Sets the sabotage resource value.
         *
         * @param inSabotageResource a <code>boolean</code> value
         */
        protected void setSabotageResource(boolean inSabotageResource)
        {
            sabotageResource = inSabotageResource;
        }
        /**
         * test resource value
         */
        private MockResource resource;
        /**
         * exception to throw, may be <code>null</code>
         */
        private Exception exception;
        /**
         * current state of the resource
         */
        private MockResource.State internalState;
        /**
         * indicates whether to sabotage the resource during testing
         */
        private boolean sabotageResource;
    }
    /**
     * test message provider
     */
    protected static final I18NMessageProvider PROVIDER = new I18NMessageProvider("core");
    /**
     * test message logger
     */
    protected static final I18NLoggerProxy LOGGER = new I18NLoggerProxy(PROVIDER);
    /**
     * test message
     */
    protected static final I18NMessage0P I18N_EXPECTED_EXCEPTION_MSG = new I18NMessage0P(LOGGER, "i18n_expected_exception_msg", "this exception is expected");
    /**
     * test resource pool
     */
    private MockResourcePool testPool;
}
