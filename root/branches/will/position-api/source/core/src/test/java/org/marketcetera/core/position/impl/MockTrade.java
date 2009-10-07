package org.marketcetera.core.position.impl;

import java.math.BigDecimal;

import org.marketcetera.core.position.PositionKey;
import org.marketcetera.core.position.PositionKeyFactory;
import org.marketcetera.core.position.Trade;
import org.marketcetera.trade.Equity;
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
public class MockTrade implements Trade<Equity> {

    protected final PositionKey<Equity> mKey;
    protected final BigDecimal price;
    protected final BigDecimal quantity;
    protected final long sequence;

    /**
     * Constructor initalizing all fields.
     * 
     * @param symbol
     * @param account
     * @param traderId
     * @param price
     * @param quantity
     * @param sequence
     */
    public MockTrade(String symbol, String account, String traderId,
            BigDecimal price, BigDecimal quantity, long sequence) {
        mKey = PositionKeyFactory.createEquityKey(symbol, account, traderId);
        this.price = price;
        this.quantity = quantity;
        this.sequence = sequence;
    }

    @Override
    public BigDecimal getPrice() {
        return price;
    }

    @Override
    public BigDecimal getQuantity() {
        return quantity;
    }

    @Override
    public long getSequenceNumber() {
        return sequence;
    }

    @Override
    public PositionKey<Equity> getPositionKey() {
        return mKey;
    }

}