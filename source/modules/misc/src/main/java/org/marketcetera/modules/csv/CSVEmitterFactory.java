package org.marketcetera.modules.csv;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.module.ModuleFactory;
import org.marketcetera.module.ModuleCreationException;
import org.marketcetera.module.ModuleURN;

/* $License$ */
/**
 * Provider that will emit data contained in CSV as a series of maps.
 * This provider creates a singleton module instance.
 * The singleton instance is auto-instantiated and auto-started.
 *
 * @see CSVEmitter
 * @author anshul@marketcetera.com
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public final class CSVEmitterFactory extends ModuleFactory {
    /**
     * Creates an instance.
     *
     */
    public CSVEmitterFactory() {
        super(PROVIDER_URN, Messages.PROVIDER_DESCRIPTION, false, false);
    }

    @Override
    public CSVEmitter create(Object... inParameters)
            throws ModuleCreationException {
        return new CSVEmitter();
    }

    /**
     * The Provider URN.
     */
    public static final ModuleURN PROVIDER_URN =
            new ModuleURN("metc:csv:system");  //$NON-NLS-1$

    /**
     * The Instance URN for the singleton instance.
     */
    public static final ModuleURN INSTANCE_URN =
            new ModuleURN(PROVIDER_URN,"single");  //$NON-NLS-1$

}
