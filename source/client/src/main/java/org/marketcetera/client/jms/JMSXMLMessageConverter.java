package org.marketcetera.client.jms;

import java.io.StringWriter;
import java.io.StringReader;

import javax.jms.*;
import javax.xml.bind.*;

import org.marketcetera.client.brokers.BrokerStatus;
import org.marketcetera.trade.ReportBaseImpl;
import org.marketcetera.util.log.I18NBoundMessage1P;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.misc.ClassVersion;

import org.springframework.jms.support.converter.MessageConversionException;
import org.springframework.jms.support.converter.MessageConverter;
import org.apache.commons.lang.ObjectUtils;

/* $License$ */
/**
 * Converts messaging objects to an XML representation that can be
 * sent over JMS.  This class is not meant to be used by clients of
 * this package.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 1.5.0
 */
@ClassVersion("$Id$")
public class JMSXMLMessageConverter implements MessageConverter {
    /**
     * Creates an instance.
     *
     * @throws JAXBException if there were errors initializing the
     * XML marshalling / unmarshalling system.
     */
    public JMSXMLMessageConverter() throws JAXBException {
        mContext = JAXBContext.newInstance
            (OrderEnvelope.class,
             ReportBaseImpl.class,
             BrokerStatus.class);
    }

    /**
     * Converts a JMS Message to a messaging object.
     *
     * @param message the received JMS message. It should be of type
     * {@link javax.jms.ObjectMessage}.
     *
     * @return the messaging object converted from the supplied JMS message.
     *
     * @throws javax.jms.JMSException if there were errors extracting the contents
     * of the JMS message.
     * @throws org.springframework.jms.support.converter.MessageConversionException if there were errors converting
     * the contents of the JMS message to a messaging object.
     */
    @Override
    public Object fromMessage(Message message)
            throws JMSException, MessageConversionException {
        SLF4JLoggerProxy.debug(this, "Converting from JMS {}", message);  //$NON-NLS-1$
        if(message instanceof TextMessage) {
            Object object = null;
            try {
                object = fromXML(((TextMessage) message).getText());
            } catch (JAXBException e) {
                throw new MessageConversionException(new I18NBoundMessage1P(
                        Messages.ERROR_CONVERTING_MESSAGE_TO_OBJECT,
                        ObjectUtils.toString(object)).getText(), e);
            }
            if((object instanceof ReportBaseImpl) ||
               (object instanceof OrderEnvelope) ||
               (object instanceof BrokerStatus)) {
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
     * Converts a messaging object to a JMS Message.
     *
     * @param inObject the message to be converted. It should either be
     * an order or a report.
     * @param session the JMS Session instance.
     *
     * @return the JMS message.
     *
     * @throws javax.jms.JMSException if there were errors serializing the
     * messaging object.
     * @throws org.springframework.jms.support.converter.MessageConversionException if the supplied object was not
     * an acceptable messaging object.
     */
    @Override
    public Message toMessage(Object inObject, Session session)
            throws JMSException, MessageConversionException {
        SLF4JLoggerProxy.debug(this, "Converting to JMS {}", inObject);  //$NON-NLS-1$
        if ((inObject instanceof ReportBaseImpl) ||
            (inObject instanceof OrderEnvelope) ||
            (inObject instanceof BrokerStatus)) {
            try {
                return session.createTextMessage(toXML(inObject));
            } catch (JAXBException e) {
                throw new MessageConversionException(new I18NBoundMessage1P(
                        Messages.ERROR_CONVERTING_OBJECT_TO_MESSAGE,
                        ObjectUtils.toString(inObject)).getText(), e);
            }
        } else {
            throw new MessageConversionException(new I18NBoundMessage1P(
                    Messages.UNEXPECTED_MESSAGE_TO_SEND,
                    ObjectUtils.toString(inObject)).getText());
        }
    }

    /**
     * Marshall the supplied object to XML.
     *
     * @param inObject the object that needs to be marshalled.
     *
     * @return the XML representation.
     *
     * @throws JAXBException if there were was an error marshalling the object
     * to XML.
     */
    String toXML(Object inObject) throws JAXBException {
        Marshaller marshaller = getMarshaller();
        StringWriter writer = new StringWriter();
        marshaller.marshal(inObject, writer);
        //no need to close or flush the writer as they do nothing.
        return writer.toString();
    }

    /**
     * Unmarshall the supplied object from XML to an instance.
     *
     * @param inXML the object in XML.
     *
     * @return the unmarshalled object instance.
     *
     * @throws JAXBException if there were was an error unmarshalling the
     * object from XML.
     */
    Object fromXML(String inXML) throws JAXBException {
        Unmarshaller unMarshaller = getUnMarshaller();
        StringReader reader = new StringReader(inXML);
        return unMarshaller.unmarshal(reader);
    }

    /**
     * Returns the underlying JAXB context for testing.
     *
     * @return the underlying JAXB context.
     */
    JAXBContext getContext() {
        return mContext;
    }

    /**
     * Gets cached copy of the marshaller to use for marshalling objects to XML.
     *
     * @return the marshaller to use for marshalling objects to XML.
     *
     * @throws JAXBException if there were errors getting the marshaller.
     */
    private Marshaller getMarshaller() throws JAXBException {
        Marshaller m = mMarshallers.get();
        if(m == null) {
            m = mContext.createMarshaller();
            mMarshallers.set(m);
        }
        return m;
    }

    /**
     * Gets the cached copy of the unmarshaller to use for unmarshalling objects
     * from XML.
     *
     * @return the unmarshaller to use for unmarshalling objects from XML.
     *
     * @throws JAXBException if there were errors getting the unmarshaller.
     */
    private Unmarshaller getUnMarshaller() throws JAXBException {
        Unmarshaller u = mUnmarshallers.get();
        if(u == null) {
            u = mContext.createUnmarshaller();
            mUnmarshallers.set(u);
        }
        return u;
    }

    private final ThreadLocal<Marshaller> mMarshallers =
            new ThreadLocal<Marshaller>();
    private final ThreadLocal<Unmarshaller> mUnmarshallers =
            new ThreadLocal<Unmarshaller>();
    private final JAXBContext mContext;
}
