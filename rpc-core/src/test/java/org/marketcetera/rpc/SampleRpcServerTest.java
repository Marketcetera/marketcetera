package org.marketcetera.rpc;

import org.junit.Test;
import org.marketcetera.rpc.sample.client.SampleRpcService;
import org.marketcetera.rpc.server.RpcServer;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class SampleRpcServerTest
{
    @Test
    public void testStart()
            throws Exception
    {
        SampleRpcService sampleRpcService = new SampleRpcService();
        RpcServer server = new RpcServer();
        server.setHostname("127.0.0.1");
        server.setPort(10005);
        server.getServerServiceDefinitions().add(sampleRpcService);
        server.start();
        Thread.sleep(10000);
        server.stop();
    }
}
