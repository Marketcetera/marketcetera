package org.marketcetera.core;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;

import org.marketcetera.util.log.SLF4JLoggerProxy;

import com.google.common.collect.Lists;

import jakarta.annotation.PostConstruct;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;

/* $License$ */

/**
 * Provides XML marshalling and unmarshalling services.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class XmlService
{
    /**
     * Validate and start the object.
     */
    @PostConstruct
    public void start()
    {
        SLF4JLoggerProxy.info(this,
                              "Starting {} with context classes: {}",
                              PlatformServices.getServiceName(getClass()),
                              contextPath);
        try {
            context = JAXBContext.newInstance(contextPath.toArray(new Class<?>[contextPath.size()]));
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * Marshals the given object to an XML stream.
     *
     * @param inObject an <code>Object</code> value
     * @return a <code>String</code> value
     * @throws JAXBException if an error occurs marshalling the data
     */
    public String marshall(Object inObject)
            throws JAXBException
    {
        StringWriter output = new StringWriter();
        context.createMarshaller().marshal(inObject,
                                           output);
        return output.toString();
    }
    /**
     * Unmarshals an object from the given XML stream.
     *
     * @param inData a <code>String</code> value
     * @return a <code>Clazz</code> value
     * @throws JAXBException if an error occurs unmarshalling the data
     */
    @SuppressWarnings("unchecked")
    public <Clazz> Clazz unmarshall(String inData)
            throws JAXBException
    {
        return (Clazz)context.createUnmarshaller().unmarshal(new StringReader(inData));
    }
    /**
     * Get the contextPath value.
     *
     * @return a <code>List&lt;Class&lt;?&gt;&gt;</code> value
     */
    public List<Class<?>> getContextPath()
    {
        return contextPath;
    }
    /**
     * Sets the contextPath value.
     *
     * @param inContextPath a <code>List&lt;Class&lt;?&gt;&gt;</code> value
     */
    public void setContextPath(List<Class<?>> inContextPath)
    {
        contextPath = inContextPath;
    }
    /**
     * holds context classes to use for marshalling and unmarshalling
     */
    private List<Class<?>> contextPath = Lists.newArrayList();
    /**
     * context used to serialize and unserialize messages as necessary
     */
    private JAXBContext context;
}
