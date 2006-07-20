package org.marketcetera.quickfix;

import java.util.prefs.BackingStoreException;

import quickfix.Message;
import org.marketcetera.core.ClassVersion;
import org.marketcetera.core.MarketceteraException;
import org.marketcetera.core.ConfigData;

@ClassVersion("$Id$")
public interface OrderModifier {
    public void init(ConfigData data) throws BackingStoreException;

    /**
     *
     * @param order the order to be modified
     * @return true if the modifier has modified the order, false otherwise
     * @throws MarketceteraException
     */
    public boolean modifyOrder(Message order) throws MarketceteraException;
}
