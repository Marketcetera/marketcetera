package org.marketcetera.ors.history;

import java.math.BigDecimal;

import org.marketcetera.trade.Currency;
import org.marketcetera.trade.Instrument;
import org.marketcetera.trade.OptionType;

/* $License$ */

/**
 * Extracts report summary fields for a currency instrument.
 *
 */
public class CurrencySummaryFields
        extends InstrumentSummaryFields<Currency>
{
    /**
     * Creates an instance that handles the specified instrument subclass.
     */
    public CurrencySummaryFields() {
        super(Currency.class);
    }

    @Override
    public String getExpiry(Instrument inInstrument) {
        return ((Currency)inInstrument).getNearTenor();
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