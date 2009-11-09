package org.marketcetera.core.position;

import java.math.BigDecimal;

import javax.annotation.Nullable;

import org.marketcetera.core.position.impl.PositionKeyImpl;
import org.marketcetera.trade.Equity;
import org.marketcetera.trade.Instrument;
import org.marketcetera.trade.Option;
import org.marketcetera.trade.OptionType;
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
     * Creates an equity position key. Note that account and traderId are
     * converted to null if they only contain whitespace.
     * 
     * @param symbol
     *            symbol, cannot be null or whitespace
     * @param account
     *            account
     * @param traderId
     *            trader id
     * @throws IllegalArgumentException
     *             if symbol is null or whitespace
     */
    public static PositionKey<Equity> createEquityKey(String symbol,
            @Nullable String account, @Nullable String traderId) {
        return createKey(new Equity(symbol), account, traderId);
    }

    /**
     * Creates an option position key. Note that account and traderId are
     * converted to null if they only contain whitespace.
     * 
     * @param symbol
     *            the option symbol
     * @param expiry
     *            the option expiry
     * @param strikePrice
     *            the option strike price
     * @param type
     *            the option type
     * @throws IllegalArgumentException
     *             if any argument is null, or if symbol or expiry is whitespace
     */
    public static PositionKey<Option> createOptionKey(String symbol,
            String expiry, BigDecimal strikePrice, OptionType type,
            @Nullable String account, @Nullable String traderId) {
        return createKey(new Option(symbol, expiry, strikePrice, type),
                account, traderId);
    }

    /**
     * Creates a position key for an arbitrary Instrument. Note that account and
     * traderId are converted to null if they only contain whitespace.
     * 
     * @param instrument
     *            instrument, cannot be null
     * @param account
     *            account
     * @param traderId
     *            trader id
     * @throws IllegalArgumentException
     *             if symbol is null or whitespace
     */
    public static <T extends Instrument> PositionKey<T> createKey(T instrument,
            @Nullable String account, @Nullable String traderId) {
        return new PositionKeyImpl<T>(instrument, account, traderId);
    }

    private PositionKeyFactory() {
        throw new AssertionError("non-instantiable"); //$NON-NLS-1$
    }
}
