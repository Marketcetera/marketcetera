package org.marketcetera.persist.example;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.persist.NDEntityBase;
import org.marketcetera.persist.PersistenceException;
import org.marketcetera.persist.SaveResult;
import org.marketcetera.persist.PersistContext;
import org.marketcetera.util.log.I18NMessage0P;

import javax.persistence.*;
import java.util.Set;
import java.util.HashSet;

/* $License$ */
/**
 * Instances of this class represent available
 * authorizations in the system. Each authorization has a
 * name and description. The authorization name is a string
 * that is used to validate the permission programmatically.
 * For example it may correspond to a value that can be
 * used to construct java permission instance.
 * The authorization description is a user friendly string
 * that describes the authorization to the user.
 * Each authorization can be assigned to multiple groups.
 *
 * @author anshul@marketcetera.com
 */
@ClassVersion("$Id$") //$NON-NLS-1$
@Entity
@Table(name = "test_auth", //$NON-NLS-1$
        uniqueConstraints={@UniqueConstraint(columnNames={"name"})}) //$NON-NLS-1$
public class Authorization extends NDEntityBase {
    private static final long serialVersionUID = 4651379333749106571L;

    /**
     * Saves this authorization to the persistent storage.
     *
     * @throws PersistenceException if there was an error
     * saving the instance
     */
    public void save() throws PersistenceException {
        saveRemote(null);
    }

    /**
     * Deletes this authorization from the system.
     * Deletion will  fail if this authorization has been
     * assigned to any group.
     *
     * @throws PersistenceException if there was an error deleting
     * the instance
     */
    public void delete() throws PersistenceException {
        deleteRemote(null);
    }

    @Override
    protected SaveResult deleteLocal(EntityManager em,
                                     PersistContext context)
            throws PersistenceException {
        //Carry out delete such authorizations can be deleted
        //even if there are groups referring to it.
        deleteAuthorization(em,getId());
        return new SaveResult(UNINITIALIZED, UNINITIALIZED, null);
    }

    /**
     * Deletes the authorization after removing it from any groups
     * that might be referring to it
     *
     * @param em the entity manager reference
     * @param id the ID for authorizatino entity
     *
     * @throws PersistenceException if there were any errors
     * deleting the authorization
     */
    static void deleteAuthorization(EntityManager em, long id)
            throws PersistenceException {
        Authorization auth = em.find(Authorization.class,id);
        //may return null, if this entity is not saved, in which
        //case don't do anything.
        if(auth == null) {
            return;
        }
        //Iterate through all groups and remove the authorization from them
        Set<SummaryGroup> groups = auth.getGroups();
        if (groups != null) {
            for(SummaryGroup g: groups) {
                Group group = new SingleGroupQuery(g.getId()).fetch();
                Set<Authorization> auths = group.getAuthorizations();
                HashSet<Authorization> updated = new HashSet<Authorization>();
                for(Authorization a: auths) {
                    if(a.getId() != auth.getId()) {
                        updated.add(a);
                    }
                }
                group.setAuthorizations(updated);
                group.save();
            }
        }
        em.remove(em.getReference(Authorization.class,auth.getId()));
    }

    /**
     * Declared to help delete authorization even
     * when they have been assigned to groups.
     *
     * @return the groups that this authorization has been assigned to
     */
    @ManyToMany(mappedBy = "authorizations", targetEntity = Group.class) //$NON-NLS-1$
    private Set<SummaryGroup> getGroups() {
        return groups;
    }

    private void setGroups(Set<SummaryGroup> groups) {
        this.groups = groups;
    }
    
    /**
     * The custom localized name for authorizations.
     *
     * @return custom localized name for authorizations.
     */
    private static I18NMessage0P getUserFriendlyName() {
        //return null to test the edge case where this method
        //returns null. The generic entity name should be used
        //in this case.
        return null;
    }

    private Set<SummaryGroup> groups;

    /**
     * The entity name as is used in various JPQL Queries
     */
    static final String ENTITY_NAME = "Authorization"; //$NON-NLS-1$
}
