package org.marketcetera.util.rpc;

import org.marketcetera.util.misc.ClassVersion;

import com.google.protobuf.BlockingService;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 2.4.0
 */
@ClassVersion("$Id$")
public interface RpcServiceSpec<SessionClazz>
{
    /**
     * 
     *
     *
     * @return
     */
    String getDescription();
    /**
     * 
     *
     *
     * @return
     */
    BlockingService generateService();
    /**
     * 
     *
     *
     * @param inServerServices
     */
    void setRpcServerServices(RpcServerServices<SessionClazz> inServerServices);
}
