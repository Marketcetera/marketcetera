package org.marketcetera.admin;

import java.util.Set;

import org.marketcetera.persist.SummaryNDEntityBase;

/* $License$ */

/**
 * Provides a means to assign permissions to perform various tasks.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: Role.java 84382 2015-01-20 19:43:06Z colin $
 * @since 1.0.1
 */
public interface Role
        extends SummaryNDEntityBase
{
    /**
     * Gets the permissions assigned to this role.
     *
     * @return a <code>Set&lt;Permission&gt;</code> value
     */
    Set<Permission> getPermissions();
    /**
     * Gets the subjects assigned to this role.
     *
     * @return a <code>Set&lt;User&gt;</code> value
     */
    Set<User> getSubjects();
}
