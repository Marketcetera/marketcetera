package org.marketcetera.persist;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.util.log.I18NBoundMessage;

import java.sql.Blob;
import java.sql.Clob;

/* $License$ */
/**
 * An interface that abstracts out functions that are not
 * defined by the JPA interface and hence are JPA vendor
 * implementation dependent
 *
 * Only one instance of this class may be
 * instantiated at a time. An attempt to initialize multiple
 * instances will result in failure.
 *
 * @author anshul@marketcetera.com
 */
@ClassVersion("$Id$") //$NON-NLS-1$
abstract class JPAVendor {

    /**
     * Creates an instance
     *
     * @throws PersistSetupException if there's an error
     */
    protected JPAVendor() throws PersistSetupException {
        VendorUtils.setJPAVendor(this);
    }

    /**
     * Creates an empty Blob instance
     *
     * @return an empty Blob instance
     *
     * @throws PersistenceException if there's an error
     * initialing the blob
     */
    public abstract Blob initBlob() throws PersistenceException;

    /**
     * Creates an empty Clob instance
     *
     * @return and empty Clob instance
     * 
     * @throws PersistenceException if there's an error
     * initialing the clob
     */
    public abstract Clob initClob() throws PersistenceException;

    /**
     * Returns the internationalized user-friendly message for the
     * supplied persistence exception.
     *
     * @param exception the JPA exception
     *
     * @return the internationalized user-friendly message for the supplied
     * exception.
     */
    public abstract I18NBoundMessage getEntityExistsMessage(
            javax.persistence.EntityExistsException exception);
}
