package org.marketcetera.quickfix;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.core.MarketceteraException;
import org.marketcetera.quickfix.messagefactory.FIXMessageAugmentor;
import quickfix.Message;

/**
 * Interface for all custom order modifiers that can be created by
 * Marketcetera Platform users, added to the OMS to be executed
 * before the orders are sent out to FIX destinations.
 * An example can be a modifier that always adds a {@link quickfix.field.SecurityID} field
 * containing the value of the {@link quickfix.field.Symbol} field.
 */
@ClassVersion("$Id$")
public interface OrderModifier {

    /**
     * Implement the function to make in-line modifications to the incoming message.
     *
     *
     * @param order the order to be modified
     * @param augmentor FIX-version specific augmentor to apply to this message
     * @return true if the modifier has modified the order, false otherwise
     * @throws MarketceteraException
     */
    public boolean modifyOrder(Message order, FIXMessageAugmentor augmentor) throws MarketceteraException;
}
