package org.marketcetera.trade.modules;

import org.marketcetera.admin.HasUser;
import org.marketcetera.event.HasFIXMessage;
import org.marketcetera.fix.HasSessionId;
import org.marketcetera.module.AbstractDataReemitterModule;
import org.marketcetera.module.AutowiredModule;
import org.marketcetera.module.DataEmitterSupport;
import org.marketcetera.module.ModuleURN;
import org.marketcetera.module.ReceiveDataException;
import org.marketcetera.trade.HasBrokerID;
import org.marketcetera.trade.OutgoingMessage;
import org.marketcetera.trade.OutgoingMessageFactory;
import org.marketcetera.trade.service.OutgoingMessageService;
import org.marketcetera.util.log.I18NBoundMessage2P;
import org.springframework.beans.factory.annotation.Autowired;

/* $License$ */

/**
 * Persists the owner of outgoing messages.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@AutowiredModule
public class OutgoingMessagePersistenceModule
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
        if(!(inData instanceof HasUser)) {
            throw new ReceiveDataException(new I18NBoundMessage2P(org.marketcetera.module.Messages.WRONG_DATA_TYPE,
                                                                  HasUser.class.getSimpleName(),
                                                                  inData.getClass().getSimpleName()));
        }
        if(!(inData instanceof HasBrokerID)) {
            throw new ReceiveDataException(new I18NBoundMessage2P(org.marketcetera.module.Messages.WRONG_DATA_TYPE,
                                                                  HasBrokerID.class.getSimpleName(),
                                                                  inData.getClass().getSimpleName()));
        }
        if(!(inData instanceof HasSessionId)) {
            throw new ReceiveDataException(new I18NBoundMessage2P(org.marketcetera.module.Messages.WRONG_DATA_TYPE,
                                                                  HasSessionId.class.getSimpleName(),
                                                                  inData.getClass().getSimpleName()));
        }
        OutgoingMessage outgoingMessage = outgoingMessageFactory.create(((HasFIXMessage)inData).getMessage(),
                                                                        ((HasBrokerID)inData).getBrokerID(),
                                                                        ((HasSessionId)inData).getSessionId(),
                                                                        ((HasUser)inData).getUser());
        outgoingMessage = outgoingMessageService.save(outgoingMessage);
        return (HasFIXMessage)inData;
    }
    /**
     * Create a new OrderPersistenceModule instance.
     *
     * @param inURN a <code>ModuleURN</code> value
     */
    OutgoingMessagePersistenceModule(ModuleURN inURN)
    {
        super(inURN,
              true);
    }
    /**
     * provides access to outgoing message services
     */
    @Autowired
    private OutgoingMessageService outgoingMessageService;
    /**
     * creates {@link OutgoingMessage] objects
     */
    @Autowired
    private OutgoingMessageFactory outgoingMessageFactory;
}
