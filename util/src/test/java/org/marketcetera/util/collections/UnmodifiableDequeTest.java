package org.marketcetera.util.collections;

import static org.junit.Assert.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.Test;
import org.marketcetera.util.test.CollectionAssert;

/* $License$ */

/**
 * Tests {@link UnmodifiableDeque}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: UnmodifiableDequeTest.java 16154 2012-07-14 16:34:05Z colin $
 * @since 2.1.4
 */
public class UnmodifiableDequeTest
{
    /**
     * Tests the use of the constructor.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void constructorTest()
            throws Exception
    {
        try {
            new UnmodifiableDeque<Object>(null);
            fail();
        } catch (NullPointerException e) {
            // expected failure
        }
        Deque<String> testCollection = new LinkedList<String>();
        Deque<String> q = new UnmodifiableDeque<String>(testCollection);
        assertTrue(q.isEmpty());
        assertEquals(0,
                     q.size());
        testCollection.add("value");
        assertEquals(1,
                     q.size());
    }
    /**
     * Tests the set of functions that are not supported.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void unsupportedOperations()
            throws Exception
    {
        Deque<String> testCollection = new LinkedList<String>();
        Deque<String> q = new UnmodifiableDeque<String>(testCollection);
        try {
            q.addAll(testCollection);
            fail();
        } catch (UnsupportedOperationException e) {}
        try {
            q.clear();
            fail();
        } catch (UnsupportedOperationException e) {}
        try {
            q.removeAll(testCollection);
            fail();
        } catch (UnsupportedOperationException e) {}
        try {
            q.retainAll(testCollection);
            fail();
        } catch (UnsupportedOperationException e) {}
        try {
            q.add("new value");
            fail();
        } catch (UnsupportedOperationException e) {}
        try {
            q.addFirst("new value");
            fail();
        } catch (UnsupportedOperationException e) {}
        try {
            q.addLast("new value");
            fail();
        } catch (UnsupportedOperationException e) {}
        try {
            q.offer("new value");
            fail();
        } catch (UnsupportedOperationException e) {}
        try {
            q.offerFirst("new value");
            fail();
        } catch (UnsupportedOperationException e) {}
        try {
            q.offerLast("new value");
            fail();
        } catch (UnsupportedOperationException e) {}
        try {
            q.poll();
            fail();
        } catch (UnsupportedOperationException e) {}
        try {
            q.pollFirst();
            fail();
        } catch (UnsupportedOperationException e) {}
        try {
            q.pollLast();
            fail();
        } catch (UnsupportedOperationException e) {}
        try {
            q.pop();
            fail();
        } catch (UnsupportedOperationException e) {}
        try {
            q.push("new value again");
            fail();
        } catch (UnsupportedOperationException e) {}
        try {
            q.remove();
            fail();
        } catch (UnsupportedOperationException e) {}
        try {
            q.remove("new value again");
            fail();
        } catch (UnsupportedOperationException e) {}
        try {
            q.removeFirst();
            fail();
        } catch (UnsupportedOperationException e) {}
        try {
            q.removeFirstOccurrence("new value again");
            fail();
        } catch (UnsupportedOperationException e) {}
        try {
            q.removeLast();
            fail();
        } catch (UnsupportedOperationException e) {}
        try {
            q.removeLastOccurrence("new value again");
            fail();
        } catch (UnsupportedOperationException e) {}
    }
    /**
     * Tests the set of supported operations.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void supportedOperations()
            throws Exception
    {
        Deque<String> testCollection = new LinkedList<String>();
        Deque<String> q = new UnmodifiableDeque<String>(testCollection);
        Deque<String> expectedCollection = new LinkedList<String>();
        verify(expectedCollection,
               q);
        String value1 = "value-" + System.nanoTime();
        testCollection.add(value1);
        expectedCollection.add(value1);
        verify(expectedCollection,
               q);
        String value2 = "value-" + System.nanoTime();
        assertFalse(value1.equals(value2));
        testCollection.addFirst(value2);
        expectedCollection.addFirst(value2);
        verify(expectedCollection,
               q);
        testCollection.addLast(value2);
        expectedCollection.addLast(value2);
        verify(expectedCollection,
               q);
    }
    /**
     * Verifies that the returned collection is not thread-safe.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void concurrenceTest()
            throws Exception
    {
        Deque<String> testCollection = new LinkedList<String>();
        final Deque<String> q = new UnmodifiableDeque<String>(testCollection);
        // add a few values to the testCollection (reflected in q)
        testCollection.add("value1");
        testCollection.add("value2");
        testCollection.add("value3");
        // set up a flag to pause iteration
        final AtomicBoolean keepWaiting = new AtomicBoolean(true);
        final AtomicBoolean clientComplete = new AtomicBoolean(false);
        final AtomicBoolean clientReady = new AtomicBoolean(false);
        // track any exceptions our iterator collects
        final List<Exception> exceptions = new ArrayList<Exception>();
        // set up an object that iterates using a q iterator
        Thread client1 = new Thread(new Runnable() {
            @Override
            public void run()
            {
                Iterator<String> iterator = q.iterator();
                clientReady.set(true);
                synchronized(clientReady) {
                    clientReady.notifyAll();
                }
                try {
                    while(iterator.hasNext()) {
                        iterator.next();
                        while(keepWaiting.get()) {
                            synchronized(keepWaiting) {
                                keepWaiting.wait();
                            }
                        }
                    }
                } catch (Exception e) {
                    exceptions.add(e);
                } finally {
                    clientComplete.set(true);
                }
            }
        });
        client1.start();
        // client1 should now be waiting after retrieving value1 using an active iterator
        assertFalse(clientComplete.get());
        while(!clientReady.get()) {
            synchronized(clientReady) {
                clientReady.wait();
            }
        }
        // remove value2 from the underlying collection and see if the iterator breaks
        assertEquals(3,
                     testCollection.size());
        testCollection.remove("value2");
        assertEquals(2,
                     testCollection.size());
        // free up client1 to complete
        keepWaiting.set(false);
        synchronized(keepWaiting) {
            keepWaiting.notifyAll();
        }
        client1.join();
        assertTrue(clientComplete.get());
        assertEquals(1,
                     exceptions.size());
        assertTrue(exceptions.get(0) instanceof ConcurrentModificationException);
    }
    /**
     * Verifies that the two collections contain the same elements and
     * have the same behavior.
     *
     * @param inExpectedCollection a <code>Deque&lt;String&gt;</code> value
     * @param inActualCollection a <code>Deque&lt;String&gt;</code> value
     * @throws Exception if an unexpected error occurs
     */
    private void verify(Deque<String> inExpectedCollection,
                        Deque<String> inActualCollection)
            throws Exception
    {
        assertNotNull(inActualCollection.toString());
        assertEquals(inExpectedCollection.isEmpty(),
                     inActualCollection.isEmpty());
        assertEquals(inExpectedCollection.size(),
                     inActualCollection.size());
        assertEquals(inExpectedCollection.peek(),
                     inActualCollection.peek());
        assertEquals(inExpectedCollection.peekFirst(),
                     inActualCollection.peekFirst());
        assertEquals(inExpectedCollection.peekLast(),
                     inActualCollection.peekLast());
        if(inExpectedCollection.isEmpty()) {
            try {
                inExpectedCollection.getFirst();
                fail();
            } catch (NoSuchElementException e) {}
            try {
                inActualCollection.getFirst();
                fail();
            } catch (NoSuchElementException e) {}
            try {
                inExpectedCollection.getLast();
                fail();
            } catch (NoSuchElementException e) {}
            try {
                inActualCollection.getLast();
                fail();
            } catch (NoSuchElementException e) {}
            try {
                inExpectedCollection.element();
                fail();
            } catch (NoSuchElementException e) {}
            try {
                inActualCollection.element();
                fail();
            } catch (NoSuchElementException e) {}
        } else {
            assertEquals(inExpectedCollection.getFirst(),
                         inActualCollection.getFirst());
            assertEquals(inExpectedCollection.getLast(),
                         inActualCollection.getLast());
            assertEquals(inExpectedCollection.element(),
                         inActualCollection.element());
        }
        assertTrue(inActualCollection.containsAll(inExpectedCollection));
        CollectionAssert.assertArrayPermutation(inExpectedCollection.toArray(),
                                                inActualCollection.toArray());
        CollectionAssert.assertArrayPermutation(inExpectedCollection.toArray(new String[0]),
                                                inActualCollection.toArray(new String[0]));
        Iterator<String> expectedIterator = inExpectedCollection.iterator();
        Iterator<String> actualIterator = inActualCollection.iterator();
        while(expectedIterator.hasNext()) {
            assertTrue(actualIterator.hasNext());
            String expectedElement = expectedIterator.next();
            String actualElement = actualIterator.next();
            assertEquals(expectedElement,
                         actualElement);
            assertTrue(inActualCollection.contains(actualElement));
            try {
                actualIterator.remove();
            } catch (UnsupportedOperationException e) {}
        }
        assertFalse(actualIterator.hasNext());
        expectedIterator = inExpectedCollection.descendingIterator();
        actualIterator = inActualCollection.descendingIterator();
        while(expectedIterator.hasNext()) {
            assertTrue(actualIterator.hasNext());
            String expectedElement = expectedIterator.next();
            String actualElement = actualIterator.next();
            assertEquals(expectedElement,
                         actualElement);
            assertTrue(inActualCollection.contains(actualElement));
            try {
                actualIterator.remove();
            } catch (UnsupportedOperationException e) {}
        }
        assertFalse(actualIterator.hasNext());
    }
}
