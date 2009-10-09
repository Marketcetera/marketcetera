package org.marketcetera.ors.filters;

import org.marketcetera.core.CoreException;
import org.marketcetera.ors.history.ReportHistoryServices;
import org.marketcetera.quickfix.FIXMessageUtil;
import org.marketcetera.quickfix.messagefactory.FIXMessageAugmentor;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.misc.ClassVersion;
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

    @Override
    public boolean modifyMessage
        (Message message,
         ReportHistoryServices historyServices,
         FIXMessageAugmentor fixMessageAugmentor)
        throws CoreException
    {
        try {
            if(FIXMessageUtil.isOrderSingle(message) &&
                    SecurityType.OPTION.equals(message.getString(SecurityType.FIELD))) {
                if (message.isSetField(Symbol.FIELD)) {
                    String fullSymbol = message.getString(Symbol.FIELD);
                    SLF4JLoggerProxy.debug(this, "Modifying symbol {}", fullSymbol); //$NON-NLS-1$
                    int plusIndex = fullSymbol.indexOf('+');
                    String routeKey = ""; //$NON-NLS-1$
                    if(plusIndex > 0) {
                        String justSymbol = fullSymbol.substring(0, plusIndex);
                        int periodPosition;
                        if ((periodPosition = fullSymbol.lastIndexOf('.')) > 0) {
                            routeKey = fullSymbol.substring(periodPosition);
                            justSymbol += routeKey;
                        }
                        SLF4JLoggerProxy.debug(this, "Setting new symbol to be {}", justSymbol); //$NON-NLS-1$
                        message.setField(new Symbol(justSymbol));
                        return true;
                    }
                }
            }
            return false;
        } catch (FieldNotFound fieldNotFound) {
            throw new CoreException(fieldNotFound);
        }
    }
}
