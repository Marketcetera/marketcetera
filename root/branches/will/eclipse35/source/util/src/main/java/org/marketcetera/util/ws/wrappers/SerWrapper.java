package org.marketcetera.util.ws.wrappers;

import java.io.Serializable;
import org.apache.commons.lang.SerializationException;
import org.apache.commons.lang.SerializationUtils;
import org.marketcetera.util.misc.ClassVersion;

/**
 * A dual-form wrapper for marshalling a serializable object. The raw
 * form is an object implementing {@link Serializable}, and the
 * marshalled form is a byte array.
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
     * use by JAXB.
     */

    protected SerWrapper() {}


    // DualWrapper.

    @SuppressWarnings("unchecked")
    @Override
    protected void toRaw()
    {
        try {
            setRawOnly((T)SerializationUtils.deserialize(getMarshalled()));
        } catch (SerializationException ex) {
            Messages.SERIALIZATION_ERROR.error(this,ex);
            setRawOnly(null);
        }
    }
    
    @Override
    protected void toMarshalled()
    {
        setMarshalledOnly(SerializationUtils.serialize(getRaw()));
    }
}
