package org.marketcetera.client.instruments;

import org.marketcetera.client.OrderValidationException;
import org.marketcetera.trade.Future;
import org.marketcetera.trade.Instrument;
import org.marketcetera.util.log.I18NBoundMessage1P;

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
     * Validates that the given value represents a valid future expiration year. 
     *
     * @param inExpirationYear a <code>String</code> value
     * @throws OrderValidationException if the given value is invalid
     */
    public static void validateExpirationYear(String inExpirationYear)
            throws OrderValidationException
    {
        try {
            if(Integer.parseInt(inExpirationYear) <= 0) {
                throw new OrderValidationException(new I18NBoundMessage1P(Messages.INVALID_FUTURE_EXPIRATION_YEAR_FORMAT,
                                                                          inExpirationYear));
            }
        } catch (OrderValidationException e) {
            throw e;
        } catch (Exception e) {
            throw new OrderValidationException(new I18NBoundMessage1P(Messages.INVALID_FUTURE_EXPIRATION_YEAR_FORMAT,
                                                                      inExpirationYear));
        }
    }
    /**
     * Validates that the given value represents a valid future expiration year. 
     *
     * @param inExpirationYear an <code>int</code> value
     * @throws OrderValidationException if the given value is invalid
     */
    public static void validateExpirationYear(int inExpirationYear)
            throws OrderValidationException
    {
        if(inExpirationYear <= 0) {
            throw new OrderValidationException(new I18NBoundMessage1P(Messages.INVALID_FUTURE_EXPIRATION_YEAR_FORMAT,
                                                                      inExpirationYear));
        }
    }
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
        Future option = (Future)inInstrument;
        validateExpirationYear(option.getExpirationYear());
    }
}
