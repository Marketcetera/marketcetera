package org.marketcetera.saclient;

import org.marketcetera.strategyengine.client.SEClientFactory;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Factory for creating remote connections to the Strategy Agent.
 *
 * @author anshul@marketcetera.com
 * @version $Id: SEClientFactoryImpl.java 17242 2016-09-02 16:46:48Z colin $
 * @since 2.0.0
 */
@ClassVersion("$Id: SEClientFactoryImpl.java 17242 2016-09-02 16:46:48Z colin $")
public class SEClientFactoryImpl
        implements SEClientFactory<SEClientParameters>
{
    /* (non-Javadoc)
     * @see org.marketcetera.saclient.SAClientFactory#create(org.marketcetera.saclient.SAClientParameters)
     */
    @Override
    public SEClientImpl create(SEClientParameters inParameters)
    {
        return new SEClientImpl(inParameters);
    }
    /**
     * Returns the singleton factory instance that can be used to
     * create clients to communicate with the remote strategy agents.
     *
     * @return the singleton factory instance.
     */
    public static SEClientFactoryImpl getInstance()
    {
        return sInstance;
    }
    /**
     * Creates an instance.
     */
    protected SEClientFactoryImpl() {}
    /**
     * The singleton factory instance.
     */
    private final static SEClientFactoryImpl sInstance = new SEClientFactoryImpl();
}
