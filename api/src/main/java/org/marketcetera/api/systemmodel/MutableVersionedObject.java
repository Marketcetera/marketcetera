package org.marketcetera.api.systemmodel;

/* $License$ */

/**
 * Provides a mutable view of a <code>VersionedObject</code> value.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface MutableVersionedObject
        extends VersionedObject
{
    /**
     * Sets the version value.
     *
     * @param inVersion an <code>int</code> value
     */
    void setVersion(int inVersion);
}
