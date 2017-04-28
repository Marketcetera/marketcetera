package org.marketcetera.quickfix.messagefactory;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.quickfix.FIXVersion;

import quickfix.FieldNotFound;
import quickfix.Message;

/**
 * Dummy noop implementation of the {@link FIXMessageAugmentor}
 * @author toli
 * @version $Id$
 */

@ClassVersion("$Id$")
public class NoOpFIXMessageAugmentor
        implements FIXMessageAugmentor
{
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
    /**
     * Get the FIX Version of this augmentor.
     *
     * @return a <code>FIXVersion</code> value
     */
    protected FIXVersion getFixVersion()
    {
        throw new UnsupportedOperationException();
    }
}
