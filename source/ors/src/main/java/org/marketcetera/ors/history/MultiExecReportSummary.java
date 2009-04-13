package org.marketcetera.ors.history;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.persist.*;
import static org.marketcetera.persist.JPQLConstants.FROM;
import static org.marketcetera.persist.JPQLConstants.S;

import java.util.List;

/* $License$ */
/**
 * Fetches multiple instances of {@link ExecutionReportSummary} objects.
 * This class exists to aid testing and is currently not used to implement
 * history feature. 
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$")
class MultiExecReportSummary extends MultipleEntityQuery {
    /**
     * Ordering that will order the results in ascending order by the
     * report ID
     */
    public static final EntityOrder BY_ID =
            new SimpleEntityOrder(ExecutionReportSummary.ATTRIBUTE_ID);
    /**
     * Creates a query that returns all the instances.
     *
     * @return a query that returns all the instances.
     */
    static MultiExecReportSummary all() {
        return new MultiExecReportSummary(FROM + S +
                ExecutionReportSummary.ENTITY_NAME + S +  ENTITY_ALIAS, null);
    }
    /**
     * Fetches the instances matched by this query.
     *
     * @return the instances matched by this query.
     *
     * @throws PersistenceException if there were errors fetching
     * the reports.
     */
    List<ExecutionReportSummary> fetch() throws PersistenceException {
        return fetchRemote(new MultiQueryProcessor<ExecutionReportSummary>(false));
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
     * Constructs an instance, specifying a query string.
     *
     * @param fromClause  the JPQL from clause string
     * @param whereClause the JPQL where clause, can be null.
     */
    protected MultiExecReportSummary(String fromClause, String whereClause) {
        super(fromClause, whereClause);
    }

    @Override
    protected String[] getFetchJoinAttributeNames() {
        return FETCH_JOIN_ATTRIBUTE_NAMES;
    }

    public static final String[] FETCH_JOIN_ATTRIBUTE_NAMES= new String[]{
        ExecutionReportSummary.ATTRIBUTE_VIEWER,
    };
    private static final long serialVersionUID = 1L;
}
