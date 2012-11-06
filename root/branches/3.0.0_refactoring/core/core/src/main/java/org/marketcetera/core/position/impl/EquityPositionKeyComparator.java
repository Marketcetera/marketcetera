package org.marketcetera.core.position.impl;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.marketcetera.core.trade.impl.EquityImpl;

/* $License$ */
/**
 * A comparator for {@link EquityImpl} instruments.
 *
 * @version $Id: EquityPositionKeyComparator.java 16063 2012-01-31 18:21:55Z colin $
 * @since 2.0.0
 */
public class EquityPositionKeyComparator extends InstrumentPositionKeyComparator<EquityImpl> {
    /**
     * Creates an instance.
     */
    public EquityPositionKeyComparator() {
        super(EquityImpl.class);
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
    public int compare(EquityImpl o1, EquityImpl o2) {
        return new CompareToBuilder().
                append(o1.getSymbol(), o2.getSymbol()).toComparison();
    }
}
