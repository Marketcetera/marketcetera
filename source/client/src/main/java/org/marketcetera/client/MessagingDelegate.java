package org.marketcetera.client;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.trade.*;
import org.marketcetera.client.dest.DestinationStatus;
import org.springframework.jms.core.JmsTemplate;

import javax.jms.ExceptionListener;
import javax.jms.JMSException;

/* $License$ */
/**
 * Provides means to communicate with JMS. Client delegates all JMS
 * communications to this abstraction.
 * This class is not meant to be used by the clients of this package.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public class MessagingDelegate implements ExceptionListener {
    /**
     * Receives an execution report from a topic.
     *
     * @param inReport the received execution report.
     */
    public void handleMessage(ExecutionReport inReport) {
        getClientImpl().notifyExecutionReport(inReport);
    }

    /**
     * Receives an order cancel reject message from a topic.
     *
     * @param inReport the received order cancel reject.
     */
    public void handleMessage(OrderCancelReject inReport) {
        getClientImpl().notifyCancelReject(inReport);
    }

    /**
     * Receives a destination status change from a topic.
     *
     * @param status The received status change.
     */

    public void handleMessage
        (DestinationStatus status)
    {
        getClientImpl().notifyDestinationStatus(status);
    }

    /**
     * Sets the instance that should be used for sending JMS messages.
     *
     * @param inSender the instance used for sending JMS messages.
     */
    public void setSender(JmsTemplate inSender) {
        mSender = inSender;
    }

    /**
     * Sends the specified order over JMS.
     *
     * @param inOrder the order that needs to be sent.
     */
    void convertAndSend(Order inOrder) {
        getSender().convertAndSend(inOrder);
    }

    /**
     * Sets the client implementation that this instance collaborates with.
     *
     * @param inClientImpl the client implementation.
     */
    void setClientImpl(ClientImpl inClientImpl) {
        mClientImpl = inClientImpl;
    }

    /**
     * The client implementation that this instance collaborates with.
     *
     * @return the client implementation.
     */
    private ClientImpl getClientImpl() {
        return mClientImpl;
    }

    /**
     * The instance used for sending messages over JMS.
     *
     * @return the instance used for sending messages over JMS.
     */
    private JmsTemplate getSender() {
        return mSender;
    }
    @Override
    public void onException(JMSException e) {
        getClientImpl().exceptionThrown(new ConnectionException(e,
                Messages.ERROR_RECEIVING_JMS_MESSAGE));
    }

    private ClientImpl mClientImpl;
    private JmsTemplate mSender;
}