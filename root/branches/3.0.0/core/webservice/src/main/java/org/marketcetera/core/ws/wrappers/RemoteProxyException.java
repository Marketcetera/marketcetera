package org.marketcetera.core.ws.wrappers;

import java.io.PrintStream;
import java.io.PrintWriter;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.ObjectUtils;
import org.marketcetera.core.attributes.ClassVersion;

/**
 * A partial, client-side representation of a server exception, when
 * the full exception cannot be reconstructed.
 * 
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id: RemoteProxyException.java 82324 2012-04-09 20:56:08Z colin $
 */

/* $License$ */

@ClassVersion("$Id: RemoteProxyException.java 82324 2012-04-09 20:56:08Z colin $")
public class RemoteProxyException
    extends Exception
{

    // CLASS DATA.

    private static final long serialVersionUID=1L;


    // INSTANCE DATA.

    private final String[] mTraceCapture;
    private final String mServerString;
    private final String mServerName;


    // CONSTRUCTORS.

    /**
     * Creates a new client-side representation of a server exception,
     * whose marshalled information is given.
     *
     * @param message The server-localized message, which may be null.
     * @param traceCapture The stack trace capture, which may be null.
     * @param serverString The server string representation, which may
     * be null.
     * @param serverName The server class name, which may be null.
     */

    public RemoteProxyException
        (String message,
         String[] traceCapture,
         String serverString,
         String serverName)
    {
        super(message);
        mTraceCapture=traceCapture;
        mServerString=serverString;
        mServerName=serverName;
    }


    // INSTANCE METHODS.

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
     * Returns the receiver's server class name.
     *
     * @return The name, which may be null.
     */

    public String getServerName()
    {
        return mServerName;
    }


    // Exception.

    @Override
    public void printStackTrace()
    {
        if (getTraceCapture()==null) {
            return;
        }
        for (String frame:getTraceCapture()) {
            System.err.println(frame);
        }
    }

    @Override
    public void printStackTrace
        (PrintStream s)
    {
        if (getTraceCapture()==null) {
            return;
        }
        for (String frame:getTraceCapture()) {
            s.println(frame);
        }
    }

    @Override
    public void printStackTrace
        (PrintWriter s)
    {
        if (getTraceCapture()==null) {
            return;
        }
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
                ObjectUtils.hashCode(toString())+
                ObjectUtils.hashCode(getServerName()));
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
                ObjectUtils.equals(toString(),o.toString()) &&
                ObjectUtils.equals(getServerName(),o.getServerName()));
    }
}
