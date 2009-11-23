package org.marketcetera.persist;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.util.log.I18NBoundMessage2P;
import org.marketcetera.util.log.I18NBoundMessage;
import static org.marketcetera.persist.Messages.*;

import java.sql.Clob;
import java.sql.Blob;

/* $License$ */
/**
 * Vendor specific utilities for persistence. Provides an
 * abstraction to use vendor specific features, without
 * having an explicit dependency on vendor's code 
 *
 * @author anshul@marketcetera.com
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public class VendorUtils {
    private VendorUtils() {}

    /**
     * Creates an empty Clob instance to help persist Clob
     * data type.
     *
     * @return empty clob instance
     *
     * @throws PersistenceException If there's an error
     * initializing the clob
     */
    public static Clob initClob() throws PersistenceException {
        return getJPAVendor().initClob();
    }

    /**
     * Creates an empty Blob instance to help persist Blob
     * data type.
     *
     * @return an empty blob instance.
     *
     * @throws PersistenceException If there's an error
     * initializing the blob
     */
    public static Blob initBlob() throws PersistenceException {
        return getJPAVendor().initBlob();
    }

    /**
     * Validates that the supplied text is supported by the underlying database
     *
     * @param s the string that needs to be validated
     *
     * @throws ValidationException if the supplied string includes characters
     * that cannot be saved into the database
     * @throws PersistSetupException if the DB vendor is not setup 
     */
    public static void validateText(CharSequence s) throws PersistenceException {
        getDBVendor().validateText(s);
    }

    /**
     * Returns the internationalized user-friendly message for the
     * supplied persistence exception.
     *
     * @param exception the exception for which a user-friendly message is
     * desired.
     *
     * @return the internationalized user-friendly message.
     *
     * @throws PersistSetupException if the JPA vendor is not configured.
     */
    static I18NBoundMessage getEntityExistsMessage(
            javax.persistence.EntityExistsException exception)
            throws PersistSetupException {
        return getJPAVendor().getEntityExistsMessage(exception);
    }

    /**
     * Gets the current JPA vendor implementation.
     *
     * @return the current jpa vendor implementation.
     *
     * @throws PersistSetupException if a vendor is
     * not configured
     */
    private static JPAVendor getJPAVendor() throws PersistSetupException {
        if(mJPAVendor == null) {
            throw new PersistSetupException(JPA_VENDOR_NOT_INITIALIZED);
        }
        return mJPAVendor;
    }
    /**
     * Gets the current DB vendor implementation.
     *
     * @return the current db vendor implementation.
     *
     * @throws PersistSetupException if a vendor is
     * not configured
     */
    private static DBVendor getDBVendor() throws PersistSetupException {
        if(mDBVendor == null) {
            throw new PersistSetupException(DB_VENDOR_NOT_INITIALIZED);
        }
        return mDBVendor;
    }

    /**
     * Sets the current JPA vendor implementation. A vendor is automatically
     * registered when an instance of
     * {@link org.marketcetera.persist.JPAVendor} is instantiated.
     *
     * @param v the current JPA vendor implementation
     * 
     * @throws PersistSetupException if a vendor is already configured
     */
    static void setJPAVendor(JPAVendor v) throws PersistSetupException {
        if(mJPAVendor != null) {
            throw new PersistSetupException(mJPAInitStackTrace,new I18NBoundMessage2P(
                    JPA_VENDOR_ALREADY_INITIALIZED,
                    v.getClass().getName(),
                    mJPAVendor.getClass().getName()));
        }
        mJPAVendor = v;
        mJPAInitStackTrace = new Exception("Original Initialization"); //$NON-NLS-1$

    }
    /**
     * Sets the current DB vendor implementation. A vendor is automatically
     * registered when an instance of
     * {@link org.marketcetera.persist.DBVendor} is instantiated.
     *
     * @param v the current DB vendor implementation
     *
     * @throws PersistSetupException if a vendor is already configured
     */
    static void setDBVendor(DBVendor v) throws PersistSetupException {
        if(mDBVendor != null) {
            throw new PersistSetupException(mDBInitStackTrace,new I18NBoundMessage2P(
                    DB_VENDOR_ALREADY_INITIALIZED,
                    v.getClass().getName(),
                    mDBVendor.getClass().getName()));
        }
        mDBVendor = v;
        mDBInitStackTrace = new Exception("Original Initialization"); //$NON-NLS-1$

    }
    private static JPAVendor mJPAVendor;
    private static DBVendor mDBVendor;
    /**
     * This field is there to aid debugging when multiple initializations
     * of the singleton are discovered
     */
    private static Exception mJPAInitStackTrace;
    /**
     * This field is there to aid debugging when multiple initializations
     * of the singleton are discovered
     */
    private static Exception mDBInitStackTrace;

}
