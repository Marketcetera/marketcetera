package org.marketcetera.client.instruments;

import org.marketcetera.client.OrderValidationException;
import org.marketcetera.trade.Future;
import org.marketcetera.trade.Instrument;

/* $License$ */

/**
 * Validates future instruments.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class FutureValidationHandler
        extends InstrumentValidationHandler<Future>
{
    /**
     * Create a new FutureValidationHandler instance.
     */
    public FutureValidationHandler()
    {
        super(Future.class);
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
