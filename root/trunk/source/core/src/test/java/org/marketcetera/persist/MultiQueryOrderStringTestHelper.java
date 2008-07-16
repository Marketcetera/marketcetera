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
@ClassVersion("$Id$")
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
            throw new IllegalArgumentException(idx + ">" + getNumInstances());
        }
        return STRINGS[idx / STRINGS.length] +
                STRINGS[idx % STRINGS.length] +
                PersistTestBase.randomString();
    }

    private static final String[] STRINGS = {
            "2", "A", "B", "1", "á", "Ä", "b", "ä", "a"
    };
}
