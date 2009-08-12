package org.marketcetera.ors.history;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.marketcetera.ors.Principals;
import org.marketcetera.ors.security.SimpleUser;
import org.marketcetera.persist.PersistenceException;
import org.marketcetera.trade.MSymbol;
import org.marketcetera.trade.OrderID;
import org.marketcetera.trade.ReportBase;
import org.marketcetera.trade.ReportBaseImpl;
import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.core.position.PositionKey;

/* $License$ */
/**
 * Provides services to save and query reports.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$")
public class ReportHistoryServices {
    /**
     * Returns all the reports received after the supplied date-time
     * value, and which are visible to the given user.
     *
     * @param inUser the user making the query. Cannot be null.
     * @param inDate the date-time value. Cannot be null.
     *
     * @return the reports that were received after the date-time
     * value, and which are visible to the given user.
     *
     * @throws PersistenceException if there were persistence errors
     * fetching the reports.
     * @throws ReportPersistenceException if the data retrieved had
     * unexpected errors.
     */
    public ReportBaseImpl[] getReportsSince
        (SimpleUser inUser,
         Date inDate)
            throws PersistenceException, ReportPersistenceException {
        MultiPersistentReportQuery query = MultiPersistentReportQuery.all();
        query.setSendingTimeAfterFilter(inDate);
        if (!inUser.isSuperuser()) {
            query.setViewerFilter(inUser);
        }
        query.setEntityOrder(MultiPersistentReportQuery.BY_ID);

        List<PersistentReport> reportList = query.fetch();
        ReportBaseImpl [] reports = new ReportBaseImpl[reportList.size()];
        int i = 0;
        for(PersistentReport report: reportList) {
            reports[i++] = (ReportBaseImpl) report.toReport();
        }
        return reports;
    }

    /**
     * Returns the position of the symbol based on all reports
     * received for it before the supplied date, and which are visible
     * to the given user.
     *
     * @param inUser the user making the query. Cannot be null.
     * @param inDate the date to compare with all the reports. Only the reports
     * that were received prior to this date will be used in this calculation.
     * Cannot be null.
     *
     * @param inSymbol the symbol whose position is desired. Cannot be null.
     *
     * @return the symbol position.
     *
     * @throws PersistenceException if there were errors retrieving the symbol
     * position
     */
    public BigDecimal getPositionAsOf
        (SimpleUser inUser,
         Date inDate,
         MSymbol inSymbol)
        throws PersistenceException
    {
        return ExecutionReportSummary.getPositionForSymbol
            (inUser,inDate,inSymbol);
    }
    /**
     * Returns the aggregate position of each (symbol,account,actor)
     * tuple based on all reports received for each tuple on or before
     * the supplied date, and which are visible to the given user.
     *
     * @param inUser the user making the query. Cannot be null.
     * @param inDate the date to compare with all the reports. Only
     * the reports that were received on or prior to this date will be
     * used in this calculation.  Cannot be null.
     *
     * @return the position map.
     *
     * @throws PersistenceException if there were errors retrieving the
     * position map.
     */
    public Map<PositionKey, BigDecimal> getPositionsAsOf
        (SimpleUser inUser,
         Date inDate)
        throws PersistenceException
    {
        return ExecutionReportSummary.getPositionsAsOf(inUser,inDate);
    }

    /**
     * Saves the supplied report to the database.
     *
     * @param inReport the report to be saved. Cannot be null.
     *
     * @throws org.marketcetera.persist.PersistenceException if there
     * were errors saving the report.
     */
    public void save(ReportBase inReport) throws PersistenceException {
        PersistentReport.save(inReport);
    }

    /**
     * Returns the principals associated with the report with given
     * order ID.
     *
     * @param orderID The order ID.
     *
     * @return The principals. If no report with the given order ID
     * exists, {@link Principals#UNKNOWN} is returned, and no
     * exception is thrown.
     *
     * @throws PersistenceException Thrown if there were errors
     * accessing the report.
     */

    public Principals getPrincipals
        (OrderID orderID)
        throws PersistenceException
    {
        return PersistentReport.getPrincipals(orderID);
    }
}
