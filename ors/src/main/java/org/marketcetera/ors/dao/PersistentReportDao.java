package org.marketcetera.ors.dao;

import java.util.Date;
import java.util.List;

import org.marketcetera.ors.history.PersistentReport;
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
    PersistentReport findByReportID(ReportID inReportId);
    List<PersistentReport> findSince(Date inDate);
}
