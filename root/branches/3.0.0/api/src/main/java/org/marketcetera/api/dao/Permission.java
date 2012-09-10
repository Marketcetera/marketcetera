package org.marketcetera.api.dao;

import org.marketcetera.api.security.GrantedPermission;
import org.marketcetera.api.systemmodel.NamedObject;
import org.marketcetera.api.systemmodel.SystemObject;
import org.marketcetera.api.systemmodel.VersionedObject;

/* $License$ */

/**
 * Represents a role granted to a {@link org.marketcetera.api.security.User}.
 *
 * @version $Id$
 * @since $Release$
 */
public interface Permission
        extends GrantedPermission, SystemObject, NamedObject, VersionedObject
{
}
