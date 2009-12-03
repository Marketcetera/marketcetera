package org.marketcetera.util.ws.wrappers;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlTransient;
import org.apache.commons.lang.SerializationException;
import org.apache.commons.lang.SerializationUtils;
import org.marketcetera.util.misc.ClassVersion;

/**
 * A dual-form wrapper for marshalling a serializable object. The raw
 * form is an object implementing {@link Serializable}, and the
 * marshalled form is a byte array.
 *
 * <p>This wrapper is itself Java serializable and
 * JAXB-marshallable. Its own deserialization/unmarshalling succeeds
 * without an exception even if the wrapped object cannot be
 * deserialized, in which case the wrapper contains a null value for
 * the wrapped object's raw and marshalled forms, logs a warning, and
 * {@link #getDeserializationException()} returns the exception thrown
 * during deserialization. Similarly, its own
 * serialization/marshalling succeeds without an exception even if the
 * wrapped object cannot be serialized, in which case the wrapper
 * contains a null value for the wrapped object's raw and marshalled
 * forms, logs a warning, and {@link #getSerializationException()}
 * returns the exception thrown during serialization.</p>
 *
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$")
public class SerWrapper<T extends Serializable>
    extends DualWrapper<T,byte[]>
{

    // CLASS DATA.

    private static final long serialVersionUID=1L;


    // INSTANCE DATA.

    private SerializationException mSerializationException;
    private SerializationException mDeserializationException;


    // CONSTRUCTORS.

    /**
     * Creates a new wrapper for the given object, in its raw form. It
     * also sets the internal marshalled form to match.
     *
     * @param raw The object, which may be null.
     */

    public SerWrapper
        (T raw)
    {
        super(raw);
    }

    /**
     * Creates a new wrapper. This empty constructor is intended for
     * use by JAXB and Java serialization.
     */

    public SerWrapper() {}


    // INSTANCE METHODS.

    /**
     * Returns the exception encountered during the most recent
     * serialization attempt; it is null if that attempt was
     * successful, or no attempt has been made yet.
     *
     * @return The exception.
     */

    @XmlTransient
    public SerializationException getSerializationException()
    {
        return mSerializationException;
    }

    /**
     * Sets the exception encountered during the most recent
     * serialization attempt to the given exception.
     *
     * @param serializationException The exception.
     */

    private void setSerializationException
        (SerializationException serializationException)
    {
        mSerializationException=serializationException;
    }

    /**
     * Returns the exception encountered during the most recent
     * deserialization attempt; it is null if that attempt was
     * successful, or no attempt has been made yet.
     *
     * @return The exception.
     */

    @XmlTransient
    public SerializationException getDeserializationException()
    {
        return mDeserializationException;
    }

    /**
     * Sets the exception encountered during the most recent
     * deserialization attempt to the given exception.
     *
     * @param deserializationException The exception.
     */

    private void setDeserializationException
        (SerializationException deserializationException)
    {
        mDeserializationException=deserializationException;
    }


    // DualWrapper.

    @SuppressWarnings("unchecked")
    @Override
    protected void toRaw()
    {
        try {
            setRawOnly((T)SerializationUtils.deserialize(getMarshalled()));
            setDeserializationException(null);
        } catch (SerializationException ex) {
            setDeserializationException(ex);
            Messages.DESERIALIZATION_FAILED.warn
                (this,getDeserializationException());
            setRawOnly(null);
        }
    }
    
    @Override
    protected void toMarshalled()
    {
        try {
            setMarshalledOnly(SerializationUtils.serialize(getRaw()));
            setSerializationException(null);
        } catch (SerializationException ex) {
            setSerializationException(ex);
            Messages.SERIALIZATION_FAILED.warn
                (this,getSerializationException());
            setMarshalledOnly(null);
        }
    }
}
