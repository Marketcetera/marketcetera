package org.marketcetera.quickfix.messagefactory;

import org.marketcetera.core.ClassVersion;
import quickfix.Message;
import quickfix.field.TransactTime;

/**
 * @author toli
 * @version $Id$
 */

@ClassVersion("$Id$")
public class FIXMessageAugmentor_42 extends NoOpFIXMessageAugmentor {
    public Message newOrderSingleAugment(Message inMessage) {
        inMessage.setField(new TransactTime());
        return super.newOrderSingleAugment(inMessage);
    }
}
