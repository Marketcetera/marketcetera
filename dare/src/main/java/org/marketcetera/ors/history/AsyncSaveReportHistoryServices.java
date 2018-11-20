package org.marketcetera.ors.history;

import org.marketcetera.client.jms.JmsManager;
import org.marketcetera.core.IDFactory;
import org.marketcetera.core.QueueProcessor;
import org.marketcetera.ors.dao.ReportService;
import org.marketcetera.trade.ReportBase;
import org.marketcetera.util.misc.ClassVersion;
import org.springframework.beans.factory.annotation.Autowired;


/* $License$ */

/**
 * Provides services to save and query reports with asynchronous saving.
 *
 * @author tlerios@marketcetera.com
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @since 2.1.0
 * @version $Id$
 */
@ClassVersion("$Id$")
public class AsyncSaveReportHistoryServices
        extends BasicReportHistoryServices
{
    /**
     * Get the reportService value.
     *
     * @return a <code>ReportService</code> value
     */
    public ReportService getReportService()
    {
        return reportService;
    }
    /**
     * Sets the reportService value.
     *
     * @param inReportService a <code>ReportService</code> value
     */
    public void setReportService(ReportService inReportService)
    {
        reportService = inReportService;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.ors.history.BasicReportHistoryServices#init(org.marketcetera.core.IDFactory, org.marketcetera.client.jms.JmsManager, com.marketcetera.ors.history.ReportSavedListener)
     */
    @Override
    public void init(IDFactory inIdFactory,
                     JmsManager inJmsManager,
                     ReportSavedListener inReportSavedListener)
    {
        super.init(inIdFactory,
                   inJmsManager,
                   inReportSavedListener);
        reports = new QueueHandler();
        reports.start();
    }
    /* (non-Javadoc)
     * @see com.marketcetera.ors.history.BasicReportHistoryServices#save(org.marketcetera.trade.ReportBase)
     */
    @Override
    public void save(ReportBase inReport)
    {
        assignID(inReport);
    	Messages.RHS_ENQUEUED_REPLY.info(this,
    	                                 inReport);
        reports.add(inReport);
    }
    /**
     * Process reports to be saved.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since 2.4.2
     */
    @ClassVersion("$Id$")
    private class QueueHandler
            extends QueueProcessor<ReportBase>
    {
        /* (non-Javadoc)
         * @see org.marketcetera.core.QueueProcessor#processData(java.lang.Object)
         */
        @Override
        protected void processData(ReportBase inReport)
                throws Exception
        {
            Messages.RHS_DEQUEUED_REPLY.debug(this,
                                              inReport);
            boolean success = false;
            try {
                reportService.save(inReport);
                success = true;
                Messages.RHS_PERSISTED_REPLY.info(this,
                                                  inReport);
            } catch (RuntimeException e) {
                Messages.RHS_PERSIST_ERROR.error(this,
                                                 e,
                                                 inReport);
            } finally {
                invokeListener(inReport,
                               success);
            }
        }
        /**
         * Adds the given report to the processing queue.
         *
         * @param inData a <code>ReportBase</code> value
         */
        protected void add(ReportBase inData)
        {
            super.add(inData);
        }
        /**
         * Create a new QueueHandler instance.
         */
        private QueueHandler()
        {
            super("Async Report Processor"); //$NON-NLS-1$
        }
    }
    /**
     * manages reports to be saved
     */
    private QueueHandler reports;
    /**
     * provides datastore access to <code>PersistentReport</code> objects
     */
    @Autowired
    private ReportService reportService;
}
