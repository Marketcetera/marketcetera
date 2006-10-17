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
     * This method is responsible for grabbing another set of
     * allowed ID's, calling both setMaxAllowedID, and setNextID.
     *
     *
     */
    protected abstract void grabIDs() throws NoMoreIDsException;

    protected void setMaxAllowedID(int newVal)
    {
        mUpTo = newVal;
    }

    protected void setNextID(int nextID)
    {
    	mNextID = nextID;
	}

    public String getNext() throws NoMoreIDsException
    {
        synchronized (this) {
            if (mNextID >= mUpTo) {
                grabIDs();
            }
            return "" + (mNextID++);
        }
    }

    protected int getNextAsInt()
    {
        return mNextID;
    }
}
