package org.marketcetera.admin.provisioning;

import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/* $License$ */

/**
 * Enables user configuration from properties.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@Configuration
@EnableAutoConfiguration
@ConfigurationProperties("admin")
public class AdminConfiguration
{
    /**
     * Validate and start the object. 
     */
    @PostConstruct
    public void start()
    {
        SLF4JLoggerProxy.debug(this,
                               "Provisioning users: {} permissions: {} roles: {} supervisor permissions: {}",
                               users,
                               permissions,
                               roles,
                               supervisorPermissions);
    }
    /**
     * Get the users value.
     *
     * @return a <code>List&lt;User&gt;</code> value
     */
    public List<User> getUsers()
    {
        return users;
    }
    /**
     * Sets the users value.
     *
     * @param inUsers a <code>List&lt;User&gt;</code> value
     */
    public void setUsers(List<User> inUsers)
    {
        users = inUsers;
    }
    /**
     * Get the roles value.
     *
     * @return a <code>List&lt;Role&gt;</code> value
     */
    public List<Role> getRoles()
    {
        return roles;
    }
    /**
     * Sets the roles value.
     *
     * @param inRoles a <code>List&lt;Role&gt;</code> value
     */
    public void setRoles(List<Role> inRoles)
    {
        roles = inRoles;
    }
    /**
     * Get the permissions value.
     *
     * @return a <code>List&lt;Permission&gt;</code> value
     */
    public List<Permission> getPermissions()
    {
        return permissions;
    }
    /**
     * Sets the permissions value.
     *
     * @param inPermissions a <code>List&lt;Permission&gt;</code> value
     */
    public void setPermissions(List<Permission> inPermissions)
    {
        permissions = inPermissions;
    }
    /**
     * Get the supervisorPermissions value.
     *
     * @return a <code>List&lt;SupervisorPermission&gt;</code> value
     */
    public List<SupervisorPermission> getSupervisorPermissions()
    {
        return supervisorPermissions;
    }
    /**
     * Sets the supervisorPermissions value.
     *
     * @param inSupervisorPermissions a <code>List&lt;SupervisorPermission&gt;</code> value
     */
    public void setSupervisorPermissions(List<SupervisorPermission> inSupervisorPermissions)
    {
        supervisorPermissions = inSupervisorPermissions;
    }
    /**
     * Describes a permission.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    public static class Permission
    {
        /* (non-Javadoc)
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString()
        {
            StringBuilder builder = new StringBuilder();
            builder.append("Permission [name=").append(name).append(", description=").append(description).append("]");
            return builder.toString();
        }
        /**
         * Get the name value.
         *
         * @return a <code>String</code> value
         */
        public String getName()
        {
            return name;
        }
        /**
         * Sets the name value.
         *
         * @param inName a <code>String</code> value
         */
        public void setName(String inName)
        {
            name = inName;
        }
        /**
         * Get the description value.
         *
         * @return a <code>String</code> value
         */
        public String getDescription()
        {
            return description;
        }
        /**
         * Sets the description value.
         *
         * @param inDescription a <code>String</code> value
         */
        public void setDescription(String inDescription)
        {
            description = inDescription;
        }
        /**
         * name value
         */
        private String name;
        /**
         * description value
         */
        private String description;
    }
    /**
     * Describes a role.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    public static class Role
    {
        /* (non-Javadoc)
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString()
        {
            StringBuilder builder = new StringBuilder();
            builder.append("Role [name=").append(name).append(", description=").append(description).append(", users=")
                    .append(users).append(", permissions=").append(permissions).append("]");
            return builder.toString();
        }
        /**
         * Get the name value.
         *
         * @return a <code>String</code> value
         */
        public String getName()
        {
            return name;
        }
        /**
         * Sets the name value.
         *
         * @param inName a <code>String</code> value
         */
        public void setName(String inName)
        {
            name = inName;
        }
        /**
         * Get the description value.
         *
         * @return a <code>String</code> value
         */
        public String getDescription()
        {
            return description;
        }
        /**
         * Sets the description value.
         *
         * @param inDescription a <code>String</code> value
         */
        public void setDescription(String inDescription)
        {
            description = inDescription;
        }
        /**
         * Get the users value.
         *
         * @return a <code>Set&lt;String&gt;</code> value
         */
        public Set<String> getUsers()
        {
            return users;
        }
        /**
         * Sets the users value.
         *
         * @param inUsers a <code>Set&lt;String&gt;</code> value
         */
        public void setUsers(Set<String> inUsers)
        {
            users = inUsers;
        }
        /**
         * Get the permissions value.
         *
         * @return a <code>Set&lt;String&gt;</code> value
         */
        public Set<String> getPermissions()
        {
            return permissions;
        }
        /**
         * Sets the permissions value.
         *
         * @param inPermissions a <code>Set&lt;String&gt;</code> value
         */
        public void setPermissions(Set<String> inPermissions)
        {
            permissions = inPermissions;
        }
        /**
         * name value
         */
        private String name;
        /**
         * description value
         */
        private String description;
        /**
         * users value
         */
        private Set<String> users = Sets.newHashSet();
        /**
         * permissions value
         */
        private Set<String> permissions = Sets.newHashSet();
    }
    /**
     * Describes a user.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    public static class User
    {
        /* (non-Javadoc)
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString()
        {
            StringBuilder builder = new StringBuilder();
            builder.append("User [name=").append(name).append(", description=").append(description)
                    .append(", isActive=").append(isActive).append("]");
            return builder.toString();
        }
        /**
         * Get the name value.
         *
         * @return a <code>String</code> value
         */
        public String getName()
        {
            return name;
        }
        /**
         * Sets the name value.
         *
         * @param inName a <code>String</code> value
         */
        public void setName(String inName)
        {
            name = inName;
        }
        /**
         * Get the description value.
         *
         * @return a <code>String</code> value
         */
        public String getDescription()
        {
            return description;
        }
        /**
         * Sets the description value.
         *
         * @param inDescription a <code>String</code> value
         */
        public void setDescription(String inDescription)
        {
            description = inDescription;
        }
        /**
         * Get the password value.
         *
         * @return a <code>String</code> value
         */
        public String getPassword()
        {
            return password;
        }
        /**
         * Sets the password value.
         *
         * @param inPassword a <code>String</code> value
         */
        public void setPassword(String inPassword)
        {
            password = inPassword;
        }
        /**
         * Get the isActive value.
         *
         * @return a <code>boolean</code> value
         */
        public boolean getIsActive()
        {
            return isActive;
        }
        /**
         * Sets the isActive value.
         *
         * @param inIsActive a <code>boolean</code> value
         */
        public void setIsActive(boolean inIsActive)
        {
            isActive = inIsActive;
        }
        /**
         * name value
         */
        private String name;
        /**
         * description value
         */
        private String description;
        /**
         * password value
         */
        private String password;
        /**
         * is active value
         */
        private boolean isActive = true;
    }
    /**
     * Defines a supervisor permission.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    public static class SupervisorPermission
    {
        /* (non-Javadoc)
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString()
        {
            StringBuilder builder = new StringBuilder();
            builder.append("SupervisorPermission [name=").append(name).append(", description=").append(description)
                    .append(", supervisorName=").append(supervisorName).append(", permissions=").append(permissions)
                    .append(", subjectNames=").append(subjectNames).append("]");
            return builder.toString();
        }
        /**
         * Get the name value.
         *
         * @return a <code>String</code> value
         */
        public String getName()
        {
            return name;
        }
        /**
         * Sets the name value.
         *
         * @param inName a <code>String</code> value
         */
        public void setName(String inName)
        {
            name = inName;
        }
        /**
         * Get the description value.
         *
         * @return a <code>String</code> value
         */
        public String getDescription()
        {
            return description;
        }
        /**
         * Sets the description value.
         *
         * @param inDescription a <code>String</code> value
         */
        public void setDescription(String inDescription)
        {
            description = inDescription;
        }
        /**
         * Get the supervisorName value.
         *
         * @return a <code>String</code> value
         */
        public String getSupervisorName()
        {
            return supervisorName;
        }
        /**
         * Sets the supervisorName value.
         *
         * @param inSupervisorName a <code>String</code> value
         */
        public void setSupervisorName(String inSupervisorName)
        {
            supervisorName = inSupervisorName;
        }
        /**
         * Get the permissions value.
         *
         * @return a <code>Set&lt;String&gt;</code> value
         */
        public Set<String> getPermissions()
        {
            return permissions;
        }
        /**
         * Sets the permissions value.
         *
         * @param inPermissions a <code>Set&lt;String&gt;</code> value
         */
        public void setPermissions(Set<String> inPermissions)
        {
            permissions = inPermissions;
        }
        /**
         * Get the subjectNames value.
         *
         * @return a <code>Set&lt;String&gt;</code> value
         */
        public Set<String> getSubjectNames()
        {
            return subjectNames;
        }
        /**
         * Sets the subjectNames value.
         *
         * @param inSubjectNames a <code>Set&lt;String&gt;</code> value
         */
        public void setSubjectNames(Set<String> inSubjectNames)
        {
            subjectNames = inSubjectNames;
        }
        /**
         * name value
         */
        private String name;
        /**
         * description value
         */
        private String description;
        /**
         * defines the supervisor user name
         */
        private String supervisorName;
        /**
         * permissions granted to the supervisor
         */
        private Set<String> permissions = Sets.newHashSet();
        /**
         * permissions granted to the supervisor over the given users
         */
        private Set<String> subjectNames = Sets.newHashSet();
    }
    /**
     * users value
     */
    private List<User> users = Lists.newArrayList();
    /**
     * roles value
     */
    private List<Role> roles = Lists.newArrayList();
    /**
     * permissions value
     */
    private List<Permission> permissions = Lists.newArrayList();
    /**
     * supervisor permissions value
     */
    private List<SupervisorPermission> supervisorPermissions = Lists.newArrayList();
}
