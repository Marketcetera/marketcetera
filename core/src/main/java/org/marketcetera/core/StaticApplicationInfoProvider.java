package org.marketcetera.core;

import java.io.File;

import org.marketcetera.util.misc.ClassVersion;
import org.springframework.context.ConfigurableApplicationContext;

/* $License$ */

/**
 * Provides application info from well-known static locations.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 2.4.0
 */
@ClassVersion("$Id$")
public class StaticApplicationInfoProvider
        implements ApplicationInfoProvider
{
    /* (non-Javadoc)
     * @see org.marketcetera.core.ApplicationInfoProvider#getAppDir()
     */
    @Override
    public File getAppDir()
    {
        return ApplicationContainer.getInstance().getAppDir();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.ApplicationInfoProvider#getConfDir()
     */
    @Override
    public File getConfDir()
    {
        return ApplicationContainer.getInstance().getConfDir();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.ApplicationInfoProvider#getArguments()
     */
    @Override
    public String[] getArguments()
    {
        return ApplicationContainer.getInstance().getArguments();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.ApplicationInfoProvider#getContext()
     */
    @Override
    public ConfigurableApplicationContext getContext()
    {
        return ApplicationContainer.getInstance().getContext();
    }
}
