package org.marketcetera.modules.cep.esper;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.module.ModuleFactory;
import org.marketcetera.module.ModuleCreationException;
import org.marketcetera.module.ModuleURN;

/* $License$ */
/**
 * Provider that will process incoming data via an Esper Engine.
 * The provider will support multiple module instances. Each instance
 * has its own separate Esper runtime.
 *
 * The instances are auto-created when they are referred to in a data flow
 * request and they are auto-started.
 * <p>
 * The factory has the following characteristics.
 * <table>
 * <caption>CEPEsperFactory characteristics</caption>
 * <tr><th>Provider URN:</th><td><code>metc:cep:esper</code></td></tr>
 * <tr><th>Cardinality:</th><td>Multi-Instance</td></tr>
 * <tr><th>Auto-Instantiated:</th><td>Yes</td></tr>
 * <tr><th>Auto-Started:</th><td>Yes</td></tr>
 * <tr><th>Instantiation Arguments:</th><td>{@link ModuleURN}: module instance URN</td></tr>
 * <tr><th>Module Type:</th><td>{@link CEPEsperProcessor}</td></tr>
 * </table>
 *
 * @see CEPEsperProcessor
 * @author anshul@marketcetera.com
 * @author toli@marketcetera.com
 * @since 1.0.0
 * @version $Id$
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public final class CEPEsperFactory extends ModuleFactory {
    /**
     * Creates an instance.
     *
     */
    public CEPEsperFactory() {
        super(PROVIDER_URN, Messages.PROVIDER_DESCRIPTION, true, true,
                ModuleURN.class);
    }

    @Override
    public CEPEsperProcessor create(Object... inParameters)
            throws ModuleCreationException {
        return new CEPEsperProcessor((ModuleURN)inParameters[0]);
    }

    /**
     * The Provider URN.
     */
    public static final ModuleURN PROVIDER_URN =
            new ModuleURN("metc:cep:esper");  //$NON-NLS-1$

}
