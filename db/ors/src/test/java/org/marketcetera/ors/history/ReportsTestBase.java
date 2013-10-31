package org.marketcetera.ors.history;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.After;
import org.junit.Before;
import org.marketcetera.core.InMemoryIDFactory;
import org.marketcetera.core.position.PositionKey;
import org.marketcetera.core.position.impl.PositionKeyImpl;
import org.marketcetera.event.HasFIXMessage;
import org.marketcetera.module.ExpectedFailure;
import org.marketcetera.ors.PersistTestBase;
import org.marketcetera.ors.security.SimpleUser;
import org.marketcetera.persist.PersistenceException;
import org.marketcetera.quickfix.FIXMessageFactory;
import org.marketcetera.quickfix.FIXVersion;
import org.marketcetera.trade.*;

import quickfix.Message;
import quickfix.field.ClOrdID;
import quickfix.field.OrigClOrdID;
import quickfix.field.SendingTime;

/* $License$ */
/**
 * Base class for various persistence tests.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 1.0.0
 */
@SuppressWarnings("rawtypes")
public class ReportsTestBase
        extends PersistTestBase
{
    /**
     * Runs before each test.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Before
    public void before()
            throws Exception
    {
        sMessageFactory = FIXVersion.FIX_SYSTEM.getMessageFactory();
        InMemoryIDFactory idFactory=new InMemoryIDFactory(0);
        idFactory.init();
        sServices = context.getBean(ReportHistoryServices.class);
        sServices.init(idFactory,
                       null,
                       null);
        sActor=new SimpleUser();
        sActor.setName("actor");
        sActor.setPassword("pass".toCharArray());
        sActor.setActive(true);
        sActor.setSuperuser(true);
        sActorID=new UserID(sActor.getId());

        sViewer=new SimpleUser();
        sViewer.setName("viewer");
        sViewer.setPassword("pass2".toCharArray());
        sViewer.setActive(true);
        sViewer.setSuperuser(false);
        sViewerID=new UserID(sViewer.getId());

        sExtraUser=new SimpleUser();
        sExtraUser.setName("extra");
        sExtraUser.setPassword("pass2".toCharArray());
        sExtraUser.setActive(true);
        sExtraUser.setSuperuser(false);
        sExtraUserID=new UserID(sExtraUser.getId());
        userService.save(sActor);
        userService.save(sViewer);
        userService.save(sExtraUser);
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
        //Verify everything's gone // TODO
//        assertEquals(0, erQuery.fetch().size());
//        assertEquals(0, prQuery.fetchCount());
//        assertEquals(0, prQuery.fetch().size());
    }

    OrderCancelReject createCancelReject()
            throws MessageCreationException {
        return createCancelReject(sViewerID);
    }
    OrderCancelReject createCancelReject(UserID inViewerID)
            throws MessageCreationException {
        return createCancelReject(BROKER,inViewerID);
    }
    OrderCancelReject createCancelReject(BrokerID inBrokerID,
                                                UserID inViewerID)
            throws MessageCreationException {
        return createCancelReject(inBrokerID,sActorID,inViewerID);
    }
    OrderCancelReject createCancelReject(BrokerID inBrokerID,
                                                UserID inActorID,
                                                UserID inViewerID)
            throws MessageCreationException {
        Message msg = sMessageFactory.newOrderCancelReject();
        msg.setField(new ClOrdID("rejord1"));
        msg.setField(new OrigClOrdID("rejorigord1"));
        setSendingTime(msg);
        return Factory.getInstance().createOrderCancelReject
            (msg, inBrokerID, Originator.Server, inActorID, inViewerID);
    }

    ExecutionReport createExecReport(String inOrderID,
                                            String inOrigOrderID,
                                            Instrument inInstrument,
                                            Side inSide,
                                            OrderStatus inOrderStatus,
                                            BigDecimal inCumQuantity,
                                            BigDecimal inAvgPrice,
                                            BigDecimal inLastQty,
                                            BigDecimal inLastPrice)
            throws Exception {
        return createExecReport(inOrderID, inOrigOrderID,
                inInstrument, inSide, inOrderStatus, inCumQuantity, inAvgPrice,
                inLastQty, inLastPrice, sViewerID);
    }
    ExecutionReport createExecReport(String inOrderID,
                                            String inOrigOrderID,
                                            Instrument inInstrument,
                                            Side inSide,
                                            OrderStatus inOrderStatus,
                                            BigDecimal inCumQuantity,
                                            BigDecimal inAvgPrice,
                                            BigDecimal inLastQty,
                                            BigDecimal inLastPrice,
                                            UserID inViewerID)
            throws Exception {
        return createExecReport(inOrderID, inOrigOrderID, inInstrument,
                inSide, inOrderStatus, inCumQuantity, inAvgPrice,
                inLastQty, inLastPrice, BROKER, inViewerID);
    }
    ExecutionReport createExecReport(String inOrderID,
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
            throws Exception {
        return createExecReport(inOrderID, inOrigOrderID, inInstrument,
                inSide, inOrderStatus, inCumQuantity, inAvgPrice,
                inLastQty, inLastPrice, inBrokerID, sActorID, inViewerID);
    }
    static ExecutionReport createExecReport(String inOrderID,
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
            throws Exception {
        return createExecReport
            (inOrderID, inOrigOrderID, inInstrument,
             inSide, inOrderStatus, inCumQuantity, inAvgPrice,
             inLastQty, inLastPrice, inBrokerID, ACCOUNT, TEXT,
             inActorID, inViewerID);
    }
    static ExecutionReport createExecReport(String inOrderID,
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
    static ExecutionReport createExecReport(String inOrderID,
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
        Message msg = sMessageFactory.newExecutionReport(inOrderID,
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

    private static void setSendingTime(Message inMsg) {
        inMsg.getHeader().setField(new SendingTime(new Date()));
    }

    static String[] getSpringFiles() {
        return new String[] {
            "file:"+DIR_ROOT+ File.separator+ //$NON-NLS-1$
            "conf"+File.separator+ //$NON-NLS-1$
            "persist_tests.xml"}; //$NON-NLS-1$
    }

    static void assertReportEquals(ReportBase inReport1,
                                   ReportBase inReport2) {
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
    protected static void nonNullCVCheck(String inFieldName,
                                         final Callable<?> inTest)
            throws Exception {
        String exceptMsg = new ExpectedFailure<PersistenceException>() {
            protected void run() throws Exception {
                inTest.call();
            }
        }.getException().getLocalizedDetail();
        assertTrue(exceptMsg, exceptMsg.contains(inFieldName));
    }

    protected static ReportBase removeSendingTime(ReportBase inReport) {
        HasFIXMessage fix = (HasFIXMessage) inReport;
        fix.getMessage().getHeader().removeField(SendingTime.FIELD);
        assertEquals(null, inReport.getSendingTime());
        return inReport;
    }

    static void assertBigDecimalEquals(BigDecimal inValue1, BigDecimal inValue2) {
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
    protected static void sleepForSignificantTime() throws InterruptedException {
        //PersistTestBase.sleepForSignificantTime();
    }

    protected BigDecimal getPosition(Date inDate, Equity inEquity)
            throws Exception {
        return getPosition(inDate, inEquity, sViewer);
    }
    
    protected BigDecimal getPosition(Date inDate, Equity inEquity, SimpleUser inViewer)
            throws Exception {
        return sServices.getEquityPositionAsOf(inViewer, inDate, inEquity);
    }

    protected Map<PositionKey<Equity>,BigDecimal> getPositions(Date inDate)
            throws Exception {
        return getPositions(inDate, sViewer);
    }
    
    protected BigDecimal getPosition(Date inDate, Currency inCurrency)
            throws Exception {
        return getPosition(inDate, inCurrency, sViewer);
    }
    
    protected BigDecimal getPosition(Date inDate, Currency inCurrency, SimpleUser inViewer)
            throws Exception {
        return sServices.getCurrencyPositionAsOf(inViewer, inDate, inCurrency);
    }

    protected Map<PositionKey<Currency>,BigDecimal> getCurrencyPositions(Date inDate)
            throws Exception {
        return getCurrencyPositions(inDate, sViewer);
    }
    
    
    protected BigDecimal getPosition(Date inDate, Option inOption) throws Exception {
        return getPosition(inDate, inOption, sViewer);
    }
    protected BigDecimal getPosition(Date inDate, Option inOption,
                                            SimpleUser inViewer)
            throws Exception {
        return sServices.getOptionPositionAsOf(inViewer, inDate, inOption);
    }

    protected Map<PositionKey<Option>,BigDecimal> getAllOptionPositions(
            Date inDate) throws PersistenceException {
        return getAllOptionPositions(inDate, sViewer);
    }
    protected Map<PositionKey<Option>,BigDecimal> getAllOptionPositions(
            Date inDate, SimpleUser inViewer) throws PersistenceException {
        return sServices.getAllOptionPositionsAsOf(inViewer, inDate);
    }

    protected Map<PositionKey<Option>,BigDecimal> getOptionPositions(
            Date inDate, String... inSymbols) throws PersistenceException {
        return getOptionPositions(inDate, sViewer,  inSymbols);
    }
    
    protected Map<PositionKey<Option>,BigDecimal> getOptionPositions(
            Date inDate, SimpleUser inViewer, String... inSymbols)
            throws PersistenceException {
        return sServices.getOptionPositionsAsOf(inViewer, inDate, inSymbols);
    }

    protected Map<PositionKey<Equity>,BigDecimal> getPositions(Date inDate, SimpleUser inViewer)
            throws Exception {
        return sServices.getAllEquityPositionsAsOf(inViewer, inDate);
    }
    
    protected Map<PositionKey<Currency>,BigDecimal> getCurrencyPositions(Date inDate, SimpleUser inViewer)
            throws Exception {
        return sServices.getAllCurrencyPositionsAsOf(inViewer, inDate);
    }


    protected ExecutionReport createAndSaveER(String inOrderID,
                                            String inOrigOrderID,
                                            Instrument inInstrument,
                                            Side inSide,
                                            BigDecimal inCumQty) throws Exception {
        return createAndSaveER(inOrderID, inOrigOrderID, inInstrument,
                               inSide, inCumQty, sViewerID);
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
                               sViewerID,
                               inOrderStatus);
    }
    protected ExecutionReport createAndSaveER(String inOrderID,
                                            String inOrigOrderID,
                                            Instrument inInstrument,
                                            Side inSide,
                                            BigDecimal inCumQty,
                                            UserID inViewerID) throws Exception {
        return createAndSaveER(inOrderID,inOrigOrderID, inInstrument,inSide,
                               inCumQty,sActorID,inViewerID);
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
                               sActorID,
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
        sServices.save(report);
        sleepForSignificantTime();
        return report;
    }
    protected <T extends Instrument> PositionKey<T> pos(T inInstrument) {
        return pos(inInstrument,ACCOUNT,sActorID);
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

    protected Matcher<Map> isOfSize(int inLength) {
        return new SizeMatcher(inLength);
    }

    private class SizeMatcher extends TypeSafeMatcher<Map> {
        SizeMatcher(int inExpectedSize) {
            mExpectedSize = inExpectedSize;
        }

        private final int mExpectedSize;

        @Override
        public void describeTo(Description inDescription) {
            inDescription.appendText("map of length ").appendValue(mExpectedSize);
        }

        @Override
        public boolean matchesSafely(Map inMap) {
            return inMap.size() == mExpectedSize;
        }
    }
    protected static final BrokerID BROKER = new BrokerID("TestBroker");
    protected static final String ACCOUNT = "account";
    protected static final String TEXT = "text";
    protected SimpleUser sActor;
    protected UserID sActorID;
    protected SimpleUser sViewer;
    protected UserID sViewerID;
    protected SimpleUser sExtraUser;
    protected UserID sExtraUserID;
    private static FIXMessageFactory sMessageFactory;
    protected ReportHistoryServices sServices;
    protected static final int SCALE = ExecutionReportSummary.DECIMAL_SCALE;
}
