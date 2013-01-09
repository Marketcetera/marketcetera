package org.marketcetera.persist;

import org.marketcetera.core.ClassVersion;

/* $License$ */
/**
 * Base class for queries that fetch single instances of entities
 * that have name and description attributes and have separate
 * summary and detail views. 
 *
 * @author anshul@marketcetera.com
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public abstract class SingleFetchQuery<S extends SummaryNDEntityBase, 
        E extends NDEntityBase> extends SingleNDEntityQuery {
    private static final long serialVersionUID = -2914581240321597861L;

    /**
     * Executes the query and returns the summary view of
     * the entity, if found.
     *
     * @return the summary view of the entity if found
     *
     * @throws NoResultException if no results were found
     * @throws NonUniqueResultException if more than one results
     * were found
     * @throws PersistenceException if there was an error
     * executing the query
     */
    public S fetchSummary() throws PersistenceException {
        return fetchRemote(new SingleQueryProcessor<S>(false));
    }

    /**
     * Executes the query and returns the detail view of
     * the entity if found
     *
     * @return the detail view of the entity if found.
     * 
     * @throws NoResultException if no results were found
     * @throws NonUniqueResultException if more than one results
     * were found
     * @throws PersistenceException if there was an
     * error executing the query
     */
    public E fetch() throws PersistenceException {
        return fetchRemote(new SingleQueryProcessor<E>(true));
    }
    protected SingleFetchQuery(String entityName, long id) {
        super(entityName, id);
    }

    protected SingleFetchQuery(String entityName, String name) {
        super(entityName, name);
    }
}
