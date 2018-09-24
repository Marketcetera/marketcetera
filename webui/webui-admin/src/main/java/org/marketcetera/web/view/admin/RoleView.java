package org.marketcetera.web.view.admin;

import java.util.Collection;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.marketcetera.admin.Permission;
import org.marketcetera.admin.Role;
import org.marketcetera.admin.User;
import org.marketcetera.admin.impl.SimpleRole;
import org.marketcetera.web.service.admin.AdminClientService;
import org.marketcetera.web.view.PagedDataContainer;

import com.vaadin.server.FontAwesome;
import com.vaadin.server.Resource;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Layout;
import com.vaadin.ui.TwinColSelect;

/* $License$ */

/**
 * Provides a view for Role CRUD.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@org.springframework.stereotype.Component
public class RoleView
        extends AbstractAdminView<Role>
{
    /* (non-Javadoc)
     * @see com.marketcetera.web.view.ContentView#getViewName()
     */
    @Override
    public String getViewName()
    {
        return NAME;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.web.view.MenuContent#getMenuCaption()
     */
    @Override
    public String getMenuCaption()
    {
        return "Roles";
    }
    /* (non-Javadoc)
     * @see com.marketcetera.web.view.MenuContent#getWeight()
     */
    @Override
    public int getWeight()
    {
        return 200;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.web.view.MenuContent#getMenuIcon()
     */
    @Override
    public Resource getMenuIcon()
    {
        return FontAwesome.GROUP;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.web.view.AbstractAdminView#getViewSubjectName()
     */
    @Override
    protected String getViewSubjectName()
    {
        return "Role";
    }
    /* (non-Javadoc)
     * @see com.marketcetera.web.view.AbstractGridView#createBeanItemContainer()
     */
    @Override
    protected PagedDataContainer<Role> createDataContainer()
    {
        return new RolePagedDataContainer(this);
    }
    /* (non-Javadoc)
     * @see com.marketcetera.web.view.AbstractGridView#onCreateNew(com.vaadin.ui.Button.ClickEvent)
     */
    @Override
    protected void onCreateNew(ClickEvent inEvent)
    {
        SimpleRole newRole = new SimpleRole();
        createOrEdit(newRole,
                     true);
    }
    /* (non-Javadoc)
     * @see com.marketcetera.web.view.admin.AbstractAdminView#onCreateOrEdit(com.vaadin.ui.Layout, org.marketcetera.persist.SummaryNDEntityBase, boolean)
     */
    @Override
    protected void onCreateOrEdit(Layout inContentLayout,
                                  Role inRole,
                                  boolean inIsNew)
    {
        permissionSelect = new TwinColSelect("Permissions");
        permissionSelect.setRows(10);
        permissionSelect.setNullSelectionAllowed(true);
        permissionSelect.setMultiSelect(true);
        permissionSelect.setLeftColumnCaption("Available Permissions");
        permissionSelect.setRightColumnCaption("Selected permissions");
        permissionSelect.setImmediate(true);
        SortedSet<DecoratedPermission> selectedPermissions = new TreeSet<>();
        for(Permission permission : inRole.getPermissions()) {
            selectedPermissions.add(new DecoratedPermission(permission));
        }
        Collection<Permission> allPermissions = AdminClientService.getInstance().getPermissions();
        SortedSet<DecoratedPermission> allDecoratedPermissions = new TreeSet<>();
        for(Permission permission : allPermissions) {
            allDecoratedPermissions.add(new DecoratedPermission(permission));
        }
        permissionSelect.addItems(allDecoratedPermissions);
        permissionSelect.setValue(selectedPermissions);
        permissionSelect.setSizeUndefined();
        userSelect = new TwinColSelect("Users");
        userSelect.setRows(10);
        userSelect.setNullSelectionAllowed(true);
        userSelect.setNewItemsAllowed(false);
        userSelect.setMultiSelect(true);
        userSelect.setLeftColumnCaption("Available Users");
        userSelect.setRightColumnCaption("Selected Users");
        userSelect.setImmediate(true);
        SortedSet<DecoratedUser> selectedUsers = new TreeSet<>();
        for(User user : inRole.getSubjects()) {
            selectedUsers.add(new DecoratedUser(user));
        }
        Collection<User> allUsers = AdminClientService.getInstance().getUsers();
        SortedSet<DecoratedUser> allDecoratedUsers = new TreeSet<>();
        for(User user : allUsers) {
            allDecoratedUsers.add(new DecoratedUser(user));
        }
        userSelect.addItems(allDecoratedUsers);
        userSelect.setValue(selectedUsers);
        userSelect.setSizeUndefined();
        inContentLayout.addComponents(permissionSelect,
                                      userSelect);
    }
    /* (non-Javadoc)
     * @see com.marketcetera.web.view.admin.AbstractAdminView#doCreate(org.marketcetera.persist.SummaryNDEntityBase)
     */
    @Override
    protected void doCreate(Role inSubject)
    {
        applyLocalUpdates(inSubject);
        AdminClientService.getInstance().createRole(inSubject);
    }
    /* (non-Javadoc)
     * @see com.marketcetera.web.view.admin.AbstractAdminView#doUpdate(java.lang.String, org.marketcetera.persist.SummaryNDEntityBase)
     */
    @Override
    protected void doUpdate(String inName,
                            Role inSubject)
    {
        applyLocalUpdates(inSubject);
        AdminClientService.getInstance().updateRole(inName,
                                                    inSubject);
    }
    /* (non-Javadoc)
     * @see com.marketcetera.web.view.admin.AbstractAdminView#doDelete(java.lang.String)
     */
    @Override
    protected void doDelete(String inName)
    {
        AdminClientService.getInstance().deleteRole(inName);
    }
    /**
     * Applies updates from the local UI controls to the given role.
     *
     * @param inRole a <code>Role</code> value
     */
    @SuppressWarnings("unchecked")
    private void applyLocalUpdates(Role inRole)
    {
        Object rawSelectedPermissions = permissionSelect.getValue();
        inRole.getPermissions().clear();
        if(rawSelectedPermissions != null) {
            Collection<DecoratedPermission> typedPermissions = (Collection<DecoratedPermission>)rawSelectedPermissions;
            for(DecoratedPermission decoratedPermission : typedPermissions) {
                inRole.getPermissions().add(decoratedPermission.permission);
            }
        }
        Object rawSelectedUsers = userSelect.getValue();
        inRole.getSubjects().clear();
        if(rawSelectedUsers != null) {
            Collection<DecoratedUser> typedUsers = (Collection<DecoratedUser>)rawSelectedUsers;
            for(DecoratedUser decoratedUser : typedUsers) {
                inRole.getSubjects().add(decoratedUser.user);
            }
        }
    }
    /**
     * Provides a <code>Permission</code> for use in the Role view.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    public static class DecoratedPermission
            implements Comparable<DecoratedPermission>
    {
        /**
         * Create a new DecoratedPermission instance.
         *
         * @param inPermission a <code>Permission</code> value
         */
        public DecoratedPermission(Permission inPermission)
        {
            permission = inPermission;
        }
        /* (non-Javadoc)
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString()
        {
            return permission.getName();
        }
        /* (non-Javadoc)
         * @see java.lang.Object#hashCode()
         */
        @Override
        public int hashCode()
        {
            return new HashCodeBuilder().append(permission.getName()).toHashCode();
        }
        /* (non-Javadoc)
         * @see java.lang.Object#equals(java.lang.Object)
         */
        @Override
        public boolean equals(Object obj)
        {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            DecoratedPermission other = (DecoratedPermission) obj;
            return new EqualsBuilder().append(other.permission.getName(),permission.getName()).isEquals();
        }
        /* (non-Javadoc)
         * @see java.lang.Comparable#compareTo(java.lang.Object)
         */
        @Override
        public int compareTo(DecoratedPermission inO)
        {
            return new CompareToBuilder().append(permission.getName(),inO.permission.getName()).toComparison();
        }
        /**
         * wrapped permission value
         */
        private final Permission permission;
    }
    /**
     * Provides a <code>User</code> for use in the Role view.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    public static class DecoratedUser
            implements Comparable<DecoratedUser>
    {
        /**
         * Create a new DecoratedUser instance.
         *
         * @param inUser a <code>User</code> value
         */
        public DecoratedUser(User inUser)
        {
            user = inUser;
        }
        /* (non-Javadoc)
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString()
        {
            return user.getName();
        }
        /* (non-Javadoc)
         * @see java.lang.Object#hashCode()
         */
        @Override
        public int hashCode()
        {
            return new HashCodeBuilder().append(user.getName()).toHashCode();
        }
        /* (non-Javadoc)
         * @see java.lang.Object#equals(java.lang.Object)
         */
        @Override
        public boolean equals(Object obj)
        {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            DecoratedUser other = (DecoratedUser) obj;
            return new EqualsBuilder().append(other.user.getName(),user.getName()).isEquals();
        }
        /* (non-Javadoc)
         * @see java.lang.Comparable#compareTo(java.lang.Object)
         */
        @Override
        public int compareTo(DecoratedUser inO)
        {
            return new CompareToBuilder().append(user.getName(),inO.user.getName()).toComparison();
        }
        /**
         * wrapped user value
         */
        private final User user;
    }
    /**
     * allows selection of permissions
     */
    private TwinColSelect permissionSelect;
    /**
     * allows selection of users
     */
    private TwinColSelect userSelect;
    /**
     * global name of this view
     */
    private static final String NAME = "RoleView";
    private static final long serialVersionUID = 1581057023135915756L;
}
