package org.marketcetera.modules.fix;

import org.marketcetera.core.PlatformServices;
import org.marketcetera.event.HasFIXMessage;
import org.marketcetera.fix.HasSessionId;
import org.marketcetera.fix.IncomingMessagePublisher;
import org.marketcetera.module.AbstractDataReemitterModule;
import org.marketcetera.module.AutowiredModule;
import org.marketcetera.module.DataEmitterSupport;
import org.marketcetera.module.ModuleURN;
import org.marketcetera.module.ReceiveDataException;
import org.marketcetera.util.log.I18NBoundMessage2P;
import org.springframework.beans.factory.annotation.Autowired;

/* $License$ */

/**
 * Facilitates the broadcast of trade messages.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@AutowiredModule
public class FixMessageBroadcastModule
        extends AbstractDataReemitterModule
{
    /* (non-Javadoc)
     * @see org.marketcetera.module.AbstractDataReemitterModule#onReceiveData(java.lang.Object, org.marketcetera.module.DataEmitterSupport)
     */
    @Override
    protected HasFIXMessage onReceiveData(Object inData,
                                          DataEmitterSupport inDataSupport)
    {
        if(!(inData instanceof HasFIXMessage)) {
            throw new ReceiveDataException(new I18NBoundMessage2P(org.marketcetera.module.Messages.WRONG_DATA_TYPE,
                                                                  HasFIXMessage.class.getSimpleName(),
                                                                  inData.getClass().getSimpleName()));
        }
        if(!(inData instanceof HasSessionId)) {
            throw new ReceiveDataException(new I18NBoundMessage2P(org.marketcetera.module.Messages.WRONG_DATA_TYPE,
                                                                  HasSessionId.class.getSimpleName(),
                                                                  inData.getClass().getSimpleName()));
        }
        if(incomingMessagePublisher == null) {
            Messages.NO_FIX_MESSAGE_PUBLISHER.warn(this,
                                                   inData);
        } else {
            try {
                incomingMessagePublisher.reportMessage(((HasSessionId)inData).getSessionId(),
                                                       ((HasFIXMessage)inData).getMessage());
            } catch (Exception e) {
                PlatformServices.handleException(this,
                                                 "Error publishing FIX message",
                                                 e);
            }
        }
        return (HasFIXMessage)inData;
    }
    /**
     * Create a new FixMessageBroadcastModule instance.
     *
     * @param inURN a <code>ModuleURN</code> value
     */
    protected FixMessageBroadcastModule(ModuleURN inURN)
    {
        super(inURN,
              true);
    }
    /**
     * fix message listener
     */
    @Autowired(required=false)
    private IncomingMessagePublisher incomingMessagePublisher;
}
