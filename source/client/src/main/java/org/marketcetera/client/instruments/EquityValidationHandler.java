package org.marketcetera.client.instruments;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.trade.Instrument;
import org.marketcetera.trade.Equity;
import org.marketcetera.client.OrderValidationException;

/* $License$ */
/**
 * Validates equity instruments.
 * <p>
 * No validations are performed for equity instruments.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 2.0.0
 */
@ClassVersion("$Id$")
public class EquityValidationHandler extends InstrumentValidationHandler<Equity> {
    /**
     * Creates an instance.
     */
    public EquityValidationHandler() {
        super(Equity.class);
    }

    @Override
    public void validate(Instrument instrument) throws OrderValidationException {
        //no validations needed for equity.
    }
}
