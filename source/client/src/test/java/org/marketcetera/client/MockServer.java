package org.marketcetera.client;

import org.marketcetera.client.jms.JmsManager;
import org.marketcetera.util.ws.stateless.ServiceInterface;
import org.marketcetera.util.ws.stateful.Server;
import org.marketcetera.util.ws.stateful.SessionManager;
import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.core.LoggerConfiguration;
import org.marketcetera.core.FIXVersionTestSuite;
import org.marketcetera.quickfix.FIXFieldConverterNotAvailable;
import org.marketcetera.quickfix.FIXMessageUtil;
import org.marketcetera.trade.Factory;
import org.marketcetera.trade.BrokerID;
import org.marketcetera.trade.Originator;
import org.marketcetera.trade.MessageCreationException;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.listener.SimpleMessageListenerContainer;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import quickfix.Message;
import quickfix.FieldNotFound;
import quickfix.InvalidMessage;
import quickfix.field.*;

import javax.jms.ConnectionFactory;
import javax.xml.bind.JAXBException;

import java.util.*;
import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/* $License$ */
/**
 * A mock server for testing the Client.
 * The server can be connected to at the URL {@link #URL}.
 * Any username / password can be used to connect to the server as long
 * as the username and password are identical.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public class MockServer {
    public static void main(String[] args)
        throws InterruptedException, FIXFieldConverterNotAvailable {
        LoggerConfiguration.logSetup();
        FIXVersionTestSuite.initializeFIXDataDictionaryManager(
                FIXVersionTestSuite.ALL_FIX_VERSIONS);
        MockServer ms = new MockServer();
        if(args.length > 0) {
            File msgFile = new File(args[0]);
            if(msgFile.canRead()) {
                SLF4JLoggerProxy.debug(MockServer.class,
                        "Reading messages from file {}",
                        msgFile.getAbsolutePath());
                ms.readMessagesFromFile(msgFile);
            }
        }
        synchronized(ms) {
            ms.wait();
        }
        ms.close();
    }
    public MockServer()
    {
        mContext = new ClassPathXmlApplicationContext
            ("mock_server.xml"); //$NON-NLS-1$
        mContext.registerShutdownHook();
        mContext.start();

        mHandler = new MockMessageHandler();

        JmsManager jmsMgr=new JmsManager
            ((ConnectionFactory)mContext.getBean
             ("metc_connection_factory_in"),
             (ConnectionFactory)mContext.getBean
             ("metc_connection_factory_out"));
        try {
            mOrderEnvelopeListener = jmsMgr.getIncomingJmsFactory().
                registerHandlerOEX
                (mHandler,Service.REQUEST_QUEUE,false);
        } catch (JAXBException ex) {
            throw new IllegalStateException
                ("Cannot initialize request queue listener",ex);
        }
        try {
            mStatusSender = jmsMgr.getOutgoingJmsFactory().
                createJmsTemplateX
                (Service.BROKER_STATUS_TOPIC,true);
        } catch (JAXBException ex) {
            throw new IllegalStateException
                ("Cannot initialize broker status sender",ex);
        }

        // Use default Server host and port 
        SessionManager<Object> sessionManager=new SessionManager<Object>
            (new MockSessionFactory(jmsMgr,mHandler));
        mServer=new Server<Object>
            (new MockAuthenticator(),sessionManager);
        mServiceImpl = new MockServiceImpl(sessionManager);
        mServiceInterface = mServer.publish(mServiceImpl,Service.class);
    }
    public void close() {
        mOrderEnvelopeListener.destroy();
        mContext.close();
        mServiceInterface.stop();
        mServer.stop();
    }

    MockServiceImpl getServiceImpl()
    {
        return mServiceImpl;
    }

    public ClassPathXmlApplicationContext getContext() {
        return mContext;
    }
    public MockMessageHandler getHandler() {
        return mHandler;
    }
    public JmsTemplate getStatusSender() {
        return mStatusSender;
    }
    private void readMessagesFromFile(File inFile) {
        BufferedReader reader = null;
        String line;
        List<Message> messages = new LinkedList<Message>();
        try {
            reader = new BufferedReader(new FileReader(inFile));
            int lineNumber = 1;
            while((line = reader.readLine()) != null) {
                try {
                    messages.add(new Message(line));
                } catch (InvalidMessage e) {
                    SLF4JLoggerProxy.warn(this, e,
                            "Ignoring Error @ line {} from message {}",
                            lineNumber, line);
                }
                lineNumber++;
            }
            sortAndAddMessages(messages);
        } catch (IOException e) {
            SLF4JLoggerProxy.warn(this, "Ignoring Error", e);
        } finally {
            if(reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    SLF4JLoggerProxy.warn(this, "Ignoring Error", e);
                }
            }
        }

    }

    private void sortAndAddMessages(List<Message> inMessages) {
        //Sort messages.
        Collections.sort(inMessages,new Comparator<Message>(){
            public int compare(Message o1, Message o2) {
                try {
                    if(o1.isSetField(ClOrdID.FIELD) && o2.isSetField(ClOrdID.FIELD)) {
                        String s1 = o1.getString(ClOrdID.FIELD);
                        String s2 = o2.getString(ClOrdID.FIELD);
                        if(s1.equals(s2)) {
                            //Compare exec type
                            if(o1.isSetField(OrdStatus.FIELD) && o2.isSetField(OrdStatus.FIELD)) {
                                char c1 = o1.getChar(OrdStatus.FIELD);
                                char c2 = o2.getChar(OrdStatus.FIELD);
                                if(c1 == c2) {
                                    if(o1.getHeader().isSetField(MsgSeqNum.FIELD) && o2.getHeader().isSetField(MsgSeqNum.FIELD)) {
                                        int i1 = o1.getHeader().getInt(MsgSeqNum.FIELD);
                                        int i2 = o2.getHeader().getInt(MsgSeqNum.FIELD);
                                        return i1 - i2;
                                    }
                                } else {
                                    if (c1 == OrdStatus.PENDING_NEW) c1 = OrdStatus.NEW - 1;
                                    if (c2 == OrdStatus.PENDING_NEW) c2 = OrdStatus.NEW - 1;
                                    return c1 - c2;
                                }
                            }

                        } else {
                            return s1.compareTo(s2);
                        }
                    }
                    if(o1.getHeader().isSetField(SendingTime.FIELD) && o2.getHeader().isSetField(SendingTime.FIELD)) {
                        Date d1 = o1.getHeader().getUtcTimeStamp(SendingTime.FIELD);
                        Date d2 = o2.getHeader().getUtcTimeStamp(SendingTime.FIELD);
                        return d1.compareTo(d2);
                    }
                } catch (FieldNotFound ignore) {
                }
                SLF4JLoggerProxy.error(this, "Message comparison failure:{} AND {}", o1, o2);
                return 0;
            }
        });
        for(Message message: inMessages) {
            try {
                if (FIXMessageUtil.isExecutionReport(message)) {
                    getHandler().addToSend(Factory.getInstance().createExecutionReport(
                        message, new BrokerID("default"),
                            Originator.Server, null, null));
                } else if(FIXMessageUtil.isCancelReject(message)) {
                    getHandler().addToSend(Factory.getInstance().createOrderCancelReject(
                        message, new BrokerID("default"), Originator.Server, null, null));
                } else {
                    SLF4JLoggerProxy.warn(this, "Ignoring:{}", message);
                }
            } catch (MessageCreationException e) {
                SLF4JLoggerProxy.warn(this, "Ignoring Error", e);
            }
        }
    }

    /**
     * The URL for the broker hosted by this server.
     * Do note that this URL is changed, the mock_server.xml & broker.xml
     * files need to be updated as well.
     */
    public static final String URL = "tcp://localhost:61616";
    private final ClassPathXmlApplicationContext mContext;
    private Server<?> mServer;
    private ServiceInterface mServiceInterface;
    private MockServiceImpl mServiceImpl;
    private MockMessageHandler mHandler;
    private SimpleMessageListenerContainer mOrderEnvelopeListener;
    private JmsTemplate mStatusSender;
}
