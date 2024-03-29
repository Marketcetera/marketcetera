package org.marketcetera.util.ws.wrappers;

import javax.xml.bind.annotation.XmlRootElement;

import org.marketcetera.util.misc.ClassVersion;

/**
 * A wrapper for any object, allowing it to become a root element in
 * JAXB marshalling.
 *
 * @author tlerios@marketcetera.com
 * @since 2.0.0
 * @version $Id: RootElementWrapper.java 16154 2012-07-14 16:34:05Z colin $
 */

/* $License$ */

@XmlRootElement
@ClassVersion("$Id: RootElementWrapper.java 16154 2012-07-14 16:34:05Z colin $")
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
