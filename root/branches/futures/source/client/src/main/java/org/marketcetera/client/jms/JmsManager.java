package org.marketcetera.client.jms;

import javax.jms.ConnectionFactory;
import javax.jms.ExceptionListener;
import org.marketcetera.util.misc.ClassVersion;

/**
 * A Spring-wrapped JMS connection manager.
 *
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$")
public class JmsManager
{

    // INSTANCE DATA.

    private final IncomingJmsFactory mIncomingJmsFactory;
    private final OutgoingJmsFactory mOutgoingJmsFactory;


    // CONSTRUCTORS.

    /**
     * Creates a new manager that uses the given standard JMS
     * connection factories to create factories for Spring-wrapped
     * connections, and directs exceptions to the given listener, if
     * any.
     *
     * @param incomingCF The incoming factory.
     * @param outgoingCF The outgoing factory.
     * @param exceptionListener The listener. It may be null.
     */

    public JmsManager
        (ConnectionFactory incomingCF,
         ConnectionFactory outgoingCF,
         ExceptionListener exceptionListener)
    {
        mIncomingJmsFactory=new IncomingJmsFactory
            (incomingCF,exceptionListener);
        mOutgoingJmsFactory=new OutgoingJmsFactory(outgoingCF);
    }

    /**
     * Creates a new manager that uses the given standard JMS
     * connection factories to create factories for Spring-wrapped
     * connections. No custom exception listener is used.
     *
     * @param incomingCF The incoming factory.
     * @param outgoingCF The outgoing factory.
     */

    public JmsManager
        (ConnectionFactory incomingCF,
         ConnectionFactory outgoingCF)
    {
        this(incomingCF,outgoingCF,null);
    }


    // INSTANCE METHODS.

    /**
     * Returns the receiver's Spring-wrapped factory of incoming
     * connections.
     *
     * @return The factory.
     */

    public IncomingJmsFactory getIncomingJmsFactory()
    {
        return mIncomingJmsFactory;
    }

    /**
     * Returns the receiver's Spring-wrapped factory of outgoing
     * connections.
     *
     * @return The factory.
     */

    public OutgoingJmsFactory getOutgoingJmsFactory()
    {
        return mOutgoingJmsFactory;
    }
}
