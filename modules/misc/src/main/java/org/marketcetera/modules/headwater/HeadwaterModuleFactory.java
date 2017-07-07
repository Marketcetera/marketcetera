package org.marketcetera.modules.headwater;

import org.marketcetera.module.Module;
import org.marketcetera.module.ModuleCreationException;
import org.marketcetera.module.ModuleFactory;
import org.marketcetera.module.ModuleURN;

/* $License$ */

/**
 * Creates {@link HeadwaterModule} objects.
 * 
 * <p>The factory has the following characteristics.
 * <table>
 * <tr><th>Provider URN:</th><td><code>metc:misc:headwater</code></td></tr>
 * <tr><th>Cardinality:</th><td>Multi-Instance</td></tr>
 * <tr><th>Auto-Instantiated:</th><td>No</td></tr>
 * <tr><th>Auto-Started:</th><td>Yes</td></tr>
 * <tr><th>Instantiation Arguments:</th><td>String: instance name</td></tr>
 * <tr><th>Module Type:</th><td>{@link HeadwaterModule}</td></tr>
 * </table></p>
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class HeadwaterModuleFactory
        extends ModuleFactory
{
    /**
     * Create a new PublisherModuleFactory instance.
     */
    public HeadwaterModuleFactory()
    {
        super(PROVIDER_URN,
              Messages.PROVIDER_DESCRIPTION,
              true,
              false,
              String.class);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.module.ModuleFactory#create(java.lang.Object[])
     */
    @Override
    public Module create(Object... inParameters)
            throws ModuleCreationException
    {
        if(inParameters == null || inParameters.length != 1) {
            throw new ModuleCreationException(Messages.PARAMETER_COUNT_ERROR);
        }
        String instanceName = String.valueOf(inParameters[0]);
        return new HeadwaterModule(new ModuleURN(PROVIDER_URN,
                                                 instanceName),
                                   instanceName);
    }
    /**
     * identifier used to identify this type of module
     */
    public static final String IDENTIFIER = "headwater";
    /**
     * unique provider URN for the receiver module
     */
    public static final ModuleURN PROVIDER_URN = new ModuleURN("metc:misc:"+IDENTIFIER);  //$NON-NLS-1$
}
