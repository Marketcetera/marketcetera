package org.marketcetera.core.ws.wrappers;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * A wrapper for any object, allowing it to become a root element in
 * JAXB marshalling.
 *
 * @since 2.0.0
 * @version $Id: RootElementWrapper.java 82324 2012-04-09 20:56:08Z colin $
 */

/* $License$ */

@XmlRootElement
public class RootElementWrapper<T>
    extends BaseWrapper<T>
{

    // CONSTRUCTORS.

    /**
     * Creates a new wrapper that wraps the given object.
     *
     * @param object The object, which may be null.
     */

    public RootElementWrapper
        (T object)
    {
        super(object);
    }

    /**
     * Creates a new wrapper. This empty constructor is intended for
     * use by JAXB.
     */

    @SuppressWarnings("unused")
    private RootElementWrapper() {}


    // INSTANCE METHODS.

    /**
     * Sets the receiver's wrapped object to the given one.
     *
     * @param object The object, which may be null.
     */

    public void setObject
        (T object)
    {
        setValue(object);
    }
    
    /**
     * Returns the receiver's wrapped object.
     *
     * @return The object, which may be null.
     */

    public T getObject()
    {
        return getValue();
    }
}
