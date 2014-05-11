package org.marketcetera.saclient.rpc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.marketcetera.core.LoggerConfiguration;
import org.marketcetera.marketdata.MarketDataFeedTestBase;
import org.marketcetera.module.DataFlowID;
import org.marketcetera.module.ExpectedFailure;
import org.marketcetera.module.ModuleInfo;
import org.marketcetera.module.ModuleState;
import org.marketcetera.module.ModuleURN;
import org.marketcetera.saclient.ConnectionException;
import org.marketcetera.saclient.ConnectionStatusListener;
import org.marketcetera.saclient.CreateStrategyParameters;
import org.marketcetera.saclient.SAClientParameters;
import org.marketcetera.trade.TradeContextClassProvider;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.rpc.RpcServer;
import org.marketcetera.util.ws.stateful.SessionManager;

/* $License$ */

/**
 * Tests {@link RpcSAClientImpl}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 2.4.0
 */
public class RpcSAClientImplTest
{
    /**
     * Runs once before all tests.
     *
     * @throws Exception if an unexpected error occurs
     */
    @BeforeClass
    public static void once()
            throws Exception
    {
        LoggerConfiguration.logSetup();
    }
    /**
     * Runs before each test.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Before
    public void setup()
            throws Exception
    {
        stopClientServer();
        hostname = "127.0.0.1";
        port = -1;
        sessionManager = new SessionManager<MockSession>();
        authenticator = new MockAuthenticator();
        serviceAdapter = new MockSAClientServiceAdapter();
        startClientServer();
        assertTrue(server.isRunning());
        assertTrue(client.isRunning());
    }
    /**
     * Runs after each test.
     *
     * @throws Exception if an unexpected error occurs
     */
    @After
    public void after()
            throws Exception
    {
        stopClientServer();
    }
    /**
     * Tests disconnection and reconnection.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testDisconnection()
            throws Exception
    {
        final AtomicBoolean status = new AtomicBoolean(false);
        ConnectionStatusListener statusListener = new ConnectionStatusListener() {
            @Override
            public void receiveConnectionStatus(boolean inStatus)
            {
                status.set(inStatus);
            }
        };
        client.addConnectionStatusListener(statusListener);
        assertTrue(status.get());
        // kill the server
        server.stop();
        assertFalse(server.isRunning());
        MarketDataFeedTestBase.wait(new Callable<Boolean>() {
            @Override
            public Boolean call()
                    throws Exception
            {
                return !client.isRunning();
            }
        });
        assertFalse(status.get());
        server.start();
        assertTrue(server.isRunning());
        MarketDataFeedTestBase.wait(new Callable<Boolean>() {
            @Override
            public Boolean call()
                    throws Exception
            {
                return client.isRunning();
            }
        });
        assertTrue(status.get());
    }
    /**
     * Tests {@link RpcSAClientImpl#getProviders()}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testGetProviders()
            throws Exception
    {
        List<ModuleURN> providersToReturn = serviceAdapter.getProvidersToReturn();
        assertTrue(providersToReturn.isEmpty());
        assertEquals(0,
                     serviceAdapter.getProvidersCount().get());
        List<ModuleURN> providers = client.getProviders();
        assertEquals(providersToReturn,
                     providers);
        assertEquals(1,
                     serviceAdapter.getProvidersCount().get());
        providersToReturn.add(new ModuleURN("this:is:provider"));
        providersToReturn.add(new ModuleURN("this:is:otherprovider"));
        providers = client.getProviders();
        assertEquals(providersToReturn,
                     providers);
        assertEquals(2,
                     serviceAdapter.getProvidersCount().get());
    }
    /**
     * Tests {@link RpcSAClientImpl#getInstances()}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testGetInstances()
            throws Exception
    {
        List<ModuleURN> instancesToReturn = serviceAdapter.getInstancesToReturn();
        assertTrue(instancesToReturn.isEmpty());
        assertTrue(serviceAdapter.getInstancesRequests().isEmpty());
        ModuleURN provider = new ModuleURN("this:is:provider");
        List<ModuleURN> instances = client.getInstances(provider);
        assertEquals(instancesToReturn,
                     instances);
        assertEquals(1,
                     serviceAdapter.getInstancesRequests().size());
        instancesToReturn.add(new ModuleURN("this:is:instance:first"));
        instancesToReturn.add(new ModuleURN("this:is:instance:second"));
        instances = client.getInstances(provider);
        assertEquals(instancesToReturn,
                     instances);
        assertEquals(2,
                     serviceAdapter.getInstancesRequests().size());
    }
    /**
     * Tests {@link RpcSAClientImpl#getModuleInfo(ModuleURN)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testGetModuleInfo()
            throws Exception
    {
        assertNull(serviceAdapter.getModuleInfoToReturn());
        assertTrue(serviceAdapter.getModuleInfoRequests().isEmpty());
        ModuleURN instance = new ModuleURN("this:is:instance:first");
        ModuleInfo info = client.getModuleInfo(instance);
        assertNull(info);
        ModuleInfo infoToReturn = new ModuleInfo(instance,
                                                 ModuleState.CREATED,
                                                 new DataFlowID[0],
                                                 new DataFlowID[] { new DataFlowID("some-data-flow-id"), new DataFlowID("some-other-data-flow-id") },
                                                 new Date(),
                                                 new Date(),
                                                 new Date(),
                                                 true,
                                                 false,
                                                 true,
                                                 false,
                                                 true,
                                                 null,
                                                 "some failure",
                                                 Integer.MAX_VALUE,
                                                 true,
                                                 0);
        serviceAdapter.setModuleInfoToReturn(infoToReturn);
        ModuleInfo returnedInfo = client.getModuleInfo(instance);
        assertNotNull(returnedInfo);
    }
    /**
     * Tests {@link RpcSAClientImpl#start(ModuleURN)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testStart()
            throws Exception
    {
        assertTrue(serviceAdapter.getStartRequests().isEmpty());
        ModuleURN instance = new ModuleURN("this:is:instance:first");
        client.start(instance);
        assertEquals(1,
                     serviceAdapter.getStartRequests().size());
    }
    /**
     * Tests {@link RpcSAClientImpl#stop(ModuleURN)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testStop()
            throws Exception
    {
        assertTrue(serviceAdapter.getStopRequests().isEmpty());
        ModuleURN instance = new ModuleURN("this:is:instance:first");
        client.stop(instance);
        assertEquals(1,
                     serviceAdapter.getStopRequests().size());
    }
    /**
     * Tests {@link RpcSAClientImpl#delete(ModuleURN)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testDelete()
            throws Exception
    {
        assertTrue(serviceAdapter.getDeleteRequests().isEmpty());
        ModuleURN instance = new ModuleURN("this:is:instance:first");
        client.delete(instance);
        assertEquals(1,
                     serviceAdapter.getDeleteRequests().size());
    }
    /**
     * Tests {@link RpcSAClientImpl#getProperties(ModuleURN)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testGetProperties()
            throws Exception
    {
        Map<String,Object> propertiesToReturn = serviceAdapter.getPropertiesToReturn();
        assertTrue(propertiesToReturn.isEmpty());
        ModuleURN instance = new ModuleURN("this:is:instance:first");
        Map<String,Object> returnedProperties = client.getProperties(instance);
        assertEquals(propertiesToReturn,
                     returnedProperties);
        propertiesToReturn.put("key1",
                               "value1");
        propertiesToReturn.put("key2",
                               "value2");
        returnedProperties = client.getProperties(instance);
        assertEquals(propertiesToReturn,
                     returnedProperties);
    }
    /**
     * Tests {@link RpcSAClientImpl#setProperties(ModuleURN, Map)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testSetProperties()
            throws Exception
    {
        Map<String,Object> propertiesToReturn = serviceAdapter.getPropertiesToReturn();
        assertTrue(propertiesToReturn.isEmpty());
        ModuleURN instance = new ModuleURN("this:is:instance:first");
        Map<String,Object> returnedProperties = client.setProperties(instance,
                                                                     propertiesToReturn);
        assertEquals(propertiesToReturn,
                     returnedProperties);
        propertiesToReturn.put("key1",
                               "value1");
        propertiesToReturn.put("key2",
                               "value2");
        returnedProperties = client.setProperties(instance,
                                                  propertiesToReturn);
        assertEquals(propertiesToReturn,
                     returnedProperties);
    }
    /**
     * Tests {@link RpcSAClientImpl#createStrategy(org.marketcetera.saclient.CreateStrategyParameters)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testCreateStrategy()
            throws Exception
    {
        assertTrue(serviceAdapter.getCreateStrategyRequests().isEmpty());
        CreateStrategyParameters parameters = new CreateStrategyParameters("instance",
                                                                           "strategy",
                                                                           "language",
                                                                           generateStrategySource(),
                                                                           "key=value:key1=value1:key2=value2",
                                                                           true);
        ModuleURN instance = new ModuleURN("this:is:my:instance");
        serviceAdapter.setCreateModuleURNToReturn(instance);
        instance = client.createStrategy(parameters);
        assertNotNull(instance);
    }
    /**
     * Tests {@link RpcSAClientImpl#getStrategyCreateParms(ModuleURN)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testGetStrategyCreateParms()
            throws Exception
    {
        ModuleURN instance = new ModuleURN("this:is:my:instance");
        CreateStrategyParameters parameters = new CreateStrategyParameters("instance",
                                                                           "strategy",
                                                                           "language",
                                                                           generateStrategySource(),
                                                                           "key=value:key1=value1:key2=value2",
                                                                           true);
        serviceAdapter.setParametersToReturn(parameters);
        parameters = client.getStrategyCreateParms(instance);
        assertNotNull(parameters);
    }
    /**
     * Tests {@link RpcSAClientImpl#sendData(Object)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testSendData()
            throws Exception
    {
        assertNull(serviceAdapter.getSentData());
        new ExpectedFailure<ConnectionException>() {
            @Override
            protected void run()
                    throws Exception
            {
                client.sendData(this);
            }
        };
        String data = "Lorum ipsum";
        client.sendData(data);
        assertEquals(data,
                     serviceAdapter.getSentData());
    }
    /**
     * Generates a file with strategy source.
     *
     * @return a <code>File</cod> value
     * @throws IOException if an error occurs
     */
    private File generateStrategySource()
            throws IOException
    {
        File tmpFile = File.createTempFile("src",
                                           "txt");
        tmpFile.deleteOnExit();
        FileUtils.writeStringToFile(tmpFile,
                                    "Lorum ipsum");
        return tmpFile;
    }
    /**
     * Stops the test client and server.
     *
     * @throws Exception if an unexpected error occurs
     */
    private void stopClientServer()
            throws Exception
    {
        if(client != null && client.isRunning()) {
            try {
                client.stop();
            } catch (Exception e) {
                SLF4JLoggerProxy.warn(this,
                                      e);
            }
        }
        client = null;
        if(server != null && server.isRunning()) {
            try {
                server.stop();
            } catch (Exception e) {
                SLF4JLoggerProxy.warn(this,
                                      e);
            }
        }
        server = null;
    }
    /**
     * Starts the test client server and client.
     *
     * @throws Exception if an unexpected error occurs
     */
    private void startClientServer()
            throws Exception
    {
        server = new RpcServer<MockSession>();
        server.setHostname(hostname);
        if(port == -1) {
            port = assignPort();
        }
        server.setPort(port);
        server.setSessionManager(sessionManager);
        server.setAuthenticator(authenticator);
        server.setContextClassProvider(TradeContextClassProvider.INSTANCE);
        SAClientRpcService<MockSession> service = new SAClientRpcService<>();
        server.getServiceSpecs().add(service);
        service.setServiceAdapter(serviceAdapter);
        server.setContextClassProvider(SAClientContextClassProvider.INSTANCE);
        server.start();
        client = new RpcSAClientFactory().create(new SAClientParameters("username",
                                                                        "password".toCharArray(),
                                                                        "",
                                                                        hostname,
                                                                        port,
                                                                        null,
                                                                        false));
        client.setContextClassProvider(SAClientContextClassProvider.INSTANCE);
        client.start();
    }
    /**
     * Assigns a port value that is not in use.
     * 
     * @return an <code>int</code> value
     */
    private int assignPort()
    {
        for(int i=MIN_PORT_NUMBER;i<=MAX_PORT_NUMBER;i++) {
            try(ServerSocket ss = new ServerSocket(i)) {
                ss.setReuseAddress(true);
                try(DatagramSocket ds = new DatagramSocket(i)) {
                    ds.setReuseAddress(true);
                    return i;
                }
            } catch (IOException e) {}
        }
        throw new IllegalArgumentException("No available ports");
    }
    /**
     * test service adapter value
     */
    private MockSAClientServiceAdapter serviceAdapter;
    /**
     * test authenticator value
     */
    private MockAuthenticator authenticator;
    /**
     * test session manager value
     */
    private SessionManager<MockSession> sessionManager;
    /**
     * test hostname
     */
    private String hostname;
    /**
     * test port
     */
    private int port;
    /**
     * minimum test port value
     */
    private static final int MIN_PORT_NUMBER = 10000;
    /**
     * maximum test port value
     */
    private static final int MAX_PORT_NUMBER = 65535;
    /**
     * test client value
     */
    private RpcSAClientImpl client;
    /**
     * tests server value
     */
    private RpcServer<MockSession> server;
}
