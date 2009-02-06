package org.marketcetera.modules.object;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.module.ModuleFactory;
import org.marketcetera.module.Module;
import org.marketcetera.module.ModuleCreationException;
import org.marketcetera.module.ModuleURN;

/* $License$ */
/**
 * Module factory that records data received in a flow as serialized objects
 * in a unique file per data flow.
 * This provider creates a singleton module instance.
 * The singleton instance is auto-instantiated and auto-started.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since $Release$
 * @see ObjectRecorder
 */
@ClassVersion("$Id$")
public class ObjectRecorderFactory extends ModuleFactory {
    public ObjectRecorderFactory() {
        super(PROVIDER_URN, Messages.RECORDER_PROVIDER_DESCRIPTION, false, false);
    }

    @Override
    public Module create(Object... inParameters) throws ModuleCreationException {
        return new ObjectRecorder(INSTANCE_URN, true);
    }
    /**
     * The Provider URN.
     */
    public static final ModuleURN PROVIDER_URN =
            new ModuleURN("metc:object:recorder");  //$NON-NLS-1$

    /**
     * The Instance URN for the singleton instance.
     */
    public static final ModuleURN INSTANCE_URN =
            new ModuleURN(PROVIDER_URN,"single");  //$NON-NLS-1$
}
