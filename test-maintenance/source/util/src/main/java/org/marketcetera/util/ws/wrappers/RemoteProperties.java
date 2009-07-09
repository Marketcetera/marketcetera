package org.marketcetera.util.ws.wrappers;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlTransient;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.marketcetera.util.except.I18NThrowable;
import org.marketcetera.util.misc.ClassVersion;

/**
 * A container for the information marshalled/serialized by a {@link
 * RemoteException}. That information is encapsulated in a container,
 * instead of being marshalled/serialized as individual properties of
 * {@link RemoteException}, so that the exception may perform
 * post-processing after all component properties below are set.
 *
 * <p>The stack trace capture is a string array, containing the
 * individual lines of the receiver's throwable stack trace printing:
 * the first string is usually the exception class and message, and
 * the remaining strings represent individual frames in the unravelled
 * stack.</p>
 *
 * <p>Equality and hash code generation ignore the receiver's
 * throwable and its wrapper.</p>
 * 
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$")
public class RemoteProperties
    implements Serializable
{

    // CLASS DATA.

    private static final long serialVersionUID=1L;


    // INSTANCE DATA.

    private transient Throwable mThrowable;
    private SerWrapper<Throwable> mWrapper;
    private String[] mTraceCapture;
    private String mServerMessage;
    private String mServerString;


    // CONSTRUCTORS.

    /**
     * Creates a new container for a {@link RemoteException} that
     * wraps the given throwable.
     *
     * @param throwable The throwable, which may be null.
     */

    public RemoteProperties
        (Throwable throwable)
    {
        setTransientThrowable(throwable);
        if (getTransientThrowable()==null) {
            return;
        }
        setWrapper(new SerWrapper<Throwable>(getTransientThrowable()));
        setTraceCapture(ExceptionUtils.getStackFrames(getTransientThrowable()));
        if (getTransientThrowable() instanceof I18NThrowable) {
            setServerMessage(((I18NThrowable)getTransientThrowable()).
                             getLocalizedDetail());
        } else {
            setServerMessage(getTransientThrowable().getLocalizedMessage());
        }
        setServerString(getTransientThrowable().toString());
    }

    /**
     * Creates a new container. This empty constructor is intended for
     * use by JAXB and Java serialization. It must be public because a
     * non-public one does not work for classes that are marshalled by
     * JAXB as part of an exception.
     */

    public RemoteProperties() {}


    // INSTANCE METHODS.

    /**
     * Sets the receiver's throwable to the given one.
     *
     * @param throwable The throwable, which may be null.
     */

    private void setTransientThrowable
        (Throwable throwable)
    {
        mThrowable=throwable;
    }

    /**
     * Returns the receiver's throwable.
     *
     * @return The throwable, which may be null.
     */

    @XmlTransient
    private Throwable getTransientThrowable()
    {
        return mThrowable;
    }

    /**
     * Set the receiver's throwable (wrapper) to the given one.
     *
     * @param wrapper The throwable (wrapper), which may be null.
     */

    public void setWrapper
        (SerWrapper<Throwable> wrapper)
    {
        mWrapper=wrapper;
    }

    /**
     * Returns the receiver's throwable (wrapper).
     *
     * @return The throwable (wrapper), which may be null.
     */

    public SerWrapper<Throwable> getWrapper()
    {
        return mWrapper;
    }

    /**
     * Set the receiver's stack trace capture to the given one.
     *
     * @param traceCapture The capture, which may be null.
     */

    public void setTraceCapture
        (String[] traceCapture)
    {
        mTraceCapture=traceCapture;
    }

    /**
     * Returns the receiver's stack trace capture.
     *
     * @return The capture, which may be null.
     */

    public String[] getTraceCapture()
    {
        return mTraceCapture;
    }

    /**
     * Sets the receiver's server-localized message to the given one.
     *
     * @param serverMessage The message, which may be null.
     */

    public void setServerMessage
        (String serverMessage)
    {
        mServerMessage=serverMessage;
    }

    /**
     * Returns the receiver's server-localized message.
     *
     * @return The message, which may be null.
     */

    public String getServerMessage()
    {
        return mServerMessage;
    }

    /**
     * Sets the receiver's server string representation to the given
     * one.
     *
     * @param serverString The string, which may be null.
     */

    public void setServerString
        (String serverString)
    {
        mServerString=serverString;
    }

    /**
     * Returns the receiver's server string representation.
     *
     * @return The string, which may be null.
     */

    public String getServerString()
    {
        return mServerString;
    }

    /**
     * Returns a best-effort reconstruction of the receiver's
     * throwable, as described in {@link RemoteException}.
     *
     * @return The throwable, which may be null.
     */

    public Throwable getThrowable()
    {
        if (getTransientThrowable()!=null) {
            return getTransientThrowable();
        }
        if (getWrapper()==null) {
            return null;
        }
        if (getWrapper().getRaw()!=null) {
            return getWrapper().getRaw();
        }
        return new RemoteProxyException
            (getServerMessage(),getTraceCapture(),getServerString());
    }


    // Object.

    @Override
    public int hashCode()
    {
        return (ArrayUtils.hashCode(getTraceCapture())+
                ObjectUtils.hashCode(getServerMessage())+
                ObjectUtils.hashCode(getServerString()));
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
        RemoteProperties o=(RemoteProperties)other;
        return (ArrayUtils.isEquals(getTraceCapture(),o.getTraceCapture()) &&
                ObjectUtils.equals(getServerMessage(),o.getServerMessage()) &&
                ObjectUtils.equals(getServerString(),o.getServerString()));
    }
}
