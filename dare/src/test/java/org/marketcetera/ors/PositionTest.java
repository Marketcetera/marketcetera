package org.marketcetera.ors;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.marketcetera.admin.User;
import org.marketcetera.core.instruments.InstrumentToMessage;
import org.marketcetera.core.position.PositionKey;
import org.marketcetera.event.EventTestBase;
import org.marketcetera.fix.FixSession;
import org.marketcetera.quickfix.FIXMessageFactory;
import org.marketcetera.quickfix.FIXMessageUtil;
import org.marketcetera.quickfix.FIXVersion;
import org.marketcetera.test.MarketceteraTestBase;
import org.marketcetera.trade.BrokerID;
import org.marketcetera.trade.ConvertibleBond;
import org.marketcetera.trade.Currency;
import org.marketcetera.trade.Equity;
import org.marketcetera.trade.ExecutionType;
import org.marketcetera.trade.Factory;
import org.marketcetera.trade.Future;
import org.marketcetera.trade.Instrument;
import org.marketcetera.trade.Option;
import org.marketcetera.trade.OptionType;
import org.marketcetera.trade.OrderSingle;
import org.marketcetera.trade.OrderStatus;
import org.marketcetera.trade.OrderType;
import org.marketcetera.trade.Side;
import org.marketcetera.trade.TimeInForce;
import org.marketcetera.util.log.SLF4JLoggerProxy;

import com.google.common.collect.Maps;

import junitparams.Parameters;
import quickfix.Message;
import quickfix.Session;
import quickfix.SessionID;

/* $License$ */

/**
 * Test position queries.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class PositionTest
        extends MarketceteraTestBase
{
    /**
     * Run before each test.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Before
    public void setup()
            throws Exception
    {
        super.setup();
        fixVersion = FIXVersion.FIX42;
        adminUser = userService.findByName("traderAdmin");
        normalUser = userService.findByName("trader");
        otherUser = userService.findByName("test");
        assertNotNull(adminUser);
        assertNotNull(normalUser);
        assertNotNull(otherUser);
    }
    /**
     * Tests single positions for a single instrument and FIX version.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testSinglePositionNoParameterTest()
            throws Exception
    {
        doSinglePositionTest(new Equity("METC"),
                             FIXVersion.FIX50SP2);
    }
    /**
     * Test retrieving a single position.
     *
     * @param inInstrument an <code>Instrument</code> value
     * @param inFixVersion a <code>FIXVersion</code> value
     * @throws Exception if an unexpected error occurs
     */
    @Test
    @Ignore
    @Parameters(method="instrumentFixVersionParameters")
    public void testSinglePosition(Instrument inInstrument,
                                   FIXVersion inFixVersion)
            throws Exception
    {
        doSinglePositionTest(inInstrument,
                             inFixVersion);
    }
    /**
     * Test retrieving all positions.
     *
     * @param inInstrument an <code>Instrument</code> value
     * @param inFixVersion a <code>FIXVersion</code> value
     * @throws Exception if an unexpected error occurs
     */
    @Test
    @Ignore
    @Parameters(method="instrumentFixVersionParameters")
    public void testAllPositions(Instrument inInstrument,
                                 FIXVersion inFixVersion)
            throws Exception
    {
        doAllPositionsTest(inInstrument,
                           inFixVersion);
    }
    /**
     * Test all positions for a single instrument and FIX version.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    @Ignore
    public void testAllPositionsNoParameters()
            throws Exception
    {
        doAllPositionsTest(new Option("METC","201811",EventTestBase.generateDecimalValue(),OptionType.Put),
                           FIXVersion.FIX42);
    }
    /* (non-Javadoc)
     * @see com.marketcetera.test.MarketceteraTestBase#getFixVersion()
     */
    @Override
    protected FIXVersion getFixVersion()
    {
        return fixVersion;
    }
    /**
     * Execute a single all positions test with the given instrument and FIX version.
     *
     * @param inInstrument an <code>Instrument</code> value
     * @param inFixVersion a <code>FIXVersion</code> value
     * @throws Exception if an unexpected error occurs
     */
    private void doAllPositionsTest(Instrument inInstrument,
                                    FIXVersion inFixVersion)
            throws Exception
    {
        SLF4JLoggerProxy.info(this,
                              "All positions test with instrument: {} FIX version: {}",
                              inInstrument,
                              inFixVersion);
        setupSession(inFixVersion);
        Date positionDate = new Date();
        BigDecimal expectedPosition = BigDecimal.ZERO;
        verifyPositionFromAllPositions(inInstrument,
                                       expectedPosition,
                                       positionDate);
        BigDecimal orderQty = new BigDecimal(10000);
        BigDecimal fillQty = new BigDecimal(1000);
        generatePosition(inInstrument,
                         orderQty,
                         fillQty);
        positionDate = new Date();
        expectedPosition = fillQty;
        verifyPositionFromAllPositions(inInstrument,
                                       expectedPosition,
                                       positionDate);
        // add a second order for the same instrument, leave at new
        generatePosition(inInstrument,
                         orderQty,
                         BigDecimal.ZERO);
        positionDate = new Date();
        verifyPositionFromAllPositions(inInstrument,
                                       expectedPosition,
                                       positionDate);
        // add a second order for the same instrument, fill
        generatePosition(inInstrument,
                         orderQty,
                         fillQty);
        expectedPosition = fillQty.add(fillQty);
        positionDate = new Date();
        verifyPositionFromAllPositions(inInstrument,
                                       expectedPosition,
                                       positionDate);
        Instrument otherInstrument = new Equity("NEW_INSTRUMENT_" + counter.incrementAndGet());
        generatePosition(otherInstrument,
                         orderQty,
                         fillQty);
        positionDate = new Date();
        verifyPositionFromAllPositions(inInstrument,
                                       expectedPosition,
                                       positionDate);
        expectedPosition = fillQty;
        verifyPositionFromAllPositions(otherInstrument,
                                       expectedPosition,
                                       positionDate);
        positionDate = new Date(0);
        expectedPosition = BigDecimal.ZERO;
        verifyPositionFromAllPositions(inInstrument,
                                       expectedPosition,
                                       positionDate);
        verifyPositionFromAllPositions(otherInstrument,
                                       expectedPosition,
                                       positionDate);
    }
    /**
     * Execute a single iteration of the single position test.
     *
     * @param inInstrument an <code>Instrument</code> value
     * @param inFixVersion a <code>FIXVersion</code> value
     * @throws Exception if an unexpected error occurs
     */
    private void doSinglePositionTest(Instrument inInstrument,
                                      FIXVersion inFixVersion)
            throws Exception
    {
        SLF4JLoggerProxy.info(this,
                              "Single position test with instrument: {} FIX version: {}",
                              inInstrument,
                              inFixVersion);
        setupSession(inFixVersion);
        Date positionDate = new Date();
        BigDecimal expectedPosition = BigDecimal.ZERO;
        verifySinglePosition(inInstrument,
                             expectedPosition,
                             positionDate);
        BigDecimal orderQty = new BigDecimal(10000);
        BigDecimal fillQty = new BigDecimal(1000);
        generatePosition(inInstrument,
                         orderQty,
                         fillQty);
        positionDate = new Date();
        expectedPosition = fillQty;
        verifySinglePosition(inInstrument,
                             expectedPosition,
                             positionDate);
        // add a second order for the same instrument, leave at new
        generatePosition(inInstrument,
                         orderQty,
                         BigDecimal.ZERO);
        positionDate = new Date();
        verifySinglePosition(inInstrument,
                             expectedPosition,
                             positionDate);
        // add a second order for the same instrument, fill
        generatePosition(inInstrument,
                         orderQty,
                         fillQty);
        expectedPosition = fillQty.add(fillQty);
        positionDate = new Date();
        verifySinglePosition(inInstrument,
                             expectedPosition,
                             positionDate);
        Instrument otherInstrument = new Equity("NEW_INSTRUMENT_" + counter.incrementAndGet());
        generatePosition(otherInstrument,
                         orderQty,
                         fillQty);
        positionDate = new Date();
        verifySinglePosition(inInstrument,
                             expectedPosition,
                             positionDate);
        expectedPosition = fillQty;
        verifySinglePosition(otherInstrument,
                             expectedPosition,
                             positionDate);
        positionDate = new Date(0);
        expectedPosition = BigDecimal.ZERO;
        verifySinglePosition(inInstrument,
                             expectedPosition,
                             positionDate);
        verifySinglePosition(otherInstrument,
                             expectedPosition,
                             positionDate);
    }
    /**
     * Set up the FIX sessions to use for the current test.
     *
     * @param inFixVersion a <code>FIXVersion</code> value
     * @throws Exception if an unexpected error occurs
     */
    private void setupSession(FIXVersion inFixVersion)
            throws Exception
    {
        fixVersion = inFixVersion;
        int sessionIndex = counter.incrementAndGet();
        createRemoteReceiverSession(sessionIndex);
        sender = createInitiatorSession(sessionIndex);
        target = FIXMessageUtil.getReversedSessionId(sender);
        messageFactory = FIXVersion.getFIXVersion(sender).getMessageFactory();
        session = brokerService.getActiveFixSession(sender).getFixSession();
        brokerId = new BrokerID(session.getBrokerId());
    }
    /**
     * Verify the position of the given instrument at the given time matches the given expected value.
     *
     * @param inInstrument an <code>Instrument</code> value
     * @param inExpectedPosition a <code>BigDecimal</code> value
     * @param inPositionDate a <code>Date</code> value
     * @throws Exception if an unexpected error occurs
     */
    private void verifySinglePosition(Instrument inInstrument,
                                      BigDecimal inExpectedPosition,
                                      Date inPositionDate)
            throws Exception
    {
        verifySinglePosition(inInstrument,
                             inExpectedPosition,
                             inPositionDate,
                             normalUser);
        verifySinglePosition(inInstrument,
                             inExpectedPosition,
                             inPositionDate,
                             adminUser);
        verifySinglePosition(inInstrument,
                             BigDecimal.ZERO,
                             inPositionDate,
                             otherUser);
    }
    /**
     * Verify the position of the given instrument at the given time matches the given expected value.
     *
     * @param inInstrument an <code>Instrument</code> value
     * @param inExpectedPosition a <code>BigDecimal</code> value
     * @param inPositionDate a <code>Date</code> value
     * @param inUser a <code>User</code> value
     * @throws Exception if an unexpected error occurs
     */
    private void verifySinglePosition(Instrument inInstrument,
                                      BigDecimal inExpectedPosition,
                                      Date inPositionDate,
                                      User inUser)
            throws Exception
    {
        BigDecimal actualPosition = getSinglePosition(inUser,
                                                      inPositionDate,
                                                      inInstrument);
        assertTrue("Expected: " + inExpectedPosition.toPlainString() + " actual: " + actualPosition.toPlainString() + " for " + inUser + " as of " + new DateTime(inPositionDate),
                   inExpectedPosition.compareTo(actualPosition) == 0);
    }
    /**
     * Verify the position of the given instrument as of the given date.
     *
     * @param inInstrument an <code>Instrument</code> value
     * @param inExpectedPosition a <code>BigDecimal</code> value
     * @param inPositionDate a <code>Date</code> value
     * @throws Exception if an unexpected error occurs
     */
    private void verifyPositionFromAllPositions(Instrument inInstrument,
                                                BigDecimal inExpectedPosition,
                                                Date inPositionDate)
            throws Exception
    {
        verifyPositionFromAllPositions(inInstrument,
                                       inExpectedPosition,
                                       inPositionDate,
                                       normalUser);
        verifyPositionFromAllPositions(inInstrument,
                                       inExpectedPosition,
                                       inPositionDate,
                                       adminUser);
        verifyPositionFromAllPositions(inInstrument,
                                       BigDecimal.ZERO,
                                       inPositionDate,
                                       otherUser);
    }
    /**
     * Verify the position of the given instrument as of the given date owned or viewable by the given user.
     *
     * @param inInstrument an <code>Instrument</code> value
     * @param inExpectedPosition a <code>BigDecimal</code> value
     * @param inPositionDate a <code>Date</code> value
     * @param inUser a <code>User</code> value
     * @throws Exception if an unexpected error occurs
     */
    private void verifyPositionFromAllPositions(Instrument inInstrument,
                                                BigDecimal inExpectedPosition,
                                                Date inPositionDate,
                                                User inUser)
            throws Exception
    {
        Map<PositionKey<? extends Instrument>,BigDecimal> allPositions = getAllPositions(inUser,
                                                                                         inPositionDate);
        BigDecimal actualPosition = BigDecimal.ZERO;
        for(Map.Entry<PositionKey<? extends Instrument>,BigDecimal> entry : allPositions.entrySet()) {
            Instrument positionInstrument = entry.getKey().getInstrument();
            if(!inInstrument.equals(positionInstrument)) {
                continue;
            }
            BigDecimal position = entry.getValue();
            actualPosition = actualPosition.add(position);
        }
        assertTrue("Expected: " + inExpectedPosition.toPlainString() + " actual: " + actualPosition.toPlainString() + " for " + inUser + " as of " + new DateTime(inPositionDate),
                   inExpectedPosition.compareTo(actualPosition) == 0);
        if(inInstrument instanceof Option) {
            Option option = (Option)inInstrument;
            Map<PositionKey<Option>,BigDecimal> actualPositionMap = reportService.getOptionPositionsAsOf(inUser,
                                                                                                         inPositionDate,
                                                                                                         new String[] { option.getSymbol() });
            for(Map.Entry<PositionKey<Option>,BigDecimal> entry : actualPositionMap.entrySet()) {
                Option positionOption = entry.getKey().getInstrument();
                actualPosition = entry.getValue();
                if(positionOption.equals(option)) {
                    assertTrue("Expected: " + inExpectedPosition.toPlainString() + " actual: " + actualPosition.toPlainString() + " for " + inUser + " as of " + new DateTime(inPositionDate),
                               inExpectedPosition.compareTo(actualPosition) == 0);
                }
            }
        }
    }
    /**
     * Get all positions as of the given date owned by or viewable by the given user.
     *
     * @param inUser a <code>User</code> value
     * @param inPositionDate a <code>Date</code> value
     * @return a <code>Map&lt;PositionKey&lt;? extends Instrument&gt;,BigDecimal&gt;</code> value
     * @throws Exception if an unexpected error occurs
     */
    private Map<PositionKey<? extends Instrument>,BigDecimal> getAllPositions(User inUser,
                                                                              Date inPositionDate)
            throws Exception
    {
        Map<PositionKey<? extends Instrument>,BigDecimal> results = Maps.newHashMap();
        results.putAll(reportService.getAllEquityPositionsAsOf(inUser,
                                                               inPositionDate));
        results.putAll(reportService.getAllOptionPositionsAsOf(inUser,
                                                               inPositionDate));
        results.putAll(reportService.getAllFuturePositionsAsOf(inUser,
                                                               inPositionDate));
        results.putAll(reportService.getAllCurrencyPositionsAsOf(inUser,
                                                                 inPositionDate));
        results.putAll(reportService.getAllConvertibleBondPositionsAsOf(inUser,
                                                                        inPositionDate));
        return results;
    }
    /**
     * Generate the given position for the given instrument with the given original order quantity.
     *
     * @param inInstrument an <code>Instrument</code> value
     * @param inOrderQty a <code>BigDecimal</code> value
     * @param inFillQty a <code>BigDecimal</code> value or <code>BigDecimal.ZERO</code> to leave the order at <code>NEW</code> status
     * @return an <code>OrderSingle</code> value
     * @throws Exception if an unexpected error occurs
     */
    private OrderSingle generatePosition(Instrument inInstrument,
                                         BigDecimal inOrderQty,
                                         BigDecimal inFillQty)
            throws Exception
    {
        String orderId = generateId();
        OrderSingle order = Factory.getInstance().createOrderSingle();
        BigDecimal orderPrice = new BigDecimal(100);
        order.setBrokerID(brokerId);
        order.setInstrument(inInstrument);
        order.setOrderType(OrderType.Limit);
        order.setPrice(orderPrice);
        order.setQuantity(inOrderQty);
        order.setSide(Side.Buy);
        client.sendOrder(order);
        Message receivedOrder = waitForAndVerifySenderMessage(sender,
                                                              quickfix.field.MsgType.ORDER_SINGLE);
        // send a pending new
        Message orderPendingNew = buildMessage("35=8",
                                               "58=pending new,6=0,11="+order.getOrderID()+",14=0,15=USD,17="+generateId()+",20=0,21=3,22=1,31=0,32=0,37="+orderId+",38="+inOrderQty.toPlainString()+",39="+OrderStatus.PendingNew.getFIXValue()+",40="+OrderType.Limit.getFIXValue()+",44="+orderPrice.toPlainString()+",54="+Side.Buy.getFIXValue()+",59="+TimeInForce.GoodTillCancel.getFIXValue()+",60=20141210-15:04:55.098,150="+ExecutionType.PendingNew.getFIXValue()+",151="+inOrderQty.toPlainString(),
                                               quickfix.field.MsgType.EXECUTION_REPORT,
                                               messageFactory);
        orderPendingNew.setField(new quickfix.field.TransactTime(new Date(System.currentTimeMillis()-1000)));
        InstrumentToMessage.SELECTOR.forInstrument(inInstrument).set(inInstrument,
                                                                     FIXMessageUtil.getDataDictionary(receivedOrder),
                                                                     quickfix.field.MsgType.EXECUTION_REPORT,
                                                                     orderPendingNew);
        orderPendingNew = fixVersion.getMessageFactory().getMsgAugmentor().executionReportAugment(orderPendingNew);
        Session.sendToTarget(orderPendingNew,
                             target);
        verifyOrderStatus(order.getOrderID(),
                          order.getOrderID(),
                          OrderStatus.PendingNew);
        reports.clear();
        // send new
        Message orderNew = buildMessage("35=8",
                                        "58=new,6=0,11="+order.getOrderID()+",14=0,15=USD,17="+generateId()+",20=0,21=3,22=1,31=0,32=0,37="+orderId+",38="+inOrderQty.toPlainString()+",39="+OrderStatus.New.getFIXValue()+",40="+OrderType.Limit.getFIXValue()+",44="+orderPrice.toPlainString()+",54="+Side.Buy.getFIXValue()+",59="+TimeInForce.GoodTillCancel.getFIXValue()+",60=20141210-15:04:55.098,150="+ExecutionType.New.getFIXValue()+",151="+inOrderQty.toPlainString(),
                                        quickfix.field.MsgType.EXECUTION_REPORT,
                                        messageFactory);
        orderNew.setField(new quickfix.field.TransactTime(new Date(System.currentTimeMillis()-1000)));
        InstrumentToMessage.SELECTOR.forInstrument(inInstrument).set(inInstrument,
                                                                     FIXMessageUtil.getDataDictionary(receivedOrder),
                                                                     quickfix.field.MsgType.EXECUTION_REPORT,
                                                                     orderNew);
        orderNew = fixVersion.getMessageFactory().getMsgAugmentor().executionReportAugment(orderNew);
        Session.sendToTarget(orderNew,
                             target);
        verifyOrderStatus(order.getOrderID(),
                          order.getOrderID(),
                          OrderStatus.New);
        if(inFillQty.compareTo(BigDecimal.ZERO) != 1) {
            return order;
        }
        // send partial fill
        Message orderFill1 = buildMessage("35=8",
                                          "58=fill1,6="+order.getPrice().toPlainString()+",11="+order.getOrderID()+",14="+inFillQty.toPlainString()+",15=USD,17="+generateId()+",20=0,21=3,22=1,31=0,32=0,37="+orderId+",38="+inOrderQty.toPlainString()+",39="+OrderStatus.PartiallyFilled.getFIXValue()+",40="+OrderType.Limit.getFIXValue()+",44="+orderPrice.toPlainString()+",54="+Side.Buy.getFIXValue()+",59="+TimeInForce.GoodTillCancel.getFIXValue()+",60=20141210-15:04:55.098,150="+ExecutionType.PartialFill.getFIXValue()+",151="+inOrderQty.subtract(inFillQty).toPlainString(),
                                          quickfix.field.MsgType.EXECUTION_REPORT,
                                          messageFactory);
        orderFill1.setField(new quickfix.field.TransactTime(new Date(System.currentTimeMillis()-1000)));
        InstrumentToMessage.SELECTOR.forInstrument(inInstrument).set(inInstrument,
                                                                     FIXMessageUtil.getDataDictionary(receivedOrder),
                                                                     quickfix.field.MsgType.EXECUTION_REPORT,
                                                                     orderFill1);
        orderFill1 = fixVersion.getMessageFactory().getMsgAugmentor().executionReportAugment(orderFill1);
        Session.sendToTarget(orderFill1,
                             target);
        verifyOrderStatus(order.getOrderID(),
                          order.getOrderID(),
                          OrderStatus.PartiallyFilled);
        return order;
    }
    /**
     * Get the position of the given instrument as of the given date owned or viewable by the given user.
     *
     * @param inUser a <code>User</code> value
     * @param inPositionDate a <code>Date</code> value
     * @param inInstrument an <code>Instrument</code> value
     * @return a <code>BigDecimal</code> value
     * @throws Exception if an unexpected error occurs
     */
    private BigDecimal getSinglePosition(User inUser,
                                         Date inPositionDate,
                                         Instrument inInstrument)
            throws Exception
    {
        if(inInstrument instanceof Equity) {
            return reportService.getEquityPositionAsOf(inUser,
                                                       inPositionDate,
                                                       (Equity)inInstrument);
        } else if(inInstrument instanceof Option) {
            return reportService.getOptionPositionAsOf(inUser,
                                                       inPositionDate,
                                                       (Option)inInstrument);
        } else if(inInstrument instanceof Future) {
            return reportService.getFuturePositionAsOf(inUser,
                                                       inPositionDate,
                                                       (Future)inInstrument);
        } else if(inInstrument instanceof Currency) {
            return reportService.getCurrencyPositionAsOf(inUser,
                                                       inPositionDate,
                                                       (Currency)inInstrument);
        } else if(inInstrument instanceof ConvertibleBond) {
            return reportService.getConvertibleBondPositionAsOf(inUser,
                                                                inPositionDate,
                                                                (ConvertibleBond)inInstrument);
        }
        fail("Unsupported instrument: " + inInstrument);
        return null;
    }
    /**
     * user with admin over {@link #normalUser}
     */
    private User adminUser;
    /**
     * user with which to conduct trading activities
     */
    private User normalUser;
    /**
     * unrelated user with no authority over {@link #normalUser}
     */
    private User otherUser;
    /**
     * sender session value
     */
    private SessionID sender;
    /**
     * target session value
     */
    private SessionID target;
    /**
     * message factory value
     */
    private FIXMessageFactory messageFactory;
    /**
     * test FIX session value
     */
    private FixSession session;
    /**
     * test broker id value
     */
    private BrokerID brokerId;
    /**
     * FIX version for this test
     */
    private FIXVersion fixVersion;
}
