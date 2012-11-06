package org.marketcetera.core.trade;

/* $License$ */

/**
 * Constructs <code>Future</code> implementations.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface FutureFactory
        extends InstrumentFactory<Future>
{
    /**
     * Creates a <code>Future</code> value.
     *
     * @param inSymbol a <code>String</code> value
     * @param inExpirationMonth a <code>FutureExpirationMonth</code> value
     * @param inExpirationYear an <code>int</code> value
     * @return a <code>Future</code> value
     */
    public Future create(String inSymbol,
                         FutureExpirationMonth inExpirationMonth,
                         int inExpirationYear);
    /**
     * Creates a <code>Future</code> value.
     *
     * @param inSymbol a <code>String</code> value
     * @param inExpiry a <code>String</code> value
     * @return a <code>Future</code> value
     */
    public Future create(String inSymbol,
                         String inExpiry);
}
