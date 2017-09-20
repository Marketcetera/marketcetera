package org.marketcetera.dataflow.client.rpc;

import org.junit.Before;
import org.marketcetera.dataflow.rpc.DataFlowContextClassProvider;
import org.marketcetera.dataflow.rpc.DataFlowRpcServiceGrpc;
import org.marketcetera.dataflow.server.rpc.DataFlowRpcService;
import org.marketcetera.rpc.RpcTestBase;
import org.marketcetera.rpc.client.RpcClientFactory;
import org.marketcetera.util.ws.tags.SessionId;

/* $License$ */

/**
 * Tests {@link DataFlowRpcClient}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: RpcSAClientImplTest.java 17223 2016-08-31 01:03:01Z colin $
 * @since 2.4.0
 */
public class DataFlowRpcClientTest
        extends RpcTestBase<DataFlowRpcClientParameters,DataFlowRpcClient,SessionId,DataFlowRpcServiceGrpc.DataFlowRpcServiceImplBase,DataFlowRpcService<SessionId>>
{
    /**
     * Runs before each test.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Before
    public void setup()
            throws Exception
    {
        super.setup();
        client = createClient();
    }
//    /**
//     * Tests disconnection and reconnection.
//     *
//     * @throws Exception if an unexpected error occurs
//     */
//    @Test
//    public void testDisconnection()
//            throws Exception
//    {
//        final AtomicBoolean status = new AtomicBoolean(false);
//        ConnectionStatusListener statusListener = new ConnectionStatusListener() {
//            @Override
//            public void receiveConnectionStatus(boolean inStatus)
//            {
//                status.set(inStatus);
//            }
//        };
//        client.addConnectionStatusListener(statusListener);
//        assertTrue(status.get());
//        // kill the server
//        rpcServer.stop();
//        assertFalse(rpcServer.isRunning());
//        MarketDataFeedTestBase.wait(new Callable<Boolean>() {
//            @Override
//            public Boolean call()
//                    throws Exception
//            {
//                return !client.isRunning();
//            }
//        });
//        assertFalse(status.get());
//        rpcServer.start();
//        assertTrue(rpcServer.isRunning());
//        MarketDataFeedTestBase.wait(new Callable<Boolean>() {
//            @Override
//            public Boolean call()
//                    throws Exception
//            {
//                return client.isRunning();
//            }
//        });
//        assertTrue(status.get());
//    }
//    /**
//     * Tests {@link RpcSAClientImpl#getProviders()}.
//     *
//     * @throws Exception if an unexpected error occurs
//     */
//    @Test
//    public void testGetProviders()
//            throws Exception
//    {
//        List<ModuleURN> providersToReturn = serviceAdapter.getProvidersToReturn();
//        assertTrue(providersToReturn.isEmpty());
//        assertEquals(0,
//                     serviceAdapter.getProvidersCount().get());
//        List<ModuleURN> providers = client.getProviders();
//        assertEquals(providersToReturn,
//                     providers);
//        assertEquals(1,
//                     serviceAdapter.getProvidersCount().get());
//        providersToReturn.add(new ModuleURN("this:is:provider"));
//        providersToReturn.add(new ModuleURN("this:is:otherprovider"));
//        providers = client.getProviders();
//        assertEquals(providersToReturn,
//                     providers);
//        assertEquals(2,
//                     serviceAdapter.getProvidersCount().get());
//    }
//    /**
//     * Tests {@link RpcSAClientImpl#getInstances()}.
//     *
//     * @throws Exception if an unexpected error occurs
//     */
//    @Test
//    public void testGetInstances()
//            throws Exception
//    {
//        List<ModuleURN> instancesToReturn = serviceAdapter.getInstancesToReturn();
//        assertTrue(instancesToReturn.isEmpty());
//        assertTrue(serviceAdapter.getInstancesRequests().isEmpty());
//        ModuleURN provider = new ModuleURN("this:is:provider");
//        List<ModuleURN> instances = client.getInstances(provider);
//        assertEquals(instancesToReturn,
//                     instances);
//        assertEquals(1,
//                     serviceAdapter.getInstancesRequests().size());
//        instancesToReturn.add(new ModuleURN("this:is:instance:first"));
//        instancesToReturn.add(new ModuleURN("this:is:instance:second"));
//        instances = client.getInstances(provider);
//        assertEquals(instancesToReturn,
//                     instances);
//        assertEquals(2,
//                     serviceAdapter.getInstancesRequests().size());
//    }
//    /**
//     * Tests {@link RpcSAClientImpl#getModuleInfo(ModuleURN)}.
//     *
//     * @throws Exception if an unexpected error occurs
//     */
//    @Test
//    public void testGetModuleInfo()
//            throws Exception
//    {
//        assertNull(serviceAdapter.getModuleInfoToReturn());
//        assertTrue(serviceAdapter.getModuleInfoRequests().isEmpty());
//        ModuleURN instance = new ModuleURN("this:is:instance:first");
//        ModuleInfo info = client.getModuleInfo(instance);
//        assertNull(info);
//        ModuleInfo infoToReturn = new ModuleInfo(instance,
//                                                 ModuleState.CREATED,
//                                                 new DataFlowID[0],
//                                                 new DataFlowID[] { new DataFlowID("some-data-flow-id"), new DataFlowID("some-other-data-flow-id") },
//                                                 new Date(),
//                                                 new Date(),
//                                                 new Date(),
//                                                 true,
//                                                 false,
//                                                 true,
//                                                 false,
//                                                 true,
//                                                 null,
//                                                 "some failure",
//                                                 Integer.MAX_VALUE,
//                                                 true,
//                                                 0);
//        serviceAdapter.setModuleInfoToReturn(infoToReturn);
//        ModuleInfo returnedInfo = client.getModuleInfo(instance);
//        assertNotNull(returnedInfo);
//    }
//    /**
//     * Tests {@link RpcSAClientImpl#start(ModuleURN)}.
//     *
//     * @throws Exception if an unexpected error occurs
//     */
//    @Test
//    public void testStart()
//            throws Exception
//    {
//        assertTrue(serviceAdapter.getStartRequests().isEmpty());
//        ModuleURN instance = new ModuleURN("this:is:instance:first");
//        client.start(instance);
//        assertEquals(1,
//                     serviceAdapter.getStartRequests().size());
//    }
//    /**
//     * Tests {@link RpcSAClientImpl#stop(ModuleURN)}.
//     *
//     * @throws Exception if an unexpected error occurs
//     */
//    @Test
//    public void testStop()
//            throws Exception
//    {
//        assertTrue(serviceAdapter.getStopRequests().isEmpty());
//        ModuleURN instance = new ModuleURN("this:is:instance:first");
//        client.stop(instance);
//        assertEquals(1,
//                     serviceAdapter.getStopRequests().size());
//    }
//    /**
//     * Tests {@link RpcSAClientImpl#delete(ModuleURN)}.
//     *
//     * @throws Exception if an unexpected error occurs
//     */
//    @Test
//    public void testDelete()
//            throws Exception
//    {
//        assertTrue(serviceAdapter.getDeleteRequests().isEmpty());
//        ModuleURN instance = new ModuleURN("this:is:instance:first");
//        client.delete(instance);
//        assertEquals(1,
//                     serviceAdapter.getDeleteRequests().size());
//    }
//    /**
//     * Tests {@link RpcSAClientImpl#getProperties(ModuleURN)}.
//     *
//     * @throws Exception if an unexpected error occurs
//     */
//    @Test
//    public void testGetProperties()
//            throws Exception
//    {
//        Map<String,Object> propertiesToReturn = serviceAdapter.getPropertiesToReturn();
//        assertTrue(propertiesToReturn.isEmpty());
//        ModuleURN instance = new ModuleURN("this:is:instance:first");
//        Map<String,Object> returnedProperties = client.getProperties(instance);
//        assertEquals(propertiesToReturn,
//                     returnedProperties);
//        propertiesToReturn.put("key1",
//                               "value1");
//        propertiesToReturn.put("key2",
//                               "value2");
//        returnedProperties = client.getProperties(instance);
//        assertEquals(propertiesToReturn,
//                     returnedProperties);
//    }
//    /**
//     * Tests {@link RpcSAClientImpl#setProperties(ModuleURN, Map)}.
//     *
//     * @throws Exception if an unexpected error occurs
//     */
//    @Test
//    public void testSetProperties()
//            throws Exception
//    {
//        Map<String,Object> propertiesToReturn = serviceAdapter.getPropertiesToReturn();
//        assertTrue(propertiesToReturn.isEmpty());
//        ModuleURN instance = new ModuleURN("this:is:instance:first");
//        Map<String,Object> returnedProperties = client.setProperties(instance,
//                                                                     propertiesToReturn);
//        assertEquals(propertiesToReturn,
//                     returnedProperties);
//        propertiesToReturn.put("key1",
//                               "value1");
//        propertiesToReturn.put("key2",
//                               "value2");
//        returnedProperties = client.setProperties(instance,
//                                                  propertiesToReturn);
//        assertEquals(propertiesToReturn,
//                     returnedProperties);
//    }
//    /**
//     * Tests {@link RpcSAClientImpl#sendData(Object)}.
//     *
//     * @throws Exception if an unexpected error occurs
//     */
//    @Test
//    public void testSendData()
//            throws Exception
//    {
//        assertNull(serviceAdapter.getSentData());
//        new ExpectedFailure<RuntimeException>() {
//            @Override
//            protected void run()
//                    throws Exception
//            {
//                client.sendData(this);
//            }
//        };
//        String data = "Lorum ipsum";
//        client.sendData(data);
//        assertEquals(data,
//                     serviceAdapter.getSentData());
//    }
    /* (non-Javadoc)
     * @see org.marketcetera.rpc.RpcTestBase#createTestService()
     */
    @Override
    protected DataFlowRpcService<SessionId> createTestService()
    {
        DataFlowRpcService<SessionId> service = new DataFlowRpcService<>();
        return service;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.rpc.RpcTestBase#getRpcClientFactory()
     */
    @Override
    protected RpcClientFactory<DataFlowRpcClientParameters,DataFlowRpcClient> getRpcClientFactory()
    {
        return new DataFlowRpcClientFactory();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.rpc.RpcTestBase#getClientParameters(java.lang.String, int, java.lang.String, java.lang.String)
     */
    @Override
    protected DataFlowRpcClientParameters getClientParameters(String inHostname,
                                                                   int inPort,
                                                                   String inUsername,
                                                                   String inPassword)
    {
        DataFlowRpcClientParameters parameters = new DataFlowRpcClientParameters();
        parameters.setContextClassProvider(DataFlowContextClassProvider.INSTANCE);
        parameters.setHostname(inHostname);
        parameters.setPassword(inPassword);
        parameters.setPort(inPort);
        parameters.setUsername(inUsername);
        return parameters;
    }
    /**
     * test client value
     */
    private DataFlowRpcClient client;
}
