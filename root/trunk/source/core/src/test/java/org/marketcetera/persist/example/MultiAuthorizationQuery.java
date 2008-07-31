package org.marketcetera.persist.example;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.persist.*;
import static org.marketcetera.persist.JPQLConstants.*;

import javax.persistence.EntityManager;
import java.util.List;

/* $License$ */
/**
 * A query that fetches multiple instances of authorizations.
 * To be able to fetch single instances of authorizations, see
 * {@link SingleAuthorizationQuery}
 *
 * @author anshul@marketcetera.com
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public class MultiAuthorizationQuery extends MultiNDQuery {
    private static final long serialVersionUID = -1359475970939490204L;

    /**
     * Creates a query that returns all the groups.
     *
     * @return a query that returns all the groups.
     */
    public static MultiAuthorizationQuery all() {
        return new MultiAuthorizationQuery(FROM + S +
                Authorization.ENTITY_NAME + S + ENTITY_ALIAS,null);
    }

    /**
     * Runs the query and returns the results.
     * Any filters and order set on this query are applied on
     * the results.
     *
     * @return the list of authorizations
     *
     * @throws PersistenceException if there was an error executing the
     * query
     */
    public List<Authorization> fetch() throws PersistenceException {
        return fetchRemote(new MultiQueryProcessor<Authorization>(true));
    }

    /**
     * Deletes all the instances fetched by this query.
     *
     * @return the number of instances deleted.
     *
     * @throws PersistenceException if there was an error executing
     * the query
     */
    public int delete() throws PersistenceException {
        //Carry out a special delete to allow Authorizations
        //to be removed even if there are groups referring to it.
        return executeRemote(new DeleteEntityProcessor<Authorization>(
                Authorization.class) {
            private static final long serialVersionUID = 5395599616168727724L;

            @Override
            protected void removeEntity(EntityManager em, Long id)
                            throws PersistenceException {
                        Authorization.deleteAuthorization(em,id);
                    }
                }).getResult();
    }

    /**
     * Creates an instance.
     * 
     * @param fromClause The JPQL query from clause
     * @param whereClause The JPQL query where clause, can be null
     */
    private MultiAuthorizationQuery(String fromClause, String whereClause) {
        super(fromClause, whereClause);
    }

    protected String[] getFetchJoinAttributeNames() {
        return SingleAuthorizationQuery.FETCH_JOIN_ATTRIBUTE_NAMES;
    }
}
