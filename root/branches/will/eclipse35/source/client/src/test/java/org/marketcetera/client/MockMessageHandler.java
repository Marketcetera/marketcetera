package org.marketcetera.client;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.trade.*;
import org.marketcetera.quickfix.FIXVersion;
import org.marketcetera.event.HasFIXMessage;
import org.marketcetera.client.jms.OrderEnvelope;
import org.marketcetera.client.jms.ReceiveOnlyHandler;

import org.springframework.jms.core.JmsOperations;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicLong;
import java.math.BigDecimal;

import quickfix.field.OrdStatus;
import quickfix.FieldNotFound;

/* $License$ */
/**
 * Message handler on the {@link MockServer} used to collect messages
 * received by it and provide it with messages that it should send to the
 * client.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public class MockMessageHandler
    implements ReceiveOnlyHandler<OrderEnvelope>
{
    @Override
    public void receiveMessage(OrderEnvelope inObject) {
        Object send;
        if(inObject.getOrder() instanceof HasFIXMessage) {
            try {
                TypesTestBase.logFields(((HasFIXMessage)inObject.getOrder()).getMessage());
            } catch (FieldNotFound ignore) {
            }
        } else {
            SLF4JLoggerProxy.debug(this, "Received {}", inObject);
        }
        mReceived.add(inObject);
        if (!mToSend.isEmpty()) {
            send = mToSend.remove();
        } else {
            send = createExecutionReport();
        }
        SLF4JLoggerProxy.debug(this, "Sending {}", send);
        getReplySender().convertAndSend(send);
    }
    public synchronized void addToSend(Object inObject) {
        SLF4JLoggerProxy.debug(this, "ADDED {}", inObject);
        //Use add() instead of put() as we need a non-blocking method.
        mToSend.add(inObject);
    }
    public synchronized void addToSendStatus(Object inObject) {
        SLF4JLoggerProxy.debug(this, "ADDED STATUS {}", inObject);
        //Use add() instead of put() as we need a non-blocking method.
        mToSendStatus.add(inObject);
    }
    public synchronized Object removeReceived() throws InterruptedException {
        //block until a report is available.
        return mReceived.take();
    }
    public synchronized int numReceived() {
        return mReceived.size();
    }
    public synchronized void clear() {
        mReceived.clear();
        mToSend.clear();
        mToSendStatus.clear();
    }
    static ExecutionReport createExecutionReport() {
        try {
            long id = mLong.getAndIncrement();
            return Factory.getInstance().createExecutionReport(FIXVersion.FIX42.
                    getMessageFactory().newExecutionReport("ord" +
                    id, "clord" + id, "exec" + id, OrdStatus.NEW,
                    quickfix.field.Side.BUY,
                    new BigDecimal("4343.49"), new BigDecimal("498.34"),
                    new BigDecimal("783343.49"), new BigDecimal("598.34"),
                    new BigDecimal("234343.49"), new BigDecimal("798.34"),
                    new MSymbol("IBM", SecurityType.CommonStock), "my acc"),
                    new BrokerID("bro"), Originator.Broker, null, null);
        } catch (MessageCreationException e) {
            throw new RuntimeException(e);
        } catch (FieldNotFound e) {
            throw new RuntimeException(e);
        }
    }
    JmsOperations getReplySender() {
        return mReplySender;
    }
    void setReplySender(JmsOperations replySender) {
        mReplySender=replySender;
    }
    private final BlockingQueue<Object> mReceived = new LinkedBlockingQueue<Object>();
    private final Queue<Object> mToSend = new LinkedList<Object>();
    private final Queue<Object> mToSendStatus = new LinkedList<Object>();
    private JmsOperations mReplySender;
    private static final AtomicLong mLong = new AtomicLong();
}
