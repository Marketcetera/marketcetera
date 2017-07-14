package org.marketcetera.admin.impl;

import org.marketcetera.admin.Permission;
import org.marketcetera.admin.PermissionFactory;

/* $License$ */

/**
 * Creates simple permission objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class SimplePermissionFactory
        implements PermissionFactory
{
    /* (non-Javadoc)
     * @see com.marketcetera.admin.PermissionFactory#create(java.lang.String, java.lang.String)
     */
    @Override
    public Permission create(String inName,
                             String inDescription)
    {
        return new SimplePermission(inName,
                                    inDescription);
    }
}
