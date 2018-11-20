package org.marketcetera.trade.modules;

import org.marketcetera.admin.HasUser;
import org.marketcetera.event.HasFIXMessage;
import org.marketcetera.module.AbstractDataReemitterModule;
import org.marketcetera.module.AutowiredModule;
import org.marketcetera.module.DataEmitterSupport;
import org.marketcetera.module.ModuleURN;
import org.marketcetera.module.ReceiveDataException;
import org.marketcetera.trade.service.MessageOwnerService;
import org.marketcetera.util.log.I18NBoundMessage2P;
import org.springframework.beans.factory.annotation.Autowired;

/* $License$ */

/**
 * Caches the owner of outgoing messages.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@AutowiredModule
public class OutgoingMessageCachingModule
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
        OwnedMessage ownedMessage = (OwnedMessage)inData;
        messageOwnerService.cacheMessageOwner(((HasFIXMessage)ownedMessage).getMessage(),
                                              ((HasUser)ownedMessage).getUser().getUserID());
        return ownedMessage;
    }
    /**
     * Create a new OrderPersisterModule instance.
     *
     * @param inURN a <code>ModuleURN</code> value
     */
    OutgoingMessageCachingModule(ModuleURN inURN)
    {
        super(inURN,
              true);
    }
    /**
     * provides access to message owner services
     */
    @Autowired
    private MessageOwnerService messageOwnerService;
}
