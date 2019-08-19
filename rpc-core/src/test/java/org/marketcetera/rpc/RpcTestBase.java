package org.marketcetera.rpc;

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
import org.marketcetera.rpc.client.RpcClient;
import org.marketcetera.rpc.client.RpcClientFactory;
import org.marketcetera.rpc.client.RpcClientParameters;
import org.marketcetera.rpc.server.AbstractRpcService;
import org.marketcetera.rpc.server.RpcServer;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.ws.stateful.SessionManager;
import org.springframework.util.SocketUtils;

import io.grpc.BindableService;
import io.grpc.StatusRuntimeException;

/* $License$ */

/**
 * Provides common tests and behaviors for RPC clients and servers.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public abstract class RpcTestBase<RpcClientParametersClazz extends RpcClientParameters,
                                  RpcClientClazz extends RpcClient,
                                  SessionClazz,
                                  ServiceClazz extends BindableService,
                                  RpcServiceClazz extends AbstractRpcService<SessionClazz,ServiceClazz>>
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
        for(RpcClientClazz client : clients) {
            try {
                client.stop();
            } catch (Exception e) {
                SLF4JLoggerProxy.warn(this,
                                      e);
            }
        }
        clients.clear();
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
        new ExpectedFailure<StatusRuntimeException>("UNAVAILABLE: Unable to resolve host not-a-valid-host") {
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
        new ExpectedFailure<StatusRuntimeException>() {
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
        final RpcClientClazz client = createClient();
        assertTrue(client.isRunning());
        MarketDataFeedTestBase.wait(new Callable<Boolean>() {
            @Override
            public Boolean call()
                    throws Exception
            {
                return client.isRunning();
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
        final RpcClientClazz client = createClient();
        MarketDataFeedTestBase.wait(new Callable<Boolean>() {
            @Override
            public Boolean call()
                    throws Exception
            {
                return client.isRunning();
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
        final List<RpcClientClazz> multipleClients = new ArrayList<>();
        for(int i=0;i<10;i++) {
            executorService.submit(new Runnable() {
                @Override
                public void run()
                {
                    try {
                        final RpcClientClazz client = createClient();
                        multipleClients.add(client);
                        Thread.sleep(1000);
                        MarketDataFeedTestBase.wait(new Callable<Boolean>() {
                            @Override
                            public Boolean call()
                                    throws Exception
                            {
                                return client.isRunning();
                            }
                        });
                        client.stop();
                        multipleClients.remove(client);
                    } catch (Exception e) {
                        SLF4JLoggerProxy.warn(RpcTestBase.this,
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
     * Create a service class instance.
     *
     * @return a <code>RpcServiceClazz</code> value
     */
    protected abstract RpcServiceClazz createTestService();
    /**
     * Get a client factory instance.
     *
     * @return a <code>RpcClientFactory&lt;RpcClientParametersClazz,RpcClientClazz&;gt;</code> value
     */
    protected abstract RpcClientFactory<RpcClientParametersClazz,RpcClientClazz> getRpcClientFactory();
    /**
     * Set up and start a client with default attributes.
     *
     * @return an <code>AbstractRpcClient&lt;?,?&gt;</code> value
     * @throws Exception if an unexpected error occurs
     */
    protected RpcClientClazz createClient()
            throws Exception
    {
        return createClient(rpcServer.getHostname(),
                            rpcServer.getPort(),
                            "test",
                            "password");
    }
    /**
     * Get the client parameters to create the client.
     *
     * @param inHostname a <code>String</code> value
     * @param inPort an <code>int</code> value
     * @param inUsername a <code>String</code> value
     * @param inPassword a <code>String</code> value
     * @return a <code>RpcClientParametersClazz</code> value
     */
    protected abstract RpcClientParametersClazz getClientParameters(String inHostname,
                                                                    int inPort,
                                                                    String inUsername,
                                                                    String inPassword);
    /**
     * Set up and start a client with the given attributes.
     *
     * @param inHostname a <code>String</code> value
     * @param inPort an <code>int</code> value
     * @param inUsername a <code>String</code> value
     * @param inPassword a <code>String<code> value
     * @return a <code>RpcClientClazz</code> value
     * @throws Exception if an unexpected error occurs
     */
    protected RpcClientClazz createClient(String inHostname,
                                          int inPort,
                                          String inUsername,
                                          String inPassword)
            throws Exception
    {
        RpcClientFactory<RpcClientParametersClazz,RpcClientClazz> factory = getRpcClientFactory();
        RpcClientClazz client = factory.create(getClientParameters(inHostname,
                                                                   inPort,
                                                                   inUsername,
                                                                   inPassword));
        prepareClient(client);
        client.start();
        clients.add(client);
        return client;
    }
    /**
     * Perform any necessary steps on the client before starting it.
     *
     * @param inClient a <code>RpcClientClazz</code> value
     */
    protected void prepareClient(RpcClientClazz inClient) {}
    /**
     * Create a test service.
     * 
     * @throws Exception if an unexpected error occurs
     */
    protected void createService()
            throws Exception
    {
        rpcService = createTestService();
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
    protected void startServer(String inHostname,
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
    protected void stopServer()
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
    private final List<RpcClientClazz> clients = new ArrayList<>();
    /**
     * test RPC service
     */
    protected RpcServiceClazz rpcService;
    /**
     * test RPC server
     */
    protected RpcServer rpcServer;
    /**
     * test authenticator
     */
    protected MockAuthenticator authenticator;
    /**
     * manages sessions
     */
    protected SessionManager<SessionClazz> sessionManager;
}
