package org.marketcetera.security;

import java.util.Collection;
import java.util.List;

import javax.annotation.concurrent.Immutable;

import org.marketcetera.api.dao.Permission;
import org.marketcetera.api.dao.PermissionAttribute;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

/* $License$ */

/**
 * Caches and interprets permissions for specific functions and permission levels.
 * 
 * <p>This class is intended to be used as a permissions cache for a specific user. Once
 * instantiated, the permission cache cannot change. If the cache is invalidated, discard
 * this object and construct a new one.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@Immutable
public class PermissionCache
{
    /**
     * Create a new PermissionResolver instance.
     *
     * @param inPermissions a <code>List&lt;Permission&gt;</code> value
     */
    public PermissionCache(List<Permission> inPermissions)
    {
        mappedPermissions = HashMultimap.create();
        for(Permission permission : inPermissions) {
            mappedPermissions.putAll(permission.getPermission(),
                                     permission.getMethod());
        }
    }
    /**
     * Indicates if the permission cache allows create permission for the given permission topic.
     *
     * @param inPermission a <code>String</code> value
     * @return a <code>boolean</code> value
     */
    public boolean hasCreatePermission(String inPermission)
    {
        return doPermissionCheck(inPermission,
                                 PermissionAttribute.Create);
    }
    /**
     * Indicates if the permission cache allows read permission for the given permission topic.
     *
     * @param inPermission a <code>String</code> value
     * @return a <code>boolean</code> value
     */
    public boolean hasReadPermission(String inPermission)
    {
        return doPermissionCheck(inPermission,
                                 PermissionAttribute.Read);
    }
    /**
     * Indicates if the permission cache allows update permission for the given permission topic.
     *
     * @param inPermission a <code>String</code> value
     * @return a <code>boolean</code> value
     */
    public boolean hasUpdatePermission(String inPermission)
    {
        return doPermissionCheck(inPermission,
                                 PermissionAttribute.Update);
    }
    /**
     * Indicates if the permission cache allows delete permission for the given permission topic.
     *
     * @param inPermission a <code>String</code> value
     * @return a <code>boolean</code> value
     */
    public boolean hasDeletePermission(String inPermission)
    {
        return doPermissionCheck(inPermission,
                                 PermissionAttribute.Delete);
    }
    /**
     * Determines if the permissions cache allows permission for the given permission level and permission topic.
     *
     * @param inPermission a <code>String</code> value
     * @param inAttribute a <code>PermissionAttribute</code> value
     * @return a <code>boolean</code> value
     */
    private boolean doPermissionCheck(String inPermission,
                                      PermissionAttribute inAttribute)
    {
        Collection<PermissionAttribute> permissionAttributes = mappedPermissions.get(inPermission);
        if(permissionAttributes == null) {
            return false;
        }
        return permissionAttributes.contains(inAttribute);
    }
    /**
     * permissions cache - do not modify this collection
     */
    private final Multimap<String,PermissionAttribute> mappedPermissions;
}
