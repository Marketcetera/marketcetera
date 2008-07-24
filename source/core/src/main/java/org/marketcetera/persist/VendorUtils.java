package org.marketcetera.persist;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.util.log.I18NBoundMessage2P;
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
@ClassVersion("$Id$")
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
        return getVendor().initClob();
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
        return getVendor().initBlob();
    }

    /**
     * Gets the current JPA vendor implementation.
     *
     * @return the current jpa vendor implementation.
     *
     * @throws PersistSetupException if a vendor is
     * not configured
     */
    private static JPAVendor getVendor() throws PersistSetupException {
        if(vendor == null) {
            throw new PersistSetupException(JPA_VENDOR_NOT_INITIALIZED);
        }
        return vendor;
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
    static void setVendor(JPAVendor v) throws PersistSetupException {
        if(vendor != null) {
            throw new PersistSetupException(initStackTrace,new I18NBoundMessage2P(
                    JPA_VENDOR_ALREADY_INITIALIZED,
                    v.getClass().getName(),
                    vendor.getClass().getName()));
        }
        vendor = v;
        initStackTrace = new Exception("Original Initialization");

    }
    private static JPAVendor vendor;
    /**
     * This field is there to aid debugging when multiple initializations
     * of the singleton are discovered
     */
    private static Exception initStackTrace;

}
