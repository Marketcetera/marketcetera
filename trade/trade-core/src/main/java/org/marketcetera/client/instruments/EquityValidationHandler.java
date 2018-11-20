package org.marketcetera.client.instruments;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.trade.Instrument;
import org.marketcetera.trade.client.OrderValidationException;
import org.marketcetera.trade.Equity;

/* $License$ */
/**
 * Validates equity instruments.
 * <p>
 * No validations are performed for equity instruments.
 *
 * @author anshul@marketcetera.com
 * @version $Id: EquityValidationHandler.java 17516 2017-08-28 17:25:59Z colin $
 * @since 2.0.0
 */
@ClassVersion("$Id: EquityValidationHandler.java 17516 2017-08-28 17:25:59Z colin $")
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
