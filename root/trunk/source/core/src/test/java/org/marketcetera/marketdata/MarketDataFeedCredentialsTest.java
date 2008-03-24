package org.marketcetera.marketdata;

import junit.framework.Test;

import org.marketcetera.core.ExpectedTestFailure;

/**
 * Tests {@link MarketDataFeedCredentials}.
 * 
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: $
 * @since 0.43-SNAPSHOT
 */
public class MarketDataFeedCredentialsTest
    extends MarketDataFeedTestBase
{
    /**
     * Create a new <code>MarketDataFeedCredentialsTest</code> instance.
     * 
     * @param inArg0
     */
    public MarketDataFeedCredentialsTest(String inArg0)
    {
        super(inArg0);
    }

    public static Test suite() 
    {
        return MarketDataFeedTestBase.suite(MarketDataFeedBaseTest.class);
    }

    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp()
            throws Exception
    {
        super.setUp();
        
        TestMarketDataFeedCredentials.sValidateThrowsThrowable = false;
    }

    public void testConstructor()
        throws Exception
    {
        // we can't say for sure that nulls are not allowed - that depends on the subclass implementation
        String url = "http://url-" + System.nanoTime();
        String username = "username-" + System.nanoTime();
        String password = "password-" + System.nanoTime();
        for(int a=0;a<=1;a++) {
            for(int b=0;b<=1;b++) {
                for(int c=0;c<=1;c++) {
                    String myURL = a==0 ? null : url;
                    String myUsername = b==0 ? null : username;
                    String myPassword = c==0 ? null : password;
                    TestMarketDataFeedCredentials credentials = doConstructorTest(myURL,
                                                                                  myUsername,
                                                                                  myPassword); 
                    assertNotNull(credentials);
                    assertEquals(myURL,
                                 credentials.getURL());
                    assertEquals(myUsername,
                                 credentials.getUsername());
                    assertEquals(myPassword,
                                 credentials.getPassword());
                }
            }
        }
    }
    
    public void testValidate()
        throws Exception
    {
        TestMarketDataFeedCredentials.sValidateThrowsThrowable = true;
        new ExpectedTestFailure(FeedException.class) {
            protected void execute()
                    throws Throwable
            {
                new TestMarketDataFeedCredentials(null,
                                                  null,
                                                  null);            }
        }.run();     
    }
    
    private TestMarketDataFeedCredentials doConstructorTest(String inURL,
                                                            String inUsername,
                                                            String inPassword)
        throws Exception
    {
        return new TestMarketDataFeedCredentials(inURL,
                                                 inUsername,
                                                 inPassword);
    }
}
