package org.marketcetera.util.ws.wrappers;

import javax.xml.bind.annotation.XmlTransient;
import org.marketcetera.util.misc.ClassVersion;

/**
 * A remote exception. It wraps any {@link Throwable} in a manner that
 * enables its marshalling using JAXB in the context of web service
 * faults; this is done by serializing the throwable and marshalling
 * it using a {@link SerWrapper}. On the server side, this class sets
 * the wrapped throwable as its cause. This also happens on the client
 * side, provided the client runs in a JVM which is able to
 * deserialize the throwable (because it has the right classes
 * available in its classpath). Otherwise, the cause is an instance of
 * {@link RemoteProxyException}, which encapsulates the throwable's
 * most important information.
 * 
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$")
public class RemoteException
    extends Exception
{

    // CLASS DATA.

    private static final long serialVersionUID=1L;


    // INSTANCE DATA.

    private RemoteProperties mProperties;


    // CONSTRUCTORS.

    /**
     * Creates a new exception that wraps the given throwable.
     *
     * @param t The throwable.
     */

    public RemoteException
        (Throwable t)
    {
        this();
        setProperties(new RemoteProperties(t));
    }

    /**
     * Creates a new exception. This empty constructor is intended for
     * use by JAXB.
     */

    public RemoteException()
    {
        super(Messages.REMOTE_EXCEPTION.getText());
    }


    // INSTANCE METHODS.

    /**
     * Set the receiver's properties to the given ones.
     *
     * @param properties The properties.
     */

    public void setProperties
        (RemoteProperties properties)
    {
        mProperties=properties;
        if (getProperties().getWrapper()==null) {
            initCause(new RemoteProxyException
                      (getProperties().getServerMessage(),
                       getProperties().getTraceCapture(),
                       getProperties().getServerString()));
        } else {
            initCause(getProperties().getWrapper().getRaw());
        }
    }

    /**
     * Returns the receiver's properties.
     *
     * @return The properties.
     */

    public RemoteProperties getProperties()
    {
        return mProperties;
    }


    // Exception.

    @XmlTransient
    @Override
    public StackTraceElement[] getStackTrace()
    {
        return super.getStackTrace();
    }
}
