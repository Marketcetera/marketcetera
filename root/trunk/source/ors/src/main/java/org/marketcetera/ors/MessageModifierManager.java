package org.marketcetera.ors;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.core.MarketceteraException;
import org.marketcetera.quickfix.FIXMessageFactory;
import org.marketcetera.quickfix.MessageModifier;
import org.marketcetera.quickfix.TransactionTimeInsertMessageModifier;
import quickfix.Message;

import java.util.LinkedList;
import java.util.List;

/**
 * Object that encapsulates a collection of message modifiers and
 * provides functions to apply all the modifiers to a particular message.
 * Can be used both in the {@link QuickFIXApplication} for to-admin messages (ie Logon)
 * and in {@link OutgoingMessageHandler} for outgoing messages
 * @author toli
 * @version $Id$
 */

@ClassVersion("$Id$")
public class MessageModifierManager {
    private List<MessageModifier> messageModifiers;
    private FIXMessageFactory msgFactory;

    public MessageModifierManager(List<MessageModifier> mods, FIXMessageFactory msgFactory){
		messageModifiers = new LinkedList<MessageModifier>();
		for (MessageModifier mod : mods) {
			messageModifiers.add(mod);
		}
		messageModifiers.add(new TransactionTimeInsertMessageModifier());
        this.msgFactory = msgFactory;
    }

    /** Apply all the order modifiers to this message */
    protected void modifyMessage(Message inMessage) throws MarketceteraException
    {
        for (MessageModifier oneModifier : messageModifiers) {
            oneModifier.modifyMessage(inMessage, msgFactory.getMsgAugmentor());
        }
    }

}
