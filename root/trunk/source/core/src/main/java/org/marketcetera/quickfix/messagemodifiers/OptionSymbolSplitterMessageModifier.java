package org.marketcetera.quickfix.messagemodifiers;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.core.LoggerAdapter;
import org.marketcetera.core.MarketceteraException;
import org.marketcetera.quickfix.MessageModifier;
import org.marketcetera.quickfix.messagefactory.FIXMessageAugmentor;
import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.field.SecurityType;
import quickfix.field.Symbol;

/**
 * Takes an incoming option order and remove the +JE from the symbol field.
 * For example, incoming IBM+JE will be returned as just IBM
 * @author toli
 * @version $Id$
 */

@ClassVersion("$Id$")
public class OptionSymbolSplitterMessageModifier implements MessageModifier {
    public boolean modifyMessage(Message message, FIXMessageAugmentor fixMessageAugmentor)
            throws MarketceteraException {
        try {
            if(SecurityType.OPTION.equals(message.getString(SecurityType.FIELD))) {
                if (message.isSetField(Symbol.FIELD)) {
                    String fullSymbol = message.getString(Symbol.FIELD);
                    if(LoggerAdapter.isDebugEnabled(this)) { LoggerAdapter.debug("Modifying symbol "+fullSymbol, this); }
                    int plusIndex = fullSymbol.indexOf('+');
                    String routeKey = "";
                    if(plusIndex > 0) {
                        String justSymbol = fullSymbol.substring(0, plusIndex);
                        int periodPosition;
                        if ((periodPosition = fullSymbol.lastIndexOf('.')) > 0) {
                            routeKey = fullSymbol.substring(periodPosition);
                            justSymbol += routeKey;
                        }
                        if(LoggerAdapter.isDebugEnabled(this)) { LoggerAdapter.debug("Setting new symbol to be "+justSymbol, this); }
                        message.setField(new Symbol(justSymbol));
                        return true;
                    }
                }
            }
            return false;
        } catch (FieldNotFound fieldNotFound) {
            throw new MarketceteraException(fieldNotFound);
        }
    }
}
