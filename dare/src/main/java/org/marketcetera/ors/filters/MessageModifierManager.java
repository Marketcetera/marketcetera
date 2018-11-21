package org.marketcetera.ors.filters;

import java.util.LinkedList;
import java.util.List;

import org.marketcetera.util.except.I18NException;
import org.marketcetera.util.misc.ClassVersion;

/**
 * Object that encapsulates a collection of message modifiers and
 * provides functions to apply all the modifiers to a particular message.
 *
 * @author toli
 * @version $Id$
 */

@ClassVersion("$Id$")
public class MessageModifierManager {
    private List<MessageModifier> messageModifiers;

    public void setMessageModifiers(List<MessageModifier> mods){
		messageModifiers = new LinkedList<MessageModifier>();
		for (MessageModifier mod : mods) {
			messageModifiers.add(mod);
		}
		messageModifiers.add(new TransactionTimeInsertMessageModifier());
    }

    /** Apply all the order modifiers to this message */
    public void modifyMessage(quickfix.Message inMessage)
        throws I18NException
    {
        for(MessageModifier oneModifier : messageModifiers) {
            oneModifier.modifyMessage(inMessage);
        }
    }
}
