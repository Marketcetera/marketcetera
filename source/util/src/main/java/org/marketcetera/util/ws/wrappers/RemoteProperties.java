package org.marketcetera.util.ws.wrappers;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.marketcetera.util.except.I18NThrowable;
import org.marketcetera.util.misc.ClassVersion;

/**
 * A container for the information marshalled for a {@link
 * RemoteException}. That information is encapsulated in a container,
 * instead of being marshalled as properties of {@link
 * RemoteException}, so that the exception may perform post-processing
 * after all component properties below are set.
 *
 * <p>The stack trace capture is a string array, containing the
 * individual lines of the receive's throwable stack trace printing:
 * the first string is usually the exception class and message, and
 * the remaining strings represent individual frames in the unravelled
 * stack.</p>
 *
 * <p>Also, equality (and hash code generation) ignore the receiver's
 * throwable (wrapper).</p>
 * 
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$")
public class RemoteProperties
{

    // INSTANCE DATA.

    private SerWrapper<Throwable> mWrapper;
    private String[] mTraceCapture;
    private String mServerMessage;
    private String mServerString;


    // CONSTRUCTORS.

    /**
     * Creates a new container for a {@link RemoteException} that
     * wraps the given throwable.
     *
     * @param t The throwable.
     */

    public RemoteProperties
        (Throwable t)
    {
        setWrapper(new SerWrapper<Throwable>(t));
        setTraceCapture(ExceptionUtils.getStackFrames(t));
        if (t instanceof I18NThrowable) {
            setServerMessage(((I18NThrowable)t).getLocalizedDetail());
        } else {
            setServerMessage(t.getLocalizedMessage());
        }
        setServerString(t.toString());
    }

    /**
     * Creates a new container. This empty constructor is intended for
     * use by JAXB. It must be public because a non-public one does
     * not work for classes that are marshalled as part of an
     * exception.
     */

    public RemoteProperties() {}


    // INSTANCE METHODS.

    /**
     * Set the receiver's throwable (wrapper) to the given one.
     *
     * @param wrapper The throwable (wrapper).
     */

    public void setWrapper
        (SerWrapper<Throwable> wrapper)
    {
        mWrapper=wrapper;
    }

    /**
     * Returns the receiver's throwable (wrapper).
     *
     * @return The throwable (wrapper).
     */

    public SerWrapper<Throwable> getWrapper()
    {
        return mWrapper;
    }

    /**
     * Set the receiver's stack trace capture to the given one.
     *
     * @param traceCapture The capture.
     */

    public void setTraceCapture
        (String[] traceCapture)
    {
        mTraceCapture=traceCapture;
    }

    /**
     * Returns the receiver's stack trace capture.
     *
     * @return The capture.
     */

    public String[] getTraceCapture()
    {
        return mTraceCapture;
    }

    /**
     * Sets the receiver's server-localized message to the given one.
     *
     * @param serverMessage The message.
     */

    public void setServerMessage
        (String serverMessage)
    {
        mServerMessage=serverMessage;
    }

    /**
     * Returns the receiver's server-localized message.
     *
     * @return The message.
     */

    public String getServerMessage()
    {
        return mServerMessage;
    }

    /**
     * Sets the receiver's server string representation to the given
     * one.
     *
     * @param serverString The string.
     */

    public void setServerString
        (String serverString)
    {
        mServerString=serverString;
    }

    /**
     * Returns the receiver's server string representation.
     *
     * @return The string.
     */

    public String getServerString()
    {
        return mServerString;
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
