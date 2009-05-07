package org.marketcetera.client.jms;

import javax.jms.ConnectionFactory;
import org.junit.Test;
import org.marketcetera.client.MockServer;
import org.marketcetera.client.brokers.BrokerStatus;
import org.marketcetera.trade.TradeMessage;
import org.marketcetera.util.test.TestCaseBase;
import org.springframework.jms.core.JmsTemplate;
import quickfix.Message;

import static org.junit.Assert.*;

/**
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id$
 */

/* $License$ */

public class JmsManagerTest
    extends TestCaseBase
{
    private static final String TEST_SEND_DEST=
        "sender";
    private static final String TEST_REPLY_DEST=
        "reply";
    private static final String TEST_REPLY_IGNORED=
        "replyIgnored";
    private static final String TEST_Q_EXT=
        "-q";
    private static final String TEST_TMX_EXT=
        "-tmx";
    private static final String TEST_OEX_EXT=
        "-oex";
    private static final String TEST_BSX_EXT=
        "-bsx";
    static final int TEST_COUNT=
        10;


    private static <T> void single
        (JmsTemplate sender,
         SampleReplyHandler<T> replies[],
         SampleReceiveOnlyHandler<T> receivers[])
        throws Exception
    {
        // Send messages.
        SampleReplyHandler<T> firstReply=replies[0];
        for (int i=0;i<TEST_COUNT;i++) {
            sender.convertAndSend(firstReply.create(i));
        }
        // Wait until delivery is complete.
        Thread.sleep(1000);
        // Confirm transferred data matches expected values.
        for (SampleReplyHandler<T> reply:replies) {
            assertEquals(TEST_COUNT,reply.getReceived().size());
            assertEquals(TEST_COUNT,reply.getReplies().size());
        }
        for (SampleReceiveOnlyHandler<T> receiver:receivers) {
            assertEquals(TEST_COUNT,receiver.getReceived().size());
        }
        for (int i=0;i<TEST_COUNT;i++) {
            for (SampleReplyHandler<T> reply:replies) {
                assertTrue(firstReply.isEqual(i,reply.getReceived().get(i)));
                assertTrue(firstReply.isEqual(i+1,reply.getReplies().get(i)));
            }
            for (SampleReceiveOnlyHandler<T> receiver:receivers) {
                assertTrue(firstReply.isEqual
                           (i+1,receiver.getReceived().get(i)));
            }
            i++;
        }
    }

    private static <T> void single
        (JmsTemplate sender,
         SampleReplyHandler<T> reply,
         SampleReceiveOnlyHandler<T> receiver)
        throws Exception
    {
        single(sender,
               new SampleReplyHandler[]{reply},
               new SampleReceiveOnlyHandler[]{receiver});
    }


    @Test
    public void simpleMessages()
        throws Exception
    {
        MockServer server=new MockServer();
        JmsManager mgr=new JmsManager
            ((ConnectionFactory)
             (server.getContext().getBean("metc_connection_factory_in")),
             (ConnectionFactory)
             (server.getContext().getBean("metc_connection_factory_out")));

        // STANDARD CONVERTER.

        String senderName=TEST_SEND_DEST;
        String replyName=TEST_REPLY_DEST;

        // Queues.
        SampleIntegerReplyHandler reply=
            new SampleIntegerReplyHandler();
        SampleReceiveOnlyHandler<Integer> receiveOnly=
            new SampleReceiveOnlyHandler<Integer>();
        mgr.getIncomingJmsFactory().registerHandler
            (reply,senderName,false,replyName,false);
        mgr.getIncomingJmsFactory().registerHandler
            (receiveOnly,replyName,false);
        single(mgr.getOutgoingJmsFactory().createJmsTemplate
               (senderName,false),reply,receiveOnly);

        // Topics.
        SampleIntegerReplyHandler[] replies=new SampleIntegerReplyHandler[] {
            new SampleIntegerReplyHandler(),
            new SampleIntegerReplyHandler()};
        SampleReceiveOnlyHandler[] receivers=new SampleReceiveOnlyHandler[] {
            new SampleReceiveOnlyHandler<Integer>(),
            new SampleReceiveOnlyHandler<Integer>()};            
        mgr.getIncomingJmsFactory().registerHandler
            (replies[0],senderName,true,replyName,true);
        mgr.getIncomingJmsFactory().registerHandler
            (replies[1],senderName,true,TEST_REPLY_IGNORED,true);
        mgr.getIncomingJmsFactory().registerHandler
            (receivers[0],replyName,true);
        mgr.getIncomingJmsFactory().registerHandler
            (receivers[1],replyName,true);
        single(mgr.getOutgoingJmsFactory().createJmsTemplate
               (senderName,true),replies,receivers);

        // QUICKFIX/J CONVERTER.

        senderName=TEST_SEND_DEST+TEST_Q_EXT;
        replyName=TEST_REPLY_DEST+TEST_Q_EXT;

        // Queues.
        SampleQMessageReplyHandler replyQ=
            new SampleQMessageReplyHandler();
        SampleReceiveOnlyHandler<Message> receiveOnlyQ=
            new SampleReceiveOnlyHandler<Message>();
        mgr.getIncomingJmsFactory().registerHandlerQ
            (replyQ,senderName,false,replyName,false);
        mgr.getIncomingJmsFactory().registerHandlerQ
            (receiveOnlyQ,replyName,false);
        single(mgr.getOutgoingJmsFactory().createJmsTemplateQ
               (senderName,false),replyQ,receiveOnlyQ);

        // Topics.
        SampleQMessageReplyHandler[] repliesQ=new SampleQMessageReplyHandler[] {
            new SampleQMessageReplyHandler(),
            new SampleQMessageReplyHandler()};
        SampleReceiveOnlyHandler[] receiversQ=new SampleReceiveOnlyHandler[] {
            new SampleReceiveOnlyHandler<Message>(),
            new SampleReceiveOnlyHandler<Message>()};            
        mgr.getIncomingJmsFactory().registerHandlerQ
            (repliesQ[0],senderName,true,replyName,true);
        mgr.getIncomingJmsFactory().registerHandlerQ
            (repliesQ[1],senderName,true,TEST_REPLY_IGNORED,true);
        mgr.getIncomingJmsFactory().registerHandlerQ
            (receiversQ[0],replyName,true);
        mgr.getIncomingJmsFactory().registerHandlerQ
            (receiversQ[1],replyName,true);
        single(mgr.getOutgoingJmsFactory().createJmsTemplateQ
               (senderName,true),repliesQ,receiversQ);

        // FIX AGNOSTIC XML CONVERTER: TRADE MESSAGES.

        senderName=TEST_SEND_DEST+TEST_TMX_EXT;
        replyName=TEST_REPLY_DEST+TEST_TMX_EXT;

        // Queues.
        SampleOrderReplyHandler replyTMX=
            new SampleOrderReplyHandler();
        SampleReceiveOnlyHandler<TradeMessage> receiveOnlyTMX=
            new SampleReceiveOnlyHandler<TradeMessage>();
        mgr.getIncomingJmsFactory().registerHandlerTMX
            (replyTMX,senderName,false,replyName,false);
        mgr.getIncomingJmsFactory().registerHandlerTMX
            (receiveOnlyTMX,replyName,false);
        single(mgr.getOutgoingJmsFactory().createJmsTemplateX
               (senderName,false),replyTMX,receiveOnlyTMX);

        // Topics.
        SampleOrderReplyHandler[] repliesTMX=new SampleOrderReplyHandler[] {
            new SampleOrderReplyHandler(),
            new SampleOrderReplyHandler()};
        SampleReceiveOnlyHandler[] receiversTMX=new SampleReceiveOnlyHandler[] {
            new SampleReceiveOnlyHandler<TradeMessage>(),
            new SampleReceiveOnlyHandler<TradeMessage>()};            
        mgr.getIncomingJmsFactory().registerHandlerTMX
            (repliesTMX[0],senderName,true,replyName,true);
        mgr.getIncomingJmsFactory().registerHandlerTMX
            (repliesTMX[1],senderName,true,TEST_REPLY_IGNORED,true);
        mgr.getIncomingJmsFactory().registerHandlerTMX
            (receiversTMX[0],replyName,true);
        mgr.getIncomingJmsFactory().registerHandlerTMX
            (receiversTMX[1],replyName,true);
        single(mgr.getOutgoingJmsFactory().createJmsTemplateX
               (senderName,true),repliesTMX,receiversTMX);

        // FIX AGNOSTIC XML CONVERTER: ORDER ENVELOPES.

        senderName=TEST_SEND_DEST+TEST_OEX_EXT;
        replyName=TEST_REPLY_DEST+TEST_OEX_EXT;

        // Queues.
        SampleEnvelopeReplyHandler replyOEX=
            new SampleEnvelopeReplyHandler();
        SampleReceiveOnlyHandler<OrderEnvelope> receiveOnlyOEX=
            new SampleReceiveOnlyHandler<OrderEnvelope>();
        mgr.getIncomingJmsFactory().registerHandlerOEX
            (replyOEX,senderName,false,replyName,false);
        mgr.getIncomingJmsFactory().registerHandlerOEX
            (receiveOnlyOEX,replyName,false);
        single(mgr.getOutgoingJmsFactory().createJmsTemplateX
               (senderName,false),replyOEX,receiveOnlyOEX);

        // Topics.
        SampleEnvelopeReplyHandler[] repliesOEX=
            new SampleEnvelopeReplyHandler[] {
            new SampleEnvelopeReplyHandler(),
            new SampleEnvelopeReplyHandler()};
        SampleReceiveOnlyHandler[] receiversOEX=new SampleReceiveOnlyHandler[] {
            new SampleReceiveOnlyHandler<OrderEnvelope>(),
            new SampleReceiveOnlyHandler<OrderEnvelope>()};            
        mgr.getIncomingJmsFactory().registerHandlerOEX
            (repliesOEX[0],senderName,true,replyName,true);
        mgr.getIncomingJmsFactory().registerHandlerOEX
            (repliesOEX[1],senderName,true,TEST_REPLY_IGNORED,true);
        mgr.getIncomingJmsFactory().registerHandlerOEX
            (receiversOEX[0],replyName,true);
        mgr.getIncomingJmsFactory().registerHandlerOEX
            (receiversOEX[1],replyName,true);
        single(mgr.getOutgoingJmsFactory().createJmsTemplateX
               (senderName,true),repliesOEX,receiversOEX);

        // FIX AGNOSTIC XML CONVERTER: BROKER STATUS MESSAGES.

        senderName=TEST_SEND_DEST+TEST_BSX_EXT;
        replyName=TEST_REPLY_DEST+TEST_BSX_EXT;

        // Queues.
        SampleBrokerStatusHandler replyBSX=
            new SampleBrokerStatusHandler();
        SampleReceiveOnlyHandler<BrokerStatus> receiveOnlyBSX=
            new SampleReceiveOnlyHandler<BrokerStatus>();
        mgr.getIncomingJmsFactory().registerHandlerBSX
            (replyBSX,senderName,false,replyName,false);
        mgr.getIncomingJmsFactory().registerHandlerBSX
            (receiveOnlyBSX,replyName,false);
        single(mgr.getOutgoingJmsFactory().createJmsTemplateX
               (senderName,false),replyBSX,receiveOnlyBSX);

        // Topics.
        SampleBrokerStatusHandler[] repliesBSX=
            new SampleBrokerStatusHandler[] {
            new SampleBrokerStatusHandler(),
            new SampleBrokerStatusHandler()};
        SampleReceiveOnlyHandler[] receiversBSX=new SampleReceiveOnlyHandler[] {
            new SampleReceiveOnlyHandler<BrokerStatus>(),
            new SampleReceiveOnlyHandler<BrokerStatus>()};            
        mgr.getIncomingJmsFactory().registerHandlerBSX
            (repliesBSX[0],senderName,true,replyName,true);
        mgr.getIncomingJmsFactory().registerHandlerBSX
            (repliesBSX[1],senderName,true,TEST_REPLY_IGNORED,true);
        mgr.getIncomingJmsFactory().registerHandlerBSX
            (receiversBSX[0],replyName,true);
        mgr.getIncomingJmsFactory().registerHandlerBSX
            (receiversBSX[1],replyName,true);
        single(mgr.getOutgoingJmsFactory().createJmsTemplateX
               (senderName,true),repliesBSX,receiversBSX);
    }
}
