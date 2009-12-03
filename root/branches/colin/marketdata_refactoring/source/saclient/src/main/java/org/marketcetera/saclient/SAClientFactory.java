package org.marketcetera.saclient;

import org.marketcetera.util.misc.ClassVersion;

/* $License$ */
/**
 * Factory for creating remote connections to the Strategy Agent.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 2.0.0
 */
@ClassVersion("$Id$")
public class SAClientFactory {
    /**
     * Creates a client that can be used to communicate with a remote strategy
     * agent.
     *
     * @param inParameters the connection details on how to connect to the
     * remote strategy agent. Cannot be null.
     *
     * @return the client instance that can be used to communicate with
     * the remote strategy agent.
     *
     * @throws ConnectionException If there were errors connecting to the
     * remote strategy agent.
     */
    public SAClient create(SAClientParameters inParameters) throws ConnectionException {
        return new SAClientImpl(inParameters);
    }

    /**
     * Returns the singleton factory instance that can be used to
     * create clients to communicate with the remote strategy agents.
     *
     * @return the singleton factory instance.
     */
    public static SAClientFactory getInstance() {
        return sInstance;
    }

    /**
     * Creates an instance.
     */
    protected SAClientFactory() {
        //defined as protected to prevent direct creation of the factory
        //by clients.
    }

    /**
     * The singleton factory instance.
     */
    private final static SAClientFactory sInstance = new SAClientFactory();
}
