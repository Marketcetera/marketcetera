package org.marketcetera.modules.fix;

import org.apache.commons.lang.Validate;
import org.marketcetera.core.fix.FixSettingsProvider;
import org.marketcetera.core.fix.FixSettingsProviderFactory;
import org.marketcetera.module.AutowiredModule;
import org.marketcetera.module.DataEmitter;
import org.marketcetera.module.DataEmitterSupport;
import org.marketcetera.module.DataFlowID;
import org.marketcetera.module.DataRequest;
import org.marketcetera.module.Module;
import org.marketcetera.module.ModuleException;
import org.marketcetera.module.ModuleURN;
import org.marketcetera.module.RequestDataException;
import org.marketcetera.module.RequestID;
import org.springframework.beans.factory.annotation.Autowired;

import quickfix.Application;
import quickfix.DoNotSend;
import quickfix.FieldNotFound;
import quickfix.IncorrectDataFormat;
import quickfix.IncorrectTagValue;
import quickfix.Message;
import quickfix.RejectLogon;
import quickfix.SessionID;
import quickfix.UnsupportedMessageType;

/* $License$ */

/**
 * Provides a FIX acceptor module.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@AutowiredModule
public class FixAcceptorModule
        extends Module
        implements DataEmitter,Application
{
    /* (non-Javadoc)
     * @see org.marketcetera.module.DataEmitter#requestData(org.marketcetera.module.DataRequest, org.marketcetera.module.DataEmitterSupport)
     */
    @Override
    public void requestData(DataRequest inRequest,
                            DataEmitterSupport inSupport)
            throws RequestDataException
    {
    }
    /* (non-Javadoc)
     * @see org.marketcetera.module.DataEmitter#cancel(org.marketcetera.module.DataFlowID, org.marketcetera.module.RequestID)
     */
    @Override
    public void cancel(DataFlowID inFlowID,
                       RequestID inRequestID)
    {
    }
    /**
     * Create a new FixAcceptorModule instance.
     *
     * @param inUrn a <code>ModuleURN</code> value
     */
    FixAcceptorModule(ModuleURN inUrn)
    {
        super(inUrn,
              false);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.module.Module#preStart()
     */
    @Override
    protected void preStart()
            throws ModuleException
    {
        Validate.notNull(fixSettingsProviderFactory,
                         "This module requires a FIX settings provider factory"); // TODO
        FixSettingsProvider fixSettingsProvider = fixSettingsProviderFactory.create();
        String acceptorHost = fixSettingsProvider.getAcceptorHost();
        int acceptorPort = fixSettingsProvider.getAcceptorPort();
        // TODO how to figure out sessions? or just let 'em fly?, no, we need a session provider
        // TODO how to dynamically add a session? via a data request? that's an interesting idea. how to dynamically remove a session, then? this might need to wait for 3.1.x.
    }
    /* (non-Javadoc)
     * @see org.marketcetera.module.Module#preStop()
     */
    @Override
    protected void preStop()
            throws ModuleException
    {
    }
    /* (non-Javadoc)
     * @see quickfix.Application#onCreate(quickfix.SessionID)
     */
    @Override
    public void onCreate(SessionID inSessionId)
    {
    }
    /* (non-Javadoc)
     * @see quickfix.Application#onLogon(quickfix.SessionID)
     */
    @Override
    public void onLogon(SessionID inSessionId)
    {
    }
    /* (non-Javadoc)
     * @see quickfix.Application#onLogout(quickfix.SessionID)
     */
    @Override
    public void onLogout(SessionID inSessionId)
    {
    }
    /* (non-Javadoc)
     * @see quickfix.Application#toAdmin(quickfix.Message, quickfix.SessionID)
     */
    @Override
    public void toAdmin(Message inMessage,
                        SessionID inSessionId)
    {
    }
    /* (non-Javadoc)
     * @see quickfix.Application#fromAdmin(quickfix.Message, quickfix.SessionID)
     */
    @Override
    public void fromAdmin(Message inMessage,
                          SessionID inSessionId)
            throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, RejectLogon
    {
    }
    /* (non-Javadoc)
     * @see quickfix.Application#toApp(quickfix.Message, quickfix.SessionID)
     */
    @Override
    public void toApp(Message inMessage,
                      SessionID inSessionId)
            throws DoNotSend
    {
    }
    /* (non-Javadoc)
     * @see quickfix.Application#fromApp(quickfix.Message, quickfix.SessionID)
     */
    @Override
    public void fromApp(Message inMessage,
                        SessionID inSessionId)
            throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, UnsupportedMessageType
    {
    }
    /**
     * provides fix settings
     */
    @Autowired
    private FixSettingsProviderFactory fixSettingsProviderFactory;
}
