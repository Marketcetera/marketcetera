package org.marketcetera.ors.filters;

import java.util.Date;

import org.marketcetera.brokers.MessageModifier;
import org.marketcetera.core.CoreException;
import org.marketcetera.fix.ServerFixSession;
import org.marketcetera.quickfix.FIXVersion;
import org.marketcetera.quickfix.messagefactory.FIXMessageAugmentor;

import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.field.TransactTime;

/**
 * Inserts the {@link TransactTime} field into an order if it's not currently present
 *
 * @author Toli Kuznets
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 */
public class TransactionTimeInsertMessageModifier
        implements MessageModifier
{
    /* (non-Javadoc)
     * @see org.marketcetera.brokers.MessageModifier#modify(org.marketcetera.fix.ServerFixSession, quickfix.Message)
     */
    @Override
    public boolean modify(ServerFixSession inServerFixSession,
                          Message inOrder)
    {
        // Only put the field in if it's not present
        if(!inOrder.isSetField(quickfix.field.TransactTime.FIELD)) {
            return false;
        }
        FIXVersion fixVersion;
        try {
            fixVersion = FIXVersion.getFIXVersion(inOrder);
            FIXMessageAugmentor augmentor = fixVersion.getMessageFactory().getMsgAugmentor();
            if(augmentor.needsTransactTime(inOrder)) {
                inOrder.setField(new TransactTime(new Date()));
                return true;
            }
            return false;
        } catch (FieldNotFound e) {
            throw new CoreException(e);
        }
    }
}
