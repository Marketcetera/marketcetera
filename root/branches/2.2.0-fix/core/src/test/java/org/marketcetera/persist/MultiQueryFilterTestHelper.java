package org.marketcetera.persist;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.core.Pair;
import static org.junit.Assert.fail;

import java.beans.PropertyDescriptor;

/* $License$ */
/**
 * Helper class too help test query filtering
 *
 * @author anshul@marketcetera.com
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public abstract class MultiQueryFilterTestHelper<E extends EntityBase,
        S extends SummaryEntityBase> {
    /**
     * Creates an instance to test query filters
     *
     * @param entityClass   The class of the instance being tested.
     * @param attributeName The name of the entity attribute that's
     *                      being filtered
     * @param queryClass    The multi query class for the entity
     * @param filterName    The name of the filter attribute on the
     *                      multi query
     * @param query         The query instance being tested.
     *
     * @throws Exception in case there's an error.
     */
    protected MultiQueryFilterTestHelper(Class<E> entityClass,
                                         String attributeName,
                                         Class<? extends MultipleEntityQuery> queryClass,
                                         String filterName,
                                         MultipleEntityQuery query) throws Exception {
        fieldDesc = PersistTestBase.getPropertyDescriptor(entityClass, attributeName);
        filterDesc = PersistTestBase.getPropertyDescriptor(queryClass, filterName);
        this.query = query;
    }

    /**
     * Returns the number of entity instances to generate for testing.
     *
     * @return number of entity instances to create for testing.
     */
    protected abstract int getNumInstances();

    /**
     * Generates the attribute value given the instance index.
     *
     * @param idx the instance index. The value is between
     * 0 and {@link #getNumInstances()}
     *
     * @return the instance index.
     */
    protected abstract Object generateValue(int idx);

    /**
     * Returns true if the filter value matches the attribute
     * value on the supplied entity instance.
     *
     * @param filter The filter value.
     * @param s      the entity instance.
     *
     * @return true if the filter matches, false if it doesn't
     *
     * @throws Exception if there's an error
     */
    protected abstract boolean matches(Object filter, S s) throws Exception;

    /**
     * Returns filter pairs that need to be tested. Each pair has the
     * filter value and the number of expected instances that
     * it will match
     *
     * @return filter pairs for testing.
     */
    public abstract FilterPair[] getFilters();

    /**
     * The query instance for testing.
     *
     * @return the multi query instance.
     */
    protected MultipleEntityQuery getQuery() {
        return query;
    }

    /**
     * Sets the entity field tested by this helper
     * to a generated value. This method is invoked
     * multiple times with the index value increasing
     * from 0 to {@link #getNumInstances()}
     *
     * @param e The entity being tested.
     * @param i The index value.
     * @throws Exception if there's an error
     */
    protected void setField(E e, int i) throws Exception {
        if(fieldDesc.getWriteMethod() == null) {
            fail("Unwritable property:"+fieldDesc.getName()); //$NON-NLS-1$
        }
        fieldDesc.getWriteMethod().invoke(e, generateValue(i));
    }

    /**
     * Retrieve's the attribute field value given the entity instance.
     *
     * @param s the entity instance.
     *
     * @return the attribute field value.
     *
     * @throws Exception if there's an error retrieving the field value.
     */
    protected Object getField(S s) throws Exception {
        if(fieldDesc.getReadMethod() == null) {
            fail("Unreadable property:"+fieldDesc.getName()); //$NON-NLS-1$
        }
        return fieldDesc.getReadMethod().invoke(s);
    }

    /**
     * Applies the filter on the supplied query instance
     *
     * @param query  the query instance.
     * @param filter the filter value.
     *
     * @throws Exception if there's an error applying the filter
     */
    protected void applyFilter(MultipleEntityQuery query,
                               Object filter) throws Exception {
        if(filterDesc.getWriteMethod() == null) {
            fail("Unwritable filter:"+filterDesc.getName()); //$NON-NLS-1$
        }
        filterDesc.getWriteMethod().invoke(query, filter);
    }

    /**
     * Instance specify a filter value and the number of entity
     * instances that this filter is supposed to match.
     */
    public static class FilterPair extends Pair<Object, Long> {
        /**
         * Creates an instance.
         *
         * @param filter       the filter value
         * @param numInstances the number of instances that the
         *                     filter should match
         */
        public FilterPair(Object filter, Long numInstances) {
            super(filter, numInstances);
        }
    }

    protected final PropertyDescriptor fieldDesc;
    protected final PropertyDescriptor filterDesc;
    private MultipleEntityQuery query;
}
