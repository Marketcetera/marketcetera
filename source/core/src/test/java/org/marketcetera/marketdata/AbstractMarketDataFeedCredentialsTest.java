package org.marketcetera.marketdata;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.marketcetera.marketdata.Messages.NULL_URL;

import org.junit.Before;
import org.junit.Test;
import org.marketcetera.module.ExpectedFailure;

/**
 * Tests {@link AbstractMarketDataFeedCredentials}.
 * 
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 0.5.0
 */
public class AbstractMarketDataFeedCredentialsTest
    extends MarketDataFeedTestBase
{
    @Before
    public void setUp()
            throws Exception
    {
        MockMarketDataFeedCredentials.sValidateThrowsThrowable = false;
    }
    @Test
    public void testValidate()
        throws Exception
    {
        MockMarketDataFeedCredentials.sValidateThrowsThrowable = true;
        new ExpectedFailure<FeedException>(NULL_URL) {
            @Override
            protected void run()
                    throws Exception
            {
                new MockMarketDataFeedCredentials(null);            
            }
        };
        new ExpectedFailure<FeedException>(NULL_URL) {
            @Override
            protected void run()
                    throws Exception
            {
                new MockMarketDataFeedCredentials("");            
            }
        };
        new ExpectedFailure<FeedException>(NULL_URL) {
            @Override
            protected void run()
                    throws Exception
            {
                new MockMarketDataFeedCredentials("           ");            
            }
        };
        new ExpectedFailure<NullPointerException>(null) {
            @Override
            protected void run()
                    throws Exception
            {
                new MockMarketDataFeedCredentials("someURL");            
            }
        };
        MockMarketDataFeedCredentials.sValidateThrowsThrowable = false;
        String url = "http://url-" + System.nanoTime(); //$NON-NLS-1$
        MockMarketDataFeedCredentials credentials = new MockMarketDataFeedCredentials(url);
        assertEquals(url,
                     credentials.getURL());
    }
    @Test
    public void testEquals()
        throws Exception
    {
        MockMarketDataFeedCredentials c1 = new MockMarketDataFeedCredentials("url1"); //$NON-NLS-1$
        MockMarketDataFeedCredentials c2 = new MockMarketDataFeedCredentials("url2"); //$NON-NLS-1$
        MockMarketDataFeedCredentials c3 = new MockMarketDataFeedCredentials("url1"); //$NON-NLS-1$
        MockMarketDataFeedCredentials c4 = new MockMarketDataFeedCredentials("url1"); //$NON-NLS-1$
        
        assertFalse(c1.equals(null));
        assertFalse(c1.equals(this));
        
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
    }
}
