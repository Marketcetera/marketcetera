package org.marketcetera.ors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;

import javax.persistence.TemporalType;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.marketcetera.core.IDFactory;
import org.marketcetera.core.LoggerConfiguration;
import org.marketcetera.core.position.PositionKey;
import org.marketcetera.core.position.impl.PositionKeyImpl;
import org.marketcetera.event.HasFIXMessage;
import org.marketcetera.module.ExpectedFailure;
import org.marketcetera.ors.dao.ReportService;
import org.marketcetera.ors.dao.UserService;
import org.marketcetera.ors.history.ExecutionReportSummary;
import org.marketcetera.ors.history.ReportHistoryServices;
import org.marketcetera.ors.security.SimpleUser;
import org.marketcetera.persist.PersistenceException;
import org.marketcetera.quickfix.FIXMessageFactory;
import org.marketcetera.quickfix.FIXVersion;
import org.marketcetera.trade.*;
import org.marketcetera.util.misc.RandomStrings;
import org.marketcetera.util.misc.UCPFilter;
import org.marketcetera.util.test.TestCaseBase;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;

import quickfix.Message;
import quickfix.field.ClOrdID;
import quickfix.field.OrigClOrdID;
import quickfix.field.SendingTime;

/* $License$ */

/**
 * Provides Spring-based persistence services for tests.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@RunWith(SpringJUnit4ClassRunner.class)
@TransactionConfiguration(defaultRollback=true)
@ContextConfiguration(locations={"file:src/test/sample_data/conf/server.xml"})
public class PersistTestBase
        extends TestCaseBase
        implements ApplicationContextAware
{
    /**
     * Runs once before all tests.
     *
     * @throws Exception if an unexpected error occurs
     */
    @BeforeClass
    public static void once()
            throws Exception
    {
        LoggerConfiguration.logSetup();
    }
    /* (non-Javadoc)
     * @see org.springframework.context.ApplicationContextAware#setApplicationContext(org.springframework.context.ApplicationContext)
     */
    @Override
    public void setApplicationContext(ApplicationContext inContext)
            throws BeansException
    {
        messageFactory = FIXVersion.FIX_SYSTEM.getMessageFactory();
        context = (ConfigurableApplicationContext)inContext;
        context.registerShutdownHook();
        userService = context.getBean(UserService.class);
        reportService = context.getBean(ReportService.class);
        IDFactory idFactory = context.getBean(IDFactory.class);
        reportHistoryServices = context.getBean(ReportHistoryServices.class);
        reportHistoryServices.init(idFactory,
                                   null,
                                   null);
        actor = new SimpleUser();
        actor.setName("actor");
        actor.setPassword("pass".toCharArray());
        actor.setActive(true);
        actor.setSuperuser(true);
        actor = userService.save(actor);
        actorID = new UserID(actor.getId());
        viewer = new SimpleUser();
        viewer.setName("viewer");
        viewer.setPassword("pass2".toCharArray());
        viewer.setActive(true);
        viewer.setSuperuser(false);
        viewer = userService.save(viewer);
        viewerID = new UserID(viewer.getId());
        extraUser = new SimpleUser();
        extraUser.setName("extra");
        extraUser.setPassword("pass2".toCharArray());
        extraUser.setActive(true);
        extraUser.setSuperuser(false);
        extraUser = userService.save(extraUser);
        extraUserID = new UserID(extraUser.getId());
    }
    /**
     * Runs after each test.
     *
     * @throws Exception if an unexpected error occurs
     */
    @After
    public void cleanTables()
            throws Exception
    {
        reportService.purgeReportsBefore(new Date());
        List<SimpleUser> allUsers = userService.findAll();
        for(SimpleUser user : allUsers) {
            userService.delete(user);
        }
        //Verify everything's gone
        assertTrue(userService.findAll().isEmpty());
        assertTrue(reportService.findAllExecutionReportSummary().isEmpty());
        assertTrue(reportService.findAllPersistentReport().isEmpty());
    }
    /**
     * Generates an unpersisted user with unique name and password values.
     *
     * @return a <code>SimpleUser</code> value
     * @throws Exception if an error occurs generating the user
     */
    protected SimpleUser generateUser()
            throws Exception
    {
        SimpleUser user = new SimpleUser();
        user.setName(randomString());
        String password = randomString();
        user.setPassword(password.toCharArray());
        user.setActive(true);
        user.setSuperuser(false);
        return user;
    }
    /**
     * Generates a <code>ReportBase</code> object with a unique {@link ReportID}.
     *
     * @return a <code>ReportBase</code> value
     * @throws Exception if an error occurs generating the user
     */
    protected ReportBase generateReport()
            throws Exception
    {
        ReportBaseImpl report = (ReportBaseImpl)createExecReport("orderid-" + counter.incrementAndGet(),
                                                                 null,
                                                                 new Equity("METC"),
                                                                 Side.Buy,
                                                                 OrderStatus.New,
                                                                 BigDecimal.TEN,
                                                                 BigDecimal.TEN,
                                                                 BigDecimal.TEN,
                                                                 BigDecimal.TEN,
                                                                 BROKER,
                                                                 ACCOUNT,
                                                                 TEXT,
                                                                 actorID,
                                                                 viewerID);
        ReportBaseImpl.assignReportID(report,
                                      new ReportID(counter.incrementAndGet()));
        return report;
    }
    /**
     * 
     *
     *
     * @param d1
     * @param d2
     * @param type
     */
    public void assertCalendarEquals(Date d1,
                                     Date d2,
                                     TemporalType type)
    {
        if(d1 == null && d2 == null) {
            return;
        }
        if(d1 == null || d2 == null) {
            fail("expected<" + d1 + "> actual<" + d2 + ">"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        }
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        c1.setTime(d1);
        c2.setTime(d2);
        if (type == TemporalType.DATE || type == TemporalType.TIMESTAMP) {
            org.junit.Assert.assertEquals(c1.get(Calendar.YEAR),c2.get(Calendar.YEAR));
            org.junit.Assert.assertEquals(c1.get(Calendar.MONTH),c2.get(Calendar.MONTH));
            org.junit.Assert.assertEquals(c1.get(Calendar.DAY_OF_MONTH),c2.get(Calendar.DAY_OF_MONTH));
        }
        if (type == TemporalType.TIME || type == TemporalType.TIMESTAMP) {
            org.junit.Assert.assertEquals(c1.get(Calendar.HOUR_OF_DAY),c2.get(Calendar.HOUR_OF_DAY));
            org.junit.Assert.assertEquals(c1.get(Calendar.MINUTE),c2.get(Calendar.MINUTE));
            org.junit.Assert.assertEquals(c1.get(Calendar.SECOND),c2.get(Calendar.SECOND));
        }
    }
    /**
     * Generates a string with random values.
     *
     * @return a <code>String</code> value
     */
    protected String randomString()
    {
        return RandomStrings.genStr(PersistUCPFilter.INSTANCE, 10);
    }
    /**
     * Returns a random name string that only has ASCII characters.
     * The returned string will pass the name validations
     * used within NDEntityTestBase instances
     *
     * @return a random name string.
     */
    protected String randomNameString()
    {
        return RandomStrings.genStr(PersistNameStringFilter.INSTANCE, 10);
    }
    /**
     * 
     *
     *
     * @return
     * @throws MessageCreationException
     */
    protected OrderCancelReject createCancelReject()
            throws MessageCreationException
    {
        return createCancelReject(viewerID);
    }
    /**
     * 
     *
     *
     * @param inViewerID
     * @return
     * @throws MessageCreationException
     */
    protected OrderCancelReject createCancelReject(UserID inViewerID)
            throws MessageCreationException
    {
        return createCancelReject(BROKER,
                                  inViewerID);
    }
    /**
     * 
     *
     *
     * @param inBrokerID
     * @param inViewerID
     * @return
     * @throws MessageCreationException
     */
    protected OrderCancelReject createCancelReject(BrokerID inBrokerID,
                                                   UserID inViewerID)
            throws MessageCreationException
    {
        return createCancelReject(inBrokerID,
                                  actorID,
                                  inViewerID);
    }
    /**
     * 
     *
     *
     * @param inBrokerID
     * @param inActorID
     * @param inViewerID
     * @return
     * @throws MessageCreationException
     */
    protected OrderCancelReject createCancelReject(BrokerID inBrokerID,
                                                UserID inActorID,
                                                UserID inViewerID)
            throws MessageCreationException
    {
        Message msg = messageFactory.newOrderCancelReject();
        msg.setField(new ClOrdID("rejord1"));
        msg.setField(new OrigClOrdID("rejorigord1"));
        setSendingTime(msg);
        return Factory.getInstance().createOrderCancelReject
            (msg, inBrokerID, Originator.Server, inActorID, inViewerID);
    }
    /**
     * 
     *
     *
     * @param inOrderID
     * @param inOrigOrderID
     * @param inInstrument
     * @param inSide
     * @param inOrderStatus
     * @param inCumQuantity
     * @param inAvgPrice
     * @param inLastQty
     * @param inLastPrice
     * @return
     * @throws Exception
     */
    protected ExecutionReport createExecReport(String inOrderID,
                                               String inOrigOrderID,
                                               Instrument inInstrument,
                                               Side inSide,
                                               OrderStatus inOrderStatus,
                                               BigDecimal inCumQuantity,
                                               BigDecimal inAvgPrice,
                                               BigDecimal inLastQty,
                                               BigDecimal inLastPrice)
            throws Exception
    {
        return createExecReport(inOrderID,
                                inOrigOrderID,
                                inInstrument,
                                inSide,
                                inOrderStatus,
                                inCumQuantity,
                                inAvgPrice,
                                inLastQty,
                                inLastPrice,
                                viewerID);
    }
    /**
     * 
     *
     *
     * @param inOrderID
     * @param inOrigOrderID
     * @param inInstrument
     * @param inSide
     * @param inOrderStatus
     * @param inCumQuantity
     * @param inAvgPrice
     * @param inLastQty
     * @param inLastPrice
     * @param inViewerID
     * @return
     * @throws Exception
     */
    protected ExecutionReport createExecReport(String inOrderID,
                                            String inOrigOrderID,
                                            Instrument inInstrument,
                                            Side inSide,
                                            OrderStatus inOrderStatus,
                                            BigDecimal inCumQuantity,
                                            BigDecimal inAvgPrice,
                                            BigDecimal inLastQty,
                                            BigDecimal inLastPrice,
                                            UserID inViewerID)
            throws Exception
    {
        return createExecReport(inOrderID,
                                inOrigOrderID,
                                inInstrument,
                                inSide,
                                inOrderStatus,
                                inCumQuantity,
                                inAvgPrice,
                                inLastQty,
                                inLastPrice,
                                BROKER,
                                inViewerID);
    }
    /**
     * 
     *
     *
     * @param inOrderID
     * @param inOrigOrderID
     * @param inInstrument
     * @param inSide
     * @param inOrderStatus
     * @param inCumQuantity
     * @param inAvgPrice
     * @param inLastQty
     * @param inLastPrice
     * @param inBrokerID
     * @param inViewerID
     * @return
     * @throws Exception
     */
    protected ExecutionReport createExecReport(String inOrderID,
                                               String inOrigOrderID,
                                               Instrument inInstrument,
                                               Side inSide,
                                               OrderStatus inOrderStatus,
                                               BigDecimal inCumQuantity,
                                               BigDecimal inAvgPrice,
                                               BigDecimal inLastQty,
                                               BigDecimal inLastPrice,
                                               BrokerID inBrokerID,
                                               UserID inViewerID)
            throws Exception
    {
        return createExecReport(inOrderID,
                                inOrigOrderID,
                                inInstrument,
                                inSide,
                                inOrderStatus,
                                inCumQuantity,
                                inAvgPrice,
                                inLastQty,
                                inLastPrice,
                                inBrokerID,
                                actorID,
                                inViewerID);
    }
    /**
     * 
     *
     *
     * @param inOrderID
     * @param inOrigOrderID
     * @param inInstrument
     * @param inSide
     * @param inOrderStatus
     * @param inCumQuantity
     * @param inAvgPrice
     * @param inLastQty
     * @param inLastPrice
     * @param inBrokerID
     * @param inActorID
     * @param inViewerID
     * @return
     * @throws Exception
     */
    protected ExecutionReport createExecReport(String inOrderID,
                                               String inOrigOrderID,
                                               Instrument inInstrument,
                                               Side inSide,
                                               OrderStatus inOrderStatus,
                                               BigDecimal inCumQuantity,
                                               BigDecimal inAvgPrice,
                                               BigDecimal inLastQty,
                                               BigDecimal inLastPrice,
                                               BrokerID inBrokerID,
                                               UserID inActorID,
                                               UserID inViewerID)
            throws Exception
    {
        return createExecReport(inOrderID,
                                inOrigOrderID,
                                inInstrument,
                                inSide,
                                inOrderStatus,
                                inCumQuantity,
                                inAvgPrice,
                                inLastQty,
                                inLastPrice,
                                inBrokerID,
                                ACCOUNT,
                                TEXT,
                                inActorID,
                                inViewerID);
    }
    /**
     * 
     *
     *
     * @param inOrderID
     * @param inOrigOrderID
     * @param inInstrument
     * @param inSide
     * @param inOrderStatus
     * @param inCumQuantity
     * @param inAvgPrice
     * @param inLastQty
     * @param inLastPrice
     * @param inBrokerID
     * @param inAccount
     * @param inText
     * @param inActorID
     * @param inViewerID
     * @return
     * @throws Exception
     */
    protected ExecutionReport createExecReport(String inOrderID,
                                               String inOrigOrderID,
                                               Instrument inInstrument,
                                               Side inSide,
                                               OrderStatus inOrderStatus,
                                               BigDecimal inCumQuantity,
                                               BigDecimal inAvgPrice,
                                               BigDecimal inLastQty,
                                               BigDecimal inLastPrice,
                                               BrokerID inBrokerID,
                                               String inAccount,
                                               String inText,
                                               UserID inActorID,
                                               UserID inViewerID)
            throws Exception
    {
        return createExecReport(inOrderID,
                                inOrigOrderID,
                                "exec1",
                                inInstrument,
                                inSide,
                                inOrderStatus,
                                new BigDecimal("23.234"),
                                new BigDecimal("343.343"),
                                inCumQuantity,
                                inAvgPrice,
                                inLastQty,
                                inLastPrice,
                                inBrokerID,
                                inAccount,
                                inText,
                                inActorID,
                                inViewerID);
    }
    /**
     * Creates an <code>ExecutionReport</code> with the given attributes. 
     *
     * @param inOrderID a <code>String</code> value
     * @param inOrigOrderID a <code>String</code> value or <code>null</code>
     * @param inExecID a <code>String</code> value
     * @param inInstrument an <code>Instrument</code> value
     * @param inSide a <code>Side</code> value
     * @param inOrderStatus an <code>OrderStatus</code> value
     * @param inOrderQty a <code>BigDecimal</code> value
     * @param inOrderPrice a <code>BigDecimal</code> value
     * @param inCumQuantity a <code>BigDecimal</code> value
     * @param inAvgPrice a <code>BigDecimal</code> value
     * @param inLastQty a <code>BigDecimal</code> value
     * @param inLastPrice a <code>BigDecimal</code> value
     * @param inBrokerID a <code>BrokerID</code> value
     * @param inAccount a <code>BigDecimal</code> value
     * @param inText a <code>BigDecimal</code> value
     * @param inActorID a <code>UserID</code> value
     * @param inViewerID a <code>UserID</code> value
     * @return an <code>ExecutionReport</code> value
     * @throws Exception if an unexpected error occurs
     */
    protected ExecutionReport createExecReport(String inOrderID,
                                            String inOrigOrderID,
                                            String inExecID,
                                            Instrument inInstrument,
                                            Side inSide,
                                            OrderStatus inOrderStatus,
                                            BigDecimal inOrderQty,
                                            BigDecimal inOrderPrice,
                                            BigDecimal inCumQuantity,
                                            BigDecimal inAvgPrice,
                                            BigDecimal inLastQty,
                                            BigDecimal inLastPrice,
                                            BrokerID inBrokerID,
                                            String inAccount,
                                            String inText,
                                            UserID inActorID,
                                            UserID inViewerID)
            throws Exception
    {
        Message msg = messageFactory.newExecutionReport(inOrderID,
                                                         inOrderID,
                                                         inExecID,
                                                         inOrderStatus.getFIXValue(),
                                                         inSide.getFIXValue(),
                                                         inOrderQty,
                                                         inOrderPrice,
                                                         inLastQty,
                                                         inLastPrice,
                                                         inCumQuantity,
                                                         inAvgPrice,
                                                         inInstrument,
                                                         inAccount,
                                                         inText);
        if (inOrigOrderID != null) {
            msg.setField(new OrigClOrdID(inOrigOrderID));
        }
        setSendingTime(msg);
        return Factory.getInstance().createExecutionReport(msg,
                                                           inBrokerID,
                                                           Originator.Server,
                                                           inActorID,
                                                           inViewerID);
    }
    /**
     * 
     *
     *
     * @param inMsg
     */
    protected void setSendingTime(Message inMsg)
    {
        inMsg.getHeader().setField(new SendingTime(new Date()));
    }
    /**
     * 
     *
     *
     * @param inReport1
     * @param inReport2
     */
    protected void assertReportEquals(ReportBase inReport1,
                                      ReportBase inReport2)
    {
        if (inReport1 instanceof ExecutionReport) {
            TypesTestBase.assertExecReportEquals((ExecutionReport) inReport1,
                    (ExecutionReport)inReport2);
        } else {
            TypesTestBase.assertCancelRejectEquals(
                    (OrderCancelReject) inReport1,
                    (OrderCancelReject) inReport2);
        }
        TypesTestBase.assertFIXEquals(inReport1, inReport2);
        assertEquals(inReport1.getReportID(), inReport2.getReportID());

    }

    /**
     * Tests a not null ConstraintViolation Failure.
     *
     * @param inFieldName the field that is expected to have non-null
     * constraint violation failure.
     *
     * @param inTest the code snippet that will fail
     *
     * @throws Exception if there's an error.
     */
    protected void nonNullCVCheck(String inFieldName,
                                  final Callable<?> inTest)
            throws Exception
    {
        String exceptMsg = new ExpectedFailure<DataIntegrityViolationException>() {
            protected void run()
                    throws Exception
            {
                inTest.call();
            }
        }.getException().getLocalizedMessage();
        assertTrue(exceptMsg,
                   exceptMsg.contains(inFieldName));
    }

    protected ReportBase removeSendingTime(ReportBase inReport) {
        HasFIXMessage fix = (HasFIXMessage) inReport;
        fix.getMessage().getHeader().removeField(SendingTime.FIELD);
        assertEquals(null, inReport.getSendingTime());
        return inReport;
    }
    /**
     * 
     *
     *
     * @param inValue1
     * @param inValue2
     */
    protected void assertBigDecimalEquals(BigDecimal inValue1,
                                          BigDecimal inValue2)
    {
        if(inValue1 == null ^ inValue2 == null) {
            org.junit.Assert.fail(getFailString(inValue1, inValue2));
        }
        if(inValue1 == null) {
            return;
        }
        org.junit.Assert.assertTrue(getFailString(inValue1, inValue2),
                inValue1.compareTo(inValue2) == 0);
    }

    private static String getFailString(Object inValue1, Object inValue2) {
        return new StringBuilder().append("expected<").append(inValue1).
                append(">, actual<").append(inValue2).append(">").toString();
    }

    /**
     * Sleeps for the minimal time that the db can differentiate between.
     *
     * @throws InterruptedException if interrupted.
     */
    protected void sleepForSignificantTime() throws InterruptedException {
        //PersistTestBase.sleepForSignificantTime();
    }

    protected BigDecimal getPosition(Date inDate, Equity inEquity)
            throws Exception {
        return getPosition(inDate, inEquity, viewer);
    }
    
    protected BigDecimal getPosition(Date inDate, Equity inEquity, SimpleUser inViewer)
            throws Exception {
        return reportHistoryServices.getEquityPositionAsOf(inViewer, inDate, inEquity);
    }

    protected Map<PositionKey<Equity>,BigDecimal> getPositions(Date inDate)
            throws Exception {
        return getPositions(inDate, viewer);
    }
    
    protected BigDecimal getPosition(Date inDate, Currency inCurrency)
            throws Exception {
        return getPosition(inDate, inCurrency, viewer);
    }
    
    protected BigDecimal getPosition(Date inDate, Currency inCurrency, SimpleUser inViewer)
            throws Exception {
        return reportHistoryServices.getCurrencyPositionAsOf(inViewer, inDate, inCurrency);
    }

    protected Map<PositionKey<Currency>,BigDecimal> getCurrencyPositions(Date inDate)
            throws Exception {
        return getCurrencyPositions(inDate, viewer);
    }
    
    
    protected BigDecimal getPosition(Date inDate, Option inOption) throws Exception {
        return getPosition(inDate, inOption, viewer);
    }
    protected BigDecimal getPosition(Date inDate, Option inOption,
                                            SimpleUser inViewer)
            throws Exception {
        return reportHistoryServices.getOptionPositionAsOf(inViewer, inDate, inOption);
    }

    protected Map<PositionKey<Option>,BigDecimal> getAllOptionPositions(
            Date inDate) throws PersistenceException {
        return getAllOptionPositions(inDate, viewer);
    }
    protected Map<PositionKey<Option>,BigDecimal> getAllOptionPositions(
            Date inDate, SimpleUser inViewer) throws PersistenceException {
        return reportHistoryServices.getAllOptionPositionsAsOf(inViewer, inDate);
    }

    protected Map<PositionKey<Option>,BigDecimal> getOptionPositions(
            Date inDate, String... inSymbols) throws PersistenceException {
        return getOptionPositions(inDate, viewer,  inSymbols);
    }
    
    protected Map<PositionKey<Option>,BigDecimal> getOptionPositions(
            Date inDate, SimpleUser inViewer, String... inSymbols)
            throws PersistenceException {
        return reportHistoryServices.getOptionPositionsAsOf(inViewer, inDate, inSymbols);
    }

    protected Map<PositionKey<Equity>,BigDecimal> getPositions(Date inDate, SimpleUser inViewer)
            throws Exception {
        return reportHistoryServices.getAllEquityPositionsAsOf(inViewer, inDate);
    }
    
    protected Map<PositionKey<Currency>,BigDecimal> getCurrencyPositions(Date inDate, SimpleUser inViewer)
            throws Exception {
        return reportHistoryServices.getAllCurrencyPositionsAsOf(inViewer, inDate);
    }


    protected ExecutionReport createAndSaveER(String inOrderID,
                                            String inOrigOrderID,
                                            Instrument inInstrument,
                                            Side inSide,
                                            BigDecimal inCumQty) throws Exception {
        return createAndSaveER(inOrderID, inOrigOrderID, inInstrument,
                               inSide, inCumQty, viewerID);
    }
    /**
     * Creates and persists an <code>ExecutionReport</code> with the given attributes.
     *
     * @param inOrderID a <code>String</code> value
     * @param inOrigOrderID a <code>String</code> value
     * @param inInstrument an <code>Instrument</code> value
     * @param inSide a <code>Side</code> value
     * @param inCumQty a <code>BigDecimal</code> value
     * @param inOrderStatus an <code>OrderStatus</code> value
     * @return an <code>ExecutionReport</code> value
     * @throws Exception if an unexpected error occurs
     */
    protected ExecutionReport createAndSaveER(String inOrderID,
                                                     String inOrigOrderID,
                                                     Instrument inInstrument,
                                                     Side inSide,
                                                     BigDecimal inCumQty,
                                                     OrderStatus inOrderStatus)
            throws Exception
    {
        return createAndSaveER(inOrderID,
                               inOrigOrderID,
                               inInstrument,
                               inSide,
                               inCumQty,
                               viewerID,
                               inOrderStatus);
    }
    protected ExecutionReport createAndSaveER(String inOrderID,
                                            String inOrigOrderID,
                                            Instrument inInstrument,
                                            Side inSide,
                                            BigDecimal inCumQty,
                                            UserID inViewerID) throws Exception {
        return createAndSaveER(inOrderID,inOrigOrderID, inInstrument,inSide,
                               inCumQty,actorID,inViewerID);
    }
    /**
     * Creates and persists an <code>ExecutionReport</code> with the given attributes.
     *
     * @param inOrderID a <code>String</code> value
     * @param inOrigOrderID a <code>String</code> value
     * @param inInstrument an <code>Instrument</code> value
     * @param inSide a <code>Side</code> value
     * @param inCumQty a <code>BigDecimal</code> value
     * @param inViewerID a <code>UserID</code> value
     * @param inOrderStatus an <code>OrderStatus</code> value
     * @return an <code>ExecutionReport</code> value
     * @throws Exception if an unexpected error occurs
     */
    protected ExecutionReport createAndSaveER(String inOrderID,
                                                     String inOrigOrderID,
                                                     Instrument inInstrument,
                                                     Side inSide,
                                                     BigDecimal inCumQty,
                                                     UserID inViewerID,
                                                     OrderStatus inOrderStatus)
            throws Exception
    {
        return createAndSaveER(inOrderID,
                               inOrigOrderID,
                               inInstrument,inSide,
                               inCumQty,
                               actorID,
                               inViewerID,
                               inOrderStatus);
    }
    protected ExecutionReport createAndSaveER(String inOrderID,
                                            String inOrigOrderID,
                                            Instrument inInstrument,
                                            Side inSide,
                                            BigDecimal inCumQty,
                                            UserID inActorID,
                                            UserID inViewerID) throws Exception {
        return createAndSaveER(inOrderID,inOrigOrderID, inInstrument,inSide,
                               inCumQty, ACCOUNT, inActorID,inViewerID);
    }
    /**
     * Creates and persists an <code>ExecutionReport</code> with the given attributes.
     *
     * @param inOrderID a <code>String</code> value
     * @param inOrigOrderID a <code>String</code> value
     * @param inInstrument an <code>Instrument</code> value
     * @param inSide a <code>Side</code> value
     * @param inCumQty a <code>BigDecimal</code> value
     * @param inActorID a <code>UserID</code> value
     * @param inViewerID a <code>UserID</code> value
     * @param inOrderStatus an <code>OrderStatus</code> value
     * @return an <code>ExecutionReport</code> value
     * @throws Exception if an unexpected error occurs
     */
    protected ExecutionReport createAndSaveER(String inOrderID,
                                                     String inOrigOrderID,
                                                     Instrument inInstrument,
                                                     Side inSide,
                                                     BigDecimal inCumQty,
                                                     UserID inActorID,
                                                     UserID inViewerID,
                                                     OrderStatus inOrderStatus)
            throws Exception
    {
        return createAndSaveER(inOrderID,
                               inOrigOrderID,
                               inInstrument,
                               inSide,
                               inCumQty,
                               ACCOUNT,
                               inActorID,
                               inViewerID,
                               inOrderStatus);
    }
    protected ExecutionReport createAndSaveER(String inOrderID,
                                                     String inOrigOrderID,
                                                     Instrument inInstrument,
                                                     Side inSide,
                                                     BigDecimal inCumQty,
                                                     String inAccount,
                                                     UserID inActorID,
                                                     UserID inViewerID)
            throws Exception
    {
        return createAndSaveER(inOrderID,
                               inOrigOrderID,
                               inInstrument,
                               inSide,
                               inCumQty,
                               inAccount,
                               inActorID,
                               inViewerID,
                               OrderStatus.PartiallyFilled);
    }
    /**
     * Creates and persists an <code>ExecutionReport</code> with the given attributes.
     *
     * @param inOrderID a <code>String</code> value
     * @param inOrigOrderID a <code>String</code> value
     * @param inInstrument an <code>Instrument</code> value
     * @param inSide a <code>Side</code> value
     * @param inCumQty a <code>BigDecimal</code> value
     * @param inAccount a <code>String</code> value
     * @param inActorID a <code>UserID</code> value
     * @param inViewerID a <code>UserID</code> value
     * @param inOrderStatus an <code>OrderStatus</code> value
     * @return an <code>ExecutionReport</code> value
     * @throws Exception if an unexpected error occurs
     */
    protected ExecutionReport createAndSaveER(String inOrderID,
                                                     String inOrigOrderID,
                                                     Instrument inInstrument,
                                                     Side inSide,
                                                     BigDecimal inCumQty,
                                                     String inAccount,
                                                     UserID inActorID,
                                                     UserID inViewerID,
                                                     OrderStatus inOrderStatus)
            throws Exception
    {
        sleepForSignificantTime();
        ExecutionReport report = createExecReport(inOrderID,
                                                  inOrigOrderID,
                                                  inInstrument,
                                                  inSide,
                                                  inOrderStatus,
                                                  inCumQty,
                                                  BigDecimal.TEN,
                                                  BigDecimal.TEN,
                                                  BigDecimal.TEN,
                                                  BROKER,
                                                  inAccount,
                                                  "text",
                                                  inActorID,
                                                  inViewerID);
        reportHistoryServices.save(report);
        sleepForSignificantTime();
        return report;
    }
    protected <T extends Instrument> PositionKey<T> pos(T inInstrument) {
        return pos(inInstrument,ACCOUNT,actorID);
    }

    protected <T extends Instrument> PositionKey<T> pos(T inInstrument,
                                                               String inAccount,
                                                               UserID inActor) {
        return new PositionKeyImpl<T>(inInstrument, inAccount,
                Long.toString(inActor.getValue()));
    }
    
    protected <T extends Instrument> PositionKey<T> pos(T inSymbol,
                                                               String inAccount,
                                                               String inActor) {
        return new PositionKeyImpl<T>(inSymbol, inAccount, inActor);
    }

    protected Matcher<Map<?,?>> isOfSize(int inLength) {
        return new SizeMatcher(inLength);
    }

    private class SizeMatcher extends TypeSafeMatcher<Map<?,?>> {
        SizeMatcher(int inExpectedSize) {
            mExpectedSize = inExpectedSize;
        }

        private final int mExpectedSize;

        @Override
        public void describeTo(Description inDescription) {
            inDescription.appendText("map of length ").appendValue(mExpectedSize);
        }

        @Override
        public boolean matchesSafely(Map<?,?> inMap) {
            return inMap.size() == mExpectedSize;
        }
    }
    /**
     * UCP Filter used for generating characters for testing.
     */
    private static class PersistUCPFilter
            extends UCPFilter
    {
        static final PersistUCPFilter INSTANCE = new PersistUCPFilter();
        public boolean isAcceptable(int ucp) {
            // mysql version dependency: this piece of code depends
            // on specific version of mysql and may need to be updated
            // whenever mysql version is updated
            return Character.isLetterOrDigit(ucp) &&
                    //Make sure its a character in the range that mysql can handle
                    UCPFilter.CHAR.isAcceptable(ucp) && 
                    //mysql doesn't support supplementary code points.
                    (!Character.isSupplementaryCodePoint(ucp));
        }
    }
    /**
     * UCP Filter used to generate ASCII characters that are
     * letters & digit for testing.
     */
    private static class PersistNameStringFilter
            extends UCPFilter
    {
        static final PersistNameStringFilter INSTANCE = new PersistNameStringFilter();
        public boolean isAcceptable(int ucp) {
            return ucp >= 32 && ucp <= 127 && Character.isLetterOrDigit(ucp); 
        }
    }
    protected FIXMessageFactory messageFactory;
    protected SimpleUser actor;
    protected SimpleUser viewer;
    protected SimpleUser extraUser;
    protected UserID actorID;
    protected UserID viewerID;
    protected UserID extraUserID;
    protected UserService userService;
    protected ReportService reportService;
    protected ConfigurableApplicationContext context;
    protected ReportHistoryServices reportHistoryServices;
    protected static final BrokerID BROKER = new BrokerID("TestBroker");
    protected static final String ACCOUNT = "account";
    protected static final String TEXT = "text";
    protected static final int SCALE = ExecutionReportSummary.DECIMAL_SCALE;
    protected static final AtomicInteger counter = new AtomicInteger(0);
}
