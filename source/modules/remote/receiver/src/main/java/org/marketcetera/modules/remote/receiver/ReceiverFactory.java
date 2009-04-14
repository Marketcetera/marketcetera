package org.marketcetera.modules.remote.receiver;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.module.ModuleFactory;
import org.marketcetera.module.Module;
import org.marketcetera.module.ModuleCreationException;
import org.marketcetera.module.ModuleURN;

/* $License$ */
/**
 * Factory for the {@link ReceiverModule}.
 * <p>
 * The factory has the following characteristics.
 * <table>
 * <tr><th>Provider URN:</th><td><code>metc:remote:receiver</code></td></tr>
 * <tr><th>Cardinality:</th><td>Singleton</td></tr>
 * <tr><th>Instance URN:</th><td><code>metc:remote:receiver:single</code></td></tr>
 * <tr><th>Auto-Instantiated:</th><td>No</td></tr>
 * <tr><th>Auto-Started:</th><td>Yes</td></tr>
 * </table>
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 1.5.0
 */
@ClassVersion("$Id$")
public class ReceiverFactory extends ModuleFactory {
    /**
     * Creates an instance.
     */
    public ReceiverFactory() {
        super(PROVIDER_URN, Messages.PROVIDER_DESCRIPTION, false, false);
    }

    @Override
    public Module create(Object... inParameters)
            throws ModuleCreationException {
        return new ReceiverModule(INSTANCE_URN);
    }

    /**
     * The module provider URN.
     */
    public static final ModuleURN PROVIDER_URN = new ModuleURN("metc:remote:receiver");  //$NON-NLS-1$
    /**
     * The single instance URN.
     */
    public static final ModuleURN INSTANCE_URN = new ModuleURN(PROVIDER_URN, "single");  //$NON-NLS-1$
}