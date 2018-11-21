package org.marketcetera.ors.filters;

import java.util.Date;

import org.marketcetera.core.CoreException;
import org.marketcetera.quickfix.FIXVersion;
import org.marketcetera.quickfix.messagefactory.FIXMessageAugmentor;

import quickfix.FieldNotFound;
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
    @Override
    public boolean modifyMessage(quickfix.Message inOrder)
            throws CoreException
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
