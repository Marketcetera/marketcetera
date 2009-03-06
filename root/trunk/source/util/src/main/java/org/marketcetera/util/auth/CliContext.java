package org.marketcetera.util.auth;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.marketcetera.util.except.ExceptUtils;
import org.marketcetera.util.except.I18NException;
import org.marketcetera.util.log.I18NBoundMessage;
import org.marketcetera.util.misc.ClassVersion;

/**
 * A context for command-line setters ({@link CliSetter}). This
 * context provides command-line parsing, so that individual setters
 * may use command-line options to set their holder data.
 *
 * @author tlerios@marketcetera.com
 * @since 0.5.0
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$")
public class CliContext
    extends Context<CliSetter<?>>
{

    // INSTANCE DATA.

    private String[] mArgs;
    private CommandLine mCommandLine;
    private OptionsProvider mOptionsProvider;
    private ParseException mParseException;


    // CONSTRUCTORS.

    /**
     * Constructor mirroring superclass constructor. The new context
     * will use the given command-line arguments.
     *
     * @param args The command-line arguments.
     *
     * @see Context#Context(I18NBoundMessage,boolean)
     */

    public CliContext
        (I18NBoundMessage name,
         boolean override,
         String[] args)
    {
        super(name,override);
        mArgs=args;
    }

    /**
     * Constructor mirroring superclass constructor. The context name
     * is set automatically to a default value. The new context will
     * use the given command-line arguments.
     *
     * @param args The command-line arguments.
     *
     * @see Context#Context(I18NBoundMessage,boolean)
     */

    public CliContext
        (boolean override,
         String[] args)
    {
        this(Messages.CLI_NAME,override,args);
    }


    // INSTANCE METHODS.

    /**
     * Returns the receiver's command-line arguments.
     *
     * @return The arguments.
     */

    public String[] getArgs()
    {
        return mArgs;
    }

    /**
     * Returns the receiver's parsed command-line. It may be null if
     * the receiver never had a reason to parse the command-line (such
     * as a non-override context which is asked to set values after
     * all values have already been set).
     *
     * @return The parsed command-line.
     */

    public CommandLine getCommandLine()
    {
        return mCommandLine;
    }


    // Context.

    @Override
    public void setValues()
        throws I18NException
    {
        Options options=new Options();
        for (CliSetter<?> setter:getSetters()) {
            setter.addOption(options);
        }
        if (getOptionsProvider()!=null) {
            getOptionsProvider().addOptions(options);
        }
        try {
            mCommandLine=(new GnuParser()).parse(options,getArgs());
        } catch (ParseException ex) {
            setParseException(ex);
            throw ExceptUtils.wrap(ex,Messages.PARSING_FAILED);
        }
        for (CliSetter<?> setter:getSetters()) {
            if (shouldProcess(setter)) {
                setter.setValue(getCommandLine());
            }
        }
    }

    /**
     * Sets the receiver's options provider, via which additional
     * options may be added to the command-line.
     *
     * @param optionsProvider The provider.
     */

    public void setOptionsProvider
        (OptionsProvider optionsProvider)
    {
        mOptionsProvider=optionsProvider;
    }

    /**
     * Returns the receiver's options provider.
     *
     * @return The provider.
     */

    public OptionsProvider getOptionsProvider()
    {
        return mOptionsProvider;
    }

    /**
     * Sets the failure that the receiver encountered when parsing the
     * command line.
     *
     * @return The failure. It may be null.
     */

    private void setParseException
        (ParseException parseException)
    {
        mParseException=parseException;
    }

    /**
     * Returns any failure that the receiver encountered when parsing
     * the command line.
     *
     * @return The failure. It may be null if no failure took place.
     */

    public ParseException getParseException()
    {
        return mParseException;
    }
}
