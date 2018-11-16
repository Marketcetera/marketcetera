package org.marketcetera.ors;

import java.math.BigDecimal;

import org.junit.Test;
import org.marketcetera.core.instruments.InstrumentToMessage;
import org.marketcetera.fix.FixSession;
import org.marketcetera.quickfix.FIXMessageFactory;
import org.marketcetera.quickfix.FIXMessageUtil;
import org.marketcetera.quickfix.FIXVersion;
import org.marketcetera.test.MarketceteraTestBase;
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

import quickfix.Message;
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
public class OrderStatusTest
        extends MarketceteraTestBase
{
    /**
     * Test that the original order record gets updated on replace.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testReplaceUpdatesOrderRecord()
            throws Exception
    {
        Instrument instrument = generateInstrument();
        int sessionIndex = counter.incrementAndGet();
        SessionID sender1 = createInitiatorSession(sessionIndex);
        SessionID target1 = FIXMessageUtil.getReversedSessionId(sender1);
        FIXMessageFactory messageFactory = FIXVersion.getFIXVersion(sender1).getMessageFactory();
        FixSession session1 = brokerService.findFixSessionBySessionId(sender1);
        BrokerID brokerId1 = new BrokerID(session1.getBrokerId());
        String order1OrderId = generateId();
        OrderSingle order = Factory.getInstance().createOrderSingle();
        BigDecimal order1Price = new BigDecimal(100);
        BigDecimal order1Qty = new BigDecimal(1000);
        order.setBrokerID(brokerId1);
        order.setInstrument(instrument);
        order.setOrderType(OrderType.Limit);
        order.setPrice(order1Price);
        order.setQuantity(order1Qty);
        order.setSide(Side.Buy);
        client.sendOrder(order);
        Message receivedOrder1 = waitForAndVerifySenderMessage(sender1,
                                                               quickfix.field.MsgType.ORDER_SINGLE);
        // send a pending new
        Message order1PendingNew = buildMessage("35=8",
                                                "58=pending new,6=0,11="+order.getOrderID()+",14=0,15=USD,17="+generateId()+",20=0,21=3,22=1,31=0,32=0,37="+order1OrderId+",38="+order1Qty.toPlainString()+",39="+OrderStatus.PendingNew.getFIXValue()+",40="+OrderType.Limit.getFIXValue()+",44="+order1Price.toPlainString()+",54="+Side.Buy.getFIXValue()+",59="+TimeInForce.GoodTillCancel.getFIXValue()+",60=20141210-15:04:55.098,150="+ExecutionType.PendingNew.getFIXValue()+",151="+order1Qty.toPlainString(),
                                                quickfix.field.MsgType.EXECUTION_REPORT,
                                                messageFactory);
        InstrumentToMessage.SELECTOR.forInstrument(instrument).set(instrument,
                                                                   FIXMessageUtil.getDataDictionary(receivedOrder1),
                                                                   quickfix.field.MsgType.EXECUTION_REPORT,
                                                                   order1PendingNew);
        Session.sendToTarget(order1PendingNew,
                             target1);
        verifyOrderStatus(order.getOrderID(),
                          order.getOrderID(),
                          OrderStatus.PendingNew);
        reports.clear();
        // send new
        Message order1New = buildMessage("35=8",
                                         "58=new,6=0,11="+order.getOrderID()+",14=0,15=USD,17="+generateId()+",20=0,21=3,22=1,31=0,32=0,37="+order1OrderId+",38="+order1Qty.toPlainString()+",39="+OrderStatus.New.getFIXValue()+",40="+OrderType.Limit.getFIXValue()+",44="+order1Price.toPlainString()+",54="+Side.Buy.getFIXValue()+",59="+TimeInForce.GoodTillCancel.getFIXValue()+",60=20141210-15:04:55.098,150="+ExecutionType.New.getFIXValue()+",151="+order1Qty.toPlainString(),
                                         quickfix.field.MsgType.EXECUTION_REPORT,
                                         messageFactory);
        InstrumentToMessage.SELECTOR.forInstrument(instrument).set(instrument,
                                                                   FIXMessageUtil.getDataDictionary(receivedOrder1),
                                                                   quickfix.field.MsgType.EXECUTION_REPORT,
                                                                   order1New);
        Session.sendToTarget(order1New,
                             target1);
        verifyOrderStatus(order.getOrderID(),
                          order.getOrderID(),
                          OrderStatus.New);
        // replace this order
        OrderReplace replace1 = Factory.getInstance().createOrderReplace((ExecutionReport)waitForClientReport());
        client.sendOrder(replace1);
        Message receivedReplace1 = waitForAndVerifySenderMessage(sender1,
                                                                 quickfix.field.MsgType.ORDER_CANCEL_REPLACE_REQUEST);
        reports.clear();
        // send pending replace
        Message order1PendingReplace = buildMessage("35=8",
                                                   "58=pending replace1,6=0,11="+replace1.getOrderID()+",14=0,15=USD,17="+generateId()+",20=0,21=3,22=1,31=0,32=0,37="+order1OrderId+",38="+order1Qty.toPlainString()+",39="+OrderStatus.PendingReplace.getFIXValue()+",40="+OrderType.Limit.getFIXValue()+",41="+order.getOrderID()+",44="+order1Price.toPlainString()+",54="+Side.Buy.getFIXValue()+",59="+TimeInForce.GoodTillCancel.getFIXValue()+",60=20141210-15:04:55.098,150="+ExecutionType.PendingReplace.getFIXValue()+",151="+order1Qty.toPlainString(),
                                                   quickfix.field.MsgType.EXECUTION_REPORT,
                                                   messageFactory);
        InstrumentToMessage.SELECTOR.forInstrument(instrument).set(instrument,
                                                                   FIXMessageUtil.getDataDictionary(receivedReplace1),
                                                                   quickfix.field.MsgType.EXECUTION_REPORT,
                                                                   order1PendingReplace);
        Session.sendToTarget(order1PendingReplace,
                             target1);
        verifyOrderStatus(order.getOrderID(),
                          replace1.getOrderID(),
                          OrderStatus.PendingReplace);
        reports.clear();
        // send replace
        Message order1Replace = buildMessage("35=8",
                                             "58=replace1,6=0,11="+replace1.getOrderID()+",14=0,15=USD,17="+generateId()+",20=0,21=3,22=1,31=0,32=0,37="+order1OrderId+",38="+order1Qty.toPlainString()+",39="+OrderStatus.Replaced.getFIXValue()+",40="+OrderType.Limit.getFIXValue()+",41="+order.getOrderID()+",44="+order1Price.toPlainString()+",54="+Side.Buy.getFIXValue()+",59="+TimeInForce.GoodTillCancel.getFIXValue()+",60=20141210-15:04:55.098,150="+ExecutionType.Replace.getFIXValue()+",151="+order1Qty.toPlainString(),
                                             quickfix.field.MsgType.EXECUTION_REPORT,
                                             messageFactory);
         InstrumentToMessage.SELECTOR.forInstrument(instrument).set(instrument,
                                                                    FIXMessageUtil.getDataDictionary(receivedReplace1),
                                                                    quickfix.field.MsgType.EXECUTION_REPORT,
                                                                    order1Replace);
         Session.sendToTarget(order1Replace,
                              target1);
         verifyOrderStatus(order.getOrderID(),
                           replace1.getOrderID(),
                           OrderStatus.Replaced);
         // replace again
         OrderReplace replace2 = Factory.getInstance().createOrderReplace((ExecutionReport)waitForClientReport());
         client.sendOrder(replace2);
         Message receivedReplace2 = waitForAndVerifySenderMessage(sender1,
                                                                  quickfix.field.MsgType.ORDER_CANCEL_REPLACE_REQUEST);
         reports.clear();
         // send pending replace
         Message order2PendingReplace = buildMessage("35=8",
                                                    "58=pending replace2,6=0,11="+replace2.getOrderID()+",14=0,15=USD,17="+generateId()+",20=0,21=3,22=1,31=0,32=0,37="+order1OrderId+",38="+order1Qty.toPlainString()+",39="+OrderStatus.PendingReplace.getFIXValue()+",40="+OrderType.Limit.getFIXValue()+",41="+replace1.getOrderID()+",44="+order1Price.toPlainString()+",54="+Side.Buy.getFIXValue()+",59="+TimeInForce.GoodTillCancel.getFIXValue()+",60=20141210-15:04:55.098,150="+ExecutionType.PendingReplace.getFIXValue()+",151="+order1Qty.toPlainString(),
                                                    quickfix.field.MsgType.EXECUTION_REPORT,
                                                    messageFactory);
         InstrumentToMessage.SELECTOR.forInstrument(instrument).set(instrument,
                                                                    FIXMessageUtil.getDataDictionary(receivedReplace2),
                                                                    quickfix.field.MsgType.EXECUTION_REPORT,
                                                                    order2PendingReplace);
         Session.sendToTarget(order2PendingReplace,
                              target1);
         verifyOrderStatus(order.getOrderID(),
                           replace2.getOrderID(),
                           OrderStatus.PendingReplace);
         reports.clear();
         // send replace
         Message order2Replace = buildMessage("35=8",
                                              "58=replace2,6=0,11="+replace2.getOrderID()+",14=0,15=USD,17="+generateId()+",20=0,21=3,22=1,31=0,32=0,37="+order1OrderId+",38="+order1Qty.toPlainString()+",39="+OrderStatus.Replaced.getFIXValue()+",40="+OrderType.Limit.getFIXValue()+",41="+replace1.getOrderID()+",44="+order1Price.toPlainString()+",54="+Side.Buy.getFIXValue()+",59="+TimeInForce.GoodTillCancel.getFIXValue()+",60=20141210-15:04:55.098,150="+ExecutionType.Replace.getFIXValue()+",151="+order1Qty.toPlainString(),
                                              quickfix.field.MsgType.EXECUTION_REPORT,
                                              messageFactory);
          InstrumentToMessage.SELECTOR.forInstrument(instrument).set(instrument,
                                                                     FIXMessageUtil.getDataDictionary(receivedReplace2),
                                                                     quickfix.field.MsgType.EXECUTION_REPORT,
                                                                     order2Replace);
          Session.sendToTarget(order2Replace,
                               target1);
          verifyOrderStatus(order.getOrderID(),
                            replace2.getOrderID(),
                            OrderStatus.Replaced);
          reports.clear();
         // partially fill the replaced order
         BigDecimal lastQty = new BigDecimal(100);
         BigDecimal cumQty = new BigDecimal(100);
         BigDecimal leavesQty = order1Qty.subtract(cumQty);
         Message order1Fill1 = buildMessage("35=8",
                                            "58=fill1,6="+order1Price.toPlainString()+",11="+replace2.getOrderID()+",14="+cumQty.toPlainString()+",15=USD,17="+generateId()+",20=0,21=3,22=1,31="+lastQty.toPlainString()+",32="+lastQty.toPlainString()+",37="+order1OrderId+",38="+order1Qty.toPlainString()+",39="+OrderStatus.PartiallyFilled.getFIXValue()+",40="+OrderType.Limit.getFIXValue()+",44="+order1Price.toPlainString()+",54="+Side.Buy.getFIXValue()+",59="+TimeInForce.GoodTillCancel.getFIXValue()+",60=20141210-15:04:55.098,150="+ExecutionType.PartialFill.getFIXValue()+",151="+leavesQty.toPlainString(),
                                            quickfix.field.MsgType.EXECUTION_REPORT,
                                            messageFactory);
          InstrumentToMessage.SELECTOR.forInstrument(instrument).set(instrument,
                                                                     FIXMessageUtil.getDataDictionary(receivedReplace1),
                                                                     quickfix.field.MsgType.EXECUTION_REPORT,
                                                                     order1Fill1);
          Session.sendToTarget(order1Fill1,
                               target1);
          verifyOrderStatus(order.getOrderID(),
                            replace2.getOrderID(),
                            OrderStatus.PartiallyFilled);
          reports.clear();
    }
}
