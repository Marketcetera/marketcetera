package org.marketcetera.quickfix.messagefactory;

import quickfix.Message;
import quickfix.FieldNotFound;

/**
 * Interface for all the FIX version-specific modifictions to messages.
 * For example, FIX40-41 doesn't require {@link quickfix.field.TransactTime} in
 * NewOrderSingle messages, but FIX42-44 does.
 *
 * Meant to work in conjunction with {@link org.marketcetera.quickfix.FIXMessageFactory} to create
 * vanilla versions of version-specific messages.
 *
 * NOTE: this class has nothing to with the other kind of augmentor:
 *    augmentor. A duct usually enclosing the exhaust jet behind the nozzle exit section of rocket to provide increased thrust.
 * @author toli
 * @version $Id$
 */
public interface FIXMessageAugmentor
{
    /**
     * Add the version-specific fields to a {@link quickfix.field.MsgType#ORDER_SINGLE} message.
     *
     * @param inMessage a <code>Message</code> value
     * @return a <code>Message</code> value
     */
    Message newOrderSingleAugment(Message inMessage);
    /**
     * Add the version-specific fields to a {@link quickfix.field.MsgType#EXECUTION_REPORT} message.
     *
     * @param inMessage a <code>Message</code> value
     * @return a <code>Message</code> value
     * @throws FieldNotFound if the message cannot be augmented
     */
    Message executionReportAugment(Message inMessage)
            throws FieldNotFound;
    /** 
     * Add the version-specific fields to a {@link quickfix.field.MsgType#ORDER_CANCEL_REQUEST} message.
     *
     * @param inMessage a <code>Message</code> value
     * @return a <code>Message</code> value
     */
    Message cancelRequestAugment(Message inMessage);
    /**
     * Add the version-specific fields to a {@link quickfix.field.MsgType#ORDER_CANCEL_REPLACE_REQUEST} message.
     *
     * @param inMessage a <code>Message</code> value
     * @return a <code>Message</code> value
     */
    Message cancelReplaceRequestAugment(Message inMessage);
    /**
     * Determines whether or not we need to add a {@link quickfix.field.TransactTime} to a message.
     *
     * @param inMsg a <code>Message</code> value
     * @return a <code>boolean</code> value
     */
    boolean needsTransactTime(Message inMsg);
}
