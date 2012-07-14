package org.marketcetera.photon.internal.core;

import org.marketcetera.photon.commons.Validate;
import org.marketcetera.photon.core.InstrumentPrettyPrinter;
import org.marketcetera.trade.Future;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Pretty prints {@link Future} objects for the UI.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 2.1.0
 */
@ClassVersion("$Id$")
public class FuturePrettyPrinter
        extends InstrumentPrettyPrinter<Future>
{
    /**
     * Create a new FuturePrettyPrinter instance.
     */
    public FuturePrettyPrinter()
    {
        super(Future.class);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.photon.core.InstrumentPrettyPrinter#doPrint(org.marketcetera.trade.Instrument)
     */
    @Override
    protected String doPrint(Future inInstrument)
    {
        return String.format("%s %s", //$NON-NLS-1$
                             printExpiry(inInstrument),
                             inInstrument.getSymbol());
    }
    /**
     * Pretty prints a future expiry.
     * 
     * @param option
     *            the option
     * @return the string value
     * @throws IllegalArgumentException
     *             if option is null
     */
    public static String printExpiry(Future inFuture)
    {
        Validate.notNull(inFuture,
                         "future"); //$NON-NLS-1$
        return inFuture.getExpiryAsMaturityMonthYear().getValue();
    }
}
