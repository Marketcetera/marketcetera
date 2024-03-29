package org.marketcetera.marketdata;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Test;
import org.marketcetera.core.publisher.Subscriber;
import org.marketcetera.core.publisher.MockSubscriber;
import org.marketcetera.module.ExpectedFailure;

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
    @Test
    public void testGenerateTokenSpec()
        throws Exception
    {
        Subscriber[] emptySubscribers = new Subscriber[0];
        Subscriber[] nonemptySubscribers = new Subscriber[] { new MockSubscriber(), new DoNothingSubscriber() }; 
        for(int b=0;b<=1;b++) {
            for (int c=0;c<=2;c++) {
                Subscriber[] listToUse = null;
                if(c == 1) {
                    listToUse = emptySubscribers;
                }
                if(c == 2) { 
                    listToUse = nonemptySubscribers;
                }
                doValidateTokenSpec(b==0 ? null : dataRequest,
                                    listToUse);
            }
        }
    }
    
    private void doValidateTokenSpec(final MarketDataRequest inRequest,
                                     final Subscriber[] inSubscribers)
        throws Exception
    {
        if(inRequest == null) {
            new ExpectedFailure<NullPointerException>() {
                protected void run()
                        throws Exception
                {
                    MarketDataFeedTokenSpec.generateTokenSpec(inRequest,
                                                              inSubscribers);
                }
            };
        } else {
            MarketDataFeedTokenSpec tokenSpec = MarketDataFeedTokenSpec.generateTokenSpec(inRequest, 
                                                                                          inSubscribers);
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
