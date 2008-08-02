package org.marketcetera.persist;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import static org.marketcetera.persist.JPQLConstants.*;

import javax.persistence.Query;
import javax.persistence.EntityManager;
import javax.persistence.TemporalType;
import java.io.Serializable;
import java.util.*;
import java.sql.Timestamp;
import java.sql.Time;

/* $License$ */
/**
 * Base class for all queries. The query instances are created
 * by the clients of the query, may be serialized and sent over to the
 * server-side process that has direct access to the database.
 *
 * The query is constructed and executed locally on the
 * server-side and its results are returned back to the
 * clients.
 *
 * <p>
 * Subclasses may expose public methods that invoke either
 * of the following methods to invoke the query on the client-side:
 * {@link #executeRemote(QueryProcessor)} or
 * {@link #executeRemoteMultiple(java.util.List)}
 *
 * <p>
 * Invocation of either of the execute methods above results
 * in {@link #executeLocal(javax.persistence.EntityManager, java.util.List)}
 * invoked on this instance on the server-side. This method iterates
 * through each processor. For each processor it invokes
 * {@link #createQuery(javax.persistence.EntityManager, QueryProcessor)}
 * to create a {@link Query} instance and invokes the supplied processor to 
 * {@link QueryProcessor#process(javax.persistence.EntityManager,javax.persistence.Query)}
 *  to process it to generate a {@link org.marketcetera.persist.QueryResults}
 * instance.
 * 
 * <p>
 * The {@link #createQuery(javax.persistence.EntityManager, QueryProcessor)}
 * method invokes {@link #generateQueryString(StringBuilder, QueryProcessor)}
 * to generate the query string, creates a Query instance from the generated
 * query string and then sets all the parameters on the query.
 *
 * @author anshul@marketcetera.com
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public abstract class QueryBase implements Serializable {
    private static final long serialVersionUID = -6252792163564112540L;

    /**
     * Constructs an instance, specifying a query string.
     *
     * @param fromClause the from clause of the JPQL query string,
     * cannot be null
     * @param whereClause the where clause for this query, can be null.
     * @param alias the jpql alias used for the entity being fetched.
     */
    protected QueryBase(String fromClause, String whereClause,
                        String alias) {
        if(fromClause == null) {
            throw new NullPointerException();
        }
        this.fromClause = fromClause;
        this.whereClause = whereClause;
        entityAlias = alias;
    }

    /**
     * Sends the query with the supplied query processor for
     * execution and returns the results back. This method is the same
     * as invoking {@link #executeRemoteMultiple(java.util.List)} with
     * a single processor
     *
     * @param processor the query processor
     *
     * @return the query results
     *
     * @throws PersistenceException if there was an error executing the query
     */
    protected final <T> QueryResults<T> executeRemote(
            QueryProcessor<T> processor)
            throws PersistenceException {
        List<QueryProcessor<T>> l = new LinkedList<QueryProcessor<T>>();
        l.add(processor);
        return executeRemoteMultiple(l).get(0);
    }

    /**
     * Sends the query with supplied query processors. The query is run with
     * each query processor within the context of the same transaction and
     * the results from each of the runs are returned back. All the query
     * processors are executed within the context of the same transaction.
     * Do note that the processors can make changes to the database.
     *
     * @param processors the array of processors
     *
     * @return the query results.
     *
     * @throws PersistenceException if there was an error executing the query
     */
    protected final <T> List<QueryResults<T>> executeRemoteMultiple(
            List<QueryProcessor<T>> processors) throws PersistenceException {
        return EntityRemoteServices.getInstance().execute(this, processors);
    }

    /**
     * This method is invoked to add order by clauses to the supplied
     * queryString. This implementation simply returns the supplied
     * queryString. Subclasses may over-ride this method to add
     * order by clauses to the query string before its executed.
     * <p>
     * Its recommended that this method only appends text to the supplied
     * query string. its not recommended that the existing text within
     * the supplied query string be modified.
     * <p>
     * Do note that this method should only be used to add order by
     * clauses. To add where clauses use {@link #addWhereClauses(StringBuilder)}
     *
     * @param queryString The query string
     */
    protected void addOrderByClauses(StringBuilder queryString) {
    }

    /**
     * This method is invoked prior to creation of the query object.
     * Subclasses can over-ride this method to transform the query,
     * mostly adding one or more sql where clauses to it.
     * <p>
     * Its recommended that this method only appends text to the supplied
     * query string. its not recommended that the existing text within
     * the supplied query string be modified.
     * <p>
     * Do note that this method should only be used to add where clauses.
     * To add order by clauses use {@link #addOrderByClauses(StringBuilder)}
     *
     * @param queryString The query string.
     */
    protected void addWhereClauses(StringBuilder queryString) {
    }

    /**
     * This method is invoked right after the Query instance has been
     * created, but before its executed. It may be over-ridden by
     * subclasses to modify the query before its executed.
     *
     * @param q the query being executed
     */
    protected void preSetParameters(Query q) {
        //do nothing, subclasses may do something here.
    }

    /**
     * Set the value of the specified parameter.
     * The supplied value should be {@link Serializable}
     *
     * @param name the parameter name
     * @param value the parameter value
     *
     * @return the old value of this parameter if it was already set.
     */
    protected final Object setParameter(String name, Object value) {
        return queryParameters.put(name,value);
    }

    /**
     * Adds a where expression to the query.
     *
     * @param queryString The query string to append to.
     * @param clause the clause to add, it may be broken up
     * into multiple string arguments for convenience.
     */
    protected final void andWhereExpression(
            StringBuilder queryString, String... clause) {
        //append where if not already there, otherwise add an AND
        if(queryString.indexOf(WHERE) < 0) {
            queryString.append(S).append(WHERE);
        } else {
            queryString.append(S).append(AND);
        }
        queryString.append(S).append(L);
        for(String s: clause) {
            queryString.append(s);
        }
        queryString.append(R);
    }

    /**
     * Returns the names of the lazy loaded attributes that
     * need to fetch-joined when retrieving the detail view
     * of an entity.
     *
     * @return the array of lazy loaded attributes.
     */
    protected abstract String[] getFetchJoinAttributeNames();

    /**
     * Executes locally within the process that has direct access
     * to the database. Runs the query and returns the results back
     * @param em The entity manager instance.
     * @param processors the query processors to process query results
     *
     * @return The results of the query.
     *
     * @throws PersistenceException if there was an error executing
     * the query.
     */
    final <T> List<QueryResults<T>> executeLocal(
            EntityManager em,
            List<QueryProcessor<T>> processors)
            throws org.marketcetera.persist.PersistenceException {
        ArrayList<QueryResults<T>> results =
                new ArrayList<QueryResults<T>>(processors.size());
        for(QueryProcessor<T>p:processors) {
            results.add(p.process(em, createQuery(em,p)));
        }
        return results;
    }

    /**
     * Returns the alias used for the entity the query string.
     * This value is correctly identify attributes in where and
     * order-by expressions.
     *
     * @return The entity alias used in the query.
     */
    String getEntityAlias() {
        return entityAlias;
    }

    /**
     * Creates a query based on the query string.
     * @param em the entity manager instance
     * @param processor the query processor
     *
     * @return the query instance based on the query string.
     *
     * @throws IllegalStateException if the query string was not specified
     * when creating this instance
     * @throws PersistenceException if there were errors creating the query
     */
    private <T> Query createQuery(
            EntityManager em, QueryProcessor<T> processor)
            throws PersistenceException {
        StringBuilder queryString = new StringBuilder();
        generateQueryString(queryString, processor);

        //Now that we have the query string ready, create
        //the query.
        String s = queryString.toString();
        SLF4JLoggerProxy.debug(this,"Creating Query {}",s); //$NON-NLS-1$
        Query query = em.createQuery(s);

        //Set the query parameters.
        setParameters(query);
        return query;
    }

    /**
     * Generates the query string stiching together the from & where
     * clauses and adding fetch join , filtering and ordering clauses
     * if configured. The supplied processor is also invoked to modify
     * the query string as its being generated.
     *
     * @param queryString the builder to generate the query string into
     * @param processor the query processor
     *
     * @return the supplied query string instance
     */
    private <T> StringBuilder generateQueryString(
            StringBuilder queryString,
            QueryProcessor<T> processor) {
        //Let processor add select if it needs one based on
        //the type of result being fetched.
        processor.preGenerate(queryString,this);
        queryString.append(S).append(fromClause);

        //Let processor add fetch joins if it needs them
        //based on the type of result being fetched.
        if(processor.needsFetchJoins()) {
            addFetchJoins(queryString);
        }
        if(whereClause != null) {
            queryString.append(S).append(whereClause);
        }
        addWhereClauses(queryString);

        //Let the processor add order by, if needed
        //based on the type of result being fetched.
        if(processor.needsOrderBy()) {
            addOrderByClauses(queryString);
        }
        processor.postGenerate(queryString);
        return queryString;
    }

    /**
     * Adds fetch joins to the query, if the query is configured
     * with any.
     *
     * @param queryString the query string.
     */
    private void addFetchJoins(StringBuilder queryString) {
        if (getFetchJoinAttributeNames() != null) {
            for(String s:getFetchJoinAttributeNames()) {
                queryString.append(S).append(FETCH_JOIN).append(S).
                        append(getEntityAlias()).append(DOT).append(s);
            }
        }
    }

    /**
     * Sets the parameters specified via {@link #setParameter(String, Object)}
     * on the query instance before its executed.
     *
     * @param q the query instance on which the parameters need to be set.
     * 
     * @throws PersistenceException if there were errors setting 
     * the parameters
     */
    private void setParameters(Query q) throws PersistenceException {
        //Let subclasses set any parameters if they want
        preSetParameters(q);
        if(!queryParameters.isEmpty()) {
            SLF4JLoggerProxy.debug(this,"Setting Query Parameters {}", //$NON-NLS-1$
                    queryParameters.toString());
            for (Map.Entry<String,Object> e: queryParameters.entrySet()) {
                if(e.getValue() instanceof Date ||
                        e.getValue() instanceof Timestamp) {
                    q.setParameter(e.getKey(), (Date)e.getValue(),
                            javax.persistence.TemporalType.TIMESTAMP);
                } else if (e.getValue() instanceof java.sql.Date) {
                    q.setParameter(e.getKey(), (Date)e.getValue(),
                            TemporalType.DATE);
                } else if(e.getValue() instanceof Time) {
                    q.setParameter(e.getKey(), (Date)e.getValue(),
                            TemporalType.TIME);
                } else {
                    if(e.getValue() instanceof String) {
                        VendorUtils.validateText((String)e.getValue());
                    }
                    q.setParameter(e.getKey(), e.getValue());
                }
            }
        }
    }

    /**
     * The from clause for the query
     */
    private String fromClause = null;
    /**
     * The where clause, can be null.
     */
    private String whereClause = null;
    /**
     * The entity alias in the jpql query
     */
    private String entityAlias;
    /**
     * The map of query parameters.
     */
    private final Map<String,Object> queryParameters =
            new HashMap<String,Object>();
}
