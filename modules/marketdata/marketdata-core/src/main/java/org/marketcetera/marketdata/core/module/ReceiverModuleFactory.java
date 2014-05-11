package org.marketcetera.marketdata.core.module;

import org.apache.commons.lang.StringUtils;
import org.marketcetera.module.Module;
import org.marketcetera.module.ModuleCreationException;
import org.marketcetera.module.ModuleFactory;
import org.marketcetera.module.ModuleURN;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Creates {@link ReceiverModule} modules.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 2.4.0
 */
@ClassVersion("$Id$")
public class ReceiverModuleFactory
        extends ModuleFactory
{
    /* (non-Javadoc)
     * @see org.marketcetera.module.ModuleFactory#create(java.lang.Object[])
     */
    @Override
    public Module create(Object... inParameters)
            throws ModuleCreationException
    {
        if(inParameters == null || inParameters.length != 1) {
            throw new ModuleCreationException(Messages.INCORRECT_PARAMETER_COUNT);
        }
        // parameter 1 is the URN instance name of the receiver
        Object parameter = inParameters[0];
        if(parameter == null || !(parameter instanceof String)) {
            throw new ModuleCreationException(Messages.INCORRECT_PARAMETER_COUNT);
        }
        String instanceName = StringUtils.trimToNull((String)parameter);
        if(instanceName == null) {
            throw new ModuleCreationException(Messages.INCORRECT_PARAMETER_COUNT);
        }
        return new ReceiverModule(instanceName);
    }
    /**
     * Create a new ReceiverModuleFactory instance.
     */
    public ReceiverModuleFactory()
    {
        super(PROVIDER_URN,
              Messages.PROVIDER_DESCRIPTION,
              true,
              false,
              String.class);
   }
    /**
     * human-readable identifier used as the provider name
     */
    public static final String IDENTIFIER = "receiver";  //$NON-NLS-1$
    /**
     * unique provider URN for the receiver module
     */
    public static final ModuleURN PROVIDER_URN = new ModuleURN("metc:misc:" + IDENTIFIER);  //$NON-NLS-1$
}
