package org.marketcetera.modules.object;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.module.ModuleFactory;
import org.marketcetera.module.ModuleCreationException;
import org.marketcetera.module.ModuleURN;
import org.marketcetera.module.Module;

/* $License$ */
/**
 * Provider that will emit serialized object data contained in file
 * as a series of object.
 * This provider creates a singleton module instance.
 * The singleton instance is auto-instantiated and auto-started.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public final class ObjectEmitterFactory extends ModuleFactory {
    /**
     * Creates an instance.
     *
     */
    public ObjectEmitterFactory() {
        super(PROVIDER_URN, Messages.EMITTER_PROVIDER_DESCRIPTION, false, false);
    }

    @Override
    public Module create(Object... inParameters)
            throws ModuleCreationException {
        return new ObjectEmitter(INSTANCE_URN, true);
    }

    /**
     * The Provider URN.
     */
    public static final ModuleURN PROVIDER_URN =
            new ModuleURN("metc:object:emit");  //$NON-NLS-1$

    /**
     * The Instance URN for the singleton instance.
     */
    public static final ModuleURN INSTANCE_URN =
            new ModuleURN(PROVIDER_URN,"single");  //$NON-NLS-1$

}