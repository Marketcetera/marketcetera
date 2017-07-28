package org.marketcetera.modules.fix;

import org.marketcetera.fix.FixSettingsProvider;
import org.marketcetera.fix.SessionSettingsProvider;
import org.marketcetera.module.ModuleURN;

import quickfix.Application;
import quickfix.ConfigError;
import quickfix.SessionSettings;
import quickfix.ThreadedSocketInitiator;
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
     */
    protected FixInitiatorModule(ModuleURN inURN)
    {
        super(inURN);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.modules.fix.AbstractFixModule#createEngine(quickfix.Application, org.marketcetera.fix.FixSettingsProvider, org.marketcetera.fix.SessionSettingsProvider)
     */
    @Override
    protected SessionConnector createEngine(Application inApplication,
                                            FixSettingsProvider inFixSettingsProvider,
                                            SessionSettingsProvider inSessionSettingsProvider)
            throws ConfigError
    {
        SessionSettings sessions = inSessionSettingsProvider.create();
        return new ThreadedSocketInitiator(inApplication,
                                           inFixSettingsProvider.getMessageStoreFactory(sessions),
                                           sessions,
                                           inFixSettingsProvider.getLogFactory(sessions),
                                           inFixSettingsProvider.getMessageFactory());
    }
}
