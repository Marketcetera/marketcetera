package org.marketcetera.modules.fix;

import org.marketcetera.module.Module;
import org.marketcetera.module.ModuleCreationException;
import org.marketcetera.module.ModuleFactory;
import org.marketcetera.module.ModuleURN;

/* $License$ */

/**
 * Creates {@link FixAcceptorModule} instances.
 * 
 * The factory has the following characteristics.
 * <table>
 * <tr><th>Provider URN:</th><td><code>metc:fix:acceptor</code></td></tr>
 * <tr><th>Cardinality:</th><td>Singleton</td></tr>
 * <tr><th>Auto-Instantiated:</th><td>No</td></tr>
 * <tr><th>Auto-Started:</th><td>No</td></tr>
 * <tr><th>Instantiation Arguments:</th><td>none</td></tr>
 * <tr><th>Module Type:</th><td>{@link FixAcceptorModule}</td></tr>
 * </table>
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class FixAcceptorModuleFactory
        extends ModuleFactory
{
    /**
     * Create a new FixAcceptorModuleFactory instance.
     */
    public FixAcceptorModuleFactory()
    {
        super(PROVIDER_URN,
              Messages.FIX_ACCEPTOR_PROVIDER_DESCRIPTION,
              false,
              false);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.module.ModuleFactory#create(java.lang.Object[])
     */
    @Override
    public Module create(Object... inParameters)
            throws ModuleCreationException
    {
        String instanceName = "singleton";
        ModuleURN instanceUrn = new ModuleURN(PROVIDER_URN,
                                              instanceName);
        return new FixAcceptorModule(instanceUrn);
    }
    /**
     * provider URN for FIX acceptor module
     */
    public static final ModuleURN PROVIDER_URN = new ModuleURN("metc:fix:acceptor");  //$NON-NLS-1$
}
