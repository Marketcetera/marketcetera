package com.marketcetera.admin;

import org.marketcetera.admin.Permission;
import org.marketcetera.admin.PermissionFactory;
import org.marketcetera.admin.Role;
import org.marketcetera.admin.RoleFactory;
import org.marketcetera.admin.User;
import org.marketcetera.admin.UserAttribute;
import org.marketcetera.admin.UserAttributeType;
import org.marketcetera.admin.UserFactory;
import org.marketcetera.rpc.base.BaseRpc;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public abstract class AdminUtil
{
    /**
     * 
     *
     *
     * @param inUser
     * @param inUserFactory
     * @return
     */
    public static User getUser(BaseRpc.User inUser,
                               UserFactory inUserFactory)
    {
        /*
//      AdminRpc.User rpcUser = response.getUser();
//      boolean isActive = false;
//      String description = null;
//      String name = null;
//      String password = "********";
//      if(rpcUser.hasActive()) {
//          isActive = rpcUser.getActive();
//      }
//      if(rpcUser.hasDescription()) {
//          description = rpcUser.getDescription();
//      }
//      if(rpcUser.hasName()) {
//          name = rpcUser.getName();
//      }
//      result = userFactory.create(name,
//                                  password,
//                                  description,
//                                  isActive);
*/
        throw new UnsupportedOperationException(); // TODO
    }
    /**
     *
     *
     * @param inNewUser
     * @return
     */
    public static BaseRpc.User getRpcUser(User inNewUser)
    {
        throw new UnsupportedOperationException(); // TODO
    }
    /**
     *
     *
     * @param inRole
     * @return
     */
    public static AdminRpc.Role getRpcRole(Role inRole)
    {
        throw new UnsupportedOperationException(); // TODO
    }
    /**
     * 
     *
     *
     * @param inRole
     * @param inRoleFactory
     * @return
     */
    public static Role getRole(AdminRpc.Role inRole,
                               RoleFactory inRoleFactory)
    {
        throw new UnsupportedOperationException(); // TODO
    }
    /**
     *
     *
     * @param inPermission
     * @return
     */
    public static AdminRpc.Permission getRpcPermission(Permission inPermission)
    {
        throw new UnsupportedOperationException(); // TODO
        
    }
    /**
     *
     *
     * @param inPermission
     * @param inPermissionFactory
     * @return
     */
    public static Permission getPermission(AdminRpc.Permission inPermission,
                                           PermissionFactory inPermissionFactory)
    {
        throw new UnsupportedOperationException(); // TODO
        
    }
    /**
     *
     *
     * @param inAttributeType
     * @return
     */
    public static AdminRpc.UserAttribute getRpcUserAttributeType(UserAttributeType inAttributeType)
    {
        throw new UnsupportedOperationException(); // TODO
        
    }
    /**
     *
     *
     * @param inUserAttribute
     * @return
     */
    public static UserAttribute getUserAttribute(AdminRpc.UserAttribute inUserAttribute)
    {
        throw new UnsupportedOperationException(); // TODO
        
    }
    /**
     *
     *
     * @param inUserAttribute
     * @return
     */
    public static com.marketcetera.admin.AdminRpc.UserAttribute getRpcUserAttribute(UserAttribute inUserAttribute)
    {
        throw new UnsupportedOperationException(); // TODO
        
    }
}
