package org.marketcetera.strategyagent;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.module.ConfigurationProviderTestFactory;
import org.marketcetera.module.ModuleURN;

/* $License$ */
/**
 * MyTestFactory
 *
 * @author anshul@marketcetera.com
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public class MyTestFactory extends ConfigurationProviderTestFactory {
    public MyTestFactory() {
        super(PROVIDER_URN);
    }
    static final ModuleURN PROVIDER_URN = new ModuleURN("metc:test:fender");
}
