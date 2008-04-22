package org.marketcetera.marketdata;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import junit.framework.Test;

import org.marketcetera.core.ExpectedTestFailure;
import org.marketcetera.core.publisher.ISubscriber;
import org.marketcetera.core.publisher.TestSubscriber;

import quickfix.Message;

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: $
 * @since 0.43-SNAPSHOT
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
    
    @SuppressWarnings("unchecked")
    public void testGenerateTokenSpec()
        throws Exception
    {
        List<? extends ISubscriber> emptySubscribers = new ArrayList<ISubscriber>();
        List nonemptySubscribers = new ArrayList();
        nonemptySubscribers.add(new TestSubscriber());
        nonemptySubscribers.add(new DoNothingSubscriber());
        for(int a=0;a<=1;a++) {
            for(int b=0;b<=1;b++) {
                for (int c=0;c<=2;c++) {
                    List listToUse = null;
                    if(c == 1) {
                        listToUse = emptySubscribers;
                    }
                    if(c == 2) { 
                        listToUse = nonemptySubscribers;
                    }
                    doValidateTokenSpec(a==0 ? null : mCredentials,
                                        b==0 ? null : mMessage,
                                        listToUse);
                }
            }
        }
    }
    
    public void testUnmodifiableSubscriberList()
        throws Exception
    {
        List<TestSubscriber> subscribers = new ArrayList<TestSubscriber>();
        final TestSubscriber s1 = new TestSubscriber();
        final TestSubscriber s2 = new TestSubscriber();
        subscribers.add(s1);
        subscribers.add(s2);
        MarketDataFeedTokenSpec<TestMarketDataFeedCredentials> tokenSpec = MarketDataFeedTokenSpec.generateTokenSpec(mCredentials, 
                                                                                                                     mMessage, 
                                                                                                                     subscribers);
        final List<? extends ISubscriber> returnedSubscribers = tokenSpec.getSubscribers();
        assertEquals(subscribers.size(),
                     returnedSubscribers.size());
        new ExpectedTestFailure(UnsupportedOperationException.class) {
            protected void execute()
                    throws Throwable
            {
                returnedSubscribers.remove(s1);
            }
        }.run();      
        new ExpectedTestFailure(UnsupportedOperationException.class) {
            protected void execute()
                    throws Throwable
            {
                returnedSubscribers.add(null);
            }
        }.run();      
    }
    
    private void doValidateTokenSpec(final TestMarketDataFeedCredentials inCredentials,
                                     final Message inMessage,
                                     final List<TestSubscriber> inSubscribers)
        throws Exception
    {
        if(inCredentials == null ||
           inMessage == null) {
            new ExpectedTestFailure(NullPointerException.class) {
                protected void execute()
                        throws Throwable
                {
                    MarketDataFeedTokenSpec.generateTokenSpec(inCredentials, 
                                                              inMessage,
                                                              inSubscribers);
                }
            }.run();                             
        } else {
            MarketDataFeedTokenSpec tokenSpec = MarketDataFeedTokenSpec.generateTokenSpec(inCredentials, 
                                                                                          inMessage, 
                                                                                          inSubscribers);
            assertEquals(inCredentials,
                         tokenSpec.getCredentials());
            assertEquals(inMessage,
                         tokenSpec.getMessage());
            if(inSubscribers == null) {
                assertEquals(0,
                             tokenSpec.getSubscribers().size());
            } else {
                assertTrue(Arrays.equals(inSubscribers.toArray(),
                                         tokenSpec.getSubscribers().toArray()));
            }
        }
    }
}
