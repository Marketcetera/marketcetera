package org.marketcetera.client.jms;

import javax.jms.ConnectionFactory;
import javax.jms.ExceptionListener;
import javax.xml.bind.JAXBException;
import org.marketcetera.client.brokers.BrokerStatus;
import org.marketcetera.trade.TradeMessage;
import org.marketcetera.util.misc.ClassVersion;
import org.springframework.jms.listener.SimpleMessageListenerContainer;
import org.springframework.jms.listener.adapter.MessageListenerAdapter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.SimpleMessageConverter;
import quickfix.Message;

/**
 * A factory of incoming Spring-wrapped JMS connections (connection
 * handlers).
 *
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$")
public class IncomingJmsFactory
    extends JmsFactory
{

    // INSTANCE DATA.

    private final ExceptionListener mExceptionListener;


    // CONSTRUCTORS.

    /**
     * Creates a new factory that uses the given standard JMS
     * connection factory to create connections, and directs
     * exceptions to the given listener, if any.
     *
     * @param connectionFactory The factory.
     * @param exceptionListener The listener. It may be null.
     */    

    public IncomingJmsFactory
        (ConnectionFactory connectionFactory,
         ExceptionListener exceptionListener)
    {
        super(connectionFactory);
        mExceptionListener=exceptionListener;
    }


    // INSTANCE METHODS.

    /**
     * Returns the receiver's exception listener.
     *
     * @return The listener. It may be null.
     */

    public ExceptionListener getExceptionListener()
    {
        return mExceptionListener;
    }

    /**
     * Registers the given method of the given message handler for
     * messages that are received by the given incoming destination
     * (and of the given type). Replies to those messages are sent to
     * the given reply destination (and of the given type), if
     * any. The given message converter is used to convert messages
     * between the type used by the handler and the standard JMS
     * message type. Returns the Spring container of the handler
     * (listener) for manual bean lifecycle management.
     *
     * @param handler The message handler.
     * @param handlerMethod The name of the message handler's method.
     * @param inDstName The incoming destination name.
     * @param isInDstTopic True if the incoming destination is a topic.
     * @param replyDstName The reply destination name. It may be null.
     * @param isReplyDstTopic True if the reply destination is a topic.
     * @param messageConverter The converter.
     *
     * @return The container.
     */

    private SimpleMessageListenerContainer registerHandler
        (Object handler,
         String handlerMethod,
         String inDstName,
         boolean isInDstTopic,
         String replyDstName,
         boolean isReplyDstTopic,
         MessageConverter messageConverter)
    {
        MessageListenerAdapter adapter=new MessageListenerAdapter(handler);
        adapter.setDefaultListenerMethod(handlerMethod);
        adapter.setMessageConverter(messageConverter);
        if (replyDstName!=null) {
            if (isReplyDstTopic) {
                adapter.setDefaultResponseTopicName(replyDstName);
            } else {
                adapter.setDefaultResponseQueueName(replyDstName);
            }
        }

        SimpleMessageListenerContainer container=
            new SimpleMessageListenerContainer();
        container.setConnectionFactory(getConnectionFactory());
        container.setDestinationName(inDstName);
        container.setPubSubDomain(isInDstTopic);
        container.setMessageListener(adapter);
        if (getExceptionListener()!=null) {
            container.setExceptionListener(getExceptionListener());
        }
        container.afterPropertiesSet();
        return container;
    }

    /**
     * Registers the given message handler for messages that are
     * received by the given incoming destination (and of the given
     * type). Replies to those messages are sent to the given reply
     * destination (and of the given type). The given message
     * converter is used to convert messages between the type used by
     * the handler and the standard JMS message type. Returns the
     * Spring container of the handler (listener) for manual bean
     * lifecycle management.
     *
     * @param handler The message handler.
     * @param inDstName The incoming destination name.
     * @param isInDstTopic True if the incoming destination is a topic.
     * @param replyDstName The reply destination name.
     * @param isReplyDstTopic True if the reply destination is a topic.
     * @param messageConverter The converter.
     *
     * @return The container.
     */

    public SimpleMessageListenerContainer registerHandler
        (ReplyHandler<?> handler,
         String inDstName,
         boolean isInDstTopic,
         String replyDstName,
         boolean isReplyDstTopic,
         MessageConverter messageConverter)
    {
        return registerHandler
            (handler,"replyToMessage", //$NON-NLS-1$
             inDstName,isInDstTopic,
             replyDstName,isReplyDstTopic,messageConverter);
    }

    /**
     * Registers the given message handler for messages that are
     * received by the given incoming destination (and of the given
     * type). The given message converter is used to convert messages
     * between the type used by the handler and the standard JMS
     * message type. Returns the Spring container of the handler
     * (listener) for manual bean lifecycle management.
     *
     * @param handler The message handler.
     * @param inDstName The incoming destination name.
     * @param isInDstTopic True if the incoming destination is a topic.
     * @param messageConverter The converter.
     *
     * @return The container.
     */

    public SimpleMessageListenerContainer registerHandler
        (ReceiveOnlyHandler<?> handler,
         String inDstName,
         boolean isInDstTopic,
         MessageConverter messageConverter)
    {
        return registerHandler
            (handler,"receiveMessage", //$NON-NLS-1$
             inDstName,isInDstTopic,
             null,false,messageConverter);
    }

    /**
     * Registers the given message handler for messages that are
     * received by the given incoming destination (and of the given
     * type). Replies to those messages are sent to the given reply
     * destination (and of the given type). The handler is expected to
     * operate on objects supported by {@link
     * SimpleMessageConverter}. Returns the Spring container of the
     * handler (listener) for manual bean lifecycle management.
     *
     * @param handler The message handler.
     * @param inDstName The incoming destination name.
     * @param isInDstTopic True if the incoming destination is a topic.
     * @param replyDstName The reply destination name.
     * @param isReplyDstTopic True if the reply destination is a topic.
     *
     * @return The container.
     */

    public SimpleMessageListenerContainer registerHandler
        (ReplyHandler<?> handler,
         String inDstName,
         boolean isInDstTopic,
         String replyDstName,
         boolean isReplyDstTopic)
    {
        return registerHandler
            (handler,inDstName,isInDstTopic,replyDstName,isReplyDstTopic,
             new SimpleMessageConverter());
    }

    /**
     * Registers the given message handler for messages that are
     * received by the given incoming destination (and of the given
     * type). The handler is expected to operate on objects supported
     * by {@link SimpleMessageConverter}. Returns the Spring container
     * of the handler (listener) for manual bean lifecycle management.
     *
     * @param handler The message handler.
     * @param inDstName The incoming destination name.
     * @param isInDstTopic True if the incoming destination is a topic.
     *
     * @return The container.
     */

    public SimpleMessageListenerContainer registerHandler
        (ReceiveOnlyHandler<?> handler,
         String inDstName,
         boolean isInDstTopic)
    {
        return registerHandler
            (handler,inDstName,isInDstTopic,new SimpleMessageConverter());
    }

    /**
     * Registers the given message handler for messages that are
     * received by the given incoming destination (and of the given
     * type). Replies to those messages are sent to the given reply
     * destination (and of the given type). The handler is expected to
     * operate on QuickFIX/J messages. Returns the Spring container of
     * the handler (listener) for manual bean lifecycle management.
     *
     * @param handler The message handler.
     * @param inDstName The incoming destination name.
     * @param isInDstTopic True if the incoming destination is a topic.
     * @param replyDstName The reply destination name.
     * @param isReplyDstTopic True if the reply destination is a topic.
     *
     * @return The container.
     */

    public SimpleMessageListenerContainer registerHandlerQ
        (ReplyHandler<Message> handler,
         String inDstName,
         boolean isInDstTopic,
         String replyDstName,
         boolean isReplyDstTopic)
    {
        return registerHandler
            (handler,inDstName,isInDstTopic,replyDstName,isReplyDstTopic,
             new JMSFIXMessageConverter());
    }

    /**
     * Registers the given message handler for messages that are
     * received by the given incoming destination (and of the given
     * type). The handler is expected to operate on QuickFIX/J
     * messages. Returns the Spring container of the handler
     * (listener) for manual bean lifecycle management.
     *
     * @param handler The message handler.
     * @param inDstName The incoming destination name.
     * @param isInDstTopic True if the incoming destination is a topic.
     *
     * @return The container.
     */

    public SimpleMessageListenerContainer registerHandlerQ
        (ReceiveOnlyHandler<Message> handler,
         String inDstName,
         boolean isInDstTopic)
    {
        return registerHandler
            (handler,inDstName,isInDstTopic,new JMSFIXMessageConverter());
    }

    /**
     * Registers the given message handler for messages that are
     * received by the given incoming destination (and of the given
     * type). Replies to those messages are sent to the given reply
     * destination (and of the given type). The handler is expected to
     * operate on FIX Agnostic trade messages transported using
     * XML. Returns the Spring container of the handler (listener) for
     * manual bean lifecycle management.
     *
     * @param handler The message handler.
     * @param inDstName The incoming destination name.
     * @param isInDstTopic True if the incoming destination is a topic.
     * @param replyDstName The reply destination name.
     * @param isReplyDstTopic True if the reply destination is a topic.
     *
     * @return The container.
     * 
     * @throws JAXBException Thrown if an error occurs in creating the
     * JMS/XML converter.
     */

    public SimpleMessageListenerContainer registerHandlerTMX
        (ReplyHandler<TradeMessage> handler,
         String inDstName,
         boolean isInDstTopic,
         String replyDstName,
         boolean isReplyDstTopic)
        throws JAXBException
    {
        return registerHandler
            (handler,inDstName,isInDstTopic,replyDstName,isReplyDstTopic,
             new JMSXMLMessageConverter());
    }

    /**
     * Registers the given message handler for messages that are
     * received by the given incoming destination (and of the given
     * type). The handler is expected to operate on FIX Agnostic trade
     * messages transported using XML. Returns the Spring container of
     * the handler (listener) for manual bean lifecycle management.
     *
     * @param handler The message handler.
     * @param inDstName The incoming destination name.
     * @param isInDstTopic True if the incoming destination is a topic.
     *
     * @return The container.
     *
     * @throws JAXBException Thrown if an error occurs in creating the
     * JMS/XML converter.
     */

    public SimpleMessageListenerContainer registerHandlerTMX
        (ReceiveOnlyHandler<TradeMessage> handler,
         String inDstName,
         boolean isInDstTopic)
        throws JAXBException
    {
        return registerHandler
            (handler,inDstName,isInDstTopic,new JMSXMLMessageConverter());
    }

    /**
     * Registers the given message handler for messages that are
     * received by the given incoming destination (and of the given
     * type). The handler is expected to operate on FIX Agnostic order
     * message envelopes transported using XML. Returns the Spring
     * container of the handler (listener) for manual bean lifecycle
     * management.
     *
     * @param handler The message handler.
     * @param inDstName The incoming destination name.
     * @param isInDstTopic True if the incoming destination is a topic.
     *
     * @return The container.
     *
     * @throws JAXBException Thrown if an error occurs in creating the
     * JMS/XML converter.
     */

    public SimpleMessageListenerContainer registerHandlerOEX
        (ReceiveOnlyHandler<OrderEnvelope> handler,
         String inDstName,
         boolean isInDstTopic)
        throws JAXBException
    {
        return registerHandler
            (handler,inDstName,isInDstTopic,new JMSXMLMessageConverter());
    }

    /**
     * Registers the given message handler for messages that are
     * received by the given incoming destination (and of the given
     * type). Replies to those messages are sent to the given reply
     * destination (and of the given type). The handler is expected to
     * operate on FIX Agnostic order message envelopes transported
     * using XML. Returns the Spring container of the handler
     * (listener) for manual bean lifecycle management.
     *
     * @param handler The message handler.
     * @param inDstName The incoming destination name.
     * @param isInDstTopic True if the incoming destination is a topic.
     * @param replyDstName The reply destination name.
     * @param isReplyDstTopic True if the reply destination is a topic.
     *
     * @return The container.
     * 
     * @throws JAXBException Thrown if an error occurs in creating the
     * JMS/XML converter.
     */

    public SimpleMessageListenerContainer registerHandlerOEX
        (ReplyHandler<OrderEnvelope> handler,
         String inDstName,
         boolean isInDstTopic,
         String replyDstName,
         boolean isReplyDstTopic)
        throws JAXBException
    {
        return registerHandler
            (handler,inDstName,isInDstTopic,replyDstName,isReplyDstTopic,
             new JMSXMLMessageConverter());
    }

    /**
     * Registers the given message handler for messages that are
     * received by the given incoming destination (and of the given
     * type). Replies to those messages are sent to the given reply
     * destination (and of the given type). The handler is expected to
     * operate on broker status messages transported using
     * XML. Returns the Spring container of the handler (listener) for
     * manual bean lifecycle management.
     *
     * @param handler The message handler.
     * @param inDstName The incoming destination name.
     * @param isInDstTopic True if the incoming destination is a topic.
     * @param replyDstName The reply destination name.
     * @param isReplyDstTopic True if the reply destination is a topic.
     *
     * @return The container.
     * 
     * @throws JAXBException Thrown if an error occurs in creating the
     * JMS/XML converter.
     */

    public SimpleMessageListenerContainer registerHandlerBSX
        (ReplyHandler<BrokerStatus> handler,
         String inDstName,
         boolean isInDstTopic,
         String replyDstName,
         boolean isReplyDstTopic)
        throws JAXBException
    {
        return registerHandler
            (handler,inDstName,isInDstTopic,replyDstName,isReplyDstTopic,
             new JMSXMLMessageConverter());
    }

    /**
     * Registers the given message handler for messages that are
     * received by the given incoming destination (and of the given
     * type). The handler is expected to operate on broker status
     * messages transported using XML. Returns the Spring container of
     * the handler (listener) for manual bean lifecycle management.
     *
     * @param handler The message handler.
     * @param inDstName The incoming destination name.
     * @param isInDstTopic True if the incoming destination is a topic.
     *
     * @return The container.
     *
     * @throws JAXBException Thrown if an error occurs in creating the
     * JMS/XML converter.
     */

    public SimpleMessageListenerContainer registerHandlerBSX
        (ReceiveOnlyHandler<BrokerStatus> handler,
         String inDstName,
         boolean isInDstTopic)
        throws JAXBException
    {
        return registerHandler
            (handler,inDstName,isInDstTopic,new JMSXMLMessageConverter());
    }
}
