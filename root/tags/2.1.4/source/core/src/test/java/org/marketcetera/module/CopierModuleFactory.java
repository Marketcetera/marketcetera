package org.marketcetera.module;

import org.marketcetera.util.log.I18NMessage0P;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */
/**
 * CopierModuleFactory
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public class CopierModuleFactory extends ModuleFactory {
    public CopierModuleFactory() {
        super(PROVIDER_URN, new I18NMessage0P(Messages.LOGGER, "provider"), false, false);
    }

    public Module create(Object... inParameters) throws ModuleCreationException {
        return new CopierModule();
    }
    static final ModuleURN PROVIDER_URN = new ModuleURN("metc:test:copier");
    public static final ModuleURN INSTANCE_URN = new ModuleURN(PROVIDER_URN, "default");
}
