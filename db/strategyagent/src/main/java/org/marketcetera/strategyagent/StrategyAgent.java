package org.marketcetera.strategyagent;

import java.io.File;
import java.io.IOException;
import java.io.LineNumberReader;
import java.lang.management.ManagementFactory;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.management.JMX;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.apache.commons.lang.ObjectUtils;
import org.marketcetera.client.ClientManager;
import org.marketcetera.core.ApplicationContainer;
import org.marketcetera.core.ApplicationVersion;
import org.marketcetera.core.Util;
import org.marketcetera.core.publisher.IPublisher;
import org.marketcetera.core.publisher.ISubscriber;
import org.marketcetera.core.publisher.PublisherEngine;
import org.marketcetera.module.*;
import org.marketcetera.saclient.SAClientVersion;
import org.marketcetera.saclient.SAService;
import org.marketcetera.util.except.I18NException;
import org.marketcetera.util.log.I18NBoundMessage2P;
import org.marketcetera.util.log.I18NBoundMessage3P;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.spring.SpringUtils;
import org.marketcetera.util.unicode.UnicodeFileReader;
import org.marketcetera.util.ws.stateful.Authenticator;
import org.marketcetera.util.ws.stateful.Server;
import org.marketcetera.util.ws.stateful.SessionManager;
import org.marketcetera.util.ws.stateless.ServiceInterface;
import org.marketcetera.util.ws.stateless.StatelessClientContext;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.StaticApplicationContext;

/* $License$ */
/**
 * The main class for the strategy agent.
 * <p>
 * This class starts off the
 * module container and then reads up a list of module manager commands
 * from a command file and executes it and waits until it's killed.
 * <p>
 * If no commands file is supplied, the strategy agent does nothing, it
 * simply waits until it's killed.
 * <p>
 * After the strategy agent is started, clients can connect to it via
 * JMX and manage it via the {@link org.marketcetera.module.ModuleManagerMXBean}
 * interface. 
 *
 * @author anshul@marketcetera.com
 */
@ClassVersion("$Id$")
public class StrategyAgent
        implements IPublisher, InitializingBean
{
//    /**
//     * Creates and runs the application.
//     *
//     * @param args the command line arguments.
//     */
//    public static void main(String[] args) {
//        initializeLogger(LOGGER_CONF_FILE);
//        Messages.LOG_APP_COPYRIGHT.info(StrategyAgent.class);
//        Messages.LOG_APP_VERSION_BUILD.info(StrategyAgent.class,
//                ApplicationVersion.getVersion(),
//                ApplicationVersion.getBuildNumber());
//        //Run the application.
//        run(new StrategyAgent(),
//            args);
//    }
    /**
     * Gets the most recently created <code>StrategyAgent</code> instance in this process.
     *
     * @return a <code>StrategyAgent</code> value
     */
    public static StrategyAgent getInstance()
    {
        return instance;
    }
    /**
     * Get the contextClasses value.
     *
     * @return a <code>Class&lt;?&gt;[]</code> value
     */
    public Class<?>[] getContextClasses()
    {
        return contextClasses;
    }
    /**
     * Sets the contextClasses value.
     *
     * @param a <code>Class&lt;?&gt;[]</code> value
     */
    public void setContextClasses(Class<?>[] inContextClasses)
    {
        contextClasses = inContextClasses;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.publisher.IPublisher#subscribe(org.marketcetera.core.publisher.ISubscriber)
     */
    @Override
    public void subscribe(ISubscriber inSubscriber)
    {
        dataPublisher.subscribe(inSubscriber);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.publisher.IPublisher#unsubscribe(org.marketcetera.core.publisher.ISubscriber)
     */
    @Override
    public void unsubscribe(ISubscriber inSubscriber)
    {
        dataPublisher.unsubscribe(inSubscriber);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.publisher.IPublisher#publish(java.lang.Object)
     */
    @Override
    public void publish(Object inData)
    {
        dataPublisher.publish(inData);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.publisher.IPublisher#publishAndWait(java.lang.Object)
     */
    @Override
    public void publishAndWait(Object inData)
            throws InterruptedException, ExecutionException
    {
        dataPublisher.publishAndWait(inData);
    }
    /**
     * Stops the strategy agent. This method is meant to help with unit testing.
     */
    protected void stop() {
        if(mContext != null) {
            mContext.destroy();
            mContext = null;
        }
        stopRemoteService();
    }
    /* (non-Javadoc)
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    @Override
    public void afterPropertiesSet()
            throws Exception
    {
        try {
            //Configure the application. If it fails, exit
            configure();
            String[] args = ApplicationContainer.getInstance().getArguments();
            if(args.length > 0) {
                int parseErrors = parseCommands(args[0]);
                if(parseErrors > 0) {
                    Messages.LOG_COMMAND_PARSE_ERRORS.error(StrategyAgent.class,
                                                            parseErrors);
                    throw new IllegalArgumentException(String.valueOf(EXIT_CMD_PARSE_ERROR));
                }
            }
        } catch(Exception e) {
            Messages.LOG_ERROR_CONFIGURE_AGENT.error(StrategyAgent.class,
                                                     getMessage(e));
            Messages.LOG_ERROR_CONFIGURE_AGENT.debug(StrategyAgent.class,
                                                     e,
                                                     getMessage(e));
            throw e;
        }
        //Initialize the application. If it fails, exit
        try {
            init();
        } catch (Exception e) {
            Messages.LOG_ERROR_INITIALIZING_AGENT.error(StrategyAgent.class,
                                                        getMessage(e));
            Messages.LOG_ERROR_INITIALIZING_AGENT.debug(StrategyAgent.class,
                                                        e,
                                                        getMessage(e));
            throw e;
        }
        //Run the commands, if commands fail, the failure is logged, but
        //the application doesn't exit.
        executeCommands();
    }
    /**
     * Configures the agent.
     */
    private void configure()
    {
        File modulesDir = new File(ApplicationContainer.getInstance().getAppDir(),
                                   "modules");  //$NON-NLS-1$
        StaticApplicationContext parentCtx = new StaticApplicationContext();
        //Provide the module jar directory path to the spring context.
        SpringUtils.addStringBean(parentCtx,
                                  "modulesDir",   //$NON-NLS-1$
                                  modulesDir.getAbsolutePath());
        ConfigurableApplicationContext context = ApplicationContainer.getInstance().getContext();
        context.setParent(parentCtx);
        context.refresh();
//        mContext = new FileSystemXmlApplicationContext(new String[] {"file:"+CONF_DIR+"strategyagent.xml"}, //$NON-NLS-1$  //$NON-NLS-2$
//                                                       parentCtx);
        mManager = context.getBean(ModuleManager.class);
        //Set the context classloader to the jar classloader so that
        //all modules have the thread context classloader set to the same
        //value as the classloader that loaded them.
        ClassLoader loader = context.getBean(ClassLoader.class);
        Thread.currentThread().setContextClassLoader(loader);
        Authenticator authenticator;
        try {
            authenticator = mContext.getBean(Authenticator.class);
            SLF4JLoggerProxy.debug(this,
                                   "Using custom authenticator {}",
                                   authenticator);
        } catch (NoSuchBeanDefinitionException e) {
            authenticator = new Authenticator() {
                @Override
                public boolean shouldAllow(StatelessClientContext context,
                                           String user,
                                           char[] password)
                        throws I18NException
                {
                    return authenticate(context,
                                        user,
                                        password);
                }
            };
        }
        //Setup the WS services after setting up the context class loader.
        String hostname = (String) mContext.getBean("wsServerHost");  //$NON-NLS-1$
        if (hostname != null && !hostname.trim().isEmpty()) {
            int port = (Integer) mContext.getBean("wsServerPort");  //$NON-NLS-1$
            SessionManager<ClientSession> sessionManager= new SessionManager<ClientSession>(new ClientSessionFactory(),
                                                                                            SessionManager.INFINITE_SESSION_LIFESPAN);
            mServer = new Server<ClientSession>(hostname,
                                                port,
                                                authenticator,
                                                sessionManager,
                                                contextClasses);
            mRemoteService = mServer.publish(new SAServiceImpl(sessionManager,
                                                               mManager,
                                                               dataPublisher),
                                             SAService.class);
            //Register a shutdown task to shutdown the remote service.
            Runtime.getRuntime().addShutdownHook(new Thread(){
                @Override
                public void run() {
                    stopRemoteService();
                }
            });
            Messages.LOG_REMOTE_WS_CONFIGURED.info(this, hostname, String.valueOf(port));
        }
    }
    /**
     * Initializes the module manager.
     *
     * @throws ModuleException if there were errors initializing the module
     * manager.
     * @throws MalformedObjectNameException if there were errors creating
     * the object name of the module manager bean.
     */
    private void init() throws ModuleException, MalformedObjectNameException {
        //Initialize the module manager.
        mManager.init();
        //Add the logger sink listener
        mManager.addSinkListener(new SinkDataListener() {
            public void receivedData(DataFlowID inFlowID, Object inData) {
                final boolean isNullData = inData == null;
                Messages.LOG_SINK_DATA.info(SINK_DATA, inFlowID,
                        isNullData ? 0: 1,
                        isNullData ? null: inData.getClass().getName(),
                        inData);
            }
        });
        mManagerBean = JMX.newMXBeanProxy(
                ManagementFactory.getPlatformMBeanServer(),
                new ObjectName(ModuleManager.MODULE_MBEAN_NAME),
                ModuleManagerMXBean.class);
    }
    /**
     * Create a new StrategyAgent instance.
     */
    public StrategyAgent()
    {
        instance = this;
    }
    /**
     * Returns the module manager.
     * This method is exposed to aid testing.
     *
     * @return the module manager.
     */
    protected ModuleManager getManager() {
        return mManager;
    }

    /**
     * Stops the remote web service.
     */
    private void stopRemoteService() {
        if(mRemoteService != null) {
            mRemoteService.stop();
            mRemoteService = null;
        }
        if(mServer != null) {
            mServer.stop();
            mServer = null;
        }
    }

    /**
     * Return the exception message from the supplied Throwable.
     *
     * @param inThrowable the throwable whose message needs to be returned.
     *
     * @return the throwable message.
     */
    private static String getMessage(Throwable inThrowable) {
        if(inThrowable instanceof I18NException) {
            return ((I18NException)inThrowable).getLocalizedDetail();
        } else {
            return inThrowable.getLocalizedMessage();
        }
    }

//    /**
//     * Initializes the logger for this application.
//     *
//     * @param logConfig the logger configuration file.
//     */
//    private static void initializeLogger(String logConfig)
//    {
//        PropertyConfigurator.configureAndWatch
//            (ApplicationBase.CONF_DIR+logConfig, LOGGER_WATCH_DELAY);
//    }
//
    /**
     * Parses the commands from the supplied commands file.
     *
     * @param inFile the file path
     *
     * @throws IOException if there were errors parsing the file.
     *
     * @return the number of errors encountered when parsing the command file.
     */
    private int parseCommands(String inFile) throws IOException {
        int numErrors = 0;
        LineNumberReader reader = new LineNumberReader(
                new UnicodeFileReader(inFile));
        try {
            String line;
            while((line = reader.readLine()) != null) {
                if(line.startsWith("#") || line.trim().isEmpty()) {  //$NON-NLS-1$
                    //Ignore comments and empty lines.
                    continue;
                }
                int idx = line.indexOf(';');  //$NON-NLS-1$
                if(idx > 0) {
                    String key = line.substring(0, idx);
                    CommandRunner runner = sRunners.get(key);
                    if(runner == null) {
                        numErrors++;
                        Messages.INVALID_COMMAND_NAME.error(this, key,
                                reader.getLineNumber());
                        continue;
                    }
                    mCommands.add(new Command(runner, line.substring(++idx),
                            reader.getLineNumber()));
                } else {
                    numErrors++;
                    Messages.INVALID_COMMAND_SYNTAX.error(this,
                            line, reader.getLineNumber());
                }
            }
            return numErrors;
        } finally {
            reader.close();
        }
    }



    /**
     * Authenticates a client connection.
     * <p>
     * This method is package protected to enable its unit testing.
     *
     * @param context the client's context.
     * @param user the user name.
     * @param password the password.
     *
     * @return if the authentication succeeded.
     *
     * @throws I18NException if the client is incompatible with the server.
     */
    static boolean authenticate(StatelessClientContext context,
                                String user,
                                char[] password)
            throws I18NException {
        //Verify client version
        String serverVersion = ApplicationVersion.getVersion();
        String clientName = Util.getName(context.getAppId());
        String clientVersion = Util.getVersion(context.getAppId());
        if (!compatibleApp(clientName)) {
            throw new I18NException
                    (new I18NBoundMessage2P(Messages.APP_MISMATCH,
                            clientName, user));
        }
        if (!compatibleVersions(clientVersion, serverVersion)) {
            throw new I18NException
                    (new I18NBoundMessage3P(Messages.VERSION_MISMATCH,
                            clientVersion, serverVersion, user));
        }
        //Use client to carry out authentication.
        return ClientManager.getInstance().isCredentialsMatch(user, password);
    }


    /**
     * Executes commands, if any were provided. If any command fails, the
     * failure is logged. Failure of any command doesn't prevent the next
     * command from executing or prevent the application from exiting.
     */
    private void executeCommands() {
        if(!mCommands.isEmpty()) {
            for(Command c: mCommands) {
                try {
                    Messages.LOG_RUNNING_COMMAND.info(this,
                            c.getRunner().getName(), c.getParameter());
                    Object result = c.getRunner().runCommand(
                            mManagerBean, c.getParameter());
                    Messages.LOG_COMMAND_RUN_RESULT.info(this,
                            c.getRunner().getName(), result);
                } catch (Throwable t) {
                    Messages.LOG_ERROR_EXEC_CMD.warn(this,
                            c.getRunner().getName(),
                            c.getParameter(), c.getLineNum(),
                            getMessage(t));
                    Messages.LOG_ERROR_EXEC_CMD.debug(this, t,
                            c.getRunner().getName(),
                            c.getParameter(), c.getLineNum(),
                            getMessage(t));
                }
            }
        }
    }

    /**
     * Adds a command runner instance to the table of command runners.
     *
     * @param inRunner the command runner to be added.
     */
    private static void addRunner(CommandRunner inRunner) {
        sRunners.put(inRunner.getName(), inRunner);
    }

    /**
     * Checks for compatibility between the given client and server
     * versions.
     *
     * @param clientVersion The client version.
     * @param serverVersion The server version.
     * @return True if the two versions are compatible.
     */

    private static boolean compatibleVersions(String clientVersion, String serverVersion) {
        // If the server's version is unknown, any client is allowed.
        return (ApplicationVersion.DEFAULT_VERSION.equals(serverVersion) ||
                ObjectUtils.equals(clientVersion, serverVersion));
    }

    /**
     * Checks if a client with the supplied name is compatible with this server.
     *
     * @param clientName The client name.
     *
     * @return True if a client with the supplied name is compatible with this
     * server.
     */
    private static boolean compatibleApp(String clientName) {
        return SAClientVersion.APP_ID_NAME.equals(clientName);
    }

    /**
     * The log category used to log all the data received by the sink module
     */
    public static final String SINK_DATA = "SINK";  //$NON-NLS-1$

    /**
     * Exit code when the command exits because of parsing errors
     */
    public static final int EXIT_CMD_PARSE_ERROR = 1;
    /**
     * Exit code when command exits because of errors starting up.
     */
    public static final int EXIT_START_ERROR = 2;
    /**
     * Exit code when command exits because of initialization errors.
     */
    public static final int EXIT_INIT_ERROR = 3;
    /**
     * The table of command names and command runners.
     */
    private static final Map<String, CommandRunner> sRunners =
            new HashMap<String, CommandRunner>();

    static {
        //Initialize the set of available command runners
        addRunner(new CreateModule());
        addRunner(new CreateDataFlow());
        addRunner(new StartModule());
    }
    /**
     * The module manager instance.
     */
    private ModuleManager mManager;
    /**
     * The module manager mxbean reference.
     */
    private ModuleManagerMXBean mManagerBean;
    /**
     * The list of parsed commands.
     */
    private List<Command> mCommands =
            new LinkedList<Command>();
    /**
     * The handle to the remote web service.
     */
    private volatile ServiceInterface mRemoteService;
    private volatile AbstractApplicationContext mContext;
    private volatile Server<ClientSession> mServer;
    /**
     * used to publish data received to interested subscribers
     */
    private final PublisherEngine dataPublisher = new PublisherEngine();
    /**
     * extra context classes to add to the server context
     */
    private volatile Class<?>[] contextClasses;
    /**
     * most recent strategy agent instance
     */
    private volatile static StrategyAgent instance;
}
