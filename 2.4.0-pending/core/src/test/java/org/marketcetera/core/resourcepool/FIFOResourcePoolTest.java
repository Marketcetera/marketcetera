package org.marketcetera.core.resourcepool;

import java.util.Iterator;
import java.util.NoSuchElementException;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.marketcetera.core.ExpectedTestFailure;
import org.marketcetera.core.MarketceteraTestSuite;
import org.marketcetera.core.Messages;

/**
 * Tests {@link FIFOResourcePool}. 
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 */
public class FIFOResourcePoolTest 
	extends TestCase 
{
    protected MockFIFOResourcePool mTestPool;
    protected MockResource mResource1;
    protected MockResource mResource2;
    
	public FIFOResourcePoolTest(String inName) 
	{
		super(inName);
	}
	
    public static Test suite() 
    {
        TestSuite suite = new MarketceteraTestSuite(FIFOResourcePoolTest.class);
//        suite.addTest(new FIFOResourcePoolTest("testSerialResourceCheckOut"));
        return suite;
    }
    
    protected void setUp()
            throws Exception
    {
        super.setUp();
        
        mTestPool = new MockFIFOResourcePool();
        MockResource.setInitializeException(null);
        MockResource.setAllocateException(null);
        mResource1 = new MockResource();
        mResource2 = new MockResource();
    }

    public void testConstructor()
        throws Exception
    {
        MockFIFOResourcePool pool = new MockFIFOResourcePool();
        assertEquals(0,
                     pool.getPoolSize());
        assertFalse(pool.poolContains(new MockResource()));
    }
    
    public void testPoolIterator()
        throws Exception
    {
        assertFalse(mTestPool.getPoolIterator().hasNext());
        mTestPool.addResourceToPool(mResource1);
        assertTrue(mTestPool.getPoolIterator().hasNext());
        assertEquals(mResource1,
                     mTestPool.getPoolIterator().next());
        assertEquals(1,
                     mTestPool.getPoolSize());
    }
    
    public void testAddResourcesToPool()
        throws Exception
    {
        new ExpectedTestFailure(NullPointerException.class) {
            protected void execute()
                    throws Throwable
            {
                mTestPool.addResourceToPool(null);
            }
        }.run();
        
        assertEquals(0,
                     mTestPool.getPoolSize());
        
        mTestPool.addResourceToPool(mResource1);
        
        assertEquals(1,
                     mTestPool.getPoolSize());
        assertTrue(mTestPool.poolContains(mResource1));
        assertEquals(mResource1,
                     mTestPool.getPoolIterator().next());
        
        mTestPool.addResourceToPool(mResource2);

        assertEquals(2,
                     mTestPool.getPoolSize());
        assertTrue(mTestPool.poolContains(mResource1));
        assertTrue(mTestPool.poolContains(mResource2));
        Iterator<Resource> iterator = mTestPool.getPoolIterator();
        assertEquals(mResource1,
                     iterator.next());
        assertEquals(mResource2,
                     iterator.next());        
    }
    
    public void testAllocateNextResource()
        throws Exception
    {
        // request on empty pool should throw NoSuchElementException
        assertEquals(0,
                     mTestPool.getPoolSize());
        new ExpectedTestFailure(NoSuchElementException.class) {
            protected void execute()
                    throws Throwable
            {
                mTestPool.allocateNextResource(null);
            }
        }.run();
        
        // add a resource
        mTestPool.addResourceToPool(mResource1);
        assertEquals(1,
                     mTestPool.getPoolSize());
        assertTrue(mTestPool.poolContains(mResource1));
        
        // get it back
        MockResource r = (MockResource) mTestPool.allocateNextResource(null);
        assertEquals(mResource1,
                     r);
        assertEquals(0,
                     mTestPool.getPoolSize());
        assertFalse(mTestPool.poolContains(mResource1));
        
        // add another resource
        mTestPool.addResourceToPool(mResource2);
        // the parameter to allocate doesn't do anything
        r = (MockResource) mTestPool.allocateNextResource(this);
        assertEquals(mResource2,
                     r);
        assertEquals(0,
                     mTestPool.getPoolSize());
        assertFalse(mTestPool.poolContains(mResource2));        
    }
    
    public void testGetNextResource()
        throws Exception
    {
        assertEquals(0,
                     mTestPool.getPoolSize());
        MockResource r = (MockResource) mTestPool.getNextResource(null);
        assertNotNull(r);
        assertFalse(r.equals(mResource1));
        assertFalse(r.equals(mResource2));
        assertEquals(0,
                     mTestPool.getPoolSize());
        
        mTestPool.returnResource(r);
        assertEquals(1,
                     mTestPool.getPoolSize());
        assertEquals(r,
                     mTestPool.getPoolIterator().next());
        
        MockResource newR = (MockResource) mTestPool.getNextResource(this);
        assertEquals(0,
                     mTestPool.getPoolSize());
        assertEquals(r,
                     newR);
        // test exception handling
        mTestPool.setThrowDuringCreateResource(true);
        new ExpectedTestFailure(ResourcePoolException.class,
                                Messages.ERROR_CANNOT_CREATE_RESOURCE_FOR_POOL.getText()) {
            protected void execute()
                    throws Throwable
            {
                mTestPool.getNextResource(null);
            }
        }.run();
        mTestPool.setThrowDuringCreateResource(false);
        
        mTestPool.setThrowDuringAddResource(true);
        new ExpectedTestFailure(ResourcePoolException.class,
                                Messages.ERROR_CANNOT_CREATE_RESOURCE_FOR_POOL.getText()) {
            protected void execute()
                    throws Throwable
            {
                mTestPool.getNextResource(null);
            }
        }.run();
        mTestPool.setThrowDuringAddResource(false);
        
        assertEquals(0,
                     mTestPool.getPoolSize());
        mTestPool.setThrowDuringAllocateResource(true);
        new ExpectedTestFailure(ResourcePoolException.class,
                                Messages.ERROR_CANNOT_CREATE_RESOURCE_FOR_POOL.getText()) {
            protected void execute()
                    throws Throwable
            {
                mTestPool.getNextResource(null);
            }
        }.run();
        mTestPool.setThrowDuringAllocateResource(false);
        assertEquals(1,
                     mTestPool.getPoolSize());
        assertNotNull(mTestPool.getNextResource(null));
        
        assertEquals(0,
                     mTestPool.getPoolSize());
        mTestPool.setEmptyPoolBeforeAllocation(true);
        new ExpectedTestFailure(ResourcePoolException.class,
                                Messages.ERROR_CANNOT_CREATE_RESOURCE_FOR_POOL.getText()) {
            protected void execute()
                    throws Throwable
            {
                mTestPool.getNextResource(null);
            }
        }.run();
        assertEquals(0,
                     mTestPool.getPoolSize());
        mTestPool.setEmptyPoolBeforeAllocation(false);
    }
    
    public void testVerifyResourceReturn()
        throws Exception
    {
        assertEquals(0,
                     mTestPool.getPoolSize());
        new ExpectedTestFailure(NullPointerException.class) {
            protected void execute()
                    throws Throwable
            {
                mTestPool.verifyResourceReturn(null);
            }
        }.run();
        
        assertEquals(0,
                     mTestPool.getPoolSize());
        assertFalse(mTestPool.poolContains(mResource1));
        mTestPool.verifyResourceReturn(mResource1);
        
        mTestPool.addResourceToPool(mResource1);
        assertEquals(1,
                     mTestPool.getPoolSize());
        assertTrue(mTestPool.poolContains(mResource1));

        mTestPool.verifyResourceReturn(mResource2);
        new ExpectedTestFailure(DuplicateResourceReturnException.class,
                                Messages.ERROR_RESOURCE_POOL_RESOURCE_ALREADY_RETURNED.getText()) {
            protected void execute()
                    throws Throwable
            {
                mTestPool.verifyResourceReturn(mResource1);
            }
        }.run();
        
        mTestPool.setThrowDuringPoolContains(true);
        new ExpectedTestFailure(ResourcePoolException.class) {
            protected void execute()
                    throws Throwable
            {
                mTestPool.verifyResourceReturn(mResource1);
            }
        }.run();
        mTestPool.setThrowDuringPoolContains(false);
    }
    
    public void testResourceOrder()
        throws Exception
    {
        MockResource r3 = new MockResource();
        assertEquals(0,
                     mTestPool.getPoolSize());
        mTestPool.addResourceToPool(mResource2);
        mTestPool.addResourceToPool(r3);
        mTestPool.addResourceToPool(mResource1);
        assertEquals(3,
                     mTestPool.getPoolSize());
        // pool contains (in FIFO) 2,3,1
        assertEquals(mResource2,
                     mTestPool.getNextResource(null));
        // pool contains (in FIFO) 3,1
        assertEquals(2,
                     mTestPool.getPoolSize());
        assertEquals(r3,
                     mTestPool.getNextResource(null));
        // pool contains (in FIFO) 1
        assertEquals(1,
                     mTestPool.getPoolSize());
        mTestPool.addResourceToPool(mResource2);
        // pool contains (in FIFO) 1,2
        assertEquals(2,
                     mTestPool.getPoolSize());
        mTestPool.addResourceToPool(r3);
        // pool contains (in FIFO) 1,2,3
        assertEquals(mResource1,
                     mTestPool.getNextResource(null));
        // pool contains (in FIFO) 2,3
        assertEquals(mResource2,
                     mTestPool.getNextResource(null));
        // pool contains (in FIFO) 3
        assertEquals(r3,
                     mTestPool.getNextResource(null));
        // pool empty
        assertEquals(0,
                     mTestPool.getPoolSize());
    }
}
