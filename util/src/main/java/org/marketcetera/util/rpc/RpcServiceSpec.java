package org.marketcetera.util.rpc;

import org.marketcetera.util.misc.ClassVersion;

import com.google.protobuf.BlockingService;

/* $License$ */

/**
 * Provide RPC services.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 2.4.0
 */
@ClassVersion("$Id$")
public interface RpcServiceSpec<SessionClazz>
{
    /**
     * Get the service description.
     *
     * @return a <code>String</code> value
     */
    String getDescription();
    /**
     * Generate the service.
     *
     * @return a <code>BlockingService</code> value
     */
    BlockingService generateService();
    /**
     * Set the RPC server services value.
     *
     * @param inServerServices a <code>RpcServiceServices&lt;SessionClazz&gt;</code> value
     */
    void setRpcServerServices(RpcServerServices<SessionClazz> inServerServices);
}
