package org.marketcetera.quickfix;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.core.MarketceteraException;
import org.marketcetera.quickfix.messagefactory.FIXMessageAugmentor;
import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.field.TransactTime;

import java.util.Date;

/**
 * Inserts the {@link TransactTime} field into an order if it's not currently present
 *
 * @author Toli Kuznets
 * @version $Id$
 */
@ClassVersion("$Id$")
public class TransactionTimeInsertMessageModifier implements MessageModifier
{
    public TransactionTimeInsertMessageModifier() {
    }

    public boolean modifyMessage(Message order, FIXMessageAugmentor augmentor) throws MarketceteraException {
        /** Only put the field in if it's not present */
        try {
            // test for presence
            order.getString(TransactTime.FIELD);
            return false;
        } catch (FieldNotFound ex){
            if(augmentor.needsTransactTime(order)) {
                order.setField(new TransactTime(new Date()));
                return true;
            }
            return false;
        }

    }
}
