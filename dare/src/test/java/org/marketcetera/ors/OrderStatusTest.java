package org.marketcetera.ors;

import java.math.BigDecimal;

import org.junit.Test;
import org.marketcetera.core.instruments.InstrumentToMessage;
import org.marketcetera.fix.FixSession;
import org.marketcetera.quickfix.FIXMessageFactory;
import org.marketcetera.quickfix.FIXMessageUtil;
import org.marketcetera.quickfix.FIXVersion;
import org.marketcetera.test.DareTestBase;
import org.marketcetera.trade.BrokerID;
import org.marketcetera.trade.ExecutionReport;
import org.marketcetera.trade.ExecutionType;
import org.marketcetera.trade.Factory;
import org.marketcetera.trade.Instrument;
import org.marketcetera.trade.OrderReplace;
import org.marketcetera.trade.OrderSingle;
import org.marketcetera.trade.OrderStatus;
import org.marketcetera.trade.OrderType;
import org.marketcetera.trade.Side;
import org.marketcetera.trade.TimeInForce;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;

import com.google.common.collect.Sets;

import junitparams.Parameters;
import quickfix.Session;
import quickfix.SessionID;

/* $License$ */

/**
 * Tests order status functions.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@EnableAutoConfiguration
public class OrderStatusTest
        extends DareTestBase
{
    /**
     * Test that the original order record gets updated on replace.
     *
     * @param inInstrument an <code>Instrument</code> value
     * @throws Exception if an unexpected error occurs
     */
    @Test
    @Parameters(method="instrumentParameters")
    public void testReplaceUpdatesOrderRecord(Instrument inInstrument)
            throws Exception
    {
        verifyNoOpenOrders();
        int sessionIndex = counter.incrementAndGet();
        SessionID sender1 = createInitiatorSession(sessionIndex);
        SessionID target1 = FIXMessageUtil.getReversedSessionId(sender1);
        FIXMessageFactory messageFactory = FIXVersion.getFIXVersion(sender1).getMessageFactory();
        FixSession session1 = brokerService.getActiveFixSession(sender1).getFixSession();
        BrokerID brokerId1 = new BrokerID(session1.getBrokerId());
        String order1OrderId = generateId();
        OrderSingle order1 = Factory.getInstance().createOrderSingle();
        BigDecimal order1Price = new BigDecimal(100);
        BigDecimal order1Qty = new BigDecimal(1000);
        order1.setBrokerID(brokerId1);
        order1.setInstrument(inInstrument);
        order1.setOrderType(OrderType.Limit);
        order1.setPrice(order1Price);
        order1.setQuantity(order1Qty);
        order1.setSide(Side.Buy);
        client.sendOrder(order1);
        quickfix.Message receivedOrder1 = waitForAndVerifySenderMessage(sender1,
                                                                        quickfix.field.MsgType.ORDER_SINGLE);
        // send a pending new
        quickfix.Message order1PendingNew = buildMessage("35=8",
                                                         "58=pending new,6=0,11="+order1.getOrderID()+",14=0,15=USD,17="+generateId()+",20=0,21=3,22=1,31=0,32=0,37="+order1OrderId+",38="+order1Qty.toPlainString()+",39="+OrderStatus.PendingNew.getFIXValue()+",40="+OrderType.Limit.getFIXValue()+",44="+order1Price.toPlainString()+",54="+Side.Buy.getFIXValue()+",59="+TimeInForce.GoodTillCancel.getFIXValue()+",60=20141210-15:04:55.098,150="+ExecutionType.PendingNew.getFIXValue()+",151="+order1Qty.toPlainString(),
                                                         quickfix.field.MsgType.EXECUTION_REPORT,
                                                         messageFactory);
        InstrumentToMessage.SELECTOR.forInstrument(inInstrument).set(inInstrument,
                                                                     FIXMessageUtil.getDataDictionary(receivedOrder1),
                                                                     quickfix.field.MsgType.EXECUTION_REPORT,
                                                                     order1PendingNew);
        Session.sendToTarget(order1PendingNew,
                             target1);
        verifyOrderStatus(order1.getOrderID(),
                          order1.getOrderID(),
                          OrderStatus.PendingNew);
        verifyOpenOrders(Sets.newHashSet(order1.getOrderID()));
        reports.clear();
        // send new
        quickfix.Message order1New = buildMessage("35=8",
                                                  "58=new,6=0,11="+order1.getOrderID()+",14=0,15=USD,17="+generateId()+",20=0,21=3,22=1,31=0,32=0,37="+order1OrderId+",38="+order1Qty.toPlainString()+",39="+OrderStatus.New.getFIXValue()+",40="+OrderType.Limit.getFIXValue()+",44="+order1Price.toPlainString()+",54="+Side.Buy.getFIXValue()+",59="+TimeInForce.GoodTillCancel.getFIXValue()+",60=20141210-15:04:55.098,150="+ExecutionType.New.getFIXValue()+",151="+order1Qty.toPlainString(),
                                                  quickfix.field.MsgType.EXECUTION_REPORT,
                                                  messageFactory);
        InstrumentToMessage.SELECTOR.forInstrument(inInstrument).set(inInstrument,
                                                                     FIXMessageUtil.getDataDictionary(receivedOrder1),
                                                                     quickfix.field.MsgType.EXECUTION_REPORT,
                                                                     order1New);
        Session.sendToTarget(order1New,
                             target1);
        verifyOrderStatus(order1.getOrderID(),
                          order1.getOrderID(),
                          OrderStatus.New);
        // replace this order
        OrderReplace replace1 = Factory.getInstance().createOrderReplace((ExecutionReport)waitForClientReport());
        client.sendOrder(replace1);
        quickfix.Message receivedReplace1 = waitForAndVerifySenderMessage(sender1,
                                                                          quickfix.field.MsgType.ORDER_CANCEL_REPLACE_REQUEST);
        verifyOpenOrders(Sets.newHashSet(order1.getOrderID()));
        reports.clear();
        // send pending replace
        quickfix.Message order1PendingReplace = buildMessage("35=8",
                                                             "58=pending replace1,6=0,11="+replace1.getOrderID()+",14=0,15=USD,17="+generateId()+",20=0,21=3,22=1,31=0,32=0,37="+order1OrderId+",38="+order1Qty.toPlainString()+",39="+OrderStatus.PendingReplace.getFIXValue()+",40="+OrderType.Limit.getFIXValue()+",41="+order1.getOrderID()+",44="+order1Price.toPlainString()+",54="+Side.Buy.getFIXValue()+",59="+TimeInForce.GoodTillCancel.getFIXValue()+",60=20141210-15:04:55.098,150="+ExecutionType.PendingReplace.getFIXValue()+",151="+order1Qty.toPlainString(),
                                                             quickfix.field.MsgType.EXECUTION_REPORT,
                                                             messageFactory);
        InstrumentToMessage.SELECTOR.forInstrument(inInstrument).set(inInstrument,
                                                                     FIXMessageUtil.getDataDictionary(receivedReplace1),
                                                                     quickfix.field.MsgType.EXECUTION_REPORT,
                                                                     order1PendingReplace);
        Session.sendToTarget(order1PendingReplace,
                             target1);
        verifyOrderStatus(order1.getOrderID(),
                          replace1.getOrderID(),
                          OrderStatus.PendingReplace);
        verifyOpenOrders(Sets.newHashSet(replace1.getOrderID()));
        reports.clear();
        // send replace
        quickfix.Message order1Replace = buildMessage("35=8",
                                                      "58=replace1,6=0,11="+replace1.getOrderID()+",14=0,15=USD,17="+generateId()+",20=0,21=3,22=1,31=0,32=0,37="+order1OrderId+",38="+order1Qty.toPlainString()+",39="+OrderStatus.Replaced.getFIXValue()+",40="+OrderType.Limit.getFIXValue()+",41="+order1.getOrderID()+",44="+order1Price.toPlainString()+",54="+Side.Buy.getFIXValue()+",59="+TimeInForce.GoodTillCancel.getFIXValue()+",60=20141210-15:04:55.098,150="+ExecutionType.Replace.getFIXValue()+",151="+order1Qty.toPlainString(),
                                                      quickfix.field.MsgType.EXECUTION_REPORT,
                                                      messageFactory);
        InstrumentToMessage.SELECTOR.forInstrument(inInstrument).set(inInstrument,
                                                                     FIXMessageUtil.getDataDictionary(receivedReplace1),
                                                                     quickfix.field.MsgType.EXECUTION_REPORT,
                                                                     order1Replace);
         Session.sendToTarget(order1Replace,
                              target1);
         verifyOrderStatus(order1.getOrderID(),
                           replace1.getOrderID(),
                           OrderStatus.Replaced);
         // replace again
         OrderReplace replace2 = Factory.getInstance().createOrderReplace((ExecutionReport)waitForClientReport());
         client.sendOrder(replace2);
         quickfix.Message receivedReplace2 = waitForAndVerifySenderMessage(sender1,
                                                                           quickfix.field.MsgType.ORDER_CANCEL_REPLACE_REQUEST);
         verifyOpenOrders(Sets.newHashSet(replace1.getOrderID()));
         reports.clear();
         // send pending replace
         quickfix.Message order2PendingReplace = buildMessage("35=8",
                                                              "58=pending replace2,6=0,11="+replace2.getOrderID()+",14=0,15=USD,17="+generateId()+",20=0,21=3,22=1,31=0,32=0,37="+order1OrderId+",38="+order1Qty.toPlainString()+",39="+OrderStatus.PendingReplace.getFIXValue()+",40="+OrderType.Limit.getFIXValue()+",41="+replace1.getOrderID()+",44="+order1Price.toPlainString()+",54="+Side.Buy.getFIXValue()+",59="+TimeInForce.GoodTillCancel.getFIXValue()+",60=20141210-15:04:55.098,150="+ExecutionType.PendingReplace.getFIXValue()+",151="+order1Qty.toPlainString(),
                                                              quickfix.field.MsgType.EXECUTION_REPORT,
                                                              messageFactory);
         InstrumentToMessage.SELECTOR.forInstrument(inInstrument).set(inInstrument,
                                                                      FIXMessageUtil.getDataDictionary(receivedReplace2),
                                                                      quickfix.field.MsgType.EXECUTION_REPORT,
                                                                      order2PendingReplace);
         Session.sendToTarget(order2PendingReplace,
                              target1);
         verifyOrderStatus(order1.getOrderID(),
                           replace2.getOrderID(),
                           OrderStatus.PendingReplace);
         verifyOpenOrders(Sets.newHashSet(replace2.getOrderID()));
         reports.clear();
         // send replace
         quickfix.Message order2Replace = buildMessage("35=8",
                                                       "58=replace2,6=0,11="+replace2.getOrderID()+",14=0,15=USD,17="+generateId()+",20=0,21=3,22=1,31=0,32=0,37="+order1OrderId+",38="+order1Qty.toPlainString()+",39="+OrderStatus.Replaced.getFIXValue()+",40="+OrderType.Limit.getFIXValue()+",41="+replace1.getOrderID()+",44="+order1Price.toPlainString()+",54="+Side.Buy.getFIXValue()+",59="+TimeInForce.GoodTillCancel.getFIXValue()+",60=20141210-15:04:55.098,150="+ExecutionType.Replace.getFIXValue()+",151="+order1Qty.toPlainString(),
                                                       quickfix.field.MsgType.EXECUTION_REPORT,
                                                       messageFactory);
         InstrumentToMessage.SELECTOR.forInstrument(inInstrument).set(inInstrument,
                                                                      FIXMessageUtil.getDataDictionary(receivedReplace2),
                                                                      quickfix.field.MsgType.EXECUTION_REPORT,
                                                                      order2Replace);
         Session.sendToTarget(order2Replace,
                              target1);
         verifyOrderStatus(order1.getOrderID(),
                           replace2.getOrderID(),
                           OrderStatus.Replaced);
         verifyOpenOrders(Sets.newHashSet(replace2.getOrderID()));
         reports.clear();
         // partially fill the replaced order
         BigDecimal lastQty = new BigDecimal(100);
         BigDecimal cumQty = new BigDecimal(100);
         BigDecimal leavesQty = order1Qty.subtract(cumQty);
         quickfix.Message order1Fill1 = buildMessage("35=8",
                                                     "58=fill1,6="+order1Price.toPlainString()+",11="+replace2.getOrderID()+",14="+cumQty.toPlainString()+",15=USD,17="+generateId()+",20=0,21=3,22=1,31="+lastQty.toPlainString()+",32="+lastQty.toPlainString()+",37="+order1OrderId+",38="+order1Qty.toPlainString()+",39="+OrderStatus.PartiallyFilled.getFIXValue()+",40="+OrderType.Limit.getFIXValue()+",44="+order1Price.toPlainString()+",54="+Side.Buy.getFIXValue()+",59="+TimeInForce.GoodTillCancel.getFIXValue()+",60=20141210-15:04:55.098,150="+ExecutionType.PartialFill.getFIXValue()+",151="+leavesQty.toPlainString(),
                                                     quickfix.field.MsgType.EXECUTION_REPORT,
                                                     messageFactory);
         InstrumentToMessage.SELECTOR.forInstrument(inInstrument).set(inInstrument,
                                                                      FIXMessageUtil.getDataDictionary(receivedReplace1),
                                                                      quickfix.field.MsgType.EXECUTION_REPORT,
                                                                      order1Fill1);
         Session.sendToTarget(order1Fill1,
                              target1);
         verifyOrderStatus(order1.getOrderID(),
                           replace2.getOrderID(),
                           OrderStatus.PartiallyFilled);
         verifyOpenOrders(Sets.newHashSet(replace2.getOrderID()));
         reports.clear();
         // create and send a new order
         String order2OrderId = generateId();
         OrderSingle order2 = Factory.getInstance().createOrderSingle();
         BigDecimal order2Price = new BigDecimal(100);
         BigDecimal order2Qty = new BigDecimal(1000);
         order2.setBrokerID(brokerId1);
         order2.setInstrument(inInstrument);
         order2.setOrderType(OrderType.Limit);
         order2.setPrice(order2Price);
         order2.setQuantity(order2Qty);
         order2.setSide(Side.Buy);
         client.sendOrder(order2);
         quickfix.Message receivedOrder2 = waitForAndVerifySenderMessage(sender1,
                                                                         quickfix.field.MsgType.ORDER_SINGLE);
         // send a pending new
         quickfix.Message order2PendingNew = buildMessage("35=8",
                                                          "58=pending new,6=0,11="+order2.getOrderID()+",14=0,15=USD,17="+generateId()+",20=0,21=3,22=1,31=0,32=0,37="+order2OrderId+",38="+order2Qty.toPlainString()+",39="+OrderStatus.PendingNew.getFIXValue()+",40="+OrderType.Limit.getFIXValue()+",44="+order2Price.toPlainString()+",54="+Side.Buy.getFIXValue()+",59="+TimeInForce.GoodTillCancel.getFIXValue()+",60=20141210-15:04:55.098,150="+ExecutionType.PendingNew.getFIXValue()+",151="+order2Qty.toPlainString(),
                                                          quickfix.field.MsgType.EXECUTION_REPORT,
                                                          messageFactory);
         InstrumentToMessage.SELECTOR.forInstrument(inInstrument).set(inInstrument,
                                                                      FIXMessageUtil.getDataDictionary(receivedOrder2),
                                                                      quickfix.field.MsgType.EXECUTION_REPORT,
                                                                      order2PendingNew);
         Session.sendToTarget(order2PendingNew,
                              target1);
         verifyOrderStatus(order2.getOrderID(),
                           order2.getOrderID(),
                           OrderStatus.PendingNew);
         verifyOpenOrders(Sets.newHashSet(replace2.getOrderID(),order2.getOrderID()));
         reports.clear();
         // completely fill order1
         lastQty = order1Fill1.getDecimal(quickfix.field.LeavesQty.FIELD);
         cumQty = order1Fill1.getDecimal(quickfix.field.OrderQty.FIELD);
         leavesQty = BigDecimal.ZERO;
         quickfix.Message order1Fill2 = buildMessage("35=8",
                                                     "58=fill1,6="+order1Price.toPlainString()+",11="+replace2.getOrderID()+",14="+cumQty.toPlainString()+",15=USD,17="+generateId()+",20=0,21=3,22=1,31="+lastQty.toPlainString()+",32="+lastQty.toPlainString()+",37="+order1OrderId+",38="+order1Qty.toPlainString()+",39="+OrderStatus.Filled.getFIXValue()+",40="+OrderType.Limit.getFIXValue()+",44="+order1Price.toPlainString()+",54="+Side.Buy.getFIXValue()+",59="+TimeInForce.GoodTillCancel.getFIXValue()+",60=20141210-15:04:55.098,150="+ExecutionType.Fill.getFIXValue()+",151="+leavesQty.toPlainString(),
                                                     quickfix.field.MsgType.EXECUTION_REPORT,
                                                     messageFactory);
         InstrumentToMessage.SELECTOR.forInstrument(inInstrument).set(inInstrument,
                                                                      FIXMessageUtil.getDataDictionary(receivedReplace1),
                                                                      quickfix.field.MsgType.EXECUTION_REPORT,
                                                                      order1Fill2);
         Session.sendToTarget(order1Fill2,
                              target1);
         verifyOrderStatus(order1.getOrderID(),
                           replace2.getOrderID(),
                           OrderStatus.Filled);
         verifyOpenOrders(Sets.newHashSet(order2.getOrderID()));
         reports.clear();
    }
}
