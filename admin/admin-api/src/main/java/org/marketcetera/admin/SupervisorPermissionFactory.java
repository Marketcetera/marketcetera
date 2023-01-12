package org.marketcetera.admin;

import org.marketcetera.core.Factory;

/* $License$ */

/**
 * Creates {@link SupervisorPermission} objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface SupervisorPermissionFactory
        extends Factory<SupervisorPermission>
{
    /**
     * Create a new <code>SupervisorPermission</code> value.
     *
     * @param inName a <code>String</code> value
     * @param inDescription a <code>String</code> value
     * @return a <code>SupervisorPermission</code> value
     */
    SupervisorPermission create(String inName,
                                String inDescription);
}
