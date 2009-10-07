package org.marketcetera.core.position.impl;

import java.util.Comparator;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.marketcetera.core.position.PositionKey;
import org.marketcetera.trade.Equity;
import org.marketcetera.trade.Instrument;
import org.marketcetera.trade.Option;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Comparator for PositionKeys that imposes a default ordering of the data.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
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
    @ClassVersion("$Id$")
    private final static class InstrumentComparator implements
            Comparator<Instrument> {

        public final static InstrumentComparator INSTANCE = new InstrumentComparator();

        @Override
        public int compare(Instrument o1, Instrument o2) {
            if (o1 instanceof Equity && o2 instanceof Equity) {
                return EquityComparator.INSTANCE.compare((Equity) o1,
                        (Equity) o2);
            } else if (o1 instanceof Option && o2 instanceof Option) {
                return OptionComparator.INSTANCE.compare((Option) o1,
                        (Option) o2);
            } else {
                return getOrdering(o1) - getOrdering(o2);
            }
        }

        private int getOrdering(Instrument instrument) {
            if (instrument instanceof Equity) {
                return 0;
            } else if (instrument instanceof Option) {
                return 1;
            } else {
                return Integer.MAX_VALUE;
            }
        }
    }

    /**
     * Comparator for Equities.
     */
    @ClassVersion("$Id$")
    private final static class EquityComparator implements Comparator<Equity> {

        public final static EquityComparator INSTANCE = new EquityComparator();

        @Override
        public int compare(Equity o1, Equity o2) {
            return new CompareToBuilder()
                    .append(o1.getSymbol(), o2.getSymbol()).toComparison();
        }
    }

    /**
     * Comparator for Options.
     */
    @ClassVersion("$Id$")
    private final static class OptionComparator implements Comparator<Option> {

        public final static OptionComparator INSTANCE = new OptionComparator();

        @Override
        public int compare(Option o1, Option o2) {
            return new CompareToBuilder()
                    .append(o1.getSymbol(), o2.getSymbol()).append(
                            o1.getExpiry(), o2.getExpiry()).append(
                            o1.getStrikePrice(), o2.getStrikePrice()).append(
                            o1.getType(), o2.getType()).toComparison();
        }
    }
}