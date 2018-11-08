package com.marketcetera.ors.filters;

import java.util.Date;

import org.marketcetera.core.CoreException;
import org.marketcetera.quickfix.messagefactory.FIXMessageAugmentor;
import org.marketcetera.util.misc.ClassVersion;

import com.marketcetera.ors.history.ReportHistoryServices;

import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.field.TransactTime;

/**
 * Inserts the {@link TransactTime} field into an order if it's not currently present
 *
 * @author Toli Kuznets
 * @version $Id: TransactionTimeInsertMessageModifier.java 16468 2014-05-12 00:36:56Z colin $
 */
@ClassVersion("$Id: TransactionTimeInsertMessageModifier.java 16468 2014-05-12 00:36:56Z colin $")
public class TransactionTimeInsertMessageModifier implements MessageModifier
{
    @Override
    public boolean modifyMessage
        (Message order,
         ReportHistoryServices historyServices,
         FIXMessageAugmentor augmentor)
        throws CoreException
    {
        /** Only put the field in if it's not present */
        try {
            // test for presence
            order.getString(TransactTime.FIELD);
            return false;
        } catch (FieldNotFound ex){
            if(augmentor.needsTransactTime(order)) {
                order.setField(new TransactTime(new Date())); //non-i18n
                return true;
            }
            return false;
        }

    }
}
