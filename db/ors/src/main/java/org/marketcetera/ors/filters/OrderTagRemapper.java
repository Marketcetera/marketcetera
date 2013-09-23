package org.marketcetera.ors.filters;

import org.marketcetera.core.CoreException;
import org.marketcetera.ors.history.ReportHistoryServices;
import org.marketcetera.quickfix.FIXMessageUtil;
import org.marketcetera.quickfix.messagefactory.FIXMessageAugmentor;
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
 * @version $Id: OrderTagRemapper.java 82949 2013-03-27 00:51:36Z colin $
 * @since $Release$
 */
public class OrderTagRemapper
        implements MessageModifier
{
    /* (non-Javadoc)
     * @see org.marketcetera.ors.filters.MessageModifier#modifyMessage(quickfix.Message, org.marketcetera.ors.history.ReportHistoryServices, org.marketcetera.quickfix.messagefactory.FIXMessageAugmentor)
     */
    @Override
    public boolean modifyMessage(Message inMessage,
                                 ReportHistoryServices inHistoryServices,
                                 FIXMessageAugmentor inAugmentor)
            throws CoreException
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
