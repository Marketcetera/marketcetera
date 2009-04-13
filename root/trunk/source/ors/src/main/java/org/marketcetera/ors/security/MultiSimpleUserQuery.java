package org.marketcetera.ors.security;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.persist.MultiNDQuery;
import org.marketcetera.persist.PersistenceException;
import org.marketcetera.persist.MultiQueryProcessor;
import static org.marketcetera.persist.JPQLConstants.FROM;
import static org.marketcetera.persist.JPQLConstants.S;

import java.util.List;

/* $License$ */
/**
 * A query that fetches multiple instances of users. To retrieve
 * single instances use
 * {@link org.marketcetera.ors.security.SingleSimpleUserQuery}
 *
 * @author anshul@marketcetera.com
 */
@ClassVersion("$Id$")
public class MultiSimpleUserQuery extends MultiNDQuery {
    private static final long serialVersionUID = -4259099618197174176L;

    @Override
    protected void addWhereClauses(StringBuilder queryString) {
        super.addWhereClauses(queryString);
        addFilterIfNotNull(queryString, SimpleUser.ATTRIBUTE_ACTIVE,
                getActiveFilter());
    }

    /**
     * Creates an instance that fetches all user instances
     * 
     * @return an instance that fetches all user instances
     */
    public static MultiSimpleUserQuery all() {
        return new MultiSimpleUserQuery(FROM + S +
                SimpleUser.ENTITY_NAME + S +  ENTITY_ALIAS,null);
    }

    /**
     * Runs the query and returns the results back.
     * All the filters set on the query are applied and if the entity order
     * is set, the results are ordered by that entity order
     *
     * @return the list of users that matched the query.
     * 
     * @throws PersistenceException if there was an error fetching
     * the users.
     */
    public List<SimpleUser> fetch() throws PersistenceException {
        return fetchRemote(new MultiQueryProcessor<SimpleUser>(false));
    }

    /**
     * deletes all the instances that will be fetched by this query.
     *
     * @return the number of instances deleted
     * 
     * @throws PersistenceException if there was an error deleting the
     * instances
     */
    public int delete() throws PersistenceException {
        return deleteRemote();
    }

    /**
     * A filter which, when applied, filters the users based on the
     * value of their {@link SimpleUser#isActive()} flag.
     *
     * @return the active flag filter.
     */
    public Boolean getActiveFilter() {
        return activeFilter;
    }

    /**
     * Sets the active flag filter
     *
     * @param activeFilter the filter value, can be null.
     */
    public void setActiveFilter(Boolean activeFilter) {
        this.activeFilter = activeFilter;
    }

    private Boolean activeFilter = null;

    /**
     * Constructs an instance.
     *
     * @param fromClause  the initial JPQL from clause
     * @param whereClause the JPQL where clause
     */
    private MultiSimpleUserQuery(String fromClause, String whereClause) {
        super(fromClause, whereClause);
    }

    protected String[] getFetchJoinAttributeNames() {
        return SingleSimpleUserQuery.FETCH_JOIN_ATTRIBUTE_NAMES;
    }
}
