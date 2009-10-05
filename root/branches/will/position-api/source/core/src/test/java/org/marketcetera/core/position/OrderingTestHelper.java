package org.marketcetera.core.position;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.google.common.collect.Lists;

/* $License$ */

/**
 * Helper for testing {@link Comparable#compareTo(Object)} behavior.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
public class OrderingTestHelper {

    /**
     * Verifies a list of objects can be sorted.
     * 
     * @param ordered
     *            the ordered list of objects
     */
    public static <T extends Comparable<? super T>> void testOrdering(List<T> ordered) {
        List<T> copy = Lists
                .newArrayList(ordered);
        Collections.shuffle(copy);
        Collections.sort(copy);
        Iterator<?> sorted = copy.iterator();
        Iterator<?> original = ordered.iterator();
        for (int i = 0; i < copy.size(); i++) {
            assertThat("Element " + i, sorted.next(), is((Object) original
                    .next()));
        }
    }
}
