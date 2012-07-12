package org.marketcetera.orderloader.system;

import org.marketcetera.util.misc.ClassVersion;

/* $License$ */
/**
 * An object instance to encapsulate an array of columns for a row.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 2.0.0
 */
@ClassVersion("$Id$")
public class Row {
    /**
     * Creates an instance.
     *
     * @param inRow the row value.
     */
    public Row(String[] inRow) {
        mRow = inRow;
    }

    /**
     * Returns the row value.
     *
     * @return the row value.
     */
    public String[] getRow() {
        return mRow;
    }

    private final String[] mRow;
}
