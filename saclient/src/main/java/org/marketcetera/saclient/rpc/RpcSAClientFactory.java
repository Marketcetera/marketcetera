package org.marketcetera.saclient.rpc;

import org.marketcetera.saclient.SAClientFactory;
import org.marketcetera.saclient.SAClientParameters;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Creates {@link RpcSAClient} instances.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 2.4.0
 */
@ClassVersion("$Id$")
public class RpcSAClientFactory
        implements SAClientFactory
{
    /* (non-Javadoc)
     * @see org.marketcetera.saclient.SAClientFactory#create(org.marketcetera.saclient.SAClientParameters)
     */
    @Override
    public RpcSAClientImpl create(SAClientParameters inParameters)
    {
        return new RpcSAClientImpl(inParameters);
    }
    /**
     * factory instance value
     */
    public static final RpcSAClientFactory INSTANCE = new RpcSAClientFactory();
}
