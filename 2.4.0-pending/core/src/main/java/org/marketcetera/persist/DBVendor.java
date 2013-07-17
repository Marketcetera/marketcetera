package org.marketcetera.persist;

import org.marketcetera.core.ClassVersion;

/* $License$ */
/**
 * This class abstracts out infrastructure functionality thats
 * dependent on the particular implementation of database chosen to
 * persist data.
 *
 * @author anshul@marketcetera.com
 */
@ClassVersion("$Id$") //$NON-NLS-1$
abstract class DBVendor {
    /**
     * Creates an instance
     *
     * @throws PersistSetupException if an instance already exists
     */
    protected DBVendor() throws PersistSetupException {
        VendorUtils.setDBVendor(this);
    }
    /**
     * Validates that the supplied text is supported by the underlying database
     *
     * @param s the string that needs to be validated
     *
     * @throws ValidationException if the supplied string includes characters
     * that cannot be saved into the database
     */
    abstract void validateText(CharSequence s) throws ValidationException;
}
