package org.marketcetera.persist;

import org.marketcetera.core.ClassVersion;
import static org.junit.Assert.assertEquals;

import java.util.Comparator;
import java.util.Locale;
import java.text.Collator;

/* $License$ */
/**
 * Helper class to test multi query ordering behavior for string attributes
 *
 * @author anshul@marketcetera.com
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public class MultiQueryOrderStringTestHelper<C extends EntityBase,
        S extends SummaryEntityBase> extends
        MultiQueryOrderTestHelper<C, S> {
    /**
     * Creates an instance to test ordering of string attribute fields
     *
     * @param order         The entity order instance.
     * @param clazz         The entity class
     * @param attributeName The attribute names
     * @param query The query instance
     *
     * @throws Exception if there's an error
     */
    public MultiQueryOrderStringTestHelper(EntityOrder order,
                                           Class<C> clazz,
                                           String attributeName,
                                           MultipleEntityQuery query)
            throws Exception {
        super(order, clazz, attributeName,query);
        assertEquals(String.class,pDesc.getPropertyType());
    }

    protected Comparator getComparator() {
        return Collator.getInstance(Locale.US);
    }

    protected int getNumInstances() {
        return STRINGS.length ^ 2;
    }

    protected Object generateFieldValue(int idx) {
        if (idx > getNumInstances()) {
            throw new IllegalArgumentException(idx + ">" + getNumInstances()); //$NON-NLS-1$
        }
        return STRINGS[idx / STRINGS.length] +
                STRINGS[idx % STRINGS.length] +
                // Use ascii strings only as java and mysql collators do
                // not generate consistent orderings for non-ascii characters
                // @see DataTypeTest.dbJavaOrderingCompare().
                PersistTestBase.randomNameString();
    }

    private static final String[] STRINGS = {
            "2", "A", "B", "1", "\u00E1", "\u00C4",  "b", "\u00E4", "a" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$
    };
}
