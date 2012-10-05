package org.marketcetera.api.dao;

import org.marketcetera.api.systemmodel.VersionedObject;

/* $License$ */

/**
 * Provides information about the running system.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface SystemInformation
        extends VersionedObject
{
    /**
     * Provides the current schema version of the persistent store.
     *
     * @return a <code>String</code> value
     */
    public String getDatabaseSchemaVersion();
    /**
     * Provides a mutable view of this object.
     *
     * @return a <code>MutableSystemInformation</code> value
     */
    public MutableSystemInformation getMutableView();
}
