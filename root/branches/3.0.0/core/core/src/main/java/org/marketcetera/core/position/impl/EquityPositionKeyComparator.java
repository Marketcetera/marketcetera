package org.marketcetera.core.position.impl;

import org.marketcetera.core.trade.Equity;
import org.apache.commons.lang.builder.CompareToBuilder;

/* $License$ */
/**
 * A comparator for {@link Equity} instruments.
 *
 * @author anshul@marketcetera.com
 * @version $Id: EquityPositionKeyComparator.java 16063 2012-01-31 18:21:55Z colin $
 * @since 2.0.0
 */
public class EquityPositionKeyComparator extends InstrumentPositionKeyComparator<Equity> {
    /**
     * Creates an instance.
     */
    public EquityPositionKeyComparator() {
        super(Equity.class);
    }

    /**
     * Returns 100.
     *
     * @return a value of 100.
     */
    @Override
    public int getRank() {
        return 100;
    }

    @Override
    public int compare(Equity o1, Equity o2) {
        return new CompareToBuilder().
                append(o1.getSymbol(), o2.getSymbol()).toComparison();
    }
}
