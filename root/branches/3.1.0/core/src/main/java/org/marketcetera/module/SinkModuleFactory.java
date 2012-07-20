package org.marketcetera.module;

import org.marketcetera.util.misc.ClassVersion;

/* $License$ */
/**
 * The factory for Sink Module.
 * <p>
 * The factory has the following characteristics.
 * <table>
 * <tr><th>Provider URN:</th><td><code>metc:sink:system</code></td></tr>
 * <tr><th>Cardinality:</th><td>Singleton</td></tr>
 * <tr><th>InstanceURN:</th><td><code>metc:sink:system:single</code></td></tr>
 * <tr><th>Auto-Instantiated:</th><td>No</td></tr>
 * <tr><th>Auto-Started:</th><td>Yes</td></tr>
 * <tr><th>Instantiation Arguments:</th><td>None</td></tr>
 * <tr><th>Module Type:</th><td>{@link SinkModule}</td></tr>
 * </table>
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$")  //$NON-NLS-1$
public final class SinkModuleFactory extends ModuleFactory {
    /**
     * Creates a new sink module instance.
     *
     * @param parameters Doesn't need any parameters.
     *
     * @return the SinkModule instance.
     *
     */
    public SinkModule create(Object... parameters) {
        return new SinkModule();
    }

    /**
     * Creates a new instance of the factory.
     */
    public SinkModuleFactory() {
        super(PROVIDER_URN, Messages.SINK_MODULE_FACTORY_DESC, false, false);
    }

    /**
     * The Sink module provider URN.
     */
    public static final ModuleURN PROVIDER_URN = new ModuleURN(
            ModuleURN.SCHEME + ModuleURN.URN_SEPARATOR_CHAR + "sink" +  //$NON-NLS-1$
                    ModuleURN.URN_SEPARATOR_CHAR + "system");  //$NON-NLS-1$
    /**
     * The sink module instance URN.
     */
    public static final ModuleURN INSTANCE_URN =
            new ModuleURN(PROVIDER_URN,  "single");  //$NON-NLS-1$
}
