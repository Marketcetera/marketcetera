package org.marketcetera.modules.cep.system;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.module.ModuleFactory;
import org.marketcetera.module.ModuleCreationException;
import org.marketcetera.module.ModuleURN;

/* $License$ */
/**
 * Provides basic "select * from xyz" functionality.
 * The provider will support multiple module instances.
 *
 * The instances are auto-created when they are referred to in a data flow
 * request and they are auto-started.
 *
 * @see CEPSystemProcessor
 * @author toli@marketcetera.com
 * @since $Release$
 * @version $Id$
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public final class CEPSystemFactory extends ModuleFactory<CEPSystemProcessor> {
    /**
     * Creates an instance.
     *
     */
    public CEPSystemFactory() {
        super(PROVIDER_URN, Messages.PROVIDER_DESCRIPTION, true, true,
                ModuleURN.class);
    }

    @Override
    public CEPSystemProcessor create(Object... inParameters)
            throws ModuleCreationException {
        return new CEPSystemProcessor((ModuleURN)inParameters[0], true);
    }

    /**
     * The Provider URN.
     */
    public static final ModuleURN PROVIDER_URN =
            new ModuleURN("metc:cep:system");  //$NON-NLS-1$

}