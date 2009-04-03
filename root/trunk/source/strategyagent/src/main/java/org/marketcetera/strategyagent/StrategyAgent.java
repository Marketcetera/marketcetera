package org.marketcetera.strategyagent;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.unicode.UnicodeFileReader;
import org.marketcetera.util.spring.SpringUtils;
import org.marketcetera.util.except.I18NException;
import org.marketcetera.core.ApplicationBase;
import org.marketcetera.core.ApplicationVersion;
import org.marketcetera.module.*;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.StaticApplicationContext;
import org.apache.log4j.PropertyConfigurator;

import javax.management.JMX;
import javax.management.ObjectName;
import javax.management.MalformedObjectNameException;
import java.io.LineNumberReader;
import java.io.IOException;
import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.List;
import java.lang.management.ManagementFactory;

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
@ClassVersion("$Id$") //$NON-NLS-1$
public class StrategyAgent extends ApplicationBase {
    /**
     * Creates and runs the application.
     *
     * @param args the command line arguments.
     */
    public static void main(String[] args) {
        initializeLogger(LOGGER_CONF_FILE);
        Messages.LOG_APP_COPYRIGHT.info(StrategyAgent.class);
        Messages.LOG_APP_VERSION_BUILD.info(StrategyAgent.class,
                ApplicationVersion.getVersion(),
                ApplicationVersion.getBuildNumber());
        //Run the application.
        run(new StrategyAgent(), args);
    }

    /**
     * Runs the application instance with the supplied set of arguments.
     *
     * @param inAgent the application instance
     * @param args the command line arguments to the instance.
     */
    static void run(StrategyAgent inAgent, String[] args) {
        try {
            //Configure the application. If it fails, exit
            inAgent.configure();
            if(args.length > 0) {
                int parseErrors = inAgent.parseCommands(args[0]);
                if(parseErrors > 0) {
                    Messages.LOG_COMMAND_PARSE_ERRORS.error(
                            StrategyAgent.class, parseErrors);
                    inAgent.exit(EXIT_CMD_PARSE_ERROR);
                    return;
                }
            }
        } catch (Throwable e) {
            Messages.LOG_ERROR_CONFIGURE_AGENT.error(StrategyAgent.class,
                    getMessage(e));
            Messages.LOG_ERROR_CONFIGURE_AGENT.debug(StrategyAgent.class,
                    e, getMessage(e));
            inAgent.exit(EXIT_START_ERROR);
            return;
        }
        //Initialize the application. If it fails, exit
        try {
            inAgent.init();
        } catch (Throwable e) {
            Messages.LOG_ERROR_INITIALIZING_AGENT.error(StrategyAgent.class,
                    getMessage(e));
            Messages.LOG_ERROR_INITIALIZING_AGENT.debug(StrategyAgent.class,
                    e, getMessage(e));
            inAgent.exit(EXIT_INIT_ERROR);
            return;
        }
        //Run the commands, if commands fail, the failure is logged, but
        //the application doesn't exit.
        inAgent.executeCommands();
        //Wait forever, do not exit unless killed.
        inAgent.startWaitingForever();
    }

    /**
     * Terminates the process with the supplied exit code.
     *
     * @param inExitCode the exit code.
     */
    void exit(int inExitCode) {
        System.exit(inExitCode);
    }

    /**
     * Returns the module manager.
     * This method is exposed to aid testing.
     *
     * @return the module manager.
     */
    ModuleManager getManager() {
        return mManager;
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
     * Initializes the logger for this application.
     *
     * @param logConfig the logger configuration file.
     */
    private static void initializeLogger(String logConfig)
    {
        PropertyConfigurator.configureAndWatch
            (ApplicationBase.CONF_DIR+logConfig, LOGGER_WATCH_DELAY);
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
     * Configures the agent. Initializes the spring configuration to get
     * a properly configured module manager instance.
     */
    private void configure() {
        File modulesDir = new File(APP_DIR,"modules");  //$NON-NLS-1$
        StaticApplicationContext parentCtx = new StaticApplicationContext();
        //Provide the module jar directory path to the spring context.
        SpringUtils.addStringBean(parentCtx,"modulesDir",   //$NON-NLS-1$
                modulesDir.getAbsolutePath());
        parentCtx.refresh();
        ConfigurableApplicationContext ctx = new ClassPathXmlApplicationContext(
                new String[]{"modules.xml"},parentCtx);  //$NON-NLS-1$
        ctx.registerShutdownHook();
        mManager = (ModuleManager) ctx.getBean("moduleManager",  //$NON-NLS-1$
                ModuleManager.class);
        //Set the context classloader to the jar classloader so that
        //all modules have the thread context classloader set to the same
        //value as the classloader that loaded them.
        ClassLoader loader = (ClassLoader) ctx.getBean("moduleLoader",  //$NON-NLS-1$
                ClassLoader.class);
        Thread.currentThread().setContextClassLoader(loader);
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
}
