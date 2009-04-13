package org.marketcetera.util.test;

import java.io.Serializable;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.SerializationException;
import org.apache.commons.lang.SerializationUtils;

/**
 * Assertions for serialization.
 * 
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id$
 */

/* $License$ */

public final class SerializableAssert
{

    // CLASS METHODS.

    /**
     * Asserts that the given object serializes correctly, i.e. the
     * deserialized version of the object is equal to the original
     * object. If the assertion does not hold, the {@link
     * AssertionError} thrown starts with the given message, which may
     * be null if no such custom message prefix is desired.
     *
     * @param message The message.
     * @param o The object.
     */

    public static void assertSerializable
        (String message,
         Serializable o)
    {
        String content=null;
        SerializationException cause=null;
        try {
            Object oNew=SerializationUtils.deserialize
                (SerializationUtils.serialize(o));
            if (ObjectUtils.equals(o,oNew)) {
                return;
            }
            content="expected object is '"+o+ //$NON-NLS-1$
                "' actual is '"+oNew+"'"; //$NON-NLS-1$ //$NON-NLS-2$
        } catch (SerializationException ex) {
            content="de/serialization failed"; //$NON-NLS-1$
            cause=ex;
        }
        if (message!=null) {
            content=message+" "+content; //$NON-NLS-1$
        }
        AssertionError error=new AssertionError(content);
        if (cause!=null) {
            error.initCause(cause);
        }
        throw error;
    }

    /**
     * Asserts that the given object serializes correctly, i.e. the
     * deserialized version of the object is equal to the original
     * object.
     *
     * @param o The object.
     */

    public static void assertSerializable
        (Serializable o)
    {
        assertSerializable(null,o);
    }


    // CONSTRUCTOR.

    /**
     * Constructor. It is private so that no instances can be created.
     */

    private SerializableAssert() {}
}
