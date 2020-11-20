package org.marketcetera.util.rpc;

import org.marketcetera.util.misc.ClassVersion;

import com.google.protobuf.BlockingService;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: RpcServiceSpec.java 16901 2014-05-11 16:14:11Z colin $
 * @since 2.4.0
 */
@ClassVersion("$Id: RpcServiceSpec.java 16901 2014-05-11 16:14:11Z colin $")
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
