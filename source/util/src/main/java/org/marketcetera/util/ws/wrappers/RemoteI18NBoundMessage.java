package org.marketcetera.util.ws.wrappers;

import java.io.Serializable;
import org.apache.commons.lang.ObjectUtils;
import org.marketcetera.util.log.I18NBoundMessage;
import org.marketcetera.util.misc.ClassVersion;

/**
 * A wrapper for an {@link I18NBoundMessage}. The message is
 * marshalled using JAXB or Java serialization as both a bound
 * internationalized message and a server-localized string. If the
 * message can be recreated on the client side (which requires all
 * necessary message files and parameter classes to be available,
 * though no check is made to ensure the message handle is present in
 * these message files), then {@link #getText()} localizes the message
 * on the client; otherwise, it returns the server-localized string.
 *
 * <p>Equality and hash code generation rely only upon the result of
 * {@link #getText()}.</p>
 *
 * @author tlerios@marketcetera.com
 * @since $Release$
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$")
public class RemoteI18NBoundMessage
    implements Serializable
{

    // CLASS DATA.

    private static final long serialVersionUID=1L;


    // INSTANCE DATA.

    private SerWrapper<I18NBoundMessage> mWrapper;
    private String mString;


    // CONSTRUCTORS.

    /**
     * Creates a new message wrapper that wraps the given message.
     *
     * @param m The message, which may be null.
     */

    public RemoteI18NBoundMessage
        (I18NBoundMessage m)
    {
        if (m==null) {
            return;
        }
        mWrapper=new SerWrapper<I18NBoundMessage>(m);
        mString=m.getText();
    }

    /**
     * Creates a new message wrapper. This empty constructor is
     * intended for use by JAXB.
     */

    @SuppressWarnings("unused")
    private RemoteI18NBoundMessage() {}


    // INSTANCE METHODS.

    /**
     * Sets the receiver's message (wrapper) to the given one.
     *
     * @param wrapper The message (wrapper), which may be null.
     */

    public void setWrapper
        (SerWrapper<I18NBoundMessage> wrapper)
    {
        mWrapper=wrapper;
    }

    /**
     * Returns the receiver's message (wrapper).
     *
     * @return The message (wrapper), which may be null.
     */

    public SerWrapper<I18NBoundMessage> getWrapper()
    {
        return mWrapper;
    }

    /**
     * Sets the receiver's (server-localized) message to the given one.
     *
     * @param string The (server-localized) message, which may be
     * null.
     */

    public void setString
        (String string)
    {
        mString=string;
    }

    /**
     * Returns the receiver's (server-localized) message.
     *
     * @return The (server-localized) message, which may be null.
     */

    public String getString()
    {
        return mString;
    }

    /**
     * Returns the receiver's message text. Preference is given to
     * localizing the message wrapper, if successfully
     * unmarshalled/deserialized; otherwise, the server-localized
     * message message is returned.
     *
     * @return The text, which may be null.
     */

    public String getText()
    {
        if (getWrapper()==null) {
            return null;
        }
        if ((getWrapper().getDeserializationException()==null) &&
            (getWrapper().getRaw()!=null)) {
            return getWrapper().getRaw().getText();
        }
        return getString();
    }


    // Object.

    @Override
    public String toString()
    {
        return getText();
    }

    @Override
    public int hashCode()
    {
        return ObjectUtils.hashCode(getText());
    }

    @Override
    public boolean equals
        (Object other)
    {
        if (this==other) {
            return true;
        }
        if ((other==null) || !getClass().equals(other.getClass())) {
            return false;
        }
        RemoteI18NBoundMessage o=(RemoteI18NBoundMessage)other;
        return ObjectUtils.equals(getText(),o.getText());
    }
}
