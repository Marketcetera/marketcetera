package org.marketcetera.ors.history;

import org.marketcetera.trade.Future;
import org.marketcetera.trade.Instrument;
import org.marketcetera.trade.Option;
import org.marketcetera.trade.OptionType;
import org.marketcetera.util.misc.ClassVersion;

import java.math.BigDecimal;

/* $License$ */

/**
 * Extracts report summary fields for an option instrument.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 2.0.0
 */
@ClassVersion("$Id$")
public class FutureSummaryFields extends InstrumentSummaryFields<Future> {
    /**
     * Creates an instance that handles the specified instrument subclass.
     */
    public FutureSummaryFields() {
        super(Future.class);
    }

    @Override
    public String getExpiry(Instrument inInstrument) {
        return ((Future)inInstrument).getExpiry();
    }

    @Override
    public BigDecimal getStrikePrice(Instrument inInstrument) {
        return null;
    }

    @Override
    public OptionType getOptionType(Instrument inInstrument) {
        return null;
    }
}