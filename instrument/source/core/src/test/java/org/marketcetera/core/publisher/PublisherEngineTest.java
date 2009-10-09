package org.marketcetera.core.publisher;

import org.junit.Before;
import org.junit.Test;
import org.junit.BeforeClass;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
import org.marketcetera.core.LoggerConfiguration;

import java.util.Random;
import java.util.List;
import java.util.LinkedList;
import java.util.concurrent.Semaphore;

/**
 * Tests {@link PublisherEngine}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 0.5.0
 */
public class PublisherEngineTest
{
    private MockPublisher mPublisher;

    @BeforeClass
    public static void logSetup()
    {
        LoggerConfiguration.logSetup();
    }
    
   @Before
    public void setUp()
            throws Exception
    {
        mPublisher = new MockPublisher();
    }

    @Test
    public void testInitializeThreadPool()
        throws Exception
    {
        mPublisher.publish(this);
        mPublisher.publish(this);
    }
    
    @Test
    public void testConstructor()
        throws Exception
    {
        PublisherEngine e = new PublisherEngine();
        e.publish(this);
    }
    
    @Test
    public void testSubscribe()
        throws Exception
    {
        mPublisher.subscribe(null);
        mPublisher.publishAndWait(this);
        MockSubscriber s = new MockSubscriber();
        mPublisher.subscribe(s);
        assertEquals(0,
                     s.getPublishCount());
        assertNull(s.getData());
        mPublisher.publishAndWait(this);
        assertEquals(this,
                     s.getData());
        assertEquals(1,
                     s.getPublishCount());
        // subscribe again, make sure we get only only publication
        s.setData(null);
        mPublisher.subscribe(s);
        assertEquals(1,
                     s.getPublishCount());
        assertNull(s.getData());
        mPublisher.publishAndWait(this);
        assertEquals(this,
                     s.getData());
        assertEquals(2, // not 3!
                     s.getPublishCount());
    }
    
    @Test
    public void testUnsubscribe()
        throws Exception
    {
        mPublisher.unsubscribe(null);
        mPublisher.publish(this);
        MockSubscriber s = new MockSubscriber();
        assertEquals(0,
                     s.getPublishCount());
        assertEquals(null,
                     s.getData());
        mPublisher.unsubscribe(s);
        mPublisher.publish(this);
        assertEquals(0,
                     s.getPublishCount());
        assertEquals(null,
                     s.getData());
        mPublisher.subscribe(s);
        mPublisher.publish(this);
        while(s.getPublishCount() == 0) {
            Thread.sleep(100);
        }
        assertEquals(this,
                     s.getData());
        assertEquals(1,
                     s.getPublishCount());
        s.setData(null);
        mPublisher.unsubscribe(s);
        mPublisher.publish(this);
        Thread.sleep(5000);
        assertEquals(1,
                     s.getPublishCount());
        assertEquals(null,
                     s.getData());
    }
    
    @Test
    public void testParallel()
        throws Exception
    {
        MockPublisher[] publishers = new MockPublisher[50];
        for(int i=0;i<50;i++) {
            publishers[i] = new MockPublisher();
        }
        MockSubscriber[] subscribers = new MockSubscriber[500];
        for(int i=0;i<subscribers.length;i++) {
            subscribers[i] = new MockSubscriber();
        }
        
        Thread[] threads = new Thread[20];
        for(int i=0;i<20;i++) {
            threads[i] = new Thread(new Tester(publishers,
                                               subscribers));
            threads[i].start();
        }
        for(Thread t : threads) {
            t.join();
        }
    }

    @Test
    public void testSubscribers() throws Exception
    {
        List<MockSubscriber> subscribers = new LinkedList<MockSubscriber>();
        //subscribers with all permutations.
        boolean [] values = {true,false};
        for(boolean interesting: values) {
            for(boolean interestingThrows: values) {
                for(boolean publishThrows: values) {
                    subscribers.add(new MockSubscriber(interesting,
                            interestingThrows, publishThrows));
                }
            }
        }
        for (boolean synchronous: values) {
            for(int i = 0; i < subscribers.size(); i++) {
                doRunTest(synchronous, subscribers.subList(0, i), null);
                doRunTest(synchronous, subscribers.subList(0, i), this);
            }
        }
    }
    
    @Test(timeout = 10000)
    public void testSyncAsyncPublishAndWait() throws Exception
    {
        //Test default constructor
        PublisherEngine engine = new PublisherEngine();
        checkPublishAndWait(engine, false);
        //Test the other constructor
        engine = new PublisherEngine(false);
        checkPublishAndWait(engine, false);
        //Test the other constructor
        engine = new PublisherEngine(true);
        checkPublishAndWait(engine, true);
    }

    @Test
    public void testSyncAsyncPublish() throws Exception
    {
        //Test default constructor
        PublisherEngine engine = new PublisherEngine();
        checkPublish(engine, false);
        //Test the other constructor
        engine = new PublisherEngine(false);
        checkPublish(engine, false);
        //Test the other constructor
        engine = new PublisherEngine(true);
        checkPublish(engine, true);
    }

    @Test(timeout = 10000)
    public void testSlowSubscriberAsync() throws Exception
    {
        MockSubscriber subscriber = new MockSubscriber();
        Semaphore acquireSemaphore = new Semaphore(0);
        Semaphore releaseSemaphore = new Semaphore(0);
        subscriber.setAcquireSemaphore(acquireSemaphore);
        subscriber.setReleaseSemaphore(releaseSemaphore);
        PublisherEngine engine = new PublisherEngine();
        engine.subscribe(subscriber);
        Object data = new Object();
        engine.publish(data);
        //Wait for subscriber to be ready
        while(!acquireSemaphore.hasQueuedThreads()) {
            Thread.sleep(500);
        }
        //verify subscriber hasn't received anything
        assertNull(subscriber.getData());
        //now let the subscriber through
        acquireSemaphore.release();
        //And wait for it to get done
        releaseSemaphore.acquire();
        //verify that subscriber received data
        assertEquals(data, subscriber.getData());
    }

    private static void checkPublishAndWait(PublisherEngine inEngine,
                                            boolean inSyncNotification)
            throws Exception
    {
        final MockSubscriber subscriber = new MockSubscriber();
        assertEquals(inSyncNotification, inEngine.isSynchronousNotification());
        inEngine.subscribe(subscriber);
        Object data = new Object();
        inEngine.publishAndWait(data);
        assertSame(data, subscriber.getData());
        if (inSyncNotification) {
            assertSame(Thread.currentThread(), subscriber.getPublishThread());
        } else {
            assertNotSame(Thread.currentThread(), subscriber.getPublishThread());
        }
    }
    private static void checkPublish(PublisherEngine inEngine,
                                     boolean inSyncNotification)
            throws Exception
    {
        final MockSubscriber subscriber = new MockSubscriber();
        final Semaphore s = new Semaphore(0);
        subscriber.setReleaseSemaphore(s);
        assertEquals(inSyncNotification, inEngine.isSynchronousNotification());
        inEngine.subscribe(subscriber);
        Object data = new Object();
        inEngine.publish(data);
        //wait for subscriber to receive it.
        s.acquire();
        assertSame(data, subscriber.getData());
        if (inSyncNotification) {
            assertSame(Thread.currentThread(), subscriber.getPublishThread());
        } else {
            assertNotSame(Thread.currentThread(), subscriber.getPublishThread());
        }

    }
    private void doRunTest(boolean inSynchronousPublication,
                           List<MockSubscriber> inSubscribers,
                           Object inData)
        throws Exception
    {
        for(MockSubscriber s : inSubscribers) {
            s.reset();
        }
        PublisherEngine engine = new PublisherEngine(inSynchronousPublication);
        for(MockSubscriber subscriber: inSubscribers) {
            engine.subscribe(subscriber);
        }

        engine.publishAndWait(inData);

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

    public static class Tester
        implements Runnable
    {
        private Random r = new Random(System.nanoTime());
        private MockPublisher[] mPublishers;
        private MockSubscriber[] mSubscribers;
        
        public Tester(MockPublisher[] inPublishers,
                      MockSubscriber[] inSubscribers)
        {
            mPublishers = inPublishers;
            mSubscribers = inSubscribers; 
        }
        
        public void run()
        {
            for(int i=0;i<10;i++) {
                for(MockSubscriber s : mSubscribers) {
                    int publisher = r.nextInt(50);
                    int flag = r.nextInt(3);
                    switch(flag) {
                        case 0:
                            mPublishers[publisher].subscribe(s);
                            break;
                        case 1:
                            mPublishers[publisher].unsubscribe(s);
                            break;
                        case 2:
                            mPublishers[publisher].publish(this);
                            break;
                    }
                }
            }        
        }
    }
}
