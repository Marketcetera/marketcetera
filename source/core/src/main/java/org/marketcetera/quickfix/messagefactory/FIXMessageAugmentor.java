package org.marketcetera.quickfix.messagefactory;

import quickfix.Message;

/**
 * Interface for all the FIX version-specific modifictions to messages.
 * For example, FIX40-41 doesn't require {@link quickfix.field.TransactTime} in
 * NewOrderSingle messages, but FIX42-44 does.
 *
 * Meant to work in conjunction with {@link org.marketcetera.quickfix.FIXMessageFactory} to create
 * vanilla versions of version-specific messages.
 *
 * NOTE: this class has nothing to with the other augmentor:
 * augmentor. A duct usually enclosing the exhaust jet behind the nozzle exit section of rocket to provide increased thrust.
 * @author toli
 * @version $Id$
 */
public interface FIXMessageAugmentor {
    /** Add the version-specific fields to a {@link quickfix.field.MsgType#ORDER_SINGLE} message */
    public Message newOrderSingleAugment(Message inMessage);

    /** Add the version-specific fields to a {@link quickfix.field.MsgType#ORDER_CANCEL_REJECT} message */
//    public Message cancelRejectAugment(Message inMessage);

    /** Add the version-specific fields to a {@link quickfix.field.MsgType#ORDER_CANCEL_REPLACE_REQUEST} message */
//    public Message cancelReplaceRequestAugment(Message inMessage);
}