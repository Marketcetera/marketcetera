package org.marketcetera.client.jms;

import java.io.Serializable;

import javax.jms.ObjectMessage;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.marketcetera.util.log.I18NBoundMessage1P;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.trade.*;

import org.springframework.jms.support.converter.MessageConversionException;
import org.springframework.jms.support.converter.MessageConverter;
import org.apache.commons.lang.ObjectUtils;

/* $License$ */
/**
 * Converts trading messages to be able to send them over JMS.
 * This class is not meant to be used by clients of this package.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$")
public class JMSMessageConverter implements MessageConverter {

    /**
     * Converts a JMS Message to a trade message.
     *
     * @param message the received JMS message. It should be of type
     * {@link ObjectMessage}.
     *
     * @return the trade message converted from the supplied JMS message.
     *
     * @throws JMSException if there were errors extracting the contents
     * of the JMS message.
     * @throws MessageConversionException if there were errors converting
     * the contents of the JMS message to a trade message.
     */
    @Override
    public Object fromMessage(Message message)
            throws JMSException, MessageConversionException {
        SLF4JLoggerProxy.debug(this, "Converting from JMS {}", message);  //$NON-NLS-1$
        if(message instanceof ObjectMessage) {
            Serializable object = ((ObjectMessage) message).getObject();
            if(object instanceof TradeMessage) {
                return object;
            } else {
                throw new MessageConversionException(new I18NBoundMessage1P(
                        Messages.UNEXPECTED_MESSAGE_RECEIVED,
                        ObjectUtils.toString(object)).getText());
            }
        } else {
            throw new MessageConversionException(new I18NBoundMessage1P(
                    Messages.UNEXPECTED_MESSAGE_RECEIVED,
                    ObjectUtils.toString(message)).getText());
        }
	}

    /**
     * Converts a trade message to a JMS Message.
     *
     * @param inObject the message to be converted. It should either be
     * an order or a report.
     * @param session the JMS Session instance.
     *
     * @return the JMS message.
     *
     * @throws JMSException if there were errors serializing the
     * trade message.
     * @throws MessageConversionException if the supplied object was not
     * an acceptable trade message.
     */
    @Override
    public Message toMessage(Object inObject, Session session)
            throws JMSException, MessageConversionException {
        SLF4JLoggerProxy.debug(this, "Converting to JMS {}", inObject);  //$NON-NLS-1$
        if (inObject instanceof TradeMessage) {
            return session.createObjectMessage((Serializable) inObject);
        } else {
            throw new MessageConversionException(new I18NBoundMessage1P(
                    Messages.UNEXPECTED_MESSAGE_TO_SEND,
                    ObjectUtils.toString(inObject)).getText());
        }
    }
}