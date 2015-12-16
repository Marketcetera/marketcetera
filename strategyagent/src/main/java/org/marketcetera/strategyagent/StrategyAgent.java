package org.marketcetera.strategyagent;

import java.io.IOException;
import java.io.LineNumberReader;
import java.lang.management.ManagementFactory;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.management.JMX;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.apache.commons.lang.Validate;
import org.marketcetera.core.ApplicationContainer;
import org.marketcetera.core.ApplicationVersion;
import org.marketcetera.core.notifications.INotification;
import org.marketcetera.core.notifications.NotificationExecutor;
import org.marketcetera.core.publisher.IPublisher;
import org.marketcetera.core.publisher.ISubscriber;
import org.marketcetera.core.publisher.PublisherEngine;
import org.marketcetera.module.DataFlowID;
import org.marketcetera.module.ModuleException;
import org.marketcetera.module.ModuleManager;
import org.marketcetera.module.ModuleManagerMXBean;
import org.marketcetera.module.SinkDataListener;
import org.marketcetera.util.except.I18NException;
import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.unicode.UnicodeFileReader;
import org.marketcetera.util.ws.stateful.Authenticator;
import org.marketcetera.util.ws.stateful.Server;
import org.marketcetera.util.ws.stateless.ServiceInterface;
import org.marketcetera.util.ws.stateless.StatelessClientContext;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.Lifecycle;

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
        implements IPublisher,Lifecycle,ApplicationContextAware
{
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
        if(isRunning()) {
            throw new IllegalStateException();
        }
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
     * Get the dataPublisher value.
     *
     * @return a <code>PublisherEngine</code> value
     */
    public PublisherEngine getDataPublisher()
    {
        return dataPublisher;
    }
    /**
     * Sets the dataPublisher value.
     *
     * @param inDataPublisher a <code>PublisherEngine</code> value
     */
    public void setDataPublisher(PublisherEngine inDataPublisher)
    {
        if(isRunning()) {
            throw new IllegalStateException();
        }
        dataPublisher = inDataPublisher;
    }
    /**
     * Get the authenticator value.
     *
     * @return an <code>Authenticator</code> value
     */
    public Authenticator getAuthenticator()
    {
        return authenticator;
    }
    /**
     * Sets the authenticator value.
     *
     * @param inAuthenticator an <code>Authenticator</code> value
     */
    public void setAuthenticator(Authenticator inAuthenticator)
    {
        authenticator = inAuthenticator;
    }
    /**
     * Sends the given notification if possible.
     *
     * @param inNotification an <code>INotification</code> value
     */
    public void notify(INotification inNotification)
    {
        if(notificationExecutor != null) {
            notificationExecutor.notify(inNotification);
        }
    }
    /**
     * Stops the strategy agent.
     */
    public void stop()
    {
        stopRemoteService();
    }
    /* (non-Javadoc)
     * @see org.springframework.context.Lifecycle#isRunning()
     */
    @Override
    public boolean isRunning()
    {
        return running.get();
    }
    /* (non-Javadoc)
     * @see org.springframework.context.Lifecycle#start()
     */
    @Override
    public void start()
    {
        Messages.LOG_APP_VERSION_BUILD.info(this,
                                            ApplicationVersion.getVersion(),
                                            ApplicationVersion.getBuildNumber());
        Validate.notNull(authenticator);
        if(loader != null) {
            Thread.currentThread().setContextClassLoader(loader);
        }
        if(dataPublisher == null) {
            dataPublisher = new PublisherEngine();
        }
        try {
            //Configure the application. If it fails, exit
            String[] args = ApplicationContainer.getInstance().getArguments();
            if(args != null && args.length > 0) {
                int parseErrors = parseCommands(args[0]);
                if(parseErrors > 0) {
                    Messages.LOG_COMMAND_PARSE_ERRORS.error(StrategyAgent.class,
                                                            parseErrors);
                    throw new IllegalArgumentException(Messages.LOG_COMMAND_PARSE_ERRORS.getText(parseErrors));
                }
            }
        } catch(Exception e) {
            Messages.LOG_ERROR_CONFIGURE_AGENT.error(StrategyAgent.class,
                                                     getMessage(e));
            Messages.LOG_ERROR_CONFIGURE_AGENT.debug(StrategyAgent.class,
                                                     e,
                                                     getMessage(e));
            throw new RuntimeException(e);
        }
        // initialize the application. If it fails, exit
        try {
            init();
        } catch (Exception e) {
            Messages.LOG_ERROR_INITIALIZING_AGENT.error(StrategyAgent.class,
                                                        getMessage(e));
            Messages.LOG_ERROR_INITIALIZING_AGENT.debug(StrategyAgent.class,
                                                        e,
                                                        getMessage(e));
            throw new RuntimeException(e);
        }
        // run the commands
        executeCommands();
        running.set(true);
    }
    /**
     * Get the moduleManager value.
     *
     * @return a <code>ModuleManager</code> value
     */
    public ModuleManager getModuleManager()
    {
        return moduleManager;
    }
    /**
     * Sets the moduleManager value.
     *
     * @param inModuleManager a <code>ModuleManager</code> value
     */
    public void setModuleManager(ModuleManager inModuleManager)
    {
        if(isRunning()) {
            throw new IllegalStateException();
        }
        moduleManager = inModuleManager;
    }
    /* (non-Javadoc)
     * @see org.springframework.context.ApplicationContextAware#setApplicationContext(org.springframework.context.ApplicationContext)
     */
    @Override
    public void setApplicationContext(ApplicationContext inApplicationContext)
            throws BeansException
    {
        applicationContext = inApplicationContext;
    }
    /**
     * Get the applicationContext value.
     *
     * @return an <code>ApplicationContext</code> value
     */
    public ApplicationContext getApplicationContext()
    {
        return applicationContext;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.publisher.IPublisher#getSubscriptionCount()
     */
    @Override
    public int getSubscriptionCount()
    {
        return dataPublisher.getSubscriptionCount();
    }
    /**
     * Get the loader value.
     *
     * @return a <code>ClassLoader</code> value
     */
    public ClassLoader getLoader()
    {
        return loader;
    }
    /**
     * Sets the loader value.
     *
     * @param inLoader a <code>ClassLoader</code> value
     */
    public void setLoader(ClassLoader inLoader)
    {
        if(isRunning()) {
            throw new IllegalStateException();
        }
        loader = inLoader;
    }
    /**
     * Initializes the module manager.
     *
     * @throws ModuleException if there were errors initializing the module
     * manager.
     * @throws MalformedObjectNameException if there were errors creating
     * the object name of the module manager bean.
     */
    private void init()
            throws ModuleException, MalformedObjectNameException
    {
        //Initialize the module manager.
        if(moduleManager != null) {
            moduleManager.init();
            //Add the logger sink listener
            moduleManager.addSinkListener(new SinkDataListener() {
                public void receivedData(DataFlowID inFlowID, Object inData) {
                    final boolean isNullData = inData == null;
                    Messages.LOG_SINK_DATA.info(SINK_DATA,
                                                inFlowID,
                                                isNullData ? 0: 1,
                                                isNullData ? null: inData.getClass().getName(),
                                                inData);
                }
            });
            mManagerBean = JMX.newMXBeanProxy(ManagementFactory.getPlatformMBeanServer(),
                                              new ObjectName(ModuleManager.MODULE_MBEAN_NAME),
                                              ModuleManagerMXBean.class);
        }
    }
    /**
     * Create a new StrategyAgent instance.
     */
    public StrategyAgent()
    {
        instance = this;
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
    boolean authenticate(StatelessClientContext context,
                                String user,
                                char[] password)
            throws I18NException
    {
        return authenticator.shouldAllow(context,
                                         user,
                                         password);
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
                } catch (Exception e) {
                    Messages.LOG_ERROR_EXEC_CMD.warn(this,
                            c.getRunner().getName(),
                            c.getParameter(), c.getLineNum(),
                            getMessage(e));
                    Messages.LOG_ERROR_EXEC_CMD.debug(this, e,
                            c.getRunner().getName(),
                            c.getParameter(), c.getLineNum(),
                            getMessage(e));
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
    private ModuleManager moduleManager;
    /**
     * SA class loader value
     */
    private ClassLoader loader;
    /**
     * The module manager mxbean reference.
     */
    private ModuleManagerMXBean mManagerBean;
    /**
     * The list of parsed commands.
     */
    private List<Command> mCommands = new LinkedList<Command>();
    /**
     * The handle to the remote web service.
     */
    private volatile ServiceInterface mRemoteService;
    /**
     * web services provider server
     */
    private volatile Server<ClientSession> mServer;
    /**
     * used to publish data received to interested subscribers
     */
    private volatile PublisherEngine dataPublisher;
    /**
     * extra context classes to add to the server context
     */
    private volatile Class<?>[] contextClasses;
    /**
     * most recent strategy agent instance
     */
    private volatile static StrategyAgent instance;
    /**
     * indicates if the SA is running or not
     */
    private final AtomicBoolean running = new AtomicBoolean(false);
    /**
     * provides authentication services
     */
    private Authenticator authenticator = new DefaultAuthenticator();
    /**
     * application context
     */
    private ApplicationContext applicationContext;
    /**
     * notification service, may be <code>null</code>
     */
    @Autowired(required=false)
    private NotificationExecutor notificationExecutor;
}
