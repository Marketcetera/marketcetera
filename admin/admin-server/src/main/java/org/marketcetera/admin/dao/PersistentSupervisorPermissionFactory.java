package org.marketcetera.admin.dao;

import org.marketcetera.admin.SupervisorPermission;
import org.marketcetera.admin.SupervisorPermissionFactory;
import org.springframework.stereotype.Service;

/* $License$ */

/**
 * Creates {@link SupervisorPermission} objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@Service
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
    /* (non-Javadoc)
     * @see org.marketcetera.core.Factory#create()
     */
    @Override
    public PersistentSupervisorPermission create()
    {
        return new PersistentSupervisorPermission();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.Factory#create(java.lang.Object)
     */
    @Override
    public SupervisorPermission create(SupervisorPermission inSupervisorPermission)
    {
        return new PersistentSupervisorPermission(inSupervisorPermission);
    }
}
