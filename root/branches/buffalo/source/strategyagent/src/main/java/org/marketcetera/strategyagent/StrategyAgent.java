package org.marketcetera.strategyagent;

import java.io.IOException;
import java.io.LineNumberReader;
import java.lang.management.ManagementFactory;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.management.JMX;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.marketcetera.client.ClientManager;
import org.marketcetera.core.ApplicationVersion;
import org.marketcetera.core.Util;
import org.marketcetera.module.*;
import org.marketcetera.saclient.CreateStrategyParameters;
import org.marketcetera.saclient.SAClientVersion;
import org.marketcetera.util.except.I18NException;
import org.marketcetera.util.log.I18NBoundMessage2P;
import org.marketcetera.util.log.I18NBoundMessage3P;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.unicode.UnicodeFileReader;
import org.marketcetera.util.ws.stateful.ClientContext;
import org.marketcetera.util.ws.stateful.SessionManager;
import org.marketcetera.util.ws.stateless.StatelessClientContext;
import org.marketcetera.util.ws.wrappers.MapWrapper;
import org.marketcetera.util.ws.wrappers.RemoteException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.Lifecycle;

/* $License$ */
/**
 * The main class for the strategy agent.
 * 
 * <p>This class starts off the module container and then reads up a list of module manager commands
 * from a command file and executes it and waits until it's killed.
 * 
 * <p>If no commands file is supplied, the strategy agent does nothing, it simply waits until it's killed.
 * 
 * <p>After the strategy agent is started, clients can connect to it via
 * JMX and manage it via the {@link org.marketcetera.module.ModuleManagerMXBean}
 * interface. 
 *
 * @author anshul@marketcetera.com
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 */
@ClassVersion("$Id$")
public class StrategyAgent
        implements StrategyAgentInterface, InitializingBean, Lifecycle
{
    /* (non-Javadoc)
     * @see org.marketcetera.saclient.SAService#getProviders(org.marketcetera.util.ws.stateful.ClientContext)
     */
    @Override
    public List<ModuleURN> getProviders(ClientContext inCtx)
            throws RemoteException
    {
        return service.getProviders(inCtx);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.saclient.SAService#getInstances(org.marketcetera.util.ws.stateful.ClientContext, org.marketcetera.module.ModuleURN)
     */
    @Override
    public List<ModuleURN> getInstances(ClientContext inCtx,
                                        ModuleURN inProviderURN)
            throws RemoteException
    {
        return service.getInstances(inCtx,
                                    inProviderURN);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.saclient.SAService#getModuleInfo(org.marketcetera.util.ws.stateful.ClientContext, org.marketcetera.module.ModuleURN)
     */
    @Override
    public ModuleInfo getModuleInfo(ClientContext inCtx,
                                    ModuleURN inURN)
            throws RemoteException
    {
        return service.getModuleInfo(inCtx,
                                     inURN);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.saclient.SAService#start(org.marketcetera.util.ws.stateful.ClientContext, org.marketcetera.module.ModuleURN)
     */
    @Override
    public void start(ClientContext inCtx,
                      ModuleURN inURN)
            throws RemoteException
    {
        service.start(inCtx,
                      inURN);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.saclient.SAService#stop(org.marketcetera.util.ws.stateful.ClientContext, org.marketcetera.module.ModuleURN)
     */
    @Override
    public void stop(ClientContext inCtx,
                     ModuleURN inURN)
            throws RemoteException
    {
        service.stop(inCtx,
                     inURN);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.saclient.SAService#delete(org.marketcetera.util.ws.stateful.ClientContext, org.marketcetera.module.ModuleURN)
     */
    @Override
    public void delete(ClientContext inCtx,
                       ModuleURN inURN)
            throws RemoteException
    {
        service.delete(inCtx,
                       inURN);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.saclient.SAService#getProperties(org.marketcetera.util.ws.stateful.ClientContext, org.marketcetera.module.ModuleURN)
     */
    @Override
    public MapWrapper<String,Object> getProperties(ClientContext inCtx,
                                                   ModuleURN inURN)
            throws RemoteException
    {
        return service.getProperties(inCtx,
                                     inURN);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.saclient.SAService#setProperties(org.marketcetera.util.ws.stateful.ClientContext, org.marketcetera.module.ModuleURN, org.marketcetera.util.ws.wrappers.MapWrapper)
     */
    @Override
    public MapWrapper<String,Object> setProperties(ClientContext inCtx,
                                                   ModuleURN inURN,
                                                   MapWrapper<String, Object> inProperties)
            throws RemoteException
    {
        return service.setProperties(inCtx,
                                     inURN,
                                     inProperties);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.saclient.SAService#createStrategy(org.marketcetera.util.ws.stateful.ClientContext, org.marketcetera.saclient.CreateStrategyParameters)
     */
    @Override
    public ModuleURN createStrategy(ClientContext inCtx,
                                    CreateStrategyParameters inParameters)
            throws RemoteException
    {
        return service.createStrategy(inCtx,
                                      inParameters);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.saclient.SAService#getStrategyCreateParms(org.marketcetera.util.ws.stateful.ClientContext, org.marketcetera.module.ModuleURN)
     */
    @Override
    public CreateStrategyParameters getStrategyCreateParms(ClientContext inServiceContext,
                                                           ModuleURN inURN)
            throws RemoteException
    {
        return service.getStrategyCreateParms(inServiceContext,
                                              inURN);
    }
    /* (non-Javadoc)
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    @Override
    public void afterPropertiesSet()
            throws Exception
    {
        try {
            Validate.notNull(moduleManager,
                             "Strategy Agent module manager must not be null");
            Validate.notNull(moduleLoader,
                             "Strategy Agent module loader must not be null");
        } catch (Exception e) {
            SLF4JLoggerProxy.error(StrategyAgent.class,
                                   e);
            throw e;
        }
    }
    /* (non-Javadoc)
     * @see org.springframework.context.Lifecycle#start()
     */
    @Override
    public void start()
    {
        Messages.LOG_APP_COPYRIGHT.info(StrategyAgent.class);
        Messages.LOG_APP_VERSION_BUILD.info(StrategyAgent.class,
                                            ApplicationVersion.getVersion(),
                                            ApplicationVersion.getBuildNumber());
        try {
            // configure the agent
            configure();
            if(argument != null) {
                int parseErrors = parseCommands(argument);
                if(parseErrors > 0) {
                    Messages.LOG_COMMAND_PARSE_ERRORS.error(StrategyAgent.class,
                                                            parseErrors);
                    return;
                }
            }
        } catch (Exception e) {
            Messages.LOG_ERROR_CONFIGURE_AGENT.error(StrategyAgent.class,
                    getMessage(e));
            Messages.LOG_ERROR_CONFIGURE_AGENT.debug(StrategyAgent.class,
                    e,
                    getMessage(e));
            return;
        }
        //Initialize the application. If it fails, exit
        try {
            init();
        } catch(Exception e) {
            Messages.LOG_ERROR_INITIALIZING_AGENT.error(StrategyAgent.class,
                    getMessage(e));
            Messages.LOG_ERROR_INITIALIZING_AGENT.debug(StrategyAgent.class,
                    e,
                    getMessage(e));
            return;
        }
        // run the commands - if commands fail, the failure is logged, but the agent doesn't exit.
        executeCommands();
        SessionManager<ClientSession> sessionManager = new SessionManager<ClientSession>(new ClientSessionFactory(),
                                                                                         SessionManager.INFINITE_SESSION_LIFESPAN);
        service = new SAServiceImpl(sessionManager,
                                    moduleManager);
    }
    /* (non-Javadoc)
     * @see org.springframework.context.Lifecycle#isRunning()
     */
    @Override
    public boolean isRunning()
    {
        return true;
    }
    /* (non-Javadoc)
     * @see org.springframework.context.Lifecycle#stop()
     */
    @Override
    public void stop()
    {
    }
    /**
     * 
     *
     *
     * @param inArguments
     */
    public void setArgument(String inArgument)
    {
        argument = StringUtils.trimToNull(inArgument);
    }
    /**
     * Runs the application instance with the supplied set of arguments.
     *
     * @param inAgent the application instance
     * @param args the command line arguments to the instance.
     */
    protected static void run(StrategyAgent inAgent,
                              String[] args)
    {
        // TODO this should be removed
//        try {
//            //Configure the application. If it fails, exit
//            inAgent.configure();
//            if(args.length > 0) {
//                int parseErrors = inAgent.parseCommands(args[0]);
//                if(parseErrors > 0) {
//                    Messages.LOG_COMMAND_PARSE_ERRORS.error(
//                            StrategyAgent.class, parseErrors);
//                    inAgent.exit(EXIT_CMD_PARSE_ERROR);
//                    return;
//                }
//            }
//        } catch (Throwable e) {
//            Messages.LOG_ERROR_CONFIGURE_AGENT.error(StrategyAgent.class,
//                    getMessage(e));
//            Messages.LOG_ERROR_CONFIGURE_AGENT.debug(StrategyAgent.class,
//                    e, getMessage(e));
//            inAgent.exit(EXIT_START_ERROR);
//            return;
//        }
//        //Initialize the application. If it fails, exit
//        try {
//            inAgent.init();
//        } catch (Throwable e) {
//            Messages.LOG_ERROR_INITIALIZING_AGENT.error(StrategyAgent.class,
//                    getMessage(e));
//            Messages.LOG_ERROR_INITIALIZING_AGENT.debug(StrategyAgent.class,
//                    e, getMessage(e));
//            inAgent.exit(EXIT_INIT_ERROR);
//            return;
//        }
//        //Run the commands, if commands fail, the failure is logged, but
//        //the application doesn't exit.
//        inAgent.executeCommands();
//        //Wait forever, do not exit unless killed.
//        // TODO
////        inAgent.startWaitingForever();
    }

    /**
     * Terminates the process with the supplied exit code.
     *
     * @param inExitCode the exit code.
     */
    protected void exit(int inExitCode) {
        // TODO
//        System.exit(inExitCode);
    }

    /**
     * Returns the module manager.
     * This method is exposed to aid testing.
     *
     * @return the module manager.
     */
    protected ModuleManager getManager() {
        return moduleManager;
    }
    /**
     * Return the exception message from the supplied Throwable.
     *
     * @param inThrowable the throwable whose message needs to be returned.
     *
     * @return the throwable message.
     */
    private static String getMessage(Throwable inThrowable)
    {
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
    private int parseCommands(String inFile)
            throws IOException
    {
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
     * 
     *
     *
     * @param inModuleManager
     */
    public void setModuleManager(ModuleManager inModuleManager)
    {
        moduleManager = inModuleManager;
    }
    /**
     * 
     *
     *
     * @return
     */
    public ModuleManager getModuleManager()
    {
        return moduleManager;
    }
    /**
     * 
     *
     *
     * @param inLoader
     */
    public void setCurrentLoader(JarClassLoader inLoader)
    {
        moduleLoader = inLoader;
    }
    public JarClassLoader getCurrentLoader()
    {
        return moduleLoader;
    }
    /**
     * Configures the agent. Initializes the spring configuration to get
     * a properly configured module manager instance.
     */
    private void configure()
    {
        // TODO
//        File modulesDir = new File(APP_DIR,"modules");  //$NON-NLS-1$
//        StaticApplicationContext parentCtx = new StaticApplicationContext();
//        // provide the module jar directory path to the spring context.
//        SpringUtils.addStringBean(parentCtx,
//                                  "modulesDir",   //$NON-NLS-1$
//                                  modulesDir.getAbsolutePath());
//        parentCtx.refresh();
//        mContext = new ClassPathXmlApplicationContext(new String[] { "modules.xml" },  //$NON-NLS-1$
//                                                      parentCtx);
//        mContext.registerShutdownHook();
//        moduleManager = (ModuleManager)mContext.getBean("moduleManager",  //$NON-NLS-1$
//                                                   ModuleManager.class);
        //Set the context classloader to the jar classloader so that
        //all modules have the thread context classloader set to the same
        //value as the classloader that loaded them.
//        ClassLoader loader = (ClassLoader)mContext.getBean("moduleLoader",  //$NON-NLS-1$
//                                                           ClassLoader.class);
        Thread.currentThread().setContextClassLoader(moduleLoader);
        //Setup the WS services after setting up the context class loader.
//        String hostname = (String) mContext.getBean("wsServerHost");  //$NON-NLS-1$
//        if(hostname != null &&
//           !hostname.trim().isEmpty()) {
//            int port = (Integer)mContext.getBean("wsServerPort");  //$NON-NLS-1$
//            SessionManager<ClientSession> sessionManager = new SessionManager<ClientSession>(new ClientSessionFactory(),
//                                                                                             SessionManager.INFINITE_SESSION_LIFESPAN);
//            mServer = new Server<ClientSession>(hostname,
//                                                port,
//                                                new Authenticator() {
//                @Override
//                public boolean shouldAllow(StatelessClientContext context,
//                                           String user,
//                                           char[] password)
//                throws I18NException
//                {
//                    return authenticate(context,
//                                        user,
//                                        password);
//                }
//            },sessionManager);
//            mRemoteService = mServer.publish(new SAServiceImpl(sessionManager,
//                                                               moduleManager),
//                                             SAService.class);
//            //Register a shutdown task to shutdown the remote service.
//            Runtime.getRuntime().addShutdownHook(new Thread(){
//                @Override
//                public void run() {
//                    stopRemoteService();
//                }
//            });
//            Messages.LOG_REMOTE_WS_CONFIGURED.info(this, hostname, String.valueOf(port));
//        }
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
            throws I18NException
    {
        //Verify client version
        String serverVersion = ApplicationVersion.getVersion();
        String clientName = Util.getName(context.getAppId());
        String clientVersion = Util.getVersion(context.getAppId());
        if(!compatibleApp(clientName)) {
            throw new I18NException
                    (new I18NBoundMessage2P(Messages.APP_MISMATCH,
                            clientName, user));
        }
        if(!compatibleVersions(clientVersion, serverVersion)) {
            throw new I18NException
                    (new I18NBoundMessage3P(Messages.VERSION_MISMATCH,
                            clientVersion, serverVersion, user));
        }
        // Use client to carry out authentication.
        return ClientManager.getInstance().isCredentialsMatch(user, password);
    }
    /**
     * Initializes the module manager.
     *
     * @throws ModuleException if there were errors initializing the module manager.
     * @throws MalformedObjectNameException if there were errors creating the object name of the module manager bean.
     */
    private void init()
            throws ModuleException, MalformedObjectNameException
    {
        //Initialize the module manager.
        moduleManager.init();
        //Add the logger sink listener
        moduleManager.addSinkListener(new SinkDataListener() {
            public void receivedData(DataFlowID inFlowID, Object inData) {
                final boolean isNullData = inData == null;
                Messages.LOG_SINK_DATA.info(SINK_DATA, inFlowID,
                        isNullData ? 0: 1,
                        isNullData ? null: inData.getClass().getName(),
                        inData);
            }
        });
        mManagerBean = JMX.newMXBeanProxy(ManagementFactory.getPlatformMBeanServer(),
                                          new ObjectName(ModuleManager.MODULE_MBEAN_NAME),
                                          ModuleManagerMXBean.class);
    }
    /**
     * Executes commands, if any were provided. If any command fails, the
     * failure is logged. Failure of any command doesn't prevent the next
     * command from executing or prevent the application from exiting.
     */
    private void executeCommands()
    {
        if(!mCommands.isEmpty()) {
            for(Command c: mCommands) {
                try {
                    Messages.LOG_RUNNING_COMMAND.info(this,
                                                      c.getRunner().getName(),
                                                      c.getParameter());
                    Object result = c.getRunner().runCommand(mManagerBean,
                                                             c.getParameter());
                    Messages.LOG_COMMAND_RUN_RESULT.info(this,
                                                         c.getRunner().getName(),
                                                         result);
                } catch (Exception t) {
                    Messages.LOG_ERROR_EXEC_CMD.warn(this,
                                                     c.getRunner().getName(),
                                                     c.getParameter(),
                                                     c.getLineNum(),
                                                     getMessage(t));
                    Messages.LOG_ERROR_EXEC_CMD.debug(this,
                                                      t,
                                                      c.getRunner().getName(),
                                                      c.getParameter(),
                                                      c.getLineNum(),
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
    private static void addRunner(CommandRunner inRunner)
    {
        sRunners.put(inRunner.getName(),
                     inRunner);
    }
    /**
     * Checks for compatibility between the given client and server
     * versions.
     *
     * @param clientVersion The client version.
     * @param serverVersion The server version.
     * @return True if the two versions are compatible.
     */
    private static boolean compatibleVersions(String clientVersion, String serverVersion)
    {
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
    private static boolean compatibleApp(String clientName)
    {
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
    private static final Map<String, CommandRunner> sRunners = new HashMap<String, CommandRunner>();

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
     * The module manager mxbean reference.
     */
    private ModuleManagerMXBean mManagerBean;
    /**
     * The list of parsed commands.
     */
    private List<Command> mCommands = new LinkedList<Command>();
    /**
     * 
     */
    private volatile JarClassLoader moduleLoader;
    /**
     * 
     */
    private volatile String argument;
    /**
     * 
     */
    private volatile SAServiceImpl service;
//    /**
//     * The handle to the remote web service.
//     */
//    private volatile ServiceInterface mRemoteService;
//    /**
//     * 
//     */
//    private volatile ClassPathXmlApplicationContext mContext;
//    /**
//     * 
//     */
//    private volatile Server<ClientSession> mServer;
}
