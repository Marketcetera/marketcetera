package org.marketcetera.security;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.lang.StringUtils;
import org.marketcetera.core.util.log.I18NBoundMessage;
import org.marketcetera.api.attributes.ClassVersion;

/**
 * A setter for a string holder that obtains the data via a
 * command-line.
 *
 * @author tlerios@marketcetera.com
 * @since 0.5.0
 * @version $Id: CliSetterString.java 82324 2012-04-09 20:56:08Z colin $
 */

/* $License$ */

@ClassVersion("$Id: CliSetterString.java 82324 2012-04-09 20:56:08Z colin $")
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
