package org.marketcetera.eventbus.test.rpc;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.marketcetera.eventbus.data.event.DataEventRpcClient;
import org.marketcetera.eventbus.data.event.DataEventRpcClientFactory;
import org.marketcetera.eventbus.data.event.DataEventRpcClientParameters;
import org.marketcetera.eventbus.data.event.DataEventRpcServer;
import org.marketcetera.eventbus.data.event.DataEventRpcServiceGrpc;
import org.marketcetera.eventbus.test.EventBusTestConfiguration;
import org.marketcetera.rpc.RpcTestBase;
import org.marketcetera.util.ws.tags.SessionId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit4.rules.SpringClassRule;
import org.springframework.test.context.junit4.rules.SpringMethodRule;

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
{
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
    @Autowired
    private DataEventRpcServer<SessionId> dataEventRpcServer;
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
