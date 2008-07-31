package org.marketcetera.persist;

import org.marketcetera.core.ClassVersion;
import static org.junit.Assert.assertEquals;

import java.util.regex.Pattern;

/* $License$ */
/**
 * Helper class to help test multi query string filters.
 *
 * @author anshul@marketcetera.com
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public class MultiQueryStringFilterTestHelper<E extends EntityBase,
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
     * @throws Exception if there's an error
     */
    public MultiQueryStringFilterTestHelper(Class<E> entityClass,
                                            String attributeName,
                                            Class<? extends MultipleEntityQuery> multipleEntityQueryClass,
                                            String filterName,
                                            MultipleEntityQuery query) throws Exception {

        super(entityClass, attributeName, multipleEntityQueryClass, filterName, query);
        assertEquals(String.class,fieldDesc.getPropertyType());
        assertEquals(StringFilter.class,filterDesc.getPropertyType());
    }

    protected int getNumInstances() {
        return TEST_DATA.length;
    }

    protected Object generateValue(int idx) {
        return TEST_DATA[idx];
    }

    protected boolean matches(Object filter, S s) throws Exception {
        String str = (String) getField(s);
        String fValue = ((StringFilter) filter).getValue();
        fValue = fValue.replace(StringFilter.MATCH_ONE, '.');
        fValue = fValue.replace("" + StringFilter.MATCH_MANY, ".*"); //$NON-NLS-1$ //$NON-NLS-2$
        return Pattern.matches(fValue, str);
    }

    public FilterPair[] getFilters() {
        return TEST_FILTERS;
    }

    private static FilterPair f(String filter, long count) throws ValidationException {
        return new FilterPair(new StringFilter(filter), count);
    }

    private static final String[] TEST_DATA = {
            "aaa", //$NON-NLS-1$
            "bbbb", //$NON-NLS-1$
            "11111", //$NON-NLS-1$
            "abc", //$NON-NLS-1$
            "pbc", //$NON-NLS-1$
            "abcd", //$NON-NLS-1$
            "p1rs", //$NON-NLS-1$
            "axyed", //$NON-NLS-1$
            "a1c" //$NON-NLS-1$
    };
    private static final FilterPair[] TEST_FILTERS;

    static {
        try {
            TEST_FILTERS = new FilterPair[] {
                    f("?", 0), //$NON-NLS-1$
                    f("*", TEST_DATA.length), //$NON-NLS-1$
                    f("a??", 3), //$NON-NLS-1$
                    f("a*", 5), //$NON-NLS-1$
                    f("??c", 3), //$NON-NLS-1$
                    f("*d", 2), //$NON-NLS-1$
                    f("*1*", 3), //$NON-NLS-1$
                    f("a?c", 2), //$NON-NLS-1$
                    f("a?c", 2), //$NON-NLS-1$
                    f("x*", 0), //$NON-NLS-1$
                    f("*y", 0), //$NON-NLS-1$
                    f("*etc*", 0) //$NON-NLS-1$
            };
        } catch (ValidationException e) {
            throw new RuntimeException(e);
        }
    }
}
