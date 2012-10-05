package org.marketcetera.api.dao;

/* $License$ */

/**
 * Provides a mutable view of a <code>SystemInformation</code> value.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface MutableSystemInformation
        extends SystemInformation
{
    /**
     * Sets the database schema version.
     *
     * @param inValue a <code>String</code> value
     */
    public void setDatabaseSchemaVersion(String inValue);
}
