package org.marketcetera.persist;

import org.marketcetera.core.ClassVersion;
import static org.junit.Assert.assertTrue;

import java.util.Comparator;

/* $License$ */
/**
 * Helper class to test multi query ordering behavior for attributes
 * that implement the Comparable interface
 *
 * @author anshul@marketcetera.com
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public abstract class MultiQueryOrderComparableTestHelper<C extends EntityBase,
        S extends SummaryEntityBase,
        T extends Comparable<T>>
        extends MultiQueryOrderTestHelper<C, S> {
    /**
     * Creates an instance to test ordering of comparable attribute fields
     *
     * @param order         The entity order instance.
     * @param clazz         The entity class
     * @param attributeName The attribute names
     * @param query The query instance
     * @throws Exception if there's an error
     */
    public MultiQueryOrderComparableTestHelper(EntityOrder order,
                                           Class<C> clazz,
                                           String attributeName,
                                           MultipleEntityQuery query)
            throws Exception {
        super(order, clazz, attributeName,query);
        assertTrue(pDesc.getPropertyType().toString(),
                Comparable.class.isAssignableFrom(pDesc.getPropertyType()));
    }

    protected Comparator<T> getComparator() {
        return new Comparablator<T>();
    }
    private static class Comparablator<T extends Comparable<T>>
            implements Comparator<T> {
        public int compare(T o1, T o2) {
            return o1.compareTo(o2); 
        }
    }
}
