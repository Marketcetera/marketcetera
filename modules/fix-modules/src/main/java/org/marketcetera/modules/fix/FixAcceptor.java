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
public class FixAcceptor
        extends AbstractFixModule
{
    /**
     * Create a new FixAcceptor instance.
     *
     * @param inURN a <code>ModuleURN</code> value
     * @param inSettingsProvider 
     */
    protected FixAcceptor(ModuleURN inURN,
                          SessionSettingsProvider inSettingsProvider)
    {
        super(inURN,
              inSettingsProvider);
    }
}
