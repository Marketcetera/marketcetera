package org.marketcetera.admin;

import java.util.Set;

import org.marketcetera.persist.SummaryNDEntityBase;

/* $License$ */

/**
 * Describes permissions granted to a user over other users.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface SupervisorPermission
        extends SummaryNDEntityBase
{
    /**
     * Get the supervisor value.
     *
     * @return a <code>User</code> value
     */
    User getSupervisor();
    /**
     * Set the supervisor value.
     *
     * @param inSupervisor a <code>User</code> value
     */
    void setSupervisor(User inSupervisor);
    /**
     * Get the permissions granted to the supervisor.
     *
     * @return a <code>Set&lt;Permission&gt;</code> value
     */
    Set<Permission> getPermissions();
    /**
     * Get the subjects over whom the supervision is granted.
     *
     * @return a <code>Set&lt;User&gt;</code> value
     */
    Set<User> getSubjects();
}
