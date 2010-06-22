package org.marketcetera.core.position.impl;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.marketcetera.trade.Future;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * A comparator for {@link org.marketcetera.trade.Future} instruments.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 2.0.0
 */
@ClassVersion("$Id$")
public class FuturePositionKeyComparator extends InstrumentPositionKeyComparator<Future> {
    /**
     * Creates an instance.
     */
    public FuturePositionKeyComparator() {
        super(Future.class);
    }

    /**
     * Returns 200.
     *
     * @return a value of 200.
     */
    @Override
    public int getRank() {
        return 300;
    }

    @Override
    public int compare(Future o1, Future o2) {
        return new CompareToBuilder()
                .append(o1.getSymbol(), o2.getSymbol()).append(
                        o1.getExpiry(), o2.getExpiry()).toComparison();
    }
}