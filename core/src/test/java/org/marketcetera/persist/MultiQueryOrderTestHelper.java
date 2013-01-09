package org.marketcetera.persist;

import org.marketcetera.core.ClassVersion;
import static org.junit.Assert.fail;

import java.beans.PropertyDescriptor;
import java.util.Comparator;

/* $License$ */
/**
 * Helper class to test multi query ordering behavior
 *
 * @author anshul@marketcetera.com
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public abstract class MultiQueryOrderTestHelper<C extends EntityBase,
        S extends SummaryEntityBase> {
    /**
     * Creates an instance.
     *
     * @param order         the entity order to use for the test.
     * @param clazz         the entity class
     * @param attributeName the attribute name that the results are ordered by.
     * @param query the query instance value for testing.
     *
     * @throws Exception if the attribute read / write methods could not be found
     */
    protected MultiQueryOrderTestHelper(EntityOrder order,
                                        Class<C> clazz,
                                        String attributeName,
                                        MultipleEntityQuery query)
            throws Exception {
        this.order = order;
        pDesc = PersistTestBase.getPropertyDescriptor(clazz, attributeName);
        this.query = query;
    }


    /**
     * Sets the field thats being tested to a test value
     *
     * @param c   The entity instance
     * @param idx the index of the entity instance. The index value is
     *            between 0 and {@link #getNumInstances()}
     *
     * @throws Exception of there's an error.
     */
    protected void setOrderField(C c, int idx) throws Exception {
        if(pDesc.getWriteMethod() == null) {
            fail("Unwritable property:"+pDesc.getName()); //$NON-NLS-1$
        }
        pDesc.getWriteMethod().invoke(c, generateFieldValue(idx));
    }

    /**
     * Gets the value of the field being tested
     * 
     * @param s the summary view of the entity
     * 
     * @return the field value
     * 
     * @throws Exception if there were errors fetching the field value.
     */
    protected Object getOrderField(S s) throws Exception {
        if(pDesc.getReadMethod() == null) {
            fail("Unreadable property:"+pDesc.getReadMethod()); //$NON-NLS-1$
        }
        return pDesc.getReadMethod().invoke(s);
    }

    /**
     * Compares the order field value on the supplied entity
     * instance and returns a negative, 0 or positive value
     * depending on whether the field value on the first instance
     * is less than, equal to or greater than the field value on
     * the second instance.
     *
     * @param summary1 The first instance
     * @param summary2 The second instance
     *
     * @return a value that indicates if the field value is less than
     *         equal to or greater than the field value on the second instance
     *
     * @throws Exception if there's an error
     */
    @SuppressWarnings("unchecked") //$NON-NLS-1$
    protected int compareOrderField(S summary1, S summary2) throws Exception {
        if(pDesc.getReadMethod() == null) {
            fail("Unreadable property:"+pDesc.getReadMethod()); //$NON-NLS-1$
        }
        return getComparator().compare(pDesc.getReadMethod().invoke(summary1),
                pDesc.getReadMethod().invoke(summary2));
    }

    /**
     * They entity order instance
     *
     * @return the entity order instance
     */
    protected EntityOrder getOrder() {
        return order;
    }

    /**
     * The query instance to use for testing.
     * @return the query instance to use for testing.
     */
    public MultipleEntityQuery getQuery() {
        return query;
    }

    /**
     * The comparator instance to compare the field values.
     *
     * @return the comparator instance
     */
    protected abstract Comparator getComparator();

    /**
     * Number of entity instances to create for testing.
     *
     * @return the number of test entity instances.
     */
    protected abstract int getNumInstances();

    /**
     * Generates the field value for the entity instance
     *
     * @param idx The entity index
     * 
     * @return the generated field value.
     */
    protected abstract Object generateFieldValue(int idx);

    protected final PropertyDescriptor pDesc;
    private MultipleEntityQuery query;
    private EntityOrder order;
}
