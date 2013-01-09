package org.marketcetera.persist.example;
import org.marketcetera.persist.*;
import org.marketcetera.persist.PersistenceException;
import org.marketcetera.util.log.I18NMessage0P;

import javax.persistence.*;
import java.util.Set;

/**
 * Instances of this class represent group of users
 * of the system.
 */
@Entity
@Table(name = "test_group", //$NON-NLS-1$
        uniqueConstraints={@UniqueConstraint(columnNames={"name"})}) //$NON-NLS-1$
public class Group extends NDEntityBase implements SummaryGroup {
    private static final long serialVersionUID = 3166790225173409322L;

    /**
     * The set of users that are a member of this group.
     *
     * @return The set of users of users that are a member of this group.
     */
    @ManyToMany(targetEntity = User.class)
    @JoinTable(name = "test_group_user") //$NON-NLS-1$
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
    @JoinTable(name = "test_group_auth") //$NON-NLS-1$
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
    public void save() throws org.marketcetera.persist.PersistenceException {
        saveRemote(null);
    }

    /**
     * Deletes this group from the system.
     *
     * @throws org.marketcetera.persist.PersistenceException if there was an
     * error deleting the Group
     */
    public void delete() throws PersistenceException {
        deleteRemote(null);
    }

    /**
     * The custom localized name for groups.
     *
     * @return custom localized name for groups.
     */
    private static I18NMessage0P getUserFriendlyName() {
        return Messages.NAME_GROUP;
    }

    /**
     * The entity name as is used in various JPQL Queries
     */
    static final String ENTITY_NAME = "Group"; //$NON-NLS-1$

    /**
     * The attribute name for user relations
     */
    static final String ATTRIBUTE_USER = "users"; //$NON-NLS-1$
    /**
     * The attribute name for authorization relations
     */
    static final String ATTRIBUTE_AUTH = "authorizations"; //$NON-NLS-1$

    private Set<SummaryUser> users;
    private Set<Authorization> authorizations;
}
