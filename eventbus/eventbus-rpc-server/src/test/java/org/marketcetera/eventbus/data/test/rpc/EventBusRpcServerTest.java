package org.marketcetera.eventbus.data.test.rpc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import org.assertj.core.util.Lists;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.marketcetera.core.PlatformServices;
import org.marketcetera.eventbus.EventBusService;
import org.marketcetera.eventbus.data.event.DataEvent;
import org.marketcetera.eventbus.data.event.DataEventRpcClient;
import org.marketcetera.eventbus.data.event.DataEventRpcClientFactory;
import org.marketcetera.eventbus.data.event.DataEventRpcClientParameters;
import org.marketcetera.eventbus.data.event.DataEventRpcServer;
import org.marketcetera.eventbus.data.event.DataEventRpcServiceGrpc;
import org.marketcetera.eventbus.test.EventBusTestConfiguration;
import org.marketcetera.rpc.RpcTestBase;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.ws.tags.SessionId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit4.rules.SpringClassRule;
import org.springframework.test.context.junit4.rules.SpringMethodRule;

import com.google.common.eventbus.Subscribe;

import junitparams.JUnitParamsRunner;

/* $License$ */

/**
 * Tests the data event RPC client and server.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 2.4.0
 */
@RunWith(JUnitParamsRunner.class)
@SpringBootTest(classes=EventBusTestConfiguration.class)
@ComponentScan(basePackages={"org.marketcetera"})
@EntityScan(basePackages={"org.marketcetera"})
public class EventBusRpcServerTest
        extends RpcTestBase<DataEventRpcClientParameters,DataEventRpcClient,SessionId,DataEventRpcServiceGrpc.DataEventRpcServiceImplBase,DataEventRpcServer<SessionId>>
        implements Consumer<DataEvent>
{
    @Before
    public void setup()
            throws Exception
    {
        super.setup();
        eventBusService.register(this);
        rpcEvents.clear();
        busEvents.clear();
    }
    @After
    public void cleanup()
            throws Exception
    {
        try {
            eventBusService.unregister(this);
        } catch (Exception ignored) {}
        super.cleanup();
    }
    @Test
    public void testFilteredRequest()
            throws Exception
    {
        DataEventRpcClient client = createClient();
        String requestId = PlatformServices.generateId();
        client.subscribeToDataEvents(requestId,
                                     Lists.newArrayList(MockDataEventType1.class),
                                     this);
        Thread.sleep(5000);
        MockDataEventType1 sentEvent1 = new MockDataEventType1();
        eventBusService.post(sentEvent1);
        DataEvent event1 = busEvents.pollFirst(10,
                                               TimeUnit.SECONDS);
        DataEvent event2 = rpcEvents.pollFirst(10,
                                               TimeUnit.SECONDS);
        assertTrue(event1 instanceof MockDataEventType1,
                   "Expected: " + MockDataEventType1.class.getSimpleName() + " actual: " + (event1 == null ? "null" : event1.getClass().getSimpleName()));
        assertTrue(event2 instanceof MockDataEventType1,
                   "Expected: " + MockDataEventType1.class.getSimpleName() + " actual: " + (event2 == null ? "null" : event2.getClass().getSimpleName()));
        assertEquals(((AbstractMockDataEvent)event1).getId(),
                     ((AbstractMockDataEvent)event2).getId());
    }
    @Subscribe
    public void acceptEvent(DataEvent inEvent)
    {
        SLF4JLoggerProxy.info(this,
                              "{} received {} via data bus",
                              PlatformServices.getServiceName(getClass()),
                              inEvent);
        if(inEvent instanceof AbstractMockDataEvent) {
            busEvents.addLast((AbstractMockDataEvent)inEvent);
        }
    }
    /* (non-Javadoc)
     * @see java.util.function.Consumer#accept(java.lang.Object)
     */
    @Override
    public void accept(DataEvent inEvent)
    {
        SLF4JLoggerProxy.info(this,
                              "{} received {} via RPC",
                              PlatformServices.getServiceName(getClass()),
                              inEvent);
        rpcEvents.addLast(inEvent);
    }
    private final BlockingDeque<DataEvent> rpcEvents = new LinkedBlockingDeque<>();
    private final BlockingDeque<DataEvent> busEvents = new LinkedBlockingDeque<>();
    /* (non-Javadoc)
     * @see org.marketcetera.rpc.RpcTestBase#getRpcClientFactory()
     */
    @Override
    protected DataEventRpcClientFactory getRpcClientFactory()
    {
        return clientFactory;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.rpc.RpcTestBase#getClientParameters(java.lang.String, int, java.lang.String, java.lang.String)
     */
    @Override
    protected DataEventRpcClientParameters getClientParameters(String inHostname,
                                                               int inPort,
                                                               String inUsername,
                                                               String inPassword)
    {
        DataEventRpcClientParameters parameters = new DataEventRpcClientParameters();
        parameters.setHeartbeatInterval(1000);
        parameters.setHostname(inHostname);
        parameters.setPassword(inPassword);
        parameters.setPort(inPort);
        parameters.setUsername(inUsername);
        return parameters;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.rpc.RpcTestBase#createTestService()
     */
    @Override
    protected DataEventRpcServer<SessionId> createTestService()
    {
        return dataEventRpcServer;
    }
    /**
     * provides the server-side implementation of the Data Event RPC server
     */
    @Autowired
    private DataEventRpcServer<SessionId> dataEventRpcServer;
    @Autowired
    private EventBusService eventBusService;
    /**
     * creates {@link DataEventRpcClient} objects
     */
    @Autowired
    private DataEventRpcClientFactory clientFactory;
    /**
     * test artifact used to identify the current test case
     */
    @Rule
    public TestName name = new TestName();
    /**
     * rule used to load test context
     */
    @ClassRule
    public static final SpringClassRule SCR = new SpringClassRule();
    /**
     * test spring method rule
     */
    @Rule
    public final SpringMethodRule springMethodRule = new SpringMethodRule();
}
