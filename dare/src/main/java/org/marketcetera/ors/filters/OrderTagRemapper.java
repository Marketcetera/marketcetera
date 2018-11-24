package org.marketcetera.ors.filters;

import org.marketcetera.brokers.MessageModifier;
import org.marketcetera.core.CoreException;
import org.marketcetera.fix.ServerFixSession;
import org.marketcetera.quickfix.FIXMessageUtil;
import org.marketcetera.util.log.SLF4JLoggerProxy;

import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.field.ClOrdID;
import quickfix.field.OrdStatus;

/* $License$ */

/**
 * Remaps saved order tags.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 2.4.2
 */
public class OrderTagRemapper
        implements MessageModifier
{
    /* (non-Javadoc)
     * @see org.marketcetera.brokers.MessageModifier#modify(org.marketcetera.fix.ServerFixSession, quickfix.Message)
     */
    @Override
    public boolean modify(ServerFixSession inServerFixSession,
                          Message inMessage)
    {
        SLF4JLoggerProxy.debug(this,
                               "Tag remapper examining {}",
                               inMessage);
        boolean modified = OrderTagRecorder.populateTags(inMessage);
        if(inMessage.isSetField(OrdStatus.FIELD)) {
            OrdStatus orderStatus = new OrdStatus();
            try {
                inMessage.getField(orderStatus);
            } catch (FieldNotFound e) {
                throw new CoreException(e);
            }
            if(!FIXMessageUtil.isCancellable(orderStatus.getValue())) {
                ClOrdID orderID = new ClOrdID();
                if(inMessage.isSetField(ClOrdID.FIELD)) {
                    try {
                        inMessage.getField(orderID);
                    } catch (FieldNotFound e) {
                        throw new CoreException(e);
                    }
                    SLF4JLoggerProxy.debug(this,
                                           "Retiring stored tags for {} because the status is no longer cancellable at {}",
                                           orderID,
                                           String.valueOf(orderStatus.getValue()));
                    OrderTagRecorder.retireOrder(orderID.getValue());
                }
            }
        }
        return modified;
    }
}
