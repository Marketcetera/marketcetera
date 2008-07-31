package org.marketcetera.persist;

import org.marketcetera.core.ClassVersion;
import static org.junit.Assert.assertEquals;

/* $License$ */
/**
 * Helper class to help test multi query boolean filters.
 *
 * @author anshul@marketcetera.com
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public class MultiQueryBooleanFilterTestHelper<E extends EntityBase,
        S extends SummaryEntityBase> extends
        MultiQueryFilterTestHelper<E, S> {
    /**
     * Creates an instance.
     *
     * @param entityClass              The entity class being tested.
     * @param attributeName            The entity attribute that's being filtered
     * @param multipleEntityQueryClass The multi query class
     * @param filterName               The filter attribute name on the multi query class
     * @param query                    the query instance to test.
     * @throws Exception if there's an error.
     */
    public MultiQueryBooleanFilterTestHelper(Class<E> entityClass,
                                             String attributeName,
                                             Class<? extends MultipleEntityQuery> multipleEntityQueryClass,
                                             String filterName,
                                             MultipleEntityQuery query) throws Exception {
        super(entityClass, attributeName, multipleEntityQueryClass, filterName, query);
        assertEquals(Boolean.TYPE,fieldDesc.getPropertyType());
        assertEquals(Boolean.class,filterDesc.getPropertyType());
    }

    protected int getNumInstances() {
        return TEST_DATA.length;
    }

    protected Object generateValue(int idx) {
        return TEST_DATA[idx];
    }

    protected boolean matches(Object filter, S s) throws Exception {
        Boolean field = (Boolean) getField(s);
        Boolean fValue = (Boolean) filter;
        return field.equals(fValue);
    }

    public FilterPair[] getFilters() {
        return TEST_FILTERS;
    }

    private static final Boolean[] TEST_DATA = {
            Boolean.TRUE,
            Boolean.FALSE,
            Boolean.FALSE,
            Boolean.TRUE,
            Boolean.TRUE,
            Boolean.TRUE,
            Boolean.FALSE,
            Boolean.FALSE,
            Boolean.TRUE,
            Boolean.FALSE
    };
    private static final FilterPair[] TEST_FILTERS = {
            new FilterPair(Boolean.TRUE, 5l),
            new FilterPair(Boolean.FALSE, 5l)
    };
}
