package org.marketcetera.util.ws.stateless;

import org.junit.Test;

/**
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id$
 */

/* $License$ */

public class StatelessClientServerTest
    extends ClientServerTestBase
{
    private static void calls
        (StatelessServer server,
         StatelessClient client)
    {
        StatelessClient client2=new StatelessClient
            (client.getHost(),client.getPort()+1,client.getAppId());
        calls(server,client,client.getContext().toString(),
              new StatelessServer(client2.getHost(),client2.getPort()),
              client2,client2.getContext().toString());
    }                         


    @Test
    public void basics()
    {
        singleClientEmpty
            (new StatelessClient(TEST_HOST,TEST_PORT,TEST_APP),
             new StatelessClient());
        singleClientJustId
            (new StatelessClient(TEST_HOST,TEST_PORT,TEST_APP),
             new StatelessClient(TEST_APP));
        singleServer
            (new StatelessServer(TEST_HOST,TEST_PORT),
             new StatelessServer());
        calls
            (new StatelessServer(),
             new StatelessClient());
        calls
            (new StatelessServer(),
             new StatelessClient(TEST_APP));
        badConnection
            (new StatelessServer(TEST_HOST,TEST_BAD_PORT),
             new StatelessClient(TEST_HOST,TEST_BAD_PORT,TEST_APP));
    }
}
