package org.marketcetera.marketdata;

import junit.framework.Test;

import org.marketcetera.core.ExpectedTestFailure;

/**
 * Tests {@link AbstractMarketDataFeedCredentials}.
 * 
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 0.43-SNAPSHOT
 */
public class AbstractMarketDataFeedCredentialsTest
    extends MarketDataFeedTestBase
{
    /**
     * Create a new <code>AbstractMarketDataFeedCredentialsTest</code> instance.
     * 
     * @param inArg0
     */
    public AbstractMarketDataFeedCredentialsTest(String inArg0)
    {
        super(inArg0);
    }

    public static Test suite() 
    {
        return MarketDataFeedTestBase.suite(AbstractMarketDataFeedCredentialsTest.class);
    }

    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp()
            throws Exception
    {
        super.setUp();
        
        MockMarketDataFeedCredentials.sValidateThrowsThrowable = false;
    }

    public void testConstructor()
        throws Exception
    {
        // we can't say for sure that nulls are not allowed - that depends on the subclass implementation
        MockMarketDataFeedCredentials credentials = new MockMarketDataFeedCredentials(null);
        assertEquals(null,
                     credentials.getURL());
        String url = "http://url-" + System.nanoTime(); //$NON-NLS-1$
        credentials = new MockMarketDataFeedCredentials(url);
        assertEquals(url,
                     credentials.getURL());
    }
    
    public void testValidate()
        throws Exception
    {
        MockMarketDataFeedCredentials.sValidateThrowsThrowable = true;
        new ExpectedTestFailure(FeedException.class) {
            protected void execute()
                    throws Throwable
            {
                new MockMarketDataFeedCredentials(null);            }
        }.run();     
    }
    
    public void testEquals()
        throws Exception
    {
        MockMarketDataFeedCredentials c1 = new MockMarketDataFeedCredentials("url1"); //$NON-NLS-1$
        MockMarketDataFeedCredentials c2 = new MockMarketDataFeedCredentials("url2"); //$NON-NLS-1$
        MockMarketDataFeedCredentials c3 = new MockMarketDataFeedCredentials("url1"); //$NON-NLS-1$
        MockMarketDataFeedCredentials c4 = new MockMarketDataFeedCredentials("url1"); //$NON-NLS-1$
        MockMarketDataFeedCredentials c5 = new MockMarketDataFeedCredentials(null);
        
        assertFalse(c1.equals(null));
        assertFalse(c1.equals(this));
        assertFalse(c1.equals(c5));
        assertFalse(c5.equals(c1));
        
        // symmetry
        assertTrue(c1.equals(c4));
        assertTrue(c4.equals(c1));
        assertFalse(c1.equals(c2));
        assertFalse(c2.equals(c1));        
        // reflexivity
        assertTrue(c1.equals(c1));
        assertTrue(c2.equals(c2));
        assertTrue(c3.equals(c3));
        assertTrue(c4.equals(c4));
        // transivity
        assertTrue(c1.equals(c3));
        assertTrue(c3.equals(c4));
        assertTrue(c1.equals(c4));
        // consistency
        assertTrue(c1.equals(c4));
        // hashcode
        assertEquals(c1.hashCode(),
                     c3.hashCode());
        assertEquals(c1.hashCode(),
                     c4.hashCode());
        assertTrue(c1.hashCode() != c2.hashCode());
        assertTrue(c2.hashCode() != c3.hashCode());
        assertTrue(c2.hashCode() != c4.hashCode());
        assertTrue(c1.hashCode() != c5.hashCode());
        assertTrue(c5.hashCode() != c1.hashCode());
    }
}
