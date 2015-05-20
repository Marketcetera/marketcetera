package org.marketcetera.clienttest;

import java.io.File;
import java.io.FileNotFoundException;

import org.marketcetera.module.ModuleURN;
import org.marketcetera.saclient.CreateStrategyParameters;
import org.marketcetera.saclient.DataReceiver;
import org.marketcetera.saclient.SAClient;
import org.marketcetera.saclient.SAClientParameters;
import org.marketcetera.saclient.rpc.RpcSAClientFactory;
import org.marketcetera.saclient.rpc.SAClientContextClassProvider;
import org.marketcetera.strategy.Language;
import org.marketcetera.util.log.SLF4JLoggerProxy;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class SaClientTest
{
    /**
     * Main run method.
     *
     * @param inArgs a <code>String[]</code> value
     */
    public static void main(String[] args)
    {
        SLF4JLoggerProxy.info(ClientTest.class,
                              "Starting strategy engine client test");
        SAClient client = RpcSAClientFactory.INSTANCE.create(new SAClientParameters("user",
                                                                                    "password".toCharArray(),
                                                                                    "tcp://localhost:61617",
                                                                                    "localhost",
                                                                                    8998,
                                                                                    SAClientContextClassProvider.INSTANCE));
        client.start();
        SLF4JLoggerProxy.info(ClientTest.class,
                              "Connected to strategy engine: {}",
                              client.isRunning());
        SLF4JLoggerProxy.info(ClientTest.class,
                              "Available providers: {}",
                              client.getProviders());
        client.addDataReceiver(new DataReceiver() {
            @Override
            public void receiveData(Object inObject)
            {
                SLF4JLoggerProxy.info(ClientTest.class,
                                      "{}",
                                      inObject);
            }
            
        });
        // lots of other operations involving starting and stopping strategies and other modules
        CreateStrategyParameters strategyParameters;
        try {
            strategyParameters = new CreateStrategyParameters("test_instance",
                                                              "HelloWorld",
                                                              Language.JAVA.name(),
                                                              new File(SaClientTest.class.getClassLoader().getResource("HelloWorld.java").getFile()),
                                                              null,
                                                              true);
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
            return;
        }
        ModuleURN strategyUrn = client.createStrategy(strategyParameters);
        try {
            client.start(strategyUrn);
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            try {
                client.stop(strategyUrn);
            } catch (Exception ignored) {}
            try {
                client.delete(strategyUrn);
            } catch (Exception ignored) {}
            client.close();
        }
        SLF4JLoggerProxy.info(ClientTest.class,
                              "Ending strategy engine client test");
    }
}
