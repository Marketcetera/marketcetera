package org.marketcetera.core.position;

import java.math.BigDecimal;

import javax.annotation.Nullable;

import org.marketcetera.core.position.impl.PositionKeyImpl;
import org.marketcetera.core.trade.*;
import org.marketcetera.core.trade.impl.ConvertibleBondImpl;
import org.marketcetera.core.trade.impl.EquityImpl;
import org.marketcetera.core.trade.impl.FutureImpl;
import org.marketcetera.core.trade.impl.OptionImpl;

/* $License$ */

/**
 * Factory for creating position keys.
 * 
 * @version $Id: PositionKeyFactory.java 16063 2012-01-31 18:21:55Z colin $
 * @since 2.0.0
 */
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
    public static PositionKey<? extends Equity> createEquityKey(String symbol,
            @Nullable String account, @Nullable String traderId) {
        return createKey(new EquityImpl(symbol), account, traderId);
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
    public static PositionKey<? extends ConvertibleBond> createConvertibleBondKey(String inSymbol,
                                                                                  @Nullable String inAccount,
                                                                                  @Nullable String inTraderID)
    {
        return createKey(new ConvertibleBondImpl(inSymbol),
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
    public static PositionKey<? extends Future> createFutureKey(String inSymbol,
                                                                @Nullable String inExpiry,
                                                                @Nullable String inAccount,
                                                                @Nullable String inTraderId)
    {
        return createKey(new FutureImpl(inSymbol,
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
    public static PositionKey<? extends Option> createOptionKey(String symbol,
            String expiry, BigDecimal strikePrice, OptionType type,
            @Nullable String account, @Nullable String traderId) {
        return createKey(new OptionImpl(symbol, expiry, strikePrice, type),
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
