package org.marketcetera.core.position.impl;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.marketcetera.core.trade.Option;
import org.marketcetera.core.trade.Option;

/* $License$ */
/**
 * A comparator for {@link Option} instruments.
 *
 * @version $Id: OptionPositionKeyComparator.java 16063 2012-01-31 18:21:55Z colin $
 * @since 2.0.0
 */
public class OptionPositionKeyComparator extends InstrumentPositionKeyComparator<Option> {
    /**
     * Creates an instance.
     */
    public OptionPositionKeyComparator() {
        super(Option.class);
    }

    /**
     * Returns 200.
     *
     * @return a value of 200.
     */
    @Override
    public int getRank() {
        return 200;
    }

    @Override
    public int compare(Option o1, Option o2) {
        return new CompareToBuilder()
                .append(o1.getSymbol(), o2.getSymbol()).append(
                        o1.getExpiry(), o2.getExpiry()).append(
                        o1.getStrikePrice(), o2.getStrikePrice()).append(
                        o1.getType(), o2.getType()).toComparison();
    }
}