package org.marketcetera.persist;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.persist.example.SingleAuthorizationQuery;
import static org.marketcetera.persist.JPQLConstants.*;

import java.util.List;

/* $License$ */
/**
 * A query that fetches multiple instances of data types.
 *
 * @author anshul@marketcetera.com
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public class MultiDataTypesQuery extends MultiNDQuery {
    private static final long serialVersionUID = 4526506972105727770L;

    /**
     * Creates a query that returns all the datatypes.
     * @return a query that returns all the datatypes.
     */
    public static MultiDataTypesQuery all() {
        return new MultiDataTypesQuery(FROM + S +
                DataTypes.ENTITY_NAME + S + ENTITY_ALIAS,null);
    }

    /**
     * Runs the query and returns the results.
     * Any filters and order set on this query are applied on
     * the results.
     *
     * @return the list of data types
     *
     * @throws PersistenceException if there was an error fetching
     * the instances
     */
    public List<DataTypes> fetch() throws PersistenceException {
        return fetchRemote(new MultiQueryProcessor<DataTypes>(true));
    }
    /**
     * Runs the query and returns a summary view of the data types.
     * Any filters and order set on this query are applied on
     * the results.
     *
     * @return the list of summary view of data types
     *
     * @throws PersistenceException if there was an error fetching
     * the instances
     */
    public List<SummaryDataType> fetchSummary() throws PersistenceException {
        return fetchRemote(new MultiQueryProcessor<SummaryDataType>(false));
    }

    /**
     * Deletes all the instances fetched by this query.
     *
     * @return the number of instances deleted.
     *
     * @throws PersistenceException if there was an error deleting the
     * instances
     */
    public int delete() throws PersistenceException {
        return deleteRemote();
    }

    /**
     * Creates an instance.
     *
     * @param fromClause The JPQL query from clause
     * 
     * @param whereClause The JPQL query where clause, can be null
     */
    private MultiDataTypesQuery(String fromClause, String whereClause) {
        super(fromClause, whereClause);
    }

    protected String[] getFetchJoinAttributeNames() {
        return SingleAuthorizationQuery.FETCH_JOIN_ATTRIBUTE_NAMES;
    }
}
