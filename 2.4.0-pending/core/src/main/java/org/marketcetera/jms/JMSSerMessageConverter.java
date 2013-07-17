package org.marketcetera.jms;

import java.io.Serializable;

import javax.jms.ObjectMessage;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.marketcetera.util.log.I18NBoundMessage1P;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.misc.ClassVersion;

import org.springframework.jms.support.converter.MessageConversionException;
import org.springframework.jms.support.converter.MessageConverter;
import org.apache.commons.lang.ObjectUtils;

/* $License$ */
/**
 * Serializes objects using Java serialization to be able to send them over JMS.
 * This class is not meant to be used by clients of this package.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 1.5.0
 */
@ClassVersion("$Id$")
public class JMSSerMessageConverter implements MessageConverter {

    /**
     * Converts a JMS Message to an object.
     *
     * @param message the received JMS message. It should be of type
     * {@link ObjectMessage}.
     *
     * @return the object converted from the supplied JMS message.
     *
     * @throws JMSException if there were errors extracting the contents
     * of the JMS message.
     * @throws MessageConversionException if there were errors converting
     * the contents of the JMS message to an object.
     */
    @Override
    public Object fromMessage(Message message)
            throws JMSException, MessageConversionException {
        SLF4JLoggerProxy.debug(this, "Converting from JMS {}", message);  //$NON-NLS-1$
        if(message instanceof ObjectMessage) {
            return ((ObjectMessage) message).getObject();
        } else {
            throw new MessageConversionException(new I18NBoundMessage1P(
                    Messages.UNEXPECTED_MESSAGE_RECEIVED,
                    ObjectUtils.toString(message)).getText());
        }
	}

    /**
     * Converts an object to a JMS Message by serializing it.
     *
     * @param inObject the object to be converted. The object should be
     * serializable.
     * @param session the JMS Session instance.
     *
     * @return the JMS message.
     *
     * @throws JMSException if there were errors serializing the object.
     * @throws MessageConversionException if the supplied object was not
     * serializable or if there were errors serializing the object.
     */
    @Override
    public Message toMessage(Object inObject, Session session)
            throws JMSException, MessageConversionException {
        SLF4JLoggerProxy.debug(this, "Converting to JMS {}", inObject);  //$NON-NLS-1$
        if (inObject instanceof Serializable) {
            try {
                return session.createObjectMessage((Serializable) inObject);
            } catch (RuntimeException e) {
                throw new MessageConversionException(new I18NBoundMessage1P(
                        Messages.UNEXPECTED_MESSAGE_TO_SEND,
                        ObjectUtils.toString(inObject)).getText(), e);
            }
        } else {
            throw new MessageConversionException(new I18NBoundMessage1P(
                    Messages.UNEXPECTED_MESSAGE_TO_SEND,
                    ObjectUtils.toString(inObject)).getText());
        }
    }
}