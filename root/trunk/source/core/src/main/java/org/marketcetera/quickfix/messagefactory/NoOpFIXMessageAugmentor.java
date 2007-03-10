package org.marketcetera.quickfix.messagefactory;

import org.marketcetera.core.ClassVersion;
import quickfix.Message;

/**
 * Dummy noop implementation of the {@link FIXMessageAugmentor}
 * @author toli
 * @version $Id$
 */

@ClassVersion("$Id$")
public class NoOpFIXMessageAugmentor implements FIXMessageAugmentor{
    public Message newOrderSingleAugment(Message inMessage) {
        return inMessage;
    }

    public Message cancelRejectAugment(Message inMessage) {
        return inMessage;
    }

    public Message cancelReplaceRequestAugment(Message inMessage) {
        return inMessage;
    }
}
