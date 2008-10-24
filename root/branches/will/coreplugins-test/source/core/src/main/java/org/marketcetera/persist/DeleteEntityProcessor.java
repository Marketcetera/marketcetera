package org.marketcetera.persist;

import org.marketcetera.core.ClassVersion;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import static org.marketcetera.persist.JPQLConstants.*;

import java.util.List;

/* $License$ */
/**
 * A processor that deletes each entity retrieved individually
 * instead of using a bulk delete query. This processor is useful
 * for deleting entities that are related to other entities via
 * association tables as JPA doesn't define how bulk deletes
 * can be carried out over association tables.
 * <p>
 * Do note that this processor attempts to delete the entity
 * by using the {@link EntityManager#remove(Object)}. This mechanism
 * doesn't work in case the entity is on the non-owning side of a
 * relationship. The {@link #removeEntity(javax.persistence.EntityManager, Long)}
 * method can be over-ridden to provide custom logic to delete the
 * entity in such a case.
 * <p>
 * Do note that this delete processor is much less efficient compared
 * to {@link org.marketcetera.persist.DeleteQueryProcessor}. Use
 * <code>DeleteQueryProcessor</code> if the entity being deleted
 * isn't related to other entities via association tables.  
 *
 * @author anshul@marketcetera.com
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public class DeleteEntityProcessor<E extends EntityBase> extends QueryProcessor<Integer> {
    private static final long serialVersionUID = -6527985544098500644L;

    /**
     * Creates an instance.
     *
     * @param clazz the class of the entity being processed
     */
    public DeleteEntityProcessor(Class<E> clazz) {
        super(false);
        this.clazz = clazz;
        className = clazz.getName();
    }
    @Override
    protected void preGenerate(StringBuilder queryString, QueryBase queryBase) {
        queryString.append(SELECT).append(S).
                append(MultipleEntityQuery.ENTITY_ALIAS).
                append(DOT).append(EntityBase.ATTRIBUTE_ID);
    }

    /**
     * Executes the supplied query and processes its results.
     *
     * @param em The currently active entity manager
     * @param q  the query thats being executed.
     * @return the result of the query.
     */
    protected QueryResults<Integer> process(EntityManager em, Query q)
            throws PersistenceException {
        //Get the list of IDs, iterate through them and delete each of the entities.
        List l = q.getResultList();
        Long id;
        for(Object o:l) {
            id = (Long) o;
            removeEntity(em, id);
        }
        return new SingleResult<Integer>(l.size());
    }

    /**
     * Removes the entity by getting a reference to it via the supplied
     * entity manager and then removing it.
     * Subclasses may over-ride this method to delete the entity in a
     * different way.
     * @param em The currently active entity manager.
     * @param id The ID of the entity being deleted.
     * @throws PersistenceException if there was an error removing the entity.
     */
    protected void removeEntity(EntityManager em, Long id)
            throws PersistenceException {
        em.remove(em.getReference(getEntityClass(),id));
    }

    /**
     * Gets the entity class by dynamically fetching it using
     * {@link #className}. The class is dynamically fetched to
     * avoid having to serialize the class object. 
     * @return The class for the entity being processed
     * @throws PersistenceException if there was an error fetching the class.
     */
    @SuppressWarnings("unchecked") //$NON-NLS-1$
    protected final Class<E> getEntityClass() throws PersistenceException {
        if(clazz == null) {
            try {
                clazz = (Class<E>)Class.forName(className);
            } catch (ClassNotFoundException e) {
                throw new PersistSetupException(e,
                        Messages.UNEXPECTED_SETUP_ISSUE);
            }
        }
        return clazz;
    }
    private String className;
    private transient Class<E> clazz;
}
