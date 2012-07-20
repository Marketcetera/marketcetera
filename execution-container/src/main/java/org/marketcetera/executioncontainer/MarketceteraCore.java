package org.marketcetera.executioncontainer;

import org.marketcetera.module.ModuleManager;
import org.marketcetera.util.misc.ClassVersion;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

/* $License$ */

/**
 * Initializes Marketcetera core components.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: MarketceteraCore.java 82384 2012-07-20 19:09:59Z colin $
 * @since $Release$
 */
@ClassVersion("$Id: MarketceteraCore.java 82384 2012-07-20 19:09:59Z colin $")
public class MarketceteraCore
        implements InitializingBean
{
    /* (non-Javadoc)
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    @Override
    public void afterPropertiesSet()
            throws Exception
    {
        moduleManager.init();
    }
    /**
     * system-wide module manager instance
     */
    @Autowired
    private volatile ModuleManager moduleManager;
}
