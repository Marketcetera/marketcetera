package org.marketcetera.persist.example;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.persist.*;
import static org.marketcetera.persist.JPQLConstants.*;

import java.util.List;
import java.util.LinkedList;

/* $License$ */
/**
 * A query that fetches multiple instances of users.
 * To be able to fetch single instances of users, see
 * {@link SingleUserQuery}
 *
 * @author anshul@marketcetera.com
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public class MultiUserQuery extends MultiNDQuery {
    /**
     * Ordering that will order the results in ascending order by the
     * employeeID
     */
    public static final EntityOrder BY_EMPLOYEE_ID =
            new SimpleEntityOrder(User.ATTRIBUTE_EMPLOYEE_ID);
    /**
     * Ordering that will order the results in ascending order by the
     * email ID
     */
    public static final EntityOrder BY_EMAIL =
            new SimpleEntityOrder(User.ATTRIBUTE_EMAIL);
    private static final long serialVersionUID = -8991126540043017433L;

    /**
     * Creates a query that returns all the users.
     *
     * @return a query that returns all the users. 
     */
    public static MultiUserQuery all() {
        return new MultiUserQuery(FROM + S +
                User.ENTITY_NAME + S +  ENTITY_ALIAS,null);
    }

    /**
     * Runs the query and returns the results.
     * Any filters and order set on this query are applied on
     * the results.
     *
     * @return the list of users
     *
     * @throws PersistenceException if there was an error fetching
     * the Users
     */
    public List<User> fetch() throws PersistenceException {
        return fetchRemote(new MultiQueryProcessor<User>(true));
    }
    /**
     * Runs the query and returns a summary view of the users.
     * Any filters and order set on this query are applied on
     * the results.
     *
     * @return the list of summary view of users
     *
     * @throws PersistenceException if there was an error fetching
     * the summary views of the users.
     */
    public List<SummaryUser> fetchSummary() throws PersistenceException {
        return fetchRemote(new MultiQueryProcessor<SummaryUser>(false));
    }

    /**
     * Deletes all the instances fetched by this query.
     *
     * @return the number of instances deleted.
     *
     * @throws PersistenceException if there was an error deleting
     * the user instances
     */
    public int delete() throws PersistenceException {
        //Run a query that first deletes the session objects
        //followed by a query that deletes the user objects.
        final String alias = "s"; //$NON-NLS-1$
        LinkedList<QueryProcessor<Integer>> l =
                new LinkedList<QueryProcessor<Integer>>();
        l.add(new DeleteQueryProcessor(DELETE + S + FROM + S +
                Setting.ENTITY_NAME + S + alias + S + WHERE +
                S + alias + DOT + User.ATTRIBUTE_OWNER + S + IN));
        l.add(DeleteQueryProcessor.DEFAULT);
        List<QueryResults<Integer>> results = executeRemoteMultiple(l);
        //return the number of user objects deleted
        return results.get(1).getResult();
    }

    /**
     * A filter when applied, filters the users based on the
     * value of their {@link SummaryUser#isEnabled()} flag.
     *
     * @return the enabled flag filter.
     */
    public Boolean getEnabledFilter() {
        return enabledFilter;
    }

    /**
     * Sets the enabled flag filter
     *
     * @param enabledFilter the filter value, can be null.
     */
    public void setEnabledFilter(Boolean enabledFilter) {
        this.enabledFilter = enabledFilter;
    }

    /**
     * A filter when applied, filters the users based on their
     * email address.
     *
     * @return the email address filter.
     */
    public StringFilter getEmailFilter() {
        return emailFilter;
    }

    /**
     * Sets the email address filter.
     *
     * @param emailFilter the email address filter value, can be null.
     */
    public void setEmailFilter(StringFilter emailFilter) {
        this.emailFilter = emailFilter;
    }

    /**
     * Sets the employee ID filter value.
     *
     * @return the employee ID filter value.
     */
    public StringFilter getEmployeeIDFilter() {
        return employeeIDFilter;
    }

    /**
     * Sets the employeeID filter value.
     *
     * @param employeeIDFilter the employeeID filter value, can be null.
     */
    public void setEmployeeIDFilter(StringFilter employeeIDFilter) {
        this.employeeIDFilter = employeeIDFilter;
    }

    @Override
    protected void addWhereClauses(StringBuilder queryString) {
        super.addWhereClauses(queryString);
        addFilterIfNotNull(queryString, User.ATTRIBUTE_ENABLED,
                getEnabledFilter());
        addFilterIfNotNull(queryString, User.ATTRIBUTE_EMAIL,
                getEmailFilter());
        addFilterIfNotNull(queryString, User.ATTRIBUTE_EMPLOYEE_ID,
                getEmployeeIDFilter());
    }

    /**
     * Creates an instance.
     *
     * @param fromClause The JPQL query from clause
     * @param whereClause The JPQL query where clause, can be null
     */
    private MultiUserQuery(String fromClause, String whereClause) {
        super(fromClause, whereClause);
    }

    protected String[] getFetchJoinAttributeNames() {
        return SingleUserQuery.FETCH_JOIN_ATTRIBUTE_NAMES;
    }

    private Boolean enabledFilter = null;
    private StringFilter emailFilter = null;
    private StringFilter employeeIDFilter = null;
}
