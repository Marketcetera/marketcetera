package org.marketcetera.ors.dao;

import org.marketcetera.ors.history.PersistentReport;
import org.marketcetera.util.misc.ClassVersion;
import org.springframework.data.jpa.repository.JpaRepository;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public interface PersistentReportDao
        extends JpaRepository<PersistentReport,Long>
{
}
