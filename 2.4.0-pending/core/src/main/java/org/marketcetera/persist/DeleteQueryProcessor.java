package org.marketcetera.persist;

import org.marketcetera.core.ClassVersion;
import static org.marketcetera.persist.JPQLConstants.*;
import org.marketcetera.util.log.SLF4JLoggerProxy;

import javax.persistence.Query;
import javax.persistence.EntityManager;

/* $License$ */
/**
 * A query processor that is used to run delete statements
 * to delete entities and returns the total number of
 * instances deleted.
 * <p>
 * This processor uses bulk delete query to delete the entity
 * instances and hence is much faster compared to
 * {@link org.marketcetera.persist.DeleteEntityProcessor}. However
 * since JPA doesn't clearly define delete behavior on association
 * tables, this query processor is not useful for deleting
 * entities that are related to other entities via association
 * tables. And you may have to resort to using
 * <code>DeleteEntityProcessor</code> for deleting entities
 * that are related to other entities via association tables.
 *
 * @author anshul@marketcetera.com
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public class DeleteQueryProcessor extends QueryProcessor<Integer>  {
    private static final long serialVersionUID = 8501063818393665951L;

    /**
     * Creates a query processor give the subQueryPrefix.
     * The supplied subQueryPrefix should end with a 'in'
     * clause that expects a select subquery that matches
     * IDs of the fetched entities.
     * This query processor, transforms the actual query into a sub-select
     * query that selects the IDs of the entities being fetched
     * @param subQueryPrefix a query that expects a sub-query that
     * fetches IDs of matching entities as its suffix.
     */
    public DeleteQueryProcessor(String subQueryPrefix) {
        this(subQueryPrefix,true);
    }
    /**
     * Creates an instance.
     * @param queryPrefix the query string to add as a prefix
     * to the query being executed
     * @param isSubQuery true if the actual query should be
     * turned into a sub query that selects the IDs of the
     * entities being fetched.
     */
    private DeleteQueryProcessor(String queryPrefix, boolean isSubQuery) {
        super(false);
        this.queryPrefix = queryPrefix;
        subQuery = isSubQuery;
    }

    /**
     * Over-ridden to prefix the query with a <code>delete</code>
     * clause to delete the fetched entities.
     * @param queryString the query string
     * @param queryBase the query instance
     */
    @Override
    protected void preGenerate(StringBuilder queryString, QueryBase queryBase) {
        if (queryPrefix != null) {
            queryString.append(queryPrefix);
        }
        if(subQuery) {
            queryString.append(L).append(SELECT).append(S).
                    append(queryBase.getEntityAlias()).
                    append(DOT).append(EntityBase.ATTRIBUTE_ID);
        }
    }

    /**
     * Over-ridden to add a closing parentheses to the subquery
     * @param queryString the query string
     */
    @Override
    protected void postGenerate(StringBuilder queryString) {
        if(subQuery) {
            queryString.append(R);
        }
    }

    /**
     * Over-ridden to indicate that this processor doesn't need
     * its results ordered.
     * @return false
     */
    @Override
    protected boolean needsOrderBy() {
        return false;
    }

    protected QueryResults<Integer> process(EntityManager em, Query q)
            throws PersistenceException {
        final int result = q.executeUpdate();
        SLF4JLoggerProxy.debug(this,"Deleted {} rows",result); //$NON-NLS-1$
        return new SingleResult<Integer>(result);
    }
    private final String queryPrefix;
    private final boolean subQuery;
    public static final DeleteQueryProcessor DEFAULT =
            new DeleteQueryProcessor(DELETE,false);
}
