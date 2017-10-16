package org.marketcetera.clienttest;

import java.util.List;

import org.marketcetera.core.PlatformServices;
import org.marketcetera.dataflow.client.DataFlowClient;
import org.marketcetera.dataflow.client.DataReceiver;
import org.marketcetera.dataflow.client.rpc.DataFlowRpcClientFactory;
import org.marketcetera.dataflow.client.rpc.DataFlowRpcClientParameters;
import org.marketcetera.dataflow.modules.DataFlowReceiverModuleFactory;
import org.marketcetera.module.DataFlowID;
import org.marketcetera.module.DataFlowInfo;
import org.marketcetera.module.DataRequest;
import org.marketcetera.module.ModuleURN;
import org.marketcetera.modules.headwater.HeadwaterModuleFactory;
import org.marketcetera.modules.ticktock.TickTockModuleFactory;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.google.common.collect.Lists;

/* $License$ */

/**
 * Demonstrates how to connect to data flow services from an external application.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@SpringBootApplication
@EnableAutoConfiguration
@SpringBootConfiguration
public class DataFlowClientTest
{
    /**
     * Main run method.
     *
     * @param inArgs a <code>String[]</code> value
     */
    public static void main(String[] inArgs)
    {
        SpringApplication.run(DataFlowClientTest.class,
                              inArgs);
        SLF4JLoggerProxy.info(DataFlowClientTest.class,
                              "Starting data flow client test");
        try {
            DataFlowClientTest clientTest = DataFlowClientTest.instance;
            clientTest.runTest();
        } catch (Exception e) {
            PlatformServices.handleException(DataFlowClientTest.class,
                                             "Data Flow Client Test Error",
                                             e);
        }
        SLF4JLoggerProxy.info(DataFlowClientTest.class,
                              "Ending data flow client test");
    }
    /**
     * Run the client test.
     *
     * @throws Exception if an error occurs running the client test
     */
    private void runTest()
            throws Exception
    {
        try {
            DataFlowRpcClientParameters params = new DataFlowRpcClientParameters();
            params.setHostname(hostname);
            params.setPort(port);
            params.setUsername(username);
            params.setPassword(password);
            dataFlowClient = dataFlowClientFactory.create(params);
            dataFlowClient.start();
            SLF4JLoggerProxy.info(this,
                                  "Data flow client connected to {}:{} as {}",
                                  hostname,
                                  port,
                                  username);
            // add a data listener
            DataReceiver dataReceiver = new DataReceiver() {
                @Override
                public void receiveData(Object inObject)
                {
                    SLF4JLoggerProxy.info(DataFlowClientTest.this,
                                          "Received {}",
                                          inObject);
                }
            };
            dataFlowClient.addDataReceiver(dataReceiver);
            SLF4JLoggerProxy.info(this,
                                  "Available providers: {}",
                                  dataFlowClient.getProviders());
            List<ModuleURN> instances = dataFlowClient.getInstances(null);
            SLF4JLoggerProxy.info(this,
                                  "Available instances: {}",
                                  instances);
            for(ModuleURN instanceUrn : instances) {
                SLF4JLoggerProxy.info(this,
                                      "Module info for {}: {}",
                                      instances,
                                      dataFlowClient.getModuleInfo(instanceUrn));
            }
            ModuleURN providerUrn = HeadwaterModuleFactory.PROVIDER_URN;
            String instanceName = "test_"+System.nanoTime();
            SLF4JLoggerProxy.info(this,
                                  "Creating new module: {} {}",
                                  providerUrn,
                                  instanceName);
            ModuleURN instanceUrn = dataFlowClient.createModule(providerUrn,
                                                                instanceName);
            SLF4JLoggerProxy.info(this,
                                  "Created: {}",
                                  instanceUrn);
            SLF4JLoggerProxy.info(this,
                                  "Stopping {}",
                                  instanceUrn);
            dataFlowClient.stopModule(instanceUrn);
            SLF4JLoggerProxy.info(this,
                                  "{} stopped",
                                  instanceUrn);
            SLF4JLoggerProxy.info(this,
                                  "Deleting {}",
                                  instanceUrn);
            dataFlowClient.deleteModule(instanceUrn);
            SLF4JLoggerProxy.info(this,
                                  "{} deleted",
                                  instanceUrn);
            // start a module
            instanceUrn = TickTockModuleFactory.INSTANCE_URN;
            SLF4JLoggerProxy.info(this,
                                  "Starting {}",
                                  instanceUrn);
            dataFlowClient.startModule(instanceUrn);
            SLF4JLoggerProxy.info(this,
                                  "{} started",
                                  instanceUrn);
            // build a data request that includes the tick-tock module and the data receiver, which will be sent to us
            List<DataRequest> dataRequestBuilder = Lists.newArrayList();
            dataRequestBuilder.add(new DataRequest(instanceUrn));
            dataRequestBuilder.add(new DataRequest(DataFlowReceiverModuleFactory.INSTANCE_URN));
            DataFlowID dataFlowId = dataFlowClient.createDataFlow(dataRequestBuilder);
            SLF4JLoggerProxy.info(this,
                                  "Data flow {} created",
                                  dataFlowId);
            Thread.sleep(5000);
            // get info on the created data flow
            SLF4JLoggerProxy.info(this,
                                  "Retrieved data flow info: {}",
                                  dataFlowClient.getDataFlowInfo(dataFlowId));
            // get all current data flows
            for(DataFlowID activeDataFlowId : dataFlowClient.getDataFlows()) {
                SLF4JLoggerProxy.info(this,
                                      "Retrieved active data flow {}",
                                      dataFlowClient.getDataFlowInfo(activeDataFlowId));
            }
            // stop the data flow
            SLF4JLoggerProxy.info(this,
                                  "Canceling data flow {}",
                                  dataFlowId);
            dataFlowClient.cancelDataFlow(dataFlowId);
            // get historical data flow info
            for(DataFlowInfo historicalDataFlowInfo : dataFlowClient.getDataFlowHistory()) {
                SLF4JLoggerProxy.info(this,
                                      "Retrieved historical data flow {}",
                                      historicalDataFlowInfo);
            }
            // stop the started module
            dataFlowClient.stopModule(instanceUrn);
            dataFlowClient.removeDataReceiver(dataReceiver);
        } finally {
            if(dataFlowClient != null) {
                dataFlowClient.stop();
            }
        }
    }
    /**
     * Create a new DataFlowClientTest instance.
     */
    public DataFlowClientTest()
    {
        instance = this;
    }
    /**
     * Get the data flow client factory value.
     *
     * @return a <code>DataFlowRpcClientFactory</code> value
     */
    @Bean
    public DataFlowRpcClientFactory getDataFlowClientFactory()
    {
        DataFlowRpcClientFactory clientFactory = new DataFlowRpcClientFactory();
        return clientFactory;
    }
    /**
     * Get the autowired instance.
     *
     * @return a <code>DataFlowClientTest</code> value
     */
    @Bean
    public static DataFlowClientTest getDataFlowClientTest()
    {
        return new DataFlowClientTest();
    }
    /**
     * instance created for autowiring purposes
     */
    private static DataFlowClientTest instance;
    /**
     * hostname value
     */
    @Value("${metc.client.hostname:127.0.0.1}")
    private String hostname;
    /**
     * username value
     */
    @Value("${metc.client.username:trader}")
    private String username;
    /**
     * password value
     */
    @Value("${metc.client.password:trader}")
    private String password;
    /**
     * port value
     */
    @Value("${metc.client.port:8998}")
    private int port;
    /**
     * provides access to market data client services
     */
    private DataFlowClient dataFlowClient;
    /**
     * creates {@link DataFlowClient} objects
     */
    @Autowired
    private DataFlowRpcClientFactory dataFlowClientFactory;
}
//package org.marketcetera.clienttest;
//
//import org.marketcetera.util.log.SLF4JLoggerProxy;
//
///* $License$ */
//
///**
// * Test the functioning of {@link SEClient}.
// *
// * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
// * @version $Id$
// * @since $Release$
// */
//public class SeClientTest
//{
//    /**
//     * Main run method.
//     *
//     * @param inArgs a <code>String[]</code> value
//     */
//    public static void main(String[] args)
//    {
//        SLF4JLoggerProxy.info(SeClientTest.class,
//                              "Starting strategy engine client test");
////        SAClient client = RpcSAClientFactory.INSTANCE.create(new SAClientParameters("user",
////                                                                                    "password".toCharArray(),
////                                                                                    "tcp://localhost:61617",
////                                                                                    "localhost",
////                                                                                    8998,
////                                                                                    SAClientContextClassProvider.INSTANCE));
////        client.start();
////        SLF4JLoggerProxy.info(SeClientTest.class,
////                              "Connected to strategy engine: {}",
////                              client.isRunning());
////        SLF4JLoggerProxy.info(SeClientTest.class,
////                              "Available providers: {}",
////                              client.getProviders());
////        client.addDataReceiver(new DataReceiver() {
////            @Override
////            public void receiveData(Object inObject)
////            {
////                SLF4JLoggerProxy.info(SeClientTest.class,
////                                      "{}",
////                                      inObject);
////            }
////            
////        });
////        // lots of other operations involving starting and stopping strategies and other modules
////        CreateStrategyParameters strategyParameters;
////        try {
////            strategyParameters = new CreateStrategyParameters("test_instance",
////                                                              "HelloWorld",
////                                                              Language.JAVA.name(),
////                                                              new File(SaClientTest.class.getClassLoader().getResource("HelloWorld.java").getFile()),
////                                                              null,
////                                                              true);
////        } catch (FileNotFoundException e1) {
////            e1.printStackTrace();
////            return;
////        }
////        ModuleURN strategyUrn = client.createStrategy(strategyParameters);
////        try {
////            client.start(strategyUrn);
////            Thread.sleep(5000);
////        } catch (InterruptedException e) {
////            e.printStackTrace();
////        } finally {
////            try {
////                client.stop(strategyUrn);
////            } catch (Exception ignored) {}
////            try {
////                client.delete(strategyUrn);
////            } catch (Exception ignored) {}
////            client.close();
////        }
//        SLF4JLoggerProxy.info(SeClientTest.class,
//                              "Ending strategy engine client test");
//    }
//}
