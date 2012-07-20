package org.marketcetera.systemmodel;

import java.util.Collection;

import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Represents a grouping of {@link User} and {@link Authority} objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: Group.java 82384 2012-07-20 19:09:59Z colin $
 * @since $Release$
 */
@ClassVersion("$Id: Group.java 82384 2012-07-20 19:09:59Z colin $")
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
