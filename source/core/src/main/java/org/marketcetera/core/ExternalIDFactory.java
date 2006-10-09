package org.marketcetera.core;

import java.sql.SQLException;

/**
 * @author Graham Miller
 * @version $Id$
 */
@ClassVersion("$Id$")
public abstract class ExternalIDFactory implements IDFactory {
    private int mUpTo = 0;
    private int mNextID = 0;

    /**
     *
     * @return the next allowed id
     */
    protected abstract int grabIDs() throws NoMoreIDsException;

    protected void setMaxAllowedID(int newVal)
    {
        mUpTo = newVal;
    }

    public String getNext() throws NoMoreIDsException
    {
        synchronized (this) {
            if (mNextID >= mUpTo) {
                int numAllowed = grabIDs();
            }
            return "" + (mNextID++);
        }
    }

    protected int getNextAsInt()
    {
        return mNextID;
    }
}
