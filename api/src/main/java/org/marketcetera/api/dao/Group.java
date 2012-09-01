package org.marketcetera.api.dao;

import java.util.Collection;

import org.marketcetera.api.security.User;
import org.marketcetera.api.systemmodel.NamedObject;
import org.marketcetera.api.systemmodel.SystemObject;
import org.marketcetera.api.systemmodel.VersionedObject;

/* $License$ */

/**
 * Represents a grouping of {@link org.marketcetera.api.security.User} and {@link Authority} objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: Group.java 82316 2012-03-21 21:13:27Z colin $
 * @since $Release$
 */
public interface Group
        extends SystemObject, VersionedObject, NamedObject
{
    /**
     * Gets the users in this group.
     *
     * @return a <code>Collection&lt;User&gt;</code> value
     */
    public Collection<User> getUsers();
    /**
     * Gets the authorities assigned to this group.
     *
     * @return a <code>Collection&lt;Authority&gt;</code> value
     */
    public Collection<Authority> getAuthorities();
}
