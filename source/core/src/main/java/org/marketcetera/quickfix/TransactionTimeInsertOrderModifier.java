package org.marketcetera.quickfix;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.core.MarketceteraException;
import org.marketcetera.quickfix.messagefactory.FIXMessageAugmentor;
import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.field.TransactTime;

/**
 * Inserts the {@link TransactTime} field into an order if it's not currently present
 *
 * @author Toli Kuznets
 * @version $Id$
 */
@ClassVersion("$Id$")
public class TransactionTimeInsertOrderModifier implements OrderModifier
{
    public TransactionTimeInsertOrderModifier() {
    }

    public boolean modifyOrder(Message order, FIXMessageAugmentor augmentor) throws MarketceteraException {
        /** Only put the field in if it's not present */
        try {
            // test for presence
            order.getString(TransactTime.FIELD);
            return false;
        } catch (FieldNotFound ex){
            if(augmentor.needsTransactTime(order)) {
                order.setField(new TransactTime());
                return true;
            }
            return false;
        }

    }
}
