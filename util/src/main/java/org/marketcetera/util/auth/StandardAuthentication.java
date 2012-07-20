package org.marketcetera.util.auth;

import java.io.PrintStream;
import org.marketcetera.util.log.I18NBoundMessage1P;
import org.marketcetera.util.log.I18NBoundMessage2P;
import org.marketcetera.util.misc.ClassVersion;

/**
 * A standard authentication system. A standard system comprises a
 * string holder for a username, and a character array holder for a
 * password; both are required. The contexts employed to retrieve the
 * username and password are, in order, a spring context, a
 * command-line context (which is an override context), and lastly a
 * console context (which is non-override). This class sets up all
 * holders and contexts with default prompts, usage instructions,
 * etc., and provides shorthands to perform operations on the
 * underlying holders and contexts.
 *
 * @author tlerios@marketcetera.com
 * @since 0.5.0
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$")
public class StandardAuthentication
{

    // CLASS DATA.

    /**
     * The default name of the properties file bean (Spring context).
     */

    public static final String PROPERTIES_FILE_BEAN=
        "propertiesFiles"; //$NON-NLS-1$

    /**
     * The default property name for the username (Spring context).
     */

    public static final String USER_PROP=
        "metc.amq.user"; //$NON-NLS-1$

    /**
     * The default property name for the password (Spring context).
     */

    public static final String PASSWORD_PROP=
        "metc.amq.password"; //$NON-NLS-1$

    /**
     * The default short form of the command-line option for the
     * username (command-line context).
     */

    public static final String USER_SHORT=
        "u"; //$NON-NLS-1$

    /**
     * The default long form of the command-line option for the
     * username (command-line context).
     */

    public static final String USER_LONG=
        "user"; //$NON-NLS-1$

    /**
     * The default short form of the command-line option for the
     * password (command-line context).
     */

    public static final String PASSWORD_SHORT=
        "p"; //$NON-NLS-1$

    /**
     * The default long form of the command-line option for the
     * password (command-line context).
     */

    public static final String PASSWORD_LONG=
        "password"; //$NON-NLS-1$


    // INSTANCE DATA.

    private Holder<String> mUserHolder;
    private HolderCharArray mPasswordHolder;
    private AuthenticationSystem mSystem;
    private CliContext mCliContext;


    // CONSTRUCTORS.

    /**
     * Creates a new standard authentication system with the given
     * options.
     *
     * @param configLocation The location of the Spring configuration
     * file (Spring context).
     * @param propertiesFilesBean The name of the properties file bean
     * (Spring context).
     * @param userPropertyName The property name for the username
     * (Spring context).
     * @param passwordPropertyName The property name for the password
     * (Spring context).
     * @param userShort The short form of the command-line option for
     * the username (command-line context).
     * @param userLong The long form of the command-line option for
     * the username (command-line context).
     * @param passwordShort The short form of the command-line option
     * for the password (command-line context).
     * @param passwordLong The long form of the command-line option
     * for the password (command-line context).
     * @param cliArgs The command-line arguments (command-line
     * context).
     */

    public StandardAuthentication
        (String configLocation,
         String propertiesFilesBean,
         String userPropertyName,
         String passwordPropertyName,
         String userShort,
         String userLong,
         String passwordShort,
         String passwordLong,
         String[] cliArgs)
    {
        mSystem=new AuthenticationSystem();

        mUserHolder=new Holder<String>(Messages.NO_USER);
        mPasswordHolder=new HolderCharArray(Messages.NO_PASSWORD);

        SpringContext springContext=new SpringContext
            (true,configLocation,propertiesFilesBean);
        springContext.add
            (new SpringSetterString
             (mUserHolder,new I18NBoundMessage1P
              (Messages.USER_SPRING_USAGE,userPropertyName),
              userPropertyName));
        springContext.add
            (new SpringSetterCharArray
             (mPasswordHolder,new I18NBoundMessage1P
              (Messages.PASSWORD_SPRING_USAGE,passwordPropertyName),
              passwordPropertyName));
        mSystem.add(springContext);

        mCliContext=new CliContext(true,cliArgs);
        mCliContext.add
            (new CliSetterString
             (mUserHolder,new I18NBoundMessage2P
              (Messages.USER_CLI_USAGE,userShort,userLong),
             userShort,userLong,Messages.USER_DESCRIPTION));
        mCliContext.add
            (new CliSetterCharArray
             (mPasswordHolder,new I18NBoundMessage2P
              (Messages.PASSWORD_CLI_USAGE,passwordShort,passwordLong),
              passwordShort,passwordLong,Messages.PASSWORD_DESCRIPTION));
        mSystem.add(mCliContext);

        ConsoleContext consoleContext=new ConsoleContext(false);
        consoleContext.add
            (new ConsoleSetterString
             (mUserHolder,Messages.USER_CONSOLE_USAGE,
              Messages.USER_PROMPT));
        consoleContext.add
            (new ConsoleSetterCharArray
             (mPasswordHolder,Messages.PASSWORD_CONSOLE_USAGE,
              Messages.PASSWORD_PROMPT));
        mSystem.add(consoleContext);
    }

    /**
     * Creates a new standard authentication system with the given
     * options, and using default values for all options not specified
     * in this constructor.
     *
     * @param configLocation The location of the Spring configuration
     * file (Spring context).
     * @param cliArgs The command-line arguments (command-line
     * context).
     */

    public StandardAuthentication
        (String configLocation,
         String[] cliArgs)
    {
        this(configLocation,PROPERTIES_FILE_BEAN,
             USER_PROP,
             PASSWORD_PROP,
             USER_SHORT,USER_LONG,
             PASSWORD_SHORT,PASSWORD_LONG,
             cliArgs);
    }


    // INSTANCE METHODS.

    /**
     * Sets the data of the receiver's username and password
     * holders. Once done, it checks whether both holders have had
     * their data set.
     *
     * @return True if so.
     */

    public boolean setValues()
    {
        return mSystem.setValues();
    }

    /**
     * Returns the receiver's command-line context.
     *
     * @return The context.
     */
    
    public CliContext getCliContext()
    {
        return mCliContext;
    }

    /**
     * Prints the receiver's usage instructions onto the given
     * stream.
     *
     * @param stream The stream.
     */

    public void printUsage
        (PrintStream stream)
    {
        mSystem.printUsage(stream);
    }

    /**
     * Returns the command-line arguments which the receiver was
     * unable to parse.
     *
     * @return The arguments.
     */

    public String[] getOtherArgs()
    {
        return mCliContext.getCommandLine().getArgs();
    }

    /**
     * Returns the receiver's username.
     *
     * @return The username, which is null if the username is not set.
     */

    public String getUser()
    {
        return mUserHolder.getValue();
    }

    /**
     * Returns the receiver's password.
     *
     * @return The password, which is null if the password is not set.
     */

    public char[] getPassword()
    {
        return mPasswordHolder.getValue();
    }

    /**
     * Returns the receiver's password as a string. This method should
     * not be called if it is important to ensure that the password
     * can be completely removed from memory using {@link
     * #clearPassword()}; this is because the string created and
     * returned by this method cannot be zeroed out..
     *
     * @return The password, which is null if the password is not set.
     */

    public String getPasswordAsString()
    {
        return mPasswordHolder.getValueAsString();
    }

    /**
     * Clears the receiver's password by first overwriting all prior
     * characters with the nul ('\0') character (if the receiver had a
     * non-null password), and then setting the password to null.
     */

    public void clearPassword()
    {
        mPasswordHolder.clear();
    }
}
