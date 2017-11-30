package org.marketcetera.test.fix;

import java.math.BigDecimal;
import java.util.Deque;
import java.util.UUID;

import org.junit.Test;
import org.marketcetera.event.HasFIXMessage;
import org.marketcetera.modules.fix.FIXMessageHolder;
import org.marketcetera.modules.fix.FixAcceptorModule;
import org.marketcetera.modules.fix.FixDataRequest;
import org.marketcetera.modules.fix.FixInitiatorModule;
import org.marketcetera.modules.headwater.HeadwaterModule;
import org.marketcetera.quickfix.FIXMessageFactory;
import org.marketcetera.quickfix.FIXMessageUtil;
import org.marketcetera.quickfix.FIXVersion;
import org.marketcetera.test.IntegrationTestBase;
import org.marketcetera.trade.Equity;
import org.marketcetera.trade.ExecutionTransType;
import org.marketcetera.trade.ExecutionType;
import org.marketcetera.trade.OrderStatus;
import org.marketcetera.trade.Side;
import org.marketcetera.trade.TimeInForce;

import com.google.common.collect.Lists;

import quickfix.Message;

/* $License$ */

/**
 * Test {@link FixAcceptorModule} and {@link FixInitiatorModule}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class FixModuleTest
        extends IntegrationTestBase
{
    /**
     * Test starting and connecting the acceptors and initiators.
     *
     * @throws Exception if an unexpected failure occurs
     */
    @Test
    public void testStartAndConnect()
            throws Exception
    {
    }
    /**
     * Test that admin messages can be received in a dataflow
     *
     * @throws Exception if an unexpected failure occurs
     */
    @Test
    public void testAdminInitiatorMessageDataFlow()
            throws Exception
    {
        // receive admin messages only with no blacklist/whitelist
        FixDataRequest fixDataRequest = new FixDataRequest();
        fixDataRequest.setIncludeAdmin(true);
        fixDataRequest.setIncludeApp(false);
        fixDataRequest.getMessageWhiteList().clear();
        fixDataRequest.getMessageBlackList().clear();
        Deque<Object> receivedMessages = Lists.newLinkedList();
        // we only need a received data request since admin messages flow automatically
        dataFlows.add(moduleManager.createDataFlow(getInitiatorReceiveDataRequest(fixDataRequest,
                                                                                  receivedMessages)));
        waitForMessages(5,
                        receivedMessages);
    }
    /**
     * Test that app messages can be received in a dataflow
     *
     * @throws Exception if an unexpected failure occurs
     */
    @Test
    public void testAppInitiatorMessageDataFlow()
            throws Exception
    {
        // receive app messages only with no blacklist/whitelist
        FixDataRequest fixDataRequest = new FixDataRequest();
        fixDataRequest.setIncludeAdmin(false);
        fixDataRequest.setIncludeApp(true);
        fixDataRequest.getMessageWhiteList().clear();
        fixDataRequest.getMessageBlackList().clear();
        // this data flow is more complicated because we need to be able to inject messages on both sides
        // this data flow is to send messages via the initiator
        String initiatorHeadwaterInstance = generateHeadwaterInstanceName();
        dataFlows.add(moduleManager.createDataFlow(getInitiatorSendDataRequest(fixDataRequest,
                                                                               initiatorHeadwaterInstance)));
        HeadwaterModule initiatorSender = HeadwaterModule.getInstance(initiatorHeadwaterInstance);
        // this data flow is to send messages via the acceptor
        String acceptorHeadwaterInstance = generateHeadwaterInstanceName();
        dataFlows.add(moduleManager.createDataFlow(getAcceptorSendDataRequest(fixDataRequest,
                                                                              acceptorHeadwaterInstance)));
        HeadwaterModule acceptorSender = HeadwaterModule.getInstance(acceptorHeadwaterInstance);
        // this data flow is to receive messages from the initiator
        Deque<Object> initiatorMessages = Lists.newLinkedList();
        dataFlows.add(moduleManager.createDataFlow(getInitiatorReceiveDataRequest(fixDataRequest,
                                                                                  initiatorMessages)));
        // this data flow is to receive messages from the acceptor
        Deque<Object> acceptorMessages = Lists.newLinkedList();
        dataFlows.add(moduleManager.createDataFlow(getAcceptorReceiveDataRequest(fixDataRequest,
                                                                                 acceptorMessages)));
        FIXMessageFactory messageFactory = FIXVersion.FIX42.getMessageFactory();
        Message order = messageFactory.newLimitOrder(UUID.randomUUID().toString(),
                                                     Side.Buy.getFIXValue(),
                                                     new BigDecimal(1000),
                                                     new Equity("METC"),
                                                     new BigDecimal(100),
                                                     TimeInForce.GoodTillCancel.getFIXValue(),
                                                     null);
        messageFactory.addTransactionTimeIfNeeded(order);
        acceptorSender.emit(new FIXMessageHolder(acceptorSessions.iterator().next(),
                                                 order));
        waitForMessages(1,
                        initiatorMessages);
        // respond with an ER
        Message receivedOrder = ((HasFIXMessage)initiatorMessages.getFirst()).getMessage();
        Message receivedOrderAck = FIXMessageUtil.createExecutionReport(receivedOrder,
                                                                        OrderStatus.New,
                                                                        ExecutionType.New,
                                                                        ExecutionTransType.New,
                                                                        "Ack");
        messageFactory.addTransactionTimeIfNeeded(receivedOrderAck);
        initiatorSender.emit(new FIXMessageHolder(initiatorSessions.iterator().next(),
                                                  receivedOrderAck));
        waitForMessages(1,
                        acceptorMessages);
    }
}
