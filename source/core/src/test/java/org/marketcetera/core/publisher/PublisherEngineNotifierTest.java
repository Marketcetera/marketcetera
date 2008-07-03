package org.marketcetera.core.publisher;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.marketcetera.core.MarketceteraTestSuite;

/**
 * Tests the {@link PublisherEngineNotifier} class.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 0.43-SNAPSHOT
 */
public class PublisherEngineNotifierTest
        extends TestCase
{
    public PublisherEngineNotifierTest(String inArg0)
    {
        super(inArg0);
    }
    
    public static Test suite() 
    {
        TestSuite suite = new MarketceteraTestSuite(PublisherEngineNotifierTest.class);
        return suite;    
    }
    
    public void testConstructor()
        throws Exception
    {
        List<ISubscriber> s0 = new ArrayList<ISubscriber>();
        List<ISubscriber> s1 = new ArrayList<ISubscriber>();
        Object d0 = new Object();
        
        assertNotNull(new PublisherEngineNotifier(null,
                                                  null));
        assertNotNull(new PublisherEngineNotifier(null,
                                                  d0));
        assertNotNull(new PublisherEngineNotifier(s0,
                                                  null));
        assertNotNull(new PublisherEngineNotifier(s0,
                                                  d0));
        assertNotNull(new PublisherEngineNotifier(s1,
                                                  d0));
    }
    
    public void testRun()
        throws Exception
    {
        doRunTest(null,
                  null);        
        doRunTest(null,
                  this);
        
        doRunTest(new ArrayList<MockSubscriber>(),
                  null);        
        doRunTest(new ArrayList<MockSubscriber>(),
                  this);
        
        MockSubscriber s1 = new MockSubscriber(false,
                                               false,
                                               false);
        MockSubscriber s2 = new MockSubscriber(false,
                                               false,
                                               true);
        MockSubscriber s3 = new MockSubscriber(false,
                                               true,
                                               false);
        MockSubscriber s4 = new MockSubscriber(false,
                                               true,
                                               true);
        MockSubscriber s5 = new MockSubscriber(true,
                                               false,
                                               false);
        MockSubscriber s6 = new MockSubscriber(true,
                                               false,
                                               true);
        MockSubscriber s7 = new MockSubscriber(true,
                                               true,
                                               false);
        MockSubscriber s8 = new MockSubscriber(true,
                                               true,
                                               true);
        
        doRunTest(Arrays.asList(new MockSubscriber[] { s1 }),
                  null);
        doRunTest(Arrays.asList(new MockSubscriber[] { s1 }),
                  this);

        doRunTest(Arrays.asList(new MockSubscriber[] { s1, s2, s3, s4, s5, s6, s7, s8 }),
                  null);
        doRunTest(Arrays.asList(new MockSubscriber[] { s1, s2, s3, s4, s5, s6, s7, s8 }),
                  this);
    }
    
    private void doRunTest(List<MockSubscriber> inSubscribers,
                           Object inData)
        throws Exception
    {
        if(inSubscribers != null) {
            for(MockSubscriber s : inSubscribers) {
                s.setData(null);
            }
        }
        PublisherEngineNotifier n = new PublisherEngineNotifier(inSubscribers,
                                                                inData);
        n.call();
        if(inSubscribers == null) {
            return;
        }
        int lastCounter = 0;
        for(MockSubscriber s : inSubscribers) {
            if(s.getInteresting() &&
               !(s.getInterestingThrows() ||
                 s.getPublishThrows())) {
                assertEquals(inData,
                             s.getData());
                assertTrue(s.getCounter() > lastCounter);
                lastCounter = s.getCounter();
            } else {
                assertNull(s.getData());
            }
        }        
    }
}
