package org.marketcetera.quickfix.messagefactory;

import org.marketcetera.core.ClassVersion;
import quickfix.Group;

/**
 * @author toli
 * @version $Id$
 */

@ClassVersion("$Id$")
public interface MyMessageFactory extends quickfix.MessageFactory {
    /**
     * Creates a group for the specified parent message type and
     * for the fields with the corresponding field ID
     *
     * Example: to create a {@link quickfix.fix42.MarketDataRequest.NoMDEntryTypes}
     * you need to call
     *       create({@link quickfix.field.MsgType#MARKET_DATA_REQUEST}, {@link quickfix.field.NoMDEntries#FIELD})
     *
     * @param msgType   Message type of the enclosing message
     * @param correspondingFieldID  the fieldID of the field in the group
     * @return  group
     */
    public Group create(String beginString, String msgType, int correspondingFieldID);
}
