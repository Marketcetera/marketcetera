package org.marketcetera.ors.history;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.trade.Instrument;
import org.marketcetera.trade.OptionType;
import org.marketcetera.trade.Option;

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
public class OptionSummaryFields extends InstrumentSummaryFields<Option> {
    /**
     * Creates an instance that handles the specified instrument subclass.
     */
    public OptionSummaryFields() {
        super(Option.class);
    }

    @Override
    public String getExpiry(Instrument inInstrument) {
        return ((Option)inInstrument).getExpiry();
    }

    @Override
    public BigDecimal getStrikePrice(Instrument inInstrument) {
        return ((Option)inInstrument).getStrikePrice();
    }

    @Override
    public OptionType getOptionType(Instrument inInstrument) {
        return ((Option)inInstrument).getType();
    }
}