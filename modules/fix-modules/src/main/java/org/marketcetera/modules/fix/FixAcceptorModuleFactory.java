package org.marketcetera.modules.fix;

import org.marketcetera.fix.SessionSettingsProvider;
import org.marketcetera.module.ModuleCreationException;
import org.marketcetera.module.ModuleFactory;
import org.marketcetera.module.ModuleURN;

/* $License$ */

/**
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
    protected FixAcceptorModuleFactory()
    {
        super(PROVIDER_URN,
              Messages.ACCEPTOR_PROVIDER_DESCRIPTION,
              true,
              false,
              String.class,
              SessionSettingsProvider.class);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.module.ModuleFactory#create(java.lang.Object[])
     */
    @Override
    public FixAcceptor create(Object... inParameters)
            throws ModuleCreationException
    {
        String identifier = String.valueOf(inParameters[0]);
        SessionSettingsProvider settingsProvider = (SessionSettingsProvider)inParameters[1];
        ModuleURN instanceUrn = new ModuleURN(PROVIDER_URN,
                                              identifier);
        return new FixAcceptor(instanceUrn,
                                settingsProvider);
    }
    /**
     * provider URN value
     */
    public static final ModuleURN PROVIDER_URN = new ModuleURN("metc:fix:acceptor");  //$NON-NLS-1$
}
