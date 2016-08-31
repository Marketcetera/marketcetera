package org.marketcetera.saclient;

import org.marketcetera.strategyengine.client.SAClient;
import org.marketcetera.strategyengine.client.SAClientFactory;
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
public class SAClientFactoryImpl
        implements SAClientFactory<SAClientParameters>
{
    /* (non-Javadoc)
     * @see org.marketcetera.saclient.SAClientFactory#create(org.marketcetera.saclient.SAClientParameters)
     */
    @Override
    public SAClient<SAClientParameters> create(SAClientParameters inParameters)
    {
        return new SAClientImpl(inParameters);
    }
    /**
     * Returns the singleton factory instance that can be used to
     * create clients to communicate with the remote strategy agents.
     *
     * @return the singleton factory instance.
     */
    public static SAClientFactoryImpl getInstance()
    {
        return sInstance;
    }
    /**
     * Creates an instance.
     */
    protected SAClientFactoryImpl() {}
    /**
     * The singleton factory instance.
     */
    private final static SAClientFactoryImpl sInstance = new SAClientFactoryImpl();
}
