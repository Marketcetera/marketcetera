package org.marketcetera.util.auth;

import org.marketcetera.util.misc.ClassVersion;
import org.apache.commons.cli.Options;

/* $License$ */
/**
 * Provides extra command line options to {@link CliContext}.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$")
public interface OptionsProvider
{
    /**
     * Add options to the set of options used by {@link CliContext}.
     *
     * @param inOptions the options that will be used by {@link CliContext}
     * to parse the command line.
     */
    public void addOptions(Options inOptions);
}
