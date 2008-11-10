package org.marketcetera.core;

import org.marketcetera.util.log.I18NBoundMessage0P;

/**
 * Implementation of IDFactory that provides identifiers unique to this run of the
 * Java VM.  They are simply the string representation of a counter that is incremented
 * every time getNext is called.
 * @author gmiller
 * $Id$
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public class InMemoryIDFactory implements IDFactory {

    long mNextID = 0;
    String prefix;
    /**
     * Creates a new instance of InMemoryOrderIDFactory, with the given starting
     * number.
     * @param startAt the value at which to start the unique identifiers.
     */
    public InMemoryIDFactory(long startAt) {
	this(startAt, ""); //$NON-NLS-1$
    }

    /**
     * Creates a new instance of InMemoryOrderIDFactory, with the given starting
     * number, and a prefix for the identifiers
     * @param startAt the value at which to start the unique identifiers.
     */
    public InMemoryIDFactory(long startAt, String suffix) {
        mNextID = startAt;
	this.prefix = suffix;
    }

    /**
     * Returns the next unique identifier as a string
     * @return the next unique identifier
     * @throws NoMoreIDsException 
     */
    public String getNext() throws NoMoreIDsException {
        long retVal = 0;
        synchronized (this){
            retVal = mNextID++;
        }
        if (retVal == Long.MAX_VALUE){
        	throw new NoMoreIDsException(Messages.ERROR_IN_MEMORY_ID_FACTORY_OVERRUN);
        }
        return prefix+retVal;
    }


    public void init() {
        // no-op
    }
}
