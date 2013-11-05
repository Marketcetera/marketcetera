package org.marketcetera.ors.dao.impl;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.marketcetera.ors.PersistTestBase;
import org.marketcetera.ors.history.PersistentReport;
import org.marketcetera.trade.ReportBase;

/* $License$ */

/**
 * Tests {@link ReportServiceImpl}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class ReportServiceImplTest
        extends PersistTestBase
{
    /**
     * Tests {@link ReportServiceImpl#save(ReportBase)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testSaveReport()
            throws Exception
    {
        List<PersistentReport> reports = findAllPersistentReport();
        assertTrue(reports.isEmpty());
        ReportBase report1 = generateReport();
        reportService.save(report1);
    }
}
