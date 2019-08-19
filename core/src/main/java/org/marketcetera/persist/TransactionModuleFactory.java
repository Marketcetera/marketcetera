package org.marketcetera.persist;

import org.marketcetera.module.Module;
import org.marketcetera.module.ModuleCreationException;
import org.marketcetera.module.ModuleFactory;
import org.marketcetera.module.ModuleURN;

/* $License$ */

/**
 * <code>ModuleFactory</code> implementation for order to FIX message conversion.
 * <p>
 * The factory has the following characteristics.
 * <table>
 * <tr><th>Provider URN:</th><td><code>metc:core:transaction</code></td></tr>
 * <tr><th>Cardinality:</th><td>Singleton</td></tr>
 * <tr><th>Instance URN:</th><td><code>metc:core:transaction:single</code></td></tr>
 * <tr><th>Auto-Instantiated:</th><td>Yes</td></tr>
 * <tr><th>Auto-Started:</th><td>Yes</td></tr>
 * <tr><th>Instantiation Arguments:</th><td>none</td></tr>
 * <tr><th>Module Type:</th><td>{@link TransactionModule}</td></tr>
 * </table>
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class TransactionModuleFactory
        extends ModuleFactory
{
    /**
     * Create a new TransactionModuleFactory instance.
     */
    public TransactionModuleFactory()
    {
        super(PROVIDER_URN,
              Messages.TRANSACTION_PROVIDER_DESCRIPTION,
              false,
              true);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.module.ModuleFactory#create(java.lang.Object[])
     */
    @Override
    public Module create(Object... inParameters)
            throws ModuleCreationException
    {
        return new TransactionModule(INSTANCE_URN);
    }
    /**
     * identifier for this URN
     */
    public static final String IDENTIFIER = "transaction";
    /**
     * provider URN value
     */
    public static final ModuleURN PROVIDER_URN = new ModuleURN("metc:core:"+IDENTIFIER);  //$NON-NLS-1$
    /**
     * instance URN value
     */
    public static final ModuleURN INSTANCE_URN = new ModuleURN(PROVIDER_URN,
                                                               "single");  //$NON-NLS-1$
}
