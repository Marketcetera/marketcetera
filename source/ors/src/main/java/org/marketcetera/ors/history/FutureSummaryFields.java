package org.marketcetera.ors.history;

import java.math.BigDecimal;

import org.marketcetera.trade.Future;
import org.marketcetera.trade.Instrument;
import org.marketcetera.trade.OptionType;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Extracts report summary fields for a future instrument.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public class FutureSummaryFields
        extends InstrumentSummaryFields<Future>
{
    /**
     * Creates an instance that handles the specified instrument subclass.
     */
    public FutureSummaryFields() {
        super(Future.class);
    }

    @Override
    public String getExpiry(Instrument inInstrument) {
        return ((Future)inInstrument).getExpiryAsMaturityMonthYear().toString();
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