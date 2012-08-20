package org.marketcetera.core.position.impl;

import java.util.Comparator;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.marketcetera.core.position.PositionKey;
import org.marketcetera.core.trade.Instrument;

/* $License$ */

/**
 * Comparator for PositionKeys that imposes a default ordering of the data.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id: PositionKeyComparator.java 16063 2012-01-31 18:21:55Z colin $
 * @since 2.0.0
 */
final class PositionKeyComparator implements Comparator<PositionKey<?>> {

    public final static PositionKeyComparator INSTANCE = new PositionKeyComparator();

    @Override
    public int compare(PositionKey<?> o1, PositionKey<?> o2) {
        /*
         * The order here affects the default order of positions returned by the
         * engine.
         */
        return new CompareToBuilder()
                .append(o1.getTraderId(), o2.getTraderId()).append(
                        o1.getInstrument(), o2.getInstrument(),
                        InstrumentComparator.INSTANCE).append(o1.getAccount(),
                        o2.getAccount()).toComparison();
    }

    /**
     * Comparator for Instruments that imposes a default ordering across all
     * instruments.
     */
        private final static class InstrumentComparator implements
            Comparator<Instrument> {

        public final static InstrumentComparator INSTANCE = new InstrumentComparator();

        @Override
        public int compare(Instrument o1, Instrument o2) {
            if(o1.getClass().equals(o2.getClass())) {
                int value = getComparator(o1).compare(o1, o2);
                return value;
            } else {
                return getComparator(o1).getRank() - getComparator(o2).getRank();
            }
        }

        @SuppressWarnings("unchecked")
        private static <I extends Instrument> InstrumentPositionKeyComparator<I> getComparator(I o1) {
            return InstrumentPositionKeyComparator.SELECTOR.forInstrument(o1);
        }
    }

}