package org.marketcetera.clienttest;

import org.marketcetera.core.PlatformServices;
import org.marketcetera.strategyengine.client.ConnectionStatusListener;
import org.marketcetera.strategyengine.client.DataReceiver;
import org.marketcetera.strategyengine.client.SEClient;
import org.marketcetera.strategyengine.client.rpc.SERpcClientParameters;
import org.marketcetera.strategyengine.client.rpc.StrategyAgentRpcClientFactory;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/* $License$ */

/**
 * Demonstrates how to connect to MATP Strategy Engine services from an external application.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@SpringBootApplication
@EnableAutoConfiguration
@SpringBootConfiguration
public class SeClientTest
{
    /**
     * Main run method.
     *
     * @param inArgs a <code>String[]</code> value
     */
    public static void main(String[] inArgs)
    {
        SpringApplication.run(SeClientTest.class,
                              inArgs);
        SLF4JLoggerProxy.info(SeClientTest.class,
                              "Starting SE client test");
        try {
            SeClientTest clientTest = SeClientTest.instance;
            clientTest.runTest();
        } catch (Exception e) {
            PlatformServices.handleException(SeClientTest.class,
                                             "SE Client Test Error",
                                             e);
        }
        SLF4JLoggerProxy.info(SeClientTest.class,
                              "Ending SE client test");
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
            SERpcClientParameters params = new SERpcClientParameters();
            params.setHostname(hostname);
            params.setPort(port);
            params.setUsername(username);
            params.setPassword(password);
            seClient = seClientFactory.create(params);
            seClient.start();
            SLF4JLoggerProxy.info(SeClientTest.class,
                                  "SEClient connected to {}:{} as {}",
                                  hostname,
                                  port,
                                  username);
            // add a connection status listener
            ConnectionStatusListener connectionStatusListener = new ConnectionStatusListener() {

                @Override
                public void receiveConnectionStatus(boolean inStatus)
                {
                    SLF4JLoggerProxy.info(SeClientTest.this,
                                          "Received {}",
                                          inStatus);
                }
            };
            seClient.addConnectionStatusListener(connectionStatusListener);
            // add a data listener
            DataReceiver dataReceiver = new DataReceiver() {
                @Override
                public void receiveData(Object inObject)
                {
                    SLF4JLoggerProxy.info(SeClientTest.this,
                                          "Received {}",
                                          inObject);
                }
            };
            seClient.addDataReceiver(dataReceiver);
            seClient.removeDataReceiver(dataReceiver);
            seClient.removeConnectionStatusListener(connectionStatusListener);
        } finally {
            if(seClient != null) {
                seClient.stop();
            }
        }
    }
    /**
     * Create a new SeClientTest instance.
     */
    public SeClientTest()
    {
        instance = this;
    }
    /**
     * Get the trading client factory value.
     *
     * @return a <code>StrategyAgentRpcClientFactory</code> value
     */
    @Bean
    public StrategyAgentRpcClientFactory getSeClientFactory()
    {
        StrategyAgentRpcClientFactory seClientFactory = new StrategyAgentRpcClientFactory();
        return seClientFactory;
    }
    /**
     * Get the autowired instance.
     *
     * @return a <code>SeClientTest</code> value
     */
    @Bean
    public static SeClientTest getSeClientTest()
    {
        return new SeClientTest();
    }
    /**
     * instance created for autowiring purposes
     */
    private static SeClientTest instance;
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
     * provides access to SE client services
     */
    private SEClient seClient;
    /**
     * creates {@link SEClient} objects
     */
    @Autowired
    private StrategyAgentRpcClientFactory seClientFactory;
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
