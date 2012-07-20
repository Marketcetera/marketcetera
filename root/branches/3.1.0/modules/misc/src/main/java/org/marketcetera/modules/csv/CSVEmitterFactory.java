package org.marketcetera.modules.csv;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.module.ModuleFactory;
import org.marketcetera.module.ModuleCreationException;
import org.marketcetera.module.ModuleURN;

/* $License$ */
/**
 * Provider that will emit data contained in CSV as a series of maps.
 * <p>
 * The factory has the following characteristics.
 * <table>
 * <tr><th>Provider URN:</th><td><code>metc:csv:system</code></td></tr>
 * <tr><th>Cardinality:</th><td>Singleton</td></tr>
 * <tr><th>InstanceURN:</th><td><code>metc:csv:system:single</code></td></tr>
 * <tr><th>Auto-Instantiated:</th><td>No</td></tr>
 * <tr><th>Auto-Started:</th><td>Yes</td></tr>
 * <tr><th>Instantiation Arguments:</th><td>None</td></tr>
 * <tr><th>Module Type</th><td>{@link CSVEmitter}</td></tr>
 * </table>
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
