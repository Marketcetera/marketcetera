package org.marketcetera.client.jms;

import java.io.StringReader;
import java.io.StringWriter;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.lang.ObjectUtils;
import org.marketcetera.client.brokers.BrokerStatus;
import org.marketcetera.event.impl.ConvertibleBondAskEventImpl;
import org.marketcetera.event.impl.ConvertibleBondBidEventImpl;
import org.marketcetera.event.impl.ConvertibleBondImbalanceEvent;
import org.marketcetera.event.impl.ConvertibleBondMarketstatEventImpl;
import org.marketcetera.event.impl.ConvertibleBondTradeEventImpl;
import org.marketcetera.event.impl.CurrencyAskEventImpl;
import org.marketcetera.event.impl.CurrencyBidEventImpl;
import org.marketcetera.event.impl.CurrencyImbalanceEvent;
import org.marketcetera.event.impl.CurrencyMarketstatEventImpl;
import org.marketcetera.event.impl.CurrencyTradeEventImpl;
import org.marketcetera.event.impl.DividendEventImpl;
import org.marketcetera.event.impl.EquityAskEventImpl;
import org.marketcetera.event.impl.EquityBidEventImpl;
import org.marketcetera.event.impl.EquityImbalanceEvent;
import org.marketcetera.event.impl.EquityMarketstatEventImpl;
import org.marketcetera.event.impl.EquityTradeEventImpl;
import org.marketcetera.event.impl.FutureAskEventImpl;
import org.marketcetera.event.impl.FutureBidEventImpl;
import org.marketcetera.event.impl.FutureImbalanceEvent;
import org.marketcetera.event.impl.FutureMarketstatEventImpl;
import org.marketcetera.event.impl.FutureTradeEventImpl;
import org.marketcetera.event.impl.OptionAskEventImpl;
import org.marketcetera.event.impl.OptionBidEventImpl;
import org.marketcetera.event.impl.OptionImbalanceEvent;
import org.marketcetera.event.impl.OptionMarketstatEventImpl;
import org.marketcetera.event.impl.OptionTradeEventImpl;
import org.marketcetera.marketdata.MarketDataRequest;
import org.marketcetera.module.DataFlowID;
import org.marketcetera.module.RequestID;
import org.marketcetera.trade.FIXResponseImpl;
import org.marketcetera.trade.ReportBaseImpl;
import org.marketcetera.util.log.I18NBoundMessage1P;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.misc.ClassVersion;
import org.springframework.jms.support.converter.MessageConversionException;
import org.springframework.jms.support.converter.MessageConverter;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

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
public class JMSXMLMessageConverter
        implements MessageConverter
{
    /**
     * Creates an instance.
     *
     * @throws JAXBException if there were errors initializing the
     * XML marshalling / unmarshalling system.
     */
    public JMSXMLMessageConverter()
            throws JAXBException
    {
        mContext = JAXBContext.newInstance(contextTypes);
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
            if(isSupported(object)) {
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
        if(isSupported(inObject)) {
            try {
                TextMessage message = session.createTextMessage(toXML(inObject));
                //Set the type property for interoperability with .NET client.
                message.setStringProperty(JMS_TYPE_PROPERTY,
                        inObject.getClass().getSimpleName());
                return message;
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
    /**
     * Indicate if the given object is of a supported type.
     *
     * @param inObject an <code>Object</code> value
     * @return a <code>boolean</cod> value
     */
    private boolean isSupported(Object inObject)
    {
        return supportedContextType.getUnchecked(inObject.getClass());
    }
    /**
     * caches supported context type lookup values
     */
    private final LoadingCache<Class<?>,Boolean> supportedContextType = CacheBuilder.newBuilder().build(new CacheLoader<Class<?>,Boolean>(){
        @Override
        public Boolean load(Class<?> inKey)
                throws Exception
        {
            for(Class<?> theClass : contextTypes) {
                if(inKey.equals(theClass) || theClass.isAssignableFrom(inKey)) {
                    return true;
                }
            }
            return false;
        }});
    private final ThreadLocal<Marshaller> mMarshallers =
            new ThreadLocal<Marshaller>();
    private final ThreadLocal<Unmarshaller> mUnmarshallers =
            new ThreadLocal<Unmarshaller>();
    private final JAXBContext mContext;
    private static final String JMS_TYPE_PROPERTY = "metc_type";  //$NON-NLS-1$
    /**
     * holds supported context types
     */
    private static final Class<?>[] contextTypes = new Class<?>[] { DataEnvelope.class,ReportBaseImpl.class,FIXResponseImpl.class,BrokerStatus.class,
                ConvertibleBondAskEventImpl.class,ConvertibleBondBidEventImpl.class,ConvertibleBondMarketstatEventImpl.class,ConvertibleBondTradeEventImpl.class,ConvertibleBondImbalanceEvent.class,
                CurrencyAskEventImpl.class,CurrencyBidEventImpl.class,CurrencyMarketstatEventImpl.class,CurrencyTradeEventImpl.class,CurrencyImbalanceEvent.class,
                EquityAskEventImpl.class,EquityBidEventImpl.class,EquityMarketstatEventImpl.class,EquityTradeEventImpl.class,EquityImbalanceEvent.class,
                FutureAskEventImpl.class,FutureBidEventImpl.class,FutureMarketstatEventImpl.class,FutureTradeEventImpl.class,FutureImbalanceEvent.class,
                OptionAskEventImpl.class,OptionBidEventImpl.class,OptionMarketstatEventImpl.class,OptionTradeEventImpl.class,OptionImbalanceEvent.class,
                DividendEventImpl.class,RequestID.class,DataFlowID.class,MarketDataRequest.class };
}
