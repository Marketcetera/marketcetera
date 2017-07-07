package org.marketcetera.client.instruments;

import org.marketcetera.client.OrderValidationException;
import org.marketcetera.trade.ConvertibleBond;
import org.marketcetera.trade.Instrument;

/* $License$ */

/**
 * Validates convertible bond instruments.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class ConvertibleBondValidationHandler
        extends InstrumentValidationHandler<ConvertibleBond>
{
    /**
     * Create a new ConvertibleBondValidationHandler instance.
     */
    public ConvertibleBondValidationHandler()
    {
        super(ConvertibleBond.class);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.instruments.InstrumentValidationHandler#validate(org.marketcetera.trade.Instrument)
     */
    @Override
    public void validate(Instrument inInstrument)
            throws OrderValidationException
    {
    }
}
