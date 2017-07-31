package org.marketcetera.modules.fix;

import org.marketcetera.fix.FixSettingsProvider;
import org.marketcetera.fix.SessionSettingsProvider;
import org.marketcetera.module.ModuleURN;

import quickfix.Application;
import quickfix.ConfigError;
import quickfix.SessionSettings;
import quickfix.SocketAcceptor;
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
     * @param inSessionSettingsProvider a <code>SessionSettingsProvider</code> value
     */
    protected FixAcceptorModule(ModuleURN inURN,
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
        SocketAcceptor socketAcceptor = new SocketAcceptor(inApplication,
                                                           inFixSettingsProvider.getMessageStoreFactory(inSessionSettings),
                                                           inSessionSettings,
                                                           inFixSettingsProvider.getLogFactory(inSessionSettings),
                                                           inFixSettingsProvider.getMessageFactory());
        return socketAcceptor;
    }
}
