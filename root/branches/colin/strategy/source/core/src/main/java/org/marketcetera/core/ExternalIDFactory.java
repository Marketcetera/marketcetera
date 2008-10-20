package org.marketcetera.core;

/**
 * @author Graham Miller
 * @version $Id$
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public abstract class ExternalIDFactory implements IDFactory {
    private int mUpTo = 0;
    private int mNextID = 0;
    private String prefix = "";  //$NON-NLS-1$

    protected ExternalIDFactory(String prefix)
    {
        this.prefix = prefix;
    }

    /**
     * This method is responsible for grabbing another set of
     * allowed ID's, calling both setMaxAllowedID, and setNextID.
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
            return prefix + (mNextID++);
        }
    }

    /** Returns the value of the next ID, but does not increment the id - this is more of a peek */
    protected int peekNextAsInt()
    {
        return mNextID;
    }
}
