package org.marketcetera.persist;

import org.marketcetera.core.ClassVersion;
import static org.marketcetera.persist.JPQLConstants.*;

import javax.persistence.Query;
import java.util.List;
import java.util.Date;

/* $License$ */
/**
 * Base class for queries that fetch multiple entities.
 *
 * The results from this query can be filtered, ordered and paged.
 * <p>
 * <b>Filtering:</b>For filtering the subclasses have properties defined for the
 * set of filters supported.
 * <p>
 * <b>Ordering</b>To order the results, an EntityOrder may be specified via
 * {@link #setEntityOrder(EntityOrder)}. Each subclass may
 * define constants for the set of entity orders that are
 * supported. For example, this class supports
 * {@link #BY_LAST_UPDATED} ordering that, when set, will
 * order the results in the ascending order of their last
 * updated time stamp values.
 * The ordering imposed by the current entity order can be
 * reversed via the {@link #setReverseOrder(boolean)} 
 * <p>
 * <b>Paging:</b>The results can be paged via methods
 * {@link #setFirstResult(int)} and {@link #setMaxResult(int)} 
 *
 * @author anshul@marketcetera.com
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public abstract class MultipleEntityQuery
        extends QueryBase {

    /**
     * Ordering that will order the results in ascending order by the
     * date time the entity was last updated.
     */
    public static final EntityOrder BY_LAST_UPDATED =
            new SimpleEntityOrder(EntityBase.ATTRIBUTE_LAST_UPDATED);
    private static final long serialVersionUID = 1448817323308134123L;

    /**
     * Get start position of the first result starting from 0.
     * The value is ignored if its set to a value less than zero.
     *
     * @return the start position of the first result.
     */
    public int getFirstResult() {
        return firstResult;
    }

    /**
     * Sets the start position of the first result starting from 0
     *
     * @param firstResult the start position of the first result.
     */
    public void setFirstResult(int firstResult) {
        this.firstResult = firstResult;
    }

    /**
     * Gets the maximum number of results to retrieve. The value
     * is ignored if its less than or equal to zero.
     *
     * @return the maximum number of results to retrieve.
     */
    public int getMaxResult() {
        return maxResult;
    }

    /**
     * Sets the maximum number of results to retrieve.
     *
     * @param maxResult the maximum number of results to retrieve.
     */
    public void setMaxResult(int maxResult) {
        this.maxResult = maxResult;
    }

    /**
     * returns the ordering that should be used to order the
     * results of this query. If the returned value is null,
     * the order of returned results is undefined.
     *
     * @return the ordering used to order the results of
     * this query
     */
    public EntityOrder getEntityOrder() {
        return entityOrder;
    }

    /**
     * Specifies the ordering that should be used to order the
     * results of this query.
     *
     * @param entityOrder the ordering value, can be null.
     */
    public void setEntityOrder(EntityOrder entityOrder) {
        this.entityOrder = entityOrder;
    }

    /**
     * Returns true if the order specified by {@link #getEntityOrder()}
     * should be reversed. The value has no effect if no
     * entity order has been specified.
     *
     * @return true if the specified entity order should be reversed
     */
    public boolean isReverseOrder() {
        return reverseOrder;
    }

    /**
     * Sets the flag to reverse the ordering specified by
     * {@link #getEntityOrder()}
     *
     * @param reverseOrder flag to reverse the entity order.
     */
    public void setReverseOrder(boolean reverseOrder) {
        this.reverseOrder = reverseOrder;
    }

    /**
     * Runs the query and returns a count of the number of
     * instances that it would fetch.
     *
     * @return the number of instances fetched.
     *
     * @throws PersistenceException if there was an error executing
     * the query
     */
    public long fetchCount() throws PersistenceException {
        return executeRemote(new CountQueryProcessor()).getResult();
    }

    /**
     * Gets the last updated after filter for this query. If set, the
     * query will match records that were updated after the filter date.
     *
     * @return the last updated after filter.
     */
    public Date getUpdatedAfterFilter() {
        return mUpdatedAfterFilter;
    }

    /**
     * Sets the last updated after filter for this query.
     *
     * @param inUpdatedAfterFilter the last updated filter for this query.
     * Can be null.
     * @see #getUpdatedAfterFilter()
     */
    public void setUpdatedAfterFilter(Date inUpdatedAfterFilter) {
        mUpdatedAfterFilter = inUpdatedAfterFilter;
    }

    /**
     * Gets the last updated before filter for this query. If set, the
     * query will match records that were updated before the filter date.
     *
     * @return the last updated before filter.
     */
    public Date getUpdatedBeforeFilter() {
        return mUpdatedBeforeFilter;
    }

    /**
     * Sets the last updated before filter for this query.
     *
     * @param inUpdatedBeforeFilter the last updated before filter for this
     * query. Can be null.
     * @see #getUpdatedBeforeFilter() 
     */
    public void setUpdatedBeforeFilter(Date inUpdatedBeforeFilter) {
        mUpdatedBeforeFilter = inUpdatedBeforeFilter;
    }

    /**
     * Constructs an instance, specifying a query string.
     *
     * @param fromClause the JPQL from clause string
     * @param whereClause the JPQL where clause, can be null.
     */
    protected MultipleEntityQuery(String fromClause,
                                  String whereClause) {
        super(fromClause, whereClause, ENTITY_ALIAS);
    }

    /**
     * Sends the query to the server-side, executes it
     * and returns the results of the query back.
     * The supplied processor determines the result of the query.
     *
     * @param processor the query processor
     *
     * @return the results of the query
     *
     * @throws PersistenceException if there was an error executing
     * the query
     */
    protected <T extends SummaryEntityBase> List<T> fetchRemote(
            MultiQueryProcessor<T> processor) throws PersistenceException {
        return executeRemote(processor).getResult();
    }

    /**
     * Deletes all the instances that will be selected by this query.
     *
     * @return number of instances deleted.
     *
     * @throws PersistenceException if there was an error executing
     * the query
     */
    protected int deleteRemote() throws PersistenceException {
        return executeRemote(DeleteQueryProcessor.DEFAULT).getResult();
    }

    /**
     * Adds the configured {@link #getEntityOrder() entity order}
     * clause to the query string being executed.
     *
     * @param queryString the JPQL query string.
     */
    @Override
    protected void addOrderByClauses(StringBuilder queryString) {
        super.addOrderByClauses(queryString);
        if (entityOrder != null) {
            entityOrder.apply(queryString, this);
        }
    }

    /**
     * Overridden to set the query's paging attributes.
     * ie. the first and the maximum result values
     *
     * @param q the query being executed.
     */
    @Override
    protected void preSetParameters(Query q) {
        super.preSetParameters(q);
        if(getFirstResult() >= 0) {
            q.setFirstResult(getFirstResult());
        }
        if(getMaxResult() > 0) {
            q.setMaxResults(getMaxResult());
        }
    }

    @Override
    protected void addWhereClauses(StringBuilder queryString) {
        super.addWhereClauses(queryString);
        addFilterIfNotNull(queryString, EntityBase.ATTRIBUTE_LAST_UPDATED,
                mUpdatedAfterFilter, true);
        addFilterIfNotNull(queryString, EntityBase.ATTRIBUTE_LAST_UPDATED,
                mUpdatedBeforeFilter, false);
    }

    /**
     * Adds the filter, if its not null, to the supplied queryString.
     * A where expression is added to the supplied query string for
     * the filter. The filter value is added as a parameter to the query
     * for execution.
     *
     * @param queryString The query string
     * @param attributeName the name of the entity's attribute
     * @param filter the filter, if the supplied value is null,
     * this method does nothing.
     *
     * @return the supplied query string instance.
     */
    protected final StringBuilder addFilterIfNotNull(
            StringBuilder queryString,
            String attributeName,
            StringFilter filter) {
        if(filter != null) {
            String filterParameter = attributeName + FILTER_SUFFIX;
            andWhereExpression(queryString, getEntityAlias(), DOT,
                    attributeName, S, LIKE,S, PARAMETER_PREFIX,
                    filterParameter, S, ESCAPE, S, QUOTE, ESCAPE_STRING,
                    QUOTE);
            setParameter(filterParameter,prepareFilter(filter));
        }
        return queryString;
    }

    /**
     * Adds the filter, if it's not null, to the supplied queryString.
     * A where expression is added to the supplied query string for
     * the filter. The filter value is added as a parameter to the query
     * for execution.
     *
     * @param queryString The query string
     * @param attributeName the name of the entity's attribute
     * @param attributeValue the desired value of the entity's
     * attribute; if the supplied value is null, this method does nothing.
     *
     * @return the supplied query string instance.
     */
    protected final StringBuilder addFilterIfNotNull(StringBuilder queryString,
                                            String attributeName,
                                            Object attributeValue) {
        if(attributeValue != null) {
            String filterParameter = attributeName + FILTER_SUFFIX;
            andWhereExpression(queryString, getEntityAlias(), DOT,
                    attributeName, S, EQUALS, S, PARAMETER_PREFIX,
                    filterParameter);
            setParameter(filterParameter,attributeValue);
        }
        return queryString;
    }

    /**
     * Adds the filter, if it's not null, to the supplied queryString.
     * A where expression is added to the supplied query string for the
     * filter. The filter value is added as a parameter to the query.
     *
     * @param inQueryString the query string
     * @param inAttributeName the name of the entity's attribute.
     * @param inFilterValue the filter value, if the supplied value is null,
     * this method does nothing.
     * @param inSelectGreater true if the matched values should be greater
     * than the filter value, false if the matched values should be less
     * than the filter value. 
     */
    protected final void addFilterIfNotNull(StringBuilder inQueryString,
                                    String inAttributeName,
                                    Date inFilterValue,
                                    boolean inSelectGreater) {
        if(inFilterValue != null) {
            String filterParameter = inAttributeName + FILTER_SUFFIX;
            andWhereExpression(inQueryString, getEntityAlias(), DOT,
                    inAttributeName, S,
                    inSelectGreater
                            ? GREATER_THAN
                            : LESS_THAN,
                    S, PARAMETER_PREFIX, filterParameter);
            setParameter(filterParameter, inFilterValue);
        }
    }

    /**
     * Converts the supplied filter to a JPQL friendly expression value.
     * In the supplied filter, <code>'?'</code> character may be used
     * to match any character and <code>'*'</code>
     *
     * @param stringFilter The filter string as typed in by the user
     * 
     * @return the filter string converted for use within a JPQL query.
     */
    private String prepareFilter(StringFilter stringFilter) {
        String filter = stringFilter.getValue();
        //escape the escape char.
        filter = filter.replace(ESCAPE_STRING, ESCAPE_STRING + ESCAPE_STRING);
        //escape the filter chars
        filter = filter.replace("_", ESCAPE_STRING + "_"); //$NON-NLS-1$ //$NON-NLS-2$
        filter = filter.replace("%", ESCAPE_STRING + "%"); //$NON-NLS-1$ //$NON-NLS-2$
        //convert user patterns to JPQL
        filter = filter.replace(StringFilter.MATCH_ONE,'_');
        filter = filter.replace(StringFilter.MATCH_MANY,'%');
        return filter;
    }
    /**
     * The first row index to return.
     */
    private int firstResult = -1;
    /**
     * The maximum number of rows to return
     */
    private int maxResult = -1;
    /**
     * If the specified entity ordering should be reversed.
     */
    private boolean reverseOrder = false;
    private Date mUpdatedAfterFilter;
    private Date mUpdatedBeforeFilter;
    /**
     * The ordering that should be used for the query.
     */
    private EntityOrder entityOrder = null;
    /**
     * The escape char for like expressions.
     */
    private static final String ESCAPE_STRING = "+"; //$NON-NLS-1$
    /**
     * The suffix used for filter paramter names
     */
    private static final String FILTER_SUFFIX = "Filter"; //$NON-NLS-1$
    /**
     * The entity alias used for all queries
     */
    protected static final String ENTITY_ALIAS = "e"; //$NON-NLS-1$
}
