package org.marketcetera.core;

import java.io.File;

import org.marketcetera.util.misc.ClassVersion;
import org.springframework.context.ApplicationContext;

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
        extends ApplicationBase
        implements ApplicationInfoProvider
{
    /* (non-Javadoc)
     * @see org.marketcetera.core.ApplicationInfoProvider#getAppDir()
     */
    @Override
    public File getAppDir()
    {
        return new File(APP_DIR);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.ApplicationInfoProvider#getConfDir()
     */
    @Override
    public File getConfDir()
    {
        return new File(CONF_DIR);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.ApplicationInfoProvider#getArguments()
     */
    @Override
    public String[] getArguments()
    {
        if(ApplicationContainer.getInstance() == null) {
            return new String[0];
        }
        return ApplicationContainer.getInstance().getArguments();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.ApplicationInfoProvider#getContext()
     */
    @Override
    public ApplicationContext getContext()
    {
        return ApplicationContextProvider.getInstance().getApplicationContext();
    }
}
