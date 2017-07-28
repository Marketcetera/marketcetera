package org.marketcetera.modules.fix;

import org.marketcetera.fix.FixSettingsProvider;
import org.marketcetera.fix.SessionSettingsProvider;
import org.marketcetera.module.ModuleURN;

import quickfix.Application;
import quickfix.ConfigError;
import quickfix.SessionSettings;
import quickfix.ThreadedSocketAcceptor;
import quickfix.mina.SessionConnector;

/* $License$ */

/**
 * Provides FIX acceptor sessions.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class FixAcceptorModule
        extends AbstractFixModule
{
    /**
     * Create a new FixAcceptor instance.
     *
     * @param inURN a <code>ModuleURN</code> value
     */
    protected FixAcceptorModule(ModuleURN inURN)
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
        ThreadedSocketAcceptor socketAcceptor = new ThreadedSocketAcceptor(inApplication,
                                                                           inFixSettingsProvider.getMessageStoreFactory(sessions),
                                                                           sessions,
                                                                           inFixSettingsProvider.getLogFactory(sessions),
                                                                           inFixSettingsProvider.getMessageFactory());
        return socketAcceptor;
    }
}
