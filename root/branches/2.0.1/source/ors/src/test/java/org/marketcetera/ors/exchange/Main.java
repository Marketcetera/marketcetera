package org.marketcetera.ors.exchange;

import java.io.File;
import org.apache.log4j.PropertyConfigurator;
import org.marketcetera.core.ApplicationBase;
import org.marketcetera.util.log.SLF4JLoggerProxy;

/**
 * A miniscule exchange: command-line execution.
 *
 * @author tlerios@marketcetera.com
 * @since 2.0.0
 * @version $Id$
 */

/* $License$ */

public class Main
    extends ApplicationBase
{
    public static void main
        (String[] args)
    {
        PropertyConfigurator.configureAndWatch
            (ApplicationBase.CONF_DIR+"log4j"+ //$NON-NLS-1$
             File.separator+"server.properties", //$NON-NLS-1$
             LOGGER_WATCH_DELAY);

        SLF4JLoggerProxy.info(Main.class,"Exchange is starting");

        final SampleExchange exchange;
        try {
            exchange=new SampleExchange(ApplicationBase.CONF_DIR+args[0]);
            exchange.start();
        } catch (Throwable t) {
            try {
                SLF4JLoggerProxy.error
                    (Main.class,t,"Exchange is terminating due to an error");
            } catch (Throwable t2) {
                System.err.println("Reporting failed");
                System.err.println("Reporting failure");
                t2.printStackTrace();
                System.err.println("Original failure");
                t.printStackTrace();
            }
            return;
        }
        SLF4JLoggerProxy.info(Main.class,
                              "Exchange started successfully. Ctrl-C to exit");

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                exchange.stop();
                SLF4JLoggerProxy.info(Main.class,"Exchange is terminated");
            }
        });

        try {
            (new Main()).startWaitingForever();
        } catch (Throwable t) {
            try {
                SLF4JLoggerProxy.error
                    (Main.class,t,"Exchange is terminating due to an error");
            } catch (Throwable t2) {
                System.err.println("Reporting failed");
                System.err.println("Reporting failure");
                t2.printStackTrace();
                System.err.println("Original failure");
                t.printStackTrace();
            }
            return;
        }
        SLF4JLoggerProxy.info(Main.class,
                              "Exchange is terminating successfully");
    }
}
