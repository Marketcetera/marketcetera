package org.marketcetera.marketdata;

/**
 * Test implementation for {@link MarketDataFeedCredentials}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: $
 * @since 0.43-SNAPSHOT
 */
public class TestMarketDataFeedCredentials
    extends MarketDataFeedCredentials
{
    public static boolean sValidateThrowsThrowable = false;

    public TestMarketDataFeedCredentials() 
        throws FeedException
    {
        this("http://url-" + System.nanoTime(),
             "username-" + System.nanoTime(),
             "password-" + System.nanoTime());
        
    }
    
    /**
     * Create a new <code>TestMarketDataFeedCredentials</code> instance.
     *
     * @param inURL
     * @param inUsername
     * @param inPassword
     * @throws FeedException
     */
    public TestMarketDataFeedCredentials(String inURL,
                                         String inUsername,
                                         String inPassword)
        throws FeedException
    {
        super(inURL,
              inUsername,
              inPassword);

    }

    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.MarketDataFeedCredentials#validateURL(java.lang.String)
     */
    @Override
    protected void validateURL(String inURL)
            throws FeedException
    {
        if(sValidateThrowsThrowable) {
            throw new NullPointerException("This exception is expected");
        }
    }
}
