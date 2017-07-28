package org.marketcetera.modules.fix;

import org.marketcetera.fix.SessionSettingsProvider;
import org.marketcetera.module.ModuleURN;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class FixInitiator
        extends AbstractFixModule
{
    /**
     * Create a new FixInitiator instance.
     *
     * @param inURN a <code>ModuleURN</code> value
     * @param inSettingsProvider 
     */
    protected FixInitiator(ModuleURN inURN,
                           SessionSettingsProvider inSettingsProvider)
    {
        super(inURN,
              inSettingsProvider);
    }
}
