package org.marketcetera.core.quickfix.messagefactory;

import quickfix.FieldNotFound;
import quickfix.Message;

/**
 * Dummy noop implementation of the {@link FIXMessageAugmentor}
 * @version $Id: NoOpFIXMessageAugmentor.java 16063 2012-01-31 18:21:55Z colin $
 */

public class NoOpFIXMessageAugmentor implements FIXMessageAugmentor{
    public Message newOrderSingleAugment(Message inMessage) {
        return inMessage;
    }

    public Message executionReportAugment(Message inMessage) throws FieldNotFound {
        return inMessage;
    }

    public Message cancelRejectAugment(Message inMessage) {
        return inMessage;
    }

    public Message cancelReplaceRequestAugment(Message inMessage) {
        return inMessage;
    }

    public Message cancelRequestAugment(Message inMessage) {
        return inMessage;
    }


    public boolean needsTransactTime(Message inMsg)
    {
        return false;
    }


}
