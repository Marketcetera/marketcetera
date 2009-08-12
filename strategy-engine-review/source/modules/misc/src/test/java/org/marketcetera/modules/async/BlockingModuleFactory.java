package org.marketcetera.modules.async;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.log.I18NMessage0P;
import org.marketcetera.module.ModuleFactory;
import org.marketcetera.module.Module;
import org.marketcetera.module.ModuleCreationException;
import org.marketcetera.module.ModuleURN;

/* $License$ */
/**
 * Factory for a module that blocks when receiving data.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public class BlockingModuleFactory extends ModuleFactory {
    /**
     * Creates an instance.
     *
     */
    public BlockingModuleFactory() {
        super(PROVIDER_URN, new I18NMessage0P(Messages.LOGGER,
                "blockingFactory"), false, false);
    }

    @Override
    public Module create(Object... inParameters) throws ModuleCreationException {
        return sLastInstance = new BlockingReceiverModule();
    }

    /**
     * The last module instance created by this factory.
     * @return the last module instance created by this factory.
     */
    public static BlockingReceiverModule getLastInstance() {
        return sLastInstance;
    }

    private volatile static BlockingReceiverModule sLastInstance;
    /**
     * The Provider URN.
     */
    public static final ModuleURN PROVIDER_URN =
            new ModuleURN("metc:test:blocking");  //$NON-NLS-1$
    public static final ModuleURN INSTANCE_URN =
            new ModuleURN(PROVIDER_URN, "single");  //$NON-NLS-1$

}
