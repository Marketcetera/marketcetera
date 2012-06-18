package org.marketcetera.core;

import org.marketcetera.util.misc.ClassVersion;

/**
 * Interface for a factory object that supplies unique identifiers as strings.
 * Note implementors of this interface must guarantee that getNext is threadsafe.
 * @author gmiller
 * $Id: IDFactory.java 16063 2012-01-31 18:21:55Z colin $
 */
@ClassVersion("$Id: IDFactory.java 16063 2012-01-31 18:21:55Z colin $")
public interface IDFactory {
    /**
     * Gets the next unique identifier as a string.  Implementors of this method must
     * guarantee that it is threadsafe.
     * @throws NoMoreIDsException When there are no more exceptions to get from the DB
     * @return the next unique identifier as a string
     */
    public String getNext() throws NoMoreIDsException;

    /** Do your init thing, you factory, you! */
    public void init() throws ClassNotFoundException, NoMoreIDsException ;
}
