package org.marketcetera.admin.dao;

import org.marketcetera.admin.MutablePermission;
import org.marketcetera.admin.MutablePermissionFactory;
import org.springframework.stereotype.Service;

/* $License$ */

/**
 * Creates {@link PersistentPermission} objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: PersistentPermissionFactory.java 84382 2015-01-20 19:43:06Z colin $
 * @since 1.0.1
 */
@Service
public class PersistentPermissionFactory
        implements MutablePermissionFactory
{
    /* (non-Javadoc)
     * @see com.marketcetera.tiaacref.systemmodel.PermissionFactory#create(java.lang.String, java.lang.String)
     */
    @Override
    public PersistentPermission create(String inName,
                                       String inDescription)
    {
        PersistentPermission permission = new PersistentPermission();
        permission.setName(inName);
        permission.setDescription(inDescription);
        return permission;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.admin.MutablePermissionFactory#create()
     */
    @Override
    public MutablePermission create()
    {
        return new PersistentPermission();
    }
}
