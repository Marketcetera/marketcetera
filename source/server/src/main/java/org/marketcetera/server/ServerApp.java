package org.marketcetera.server;

import java.io.File;

import org.apache.commons.lang.Validate;
import org.apache.log4j.PropertyConfigurator;
import org.marketcetera.api.server.ClientContext;
import org.marketcetera.api.server.ContextValidator;
import org.marketcetera.api.server.Server;
import org.marketcetera.api.server.ServerConfig;
import org.marketcetera.core.ApplicationBase;
import org.marketcetera.core.ApplicationVersion;
import org.marketcetera.ors.OrderRoutingSystem;
import org.marketcetera.server.ws.Services;
import org.marketcetera.server.ws.impl.ServicesImpl;
import org.marketcetera.strategyagent.StrategyAgent;
import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.ws.stateless.StatelessServer;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.Lifecycle;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.context.support.StaticApplicationContext;

/* $License$ */

/**
 * Provides Marketcetera server-space services.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public class ServerApp
        extends ApplicationBase
        implements Lifecycle, InitializingBean, Server
{
    /**
     * Gets the <code>Server</code> instance.
     *
     * @return a <code>Server</code> instance
     * @throws IllegalStateException if the <code>Server</code> has not been initialized
     */
    public static ServerApp getInstance()
    {
        if(instance == null) {
            throw new IllegalStateException("The server has not been initialized");
        }
        return instance;
    }
    /**
     *
     *
     * @param inArgs
     */
    public static void main(String[] inArgs)
    {
        // Configure logger.
        PropertyConfigurator.configureAndWatch(String.format("%slog4j%sserver.properties",
                                                             ApplicationBase.CONF_DIR,
                                                             File.separator),
                                               LOGGER_WATCH_DELAY);
        // Log application start.
        Messages.APP_COPYRIGHT.info(LOGGER_CATEGORY);
        Messages.APP_VERSION_BUILD.info(LOGGER_CATEGORY,
                                        ApplicationVersion.getVersion(),
                                        ApplicationVersion.getBuildNumber());
        Messages.APP_START.info(LOGGER_CATEGORY);
        // Start application
        // build base Spring configuration
        context = new StaticApplicationContext(new FileSystemXmlApplicationContext(APP_CONTEXT_CFG_BASE));
        context.refresh();
        // instantiate the objects specified in the configuration
        context.start();
        // the App instance is created by the Spring config
        final ServerApp app = ServerApp.getInstance();
        if(app == null) {
            Messages.APP_STOP_ERROR.error(LOGGER_CATEGORY);
            return;
        }
        Messages.APP_STARTED.info(LOGGER_CATEGORY);
        // Hook to log shutdown.
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                app.stop();
                Messages.APP_STOP.info(LOGGER_CATEGORY);
            }
        });
        // Execute application.
        try {
            app.start();
            app.startWaitingForever();
        } catch(Exception t) {
            try {
                Messages.APP_STOP_ERROR.error(LOGGER_CATEGORY,
                                              t);
            } finally {
                app.stop();
            }
            return;
        }
        Messages.APP_STOP_SUCCESS.info(LOGGER_CATEGORY);
    }
    /* (non-Javadoc)
     * @see org.springframework.context.Lifecycle#isRunning()
     */
    @Override
    public boolean isRunning()
    {
        return super.isWaitingForever();
    }
    /* (non-Javadoc)
     * @see org.springframework.context.Lifecycle#start()
     */
    @Override
    public synchronized void start()
    {
        getOrsNode().start();
        webServicesProvider = new StatelessServer(config.getHostname(),
                                                  config.getPort());
        webServicesProvider.publish(servicesImpl,
                                    Services.class);
    }
    /* (non-Javadoc)
     * @see org.springframework.context.Lifecycle#stop()
     */
    @Override
    public synchronized void stop()
    {
        if(webServicesProvider != null) {
            webServicesProvider.stop();
            webServicesProvider = null;
        }
        OrderRoutingSystem ors = getOrsNode();
        if(ors != null) {
            ors.stop();
        }
        // TODO stop SA node
    }
    /* (non-Javadoc)
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    @Override
    public void afterPropertiesSet()
            throws Exception
    {
        Validate.notNull(servicesImpl,
                         "Must provide a valid services implementation");
        Validate.notNull(config,
                         "Must provide a valid server configuration");
        Validate.notNull(validator,
                         "Must provide a valid context validator");
        Validate.notNull(getOrsNode(),
                         "Must provide an order routing node");
    }
    /**
     * 
     *
     *
     * @param inServicesProvider
     */
    public void setServicesProvider(Services inServicesProvider)
    {
        servicesImpl = inServicesProvider;
    }
    /**
     * 
     *
     *
     * @param inConfig
     */
    public void setServerConfig(ServerConfig inConfig)
    {
        config = inConfig;
    }
    /**
     * 
     *
     *
     * @param inValidator
     */
    public void setContextValidator(ContextValidator inValidator)
    {
        validator = inValidator;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.api.server.Server#getContextValidator()
     */
    @Override
    public ContextValidator getContextValidator()
    {
        return validator;
    }
    /**
     * Get the orsNode value.
     *
     * @return an <code>OrderRoutingSystem</code> value
     */
    public OrderRoutingSystem getOrsNode()
    {
        return orsNode;
    }
    /**
     * Sets the orsNode value.
     *
     * @param an <code>OrderRoutingSystem</code> value
     */
    public void setOrsNode(OrderRoutingSystem inOrsNode)
    {
        orsNode = inOrsNode;
    }
    /**
     * Create a new ServerApp instance.
     */
    public ServerApp()
            throws Exception
    {
        servicesImpl = new ServicesImpl();
        validator = new ContextValidator() {
            @Override
            public void validate(ClientContext inContext)
            {
                System.out.println("Validating: " + inContext);
                // do nothing
            }
        };
        instance = this;
    }
    /**
     * routing node
     */
    private volatile OrderRoutingSystem orsNode;
    /**
     * 
     */
    private volatile StrategyAgent saNode;
    /**
     * 
     */
    private volatile ContextValidator validator;
    /**
     * 
     */
    private volatile ServerConfig config;
    /**
     * 
     */
    private volatile Services servicesImpl;
    /**
     * 
     */
    private volatile StatelessServer webServicesProvider;
    /**
     * 
     */
    private static final Class<?> LOGGER_CATEGORY = ServerApp.class;
    /**
     * 
     */
    private static final String APP_CONTEXT_CFG_BASE = "file:" + CONF_DIR + "server.xml";
    /**
     * the singleton instance of the server
     */
    private static volatile ServerApp instance;
    /**
     * the Spring context which with the App was constructed
     */
    private static volatile AbstractApplicationContext context;
}
