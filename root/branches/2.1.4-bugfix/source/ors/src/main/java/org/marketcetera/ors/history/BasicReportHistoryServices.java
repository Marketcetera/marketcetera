package org.marketcetera.ors.history;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.marketcetera.client.jms.JmsManager;
import org.marketcetera.core.IDFactory;
import org.marketcetera.core.NoMoreIDsException;
import org.marketcetera.core.position.PositionKey;
import org.marketcetera.ors.LongIDFactory;
import org.marketcetera.ors.Principals;
import org.marketcetera.ors.security.SimpleUser;
import org.marketcetera.persist.PersistenceException;
import org.marketcetera.trade.*;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */
/**
 * Provides basic services to save and query reports.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 2.1.0
 */
@ClassVersion("$Id$")
public class BasicReportHistoryServices
    implements ReportHistoryServices
{

    // INSTANCE DATA.

    private LongIDFactory mReportIDFactory;
    private JmsManager mJmsManager;
    private ReportSavedListener mReportSavedListener;


    // CONSTRUCTORS.

    /**
     * Creates a new basic report history services provider.
     */

    public BasicReportHistoryServices() {}


    // ReportHistoryServices.

    @Override
    public void init
        (IDFactory idFactory,
         JmsManager jmsManager,
         ReportSavedListener reportSavedListener)
        throws ReportPersistenceException
    {
        mReportIDFactory=new LongIDFactory(idFactory);
        mJmsManager=jmsManager;
        mReportSavedListener=reportSavedListener;
    }

    @Override
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

    @Override
    public BigDecimal getEquityPositionAsOf
        (SimpleUser inUser,
         Date inDate,
         Equity inEquity)
        throws PersistenceException
    {
        return ExecutionReportSummary.getEquityPositionAsOf
            (inUser,inDate,inEquity);
    }

    @Override
    public Map<PositionKey<Equity>, BigDecimal> getAllEquityPositionsAsOf
        (SimpleUser inUser,
         Date inDate)
        throws PersistenceException
    {
        return ExecutionReportSummary.getAllEquityPositionsAsOf(inUser,inDate);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.ors.history.ReportHistoryServices#getAllFuturePositionsAsOf(org.marketcetera.ors.security.SimpleUser, java.util.Date)
     */
    @Override
    public Map<PositionKey<Future>,BigDecimal> getAllFuturePositionsAsOf(SimpleUser inUser,
                                                                         Date inDate)
            throws PersistenceException
    {
        return ExecutionReportSummary.getAllFuturePositionsAsOf(inUser,
                                                                inDate);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.ors.history.ReportHistoryServices#getFuturePositionAsOf(org.marketcetera.ors.security.SimpleUser, java.util.Date, org.marketcetera.trade.Future)
     */
    @Override
    public BigDecimal getFuturePositionAsOf(SimpleUser inUser,
                                            Date inDate,
                                            Future inFuture)
            throws PersistenceException
    {
        return ExecutionReportSummary.getFuturePositionAsOf(inUser,
                                                            inDate,
                                                            inFuture);
    }     
    @Override
    public BigDecimal getOptionPositionAsOf
        (final SimpleUser inUser,
         final Date inDate,
         final Option inOption)
        throws PersistenceException {
        return ExecutionReportSummary.getOptionPositionAsOf(inUser,
                inDate, inOption);
    }

    @Override
    public Map<PositionKey<Option>, BigDecimal> getAllOptionPositionsAsOf
        (final SimpleUser inUser,
         final Date inDate)
        throws PersistenceException {
        return ExecutionReportSummary.getAllOptionPositionsAsOf(inUser, inDate);
    }

    @Override
    public Map<PositionKey<Option>, BigDecimal> getOptionPositionsAsOf
        (final SimpleUser inUser,
         final Date inDate,
         final String... inSymbols)
        throws PersistenceException {
        return ExecutionReportSummary.getOptionPositionsAsOf(inUser, inDate, inSymbols);
    }
    @Override
    public void save
        (ReportBase report)
        throws PersistenceException
    {
        boolean success=false;
        try {
            assignID(report);
            PersistentReport.save(report);
            success=true;
            Messages.RHS_PERSISTED_REPLY.info(this,report);
        } finally {
            invokeListener(report,success);
        }
    }

    @Override
    public Principals getPrincipals
        (OrderID orderID)
        throws PersistenceException
    {
        return PersistentReport.getPrincipals(orderID);
    }


    // INSTANCE METHODS.

    /**
     * Returns the receiver's report ID factory.
     *
     * @return The factory.
     */

    protected LongIDFactory getReportIDFactory()
    {
        return mReportIDFactory;
    }

    /**
     * Returns the receiver's JMS manager.
     *
     * @return The manager. It may be null, but only if allowed by the
     * specific subclass.
     */

    protected JmsManager getJmsManager()
    {
        return mJmsManager;
    }

    /**
     * Returns the receiver's listener which should be notified after
     * a report has been saved.
     *
     * @return The listener. It may be null.
     */

    protected ReportSavedListener getReportSavedListener()
    {
        return mReportSavedListener;
    }

    /**
     * Sets the ID of the given report.
     *
     * @param report The report.
     *
     * @throws PersistenceException Thrown if there were errors
     * assigning the ID.
     */

    protected void assignID(ReportBase report)
        throws PersistenceException
    {
        try {
            ReportBaseImpl.assignReportID
                ((ReportBaseImpl)report,
                 new ReportID(getReportIDFactory().getNext()));
        } catch (NoMoreIDsException ex) {
            throw new PersistenceException(ex,Messages.RHS_NO_MORE_IDS);
        }
    }

    /**
     * Invokes the listener which should be notified after the given
     * report has been saved. The given flag indicates whether saving
     * completed successfully or not.
     *
     * @param report The report.
     * @param status True if saving completed successfully.
     */

    protected void invokeListener
        (ReportBase report,
         boolean status)
    {
        if (getReportSavedListener()!=null) {
            getReportSavedListener().reportSaved(report,status);
        }
    }
}
