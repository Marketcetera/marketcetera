package org.marketcetera.core.position;

import java.math.BigDecimal;

import org.marketcetera.core.position.Option.Type;
import org.marketcetera.core.position.impl.EquityImpl;
import org.marketcetera.core.position.impl.OptionImpl;
import org.marketcetera.core.position.impl.PositionKeyImpl;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Factory for creating position keys.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public class PositionKeyFactory {

    /**
     * Creates an equity position key.
     * 
     * @param symbol
     *            symbol, cannot be null or empty
     * @param account
     *            account
     * @param traderId
     *            trader id
     * @throws IllegalArgumentException
     *             if symbol is null or empty
     */
    public static PositionKey<Equity> createEquityKey(String symbol,
            String account, String traderId) {
        return new PositionKeyImpl<Equity>(new EquityImpl(symbol), account,
                traderId);
    }

    /**
     * Creates an option position key.
     * 
     * @param symbol
     *            the option symbol
     * @param type
     *            the option type
     * @param expiry
     *            the option expiry
     * @param strikePrice
     *            the option strike price
     * @throws IllegalArgumentException
     *             if any argument is null, or if symbol or expiry is empty
     */
    public static PositionKey<Option> createOptionKey(String symbol, Type type,
            String expiry, BigDecimal strikePrice, String account,
            String traderId) {
        return new PositionKeyImpl<Option>(new OptionImpl(symbol, type, expiry,
                strikePrice), account, traderId);
    }

    private PositionKeyFactory() {
        throw new AssertionError("non-instantiable"); //$NON-NLS-1$
    }
}
