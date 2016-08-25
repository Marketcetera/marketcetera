package org.marketcetera.rpc.client;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.marketcetera.marketdata.MarketDataFeedTestBase;
import org.marketcetera.module.ExpectedFailure;
import org.marketcetera.rpc.MockAuthenticator;
import org.marketcetera.rpc.sample.client.SampleRpcClient;
import org.marketcetera.rpc.sample.client.SampleRpcService;
import org.marketcetera.rpc.server.RpcServer;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.springframework.util.SocketUtils;

import io.grpc.BindableService;
import io.grpc.StatusRuntimeException;

/* $License$ */

/**
 * Tests {@link AbstractRpcClient}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class RpcClientTest
{
    /**
     * Run before each test.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Before
    public void setup()
            throws Exception
    {
        clients.clear();
        authenticator = new MockAuthenticator();
        authenticator.getUserstore().put("test",
                                         "password");
        int port = SocketUtils.findAvailableTcpPort();
        createService();
        startServer("127.0.0.1",
                    port,
                    rpcService);
    }
    /**
     * Run after each test.
     *
     * @throws Exception if an unexpected error occurs
     */
    @After
    public void cleanup()
            throws Exception
    {
        for(SampleRpcClient client : clients) {
            try {
                client.stop();
            } catch (Exception e) {
                SLF4JLoggerProxy.warn(this,
                                      e);
            }
        }
        stopServer();
    }
    /**
     * Test creating a client with a bad password.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testBadPassword()
            throws Exception
    {
        new ExpectedFailure<StatusRuntimeException>("UNAUTHENTICATED: test is not a valid user or the password was invalid") {
            @Override
            protected void run()
                    throws Exception
            {
                createClient(rpcServer.getHostname(),
                             rpcServer.getPort(),
                             "test",
                             "bad-password");
            }
        };
    }
    /**
     * Test creating a client with a bad username.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testBadUsername()
            throws Exception
    {
        new ExpectedFailure<StatusRuntimeException>("UNAUTHENTICATED: bad-username is not a valid user or the password was invalid") {
            @Override
            protected void run()
                    throws Exception
            {
                createClient(rpcServer.getHostname(),
                             rpcServer.getPort(),
                             "bad-username",
                             "password");
            }
        };
    }
    /**
     * Test creating a client with a bad hostname.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testBadServerHostname()
            throws Exception
    {
        new ExpectedFailure<StatusRuntimeException>("UNAVAILABLE: Name resolution failed") {
            @Override
            protected void run()
                    throws Exception
            {
                createClient("not-a-valid-host",
                             rpcServer.getPort(),
                             "test",
                             "password");
            }
        };
    }
    /**
     * Test creating a client with a bad port.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testBadServerPort()
            throws Exception
    {
        new ExpectedFailure<StatusRuntimeException>("UNAVAILABLE") {
            @Override
            protected void run()
                    throws Exception
            {
                createClient(rpcServer.getHostname(),
                             rpcServer.getPort()+1,
                             "test",
                             "password");
            }
        };
    }
    /**
     * Test normal connection/receipt of heartbeats.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testNormalLogin()
            throws Exception
    {
        final SampleRpcClient client = createClient();
        assertTrue(client.isRunning());
        MarketDataFeedTestBase.wait(new Callable<Boolean>() {
            @Override
            public Boolean call()
                    throws Exception
            {
                return client.getHeartbeatCount() >= 5;
            }
        });
    }
    /**
     * Test client auto-reconnection.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testServerReconnection()
            throws Exception
    {
        final SampleRpcClient client = createClient();
        assertTrue(client.isRunning());
        MarketDataFeedTestBase.wait(new Callable<Boolean>() {
            @Override
            public Boolean call()
                    throws Exception
            {
                return client.getHeartbeatCount() >= 5;
            }
        });
        rpcServer.stop();
        MarketDataFeedTestBase.wait(new Callable<Boolean>() {
            @Override
            public Boolean call()
                    throws Exception
            {
                return !client.isRunning();
            }
        });
        Thread.sleep(5000);
        rpcServer.start();
        MarketDataFeedTestBase.wait(new Callable<Boolean>() {
            @Override
            public Boolean call()
                    throws Exception
            {
                return client.isRunning();
            }
        });
        MarketDataFeedTestBase.wait(new Callable<Boolean>() {
            @Override
            public Boolean call()
                    throws Exception
            {
                return client.getHeartbeatCount() >= 5;
            }
        });
    }
    /**
     * 
     *
     *
     * @return
     */
    private SampleRpcClient createClient()
    {
        return createClient(rpcServer.getHostname(),
                            rpcServer.getPort(),
                            "test",
                            "password");
    }
    /**
     * 
     *
     *
     * @param inHostname
     * @param inPort
     * @param inUsername
     * @param inPassword
     * @return
     */
    private SampleRpcClient createClient(String inHostname,
                                         int inPort,
                                         String inUsername,
                                         String inPassword)
    {
        SampleRpcClient client = new SampleRpcClient();
        client.setHost(inHostname);
        client.setPort(inPort);
        client.setUsername(inUsername);
        client.setPassword(inPassword);
        client.start();
        clients.add(client);
        return client;
    }
    private void createService()
    {
        rpcService = new SampleRpcService();
        rpcService.setAuthenticator(authenticator);
    }
    /**
     * 
     *
     *
     * @param inHostname
     * @param inPort
     * @param inServices
     * @throws Exception
     */
    private void startServer(String inHostname,
                             int inPort,
                             BindableService...inServices)
            throws Exception
    {
        rpcServer = new RpcServer();
        rpcServer.setHostname(inHostname);
        rpcServer.setPort(inPort);
        if(inServices != null) {
            for(BindableService service : inServices) {
                rpcServer.getServerServiceDefinitions().add(service);
            }
        }
        rpcServer.start();
    }
    /**
     * 
     *
     *
     * @throws Exception
     */
    private void stopServer()
            throws Exception
    {
        if(rpcServer != null) {
            try {
                rpcServer.stop();
            } catch (Exception ignored) {}
            rpcServer = null;
        }
    }
    private final List<SampleRpcClient> clients = new ArrayList<>();
    private SampleRpcService rpcService;
    private RpcServer rpcServer;
    private MockAuthenticator authenticator;
}
