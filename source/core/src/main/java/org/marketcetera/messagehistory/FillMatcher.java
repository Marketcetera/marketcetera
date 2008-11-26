/**
 * 
 */
package org.marketcetera.messagehistory;


import org.marketcetera.core.ClassVersion;
import org.marketcetera.quickfix.FIXMessageUtil;

import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.field.LastShares;
import quickfix.field.OrdStatus;
import ca.odell.glazedlists.matchers.Matcher;

@ClassVersion("$Id$") //$NON-NLS-1$
public final class FillMatcher implements Matcher<MessageHolder> {
    public boolean matches(MessageHolder holder) {
        if (holder instanceof IncomingMessageHolder) {
            IncomingMessageHolder incomingHolder = (IncomingMessageHolder) holder;
            
            try {
                Message message = incomingHolder.getMessage();
                char ordStatus = message.getChar(OrdStatus.FIELD);
                if (FIXMessageUtil.isExecutionReport(message)
                        && (ordStatus == OrdStatus.PARTIALLY_FILLED || ordStatus == OrdStatus.FILLED || ordStatus == OrdStatus.PENDING_CANCEL)
                        && message.getDouble(LastShares.FIELD)>0){
                    return true;
                }
            } catch (FieldNotFound e) {
                return false;
            }
        }
        return false;
    }
}
