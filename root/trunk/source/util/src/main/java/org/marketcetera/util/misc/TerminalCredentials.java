package org.marketcetera.util.misc;

import java.io.Console;
import java.io.PrintStream;
import java.util.Arrays;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.marketcetera.core.ClassVersion;
import org.marketcetera.util.except.ExceptUtils;
import org.marketcetera.util.except.I18NException;

/**
 *
 * @author tlerios
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$")
public class TerminalCredentials
{

    // CLASS DATA.

    /**
     * The short form of the command-line switch specifying the user.
     */

    public static final String USER_SHORT="u";

    /**
     * The long form of the command-line switch specifying the user.
     */

    public static final String USER_LONG="user";

    /**
     * The short form of the command-line switch specifying the
     * password.
     */

    public static final String PASSWORD_SHORT="p";

    /**
     * The long form of the command-line switch specifying the
     * password.
     */

    public static final String PASSWORD_LONG="password";


    // INSTANCE DATA.

    private Options mOptions;
    private CommandLine mCommandLine;
    private String mUser;
    private char[] mPassword;


    // CONSTRUCTORS.

    public TerminalCredentials()
    {
        mOptions=new Options();
    }

    public TerminalCredentials
        (String user,
         char[] password)
    {
        mOptions=new Options();
        mUser=user;
        mPassword=password;
    }


    public static void showUsage(PrintStream stream)
    {
        stream.println
            ("-"+USER_SHORT+" (or -"+USER_LONG+"): "+
             Messages.USER_DESCRIPTION.getText());
        stream.println
            ("-"+PASSWORD_SHORT+" (or -"+PASSWORD_LONG+"): "+
             Messages.PASSWORD_DESCRIPTION.getText());
    }

    // INSTANCE METHODS.

    public Options getOptions()
    {
        return mOptions;
    }

    public CommandLine getCommandLine()
    {
        return mCommandLine;
    }

    public String getUser()
    {
        return mUser;
    }

    public char[] getPassword()
    {
        return mPassword;
    }

    public String[] getOtherArgs()
    {
        return getCommandLine().getArgs();
    }

    public void parse
        (String... args)
        throws I18NException
    {
        getOptions().addOption
            (USER_SHORT,USER_LONG,true,
             Messages.USER_DESCRIPTION.getText());
        getOptions().addOption
            (PASSWORD_SHORT,PASSWORD_LONG,true,
             Messages.PASSWORD_DESCRIPTION.getText());
        try {
            mCommandLine=(new GnuParser()).parse(getOptions(),args);
        } catch (ParseException ex) {
            throw ExceptUtils.wrap(ex,Messages.PARSING_FAILED);
        }
    }

    public String obtainUser()
        throws I18NException
    {
        if (getUser()!=null) {
            return getUser();
        }
        Console console=System.console();
        mUser=getCommandLine().getOptionValue(USER_SHORT);
        if ((console!=null) && (getUser()==null)) {
            mUser=console.readLine
                ("%s",Messages.USER_PROMPT.getText());
        }
        if (getUser()==null) {
            throw new I18NException(Messages.NO_USER);
        }
        return getUser();
    }

    public char[] obtainPassword()
        throws I18NException
    {
        if (getPassword()!=null) {
            return getPassword();
        }
        Console console=System.console();
        String passwordString=getCommandLine().getOptionValue(PASSWORD_SHORT);
        if (passwordString==null) {
            if (console!=null) {
                mPassword=console.readPassword
                    ("%s",Messages.PASSWORD_PROMPT.getText());
            }
        } else {
            mPassword=passwordString.toCharArray();
        }
        if (getPassword()==null) {
            throw new I18NException(Messages.NO_PASSWORD);
        }
        return getPassword();
    }

    public void obtainCredentials()
        throws I18NException
    {
        obtainUser();
        obtainPassword();
    }

    public void clearPassword()
    {
        Arrays.fill(mPassword,' ');
    }
}
