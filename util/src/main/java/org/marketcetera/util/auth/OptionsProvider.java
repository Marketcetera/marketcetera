package org.marketcetera.util.auth;

import org.apache.commons.cli.Options;
import org.marketcetera.util.misc.ClassVersion;

/**
 * A provider of extra command line options.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 1.0.0
 */

/* $License$ */

@ClassVersion("$Id$")
public interface OptionsProvider
{

    /**
     * Adds the given options to the receiver's set of options.
     *
     * @param options The options.
     */

    void addOptions
        (Options options);
}
