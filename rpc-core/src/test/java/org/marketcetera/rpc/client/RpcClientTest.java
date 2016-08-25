package org.marketcetera.rpc.client;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.marketcetera.marketdata.MarketDataFeedTestBase;
import org.marketcetera.module.ExpectedFailure;
import org.marketcetera.rpc.MockAuthenticator;
import org.marketcetera.rpc.sample.SampleRpcService;
import org.marketcetera.rpc.sample.client.SampleRpcClient;
import org.marketcetera.rpc.server.RpcServer;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.ws.stateful.SessionManager;
import org.marketcetera.util.ws.tags.SessionId;
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
        sessionManager = new SessionManager<>();
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
     * Test that multiple clients are supported with the same credentials.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testMultipleClients()
            throws Exception
    {
        ExecutorService executorService = Executors.newCachedThreadPool();
        final List<Exception> exceptions = new ArrayList<>();
        final List<SampleRpcClient> multipleClients = new ArrayList<>();
        for(int i=0;i<10;i++) {
            executorService.submit(new Runnable() {
                @Override
                public void run()
                {
                    try {
                        final SampleRpcClient client = createClient();
                        multipleClients.add(client);
                        assertTrue(client.isRunning());
                        MarketDataFeedTestBase.wait(new Callable<Boolean>() {
                            @Override
                            public Boolean call()
                                    throws Exception
                            {
                                return client.getHeartbeatCount() >= 5;
                            }
                        });
                        client.stop();
                        multipleClients.remove(client);
                    } catch (Exception e) {
                        SLF4JLoggerProxy.warn(RpcClientTest.this,
                                              e);
                        exceptions.add(e);
                    }
                }
            });
        }
        MarketDataFeedTestBase.wait(new Callable<Boolean>() {
            @Override
            public Boolean call()
                    throws Exception
            {
                return !multipleClients.isEmpty();
            }
        });
        MarketDataFeedTestBase.wait(new Callable<Boolean>() {
            @Override
            public Boolean call()
                    throws Exception
            {
                return multipleClients.isEmpty();
            }
        });
        assertTrue(exceptions.isEmpty());
    }
    /**
     * Create and start a client with default attributes.
     *
     * @return a <code>SampleRpcClient</code> value
     */
    private SampleRpcClient createClient()
    {
        return createClient(rpcServer.getHostname(),
                            rpcServer.getPort(),
                            "test",
                            "password");
    }
    /**
     * Create and start a client with the given attributes.
     *
     * @param inHostname a <code>String</code> value
     * @param inPort an <code>int</code> value
     * @param inUsername a <code>String</code> value
     * @param inPassword a <code>String<code> value
     * @return a <code>SampleRpcClient</code> value
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
    /**
     * Create a test service.
     */
    private void createService()
    {
        rpcService = new SampleRpcService<>();
        rpcService.setAuthenticator(authenticator);
        rpcService.setSessionManager(sessionManager);
        rpcService.start();
    }
    /**
     * Create and start a test server with the given attributes.
     *
     * @param inHostname a <code>String</code> value
     * @param inPort an <code>int</code> value
     * @param inServices a <code>BindableService[]</code> value
     * @throws Exception if an unexpected error occurs
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
     * Stop the test server.
     *
     * @throws Exception if an unexpected error occurs
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
    /**
     * tracks the clients successfully started during the test session
     */
    private final List<SampleRpcClient> clients = new ArrayList<>();
    /**
     * test RPC service
     */
    private SampleRpcService<SessionId> rpcService;
    /**
     * test RPC server
     */
    private RpcServer rpcServer;
    /**
     * test authenticator
     */
    private MockAuthenticator authenticator;
    /**
     * 
     */
    private SessionManager<SessionId> sessionManager;
}
