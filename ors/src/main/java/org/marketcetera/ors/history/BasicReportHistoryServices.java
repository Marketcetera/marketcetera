package org.marketcetera.ors.history;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.marketcetera.client.jms.JmsManager;
import org.marketcetera.core.IDFactory;
import org.marketcetera.core.NoMoreIDsException;
import org.marketcetera.core.position.PositionKey;
import org.marketcetera.core.time.TimeFactory;
import org.marketcetera.core.time.TimeFactoryImpl;
import org.marketcetera.ors.LongIDFactory;
import org.marketcetera.ors.Principals;
import org.marketcetera.ors.security.SimpleUser;
import org.marketcetera.persist.PersistenceException;
import org.marketcetera.trade.*;
import org.marketcetera.util.log.SLF4JLoggerProxy;
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
    private DateTime mPurgeDate;
    /**
     * pattern used to identify a history threshold value expressed in seconds
     */
    private static final Pattern SECOND_INTERVAL = Pattern.compile("\\d{1,}[s|S]{1}");
    /**
     * pattern used to identify a history threshold value expressed in minutes
     */
    private static final Pattern MINUTE_INTERVAL = Pattern.compile("\\d{1,}[m|M]{1}");
    /**
     * pattern used to identify a history threshold value expressed in hours
     */
    private static final Pattern HOUR_INTERVAL = Pattern.compile("\\d{1,}[h|H]{1}");
    /**
     * pattern used to identify a history threshold value expressed in days
     */
    private static final Pattern DAY_INTERVAL = Pattern.compile("\\d{1,}[d|D]{1}");
    /**
     * pattern used to identify a history threshold value expressed in weeks
     */
    private static final Pattern WEEK_INTERVAL = Pattern.compile("\\d{1,}[w|W]{1}");
    /**
     * creates time values
     */
    private TimeFactory timeFactory = new TimeFactoryImpl();
    // CONSTRUCTORS.

    /**
     * Creates a new basic report history services provider.
     */

    public BasicReportHistoryServices() {}


    // ReportHistoryServices.

    @Override
    public void init(IDFactory idFactory,
                     JmsManager jmsManager,
                     ReportSavedListener reportSavedListener)
            throws ReportPersistenceException, PersistenceException
    {
        mReportIDFactory=new LongIDFactory(idFactory);
        mJmsManager=jmsManager;
        mReportSavedListener=reportSavedListener;
        // purge report history, if necessary
        if(mPurgeDate != null) {
            SLF4JLoggerProxy.info(this,
                                  "Purging report history before {}",
                                  mPurgeDate);
            int count = PersistentReport.deleteBefore(mPurgeDate.toDate());
            SLF4JLoggerProxy.info(this,
                                  "{} history record(s) purged",
                                  count);
        }
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
    /* (non-Javadoc)
     * @see org.marketcetera.ors.history.ReportHistoryServices#getOpenOrders(org.marketcetera.ors.security.SimpleUser)
     */
    @Override
    public List<ReportBase> getOpenOrders(SimpleUser inUser)
            throws PersistenceException
    {
        List<ReportBase> reports = new ArrayList<ReportBase>();
        List<ExecutionReportSummary> rawReports = ExecutionReportSummary.getOpenOrders(inUser);
        try {
            for(ExecutionReportSummary summary : rawReports) {
                reports.add(summary.getReport().toReport());
            }
        } catch (ReportPersistenceException e) {
            throw new PersistenceException(e);
        }
        return reports;
    }
    @Override
    public Map<PositionKey<Equity>, BigDecimal> getAllEquityPositionsAsOf
        (SimpleUser inUser,
         Date inDate)
        throws PersistenceException
    {
        return ExecutionReportSummary.getAllEquityPositionsAsOf(inUser,inDate);
    }
    
    
    @Override
    public BigDecimal getCurrencyPositionAsOf
        (SimpleUser inUser,
         Date inDate,
         Currency inCurrency)
        throws PersistenceException
    {
        return ExecutionReportSummary.getCurrencyPositionAsOf
            (inUser,inDate,inCurrency);
    }
    
    /* (non-Javadoc)
     * @see org.marketcetera.ors.history.ReportHistoryServices#getAllCurrencyPositionsAsOf(org.marketcetera.ors.security.SimpleUser, java.util.Date)
     */
    @Override
    public Map<PositionKey<Currency>, BigDecimal> getAllCurrencyPositionsAsOf
        (SimpleUser inUser,
         Date inDate)
        throws PersistenceException
    {
        return ExecutionReportSummary.getAllCurrencyPositionsAsOf(inUser,inDate);
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
    /* (non-Javadoc)
     * @see org.marketcetera.ors.history.ReportHistoryServices#delete(org.marketcetera.trade.ReportBase)
     */
    @Override
    public void delete(ReportBase inReport)
            throws PersistenceException
    {
        PersistentReport.delete(inReport);
    }
    @Override
    public Principals getPrincipals
        (OrderID orderID)
        throws PersistenceException
    {
        return PersistentReport.getPrincipals(orderID);
    }
    /**
     * Get the purgeDate value.
     *
     * @return a <code>String</code> value
     */
    public String getPurgeDate()
    {
        return String.valueOf(mPurgeDate);
    }
    /**
     * Sets the purgeDate value.
     * 
     * <p>Purge date describes a point in time UTC before which all report history
     * should be truncated. May be described as an actual point in time:
     * <ul>
     *   <li>YYYYMMDD-HH:MM:SS</li>
     *   <li>HH:MM:SS</li>
     *   <li>HH:MM</li>
     * </ul>
     * May also be described as a relative point in time:
     * <ul>
     *   <li>4w</li>
     *   <li>30d</li>
     *   <li>3h</li>
     *   <li>120m</li>
     *   <li>10s</li>
     * </ul>
     *
     * @param inPurgeDate a <code>String</code> value
     */
    public void setPurgeDate(String inPurgeDate)
    {
        mPurgeDate = translateHistoryValue(inPurgeDate);
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
    /**
     * Translates the given literal value to a <code>DateTime</code> value.
     *
     * @param inHistoryValue a <code>String</code> value
     * @return a <code>DateTime</code> value
     */
    private DateTime translateHistoryValue(String inHistoryValue)
    {
        inHistoryValue = StringUtils.trimToNull(inHistoryValue);
        if(inHistoryValue == null) {
            return new DateTime(0);
        }
        if(SECOND_INTERVAL.matcher(inHistoryValue).matches()) {
            int seconds = Integer.parseInt(inHistoryValue.substring(0,inHistoryValue.length()-1));
            return new DateTime().minusSeconds(seconds);
        }
        if(MINUTE_INTERVAL.matcher(inHistoryValue).matches()) {
            int minutes = Integer.parseInt(inHistoryValue.substring(0,inHistoryValue.length()-1));
            return new DateTime().minusMinutes(minutes);
        }
        if(HOUR_INTERVAL.matcher(inHistoryValue).matches()) {
            int hours = Integer.parseInt(inHistoryValue.substring(0,inHistoryValue.length()-1));
            return new DateTime().minusHours(hours);
        }
        if(DAY_INTERVAL.matcher(inHistoryValue).matches()) {
            int days = Integer.parseInt(inHistoryValue.substring(0,inHistoryValue.length()-1));
            return new DateTime().minusDays(days);
        }
        if(WEEK_INTERVAL.matcher(inHistoryValue).matches()) {
            int weeks = Integer.parseInt(inHistoryValue.substring(0,inHistoryValue.length()-1));
            return new DateTime().minusWeeks(weeks);
        }
        return timeFactory.create(inHistoryValue);
    }
}
