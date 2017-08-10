package org.marketcetera.trade;

import java.math.BigDecimal;

import org.marketcetera.trade.ConvertibleBond;
import org.marketcetera.trade.Instrument;
import org.marketcetera.trade.OptionType;

/* $License$ */

/**
 * Extracts report summary fields for a convertible bond instrument.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class ConvertibleBondSummaryFields
        extends InstrumentSummaryFields<ConvertibleBond>
{
    /**
     * Create a new ConvertibleBondSummaryFields instance.
     */
    public ConvertibleBondSummaryFields()
    {
        super(ConvertibleBond.class);
    }
    /* (non-Javadoc)
     * @see com.marketcetera.ors.history.InstrumentSummaryFields#getExpiry(org.marketcetera.trade.Instrument)
     */
    @Override
    public String getExpiry(Instrument inInstrument)
    {
        return null;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.ors.history.InstrumentSummaryFields#getStrikePrice(org.marketcetera.trade.Instrument)
     */
    @Override
    public BigDecimal getStrikePrice(Instrument inInstrument)
    {
        return null;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.ors.history.InstrumentSummaryFields#getOptionType(org.marketcetera.trade.Instrument)
     */
    @Override
    public OptionType getOptionType(Instrument inInstrument)
    {
        return null;
    }
}
