package org.marketcetera.ors.jms;

import java.io.File;
import javax.jms.ConnectionFactory;
import org.junit.Test;
import org.marketcetera.ors.DBInit;
import org.marketcetera.trade.TradeMessage;
import org.marketcetera.util.test.TestCaseBase;
import org.springframework.context.support.FileSystemXmlApplicationContext;
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
    private static final String TEST_ROOT=
        DIR_ROOT+File.separator+
        "conf"+File.separator;
    private static final String TEST_FILE=
        TEST_ROOT+"jms_test.xml";
    private static final String TEST_SEND_DEST=
        "sender";
    private static final String TEST_REPLY_DEST=
        "reply";
    private static final String TEST_Q_EXT=
        "-q";
    private static final String TEST_TM_EXT=
        "-tm";
    static final int TEST_COUNT=
        10;


    private static <T> void single
        (JmsTemplate sender,
         SampleReplyHandler<T> reply,
         SampleReceiveOnlyHandler<T> receiveOnly)
        throws Exception
    {
        // Send messages.
        for (int i=0;i<TEST_COUNT;i++) {
            sender.convertAndSend(reply.create(i));
        }
        // Wait until delivery is complete.
        Thread.sleep(1000);
        // Confirm transferred data matches expected values.
        assertEquals(TEST_COUNT,reply.getReceived().size());
        assertEquals(TEST_COUNT,reply.getReplies().size());
        assertEquals(TEST_COUNT,receiveOnly.getReceived().size());
        for (int i=0;i<TEST_COUNT;i++) {
            assertTrue(reply.isEqual(i,reply.getReceived().get(i)));
            assertTrue(reply.isEqual(i+1,reply.getReplies().get(i)));
            assertTrue(reply.isEqual(i+1,receiveOnly.getReceived().get(i)));
            i++;
        }
    }


    @Test
    public void simpleMessages()
        throws Exception
    {
        DBInit.initORSDB();
        FileSystemXmlApplicationContext context=
            new FileSystemXmlApplicationContext(TEST_FILE);
        JmsManager mgr=new JmsManager
            ((ConnectionFactory)
             (context.getBean("metc_connection_factory_in")),
             (ConnectionFactory)
             (context.getBean("metc_connection_factory_out")));

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
        reply=new SampleIntegerReplyHandler();
        receiveOnly=new SampleReceiveOnlyHandler<Integer>();
        mgr.getIncomingJmsFactory().registerHandler
            (reply,senderName,true,replyName,true);
        mgr.getIncomingJmsFactory().registerHandler
            (receiveOnly,replyName,true);
        single(mgr.getOutgoingJmsFactory().createJmsTemplate
               (senderName,true),reply,receiveOnly);

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
        replyQ=new SampleQMessageReplyHandler();
        receiveOnlyQ=new SampleReceiveOnlyHandler<Message>();
        mgr.getIncomingJmsFactory().registerHandlerQ
            (replyQ,senderName,true,replyName,true);
        mgr.getIncomingJmsFactory().registerHandlerQ
            (receiveOnlyQ,replyName,true);
        single(mgr.getOutgoingJmsFactory().createJmsTemplateQ
               (senderName,true),replyQ,receiveOnlyQ);

        // FIX AGNOSTIC CONVERTER.

        senderName=TEST_SEND_DEST+TEST_TM_EXT;
        replyName=TEST_REPLY_DEST+TEST_TM_EXT;

        // Queues.
        SampleOrderReplyHandler replyTM=
            new SampleOrderReplyHandler();
        SampleReceiveOnlyHandler<TradeMessage> receiveOnlyTM=
            new SampleReceiveOnlyHandler<TradeMessage>();
        mgr.getIncomingJmsFactory().registerHandlerTM
            (replyTM,senderName,false,replyName,false);
        mgr.getIncomingJmsFactory().registerHandlerTM
            (receiveOnlyTM,replyName,false);
        single(mgr.getOutgoingJmsFactory().createJmsTemplateTM
               (senderName,false),replyTM,receiveOnlyTM);

        // Topics.
        replyTM=new SampleOrderReplyHandler();
        receiveOnlyTM=new SampleReceiveOnlyHandler<TradeMessage>();
        mgr.getIncomingJmsFactory().registerHandlerTM
            (replyTM,senderName,true,replyName,true);
        mgr.getIncomingJmsFactory().registerHandlerTM
            (receiveOnlyTM,replyName,true);
        single(mgr.getOutgoingJmsFactory().createJmsTemplateTM
               (senderName,true),replyTM,receiveOnlyTM);
    }
}
