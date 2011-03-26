package org.marketcetera.server.service.impl;

import org.marketcetera.server.service.ReportManager;
import org.marketcetera.systemmodel.Report;
import org.marketcetera.systemmodel.persistence.ReportDao;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.misc.ClassVersion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@Service
@ClassVersion("$Id$")
class ReportManagerImpl
        implements ReportManager
{
    /* (non-Javadoc)
     * @see org.marketcetera.server.service.ReportManager#write(org.marketcetera.systemmodel.Report)
     */
    @Override
    @Transactional(propagation=Propagation.REQUIRED,readOnly=false)
    public void write(Report inReport)
    {
        reportDao.write(inReport);
        try {
            reportDao.write(inReport.getSummary());
        } catch (Exception e) {
            SLF4JLoggerProxy.warn(ReportManagerImpl.class,
                                  e,
                                  "Cannot summarize {} - position changes from this report may not be reflected",
                                  inReport);
        }
    }
    /**
     * 
     */
    @Autowired
    private ReportDao reportDao;
}
