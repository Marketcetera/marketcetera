package org.marketcetera.util.auth;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.lang.StringUtils;
import org.marketcetera.util.log.I18NBoundMessage;
import org.marketcetera.util.misc.ClassVersion;

/**
 * A setter for a string holder that obtains the data via a
 * command-line.
 *
 * @author tlerios@marketcetera.com
 * @since 0.5.0
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$")
public class CliSetterString
    extends CliSetter<Holder<String>>
{

    // CONSTRUCTORS.

    /**
     * Constructor mirroring superclass constructor.
     *
     * @see CliSetter#CliSetter(Holder,I18NBoundMessage,String,String,I18NBoundMessage)
     */

    public CliSetterString
        (Holder<String> holder,
         I18NBoundMessage usage,
         String shortForm,
         String longForm,
         I18NBoundMessage description)
    {
        super(holder,usage,shortForm,longForm,description);
    }


    // CliSetter.

    @Override
    public void setValue
        (CommandLine commandLine)
    {
        String value=commandLine.getOptionValue(getShortForm());
        if ((value!=null) && !StringUtils.EMPTY.equals(value)) {
            getHolder().setValue(value);
        }
    }
}
