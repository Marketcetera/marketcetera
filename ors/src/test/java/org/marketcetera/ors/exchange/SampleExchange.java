package org.marketcetera.ors.exchange;

import java.math.BigDecimal;
import java.util.Date;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import org.marketcetera.ors.ReportCache;
import org.marketcetera.quickfix.FIXMessageFactory;
import org.marketcetera.quickfix.FIXMessageUtil;
import org.marketcetera.quickfix.FIXVersion;
import org.marketcetera.trade.ExecutionReport;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.quickfix.SpringSessionSettings;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import quickfix.*;
import quickfix.field.*;

/**
 * A miniscule exchange.
 *
 * @author tlerios@marketcetera.com
 * @since 2.0.0
 * @version $Id$
 */

/* $License$ */

public class SampleExchange
    implements Application
{

    // INSTANCE DATA.

    private final AbstractApplicationContext mContext;
    private final Acceptor mAcceptor;
    private final BlockingQueue<Event> mEvents;


    // CONSTRUCTORS.

    public SampleExchange(String configFile)
        throws ConfigError
    {
        mDataDictionary=new DataDictionary(FIXVersion.FIX_SYSTEM.getDataDictionaryURL());
        mEvents=new LinkedBlockingQueue<Event>();
        mContext=new FileSystemXmlApplicationContext
            (new String[] {"file:"+configFile});
        SpringSessionSettings settings=
            (SpringSessionSettings)mContext.getBean("x_settings");
        mAcceptor=new SocketAcceptor
            (this,settings.getQMessageStoreFactory(),
             settings.getQSettings(),settings.getQLogFactory(),
             new DefaultMessageFactory());
    }


    // Application.

    @Override
    public void onCreate
        (SessionID sessionId)
    {
        add(new CreateEvent(sessionId));
    }

    @Override
    public void onLogon
        (SessionID sessionId)
    {
        add(new LogonEvent(sessionId));
    }

    @Override
    public void onLogout
        (SessionID sessionId)
    {
        add(new LogoutEvent(sessionId));
    }

    @Override
    public void toAdmin
        (Message message,
         SessionID sessionId)
    {
        add(new ToAdminEvent(sessionId,message));
    }

    @Override
    public void toApp
        (Message message,
         SessionID sessionId)
    {
        add(new ToAppEvent(sessionId,message));
    }

    @Override
    public void fromAdmin
        (Message message,
         SessionID sessionId)
    {
        add(new FromAdminEvent(sessionId,message));
    }

    @Override
    public void fromApp(Message message,
                        SessionID sessionId)
    {
        add(new FromAppEvent(sessionId,message));
        try {
            Message reply = createExecutionReport(message);
            sendMessage(reply,
                        sessionId);
        } catch (FieldNotFound e) {
            throw new RuntimeException(e);
        } catch (SessionNotFound e) {
            throw new RuntimeException(e);
        } catch (ConfigError e) {
            throw new RuntimeException(e);
        }
    }
    private Message createExecutionReport(Message inMessage)
            throws FieldNotFound, ConfigError
    {
        // Choose status that matches that of the incoming message.
        char ordStatus;
        String orderID;
        if(FIXMessageUtil.isOrderSingle(inMessage)) {
            ordStatus=OrdStatus.PENDING_NEW;
            orderID=SELF_ORDER_ID;
        } else if (FIXMessageUtil.isCancelReplaceRequest(inMessage)) {
            ordStatus=OrdStatus.PENDING_REPLACE;
            orderID = getOptFieldStr(inMessage,OrderID.FIELD);
            if(orderID==null) {
                orderID=SELF_ORDER_ID;
            }
        } else if (FIXMessageUtil.isCancelRequest(inMessage)) {
            ordStatus=OrdStatus.PENDING_CANCEL;
            orderID=getOptFieldStr(inMessage,OrderID.FIELD);
            if(orderID==null) {
                orderID=SELF_ORDER_ID;
            }
        } else {
            return null;
        }
        // Create execution report.
        FIXMessageFactory messageFactory = getBestMsgFactory(inMessage);
        Message qMsgReply= messageFactory.newExecutionReportEmpty();
        String msgType = MsgType.EXECUTION_REPORT;
        DataDictionary dict=getBestDataDictionary(inMessage);
        String clOrderId = null;
        if(inMessage.isSetField(OrigClOrdID.FIELD)) {
            OrigClOrdID clOrdId = new OrigClOrdID();
            inMessage.getField(clOrdId);
            clOrderId = clOrdId.getValue();
        }
        ExecutionReport latestMessage = ReportCache.INSTANCE.getLatestReportFor(clOrderId);
        if(dict.isMsgField(msgType,OrderID.FIELD)) {
            qMsgReply.setField(new OrderID(orderID));
        }
        if(dict.isMsgField(msgType,ExecID.FIELD)) {
            qMsgReply.setField(new ExecID(getNextExecId().getValue()));
        }
        if(dict.isMsgField(msgType,OrdStatus.FIELD)) {
            qMsgReply.setField(new OrdStatus(ordStatus));
        }
        if(dict.isMsgField(msgType,
                           LastShares.FIELD)) {
            if(latestMessage != null &&
               latestMessage.getLastQuantity() != null) {
                LastShares lastShares = new LastShares(latestMessage.getLastQuantity());
                qMsgReply.setField(lastShares);
            } else {
                qMsgReply.setField(new LastShares(BigDecimal.ZERO));
            }
        }
        if(dict.isMsgField(msgType,
                           LastPx.FIELD)) {
            if(latestMessage != null &&
               latestMessage.getLastPrice() != null) {
                LastPx lastPx = new LastPx(latestMessage.getLastPrice());
                qMsgReply.setField(lastPx);
            } else {
                qMsgReply.setField(new LastPx(BigDecimal.ZERO));
            }
        }
        if(dict.isMsgField(msgType,
                           CumQty.FIELD)) {
            if(latestMessage != null &&
               latestMessage.getCumulativeQuantity() != null) {
                CumQty cumQty = new CumQty(latestMessage.getCumulativeQuantity());
                qMsgReply.setField(cumQty);
            } else {
                qMsgReply.setField(new CumQty(BigDecimal.ZERO));
            }
        }
        if(dict.isMsgField(msgType,
                           AvgPx.FIELD)) {
            if(latestMessage != null &&
               latestMessage.getAveragePrice() != null) {
                AvgPx price = new AvgPx(latestMessage.getAveragePrice());
                qMsgReply.setField(price);
            } else {
                qMsgReply.setField(new AvgPx(BigDecimal.ZERO));
            }
        }
        if(dict.isMsgField(msgType,
                           LeavesQty.FIELD)) {
            if(latestMessage != null &&
               latestMessage.getLeavesQuantity() != null) {
                LeavesQty leavesQty = new LeavesQty(latestMessage.getLeavesQuantity());
                qMsgReply.setField(leavesQty);
            } else {
                qMsgReply.setField(new LeavesQty(BigDecimal.ZERO));
            }
        }
        if(dict.isMsgField(msgType,
                           OrdType.FIELD)) {
            if(latestMessage != null &&
               latestMessage.getOrderType() != null) {
                OrdType ordType = new OrdType(latestMessage.getOrderType().getFIXValue());
                qMsgReply.setField(ordType);
            }
        }
        if(dict.isMsgField(msgType,
                           Price.FIELD)) {
            if(latestMessage != null &&
               latestMessage.getPrice() != null) {
                Price price = new Price(latestMessage.getPrice());
                qMsgReply.setField(price);
            } else {
                qMsgReply.setField(new Price(BigDecimal.ZERO));
            }
        }
        if(dict.isMsgField(msgType,
                           OrderID.FIELD)) {
            if(latestMessage != null &&
               latestMessage.getBrokerOrderID() != null) {
                OrderID brokerOrderID = new OrderID(latestMessage.getBrokerOrderID());
                qMsgReply.setField(brokerOrderID);
            }
        }
        if(dict.isMsgField(msgType,ExecTransType.FIELD)) {
            qMsgReply.setField(new ExecTransType(ExecTransType.NEW));
        }
        // Add all the fields of the incoming message.
        FIXMessageUtil.fillFieldsFromExistingMessage(qMsgReply,
                                                     inMessage,
                                                     getBestDataDictionary(inMessage),
                                                     false);
        messageFactory.getMsgAugmentor().executionReportAugment(qMsgReply);
        // Add required header/trailer fields.
        addRequiredFields(qMsgReply,
                          inMessage.getHeader().getString(TargetCompID.FIELD),
                          inMessage.getHeader().getString(SenderCompID.FIELD));
        return qMsgReply;
    }
    private static String getOptFieldStr(Message msg,
                                         int field)
    {
        try {
            return msg.getString(field);
        } catch(FieldNotFound ex) {
            return null;
        }
    }
    private DataDictionary getBestDataDictionary(Message inMessage)
            throws FieldNotFound, ConfigError
    {
        if(inMessage == null) {
            return getDataDictionary();
        }
        FIXVersion version = FIXVersion.getFIXVersion(inMessage);
        return new DataDictionary(version.getDataDictionaryURL());
    }
    public DataDictionary getDataDictionary()
    {
        return mDataDictionary;
    }
    private ExecID getNextExecId()
    {
        return new ExecID(String.valueOf(System.nanoTime()));
    }
    private static void addRequiredFields(Message msg,
                                          String inSenderCompId,
                                          String inTargetCompId)
            throws FieldNotFound
    {
        msg.getHeader().setField(new MsgSeqNum(seqNum.incrementAndGet()));
        msg.getHeader().setField(new SenderCompID(inSenderCompId));
        msg.getHeader().setField(new TargetCompID(inTargetCompId));
        msg.getHeader().setField(new SendingTime(new Date()));
        // This indirectly adds body length and checksum.
        msg.toString();
    }
    private static final AtomicInteger seqNum = new AtomicInteger(0);
    // INSTANCE METHODS.

    public void start()
        throws ConfigError
    {
        mContext.registerShutdownHook();
        mContext.start();
        mAcceptor.start();
        SLF4JLoggerProxy.debug(this,"Exchange started");
    }

    public synchronized void stop()
    {
        SLF4JLoggerProxy.debug(this,"Exchange stopping");
        mAcceptor.stop();
        mContext.close();
        SLF4JLoggerProxy.debug(this,"Exchange stopped");
    }

    public void add
        (Event event)
    {
        SLF4JLoggerProxy.debug(this,"Adding event {}",event);
        mEvents.add(event);
    }

    public Event getNext()
        throws InterruptedException 
    {
        return mEvents.take();
    }
    
    public void clear()
    {
        mEvents.clear();
    }

    public static void sendMessage
        (Message message,
         SessionID sessionID)
        throws SessionNotFound
    {
        Session.sendToTarget(message,sessionID);
    }
    private FIXMessageFactory getBestMsgFactory(Message inMessage)
            throws FieldNotFound
    {
        if(inMessage == null) {
            return getMsgFactory();
        }
        return FIXVersion.getFIXVersion(inMessage).getMessageFactory();
    }
    public FIXMessageFactory getMsgFactory()
    {
        return FIXVersion.FIX_SYSTEM.getMessageFactory();
    }
    private static final String SELF_ORDER_ID = "NONE";
    private final DataDictionary mDataDictionary;
}
