package org.marketcetera.persist;

import org.marketcetera.core.ClassVersion;
import static org.junit.Assert.assertEquals;

import java.util.Date;
import java.util.List;
import java.util.LinkedList;

/* $License$ */
/**
 * Helper class to help test multi query date filters.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public class MultiQueryDateFilterTestHelper<E extends EntityBase,
        S extends SummaryEntityBase> extends
        MultiQueryFilterTestHelper<E, S> {
    /**
     * Creates an instance
     *
     * @param entityClass              The entity class
     * @param attributeName            the string attribute that's being filtered.
     * @param multipleEntityQueryClass the multi query class
     * @param filterName               The filter attribute name
     * @param query                    the query instance
     * @param isAfter                  if the filter matches dates after the filter date
     * @throws Exception if there's an error
     */
    public MultiQueryDateFilterTestHelper(Class<E> entityClass,
                                            String attributeName,
                                            Class<? extends MultipleEntityQuery> multipleEntityQueryClass,
                                            String filterName,
                                            MultipleEntityQuery query,
                                            boolean isAfter) throws Exception {

        super(entityClass, attributeName, multipleEntityQueryClass, filterName, query);
        assertEquals(Date.class,fieldDesc.getPropertyType());
        assertEquals(Date.class,filterDesc.getPropertyType());
        mAfter = isAfter;
    }

    @Override
    protected int getNumInstances() {
        return NUM_INSTANCES;
    }

    @Override
    protected Object generateValue(int idx) {
        //Don't care as this method will not be invoked as
        //we override setField instead.
        return null;
    }

    @Override
    protected void setField(E e, int i) throws Exception {
        //Do nothing, sleep here so that db assigns incremental
        //lastUpdated values
        int matchCount = mAfter? NUM_INSTANCES - i: i;
        if (mAfter) {
            mFilters.add(f(new Date(), matchCount));
            PersistTestBase.sleepForSignificantTime();
        } else {
            PersistTestBase.sleepForSignificantTime();
            mFilters.add(f(new Date(), matchCount));
        }
    }

    @Override
    protected boolean matches(Object filter, S s) throws Exception {
        Date fieldValue = (Date) getField(s);
        Date filterValue = ((Date) filter);
        boolean isFieldValueAfter = filterValue.compareTo(fieldValue) < 0;
        //true if isFieldValueAfter is the same as mAfter.
        return !(isFieldValueAfter ^ mAfter);
    }

    @Override
    public FilterPair[] getFilters() {
        return mFilters.toArray(new FilterPair[mFilters.size()]);
    }

    private static FilterPair f(Date filter, long count) throws ValidationException {
        return new FilterPair(filter, count);
    }

    public String toString() {
        return "MultiQueryDateFilterTestHelper{" +
                "mAfter=" + mAfter +
                '}';
    }

    private static final int NUM_INSTANCES = 5;
    private boolean mAfter;
    private List<FilterPair> mFilters = new LinkedList<FilterPair>();
}