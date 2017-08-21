package org.marketcetera.server;

import org.marketcetera.core.ApplicationContainer;
import org.marketcetera.core.ApplicationVersion;
import org.marketcetera.core.Messages;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/* $License$ */

/**
 * Application entry point for the web UI.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@SpringBootApplication(scanBasePackages={"org.marketcetera"})
@EntityScan(basePackages={"org.marketcetera"})
@EnableJpaRepositories(basePackages={"org.marketcetera"})
public class ServerApplication
{
    /**
     * Main application entry.
     *
     * @param inArgs
     */
    public static void main(String[] inArgs)
    {
        SpringApplication.run(ServerApplication.class,
                              inArgs);
        // log application start
        Messages.APP_COPYRIGHT.info(ApplicationContainer.class);
        Messages.APP_VERSION_BUILD.info(ApplicationContainer.class,
                                        ApplicationVersion.getVersion(ApplicationContainer.class),
                                        ApplicationVersion.getBuildNumber());
        Messages.APP_START.info(ApplicationContainer.class);
        // check to see if we're using a different starting context file than the default
//        String rawValue = StringUtils.trimToNull(System.getProperty(CONTEXT_FILE_PROP));
//        if(rawValue != null) {
//            contextFilename = rawValue;
//        }
        final ApplicationContainer application;
        try {
            application = new ApplicationContainer();
            application.setArguments(inArgs);
            application.start();
        } catch(Exception e) {
            if(exitCode == 0) {
                exitCode = -1;
            }
            e.printStackTrace();
            try {
                Messages.APP_STOP_ERROR.error(ApplicationContainer.class,
                                              e);
            } catch(Exception e2) {
                System.err.println("Reporting failed"); //$NON-NLS-1$
                System.err.println("Reporting failure"); //$NON-NLS-1$
                e2.printStackTrace();
                System.err.println("Original failure"); //$NON-NLS-1$
                e.printStackTrace();
            }
            System.exit(exitCode);
            return;
        }
        Messages.APP_STARTED.info(ApplicationContainer.class);
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                application.stop();
                Messages.APP_STOP.info(ApplicationContainer.class);
//                if(LogManager.getContext() instanceof LoggerContext) {
//                    Configurator.shutdown((LoggerContext)LogManager.getContext());
//                }
            }
        });
        try {
            application.startWaitingForever();
        } catch(Exception e) {
            try {
                Messages.APP_STOP_ERROR.error(ApplicationContainer.class,
                                              e);
            } catch(Exception e2) {
                System.err.println("Reporting failed"); //$NON-NLS-1$
                System.err.println("Reporting failure"); //$NON-NLS-1$
                e2.printStackTrace();
                System.err.println("Original failure"); //$NON-NLS-1$
                e.printStackTrace();
            }
            System.exit(exitCode);
            return;
        }
        Messages.APP_STOP_SUCCESS.info(ApplicationContainer.class);
        System.exit(exitCode);
    }
    /**
     * exit code to return on exit
     */
    protected static int exitCode = 0;
}
