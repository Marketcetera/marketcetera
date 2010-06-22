package org.marketcetera.client.instruments;

import org.marketcetera.client.OrderValidationException;
import org.marketcetera.trade.Future;
import org.marketcetera.trade.Instrument;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class FutureValidationHandler
        extends InstrumentValidationHandler<Future>
{
    /* (non-Javadoc)
     * @see org.marketcetera.client.instruments.InstrumentValidationHandler#validate(org.marketcetera.trade.Instrument)
     */
    @Override
    public void validate(Instrument inInstrument)
            throws OrderValidationException
    {
        // todo: i imagine this needs to validate the expiry somehow?
    }
    /**
     * Create a new FutureValidationHandler instance.
     */
    public FutureValidationHandler()
    {
        super(Future.class);
    }
}
