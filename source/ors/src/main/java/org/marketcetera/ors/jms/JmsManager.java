package org.marketcetera.ors.jms;

import javax.jms.ConnectionFactory;
import org.marketcetera.util.misc.ClassVersion;

/**
 * A Spring-wrapped JMS connection manager.
 *
 * @author tlerios@marketcetera.com
 * @since $Release$
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$") //$NON-NLS-1$
public class JmsManager
{

    // INSTANCE DATA.

    private final IncomingJmsFactory mIncomingJmsFactory;
    private final OutgoingJmsFactory mOutgoingJmsFactory;


    // CONSTRUCTORS.

    /**
     * Creates a new manager that uses the given standard JMS
     * connection factories to create factories for Spring-wrapped
     * connections.
     *
     * @param incomingCF The incoming factory.
     * @param outgoingCF The outgoing factory.
     */

    public JmsManager
        (ConnectionFactory incomingCF,
         ConnectionFactory outgoingCF)
    {
        mIncomingJmsFactory=new IncomingJmsFactory(incomingCF);
        mOutgoingJmsFactory=new OutgoingJmsFactory(outgoingCF);
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
