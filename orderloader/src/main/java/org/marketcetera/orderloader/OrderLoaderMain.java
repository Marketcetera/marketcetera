package org.marketcetera.orderloader;

import static org.marketcetera.orderloader.Messages.*;

import java.io.File;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.cli.*;
import org.marketcetera.client.ClientParameters;
import org.marketcetera.core.ApplicationContainer;
import org.marketcetera.core.ApplicationVersion;
import org.marketcetera.trade.BrokerID;
import org.marketcetera.util.except.I18NException;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.misc.ClassVersion;
import org.springframework.context.Lifecycle;

/* $License$ */
/**
 * The entry point for running the order loader as an application
 *
 * @author anshul@marketcetera.com
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$")
public class OrderLoaderMain
        implements Lifecycle
{
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
        run(getArgs());
        running.set(true);
        exit();
    }
    /* (non-Javadoc)
     * @see org.springframework.context.Lifecycle#stop()
     */
    @Override
    public void stop()
    {
        running.set(false);
    }
    /**
     * Get the clientUsername value.
     *
     * @return a <code>String</code> value
     */
    public String getClientUsername()
    {
        return clientUsername;
    }
    /**
     * Sets the clientUsername value.
     *
     * @param inClientUsername a <code>String</code> value
     */
    public void setClientUsername(String inClientUsername)
    {
        clientUsername = inClientUsername;
    }
    /**
     * Sets the clientPassword value.
     *
     * @param inClientPassword a <code>char[]</code> value
     */
    public void setClientPassword(char[] inClientPassword)
    {
        clientPassword = inClientPassword;
    }
    /**
     * Get the clientURL value.
     *
     * @return a <code>String</code> value
     */
    public String getClientURL()
    {
        return clientURL;
    }
    /**
     * Sets the clientURL value.
     *
     * @param inClientURL a <code>String</code> value
     */
    public void setClientURL(String inClientURL)
    {
        clientURL = inClientURL;
    }
    /**
     * Get the clientWsHost value.
     *
     * @return a <code>String</code> value
     */
    public String getClientWsHost()
    {
        return clientWsHost;
    }
    /**
     * Sets the clientWsHost value.
     *
     * @param inClientWsHost a <code>String</code> value
     */
    public void setClientWsHost(String inClientWsHost)
    {
        clientWsHost = inClientWsHost;
    }
    /**
     * Get the clientIdPrefix value.
     *
     * @return a <code>String</code> value
     */
    public String getClientIdPrefix()
    {
        return clientIdPrefix;
    }
    /**
     * Sets the clientIdPrefix value.
     *
     * @param inClientIdPrefix a <code>String</code> value
     */
    public void setClientIdPrefix(String inClientIdPrefix)
    {
        clientIdPrefix = inClientIdPrefix;
    }
    /**
     * Get the clientWsPort value.
     *
     * @return a <code>String</code> value
     */
    public String getClientWsPort()
    {
        return clientWsPort;
    }
    /**
     * Sets the clientWsPort value.
     *
     * @param inClientWsPort a <code>String</code> value
     */
    public void setClientWsPort(String inClientWsPort)
    {
        clientWsPort = inClientWsPort;
    }
    /**
     * Sets the stream on which all the messages should be printed.
     *
     * @param inMsgStream the message stream on which all the messages should
     * be printed.
     */
    protected void setMsgStream(PrintStream inMsgStream)
    {
        mMsgStream = inMsgStream;
    }
    /**
     * Exits the current process with the supplied exit code.
     */
    protected void exit()
    {
        ApplicationContainer.stopInstanceWaiting();
    }
    /**
     * 
     *
     *
     * @return
     */
    protected String[] getArgs()
    {
        return ApplicationContainer.getInstanceArguments();
    }
    /**
     * Reads the orders from the supplied and sends them to the server.
     *
     * @throws Exception if there were errors.
     */
    protected void doProcessing()
            throws Exception
    {
        // create the order processor
        ClientParameters parameters = new ClientParameters(clientUsername,
                                                           clientPassword,
                                                           clientURL,
                                                           clientWsHost,
                                                           Integer.parseInt(clientWsPort),
                                                           clientIdPrefix);
        OrderProcessor processor = createProcessor(parameters);
        // run the order loader and display the summary of results.
        try {
            displaySummary(new OrderLoader(mMode,
                                           mBrokerID,
                                           processor,
                                           new File(mFilename)));
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
            throws Exception
    {
        return new ServerOrderProcessor(inParameters);
    }
    /**
     * Displays the summary of results after the order loader is done processing.
     *
     * @param inLoader the order loader instance.
     */
    protected void displaySummary(OrderLoader inLoader)
    {
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
    protected void printMessage(String inMessage)
    {
        mMsgStream.println(inMessage);
    }
    /**
     * Prints the supplied exception's message to the output.
     *
     * @param inException the exception whose messages should be
     * printed on the output.
     */
    protected void printError(Exception inException)
    {
        printMessage(getExceptionMsg(inException));
        SLF4JLoggerProxy.debug(this,
                               inException);
    }
    /**
     * Gets the exception message from the supplied exception.
     *
     * @param inException the exception
     *
     * @return the message from the exception.
     */
    protected String getExceptionMsg(Exception inException)
    {
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
     */
    private void run(String[] inArgs)
    {
        printMessage(LOG_APP_COPYRIGHT.getText());
        printMessage(LOG_APP_VERSION_BUILD.getText(ApplicationVersion.getVersion(),
                                                   ApplicationVersion.getBuildNumber()));
        try {
            execute(new GnuParser().parse(options(),
                                          inArgs));
        } catch (Exception e) {
            printError(e);
            usage();
        }
    }
    /**
     * Executes the given parsed command line.
     *
     * @param inLine a <code>CommandLine</code> value
     * @throws Exception if an error occurs parsing the command line
     */
    private void execute(CommandLine inLine)
            throws Exception
    {
        if(inLine.hasOption(OPT_USERNAME)) {
            clientUsername = inLine.getOptionValue(OPT_USERNAME);
        }
        if(inLine.hasOption(OPT_PASSWORD)) {
            clientPassword = inLine.getOptionValue(OPT_PASSWORD).toCharArray();
        }
        if(inLine.hasOption(OPT_MODE)) {
            mMode = inLine.getOptionValue(OPT_MODE);
        }
        if(inLine.hasOption(OPT_BROKER)) {
            mBrokerID = new BrokerID(inLine.getOptionValue(OPT_BROKER));
        }
        if(inLine.getArgList().isEmpty()) {
            throw new IllegalArgumentException(ERROR_MISSING_FILE.getText());
        } else {
            mFilename = String.valueOf(inLine.getArgList().remove(0));
        }
        if(!inLine.getArgList().isEmpty()) {
            throw new IllegalArgumentException(ERROR_TOO_MANY_ARGUMENTS.getText());
        }
        doProcessing();
    }
    /**
     * Constructs the command line options for the orderloader.
     *
     * @return an <code>Options</code> value
     */
    @SuppressWarnings("static-access")
    private static Options options()
    {
        Options opts = new Options();
        opts.addOption(OptionBuilder.hasArg().withArgName(ARG_MODE_VALUE.getText()).withDescription(ARG_MODE_DESCRIPTION.getText()).isRequired(false).create(OPT_MODE));
        opts.addOption(OptionBuilder.hasArg().withArgName(ARG_BROKER_VALUE.getText()).withDescription(ARG_BROKER_DESCRIPTION.getText()).isRequired(false).create(OPT_BROKER));
        opts.addOption(OptionBuilder.hasArg().withArgName(ARG_USERNAME_VALUE.getText()).withDescription(ARG_USERNAME_DESCRIPTION.getText()).isRequired(false).create(OPT_USERNAME));
        opts.addOption(OptionBuilder.hasArg().withArgName(ARG_PASSWORD_VALUE.getText()).withDescription(ARG_PASSWORD_DESCRIPTION.getText()).isRequired(false).create(OPT_PASSWORD));
        return opts;
    }
    /**
     * Prints the usage to the output.
     */
    private void usage()
    {
        HelpFormatter formatter = new HelpFormatter();
        PrintWriter pw = new PrintWriter(mMsgStream);
        pw.append(ERROR_USAGE.getText());
        pw.println();
        formatter.printOptions(pw,
                               HelpFormatter.DEFAULT_WIDTH,
                               options,
                               HelpFormatter.DEFAULT_LEFT_PAD,
                               HelpFormatter.DEFAULT_DESC_PAD);
        pw.println();
        pw.flush();
        exit();
    }
    /**
     * options value contains all command line options
     */
    private final Options options = options();
    /**
     * print stream used to render output
     */
    private PrintStream mMsgStream = System.err;
    /**
     * FIX mode to use
     */
    private String mMode;
    /**
     * broker ID to use
     */
    private BrokerID mBrokerID;
    /**
     * filename to parse
     */
    private String mFilename;
    /**
     * 
     */
    private static final String OPT_MODE = "m";  //$NON-NLS-1$
    /**
     * 
     */
    private static final String OPT_BROKER = "b";  //$NON-NLS-1$
    /**
     * 
     */
    private static final String OPT_USERNAME = "u";  //$NON-NLS-1$
    /**
     * 
     */
    private static final String OPT_PASSWORD = "p";  //$NON-NLS-1$
    /**
     * indicates if the order loader is running or not 
     */
    private final AtomicBoolean running = new AtomicBoolean(false);
    private String clientUsername;
    /**
     * 
     */
    private char[] clientPassword;
    /**
     * 
     */
    private String clientURL;
    /**
     * 
     */
    private String clientWsHost;
    /**
     * 
     */
    private String clientIdPrefix;
    /**
     * 
     */
    private String clientWsPort;
}
