package org.marketcetera.ors.history;

import org.marketcetera.persist.PersistenceException;
import org.marketcetera.trade.MSymbol;
import org.marketcetera.trade.ReportBase;
import org.marketcetera.trade.ReportBaseImpl;
import org.marketcetera.trade.UserID;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.misc.ClassVersion;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.math.BigDecimal;

/* $License$ */
/**
 * Provides services to save and query reports.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public class ReportHistoryServices {
    /**
     * Returns all the reports received after the supplied date-time value.
     *
     * @param inDate the date-time value. Cannot be null.
     *
     * @return the reports that were received after the date-time value.
     *
     * @throws PersistenceException if there were persistence errors
     * fetching the reports.
     * @throws ReportPersistenceException if the data retrieved had
     * unexpected errors.
     */
    public ReportBaseImpl[] getReportsSince(Date inDate)
            throws PersistenceException, ReportPersistenceException {
        MultiPersistentReportQuery query = MultiPersistentReportQuery.all();
        query.setSendingTimeAfterFilter(inDate);
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
     * Returns the position of the symbol based on all reports received for
     * it before the supplied date.
     *
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
    public BigDecimal getPositionAsOf(Date inDate, MSymbol inSymbol)
            throws PersistenceException {
        return ExecutionReportSummary.getPositionForSymbol(inDate, inSymbol);
    }
    /**
     * Returns the positions of all the symbol based on all reports received for
     * them before the supplied date.
     *
     * @param inDate the date to compare with all the reports. Only the reports
     * that were received prior to this date will be used in this calculation.
     * Cannot be null.
     *
     * @return the symbol positions.
     *
     * @throws PersistenceException if there were errors retrieving the symbol
     * position
     */
    public Map<MSymbol, BigDecimal> getPositionsAsOf(final Date inDate)
            throws PersistenceException {
        return ExecutionReportSummary.getPositionsAsOf(inDate);
    }

    /**
     * Saves the supplied report to the database. Returns the ID of
     * the regular user who may view this report.
     *
     * @param inReport the report to be saved. Cannot be null.
     *
     * @return The viewer ID. It may be null.
     *
     * @throws org.marketcetera.persist.PersistenceException if there
     * were errors saving the report.
     */
    public UserID save(ReportBase inReport) throws PersistenceException {
        return PersistentReport.save(inReport);
    }
}
