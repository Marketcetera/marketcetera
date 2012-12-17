package org.marketcetera.util.ws.wrappers;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import org.apache.commons.lang.ObjectUtils;
import org.marketcetera.util.misc.ClassVersion;

/**
 * A remote exception. It wraps any {@link Throwable} in a manner that
 * enables its marshalling using JAXB in the context of web service
 * faults, or for use by Java serialization in other contexts; this is
 * done by serializing the throwable and marshalling it using a {@link
 * SerWrapper}. On the server side, this class sets the wrapped
 * throwable as its cause. This also happens on the client side,
 * provided the client runs in a JVM which is able to deserialize the
 * throwable (because it has the right resources and classes available
 * in its classpath). Otherwise, the cause is an instance of {@link
 * RemoteProxyException}, which encapsulates the throwable's most
 * important information, as conveyed via a {@link RemoteProperties}
 * instance.
 *
 * <p>Equality and hash code generation rely only on a temporary
 * {@link RemoteProperties} instance created for comparison purposes
 * from the receiver's cause; notably, the class of the cause is
 * ignored.</p>
 * 
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$")
public class RemoteException
    extends Exception
    implements Externalizable
{

    // CLASS DATA.

    private static final long serialVersionUID=1L;


    // INSTANCE DATA.

    private RemoteProperties mProperties;


    // CONSTRUCTORS.

    /**
     * Creates a new exception that wraps the given throwable.
     *
     * @param t The throwable, which may be null.
     */

    public RemoteException
        (Throwable t)
    {
        this();
        if (t==null) {
            return;
        }
        setProperties(new RemoteProperties(t));
    }

    /**
     * Creates a new exception. This empty constructor is intended for
     * use by JAXB and Java serialization.
     */

    public RemoteException()
    {
        super(Messages.REMOTE_EXCEPTION.getText());
    }


    // INSTANCE METHODS.

    /**
     * Set the receiver's properties to the given ones. This method
     * can only be called once with a non-null argument because a side
     * effect is that it sets the receiver's cause based on its
     * non-null argument.
     *
     * @param properties The properties, which may be null.
     */

    public void setProperties
        (RemoteProperties properties)
    {
        mProperties=properties;
        if (properties==null) {
            return;
        }
        initCause(getProperties().getThrowable());
    }

    /**
     * Returns the receiver's properties.
     *
     * @return The properties, which may be null.
     */

    public RemoteProperties getProperties()
    {
        return mProperties;
    }


    // Externalizable.

    @Override
    public void writeExternal
        (ObjectOutput out)
        throws IOException
    {
        out.writeObject(getProperties());
    }

    @Override
    public void readExternal
        (ObjectInput in)
        throws IOException,
               ClassNotFoundException
    {
        setProperties((RemoteProperties)in.readObject());
    }


    // Exception.

    @Override
    public int hashCode()
    {
        if (getCause()==null) {
            return 0;
        }
        return ObjectUtils.hashCode(new RemoteProperties(getCause()));
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
        RemoteException o=(RemoteException)other;
        if (getCause()==null) {
            if (o.getCause()==null) {
                return true;
            }
            return false;
        } else if (o.getCause()==null) {
            return false;
        }
        return ObjectUtils.equals(new RemoteProperties(getCause()),
                                  new RemoteProperties(o.getCause()));
    }
}
