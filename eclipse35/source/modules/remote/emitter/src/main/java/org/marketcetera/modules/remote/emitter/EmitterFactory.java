package org.marketcetera.modules.remote.emitter;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.module.ModuleFactory;
import org.marketcetera.module.Module;
import org.marketcetera.module.ModuleCreationException;
import org.marketcetera.module.ModuleURN;

/* $License$ */
/**
 * Factory for the remote {@link EmitterModule}. The remote emitter module
 * receives data from a remote
 * {@link org.marketcetera.modules.remote.receiver.ReceiverModule} and
 * emits it into local data flows.
 * <p>
 * The factory has the following characteristics.
 * <table>
 * <tr><th>Provider URN:</th><td><code>metc:remote:emitter</code></td></tr>
 * <tr><th>Cardinality:</th><td>Multi-Instance</td></tr>
 * <tr><th>Auto-Instantiated:</th><td>No</td></tr>
 * <tr><th>Auto-Started:</th><td>Yes</td></tr>
 * <tr><th>Instantiation Arguments:</th><td>module instance name</td></tr>
 * </table>
 * <p>
 * The URN of the created module instance has the provider URN as the prefix
 * and the supplied module instance name as the URN instance name.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 1.5.0
 */
@ClassVersion("$Id$")
public class EmitterFactory extends ModuleFactory {
    /**
     * Creates an instance.
     */
    public EmitterFactory() {
        super(PROVIDER_URN, Messages.PROVIDER_DESCRIPTION,
                true, false, String.class);
    }

    @Override
    public Module create(Object... inParameters)
            throws ModuleCreationException {
        return new EmitterModule(new ModuleURN(PROVIDER_URN,
                (String)inParameters[0]));
    }

    /**
     * The provider URN for this module provider.
     */
    public static final ModuleURN PROVIDER_URN =
            new ModuleURN("metc:remote:emitter");  //$NON-NLS-1$
}
