package org.marketcetera.admin;

import org.springframework.security.core.GrantedAuthority;

/* $License$ */

/**
 * Defines Admin permission names.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public enum AdminPermissions
        implements GrantedAuthority
{
    AddSessionAction,
    DeleteSessionAction,
    DisableSessionAction,
    EditSessionAction,
    EnableSessionAction,
    StartSessionAction,
    StopSessionAction,
    ViewSessionAction,
    UpdateSequenceAction,
    CreateUserAction,
    ChangeUserPasswordAction,
    ReadUserAction,
    UpdateUserAction,
    DeleteUserAction,
    ReadUserPermisionsAction,
    CreatePermissionAction,
    ReadPermissionAction,
    UpdatePermissionAction,
    DeletePermissionAction,
    CreateRoleAction,
    ReadRoleAction,
    UpdateRoleAction,
    DeleteRoleAction,
    ReadInstanceDataAction,
    ReadFixSessionAttributeDescriptorsAction,
    ReadUserAttributeAction,
    ResetUserPasswordAction,
    WriteUserAttributeAction;
    /* (non-Javadoc)
     * @see org.springframework.security.core.GrantedAuthority#getAuthority()
     */
    @Override
    public String getAuthority()
    {
        return name();
    }
}
