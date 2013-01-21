package org.marketcetera.persist.example;
import java.util.Set;

import javax.persistence.*;

import org.marketcetera.persist.NDEntityBase;
import org.marketcetera.util.log.I18NMessage0P;

/**
 * Instances of this class represent group of users
 * of the system.
 */
@Entity
@Table(name = "test_group",uniqueConstraints={@UniqueConstraint(columnNames={"name"})})
public class Group
        extends NDEntityBase
        implements SummaryGroup
{
    /**
     * The set of users that are a member of this group.
     *
     * @return The set of users of users that are a member of this group.
     */
    @ManyToMany(targetEntity = User.class)
    @JoinTable(name = "test_group_user")
    public Set<SummaryUser> getUsers() {
        return users;
    }

    /**
     * Set the users that should be a member of this group.
     *
     * @param users The users that are a member of this group.
     */
    public void setUsers(Set<SummaryUser> users) {
        this.users = users;
    }

    /**
     * The set of authorizations assigned to this group
     *
     * @return the set of authorizations assigned to this group
     */
    @ManyToMany
    @JoinTable(name = "test_group_auth")
    public Set<Authorization> getAuthorizations() {
        return authorizations;
    }

    /**
     * Set the authorizations assigned to this group.
     *
     * @param authorizations the authorizations to be assigned to this group.
     */
    public void setAuthorizations(Set<Authorization> authorizations) {
        this.authorizations = authorizations;
    }

    /**
     * Saves this group to the persistent storage.
     *
     * @throws org.marketcetera.persist.PersistenceException if there
     * was an error saving the Group
     */
    public void save() {
        saveRemote(null);
    }

    /**
     * Deletes this group from the system.
     *
     * @throws org.marketcetera.persist.PersistenceException if there was an
     * error deleting the Group
     */
    public void delete() {
        deleteRemote(null);
    }

    /**
     * The custom localized name for groups.
     *
     * @return custom localized name for groups.
     */
    @SuppressWarnings("unused")
    private static I18NMessage0P getUserFriendlyName() {
        return Messages.NAME_GROUP;
    }

    /**
     * The entity name as is used in various JPQL Queries
     */
    static final String ENTITY_NAME = "Group";

    /**
     * The attribute name for user relations
     */
    static final String ATTRIBUTE_USER = "users";
    /**
     * The attribute name for authorization relations
     */
    static final String ATTRIBUTE_AUTH = "authorizations";

    private Set<SummaryUser> users;
    private Set<Authorization> authorizations;
    private static final long serialVersionUID = 1L;
}
