package org.marketcetera.client.jms;

import javax.jms.ConnectionFactory;
import javax.xml.bind.JAXBException;
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

@ClassVersion("$Id$")
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
     * Returns a new Spring JMS connection template for the given
     * destination (and of the given type). The given message
     * converter is used to convert messages from the type used by the
     * producer to the standard JMS message type.
     *
     * @param dstName The destination name.
     * @param isDstTopic True if the destination is a topic.
     * @param messageConverter The converter.
     *
     * @return The connection template.
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
     * Returns a new Spring JMS connection template for the given
     * destination (and of the given type), and for a producer which
     * emits objects supported by {@link SimpleMessageConverter}.
     *
     * @param dstName The destination name.
     * @param isDstTopic True if the destination is a topic.
     *
     * @return The connection template.
     */

    public JmsTemplate createJmsTemplate
        (String dstName,
         boolean isDstTopic)
    {
        return createJmsTemplate
            (dstName,isDstTopic,new SimpleMessageConverter());
    }

    /**
     * Returns a new Spring JMS connection template for the given
     * destination (and of the given type), and for a
     * producer which emits QuickFIX/J messages.
     *
     * @param dstName The destination name.
     * @param isDstTopic True if the destination is a topic.
     *
     * @return The connection template.
     */

    public JmsTemplate createJmsTemplateQ
        (String dstName,
         boolean isDstTopic)
    {
        return createJmsTemplate
            (dstName,isDstTopic,new JMSFIXMessageConverter());
    }

    /**
     * Returns a new Spring JMS connection template for the given
     * destination (and of the given type), and for a producer which
     * emits FIX Agnostic trade messages, order message envelopes, or
     * broker status messages, transported using XML.
     *
     * @param dstName The destination name.
     * @param isDstTopic True if the destination is a topic.
     *
     * @return The connection template.
     *
     * @throws JAXBException Thrown if an error occurs in creating the
     * JMS/XML converter.
     */

    public JmsTemplate createJmsTemplateX
        (String dstName,
         boolean isDstTopic)
        throws JAXBException
    {
        return createJmsTemplate
            (dstName,isDstTopic,new JMSXMLMessageConverter());
    }
}
