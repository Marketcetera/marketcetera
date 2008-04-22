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
 * @version $Id: $
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
        
        doRunTest(new ArrayList<TestSubscriber>(),
                  null);        
        doRunTest(new ArrayList<TestSubscriber>(),
                  this);
        
        TestSubscriber s1 = new TestSubscriber(false,
                                               false,
                                               false);
        TestSubscriber s2 = new TestSubscriber(false,
                                               false,
                                               true);
        TestSubscriber s3 = new TestSubscriber(false,
                                               true,
                                               false);
        TestSubscriber s4 = new TestSubscriber(false,
                                               true,
                                               true);
        TestSubscriber s5 = new TestSubscriber(true,
                                               false,
                                               false);
        TestSubscriber s6 = new TestSubscriber(true,
                                               false,
                                               true);
        TestSubscriber s7 = new TestSubscriber(true,
                                               true,
                                               false);
        TestSubscriber s8 = new TestSubscriber(true,
                                               true,
                                               true);
        
        doRunTest(Arrays.asList(new TestSubscriber[] { s1 }),
                  null);
        doRunTest(Arrays.asList(new TestSubscriber[] { s1 }),
                  this);

        doRunTest(Arrays.asList(new TestSubscriber[] { s1, s2, s3, s4, s5, s6, s7, s8 }),
                  null);
        doRunTest(Arrays.asList(new TestSubscriber[] { s1, s2, s3, s4, s5, s6, s7, s8 }),
                  this);
    }
    
    private void doRunTest(List<TestSubscriber> inSubscribers,
                           Object inData)
        throws Exception
    {
        if(inSubscribers != null) {
            for(TestSubscriber s : inSubscribers) {
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
        for(TestSubscriber s : inSubscribers) {
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
