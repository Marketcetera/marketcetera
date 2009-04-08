/*
 * OrderID.java
 *
 * Created on April 18, 2005, 1:44 PM
 */

package org.marketcetera.core;


/**
 * The superclass of all identifiers in the trading library.  Does simple things
 * like implement equals and hash for hashtable usage.  Really it is just a thin 
 * wrapper around java.lang.String.
 * @author gmiller
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public class InternalID {
    final String mID;
    final Class mClass = this.getClass();
    /**
     * Create a new InternalID object with the given string representation of a unique
     * id.
     * @param internalID The string representation of the id to back this object
     */
    public InternalID(String internalID) {
        if (internalID == null) throw new IllegalArgumentException(Messages.ERROR_NULL_ID.getText());
        mID = internalID;
    }
    /**
     * Get the string representation of this InternalID
     * @return The Stirng representation of the id backing this object.
     */
    public String toString() { return mID; }
    /**
     * Thin wrapper around String.equals()
     * @param obj The object to which to compare this.
     * @return true if the given object equals this, false otherwise
     */
    public boolean equals(Object obj) {
        return (obj != null && mClass.isInstance(obj) && obj.toString().equals(mID));
    }
    /**
     * Thin wrapper around String.hashCode()
     * @return The hash code for this InternalID.
     */
    public int hashCode() {
        return mID.hashCode();
    }

}
