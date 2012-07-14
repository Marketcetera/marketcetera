package org.marketcetera.util.auth;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.marketcetera.util.log.I18NBoundMessage;
import org.marketcetera.util.misc.ClassVersion;

/**
 * A setter that obtains holder data via a command-line. It is
 * supported by a {@link CliContext}.
 *
 * @author tlerios@marketcetera.com
 * @since 0.5.0
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$")
public abstract class CliSetter<T extends Holder<?>>
    extends Setter<T>
{

    // INSTANCE DATA.

    private String mShortForm;
    private String mLongForm;
    private I18NBoundMessage mDescription;


    // CONSTRUCTORS.

    /**
     * Constructor mirroring superclass constructor. The command-line
     * option whose value is assigned to the holder data has the given
     * short and long forms and description.
     *
     * @param shortForm The short form.
     * @param longForm The long form.
     * @param description The description.
     *
     * @see Setter#Setter(Holder,I18NBoundMessage)
     */

    public CliSetter
        (T holder,
         I18NBoundMessage usage,
         String shortForm,
         String longForm,
         I18NBoundMessage description)
    {
        super(holder,usage);
        mShortForm=shortForm;
        mLongForm=longForm;
        mDescription=description;
    }


    // INSTANCE METHODS.

    /**
     * Returns the short form of the receiver's command-line option.
     *
     * @return The short form.
     */

    public String getShortForm()
    {
        return mShortForm;
    }

    /**
     * Returns the long form of the receiver's command-line option.
     *
     * @return The long form.
     */

    public String getLongForm()
    {
        return mLongForm;
    }

    /**
     * Returns the description of the receiver's command-line option.
     *
     * @return The description.
     */

    public I18NBoundMessage getDescription()
    {
        return mDescription;
    }

    /**
     * Adds the receiver command-line option to the given options.
     * This method is called by a {@link CliContext}.
     *
     * @param options The command-line options.
     */

    public void addOption
        (Options options)
    {
        options.addOption
            (getShortForm(),getLongForm(),true,getDescription().getText());
    }

    /**
     * Sets the holder's data by obtaining it from the given parsed
     * command-line. This method is called by a {@link CliContext}.
     *
     * @param commandLine The command-line.
     */

    public abstract void setValue
        (CommandLine commandLine);
}
