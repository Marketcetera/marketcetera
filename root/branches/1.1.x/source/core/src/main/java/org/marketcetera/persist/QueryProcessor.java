package org.marketcetera.persist;

import org.marketcetera.core.ClassVersion;

import javax.persistence.Query;
import javax.persistence.EntityManager;
import java.io.Serializable;

/* $License$ */
/**
 * Base class of any class that processes {@link QueryBase query} results.
 * This class enables modification of a query based on the kinds of
 * results are to be retrieved from the query.
 *
 * @author anshul@marketcetera.com
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public abstract class QueryProcessor<R>
        implements Serializable {
    private static final long serialVersionUID = -4913206891375564352L;

    /**
     * Creates an instance.
     *
     * @param detail if the processor retrieves
     * detailed results.
     */
    protected QueryProcessor(boolean detail) {
        this.detail = detail;
    }

    /**
     * Returns true if detailed results need to be fetched.
     * If the returned value is true, fetch-join clauses are
     * added to retrieve the lazy loaded associations.
     *
     * @return if the detailed results need to be fetched.
     */
    public boolean isDetail() {
        return detail;
    }

    private boolean detail;

    /**
     * This method is invoked before the query string is constructed.
     * It can be over-ridden to add explicit selects or any other sql
     * prefix to the query.
     * After this method is done, the from clause is appended to the
     * query.
     * By default the query doesn't have a select clause
     * which leads to it retrieving the entity that the query targets.
     * This method can be over-ridden to have the query return a different
     * value than the entity instance, or execute a delete or update query
     * that operates on the instances that will be fetched by the query
     * by transforming the query into a subselect clause of the delete
     * / update query. 
     *
     * @param queryString the query string being constructed.
     * @param queryBase the query instance that is being executed.
     */
    protected void preGenerate(StringBuilder queryString,
                               QueryBase queryBase) {
        //do nothing, subclasses may do something here.
    }

    /**
     * Returns true if fetch joins should be added to the query.
     * Fetch joins need to be added if the query results needs
     * to have its otherwise lazily fetched attributes, eagerly
     * fetched.
     *
     * @return if the query should add fetch joins.
     */
    protected boolean needsFetchJoins() {
        return isDetail();
    }

    /**
     * Returns true if the results should be ordered
     * based on the ordering configured on the query base
     * by default this method returns false, it should
     * be over-ridden to return true, in case the query
     * result need the results to be ordered.
     * <p> Ordering results takes time, so if the processor
     * doesn't need its results ordered, they are encouraged
     * to return false
     *
     * @return true if the query results need to be ordered
     */
    protected boolean needsOrderBy() {
        return true;
    }

    /**
     * Executes the supplied query and processes its results.
     *
     * @param em The currently active entity manager
     * @param q the query thats being executed.
     *
     * @return the result of the query.
     *
     * @throws PersistenceException if there was an error
     * processing results
     */
    protected abstract QueryResults<R> process(EntityManager em, Query q)
            throws PersistenceException;

    /**
     * This method is invoked after the query string
     * has been completely generated. This method may
     * be over-ridden to make any finishing touches to
     * the query, like adding closing parentheses or
     * extra jpql clauses.
     *
     * @param queryString the query string
     */
    protected void postGenerate(StringBuilder queryString) {
        //do nothing, subclasses may do something here.
    }
}
