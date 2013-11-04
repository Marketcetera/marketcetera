package org.marketcetera.ors.dao;

import java.util.Date;
import java.util.List;

import org.marketcetera.ors.history.PersistentReport;
import org.marketcetera.ors.security.SimpleUser;
import org.marketcetera.trade.OrderID;
import org.marketcetera.trade.ReportID;
import org.marketcetera.util.misc.ClassVersion;
import org.springframework.data.jpa.repository.JpaRepository;

/* $License$ */

/**
 * Provides datastore access to {@link PersistentReport} objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public interface PersistentReportDao
        extends JpaRepository<PersistentReport,Long>
{
    /**
     * Finds the report with the given report id.
     *
     * @param inReportId a <code>ReportID</code> value
     * @return a <code>PersistentReport</code> value
     */
    PersistentReport findByReportID(ReportID inReportId);
    /**
     * Finds all reports with the given order id.
     *
     * @param inOrderId an <code>OrderID</code> value
     * @return a <code>List&lt;PersistentReport</code> value
     */
    List<PersistentReport> findByOrderID(OrderID inOrderId);
    /**
     * Finds all reports since the given date.
     *
     * @param inDate a <code>Date</code> value
     * @return a <code>List&lt;PersistentReport</code> value
     */
    List<PersistentReport> findSince(Date inDate);
    /**
     * Finds all reports for the given viewer.
     *
     * @param inViewer a <code>SimpleUser</code> value
     * @return a <code>List&lt;PersistentReport</code> value
     */
    List<PersistentReport> findByViewer(SimpleUser inViewer);
}
