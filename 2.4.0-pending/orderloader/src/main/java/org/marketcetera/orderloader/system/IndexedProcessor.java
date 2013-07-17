package org.marketcetera.orderloader.system;

import org.marketcetera.util.misc.ClassVersion;

/**
 * A processor that extracts a value from the specified column index in
 * an order row.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$")
abstract class IndexedProcessor implements FieldProcessor {
    /**
     * Creates an instance.
     *
     * @param inIndex the index at which the field processed by this processor
     * exists.
     */
    protected IndexedProcessor(int inIndex) {
        mIndex = inIndex;
    }

    /**
     * The field value from the supplied order row.
     *
     * @param inRow the order row.
     *
     * @return the field value from the supplied order row.
     */
    protected final String getValue(String[] inRow) {
        return inRow[mIndex];
    }

    private final int mIndex;
}
