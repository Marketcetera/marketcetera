package org.marketcetera.rpc.client;

import org.marketcetera.rpc.RpcTestBase;
import org.marketcetera.rpc.sample.SampleRpcService;
import org.marketcetera.rpc.sample.client.SampleRpcClient;
import org.marketcetera.rpc.server.AbstractRpcService;
import org.marketcetera.util.ws.tags.SessionId;

/* $License$ */

/**
 * Tests {@link AbstractRpcClient}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class RpcClientTest
        extends RpcTestBase
{
    /* (non-Javadoc)
     * @see org.marketcetera.rpc.RpcTestBase#createClient()
     */
    @Override
    protected AbstractRpcClient<?,?> createClient()
    {
        return new SampleRpcClient();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.rpc.RpcTestBase#createTestService()
     */
    @Override
    protected AbstractRpcService<SessionId,?> createTestService()
    {
        return new SampleRpcService<SessionId>();
    }
}
