package org.marketcetera.marketdata;

/**
 * Test implementation for {@link AbstractMarketDataFeedCredentials}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 0.43-SNAPSHOT
 */
public class TestMarketDataFeedCredentials
    extends AbstractMarketDataFeedCredentials
{
    public static boolean sValidateThrowsThrowable = false;

    public TestMarketDataFeedCredentials() 
        throws FeedException
    {
        this("http://url-" + System.nanoTime());        
    }
    
    /**
     * Create a new <code>TestMarketDataFeedCredentials</code> instance.
     *
     * @param inURL
     * @throws FeedException
     */
    public TestMarketDataFeedCredentials(String inURL)
        throws FeedException
    {
        super(inURL);
    }

    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.AbstractMarketDataFeedCredentials#validateURL(java.lang.String)
     */
    @Override
    protected void validateURL(String inURL)
            throws FeedException
    {
        super.validateURL(inURL);
        if(sValidateThrowsThrowable) {
            throw new NullPointerException("This exception is expected");
        }
    }
}
