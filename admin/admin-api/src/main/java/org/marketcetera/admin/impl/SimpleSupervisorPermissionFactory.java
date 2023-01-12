package org.marketcetera.admin.impl;

import org.marketcetera.admin.SupervisorPermission;
import org.marketcetera.admin.SupervisorPermissionFactory;

/* $License$ */

/**
 * Creates simple permission objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class SimpleSupervisorPermissionFactory
        implements SupervisorPermissionFactory
{
    /* (non-Javadoc)
     * @see org.marketcetera.core.Factory#create()
     */
    @Override
    public SimpleSupervisorPermission create()
    {
        return new SimpleSupervisorPermission();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.Factory#create(java.lang.Object)
     */
    @Override
    public SimpleSupervisorPermission create(SupervisorPermission inSupervisorPermission)
    {
        return new SimpleSupervisorPermission(inSupervisorPermission);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.admin.SupervisorPermissionFactory#create(java.lang.String, java.lang.String)
     */
    @Override
    public SimpleSupervisorPermission create(String inName,
                                             String inDescription)
    {
        SimpleSupervisorPermission supervisorPermission = create();
        supervisorPermission.setName(inName);
        supervisorPermission.setDescription(inDescription);
        return supervisorPermission;
    }
}
