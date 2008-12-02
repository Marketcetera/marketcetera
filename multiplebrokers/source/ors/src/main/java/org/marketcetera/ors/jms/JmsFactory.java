package org.marketcetera.ors.jms;

import javax.jms.ConnectionFactory;
import org.marketcetera.util.misc.ClassVersion;

/**
 * A factory of Spring-wrapped JMS connections.
 *
 * @author tlerios@marketcetera.com
 * @since $Release$
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$") //$NON-NLS-1$
public class JmsFactory
{

    // INSTANCE DATA.

    private final ConnectionFactory mConnectionFactory;


    // CONSTRUCTORS.

    /**
     * Creates a new factory that uses the given standard JMS
     * connection factory to create connections.
     *
     * @param connectionFactory The factory.
     */    

    public JmsFactory
        (ConnectionFactory connectionFactory)
    {
        mConnectionFactory=connectionFactory;
    }


    // INSTANCE METHODS.

    /**
     * Returns the receiver's standard JMS connection factory.
     *
     * @return The factory.
     */

    public ConnectionFactory getConnectionFactory()
    {
        return mConnectionFactory;
    }
}
