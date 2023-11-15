package org.marketcetera.trade;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.math.BigDecimal;

import org.junit.Test;
import org.marketcetera.core.instruments.InstrumentToMessage;
import org.marketcetera.fix.FixSession;
import org.marketcetera.quickfix.FIXMessageFactory;
import org.marketcetera.quickfix.FIXMessageUtil;
import org.marketcetera.quickfix.FIXVersion;
import org.marketcetera.test.DareTestBase;
import org.marketcetera.trade.dao.PersistentExecutionReport;
import org.marketcetera.trade.dao.QPersistentExecutionReport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.google.common.collect.Sets;
import com.querydsl.core.BooleanBuilder;

import quickfix.Session;
import quickfix.SessionID;

/* $License$ */

/**
 * Tests root order id functionality.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@EnableAutoConfiguration
public class RootOrderIdTest
        extends DareTestBase
{
    /**
     * Tests handling of an order chain that incorrectly has two root order id values.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testDuplicateRootOrderIdValues()
            throws Exception
    {
        // create two unrelated orders
        // modify the exec_report record of one of them to share the same root order id
        Instrument inInstrument = new Equity("METC");
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
        ExecutionReport report1 = (ExecutionReport)reports.getFirst();
        reports.clear();
        // send a second order
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
        quickfix.Message receivedorder2 = waitForAndVerifySenderMessage(sender1,
                                                                        quickfix.field.MsgType.ORDER_SINGLE);
        // send a pending new
        quickfix.Message order2PendingNew = buildMessage("35=8",
                                                         "58=pending new,6=0,11="+order2.getOrderID()+",14=0,15=USD,17="+generateId()+",20=0,21=3,22=1,31=0,32=0,37="+order2OrderId+",38="+order2Qty.toPlainString()+",39="+OrderStatus.PendingNew.getFIXValue()+",40="+OrderType.Limit.getFIXValue()+",44="+order2Price.toPlainString()+",54="+Side.Buy.getFIXValue()+",59="+TimeInForce.GoodTillCancel.getFIXValue()+",60=20141210-15:04:55.098,150="+ExecutionType.PendingNew.getFIXValue()+",151="+order2Qty.toPlainString(),
                                                         quickfix.field.MsgType.EXECUTION_REPORT,
                                                         messageFactory);
        InstrumentToMessage.SELECTOR.forInstrument(inInstrument).set(inInstrument,
                                                                     FIXMessageUtil.getDataDictionary(receivedorder2),
                                                                     quickfix.field.MsgType.EXECUTION_REPORT,
                                                                     order2PendingNew);
        Session.sendToTarget(order2PendingNew,
                             target1);
        verifyOrderStatus(order2.getOrderID(),
                          order2.getOrderID(),
                          OrderStatus.PendingNew);
        verifyOpenOrders(Sets.newHashSet(order1.getOrderID(),order2.getOrderID()));
        reports.clear();
        // send new
        quickfix.Message order2New = buildMessage("35=8",
                                                  "58=new,6=0,11="+order2.getOrderID()+",14=0,15=USD,17="+generateId()+",20=0,21=3,22=1,31=0,32=0,37="+order2OrderId+",38="+order2Qty.toPlainString()+",39="+OrderStatus.New.getFIXValue()+",40="+OrderType.Limit.getFIXValue()+",44="+order2Price.toPlainString()+",54="+Side.Buy.getFIXValue()+",59="+TimeInForce.GoodTillCancel.getFIXValue()+",60=20141210-15:04:55.098,150="+ExecutionType.New.getFIXValue()+",151="+order2Qty.toPlainString(),
                                                  quickfix.field.MsgType.EXECUTION_REPORT,
                                                  messageFactory);
        InstrumentToMessage.SELECTOR.forInstrument(inInstrument).set(inInstrument,
                                                                     FIXMessageUtil.getDataDictionary(receivedorder2),
                                                                     quickfix.field.MsgType.EXECUTION_REPORT,
                                                                     order2New);
        Session.sendToTarget(order2New,
                             target1);
        verifyOrderStatus(order2.getOrderID(),
                          order2.getOrderID(),
                          OrderStatus.New);
        ExecutionReport report2 = (ExecutionReport)reports.getFirst();
        reports.clear();
        // check root order ids of both orders
        OrderID rootOrderId1_1 = rootOrderIdFactory.getRootOrderId(report1);
        OrderID rootOrderId2_1 = rootOrderIdFactory.getRootOrderId(report2);
        OrderID rootOrderId1_2 = rootOrderIdFactory.getRootOrderId(order1New);
        OrderID rootOrderId2_2 = rootOrderIdFactory.getRootOrderId(order2New);
        assertNotNull(rootOrderId1_1);
        assertNotNull(rootOrderId2_1);
        assertNotNull(rootOrderId1_2);
        assertNotNull(rootOrderId2_2);
        assertEquals(rootOrderId1_1,
                     rootOrderId1_2);
        assertEquals(rootOrderId2_1,
                     rootOrderId2_2);
        // this is all right and correct, now, cause the problem we're trying to fix by forcing an overlap in the root order ids
        // this should not happen under normal circumstances, but, when it does happen, it needs to be able to be fixable or recoverable
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("transactionModuleTransaction");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setReadOnly(false);
        TransactionStatus status = txManager.getTransaction(def);
        try {
            BooleanBuilder where = new BooleanBuilder();
            where = where.and(QPersistentExecutionReport.persistentExecutionReport.executionId.eq(report1.getExecutionID()));
            PersistentExecutionReport pExecutionReport1 = executionReportDao.findOne(where).get();
            where = new BooleanBuilder();
            where = where.and(QPersistentExecutionReport.persistentExecutionReport.executionId.eq(report2.getExecutionID()));
            PersistentExecutionReport pExecutionReport2 = executionReportDao.findOne(where).get();
            assertNotNull(pExecutionReport1,
                          "No report for " + report1.getExecutionID());
            assertNotNull(pExecutionReport2,
                          "No report for " + report2.getExecutionID());
            // take the report2 and set the root order id to report1
            pExecutionReport2.setRootOrderID(pExecutionReport1.getRootOrderID());
            pExecutionReport2 = executionReportDao.save(pExecutionReport2);
            // now, look for the root order id of report2 (if this succeeds at all, we're in good shape)
            rootOrderIdFactory.getRootOrderId(report2);
            rootOrderIdFactory.getRootOrderId(order2New);
        } finally {
            txManager.commit(status);
        }
    }
    /**
     * transaction manager value
     */
    @Autowired
    private JpaTransactionManager txManager;
    /**
     * provides access to root order id value
     */
    @Autowired
    private RootOrderIdFactory rootOrderIdFactory;
}
