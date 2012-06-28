package org.marketcetera.util.auth;

import org.apache.commons.cli.Options;
import org.marketcetera.core.attributes.ClassVersion;

/**
 * A provider of extra command line options.
 *
 * @author anshul@marketcetera.com
 * @version $Id: OptionsProvider.java 82324 2012-04-09 20:56:08Z colin $
 * @since 1.0.0
 */

/* $License$ */

@ClassVersion("$Id: OptionsProvider.java 82324 2012-04-09 20:56:08Z colin $")
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
