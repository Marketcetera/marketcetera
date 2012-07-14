package org.marketcetera.modules.cep.system;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.module.ModuleFactory;
import org.marketcetera.module.ModuleCreationException;
import org.marketcetera.module.ModuleURN;

/* $License$ */
/**
 * See {@link CEPSystemProcessor} for an explanation of how the System CEP module works.
 * <p>
 * The factory has the following characteristics.
 * <table>
 * <tr><th>Provider URN:</th><td><code>metc:cep:system</code></td></tr>
 * <tr><th>Cardinality:</th><td>Multi-Instance</td></tr>
 * <tr><th>Auto-Instantiated:</th><td>Yes</td></tr>
 * <tr><th>Auto-Started:</th><td>Yes</td></tr>
 * <tr><th>Instantiation Arguments:</th><td>{@link ModuleURN}: module instance URN</td></tr>
 * <tr><th>Module Type:</th><td>{@link CEPSystemProcessor}</td></tr>
 * </table>
 *
 * @see CEPSystemProcessor
 * @author toli@marketcetera.com
 * @since 1.0.0
 * @version $Id$
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public final class CEPSystemFactory extends ModuleFactory {
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