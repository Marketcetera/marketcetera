package org.marketcetera.orderloader;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.auth.StandardAuthentication;
import org.marketcetera.util.auth.OptionsProvider;
import static org.marketcetera.util.auth.StandardAuthentication.*;
import org.marketcetera.util.except.I18NException;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.apache.log4j.PropertyConfigurator;
import org.apache.commons.cli.*;
import static org.marketcetera.core.ApplicationBase.*;
import org.marketcetera.core.ApplicationVersion;
import static org.marketcetera.orderloader.Messages.*;
import org.marketcetera.trade.BrokerID;
import org.marketcetera.client.ClientParameters;
import org.springframework.context.support.StaticApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import java.io.PrintStream;
import java.io.File;
import java.util.List;
import java.util.Arrays;

/* $License$ */
/**
 * The entry point for running the order loader as an application
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$")
public class Main {

    /**
     * Runs the order loader given the array of command line arguments.
     *
     * @param inArgs the arguments to the order loader.
     */
    public static void main(String []inArgs) {
        PropertyConfigurator.configureAndWatch
            (CONF_DIR + LOGGER_CONF_FILE, LOGGER_WATCH_DELAY);
        LOG_APP_COPYRIGHT.info(Main.class);
        LOG_APP_VERSION_BUILD.info(Main.class,
                ApplicationVersion.getVersion(),
                ApplicationVersion.getBuildNumber());
        Main main = new Main();
        run(inArgs, main);
    }

    /**
     * Sets the stream on which all the messages should be printed.
     *
     * @param inMsgStream the message stream on which all the messages should
     * be printed.
     */
    protected void setMsgStream(PrintStream inMsgStream) {
        mMsgStream = inMsgStream;
    }

    /**
     * Processes the supplied command line options and configures
     * the order loader to run per the options.
     *
     * @param inArgs the command line arguments.
     *
     * @return true if the argument processing succeeded, false if it
     * did not. Do note the return value only matters for unit testing.
     * When used by the user, if there's an error, this method will never
     * return, it will exit the process.
     */
    protected boolean processArguments(String[] inArgs) {
        //Get user name password
        mAuthentication = new StandardAuthentication(CFG_BASE_FILE_NAME,
                PROPERTIES_FILE_BEAN, USER_PROPERTY, PASSWORD_PROPERTY,
                USER_SHORT, USER_LONG, PASSWORD_SHORT, PASSWORD_LONG, inArgs);
        mAuthentication.getCliContext().setOptionsProvider(new OptionsProvider() {
            public void addOptions(Options inOptions) {
                options(inOptions);
            }
        });
        if (!mAuthentication.setValues()) {
            usage();
            return false;
        }
        //Check for invalid command line arg syntax.
        ParseException parseException = mAuthentication.getCliContext().getParseException();
        if(parseException != null) {
            printError(parseException);
            usage();
            return false;
        }
        //Process non-authentication parameters
        CommandLine cmdLine = mAuthentication.getCliContext().getCommandLine();
        //Mode of operation
        mMode = cmdLine.getOptionValue(OPT_MODE);
        String broker = cmdLine.getOptionValue(OPT_BROKER);
        //Broker ID
        mBrokerID = broker == null
                ? null
                : new BrokerID(broker);
        //The input file path
        inArgs= mAuthentication.getOtherArgs();
        if (inArgs.length < 1) {
            printMessage(ERROR_MISSING_FILE.getText());
            usage();
            return false;
        }
        if (inArgs.length > 1) {
            printMessage(ERROR_TOO_MANY_ARGUMENTS.getText());
            usage();
            return false;
        }
        mFileName = inArgs[0];
        return true;
    }

    /**
     * Exits the current process with the supplied exit code.
     *
     * @param inCode the exit code for the process.
     */
    protected void exit(int inCode) {
        System.exit(inCode);
    }

    /**
     * Reads the orders from the supplied and sends them to the server.
     *
     * @throws Exception if there were errors.
     */
    protected void doProcessing()
            throws Exception {
        //Create the order processor
        StaticApplicationContext context =
            new StaticApplicationContext
            (new FileSystemXmlApplicationContext(CFG_BASE_FILE_NAME));
        String clientURL = (String) context.getBean("clientURL");  //$NON-NLS-1$
        String clientWSHost = (String) context.getBean("clientWSHost");  //$NON-NLS-1$
        Integer clientWSPort = (Integer) context.getBean("clientWSPort");  //$NON-NLS-1$
        String clientIDPrefix = (String) context.getBean("clientIDPrefix");  //$NON-NLS-1$
        ClientParameters parameters = new ClientParameters(
                mAuthentication.getUser(),
                mAuthentication.getPassword(),
                clientURL, clientWSHost, clientWSPort,clientIDPrefix);
        OrderProcessor processor = createProcessor(parameters);
        //Run the order loader and display the summary of results.
        try {
            displaySummary(new OrderLoader(mMode, mBrokerID,
                    processor, new File(mFileName)));
        } finally {
            processor.done();
        }
    }

    /**
     * Creates a processor given the parameters.
     * <p>
     * This method is an implementation artifact to aid unit testing.
     *
     * @param inParameters the parameters to connect to the server.
     *
     * @return the order processor that will send orders to the server.
     *
     * @throws Exception if there were errors creating the order processor.
     */
    protected OrderProcessor createProcessor(ClientParameters inParameters)
            throws Exception {
        return new ServerOrderProcessor(inParameters);
    }

    /**
     * Displays the summary of results after the order loader is done
     * processing.
     *
     * @param inLoader the order loader instance.
     */
    protected void displaySummary(OrderLoader inLoader) {
        printMessage(LINE_SUMMARY.getText(inLoader.getNumLines(),
                inLoader.getNumBlankLines(),
                inLoader.getNumComments()));
        printMessage(ORDER_SUMMARY.getText(inLoader.getNumSuccess(),
                inLoader.getNumFailed()));
        List<FailedOrderInfo> list = inLoader.getFailedOrders();
        if(!list.isEmpty()) {
            printMessage(FAILED_ORDERS.getText());
            for(FailedOrderInfo info: list) {
                printMessage(FAILED_ORDER.getText(info.getIndex(),
                        Arrays.toString(info.getRow()),
                        getExceptionMsg(info.getException())));
            }
        }
    }

    /**
     * Prints the message to the output.
     *
     * @param inMessage the message to print.
     */
    protected void printMessage(String inMessage) {
        mMsgStream.println(inMessage);
    }

    /**
     * Prints the supplied exception's message to the output.
     *
     * @param inException the exception whose messages should be
     * printed on the output.
     */
    protected void printError(Exception inException) {
        printMessage(getExceptionMsg(inException));
        SLF4JLoggerProxy.debug(this,inException);
    }

    /**
     * Gets the exception message from the supplied exception.
     *
     * @param inException the exception
     *
     * @return the message from the exception.
     */
    protected String getExceptionMsg(Exception inException) {
        if(inException instanceof I18NException) {
            return ((I18NException)inException).getLocalizedDetail();
        } else {
            return inException.getLocalizedMessage();
        }
    }

    /**
     * Runs the supplied instance given the arguments.
     *
     * @param inArgs the command line arguments to run.
     * @param inMain the instance that needs to be run.
     */
    static void run(String[] inArgs, Main inMain){
        inMain.printMessage(LOG_APP_COPYRIGHT.getText());
        inMain.printMessage(LOG_APP_VERSION_BUILD.getText(
                ApplicationVersion.getVersion(),
                ApplicationVersion.getBuildNumber()));
        if (inMain.processArguments(inArgs)) {
            try {
                inMain.doProcessing();
                inMain.exit(EXIT_CODE_SUCCESS);
            } catch (Exception e) {
                inMain.printError(e);
                inMain.exit(EXIT_CODE_FAILURE);
            }
        }
    }

    /**
     * Prints the usage to the output.
     */
    private void usage()
    {
        printMessage(ERROR_USAGE.getText());
        printMessage("");  //$NON-NLS-1$
        printMessage(ERROR_EXAMPLE.getText());
        printMessage("");  //$NON-NLS-1$
        printMessage(USAGE_LOADER_OPTIONS.getText());
        printMessage(USAGE_MODE.getText());
        printMessage(USAGE_BROKER_ID.getText());
        printMessage("");  //$NON-NLS-1$
        printMessage(ERROR_AUTHENTICATION.getText());
        mAuthentication.printUsage(mMsgStream);
        exit(EXIT_CODE_USAGE);
    }

    /**
     * Returns the command line options for the optional arguments.
     *
     * @param inOptions the options used for parsing the command line.
     */
    private void options(Options inOptions) {
        inOptions.addOption(OptionBuilder.hasArg().
                withArgName(ARG_MODE_VALUE.getText()).
                withDescription(ARG_MODE_DESCRIPTION.getText()).
                isRequired(false).create(OPT_MODE));
        inOptions.addOption(OptionBuilder.hasArg().
                withArgName(ARG_BROKER_VALUE.getText()).
                withDescription(ARG_BROKER_DESCRIPTION.getText()).
                isRequired(false).create(OPT_BROKER));
    }
    private PrintStream mMsgStream = System.err;
    private StandardAuthentication mAuthentication;
    private String mMode;
    private BrokerID mBrokerID;
    private String mFileName;
    private static final String CFG_BASE_FILE_NAME=
        "file:" + CONF_DIR + "orderloader.xml"; //$NON-NLS-1$ //$NON-NLS-2$
    private static final String OPT_MODE = "m";  //$NON-NLS-1$
    private static final String OPT_BROKER = "b";  //$NON-NLS-1$
    private static final String USER_PROPERTY = "metc.client.user";  //$NON-NLS-1$
    private static final String PASSWORD_PROPERTY = "metc.client.password";  //$NON-NLS-1$
    static final int EXIT_CODE_FAILURE = 2;
    static final int EXIT_CODE_USAGE = 1;
    static final int EXIT_CODE_SUCCESS = 0;
}
