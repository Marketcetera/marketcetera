package org.marketcetera.core.systemmodel;

import java.util.Collection;

import org.marketcetera.api.security.User;
import org.marketcetera.api.systemmodel.MutableNamedObject;

/* $License$ */

/**
 * Provides a mutable view of a <code>Group</code> object.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface MutableGroup
        extends Group, MutableNamedObject
{
    /**
     * Set the users value. 
     *
     * @param inUsers a <code>Collection&lt;User&gt;</code> value
     */
    public void setUsers(Collection<User> inUsers);
    /**
     * Set the authorities value.
     *
     * @param inAuthorities a <code>Collection&lt;Authority&gt;</code> value
     */
    public void setAuthorities(Collection<Authority> inAuthorities);
}
