package org.marketcetera.ors.filters;

import java.util.LinkedList;
import java.util.List;

import org.marketcetera.ors.history.ReportHistoryServices;
import org.marketcetera.ors.info.RequestInfo;
import org.marketcetera.ors.info.SystemInfo;
import org.marketcetera.quickfix.FIXMessageFactory;
import org.marketcetera.quickfix.messagefactory.FIXMessageAugmentor;
import org.marketcetera.util.except.I18NException;
import org.marketcetera.util.misc.ClassVersion;

import quickfix.Message;

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
    public void modifyMessage(RequestInfo info)
        throws I18NException
    {
        Message inMessage=(Message)info.getValue(RequestInfo.CURRENT_MESSAGE);
        ReportHistoryServices historyServices=(ReportHistoryServices)
            (info.getSessionInfo().getSystemInfo().getValue
             (SystemInfo.HISTORY_SERVICES));
        FIXMessageAugmentor augmentor=
            ((FIXMessageFactory)
             info.getValue(RequestInfo.FIX_MESSAGE_FACTORY)).getMsgAugmentor();
        for(MessageModifier oneModifier : messageModifiers) {
            if(oneModifier instanceof SessionAwareMessageModifier) {
                ((SessionAwareMessageModifier)oneModifier).setSessionInfo(info.getSessionInfo());
            }
            oneModifier.modifyMessage(inMessage,historyServices,augmentor);
        }
    }
}
