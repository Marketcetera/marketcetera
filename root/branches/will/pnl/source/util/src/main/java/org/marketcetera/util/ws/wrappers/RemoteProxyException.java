package org.marketcetera.util.ws.wrappers;

import java.io.PrintStream;
import java.io.PrintWriter;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.ObjectUtils;
import org.marketcetera.util.misc.ClassVersion;

/**
 * A partial, client-side representation of a server exception, when
 * the full exception cannot be reconstructed.
 * 
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$")
public class RemoteProxyException
    extends Exception
{

    // CLASS DATA.

    private static final long serialVersionUID=1L;


    // INSTANCE DATA.

    private final String[] mTraceCapture;
    private final String mServerString;


    // CONSTRUCTORS.

    /**
     * Creates a new client-side representation of a server exception,
     * whose marshalled information is given.
     *
     * @param message The server-localized message.
     * @param traceCapture The stack trace capture.
     * @param serverString The server string representation.
     */

    public RemoteProxyException
        (String message,
         String[] traceCapture,
         String serverString)
    {
        super(message);
        mTraceCapture=traceCapture;
        mServerString=serverString;
    }


    // INSTANCE METHODS.

    /**
     * Returns the receiver's stack trace capture.
     *
     * @return The capture.
     */

    public String[] getTraceCapture()
    {
        return mTraceCapture;
    }


    // Exception.

    @Override
    public void printStackTrace()
    {
        for (String frame:getTraceCapture()) {
            System.err.println(frame);
        }
    }

    @Override
    public void printStackTrace
        (PrintStream s)
    {
        for (String frame:getTraceCapture()) {
            s.println(frame);
        }
    }

    @Override
    public void printStackTrace
        (PrintWriter s)
    {
        for (String frame:getTraceCapture()) {
            s.println(frame);
        }
    }

    @Override
    public StackTraceElement[] getStackTrace()
    {
        return null;
    }

    @Override
    public String toString()
    {
        return mServerString;
    }

    @Override
    public int hashCode()
    {
        return (ObjectUtils.hashCode(getMessage())+
                ArrayUtils.hashCode(getTraceCapture())+
                ObjectUtils.hashCode(toString()));
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
        RemoteProxyException o=(RemoteProxyException)other;
        return (ObjectUtils.equals(getMessage(),o.getMessage()) &&
                ArrayUtils.isEquals(getTraceCapture(),o.getTraceCapture()) &&
                ObjectUtils.equals(toString(),o.toString()));
    }
}
