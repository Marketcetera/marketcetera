package org.marketcetera.admin.impl;

import org.marketcetera.admin.SupervisorPermissionFactory;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class PersistentSupervisorPermissionFactory
        implements SupervisorPermissionFactory
{
    /* (non-Javadoc)
     * @see com.marketcetera.admin.SupervisorPermissionFactory#create(java.lang.String, java.lang.String)
     */
    @Override
    public PersistentSupervisorPermission create(String inName,
                                                 String inDescription)
    {
        PersistentSupervisorPermission supervisorPermission = new PersistentSupervisorPermission();
        supervisorPermission.setName(inName);
        supervisorPermission.setDescription(inDescription);
        return supervisorPermission;
    }
}
