package org.marketcetera.ors.jms;

import javax.jms.ConnectionFactory;
import org.marketcetera.client.JMSMessageConverter;
import org.marketcetera.ors.jms.JMSFIXMessageConverter;
import org.marketcetera.util.misc.ClassVersion;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.SimpleMessageConverter;

/**
 * A factory of outgoing Spring-wrapped JMS connections (Spring JMS
 * connection templates).
 *
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$") //$NON-NLS-1$
public class OutgoingJmsFactory
    extends JmsFactory
{

    // CONSTRUCTORS.

    /**
     * Creates a new factory that uses the given standard JMS
     * connection factory to create connections.
     *
     * @param connectionFactory The factory.
     */    

    public OutgoingJmsFactory
        (ConnectionFactory connectionFactory)
    {
        super(connectionFactory);
    }


    // INSTANCE METHODS.

    /**
     * Creates a new Spring JMS connection template for the given
     * destination (and of the given type). The given message
     * converter is used to convert messages from the type used by the
     * producer to the standard JMS message type.
     *
     * @param dstName The destination name.
     * @param isDstTopic True if the destination is a topic.
     * @param messageConverter The converter.
     */

    public JmsTemplate createJmsTemplate
        (String dstName,
         boolean isDstTopic,
         MessageConverter messageConverter)
    {
        JmsTemplate template=new JmsTemplate(getConnectionFactory());
        template.setDefaultDestinationName(dstName);
        template.setPubSubDomain(isDstTopic);
        template.setMessageConverter(messageConverter);
        template.afterPropertiesSet();
        return template;
    }

    /**
     * Creates a new Spring JMS connection template for the given
     * destination (and of the given type), and for a producer which
     * emits objects supported by {@link SimpleMessageConverter}.
     *
     * @param dstName The destination name.
     * @param isDstTopic True if the destination is a topic.
     */

    public JmsTemplate createJmsTemplate
        (String dstName,
         boolean isDstTopic)
    {
        return createJmsTemplate
            (dstName,isDstTopic,new SimpleMessageConverter());
    }

    /**
     * Creates a new Spring JMS connection template for the given
     * destination (and of the given type), and for a
     * producer which emits QuickFIX/J messages.
     *
     * @param dstName The destination name.
     * @param isDstTopic True if the destination is a topic.
     */

    public JmsTemplate createJmsTemplateQ
        (String dstName,
         boolean isDstTopic)
    {
        return createJmsTemplate
            (dstName,isDstTopic,new JMSFIXMessageConverter());
    }

    /**
     * Creates a new Spring JMS connection template for the given
     * destination (and of the given type), and for a producer which
     * emits FIX Agnostic trade messages.
     *
     * @param dstName The destination name.
     * @param isDstTopic True if the destination is a topic.
     */

    public JmsTemplate createJmsTemplateTM
        (String dstName,
         boolean isDstTopic)
    {
        return createJmsTemplate
            (dstName,isDstTopic,new JMSMessageConverter());
    }
}
