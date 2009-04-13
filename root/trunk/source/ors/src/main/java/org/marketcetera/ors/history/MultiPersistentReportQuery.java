package org.marketcetera.ors.history;

import org.marketcetera.ors.security.SimpleUser;
import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.persist.*;
import static org.marketcetera.persist.JPQLConstants.*;

import java.util.Date;
import java.util.List;

/* $License$ */
/**
 * A query that fetches multiple instances of persistent reports.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$")
class MultiPersistentReportQuery extends MultipleEntityQuery {
    /**
     * Constructs an instance, specifying a query string.
     *
     * @param fromClause  the JPQL from clause string
     * @param whereClause the JPQL where clause, can be null.
     */
    protected MultiPersistentReportQuery(String fromClause, String whereClause) {
        super(fromClause, whereClause);
    }

    @Override
    protected String[] getFetchJoinAttributeNames() {
        return FETCH_JOIN_ATTRIBUTE_NAMES;
    }

    @Override
    protected void addWhereClauses(StringBuilder queryString) {
        super.addWhereClauses(queryString);
        addFilterIfNotNull(queryString, PersistentReport.ATTRIBUTE_SENDING_TIME,
                getSendingTimeAfterFilter(), true);
        addFilterIfNotNull(queryString, PersistentReport.ATTRIBUTE_VIEWER,
                getViewerFilter());
    }
    /**
     * Creates a query that returns all the instances.
     * The returned query instance is setup to sort the messages by the
     * message ID.
     *
     * @return a query that returns all the users.
     *
     * @see #BY_ID
     */
    static MultiPersistentReportQuery all() {
        MultiPersistentReportQuery query = new MultiPersistentReportQuery(
                FROM + S + PersistentReport.ENTITY_NAME + S + ENTITY_ALIAS,
                null);
        query.setEntityOrder(BY_ID);
        return query;
    }

    /**
     * Fetches the instances matched by this query.
     *
     * @return the instances matched by this query.
     *
     * @throws PersistenceException if there were errors fetching
     * the reports.
     */
    List<PersistentReport> fetch() throws PersistenceException {
        return fetchRemote(new MultiQueryProcessor<PersistentReport>(false));
    }

    /**
     * Gets the value of sending time after filter. If specified, it only
     * matches reports with sending times after the specified date time value.
     *
     * @return the sending time after filter value.
     */
    Date getSendingTimeAfterFilter() {
        return mSendingTimeAfterFilter;
    }

    /**
     * Sets the value of sending time after filter.
     *
     * @param inSendingTimeAfterFilter the sending time after filter value.
     * Can be null.
     */
    void setSendingTimeAfterFilter(Date inSendingTimeAfterFilter) {
        mSendingTimeAfterFilter = inSendingTimeAfterFilter;
    }

    /**
     * Gets the value of viewer filter. If specified, it only matches
     * reports with the given viewer.
     *
     * @return the viewer filter value.
     */
    SimpleUser getViewerFilter() {
        return mViewerFilter;
    }

    /**
     * Sets the value of viewer filter.
     *
     * @param inViewerFilter the viewer filter value. Can be null.
     */
    void setViewerFilter(SimpleUser inViewerFilter) {
        mViewerFilter = inViewerFilter;
    }

    /**
     * Deletes the instances matched by this query.
     *
     * @return the number of instances deleted.
     *
     * @throws PersistenceException if there were errors deleting the reports.
     */
    int delete() throws PersistenceException {
        return deleteRemote();
    }
    /**
     * Ordering that will order the results in ascending order by the
     * report ID
     */
    public static final EntityOrder BY_ID =
            new SimpleEntityOrder(PersistentReport.ATTRIBUTE_ID);

    public Date mSendingTimeAfterFilter;
    public SimpleUser mViewerFilter;
    public static final String[] FETCH_JOIN_ATTRIBUTE_NAMES=new String[] {
        PersistentReport.ATTRIBUTE_ACTOR,
        PersistentReport.ATTRIBUTE_VIEWER,
    };
    private static final long serialVersionUID = 1L;
}
