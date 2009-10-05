package org.marketcetera.persist.example;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.persist.*;
import static org.marketcetera.persist.JPQLConstants.*;

import java.util.List;

/* $License$ */
/**
 * A query that fetches multiple instances of groups.
 * To be able to fetch single instances of groups, see
 * {@link SingleGroupQuery}
 *
 * @author anshul@marketcetera.com
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public class MultiGroupQuery extends MultiNDQuery {
    private static final long serialVersionUID = -7663471958224460264L;

    /**
     * Creates a query that returns all the groups.
     * @return a query that returns all the groups.
     */
    public static MultiGroupQuery all() {
        return new MultiGroupQuery(FROM + S + Group.ENTITY_NAME 
                + S + ENTITY_ALIAS,null);
    }

    /**
     * Runs the query and returns the results.
     * Any filters and order set on this query are applied on
     * the results.
     *
     * @return the list of groups
     *
     * @throws PersistenceException if there was an error executing
     * the query
     */
    public List<Group> fetch() throws PersistenceException {
        return fetchRemote(new MultiQueryProcessor<Group>(true));
    }
    /**
     * Runs the query and returns a summary view of the groups.
     * Any filters and order set on this query are applied on
     * the results.
     *
     * @return the list of summary view of groups
     *
     * @throws PersistenceException if there was an error executing
     * the query
     */
    public List<SummaryGroup> fetchSummary() throws PersistenceException {
        return fetchRemote(new MultiQueryProcessor<SummaryGroup>(false));
    }

    /**
     * Deletes all the instances fetched by this query.
     * Do note that the current implementation is
     * inefficient.
     *
     * @return the number of instances deleted.
     *
     * @throws PersistenceException if there was an error deleting
     * the user instances
     */
    public int delete() throws PersistenceException {
        return executeRemote(new DeleteEntityProcessor<Group>(
                Group.class)).getResult();
    }

    /**
     * Creates an instance.
     *
     * @param fromClause The JPQL query from clause
     * @param whereClause The JPQL query where clause, can be null
     */
    private MultiGroupQuery(String fromClause, String whereClause) {
        super(fromClause, whereClause);
    }

    protected String[] getFetchJoinAttributeNames() {
        return SingleGroupQuery.FETCH_JOIN_ATTRIBUTE_NAMES;
    }
}
