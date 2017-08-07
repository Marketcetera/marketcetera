package org.marketcetera.modules.fix;

import java.util.Collection;

import org.marketcetera.brokers.Broker;
import org.marketcetera.fix.FixSession;
import org.marketcetera.fix.FixSettingsProvider;
import org.marketcetera.module.ModuleURN;

import com.google.common.collect.Lists;

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
     */
    protected FixInitiatorModule(ModuleURN inURN)
    {
        super(inURN);
        instance = this;
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
    /* (non-Javadoc)
     * @see org.marketcetera.modules.fix.AbstractFixModule#getFixSessions()
     */
    @Override
    protected Collection<FixSession> getFixSessions()
    {
        Collection<FixSession> fixSessions = Lists.newArrayList();
        for(Broker broker : getBrokerService().getBrokers()) {
            if(!broker.getFixSession().isAcceptor()) {
                fixSessions.add(broker.getFixSession());
            }
        }
        return fixSessions;
    }
    /**
     * holds a static reference to this singleton object
     */
    static FixInitiatorModule instance;
}
