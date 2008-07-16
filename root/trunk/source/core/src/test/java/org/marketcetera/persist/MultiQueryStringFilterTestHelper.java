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
@ClassVersion("$Id$")
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
        fValue = fValue.replace("" + StringFilter.MATCH_MANY, ".*");
        return Pattern.matches(fValue, str);
    }

    public FilterPair[] getFilters() {
        return TEST_FILTERS;
    }

    private static FilterPair f(String filter, long count) throws ValidationException {
        return new FilterPair(new StringFilter(filter), count);
    }

    private static final String[] TEST_DATA = {
            "aaa",
            "bbbb",
            "11111",
            "abc",
            "pbc",
            "abcd",
            "p1rs",
            "axyed",
            "a1c"
    };
    private static final FilterPair[] TEST_FILTERS;

    static {
        try {
            TEST_FILTERS = new FilterPair[] {
                    f("?", 0),
                    f("*", TEST_DATA.length),
                    f("a??", 3),
                    f("a*", 5),
                    f("??c", 3),
                    f("*d", 2),
                    f("*1*", 3),
                    f("a?c", 2),
                    f("a?c", 2),
                    f("x*", 0),
                    f("*y", 0),
                    f("*etc*", 0)
            };
        } catch (ValidationException e) {
            throw new RuntimeException(e);
        }
    }
}
