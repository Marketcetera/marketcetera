package org.marketcetera.strategyengine.client.rpc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.marketcetera.marketdata.MarketDataFeedTestBase;
import org.marketcetera.module.DataFlowID;
import org.marketcetera.module.ExpectedFailure;
import org.marketcetera.module.ModuleInfo;
import org.marketcetera.module.ModuleState;
import org.marketcetera.module.ModuleURN;
import org.marketcetera.rpc.RpcTestBase;
import org.marketcetera.rpc.client.RpcClientFactory;
import org.marketcetera.saclient.rpc.SAClientServiceRpcGrpc;
import org.marketcetera.strategyengine.client.ConnectionStatusListener;
import org.marketcetera.strategyengine.client.CreateStrategyParameters;
import org.marketcetera.strategyengine.client.server.rpc.StrategyAgentRpcService;
import org.marketcetera.util.ws.tags.SessionId;

/* $License$ */

/**
 * Tests {@link RpcSAClientImpl}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: RpcSAClientImplTest.java 17223 2016-08-31 01:03:01Z colin $
 * @since 2.4.0
 */
public class RpcSAClientImplTest
        extends RpcTestBase<StrategyAgentRpcClientParameters,StrategyAgentRpcClient,SessionId,SAClientServiceRpcGrpc.SAClientServiceRpcImplBase,StrategyAgentRpcService<SessionId>>
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
        serviceAdapter = new MockSAClientServiceAdapter();
        super.setup();
        client = createClient();
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
        rpcServer.stop();
        assertFalse(rpcServer.isRunning());
        MarketDataFeedTestBase.wait(new Callable<Boolean>() {
            @Override
            public Boolean call()
                    throws Exception
            {
                return !client.isRunning();
            }
        });
        assertFalse(status.get());
        rpcServer.start();
        assertTrue(rpcServer.isRunning());
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
     * Tests {@link RpcSAClientImpl#createStrategy(org.marketcetera.strategyengine.client.CreateStrategyParameters)}.
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
        new ExpectedFailure<RuntimeException>() {
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
    /* (non-Javadoc)
     * @see org.marketcetera.rpc.RpcTestBase#createTestService()
     */
    @Override
    protected StrategyAgentRpcService<SessionId> createTestService()
    {
        StrategyAgentRpcService<SessionId> service = new StrategyAgentRpcService<>();
        service.setServiceAdapter(serviceAdapter);
        service.setContextClassProvider(StrategyAgentClientContextClassProvider.INSTANCE);
        return service;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.rpc.RpcTestBase#getRpcClientFactory()
     */
    @Override
    protected RpcClientFactory<StrategyAgentRpcClientParameters,StrategyAgentRpcClient> getRpcClientFactory()
    {
        return new StrategyAgentRpcClientFactory();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.rpc.RpcTestBase#getClientParameters(java.lang.String, int, java.lang.String, java.lang.String)
     */
    @Override
    protected StrategyAgentRpcClientParameters getClientParameters(String inHostname,
                                                                   int inPort,
                                                                   String inUsername,
                                                                   String inPassword)
    {
        StrategyAgentRpcClientParameters parameters = new StrategyAgentRpcClientParameters();
        parameters.setContextClassProvider(StrategyAgentClientContextClassProvider.INSTANCE);
        parameters.setHostname(inHostname);
        parameters.setPassword(inPassword);
        parameters.setPort(inPort);
        parameters.setUsername(inUsername);
        return parameters;
    }
    /**
     * test service adapter value
     */
    private MockSAClientServiceAdapter serviceAdapter;
    /**
     * test client value
     */
    private StrategyAgentRpcClient client;
}
