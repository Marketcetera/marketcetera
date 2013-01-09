package org.marketcetera.core.position;

import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicLong;

import org.marketcetera.core.position.PositionKey;
import org.marketcetera.core.position.PositionKeyFactory;
import org.marketcetera.core.position.Trade;
import org.marketcetera.trade.Currency;
import org.marketcetera.trade.Equity;
import org.marketcetera.trade.Instrument;
import org.marketcetera.trade.Option;
import org.marketcetera.trade.OptionType;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Simple implementation of the {@link Trade} interface.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.5.0
 */
@ClassVersion("$Id$")
public class MockTrade<T extends Instrument> implements Trade<T> {

    protected final PositionKey<T> mKey;
    protected final BigDecimal mPrice;
    protected final BigDecimal mQuantity;
    protected final long mSequence;
    protected static final AtomicLong mSequenceGenerator = new AtomicLong();

    public static MockTrade<Equity> createEquityTrade(String symbol,
            String account, String traderId, String quantity, String price) {
        return createTrade(PositionKeyFactory.createEquityKey(symbol, account,
                traderId), quantity, price);
    }

    public static MockTrade<Option> createOptionTrade(String symbol,
            String expiry, String strikePrice, OptionType type, String account,
            String traderId, String quantity, String price) {
        return createTrade(PositionKeyFactory.createOptionKey(symbol, expiry,
                new BigDecimal(strikePrice), type, account, traderId),
                quantity, price);
    }
    
    public static MockTrade<Currency> createCurrencyTrade(String leftCcy, String rightCcy,
            String account, String traderId, String quantity, String price) {
        return createTrade(PositionKeyFactory.createCurrencyKey(leftCcy, rightCcy,"","", account,
                traderId), quantity, price);
    }

    public static <T extends Instrument> MockTrade<T> createTrade(T instrument,
            String account, String traderId, String quantity, String price) {
        return createTrade(PositionKeyFactory.createKey(instrument, account,
                traderId), quantity, price);
    }

    public static <T extends Instrument> MockTrade<T> createTrade(
            PositionKey<T> key, String quantity, String price) {
        return new MockTrade<T>(key, new BigDecimal(quantity), new BigDecimal(
                price));
    }

    public MockTrade(PositionKey<T> key, BigDecimal quantity, BigDecimal price) {
        this(key, quantity, price, mSequenceGenerator.incrementAndGet());
    }

    public MockTrade(PositionKey<T> key, BigDecimal quantity, BigDecimal price,
            long sequence) {
        mKey = key;
        mPrice = price;
        mQuantity = quantity;
        mSequence = sequence;
    }

    @Override
    public PositionKey<T> getPositionKey() {
        return mKey;
    }

    @Override
    public BigDecimal getPrice() {
        return mPrice;
    }

    @Override
    public BigDecimal getQuantity() {
        return mQuantity;
    }

    @Override
    public long getSequenceNumber() {
        return mSequence;
    }

}