package org.marketcetera.marketdata;

import java.util.Arrays;

import junit.framework.Test;

import org.marketcetera.core.ExpectedTestFailure;
import org.marketcetera.core.publisher.ISubscriber;
import org.marketcetera.core.publisher.MockSubscriber;

/**
 * Tests {@link MarketDataFeedTokenSpec}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 0.5.0
 */
public class MarketDataFeedTokenSpecTest
        extends MarketDataFeedTestBase
{
    /**
     * Create a new MarketDataFeedTokenSpecTest instance.
     *
     * @param inArg0
     */
    public MarketDataFeedTokenSpecTest(String inArg0)
    {
        super(inArg0);
    }

    public static Test suite() 
    {
        return MarketDataFeedTestBase.suite(MarketDataFeedTokenSpecTest.class);
    }
    
    public void testGenerateTokenSpec()
        throws Exception
    {
        ISubscriber[] emptySubscribers = new ISubscriber[0];
        ISubscriber[] nonemptySubscribers = new ISubscriber[] { new MockSubscriber(), new DoNothingSubscriber() }; 
        for(int a=0;a<=1;a++) {
            for(int b=0;b<=1;b++) {
                for (int c=0;c<=2;c++) {
                    ISubscriber[] listToUse = null;
                    if(c == 1) {
                        listToUse = emptySubscribers;
                    }
                    if(c == 2) { 
                        listToUse = nonemptySubscribers;
                    }
                    doValidateTokenSpec(a==0 ? null : mCredentials,
                                        b==0 ? null : dataRequest,
                                        listToUse);
                }
            }
        }
    }
    
    private void doValidateTokenSpec(final MockMarketDataFeedCredentials inCredentials,
                                     final DataRequest inRequest,
                                     final ISubscriber[] inSubscribers)
        throws Exception
    {
        if(inCredentials == null ||
           inRequest == null) {
            new ExpectedTestFailure(NullPointerException.class) {
                protected void execute()
                        throws Throwable
                {
                    MarketDataFeedTokenSpec.generateTokenSpec(inCredentials, 
                                                              inRequest,
                                                              inSubscribers);
                }
            }.run();                             
        } else {
            MarketDataFeedTokenSpec<MockMarketDataFeedCredentials> tokenSpec = MarketDataFeedTokenSpec.generateTokenSpec(inCredentials, 
                                                                                                                         inRequest, 
                                                                                                                         inSubscribers);
            assertEquals(inCredentials,
                         tokenSpec.getCredentials());
            assertEquals(inRequest,
                         tokenSpec.getDataRequest());
            if(inSubscribers == null) {
                assertEquals(0,
                             tokenSpec.getSubscribers().length);
            } else {
                assertTrue(Arrays.equals(inSubscribers,
                                         tokenSpec.getSubscribers()));
            }
        }
    }
}
