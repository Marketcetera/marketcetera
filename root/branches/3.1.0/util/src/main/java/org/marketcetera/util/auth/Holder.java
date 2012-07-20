package org.marketcetera.util.auth;

import org.marketcetera.util.log.I18NBoundMessage;
import org.marketcetera.util.misc.ClassVersion;

/**
 * A holder of a piece of data.
 *
 * @author tlerios@marketcetera.com
 * @since 0.5.0
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$")
public class Holder<T>
{

    // INSTANCE DATA.

    private T mValue;
    private I18NBoundMessage mMessage;


    // CONSTRUCTORS.

    /**
     * Creates a new holder. If the given message is non-null, the
     * holder requires that its data is set during authentication; if
     * the data is not set, the message is logged.
     *
     * @param message The data-not-set message. It may be null if the
     * holder's data is optional and may remain unset.
     */

    public Holder
        (I18NBoundMessage message)
    {
        mMessage=message;
    }

    /**
     * Creates a new holder which does not require its data to be set
     * during authentication.
     */

    public Holder() {}


    // INSTANCE METHODS.

    /**
     * Sets the receiver's data to the given value.
     *
     * @param value The value, which may be null to unset the
     * receiver's data.
     */

    public void setValue
        (T value)
    {
        mValue=value;
    }

    /**
     * Returns the receiver's data.
     *
     * @return The data, which is null if the receiver's data is not
     * set.
     */

    public T getValue()
    {
        return mValue;
    }

    /**
     * Checks whether the receiver's data is set.
     *
     * @return True if so.
     */

    public boolean isSet()
    {
        return (getValue()!=null);
    }

    /**
     * Returns the receiver's optional data-not-set message.
     *
     * @return The message, which is null if the receiver's data is
     * optional.
     */

    public I18NBoundMessage getMessage()
    {
        return mMessage;
    }
}