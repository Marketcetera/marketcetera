package org.marketcetera.orderloader;

import org.marketcetera.util.misc.ClassVersion;

/* $License$ */
/**
 * Contains failure information encountered when processing an order. 
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$")
public class FailedOrderInfo {
    /**
     * Creates an instance.
     *
     * @param inIndex the row index at which the failure happened.
     * @param inRow the contents of the row at which the failure happened.
     * @param inException the exception indicating the failure.
     */
    public FailedOrderInfo(int inIndex, String[] inRow, Exception inException) {
        mIndex = inIndex;
        mRow = inRow;
        mException = inException;
    }

    /**
     * The row index at which the failure happened.
     *
     * @return the row index at which the failure happened.
     */
    public int getIndex() {
        return mIndex;
    }

    /**
     * The contents of the row at which the failure happened.
     *
     * @return the contents of the row at which the failure happened.
     */
    public String[] getRow() {
        return mRow;
    }

    /**
     * The exception indicating the failure.
     *
     * @return the exception indicating the failure.
     */
    public Exception getException() {
        return mException;
    }

    private final int mIndex;
    private final String[] mRow;
    private final Exception mException;
}
