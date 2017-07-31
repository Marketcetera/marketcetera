package org.marketcetera.modules.fix;

import org.marketcetera.fix.FixSettingsProvider;
import org.marketcetera.fix.SessionSettingsProvider;
import org.marketcetera.module.ModuleURN;

import quickfix.Application;
import quickfix.ConfigError;
import quickfix.SessionSettings;
import quickfix.SocketInitiator;
import quickfix.mina.SessionConnector;

/* $License$ */

/**
 * Provides FIX initiator sessions.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class FixInitiatorModule
        extends AbstractFixModule
{
    /**
     * Create a new FixInitiator instance.
     *
     * @param inURN a <code>ModuleURN</code> value
     * @param inSessionSettingsProvider a <code>SessionSettingsProvider</code> value
     */
    protected FixInitiatorModule(ModuleURN inURN,
                                 SessionSettingsProvider inSessionSettingsProvider)
    {
        super(inURN,
              inSessionSettingsProvider);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.modules.fix.AbstractFixModule#createEngine(quickfix.Application, org.marketcetera.fix.FixSettingsProvider, quickfix.SessionSettings)
     */
    @Override
    protected SessionConnector createEngine(Application inApplication,
                                            FixSettingsProvider inFixSettingsProvider,
                                            SessionSettings inSessionSettings)
            throws ConfigError
    {
        SocketInitiator socketInitiator = new SocketInitiator(inApplication,
                                                              inFixSettingsProvider.getMessageStoreFactory(inSessionSettings),
                                                              inSessionSettings,
                                                              inFixSettingsProvider.getLogFactory(inSessionSettings),
                                                              inFixSettingsProvider.getMessageFactory());
        return socketInitiator;
    }
}
