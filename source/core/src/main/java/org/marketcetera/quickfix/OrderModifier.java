package org.marketcetera.quickfix;

import quickfix.Message;
import org.marketcetera.core.ClassVersion;
import org.marketcetera.core.MarketceteraException;
import org.marketcetera.quickfix.messagefactory.FIXMessageAugmentor;

@ClassVersion("$Id$")
public interface OrderModifier {

    /**
     *
     * @param order the order to be modified
     * @param augmentor The augmentor to apply to this message
     * @return true if the modifier has modified the order, false otherwise
     * @throws MarketceteraException
     */
    public boolean modifyOrder(Message order, FIXMessageAugmentor augmentor) throws MarketceteraException;
}
