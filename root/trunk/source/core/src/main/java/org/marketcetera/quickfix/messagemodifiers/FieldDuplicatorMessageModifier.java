package org.marketcetera.quickfix.messagemodifiers;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.core.MarketceteraException;
import org.marketcetera.quickfix.MessageModifier;
import org.marketcetera.quickfix.messagefactory.FIXMessageAugmentor;
import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.StringField;

/**
 * Copies the string value of one field to another.
 * Will only copy the source field if it's present.
 * @author toli
 * @version $Id$
 */

@ClassVersion("$Id$")
public class FieldDuplicatorMessageModifier implements MessageModifier {
    private int destField;
    private int sourceField;

    public FieldDuplicatorMessageModifier(int sourceField, int destField) {
        this.sourceField = sourceField;
        this.destField = destField;
    }

    public boolean modifyMessage(Message message, FIXMessageAugmentor augmentor) throws MarketceteraException {
        try {
            if(message.isSetField(sourceField)) {
                String value = message.getString(sourceField);
                message.setField(new StringField(destField, value));
                return true;
            }
            return false;
        } catch (FieldNotFound fieldNotFound) {
            throw new MarketceteraException(fieldNotFound);
        }
    }
}
