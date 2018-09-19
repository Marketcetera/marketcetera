package org.marketcetera.modules.fix;

import java.util.Collection;

import org.marketcetera.fix.ActiveFixSession;
import org.marketcetera.fix.FixSession;
import org.marketcetera.fix.FixSettingsProvider;
import org.marketcetera.module.ModuleURN;
import org.marketcetera.util.ws.stateful.PortDescriptor;
import org.marketcetera.util.ws.stateful.UsesPort;

import com.google.common.collect.Lists;

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
        implements UsesPort
{
    /* (non-Javadoc)
     * @see org.marketcetera.util.ws.stateful.UsesPort#getPortDescriptors()
     */
    @Override
    public Collection<PortDescriptor> getPortDescriptors()
    {
        return ports;
    }
    /**
     * Create a new FixAcceptor instance.
     *
     * @param inURN a <code>ModuleURN</code> value
     */
    protected FixAcceptorModule(ModuleURN inURN)
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
        ports.add(new PortDescriptor(inFixSettingsProvider.getAcceptorPort(),
                                     "FIX Acceptor Service"));
        SocketAcceptor socketAcceptor = new SocketAcceptor(inApplication,
                                                           inFixSettingsProvider.getMessageStoreFactory(inSessionSettings),
                                                           inSessionSettings,
                                                           inFixSettingsProvider.getLogFactory(inSessionSettings),
                                                           inFixSettingsProvider.getMessageFactory());
        return socketAcceptor;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.modules.fix.AbstractFixModule#getFixSessions()
     */
    @Override
    protected Collection<FixSession> getFixSessions()
    {
        Collection<FixSession> fixSessions = Lists.newArrayList();
        for(ActiveFixSession broker : getBrokerService().getActiveFixSessions()) {
            if(broker.getFixSession().isAcceptor()) {
                fixSessions.add(broker.getFixSession());
            }
        }
        return fixSessions;
    }
    /**
     * static reference to this singleton object
     */
    static FixAcceptorModule instance;
    /**
     * indicates ports in use
     */
    private final Collection<PortDescriptor> ports = Lists.newArrayList();
}
