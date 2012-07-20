package org.marketcetera.core.position.impl;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import com.google.common.collect.Lists;

/* $License$ */

/**
 * Helper for testing {@link Comparable#compareTo(Object)} behavior.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 2.0.0
 */
public class OrderingTestHelper {

    /**
     * Verifies a list of objects can be sorted.
     * 
     * @param ordered
     *            the ordered list of objects
     */
    public static <T extends Comparable<? super T>> void testOrdering(
            List<T> ordered) {
        List<T> copy = Lists.newArrayList(ordered);
        Collections.shuffle(copy);
        Collections.sort(copy);
        assertSameOrder(ordered, copy);
    }

    /**
     * Verifies that a comparator correctly sorts a list of objects.
     * 
     * @param ordered
     *            the ordered list of objects
     * @param comparator
     *            the comparator to test
     */
    public static <T> void testOrdering(List<T> ordered,
            Comparator<? super T> comparator) {
        List<T> copy = Lists.newArrayList(ordered);
        Collections.shuffle(copy);
        Collections.sort(copy, comparator);
        assertSameOrder(ordered, copy);
    }

    private static <T> void assertSameOrder(List<T> ordered, List<T> copy) {
        Iterator<?> sorted = copy.iterator();
        Iterator<?> original = ordered.iterator();
        for (int i = 0; i < copy.size(); i++) {
            assertThat("Element " + i, sorted.next(), is((Object) original
                    .next()));
        }
    }
}
