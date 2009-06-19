package org.marketcetera.util.ws.wrappers;

import java.io.Serializable;
import org.marketcetera.util.misc.ClassVersion;

/**
 * A dual-form wrapper for marshalling a comparable and serializable
 * object. The raw form is an object implementing {@link Serializable}
 * and {@link Comparable}, and the marshalled form is a byte
 * array.
 * 
 * <p>This wrapper is itself serializable: its own deserialization
 * succeeds without an exception even if the wrapped object cannot be
 * deserialized, in which case the wrapper contains a null value for
 * the wrapped object's raw and marshalled forms, and {@link
 * SerWrapper#getDeserializationException()} returns the exception
 * thrown during serialization.</p>
 *
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$")
public class CompSerWrapper<T extends Serializable &
                                      Comparable<? super T>>
    extends SerWrapper<T>
    implements Comparable<CompSerWrapper<T>>
{

    // CLASS DATA.

    private static final long serialVersionUID=1L;


    // CONSTRUCTORS.

    /**
     * Creates a new wrapper for the given object, in its raw form. It
     * also sets the internal marshalled form to match.
     *
     * @param raw The object, which may be null.
     */

    public CompSerWrapper
        (T raw)
    {
        super(raw);
    }

    /**
     * Creates a new wrapper. This empty constructor is intended for
     * use by JAXB and Java serialization.
     */

    public CompSerWrapper() {}


    // Comparable.

    @Override
    public int compareTo
        (CompSerWrapper<T> other)
    {
        if (getRaw()==null) {
            throw new NullPointerException
                (Messages.RECEIVER_WRAPS_NULL.getText());
        }
        if (other==null) {
            throw new NullPointerException
                (Messages.ARGUMENT_IS_NULL.getText());
        }
        if (other.getRaw()==null) {
            throw new NullPointerException
                (Messages.ARGUMENT_WRAPS_NULL.getText());
        }
        return getRaw().compareTo(other.getRaw());
    }
}
