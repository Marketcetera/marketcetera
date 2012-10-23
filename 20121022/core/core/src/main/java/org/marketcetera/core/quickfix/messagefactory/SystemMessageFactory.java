package org.marketcetera.core.quickfix.messagefactory;

import quickfix.MessageFactory;
import quickfix.Message;
import quickfix.Group;
import quickfix.field.BeginString;

/* $License$ */
/**
 * A Message factory for System FIX messages.
 * <p>
 * This factory is used to overwrite the BeginString of the created
 * messages to
 * {@link org.marketcetera.core.quickfix.FIXDataDictionary#FIX_SYSTEM_BEGIN_STRING}
 * after they have been created.
 *
 * @version $Id: SystemMessageFactory.java 16063 2012-01-31 18:21:55Z colin $
 * @since 1.0.0
 */
public class SystemMessageFactory implements MessageFactory {
    @Override
    public Message create(String inBeginString, String inMsgType) {
        Message m = mDelegate.create(inBeginString,inMsgType);
        m.getHeader().setField(new BeginString(inBeginString));
        return m;
    }

    @Override
    public Group create(String beginString, String msgType,
                        int correspondingFieldID) {
        return mDelegate.create(beginString,msgType,correspondingFieldID);
    }
    private final MessageFactory mDelegate = new quickfix.fix44.MessageFactory();
}
