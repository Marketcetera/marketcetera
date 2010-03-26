package org.marketcetera.ors.history;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.trade.Instrument;
import org.marketcetera.trade.OptionType;
import org.marketcetera.trade.Equity;

import java.math.BigDecimal;

/* $License$ */
/**
 * Extracts report summary fields for an equity instrument.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 2.0.0
 */
@ClassVersion("$Id$")
public class EquitySummaryFields extends InstrumentSummaryFields<Equity> {
    /**
     * Creates an instance that handles the specified instrument subclass.
     */
    public EquitySummaryFields() {
        super(Equity.class);
    }

    @Override
    public String getExpiry(Instrument inInstrument) {
        return null;
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
