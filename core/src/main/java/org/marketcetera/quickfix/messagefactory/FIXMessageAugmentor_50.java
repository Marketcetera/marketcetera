package org.marketcetera.quickfix.messagefactory;

import org.marketcetera.quickfix.FIXVersion;

import quickfix.FieldNotFound;
import quickfix.Message;

/* $License$ */

/**
 * Augments messages to and from FIX4.4 to FIX5.0.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class FIXMessageAugmentor_50
        extends FIXMessageAugmentor_44
{
    /* (non-Javadoc)
     * @see org.marketcetera.quickfix.messagefactory.FIXMessageAugmentor_43#newOrderSingleAugment(quickfix.Message)
     */
    @Override
    public Message newOrderSingleAugment(Message inMessage)
    {
        inMessage = super.newOrderSingleAugment(inMessage);
        return addApplVersion(inMessage);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.quickfix.messagefactory.FIXMessageAugmentor_43#cancelReplaceRequestAugment(quickfix.Message)
     */
    @Override
    public Message cancelReplaceRequestAugment(Message inMessage)
    {
        inMessage = super.cancelReplaceRequestAugment(inMessage);
        return addApplVersion(inMessage);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.quickfix.messagefactory.FIXMessageAugmentor_43#executionReportAugment(quickfix.Message)
     */
    @Override
    public Message executionReportAugment(Message inMessage)
            throws FieldNotFound
    {
        inMessage = super.executionReportAugment(inMessage);
        return addApplVersion(inMessage);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.quickfix.messagefactory.FIXMessageAugmentor_40#cancelRequestAugment(quickfix.Message)
     */
    @Override
    public Message cancelRequestAugment(Message inMessage)
    {
        inMessage = super.cancelRequestAugment(inMessage);
        return addApplVersion(inMessage);
    }
    /**
     * Set the application version, if necessary.
     *
     * @param inMessage a <code>Message</code> value
     * @return a <code>Message</code> value
     */
    protected Message addApplVersion(Message inMessage)
    {
        if(inMessage.getHeader().isSetField(quickfix.field.ApplVerID.FIELD)) {
            return inMessage;
        }
        inMessage.getHeader().setField(new quickfix.field.ApplVerID(getFixVersion().getApplicationVersion()));
        return inMessage;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.quickfix.messagefactory.NoOpFIXMessageAugmentor#getFixVersion()
     */
    @Override
    protected FIXVersion getFixVersion()
    {
        return FIXVersion.FIX50;
    }
}
