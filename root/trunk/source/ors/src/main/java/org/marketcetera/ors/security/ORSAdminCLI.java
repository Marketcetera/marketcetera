package org.marketcetera.ors.security;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.core.MessageBundleManager;
import static org.marketcetera.ors.security.Messages.*;
import org.marketcetera.ors.OrderRoutingSystem;
import org.marketcetera.util.except.I18NException;
import org.marketcetera.util.log.I18NBoundMessage1P;
import org.marketcetera.util.log.I18NMessage1P;
import org.marketcetera.util.misc.SystemProperties;
import org.marketcetera.persist.PersistenceException;
import org.marketcetera.persist.StringFilter;

import org.apache.commons.cli.*;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Console;
import java.util.List;
import java.util.Arrays;

/* $License$ */
/**
 * The CLI to manage users and password on ORS.
 * Invoke {@link #parseAndRun(String[])} to run a CLI command. This method
 * can be invoked multiple times to invoke several commands.
 * The CLI instance can eventually be destroyed by invoking {@link #close()} 
 *
 * @author anshul@marketcetera.com
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public class ORSAdminCLI {
    private AbstractApplicationContext context;


    /**
     * Creates an instance
     * @param out The output stream to write output to
     * @param err The error stream to write error output to
     */
    public ORSAdminCLI(PrintStream out, PrintStream err) {
        MessageBundleManager.registerCoreMessageBundle();
        OrderRoutingSystem.initializeLogger("log4j-cli.properties"); //$NON-NLS-1$
        this.out = out;
        this.err = err;
        context = new ClassPathXmlApplicationContext(getConfigurations(),
                new FileSystemXmlApplicationContext(
                        OrderRoutingSystem.CFG_BASE_FILE_NAME));
        context.registerShutdownHook();
    }

    public static void main(String[] args) {
        ORSAdminCLI cli = new ORSAdminCLI(System.out,System.err);
        try {
            cli.parseAndRun(args);
            System.exit(0);
        } catch (Exception e) {
            System.exit(1);
        }
    }

    /**
     * Closes the application. Destroys all the resources that were
     * used by the application.
     */
    public void close() {
        context.close();
    }

    /**
     * Parses and runs the supplied command. This method
     * can be invoked multiple times.
     *
     * @param args the arguments per the usage of the CLI
     *
     * @throws Exception if there were errors running the command
     */
    public void parseAndRun(String... args) throws Exception {
        try {
            execute(new GnuParser().parse(options(),args));
        } catch (Exception e) {
            printError(e.getLocalizedMessage());
            printUsage();
            throw e;
        }
    }

    /**
     * Returns a list of spring configurations that should be used
     * to configure the CLI
     *
     * @return the list of spring configurations
     */
    protected String[] getConfigurations() {
        return new String[]{"ors_orm_vendor.xml", "ors_orm.xml", "ors_db.xml"}; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    }

    /**
     * Reads the password from the console if one is available.
     *
     * @param message the password prompt to display to the user
     * 
     * @return the password, null, if no console is available or
     * if end of stream is reached.
     */
    protected char[] readPasswordFromConsole(String message) {
        Console console = System.console();
        if(console == null) {
            return null;
        } else {
            return console.readPassword(message);
        }
    }

    /**
     * Executes the command line based on the supplied options
     *
     * @param commandLine the parsed command line
     *
     * @throws I18NException if there were errors in the command line options
     * or if there were errors executing commands.
     */
    private void execute(CommandLine commandLine) throws I18NException {
        String userName = commandLine.getOptionValue(OPT_CURRENT_USER);
        String password = commandLine.getOptionValue(OPT_CURRENT_PASSWORD);
        String opUser = commandLine.getOptionValue(OPT_OPERATED_USER);
        String opPass = commandLine.getOptionValue(OPT_OPERATED_PASSWORD);
        if(commandLine.hasOption(CMD_ADD_USER)) {
            if(!commandLine.hasOption(OPT_OPERATED_USER)) {
                throw new I18NException(new I18NBoundMessage1P(
                        CLI_ERR_OPTION_MISSING, OPT_OPERATED_USER));
            }
            if(!commandLine.hasOption(OPT_OPERATED_PASSWORD)) {
                opPass = getOptionFromConsole(opUser, opPass,
                        OPT_OPERATED_PASSWORD, CLI_PROMPT_PASSWORD);
            }
            authorize(Authorization.ADD_USER,userName,password);
            addUser(opUser, opPass);
        } else if (commandLine.hasOption(CMD_DELETE_USER)) {
            if(!commandLine.hasOption(OPT_OPERATED_USER)) {
                throw new I18NException(new I18NBoundMessage1P(
                        CLI_ERR_OPTION_MISSING, OPT_OPERATED_USER));
            }
            authorize(Authorization.DELETE_USER,userName,password);
            deleteUser(opUser);
        } else if (commandLine.hasOption(CMD_LIST_USERS)) {
            authorize(Authorization.LIST_USERS,userName,password);
            listUsers(opUser);
        } else if (commandLine.hasOption(CMD_CHANGE_PASS)) {
            //The order of these statements is important as it
            //determines the order in which the user is prompted
            //First we want to validate the login password
            //and then we'd want to prompt for the new password
            //if one wasn't supplied on the command line

            //Get the login password
            if(!commandLine.hasOption(OPT_CURRENT_PASSWORD)) {
                password = getOptionFromConsole(userName, password,
                        OPT_CURRENT_PASSWORD, CLI_PROMPT_PASSWORD);
            }
            //Get the new password
            if(!commandLine.hasOption(OPT_OPERATED_PASSWORD)) {
                String curUser = opUser == null? userName: opUser;
                opPass = getOptionFromConsole(curUser, opPass,
                        OPT_OPERATED_PASSWORD, CLI_PROMPT_NEW_PASSWORD);
            }
            //Only authorize if changing password for a different user
            if(commandLine.hasOption(OPT_OPERATED_USER) &&
                    !userName.equals(opUser)) {
                authorize(Authorization.CHANGE_PASSWORD,userName,password);
            }
            changePassword(userName, opUser, password, opPass);
        } else {
            //A MissingOptionException would have been already thrown
            throw new IllegalStateException();
        }
    }
    private void authorize(Authorization auth,
                           String userName,
                           String password) throws I18NException {
        password = getOptionFromConsole(userName, password,
                OPT_CURRENT_PASSWORD, CLI_PROMPT_PASSWORD);
        auth.authorize(userName, password);
    }

    private String getOptionFromConsole(String userName,
                                        String password,
                                        String optName,
                                        I18NMessage1P prompt)
            throws I18NException {
        if(password == null) {
            char[] p = readPasswordFromConsole(
                    prompt.getText(userName));
            if(p == null) {
                throw new I18NException(new I18NBoundMessage1P(
                        CLI_ERR_OPTION_MISSING, optName));
            }
            password = new String(p);
            Arrays.fill(p,'0');
        }
        return password;
    }

    /**
     * Changes the user password.
     *
     * @param userName the user name of the user running the command
     * @param opUser the name of the user who's password needs to be reset.
     * Can be null. If null, the password of the user running the command
     * is changed
     * @param password the password supplied by the user running the command
     * @param opPass the new password value.
     * 
     * @throws PersistenceException if there were errors reseting the password.
     */
    private void changePassword(String userName,
                                String opUser,
                                String password,
                                String opPass) throws PersistenceException {
        SimpleUser u = null;
        if(opUser != null) {
            u = new SingleSimpleUserQuery(opUser).fetch();
            //go through set name to reset the password as we do not have
            //the original password
            String name = u.getName();
            u.setName(null);
            u.setName(name);
            u.setPassword(opPass.toCharArray());
        } else {
            u = new SingleSimpleUserQuery(userName).fetch();
            u.changePassword(password.toCharArray(), opPass.toCharArray());
        }
        u.save();
        out.println(CLI_OUT_USER_CHG_PASS.getText(u.getName()));
    }

    /**
     * Lists the users in the database.
     *
     * @param nameFilter a filter to filter the list of users. Can be null, in
     * which case all the users are listed.
     *
     * @throws PersistenceException if there was an error fetching the users.
     */
    private void listUsers(String nameFilter) throws PersistenceException {
        MultiSimpleUserQuery q = MultiSimpleUserQuery.all();
        q.setEntityOrder(q.BY_NAME);
        if(nameFilter != null) {
            q.setNameFilter(new StringFilter(nameFilter));
        }
        List<SimpleUser> l = q.fetch();
        for(SimpleUser u:l) {
            out.println(u.getName());
        }
    }

    /**
     * Deletes the user from the database.
     *
     * @param opUser the name of the user to be deleted.
     *
     * @throws I18NException if there were errors deleting the user, or
     * an attempt was made to delete the admin user.
     */
    private void deleteUser(String opUser) throws I18NException {
        if(ADMIN_USER_NAME.equals(opUser)) {
            throw new I18NException(new I18NBoundMessage1P(
                    CLI_ERR_UNAUTH_DELETE,opUser));
        }
        SimpleUser u = new SingleSimpleUserQuery(opUser).fetch();
        u.delete();
        out.println(CLI_OUT_USER_DELETED.getText(u.getName()));
    }

    /**
     * Adds the supplied user to the database
     *
     * @param opUser the name of the new user
     * @param opPass the password for the new user
     *
     * @throws PersistenceException if there was an error adding
     * the new user
     */
    private void addUser(String opUser, String opPass)
            throws PersistenceException {
        SimpleUser u = new SimpleUser();
        u.setName(opUser);
        u.setPassword(opPass.toCharArray());
        u.save();
        out.println(CLI_OUT_USER_CREATED.getText(u.getName()));
    }

    /**
     * Enum to aid authorization of running various user commands
     */
    private enum Authorization {
        ADD_USER,
        DELETE_USER,
        CHANGE_PASSWORD,
        LIST_USERS {
            @Override
            public void authorize(String userName, String password)
                    throws I18NException {
                validateUser(userName, password);
            }}
        ;

        public void authorize(String userName, String password) throws I18NException {
            validateUser(userName,password);
            if(!userName.toLowerCase().equals(ADMIN_USER_NAME)) {
                throw new I18NException(CLI_UNAUTHORIZED_ACTION);
            }
        }
        private static void validateUser(String userName, String password)
                throws I18NException {
            try {
                new SingleSimpleUserQuery(userName).fetch().validatePassword(
                        password.toCharArray());
            } catch (PersistenceException e) {
                throw new I18NException(CLI_ERR_INVALID_LOGIN);
            }
        }
    }

    /**
     * Returns the options accepted by the CLI.
     *
     * @return the options accepted by the CLI
     */
    private static Options options() {
        Options opts = new Options();
        opts.addOption(OptionBuilder.hasArg().
                withArgName(CLI_ARG_LOGIN_VALUE.getText()).
                withDescription(CLI_PARM_USER.getText()).
                isRequired(true).create(OPT_CURRENT_USER));
        opts.addOption(OptionBuilder.hasArg().
                withArgName(CLI_ARG_LOGIN_PASSWORD_VALUE.getText()).
                withDescription(CLI_PARM_PASSWORD.getText()).
                isRequired(false).create(OPT_CURRENT_PASSWORD));

        OptionGroup commands = new OptionGroup();
        commands.setRequired(true);
        commands.addOption(OptionBuilder.withLongOpt(CMD_LIST_USERS).
                withDescription(CLI_CMD_LIST_USERS.getText()).
                isRequired().create());
        commands.addOption(OptionBuilder.withLongOpt(CMD_ADD_USER).
                withDescription(CLI_CMD_ADD_USER.getText()).
                isRequired().create());
        commands.addOption(OptionBuilder.withLongOpt(CMD_DELETE_USER).
                withDescription(CLI_CMD_DELETE_USER.getText()).
                isRequired().create());
        commands.addOption(OptionBuilder.withLongOpt(CMD_CHANGE_PASS).
                withDescription(CLI_CMD_CHANGE_PASSWORD.getText()).
                isRequired().create());
        opts.addOptionGroup(commands);
        //Add optional arguments
        opts.addOption(OptionBuilder.withLongOpt("username"). //$NON-NLS-1$
                hasArg(true).withArgName(CLI_ARG_USER_NAME_VALUE.getText()).
                withDescription(CLI_PARM_OP_USER.getText()).
                isRequired(false).create(OPT_OPERATED_USER));
        opts.addOption(OptionBuilder.withLongOpt("password"). //$NON-NLS-1$
                hasArg(true).withArgName(CLI_ARG_USER_PASSWORD_VALUE.getText()).
                withDescription(CLI_PARM_OP_PASSWORD.getText()).
                isRequired(false).create(OPT_OPERATED_PASSWORD));
        return opts;
    }

    /**
     * Prints the message onto the error output stream
     * @param msg the error message
     */
    private void printError(String msg) {
        err.println(msg);
    }

    /**
     * Prints the usage onto the error output stream
     */
    private void printUsage() {
        HelpFormatter h = new HelpFormatter();
        final int width = 100;
        final int leftPad = 4;
        final int descPad = 4;
        final String LS = SystemProperties.LINE_SEPARATOR;
        final String l = "-u <" + CLI_ARG_LOGIN_VALUE.getText() + "> "; //$NON-NLS-1$ //$NON-NLS-2$
        final String p = "-p <" + CLI_ARG_LOGIN_PASSWORD_VALUE.getText() + "> "; //$NON-NLS-1$ //$NON-NLS-2$
        final String u = "-n <" + CLI_ARG_USER_NAME_VALUE.getText() + "> "; //$NON-NLS-1$ //$NON-NLS-2$
        final String up = "-w <" + CLI_ARG_USER_PASSWORD_VALUE.getText() + "> "; //$NON-NLS-1$ //$NON-NLS-2$
        final String prefix = CMD_NAME + " " + l + p ; //$NON-NLS-1$
        final String s = prefix + "--" + CMD_LIST_USERS + //$NON-NLS-1$
                LS + prefix + "--" + CMD_ADD_USER + " " + u + " " + up + //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                LS + prefix + "--" + CMD_DELETE_USER + " " + u + //$NON-NLS-1$ //$NON-NLS-2$
                LS + prefix + "--" + CMD_CHANGE_PASS + " " + up + " [" + u + "]" + LS; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        final PrintWriter writer = new PrintWriter(err);
        h.printHelp(writer, width, s, CLI_DESC_OPTIONS_HEADER.getText(),
                options(), leftPad, descPad, "", false); //$NON-NLS-1$
        writer.flush();
    }
    private PrintStream out;
    private PrintStream err;
    private static final String CMD_LIST_USERS = "listUsers"; //$NON-NLS-1$
    private static final String CMD_ADD_USER = "addUser"; //$NON-NLS-1$
    private static final String CMD_DELETE_USER = "deleteUser"; //$NON-NLS-1$
    private static final String CMD_CHANGE_PASS = "changePassword"; //$NON-NLS-1$
    private static final String OPT_CURRENT_USER = "u"; //$NON-NLS-1$
    private static final String OPT_CURRENT_PASSWORD = "p"; //$NON-NLS-1$
    private static final String OPT_OPERATED_USER = "n"; //$NON-NLS-1$
    private static final String OPT_OPERATED_PASSWORD = "w"; //$NON-NLS-1$
    private static final String ADMIN_USER_NAME = "admin"; //$NON-NLS-1$
    static final String CMD_NAME = "orsadmin"; //$NON-NLS-1$
}
