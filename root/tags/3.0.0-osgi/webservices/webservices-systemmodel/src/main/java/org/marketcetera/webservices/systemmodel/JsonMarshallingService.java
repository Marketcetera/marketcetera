package org.marketcetera.webservices.systemmodel;

/* $License$ */

/**
 * Provides a service for marshalling to and unmarshalling from JSON.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface JsonMarshallingService
{
    /**
     * Marshals the given object to a JSON string.
     *
     * @param inValue an <code>Object</code> value
     * @return a <code>String</code> value
     */
    public String marshal(Object inValue);
    /**
     * Unmarshals the given <code>String</code> to an object of the given type.
     *
     * @param inStringValue a <code>String</code> value
     * @param inType a <code>Class&lt;Clazz&gt;</code> value
     * @return a <code>Clazz</code> value
     */
    public <Clazz> Clazz unmarshal(String inStringValue,
                                   Class<Clazz> inType);
}
