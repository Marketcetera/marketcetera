package org.marketcetera.ors.jms;

import javax.jms.ConnectionFactory;
import javax.xml.bind.JAXBException;

import org.marketcetera.client.JMSMessageConverter;
import org.marketcetera.client.JMSXMLMessageConverter;
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

@ClassVersion("$Id$") //$NON-NLS-1$
public class IncomingJmsFactory
    extends JmsFactory
{

    // CONSTRUCTORS.

    /**
     * Creates a new factory that uses the given standard JMS
     * connection factory to create connections.
     *
     * @param connectionFactory The factory.
     */    

    public IncomingJmsFactory
        (ConnectionFactory connectionFactory)
    {
        super(connectionFactory);
    }


    // INSTANCE METHODS.

    /**
     * Registers the given message handler for messages that are
     * received by the given incoming destination (and of the given
     * type). Replies to those messages are sent to the given reply
     * destination (and of the given type). The given message
     * converter is used to convert messages between the type used by
     * the handler and the standard JMS message type.
     *
     * @param handler The message handler.
     * @param inDstName The incoming destination name.
     * @param isInDstTopic True if the incoming destination is a topic.
     * @param replyDstName The reply destination name.
     * @param isReplyDstTopic True if the reply destination is a topic.
     * @param messageConverter The converter.
     */

    public void registerHandler
        (ReplyHandler<?> handler,
         String inDstName,
         boolean isInDstTopic,
         String replyDstName,
         boolean isReplyDstTopic,
         MessageConverter messageConverter)
    {
        MessageListenerAdapter adapter=new MessageListenerAdapter(handler);
        adapter.setDefaultListenerMethod("replyToMessage"); //$NON-NLS-1$
        adapter.setMessageConverter(messageConverter);
        if (isReplyDstTopic) {
            adapter.setDefaultResponseTopicName(replyDstName);
        } else {
            adapter.setDefaultResponseQueueName(replyDstName);
        }

        SimpleMessageListenerContainer container=
            new SimpleMessageListenerContainer();
        container.setConnectionFactory(getConnectionFactory());
        container.setDestinationName(inDstName);
        container.setPubSubDomain(isInDstTopic);
        container.setMessageListener(adapter);
        container.afterPropertiesSet();
    }

    /**
     * Registers the given message handler for messages that are
     * received by the given incoming destination (and of the given
     * type). The given message converter is used to convert messages
     * between the type used by the handler and the standard JMS
     * message type.
     *
     * @param handler The message handler.
     * @param inDstName The incoming destination name.
     * @param isInDstTopic True if the incoming destination is a topic.
     * @param messageConverter The converter.
     */

    public void registerHandler
        (ReceiveOnlyHandler<?> handler,
         String inDstName,
         boolean isInDstTopic,
         MessageConverter messageConverter)
    {
        MessageListenerAdapter adapter=new MessageListenerAdapter(handler);
        adapter.setDefaultListenerMethod("receiveMessage"); //$NON-NLS-1$
        adapter.setMessageConverter(messageConverter);

        SimpleMessageListenerContainer container=
            new SimpleMessageListenerContainer();
        container.setConnectionFactory(getConnectionFactory());
        container.setDestinationName(inDstName);
        container.setPubSubDomain(isInDstTopic);
        container.setMessageListener(adapter);
        container.afterPropertiesSet();
    }

    /**
     * Registers the given message handler for messages that are
     * received by the given incoming destination (and of the given
     * type). Replies to those messages are sent to the given reply
     * destination (and of the given type). The handler is expected to
     * operate on objects supported by {@link SimpleMessageConverter}.
     *
     * @param handler The message handler.
     * @param inDstName The incoming destination name.
     * @param isInDstTopic True if the incoming destination is a topic.
     * @param replyDstName The reply destination name.
     * @param isReplyDstTopic True if the reply destination is a topic.
     */

    public void registerHandler
        (ReplyHandler<?> handler,
         String inDstName,
         boolean isInDstTopic,
         String replyDstName,
         boolean isReplyDstTopic)
    {
        registerHandler
            (handler,inDstName,isInDstTopic,replyDstName,isReplyDstTopic,
             new SimpleMessageConverter());
    }

    /**
     * Registers the given message handler for messages that are
     * received by the given incoming destination (and of the given
     * type). The handler is expected to operate on objects supported
     * by {@link SimpleMessageConverter}.
     *
     * @param handler The message handler.
     * @param inDstName The incoming destination name.
     * @param isInDstTopic True if the incoming destination is a topic.
     */

    public void registerHandler
        (ReceiveOnlyHandler<?> handler,
         String inDstName,
         boolean isInDstTopic)
    {
        registerHandler
            (handler,inDstName,isInDstTopic,new SimpleMessageConverter());
    }

    /**
     * Registers the given message handler for messages that are
     * received by the given incoming destination (and of the given
     * type). Replies to those messages are sent to the given reply
     * destination (and of the given type). The handler is expected to
     * operate on QuickFIX/J messages.
     *
     * @param handler The message handler.
     * @param inDstName The incoming destination name.
     * @param isInDstTopic True if the incoming destination is a topic.
     * @param replyDstName The reply destination name.
     * @param isReplyDstTopic True if the reply destination is a topic.
     */

    public void registerHandlerQ
        (ReplyHandler<Message> handler,
         String inDstName,
         boolean isInDstTopic,
         String replyDstName,
         boolean isReplyDstTopic)
    {
        registerHandler
            (handler,inDstName,isInDstTopic,replyDstName,isReplyDstTopic,
             new JMSFIXMessageConverter());
    }

    /**
     * Registers the given message handler for messages that are
     * received by the given incoming destination (and of the given
     * type). The handler is expected to operate on QuickFIX/J
     * messages.
     *
     * @param handler The message handler.
     * @param inDstName The incoming destination name.
     * @param isInDstTopic True if the incoming destination is a topic.
     */

    public void registerHandlerQ
        (ReceiveOnlyHandler<Message> handler,
         String inDstName,
         boolean isInDstTopic)
    {
        registerHandler
            (handler,inDstName,isInDstTopic,new JMSFIXMessageConverter());
    }

    /**
     * Registers the given message handler for messages that are
     * received by the given incoming destination (and of the given
     * type). Replies to those messages are sent to the given reply
     * destination (and of the given type). The handler is expected to
     * operate on FIX Agnostic trade messages.
     *
     * @param handler The message handler.
     * @param inDstName The incoming destination name.
     * @param isInDstTopic True if the incoming destination is a topic.
     * @param replyDstName The reply destination name.
     * @param isReplyDstTopic True if the reply destination is a topic.
     */

    public void registerHandlerTM
        (ReplyHandler<TradeMessage> handler,
         String inDstName,
         boolean isInDstTopic,
         String replyDstName,
         boolean isReplyDstTopic)
    {
        registerHandler
            (handler,inDstName,isInDstTopic,replyDstName,isReplyDstTopic,
             new JMSMessageConverter());
    }

    /**
     * Registers the given message handler for messages that are
     * received by the given incoming destination (and of the given
     * type). The handler is expected to operate on FIX Agnostic trade
     * messages.
     *
     * @param handler The message handler.
     * @param inDstName The incoming destination name.
     * @param isInDstTopic True if the incoming destination is a topic.
     */

    public void registerHandlerTM
        (ReceiveOnlyHandler<TradeMessage> handler,
         String inDstName,
         boolean isInDstTopic)
    {
        registerHandler
            (handler,inDstName,isInDstTopic,new JMSMessageConverter());
    }

    /**
     * Registers the given message handler for messages that are
     * received by the given incoming destination (and of the given
     * type). Replies to those messages are sent to the given reply
     * destination (and of the given type). The handler is expected to
     * operate on FIX Agnostic trade messages in XML.
     *
     * @param handler The message handler.
     * @param inDstName The incoming destination name.
     * @param isInDstTopic True if the incoming destination is a topic.
     * @param replyDstName The reply destination name.
     * @param isReplyDstTopic True if the reply destination is a topic.
     * 
     * @throws javax.xml.bind.JAXBException if there were errors
     */

    public void registerHandlerXM
        (ReplyHandler<TradeMessage> handler,
         String inDstName,
         boolean isInDstTopic,
         String replyDstName,
         boolean isReplyDstTopic) throws JAXBException
    {
        registerHandler
            (handler,inDstName,isInDstTopic,replyDstName,isReplyDstTopic,
             new JMSXMLMessageConverter());
    }

    /**
     * Registers the given message handler for messages that are
     * received by the given incoming destination (and of the given
     * type). The handler is expected to operate on FIX Agnostic trade
     * messages in XML.
     *
     * @param handler The message handler.
     * @param inDstName The incoming destination name.
     * @param isInDstTopic True if the incoming destination is a topic.
     *
     * @throws javax.xml.bind.JAXBException if there were errors
     */

    public void registerHandlerXM
        (ReceiveOnlyHandler<TradeMessage> handler,
         String inDstName,
         boolean isInDstTopic) throws JAXBException
    {
        registerHandler
            (handler,inDstName,isInDstTopic,new JMSXMLMessageConverter());
    }
}
