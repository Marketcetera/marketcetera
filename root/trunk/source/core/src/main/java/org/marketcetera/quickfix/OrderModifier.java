package org.marketcetera.quickfix;

import quickfix.Message;
import org.marketcetera.core.ClassVersion;
import org.marketcetera.core.MarketceteraException;

@ClassVersion("$Id$")
public interface OrderModifier {

    /**
     *
     * @param order the order to be modified
     * @return true if the modifier has modified the order, false otherwise
     * @throws MarketceteraException
     */
    public boolean modifyOrder(Message order) throws MarketceteraException;
}
