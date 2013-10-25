package org.marketcetera.persist;

import org.marketcetera.core.ClassVersion;
import static org.marketcetera.persist.JPQLConstants.*;

/* $License$ */
/**
 * Query that fetches single entities from the database.
 * The entities can be fetched by specifying the object ID OR
 * by specifying a elements of JPQL query.
 * Do note that if the query string is specified, the query should
 * not fetch more than one instance of the entity.
 * <p>
 * Subclasses can invoke {@link #fetchRemote(SingleQueryProcessor)} to
 * invoke this query on the client-side. 
 *
 * @author anshul@marketcetera.com
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public abstract class SingleEntityQuery
        extends QueryBase {
    private static final long serialVersionUID = -1442796823345156735L;

    /**
     * Runs the query and returns true if the query would
     * fetch any results.
     *
     * @return true if the query would fetch any results, false
     * if the query wouldn't fetch any results.
     *
     * @throws PersistenceException if there was an error executing
     * the query
     */
    public boolean exists() throws PersistenceException {
        return executeRemote(new CountQueryProcessor()).getResult() > 0;
    }

    /**
     * This method is invoked on the client-end. It sends the query
     * to the server-side, runs the query on the server-side and
     * returns the results of the query back.
     *
     * @param processor the query processor to process the
     * retrieved result.
     *
     * @return the retrieved result
     *
     * @throws NoResultException if no results were found
     * @throws NonUniqueResultException if more than one results
     * were found
     * @throws PersistenceException if there was an error
     * executing the query
     */
    protected <T extends SummaryEntityBase>T fetchRemote(
            SingleQueryProcessor<T> processor)
            throws PersistenceException {
        return executeRemote(processor).getResult();
    }

    /**
     * Creates an instance that fetches the entity given its ID value.
     *
     * @param entityName The entity name
     * @param id the entity ID value
     */
    protected SingleEntityQuery(String entityName, long id) {
        this(entityName, EntityBase.ATTRIBUTE_ID,id);
    }
    /**
     * Creates a query instance that fetches an entity based
     * on an attribute value. Do note that the attribute used
     * must be have a unique constraint on it, otherwise the
     * query may fail if it retrieves multiple results.
     *
     * @param entityName the entity type name
     * @param attributeName the entity attribute name
     * @param attributeValue the entity attribute value
     */
    protected SingleEntityQuery(String entityName,
                                String attributeName,
                                Object attributeValue) {
        this(FROM + S + entityName + S + ENTITY_ALIAS,
                WHERE + S + ENTITY_ALIAS + DOT + attributeName
                + S + EQUALS + S + PARAMETER_PREFIX  + attributeName);
        setParameter(attributeName,attributeValue);
    }

    /**
     * Constructs an instance, specifying a query string.
     *
     * @param fromClause the JPQL from clause, cannot be null.
     * @param whereClause the JPQL where clause, cannot be null.
     */
    private SingleEntityQuery(String fromClause,
                              String whereClause) {
        super(fromClause, whereClause, ENTITY_ALIAS);
        if(whereClause == null) {
            throw new NullPointerException();
        }
    }
    /**
     * The entity alias used for all queries
     */
    protected static final String ENTITY_ALIAS = "e"; //$NON-NLS-1$
}
