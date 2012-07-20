package org.marketcetera.core.position;

import java.math.BigDecimal;

import javax.annotation.Nullable;

import org.marketcetera.core.position.impl.PositionKeyImpl;
import org.marketcetera.trade.*;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Factory for creating position keys.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 2.0.0
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
     * Creates a convertible bond key.
     * 
     * <p>Note that account and traderID are converted to <code>null</code> if they contain only whitespace.
     *
     * @param inSymbol a <code>String</code> value
     * @param inAccount a <code>String</code> value
     * @param inTraderID a <code>String</code> value
     * @return a PositionKey&lt;ConvertibleBond&gt;</code> value
     * @throws IllegalArgumentException if one of the given parameters are invalid
     */
    public static PositionKey<ConvertibleBond> createConvertibleBondKey(String inSymbol,
                                                                        @Nullable String inAccount,
                                                                        @Nullable String inTraderID)
    {
        return createKey(new ConvertibleBond(inSymbol),
                         inAccount,
                         inTraderID);
    }
    /**
     * Creates a future position key. Note that account and traderId are
     * converted to null if they only contain whitespace.
     * 
     * @param inSymbol a <code>String</code> value
     * @param inExpiry a <code>String</code> value
     * @param inAccount a <code>String</code> value
     * @param inTraderId a <code>String</code> value
     * @throws IllegalArgumentException if one of the given parameters are invalid
     */
    public static PositionKey<Future> createFutureKey(String inSymbol,
                                                      @Nullable String inExpiry,
                                                      @Nullable String inAccount,
                                                      @Nullable String inTraderId)
    {
        return createKey(new Future(inSymbol,
                                    inExpiry),
                         inAccount,
                         inTraderId);
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
