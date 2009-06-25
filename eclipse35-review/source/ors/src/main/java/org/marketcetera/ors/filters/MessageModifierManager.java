package org.marketcetera.ors.filters;

import java.util.LinkedList;
import java.util.List;
import org.marketcetera.core.CoreException;
import org.marketcetera.ors.history.ReportHistoryServices;
import org.marketcetera.quickfix.FIXMessageFactory;
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
    private FIXMessageFactory msgFactory;
    private ReportHistoryServices mHistoryServices; 

    public MessageModifierManager() {}

    public MessageModifierManager(List<MessageModifier> mods, FIXMessageFactory msgFactory){
        setMessageModifiers(mods);
        setMessageFactory(msgFactory);
    }

    public void setMessageModifiers(List<MessageModifier> mods){
		messageModifiers = new LinkedList<MessageModifier>();
		for (MessageModifier mod : mods) {
			messageModifiers.add(mod);
		}
		messageModifiers.add(new TransactionTimeInsertMessageModifier());
    }

    public void setMessageFactory(FIXMessageFactory msgFactory){
        this.msgFactory = msgFactory;
    }

    public ReportHistoryServices getHistoryServices()
    {
        return mHistoryServices;
    }

    public void setHistoryServices
        (ReportHistoryServices historyServices)
    {
        mHistoryServices=historyServices;
    }

    /** Apply all the order modifiers to this message */
    public void modifyMessage(Message inMessage) throws CoreException
    {
        for (MessageModifier oneModifier : messageModifiers) {
            oneModifier.modifyMessage
                (inMessage, getHistoryServices(), msgFactory.getMsgAugmentor());
        }
    }

}
