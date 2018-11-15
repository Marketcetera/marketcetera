package org.marketcetera.core;

import java.io.File;

import org.marketcetera.util.misc.ClassVersion;
import org.springframework.context.ConfigurableApplicationContext;

/* $License$ */

/**
 * Provides application information.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 2.4.0
 */
@ClassVersion("$Id$")
public interface ApplicationInfoProvider
{
    /**
     * Gets the application directory value.
     *
     * @return a <code>File</code> value
     */
    public File getAppDir();
    /**
     * Gets the configuration directory value.
     *
     * @return a <code>File</code> value
     */
    public File getConfDir();
    /**
     * Gets the application arguments.
     *
     * @return a <code>String[]</code> value
     */
    public String[] getArguments();
    /**
     * Gets the application context.
     *
     * @return a <code>ConfigurableApplicationContext</code> value
     */
    public ConfigurableApplicationContext getContext();
}
